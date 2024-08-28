/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.integration.jee.test.command;

import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.integration.jee.test.utils.ResponseDtoReader;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityCiscoAsr9000NodesDataSetup;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityCiscoAsr900NodesDataSetup;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityDataConstants;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityFronthaul6392NodesDataSetup;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityJuniperMxNodesDataSetup;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityMiniLink6351NodesDataSetup;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityMiniLink6352NodesDataSetup;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityMiniLink665xNodesDataSetup;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityMiniLink669xNodesDataSetup;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityMiniLinkMW2NodesDataSetup;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityMiniLink6366NodesDataSetup;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityMiniLinkCN210NodesDataSetup;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityMiniLinkCN510R1NodesDataSetup;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityMiniLinkCN510R2NodesDataSetup;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityMiniLinkCN810R1NodesDataSetup;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityMiniLinkCN810R2NodesDataSetup;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityMiniLinkIndoorNodesDataSetup;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityMiniLinkPT2020NodesDataSetup;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityRadioNodesDataSetup;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecuritySwitch6391NodesDataSetup;
import com.ericsson.oss.services.scriptengine.spi.CommandHandler;
import com.ericsson.oss.services.scriptengine.spi.dtos.Command;
import com.ericsson.oss.services.scriptengine.spi.dtos.CommandResponseDto;
import com.ericsson.oss.services.scriptengine.spi.dtos.ResponseDto;

public class SnmpAuthTest implements SnmpAuthTests {

    @Inject
    CommandHandler commandHandler;

    @Inject
    NodeSecurityRadioNodesDataSetup dataSetup;

    @Inject
    NodeSecurityMiniLinkIndoorNodesDataSetup minilinkIndoorNodesDataSetup;

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
    NodeSecurityMiniLink6352NodesDataSetup minilink6352NodesDataSetup;

    @Inject
    NodeSecurityMiniLink6351NodesDataSetup minilink6351NodesDataSetup;

    @Inject
    NodeSecurityMiniLink6366NodesDataSetup minilink6366NodesDataSetup;

    @Inject
    NodeSecurityMiniLinkPT2020NodesDataSetup minilinkPT2020NodesDataSetup;

    @Inject
    NodeSecuritySwitch6391NodesDataSetup switch6391NodeDataSetup;

    @Inject
    NodeSecurityFronthaul6392NodesDataSetup fronthaul6392NodeDataSetup;

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

    private final static String SNMP_AUTHPRIV_COMMAND = "snmp authpriv";
    private final static String SNMP_AUTHNOPRIV_COMMAND = "snmp authnopriv";

    private static final String CREDS_CREATE = "credentials create";

    public static final String USERNAME_PASSWORDS = "  --secureusername \"secureUserName\" --secureuserpassword \"secureUserPassword\" ";
    public static final String ALL_USERNAME_PASSWORDS = " --rootusername u1 --rootuserpassword pw1 --secureusername u2 --secureuserpassword pw2 --normalusername u3 --normaluserpassword pw3 ";

    public static final String AUTH_PASSWD = " --auth_password ericsson1235";
    public static final String AUTH_ALGO = " --auth_algo MD5";
    public static final String PRIV_PASSWD = " --priv_password ericsson1234";
    public static final String PRIV_ALGO = " --priv_algo DES";

    ResponseDtoReader responseDtoReader = new ResponseDtoReader();

    @Override
    public void snmpAuthpriv() throws Exception {

        dataSetup.insertData();

        createCredentials(NodeSecurityDataConstants.MECONTEXT_FDN3);

        final Command command = new Command("secadm",
                SNMP_AUTHPRIV_COMMAND + AUTH_ALGO + AUTH_PASSWD + PRIV_ALGO + PRIV_PASSWD + " -n " + NodeSecurityDataConstants.MECONTEXT_FDN3);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        log.info("snmpAuthpriv commandResponseDto.getStatusMessage(): " + commandResponseDto.getStatusMessage());
        log.info("snmpAuthpriv commandResponseDto.getCommand(): " + commandResponseDto.getCommand());
        log.info("snmpAuthpriv commandResponseDto.getSolution(): " + commandResponseDto.getSolution());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto(): " + commandResponseDto.getResponseDto());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto().getClass(): " + commandResponseDto.getResponseDto().getClass());
        log.info("snmpAuthpriv commandResponseDto.getStatusCode(): " + commandResponseDto.getStatusCode());
        log.info("snmpAuthpriv commandResponseDto.getErrorCode(): " + commandResponseDto.getErrorCode());

        final ResponseDto responseDto = commandResponseDto.getResponseDto();

        log.info("snmpAuthpriv NoderesponseDto size of elements list: " + responseDto.getElements().size());

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info(row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Command OK", rowsAsListOfStrings));

        dataSetup.deleteAllNodes();

    }

    @Override
    public void snmpAuthnopriv() throws Exception {

        dataSetup.insertData();

        createCredentials(NodeSecurityDataConstants.MECONTEXT_FDN3);

        final Command command = new Command("secadm",
                SNMP_AUTHNOPRIV_COMMAND + AUTH_ALGO + AUTH_PASSWD + " -n " + NodeSecurityDataConstants.MECONTEXT_FDN3);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        log.info("snmpAuthpriv commandResponseDto.getStatusMessage(): " + commandResponseDto.getStatusMessage());
        log.info("snmpAuthpriv commandResponseDto.getCommand(): " + commandResponseDto.getCommand());
        log.info("snmpAuthpriv commandResponseDto.getSolution(): " + commandResponseDto.getSolution());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto(): " + commandResponseDto.getResponseDto());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto().getClass(): " + commandResponseDto.getResponseDto().getClass());
        log.info("snmpAuthpriv commandResponseDto.getStatusCode(): " + commandResponseDto.getStatusCode());
        log.info("snmpAuthpriv commandResponseDto.getErrorCode(): " + commandResponseDto.getErrorCode());

        final ResponseDto responseDto = commandResponseDto.getResponseDto();

        log.info("snmpAuthpriv NoderesponseDto size of elements list: " + responseDto.getElements().size());

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info(row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Command OK", rowsAsListOfStrings));

        dataSetup.deleteAllNodes();

    }

    @Override
    public void miniLinkCn210SnmpAuthPriv() throws Exception {

        miniLinkCn210NodesDataSetup.insertData();

        createMiniLinkCredentials(NodeSecurityDataConstants.NODE_NAME_ML_CN210);

        final Command command = new Command("secadm",
                SNMP_AUTHPRIV_COMMAND + AUTH_ALGO + AUTH_PASSWD + PRIV_ALGO + PRIV_PASSWD + " -n " + NodeSecurityDataConstants.NODE_NAME_ML_CN210);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        log.info("snmpAuthpriv commandResponseDto.getStatusMessage(): " + commandResponseDto.getStatusMessage());
        log.info("snmpAuthpriv commandResponseDto.getCommand(): " + commandResponseDto.getCommand());
        log.info("snmpAuthpriv commandResponseDto.getSolution(): " + commandResponseDto.getSolution());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto(): " + commandResponseDto.getResponseDto());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto().getClass(): " + commandResponseDto.getResponseDto().getClass());
        log.info("snmpAuthpriv commandResponseDto.getStatusCode(): " + commandResponseDto.getStatusCode());
        log.info("snmpAuthpriv commandResponseDto.getErrorCode(): " + commandResponseDto.getErrorCode());

        final ResponseDto responseDto = commandResponseDto.getResponseDto();

        log.info("snmpAuthpriv NoderesponseDto size of elements list: " + responseDto.getElements().size());

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info(row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Command OK", rowsAsListOfStrings));

        miniLinkCn210NodesDataSetup.deleteAllNodes();

    }

    @Override
    public void miniLinkCn210SnmpAuthNoPriv() throws Exception {

        miniLinkCn210NodesDataSetup.insertData();

        createMiniLinkCredentials(NodeSecurityDataConstants.NODE_NAME_ML_CN210);

        final Command command = new Command("secadm",
                SNMP_AUTHNOPRIV_COMMAND + AUTH_ALGO + AUTH_PASSWD + " -n " + NodeSecurityDataConstants.NODE_NAME_ML_CN210);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        log.info("snmpAuthpriv commandResponseDto.getStatusMessage(): " + commandResponseDto.getStatusMessage());
        log.info("snmpAuthpriv commandResponseDto.getCommand(): " + commandResponseDto.getCommand());
        log.info("snmpAuthpriv commandResponseDto.getSolution(): " + commandResponseDto.getSolution());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto(): " + commandResponseDto.getResponseDto());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto().getClass(): " + commandResponseDto.getResponseDto().getClass());
        log.info("snmpAuthpriv commandResponseDto.getStatusCode(): " + commandResponseDto.getStatusCode());
        log.info("snmpAuthpriv commandResponseDto.getErrorCode(): " + commandResponseDto.getErrorCode());

        final ResponseDto responseDto = commandResponseDto.getResponseDto();

        log.info("snmpAuthpriv NoderesponseDto size of elements list: " + responseDto.getElements().size());

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info(row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Command OK", rowsAsListOfStrings));

        miniLinkCn210NodesDataSetup.deleteAllNodes();

    }

    @Override
    public void miniLink665xSnmpAuthNoPriv() throws Exception {

        miniLink665xNodesDataSetup.insertData();

        createMiniLinkCredentials(NodeSecurityDataConstants.NODE_NAME_ML_665x);

        final Command command = new Command("secadm",
                SNMP_AUTHNOPRIV_COMMAND + AUTH_ALGO + AUTH_PASSWD + " -n " + NodeSecurityDataConstants.NODE_NAME_ML_665x);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        log.info("snmpAuthpriv commandResponseDto.getStatusMessage(): " + commandResponseDto.getStatusMessage());
        log.info("snmpAuthpriv commandResponseDto.getCommand(): " + commandResponseDto.getCommand());
        log.info("snmpAuthpriv commandResponseDto.getSolution(): " + commandResponseDto.getSolution());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto(): " + commandResponseDto.getResponseDto());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto().getClass(): " + commandResponseDto.getResponseDto().getClass());
        log.info("snmpAuthpriv commandResponseDto.getStatusCode(): " + commandResponseDto.getStatusCode());
        log.info("snmpAuthpriv commandResponseDto.getErrorCode(): " + commandResponseDto.getErrorCode());

        final ResponseDto responseDto = commandResponseDto.getResponseDto();

        log.info("snmpAuthpriv NoderesponseDto size of elements list: " + responseDto.getElements().size());

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info(row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Command OK", rowsAsListOfStrings));

        miniLink665xNodesDataSetup.deleteAllNodes();

    }

    @Override
    public void miniLink669xSnmpAuthNoPriv() throws Exception {

        miniLink669xNodesDataSetup.insertData();

        createMiniLinkCredentials(NodeSecurityDataConstants.NODE_NAME_ML_669x);

        final Command command = new Command("secadm",
                SNMP_AUTHNOPRIV_COMMAND + AUTH_ALGO + AUTH_PASSWD + " -n " + NodeSecurityDataConstants.NODE_NAME_ML_669x);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        log.info("snmpAuthpriv commandResponseDto.getStatusMessage(): " + commandResponseDto.getStatusMessage());
        log.info("snmpAuthpriv commandResponseDto.getCommand(): " + commandResponseDto.getCommand());
        log.info("snmpAuthpriv commandResponseDto.getSolution(): " + commandResponseDto.getSolution());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto(): " + commandResponseDto.getResponseDto());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto().getClass(): " + commandResponseDto.getResponseDto().getClass());
        log.info("snmpAuthpriv commandResponseDto.getStatusCode(): " + commandResponseDto.getStatusCode());
        log.info("snmpAuthpriv commandResponseDto.getErrorCode(): " + commandResponseDto.getErrorCode());

        final ResponseDto responseDto = commandResponseDto.getResponseDto();

        log.info("snmpAuthpriv NoderesponseDto size of elements list: " + responseDto.getElements().size());

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info(row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Command OK", rowsAsListOfStrings));

        miniLink669xNodesDataSetup.deleteAllNodes();

    }

    @Override
    public void miniLinkMW2SnmpAuthNoPriv() throws Exception {

        miniLinkMW2NodesDataSetup.insertData();

        createMiniLinkCredentials(NodeSecurityDataConstants.NODE_NAME_ML_MW2);

        final Command command = new Command("secadm",
                SNMP_AUTHNOPRIV_COMMAND + AUTH_ALGO + AUTH_PASSWD + " -n " + NodeSecurityDataConstants.NODE_NAME_ML_MW2);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        log.info("snmpAuthpriv commandResponseDto.getStatusMessage(): " + commandResponseDto.getStatusMessage());
        log.info("snmpAuthpriv commandResponseDto.getCommand(): " + commandResponseDto.getCommand());
        log.info("snmpAuthpriv commandResponseDto.getSolution(): " + commandResponseDto.getSolution());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto(): " + commandResponseDto.getResponseDto());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto().getClass(): " + commandResponseDto.getResponseDto().getClass());
        log.info("snmpAuthpriv commandResponseDto.getStatusCode(): " + commandResponseDto.getStatusCode());
        log.info("snmpAuthpriv commandResponseDto.getErrorCode(): " + commandResponseDto.getErrorCode());

        final ResponseDto responseDto = commandResponseDto.getResponseDto();

        log.info("snmpAuthpriv NoderesponseDto size of elements list: " + responseDto.getElements().size());

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info(row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Command OK", rowsAsListOfStrings));

        miniLinkMW2NodesDataSetup.deleteAllNodes();

    }

    @Override
    public void miniLinkCn510R1SnmpAuthPriv() throws Exception {

        miniLinkCn510R1NodesDataSetup.insertData();

        createMiniLinkCredentials(NodeSecurityDataConstants.NODE_NAME_ML_CN510R1);

        final Command command = new Command("secadm",
                SNMP_AUTHPRIV_COMMAND + AUTH_ALGO + AUTH_PASSWD + PRIV_ALGO + PRIV_PASSWD + " -n " + NodeSecurityDataConstants.NODE_NAME_ML_CN510R1);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        log.info("snmpAuthpriv commandResponseDto.getStatusMessage(): " + commandResponseDto.getStatusMessage());
        log.info("snmpAuthpriv commandResponseDto.getCommand(): " + commandResponseDto.getCommand());
        log.info("snmpAuthpriv commandResponseDto.getSolution(): " + commandResponseDto.getSolution());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto(): " + commandResponseDto.getResponseDto());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto().getClass(): " + commandResponseDto.getResponseDto().getClass());
        log.info("snmpAuthpriv commandResponseDto.getStatusCode(): " + commandResponseDto.getStatusCode());
        log.info("snmpAuthpriv commandResponseDto.getErrorCode(): " + commandResponseDto.getErrorCode());

        final ResponseDto responseDto = commandResponseDto.getResponseDto();

        log.info("snmpAuthpriv NoderesponseDto size of elements list: " + responseDto.getElements().size());

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info(row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Command OK", rowsAsListOfStrings));

        miniLinkCn510R1NodesDataSetup.deleteAllNodes();

    }

    @Override
    public void miniLinkCn510R1SnmpAuthNoPriv() throws Exception {

        miniLinkCn510R1NodesDataSetup.insertData();

        createMiniLinkCredentials(NodeSecurityDataConstants.NODE_NAME_ML_CN510R1);

        final Command command = new Command("secadm",
                SNMP_AUTHNOPRIV_COMMAND + AUTH_ALGO + AUTH_PASSWD + " -n " + NodeSecurityDataConstants.NODE_NAME_ML_CN510R1);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        log.info("snmpAuthpriv commandResponseDto.getStatusMessage(): " + commandResponseDto.getStatusMessage());
        log.info("snmpAuthpriv commandResponseDto.getCommand(): " + commandResponseDto.getCommand());
        log.info("snmpAuthpriv commandResponseDto.getSolution(): " + commandResponseDto.getSolution());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto(): " + commandResponseDto.getResponseDto());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto().getClass(): " + commandResponseDto.getResponseDto().getClass());
        log.info("snmpAuthpriv commandResponseDto.getStatusCode(): " + commandResponseDto.getStatusCode());
        log.info("snmpAuthpriv commandResponseDto.getErrorCode(): " + commandResponseDto.getErrorCode());

        final ResponseDto responseDto = commandResponseDto.getResponseDto();

        log.info("snmpAuthpriv NoderesponseDto size of elements list: " + responseDto.getElements().size());

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info(row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Command OK", rowsAsListOfStrings));

        miniLinkCn510R1NodesDataSetup.deleteAllNodes();

    }

    @Override
    public void miniLinkCn510R2SnmpAuthPriv() throws Exception {

        miniLinkCn510R2NodesDataSetup.insertData();

        createMiniLinkCredentials(NodeSecurityDataConstants.NODE_NAME_ML_CN510R2);

        final Command command = new Command("secadm",
                SNMP_AUTHPRIV_COMMAND + AUTH_ALGO + AUTH_PASSWD + PRIV_ALGO + PRIV_PASSWD + " -n " + NodeSecurityDataConstants.NODE_NAME_ML_CN510R2);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        log.info("snmpAuthpriv commandResponseDto.getStatusMessage(): " + commandResponseDto.getStatusMessage());
        log.info("snmpAuthpriv commandResponseDto.getCommand(): " + commandResponseDto.getCommand());
        log.info("snmpAuthpriv commandResponseDto.getSolution(): " + commandResponseDto.getSolution());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto(): " + commandResponseDto.getResponseDto());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto().getClass(): " + commandResponseDto.getResponseDto().getClass());
        log.info("snmpAuthpriv commandResponseDto.getStatusCode(): " + commandResponseDto.getStatusCode());
        log.info("snmpAuthpriv commandResponseDto.getErrorCode(): " + commandResponseDto.getErrorCode());

        final ResponseDto responseDto = commandResponseDto.getResponseDto();

        log.info("snmpAuthpriv NoderesponseDto size of elements list: " + responseDto.getElements().size());

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info(row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Command OK", rowsAsListOfStrings));

        miniLinkCn510R2NodesDataSetup.deleteAllNodes();

    }

    @Override
    public void miniLinkCn510R2SnmpAuthNoPriv() throws Exception {

        miniLinkCn510R2NodesDataSetup.insertData();

        createMiniLinkCredentials(NodeSecurityDataConstants.NODE_NAME_ML_CN510R2);

        final Command command = new Command("secadm",
                SNMP_AUTHNOPRIV_COMMAND + AUTH_ALGO + AUTH_PASSWD + " -n " + NodeSecurityDataConstants.NODE_NAME_ML_CN510R2);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        log.info("snmpAuthpriv commandResponseDto.getStatusMessage(): " + commandResponseDto.getStatusMessage());
        log.info("snmpAuthpriv commandResponseDto.getCommand(): " + commandResponseDto.getCommand());
        log.info("snmpAuthpriv commandResponseDto.getSolution(): " + commandResponseDto.getSolution());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto(): " + commandResponseDto.getResponseDto());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto().getClass(): " + commandResponseDto.getResponseDto().getClass());
        log.info("snmpAuthpriv commandResponseDto.getStatusCode(): " + commandResponseDto.getStatusCode());
        log.info("snmpAuthpriv commandResponseDto.getErrorCode(): " + commandResponseDto.getErrorCode());

        final ResponseDto responseDto = commandResponseDto.getResponseDto();

        log.info("snmpAuthpriv NoderesponseDto size of elements list: " + responseDto.getElements().size());

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info(row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Command OK", rowsAsListOfStrings));

        miniLinkCn510R2NodesDataSetup.deleteAllNodes();

    }

    @Override
    public void miniLinkCn810R1SnmpAuthPriv() throws Exception {

        miniLinkCn810R1NodesDataSetup.insertData();

        createMiniLinkCredentials(NodeSecurityDataConstants.NODE_NAME_ML_CN810R1);

        final Command command = new Command("secadm",
                SNMP_AUTHPRIV_COMMAND + AUTH_ALGO + AUTH_PASSWD + PRIV_ALGO + PRIV_PASSWD + " -n " + NodeSecurityDataConstants.NODE_NAME_ML_CN810R1);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        log.info("snmpAuthpriv commandResponseDto.getStatusMessage(): " + commandResponseDto.getStatusMessage());
        log.info("snmpAuthpriv commandResponseDto.getCommand(): " + commandResponseDto.getCommand());
        log.info("snmpAuthpriv commandResponseDto.getSolution(): " + commandResponseDto.getSolution());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto(): " + commandResponseDto.getResponseDto());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto().getClass(): " + commandResponseDto.getResponseDto().getClass());
        log.info("snmpAuthpriv commandResponseDto.getStatusCode(): " + commandResponseDto.getStatusCode());
        log.info("snmpAuthpriv commandResponseDto.getErrorCode(): " + commandResponseDto.getErrorCode());

        final ResponseDto responseDto = commandResponseDto.getResponseDto();

        log.info("snmpAuthpriv NoderesponseDto size of elements list: " + responseDto.getElements().size());

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info(row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Command OK", rowsAsListOfStrings));

        miniLinkCn810R1NodesDataSetup.deleteAllNodes();

    }

    @Override
    public void miniLinkCn810R1SnmpAuthNoPriv() throws Exception {

        miniLinkCn810R1NodesDataSetup.insertData();

        createMiniLinkCredentials(NodeSecurityDataConstants.NODE_NAME_ML_CN810R1);

        final Command command = new Command("secadm",
                SNMP_AUTHNOPRIV_COMMAND + AUTH_ALGO + AUTH_PASSWD + " -n " + NodeSecurityDataConstants.NODE_NAME_ML_CN810R1);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        log.info("snmpAuthpriv commandResponseDto.getStatusMessage(): " + commandResponseDto.getStatusMessage());
        log.info("snmpAuthpriv commandResponseDto.getCommand(): " + commandResponseDto.getCommand());
        log.info("snmpAuthpriv commandResponseDto.getSolution(): " + commandResponseDto.getSolution());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto(): " + commandResponseDto.getResponseDto());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto().getClass(): " + commandResponseDto.getResponseDto().getClass());
        log.info("snmpAuthpriv commandResponseDto.getStatusCode(): " + commandResponseDto.getStatusCode());
        log.info("snmpAuthpriv commandResponseDto.getErrorCode(): " + commandResponseDto.getErrorCode());

        final ResponseDto responseDto = commandResponseDto.getResponseDto();

        log.info("snmpAuthpriv NoderesponseDto size of elements list: " + responseDto.getElements().size());

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info(row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Command OK", rowsAsListOfStrings));

        miniLinkCn810R1NodesDataSetup.deleteAllNodes();

    }

    @Override
    public void miniLinkCn810R2SnmpAuthPriv() throws Exception {

        miniLinkCn810R2NodesDataSetup.insertData();

        createMiniLinkCredentials(NodeSecurityDataConstants.NODE_NAME_ML_CN810R2);

        final Command command = new Command("secadm",
                SNMP_AUTHPRIV_COMMAND + AUTH_ALGO + AUTH_PASSWD + PRIV_ALGO + PRIV_PASSWD + " -n " + NodeSecurityDataConstants.NODE_NAME_ML_CN810R2);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        log.info("snmpAuthpriv commandResponseDto.getStatusMessage(): " + commandResponseDto.getStatusMessage());
        log.info("snmpAuthpriv commandResponseDto.getCommand(): " + commandResponseDto.getCommand());
        log.info("snmpAuthpriv commandResponseDto.getSolution(): " + commandResponseDto.getSolution());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto(): " + commandResponseDto.getResponseDto());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto().getClass(): " + commandResponseDto.getResponseDto().getClass());
        log.info("snmpAuthpriv commandResponseDto.getStatusCode(): " + commandResponseDto.getStatusCode());
        log.info("snmpAuthpriv commandResponseDto.getErrorCode(): " + commandResponseDto.getErrorCode());

        final ResponseDto responseDto = commandResponseDto.getResponseDto();

        log.info("snmpAuthpriv NoderesponseDto size of elements list: " + responseDto.getElements().size());

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info(row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Command OK", rowsAsListOfStrings));

        miniLinkCn810R2NodesDataSetup.deleteAllNodes();

    }

    @Override
    public void miniLinkCn810R2SnmpAuthNoPriv() throws Exception {

        miniLinkCn810R2NodesDataSetup.insertData();

        createMiniLinkCredentials(NodeSecurityDataConstants.NODE_NAME_ML_CN810R2);

        final Command command = new Command("secadm",
                SNMP_AUTHNOPRIV_COMMAND + AUTH_ALGO + AUTH_PASSWD + " -n " + NodeSecurityDataConstants.NODE_NAME_ML_CN810R2);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        log.info("snmpAuthpriv commandResponseDto.getStatusMessage(): " + commandResponseDto.getStatusMessage());
        log.info("snmpAuthpriv commandResponseDto.getCommand(): " + commandResponseDto.getCommand());
        log.info("snmpAuthpriv commandResponseDto.getSolution(): " + commandResponseDto.getSolution());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto(): " + commandResponseDto.getResponseDto());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto().getClass(): " + commandResponseDto.getResponseDto().getClass());
        log.info("snmpAuthpriv commandResponseDto.getStatusCode(): " + commandResponseDto.getStatusCode());
        log.info("snmpAuthpriv commandResponseDto.getErrorCode(): " + commandResponseDto.getErrorCode());

        final ResponseDto responseDto = commandResponseDto.getResponseDto();

        log.info("snmpAuthpriv NoderesponseDto size of elements list: " + responseDto.getElements().size());

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info(row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Command OK", rowsAsListOfStrings));

        miniLinkCn810R2NodesDataSetup.deleteAllNodes();

    }

    @Override
    public void miniLinkIndoorSnmpAuthPriv() throws Exception {

        minilinkIndoorNodesDataSetup.insertData();

        createMiniLinkCredentials(NodeSecurityDataConstants.NODE_NAME_ML_INDOOR);

        final Command command = new Command("secadm",
                SNMP_AUTHPRIV_COMMAND + AUTH_ALGO + AUTH_PASSWD + PRIV_ALGO + PRIV_PASSWD + " -n " + NodeSecurityDataConstants.NODE_NAME_ML_INDOOR);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        log.info("snmpAuthpriv commandResponseDto.getStatusMessage(): " + commandResponseDto.getStatusMessage());
        log.info("snmpAuthpriv commandResponseDto.getCommand(): " + commandResponseDto.getCommand());
        log.info("snmpAuthpriv commandResponseDto.getSolution(): " + commandResponseDto.getSolution());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto(): " + commandResponseDto.getResponseDto());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto().getClass(): " + commandResponseDto.getResponseDto().getClass());
        log.info("snmpAuthpriv commandResponseDto.getStatusCode(): " + commandResponseDto.getStatusCode());
        log.info("snmpAuthpriv commandResponseDto.getErrorCode(): " + commandResponseDto.getErrorCode());

        final ResponseDto responseDto = commandResponseDto.getResponseDto();

        log.info("snmpAuthpriv NoderesponseDto size of elements list: " + responseDto.getElements().size());

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info(row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Command OK", rowsAsListOfStrings));

        minilinkIndoorNodesDataSetup.deleteAllNodes();

    }

    @Override
    public void miniLinkIndoorSnmpAuthNoPriv() throws Exception {

        minilinkIndoorNodesDataSetup.insertData();

        createMiniLinkCredentials(NodeSecurityDataConstants.NODE_NAME_ML_INDOOR);

        final Command command = new Command("secadm",
                SNMP_AUTHNOPRIV_COMMAND + AUTH_ALGO + AUTH_PASSWD + " -n " + NodeSecurityDataConstants.NODE_NAME_ML_INDOOR);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        log.info("snmpAuthpriv commandResponseDto.getStatusMessage(): " + commandResponseDto.getStatusMessage());
        log.info("snmpAuthpriv commandResponseDto.getCommand(): " + commandResponseDto.getCommand());
        log.info("snmpAuthpriv commandResponseDto.getSolution(): " + commandResponseDto.getSolution());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto(): " + commandResponseDto.getResponseDto());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto().getClass(): " + commandResponseDto.getResponseDto().getClass());
        log.info("snmpAuthpriv commandResponseDto.getStatusCode(): " + commandResponseDto.getStatusCode());
        log.info("snmpAuthpriv commandResponseDto.getErrorCode(): " + commandResponseDto.getErrorCode());

        final ResponseDto responseDto = commandResponseDto.getResponseDto();

        log.info("snmpAuthpriv NoderesponseDto size of elements list: " + responseDto.getElements().size());

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info(row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Command OK", rowsAsListOfStrings));

        minilinkIndoorNodesDataSetup.deleteAllNodes();

    }

    @Override
    public void miniLink6352SnmpAuthPriv() throws Exception {

        minilink6352NodesDataSetup.insertData();

        createMiniLinkCredentials(NodeSecurityDataConstants.NODE_NAME_ML_6352);

        final Command command = new Command("secadm",
                SNMP_AUTHPRIV_COMMAND + AUTH_ALGO + AUTH_PASSWD + PRIV_ALGO + PRIV_PASSWD + " -n " + NodeSecurityDataConstants.NODE_NAME_ML_6352);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        log.info("snmpAuthpriv commandResponseDto.getStatusMessage(): " + commandResponseDto.getStatusMessage());
        log.info("snmpAuthpriv commandResponseDto.getCommand(): " + commandResponseDto.getCommand());
        log.info("snmpAuthpriv commandResponseDto.getSolution(): " + commandResponseDto.getSolution());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto(): " + commandResponseDto.getResponseDto());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto().getClass(): " + commandResponseDto.getResponseDto().getClass());
        log.info("snmpAuthpriv commandResponseDto.getStatusCode(): " + commandResponseDto.getStatusCode());
        log.info("snmpAuthpriv commandResponseDto.getErrorCode(): " + commandResponseDto.getErrorCode());

        final ResponseDto responseDto = commandResponseDto.getResponseDto();

        log.info("snmpAuthpriv NoderesponseDto size of elements list: " + responseDto.getElements().size());

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info(row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Command OK", rowsAsListOfStrings));

        minilink6352NodesDataSetup.deleteAllNodes();

    }

    @Override
    public void miniLink6352SnmpAuthNoPriv() throws Exception {

        minilink6352NodesDataSetup.insertData();

        createMiniLinkCredentials(NodeSecurityDataConstants.NODE_NAME_ML_6352);

        final Command command = new Command("secadm",
                SNMP_AUTHNOPRIV_COMMAND + AUTH_ALGO + AUTH_PASSWD + " -n " + NodeSecurityDataConstants.NODE_NAME_ML_6352);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        log.info("snmpAuthpriv commandResponseDto.getStatusMessage(): " + commandResponseDto.getStatusMessage());
        log.info("snmpAuthpriv commandResponseDto.getCommand(): " + commandResponseDto.getCommand());
        log.info("snmpAuthpriv commandResponseDto.getSolution(): " + commandResponseDto.getSolution());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto(): " + commandResponseDto.getResponseDto());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto().getClass(): " + commandResponseDto.getResponseDto().getClass());
        log.info("snmpAuthpriv commandResponseDto.getStatusCode(): " + commandResponseDto.getStatusCode());
        log.info("snmpAuthpriv commandResponseDto.getErrorCode(): " + commandResponseDto.getErrorCode());

        final ResponseDto responseDto = commandResponseDto.getResponseDto();

        log.info("snmpAuthpriv NoderesponseDto size of elements list: " + responseDto.getElements().size());

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info(row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Command OK", rowsAsListOfStrings));

        minilink6352NodesDataSetup.deleteAllNodes();

    }

    @Override
    public void miniLink6351SnmpAuthPriv() throws Exception {

        minilink6351NodesDataSetup.insertData();

        createMiniLinkCredentials(NodeSecurityDataConstants.NODE_NAME_ML_6351);

        final Command command = new Command("secadm",
                SNMP_AUTHPRIV_COMMAND + AUTH_ALGO + AUTH_PASSWD + PRIV_ALGO + PRIV_PASSWD + " -n " + NodeSecurityDataConstants.NODE_NAME_ML_6351);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        log.info("snmpAuthpriv commandResponseDto.getStatusMessage(): " + commandResponseDto.getStatusMessage());
        log.info("snmpAuthpriv commandResponseDto.getCommand(): " + commandResponseDto.getCommand());
        log.info("snmpAuthpriv commandResponseDto.getSolution(): " + commandResponseDto.getSolution());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto(): " + commandResponseDto.getResponseDto());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto().getClass(): " + commandResponseDto.getResponseDto().getClass());
        log.info("snmpAuthpriv commandResponseDto.getStatusCode(): " + commandResponseDto.getStatusCode());
        log.info("snmpAuthpriv commandResponseDto.getErrorCode(): " + commandResponseDto.getErrorCode());

        final ResponseDto responseDto = commandResponseDto.getResponseDto();

        log.info("snmpAuthpriv NoderesponseDto size of elements list: " + responseDto.getElements().size());

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info(row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Command OK", rowsAsListOfStrings));

        minilink6351NodesDataSetup.deleteAllNodes();

    }

    @Override
    public void miniLink6351SnmpAuthNoPriv() throws Exception {

        minilink6351NodesDataSetup.insertData();

        createMiniLinkCredentials(NodeSecurityDataConstants.NODE_NAME_ML_6351);

        final Command command = new Command("secadm",
                SNMP_AUTHNOPRIV_COMMAND + AUTH_ALGO + AUTH_PASSWD + " -n " + NodeSecurityDataConstants.NODE_NAME_ML_6351);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        log.info("snmpAuthpriv commandResponseDto.getStatusMessage(): " + commandResponseDto.getStatusMessage());
        log.info("snmpAuthpriv commandResponseDto.getCommand(): " + commandResponseDto.getCommand());
        log.info("snmpAuthpriv commandResponseDto.getSolution(): " + commandResponseDto.getSolution());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto(): " + commandResponseDto.getResponseDto());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto().getClass(): " + commandResponseDto.getResponseDto().getClass());
        log.info("snmpAuthpriv commandResponseDto.getStatusCode(): " + commandResponseDto.getStatusCode());
        log.info("snmpAuthpriv commandResponseDto.getErrorCode(): " + commandResponseDto.getErrorCode());

        final ResponseDto responseDto = commandResponseDto.getResponseDto();

        log.info("snmpAuthpriv NoderesponseDto size of elements list: " + responseDto.getElements().size());

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info(row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Command OK", rowsAsListOfStrings));

        minilink6351NodesDataSetup.deleteAllNodes();

    }

    @Override
    public void miniLink6366SnmpAuthPriv() throws Exception {
        minilink6366NodesDataSetup.insertData();

        createMiniLinkCredentials(NodeSecurityDataConstants.NODE_NAME_ML_6366);

        final Command command = new Command("secadm", SNMP_AUTHPRIV_COMMAND + AUTH_ALGO + AUTH_PASSWD + PRIV_ALGO + PRIV_PASSWD + " -n "
                + NodeSecurityDataConstants.NODE_NAME_ML_6366);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        log.info("snmpAuthpriv commandResponseDto.getStatusMessage(): " + commandResponseDto.getStatusMessage());
        log.info("snmpAuthpriv commandResponseDto.getCommand(): " + commandResponseDto.getCommand());
        log.info("snmpAuthpriv commandResponseDto.getSolution(): " + commandResponseDto.getSolution());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto(): " + commandResponseDto.getResponseDto());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto().getClass(): " + commandResponseDto.getResponseDto().getClass());
        log.info("snmpAuthpriv commandResponseDto.getStatusCode(): " + commandResponseDto.getStatusCode());
        log.info("snmpAuthpriv commandResponseDto.getErrorCode(): " + commandResponseDto.getErrorCode());

        final ResponseDto responseDto = commandResponseDto.getResponseDto();

        log.info("snmpAuthpriv NoderesponseDto size of elements list: " + responseDto.getElements().size());

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info(row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Command OK", rowsAsListOfStrings));

        minilink6366NodesDataSetup.deleteAllNodes();

    }
    @Override
    public void miniLink6366SnmpAuthNoPriv() throws Exception {

        minilink6366NodesDataSetup.insertData();

        createMiniLinkCredentials(NodeSecurityDataConstants.NODE_NAME_ML_6366);

        final Command command = new Command("secadm", SNMP_AUTHNOPRIV_COMMAND + AUTH_ALGO + AUTH_PASSWD + " -n " + NodeSecurityDataConstants.NODE_NAME_ML_6366);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        log.info("snmpAuthpriv commandResponseDto.getStatusMessage(): " + commandResponseDto.getStatusMessage());
        log.info("snmpAuthpriv commandResponseDto.getCommand(): " + commandResponseDto.getCommand());
        log.info("snmpAuthpriv commandResponseDto.getSolution(): " + commandResponseDto.getSolution());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto(): " + commandResponseDto.getResponseDto());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto().getClass(): " + commandResponseDto.getResponseDto().getClass());
        log.info("snmpAuthpriv commandResponseDto.getStatusCode(): " + commandResponseDto.getStatusCode());
        log.info("snmpAuthpriv commandResponseDto.getErrorCode(): " + commandResponseDto.getErrorCode());

        final ResponseDto responseDto = commandResponseDto.getResponseDto();

        log.info("snmpAuthpriv NoderesponseDto size of elements list: " + responseDto.getElements().size());

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info(row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Command OK", rowsAsListOfStrings));

        minilink6366NodesDataSetup.deleteAllNodes();
    }


    @Override
    public void miniLinkPT2020SnmpAuthPriv() throws Exception {

        minilinkPT2020NodesDataSetup.insertData();

        createMiniLinkCredentials(NodeSecurityDataConstants.NODE_NAME_ML_PT2020);

        final Command command = new Command("secadm",
                SNMP_AUTHPRIV_COMMAND + AUTH_ALGO + AUTH_PASSWD + PRIV_ALGO + PRIV_PASSWD + " -n " + NodeSecurityDataConstants.NODE_NAME_ML_PT2020);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        log.info("snmpAuthpriv commandResponseDto.getStatusMessage(): " + commandResponseDto.getStatusMessage());
        log.info("snmpAuthpriv commandResponseDto.getCommand(): " + commandResponseDto.getCommand());
        log.info("snmpAuthpriv commandResponseDto.getSolution(): " + commandResponseDto.getSolution());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto(): " + commandResponseDto.getResponseDto());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto().getClass(): " + commandResponseDto.getResponseDto().getClass());
        log.info("snmpAuthpriv commandResponseDto.getStatusCode(): " + commandResponseDto.getStatusCode());
        log.info("snmpAuthpriv commandResponseDto.getErrorCode(): " + commandResponseDto.getErrorCode());

        final ResponseDto responseDto = commandResponseDto.getResponseDto();

        log.info("snmpAuthpriv NoderesponseDto size of elements list: " + responseDto.getElements().size());

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info(row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Command OK", rowsAsListOfStrings));

        minilinkPT2020NodesDataSetup.deleteAllNodes();

    }

    @Override
    public void miniLinkPT2020SnmpAuthNoPriv() throws Exception {

        minilinkPT2020NodesDataSetup.insertData();

        createMiniLinkCredentials(NodeSecurityDataConstants.NODE_NAME_ML_PT2020);

        final Command command = new Command("secadm",
                SNMP_AUTHNOPRIV_COMMAND + AUTH_ALGO + AUTH_PASSWD + " -n " + NodeSecurityDataConstants.NODE_NAME_ML_PT2020);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        log.info("snmpAuthpriv commandResponseDto.getStatusMessage(): " + commandResponseDto.getStatusMessage());
        log.info("snmpAuthpriv commandResponseDto.getCommand(): " + commandResponseDto.getCommand());
        log.info("snmpAuthpriv commandResponseDto.getSolution(): " + commandResponseDto.getSolution());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto(): " + commandResponseDto.getResponseDto());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto().getClass(): " + commandResponseDto.getResponseDto().getClass());
        log.info("snmpAuthpriv commandResponseDto.getStatusCode(): " + commandResponseDto.getStatusCode());
        log.info("snmpAuthpriv commandResponseDto.getErrorCode(): " + commandResponseDto.getErrorCode());

        final ResponseDto responseDto = commandResponseDto.getResponseDto();

        log.info("snmpAuthpriv NoderesponseDto size of elements list: " + responseDto.getElements().size());

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info(row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Command OK", rowsAsListOfStrings));

        minilinkPT2020NodesDataSetup.deleteAllNodes();

    }

    @Override
    public void switch6391SnmpAuthPriv() throws Exception {

        switch6391NodeDataSetup.insertData();

        createMiniLinkCredentials(NodeSecurityDataConstants.NODE_SWITCH_6391);

        final Command command = new Command("secadm",
                SNMP_AUTHPRIV_COMMAND + AUTH_ALGO + AUTH_PASSWD + PRIV_ALGO + PRIV_PASSWD + " -n " + NodeSecurityDataConstants.NODE_SWITCH_6391);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        log.info("snmpAuthpriv commandResponseDto.getStatusMessage(): " + commandResponseDto.getStatusMessage());
        log.info("snmpAuthpriv commandResponseDto.getCommand(): " + commandResponseDto.getCommand());
        log.info("snmpAuthpriv commandResponseDto.getSolution(): " + commandResponseDto.getSolution());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto(): " + commandResponseDto.getResponseDto());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto().getClass(): " + commandResponseDto.getResponseDto().getClass());
        log.info("snmpAuthpriv commandResponseDto.getStatusCode(): " + commandResponseDto.getStatusCode());
        log.info("snmpAuthpriv commandResponseDto.getErrorCode(): " + commandResponseDto.getErrorCode());

        final ResponseDto responseDto = commandResponseDto.getResponseDto();

        log.info("snmpAuthpriv NoderesponseDto size of elements list: " + responseDto.getElements().size());

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info(row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Command OK", rowsAsListOfStrings));

        switch6391NodeDataSetup.deleteAllNodes();

    }

    @Override
    public void switch6391SnmpAuthNoPriv() throws Exception {

        switch6391NodeDataSetup.insertData();

        createMiniLinkCredentials(NodeSecurityDataConstants.NODE_SWITCH_6391);

        final Command command = new Command("secadm",
                SNMP_AUTHNOPRIV_COMMAND + AUTH_ALGO + AUTH_PASSWD + " -n " + NodeSecurityDataConstants.NODE_SWITCH_6391);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        log.info("snmpAuthpriv commandResponseDto.getStatusMessage(): " + commandResponseDto.getStatusMessage());
        log.info("snmpAuthpriv commandResponseDto.getCommand(): " + commandResponseDto.getCommand());
        log.info("snmpAuthpriv commandResponseDto.getSolution(): " + commandResponseDto.getSolution());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto(): " + commandResponseDto.getResponseDto());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto().getClass(): " + commandResponseDto.getResponseDto().getClass());
        log.info("snmpAuthpriv commandResponseDto.getStatusCode(): " + commandResponseDto.getStatusCode());
        log.info("snmpAuthpriv commandResponseDto.getErrorCode(): " + commandResponseDto.getErrorCode());

        final ResponseDto responseDto = commandResponseDto.getResponseDto();

        log.info("snmpAuthpriv NoderesponseDto size of elements list: " + responseDto.getElements().size());

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info(row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Command OK", rowsAsListOfStrings));

        switch6391NodeDataSetup.deleteAllNodes();

    }

    @Override
    public void fronthaul6392SnmpAuthPriv() throws Exception {

        fronthaul6392NodeDataSetup.insertData();

        createMiniLinkCredentials(NodeSecurityDataConstants.NODE_FRONTHAUL_6392);

        final Command command = new Command("secadm",
                SNMP_AUTHPRIV_COMMAND + AUTH_ALGO + AUTH_PASSWD + PRIV_ALGO + PRIV_PASSWD + " -n " + NodeSecurityDataConstants.NODE_FRONTHAUL_6392);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        log.info("snmpAuthpriv commandResponseDto.getStatusMessage(): " + commandResponseDto.getStatusMessage());
        log.info("snmpAuthpriv commandResponseDto.getCommand(): " + commandResponseDto.getCommand());
        log.info("snmpAuthpriv commandResponseDto.getSolution(): " + commandResponseDto.getSolution());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto(): " + commandResponseDto.getResponseDto());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto().getClass(): " + commandResponseDto.getResponseDto().getClass());
        log.info("snmpAuthpriv commandResponseDto.getStatusCode(): " + commandResponseDto.getStatusCode());
        log.info("snmpAuthpriv commandResponseDto.getErrorCode(): " + commandResponseDto.getErrorCode());

        final ResponseDto responseDto = commandResponseDto.getResponseDto();

        log.info("snmpAuthpriv NoderesponseDto size of elements list: " + responseDto.getElements().size());

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info(row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Command OK", rowsAsListOfStrings));

        fronthaul6392NodeDataSetup.deleteAllNodes();

    }

    @Override
    public void fronthaul6392SnmpAuthNoPriv() throws Exception {

        fronthaul6392NodeDataSetup.insertData();

        createMiniLinkCredentials(NodeSecurityDataConstants.NODE_FRONTHAUL_6392);

        final Command command = new Command("secadm",
                SNMP_AUTHNOPRIV_COMMAND + AUTH_ALGO + AUTH_PASSWD + " -n " + NodeSecurityDataConstants.NODE_FRONTHAUL_6392);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        log.info("snmpAuthpriv commandResponseDto.getStatusMessage(): " + commandResponseDto.getStatusMessage());
        log.info("snmpAuthpriv commandResponseDto.getCommand(): " + commandResponseDto.getCommand());
        log.info("snmpAuthpriv commandResponseDto.getSolution(): " + commandResponseDto.getSolution());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto(): " + commandResponseDto.getResponseDto());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto().getClass(): " + commandResponseDto.getResponseDto().getClass());
        log.info("snmpAuthpriv commandResponseDto.getStatusCode(): " + commandResponseDto.getStatusCode());
        log.info("snmpAuthpriv commandResponseDto.getErrorCode(): " + commandResponseDto.getErrorCode());

        final ResponseDto responseDto = commandResponseDto.getResponseDto();

        log.info("snmpAuthpriv NoderesponseDto size of elements list: " + responseDto.getElements().size());

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info(row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Command OK", rowsAsListOfStrings));

        fronthaul6392NodeDataSetup.deleteAllNodes();

    }

    @Override
    public void ciscoAsr9000SnmpAuthPriv() throws Exception {

        ciscoAsr9000NodesDataSetup.insertData();

        createCiscoCredentials(NodeSecurityDataConstants.NODE_NAME_CISCO_ASR9000);

        final Command command = new Command("secadm", SNMP_AUTHPRIV_COMMAND + AUTH_ALGO + AUTH_PASSWD + PRIV_ALGO + PRIV_PASSWD + " -n "
                + NodeSecurityDataConstants.NODE_NAME_CISCO_ASR9000);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        log.info("snmpAuthpriv commandResponseDto.getStatusMessage(): " + commandResponseDto.getStatusMessage());
        log.info("snmpAuthpriv commandResponseDto.getCommand(): " + commandResponseDto.getCommand());
        log.info("snmpAuthpriv commandResponseDto.getSolution(): " + commandResponseDto.getSolution());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto(): " + commandResponseDto.getResponseDto());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto().getClass(): " + commandResponseDto.getResponseDto().getClass());
        log.info("snmpAuthpriv commandResponseDto.getStatusCode(): " + commandResponseDto.getStatusCode());
        log.info("snmpAuthpriv commandResponseDto.getErrorCode(): " + commandResponseDto.getErrorCode());

        final ResponseDto responseDto = commandResponseDto.getResponseDto();

        log.info("snmpAuthpriv NoderesponseDto size of elements list: " + responseDto.getElements().size());

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info(row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Command OK", rowsAsListOfStrings));

        ciscoAsr9000NodesDataSetup.deleteAllNodes();

    }

    @Override
    public void ciscoAsr9000SnmpAuthNoPriv() throws Exception {

        ciscoAsr9000NodesDataSetup.insertData();

        createCiscoCredentials(NodeSecurityDataConstants.NODE_NAME_CISCO_ASR9000);

        final Command command = new Command("secadm",
                SNMP_AUTHNOPRIV_COMMAND + AUTH_ALGO + AUTH_PASSWD + " -n " + NodeSecurityDataConstants.NODE_NAME_CISCO_ASR9000);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        log.info("snmpAuthpriv commandResponseDto.getStatusMessage(): " + commandResponseDto.getStatusMessage());
        log.info("snmpAuthpriv commandResponseDto.getCommand(): " + commandResponseDto.getCommand());
        log.info("snmpAuthpriv commandResponseDto.getSolution(): " + commandResponseDto.getSolution());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto(): " + commandResponseDto.getResponseDto());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto().getClass(): " + commandResponseDto.getResponseDto().getClass());
        log.info("snmpAuthpriv commandResponseDto.getStatusCode(): " + commandResponseDto.getStatusCode());
        log.info("snmpAuthpriv commandResponseDto.getErrorCode(): " + commandResponseDto.getErrorCode());

        final ResponseDto responseDto = commandResponseDto.getResponseDto();

        log.info("snmpAuthpriv NoderesponseDto size of elements list: " + responseDto.getElements().size());

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info(row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Command OK", rowsAsListOfStrings));

        ciscoAsr9000NodesDataSetup.deleteAllNodes();

    }

    @Override
    public void ciscoAsr900SnmpAuthPriv() throws Exception {

        ciscoAsr900NodesDataSetup.insertData();

        createCiscoCredentials(NodeSecurityDataConstants.NODE_NAME_CISCO_ASR900);

        final Command command = new Command("secadm", SNMP_AUTHPRIV_COMMAND + AUTH_ALGO + AUTH_PASSWD + PRIV_ALGO + PRIV_PASSWD + " -n "
                + NodeSecurityDataConstants.NODE_NAME_CISCO_ASR900);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        log.info("snmpAuthpriv commandResponseDto.getStatusMessage(): " + commandResponseDto.getStatusMessage());
        log.info("snmpAuthpriv commandResponseDto.getCommand(): " + commandResponseDto.getCommand());
        log.info("snmpAuthpriv commandResponseDto.getSolution(): " + commandResponseDto.getSolution());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto(): " + commandResponseDto.getResponseDto());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto().getClass(): " + commandResponseDto.getResponseDto().getClass());
        log.info("snmpAuthpriv commandResponseDto.getStatusCode(): " + commandResponseDto.getStatusCode());
        log.info("snmpAuthpriv commandResponseDto.getErrorCode(): " + commandResponseDto.getErrorCode());

        final ResponseDto responseDto = commandResponseDto.getResponseDto();

        log.info("snmpAuthpriv NoderesponseDto size of elements list: " + responseDto.getElements().size());

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info(row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Command OK", rowsAsListOfStrings));

        ciscoAsr900NodesDataSetup.deleteAllNodes();

    }

    @Override
    public void ciscoAsr900SnmpAuthNoPriv() throws Exception {

        ciscoAsr900NodesDataSetup.insertData();

        createCiscoCredentials(NodeSecurityDataConstants.NODE_NAME_CISCO_ASR900);

        final Command command = new Command("secadm",
                SNMP_AUTHNOPRIV_COMMAND + AUTH_ALGO + AUTH_PASSWD + " -n " + NodeSecurityDataConstants.NODE_NAME_CISCO_ASR900);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        log.info("snmpAuthpriv commandResponseDto.getStatusMessage(): " + commandResponseDto.getStatusMessage());
        log.info("snmpAuthpriv commandResponseDto.getCommand(): " + commandResponseDto.getCommand());
        log.info("snmpAuthpriv commandResponseDto.getSolution(): " + commandResponseDto.getSolution());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto(): " + commandResponseDto.getResponseDto());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto().getClass(): " + commandResponseDto.getResponseDto().getClass());
        log.info("snmpAuthpriv commandResponseDto.getStatusCode(): " + commandResponseDto.getStatusCode());
        log.info("snmpAuthpriv commandResponseDto.getErrorCode(): " + commandResponseDto.getErrorCode());

        final ResponseDto responseDto = commandResponseDto.getResponseDto();

        log.info("snmpAuthpriv NoderesponseDto size of elements list: " + responseDto.getElements().size());

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info(row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Command OK", rowsAsListOfStrings));

        ciscoAsr900NodesDataSetup.deleteAllNodes();

    }

    @Override
    public void juniperMxSnmpAuthPriv() throws Exception {

        juniperMxNodesDataSetup.insertData();

        createJuniperCredentials(NodeSecurityDataConstants.NODE_NAME_JUNIPER_MX);

        final Command command = new Command("secadm",
                SNMP_AUTHPRIV_COMMAND + AUTH_ALGO + AUTH_PASSWD + PRIV_ALGO + PRIV_PASSWD + " -n " + NodeSecurityDataConstants.NODE_NAME_JUNIPER_MX);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        log.info("snmpAuthpriv commandResponseDto.getStatusMessage(): " + commandResponseDto.getStatusMessage());
        log.info("snmpAuthpriv commandResponseDto.getCommand(): " + commandResponseDto.getCommand());
        log.info("snmpAuthpriv commandResponseDto.getSolution(): " + commandResponseDto.getSolution());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto(): " + commandResponseDto.getResponseDto());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto().getClass(): " + commandResponseDto.getResponseDto().getClass());
        log.info("snmpAuthpriv commandResponseDto.getStatusCode(): " + commandResponseDto.getStatusCode());
        log.info("snmpAuthpriv commandResponseDto.getErrorCode(): " + commandResponseDto.getErrorCode());

        final ResponseDto responseDto = commandResponseDto.getResponseDto();

        log.info("snmpAuthpriv NoderesponseDto size of elements list: " + responseDto.getElements().size());

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info(row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Command OK", rowsAsListOfStrings));

        juniperMxNodesDataSetup.deleteAllNodes();

    }

    @Override
    public void juniperMxSnmpAuthNoPriv() throws Exception {

        juniperMxNodesDataSetup.insertData();

        createJuniperCredentials(NodeSecurityDataConstants.NODE_NAME_JUNIPER_MX);

        final Command command = new Command("secadm",
                SNMP_AUTHNOPRIV_COMMAND + AUTH_ALGO + AUTH_PASSWD + " -n " + NodeSecurityDataConstants.NODE_NAME_JUNIPER_MX);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        log.info("snmpAuthpriv commandResponseDto.getStatusMessage(): " + commandResponseDto.getStatusMessage());
        log.info("snmpAuthpriv commandResponseDto.getCommand(): " + commandResponseDto.getCommand());
        log.info("snmpAuthpriv commandResponseDto.getSolution(): " + commandResponseDto.getSolution());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto(): " + commandResponseDto.getResponseDto());
        log.info("snmpAuthpriv commandResponseDto.getCommandResultDto().getClass(): " + commandResponseDto.getResponseDto().getClass());
        log.info("snmpAuthpriv commandResponseDto.getStatusCode(): " + commandResponseDto.getStatusCode());
        log.info("snmpAuthpriv commandResponseDto.getErrorCode(): " + commandResponseDto.getErrorCode());

        final ResponseDto responseDto = commandResponseDto.getResponseDto();

        log.info("snmpAuthpriv NoderesponseDto size of elements list: " + responseDto.getElements().size());

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info(row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Command OK", rowsAsListOfStrings));

        juniperMxNodesDataSetup.deleteAllNodes();

    }

    private void createCredentials(final String node) {
        createCred(new Command("secadm", CREDS_CREATE + USERNAME_PASSWORDS + " -n " + node));
    }

    private void createMiniLinkCredentials(final String node) {
        createCred(new Command("secadm", CREDS_CREATE + ALL_USERNAME_PASSWORDS + " -n " + node));
    }

    private void createCiscoCredentials(final String node) {
        createCred(new Command("secadm", CREDS_CREATE + USERNAME_PASSWORDS + " -n " + node));
    }

    private void createJuniperCredentials(final String node) {
        createCred(new Command("secadm", CREDS_CREATE + USERNAME_PASSWORDS + " -n " + node));
    }

    private void createCred(final Command command) {
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info("create credentials :" + row);
            }
        }
        assertTrue(responseDtoReader.messageIsContainedInList("All credentials were created successfully", rowsAsListOfStrings));
    }

}
