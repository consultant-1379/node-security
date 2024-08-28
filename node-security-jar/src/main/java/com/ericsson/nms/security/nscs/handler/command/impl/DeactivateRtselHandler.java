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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.manager.NscsRtselCommandManager;
import com.ericsson.nms.security.nscs.api.command.types.RtselCommand;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.RtselWfException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.handler.validation.impl.RtselValidator;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.rtsel.utility.DeactivateRtselResponseBuilder;
import com.ericsson.nms.security.nscs.rtsel.utility.RtselConstants;
import com.ericsson.nms.security.nscs.utilities.NscsInputNodeRetrievalUtility;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

/**
 * <p>
 * Initiates the process to deactivate the RTSEL feature for the provided list of nodes.
 * </p>
 *
 * @author xgvgvgv
 *
 */
@CommandType(NscsCommandType.RTSEL_DEACTIVATE)
@Local(CommandHandlerInterface.class)
public class DeactivateRtselHandler implements CommandHandler<RtselCommand>, CommandHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @EJB
    private NscsRtselCommandManager nscsRtselCommandManager;

    @Inject
    private NscsJobCacheHandler nscsJobCacheHandler;

    @Inject
    private DeactivateRtselResponseBuilder deactivateRtselResponseBuilder;

    @Inject
    private RtselValidator rtselValidator;

    @Inject
    private NscsInputNodeRetrievalUtility nscsInputNodeRetrievalUtility;

    @Inject
    private NscsContextService nscsContextService;

    private final Map<NodeReference, NscsServiceException> invalidNodesErrorMap = new HashMap<NodeReference, NscsServiceException>();


    @Override
    public NscsCommandResponse process(final RtselCommand command, final CommandContext context) throws NscsServiceException {

    	nscsLogger.commandHandlerStarted(command);
        JobStatusRecord jobStatusRecord = null;

        final List<NodeReference> inputNodes = nscsInputNodeRetrievalUtility.getNodeReferenceList(command);
        final List<NodeReference> uniqueNodes = new ArrayList<>(new HashSet<>(inputNodes));

        final List<String> validNodeDetailsList = new ArrayList<String>();

        rtselValidator.validateNodesForDeactivate(uniqueNodes, validNodeDetailsList, command.getCommandType(), invalidNodesErrorMap);

        nscsContextService.initItemsStatsForAsyncCommand(Integer.valueOf(validNodeDetailsList.size()), Integer.valueOf(invalidNodesErrorMap.size()));

        if (!validNodeDetailsList.isEmpty()) {
            try {
                jobStatusRecord = nscsJobCacheHandler.insertJob(NscsCommandType.RTSEL_DEACTIVATE);
                nscsRtselCommandManager.executeDeActivateRtselWfs(validNodeDetailsList, jobStatusRecord);
            } catch (Exception ex) {
                nscsLogger.error(ex.getMessage(), ex);
                nscsLogger.commandHandlerFinishedWithError(command, ex.getMessage());
                throw new RtselWfException();
            }
        }
        if (validNodeDetailsList.size() != 0 && invalidNodesErrorMap.size() == 0) {
            nscsLogger.commandHandlerFinishedWithSuccess(command, RtselConstants.DEACTIVATE_RTSEL_SUCCESS_FOR_ALL_NODES);
            return deactivateRtselResponseBuilder.buildResponseForAllValidInputNodes(jobStatusRecord);
        } else if (validNodeDetailsList.size() != 0 && invalidNodesErrorMap.size() != 0) {
            nscsLogger.commandHandlerFinishedWithSuccess(command, RtselConstants.DEACTIVATE_RTSEL_PARTIAL_FAILED);
            return deactivateRtselResponseBuilder.buildResponseForPartialValidInputNodes(jobStatusRecord, invalidNodesErrorMap);
        } else {
            nscsLogger.commandHandlerFinishedWithError(command, RtselConstants.DEACTIVATE_RTSEL_FAILED_ALL_NODES);
            return deactivateRtselResponseBuilder.buildResponseForAllInvalidInputNodes(command, invalidNodesErrorMap);
        }
    }
}