/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.integration.jee.test.command.ciphersconfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.integration.jee.test.utils.FileUtility;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.IntegrationTestBase;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.ResponseDtoReader;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.*;
import com.ericsson.oss.services.scriptengine.spi.CommandHandler;
import com.ericsson.oss.services.scriptengine.spi.dtos.Command;
import com.ericsson.oss.services.scriptengine.spi.dtos.CommandResponseDto;

/**
 * This class will have test cases to test get ciphers command on nodes.
 *
 */
@RunWith(Arquillian.class)
@Stateless
public class GetCiphersTest extends IntegrationTestBase {

    @Inject
    private CommandHandler commandHandler;

    @Inject
    private NodeSecurityRadioNodesDataSetup nodeSecurityRadioNodesDataSetup;

    @Inject
    private NodeSecurityCPPNodesDataSetup nodeSecurityCPPNodesDataSetup;

    @Inject
    private NodeSecurityMiniLinkIndoorNodesDataSetup nodeSecurityMiniLinkIndoorNodesDataSetup;

    @Inject
    FileUtility fileUtility;

    @Inject
    private Logger logger;

    private ResponseDtoReader responseDtoReader = new ResponseDtoReader();

    @Test
    //@Ignore // Passed
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(1)
    public void testGetCiphers_InvalidProtocolType_Failure() {
        logger.info("******* Start: testGetCiphers_InvalidProtocolType_Failure Test ******");

        try {
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM, CiphersConfigTestConstants.GET_COMMAND_WITH_INVALID_PROTOCOL);

            testGetCiphers("testGetCiphers_InvalidProtocolType_Failure", command, CiphersConfigTestConstants.COMMAND_WITH_INVALID_PROTOCOL_RESPONSE_MESSAGE, false);

        } catch (Exception e) {
            logger.info("Exception ocurred while running the test case testGetCiphers_InvalidProtocolType_Failure " + e.getMessage());
            Assert.fail(e.getMessage());
        }

        logger.info("******* End: testGetCiphers_InvalidProtocolType_Failure Test ******");
    }

    @Test
    //@Ignore // Passed
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(2)
    public void testGetCiphers_InvalidNodeName_Failure() {

        logger.info("******* Start: testGetCiphers_InvalidNodeName_Failure Test ******");

        try {
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM, CiphersConfigTestConstants.GET_CIPHERS_TLS_COMMAND + "InvalidNodeName");
            testGetCiphers("testGetCiphers_InvalidNodeName_Failure", command, CiphersConfigTestConstants.COMMAND_WITH_INVALID_NODE_NAME_RESPONSE_MESSAGE, false);

        } catch (Exception e) {
            logger.info("Exception ocurred while running the test case testGetCiphers_InvalidNodeName_Failure " + e.getMessage());
            Assert.fail(e.getMessage());
        }

        logger.info("******* End: testGetCiphers_InvalidNodeName_Failure Test ******");
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(3)
    public void testGetCiphers_NodeNotInSynch_Failure() {

        logger.info("******* Start: testGetCiphers_NodeNotInSynch_Failure Test ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCiphersConfig(CiphersConfigTestConstants.DG2_NODE_NAME,
                    CiphersConfigTestConstants.UNSYNC, true);

            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM, CiphersConfigTestConstants.GET_CIPHERS_TLS_COMMAND + CiphersConfigTestConstants.DG2_NODE_NAME);
            testGetCiphers("testGetCiphers_NodeNotInSynch_Failure", command, CiphersConfigTestConstants.COMMAND_WITH_NODE_NOT_IN_SYNC_RESPONSE_MESSAGE, false);
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
        } catch (Exception e) {
            logger.info("Exception ocurred while running the test case testGetCiphers_NodeNotInSynch_Failure " + e.getMessage());
            Assert.fail(e.getMessage());
        }

        logger.info("******* End: testGetCiphers_NodeNotInSynch_Failure Test ******");
    }

    @Test
    //@Ignore // Passed
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(4)
    public void testGetCiphers_UnassociatedNetworkElementException() {
        logger.info("******* Start: testGetCiphers_UnassociatedNetworkElementException Test ******");

        try {
            nodeSecurityMiniLinkIndoorNodesDataSetup.deleteAllNodes();
            nodeSecurityMiniLinkIndoorNodesDataSetup.insertData();
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM, CiphersConfigTestConstants.GET_CIPHERS_SSH_COMMAND + CiphersConfigTestConstants.ML_INDOOR_NODE_NAME);
            testGetCiphers("testGetCiphers_UnassociatedNetworkElementException", command, CiphersConfigTestConstants.COMMAND_WITH_UNASSOICIATED_NETWORK_ELEMENT_RESPONSE_MESSAGE, false);
            nodeSecurityMiniLinkIndoorNodesDataSetup.deleteAllNodes();
        } catch (Exception e) {
            logger.info("Exception ocurred while running the test case testGetCiphers_UnassociatedNetworkElementException " + e.getMessage());
            Assert.fail(e.getMessage());
        }

        logger.info("******* End: testGetCiphers_UnassociatedNetworkElementException Test ******");
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(5)
    public void testGetCiphers_Tls_PartialSuccess() {
        logger.info("******* Start: testGetCiphers_Tls_PartialSuccess Test ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCiphersConfig(CiphersConfigTestConstants.DG2_NODE_NAME,
                    CiphersConfigTestConstants.SYNC, true);
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM, CiphersConfigTestConstants.GET_CIPHERS_TLS_COMMAND + CiphersConfigTestConstants.DG2_NODE_NAME + ","
                    + CiphersConfigTestConstants.ML_INDOOR_NODE_NAME);
            testGetCiphers("testGetCiphers_Tls_PartialSuccess", command, CiphersConfigTestConstants.GET_CIPHERS_FILE_OUTPUT_RESPONSE_MESSAGE, true);
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
        } catch (Exception e) {
            logger.info("Exception ocurred while running the test case testGetCiphers_Tls_PartialSuccess " + e.getMessage());
            Assert.fail(e.getMessage());
        }

        logger.info("******* End: testGetCiphers_Tls_PartialSuccess Test ******");
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(6)
    public void testGetCiphers_Tls_WithSingleComEcimNode_Success() {
        logger.info("******* Start: testGetCiphers_Tls_WithSingleComEcimNode_Success Test ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCiphersConfig(CiphersConfigTestConstants.DG2_NODE_NAME,
                    CiphersConfigTestConstants.SYNC, true);
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM, CiphersConfigTestConstants.GET_CIPHERS_TLS_COMMAND + CiphersConfigTestConstants.DG2_NODE_NAME);
            testGetCiphers("testGetCiphers_Tls_WithSingleComEcimNode_Success", command, CiphersConfigTestConstants.GET_CIPHERS_RESPONSE_MESSGAE, false);
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
        } catch (Exception e) {
            logger.info("Exception ocurred while running the test case testGetCiphers_Tls_WithSingleComEcimNode_Success " + e.getMessage());
            Assert.fail(e.getMessage());
        }

        logger.info("******* End: testGetCiphers_Tls_WithSingleComEcimNode_Success Test ******");
    }

    @Test
    //@Ignore // Passed
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(7)
    public void testGetCiphers_Tls_WithMultipleComEcimNodes_Success() {
        logger.info("******* Start: testGetCiphers_Tls_WithMultipleComEcimNodes_Success Test ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCiphersConfig(CiphersConfigTestConstants.DG2_NODE_NAME_3,
                    CiphersConfigTestConstants.SYNC, true);
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCiphersConfig(CiphersConfigTestConstants.DG2_NODE_NAME_4,
                    CiphersConfigTestConstants.SYNC, true);
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM, CiphersConfigTestConstants.GET_CIPHERS_TLS_COMMAND + CiphersConfigTestConstants.DG2_NODE_NAME + ","
                    + CiphersConfigTestConstants.DG2_NODE_NAME_2);
            testGetCiphers("testGetCiphers_Tls_WithMultipleComEcimNodes_Success", command, CiphersConfigTestConstants.GET_CIPHERS_FILE_OUTPUT_RESPONSE_MESSAGE, true);
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
        } catch (Exception e) {
            logger.info("Exception ocurred while running the test case testGetCiphers_Tls_WithMultipleComEcimNodes_Success " + e.getMessage());
            Assert.fail(e.getMessage());
        }

        logger.info("******* End: testGetCiphers_Tls_WithMultipleComEcimNodes_Success Test ******");
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(8)
    public void testGetCiphers_Tls_WithSingleCppNode_Success() {
        logger.info("******* Start: testGetCiphers_Tls_WithSingleCppNode_Success Test ******");

        try {
            nodeSecurityCPPNodesDataSetup.deleteAllNodes();
            nodeSecurityCPPNodesDataSetup.createCPPNodeForCiphersConfig(CiphersConfigTestConstants.ERBS_NODE_NAME,
                    CiphersConfigTestConstants.SYNC, true);
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM, CiphersConfigTestConstants.GET_CIPHERS_TLS_COMMAND + CiphersConfigTestConstants.ERBS_NE_NAME);
            testGetCiphers("testGetCiphers_Tls_WithSingleCppNode_Success", command, CiphersConfigTestConstants.GET_CIPHERS_RESPONSE_MESSGAE, false);
            nodeSecurityCPPNodesDataSetup.deleteAllNodes();
        } catch (Exception e) {
            logger.info("Exception ocurred while running the test case testGetCiphers_Tls_WithSingleCppNode_Success " + e.getMessage());
            Assert.fail(e.getMessage());
        }

        logger.info("******* End: testGetCiphers_Tls_WithSingleCppNode_Success Test ******");
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(9)
    public void testGetCiphers_Tls_WithMultipleCppNodes_Success() {
        logger.info("******* Start: testGetCiphers_Tls_WithMultipleCppNodes_Success Test ******");

        try {
            nodeSecurityCPPNodesDataSetup.deleteAllNodes();
            nodeSecurityCPPNodesDataSetup.createCPPNodeForCiphersConfig(CiphersConfigTestConstants.ERBS_NODE_NAME,
                    CiphersConfigTestConstants.SYNC, true);
            nodeSecurityCPPNodesDataSetup.createCPPNodeForCiphersConfig(CiphersConfigTestConstants.ERBS_NODE_NAME_2, 
                    CiphersConfigTestConstants.SYNC, true);
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM, CiphersConfigTestConstants.GET_CIPHERS_TLS_COMMAND + CiphersConfigTestConstants.ERBS_NE_NAME + ","
                    + CiphersConfigTestConstants.ERBS_NE_NAME_2);
            testGetCiphers("testGetCiphers_Tls_WithMultipleCppNodes_Success", command, CiphersConfigTestConstants.GET_CIPHERS_FILE_OUTPUT_RESPONSE_MESSAGE, true);
            nodeSecurityCPPNodesDataSetup.deleteAllNodes();
        } catch (Exception e) {
            logger.info("Exception ocurred while running the test case testGetCiphers_Tls_WithMultipleCppNodes_Success " + e.getMessage());
            Assert.fail(e.getMessage());
        }

        logger.info("******* End: testGetCiphers_Tls_WithMultipleCppNodes_Success Test ******");
    }

    @Test
    //@Ignore // Passed
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(10)
    public void testGetCiphers_Ssh_PartialSuccess() {
        logger.info("******* Start: testGetCiphers_Ssh_PartialSuccess Test ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCiphersConfig(CiphersConfigTestConstants.DG2_NODE_NAME,
                    CiphersConfigTestConstants.SYNC, true);
            nodeSecurityMiniLinkIndoorNodesDataSetup.deleteAllNodes();
            nodeSecurityMiniLinkIndoorNodesDataSetup.insertData();
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM, CiphersConfigTestConstants.GET_CIPHERS_TLS_COMMAND + CiphersConfigTestConstants.DG2_NODE_NAME + ","
                    + CiphersConfigTestConstants.ML_INDOOR_NODE_NAME);
            testGetCiphers("testGetCiphers_Ssh_PartialSuccess", command, CiphersConfigTestConstants.GET_CIPHERS_FILE_OUTPUT_RESPONSE_MESSAGE, true);
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
        } catch (Exception e) {
            logger.info("Exception ocurred while running the test case testGetCiphers_Ssh_PartialSuccess " + e.getMessage());
            Assert.fail(e.getMessage());
        }

        logger.info("******* End: testGetCiphers_Ssh_PartialSuccess Test ******");
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(11)
    public void testGetCiphers_Ssh_WithSingleComEcimNode_Success() {
        logger.info("******* Start: testGetCiphers_Ssh_WithSingleComEcimNode_Success Test ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCiphersConfig(CiphersConfigTestConstants.DG2_NODE_NAME,
                    CiphersConfigTestConstants.SYNC, true);
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM, CiphersConfigTestConstants.GET_CIPHERS_SSH_COMMAND + CiphersConfigTestConstants.DG2_NODE_NAME);
            testGetCiphers("testGetCiphers_Ssh_WithSingleComEcimNode_Success", command, CiphersConfigTestConstants.GET_CIPHERS_RESPONSE_MESSGAE, false);

            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
        } catch (Exception e) {
            logger.info("Exception ocurred while running the test case testGetCiphers_Ssh_WithSingleComEcimNode_Success " + e.getMessage());
            Assert.fail(e.getMessage());
        }

        logger.info("******* End: testGetCiphers_Ssh_WithSingleComEcimNode_Success Test ******");
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(12)
    public void testGetCiphers_Ssh_WithMultipleComEcimNodes_Success() {
        logger.info("******* Start: testGetCiphers_Ssh_WithMultipleComEcimNodes_Success Test ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCiphersConfig(CiphersConfigTestConstants.DG2_NODE_NAME,
                    CiphersConfigTestConstants.SYNC, true);
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCiphersConfig(CiphersConfigTestConstants.DG2_NODE_NAME_2, 
                    CiphersConfigTestConstants.SYNC, true);
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM, CiphersConfigTestConstants.GET_CIPHERS_SSH_COMMAND + CiphersConfigTestConstants.DG2_NODE_NAME + ","
                    + CiphersConfigTestConstants.DG2_NODE_NAME_2);
            testGetCiphers("testGetCiphers_Ssh_WithMultipleComEcimNodes_Success", command, CiphersConfigTestConstants.GET_CIPHERS_FILE_OUTPUT_RESPONSE_MESSAGE, true);
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
        } catch (Exception e) {
            logger.info("Exception ocurred while running the test case testGetCiphers_Ssh_WithMultipleComEcimNodes_Success " + e.getMessage());
            Assert.fail(e.getMessage());
        }

        logger.info("******* End: testGetCiphers_Ssh_WithMultipleComEcimNodes_Success Test ******");
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(13)
    public void testGetCiphers_Ssh_WithSingleCppNode_Success() {
        logger.info("******* Start: testGetCiphers_Ssh_WithSingleCppNode_Success Test ******");

        try {
            nodeSecurityCPPNodesDataSetup.deleteAllNodes();
            nodeSecurityCPPNodesDataSetup.createCPPNodeForCiphersConfig(CiphersConfigTestConstants.ERBS_NODE_NAME,
                    CiphersConfigTestConstants.SYNC, true);
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM, CiphersConfigTestConstants.GET_CIPHERS_SSH_COMMAND + CiphersConfigTestConstants.ERBS_NE_NAME);
            testGetCiphers("testGetCiphers_Ssh_WithSingleCppNode_Success", command, CiphersConfigTestConstants.GET_CIPHERS_RESPONSE_MESSGAE, false);

            nodeSecurityCPPNodesDataSetup.deleteAllNodes();
        } catch (Exception e) {
            logger.info("Exception ocurred while running the test case testGetCiphers_Ssh_WithSingleCppNode_Success " + e.getMessage());
            Assert.fail(e.getMessage());
        }

        logger.info("******* End: testGetCiphers_Ssh_WithSingleCppNode_Success Test ******");
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(14)
    public void testGetCiphers_Ssh_WithMultipleCppNodes_Success() {
        logger.info("******* Start: testGetCiphers_Ssh_WithMultipleCppNodes_Success Test ******");

        try {
            nodeSecurityCPPNodesDataSetup.deleteAllNodes();
            nodeSecurityCPPNodesDataSetup.createCPPNodeForCiphersConfig(CiphersConfigTestConstants.ERBS_NODE_NAME,
                    CiphersConfigTestConstants.SYNC, true);
            nodeSecurityCPPNodesDataSetup.createCPPNodeForCiphersConfig(CiphersConfigTestConstants.ERBS_NODE_NAME_2,
                    CiphersConfigTestConstants.SYNC, true);
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM, CiphersConfigTestConstants.GET_CIPHERS_SSH_COMMAND + CiphersConfigTestConstants.ERBS_NE_NAME + ","
                    + CiphersConfigTestConstants.ERBS_NE_NAME_2);
            testGetCiphers("testGetCiphers_Ssh_WithMultipleCppNodes_Success", command, CiphersConfigTestConstants.GET_CIPHERS_FILE_OUTPUT_RESPONSE_MESSAGE, true);
            nodeSecurityCPPNodesDataSetup.deleteAllNodes();
        } catch (Exception e) {
            logger.info("Exception ocurred while running the test case testGetCiphers_Ssh_WithMultipleCppNodes_Success " + e.getMessage());
            Assert.fail(e.getMessage());
        }

        logger.info("******* End: testGetCiphers_Ssh_WithMultipleCppNodes_Success Test ******");
    }

    private void testGetCiphers(final String testCaseName, final Command command, final String expectedResponseMessage, final boolean isFileDownloadREquest) {
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        if (isFileDownloadREquest) {
            final String actualResponse = commandResponseDto.getResponseDto().getElements().get(1).toString();
            logger.info(testCaseName + " file output response " + actualResponse);
            assertEquals(expectedResponseMessage, actualResponse);
        } else {
            final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
            for (final String row : rowsAsListOfStrings) {
                if (row != null) {
                    logger.info(testCaseName + " response " + row);
                }
            }
            assertTrue(responseDtoReader.messageIsContainedInList(expectedResponseMessage, rowsAsListOfStrings));
        }
    }
}
