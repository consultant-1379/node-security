/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.integration.jee.test.command;

import static com.ericsson.nms.security.nscs.api.exception.NscsServiceException.*;
import static org.junit.Assert.*;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException.ErrorType;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.ResponseDtoReader;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityDataConstants;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityDataSetup;
import com.ericsson.oss.services.scriptengine.spi.CommandHandler;
import com.ericsson.oss.services.scriptengine.spi.dtos.*;

public class CredentialsTest implements CredentialsTests {

    @Inject
    CommandHandler commandHandler;

    @Inject
    NodeSecurityDataSetup dataSetup;

    @Inject
    Logger log;

    public static final String CREDS_UPDATE = "credentials update";
    public static final String CREDS_CREATE = "credentials create";
    public static final String CREDS_GET = "credentials get";

    public static final String USERNAME_PASSWORDS = " --rootusername \"rootUserName\" --rootuserpassword \"rootUserPassword\" --secureuserpassword \"secureUserPassword\" --secureusername \"secureUserName\" --normaluserpassword \"normalUserPassword\" --normalusername \"normalUserName\" ";
    public static final String USERNAME_PASSWORDS_COM_ECIM = " --rootusername null --rootuserpassword null --secureuserpassword \"secureUserPassword\" --secureusername \"secureUserName\" --normaluserpassword null --normalusername null ";
    public static final String USERNAME_PASSWORDS_MISSING = " --rootusername \"rootUserName\" --rootuserpassword \"rootUserPassword\" --secureuserpassword \"secureUserPassword\" --secureusername \"secureUserName\" ";
    public static final String ROOTUSERNAME = " --rootusername \"rootUserName\" ";
    public static final String PLAINTEXT_HIDE = "--plaintext \"hide\"";
    public static final String PLAINTEXT_SHOW = "--plaintext \"show\"";
    public static final String USERTYPE_SECURE = "--usertype \"secure\"";
    public static final String USERTYPE_ROOT = "--usertype \"root\"";
    public static final String USERTYPE_NORMAL = "--usertype \"normal\"";
    private static final String COMMAND_EXECUTED_SUCCESSFULLY = "Command Executed Successfully";
    public static final String SECURE = "secure";
    public static final String ROOT = "root";
    public static final String NORMAL = "normal";
    public static final String PASSWORD_HIDE = "***********";
    public static final String NOT_APPLICABLE = "Not Applicable";

    ResponseDtoReader responseDtoReader = new ResponseDtoReader();

    @Override
    public void updateCredentialsOneNode() throws Exception {

        log.info("updateCredentialsOneNode");

        dataSetup.insertData();

        createCredentials(NodeSecurityDataConstants.MECONTEXT_FDN1);

        final Command command = new Command("secadm", CREDS_UPDATE + USERNAME_PASSWORDS + " -n " + NodeSecurityDataConstants.MECONTEXT_FDN1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        log.info("updateCredentialsOneNode commandResponseDto.getStatusMessage(): " + commandResponseDto.getStatusMessage());
        log.info("updateCredentialsOneNode commandResponseDto.getCommand(): " + commandResponseDto.getCommand());
        log.info("updateCredentialsOneNode commandResponseDto.getSolution(): " + commandResponseDto.getSolution());
        log.info("updateCredentialsOneNode commandResponseDto.getCommandResultDto(): " + commandResponseDto.getResponseDto());
        log.info("updateCredentialsOneNode commandResponseDto.getCommandResultDto().getClass(): " + commandResponseDto.getResponseDto().getClass());
        log.info("updateCredentialsOneNode commandResponseDto.getStatusCode(): " + commandResponseDto.getStatusCode());
        log.info("updateCredentialsOneNode commandResponseDto.getErrorCode(): " + commandResponseDto.getErrorCode());

        final ResponseDto responseDto = commandResponseDto.getResponseDto();

        log.info("updateCredentialsOne NoderesponseDto size of elements list: " + responseDto.getElements().size());

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info("updateCredentialsOneNode :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("All credentials updated successfully", rowsAsListOfStrings));

        dataSetup.deleteAllNodes();
    }

    @Override
    public void updateCredentialsOneNodeOneParam() throws Exception {

        log.info("updateCredentialsOneNodeOneParam");

        dataSetup.insertData();

        createCredentials(NodeSecurityDataConstants.MECONTEXT_FDN1);

        final Command command = new Command("secadm", CREDS_UPDATE + ROOTUSERNAME + " -n " + NodeSecurityDataConstants.MECONTEXT_FDN1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info("updateCredentialsOneNodeOneParam :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("All credentials updated successfully", rowsAsListOfStrings));

        dataSetup.deleteAllNodes();
    }

    @Override
    public void updateCredentialsNoParam() throws Exception {

        log.info("updateCredentialsNoParam");

        final Command command = new Command("secadm", CREDS_UPDATE + " -n " + NodeSecurityDataConstants.MECONTEXT_FDN1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info("updateCredentialsNoParam :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(NscsErrorCodes.SYNTAX_ERROR, rowsAsListOfStrings));

    }

    @Override
    public void updateCredentialsMissingMO() throws Exception {

        log.info("updateCredentialsMissingMO");

        dataSetup.insertData();

        final Command command = new Command("secadm", CREDS_UPDATE + USERNAME_PASSWORDS + " -n " + NodeSecurityDataConstants.MECONTEXT_FDN1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info("updateCredentialsMissingMO :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("The node specified requires the node credentials to be defined", rowsAsListOfStrings));

        dataSetup.deleteAllNodes();
    }

    @Override
    public void updateCredentialsDuplicateParam() throws Exception {

        log.info("updateCredentialsDuplicateParam");

        final Command command = new Command("secadm", CREDS_UPDATE + ROOTUSERNAME + ROOTUSERNAME + " -n " + NodeSecurityDataConstants.MECONTEXT_FDN1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info("updateCredentialsDuplicateParam :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(NscsErrorCodes.SYNTAX_ERROR, rowsAsListOfStrings));

    }

    @Override
    public void getCredentialsSecureHide() throws Exception {

        log.info("getCredentialsSecureHide");

        dataSetup.insertDataWithDG2();

        createCredentials(NodeSecurityDataConstants.MECONTEXT_FDN1);

        final Command command = new Command("secadm",
                CREDS_GET + " " + USERTYPE_SECURE + " " + PLAINTEXT_HIDE + " -n " + NodeSecurityDataConstants.MECONTEXT_FDN1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info("getCredentialsSecureHide :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(COMMAND_EXECUTED_SUCCESSFULLY, rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList(SECURE, rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList(PASSWORD_HIDE, rowsAsListOfStrings));
        dataSetup.deleteAllNodes();

    }

    @Override
    public void getCredentialsSecureShow() throws Exception {

        log.info("getCredentialsSecureShow");

        dataSetup.insertDataWithDG2();

        createCredentials(NodeSecurityDataConstants.MECONTEXT_FDN1);

        final Command command = new Command("secadm",
                CREDS_GET + " " + USERTYPE_SECURE + " " + PLAINTEXT_SHOW + " -n " + NodeSecurityDataConstants.MECONTEXT_FDN1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info("getCredentialsSecureShow :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(COMMAND_EXECUTED_SUCCESSFULLY, rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList(SECURE, rowsAsListOfStrings));
        assertFalse(responseDtoReader.messageIsContainedInList(PASSWORD_HIDE, rowsAsListOfStrings));
        dataSetup.deleteAllNodes();
    }

    @Override
    public void getCredentialsSecureDefaultHide() throws Exception {

        log.info("getCredentialsSecureDefaultHide");

        dataSetup.insertDataWithDG2();

        createCredentials(NodeSecurityDataConstants.MECONTEXT_FDN1);

        final Command command = new Command("secadm", CREDS_GET + " " + USERTYPE_SECURE + " -n " + NodeSecurityDataConstants.MECONTEXT_FDN1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info("getCredentialsSecureDefaultHide :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(COMMAND_EXECUTED_SUCCESSFULLY, rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList(SECURE, rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList(PASSWORD_HIDE, rowsAsListOfStrings));
        dataSetup.deleteAllNodes();
    }

    @Override
    public void getCredentialsRootHide() throws Exception {

        log.info("getCredentialsRootHide");

        dataSetup.insertDataWithDG2();

        createCredentials(NodeSecurityDataConstants.MECONTEXT_FDN1);

        final Command command = new Command("secadm",
                CREDS_GET + " " + USERTYPE_ROOT + " " + PLAINTEXT_HIDE + " -n " + NodeSecurityDataConstants.MECONTEXT_FDN1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info("getCredentialsRootHide :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(COMMAND_EXECUTED_SUCCESSFULLY, rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList(ROOT, rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList(PASSWORD_HIDE, rowsAsListOfStrings));
        dataSetup.deleteAllNodes();
    }

    @Override
    public void getCredentialsRootShow() throws Exception {

        log.info("getCredentialsRootShow");

        dataSetup.insertDataWithDG2();

        createCredentials(NodeSecurityDataConstants.MECONTEXT_FDN1);

        final Command command = new Command("secadm",
                CREDS_GET + " " + USERTYPE_ROOT + " " + PLAINTEXT_SHOW + " -n " + NodeSecurityDataConstants.MECONTEXT_FDN1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info("getCredentialsRootShow :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(COMMAND_EXECUTED_SUCCESSFULLY, rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList(ROOT, rowsAsListOfStrings));
        assertFalse(responseDtoReader.messageIsContainedInList(PASSWORD_HIDE, rowsAsListOfStrings));
        dataSetup.deleteAllNodes();
    }

    @Override
    public void getCredentialsRootDefaultHide() throws Exception {

        log.info("getCredentialsRootDefaultHide");

        dataSetup.insertDataWithDG2();

        createCredentials(NodeSecurityDataConstants.MECONTEXT_FDN1);

        final Command command = new Command("secadm", CREDS_GET + " " + USERTYPE_ROOT + " -n " + NodeSecurityDataConstants.MECONTEXT_FDN1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info("getCredentialsRootDefaultHide :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(COMMAND_EXECUTED_SUCCESSFULLY, rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList(ROOT, rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList(PASSWORD_HIDE, rowsAsListOfStrings));
        dataSetup.deleteAllNodes();
    }

    @Override
    public void getCredentialsNormalHide() throws Exception {

        log.info("getCredentialsNormalHide");

        dataSetup.insertDataWithDG2();

        createCredentials(NodeSecurityDataConstants.MECONTEXT_FDN1);

        final Command command = new Command("secadm",
                CREDS_GET + " " + USERTYPE_NORMAL + " " + PLAINTEXT_HIDE + " -n " + NodeSecurityDataConstants.MECONTEXT_FDN1);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info("getCredentialsNormalHide :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(COMMAND_EXECUTED_SUCCESSFULLY, rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList(NORMAL, rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList(PASSWORD_HIDE, rowsAsListOfStrings));
        dataSetup.deleteAllNodes();
    }

    @Override
    public void getCredentialsNormalShow() throws Exception {

        log.info("getCredentialsNormalShow");

        dataSetup.insertDataWithDG2();

        createCredentials(NodeSecurityDataConstants.MECONTEXT_FDN1);

        final Command command = new Command("secadm",
                CREDS_GET + " " + USERTYPE_NORMAL + " " + PLAINTEXT_SHOW + " -n " + NodeSecurityDataConstants.MECONTEXT_FDN1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info("getCredentialsNormalShowParam :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(COMMAND_EXECUTED_SUCCESSFULLY, rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList(NORMAL, rowsAsListOfStrings));
        assertFalse(responseDtoReader.messageIsContainedInList(PASSWORD_HIDE, rowsAsListOfStrings));
        dataSetup.deleteAllNodes();
    }

    @Override
    public void getCredentialsNormalDefaultHide() throws Exception {

        log.info("getCredentialsNormalDefaultHide");

        dataSetup.insertDataWithDG2();

        createCredentials(NodeSecurityDataConstants.MECONTEXT_FDN1);

        final Command command = new Command("secadm", CREDS_GET + " " + USERTYPE_NORMAL + " -n " + NodeSecurityDataConstants.MECONTEXT_FDN1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info("getCredentialsNormalDefaultHide :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(COMMAND_EXECUTED_SUCCESSFULLY, rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList(NORMAL, rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList(PASSWORD_HIDE, rowsAsListOfStrings));
        dataSetup.deleteAllNodes();
    }

    @Override
    public void getCredentialsSecureHideMissingNodes() throws Exception {

        log.info("getCredentialsSecureHideMissingNodes");

        final Command command = new Command("secadm", CREDS_GET + " " + USERTYPE_SECURE + " " + PLAINTEXT_HIDE + " -n ");
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info("getCredentialsSecureHideMissingNodes :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(NscsErrorCodes.SYNTAX_ERROR, rowsAsListOfStrings));

    }

    @Override
    public void getCredentialsRootHideMissingNodes() throws Exception {

        log.info("getCredentialsRootHideMissingNodes");

        final Command command = new Command("secadm", CREDS_GET + " " + USERTYPE_ROOT + " " + PLAINTEXT_HIDE + " -n ");
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info("getCredentialsRootHideMissingNodes :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(NscsErrorCodes.SYNTAX_ERROR, rowsAsListOfStrings));

    }

    @Override
    public void getCredentialsNormalHideMissingNodes() throws Exception {

        log.info("getCredentialsNormalHideMissingNodes");

        final Command command = new Command("secadm", CREDS_GET + " " + USERTYPE_NORMAL + " " + PLAINTEXT_HIDE + " -n ");
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info("getCredentialsNormalHideMissingNodes :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(NscsErrorCodes.SYNTAX_ERROR, rowsAsListOfStrings));

    }

    @Override
    public void getCredentialsNormalHideNotExistingNodes() throws Exception {

        log.info("getCredentialsNormalHideNotExistingNodes");

        final Command command = new Command("secadm", CREDS_GET + " " + USERTYPE_NORMAL + " " + PLAINTEXT_HIDE + " -n NotExinstingNode");
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info("getCredentialsNormalHideNotExistingNodes :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(NscsErrorCodes.THE_NETWORK_ELEMENT_SPECIFIED_DOES_NOT_EXIST, rowsAsListOfStrings));
    }

    @Override
    public void getCredentialsSGSNWithNormalUserTypeShow() throws Exception {

        log.info("getCredentialsSGSNWithNormalUserTypeShow");

        dataSetup.insertDataWithDG2();

        createCredentialsForComEcim(NodeSecurityDataConstants.MECONTEXT_FDN3);

        final Command command = new Command("secadm",
                CREDS_GET + " " + USERTYPE_NORMAL + " " + PLAINTEXT_SHOW + " -n " + NodeSecurityDataConstants.MECONTEXT_FDN3);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info("getCredentialsSGSNWithNormalUserTypeShow :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(COMMAND_EXECUTED_SUCCESSFULLY, rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList(NORMAL, rowsAsListOfStrings));
        dataSetup.deleteAllNodes();

    }

    @Override
    public void getCredentialsSGSNWithNormalUserTypeHide() throws Exception {

        log.info("getCredentialsSGSNWithNormalUserTypeHide");

        dataSetup.insertDataWithDG2();

        createCredentialsForComEcim(NodeSecurityDataConstants.MECONTEXT_FDN3);

        final Command command = new Command("secadm",
                CREDS_GET + " " + USERTYPE_NORMAL + " " + PLAINTEXT_HIDE + " -n " + NodeSecurityDataConstants.MECONTEXT_FDN3);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info("getCredentialsSGSNWithNormalUserTypeHide :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(COMMAND_EXECUTED_SUCCESSFULLY, rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList(NORMAL, rowsAsListOfStrings));
        dataSetup.deleteAllNodes();
    }

    @Override
    public void getCredentialsSGSNWithSecureUserTypeShow() throws Exception {
        log.info("getCredentialsSGSNWithSecureUserTypeShow");

        dataSetup.insertDataWithDG2();

        createCredentialsForComEcim(NodeSecurityDataConstants.MECONTEXT_FDN3);

        final Command command = new Command("secadm",
                CREDS_GET + " " + USERTYPE_SECURE + " " + PLAINTEXT_SHOW + " -n " + NodeSecurityDataConstants.MECONTEXT_FDN3);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info("getCredentialsSGSNWithSecureUserTypeShow :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(COMMAND_EXECUTED_SUCCESSFULLY, rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList(SECURE, rowsAsListOfStrings));
        assertFalse(responseDtoReader.messageIsContainedInList(PASSWORD_HIDE, rowsAsListOfStrings));
        dataSetup.deleteAllNodes();

    }

    @Override
    public void getCredentialsSGSNWithSecureUserTypeHide() throws Exception {

        log.info("getCredentialsSGSNWithSecureUserTypeHide");

        dataSetup.insertDataWithDG2();

        createCredentialsForComEcim(NodeSecurityDataConstants.MECONTEXT_FDN3);

        final Command command = new Command("secadm",
                CREDS_GET + " " + USERTYPE_SECURE + " " + PLAINTEXT_HIDE + " -n " + NodeSecurityDataConstants.MECONTEXT_FDN3);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info("getCredentialsSGSNWithSecureUserTypeHide :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(COMMAND_EXECUTED_SUCCESSFULLY, rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList(SECURE, rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList(PASSWORD_HIDE, rowsAsListOfStrings));
        dataSetup.deleteAllNodes();
    }

    @Override
    public void getCredentialsSGSNWithRootUserTypeShow() throws Exception {

        log.info("getCredentialsSGSNWithRootUserTypeShow");

        dataSetup.insertDataWithDG2();

        createCredentialsForComEcim(NodeSecurityDataConstants.MECONTEXT_FDN3);

        final Command command = new Command("secadm",
                CREDS_GET + " " + USERTYPE_ROOT + " " + PLAINTEXT_SHOW + " -n " + NodeSecurityDataConstants.MECONTEXT_FDN3);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info("getCredentialsSGSNWithRootUserTypeShow :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(COMMAND_EXECUTED_SUCCESSFULLY, rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList(ROOT, rowsAsListOfStrings));
        dataSetup.deleteAllNodes();
    }

    @Override
    public void getCredentialsSGSNWithRootUserTypeHide() throws Exception {

        log.info("getCredentialsSGSNWithRootUserTypeHide");

        dataSetup.insertDataWithDG2();

        createCredentialsForComEcim(NodeSecurityDataConstants.MECONTEXT_FDN3);

        final Command command = new Command("secadm",
                CREDS_GET + " " + USERTYPE_ROOT + " " + PLAINTEXT_HIDE + " -n " + NodeSecurityDataConstants.MECONTEXT_FDN3);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info("getCredentialsSGSNWithRootUserTypeHide :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(COMMAND_EXECUTED_SUCCESSFULLY, rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList(ROOT, rowsAsListOfStrings));
        dataSetup.deleteAllNodes();

    }

    @Override
    public void getCredentialsWithoutUserTypeShow() throws Exception {

        log.info("getCredentialsWithoutUserTypeShow");

        dataSetup.insertDataWithDG2();

        createCredentials(NodeSecurityDataConstants.MECONTEXT_FDN1);

        final Command command = new Command("secadm", CREDS_GET + " " + PLAINTEXT_SHOW + " -n " + NodeSecurityDataConstants.MECONTEXT_FDN1);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info("getCredentialsWithoutUserTypeShow :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(COMMAND_EXECUTED_SUCCESSFULLY, rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList(NORMAL, rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList(ROOT, rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList(SECURE, rowsAsListOfStrings));
        assertFalse(responseDtoReader.messageIsContainedInList(PASSWORD_HIDE, rowsAsListOfStrings));
        dataSetup.deleteAllNodes();

    }

    @Override
    public void getCredentialsWithoutUserTypeHide() throws Exception {

        log.info("getCredentialsWithoutUserTypeHide");

        dataSetup.insertDataWithDG2();

        createCredentials(NodeSecurityDataConstants.MECONTEXT_FDN1);

        final Command command = new Command("secadm", CREDS_GET + " " + PLAINTEXT_HIDE + " -n " + NodeSecurityDataConstants.MECONTEXT_FDN1);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info("getCredentialsWithoutUserTypeHide :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(COMMAND_EXECUTED_SUCCESSFULLY, rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList(NORMAL, rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList(ROOT, rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList(SECURE, rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList(PASSWORD_HIDE, rowsAsListOfStrings));
        dataSetup.deleteAllNodes();

    }

    @Override
    public void getCredentialsWithoutUserTypeAndPlainText() throws Exception {

        log.info("getCredentialsWithoutUserTypeAndPlainText");

        dataSetup.insertDataWithDG2();

        createCredentials(NodeSecurityDataConstants.MECONTEXT_FDN1);

        final Command command = new Command("secadm", CREDS_GET + " -n " + NodeSecurityDataConstants.MECONTEXT_FDN1);

        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info("getCredentialsWithoutUserTypeAndPlainText :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(COMMAND_EXECUTED_SUCCESSFULLY, rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList(NORMAL, rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList(ROOT, rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList(SECURE, rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList(PASSWORD_HIDE, rowsAsListOfStrings));
        dataSetup.deleteAllNodes();

    }

    private void createCredentials(final String node) {
        final Command command = new Command("secadm", CREDS_CREATE + USERNAME_PASSWORDS + " -n " + node);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info("create credentials :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("All credentials were created successfully", rowsAsListOfStrings));
    }

    private void createCredentialsForComEcim(final String node) {
        final Command command = new Command("secadm", CREDS_CREATE + USERNAME_PASSWORDS_COM_ECIM + " -n " + node);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                log.info("create credentials :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("All credentials were created successfully", rowsAsListOfStrings));
    }

    private static void assertErrorCode(final ErrorType type, final int actualErrorCode) {
        assertEquals(ERROR_CODE_START_INT + type.toInt(), actualErrorCode);
    }

}
