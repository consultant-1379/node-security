/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.handler.command.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse;
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManager;
import com.ericsson.nms.security.nscs.api.command.types.CrlCheckCommand;
import com.ericsson.nms.security.nscs.api.exception.CrlCheckEnableOrDisableWfException;
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.handler.validation.UseValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.StarIsNotAllowedValidator;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NscsInputNodeRetrievalUtility;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

/**
 * <p>
 * Initiates the process to set the crlcheck attribute as activated(disable) on a set of nodes.
 * </p>
 * 
 * @author xchowja
 *
 */
@UseValidator({ StarIsNotAllowedValidator.class })
@CommandType(NscsCommandType.CRL_CHECK_DISABLE)
@Local(CommandHandlerInterface.class)
public class CrlCheckDisableHandler implements CommandHandler<CrlCheckCommand>, CommandHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @EJB
    private NscsCommandManager commandManager;

    @Inject
    private NscsJobCacheHandler cacheHandler;

    @Inject
    private NscsInputNodeRetrievalUtility nscsInputNodeRetrievalUtility;

    @Inject
    private NscsContextService nscsContextService;

    public static final String CRLCHECK_DISABLE_EXECUTED = "Successfully started a job for CRL Check disable operation. Perform 'secadm job get -j %s' to get progress info.";
    public static final String CRLCHECK_DISABLE_EXECUTED_DYN_ISSUE = "Successfully started a job for CRL Check disable operation for some node(s). Perform 'secadm job get -j %s' to get progress info of valid nodes. Invalid node details are given below :";
    public static final String CRLCHECK_DISABLE_NOT_EXECUTED = "Failed to start the job for CRL Check disable operation as all the provided node(s) are invalid. Invalid node details are given below :";
    public static final String CRLCHECK_DISABLE_WF_FAILED = " during CRL Check disable operation.";

    public static final String[] ERROR_HEADER = new String[] { "Node Name", "Error Code", "Error Detail", "Suggested Solution" };

    private static final int NO_OF_COLUMNS = 3;
    private static final String CRL_CHECK_STATUS = "DEACTIVATED";

    @Override
    public NscsCommandResponse process(final CrlCheckCommand command, final CommandContext context) throws NscsServiceException {
        String responseMessageWithJobId = "";
        JobStatusRecord jobStatusRecord = null;
        nscsLogger.commandHandlerStarted(command);
        final long startTime = System.currentTimeMillis();
        final String certType = command.getCertType();
        nscsLogger.debug("certType[{}]", certType);
        if (!CrlCheckCommand.ALL.equals(certType) && !commandManager.validateCertTypeValue(certType)) {
            nscsLogger.error(NscsErrorCodes.INVALID_ARGUMENT_VALUE);
            List<String> validCertTypes = new ArrayList<String>();
            validCertTypes.addAll(commandManager.getValidCertificateTypes());
            validCertTypes.add(CrlCheckCommand.ALL);
            final String errmsg = String.format(" Invalid argument for parameter %s", CrlCheckCommand.CERT_TYPE_PROPERTY + ". Accepted arguments are " + validCertTypes);
            nscsLogger.error(errmsg);
            nscsLogger.commandHandlerFinishedWithError(command, errmsg);
            throw new InvalidArgumentValueException(errmsg);
        }
        final List<NodeReference> validNodesList = new ArrayList<NodeReference>();
        final Map<NodeReference, NscsServiceException> invalidNodesErrorMap = new HashMap<>();
        final Map<String, String[]> invalidDynamicNodesMap = new HashMap<>();
        final List<NodeReference> inputNodes = nscsInputNodeRetrievalUtility.getNodeReferenceList(command);
        final List<NodeReference> uniqueNodes = new ArrayList<NodeReference>(new HashSet<NodeReference>(inputNodes));
        nscsLogger.debug("Number of input nodes {}", uniqueNodes.size());
        final boolean areInputNodesValid = commandManager.validateNodesForCrlCheck(uniqueNodes, certType, validNodesList, invalidNodesErrorMap, invalidDynamicNodesMap, false);

        nscsContextService.initItemsStatsForAsyncCommand(Integer.valueOf(validNodesList.size()), Integer.valueOf(invalidNodesErrorMap.size()));

        if (areInputNodesValid) {
            try {
            	jobStatusRecord = cacheHandler.insertJob(NscsCommandType.CRL_CHECK_DISABLE);
                nscsLogger.info("All of the given input nodes are Valid.CrlCheckDisable workflow need to be executed.");
                commandManager.executeCrlCheckWfs(validNodesList, command.getCertType(), CRL_CHECK_STATUS, jobStatusRecord);
            } catch (Exception ex) {
                nscsLogger.error(ex.getMessage(), ex);
                nscsLogger.commandHandlerFinishedWithError(command, ex.getMessage());
                throw new CrlCheckEnableOrDisableWfException(CRLCHECK_DISABLE_WF_FAILED);
            } finally {
                final long endTime = System.currentTimeMillis();
                nscsLogger.info("Total elapsed time for CrlCheck Disable Handler: " + String.format("%.3f", (endTime - startTime) / 1000f));
            }
            responseMessageWithJobId = String.format(CRLCHECK_DISABLE_EXECUTED, jobStatusRecord.getJobId().toString());
            nscsLogger.commandHandlerFinishedWithSuccess(command, "workflow started successfully for any instance");
            return NscsCommandResponse.message(responseMessageWithJobId);
        } else {
            final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(NO_OF_COLUMNS);
            response.add(ERROR_HEADER[0], Arrays.copyOfRange(ERROR_HEADER, 1, ERROR_HEADER.length));
            if (!validNodesList.isEmpty()) {

                nscsLogger.debug("Number of Valid Nodes are :{}", validNodesList.size());
                try {
                	jobStatusRecord = cacheHandler.insertJob(NscsCommandType.CRL_CHECK_DISABLE);
                    nscsLogger.info("CrlCheckDisable workflow need to be executed for valid nodes.");
                    commandManager.executeCrlCheckWfs(validNodesList, command.getCertType(), CRL_CHECK_STATUS, jobStatusRecord);
                    responseMessageWithJobId = String.format(CRLCHECK_DISABLE_EXECUTED_DYN_ISSUE, jobStatusRecord.getJobId().toString());
                } catch (Exception ex) {
                    nscsLogger.error(ex.getMessage(), ex);
                    nscsLogger.commandHandlerFinishedWithError(command, ex.getMessage());
                    throw new CrlCheckEnableOrDisableWfException(CRLCHECK_DISABLE_WF_FAILED);
                } finally {
                    final long endTime = System.currentTimeMillis();
                    nscsLogger.info("Total elapsed time for CrlCheck Disable Handler: " + String.format("%.3f", (endTime - startTime) / 1000f));
                }

                if (!invalidDynamicNodesMap.isEmpty() && invalidDynamicNodesMap.size() == invalidNodesErrorMap.size()) {
                    final Set<java.util.Map.Entry<String, String[]>> entrySet = invalidDynamicNodesMap.entrySet();
                    nscsLogger.debug("invalidDynamicNodesMap size is {}", invalidDynamicNodesMap.size());
                    for (java.util.Map.Entry<String, String[]> entry : entrySet) {
                        response.add(entry.getKey(), entry.getValue());
                    }
                } else if (!invalidNodesErrorMap.isEmpty()) {
                    nscsLogger.debug("invalidNodesErrorMap size is {}", invalidNodesErrorMap.size());
                    final Set<java.util.Map.Entry<NodeReference, NscsServiceException>> entrySet = invalidNodesErrorMap.entrySet();
                    for (java.util.Map.Entry<NodeReference, NscsServiceException> entry : entrySet) {
                        response.add(entry.getKey().getFdn(), new String[] { "" + entry.getValue().getErrorCode(), entry.getValue().getMessage(), entry.getValue().getSuggestedSolution() });
                    }
                }
                response.setAdditionalInformation(responseMessageWithJobId);
                nscsLogger.commandHandlerFinishedWithSuccess(command, "workflow started successfully for any instance");
                return response;
            } else {
                nscsLogger.info("invalidNodesErrorMap is : [{}]", invalidNodesErrorMap);
                for (java.util.Map.Entry<NodeReference, NscsServiceException> entry : invalidNodesErrorMap.entrySet()) {
                    response.add(entry.getKey().getFdn(), new String[] { "" + entry.getValue().getErrorCode(), entry.getValue().getMessage(), entry.getValue().getSuggestedSolution() });
                }
                response.setAdditionalInformation(CRLCHECK_DISABLE_NOT_EXECUTED);
                nscsLogger.commandHandlerFinishedWithError(command, CRLCHECK_DISABLE_NOT_EXECUTED);
                return response;
            }

        }
    }
}
