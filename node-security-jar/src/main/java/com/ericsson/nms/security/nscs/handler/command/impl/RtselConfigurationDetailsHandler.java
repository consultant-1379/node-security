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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.types.RtselCommand;
import com.ericsson.nms.security.nscs.api.exception.CouldNotReadMoAttributeException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.handler.validation.impl.RtselConfigurationDetailsValidator;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.rtsel.utility.GetRtselConfigurationDetailsImpl;
import com.ericsson.nms.security.nscs.rtsel.utility.RtselConfigurationDetailsResponseBuilder;
import com.ericsson.nms.security.nscs.rtsel.utility.RtselConstants;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoException;

/**
 * Initiates the process to get Real Time Sec Log details on the nodes
 * 
 * @author xvekkar
 *
 */
@CommandType(NscsCommandType.RTSEL_GET)
@Local(CommandHandlerInterface.class)
public class RtselConfigurationDetailsHandler implements CommandHandler<RtselCommand>, CommandHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private RtselConfigurationDetailsResponseBuilder rtselConfigurationDetailsResponseBuilder;

    @Inject
    private RtselConfigurationDetailsValidator rtselConfigurationDetailsValidator;

    @Inject
    private GetRtselConfigurationDetailsImpl getRtselConfigurationDetailsImpl;

    private final Map<NodeReference, NscsServiceException> invalidNodesError = new HashMap<>();

    @Override
    public NscsCommandResponse process(final RtselCommand command, final CommandContext context) throws MissingMoException, NscsServiceException {

        nscsLogger.commandHandlerStarted(command);

        Map<String, Map<String, Object>> rtselDetails = new LinkedHashMap<String, Map<String, Object>>();
        final List<NormalizableNodeReference> validNodes = new ArrayList<NormalizableNodeReference>();
        NscsCommandResponse nscsCommandResponse = null;

        rtselConfigurationDetailsValidator.validateNodes(command, validNodes, invalidNodesError, command.getCommandType());

        if (validNodes.size() > 0) {
            rtselDetails = getRtselDetails(validNodes);
        }

        nscsCommandResponse = rtselConfigurationDetailsResponseBuilder.buildRtselDetailsResponse(rtselDetails, invalidNodesError);
        nscsLogger.commandHandlerFinishedWithSuccess(command, RtselConstants.COMMAND_EXECUTED_SUCCESSFULLY);
        return nscsCommandResponse;

    }

    private Map<String, Map<String, Object>> getRtselDetails(final List<NormalizableNodeReference> validNodes) {
        final Map<String, Map<String, Object>> rtselDetails = new LinkedHashMap<String, Map<String, Object>>();

        for (final NormalizableNodeReference normNode : validNodes) {

            try {

                nscsLogger.info("RtselConfigurationDetailsHandler::prepareRtselDetailsMap method: input node: {}", normNode);
                final Map<String, Object> rtselMap = getRtselConfigurationDetailsImpl.getRtselConfigurationDetails(normNode);
                rtselDetails.put(normNode.getNormalizedRef().getFdn(), rtselMap);
                nscsLogger.info("node fdn::: {} ", normNode.getNormalizedRef().getFdn());

            } catch (MissingMoException missingMoException) {
                invalidNodesError.put(normNode.getNormalizedRef(), new CouldNotReadMoAttributeException(missingMoException.getMessage()));
            }

        }
        return rtselDetails;
    }
}