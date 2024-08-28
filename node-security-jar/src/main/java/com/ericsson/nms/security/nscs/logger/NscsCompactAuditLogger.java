/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2023
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.handler.command.utility.PasswordHelper;
import com.ericsson.oss.itpf.sdk.recording.CommandPhase;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.dto.WfResult;
import com.ericsson.oss.services.enums.JobGlobalStatusEnum;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;
import com.ericsson.oss.services.security.nscs.jobs.JobStatsFormatter;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NscsCompactAuditLogger {

    /**
     * Compact Audit Log resources.
     * 
     * A short string representing the entity type affected by the action.
     * 
     * First character of each word must be uppercase, other characters lowercase.
     * 
     * Words must be separated by single “blank” character (hyphen or underscore are not allowed as separator).
     */
    private static final String COMPACT_AUDIT_UNKNOWN_RESOURCE = "Unknown";
    private static final String COMPACT_AUDIT_NODE_RESOURCE = "Node";
    private static final String COMPACT_AUDIT_PROXY_ACCOUNT_RESOURCE = "Proxy Account";
    private static final String COMPACT_AUDIT_JOB_RESOURCE = "Job";
    private static final String COMPACT_AUDIT_CAPABILITY_RESOURCE = "Capability";
    private static final String COMPACT_AUDIT_NODE_CACHE_RESOURCE = "Node Cache";

    /**
     * Compact Audit Log constants.
     */
    private static final String COMPACT_AUDIT_ADDITIONAL_INFO_EMPTY_STRING = "";
    private static final String COMPACT_AUDIT_ERROR_RESULT_NO_JOB_STARTED = "No job started since all nodes are invalid.";
    private static final String COMPACT_AUDIT_OP_TYPE_CREATE = "create";
    private static final String SPACE_WORD_SEPARATOR = " ";
    private static final String COMMAND_TYPE_WORD_SEPARATOR = "_";

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private NscsSystemRecorder nscsSystemRecorder;

    @Inject
    private NscsContextService nscsContextService;

    @Inject
    private PasswordHelper passwordHelper;

    /**
     * Records Compact Audit Log (CAL) when a command (both synchronous and asynchronous) finishes with success.
     * 
     * A command finishes with success if the command processing does not throw an exception.
     * 
     * This does not necessarily mean that the command has been successfully executed on all requested resources because some resources could have
     * been invalid or the command could have failed for some valid resources.
     * 
     * For asynchronous commands the successful creation of the job shall be logged here. This means that in case of no valid resources, the job
     * creation has not been performed.
     * 
     * This method actually invokes SFWK system recorder only if command text, user ID, session ID and source IP address are available in the context.
     * 
     * @param prefix
     *            the prefix.
     * @param obfuscatedCommandText
     *            the obfuscated command text.
     */
    public void recordCommandFinishedWithSuccessCompactAudit(final String prefix, final String obfuscatedCommandText) {
        logger.debug("CAL_DEBUG CMD: prefix [{}] obfuscatedCommandText [{}]", prefix, obfuscatedCommandText);
        final String cmdTypeStr = nscsContextService.getCommandTypeContextValue();
        logger.debug("CAL_DEBUG CMD: cmdTypeStr [{}]", cmdTypeStr);
        if (cmdTypeStr != null) {
            final String commandName = String.format("CLI: %s%s", prefix, obfuscatedCommandText);
            logger.debug("CAL_DEBUG CMD: commandName [{}]", commandName);
            final NscsCommandType cmdType = NscsCommandType.valueOf(cmdTypeStr);
            final NscsCompactAuditLogMode calMode = NscsCompactAuditLogMode.fromCmdType(cmdType, commandName, false);
            logger.debug("CAL_DEBUG CMD: cmdType [{}] calMode [{}]", cmdType, calMode);
            if (!NscsCompactAuditLogMode.NOT_COMPACT_AUDIT_LOGGED.equals(calMode)) {
                final CommandPhase commandPhase = toCompactAuditCommandPhase(calMode);
                final String resource = getCompactAuditResource(calMode);
                final String additionalInfo = toCompactAuditAdditionalInfo(calMode, commandPhase, resource, cmdType);
                logger.debug("CAL_DEBUG CMD: commandPhase [{}] resource [{}] additionalInfo [{}]", commandPhase, resource, additionalInfo);
                recordCommandCompactAudit(commandName, commandPhase, resource, additionalInfo);
            }
        } else {
            logger.error("CAL command type not present in context for prefix [{}] obfuscatedCommandText [{}]", prefix, obfuscatedCommandText);
        }
    }

    /**
     * Records Compact Audit Log (CAL) when a command (both synchronous and asynchronous) finishes with error.
     * 
     * A command finishes with error if the command processing throws an exception.
     * 
     * The command processing throws an exception when a command (both synchronous and asynchronous) is rejected due to syntax error, security
     * violation, command validation failed, unexpected exception during the command handler execution.
     * 
     * The "command validation failed" error refers to syntactically correct commands, performed by user with the requested role, containing valid
     * resources (the validity of resources depends on the involved use case) but that are "ambiguous" (e.g. credentials command with non homogeneous
     * nodes or the same resource is present twice in an XML file with different conflicting parameters). In such cases, the Node Security application
     * shall not guess which was the intention of the operator and the command shall be rejected.
     * 
     * A special case is for commands rejected because of syntax error, so, before extracting the command type. Since Node Security is not able to
     * determine which command type is involved. For this reason the command is logged anyway related to an Unknown resource.
     * 
     * This method actually invokes SFWK system recorder only if command text, user ID, session ID and source IP address are available in the context.
     * 
     * @param prefix
     *            the prefix.
     * @param obfuscatedCommandText
     *            the obfuscated command text.
     * @param errorMsg
     *            the error message.
     */
    public void recordCommandFinishedWithErrorCompactAudit(final String prefix, final String obfuscatedCommandText, final String errorMsg) {
        logger.debug("CAL_DEBUG CMD: prefix [{}] obfuscatedCommandText [{}] errorMsg[{}]", prefix, obfuscatedCommandText, errorMsg);
        final CommandPhase commandPhase = CommandPhase.FINISHED_WITH_ERROR;
        final String cmdTypeStr = nscsContextService.getCommandTypeContextValue();
        logger.debug("CAL_DEBUG CMD: cmdTypeStr [{}]", cmdTypeStr);
        final String commandName = String.format("CLI: %s%s", prefix, obfuscatedCommandText);
        String resource = null;
        final String additionalInfo = toCompactAuditAdditionalInfoErrorDetail(errorMsg);
        if (cmdTypeStr != null) {
            final NscsCommandType cmdType = NscsCommandType.valueOf(cmdTypeStr);
            final NscsCompactAuditLogMode calMode = NscsCompactAuditLogMode.fromCmdType(cmdType, commandName, true);
            logger.debug("CAL_DEBUG CMD: cmdType [{}] calMode [{}]", cmdType, calMode);
            if (!NscsCompactAuditLogMode.NOT_COMPACT_AUDIT_LOGGED.equals(calMode)) {
                resource = getCompactAuditResource(calMode);
            }
        } else {
            // command syntax error
            resource = COMPACT_AUDIT_UNKNOWN_RESOURCE;
        }
        logger.debug("CAL_DEBUG CMD: commandName [{}] resource [{}] additionalInfo [{}]", commandName, resource, additionalInfo);
        recordCommandCompactAudit(commandName, commandPhase, resource, additionalInfo);
    }

    /**
     * Records Compact Audit Log (CAL) for the given job.
     * 
     * This method actually invokes SFWK system recorder only for asynchronous commands requesting Compact Audit Logging if the related job is
     * completed and if command name, user ID, session ID and source IP address are available in the job.
     * 
     * @param job
     *            the updated job record.
     * @param wfResults
     *            the updated list of results for the workflows of the involved job.
     */
    public void recordJobCacheCompletedCompactAudit(final JobStatusRecord job, final List<WfResult> wfResults) {
        if (job != null && JobGlobalStatusEnum.COMPLETED.equals(job.getGlobalStatus())) {
            final String userId = job.getUserId();
            final String encryptedSessionId = job.getSessionId();
            final String sessionId = encryptedSessionId != null ? passwordHelper.decryptDecode(encryptedSessionId) : null;
            final String sourceIP = job.getSourceIP();
            final String commandName = job.getCommandName();
            logger.debug("CAL_DEBUG JOB: userId [{}] sessionId [{}] sourceIP [{}] commandName [{}]", userId, sessionId != null ? "*******" : null,
                    sourceIP, commandName);
            if (hasJobCompactAuditLogParams(userId, sessionId, sourceIP, commandName)) {
                recordJobCacheCompletedCompactAudit(job, wfResults, userId, sessionId, sourceIP, commandName);
            } else {
                logger.warn(
                        "CAL params not present in job [{}]: userId [{}] sessionId [{}] sourceIP [{}] commandName [{}]. This can happen in an upgrade scenario.",
                        job.getJobId(), userId, sessionId != null ? "*******" : null, sourceIP, commandName);
            }
        }
    }

    /**
     * Records Compact Audit Log (CAL) when a REST finishes with success.
     * 
     * A REST finishes with success if the processing does not throw an exception.
     * 
     * This does not necessarily mean that the command has been successfully executed on all requested resources because some resources could have
     * been invalid or the rest could have failed for some valid resources.
     * 
     * This method actually invokes SFWK system recorder only if rest resource file, user ID, session ID and source IP address are available in the
     * context.
     */
    public void recordRestFinishedWithSuccessCompactAudit() {
        final String restUrlPath = nscsContextService.getRestUrlPathContextValue();
        logger.debug("REST_CAL restUrlPath [{}]", restUrlPath);
        if (restUrlPath != null) {
            final String restMethod = nscsContextService.getRestMethodContextValue();
            logger.debug("REST_CAL restMethod [{}]", restMethod);
            final NscsCompactAuditLogMode calMode = NscsCompactAuditLogMode.fromRestUrlPath(restUrlPath, restMethod);
            logger.debug("REST_CAL calMode [{}]", calMode);
            if (!NscsCompactAuditLogMode.NOT_COMPACT_AUDIT_LOGGED.equals(calMode) && isRestToBeCompactAuditLogged(restUrlPath, restMethod)) {
                final String operationSlogan = getOperationSlogan(restUrlPath, restMethod);
                logger.debug("REST_CAL operationSlogan [{}]", operationSlogan);
                final String restUrlFile = nscsContextService.getRestUrlFileContextValue();
                logger.debug("REST_CAL restUrlFile [{}]", restUrlFile);
                final String requestPayload = nscsContextService.getRestRequestPayloadContextValue();
                logger.debug("REST_CAL requestPayload [{}]", requestPayload);
                // <OPERATION_SLOGAN> - REST_method  Resource: Resource_path   Body: Request_Payload
                final String commandName = String.format("<%s> - %s Resource: %s Body: %s", operationSlogan, restMethod, restUrlFile, requestPayload);
                logger.debug("REST_CAL commandName [{}]", commandName);
                final CommandPhase commandPhase = toCompactAuditCommandPhase(calMode);
                logger.debug("REST_CAL commandPhase [{}]", commandPhase);
                final String resource = getCompactAuditResource(calMode);
                logger.debug("REST_CAL resource [{}]", resource);
                final String additionalInfo = toCompactAuditAdditionalInfo(calMode, resource, operationSlogan);
                logger.debug("REST_CAL additionalInfo [{}]", additionalInfo);
                recordCommandCompactAudit(commandName, commandPhase, resource, additionalInfo);
            }
        } else {
            logger.error("REST_CAL restUrlPath not present in context");
        }
    }

    /**
     * Records Compact Audit Log (CAL) when a REST finishes with error.
     * 
     * A REST finishes with error if the processing throws an exception.
     * 
     * This method actually invokes SFWK system recorder only if rest resource file, user ID, session ID and source IP address are available in the
     * context.
     */
    public void recordRestFinishedWithErrorCompactAudit() {
        final String restUrlPath = nscsContextService.getRestUrlPathContextValue();
        logger.debug("REST_CAL restUrlPath [{}]", restUrlPath);
        if (restUrlPath != null) {
            final String restMethod = nscsContextService.getRestMethodContextValue();
            logger.debug("REST_CAL restMethod [{}]", restMethod);
            final NscsCompactAuditLogMode calMode = NscsCompactAuditLogMode.fromRestUrlPath(restUrlPath, restMethod);
            logger.debug("REST_CAL calMode [{}]", calMode);
            if (!NscsCompactAuditLogMode.NOT_COMPACT_AUDIT_LOGGED.equals(calMode)) {
                final String operationSlogan = getOperationSlogan(restUrlPath, restMethod);
                logger.debug("REST_CAL operationSlogan [{}]", operationSlogan);
                final String restUrlFile = nscsContextService.getRestUrlFileContextValue();
                logger.debug("REST_CAL restUrlFile [{}]", restUrlFile);
                final String requestPayload = nscsContextService.getRestRequestPayloadContextValue();
                logger.debug("REST_CAL requestPayload [{}]", requestPayload);
                // <OPERATION_SLOGAN> - REST_method  Resource: Resource_path   Body: Request_Payload
                final String commandName = String.format("<%s> - %s Resource: %s Body: %s", operationSlogan, restMethod, restUrlFile, requestPayload);
                logger.debug("REST_CAL commandName [{}]", commandName);
                final CommandPhase commandPhase = CommandPhase.FINISHED_WITH_ERROR;
                logger.debug("REST_CAL commandPhase [{}]", commandPhase);
                final String resource = getCompactAuditResource(calMode);
                logger.debug("REST_CAL resource [{}]", resource);
                final String additionalInfo = toCompactAuditAdditionalInfo(calMode, resource, operationSlogan);
                logger.debug("REST_CAL additionalInfo [{}]", additionalInfo);
                recordCommandCompactAudit(commandName, commandPhase, resource, additionalInfo);
            }
        } else {
            logger.error("REST_CAL restUrlPath not present in context");
        }
    }

    /**
     * Returns if job contains the Compact Audit Log parameters.
     * 
     * The CAL parameters could be not present in the job in an upgrade scenario where the job was inserted in the job cache by an old instance of
     * node security not yet supporting CAL.
     * 
     * @param userId
     *            the user ID contained in the job.
     * @param sessionId
     *            the session ID contained in the job.
     * @param sourceIP
     *            the source IP address contained in the job.
     * @param commandName
     *            the command name contained in the job.
     * @return true if CAL parameters are contained in the job, false otherwise.
     */
    private boolean hasJobCompactAuditLogParams(final String userId, final String sessionId, final String sourceIP, final String commandName) {
        return sessionId != null && sourceIP != null && commandName != null && userId != null;
    }

    /**
     * Records Compact Audit Log (CAL) for the given completed job and for the given not null CAL parameters (command text, user ID, session ID and
     * source IP address).
     * 
     * @param job
     *            the completed job record.
     * @param wfResults
     *            the list of results for the workflows of the involved job.
     * @param userId
     *            the not null user ID.
     * @param sessionId
     *            the not null session ID.
     * @param sourceIP
     *            the not null source IP address.
     * @param commandText
     *            the not null command text.
     */
    private void recordJobCacheCompletedCompactAudit(final JobStatusRecord job, final List<WfResult> wfResults, final String userId,
            final String sessionId, final String sourceIP, final String commandText) {
        logger.debug("CAL_DEBUG JOB: commandText [{}]", commandText);
        final NscsCommandType cmdType = NscsCommandType.valueOf(job.getCommandId());
        final NscsCompactAuditLogMode calMode = NscsCompactAuditLogMode.fromCmdType(cmdType, commandText, false);
        logger.debug("CAL_DEBUG JOB: cmdType [{}] calMode [{}]", cmdType, calMode);
        if (NscsCompactAuditLogMode.COMPACT_AUDIT_LOGGED_ASYNC_NODE_CMD.equals(calMode)) {
            final String commandName = toCompactAuditJobCompletedCommandName(job);
            logger.debug("CAL_DEBUG JOB: commandName [{}]", commandName);
            final CommandPhase commandPhase = CommandPhase.EXECUTED;
            logger.debug("CAL_DEBUG JOB: commandPhase [{}]", commandPhase);
            // At job completion the resource is related to changed entities (Node) and not to Job itself
            final String resource = COMPACT_AUDIT_NODE_RESOURCE;
            logger.debug("CAL_DEBUG JOB: resource [{}]", resource);
            final int numOfInvalid = job.getNumOfInvalid();
            logger.debug("CAL_DEBUG JOB: numOfInvalid [{}]", numOfInvalid);
            final JobStatsFormatter jobStatsFormatter = new JobStatsFormatter(job, wfResults);
            final String additionalInfo = toCompactAuditAdditionalInfoSummaryResult(COMPACT_AUDIT_NODE_RESOURCE, cmdType, jobStatsFormatter,
                    numOfInvalid);
            logger.debug("CAL_DEBUG JOB: commandName [{}] commandPhase [{}] resource [{}] additionalInfo [{}]", commandName, commandPhase, resource,
                    additionalInfo);
            /**
             * Customers are parsing the following compact audit log and they need to be notified of changes.
             */
            nscsSystemRecorder.recordCompactAudit(userId, commandName, commandPhase, resource, sourceIP, sessionId, additionalInfo);
        }
    }

    /**
     * Convert to SDK recording compact audit command name for the given completed job.
     * 
     * The format is:
     * 
     * <OPERATION_SLOGAN> - Job Result jobId: job_identifier
     * 
     * where:
     * 
     * - OPERATION_SLOGAN: an UPPERCASE string. It shall be a short operation descriptor, enclosed in angle brackets; words must be separated by
     * single “blank” character (hyphen or underscore are not allowed as separator).
     * 
     * - job_identifier: a unique identifier of the job.
     * 
     * @param job
     *            the completed job.
     * @return the command name.
     */
    private String toCompactAuditJobCompletedCommandName(final JobStatusRecord job) {
        final String jobIdentifier = job.getJobId().toString();
        final String operationDescr = job.getCommandId().replaceAll(COMMAND_TYPE_WORD_SEPARATOR, SPACE_WORD_SEPARATOR);
        return String.format("<%s> - Job Result jobId: %s", operationDescr, jobIdentifier);
    }

    /**
     * Convert context statistics of a finished with success command to SDK recording compact audit additional info for the given CAL mode, command
     * phase, resource and command type.
     * 
     * This method produces a JSON string to be used as additional info.
     * 
     * @param calMode
     *            the CAL mode.
     * @param commandPhase
     *            the command phase.
     * @param resource
     *            the resource.
     * @param cmdType
     *            the command type.
     * @return the JSON formatted additional info or null if no additional info is available.
     */
    private String toCompactAuditAdditionalInfo(final NscsCompactAuditLogMode calMode, final CommandPhase commandPhase, final String resource,
            final NscsCommandType cmdType) {
        String additionalInfo = null;
        switch (calMode) {
        case COMPACT_AUDIT_LOGGED_SYNC_NODE_CMD:
            // break intentionally omitted
        case COMPACT_AUDIT_LOGGED_SYNC_PROXY_ACCOUNT_CMD:
            additionalInfo = toCompactAuditAdditionalInfoSummaryResult(resource, cmdType);
            break;

        case COMPACT_AUDIT_LOGGED_SYNC_SINGLE_PROXY_ACCOUNT_CMD:
            additionalInfo = toCompactAuditAdditionalInfoDetailResult(resource, COMPACT_AUDIT_OP_TYPE_CREATE);
            break;

        case COMPACT_AUDIT_LOGGED_ASYNC_NODE_CMD:
            if (CommandPhase.FINISHED_WITH_SUCCESS.equals(commandPhase)) {
                additionalInfo = toCompactAuditAdditionalInfoDetailResult(resource, COMPACT_AUDIT_OP_TYPE_CREATE);
            } else {
                additionalInfo = toCompactAuditAdditionalInfoErrorDetail(COMPACT_AUDIT_ERROR_RESULT_NO_JOB_STARTED);
            }
            break;

        case COMPACT_AUDIT_LOGGED_GET_NODE_CMD:
            // break intentionally omitted
        case COMPACT_AUDIT_LOGGED_GET_PROXY_ACCOUNT_CMD:
            // break intentionally omitted
        case COMPACT_AUDIT_LOGGED_GET_JOB_CMD:
            // break intentionally omitted
        case COMPACT_AUDIT_LOGGED_GET_CAPABILITY_CMD:
            // break intentionally omitted
        case COMPACT_AUDIT_LOGGED_GET_UNKNOWN_CMD:
            additionalInfo = COMPACT_AUDIT_ADDITIONAL_INFO_EMPTY_STRING;
            break;

        default:
            break;
        }
        return additionalInfo;
    }

    /**
     * Convert the given error message to SDK recording compact audit additional info format containing the error detail.
     * 
     * This method produces a JSON string to be used as additional info.
     * 
     * @param errorMsg
     *            the error message.
     * @return the JSON formatted additional info or null if no additional info is available.
     */
    private String toCompactAuditAdditionalInfoErrorDetail(final String errorMsg) {
        final NscsCompactAuditLogAdditionalInfo calAdditionalInfo = new NscsCompactAuditLogAdditionalInfo();
        calAdditionalInfo.setErrorDetail(errorMsg);
        return toJsonString(calAdditionalInfo);
    }

    /**
     * Convert job statistics for a completed job to SDK recording compact audit additional info format containing the summary result for the given
     * resource and command type.
     * 
     * Note that the resource refers to the items described in the summary.
     * 
     * This method produces a JSON string to be used as additional info.
     * 
     * @param resource
     *            the resource.
     * @param cmdType
     *            the command type.
     * @param jobStatsFormatter
     *            the job statistics.
     * @param numOfInvalid
     *            the number of invalid items.
     * @return the JSON formatted additional info or null if no additional info is available.
     */
    private String toCompactAuditAdditionalInfoSummaryResult(final String resource, final NscsCommandType cmdType,
            final JobStatsFormatter jobStatsFormatter, final int numOfInvalid) {
        NscsCompactAuditLogAdditionalInfo calAdditionalInfo = null;
        final Map<String, Serializable> summary = buildSummaryFromJob(resource, cmdType, jobStatsFormatter, numOfInvalid);
        if (!summary.isEmpty()) {
            calAdditionalInfo = new NscsCompactAuditLogAdditionalInfo();
            final List<Map<String, Serializable>> summaryResult = new ArrayList<>();
            summaryResult.add(summary);
            calAdditionalInfo.setSummaryResult(summaryResult);
        }
        return toJsonString(calAdditionalInfo);
    }

    /**
     * Build the summary from the given job statistics for the given resource and command type.
     * 
     * @param resource
     *            the resource.
     * @param cmdType
     *            the command type.
     * @param jobStatsFormatter
     *            the job statistics.
     * @param numOfInvalid
     *            the number of invalid items.
     * @return the detail.
     */
    private Map<String, Serializable> buildSummaryFromJob(final String resource, final NscsCommandType cmdType,
            final JobStatsFormatter jobStatsFormatter, final int numOfInvalid) {
        final Map<String, Serializable> summary = new HashMap<>();
        summary.put("id", jobStatsFormatter.getJobId());
        summary.put("opType", cmdType.name().replaceAll(COMMAND_TYPE_WORD_SEPARATOR, SPACE_WORD_SEPARATOR).toLowerCase(Locale.ROOT));
        summary.put("entity", resource);
        final Map<String, Serializable> results = jobStatsFormatter.toCompactAuditAdditionalInfo(numOfInvalid);
        summary.put("result", (Serializable) results);
        return summary;
    }

    /**
     * Convert context statistics for a finished with success async command with valid nodes or sync command on a single item to SDK recording compact
     * audit additional info format containing the detail result for the given resource and operation type.
     * 
     * This method produces a JSON string to be used as additional info.
     * 
     * @param resource
     *            the resource.
     * @param opType
     *            the operation type.
     * @return the JSON formatted additional info or null if no additional info is available.
     */
    private String toCompactAuditAdditionalInfoDetailResult(final String resource, final String opType) {
        NscsCompactAuditLogAdditionalInfo calAdditionalInfo = null;
        final Map<String, Serializable> detail = buildDetailFromContext(resource, opType);
        if (!detail.isEmpty()) {
            calAdditionalInfo = new NscsCompactAuditLogAdditionalInfo();
            final List<Map<String, Serializable>> detailResult = new ArrayList<>();
            detailResult.add(detail);
            calAdditionalInfo.setDetailResult(detailResult);
        }
        return toJsonString(calAdditionalInfo);
    }

    /**
     * Build the detail from context statistics for the given resource and operation type.
     * 
     * @param resource
     *            the resource.
     * @param opType
     *            the operation type.
     * 
     * @return The detail.
     */
    private Map<String, Serializable> buildDetailFromContext(final String resource, final String opType) {
        final Map<String, Serializable> detail = new HashMap<>();
        switch (resource) {
        case COMPACT_AUDIT_JOB_RESOURCE:
            // creation of a job
            final UUID jobId = nscsContextService.getJobIdContextValue();
            if (jobId != null) {
                detail.put("opType", opType);
                detail.put("id", jobId.toString());
                final Map<String, Serializable> currentValues = new HashMap<>();
                final Integer valid = nscsContextService.getNumValidItemsContextValue();
                if (valid != null) {
                    currentValues.put("validNodes", valid);
                }
                final Integer invalid = nscsContextService.getNumInvalidItemsContextValue();
                if (invalid != null) {
                    currentValues.put("invalidNodes", invalid);
                }
                if (valid != null && invalid != null) {
                    currentValues.put("totalNodes", valid + invalid);
                }
                detail.put("currentValues", (Serializable) currentValues);
            }
            break;

        case COMPACT_AUDIT_PROXY_ACCOUNT_RESOURCE:
            // creation of a proxy account (ldap configure manual)
            final String proxyAccountName = nscsContextService.getProxyAccountNameContextValue();
            if (proxyAccountName != null) {
                detail.put("opType", opType);
                detail.put("id", proxyAccountName);
            }
            break;

        default:
            break;
        }
        return detail;
    }

    /**
     * Convert context statistics for a finished with success sync command to SDK recording compact audit additional info format containing the
     * summary result for the given resource and command type.
     * 
     * This method produces a JSON string to be used as additional info.
     * 
     * @param resource
     *            the resource.
     * @param cmdType
     *            the command type.
     * @return the JSON formatted additional info or null if no additional info is available.
     */
    private String toCompactAuditAdditionalInfoSummaryResult(final String resource, final NscsCommandType cmdType) {
        NscsCompactAuditLogAdditionalInfo calAdditionalInfo = null;
        final Map<String, Serializable> summary = buildSummaryFromContext(resource, cmdType);
        if (!summary.isEmpty()) {
            calAdditionalInfo = new NscsCompactAuditLogAdditionalInfo();
            final List<Map<String, Serializable>> summaryResult = new ArrayList<>();
            summaryResult.add(summary);
            calAdditionalInfo.setSummaryResult(summaryResult);
        }
        return toJsonString(calAdditionalInfo);
    }

    /**
     * Build the summary from context statistics for the given resource and command type.
     * 
     * @param resource
     *            the resource.
     * @param cmdType
     *            the command type.
     * 
     * @return The summary.
     */
    private Map<String, Serializable> buildSummaryFromContext(final String resource, final NscsCommandType cmdType) {
        final Map<String, Serializable> summary = new HashMap<>();
        summary.put("opType", cmdType.name().replaceAll(COMMAND_TYPE_WORD_SEPARATOR, SPACE_WORD_SEPARATOR).toLowerCase(Locale.ROOT));
        summary.put("entity", resource);
        final Map<String, Serializable> results = buildResultFromContext();
        summary.put("result", (Serializable) results);
        return summary;
    }

    /**
     * Build the result of summary from context statistics.
     * 
     * @return The result of summary.
     */
    private Map<String, Serializable> buildResultFromContext() {
        final Map<String, Serializable> results = new HashMap<>();
        final Integer valid = nscsContextService.getNumValidItemsContextValue();
        final Integer invalid = nscsContextService.getNumInvalidItemsContextValue();
        final Integer skipped = nscsContextService.getNumSkippedItemsContextValue();
        Integer total = null;
        if (valid != null && invalid != null) {
            total = valid + invalid;
            if (skipped != null) {
                total += skipped;
            }
        }
        if (total != null) {
            results.put("total", total);
        }
        if (valid != null) {
            results.put("valid", valid);
        }
        if (invalid != null) {
            results.put("invalid", invalid);
        }
        if (skipped != null) {
            results.put("skipped", skipped);
        }
        final Integer success = nscsContextService.getNumSuccessItemsContextValue();
        if (success != null) {
            results.put("success", success);
        }
        final Integer failed = nscsContextService.getNumFailedItemsContextValue();
        if (failed != null) {
            results.put("failed", failed);
        }
        return results;
    }

    /**
     * Serialize to JSON string the given Compact Audit Log additional info.
     * 
     * The null Compact Audit Log additional info or the null fields of a not null Compact Audit Log additional info are not included.
     * 
     * @param calAdditionalInfo
     *            the Compact Audit Log additional info.
     * @return the serialized JSON string or the string 'null' if the Compact Audit Log additional info is null.
     */
    private String toJsonString(final NscsCompactAuditLogAdditionalInfo calAdditionalInfo) {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        objectMapper.setSerializationInclusion(Include.NON_EMPTY);
        try {
            return objectMapper.writeValueAsString(calAdditionalInfo);
        } catch (final JsonProcessingException e) {
            logger.error("Exception occurred serializing to JSON string a Compact Audit Log additional info", e);
            return null;
        }
    }

    /**
     * Convert context statistics to SDK recording compact audit command phase for the given CAL mode.
     * 
     * @param calMode
     *            the CAL mode.
     * @return the command phase.
     */
    private CommandPhase toCompactAuditCommandPhase(final NscsCompactAuditLogMode calMode) {
        CommandPhase commandPhase = CommandPhase.FINISHED_WITH_SUCCESS;
        switch (calMode) {
        case COMPACT_AUDIT_LOGGED_SYNC_NODE_CMD:
            // break intentionally omitted
        case COMPACT_AUDIT_LOGGED_SYNC_PROXY_ACCOUNT_CMD:
            // break intentionally omitted
        case COMPACT_AUDIT_LOGGED_SYNC_NODE_CACHE_REST:
            // break intentionally omitted
        case COMPACT_AUDIT_LOGGED_SYNC_NODE_REST:
            final Integer invalid = nscsContextService.getNumInvalidItemsContextValue();
            final Integer failed = nscsContextService.getNumFailedItemsContextValue();
            final String errorDetail = nscsContextService.getErrorDetailContextValue();
            if ((invalid != null && invalid > 0) || (failed != null && failed > 0) || errorDetail != null) {
                commandPhase = CommandPhase.FINISHED_WITH_ERROR;
            }
            break;

        case COMPACT_AUDIT_LOGGED_ASYNC_NODE_CMD:
            final Integer valid = nscsContextService.getNumValidItemsContextValue();
            if (valid != null && valid <= 0) {
                commandPhase = CommandPhase.FINISHED_WITH_ERROR;
            }
            break;

        case COMPACT_AUDIT_LOGGED_GET_NODE_CMD:
            // break intentionally omitted
        case COMPACT_AUDIT_LOGGED_GET_PROXY_ACCOUNT_CMD:
            // break intentionally omitted
        case COMPACT_AUDIT_LOGGED_GET_JOB_CMD:
            // break intentionally omitted
        case COMPACT_AUDIT_LOGGED_GET_CAPABILITY_CMD:
            // break intentionally omitted
        case COMPACT_AUDIT_LOGGED_GET_UNKNOWN_CMD:
            // break intentionally omitted
        case COMPACT_AUDIT_LOGGED_SYNC_SINGLE_PROXY_ACCOUNT_CMD:
            // break intentionally omitted
        default:
            break;
        }
        return commandPhase;
    }

    /**
     * Records Compact Audit Log (CAL) for a given command text, command phase, resource and additional info.
     * 
     * This method actually invokes SFWK system recorder only if the resource is not null and if CAL parameters (user ID, session ID and source IP
     * address) are available in the context.
     * 
     * @param commandName
     *            the name of the command executed. Must not be null or empty String.
     * @param commandPhase
     *            the phase of command. Must not be null.
     * @param resource
     *            the entity directly affected by the command. Good examples for Node Security are a Node, a Proxy Account etc., in other words, the
     *            entity which directly relates to the command.
     * @param additionalInfo
     *            text with additional information. Must not exceed 50KB in size.
     */
    private void recordCommandCompactAudit(final String commandName, final CommandPhase commandPhase, final String resource,
            final String additionalInfo) {
        if (resource != null) {
            final String userId = nscsContextService.getUserIdContextValue();
            final String sessionId = nscsContextService.getSessionIdContextValue();
            final String sourceIpAddress = nscsContextService.getSourceIpAddrContextValue();
            logger.debug("CAL_DEBUG GEN: userId [{}] sessionId [{}] sourceIP [{}]", userId, sessionId != null ? "*******" : null, sourceIpAddress);
            if (hasContextCompactAuditLogParams(userId, sessionId, sourceIpAddress)) {
                /**
                 * Customers are parsing the following compact audit log and they need to be notified of changes.
                 */
                nscsSystemRecorder.recordCompactAudit(userId, commandName, commandPhase, resource, sourceIpAddress, sessionId, additionalInfo);
            } else {
                logger.warn("CAL params not present in context: userId [{}] sessionId [{}] sourceIP [{}]. This can happen in an upgrade scenario.",
                        userId, sessionId != null ? "*******" : null, sourceIpAddress);
            }
        }
    }

    /**
     * Returns if context contains the Compact Audit Log parameters.
     * 
     * The CAL parameters could be not present in the context in an upgrade scenario where the command was received by an old instance of script
     * engine not yet supporting CAL and managed by a new instance of node security already supporting CAL.
     * 
     * @param userId
     *            the user ID contained in the context.
     * @param sessionId
     *            the session ID contained in the context.
     * @param sourceIP
     *            the source IP address contained in the context.
     * @return true if CAL parameters are contained in the context, false otherwise.
     */
    private boolean hasContextCompactAuditLogParams(final String userId, final String sessionId, final String sourceIP) {
        return sessionId != null && sourceIP != null && userId != null;
    }

    /**
     * Get the Compact Audit resource according to the given CAL mode.
     * 
     * @param calMode
     *            the CAL mode.
     * @return the resource.
     */
    private String getCompactAuditResource(final NscsCompactAuditLogMode calMode) {
        String resource = COMPACT_AUDIT_UNKNOWN_RESOURCE;
        switch (calMode) {
        case COMPACT_AUDIT_LOGGED_GET_NODE_CMD:
            // break intentionally omitted
        case COMPACT_AUDIT_LOGGED_SYNC_NODE_CMD:
            // break intentionally omitted
        case COMPACT_AUDIT_LOGGED_SYNC_NODE_REST:
            resource = COMPACT_AUDIT_NODE_RESOURCE;
            break;

        case COMPACT_AUDIT_LOGGED_GET_JOB_CMD:
            // break intentionally omitted
        case COMPACT_AUDIT_LOGGED_ASYNC_NODE_CMD:
            resource = COMPACT_AUDIT_JOB_RESOURCE;
            break;

        case COMPACT_AUDIT_LOGGED_GET_CAPABILITY_CMD:
            resource = COMPACT_AUDIT_CAPABILITY_RESOURCE;
            break;

        case COMPACT_AUDIT_LOGGED_GET_PROXY_ACCOUNT_CMD:
            // break intentionally omitted
        case COMPACT_AUDIT_LOGGED_SYNC_PROXY_ACCOUNT_CMD:
            // break intentionally omitted
        case COMPACT_AUDIT_LOGGED_SYNC_SINGLE_PROXY_ACCOUNT_CMD:
            resource = COMPACT_AUDIT_PROXY_ACCOUNT_RESOURCE;
            break;

        case COMPACT_AUDIT_LOGGED_SYNC_NODE_CACHE_REST:
            resource = COMPACT_AUDIT_NODE_CACHE_RESOURCE;
            break;

        case COMPACT_AUDIT_LOGGED_GET_UNKNOWN_CMD:
            // break intentionally omitted
        default:
            break;
        }
        return resource;
    }

    /**
     * Convert context statistics of a finished with success REST to SDK recording compact audit additional info for the given CAL mode, resource and
     * operation slogan.
     * 
     * This method produces a JSON string to be used as additional info.
     * 
     * @param calMode
     *            the CAL mode.
     * @param resource
     *            the resource.
     * @param operationSlogan
     *            the operation slogan.
     * @return the JSON formatted additional info or null if no additional info is available.
     */
    private String toCompactAuditAdditionalInfo(final NscsCompactAuditLogMode calMode, final String resource, final String operationSlogan) {
        String additionalInfo = null;
        switch (calMode) {
        case COMPACT_AUDIT_LOGGED_SYNC_NODE_CACHE_REST:
            // break intentionally omitted
        case COMPACT_AUDIT_LOGGED_SYNC_NODE_REST:
            final String errorDetail = nscsContextService.getErrorDetailContextValue();
            if (errorDetail == null) {
                additionalInfo = toCompactAuditAdditionalInfoSummaryResult(resource, operationSlogan);
            } else {
                additionalInfo = toCompactAuditAdditionalInfoErrorDetail(errorDetail);
            }
            break;

        default:
            break;
        }
        return additionalInfo;
    }

    /**
     * Convert context statistics for a finished with success REST to SDK recording compact audit additional info format containing the summary result
     * for the given resource and operation slogan.
     * 
     * This method produces a JSON string to be used as additional info.
     * 
     * @param resource
     *            the resource.
     * @param operationSlogan
     *            the operation slogan.
     * @return the JSON formatted additional info or null if no additional info is available.
     */
    private String toCompactAuditAdditionalInfoSummaryResult(final String resource, final String operationSlogan) {
        NscsCompactAuditLogAdditionalInfo calAdditionalInfo = null;
        final Map<String, Serializable> summary = buildSummaryFromContext(resource, operationSlogan);
        if (!summary.isEmpty()) {
            calAdditionalInfo = new NscsCompactAuditLogAdditionalInfo();
            final List<Map<String, Serializable>> summaryResult = new ArrayList<>();
            summaryResult.add(summary);
            calAdditionalInfo.setSummaryResult(summaryResult);
        }
        return toJsonString(calAdditionalInfo);
    }

    /**
     * Build the summary from context statistics for the given resource and operation slogan.
     * 
     * @param resource
     *            the resource.
     * @param operationSlogan
     *            the operation slogan.
     * 
     * @return The summary.
     */
    private Map<String, Serializable> buildSummaryFromContext(final String resource, final String operationSlogan) {
        final Map<String, Serializable> summary = new HashMap<>();
        summary.put("opType", operationSlogan.toLowerCase(Locale.ROOT));
        summary.put("entity", resource);
        final Map<String, Serializable> results = buildResultFromContext();
        summary.put("result", (Serializable) results);
        return summary;
    }

    /**
     * Returns if the given rest URL path and rest method are to be Compact Audit Logged according to the context Compact Audit Log parameters.
     * 
     * @param restUrlPath
     *            the rest URL path.
     * @param restMethod
     *            the rest method.
     * @return true if logged, false otherwise.
     */
    private boolean isRestToBeCompactAuditLogged(final String restUrlPath, final String restMethod) {
        boolean isLogged = false;
        switch (restUrlPath) {
        case "/node-security/2.0/nodes":
            final Integer valid = nscsContextService.getNumValidItemsContextValue();
            final Integer invalid = nscsContextService.getNumInvalidItemsContextValue();
            final String errorDetail = nscsContextService.getErrorDetailContextValue();
            if ((valid != null && valid > 0) || (invalid != null && invalid > 0) || errorDetail != null) {
                isLogged = true;
            }
            break;

        case "/node-security/nodes/seclevel":
            isLogged = true;
            break;

        default:
            logger.debug("REST_CAL not CALled restUrlPath [{}] method [{}]", restUrlPath, restMethod);
            break;
        }
        return isLogged;
    }

    /**
     * Get the operation slogan for the given rest URL path and rest method.
     * 
     * It is an UPPERCASE string. It shall be a short operation descriptor (to be enclosed in angle brackets); words must be separated by single
     * “blank” character (hyphen or underscore are not allowed as separator).
     * 
     * @param restUrlPath
     *            the rest URL path.
     * @param restMethod
     *            the rest method.
     * @return the operation slogan or null if the operation did not change anything.
     */
    private String getOperationSlogan(final String restUrlPath, final String restMethod) {
        String operationSlogan = null;
        switch (restUrlPath) {
        case "/node-security/2.0/nodes":
            operationSlogan = "CREATE NODE CACHE";
            break;

        case "/node-security/nodes/seclevel":
            operationSlogan = "START SECURITY LEVEL SWITCH";
            break;

        default:
            logger.debug("REST_CAL not CALled restUrlPath [{}] method[{}]", restUrlPath, restMethod);
            break;
        }
        return operationSlogan;
    }

}
