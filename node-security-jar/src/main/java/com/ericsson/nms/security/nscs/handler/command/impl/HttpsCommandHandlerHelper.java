/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
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
import java.util.LinkedHashSet;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManager;
import com.ericsson.nms.security.nscs.api.command.types.HttpsCommand;
import com.ericsson.nms.security.nscs.api.exception.HttpsActivateOrDeactivateWfException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.util.CommandType;
import com.ericsson.nms.security.nscs.utilities.NscsInputNodeRetrievalUtility;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

/**
 * Created by ekrzsia on 7/25/17.
 */
public class HttpsCommandHandlerHelper {

    public static final String HTTPS_ACTIVATE_EXECUTED = "Successfully started a job for HTTPS activate operation. Perform 'secadm job get -j %s' to get progress info.";
    public static final String HTTPS_ACTIVATE_WF_FAILED = "Error during HTTPS Activate operation.";
    public static final String HTTPS_DEACTIVATE_EXECUTED = "Successfully started a job for HTTPS deactivate operation. Perform 'secadm job get -j %s' to get progress info.";
    public static final String HTTPS_DEACTIVATE_WF_FAILED = "Error during HTTPS Deactivate operation.";

    @Inject
    private NscsLogger nscsLogger;

    @EJB
    private NscsCommandManager nscsCommandManager;

    @Inject
    private NscsJobCacheHandler nscsJobCacheHandler;

    @Inject
    private NscsInputNodeRetrievalUtility nscsInputNodeRetrievalUtility;

    @Inject
    private NscsContextService nscsContextService;

    public NscsCommandResponse processActivate(final HttpsCommand command, final CommandContext context) {

        return process(command, context, CommandType.ACTIVATE);
    }

    public NscsCommandResponse processDeactivate(final HttpsCommand command, final CommandContext context) {

        return process(command, context, CommandType.DEACTIVATE);
    }

    private NscsCommandResponse process(final HttpsCommand command, final CommandContext context,
            final CommandType commandType) {

        nscsLogger.commandHandlerStarted(command);

        String responseMessageWithJobId = "";
        final long startTime = System.currentTimeMillis();
        JobStatusRecord jobStatusRecord = null;

        final List<NodeReference> inputNodes = nscsInputNodeRetrievalUtility.getNodeReferenceList(command);
        final List<NodeReference> uniqueNodes = new ArrayList<>(new LinkedHashSet<>(inputNodes));

        nscsLogger.debug("Number of input nodes {}", uniqueNodes.size());

        nscsContextService.initItemsStatsForAsyncCommand(Integer.valueOf(uniqueNodes.size()), Integer.valueOf(0));

        try {
            jobStatusRecord = nscsJobCacheHandler.insertJob(commandType.getNscsCommandType());
            nscsLogger.info("All of the given input nodes are valid. HTTPS workflow need to be executed.");

            if (commandType.equals(CommandType.ACTIVATE)) {
                nscsCommandManager.executeActivateHttpsWfs(uniqueNodes, jobStatusRecord);
            } else {
                nscsCommandManager.executeDeactivateHttpsWfs(uniqueNodes, jobStatusRecord);
            }
        } catch (NscsServiceException ex) {
            nscsLogger.error(ex.getMessage(), ex);
            nscsLogger.commandHandlerFinishedWithError(command, ex.getMessage());
            throw new HttpsActivateOrDeactivateWfException(commandType.getFailedMessage());
        } finally {
            final long endTime = System.currentTimeMillis();
            nscsLogger.info("Total elapsed time for " + commandType.getName() + " HTTPS Handler: "
                    + String.format("%.3f", (endTime - startTime) / 1000f));
        }

        responseMessageWithJobId = String.format(commandType.getExecutedMessage(),
                jobStatusRecord.getJobId().toString());
        nscsLogger.commandHandlerFinishedWithSuccess(command, "workflow started successfully for any instance");
        return NscsCommandResponse.message(responseMessageWithJobId);
    }
}
