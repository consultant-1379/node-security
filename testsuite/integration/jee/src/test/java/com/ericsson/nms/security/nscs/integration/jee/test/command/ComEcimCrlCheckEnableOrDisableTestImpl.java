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

import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

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
 * This Class is for enable or disable crlCheck attribute on g2 nodes for OAM or IPSEC
 *
 */
public class ComEcimCrlCheckEnableOrDisableTestImpl implements ComEcimCrlCheckEnableOrDisableTest {

    private static final String CRL_CHECK_ENABLE = "enable crlcheck -ct OAM -nf file:MultipleNodesCrlCheck.txt";
    private static final String CRL_CHECK_DISABLE = "disable crlcheck -ct OAM -nf file:MultipleNodesCrlCheck.txt";
    private static final String NODE_NAME_1 = "DUG22";
    private static final String NODE_NAME_2 = "DUG28";
    public static final String COMMAND_SECADM = "secadm";

    @Inject
    private CommandHandler commandHandler;

    @Inject
    private NodeSecurityRadioNodesDataSetup nodeSecurityRadioNodesDataSetup;

    @Inject
    private FileUtility fileUtility;

    @Inject
    private Logger logger;

    ResponseDtoReader responseDtoReader = new ResponseDtoReader();

    @Override
    public void testEnableCrlCheck_NodeDoesNotExist_Failure() throws Exception {
        logger.info("******* testEnableCrlCheck_NodeDoesNotExist_Failure Test Started ******");
        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
        } catch (Exception e) {
            logger.error("Exception ocurred while deleting node for testEnableCrlCheck_NodeDoesNotExist_Failure " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "enable crlcheck -ct OAM -n " + NODE_NAME_1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("EnableCrlCheckOnNode for NodeDoesNotExist_Failure Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("NetworkElement=DUG22 10004 The node specified does not exist Please specify a valid node that exists in the system.",
                rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("******* testEnableCrlCheck_NodeDoesNotExist_Failure Test End ******");

    }

    @Override
    public void testEnableCrlCheck_TrustCategoryDoesNotExist_Failure() throws Exception {
        logger.info("******* testEnableCrlCheck_TrustCategoryDoesNotExist_Failure Test Started ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNode(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2);
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testEnableCrlCheck_TrustCategoryDoesNotExist_Failure " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "enable crlcheck -ct OAM -n " + NODE_NAME_1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("EnableCrlCheckOnNode for TrustCategoryDoesNotExist_Failure Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(
                "NetworkElement=DUG22 10101 Trust Category MO does not exist for the given node. Perform Online certificate Enrollment on the node for TrustCategory MO to be present.", rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("******* testEnableCrlCheck_TrustCategoryDoesNotExist_Failure Test End ******");

    }

    @Override
    public void testEnableCrlCheck_InvalidCertificateType_Failure() throws Exception {
        logger.info("******* testEnableCrlCheck_InvalidCertificateType_Failure Test Started ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNode(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2);
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testEnableCrlCheck_InvalidCertificateType_Failure " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "enable crlcheck -ct OAM123 -n " + NODE_NAME_1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("EnableCrlCheckOnNode for InvalidCertificateType_Failure Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Accepted arguments are [IPSEC, OAM, ALL]", rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("******* testEnableCrlCheck_InvalidCertificateType Test End ******");
    }

    @Override
    public void testEnableCrlCheck_NodeNotInSynch_Failure() throws Exception {
        logger.info("******* testEnableCrlCheck_NodeNotInSynch_Failure Test Started ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNode(NODE_NAME_1, "UNSYNCHRONIZED", SecurityLevel.LEVEL_2);
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testEnableCrlCheck_NodeNotInSynch_Failure " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "enable crlcheck -ct OAM -n " + NODE_NAME_1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("EnableCrlCheckOnNode for NodeNotInSynch_Failure Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("NetworkElement=DUG22 10005 The node specified is not synchronized Please ensure the node specified is synchronized.",
                rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("******* testEnableCrlCheck_NodeNotInSynch_Failure Test End ******");

    }

    @Override
    public void testEnableCrlCheck_DuplicateNodes_Failure() throws Exception {
        logger.info("******* testEnableCrlCheck_DuplicateNodes_Failure Test Started ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCrlCheck(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testEnableCrlCheck_DuplicateNodes_Failure " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "enable crlcheck -ct OAM -n " + NODE_NAME_1 + "," + NODE_NAME_1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("EnableCrlCheckOnNode for DuplicateNodes_Failure Response :" + row);
            }
        }

		assertTrue(responseDtoReader.messageIsContainedInList("Successfully started a job for CRL Check enable operation.", rowsAsListOfStrings));
        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("******* testEnableCrlCheck_DuplicateNodes_Failure Test End ******");

    }

    @Override
    public void testEnableCrlCheck_WithWrongFileContent_Failure() throws Exception {
        logger.info("******* testEnableCrlCheck_WithWrongFileContent_Failure Test Started ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCrlCheck(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testEnableCrlCheck_WithWrongFileContent_Failure " + e.getMessage());
            e.printStackTrace();
        }

        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("file:", fileUtility.readResourceFile("EmptyCrlCheck.txt"));

        final Command command = new Command("secadm", CRL_CHECK_ENABLE, properties);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("EnableCrlCheckOnNode for WithWrongFileContent_Failure Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Error 10002 : The contents of the file provided are not in the correct format", rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("******* testEnableCrlCheck_WithWrongFileContent_Failure Test End ******");

    }

    @Override
    public void testEnableCrlCheck_InvalidNodesWithFile_Failure() throws Exception {
        logger.info("******* testEnableCrlCheck_InvalidNodesWithFile_Failure Test Started ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCrlCheck(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testEnableCrlCheck_InvalidNodesWithFile_Failure " + e.getMessage());
            e.printStackTrace();
        }

        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("file:", fileUtility.readResourceFile("InvalidNodesCrlCheck.txt"));

        final Command command = new Command("secadm", CRL_CHECK_ENABLE, properties);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("EnableCrlCheckOnNode for InvalidNodesWithFile_Failure Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("The node specified does not exist Please specify a valid node that exists in the system.", rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("******* testEnableCrlCheck_InvalidNodesWithFile_Failure Test End ******");

    }

    @Override
    public void testEnableCrlCheck_WithMulitpleNodes_Failure() throws Exception {
        logger.info("******* testEnableCrlCheck_WithMulitpleNodes_Failure Test Started ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCrlCheck(NODE_NAME_1, "UNSYNCHRONIZED", SecurityLevel.LEVEL_2, "1");
            nodeSecurityRadioNodesDataSetup.createComEcimNode("LTE04dg2ERBS00012", "SYNCHRONIZED", SecurityLevel.LEVEL_2);
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testEnableCrlCheck_WithMulitpleNodes_Failure " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "enable crlcheck -ct OAM -n " + NODE_NAME_1 + ",LTE04dg2ERBS00011,LTE04dg2ERBS00012");
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("EnableCrlCheckOnNode for WithMulitpleNodes_Failure Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Failed to start the job for CRL Check enable operation as all the provided node(s) are invalid. Invalid node details are given below :",
                rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList(
                "NetworkElement=LTE04dg2ERBS00012 10101 Trust Category MO does not exist for the given node. Perform Online certificate Enrollment on the node for TrustCategory MO to be present.", rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("******* testEnableCrlCheck_WithMulitpleNodes_Failure Test End ******");

    }

    @Override
    public void testEnableCrlCheck_NodeDoesNotExist_PartialSuccess() throws Exception {
        logger.info("******* testEnableCrlCheck_NodeDoesNotExist_PartialSuccess Test Started ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCrlCheck(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testEnableCrlCheck_NodeDoesNotExist_PartialSuccess " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "enable crlcheck -ct OAM -n " + NODE_NAME_1 + ",LTE04dg2ERBS00012");
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("EnableCrlCheckOnNode for NodeDoesNotExist_PartialSuccess Response :" + row);
            }
        }

        assertTrue(responseDtoReader
                .messageIsContainedInList("Successfully started a job for CRL Check enable operation for some node(s).", rowsAsListOfStrings));
        assertTrue(responseDtoReader
                .messageIsContainedInList("Invalid node details are given below :", rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList("NetworkElement=LTE04dg2ERBS00012 10004 The node specified does not exist Please specify a valid node that exists in the system.",
                rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("******* testEnableCrlCheck_NodeDoesNotExist_PartialSuccess Test End ******");

    }

    @Override
    public void testEnableCrlCheck_NodeNotInSynch_PartialSuccess() throws Exception {
        logger.info("******* testEnableCrlCheck_NodeNotInSynch_PartialSuccess Test Started ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCrlCheck(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCrlCheck("LTE04dg2ERBS00012", "UNSYNCHRONIZED", SecurityLevel.LEVEL_2, "1");
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testEnableCrlCheck_NodeNotInSynch_PartialSuccess " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "enable crlcheck -ct OAM -n " + NODE_NAME_1 + ",LTE04dg2ERBS00012");
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("EnableCrlCheckOnNode for NodeNotInSynch_PartialSuccess Response :" + row);
            }
        }

        assertTrue(responseDtoReader
                .messageIsContainedInList("Successfully started a job for CRL Check enable operation for some node(s).", rowsAsListOfStrings));
        assertTrue(responseDtoReader
                .messageIsContainedInList("Invalid node details are given below :", rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList("NetworkElement=LTE04dg2ERBS00012 10005 The node specified is not synchronized Please ensure the node specified is synchronized.",
                rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("******* testEnableCrlCheck_NodeNotInSynch_PartialSuccess Test End ******");

    }

    @Override
    public void testEnableCrlCheck_FileWithMulitpleNodes_Success() throws Exception {
        logger.info("******* testEnableCrlCheck_FileWithMulitpleNodes_Success Test Started ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCrlCheck(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCrlCheck(NODE_NAME_2, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testEnableCrlCheck_FileWithMulitpleNodes_Success " + e.getMessage());
            e.printStackTrace();
        }

        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("file:", fileUtility.readResourceFile("MultipleNodesCrlCheck.txt"));

        final Command command = new Command("secadm", CRL_CHECK_ENABLE, properties);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("EnableCrlCheckOnNode for FileWithMulitpleNodes_Success Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Successfully started a job for CRL Check enable operation.", rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("******* testEnableCrlCheck_FileWithMulitpleNodes_Success Test End ******");

    }

    @Override
    public void testEnableCrlCheck_SingleNodeWithOutFile_Success() throws Exception {
        logger.info("******* testEnableCrlCheck_SingleNodeWithOutFile_Success Test Started ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCrlCheck(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testEnableCrlCheck_SingleNodeWithOutFile_Success " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "enable crlcheck -ct OAM -n " + NODE_NAME_1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("EnableCrlCheckOnNode for SingleNodeWithOutFile_Success Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Successfully started a job for CRL Check enable operation.", rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("******* testEnableCrlCheck_SingleNodeWithOutFile_Success Test End ******");

    }

    @Override
    public void testEnableCrlCheck_MulitpleNodesWithOutFile_Success() throws Exception {
        logger.info("******* testEnableCrlCheck_MulitpleNodesWithOutFile_Success Test Started ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCrlCheck(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCrlCheck(NODE_NAME_2, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testEnableCrlCheck_MulitpleNodesWithOutFile_Success " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "enable crlcheck -ct OAM -n " + NODE_NAME_1 + "," + NODE_NAME_2);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("EnableCrlCheckOnNode for MulitpleNodesWithOutFile_Success Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Successfully started a job for CRL Check enable operation.", rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("******* testEnableCrlCheck_MulitpleNodesWithOutFile_Success Test End ******");

    }

    @Override
    public void testDisableCrlCheck_FileWithMulitpleNodes_Success() throws Exception {
        logger.info("******* testDisableCrlCheck_FileWithMulitpleNodes_Success Test Started ******");
        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCrlCheck(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCrlCheck(NODE_NAME_2, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testDisableCrlCheck_FileWithMulitpleNodes_Success " + e.getMessage());
            e.printStackTrace();
        }

        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("file:", fileUtility.readResourceFile("MultipleNodesCrlCheck.txt"));

        final Command command = new Command("secadm", CRL_CHECK_DISABLE, properties);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("DisableCrlCheckOnNode for FileWithMulitpleNodes_Success Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Successfully started a job for CRL Check disable operation.", rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("******* testDisableCrlCheck_FileWithMulitpleNodes_Success Test End ******");

    }

    @Override
    public void testDisableCrlCheck_SingleNodeWithOutFile_Success() throws Exception {
        logger.info("******* testDisableCrlCheck_SingleNodeWithOutFile_Success Test Started ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCrlCheck(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testDisableCrlCheck_SingleNodeWithOutFile_Success " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "disable crlcheck -ct OAM -n " + NODE_NAME_1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("DisableCrlCheckOnNode for FileWithMulitpleNodes_Success Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Successfully started a job for CRL Check disable operation.", rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();
        logger.info("******* testDisableCrlCheck_SingleNodeWithOutFile_Success Test End ******");

    }

    @Override
    public void testDisableCrlCheck_MulitpleNodesWithOutFile_Success() throws Exception {
        logger.info("******* testDisableCrlCheck_MulitpleNodesWithOutFile_Success Test Started ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCrlCheck(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCrlCheck(NODE_NAME_2, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testDisableCrlCheck_MulitpleNodesWithOutFile_Success " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "disable crlcheck -ct OAM -n " + NODE_NAME_1 + "," + NODE_NAME_2);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("DisableCrlCheckOnNode for MulitpleNodesWithOutFile_Success Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Successfully started a job for CRL Check disable operation.", rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("******* testDisableCrlCheck_MulitpleNodesWithOutFile_Success Test End ******");

    }

    @Override
    public void testDisableCrlCheck_NodeDoesNotExist_Failure() throws Exception {
        logger.info("******* testDisableCrlCheck_NodeDoesNotExist_Failure Test Started ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
        } catch (Exception e) {
            logger.info("Exception ocurred while deleting node for testDisableCrlCheck_NodeDoesNotExist_Failure " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "disable crlcheck -ct OAM -n " + NODE_NAME_1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("DiableCrlCheckOnNode for NodeDoesNotExist_Failure Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("NetworkElement=DUG22 10004 The node specified does not exist Please specify a valid node that exists in the system.",
                rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("******* testDisableCrlCheck_NodeDoesNotExist_Failure Test Started ******");
    }

    @Override
    public void testDisableCrlCheck_TrustCategoryDoesNotExist_Failure() throws Exception {
        logger.info("******* testDisableCrlCheck_TrustCategoryDoesNotExist_Failure Test Started ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNode(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2);
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testDisableCrlCheck_TrustCategoryDoesNotExist_Failure " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "disable crlcheck -ct OAM -n " + NODE_NAME_1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("DisableCrlCheckOnNode for TrustCategoryDoesNotExist_Failure Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(
                "NetworkElement=DUG22 10101 Trust Category MO does not exist for the given node. Perform Online certificate Enrollment on the node for TrustCategory MO to be present.", rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("******* testDisableCrlCheck_TrustCategoryDoesNotExist_Failure Test End ******");

    }

    @Override
    public void testDisableCrlCheck_InvalidCertificateType_Failure() throws Exception {
        logger.info("******* testDisableCrlCheck_InvalidCertificateType_Failure Test Started ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNode(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2);
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testDisableCrlCheck_InvalidCertificateType_Failure " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "disable crlcheck -ct OAM123 -n " + NODE_NAME_1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("DisableCrlCheckOnNode for InvalidCertificateType_Failure Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Accepted arguments are [IPSEC, OAM, ALL]", rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("******* testDisableCrlCheck_InvalidCertificateType_Failure Test End ******");
    }

    @Override
    public void testDisableCrlCheck_NodeNotInSynch_Failure() throws Exception {
        logger.info("******* testDisableCrlCheck_NodeNotInSynch_Failure Test Started ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNode(NODE_NAME_1, "UNSYNCHRONIZED", SecurityLevel.LEVEL_2);
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testDisableCrlCheck_NodeNotInSynch_Failure " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "disable crlcheck -ct OAM -n " + NODE_NAME_1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("DisableCrlCheckOnNode for NodeNotInSynch_Failure Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("NetworkElement=DUG22 10005 The node specified is not synchronized Please ensure the node specified is synchronized.",
                rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("******* testDisableCrlCheck_NodeNotInSynch_Failure Test End ******");

    }

    @Override
    public void testDisableCrlCheck_NodeDoesNotExist_PartialSuccess() throws Exception {
        logger.info("******* testEnableCrlCheck_NodeDoesNotExist_PartialSuccess Test Started ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCrlCheck(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testEnableCrlCheck_NodeDoesNotExist_PartialSuccess " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "disable crlcheck -ct OAM -n " + NODE_NAME_1 + ",LTE04dg2ERBS00012");
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("DisableCrlCheckOnNode for NodeDoesNotExist_PartialSuccess Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Successfully started a job for CRL Check disable operation for some node(s).",
                rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList("Invalid node details are given below :",
                rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList("NetworkElement=LTE04dg2ERBS00012 10004 The node specified does not exist Please specify a valid node that exists in the system.",
                rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("******* testEnableCrlCheck_NodeDoesNotExist_PartialSuccess Test End ******");

    }

    @Override
    public void testDisableCrlCheck_NodeNotInSynch_PartialSuccess() throws Exception {
        logger.info("******* testDisableCrlCheck_NodeNotInSynch_PartialSuccess Test Started ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCrlCheck(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCrlCheck("LTE04dg2ERBS00012", "UNSYNCHRONIZED", SecurityLevel.LEVEL_2, "1");
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testDisableCrlCheck_NodeNotInSynch_PartialSuccess " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "disable crlcheck -ct OAM -n " + NODE_NAME_1 + ",LTE04dg2ERBS00012");
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("DisableCrlCheckOnNode for NodeNotInSynch_PartialSuccess Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Successfully started a job for CRL Check disable operation for some node(s).",
                rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList("Invalid node details are given below :",
                rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList("NetworkElement=LTE04dg2ERBS00012 10005 The node specified is not synchronized Please ensure the node specified is synchronized.",
                rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("******* testDisableCrlCheck_NodeNotInSynch_PartialSuccess Test End ******");

    }

    @Override
    public void testDisableCrlCheck_DuplicateNodes_Failure() throws Exception {
        logger.info("******* testDisableCrlCheck_DuplicateNodes_Failure Test Started ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCrlCheck(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testDisableCrlCheck_DuplicateNodes_Failure " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "disable crlcheck -ct OAM -n " + NODE_NAME_1 + "," + NODE_NAME_1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("DisableCrlCheckOnNode for DuplicateNodes_Failure Response :" + row);
            }
        }
        
		assertTrue(responseDtoReader.messageIsContainedInList("Successfully started a job for CRL Check disable operation.", rowsAsListOfStrings));
        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("******* testDisableCrlCheck_DuplicateNodes_Failure Test End ******");

    }

    @Override
    public void testDisableCrlCheck_WithWrongFileContent_Failure() throws Exception {
        logger.info("******* testDisableCrlCheck_WithWrongFileContent_Failure Test Started ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCrlCheck(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testDisableCrlCheck_WithWrongFileContent_Failure " + e.getMessage());
            e.printStackTrace();
        }

        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("file:", fileUtility.readResourceFile("EmptyCrlCheck.txt"));

        final Command command = new Command("secadm", CRL_CHECK_DISABLE, properties);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("DisableCrlCheckOnNode for WithEmptyFile_Failure Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Error 10002 : The contents of the file provided are not in the correct format", rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("******* testDisableCrlCheck_WithWrongFileContent_Failure Test End ******");

    }

    @Override
    public void testDisableCrlCheck_InvalidNodesWithFile_Failure() throws Exception {
        logger.info("******* testDisableCrlCheck_InvalidNodesWithFile_Failure Test Started ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCrlCheck(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testDisableCrlCheck_InvalidNodesWithFile_Failure " + e.getMessage());
            e.printStackTrace();
        }

        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("file:", fileUtility.readResourceFile("InvalidNodesCrlCheck.txt"));

        final Command command = new Command("secadm", CRL_CHECK_DISABLE, properties);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("DisableCrlCheckOnNode for InvalidNodesWithFile_Failure Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("The node specified does not exist Please specify a valid node that exists in the system.", rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("******* testDisableCrlCheck_InvalidNodesWithFile_Failure Test End ******");

    }

    @Override
    public void testDisableCrlCheck_WithMulitpleNodes_Failure() throws Exception {
        logger.info("******* testDisableCrlCheck_WithMulitpleNodes_Failure Test Started ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCrlCheck(NODE_NAME_1, "UNSYNCHRONIZED", SecurityLevel.LEVEL_2, "1");
            nodeSecurityRadioNodesDataSetup.createComEcimNode("LTE04dg2ERBS00012", "SYNCHRONIZED", SecurityLevel.LEVEL_2);
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testDisableCrlCheck_WithMulitpleNodes_Failure " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "disable crlcheck -ct OAM -n " + NODE_NAME_1 + ",LTE04dg2ERBS00011,LTE04dg2ERBS00012");
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("DisableCrlCheckOnNode for WithMulitpleNodes_Failure Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(
                "Failed to start the job for CRL Check disable operation as all the provided node(s) are invalid. Invalid node details are given below :", rowsAsListOfStrings));

        assertTrue(responseDtoReader.messageIsContainedInList(
                "NetworkElement=LTE04dg2ERBS00012 10101 Trust Category MO does not exist for the given node. Perform Online certificate Enrollment on the node for TrustCategory MO to be present.", rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("******* testDisableCrlCheck_WithMulitpleNodes_Failure Test End ******");

    }

    private byte[] fileToBytes(final String filePath) throws Exception {

        FileInputStream fileInputStream = null;

        File file = new File(filePath);

        byte[] bFile = new byte[(int) file.length()];

        try {
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bFile);
            fileInputStream.close();

        } catch (Exception exception) {
            logger.error("Error while reading data from file  " + exception.getMessage());
            throw new Exception(exception.getMessage());
        }
        return bFile;

    }

}
