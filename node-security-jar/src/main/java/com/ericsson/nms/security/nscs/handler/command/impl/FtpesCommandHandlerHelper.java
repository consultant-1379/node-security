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
import com.ericsson.nms.security.nscs.api.command.types.FtpesCommand;
import com.ericsson.nms.security.nscs.api.exception.FtpesActivateOrDeactivateWfException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.util.FtpesCommandType;
import com.ericsson.nms.security.nscs.utilities.NscsInputNodeRetrievalUtility;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;


public class FtpesCommandHandlerHelper {

    public static final String FTPES_ACTIVATE_EXECUTED = "Successfully started a job for FTPES activate operation. Perform 'secadm job get -j %s' to get progress info.";
    public static final String FTPES_ACTIVATE_WF_FAILED = "Error during FTPES Activate operation.";
    public static final String FTPES_DEACTIVATE_EXECUTED = "Successfully started a job for FTPES deactivate operation. Perform 'secadm job get -j %s' to get progress info.";
    public static final String FTPES_DEACTIVATE_WF_FAILED = "Error during FTPES Deactivate operation.";

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

    public NscsCommandResponse processActivate(final FtpesCommand command, final CommandContext context) {

        return process(command, context, FtpesCommandType.ACTIVATE);
    }

    public NscsCommandResponse processDeactivate(final FtpesCommand command, final CommandContext context) {

        return process(command, context, FtpesCommandType.DEACTIVATE);
    }

    private NscsCommandResponse process(final FtpesCommand command, final CommandContext context,
                                        final FtpesCommandType commandType) {

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
            nscsLogger.info("All of the given input nodes are valid. FTPES workflow need to be executed.");

            if (commandType.equals(FtpesCommandType.ACTIVATE)) {
                nscsCommandManager.executeActivateFtpesWfs(uniqueNodes, jobStatusRecord);
            } else {
                nscsCommandManager.executeDeactivateFtpesWfs(uniqueNodes, jobStatusRecord);
            }
        } catch (NscsServiceException ex) {
            nscsLogger.error(ex.getMessage(), ex);
            nscsLogger.commandHandlerFinishedWithError(command, ex.getMessage());
            throw new FtpesActivateOrDeactivateWfException(commandType.getFailedMessage());
        } finally {
            final long endTime = System.currentTimeMillis();
            nscsLogger.info("Total elapsed time for " + commandType.getName() + " FTPES Handler: "
                    + String.format("%.3f", (endTime - startTime) / 1000f));
        }

        responseMessageWithJobId = String.format(commandType.getExecutedMessage(),
                jobStatusRecord.getJobId().toString());
        nscsLogger.commandHandlerFinishedWithSuccess(command, "workflow started successfully for any instance");
        return NscsCommandResponse.message(responseMessageWithJobId);
    }
}
