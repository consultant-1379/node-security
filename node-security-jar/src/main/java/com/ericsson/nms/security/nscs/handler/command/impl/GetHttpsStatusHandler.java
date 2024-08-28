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

import java.util.*;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse;
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManager;
import com.ericsson.nms.security.nscs.api.exception.GetHttpsWfException;
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.api.exception.NodeNotSynchronizedException;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.utility.WebServerStatus;
import com.ericsson.nms.security.nscs.utilities.MoAttributeHandler;
import com.ericsson.nms.security.nscs.utilities.NscsInputNodeRetrievalUtility;
import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.NscsMessageCommandResponse;
import com.ericsson.nms.security.nscs.api.command.types.HttpsCommand;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.handler.command.utility.NscsNameMultipleValueResponseBuilder;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;

/**
 * Created by eelzkoc on 6/30/17.
 */

@CommandType(NscsCommandType.HTTPS_GET_STATUS)
@Local(CommandHandlerInterface.class)
public class GetHttpsStatusHandler implements CommandHandler<HttpsCommand>, CommandHandlerInterface {

    @Inject
    private Logger logger;

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService reader;

    @Inject
    private NodeValidatorUtility nodeValidatorUtility;

    @Inject
    private MoAttributeHandler moAttributeHandler;

    @Inject
    private NscsJobCacheHandler nscsJobCacheHandler;

    @Inject
    private NscsInputNodeRetrievalUtility nscsInputNodeRetrievalUtility;

    @EJB
    private NscsCommandManager nscsCommandManager;

    private static final String NODE_NAME = "Node Name";
    private static final String NE_STATUS = "NE status";
    private static final String CONFIGURED_STATUS = "Configured Status";
    private static final String COMPARE = "Compare";
    private static final String ERROR_MESSAGE = "Error Message";
    private static final String SUGGESTED_SOLUTION = "Suggested solution";
    private static final String COMMAND_EXECUTED_SUCCESSFULLY = "Command executed successfully";
    private static final String WEBSERVER_ATTRIBUTE = ModelDefinition.Security.WEBSERVER;
    private static final String HTTPS_ATTRIBUTE = ModelDefinition.CppConnectivityInformation.HTTPS;
    private static final Mo CPP_CONNECTIVITY_INFORMATION_MO = Model.NETWORK_ELEMENT.cppConnectivityInformation;
    private static final Mo SECURITY_MO = Model.ME_CONTEXT.managedElement.systemFunctions.security;

    public static final String GET_HTTPS_STATUS_EXECUTED = "Successfully started a job for get HTTPS status operation. Perform 'secadm job get -j %s' to get progress info.";
    public static final String GET_HTTPS_STATUS_WF_FAILED = "Error during Get HTTPS Status operation.";


    @Override
    public NscsCommandResponse process(HttpsCommand command, CommandContext context) throws NscsServiceException {
        nscsLogger.commandHandlerStarted(command);

        Map<String, String> responseRow;
        String httpsAttributeValue = null;
        WebServerStatus configuredStatus = null;
        final List<Map<String, String>> responseRows = new ArrayList<>();
        final List<NodeReference> notSynchronizedNodes = new ArrayList<>();

        List<NodeReference> inputNodes = nscsInputNodeRetrievalUtility.getNodeReferenceList(command);
        for (NodeReference inputNode : inputNodes) {

            final String normNodeFDN = inputNode.getFdn();
            final String normNodeName = inputNode.getName();
            final NodeReference nodeReference = new NodeRef(normNodeFDN);
            final NormalizableNodeReference normalizableNodeReference = reader.getNormalizableNodeReference(nodeReference);
            responseRow = new HashMap<>();
            responseRow.put(NODE_NAME, normNodeName);

            try {
                nodeValidatorUtility.validateNodeForHttpsStatus(normalizableNodeReference);
                httpsAttributeValue = moAttributeHandler.getMOAttributeValue(normNodeFDN, CPP_CONNECTIVITY_INFORMATION_MO.type(), CPP_CONNECTIVITY_INFORMATION_MO.namespace(), HTTPS_ATTRIBUTE);
                configuredStatus = WebServerStatus.valueOf(moAttributeHandler.getMOAttributeValue(normalizableNodeReference.getFdn(),
                        SECURITY_MO.type(), SECURITY_MO.namespace(), WEBSERVER_ATTRIBUTE));

                if (isAttributeValueNotNull(httpsAttributeValue)) {
                    Boolean httpsAttributeInCppConnectivityInfo = Boolean.parseBoolean(httpsAttributeValue);

                    responseRow.put(NE_STATUS, httpsAttributeInCppConnectivityInfo.toString());
                    responseRow.put(CONFIGURED_STATUS, configuredStatus.toString());
                    responseRow.put(COMPARE, moAttributeHandler.match(configuredStatus, httpsAttributeInCppConnectivityInfo));
                    responseRow.put(ERROR_MESSAGE,"N/A");
                    responseRow.put(SUGGESTED_SOLUTION, "N/A");
                    responseRows.add(responseRow);
                }

            } catch (final InvalidArgumentValueException exception) {

                nscsLogger.error(exception.getMessage(), exception);
                responseRow.put(NE_STATUS, httpsAttributeValue);
                responseRow.put(CONFIGURED_STATUS, configuredStatus.toString());
                responseRow.put(COMPARE, "MISMATCH");
                responseRow.put(ERROR_MESSAGE, "An incorrect value null is encountered for HTTPS attribute.");
                responseRow.put(SUGGESTED_SOLUTION, "Update HTTPS value under CppConnectivityInformation MO to either true or false.");
                responseRows.add(responseRow);

            } catch (final NscsServiceException exception) {

                if (exception instanceof NodeNotSynchronizedException) {
                    notSynchronizedNodes.add(nodeReference);
                }
                responseRow.put(NE_STATUS, "N/A");
                responseRow.put(CONFIGURED_STATUS, "N/A");
                responseRow.put(COMPARE,"N/A");
                responseRow.put(ERROR_MESSAGE, exception.getMessage());
                responseRow.put(SUGGESTED_SOLUTION, exception.getSuggestedSolution());
                responseRows.add(responseRow);

            } catch (final Exception exception) {
                nscsLogger.commandHandlerFinishedWithError(command, exception.getMessage());
                return NscsMessageCommandResponse.message(exception.getMessage());
            }
        }

        JobStatusRecord jobStatusRecord = null;

        if (!notSynchronizedNodes.isEmpty()) {

            try {
                jobStatusRecord = nscsJobCacheHandler.insertJob(NscsCommandType.HTTPS_GET_STATUS);
                nscsCommandManager.executeGetHttpsStatusWfs(notSynchronizedNodes, jobStatusRecord);
            } catch (NscsServiceException ex) {
                nscsLogger.error(ex.getMessage(), ex);
                nscsLogger.commandHandlerFinishedWithError(command, ex.getMessage());
                throw new GetHttpsWfException(GET_HTTPS_STATUS_WF_FAILED);
            }

            nscsLogger.commandHandlerFinishedWithSuccess(command, COMMAND_EXECUTED_SUCCESSFULLY);
            return buildResponse(responseRows, String.format(GET_HTTPS_STATUS_EXECUTED,jobStatusRecord.getJobId().toString()));
        }

        nscsLogger.commandHandlerFinishedWithSuccess(command, COMMAND_EXECUTED_SUCCESSFULLY);
        return buildResponse(responseRows,null);
    }

    private Boolean isAttributeValueNotNull(final String httpsAttributeValue) {

        if (httpsAttributeValue != null && !httpsAttributeValue.equalsIgnoreCase("null")) {
            return true;
        } else {
            logger.error("An incorrect value null is encountered");
            throw new InvalidArgumentValueException();
        }
    }

    private NscsNameMultipleValueCommandResponse buildResponse(final List<Map<String, String>> responseRows, final String responseMessage) {

        final NscsNameMultipleValueResponseBuilder responseBuilder = new NscsNameMultipleValueResponseBuilder(5);

        if (!responseRows.isEmpty()) {
            Map<String, Integer> header = new HashMap<>();
            header.put(NE_STATUS, 0);
            header.put(CONFIGURED_STATUS, 1);
            header.put(COMPARE, 2);
            header.put(ERROR_MESSAGE, 3);
            header.put(SUGGESTED_SOLUTION, 4);
            responseBuilder.add(NODE_NAME, responseBuilder.formatHeader(header));

            for (Map<String, String> responseRow : responseRows) {
                responseBuilder.add(responseRow.get(NODE_NAME), responseBuilder.formatRow(header, responseRow));
            }
        }

        NscsNameMultipleValueCommandResponse response = responseBuilder.getResponse();
        if (responseMessage != null) {
            response.setAdditionalInformation(responseMessage);
        }

        return response;
    }
}
