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

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse;
import com.ericsson.nms.security.nscs.api.command.types.SsoCommand;
import com.ericsson.nms.security.nscs.api.exception.InvalidNodeNameException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.SsoNotSupportedException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.handler.command.utility.NscsNameMultipleValueResponseBuilder;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NscsInputNodeRetrievalUtility;
import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;

@CommandType(NscsCommandType.SSO_GET)
@Local(CommandHandlerInterface.class)
public class GetSsoHandler implements CommandHandler<SsoCommand>, CommandHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsInputNodeRetrievalUtility nscsInputNodeRetrievalUtility;

    @Inject
    private Logger logger;

    @EServiceRef
    DataPersistenceService dps;

    private static final String NODE_NAME = "Node Name";
    private static final String SSO_STATUS = "SSO Status";
    private static final String STATUS = "Status";
    private static final String SUGGESTED_SOLUTION = "Suggested solution";
    private static final String COMMAND_EXECUTED_SUCCESSFULLY = "Command executed successfully";
    private static final String SSO_ATTRIBUTE = "SSO";

    public enum SsoStatus {
        ENABLED, DISABLED, NULL
    }

    @Override
    public NscsCommandResponse process(final SsoCommand command, final CommandContext context) {
        nscsLogger.commandHandlerStarted(command);

        Map<String, String> responseRow;
        final List<Map<String, String>> responseRows = new ArrayList<>();

        final List<NodeReference> inputNodes = nscsInputNodeRetrievalUtility.getNodeReferenceList(command);
        for (final NodeReference inputNode : inputNodes) {

            final String normNodeName = inputNode.getName();
            responseRow = new HashMap<>();
            responseRow.put(NODE_NAME, normNodeName);

            try {
                final SsoStatus ssoStatus;
                final String fdn = "NetworkElement=" + normNodeName + ",SecurityFunction=1,NetworkElementSecurity=1";
                final ManagedObject mo = dps.getLiveBucket().findMoByFdn(fdn);
                if (mo == null) {
                    logger.error("Network Element Specified does not exist");
                    throw new InvalidNodeNameException();
                } else if (mo.getAllAttributes().containsKey(SSO_ATTRIBUTE)) {
                    final boolean isSSOEnabled = mo.getAttribute(SSO_ATTRIBUTE);
                    logger.info("SSO VALUE IS {}", isSSOEnabled);
                    if (isSSOEnabled) {
                        ssoStatus = SsoStatus.ENABLED;
                    } else {
                        ssoStatus = SsoStatus.DISABLED;
                    }
                    responseRow.put(SSO_STATUS, ssoStatus.toString());
                    responseRow.put(STATUS, "None");
                    responseRow.put(SUGGESTED_SOLUTION, "None");
                } else {
                    logger.error("SSO is not supported for the given node type");
                    throw new SsoNotSupportedException();
                }

                responseRows.add(responseRow);

            } catch (final NscsServiceException exception) {
                responseRow.put(SSO_STATUS, "N/A");
                responseRow.put(STATUS, exception.getMessage());
                responseRow.put(SUGGESTED_SOLUTION, exception.getSuggestedSolution());
                responseRows.add(responseRow);
            } catch (final Exception exception) {
                nscsLogger.commandHandlerFinishedWithError(command, exception.getMessage());
                return NscsCommandResponse.message(exception.getMessage());
            }
        }
        nscsLogger.commandHandlerFinishedWithSuccess(command, COMMAND_EXECUTED_SUCCESSFULLY);
        return buildResponse(responseRows);

    }

    private NscsNameMultipleValueCommandResponse buildResponse(final List<Map<String, String>> responseRows) {
        final NscsNameMultipleValueResponseBuilder responseBuilder = new NscsNameMultipleValueResponseBuilder(3);
        final Map<String, Integer> header = new HashMap<>();
        header.put(SSO_STATUS, 0);
        header.put(STATUS, 1);
        header.put(SUGGESTED_SOLUTION, 2);
        responseBuilder.add(NODE_NAME, responseBuilder.formatHeader(header));
        for (final Map<String, String> responseRow : responseRows) {
            responseBuilder.add(responseRow.get(NODE_NAME), responseBuilder.formatRow(header, responseRow));
        }
        return responseBuilder.getResponse();
    }
}
