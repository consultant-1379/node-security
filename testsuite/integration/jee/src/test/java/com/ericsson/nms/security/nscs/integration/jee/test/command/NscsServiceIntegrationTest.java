/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.integration.jee.test.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.handler.command.impl.CppGetSecurityLevelHandler;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.AccessControlHelper;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.ResponseDtoReader;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityDataConstants;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityDataSetup;
import com.ericsson.oss.services.scriptengine.spi.CommandHandler;
import com.ericsson.oss.services.scriptengine.spi.dtos.Command;
import com.ericsson.oss.services.scriptengine.spi.dtos.CommandResponseDto;

public class NscsServiceIntegrationTest implements CommandSecurityLevelsTests {

    private static final String SECURITY_LEVEL_INITIATED = "Security level change initiated, check the system logs for results";

    @Inject
    NodeSecurityDataSetup dataSetup;

    @Inject
    Logger log;

    @Inject
    CommandHandler commandHandler;

    @Override
    public void testCmdSlNscsService__CPP_GET_SL_Single_NODE() throws Exception {

        log.info("-----------testCmdSlNscsService__CPP_GET_SL_Single_NODE starts--------------");

        testNscsService__CPP_GET_SL_Single_NODE();

        log.info("-----------testCmdSlNscsService__CPP_GET_SL_Single_NODE ends--------------");
    }

    @Override
    public void testCmdSlNscsService__CPP_GET_SL_ALL() throws Exception {

        log.info("-----------testCmdSlNscsService__CPP_GET_SL_ALL starts--------------");

        dataSetup.insertData();

        final Command command = new Command("secadm", "sl get *");

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);
        //assertReturnedValues(commandResponseDto);

        final ResponseDtoReader responseDtoReader = new ResponseDtoReader();

        final List<String> listOfRows = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : listOfRows) {
            if (row != null) {
                log.info("testNscsService__CPP_GET_SL_ALL :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("ERBS1 level 1", listOfRows));
        assertTrue(responseDtoReader.messageIsContainedInList("ERBS2 level 1", listOfRows));

        log.info("-----------testCmdSlNscsService__CPP_GET_SL_ALL ends--------------");
    }

    @Override
    public void testCmdSlNscsService__CPP_GET_SL_2() throws Exception {

        log.info("-----------testCmdSlNscsService__CPP_GET_SL_2 starts--------------");

        dataSetup.deleteAllNodes();
        dataSetup.createNode("node_level_1", "SYNCHRONIZED", SecurityLevel.LEVEL_1, NodeSecurityDataSetup.IpAddressVersion.IPv4);
        dataSetup.createNode("node_level_2", "SYNCHRONIZED", SecurityLevel.LEVEL_2, NodeSecurityDataSetup.IpAddressVersion.IPv4);

        final Command command = new Command("secadm", "sl get -l 2 *");
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final ResponseDtoReader responseDtoReader = new ResponseDtoReader();

        final List<String> listOfRows = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : listOfRows) {
            if (row != null) {
                log.info("testNscsService__CPP_GET_SL_2 :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("node_level_2 level 2", listOfRows));

        log.info("-----------testCmdSlNscsService__CPP_GET_SL_2 ends--------------");
    }

    @Override
    public void testCmdSlNscsService__CPP_SET_SL_2_NegativeSyntaxError() throws Exception {

        log.info("-----------testCmdSlNscsService__CPP_SET_SL_2_NegativeSyntaxError starts--------------");

        dataSetup.insertData();
        dataSetup.createNetworkElementSecurity(NodeSecurityDataSetup.networkElementNameFromMeContextName("ERBS1"));

        // command with option --nodelist (-n) is no more supported
        final Command command = new Command("secadm", "securitylevel set --level 2 -n \"MeContext=ERBS1\"");

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final ResponseDtoReader responseDtoReader = new ResponseDtoReader();

        final List<String> listOfRows = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : listOfRows) {
            if (row != null) {
                log.info("testNscsService__CPP_SET_SL_2_NegativeSyntaxError : " + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(NscsErrorCodes.SYNTAX_ERROR, listOfRows));

        log.info("-----------testCmdSlNscsService__CPP_SET_SL_2_NegativeSyntaxError ends--------------");
    }

    @Override
    public void testCmdSlCommandCppSetSL2_NE_already_atSL2() throws Exception {

        log.info("-----------testCmdSlCommandCppSetSL2_NE_already_atSL2 starts--------------");

        dataSetup.deleteAllNodes();
        dataSetup.createNode("ERBS_SL2", "SYNCHRONIZED", SecurityLevel.LEVEL_2, NodeSecurityDataSetup.IpAddressVersion.IPv4);
        dataSetup.createNetworkElementSecurity(NodeSecurityDataSetup.networkElementNameFromMeContextName("ERBS_SL2"));

        final Command command = new Command("secadm", "securitylevel set --level 2 -n \"MeContext=ERBS_SL2\"");

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final ResponseDtoReader responseDtoReader = new ResponseDtoReader();

        final List<String> listOfRows = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : listOfRows) {
            if (row != null) {
                log.info("commandCppSetSL2_NE_already_atSL2 :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(NscsErrorCodes.REQUESTED_LEVEL_ALREADY_SET_MESSAGE, listOfRows));

        log.info("-----------testCmdSlCommandCppSetSL2_NE_already_atSL2 ends--------------");
    }

    @Override
    public void testCmdSlCommandCppSetSL2_NE_in_progress() throws Exception {

        log.info("-----------testCmdSlCommandCppSetSL2_NE_in_progress starts--------------");

        //Setup create node
        dataSetup.deleteAllNodes();
        final String nodeName = "ERBS_SL1_in_progress";
        dataSetup.createNode(nodeName);
        dataSetup.createNetworkElementSecurity(NodeSecurityDataSetup.networkElementNameFromMeContextName(nodeName));

        final Command command = new Command("secadm", "securitylevel set --level 2 -n \"MeContext=" + nodeName + "\"");

        //Set SL2
        CommandResponseDto commandResponseDto = commandHandler.execute(command);

        ResponseDtoReader responseDtoReader = new ResponseDtoReader();

        List<String> listOfRows = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : listOfRows) {
            if (row != null) {
                log.info("commandCppSetSL2_NE_already_atSL2 :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(SECURITY_LEVEL_INITIATED, listOfRows));

        log.info("commandCppSetSL2_NE_in_progress after first command");

        Thread.sleep(1000);
        //Set SL2 second time (workflows is ongoing)		
        commandResponseDto = commandHandler.execute(command);

        log.info("commandCppSetSL2_NE_in_progress after second command");

        responseDtoReader = new ResponseDtoReader();

        listOfRows = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : listOfRows) {
            if (row != null) {
                log.info("commandCppSetSL2_NE_already_atSL2 :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(NscsErrorCodes.PLEASE_WAIT_UNTIL_CURRENT_ACTION_COMPLETE, listOfRows));

        log.info("-----------testCmdSlCommandCppSetSL2_NE_in_progress ends--------------");
    }

    @Override
    public void testCmdSlCommandCppSetSL1_NE_in_progress_Deactivate() throws Exception {

        log.info("-----------testCmdSlCommandCppSetSL1_NE_in_progress_Deactivate starts--------------");

        //Setup create node
        dataSetup.deleteAllNodes();
        final String nodeName = "ERBS_SL1_in_progressDeActivate";
        dataSetup.createNode(nodeName, "SYNCHRONIZED", SecurityLevel.LEVEL_2, NodeSecurityDataSetup.IpAddressVersion.IPv4);
        dataSetup.createNetworkElementSecurity(NodeSecurityDataSetup.networkElementNameFromMeContextName(nodeName));

        final Command command = new Command("secadm", "securitylevel set --level 1 -n \"MeContext=" + nodeName + "\"");

        //Set SL2
        CommandResponseDto commandResponseDto = commandHandler.execute(command);

        ResponseDtoReader responseDtoReader = new ResponseDtoReader();

        List<String> listOfRows = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : listOfRows) {
            if (row != null) {
                log.info("commandCppSetSL1_NE_in_progress_Deactivate :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(SECURITY_LEVEL_INITIATED, listOfRows));

        log.info("commandCppSetSL1_NE_in_progress_Deactivate after first command");

        Thread.sleep(1000);
        //Set SL2 second time (workflows is ongoing)
        commandResponseDto = commandHandler.execute(command);

        log.info("commandCppSetSL1_NE_in_progress_Deactivate after second command");

        responseDtoReader = new ResponseDtoReader();

        listOfRows = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : listOfRows) {
            if (row != null) {
                log.info("commandCppSetSL1_NE_in_progress_Deactivate :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(NscsErrorCodes.PLEASE_WAIT_UNTIL_CURRENT_ACTION_COMPLETE, listOfRows));

        log.info("-----------testCmdSlCommandCppSetSL1_NE_in_progress_Deactivate ends--------------");
    }

    @Override
    public void testCmdSlCommandCppSetSL2_NE_in_progress_MultipleNodes() throws Exception {

        log.info("-----------testCmdSlCommandCppSetSL2_NE_in_progress_MultipleNodes starts--------------");

        dataSetup.deleteAllNodes();
        final String nodeName = "ERBS_SL1_in_progress_MultipleNodes";
        dataSetup.createNode(nodeName);
        dataSetup.createNetworkElementSecurity(NodeSecurityDataSetup.networkElementNameFromMeContextName(nodeName));
        final Command command = new Command("secadm", "securitylevel set --level 2 -n \"MeContext=" + nodeName + "\"");

        //Set SL2
        CommandResponseDto commandResponseDto = commandHandler.execute(command);

        ResponseDtoReader responseDtoReader = new ResponseDtoReader();

        List<String> listOfRows = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : listOfRows) {
            if (row != null) {
                log.info("commandCppSetSL2_NE_already_atSL2 :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(SECURITY_LEVEL_INITIATED, listOfRows));

        log.info("commandCppSetSL2_NE_in_progress after first command");

        Thread.sleep(1000);
        //Set SL2 second time (workflows is ongoing)		
        commandResponseDto = commandHandler.execute(command);

        log.info("commandCppSetSL2_NE_in_progress after second command");

        responseDtoReader = new ResponseDtoReader();

        listOfRows = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : listOfRows) {
            if (row != null) {
                log.info("commandCppSetSL2_NE_already_atSL2 :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(NscsErrorCodes.PLEASE_WAIT_UNTIL_CURRENT_ACTION_COMPLETE, listOfRows));

        log.info("-----------testCmdSlCommandCppSetSL2_NE_in_progress_MultipleNodes ends--------------");
    }

    @Override
    public void testCmdSlCommandCppSetSL2_NE_not_synced() throws Exception {

        log.info("-----------testCmdSlCommandCppSetSL2_NE_not_synced starts--------------");

        final String nodeName = "ERBS_NON_SYNCED";
        dataSetup.deleteAllNodes();
        dataSetup.createNode(nodeName, "UNSYNCHRONIZED", NodeSecurityDataSetup.IpAddressVersion.IPv4);
        final Command command = new Command("secadm", "securitylevel set --level 2 -n \"MeContext=" + nodeName + "\"");

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final ResponseDtoReader responseDtoReader = new ResponseDtoReader();

        final List<String> listOfRows = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : listOfRows) {
            if (row != null) {
                log.info("commandCppSetSL2_NE_not_synced :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(NscsErrorCodes.THE_NODE_SPECIFIED_IS_NOT_SYNCHRONIZED, listOfRows));

        dataSetup.deleteAllNodes();

        log.info("-----------testCmdSlCommandCppSetSL2_NE_not_synced ends--------------");
    }

    @Override
    public void testCmdSlCommandCppSetSL2_NE_not_synced_and_non_existing() throws Exception {

        log.info("-----------testCmdSlCommandCppSetSL2_NE_not_synced_and_non_existing starts--------------");

        final String nodeName = "ERBS_NON_SYNCED";
        dataSetup.deleteAllNodes();
        dataSetup.createNode(nodeName, "UNSYNCHRONIZED", NodeSecurityDataSetup.IpAddressVersion.IPv4);
        final Command command = new Command("secadm", "securitylevel set --level 2 -n \"MeContext=" + nodeName + "\",ERBS999");

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final ResponseDtoReader responseDtoReader = new ResponseDtoReader();

        final List<String> listOfRows = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : listOfRows) {
            if (row != null) {
                log.info("commandCppSetSL2_NE_not_synced_and_non_existing :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(
                "MeContext=ERBS_NON_SYNCED 10005 The node specified is not synchronized Please ensure the node specified is synchronized.",
                listOfRows));
        assertTrue(responseDtoReader.messageIsContainedInList(
                "NetworkElement=ERBS999 10004 The node specified does not exist Please specify a valid node that exists in the system.", listOfRows));

        dataSetup.deleteAllNodes();

        log.info("-----------testCmdSlCommandCppSetSL2_NE_not_synced_and_non_existing ends--------------");
    }

    @Override

    public void testCmdSlNscsService__CPP_GET_SL_ALL_Negative_Syntax_invalid() throws Exception {

        log.info("-----------testCmdSlNscsService__CPP_GET_SL_ALL_Negative_Syntax_invalid starts--------------");

        dataSetup.insertData();

        final Command command = new Command("secadm", "sl get ***");

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final ResponseDtoReader responseDtoReader = new ResponseDtoReader();

        final List<String> listOfRows = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : listOfRows) {
            if (row != null) {
                log.info("testNscsService__CPP_GET_SL_ALL_Negative_Syntax_invalid :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(NscsErrorCodes.SYNTAX_ERROR, listOfRows));

        log.info("-----------testCmdSlNscsService__CPP_GET_SL_ALL_Negative_Syntax_invalid ends--------------");
    }

    @Override
    public void testCmdSlNscsService__CPP_GET_SL_ALL_Negative_Nodes_invalid() throws Exception {

        log.info("-----------testCmdSlNscsService__CPP_GET_SL_ALL_Negative_Nodes_invalid starts--------------");

        dataSetup.insertData();

        final Command command = new Command("secadm", "sl get --nodelist \"MeContext=ERBS12224\"");

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final ResponseDtoReader responseDtoReader = new ResponseDtoReader();

        final List<String> listOfRows = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : listOfRows) {
            if (row != null) {
                log.info("testNscsService__CPP_GET_SL_ALL_Negative_Nodes_invalid :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(NscsErrorCodes.THE_NODE_SPECIFIED_DOES_NOT_EXIST, listOfRows));

        log.info("-----------testCmdSlNscsService__CPP_GET_SL_ALL_Negative_Nodes_invalid ends--------------");
    }

    @Override
    /**
     * This Test for now is only testing by seeing the logged event, later on this will be enhanced to assert values based on dps events
     * 
     * @throws Exception
     */
    public void testCmdSlNscsService__SecurityMO_Changed_Events() throws Exception {

        log.info("-----------testCmdSlNscsService__SecurityMO_Changed_Events starts--------------");

        //insertData();
        dataSetup.deleteAllNodes();
        final Map<String, String> map = dataSetup.createNode(NodeSecurityDataConstants.NODE_NAME1);
        final String securityMOFdn = map.get("SecurityFunctionsMOFDN");

        final Map<String, Object> mandatorySecurityAttributes = dataSetup.createMapAndInsertValues(
                NodeSecurityDataConstants.OPERATIONAL_SECURITY_LEVEL_ATTRIBUTE,
                NodeSecurityDataConstants.OPERATIONAL_SECURITY_LEVEL_ATTRIBUTE_VALUE_2, NodeSecurityDataConstants.USER_LABEL_ATTRIBUTE,
                NodeSecurityDataConstants.USER_LABEL_SECURITY_ATTRIBUTE_VALUE_2);

        final Map<String, Object> userDefProfilesInfoAttributesMap = dataSetup.createMapAndInsertValues(
                NodeSecurityDataConstants.LATEST_STATE_CHANGE_ATTRIBUTE, NodeSecurityDataConstants.LATEST_STATE_CHANGE_ATTRIBUTE_VALUE_2,
                NodeSecurityDataConstants.DESCRIPTION_ATTRIBUTE, NodeSecurityDataConstants.DESCRIPTION_ATTRIBUTE_VALUE_2,
                NodeSecurityDataConstants.STATE_ATTRIBUTE, NodeSecurityDataConstants.STATE_ATTRIBUTE_VALUE_2);

        mandatorySecurityAttributes.put(NodeSecurityDataConstants.ACTIVE_USER_PROFILE_COMPLEX_ATTRIBUTE, userDefProfilesInfoAttributesMap);

        dataSetup.changeAttributesForSecurityMO(securityMOFdn, mandatorySecurityAttributes);
        dataSetup.deleteAllNodes();

        log.info("-----------testCmdSlNscsService__SecurityMO_Changed_Events ends--------------");
    }

    @Override
    public void testCmdSlNscsService__CPP_GET_SL_ALL_Negative_Nodes_DoesNot_Exist() throws Exception {

        log.info("-----------testCmdSlNscsService__CPP_GET_SL_ALL_Negative_Nodes_DoesNot_Exist starts--------------");

        final Command command = new Command("secadm", "sl get *");

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final ResponseDtoReader responseDtoReader = new ResponseDtoReader();

        final List<String> listOfRows = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : listOfRows) {
            if (row != null) {
                log.info("testNscsService__CPP_GET_SL_ALL_Negative_Nodes_DoesNot_Exist :" + row);
            }
        }

        assertTrue(responseDtoReader
                .messageIsContainedInList("Error. No Nodes found, when attempting to retrieve the list of nodes from the database.", listOfRows));

        log.info("-----------testCmdSlNscsService__CPP_GET_SL_ALL_Negative_Nodes_DoesNot_Exist ends--------------");
    }

    @Override
    public void testCmdSlNscsService__CPP_GET_SL_Single_NODE_Invalid_User() throws Exception {

        log.info("-----------testCmdSlNscsService__CPP_GET_SL_Single_NODE_Invalid_User starts--------------");

        AccessControlHelper.setupUser("toruser1");
        try {
            testNscsService__CPP_GET_SL_Single_NODE();
            fail("Should not come at this point");
        } catch (final Exception e) {
        }

        log.info("-----------testCmdSlNscsService__CPP_GET_SL_Single_NODE_Invalid_User ends--------------");
    }

    private void testNscsService__CPP_GET_SL_Single_NODE() throws Exception {

        dataSetup.insertData();

        final Command command = new Command("secadm", "sl get -n \"MeContext=ERBS1\"");

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        //log.info("Command entered  Status Message = {}, Status Code = {}", commandResponseDto.getStatusMessage(), commandResponseDto.getStatusCode());

        final ResponseDtoReader responseDtoReader = new ResponseDtoReader();

        final List<String> listOfRows = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : listOfRows) {
            if (row != null) {
                log.info("testNscsService__CPP_GET_SL_Single_NODE :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("ERBS1 level 1", listOfRows));
    }

    private static void assertReturnedNodeInList(final Map row, final String expectedNode, final SecurityLevel expectedLevel) {
        final String actualNode = (String) row.get(CppGetSecurityLevelHandler.NODE_NAME_HEADER);
        final String actualLevel = (String) row.get(CppGetSecurityLevelHandler.NODE_SECURITY_LEVEL_HEADER);
        assertEquals(expectedNode, actualNode);
        assertEquals(CppGetSecurityLevelHandler.formatLevel(expectedLevel.getLevel()), actualLevel);
    }

}