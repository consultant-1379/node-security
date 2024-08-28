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

import com.ericsson.nms.security.nscs.api.command.*;
import com.ericsson.nms.security.nscs.api.command.types.FtpesCommand;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.handler.command.utility.NscsNameMultipleValueResponseBuilder;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.MoAttributeHandler;
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility;
import com.ericsson.nms.security.nscs.utilities.NscsInputNodeRetrievalUtility;

import javax.ejb.Local;
import javax.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CommandType(NscsCommandType.FTPES_GET_STATUS)
@Local(CommandHandlerInterface.class)
public class GetFtpesStatusHandler implements CommandHandler<FtpesCommand>, CommandHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService reader;

    @Inject
    private NodeValidatorUtility nodeValidatorUtility;

    @Inject
    private MoAttributeHandler moAttributeHandler;

    @Inject
    private NscsInputNodeRetrievalUtility nscsInputNodeRetrievalUtility;

    private static final String NODE_NAME = "Node Name";
    private static final String FTPES_STATUS = "FTPES Status";
    private static final String ERROR_MESSAGE = "Error Message";
    private static final String SUGGESTED_SOLUTION = "Suggested solution";
    private static final String COMMAND_EXECUTED_SUCCESSFULLY = "Command executed successfully";
    private static final String FTPES_ATTRIBUTE_NAME = ModelDefinition.ComConnectivityInformation.FILE_TRANSFER_PROTOCOL;
    private static final ModelDefinition.Mo COM_CONNECTIVITY_INFORMATION_MO = Model.NETWORK_ELEMENT.comConnectivityInformation;

    public enum FtpesStatus {
        ON, OFF
    }

    @Override
    public NscsCommandResponse process(FtpesCommand command, CommandContext context) throws NscsServiceException {
        nscsLogger.commandHandlerStarted(command);

        Map<String, String> responseRow;
        final List<Map<String, String>> responseRows = new ArrayList<>();

        List<NodeReference> inputNodes = nscsInputNodeRetrievalUtility.getNodeReferenceList(command);
        for (NodeReference inputNode : inputNodes) {

            final String normNodeFDN = inputNode.getFdn();
            final String normNodeName = inputNode.getName();
            final NodeReference nodeReference = new NodeRef(normNodeFDN);
            final NormalizableNodeReference normalizableNodeReference = reader.getNormalizableNodeReference(nodeReference);
            responseRow = new HashMap<>();
            responseRow.put(NODE_NAME, normNodeName);

            try {
                nodeValidatorUtility.validateNodeForFtpes(normalizableNodeReference);

                String fileTransferProtocolAttributeInComConnectivityInfo = moAttributeHandler.getMOAttributeValue(normNodeFDN,
                        COM_CONNECTIVITY_INFORMATION_MO.type(), COM_CONNECTIVITY_INFORMATION_MO.namespace(), FTPES_ATTRIBUTE_NAME);

                FtpesStatus ftpesStatus;
                if (fileTransferProtocolAttributeInComConnectivityInfo.equalsIgnoreCase(ModelDefinition.ComConnectivityInformation.BasicFileTransferProtocolType.FTPES.toString())) {
                    ftpesStatus = FtpesStatus.ON;
                } else {
                    ftpesStatus = FtpesStatus.OFF;
                }

                responseRow.put(FTPES_STATUS, ftpesStatus.toString());
                responseRow.put(ERROR_MESSAGE, "N/A");
                responseRow.put(SUGGESTED_SOLUTION, "N/A");

                responseRows.add(responseRow);

            } catch (final NscsServiceException exception) {
                responseRow.put(FTPES_STATUS, "N/A");
                responseRow.put(ERROR_MESSAGE, exception.getMessage());
                responseRow.put(SUGGESTED_SOLUTION, exception.getSuggestedSolution());
                responseRows.add(responseRow);

            } catch (final Exception exception) {
                nscsLogger.commandHandlerFinishedWithError(command, exception.getMessage());
                return NscsMessageCommandResponse.message(exception.getMessage());
            }
        }
        nscsLogger.commandHandlerFinishedWithSuccess(command, COMMAND_EXECUTED_SUCCESSFULLY);
        return buildResponse(responseRows);
    }

    private NscsNameMultipleValueCommandResponse buildResponse(final List<Map<String, String>> responseRows) {
        NscsNameMultipleValueResponseBuilder responseBuilder = new NscsNameMultipleValueResponseBuilder(3);
        Map<String, Integer> header = new HashMap<>();
        header.put(FTPES_STATUS, 0);
        header.put(ERROR_MESSAGE, 1);
        header.put(SUGGESTED_SOLUTION, 2);
        responseBuilder.add(NODE_NAME, responseBuilder.formatHeader(header));
        for (Map<String, String> responseRow : responseRows) {
            responseBuilder.add(responseRow.get(NODE_NAME), responseBuilder.formatRow(header, responseRow));
        }
        return responseBuilder.getResponse();
    }
}
