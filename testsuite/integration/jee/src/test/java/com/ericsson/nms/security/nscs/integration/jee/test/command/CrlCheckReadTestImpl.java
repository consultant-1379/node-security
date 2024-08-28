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

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.FileUtility;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.ResponseDtoReader;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityRadioNodesDataSetup;
import com.ericsson.oss.services.scriptengine.spi.CommandHandler;
import com.ericsson.oss.services.scriptengine.spi.dtos.Command;
import com.ericsson.oss.services.scriptengine.spi.dtos.CommandResponseDto;

/**
 * This Class is for Read crlCheck attribute on g2 nodes for OAM or IPSEC
 *
 */
public class CrlCheckReadTestImpl implements CrlCheckReadTest {

    private static final String CRL_CHECK_READ = "read crlcheck -ct OAM -nf file:MultipleNodesCrlCheck.txt";
    private static final String NODE_NAME_1 = "DUG22";
    public static final String COMMAND_SECADM = "secadm";

    @Inject
    private CommandHandler commandHandler;

    @Inject
    private NodeSecurityRadioNodesDataSetup nodeSecurityRadioNodesDataSetup;

    @Inject
    private Logger logger;

    @Inject
    private FileUtility fileUtility;

    ResponseDtoReader responseDtoReader = new ResponseDtoReader();

    @Override
    public void testReadCrlCheck_NodeDoesNotExist() throws Exception {
        logger.info("******* testReadCrlCheck_NodeDoesNotExist Test Started ******");
        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
        } catch (Exception e) {
            logger.error("Exception Ocurred while deleting node for testReadCrlCheck_NodeDoesNotExist " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "read crlcheck -ct OAM -n " + NODE_NAME_1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("ReadCrlCheckOnNode for testReadCrlCheck_NodeDoesNotExist Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(
                "The node specified does not exist Please specify a valid node that exists in the system.", rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("******* testReadCrlCheck_NodeDoesNotExist Test End ******");

    }

    @Override
    public void testReadCrlCheck_TrustCategoryDoesNotExist() throws Exception {
        logger.info("******* testReadCrlCheck_TrustCategoryDoesNotExist Test Started ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNode(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2);
        } catch (Exception e) {
            logger.info("Exception Ocurred while creating node for testReadCrlCheck_TrustCategoryDoesNotExist " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "read crlcheck -ct OAM -n " + NODE_NAME_1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("ReadCrlCheckOnNode for testReadCrlCheck_TrustCategoryDoesNotExist Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Trust Category MO does not exist for the given node.", rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("******* testReadCrlCheck_TrustCategoryDoesNotExist Test End ******");

    }

    @Override
    public void testReadCrlCheck_InvalidCertificateType() throws Exception {
        logger.info("******* testReadCrlCheck_InvalidCertificateType Test Started ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNode(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2);
        } catch (Exception e) {
            logger.info("Exception Ocurred while creating node for testReadCrlCheck_InvalidCertificateType " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "read crlcheck -ct OAM123 -n " + NODE_NAME_1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("ReadCrlCheckOnNode for testReadCrlCheck_InvalidCertificateType Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Accepted arguments are [IPSEC, OAM]", rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("******* testReadCrlCheck_InvalidCertificateType Test End ******");
    }

    @Override
    public void testReadCrlCheck_NodeNotInSynch() throws Exception {
        logger.info("******* testReadCrlCheck_NodeNotInSynch Test Started ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNode(NODE_NAME_1, "UNSYNCHRONIZED", SecurityLevel.LEVEL_2);
        } catch (Exception e) {
            logger.info("Exception Ocurred while creating node for testReadCrlCheck_NodeNotInSynch " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "read crlcheck -ct OAM -n " + NODE_NAME_1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("ReadCrlCheckOnNode for testReadCrlCheck_NodeNotInSynch Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(
                "NetworkElement=DUG22 ERROR 10005 The node specified is not synchronized Please ensure the node specified is synchronized.",
                rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("******* testReadCrlCheck_NodeNotInSynch Test End ******");

    }

    @Override
    public void testReadCrlCheck_WithEmptyFile() throws Exception {
        logger.info("******* testReadCrlCheck_WithEmptyFile Test Started ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCrlCheck(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");
        } catch (Exception e) {
            logger.info("Exception Ocurred while creating node for testReadCrlCheck_WithEmptyFile " + e.getMessage());
            e.printStackTrace();
        }

        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("file:", fileUtility.readResourceFile("EmptyCrlCheck.txt"));

        final Command command = new Command("secadm", CRL_CHECK_READ, properties);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("ReadCrlCheckOnNode for testReadCrlCheck_WithEmptyFile Response :" + row);
            }
        }
        assertTrue(responseDtoReader.messageIsContainedInList("Error 10002 : The contents of the file provided are not in the correct format",
                rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("******* testReadCrlCheck_WithEmptyFile Test End ******");

    }

    @Override
    public void testReadCrlCheck_WithWrongFileContent() throws Exception {
        logger.info("******* testReadCrlCheck_WithWrongFileContent Test Started ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCrlCheck(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");
        } catch (Exception e) {
            logger.info("Exception Ocurred while creating node for testReadCrlCheck_WithWrongFileContent " + e.getMessage());
            e.printStackTrace();
        }

        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("file:", fileUtility.readResourceFile("EmptyCrlCheck.txt"));

        final Command command = new Command("secadm", CRL_CHECK_READ, properties);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("ReadCrlCheckOnNode for testReadCrlCheck_WithWrongFileContent Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Error 10002 : The contents of the file provided are not in the correct format",
                rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("******* testReadCrlCheck_WithWrongFileContent Test End ******");

    }

    @Override
    public void testReadCrlCheck_WithMulitpleNodes() throws Exception {
        logger.info("******* testReadCrlCheck_WithMulitpleNodes Test Started ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCrlCheck(NODE_NAME_1, "UNSYNCHRONIZED", SecurityLevel.LEVEL_2, "1");
            nodeSecurityRadioNodesDataSetup.createComEcimNode("LTE04dg2ERBS00012", "SYNCHRONIZED", SecurityLevel.LEVEL_2);
        } catch (Exception e) {
            logger.info("Exception Ocurred while creating node for testReadCrlCheck_WithMulitpleNodes " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "read crlcheck -ct OAM -n " + NODE_NAME_1 + ",LTE04dg2ERBS00011,LTE04dg2ERBS00012");
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("ReadCrlCheckOnNode for testReadCrlCheck_WithMulitpleNodes Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(
                "ERROR 10101 Trust Category MO does not exist for the given node. Perform Online certificate Enrollment on the node for TrustCategory MO to be present.",
                rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList(
                "ERROR 10005 The node specified is not synchronized Please ensure the node specified is synchronized.", rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList(
                "ERROR 10004 The node specified does not exist Please specify a valid node that exists in the system.", rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("******* testReadCrlCheck_WithMulitpleNodes Test End ******");

    }

}
