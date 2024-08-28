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
 * This Class is to test on demand CRL download.
 *
 */
public class OnDemandCrlDownloadTestImpl implements OnDemandCrlDownloadTest {

    private static final String OnDemand_CRL_DOWNLOAD = "crl download -nf file:MultipleNodesCrlCheck.txt";
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
    public void testOnDemandCrlDownload_NodeDoesNotExist_Failure() throws Exception {
        logger.info("*******testOnDemandCrlDownload_NodeDoesNotExist_Failure Test Started ******");
        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
        } catch (Exception e) {
            logger.error("Exception ocurred while deleting node for testOnDemandCrlDownload_NodeDoesNotExist_Failure " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "crl download -n " + NODE_NAME_1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("On Demand CRL Download for NodeDoesNotExist_Failure Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("NetworkElement=DUG22 10004 The node specified does not exist Please specify a valid node that exists in the system.",
                rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("*******testOnDemandCrlDownload_NodeDoesNotExist_Failure Test End ******");

    }

    @Override
    public void testOnDemandCrlDownload_NodeNotInSynch_Failure() throws Exception {
        logger.info("*******testOnDemandCrlDownload_NodeNotInSynch_Failure Test Started ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNode(NODE_NAME_1, "UNSYNCHRONIZED", SecurityLevel.LEVEL_2);
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testOnDemandCrlDownload_NodeNotInSynch_Failure " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "crl download -n " + NODE_NAME_1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("On Demand CRL Download for NodeNotInSynch_Failure Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("NetworkElement=DUG22 10005 The node specified is not synchronized Please ensure the node specified is synchronized.",
                rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("*******testOnDemandCrlDownload_NodeNotInSynch_Failure Test End ******");

    }

    @Override
    public void testOnDemandCrlDownload_DuplicateNodes_Success() throws Exception {
        logger.info("*******testOnDemandCrlDownload_DuplicateNodes_Success Test Started ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCrlCheck(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testOnDemandCrlDownload_DuplicateNodes_Success " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "crl download -n " + NODE_NAME_1 + "," + NODE_NAME_1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("On Demand CRL Download for DuplicateNodes_Success Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Successfully started a job to download CRL on demand.", rowsAsListOfStrings));
        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("*******testOnDemandCrlDownload_DuplicateNodes_Success Test End ******");

    }

    @Override
    public void testOnDemandCrlDownload_WithWrongFileContent_Failure() throws Exception {
        logger.info("*******testOnDemandCrlDownload_WithWrongFileContent_Failure Test Started ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCrlCheck(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testOnDemandCrlDownload_WithWrongFileContent_Failure " + e.getMessage());
            e.printStackTrace();
        }

        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("file:", fileUtility.readResourceFile("EmptyCrlCheck.txt"));

        final Command command = new Command("secadm", OnDemand_CRL_DOWNLOAD, properties);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("On Demand CRL Download for WithWrongFileContent_Failure Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Error 10002 : The contents of the file provided are not in the correct format", rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("*******testOnDemandCrlDownload_WithWrongFileContent_Failure Test End ******");

    }

    @Override
    public void testOnDemandCrlDownload_InvalidNodesWithFile_Failure() throws Exception {
        logger.info("*******testOnDemandCrlDownload_InvalidNodesWithFile_Failure Test Started ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCrlCheck(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testOnDemandCrlDownload_InvalidNodesWithFile_Failure " + e.getMessage());
            e.printStackTrace();
        }

        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("file:", fileUtility.readResourceFile("InvalidNodesCrlCheck.txt"));

        final Command command = new Command("secadm", OnDemand_CRL_DOWNLOAD, properties);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("On Demand CRL Download for InvalidNodesWithFile_Failure Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("The node specified does not exist Please specify a valid node that exists in the system.", rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("*******testOnDemandCrlDownload_InvalidNodesWithFile_Failure Test End ******");

    }

    @Override
    public void testOnDemandCrlDownload_NodeDoesNotExist_PartialSuccess() throws Exception {
        logger.info("*******testOnDemandCrlDownload_NodeDoesNotExist_PartialSuccess Test Started ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCrlCheck(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testOnDemandCrlDownload_NodeDoesNotExist_PartialSuccess " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "crl download -n " + NODE_NAME_1 + ",LTE04dg2ERBS00012");
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("On Demand CRL Download for NodeDoesNotExist_PartialSuccess Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Successfully started a job to download CRL on demand for some node(s).", rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList("Invalid node details are given below :", rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList("NetworkElement=LTE04dg2ERBS00012 10004 The node specified does not exist Please specify a valid node that exists in the system.",
                rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("*******testOnDemandCrlDownload_NodeDoesNotExist_PartialSuccess Test End ******");

    }

    @Override
    public void testOnDemandCrlDownload_NodeNotInSynch_PartialSuccess() throws Exception {
        logger.info("*******testOnDemandCrlDownload_NodeNotInSynch_PartialSuccess Test Started ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCrlCheck(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCrlCheck("LTE04dg2ERBS00012", "UNSYNCHRONIZED", SecurityLevel.LEVEL_2, "1");
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testOnDemandCrlDownload_NodeNotInSynch_PartialSuccess " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "crl download -n " + NODE_NAME_1 + ",LTE04dg2ERBS00012");
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("On Demand CRL Download for NodeNotInSynch_PartialSuccess Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Successfully started a job to download CRL on demand for some node(s).", rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList("Invalid node details are given below :", rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList("NetworkElement=LTE04dg2ERBS00012 10005 The node specified is not synchronized Please ensure the node specified is synchronized.",
                rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("*******testOnDemandCrlDownload_NodeNotInSynch_PartialSuccess Test End ******");

    }

    @Override
    public void testOnDemandCrlDownload_FileWithMulitpleNodes_Success() throws Exception {
        logger.info("*******testOnDemandCrlDownload_FileWithMulitpleNodes_Success Test Started ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCrlCheck(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCrlCheck(NODE_NAME_2, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testOnDemandCrlDownload_FileWithMulitpleNodes_Success " + e.getMessage());
            e.printStackTrace();
        }

        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("file:", fileUtility.readResourceFile("MultipleNodesCrlCheck.txt"));

        final Command command = new Command("secadm", OnDemand_CRL_DOWNLOAD, properties);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("On Demand CRL Download for FileWithMulitpleNodes_Success Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Successfully started a job to download CRL on demand.", rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("*******testOnDemandCrlDownload_FileWithMulitpleNodes_Success Test End ******");

    }

    @Override
    public void testOnDemandCrlDownload_SingleNodeWithOutFile_Success() throws Exception {
        logger.info("*******testOnDemandCrlDownload_SingleNodeWithOutFile_Success Test Started ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCrlCheck(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node fortestOnDemandCrlDownload_SingleNodeWithOutFile_Success " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "crl download -n " + NODE_NAME_1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("On Demand CRL Download for SingleNodeWithOutFile_Success Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Successfully started a job to download CRL on demand.", rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("*******testOnDemandCrlDownload_SingleNodeWithOutFile_Success Test End ******");

    }

    @Override
    public void testOnDemandCrlDownload_MulitpleNodesWithOutFile_Success() throws Exception {
        logger.info("*******testOnDemandCrlDownload_MulitpleNodesWithOutFile_Success Test Started ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCrlCheck(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCrlCheck(NODE_NAME_2, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node fortestOnDemandCrlDownload_MulitpleNodesWithOutFile_Success " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "crl download -n " + NODE_NAME_1 + "," + NODE_NAME_2);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("On Demand CRL Download for MulitpleNodesWithOutFile_Success Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Successfully started a job to download CRL on demand.", rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("*******testOnDemandCrlDownload_MulitpleNodesWithOutFile_Success Test End ******");

    }
    
    @Override
    public void testOnDemandCrlDownload_WithMulitpleNodes_Failure() throws Exception {
        logger.info("******* testOnDemandCrlDownload_WithMulitpleNodes_Failure Test Started ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCrlCheck(NODE_NAME_1, "UNSYNCHRONIZED", SecurityLevel.LEVEL_2, "1");
            nodeSecurityRadioNodesDataSetup.createComEcimNode("LTE04dg2ERBS00012", "SYNCHRONIZED", SecurityLevel.LEVEL_2);
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testDisableCrlCheck_WithMulitpleNodes_Failure " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "crl download -n " + NODE_NAME_1 + ",LTE04dg2ERBS00011");
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("On Demand CRL Download for WithMulitpleNodes_Failure Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(
                "Failed to start the job to download CRL on demand as all the provided node(s) are invalid.", rowsAsListOfStrings));

        nodeSecurityRadioNodesDataSetup.deleteAllNodes();

        logger.info("******* testOnDemandCrlDownload_WithMulitpleNodes_Failure Test End ******");

    }

}
