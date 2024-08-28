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
import com.ericsson.nms.security.nscs.api.command.types.OnDemandCrlDownloadCommand;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.OnDemandCrlDownloadWfException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NscsInputNodeRetrievalUtility;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

@CommandType(NscsCommandType.ON_DEMAND_CRL_DOWNLOAD)
@Local(CommandHandlerInterface.class)
public class OnDemandCrlDownloadHandler implements CommandHandler<OnDemandCrlDownloadCommand>, CommandHandlerInterface {

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

    public static final String ON_DEMAND_CRL_DOWNLOAD_EXECUTED = "Successfully started a job to download CRL on demand. Perform 'secadm job get -j %s' to get progress info.";
    public static final String ON_DEMAND_CRL_DOWNLOAD_EXECUTED_DYN_ISSUE = "Successfully started a job to download CRL on demand for some node(s). Perform 'secadm job get -j %s' to get progress info of valid nodes. Invalid node details are given below :";
    public static final String ON_DEMAND_CRL_DOWNLOAD_NOT_EXECUTED = "Failed to start the job to download CRL on demand as all the provided node(s) are invalid. Invalid node details are given below :";
    public static final String ON_DEMAND_CRL_DOWNLOAD_WF_FAILED = " during download CRL operation.";

    public static final String[] ERROR_HEADER = new String[] { "Node Name", "Error Code", "Error Detail", "Suggested Solution" };

    private static final int NO_OF_COLUMNS = 3;

    @Override
    public NscsCommandResponse process(final OnDemandCrlDownloadCommand command, final CommandContext context) throws NscsServiceException {

        nscsLogger.commandHandlerStarted(command);
        long startTime = System.currentTimeMillis();
        String jobIdMessage = "";
        JobStatusRecord jobStatusRecord = null;
        final List<NodeReference> validNodesList = new ArrayList<NodeReference>();
        final Map<NodeReference, NscsServiceException> invalidNodesErrorMap = new HashMap<>();
        final Map<String, String[]> invalidDynamicNodesMap = new HashMap<>();
        final List<NodeReference> inputNodes = nscsInputNodeRetrievalUtility.getNodeReferenceList(command);
        final List<NodeReference> uniqueNodes = new ArrayList<NodeReference>(new HashSet<NodeReference>(inputNodes));
        nscsLogger.debug("Number of input nodes {} ", uniqueNodes.size());
        final boolean areInputNodesValid = commandManager.validateNodesForOnDemandCrlDownload(uniqueNodes, validNodesList, invalidNodesErrorMap, invalidDynamicNodesMap);

        nscsContextService.initItemsStatsForAsyncCommand(Integer.valueOf(validNodesList.size()), Integer.valueOf(invalidNodesErrorMap.size()));

        if (areInputNodesValid) {
            try {
                jobStatusRecord = cacheHandler.insertJob(NscsCommandType.ON_DEMAND_CRL_DOWNLOAD);
                nscsLogger.info("All of the given input nodes are Valid. OnDemandCrlDownload workflow need to be executed.");
                commandManager.executeOnDemandCrlDownloadWfs(validNodesList, jobStatusRecord);
                jobIdMessage = String.format(ON_DEMAND_CRL_DOWNLOAD_EXECUTED, jobStatusRecord.getJobId().toString());
            } catch (Exception ex) {
                nscsLogger.error(ex.getMessage(), ex);
                nscsLogger.commandHandlerFinishedWithError(command, ex.getMessage());
                throw new OnDemandCrlDownloadWfException(ON_DEMAND_CRL_DOWNLOAD_WF_FAILED);
            } finally {
                final long endTime = System.currentTimeMillis();
                nscsLogger.info("Total elapsed time for On Demand CRL Download Handler: " + String.format("%.3f", (endTime - startTime) / 1000f));
            }
            nscsLogger.commandHandlerFinishedWithSuccess(command, "workflow started successfully for any instance");
            return NscsCommandResponse.message(jobIdMessage);
        } else {
            final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(NO_OF_COLUMNS);
            response.add(ERROR_HEADER[0], Arrays.copyOfRange(ERROR_HEADER, 1, ERROR_HEADER.length));
            if (!validNodesList.isEmpty()) {

                nscsLogger.debug("Number of Valid Nodes are :{}", validNodesList.size());
                try {
                    jobStatusRecord = cacheHandler.insertJob(NscsCommandType.ON_DEMAND_CRL_DOWNLOAD);
                    nscsLogger.info("OnDemandCrlDownload workflow need to be executed for valid nodes.");
                    commandManager.executeOnDemandCrlDownloadWfs(validNodesList, jobStatusRecord);
                    jobIdMessage = String.format(ON_DEMAND_CRL_DOWNLOAD_EXECUTED_DYN_ISSUE, jobStatusRecord.getJobId().toString());
                } catch (Exception ex) {
                    nscsLogger.error(ex.getMessage(), ex);
                    nscsLogger.commandHandlerFinishedWithError(command, ex.getMessage());
                    throw new OnDemandCrlDownloadWfException(ON_DEMAND_CRL_DOWNLOAD_WF_FAILED);
                } finally {
                    final long endTime = System.currentTimeMillis();
                    nscsLogger.info("Total elapsed time for On Demand CRL Download Handler: " + String.format("%.3f", (endTime - startTime) / 1000f));
                }
                if (!invalidDynamicNodesMap.isEmpty() && invalidDynamicNodesMap.size() == invalidNodesErrorMap.size()) {
                    final Set<java.util.Map.Entry<String, String[]>> entrySet = invalidDynamicNodesMap.entrySet();
                    nscsLogger.debug("invalidDynamicNodesMap size is {} ", invalidDynamicNodesMap.size());
                    for (java.util.Map.Entry<String, String[]> entry : entrySet) {
                        response.add(entry.getKey(), entry.getValue());
                    }
                } else if (!invalidNodesErrorMap.isEmpty()) {
                    nscsLogger.debug("invalidNodesErrorMap size is {} ", invalidNodesErrorMap.size());
                    final Set<java.util.Map.Entry<NodeReference, NscsServiceException>> entrySet = invalidNodesErrorMap.entrySet();
                    for (java.util.Map.Entry<NodeReference, NscsServiceException> entry : entrySet) {
                        response.add(entry.getKey().getFdn(), new String[] { "" + entry.getValue().getErrorCode(), entry.getValue().getMessage(), entry.getValue().getSuggestedSolution() });
                    }
                }
                response.setAdditionalInformation(jobIdMessage);
                nscsLogger.commandHandlerFinishedWithSuccess(command, "workflow started successfully for any instance");
                return response;
            } else {
                nscsLogger.info("invalidNodesErrorMap is : [{}] ", invalidNodesErrorMap);
                for (java.util.Map.Entry<NodeReference, NscsServiceException> entry : invalidNodesErrorMap.entrySet()) {
                    response.add(entry.getKey().getFdn(), new String[] { "" + entry.getValue().getErrorCode(), entry.getValue().getMessage(), entry.getValue().getSuggestedSolution() });
                }
                response.setAdditionalInformation(ON_DEMAND_CRL_DOWNLOAD_NOT_EXECUTED);
                nscsLogger.commandHandlerFinishedWithError(command, ON_DEMAND_CRL_DOWNLOAD_NOT_EXECUTED);
                return response;
            }

        }
    }
}
