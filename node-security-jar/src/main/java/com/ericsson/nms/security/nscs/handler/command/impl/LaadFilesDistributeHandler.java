/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
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
import com.ericsson.nms.security.nscs.api.command.manager.NscsLaadCommandManager;
import com.ericsson.nms.security.nscs.api.command.types.LaadCommand;
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.api.exception.LaadFilesDistributionWFException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.handler.laad.utility.LaadFilesDistributeConstants;
import com.ericsson.nms.security.nscs.handler.laad.utility.LaadFilesDistributeResponseBuilder;
import com.ericsson.nms.security.nscs.handler.validation.impl.LaadFilesDistributeValidator;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.util.FileUtil;
import com.ericsson.nms.security.nscs.utilities.Constants;
import com.ericsson.nms.security.nscs.utilities.NscsInputNodeRetrievalUtility;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

/**
 * Initiates the process to distribute Local Authentication and Authorization Database (LAAD) files to the nodes
 *
 * @author tcsgoja
 */
@CommandType(NscsCommandType.LAAD_FILES_DISTRIBUTE)
@Local(CommandHandlerInterface.class)
public class LaadFilesDistributeHandler implements CommandHandler<LaadCommand>, CommandHandlerInterface {

    @Inject
    private LaadFilesDistributeResponseBuilder laadFilesDistributeResponseBuilder;

    @Inject
    private LaadFilesDistributeValidator laadFilesDistributeValidator;

    @Inject
    private NscsInputNodeRetrievalUtility nscsInputNodeRetrievalUtility;

    @Inject
    private NscsJobCacheHandler nscsJobCacheHandler;

    @EJB
    private NscsLaadCommandManager nscsLaadCommandManager;

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    FileUtil fileUtil;

    @Inject
    private NscsContextService nscsContextService;

    private final Map<NodeReference, NscsServiceException> invalidNodesErrorMap = new HashMap<>();

    @Override
    public NscsCommandResponse process(final LaadCommand command, final CommandContext context) {

        nscsLogger.commandHandlerStarted(command);

        final Map<String, Object> properties = command.getProperties();
        final String fileName = (String) properties.get(Constants.FILE_NAME);

        if (fileName != null && !fileUtil.isValidFileExtension(fileName, Constants.FILE_EXT_TXT)) {
            nscsLogger.error("Input Node file: {} to laad distribute command. " + NscsErrorCodes.INVALID_FILE_NOT_TXT_TYPE, fileName);
            nscsLogger.commandHandlerFinishedWithError(command,
                    "Input Node file: " + fileName + " to laad distribute command. " + NscsErrorCodes.INVALID_FILE_NOT_TXT_TYPE);
            throw new InvalidArgumentValueException(NscsErrorCodes.INVALID_FILE_NOT_TXT_TYPE).setSuggestedSolution(NscsErrorCodes.CHECK_ONLINE_HELP_FOR_VALID_TXT_FILE_TEMPLATE);
        }

        final List<NodeReference> inputNodes = nscsInputNodeRetrievalUtility.getNodeReferenceList(command);
        final List<NodeReference> uniqueNodes = new ArrayList<>(new HashSet<>(inputNodes));

        final List<String> validNodes = new ArrayList<>();

        nscsLogger.info("Number of input nodes {}", inputNodes.size());

        laadFilesDistributeValidator.validateNodes(uniqueNodes, validNodes, invalidNodesErrorMap);

        nscsLogger.info("valid nodes : {}", validNodes);

        JobStatusRecord jobStatusRecord = null;

        nscsContextService.initItemsStatsForAsyncCommand(Integer.valueOf(validNodes.size()), Integer.valueOf(invalidNodesErrorMap.size()));

        if (!validNodes.isEmpty()) {
            try {
                jobStatusRecord = nscsJobCacheHandler.insertJob(NscsCommandType.LAAD_FILES_DISTRIBUTE);
                nscsLaadCommandManager.executeLaadFilesDistributeWorkFlow(validNodes, jobStatusRecord);
            } catch (final Exception ex) {
                nscsLogger.error(ex.getMessage(), ex);
                nscsLogger.commandHandlerFinishedWithError(command, ex.getMessage());
                throw new LaadFilesDistributionWFException();
            }
        }
        if (!validNodes.isEmpty() && invalidNodesErrorMap.isEmpty()) {
            nscsLogger.commandHandlerFinishedWithSuccess(command, LaadFilesDistributeConstants.LAAD_DISTRIBUTE_EXECUTED);
            return laadFilesDistributeResponseBuilder.buildResponseForAllValidInputNodes(jobStatusRecord);
        } else if (!validNodes.isEmpty() && !invalidNodesErrorMap.isEmpty()) {
            nscsLogger.commandHandlerFinishedWithSuccess(command, LaadFilesDistributeConstants.LAAD_DISTRIBUTE_PARTIALLY_FAILED);
            return laadFilesDistributeResponseBuilder.buildResponseForPartialValidInputNodes(jobStatusRecord, invalidNodesErrorMap);
        } else {
            nscsLogger.commandHandlerFinishedWithError(command, LaadFilesDistributeConstants.LAAD_DISTRIBUTE_NOT_EXECUTED);
            return laadFilesDistributeResponseBuilder.buildResponseForAllInvalidInputNodes(command, invalidNodesErrorMap);
        }

    }

}