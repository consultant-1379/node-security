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

import static org.junit.Assert.assertTrue;

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

public class CppOnDemandCrlDownloadTestImpl implements CppOnDemandCrlDownloadTest {

	private static final String CRL_DOWNLOAD = "crl download -nf file:MultipleCPPNodesCrlCheck.txt";
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
	public void testDownloadCrl_CPPNodeDoesNotExist_Failure() throws Exception {
		logger.info("******* testDownloadCrl_CPPNodeDoesNotExist_Failure Test Started ******");
		try {
			nodeSecurityNodesDataSetup.deleteAllNodes();
		} catch (Exception e) {
			logger.error("Exception ocurred while deleting node for testDownloadCrl_CPPNodeDoesNotExist_Failure "
					+ e.getMessage());
			e.printStackTrace();

			throw new Exception();
		}

		final Command command = new Command("secadm", "crl download -n "
				+ NETWORK_ELEMENT_1);
		final CommandResponseDto commandResponseDto = commandHandler
				.execute(command);

		final List<String> rowsAsListOfStrings = responseDtoReader
				.extractListOfRowsFromCommandResponseDto(commandResponseDto);
		for (final String row : rowsAsListOfStrings) {
			if (row != null) {
				logger.info("DownloadCrlOnNode for CPPNodeDoesNotExist_Failure Response :"
						+ row);
			}
		}

		assertTrue(responseDtoReader
				.messageIsContainedInList(
						"NetworkElement=ERBS1-NE 10004 The node specified does not exist Please specify a valid node that exists in the system.",
						rowsAsListOfStrings));

		nodeSecurityNodesDataSetup.deleteAllNodes();

		logger.info("******* testDownloadCrl_NodeDoesNotExist_Failure Test End ******");

	}

	@Override
	public void testDownloadCrl_SecurityDoesNotExist_Failure() throws Exception {
		logger.info("******* testDownloadCrl_SecurityDoesNotExist_Failure Test Started ******");

		try {
			nodeSecurityNodesDataSetup.deleteAllNodes();
			nodeSecurityNodesDataSetup
					.createCppNodeForCrlCheckWithoutSecurityMO(NODE_NAME_1,
							"SYNCHRONIZED", SecurityLevel.LEVEL_2);
		} catch (Exception e) {
			logger.info("Exception ocurred while creating node for testDownloadCrl_SecurityDoesNotExist_Failure "
					+ e.getMessage());
			e.printStackTrace();
			throw new Exception();
		}

		final Command command = new Command("secadm", "crl download -n "
				+ NETWORK_ELEMENT_1);
		final CommandResponseDto commandResponseDto = commandHandler
				.execute(command);

		final List<String> rowsAsListOfStrings = responseDtoReader
				.extractListOfRowsFromCommandResponseDto(commandResponseDto);
		for (final String row : rowsAsListOfStrings) {
			if (row != null) {
				logger.info("DownloadCrlOnNode for SecurityDoesNotExist_Failure Response :"
						+ row);
			}
		}

		assertTrue(responseDtoReader
				.messageIsContainedInList(
						"10103 Security MO does not exist for the given node. Issue certificate to the node for Security MO to be present.",
						rowsAsListOfStrings));

		nodeSecurityNodesDataSetup.deleteAllNodes();

		logger.info("******* testDownloadCrl_SecurityDoesNotExist_Failure Test End ******");

	}

	@Override
	public void testDownloadCrl_CPPNodeNotInSynch_Failure() throws Exception {
		logger.info("******* testDownloadCrl_CPPNodeNotInSynch_Failure Test Started ******");

		try {
			nodeSecurityNodesDataSetup.deleteAllNodes();
			nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck(NODE_NAME_1,
					"UNSYNCHRONIZED", SecurityLevel.LEVEL_2);
		} catch (Exception e) {
			logger.info("Exception ocurred while creating node for testDownloadCrl_CPPNodeNotInSynch_Failure "
					+ e.getMessage());
			e.printStackTrace();
			throw new Exception();
		}

		final Command command = new Command("secadm", "crl download -n "
				+ NETWORK_ELEMENT_1);
		final CommandResponseDto commandResponseDto = commandHandler
				.execute(command);

		final List<String> rowsAsListOfStrings = responseDtoReader
				.extractListOfRowsFromCommandResponseDto(commandResponseDto);
		for (final String row : rowsAsListOfStrings) {
			if (row != null) {
				logger.info("DownloadCrlOnNode for NodeNotInSynch_Failure Response :"
						+ row);
			}
		}

		assertTrue(responseDtoReader
				.messageIsContainedInList(
						"10005 The node specified is not synchronized Please ensure the node specified is synchronized.",
						rowsAsListOfStrings));

		nodeSecurityNodesDataSetup.deleteAllNodes();

		logger.info("******* testDownloadCrl_CPPNodeNotInSynch_Failure Test End ******");

	}

	@Override
	public void testDownloadCrl_WithWrongFileContent_Failure() throws Exception {
		logger.info("******* testDownloadCrl_WithWrongFileContent_Failure Test Started ******");

		try {
			nodeSecurityNodesDataSetup.deleteAllNodes();
			nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck(NODE_NAME_1,
					ModelDefinition.CmFunction.SyncStatusValue.SYNCHRONIZED
							.name(), SecurityLevel.LEVEL_2);
		} catch (Exception e) {
			logger.info("Exception ocurred while creating node for testDownloadCrl_WithWrongFileContent_Failure "
					+ e.getMessage());
			e.printStackTrace();
			throw new Exception();
		}

		final Map<String, Object> properties = new HashMap<String, Object>();
                properties.put("file:", fileUtility.readResourceFile("EmptyCrlCheck.txt"));

		final Command command = new Command("secadm", CRL_DOWNLOAD, properties);
		final CommandResponseDto commandResponseDto = commandHandler
				.execute(command);

		final List<String> rowsAsListOfStrings = responseDtoReader
				.extractListOfRowsFromCommandResponseDto(commandResponseDto);
		for (final String row : rowsAsListOfStrings) {
			if (row != null) {
				logger.info("DownloadCrlOnNode for WithWrongFileContent_Failure Response :"
						+ row);
			}
		}

		assertTrue(responseDtoReader
				.messageIsContainedInList(
						"Error 10002 : The contents of the file provided are not in the correct format",
						rowsAsListOfStrings));

		nodeSecurityNodesDataSetup.deleteAllNodes();

		logger.info("******* testDownloadCrl_WithWrongFileContent_Failure Test End ******");

	}

	@Override
	public void testDownloadCrl_CPPInvalidNodesWithFile_Failure()
			throws Exception {
		logger.info("******* testDownloadCrl_InvalidNodesWithFile_Failure Test Started ******");

		try {
			nodeSecurityNodesDataSetup.deleteAllNodes();
			nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck(NODE_NAME_1,
					ModelDefinition.CmFunction.SyncStatusValue.SYNCHRONIZED
							.name(), SecurityLevel.LEVEL_2);
		} catch (Exception e) {
			logger.info("Exception ocurred while creating node for testDownloadCrl_InvalidNodesWithFile_Failure "
					+ e.getMessage());
			e.printStackTrace();
			throw new Exception();
		}

		final Map<String, Object> properties = new HashMap<String, Object>();
                properties.put("file:", fileUtility.readResourceFile("InvalidNodesCrlCheck.txt"));

		final Command command = new Command("secadm", CRL_DOWNLOAD, properties);
		final CommandResponseDto commandResponseDto = commandHandler
				.execute(command);

		final List<String> rowsAsListOfStrings = responseDtoReader
				.extractListOfRowsFromCommandResponseDto(commandResponseDto);
		for (final String row : rowsAsListOfStrings) {
			if (row != null) {
				logger.info("DownloadCrlOnNode for InvalidNodesWithFile_Failure Response :"
						+ row);
			}
		}

		assertTrue(responseDtoReader
				.messageIsContainedInList(
						"The node specified does not exist Please specify a valid node that exists in the system.",
						rowsAsListOfStrings));

		nodeSecurityNodesDataSetup.deleteAllNodes();

		logger.info("******* testDownloadCrl_InvalidNodesWithFile_Failure Test End ******");

	}

	@Override
	public void testDownloadCrl_WithMulitpleCPPNodes_Failure() throws Exception {
		logger.info("******* testDownloadCrl_WithMulitpleCPPNodes_Failure Test Started ******");

		try {
			nodeSecurityNodesDataSetup.deleteAllNodes();
			nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck(NODE_NAME_1,
					"UNSYNCHRONIZED", SecurityLevel.LEVEL_2);
			nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck(
					"LTE04dg2ERBS00012", "SYNCHRONIZED", SecurityLevel.LEVEL_2);
		} catch (Exception e) {
			logger.info("Exception ocurred while creating node for testDownloadCrl_WithMulitpleCPPNodes_Failure "
					+ e.getMessage());
			e.printStackTrace();
			throw new Exception();
		}

		final Command command = new Command("secadm", "crl download -n "
				+ NETWORK_ELEMENT_1 + ",LTE04dg2ERBS00011,LTE04dg2ERBS00012");
		final CommandResponseDto commandResponseDto = commandHandler
				.execute(command);

		final List<String> rowsAsListOfStrings = responseDtoReader
				.extractListOfRowsFromCommandResponseDto(commandResponseDto);
		for (final String row : rowsAsListOfStrings) {
			if (row != null) {
				logger.info("DownloadCrlOnNode for WithMulitpleCPPNodes_Failure Response :"
						+ row);
			}
		}

		assertTrue(responseDtoReader
				.messageIsContainedInList(
						"Failed to start the job to download CRL on demand as all the provided node(s) are invalid.",
						rowsAsListOfStrings));
		assertTrue(responseDtoReader
				.messageIsContainedInList(
						"NetworkElement=ERBS1-NE 10005 The node specified is not synchronized Please ensure the node specified is synchronized.",
						rowsAsListOfStrings));

		nodeSecurityNodesDataSetup.deleteAllNodes();

		logger.info("******* testDownloadCrl_WithMulitpleCPPNodes_Failure Test End ******");

	}

	@Override
	public void testDownloadCrl_CPPNodeDoesNotExist_PartialSuccess()
			throws Exception {
		logger.info("******* testDownloadCrl_CPPNodeDoesNotExist_PartialSuccess Test Started ******");

		try {
			nodeSecurityNodesDataSetup.deleteAllNodes();
			nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck(NODE_NAME_1,
					"SYNCHRONIZED", SecurityLevel.LEVEL_2);
		} catch (Exception e) {
			logger.info("Exception ocurred while creating node for testDownloadCrl_CPPNodeDoesNotExist_PartialSuccess "
					+ e.getMessage());
			e.printStackTrace();
			throw new Exception();
		}

		final Command command = new Command("secadm", "crl download -n "
				+ NETWORK_ELEMENT_1 + ",LTE04dg2ERBS00012");
		final CommandResponseDto commandResponseDto = commandHandler
				.execute(command);

		final List<String> rowsAsListOfStrings = responseDtoReader
				.extractListOfRowsFromCommandResponseDto(commandResponseDto);
		for (final String row : rowsAsListOfStrings) {
			if (row != null) {
				logger.info("DownloadCrlOnNode for CPPNodeDoesNotExist_PartialSuccess Response :"
						+ row);
			}
		}

		assertTrue(responseDtoReader
				.messageIsContainedInList(
						"Successfully started a job to download CRL on demand for some node(s).",
						rowsAsListOfStrings));
		assertTrue(responseDtoReader
				.messageIsContainedInList(
						"NetworkElement=LTE04dg2ERBS00012 10004 The node specified does not exist Please specify a valid node that exists in the system.",
						rowsAsListOfStrings));

		nodeSecurityNodesDataSetup.deleteAllNodes();

		logger.info("******* testDownloadCrl_CPPNodeDoesNotExist_PartialSuccess Test End ******");

	}

	@Override
	public void testDownloadCrl_CPPNodeNotInSynch_PartialSuccess()
			throws Exception {
		logger.info("******* testDownloadCrl_CPPNodeNotInSynch_PartialSuccess Test Started ******");

		try {
			nodeSecurityNodesDataSetup.deleteAllNodes();
			nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck(NODE_NAME_1,
					"SYNCHRONIZED", SecurityLevel.LEVEL_2);
			nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck(
					"LTE04dg2ERBS00012", "UNSYNCHRONIZED",
					SecurityLevel.LEVEL_2);
		} catch (Exception e) {
			logger.info("Exception ocurred while creating node for testDownloadCrl_CPPNodeNotInSynch_PartialSuccess "
					+ e.getMessage());
			e.printStackTrace();
			throw new Exception();
		}

		final Command command = new Command("secadm", "crl download -n "
				+ NETWORK_ELEMENT_1 + ",LTE04dg2ERBS00012-NE");
		final CommandResponseDto commandResponseDto = commandHandler
				.execute(command);

		final List<String> rowsAsListOfStrings = responseDtoReader
				.extractListOfRowsFromCommandResponseDto(commandResponseDto);
		for (final String row : rowsAsListOfStrings) {
			if (row != null) {
				logger.info("DownloadCrlOnNode for CPPNodeNotInSynch_PartialSuccess Response :"
						+ row);
			}
		}

		assertTrue(responseDtoReader
				.messageIsContainedInList(
						"Successfully started a job to download CRL on demand for some node(s).",
						rowsAsListOfStrings));
		assertTrue(responseDtoReader
				.messageIsContainedInList(
						"NetworkElement=LTE04dg2ERBS00012-NE 10005 The node specified is not synchronized Please ensure the node specified is synchronized.",
						rowsAsListOfStrings));

		nodeSecurityNodesDataSetup.deleteAllNodes();

		logger.info("******* testDownloadCrl_CPPNodeNotInSynch_PartialSuccess Test End ******");

	}

	@Override
	public void testDownloadCrl_FileWithMulitpleCPPNodes_Success()
			throws Exception {
		logger.info("******* testDownloadCrl_FileWithMulitpleCPPNodes_Success Test Started ******");

		try {
			nodeSecurityNodesDataSetup.deleteAllNodes();
			nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck(NODE_NAME_1,
					"SYNCHRONIZED", SecurityLevel.LEVEL_2);
			nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck(NODE_NAME_2,
					"SYNCHRONIZED", SecurityLevel.LEVEL_2);
		} catch (Exception e) {
			logger.info("Exception ocurred while creating node for testDownloadCrl_FileWithMulitpleCPPNodes_Success "
					+ e.getMessage());
			e.printStackTrace();
			throw new Exception();
		}

		final Map<String, Object> properties = new HashMap<String, Object>();
                properties.put("file:", fileUtility.readResourceFile("MultipleCPPNodesCrlCheck.txt"));

		final Command command = new Command("secadm", CRL_DOWNLOAD, properties);
		final CommandResponseDto commandResponseDto = commandHandler
				.execute(command);

		final List<String> rowsAsListOfStrings = responseDtoReader
				.extractListOfRowsFromCommandResponseDto(commandResponseDto);
		for (final String row : rowsAsListOfStrings) {
			if (row != null) {
				logger.info("DownloadCrlOnNode for FileWithMulitpleCPPNodes_Success Response :"
						+ row);
			}
		}

		assertTrue(responseDtoReader.messageIsContainedInList(
				"Successfully started a job to download CRL on demand.",
				rowsAsListOfStrings));

		nodeSecurityNodesDataSetup.deleteAllNodes();

		logger.info("******* testDownloadCrl_FileWithMulitpleCPPNodes_Success Test End ******");

	}

	@Override
	public void testDownloadCrl_SingleCPPNodeWithOutFile_Success()
			throws Exception {
		logger.info("******* testDownloadCrl_SingleCPPNodeWithOutFile_Success Test Started ******");

		try {
			nodeSecurityNodesDataSetup.deleteAllNodes();
			nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck(NODE_NAME_1,
					"SYNCHRONIZED", SecurityLevel.LEVEL_2);
		} catch (Exception e) {
			logger.info("Exception ocurred while creating node for testDownloadCrl_SingleCPPNodeWithOutFile_Success "
					+ e.getMessage());
			e.printStackTrace();
			throw new Exception();
		}

		final Command command = new Command("secadm", "crl download -n "
				+ NETWORK_ELEMENT_1);
		final CommandResponseDto commandResponseDto = commandHandler
				.execute(command);

		final List<String> rowsAsListOfStrings = responseDtoReader
				.extractListOfRowsFromCommandResponseDto(commandResponseDto);
		for (final String row : rowsAsListOfStrings) {
			if (row != null) {
				logger.info("DownloadCrlOnNode for SingleCPPNodeWithOutFile_Success Response :"
						+ row);
			}
		}

		assertTrue(responseDtoReader.messageIsContainedInList(
				"Successfully started a job to download CRL on demand.",
				rowsAsListOfStrings));

		nodeSecurityNodesDataSetup.deleteAllNodes();

		logger.info("******* testDownloadCrl_SingleCPPNodeWithOutFile_Success Test End ******");

	}

	@Override
	public void testDownloadCrl_MulitpleCPPNodesWithOutFile_Success()
			throws Exception {
		logger.info("******* testDownloadCrl_MulitpleCPPNodesWithOutFile_Success Test Started ******");

		try {
			nodeSecurityNodesDataSetup.deleteAllNodes();
			nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck(NODE_NAME_1,
					"SYNCHRONIZED", SecurityLevel.LEVEL_2);
			nodeSecurityNodesDataSetup.createCPPNodeForCrlCheck(NODE_NAME_2,
					"SYNCHRONIZED", SecurityLevel.LEVEL_2);
		} catch (Exception e) {
			logger.info("Exception ocurred while creating node for testDownloadCrl_MulitpleCPPNodesWithOutFile_Success "
					+ e.getMessage());
			e.printStackTrace();
		}

		final Command command = new Command("secadm", "crl download -n "
				+ NETWORK_ELEMENT_1 + "," + NETWORK_ELEMENT_2);
		final CommandResponseDto commandResponseDto = commandHandler
				.execute(command);

		final List<String> rowsAsListOfStrings = responseDtoReader
				.extractListOfRowsFromCommandResponseDto(commandResponseDto);
		for (final String row : rowsAsListOfStrings) {
			if (row != null) {
				logger.info("DownloadCrlOnNode for MulitpleCPPNodesWithOutFile_Success Response :"
						+ row);
			}
		}

		assertTrue(responseDtoReader.messageIsContainedInList(
				"Successfully started a job to download CRL on demand.",
				rowsAsListOfStrings));

		nodeSecurityNodesDataSetup.deleteAllNodes();

		logger.info("******* testDownloadCrl_MulitpleCPPNodesWithOutFile_Success Test End ******");

	}

}
