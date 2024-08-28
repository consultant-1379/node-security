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
package com.ericsson.nms.security.nscs.integration.jee.test.command;

import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.ResponseDtoReader;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityCPPNodesDataSetup;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityRadioNodesDataSetup;
import com.ericsson.oss.services.scriptengine.spi.CommandHandler;
import com.ericsson.oss.services.scriptengine.spi.dtos.Command;
import com.ericsson.oss.services.scriptengine.spi.dtos.CommandResponseDto;
import org.slf4j.Logger;

import javax.inject.Inject;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

public class SecurityProtocolChangeTests {

    public static final String RADIO_NODE = "RadioNode";
    public static final String CPP_NODE = "CppNode";
    public static final String NOT_EXISTING_NODE = "NotExistingNode";
    private static final String SECADM = "secadm";
    private static final String JOB_GET_COMMAND = "job get -j ";
    private static final String ERROR_STATUS = "ERROR";
    private static final String NODE_NOT_EXIST_MESSAGE = "Node does not exist.";
    private static final String NODE_TYPE_NOT_SUPPORTED_MESSAGE = "Node does not support";
    private static final String NODE_NOT_SYNCHRONIZED_MESSAGE = "Node is not synchronized.";

    @Inject
    private CommandHandler commandHandler;

    @Inject
    private NodeSecurityCPPNodesDataSetup nodeSecurityCPPNodesDataSetup;

    @Inject
    private NodeSecurityRadioNodesDataSetup nodeSecurityRadioNodesDataSetup;

    @Inject
    private Logger logger;

    private ResponseDtoReader responseReader = new ResponseDtoReader();


    public void nodeNotExistTest(SecurityProtocol securityProtocol, String commandToExecute) throws Exception {
        logger.info("******* SecurityProtocolChangeCommandShouldInformWhenNodeNotExist Test Started - Command: [" + commandToExecute + "] ******");

        deleteAllNodes();

        final Command command = new Command(SECADM, commandToExecute + NOT_EXISTING_NODE);

        final List<String> workflowResultResponse = getExecutedCommandResponse(command);

        assertTrue(responseReader.messageIsContainedInList(NODE_NOT_EXIST_MESSAGE, workflowResultResponse));

        deleteAllNodes();

        logger.info("******* SecurityProtocolChangeCommandShouldInformWhenNodeNotExist Test Finished - Command: [" + commandToExecute + "] ******");
    }

    public void nodeNotSyncTest(SecurityProtocol securityProtocol, String commandToExecute) throws Exception {
        logger.info("******* SecurityProtocolChangeCommandShouldInformWhenNodeIsNotSync Test Started - Command: [" + commandToExecute + "] ******");

        deleteAllNodes();

        createNode(securityProtocol.getSupportedNodes(), "UNSYNCHRONIZED", SecurityLevel.LEVEL_1);

        final Command command = new Command(SECADM, commandToExecute + getNodeName(securityProtocol, true));

        final List<String> workflowResultResponse = getExecutedCommandResponse(command);


        assertTrue(responseReader.messageIsContainedInList(NODE_NOT_SYNCHRONIZED_MESSAGE, workflowResultResponse));

        deleteAllNodes();

        logger.info("******* SecurityProtocolChangeCommandShouldInformWhenNodeIsNotSyncTest Finished - Command: [" + commandToExecute + "] ******");
    }

    public void wrongNodeTypeTest(SecurityProtocol securityProtocol, String commandToExecute) throws Exception {
        logger.info("******* SecurityProtocolChangeCommandShouldInformWhenNodeIsWrongType Test Started - Command: [" + commandToExecute + "] ******");

        deleteAllNodes();

        createNode(securityProtocol.getUnsupportedNodes(), "SYNCHRONIZED", SecurityLevel.LEVEL_1);

        final Command command = new Command(SECADM, commandToExecute + getNodeName(securityProtocol, false));

        final List<String> workflowResultResponse = getExecutedCommandResponse(command);

        assertTrue(responseReader.messageIsContainedInList(NODE_TYPE_NOT_SUPPORTED_MESSAGE, workflowResultResponse));

        deleteAllNodes();

        logger.info("******* SecurityProtocolChangeCommandShouldInformWhenNodeIsWrongType Test Finished - Command: [" + commandToExecute + "] ******");
    }

    private void createNode(String nodeType, String sync, SecurityLevel securityLevel) throws Exception {
        try {
            switch (nodeType) {
                case RADIO_NODE:
                    nodeSecurityRadioNodesDataSetup.createNode(RADIO_NODE, sync, securityLevel);
                case CPP_NODE:
                    nodeSecurityCPPNodesDataSetup.createCPPNode(CPP_NODE, sync, securityLevel);
            }
        } catch (Exception e) {
            logger.error(String.format("Error while creating node: %s", nodeType));
            throw new Exception(e.getMessage());
        }
    }

    private void deleteAllNodes() throws Exception {
        nodeSecurityRadioNodesDataSetup.deleteAllNodes();
        nodeSecurityCPPNodesDataSetup.deleteAllNodes();
    }

    private List<String> getExecutedCommandResponse(Command command) throws Exception {
        logger.info("Executing [" + command.getCommand() + "] command");

        final CommandResponseDto activateCommandResponse = commandHandler.execute(command);
        final String workflowID = extractWorkflowIdFormCommandResponse(activateCommandResponse);
        final int workflowStatusCheckAttempts = 20;

        final Command getCommand = new Command(SECADM, JOB_GET_COMMAND + workflowID);

        logger.info("Executing JOB GET commands for workflowid: " + workflowID);
        for (int i = 0; i < workflowStatusCheckAttempts; i++) {
            final CommandResponseDto getCommandResponse = commandHandler.execute(getCommand);
            List<String> commandResponseAsList = responseReader.extractListOfRowsFromCommandResponseDto(getCommandResponse);

            if (responseReader.messageIsContainedInList(ERROR_STATUS, commandResponseAsList)) {
                logger.info("Command [" + command.getCommand() + "] workflow finished with error.");
                return commandResponseAsList;
            }
            Thread.sleep(20000);
        }
        logger.info("Command [" + command.getCommand() + "] workflow workflow timeout error.");
        return Collections.emptyList();
    }

    private String extractWorkflowIdFormCommandResponse(CommandResponseDto commandResponse) throws Exception {

        final List<String> responseAsStrings = responseReader.extractListOfRowsFromCommandResponseDto(commandResponse);

        Pattern pattern = Pattern.compile("-j (.*?)'");
        Matcher matcher = pattern.matcher(responseAsStrings.get(0));

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            throw new Exception("Workflow id not found");
        }
    }

    private String getNodeName(SecurityProtocol securityProtocol, boolean isSupported) {

        StringBuilder nodeName = new StringBuilder();

        if (isSupported) {
            nodeName.append(securityProtocol.getSupportedNodes());
        } else {
            nodeName.append(securityProtocol.getUnsupportedNodes());
        }

        if(nodeName.toString().equals(CPP_NODE)){
            nodeName.append("-NE");
        }

        return nodeName.toString();
    }
}
