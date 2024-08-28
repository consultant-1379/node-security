/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2024
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.nbi.logger;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.recording.CommandPhase;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Auxiliary class to manage logger and system recorder in NBI.
 */
public class NbiLogger {

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
    private static final String COMPACT_AUDIT_JOB_RESOURCE = "Job";

    private static final String COMMAND_NSCS_NBI_SOURCE = "Node Security";
    private static final String COMPACT_AUDIT_NSCS_NBI_SOURCE = "Node Security";

    /**
     * DDP Event Data.
     */
    // NSCS namespace
    private static final String NSCS_NS = "NODE_SECURITY";
    private static final String NBI_REST_COMPLETED_EVENT_DATA_TYPE = "NBI_REST_COMPLETED";

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private NbiLogRecorderDto restLogRecorderDto;

    @Inject
    private SystemRecorder systemRecorder;

    /**
     * Log and record a REST started.
     */
    public void recordRestStarted() {
        /**
         * Customers are parsing the following log and they need to be notified of changes.
         */
        if (!systemRecorder.isCompactAuditEnabled()) {
            recordRestStartedCommandLogger();
        }
    }

    /**
     * Log and record a REST finished with success.
     */
    public void recordRestFinishedWithSuccess() {
        /**
         * DDP is parsing the following event data and DDP team needs to be notified of changes
         */
        recordEventData(getEventDataType(NBI_REST_COMPLETED_EVENT_DATA_TYPE), toEventData(EventDataNbiRestResult.SUCCESS));

        /**
         * Customers are parsing the following log and they need to be notified of changes.
         */
        if (systemRecorder.isCompactAuditEnabled()) {
            recordRestFinishedWithSuccessCompactAuditLogger();
        } else {
            recordRestFinishedWithSuccessCommandLogger();
        }
    }

    /**
     * Log and record a REST finished with error.
     */
    public void recordRestFinishedWithError() {
        /**
         * DDP is parsing the following event data and DDP team needs to be notified of changes
         */
        recordEventData(getEventDataType(NBI_REST_COMPLETED_EVENT_DATA_TYPE), toEventData(EventDataNbiRestResult.ERROR));

        /**
         * Customers are parsing the following log and they need to be notified of changes.
         */
        if (systemRecorder.isCompactAuditEnabled()) {
            recordRestFinishedWithErrorCompactAuditLogger();
        } else {
            recordRestFinishedWithErrorCommandLogger();
        }
    }

    /**
     * Log and record a completed job.
     * 
     * @param restLogRecorder
     *            the log recorder containing the relevant info for the completed job.
     */
    public void recordJobCompleted(final RestLogRecorder restLogRecorder) {
        /**
         * Customers are parsing the following compact audit log and they need to be notified of changes.
         */
        if (systemRecorder.isCompactAuditEnabled()) {
            recordJobCompletedCompactAuditLogger(restLogRecorder);
        }
    }

    /**
     * Records Command Log when a REST starts.
     */
    private void recordRestStartedCommandLogger() {
        final String method = restLogRecorderDto.getMethod();
        logger.debug("NBI_CL method [{}]", method);
        final NbiCompactAuditLogMode calMode = NbiCompactAuditLogMode.COMPACT_AUDIT_LOGGED_SYNC_NODES_REST;
        logger.debug("NBI_CL calMode [{}]", calMode);
        final String commandName = toCommandName(method);
        logger.debug("NBI_CL commandName [{}]", commandName);
        CommandPhase commandPhase = CommandPhase.STARTED;
        logger.debug("NBI_CL commandPhase [{}]", commandPhase);
        final String resource = getCompactAuditResource(calMode);
        logger.debug("NBI_CL resource [{}]", resource);
        final String source = COMMAND_NSCS_NBI_SOURCE;
        logger.debug("NBI_CL source [{}]", source);
        final String additionalInfo = null;
        logger.debug("NBI_CL additionalInfo [{}]", additionalInfo);
        systemRecorder.recordCommand(commandName, commandPhase, source, resource, additionalInfo);
    }

    /**
     * Records Command Log when a REST finishes with success.
     * 
     * A REST finishes with success if the processing does not throw an exception.
     * 
     * This does not necessarily mean that the command has been successfully executed on all requested resources because some resources could have
     * been invalid or the rest could have failed for some valid resources.
     */
    private void recordRestFinishedWithSuccessCommandLogger() {
        final boolean isFinishedWithError = false;
        recordRestFinishedCommandLogger(isFinishedWithError);
    }

    /**
     * Records Command Log when a REST finishes with error.
     * 
     * A REST finishes with success if the processing throws an exception.
     */
    private void recordRestFinishedWithErrorCommandLogger() {
        final boolean isFinishedWithError = true;
        recordRestFinishedCommandLogger(isFinishedWithError);
    }

    /**
     * Records Command Log (CAL) when a REST finishes.
     * 
     * @param isFinishedWithError
     *            true if finished with error, true otherwise.
     */
    private void recordRestFinishedCommandLogger(final boolean isFinishedWithError) {
        final String method = restLogRecorderDto.getMethod();
        logger.debug("NBI_CL method [{}]", method);
        final NbiCompactAuditLogMode calMode = NbiCompactAuditLogMode.COMPACT_AUDIT_LOGGED_SYNC_NODES_REST;
        logger.debug("NBI_CL calMode [{}]", calMode);
        final String commandName = toCommandName(method);
        logger.debug("NBI_CL commandName [{}]", commandName);
        final CompactAuditLogAdditionalInfo calAdditionalInfo = restLogRecorderDto.getAdditionalInfo();
        CommandPhase commandPhase = CommandPhase.FINISHED_WITH_SUCCESS;
        if (calAdditionalInfo.getErrorDetail() != null || isFinishedWithError) {
            commandPhase = CommandPhase.FINISHED_WITH_ERROR;
        }
        logger.debug("NBI_CL commandPhase [{}]", commandPhase);
        final String resource = getCompactAuditResource(calMode);
        logger.debug("NBI_CL resource [{}]", resource);
        final String additionalInfo = getCompactAuditLogAdditionalInfo(calAdditionalInfo);
        logger.debug("NBI_CL additionalInfo [{}]", additionalInfo);
        final String source = COMMAND_NSCS_NBI_SOURCE;
        logger.debug("NBI_CL source [{}]", source);
        systemRecorder.recordCommand(commandName, commandPhase, source, resource, additionalInfo);
    }

    /**
     * Records Compact Audit Log (CAL) when a REST finishes with success.
     * 
     * A REST finishes with success if the processing does not throw an exception.
     * 
     * This does not necessarily mean that the command has been successfully executed on all requested resources because some resources could have
     * been invalid or the rest could have failed for some valid resources.
     * 
     * This method actually invokes SFWK system recorder only if rest resource path, user ID, session ID and source IP address are available.
     */
    private void recordRestFinishedWithSuccessCompactAuditLogger() {
        final boolean isFinishedWithError = false;
        recordRestFinishedCompactAudit(isFinishedWithError);
    }

    /**
     * Records Compact Audit Log (CAL) when a REST finishes with error.
     * 
     * A REST finishes with error if the processing throws an exception.
     * 
     * This method actually invokes SFWK system recorder only if rest resource path, user ID, session ID and source IP address are available.
     */
    private void recordRestFinishedWithErrorCompactAuditLogger() {
        final boolean isFinishedWithError = true;
        recordRestFinishedCompactAudit(isFinishedWithError);
    }

    /**
     * Records Compact Audit Log (CAL) for a job completed.
     * 
     * @param restLogRecorder
     *            the log recorder containing the relevant info for the completed job.
     */
    private void recordJobCompletedCompactAuditLogger(final RestLogRecorder restLogRecorder) {
        final String jobId = restLogRecorder.getJobId();
        logger.debug("NBI_CAL jobId [{}]", jobId);
        final NbiCompactAuditLogMode calMode = NbiCompactAuditLogMode.COMPACT_AUDIT_LOGGED_ASYNC_NODES_REST;
        logger.debug("NBI_CAL calMode [{}]", calMode);
        if (!NbiCompactAuditLogMode.NOT_COMPACT_AUDIT_LOGGED.equals(calMode)) {
            final String commandName = toJobCompletedCompactAuditCommandName(jobId);
            logger.debug("NBI_CAL commandName [{}]", commandName);
            final CommandPhase commandPhase = CommandPhase.EXECUTED;
            logger.debug("NBI_CAL commandPhase [{}]", commandPhase);
            final String resource = getJobCompletedCompactAuditResource(calMode);
            logger.debug("NBI_CAL resource [{}]", resource);
            final String additionalInfo = getCompactAuditLogAdditionalInfo(restLogRecorder.getAdditionalInfo());
            logger.debug("NBI_CAL additionalInfo [{}]", additionalInfo);
            recordCommandCompactAudit(commandName, commandPhase, resource, additionalInfo, restLogRecorder);
        }
    }

    /**
     * Records Compact Audit Log (CAL) when a REST finishes.
     * 
     * @param isFinishedWithError
     *            true if finished with error, true otherwise.
     */
    private void recordRestFinishedCompactAudit(final boolean isFinishedWithError) {
        final String method = restLogRecorderDto.getMethod();
        logger.debug("NBI_CAL method [{}]", method);
        final NbiCompactAuditLogMode calMode = NbiCompactAuditLogMode.COMPACT_AUDIT_LOGGED_SYNC_NODES_REST;
        logger.debug("NBI_CAL calMode [{}]", calMode);
        if (!NbiCompactAuditLogMode.NOT_COMPACT_AUDIT_LOGGED.equals(calMode)) {
            final String commandName = toCommandName(method);
            final CompactAuditLogAdditionalInfo calAdditionalInfo = restLogRecorderDto.getAdditionalInfo();
            CommandPhase commandPhase = CommandPhase.FINISHED_WITH_SUCCESS;
            if (calAdditionalInfo.getErrorDetail() != null || isFinishedWithError) {
                commandPhase = CommandPhase.FINISHED_WITH_ERROR;
            }
            logger.debug("NBI_CAL commandPhase [{}]", commandPhase);
            final String resource = getCompactAuditResource(calMode);
            logger.debug("NBI_CAL resource [{}]", resource);
            final String additionalInfo = getCompactAuditLogAdditionalInfo(calAdditionalInfo);
            logger.debug("NBI_CAL additionalInfo [{}]", additionalInfo);
            recordCommandCompactAudit(commandName, commandPhase, resource, additionalInfo);
        }
    }

    /**
     * Get the compact audit log additional info as string.
     * 
     * @param calAdditionalInfo
     *            the Compact Audit Log additional info object.
     * @return the compact audit log additional info as string.
     */
    private String getCompactAuditLogAdditionalInfo(final CompactAuditLogAdditionalInfo calAdditionalInfo) {
        String additionalInfo = null;
        try {
            additionalInfo = toJsonString(calAdditionalInfo);
        } catch (final JsonProcessingException e) {
            logger.error("NBI_CAL JsonProcessingException occurred invoking toJsonString on CompactAuditLogAdditionalInfo", e);
        }
        return additionalInfo;
    }

    /**
     * Serialize to JSON string the given Compact Audit Log additional info.
     * 
     * The null Compact Audit Log additional info or the null fields of a not null Compact Audit Log additional info are not included.
     * 
     * @return the serialized JSON string.
     * @throws {@link
     *             JsonProcessingException} if serialization fails.
     */
    private String toJsonString(final CompactAuditLogAdditionalInfo calAdditionalInfo) throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        objectMapper.setSerializationInclusion(Include.NON_EMPTY);
        return objectMapper.writeValueAsString(calAdditionalInfo);
    }

    /**
     * Convert to SDK recording command name for the given finished operation.
     * 
     * The format is:
     * 
     * <OPERATION_SLOGAN> - REST_method Resource: Resource_path Body: Request_Payload
     * 
     * where:
     * 
     * - OPERATION_SLOGAN: an UPPERCASE string. It shall be a short operation descriptor, enclosed in angle brackets; words must be separated by
     * single “blank” character (hyphen or underscore are not allowed as separator).
     * 
     * - REST_method: UPPERCASE REST method.
     * 
     * - Resource_path: the resource part of the header URL, including optional query parameters. The “scheme” and “host” components in URL leftmost
     * section must be skipped.
     * 
     * - Request_Payload: raw string representation of the request payload. This will usually include all the parameters set by the user to perform
     * the requested action. If the payload includes a file name, then the file content must not be recorded in the log.
     * 
     * @param method
     *            the method.
     * @return the command name.
     */
    private String toCommandName(final String method) {
        final String operationSlogan = toOperationSlogan();
        logger.debug("NBI_CAL operationSlogan [{}]", operationSlogan);
        final String urlFile = restLogRecorderDto.getUrlFile();
        logger.debug("NBI_CAL urlFile [{}]", urlFile);
        final String requestPayload = getRequestPayload();
        logger.debug("NBI_CAL requestPayload [{}]", requestPayload);
        // <OPERATION_SLOGAN> - REST_method  Resource: Resource_path   Body: Request_Payload
        final String commandName = String.format("<%s> - %s Resource: %s Body: %s", operationSlogan, method, urlFile, requestPayload);
        logger.debug("NBI_CAL commandName [{}]", commandName);
        return commandName;
    }

    /**
     * Convert to SDK recording operation slogan.
     * 
     * The operation slogan shall be an UPPERCASE string. It shall be a short operation descriptor; words must be separated by single “blank”
     * character (hyphen or underscore are not allowed as separator).
     * 
     * @return the operation slogan.
     */
    private String toOperationSlogan() {
        final EventDataNbiRestIdentifier restId = getRestId();
        logger.debug("NBI_REST restId [{}]", restId);
        if (restId == null) {
            return null;
        }
        return restId.toOperationSlogan();
    }

    /**
     * Get the request payload.
     * 
     * For privacy constraints it can be obfuscated.
     * 
     * @return the request payload or "*******" if obfuscated.
     */
    private String getRequestPayload() {
        String requestPayload = restLogRecorderDto.getRequestPayload();
        final EventDataNbiRestIdentifier restId = getRestId();
        if (restId == null || EventDataNbiRestIdentifier.V1_NODE_CREDENTIALS_PUT.equals(restId)
                || EventDataNbiRestIdentifier.V1_NODE_SNMP_PUT.equals(restId)) {
            requestPayload = "*******";
        }
        return requestPayload;
    }

    /**
     * Get the REST identifier.
     * 
     * @return the REST identifier.
     */
    private EventDataNbiRestIdentifier getRestId() {
        final String urlPath = restLogRecorderDto.getUrlPath();
        logger.debug("NBI_CL urlPath [{}]", urlPath);
        final String method = restLogRecorderDto.getMethod();
        logger.debug("NBI_CAL method [{}]", method);
        EventDataNbiRestIdentifier restId = null;
        switch (urlPath) {
        case "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/credentials":
            if ("PUT".equals(method)) {
                restId = EventDataNbiRestIdentifier.V1_NODE_CREDENTIALS_PUT;
            }
            break;

        case "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/snmp":
            if ("PUT".equals(method)) {
                restId = EventDataNbiRestIdentifier.V1_NODE_SNMP_PUT;
            }
            break;

        case "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/domains/domainName":
            if ("POST".equals(method)) {
                restId = EventDataNbiRestIdentifier.V1_NODE_DOMAIN_POST;
            } else if ("DELETE".equals(method)) {
                restId = EventDataNbiRestIdentifier.V1_NODE_DOMAIN_DELETE;
            }
            break;

        case "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/ldap":
            if ("POST".equals(method)) {
                restId = EventDataNbiRestIdentifier.V1_NODE_LDAP_POST;
            } else if ("DELETE".equals(method)) {
                restId = EventDataNbiRestIdentifier.V1_NODE_LDAP_DELETE;
            }
            break;

        default:
            break;
        }
        return restId;
    }

    /**
     * Convert to SDK recording compact audit command name for the given completed job representing a sync action.
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
     * @param jobId
     *            the job ID.
     * @return the command name.
     */
    private String toJobCompletedCompactAuditCommandName(final String jobId) {
        final String operationSlogan = getOperationSlogan(jobId);
        final String jobIdentifier = jobId;
        return String.format("<%s> - Job Result jobId: %s", operationSlogan, jobIdentifier);
    }

    /**
     * Get the operation slogan for the given job.
     * 
     * It is an UPPERCASE string. It shall be a short operation descriptor (to be enclosed in angle brackets); words must be separated by single
     * “blank” character (hyphen or underscore are not allowed as separator).
     * 
     * @param jobId
     *            the job ID.
     * @return the operation slogan.
     */
    private String getOperationSlogan(final String jobId) {
        return jobId.toUpperCase(Locale.ENGLISH);
    }

    /**
     * Get the Compact Audit resource according to the given CAL mode.
     * 
     * @param calMode
     *            the CAL mode.
     * @return the resource.
     */
    private String getCompactAuditResource(final NbiCompactAuditLogMode calMode) {
        String resource = COMPACT_AUDIT_UNKNOWN_RESOURCE;
        switch (calMode) {
        case COMPACT_AUDIT_LOGGED_SYNC_NODES_REST:
            resource = COMPACT_AUDIT_NODE_RESOURCE;
            break;

        case COMPACT_AUDIT_LOGGED_ASYNC_NODES_REST:
            resource = COMPACT_AUDIT_JOB_RESOURCE;
            break;

        default:
            break;
        }
        return resource;
    }

    /**
     * Get the Compact Audit resource for a completed job according to the given CAL mode.
     * 
     * @param calMode
     *            the CAL mode.
     * @return the resource.
     */
    private String getJobCompletedCompactAuditResource(final NbiCompactAuditLogMode calMode) {
        String resource = COMPACT_AUDIT_UNKNOWN_RESOURCE;
        if (NbiCompactAuditLogMode.COMPACT_AUDIT_LOGGED_ASYNC_NODES_REST.equals(calMode)) {
            resource = COMPACT_AUDIT_NODE_RESOURCE;
        }
        return resource;
    }

    /**
     * Records Compact Audit Log (CAL) for a given command text, command phase, resource and additional info.
     * 
     * This method actually invokes SFWK system recorder only if the resource is not null.
     * 
     * @param commandName
     *            the name of the command executed. Must not be null or empty String.
     * @param commandPhase
     *            the phase of command. Must not be null.
     * @param resource
     *            the entity directly affected by the command. Good examples for FIDM are FIDM Sync, Federated User etc., in other words, the entity
     *            which directly relates to the REST.
     * @param additionalInfo
     *            text with additional information. Must not exceed 50KB in size.
     */
    private void recordCommandCompactAudit(final String commandName, final CommandPhase commandPhase, final String resource,
            final String additionalInfo) {
        if (resource != null) {
            final String userId = restLogRecorderDto.getUserId();
            final String sessionId = restLogRecorderDto.getSessionId();
            final String sourceIpAddress = restLogRecorderDto.getSourceIpAddr();
            recordCommandCompactAudit(userId, commandName, commandPhase, resource, sourceIpAddress, sessionId, additionalInfo);
        }
    }

    /**
     * Records Compact Audit Log (CAL) for a given command text, command phase, resource, additional info and REST log recorder.
     * 
     * This method actually invokes SFWK system recorder only if the resource is not null.
     * 
     * @param commandName
     *            the name of the command executed. Must not be null or empty String.
     * @param commandPhase
     *            the phase of command. Must not be null.
     * @param resource
     *            the entity directly affected by the command. Good examples for FIDM are FIDM Sync, Federated User etc., in other words, the entity
     *            which directly relates to the REST.
     * @param additionalInfo
     *            text with additional information. Must not exceed 50KB in size.
     * @param restLogRecorder
     *            The REST log recorder.
     */
    private void recordCommandCompactAudit(final String commandName, final CommandPhase commandPhase, final String resource,
            final String additionalInfo, final RestLogRecorder restLogRecorder) {
        if (resource != null) {
            final String userId = restLogRecorder.getUserId();
            final String sessionId = restLogRecorder.getSessionId();
            final String sourceIpAddress = restLogRecorder.getSourceIpAddr();
            recordCommandCompactAudit(userId, commandName, commandPhase, resource, sourceIpAddress, sessionId, additionalInfo);
        }
    }

    /**
     * Records Compact Audit Log (CAL).
     * 
     * This method actually invokes SFWK system recorder only if CAL parameters (user ID, session ID and source IP address) are available.
     * 
     * @param userId
     *            the user ID.
     * @param commandName
     *            the name of the command executed. Must not be null or empty String.
     * @param commandPhase
     *            the phase of command. Must not be null.
     * @param resource
     *            the entity directly affected by the command. Good examples for FIDM are FIDM Sync, Federated User etc., in other words, the entity
     *            which directly relates to the REST.
     * @param sourceIpAddress
     *            the source IP address.
     * @param sessionId
     *            the session ID.
     * @param additionalInfo
     *            text with additional information. Must not exceed 50KB in size.
     */
    private void recordCommandCompactAudit(final String userId, final String commandName, final CommandPhase commandPhase, final String resource,
            final String sourceIpAddress, final String sessionId, final String additionalInfo) {
        logger.debug("NBI_CAL userId [{}] sessionId [{}] sourceIP [{}]", userId, sessionId != null ? "*******" : null, sourceIpAddress);
        if (hasContextCompactAuditLogParams(userId, sessionId, sourceIpAddress)) {
            /**
             * Customers are parsing the following compact audit log and they need to be notified of changes.
             */
            recordCompactAudit(userId, commandName, commandPhase, resource, sourceIpAddress, sessionId, additionalInfo);
        } else {
            logger.error("NBI_CAL CAL params not available: userId [{}] sessionId [{}] sourceIP [{}].", userId, sessionId != null ? "*******" : null,
                    sourceIpAddress);
        }
    }

    /**
     * Returns if context contains the Compact Audit Log parameters.
     * 
     * The CAL parameters should be always present.
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
     * Records Compact Audit Log (CAL).
     * 
     * @param userId
     *            the user ID.
     * @param commandName
     *            the name of the command executed. Must not be null or empty String.
     * @param commandPhase
     *            the phase of command. Must not be null.
     * @param resource
     *            the entity directly affected by the command. Good examples for FIDM are FIDM Sync, Federated User etc., in other words, the entity
     *            which directly relates to the command.
     * @param sourceIP
     *            the source IP Address.
     * @param sessionId
     *            the session id.
     * @param additionalInfo
     *            text with additional information. Must not exceed 50KB in size.
     */
    private void recordCompactAudit(final String userId, final String commandName, final CommandPhase commandPhase, final String resource,
            final String sourceIP, final String sessionId, final String additionalInfo) {
        systemRecorder.recordCompactAudit(userId, commandName, commandPhase, COMPACT_AUDIT_NSCS_NBI_SOURCE, resource, sourceIP, sessionId,
                additionalInfo);
    }

    /**
     * Returns the NSCS event data type for the given event type.
     * 
     * @param eventType
     *            the type of the event recorded. Must not be null or empty String or contain white space. The event type is considered API, i.e. once
     *            baselined may not be modified spontaneously. Consumers of recordings may code their logic against event types. The event type should
     *            contain a simplified namespace to avoid name clashes, e.g. "NODE_SECURITY.NBI_REST_COMPLETED".
     * @return the NSCS event data type.
     */
    private String getEventDataType(final String eventType) {
        return String.format("%s.%s", NSCS_NS, eventType);
    }

    /**
     * Converts NBI REST statistics to SDK recording event data format.
     * 
     * @param result
     *            the NBI REST result.
     * @return a Map containing the key-value pairs for the event data. The names must be non-empty strings, white space in the key is prohibited. The
     *         value must be non-null. For privacy and security reasons, usernames / user IDs / IP addresses and any types of PII data are not
     *         allowed.
     */
    private Map<String, Object> toEventData(final EventDataNbiRestResult result) {
        final Map<String, Object> eventData = new HashMap<>();
        eventData.put("REST_ID", getRestId());
        eventData.put("REST_DURATION", toRestDurationInSec());
        eventData.put("REST_RESULT", result.toEventData());
        return eventData;
    }

    /**
     * Gets the duration (in seconds) of the NBI REST.
     * 
     * @return the duration (in seconds) of the NBI REST
     */
    private Integer toRestDurationInSec() {
        final Date urlStartDate = restLogRecorderDto.getStartDate();
        final Date now = new Date();
        final Long restDurationInMillis = getDurationInMillis(urlStartDate, now);
        return getDurationInSec(restDurationInMillis);
    }

    /**
     * Gets the duration (in milliseconds) between a start and an end date.
     * 
     * @param startDate
     *            the start date.
     * @param endDate
     *            the end date.
     * @return the duration (in milliseconds) or 0 if any of the dates is null or start date is after or equal to end date.
     */
    private Long getDurationInMillis(final Date startDate, final Date endDate) {
        Long durationInMillis = 0L;
        if (startDate != null && endDate != null) {
            if (startDate.equals(endDate)) {
                durationInMillis = 0L;
            } else if (startDate.before(endDate)) {
                durationInMillis = endDate.getTime() - startDate.getTime();
            }
        }
        return durationInMillis;
    }

    /**
     * Gets the duration (in seconds) of a given duration expressed in milliseconds.
     * 
     * @param durationInMillis
     *            the duration expressed in milliseconds.
     * @return the duration (in seconds).
     */
    private Integer getDurationInSec(final Long durationInMillis) {
        Long durationInSec = 0L;
        if (durationInMillis > 0L) {
            durationInSec = durationInMillis / 1000L + ((durationInMillis % 1000) == 0L ? 0L : 1L);
        }
        return durationInSec.intValue();
    }

    /**
     * Records an event data.
     * 
     * @param eventType
     *            the type of the event recorded. Must not be null or empty String or contain white space. The event type is considered API, i.e. once
     *            baselined may not be modified spontaneously. Consumers of recordings may code their logic against event types. The event type should
     *            contain a simplified namespace to avoid name clashes, e.g. "NODE_SECURITY.NBI_REST_COMPLETED".
     * @param eventData
     *            a Map containing the key-value pairs for the event data. The names must be non-empty strings, white space in the key is prohibited
     *            The value must be non-null For privacy and security reasons, usernames / user IDs / IP addresses and any types of PII data are not
     *            allowed.
     */
    private void recordEventData(final String eventType, final Map<String, Object> eventData) {
        systemRecorder.recordEventData(eventType, eventData);
    }

}
