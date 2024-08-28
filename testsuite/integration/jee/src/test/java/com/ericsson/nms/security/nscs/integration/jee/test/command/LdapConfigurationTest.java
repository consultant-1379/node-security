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

import java.io.*;
import java.net.URLDecoder;
import java.util.*;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.handler.command.impl.LdapCommandHandlerHelper;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.AccessControlHelper;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.FileUtility;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.ResponseDtoReader;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityDataConstants;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityRadioNodesDataSetup;
import com.ericsson.oss.services.scriptengine.spi.CommandHandler;
import com.ericsson.oss.services.scriptengine.spi.dtos.Command;
import com.ericsson.oss.services.scriptengine.spi.dtos.CommandResponseDto;

public class LdapConfigurationTest implements LdapConfigurationTests {

    @Inject
    Logger logger;

    @Inject
    CommandHandler commandHandler;

    @Inject
    NodeSecurityRadioNodesDataSetup dataSetup;

    @Inject
    ResponseDtoReader responseDtoReader;

    @Inject
    NscsCMReaderService reader;

    @Inject
    private FileUtility fileUtility;

    public static final String CREDS_CREATE = "credentials create";

    public static final String USERNAME_PASSWORDS = " --secureusername radionode --secureuserpassword \"radionode123\" ";
    private static final String ACC_CNTRL_PROPERTIES_FILE = "src/test/resources/accesscontrol.global.properties";
            
    static {
        File file = new File(ACC_CNTRL_PROPERTIES_FILE);
        if (file.exists() && file.isFile())
            System.setProperty("configuration.java.properties", ACC_CNTRL_PROPERTIES_FILE);
    }

    @Override
    public void ldapValidConfigurationTest() throws IOException {
        logger.info("*** Ldap Configuration Test Started ***");

        AccessControlHelper.setupUser("toruser1");
        try {
            dataSetup.deleteAllNodes();
            dataSetup.createComEcimNode(NodeSecurityDataConstants.NODE_NAME3, "SYNCHRONIZED", SecurityLevel.LEVEL_2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        createCredentials(NodeSecurityDataConstants.NODE_NAME3);

        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("file:", fileUtility.readResourceFile("LdapConfiguration.xml"));

        final Command command = new Command("secadm", "ldap configure -xf file:LdapConfiguration.xml", properties);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);
        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("Command Response1 :: " + row);

            }
            String checkMsg = extractMsgUntilSpecificSeparator(LdapCommandHandlerHelper.LDAPConfigurationMode.LDAP_CONFIGURE.getExecutedMessage(), "\\.");
            assertTrue(responseDtoReader.messageIsContainedInList(checkMsg, rowsAsListOfStrings));
        }
        logger.info("*** Ldap Configuration Test Finished ***");
    }

    @Override
    public void ldapInvalidConfigurationTest() throws IOException {
        logger.info("*** Ldap Configuration Test with Invalid Configuration Started ***");

        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("file:", fileUtility.readResourceFile("InvalidLdapConfiguration.xml"));

        final Command command = new Command("secadm", "ldap configure -xf file:InvalidLdapConfiguration.xml", properties);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);
        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("Command Response2 :: " + row);

            }
            assertTrue(responseDtoReader.messageIsContainedInList(NscsErrorCodes.INVALID_INPUT_XML_FILE, rowsAsListOfStrings));
        }
        logger.info("*** Ldap Configuration Test with Invalid Configuration Finished ***");
    }

    @Override
    public void ldapConfigurationPartialInvalidNodeTest() throws IOException {
        logger.info("*** Ldap Configuration Test with Partial Invalid Nodes Started ***");

        AccessControlHelper.setupUser("toruser1");
        try {
            dataSetup.deleteAllNodes();
            dataSetup.createComEcimNode(NodeSecurityDataConstants.NODE_NAME3, "SYNCHRONIZED", SecurityLevel.LEVEL_2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        createCredentials(NodeSecurityDataConstants.NODE_NAME3);

        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("file:", fileUtility.readResourceFile("LdapPartialInvalidNodesConfiguration.xml"));

        final Command command = new Command("secadm", "ldap configure -xf file:LdapPartialInvalidNodesConfiguration.xml", properties);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);
        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("Command Response3 :: " + row);

            }
            assertTrue(responseDtoReader.messageIsContainedInList(NscsErrorCodes.INVALID_INPUT_NODE_LIST_FOR_COMMAND, rowsAsListOfStrings));
        }
        logger.info("*** Ldap Configuration Test with Partial Invalid Nodes Finished ***");
    }

    @Override
    public void ldapConfigurationAllInvalidNodeTest() throws IOException {
        logger.info("*** Ldap Configuration Test with All Invalid Nodes Started ***");

        AccessControlHelper.setupUser("toruser1");
        try {
            dataSetup.deleteAllNodes();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("file:", fileUtility.readResourceFile("LdapConfiguration.xml"));

        final Command command = new Command("secadm", "ldap configure -xf file:LdapConfiguration.xml", properties);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);
        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("Command Response4 :: " + row);

            }
            assertTrue(responseDtoReader.messageIsContainedInList(NscsErrorCodes.INVALID_INPUT_NODE_LIST_FOR_COMMAND, rowsAsListOfStrings));
        }
        logger.info("*** Ldap Configuration Test with All Invalid Nodes Started Finished ***");
    }

    @Override
    public void ldapValidReConfigurationTest() throws IOException {
        logger.info("*** Ldap ReConfiguration Test Started ***");

        try {
            dataSetup.deleteAllNodes();
            dataSetup.createComEcimNode(NodeSecurityDataConstants.NODE_NAME3, "SYNCHRONIZED", SecurityLevel.LEVEL_2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        createCredentials(NodeSecurityDataConstants.NODE_NAME3);

        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("file:", fileUtility.readResourceFile("LdapReconfiguration.xml"));

        final Command command = new Command("secadm", "ldap reconfigure -xf file:LdapReconfiguration.xml", properties);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);
        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("Command Response5 :: " + row);

            }
            String checkMsg = extractMsgUntilSpecificSeparator(LdapCommandHandlerHelper.LDAPConfigurationMode.LDAP_RECONFIGURE.getExecutedMessage(), "\\.");
            assertTrue(responseDtoReader.messageIsContainedInList(checkMsg, rowsAsListOfStrings));

        }
        logger.info("*** Ldap ReConfiguration Test Started Finished ***");
    }

    @Override
    public void ldapReConfigurationPartialInvalidNodeTest() throws IOException {
        logger.info("*** Ldap ReConfiguration Partial Invalid Nodes Test Started ***");

        try {
            dataSetup.deleteAllNodes();
            dataSetup.createComEcimNode(NodeSecurityDataConstants.NODE_NAME3, "SYNCHRONIZED", SecurityLevel.LEVEL_2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        createCredentials(NodeSecurityDataConstants.NODE_NAME3);

        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("file:", fileUtility.readResourceFile("LdapPartialInvalidNodesConfiguration.xml"));

        final Command command = new Command("secadm", "ldap reconfigure -xf file:LdapPartialInvalidNodesConfiguration.xml", properties);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);
        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("Command Response6 :: " + row);

            }
            assertTrue(responseDtoReader.messageIsContainedInList(NscsErrorCodes.INVALID_INPUT_NODE_LIST_FOR_COMMAND, rowsAsListOfStrings));

        }
        logger.info("*** Ldap ReConfiguration Partial Invalid Nodes Test Finished ***");
    }

    @Override
    public void ldapReConfigurationAllInvalidNodeTest() throws IOException {
        logger.info("*** Ldap ReConfiguration All Invalid Nodes Test Started ***");

        try {
            dataSetup.deleteAllNodes();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("file:", fileUtility.readResourceFile("LdapReconfiguration.xml"));

        final Command command = new Command("secadm", "ldap reconfigure -xf file:LdapReconfiguration.xml", properties);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);
        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("Command Response7 :: " + row);

            }
            assertTrue(responseDtoReader.messageIsContainedInList(NscsErrorCodes.INVALID_INPUT_NODE_LIST_FOR_COMMAND, rowsAsListOfStrings));

        }
        logger.info("*** Ldap ReConfiguration All Invalid Nodes Test Started Finished ***");
    }

    @Override
    public void ldapInvalidReConfigurationTest() throws IOException {
        logger.info("*** Ldap ReConfiguration Test with Invalid Configuration Started ***");

        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("file:", fileUtility.readResourceFile("InvalidLdapConfiguration.xml"));

        final Command command = new Command("secadm", "ldap reconfigure -xf file:InvalidLdapConfiguration.xml", properties);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);
        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("Command Response8 :: " + row);

            }
            assertTrue(responseDtoReader.messageIsContainedInList(NscsErrorCodes.INVALID_INPUT_XML_FILE, rowsAsListOfStrings));
        }
        logger.info("*** Ldap ReConfiguration Test with Invalid Configuration Finished ***");
    }

    @Override
    public void ldapConfigurationManualTest() {
        logger.info("*** Ldap Configuration Manual Option Test Started ***");

        AccessControlHelper.setupUser("toruser1");
        try {
            dataSetup.deleteAllNodes();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "ldap configure --manual");
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);
        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("Command Response9 :: " + row);

            }

            assertTrue(responseDtoReader.messageIsContainedInList("PROPERTY VALUE", rowsAsListOfStrings));
        }
        logger.info("*** Ldap Configuration Manual Option Test Finished ***");
    }

    private void createCredentials(final String node) {
        final Command command = new Command("secadm", CREDS_CREATE + USERNAME_PASSWORDS + " -n " + node);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);

        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("create credentials :" + row);
            }
        }
        assertTrue(responseDtoReader.messageIsContainedInList("All credentials were created successfully", rowsAsListOfStrings));
    }

    public String getTargetPath() {
        String responsePath = "";
        try {
            String path = this.getClass().getClassLoader().getResource("").getPath();
            String fullPath = URLDecoder.decode(path, "UTF-8");
            String pathArr[] = fullPath.split("/target");
            fullPath = pathArr[0];
            if (!File.separator.equalsIgnoreCase("/")) {
                responsePath = "\\";
            } else {
                responsePath = "";
            }
            responsePath = responsePath + new File(fullPath).getPath() + File.separator + "target" + File.separator + "test-classes";
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            logger.error("Error while getting path " + unsupportedEncodingException.getMessage());
        }
        return responsePath;
    }

    private String extractMsgUntilSpecificSeparator( String str, String separator) {
        String[] parts = str.split(separator);
        return parts[0];
    }

}
