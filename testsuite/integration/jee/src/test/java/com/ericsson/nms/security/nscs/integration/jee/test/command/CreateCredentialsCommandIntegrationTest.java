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

import static org.junit.Assert.*;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.ResponseDtoReader;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.*;
import com.ericsson.oss.services.cm.cmreader.api.CmReaderService;
import com.ericsson.oss.services.cm.cmshared.dto.CmConstants;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.scriptengine.spi.CommandHandler;
import com.ericsson.oss.services.scriptengine.spi.dtos.Command;
import com.ericsson.oss.services.scriptengine.spi.dtos.CommandResponseDto;

public class CreateCredentialsCommandIntegrationTest implements CreateCredentialsTests {

    private static final String NODE1_NAME = "ERBS1";
    private static final String NODE2_NAME = "ERBS2";
    private static final String NODE1_FDN = "MeContext=" + NODE1_NAME;
    private static final String NODE2_FDN = "MeContext=" + NODE2_NAME;
    private static final String CREATE_CREDENTIALS_USERNAME_PASSWORDS = "credentials create --rootusername \"rootUserName\" --rootuserpassword \"rootUserPassword\" --secureuserpassword \"secureUserPassword\" --secureusername \"secureUserName\" --normaluserpassword \"normalUserPassword\" --normalusername \"normalUserName\" ";
    private static final String CREATE_CREDENTIALS_WITHOUT_ROOT = "credentials create --secureuserpassword \"secureUserPassword\" --secureusername \"secureUserName\" --normaluserpassword \"normalUserPassword\" --normalusername \"normalUserName\" ";
    private static final String CREATE_CREDENTIALS_WITHOUT_ROOT_AND_NORMAL = "credentials create --secureuserpassword \"secureUserPassword\" --secureusername \"secureUserName\" ";

    private static final String EXISTING_NODES = "--nodelist " + NODE1_FDN + "," + NODE2_FDN;
    private static final String EXISTING_NODE1 = "--nodelist " + NODE1_FDN;
    private static final String VALID_INVALID_NODES = "--nodelist " + NODE1_FDN + ",INVNODE1";
    private static final String ADD_TARGET_GROUPS_COMMAND = "targetgroup add --targetgroups group2 " + EXISTING_NODES;
    private static final String ADD_EXISTING_TARGET_GROUPS_COMMAND = "targetgroup add --targetgroups defaultTargetGroup " + EXISTING_NODES;
    private static final String EXISTING_NETWORK_ELEMENT_NODES = "--nodelist \"" + NodeSecurityDataSetup.networkElementNameFromMeContextName(NODE1_NAME) + "\",\"NetworkElement="
            + NodeSecurityDataSetup.networkElementNameFromMeContextName(NODE2_NAME) + "\"";

    @Inject
    NodeSecurityDataSetup dataSetup;

    @Inject
    NodeSecurityMiniLinkIndoorNodesDataSetup miniLinkIndoorNodesDataSetup;

    @Inject
    NodeSecurityMiniLinkCN210NodesDataSetup miniLinkCn210NodesDataSetup;

    @Inject
    NodeSecurityMiniLinkCN510R1NodesDataSetup miniLinkCn510R1NodesDataSetup;

    @Inject
    NodeSecurityMiniLinkCN510R2NodesDataSetup miniLinkCn510R2NodesDataSetup;

    @Inject
    NodeSecurityMiniLinkCN810R1NodesDataSetup miniLinkCn810R1NodesDataSetup;

    @Inject
    NodeSecurityMiniLinkCN810R2NodesDataSetup miniLinkCn810R2NodesDataSetup;

    @Inject
    NodeSecurityMiniLink6352NodesDataSetup miniLink6352NodesDataSetup;

    @Inject
    NodeSecurityMiniLink6351NodesDataSetup miniLink6351NodesDataSetup;

    @Inject
    NodeSecurityMiniLink6366NodesDataSetup miniLink6366NodesDataSetup;

    @Inject
    NodeSecurityMiniLinkPT2020NodesDataSetup miniLinkPT2020NodesDataSetup;

    @Inject
    NodeSecuritySwitch6391NodesDataSetup switch6391NodesDataSetup;

    @Inject
    NodeSecurityFronthaul6392NodesDataSetup fronthaul6392NodesDataSetup;

    @Inject
    NodeSecurityCiscoAsr9000NodesDataSetup ciscoAsr9000NodesDataSetup;

    @Inject
    NodeSecurityCiscoAsr900NodesDataSetup ciscoAsr900NodesDataSetup;

    @Inject
    NodeSecurityJuniperMxNodesDataSetup juniperMxNodesDataSetup;

    @Inject
    NodeSecurityMiniLink665xNodesDataSetup miniLink665xNodesDataSetup;

    @Inject
    NodeSecurityMiniLink669xNodesDataSetup miniLink669xNodesDataSetup;

    @Inject
    NodeSecurityMiniLinkMW2NodesDataSetup miniLinkMW2NodesDataSetup;

    @Inject
    Logger log;

    @Inject
    CommandHandler commandHandler;

    @Inject
    CmReaderService cmReaderService;

    @Override
    public void cppCreateCredentialsWith2ValidNodes() throws Exception {
        log.info("-----------testNscsService__cppCreateCredentialsWith2ValidNodes starts--------------");

        dataSetup.insertData();

        final Command command = new Command("secadm", CREATE_CREDENTIALS_USERNAME_PASSWORDS + EXISTING_NODES);

        log.info("Credentials command syntax {}", command);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final ResponseDtoReader responseDtoReader = new ResponseDtoReader();

        final List<String> listOfRows = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        CmResponse nes = cmReaderService.getMoByFdn(Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.withNames(NODE1_NAME + NodeSecurityDataSetup.NETWORK_ELEMENT_NAME_SULFIX).fdn(),
                CmConstants.LIVE_CONFIGURATION);

        assertEquals("Could not find expected NetworkElementSecurity on node 1", 0, nes.getErrorCode());

        nes = cmReaderService.getMoByFdn(Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.withNames(NODE2_NAME + NodeSecurityDataSetup.NETWORK_ELEMENT_NAME_SULFIX).fdn(),
                CmConstants.LIVE_CONFIGURATION);
        assertEquals("Could not find expected NetworkElementSecurity on node 2", 0, nes.getErrorCode());

    }

    @Override
    public void cppCreateCredentialsWith2ValidNodesTwice() throws Exception {
        log.info("-----------testNscsService__cppCreateCredentialsWith2ValidNodesTwice starts--------------");

        dataSetup.insertData();

        final Command command = new Command("secadm", CREATE_CREDENTIALS_USERNAME_PASSWORDS + EXISTING_NODES);

        log.info("Credentials command syntax {}", command);

        CommandResponseDto commandResponseDto = commandHandler.execute(command);

        commandResponseDto = commandHandler.execute(command);

        final ResponseDtoReader responseDtoReader = new ResponseDtoReader();

        final List<String> listOfRows = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        log.info("Credentials Command exited with Response: {} ", listOfRows.toArray().toString());
        for (final String row : listOfRows) {
            if (row != null) {
                log.info("cppCreateCredentialsWith2ValidNodesTwice :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(NscsErrorCodes.CREDENTIALS_ALREADY_EXIST_FOR_THE_NODE_SPECIFIED, listOfRows));
    }

    @Override
    public void cppCreateCredentialsWithInvalidNodeTest() throws Exception {
        log.info("-----------testNscsService__cppCreateCredentialsWithInvalidNodeTest starts--------------");

        dataSetup.insertData();

        final Command command = new Command("secadm", CREATE_CREDENTIALS_USERNAME_PASSWORDS + VALID_INVALID_NODES);

        log.info("Credentials command syntax {}", command);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final ResponseDtoReader responseDtoReader = new ResponseDtoReader();

        final List<String> listOfRows = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : listOfRows) {
            if (row != null) {
                log.info("cppCreateCredentialsWithInvalidNodeTest :" + row);
            }
        }
        assertTrue(responseDtoReader.messageIsContainedInList(NscsErrorCodes.THE_NETWORK_ELEMENT_SPECIFIED_DOES_NOT_EXIST, listOfRows));
    }

    @Override
    public void cppCreateCredentialsWithStarTest() throws Exception {
        log.info("-----------testNscsService__cppCreateCredentialsWithStarTest starts--------------");

        dataSetup.insertData();

        final Command command = new Command("secadm", CREATE_CREDENTIALS_USERNAME_PASSWORDS + "*");

        log.info("Credentials command syntax {}", command);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final ResponseDtoReader responseDtoReader = new ResponseDtoReader();

        final List<String> listOfRows = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : listOfRows) {
            if (row != null) {
                log.info("cppCreateCredentialsWithStarTest :" + row);
            }
        }
        assertTrue(responseDtoReader.messageIsContainedInList("Unsupported command argument : Node list cannot be '*'", listOfRows));

    }

    @Override
    public void cppCreateCredentialsWithMissingAttributeTest() throws Exception {
        log.info("-----------testNscsService__cppCreateCredentialsWithMissingAttributeTest starts--------------");

        dataSetup.insertData();

        final Command command = new Command("secadm", CREATE_CREDENTIALS_WITHOUT_ROOT + EXISTING_NODES);

        log.info("Credentials command syntax {}", command);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final ResponseDtoReader responseDtoReader = new ResponseDtoReader();

        final List<String> listOfRows = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : listOfRows) {
            if (row != null) {
                log.info("cppCreateCredentialsWithMissingAttributeTest :" + row);
            }
        }
        assertTrue(responseDtoReader.messageIsContainedInList("Command syntax error", listOfRows));

    }

    @Override
    public void testAddTargetGroupsWhenSecurityMODoesNotExist() throws Exception {

        log.info("-----------testNscsService__testAddTargetGroupsWhenSecurityMODoesNotExist starts--------------");
        dataSetup.insertData();

        final Command command = new Command("secadm", ADD_TARGET_GROUPS_COMMAND);
        log.info("add-target-groups command syntax {}", command);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final ResponseDtoReader responseDtoReader = new ResponseDtoReader();

        final List<String> listOfRows = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : listOfRows) {
            if (row != null) {
                log.info("testAddTargetGroupsWhenSecurityMODoesNotExist :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(NscsErrorCodes.CREDENTIALS_DO_NOT_EXIST_FOR_THE_NODE_SPECIFIED, listOfRows));

    }

    @Override
    public void testAddTargetGroupsWhenAllSecurityMOsExist() throws Exception {

        log.info("-----------testNscsService__testAddTargetGroupsWhenAllSecurityMOsExist starts--------------");
        dataSetup.insertData();

        Command command = new Command("secadm", CREATE_CREDENTIALS_USERNAME_PASSWORDS + EXISTING_NODES);
        CommandResponseDto commandResponseDto = commandHandler.execute(command);

        command = new Command("secadm", ADD_TARGET_GROUPS_COMMAND);
        log.info("add-target-groups command syntax {}", command);
        commandResponseDto = commandHandler.execute(command);

        final ResponseDtoReader responseDtoReader = new ResponseDtoReader();

        final List<String> listOfRows = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : listOfRows) {
            if (row != null) {
                log.info("testAddTargetGroupsWhenAllSecurityMOsExist :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Target groups successfully added", listOfRows));

    }

    @Override
    public void testAddTargetGroupsWhenSomeSecurityMOsExist() throws Exception {

        log.info("-----------testNscsService__testAddTargetGroupsWhenSomeSecurityMOsExist starts--------------");
        dataSetup.insertData();

        Command command = new Command("secadm", CREATE_CREDENTIALS_USERNAME_PASSWORDS + EXISTING_NODE1);
        CommandResponseDto commandResponseDto = commandHandler.execute(command);

        command = new Command("secadm", ADD_TARGET_GROUPS_COMMAND);
        log.info("add-target-groups command syntax {}", command);
        commandResponseDto = commandHandler.execute(command);

        final ResponseDtoReader responseDtoReader = new ResponseDtoReader();

        final List<String> listOfRows = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : listOfRows) {
            if (row != null) {
                log.info("testAddTargetGroupsWhenSomeSecurityMOsExist :" + row);
            }
        }
        assertTrue(responseDtoReader.messageIsContainedInList(NscsErrorCodes.CREDENTIALS_DO_NOT_EXIST_FOR_THE_NODE_SPECIFIED, listOfRows));

    }

    @Override
    public void testAddTargetGroupsWhenTargetGroupsAlreadySet() throws Exception {

        log.info("-----------testNscsService__testAddTargetGroupsWhenTargetGroupsAlreadySet starts--------------");
        dataSetup.insertData();

        Command command = new Command("secadm", CREATE_CREDENTIALS_USERNAME_PASSWORDS + EXISTING_NODES);
        CommandResponseDto commandResponseDto = commandHandler.execute(command);

        command = new Command("secadm", ADD_EXISTING_TARGET_GROUPS_COMMAND);
        log.info("add-target-groups command syntax {}", command);
        commandResponseDto = commandHandler.execute(command);

        final ResponseDtoReader responseDtoReader = new ResponseDtoReader();

        final List<String> listOfRows = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : listOfRows) {
            if (row != null) {
                log.info("testAddTargetGroupsWhenTargetGroupsAlreadySet :" + row);
            }
        }
        assertTrue(responseDtoReader.messageIsContainedInList("Node has one or more target groups already set", listOfRows));

    }

    @Override
    public void testAddTargetGroupsWhenTargetGroupsNotAlreadySet() throws Exception {

        log.info("-----------testNscsService__testAddTargetGroupsWhenTargetGroupsNotAlreadySet starts--------------");
        dataSetup.insertData();

        Command command = new Command("secadm", CREATE_CREDENTIALS_USERNAME_PASSWORDS + EXISTING_NODES);
        CommandResponseDto commandResponseDto = commandHandler.execute(command);

        command = new Command("secadm", ADD_TARGET_GROUPS_COMMAND);
        log.info("add-target-groups command syntax {}", command);
        commandResponseDto = commandHandler.execute(command);

        log.info("add-target-groups command entered  Status Message = {}, Status Code = {}, Error Code = {}", commandResponseDto.getStatusMessage(), commandResponseDto.getStatusCode(),
                commandResponseDto.getErrorCode());

        log.info("Attempting to add same target groups again which should result in an error");
        commandResponseDto = commandHandler.execute(command);

        final ResponseDtoReader responseDtoReader = new ResponseDtoReader();

        final List<String> listOfRows = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : listOfRows) {
            if (row != null) {
                log.info("testAddTargetGroupsWhenTargetGroupsNotAlreadySet :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Node has one or more target groups already set", listOfRows));

    }

    @Override
    public void cppCreateCredentialsWith2ValidNetworkElementNodes() throws Exception {
        log.info("-----------testNscsService__cppCreateCredentialsWith2ValidNetworkElementNodes starts--------------");

        dataSetup.insertData();

        final Command command = new Command("secadm", CREATE_CREDENTIALS_USERNAME_PASSWORDS + EXISTING_NETWORK_ELEMENT_NODES);

        log.info("Credentials command syntax {}", command);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        log.info("Credentials Command exited with  Status Message = {}, Status Code = {}, Error Code = {}", commandResponseDto.getStatusMessage(), commandResponseDto.getStatusCode(),
                commandResponseDto.getErrorCode());

        final ResponseDtoReader responseDtoReader = new ResponseDtoReader();

        final List<String> listOfRows = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : listOfRows) {
            if (row != null) {
                log.info("testNscsService__cppCreateCredentialsWith2ValidNetworkElementNodes :" + row);
            }
        }

        CmResponse nes = cmReaderService.getMoByFdn(Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.withNames(NodeSecurityDataSetup.networkElementNameFromMeContextName(NODE1_NAME))
                .fdn(), CmConstants.LIVE_CONFIGURATION);
        assertTrue(responseDtoReader.messageIsContainedInList("All credentials were created successfully", listOfRows));

        nes = cmReaderService.getMoByFdn(Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.withNames(NodeSecurityDataSetup.networkElementNameFromMeContextName(NODE2_NAME)).fdn(),
                CmConstants.LIVE_CONFIGURATION);
        assertTrue(responseDtoReader.messageIsContainedInList("All credentials were created successfully", listOfRows));
    }

    @Override
    public void testMiniLinkIndoorCreateCredentials() throws Exception {
        log.info("\n\n\n\n\n-----------testNscsService__testMiniLinkIndoorCreateCredentials starts--------------");
        miniLinkIndoorNodesDataSetup.insertData();
        final String neName = miniLinkIndoorNodesDataSetup.getNeName();
        testCreateCredentialsFor(neName, CREATE_CREDENTIALS_USERNAME_PASSWORDS);
        log.info("-----------testNscsService__testMiniLinkIndoorCreateCredentials ends----------------\n\n\n\n\n");
    }

    @Override
    public void testMiniLinkCn210CreateCredentials() throws Exception {
        log.info("\n\n\n\n\n-----------testNscsService__testMiniLinkCn210CreateCredentials starts--------------");
        miniLinkCn210NodesDataSetup.insertData();
        final String neName = miniLinkCn210NodesDataSetup.getNeName();
        testCreateCredentialsFor(neName, CREATE_CREDENTIALS_USERNAME_PASSWORDS);
        log.info("-----------testNscsService__testMiniLinkCn210CreateCredentials ends----------------\n\n\n\n\n");
    }


    @Override
    public void testMiniLink665xCreateCredentials() throws Exception {
        log.info("\n\n\n\n\n-----------testNscsService__testMiniLink665xCreateCredentials starts--------------");
        miniLink665xNodesDataSetup.insertData();
        final String neName = miniLink665xNodesDataSetup.getNeName();
        testCreateCredentialsFor(neName, CREATE_CREDENTIALS_USERNAME_PASSWORDS);
        log.info("-----------testNscsService__testMiniLink665xCreateCredentials ends----------------\n\n\n\n\n");
    }

    @Override
    public void testMiniLinkMW2CreateCredentials() throws Exception {
        log.info("\n\n\n\n\n-----------testNscsService__testMiniLinkMW2CreateCredentials starts--------------");
        miniLinkMW2NodesDataSetup.insertData();
        final String neName = miniLinkMW2NodesDataSetup.getNeName();
        testCreateCredentialsFor(neName, CREATE_CREDENTIALS_USERNAME_PASSWORDS);
        log.info("-----------testNscsService__testMiniLinkMW2CreateCredentials ends----------------\n\n\n\n\n");
    }

    @Override
    public void testMiniLink669xCreateCredentials() throws Exception {
        log.info("\n\n\n\n\n-----------testNscsService__testMiniLink669xCreateCredentials starts--------------");
        miniLink669xNodesDataSetup.insertData();
        final String neName = miniLink669xNodesDataSetup.getNeName();
        testCreateCredentialsFor(neName, CREATE_CREDENTIALS_USERNAME_PASSWORDS);
        log.info("-----------testNscsService__testMiniLink669xCreateCredentials ends----------------\n\n\n\n\n");
    }

    @Override
    public void testMiniLinkCn510R1CreateCredentials() throws Exception {
        log.info("\n\n\n\n\n-----------testNscsService__testMiniLinkCn510R1CreateCredentials starts--------------");
        miniLinkCn510R1NodesDataSetup.insertData();
        final String neName = miniLinkCn510R1NodesDataSetup.getNeName();
        testCreateCredentialsFor(neName, CREATE_CREDENTIALS_USERNAME_PASSWORDS);
        log.info("-----------testNscsService__testMiniLinkCn510R1CreateCredentials ends----------------\n\n\n\n\n");
    }

    @Override
    public void testMiniLinkCn510R2CreateCredentials() throws Exception {
        log.info("\n\n\n\n\n-----------testNscsService__testMiniLinkCn510R2CreateCredentials starts--------------");
        miniLinkCn510R2NodesDataSetup.insertData();
        final String neName = miniLinkCn510R2NodesDataSetup.getNeName();
        testCreateCredentialsFor(neName, CREATE_CREDENTIALS_USERNAME_PASSWORDS);
        log.info("-----------testNscsService__testMiniLinkCn510R2CreateCredentials ends----------------\n\n\n\n\n");
    }

    @Override
    public void testMiniLinkCn810R1CreateCredentials() throws Exception {
        log.info("\n\n\n\n\n-----------testNscsService__testMiniLinkCn810R1CreateCredentials starts--------------");
        miniLinkCn810R1NodesDataSetup.insertData();
        final String neName = miniLinkCn810R1NodesDataSetup.getNeName();
        testCreateCredentialsFor(neName, CREATE_CREDENTIALS_USERNAME_PASSWORDS);
        log.info("-----------testNscsService__testMiniLinkCn810R1CreateCredentials ends----------------\n\n\n\n\n");
    }

    @Override
    public void testMiniLinkCn810R2CreateCredentials() throws Exception {
        log.info("\n\n\n\n\n-----------testNscsService__testMiniLinkCn810R2CreateCredentials starts--------------");
        miniLinkCn810R2NodesDataSetup.insertData();
        final String neName = miniLinkCn810R2NodesDataSetup.getNeName();
        testCreateCredentialsFor(neName, CREATE_CREDENTIALS_USERNAME_PASSWORDS);
        log.info("-----------testNscsService__testMiniLinkCn810R2CreateCredentials ends----------------\n\n\n\n\n");
    }

    @Override
    public void testMiniLink6352CreateCredentials() throws Exception {
        log.info("\n\n\n\n\n-----------testNscsService__testMiniLink6352CreateCredentials starts--------------");
        miniLink6352NodesDataSetup.insertData();
        final String neName = miniLink6352NodesDataSetup.getNeName();
        testCreateCredentialsFor(neName, CREATE_CREDENTIALS_USERNAME_PASSWORDS);
        log.info("-----------testNscsService__testMiniLink6352CreateCredentials ends----------------\n\n\n\n\n");
    }

    @Override
    public void testMiniLink6351CreateCredentials() throws Exception {
        log.info("\n\n\n\n\n-----------testNscsService__testMiniLink6351CreateCredentials starts--------------");
        miniLink6351NodesDataSetup.insertData();
        final String neName = miniLink6351NodesDataSetup.getNeName();
        testCreateCredentialsFor(neName, CREATE_CREDENTIALS_USERNAME_PASSWORDS);
        log.info("-----------testNscsService__testMiniLink6351CreateCredentials ends----------------\n\n\n\n\n");
    }

    @Override
    public void testMiniLink6366CreateCredentials() throws Exception {
        log.info("\n\n\n\n\n-----------testNscsService__testMiniLink6366CreateCredentials starts--------------");
        miniLink6366NodesDataSetup.insertData();
        final String neName = miniLink6366NodesDataSetup.getNeName();
        testCreateCredentialsFor(neName, CREATE_CREDENTIALS_USERNAME_PASSWORDS);
        log.info("-----------testNscsService__testMiniLink6366CreateCredentials ends----------------\n\n\n\n\n");
    }

    @Override
    public void testMiniLinkPT2020CreateCredentials() throws Exception {
        log.info("\n\n\n\n\n-----------testNscsService__testMiniLinkPT2020CreateCredentials starts--------------");
        miniLinkPT2020NodesDataSetup.insertData();
        final String neName = miniLinkPT2020NodesDataSetup.getNeName();
        testCreateCredentialsFor(neName, CREATE_CREDENTIALS_USERNAME_PASSWORDS);
        log.info("-----------testNscsService__testMiniLinkPT2020CreateCredentials ends----------------\n\n\n\n\n");
    }

    @Override
    public void testCiscoAsr9000CreateCredentials() throws Exception {
        log.info("\n\n\n\n\n-----------testNscsService__testCiscoAsr9000CreateCredentials starts--------------");
        ciscoAsr9000NodesDataSetup.insertData();
        final String neName = ciscoAsr9000NodesDataSetup.getNeName();
        testCreateCredentialsFor(neName, CREATE_CREDENTIALS_WITHOUT_ROOT_AND_NORMAL);
        log.info("-----------testNscsService__testCiscoAsr9000CreateCredentials ends----------------\n\n\n\n\n");
    }

    @Override
    public void testCiscoAsr900CreateCredentials() throws Exception {
        log.info("\n\n\n\n\n-----------testNscsService__testCiscoAsr900CreateCredentials starts--------------");
        ciscoAsr900NodesDataSetup.insertData();
        final String neName = ciscoAsr900NodesDataSetup.getNeName();
        testCreateCredentialsFor(neName, CREATE_CREDENTIALS_WITHOUT_ROOT_AND_NORMAL);
        log.info("-----------testNscsService__testCiscoAsr900CreateCredentials ends----------------\n\n\n\n\n");
    }

    @Override
    public void testJuniperMxCreateCredentials() throws Exception {
        log.info("\n\n\n\n\n-----------testNscsService__testJuniperMxCreateCredentials starts--------------");
        juniperMxNodesDataSetup.insertData();
        final String neName = juniperMxNodesDataSetup.getNeName();
        testCreateCredentialsFor(neName, CREATE_CREDENTIALS_WITHOUT_ROOT_AND_NORMAL);
        log.info("-----------testNscsService__testJuniperMxCreateCredentials ends----------------\n\n\n\n\n");
    }

    private void testCreateCredentialsFor(final String neName, final String credentialsToCreate) {
        final Command command = new Command("secadm", credentialsToCreate + " -n " + neName);
        log.info("\n\n\n\n\nCredentials command syntax {}", command);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);
        log.info("\n\n\n\n\nCredentials command exited with: Status Message = {}, Status Code = {}, Error Code = {}", commandResponseDto.getStatusMessage(), commandResponseDto.getStatusCode(),
                commandResponseDto.getErrorCode());
        assertEquals(0, commandResponseDto.getStatusCode());

        final ResponseDtoReader responseDtoReader = new ResponseDtoReader();
        final List<String> listOfRows = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        log.info("\n\n\n\n\nCredentials command response:");
        for (final String row : listOfRows) {
            if (row != null) {
                log.info(row);
            }
        }
        assertTrue(responseDtoReader.messageIsContainedInList("All credentials were created successfully", listOfRows));

        final String nodeFdn = "NetworkElement=" + neName + ",SecurityFunction=1,NetworkElementSecurity=1";
        final CmResponse networkElementSecurityRequest = cmReaderService.getMoByFdn(nodeFdn, CmConstants.LIVE_CONFIGURATION);
        assertEquals("Could not find expected NetworkElementSecurity on node", 0, networkElementSecurityRequest.getErrorCode());
    }

    @Override
    public void testSwitch6391CreateCredentials() throws Exception {
        log.info("\n\n\n\n\n-----------testNscsService__testSwitch6391CreateCredentials starts--------------");
        switch6391NodesDataSetup.insertData();
        final String neName = switch6391NodesDataSetup.getNeName();
        testCreateCredentialsFor(neName, CREATE_CREDENTIALS_USERNAME_PASSWORDS);
        log.info("-----------testNscsService__testSwitch6391CreateCredentials ends----------------\n\n\n\n\n");
    }
    
    @Override
    public void testFronthaul6392CreateCredentials() throws Exception {
        log.info("\n\n\n\n\n-----------testNscsService__testFronthaul6392CreateCredentials starts--------------");
        fronthaul6392NodesDataSetup.insertData();
        final String neName = fronthaul6392NodesDataSetup.getNeName();
        testCreateCredentialsFor(neName, CREATE_CREDENTIALS_USERNAME_PASSWORDS);
        log.info("-----------testNscsService__testFronthaul6392CreateCredentials ends----------------\n\n\n\n\n");
    }

}
