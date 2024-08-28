/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
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
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse;
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManager;
import com.ericsson.nms.security.nscs.api.command.types.SsoCommand;
import com.ericsson.nms.security.nscs.api.exception.InvalidNodeNameException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.SsoNotSupportedException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NormalizedRootMO;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService.WriterSpecificationBuilder;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.utility.NscsNameMultipleValueResponseBuilder;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NscsInputNodeRetrievalUtility;
import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

public class SsoCommandHandlerHelper {

    private static final String SSO_ATTRIBUTE = "SSO";
    private static final String SSO_ATTRIBUTE_UPDATED_SUCCESSFULLY = "SSO attribute updated successfully";

    @Inject
    private Logger logger;

    @Inject
    private NscsLogger nscsLogger;

    @EJB
    private NscsCommandManager nscsCommandManager;

    @Inject
    private NscsCMWriterService writer;

    @Inject
    private NscsInputNodeRetrievalUtility nscsInputNodeRetrievalUtility;

    @EServiceRef
    DataPersistenceService dps;

    @Inject
    private NscsContextService nscsContextService;

    private static final String NODE_NAME = "Node Name";
    private static final String STATUS = "Status";
    private static final String SUGGESTED_SOLUTION = "Suggested solution";

    public NscsCommandResponse process(final SsoCommand command, final CommandContext context,
            final boolean ssoValue) {
        nscsLogger.commandHandlerStarted(command);

        boolean areAllNodesValid = true;

        Map<String, String> responseRow;
        final List<Map<String, String>> responseRows = new ArrayList<>();

        final List<NodeReference> inputNodes = nscsInputNodeRetrievalUtility.getNodeReferenceList(command);

        logger.info("context is {}", context);
        logger.info("SSO Value is {}", ssoValue);
        final Boolean ssoVal = Boolean.valueOf(ssoValue);

        for (final NodeReference inputNode : inputNodes) {
            try {
                final String fdn = "NetworkElement=" + inputNode.getName() + ",SecurityFunction=1,NetworkElementSecurity=1";
                final ManagedObject mo = dps.getLiveBucket().findMoByFdn(fdn);
                if (mo == null) {
                    logger.error("Network Element Specified does not exist");
                    throw new InvalidNodeNameException();
                } else if (mo.getAllAttributes().containsKey(SSO_ATTRIBUTE)) {
                    logger.debug("Updating SSO {}", ssoVal);
                    command.getProperties().put(SSO_ATTRIBUTE, ssoVal);
                } else {
                    logger.error("SSO is not supported for the given node type");
                    throw new SsoNotSupportedException();
                }
            } catch (final NscsServiceException exception) {
                areAllNodesValid = false;
                responseRow = new HashMap<>();
                final String normNodeName = inputNode.getName();
                responseRow.put(NODE_NAME, normNodeName);
                responseRow.put(STATUS, exception.getMessage());
                responseRow.put(SUGGESTED_SOLUTION, exception.getSuggestedSolution());
                responseRows.add(responseRow);
                nscsLogger.commandHandlerFinishedWithError(command, exception.getMessage());

            } catch (final Exception exception) {
                nscsLogger.commandHandlerFinishedWithError(command, exception.getMessage());
                return NscsCommandResponse.message(exception.getMessage());
            }
        }

        nscsContextService.initItemsStatsForSyncCommand(Integer.valueOf(inputNodes.size() - responseRows.size()),
                Integer.valueOf(responseRows.size()));

        if (!areAllNodesValid) {
            return buildResponse(responseRows);
        }
        final WriterSpecificationBuilder specification = writer.withSpecification();
        logger.debug("Updating SSO {}", ssoVal);
        specification.setAttribute(SSO_ATTRIBUTE, ssoVal);

        for (final NodeReference node : nscsInputNodeRetrievalUtility.getNodeReferenceList(command)) {
            logger.debug("Updating NetworkElementSecurity for {}", node);
            final String nodeFdn = node.getFdn();
            final NormalizedRootMO normal = Model.getNomalizedRootMO(nodeFdn);
            specification.setFdn(normal.securityFunction.networkElementSecurity.withNames(node.getName()).fdn());
            specification.updateMO();
            logger.debug("Updated NetworkElementSecurity for {}", node);
        }
        logger.debug("SSO attribute updated succesfully");

        nscsContextService.updateItemsResultStatsForSyncCommand(Integer.valueOf(inputNodes.size()), Integer.valueOf(0));

        nscsLogger.commandHandlerFinishedWithSuccess(command, "SSO Updated");

        return NscsCommandResponse.message(SSO_ATTRIBUTE_UPDATED_SUCCESSFULLY);

    }

    private NscsNameMultipleValueCommandResponse buildResponse(final List<Map<String, String>> responseRows) {
        final NscsNameMultipleValueResponseBuilder responseBuilder = new NscsNameMultipleValueResponseBuilder(2);
        final Map<String, Integer> header = new HashMap<>();
        header.put(STATUS, 0);
        header.put(SUGGESTED_SOLUTION, 1);
        responseBuilder.add(NODE_NAME, responseBuilder.formatHeader(header));
        for (final Map<String, String> responseRow : responseRows) {
            responseBuilder.add(responseRow.get(NODE_NAME), responseBuilder.formatRow(header, responseRow));
        }
        return responseBuilder.getResponse();
    }
}
