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
import java.net.URLDecoder;
import java.util.*;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.FileUtility;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.ResponseDtoReader;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityCPPNodesDataSetup;
import com.ericsson.oss.services.scriptengine.spi.CommandHandler;
import com.ericsson.oss.services.scriptengine.spi.dtos.Command;
import com.ericsson.oss.services.scriptengine.spi.dtos.CommandResponseDto;

/**
 * This Class is for enable or disable crlCheck attribute on g1 nodes for OAM and IPSEC both / only OAM for MGW node
 *
 */
public class CPPCrlCheckEnableOrDisableTestImpl implements CPPCrlCheckEnableOrDisableTest {

    private static final String CRL_CHECK_ENABLE = "enable crlcheck -ct ALL -nf file:MultipleCPPNodesCrlCheck.txt";
    private static final String CRL_CHECK_DISABLE = "disable crlcheck -ct ALL -nf file:MultipleCPPNodesCrlCheck.txt";
    private static final String NODE_NAME_1 = "ERBS1";
    private static final String NETWORK_ELEMENT_1 = "ERBS1-NE";
    private static final String NODE_NAME_2 = "ERBS2";
    private static final String NETWORK_ELEMENT_2 = "ERBS2-NE";
    
    public static final String COMMAND_SECADM = "secadm";

    @Inject
    private CommandHandler commandHandler;
    
    @Inject
    private NodeSecurityCPPNodesDataSetup nodeSecurityNodesDataSetup;

    @Inject
    private FileUtility fileUtility;

    @Inject
    private Logger logger;

    ResponseDtoReader responseDtoReader = new ResponseDtoReader();

    @Override
    public void testEnableCrlCheck_CPPNodeDoesNotExist_Failure() throws Exception {
        logger.info("******* testEnableCrlCheck_CPPNodeDoesNotExist_Failure Test Started ******");
        try {        	
        	nodeSecurityNodesDataSetup.deleteAllNodes();            
        } catch (Exception e) {
            logger.error("Exception ocurred while deleting node for testEnableCrlCheck_CPPNodeDoesNotExist_Failure " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "enable crlcheck -ct ALL -n " + NETWORK_ELEMENT_1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("EnableCrlCheckOnNode for CPPNodeDoesNotExist_Failure Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("NetworkElement=ERBS1-NE 10004 The node specified does not exist Please specify a valid node that exists in the system.",
                rowsAsListOfStrings));

        nodeSecurityNodesDataSetup.deleteAllNodes();

        logger.info("******* testEnableCrlCheck_NodeDoesNotExist_Failure Test End ******");

    }
    
    @Override
    public void testEnableCrlCheck_SecurityDoesNotExist_Failure() throws Exception {
        logger.info("******* testEnableCrlCheck_SecurityDoesNotExist_Failure Test Started ******");

        try {
        	nodeSecurityNodesDataSetup.deleteAllNodes();
        	nodeSecurityNodesDataSetup.createCppNodeForCrlCheckWithoutSecurityMO(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2);
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testEnableCrlCheck_SecurityDoesNotExist_Failure " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "enable crlcheck -ct ALL -n " + NETWORK_ELEMENT_1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("EnableCrlCheckOnNode for SecurityDoesNotExist_Failure Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(
                "10103 Security MO does not exist for the given node. Issue certificate to the node for Security MO to be present.", rowsAsListOfStrings));

        nodeSecurityNodesDataSetup.deleteAllNodes();

        logger.info("******* testEnableCrlCheck_SecurityDoesNotExist_Failure Test End ******");

    }

    @Override
    public void testEnableCrlCheck_CPPInvalidCertificateType_Failure() throws Exception {
        logger.info("******* testEnableCrlCheck_CPPInvalidCertificateType_Failure Test Started ******");

        try {  
        	nodeSecurityNodesDataSetup.deleteAllNodes();
        	nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck(NODE_NAME_1, ModelDefinition.CmFunction.SyncStatusValue.SYNCHRONIZED.name(),SecurityLevel.LEVEL_2);
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testEnableCrlCheck_CPPInvalidCertificateType_Failure " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "enable crlcheck -ct OAM123 -n " + NETWORK_ELEMENT_1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("EnableCrlCheckOnNode for CPPInvalidCertificateType_Failure Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Accepted arguments are [IPSEC, OAM, ALL]", rowsAsListOfStrings));

        nodeSecurityNodesDataSetup.deleteAllNodes();

        logger.info("******* testEnableCrlCheck_CPPInvalidCertificateType Test End ******");
    }
    
    @Override
    public void testEnableCrlCheck_CPPNodeNotInSynch_Failure() throws Exception {
        logger.info("******* testEnableCrlCheck_CPPNodeNotInSynch_Failure Test Started ******");

        try {
            nodeSecurityNodesDataSetup.deleteAllNodes();
            nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck(NODE_NAME_1, "UNSYNCHRONIZED", SecurityLevel.LEVEL_2);
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testEnableCrlCheck_CPPNodeNotInSynch_Failure " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "enable crlcheck -ct ALL -n " + NETWORK_ELEMENT_1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("EnableCrlCheckOnNode for NodeNotInSynch_Failure Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("10005 The node specified is not synchronized Please ensure the node specified is synchronized.",
                rowsAsListOfStrings));

        nodeSecurityNodesDataSetup.deleteAllNodes();

        logger.info("******* testEnableCrlCheck_CPPNodeNotInSynch_Failure Test End ******");

    }

    @Override
    public void testEnableCrlCheck_CPPDuplicateNodes_Failure() throws Exception {
        logger.info("******* testEnableCrlCheck_CPPDuplicateNodes_Failure Test Started ******");

        try {
            nodeSecurityNodesDataSetup.deleteAllNodes();
            nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2);
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testEnableCrlCheck_DuplicateNodes_Failure " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "enable crlcheck -ct ALL -n " + NETWORK_ELEMENT_1 + "," + NETWORK_ELEMENT_1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("EnableCrlCheckOnNode for CPPDuplicateNodes_Failure Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Successfully started a job for CRL Check enable operation.", rowsAsListOfStrings));

        nodeSecurityNodesDataSetup.deleteAllNodes();

        logger.info("******* testEnableCrlCheck_CPPDuplicateNodes_Failure Test End ******");

    }

    @Override
    public void testEnableCrlCheck_CPPWithWrongFileContent_Failure() throws Exception {
        logger.info("******* testEnableCrlCheck_WithWrongFileContent_Failure Test Started ******");

        try {
        	nodeSecurityNodesDataSetup.deleteAllNodes();
        	nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck(NODE_NAME_1, ModelDefinition.CmFunction.SyncStatusValue.SYNCHRONIZED.name(),SecurityLevel.LEVEL_2);
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testEnableCrlCheck_WithWrongFileContent_Failure " + e.getMessage());
            e.printStackTrace();
        }

//        final String configFilePath = getTargetPath() + File.separator + "crlcheck" + File.separator + "EmptyCrlCheck.txt";
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

        nodeSecurityNodesDataSetup.deleteAllNodes();

        logger.info("******* testEnableCrlCheck_WithWrongFileContent_Failure Test End ******");

    }

    @Override
    public void testEnableCrlCheck_CPPInvalidNodesWithFile_Failure() throws Exception {
        logger.info("******* testEnableCrlCheck_InvalidNodesWithFile_Failure Test Started ******");

        try {        	
        	nodeSecurityNodesDataSetup.deleteAllNodes();
        	nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck(NODE_NAME_1, ModelDefinition.CmFunction.SyncStatusValue.SYNCHRONIZED.name(),SecurityLevel.LEVEL_2);
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

        nodeSecurityNodesDataSetup.deleteAllNodes();

        logger.info("******* testEnableCrlCheck_InvalidNodesWithFile_Failure Test End ******");

    }   

    @Override
    public void testEnableCrlCheck_WithMulitpleCPPNodes_Failure() throws Exception {
        logger.info("******* testEnableCrlCheck_WithMulitpleCPPNodes_Failure Test Started ******");

        try {
            nodeSecurityNodesDataSetup.deleteAllNodes();
            nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck(NODE_NAME_1, "UNSYNCHRONIZED", SecurityLevel.LEVEL_2);
            nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck("LTE04dg2ERBS00012", "SYNCHRONIZED", SecurityLevel.LEVEL_2);
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testEnableCrlCheck_WithMulitpleCPPNodes_Failure " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "enable crlcheck -ct ALL -n " + NETWORK_ELEMENT_1 + ",LTE04dg2ERBS00011,LTE04dg2ERBS00012");
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("EnableCrlCheckOnNode for WithMulitpleCPPNodes_Failure Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Failed to start the job for CRL Check enable operation as all the provided node(s) are invalid. Invalid node details are given below :",
                rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList(
                "ERBS1-NE 10005 The node specified is not synchronized Please ensure the node specified is synchronized.", rowsAsListOfStrings));

        nodeSecurityNodesDataSetup.deleteAllNodes();

        logger.info("******* testEnableCrlCheck_WithMulitpleCPPNodes_Failure Test End ******");

    }
    
    @Override
    public void testEnableCrlCheck_CPPNodeDoesNotExist_PartialSuccess() throws Exception {
        logger.info("******* testEnableCrlCheck_CPPNodeDoesNotExist_PartialSuccess Test Started ******");

        try {
            nodeSecurityNodesDataSetup.deleteAllNodes();
            nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2);
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testEnableCrlCheck_CPPNodeDoesNotExist_PartialSuccess " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "enable crlcheck -ct ALL -n " + NETWORK_ELEMENT_1 + ",LTE04dg2ERBS00012");
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("EnableCrlCheckOnNode for CPPNodeDoesNotExist_PartialSuccess Response :" + row);
            }
        }

        assertTrue(responseDtoReader
                .messageIsContainedInList("Successfully started a job for CRL Check enable operation for some node(s).", rowsAsListOfStrings));
        assertTrue(responseDtoReader
                .messageIsContainedInList("Invalid node details are given below :", rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList("NetworkElement=LTE04dg2ERBS00012 10004 The node specified does not exist Please specify a valid node that exists in the system.",
                rowsAsListOfStrings));

        nodeSecurityNodesDataSetup.deleteAllNodes();

        logger.info("******* testEnableCrlCheck_CPPNodeDoesNotExist_PartialSuccess Test End ******");

    }

    @Override
    public void testEnableCrlCheck_CPPNodeNotInSynch_PartialSuccess() throws Exception {
        logger.info("******* testEnableCrlCheck_CPPNodeNotInSynch_PartialSuccess Test Started ******");

        try {
            nodeSecurityNodesDataSetup.deleteAllNodes();
            nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2);
            nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck("LTE04dg2ERBS00012", "UNSYNCHRONIZED", SecurityLevel.LEVEL_2);
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testEnableCrlCheck_CPPNodeNotInSynch_PartialSuccess " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "enable crlcheck -ct ALL -n " + NETWORK_ELEMENT_1 + ",LTE04dg2ERBS00012-NE");
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("EnableCrlCheckOnNode for CPPNodeNotInSynch_PartialSuccess Response :" + row);
            }
        }

        assertTrue(responseDtoReader
                .messageIsContainedInList("Successfully started a job for CRL Check enable operation for some node(s).", rowsAsListOfStrings));
        assertTrue(responseDtoReader
                .messageIsContainedInList("Invalid node details are given below :", rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList("NetworkElement=LTE04dg2ERBS00012-NE 10005 The node specified is not synchronized Please ensure the node specified is synchronized.",
                rowsAsListOfStrings));

        nodeSecurityNodesDataSetup.deleteAllNodes();

        logger.info("******* testEnableCrlCheck_CPPNodeNotInSynch_PartialSuccess Test End ******");

    }

    @Override
    public void testEnableCrlCheck_FileWithMulitpleCPPNodes_Success() throws Exception {
        logger.info("******* testEnableCrlCheck_FileWithMulitpleCPPNodes_Success Test Started ******");

        try {
            nodeSecurityNodesDataSetup.deleteAllNodes();
            nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2);
            nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck(NODE_NAME_2, "SYNCHRONIZED", SecurityLevel.LEVEL_2);
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testEnableCrlCheck_FileWithMulitpleCPPNodes_Success " + e.getMessage());
            e.printStackTrace();
        }

        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("file:", fileUtility.readResourceFile("MultipleCPPNodesCrlCheck.txt"));

        final Command command = new Command("secadm", CRL_CHECK_ENABLE, properties);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("EnableCrlCheckOnNode for FileWithMulitpleCPPNodes_Success Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Successfully started a job for CRL Check enable operation.", rowsAsListOfStrings));

        nodeSecurityNodesDataSetup.deleteAllNodes();

        logger.info("******* testEnableCrlCheck_FileWithMulitpleCPPNodes_Success Test End ******");

    }

    @Override
    public void testEnableCrlCheck_SingleCPPNodeWithOutFile_Success() throws Exception {
        logger.info("******* testEnableCrlCheck_SingleCPPNodeWithOutFile_Success Test Started ******");

        try {
            nodeSecurityNodesDataSetup.deleteAllNodes();
            nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2);
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testEnableCrlCheck_SingleCPPNodeWithOutFile_Success " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "enable crlcheck -ct ALL -n " + NETWORK_ELEMENT_1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("EnableCrlCheckOnNode for SingleCPPNodeWithOutFile_Success Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Successfully started a job for CRL Check enable operation.", rowsAsListOfStrings));

        nodeSecurityNodesDataSetup.deleteAllNodes();

        logger.info("******* testEnableCrlCheck_SingleCPPNodeWithOutFile_Success Test End ******");

    }

    @Override
    public void testEnableCrlCheck_MulitpleCPPNodesWithOutFile_Success() throws Exception {
        logger.info("******* testEnableCrlCheck_MulitpleCPPNodesWithOutFile_Success Test Started ******");

        try {
            nodeSecurityNodesDataSetup.deleteAllNodes();
            nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2);
            nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck(NODE_NAME_2, "SYNCHRONIZED", SecurityLevel.LEVEL_2);
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testEnableCrlCheck_MulitpleCPPNodesWithOutFile_Success " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "enable crlcheck -ct ALL -n " + NETWORK_ELEMENT_1 + "," + NETWORK_ELEMENT_2);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("EnableCrlCheckOnNode for MulitpleCPPNodesWithOutFile_Success Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Successfully started a job for CRL Check enable operation.", rowsAsListOfStrings));

        nodeSecurityNodesDataSetup.deleteAllNodes();

        logger.info("******* testEnableCrlCheck_MulitpleCPPNodesWithOutFile_Success Test End ******");

    }
    
    @Override
    public void testDisableCrlCheck_CPPNodeDoesNotExist_Failure() throws Exception {
        logger.info("******* testDisableCrlCheck_CPPNodeDoesNotExist_Failure Test Started ******");
        try {        	
        	nodeSecurityNodesDataSetup.deleteAllNodes();            
        } catch (Exception e) {
            logger.error("Exception ocurred while deleting node for testDisableCrlCheck_CPPNodeDoesNotExist_Failure " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "disable crlcheck -ct ALL -n " + NETWORK_ELEMENT_1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("DisableCrlCheckOnNode for CPPNodeDoesNotExist_Failure Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("NetworkElement=ERBS1-NE 10004 The node specified does not exist Please specify a valid node that exists in the system.",
                rowsAsListOfStrings));

        nodeSecurityNodesDataSetup.deleteAllNodes();

        logger.info("******* testDisableCrlCheck_NodeDoesNotExist_Failure Test End ******");

    }
    
    @Override
    public void testDisableCrlCheck_SecurityDoesNotExist_Failure() throws Exception {
        logger.info("******* testDisableCrlCheck_SecurityDoesNotExist_Failure Test Started ******");

        try {
        	nodeSecurityNodesDataSetup.deleteAllNodes();
        	nodeSecurityNodesDataSetup.createCppNodeForCrlCheckWithoutSecurityMO(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2);
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testDisableCrlCheck_SecurityDoesNotExist_Failure " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "disable crlcheck -ct ALL -n " + NETWORK_ELEMENT_1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("DisableCrlCheckOnNode for SecurityDoesNotExist_Failure Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList(
                "10103 Security MO does not exist for the given node. Issue certificate to the node for Security MO to be present.", rowsAsListOfStrings));

        nodeSecurityNodesDataSetup.deleteAllNodes();

        logger.info("******* testDisableCrlCheck_SecurityDoesNotExist_Failure Test End ******");

    }

    @Override
    public void testDisableCrlCheck_CPPInvalidCertificateType_Failure() throws Exception {
        logger.info("******* testDisableCrlCheck_CPPInvalidCertificateType_Failure Test Started ******");

        try {  
        	nodeSecurityNodesDataSetup.deleteAllNodes();
        	nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck(NODE_NAME_1, ModelDefinition.CmFunction.SyncStatusValue.SYNCHRONIZED.name(),SecurityLevel.LEVEL_2);
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testDisableCrlCheck_CPPInvalidCertificateType_Failure " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "disable crlcheck -ct OAM123 -n " + NETWORK_ELEMENT_1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("DisableCrlCheckOnNode for CPPInvalidCertificateType_Failure Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Accepted arguments are [IPSEC, OAM, ALL]", rowsAsListOfStrings));

        nodeSecurityNodesDataSetup.deleteAllNodes();

        logger.info("******* testDisableCrlCheck_CPPInvalidCertificateType Test End ******");
    }
    
    @Override
    public void testDisableCrlCheck_CPPNodeNotInSynch_Failure() throws Exception {
        logger.info("******* testDisableCrlCheck_CPPNodeNotInSynch_Failure Test Started ******");

        try {
            nodeSecurityNodesDataSetup.deleteAllNodes();
            nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck(NODE_NAME_1, "UNSYNCHRONIZED", SecurityLevel.LEVEL_2);
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testDisableCrlCheck_CPPNodeNotInSynch_Failure " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "disable crlcheck -ct ALL -n " + NETWORK_ELEMENT_1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("DisableCrlCheckOnNode for NodeNotInSynch_Failure Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("10005 The node specified is not synchronized Please ensure the node specified is synchronized.",
                rowsAsListOfStrings));

        nodeSecurityNodesDataSetup.deleteAllNodes();

        logger.info("******* testDisableCrlCheck_CPPNodeNotInSynch_Failure Test End ******");

    }

    @Override
    public void testDisableCrlCheck_CPPDuplicateNodes_Failure() throws Exception {
        logger.info("******* testDisableCrlCheck_CPPDuplicateNodes_Failure Test Started ******");

        try {
            nodeSecurityNodesDataSetup.deleteAllNodes();
            nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2);
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testDisableCrlCheck_DuplicateNodes_Failure " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "disable crlcheck -ct ALL -n " + NETWORK_ELEMENT_1 + "," + NETWORK_ELEMENT_1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("DisableCrlCheckOnNode for CPPDuplicateNodes_Failure Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Successfully started a job for CRL Check disable operation.", rowsAsListOfStrings));

        nodeSecurityNodesDataSetup.deleteAllNodes();

        logger.info("******* testDisableCrlCheck_CPPDuplicateNodes_Failure Test End ******");

    }

    @Override
    public void testDisableCrlCheck_CPPWithWrongFileContent_Failure() throws Exception {
        logger.info("******* testDisableCrlCheck_WithWrongFileContent_Failure Test Started ******");

        try {
        	nodeSecurityNodesDataSetup.deleteAllNodes();
        	nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck(NODE_NAME_1, ModelDefinition.CmFunction.SyncStatusValue.SYNCHRONIZED.name(),SecurityLevel.LEVEL_2);
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testDisableCrlCheck_WithWrongFileContent_Failure " + e.getMessage());
            e.printStackTrace();
        }

//        final String configFilePath = getTargetPath() + File.separator + "crlcheck" + File.separator + "EmptyCrlCheck.txt";
        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("file:", fileUtility.readResourceFile("EmptyCrlCheck.txt"));

        final Command command = new Command("secadm", CRL_CHECK_DISABLE, properties);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("DisableCrlCheckOnNode for WithWrongFileContent_Failure Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Error 10002 : The contents of the file provided are not in the correct format", rowsAsListOfStrings));

        nodeSecurityNodesDataSetup.deleteAllNodes();

        logger.info("******* testDisableCrlCheck_WithWrongFileContent_Failure Test End ******");

    }

    @Override
    public void testDisableCrlCheck_CPPInvalidNodesWithFile_Failure() throws Exception {
        logger.info("******* testDisableCrlCheck_InvalidNodesWithFile_Failure Test Started ******");

        try {        	
        	nodeSecurityNodesDataSetup.deleteAllNodes();
        	nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck(NODE_NAME_1, ModelDefinition.CmFunction.SyncStatusValue.SYNCHRONIZED.name(),SecurityLevel.LEVEL_2);
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

        nodeSecurityNodesDataSetup.deleteAllNodes();

        logger.info("******* testDisableCrlCheck_InvalidNodesWithFile_Failure Test End ******");

    }   

    @Override
    public void testDisableCrlCheck_WithMulitpleCPPNodes_Failure() throws Exception {
        logger.info("******* testDisableCrlCheck_WithMulitpleCPPNodes_Failure Test Started ******");

        try {
            nodeSecurityNodesDataSetup.deleteAllNodes();
            nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck(NODE_NAME_1, "UNSYNCHRONIZED", SecurityLevel.LEVEL_2);
            nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck("LTE04dg2ERBS00012", "SYNCHRONIZED", SecurityLevel.LEVEL_2);
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testDisableCrlCheck_WithMulitpleCPPNodes_Failure " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "disable crlcheck -ct ALL -n " + NETWORK_ELEMENT_1 + ",LTE04dg2ERBS00011,LTE04dg2ERBS00012");
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("DisableCrlCheckOnNode for WithMulitpleCPPNodes_Failure Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Failed to start the job for CRL Check disable operation as all the provided node(s) are invalid. Invalid node details are given below :",
                rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList(
                "ERBS1-NE 10005 The node specified is not synchronized Please ensure the node specified is synchronized.", rowsAsListOfStrings));

        nodeSecurityNodesDataSetup.deleteAllNodes();

        logger.info("******* testDisableCrlCheck_WithMulitpleCPPNodes_Failure Test End ******");

    }
    
    @Override
    public void testDisableCrlCheck_CPPNodeDoesNotExist_PartialSuccess() throws Exception {
        logger.info("******* testDisableCrlCheck_CPPNodeDoesNotExist_PartialSuccess Test Started ******");

        try {
            nodeSecurityNodesDataSetup.deleteAllNodes();
            nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2);
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testDisableCrlCheck_CPPNodeDoesNotExist_PartialSuccess " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "disable crlcheck -ct ALL -n " + NETWORK_ELEMENT_1 + ",LTE04dg2ERBS00012");
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("DisableCrlCheckOnNode for CPPNodeDoesNotExist_PartialSuccess Response :" + row);
            }
        }

        assertTrue(responseDtoReader
                .messageIsContainedInList("Successfully started a job for CRL Check disable operation for some node(s).", rowsAsListOfStrings));
        assertTrue(responseDtoReader
                .messageIsContainedInList("Invalid node details are given below :", rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList("NetworkElement=LTE04dg2ERBS00012 10004 The node specified does not exist Please specify a valid node that exists in the system.",
                rowsAsListOfStrings));

        nodeSecurityNodesDataSetup.deleteAllNodes();

        logger.info("******* testDisableCrlCheck_CPPNodeDoesNotExist_PartialSuccess Test End ******");

    }

    @Override
    public void testDisableCrlCheck_CPPNodeNotInSynch_PartialSuccess() throws Exception {
        logger.info("******* testDisableCrlCheck_CPPNodeNotInSynch_PartialSuccess Test Started ******");

        try {
            nodeSecurityNodesDataSetup.deleteAllNodes();
            nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2);
            nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck("LTE04dg2ERBS00012", "UNSYNCHRONIZED", SecurityLevel.LEVEL_2);
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testDisableCrlCheck_CPPNodeNotInSynch_PartialSuccess " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "disable crlcheck -ct ALL -n " + NETWORK_ELEMENT_1 + ",LTE04dg2ERBS00012-NE");
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("DisableCrlCheckOnNode for CPPNodeNotInSynch_PartialSuccess Response :" + row);
            }
        }

        assertTrue(responseDtoReader
                .messageIsContainedInList("Successfully started a job for CRL Check disable operation for some node(s).", rowsAsListOfStrings));
        assertTrue(responseDtoReader
                .messageIsContainedInList("Invalid node details are given below :", rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList("NetworkElement=LTE04dg2ERBS00012-NE 10005 The node specified is not synchronized Please ensure the node specified is synchronized.",
                rowsAsListOfStrings));

        nodeSecurityNodesDataSetup.deleteAllNodes();

        logger.info("******* testDisableCrlCheck_CPPNodeNotInSynch_PartialSuccess Test End ******");

    }

    @Override
    public void testDisableCrlCheck_FileWithMulitpleCPPNodes_Success() throws Exception {
        logger.info("******* testDisableCrlCheck_FileWithMulitpleCPPNodes_Success Test Started ******");

        try {
            nodeSecurityNodesDataSetup.deleteAllNodes();
            nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2);
            nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck(NODE_NAME_2, "SYNCHRONIZED", SecurityLevel.LEVEL_2);
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testDisableCrlCheck_FileWithMulitpleCPPNodes_Success " + e.getMessage());
            e.printStackTrace();
        }

        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("file:", fileUtility.readResourceFile("MultipleCPPNodesCrlCheck.txt"));

        final Command command = new Command("secadm", CRL_CHECK_DISABLE, properties);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("DisableCrlCheckOnNode for FileWithMulitpleCPPNodes_Success Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Successfully started a job for CRL Check disable operation.", rowsAsListOfStrings));

        nodeSecurityNodesDataSetup.deleteAllNodes();

        logger.info("******* testDisableCrlCheck_FileWithMulitpleCPPNodes_Success Test End ******");

    }

    @Override
    public void testDisableCrlCheck_SingleCPPNodeWithOutFile_Success() throws Exception {
        logger.info("******* testDisableCrlCheck_SingleCPPNodeWithOutFile_Success Test Started ******");

        try {
            nodeSecurityNodesDataSetup.deleteAllNodes();
            nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2);
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testDisableCrlCheck_SingleCPPNodeWithOutFile_Success " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "disable crlcheck -ct ALL -n " + NETWORK_ELEMENT_1);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("DisableCrlCheckOnNode for SingleCPPNodeWithOutFile_Success Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Successfully started a job for CRL Check disable operation.", rowsAsListOfStrings));

        nodeSecurityNodesDataSetup.deleteAllNodes();

        logger.info("******* testDisableCrlCheck_SingleCPPNodeWithOutFile_Success Test End ******");

    }

    @Override
    public void testDisableCrlCheck_MulitpleCPPNodesWithOutFile_Success() throws Exception {
        logger.info("******* testDisableCrlCheck_MulitpleCPPNodesWithOutFile_Success Test Started ******");

        try {
            nodeSecurityNodesDataSetup.deleteAllNodes();
            nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck(NODE_NAME_1, "SYNCHRONIZED", SecurityLevel.LEVEL_2);
            nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck(NODE_NAME_2, "SYNCHRONIZED", SecurityLevel.LEVEL_2);
        } catch (Exception e) {
            logger.info("Exception ocurred while creating node for testDisableCrlCheck_MulitpleCPPNodesWithOutFile_Success " + e.getMessage());
            e.printStackTrace();
        }

        final Command command = new Command("secadm", "disable crlcheck -ct ALL -n " + NETWORK_ELEMENT_1 + "," + NETWORK_ELEMENT_2);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("DisableCrlCheckOnNode for MulitpleCPPNodesWithOutFile_Success Response :" + row);
            }
        }

        assertTrue(responseDtoReader.messageIsContainedInList("Successfully started a job for CRL Check disable operation.", rowsAsListOfStrings));

        nodeSecurityNodesDataSetup.deleteAllNodes();

        logger.info("******* testDisableCrlCheck_MulitpleCPPNodesWithOutFile_Success Test End ******");

    }
    
    private String getTargetPath() throws UnsupportedEncodingException {
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
            throw new UnsupportedEncodingException(unsupportedEncodingException.getMessage());
        }
        return responsePath;
    }
    
}
