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

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.integration.jee.test.utils.IntegrationTestBase;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.integration.jee.test.utils.FileUtility;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.ResponseDtoReader;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityCPPNodesDataSetup;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityMiniLinkIndoorNodesDataSetup;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityRadioNodesDataSetup;
import com.ericsson.oss.services.scriptengine.spi.CommandHandler;
import com.ericsson.oss.services.scriptengine.spi.dtos.Command;
import com.ericsson.oss.services.scriptengine.spi.dtos.CommandResponseDto;

/**
 * This class will have test cases to test set ciphers command on nodes with nodename/nodelist input.
 *
 */
@RunWith(Arquillian.class)
@Stateless
public class SetCiphersTest extends IntegrationTestBase {

    @Inject
    private CommandHandler commandHandler;

    @Inject
    private NodeSecurityRadioNodesDataSetup nodeSecurityRadioNodesDataSetup;

    @Inject
    private NodeSecurityCPPNodesDataSetup nodeSecurityCPPNodesDataSetup;

    @Inject
    private NodeSecurityMiniLinkIndoorNodesDataSetup nodeSecurityMiniLinkIndoorNodesDataSetup;

    @Inject
    private FileUtility fileUtility;

    @Inject
    private Logger logger;

    private ResponseDtoReader responseDtoReader = new ResponseDtoReader();

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(1)
    public void testSetCiphers_Ssh_WithSingleComEcimNode_Success() {
        logger.info("******* Start: testSetCiphers_Ssh_WithSingleComEcimNode_Success Test ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCiphersConfig(CiphersConfigTestConstants.DG2_NODE_NAME,
                    CiphersConfigTestConstants.SYNC, true);
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM,
                    CiphersConfigTestConstants.SET_CIPHERS_SSH_COMMAND + CiphersConfigTestConstants.DG2_NODE_NAME);
            testSetCiphers("testSetCiphers_Ssh_WithSingleComEcimNode_Success", command,
                    CiphersConfigTestConstants.SET_CIPHERS_SUCCESS_RESPONSE_MESSGAE);

            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
        } catch (final Exception e) {
            logger.info("Exception ocurred while running the test case testSetCiphers_Ssh_WithSingleComEcimNode_Success " + e.getMessage());
            Assert.fail(e.getMessage());
        }
        logger.info("******* End: testSetCiphers_Ssh_WithSingleComEcimNode_Success Test ******");
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(2)
    public void testSetCiphers_Ssh_WithMultipleComEcimNodes_Success() {
        logger.info("******* Start: testSetCiphers_Ssh_WithMultipleComEcimNodes_Success Test ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCiphersConfig(CiphersConfigTestConstants.DG2_NODE_NAME,
                    CiphersConfigTestConstants.SYNC, true);
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCiphersConfig(CiphersConfigTestConstants.DG2_NODE_NAME_2,
                    CiphersConfigTestConstants.SYNC, true);
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM, CiphersConfigTestConstants.SET_CIPHERS_SSH_COMMAND
                    + CiphersConfigTestConstants.DG2_NODE_NAME + "," + CiphersConfigTestConstants.DG2_NODE_NAME_2);
            testSetCiphers("testSetCiphers_Ssh_WithMultipleComEcimNodes_Success", command,
                    CiphersConfigTestConstants.SET_CIPHERS_SUCCESS_RESPONSE_MESSGAE);
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
        } catch (final Exception e) {
            logger.info("Exception ocurred while running the test case testSetCiphers_Ssh_WithMultipleComEcimNodes_Success " + e.getMessage());
            Assert.fail(e.getMessage());
        }

        logger.info("******* End: testSetCiphers_Ssh_WithMultipleComEcimNodes_Success Test ******");
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(3)
    public void testSetCiphers_Tls_WithSingleComEcimNode_Success() {
        logger.info("******* Start: testSetCiphers_Tls_WithSingleComEcimNode_Success Test ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCiphersConfig(CiphersConfigTestConstants.DG2_NODE_NAME,
                    CiphersConfigTestConstants.SYNC, true);
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM,
                    CiphersConfigTestConstants.SET_CIPHERS_TLS_COMMAND + CiphersConfigTestConstants.DG2_NODE_NAME);
            testSetCiphers("testSetCiphers_Tls_WithSingleComEcimNode_Success", command,
                    CiphersConfigTestConstants.SET_CIPHERS_SUCCESS_RESPONSE_MESSGAE);
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
        } catch (final Exception e) {
            logger.info("Exception ocurred while running the test case testSetCiphers_Tls_WithSingleComEcimNode_Success " + e.getMessage());
            Assert.fail(e.getMessage());
        }

        logger.info("******* End: testSetCiphers_Tls_WithSingleComEcimNode_Success Test ******");
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(4)
    public void testSetCiphers_Tls_WithMultipleComEcimNodes_Success() {
        logger.info("******* Start: testSetCiphers_Tls_WithMultipleComEcimNodes_Success Test ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCiphersConfig(CiphersConfigTestConstants.DG2_NODE_NAME,
                    CiphersConfigTestConstants.SYNC, true);
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCiphersConfig(CiphersConfigTestConstants.DG2_NODE_NAME_2,
                    CiphersConfigTestConstants.SYNC, true);
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM, CiphersConfigTestConstants.SET_CIPHERS_TLS_COMMAND
                    + CiphersConfigTestConstants.DG2_NODE_NAME + "," + CiphersConfigTestConstants.DG2_NODE_NAME_2);
            testSetCiphers("testSetCiphers_Tls_WithMultipleComEcimNodes_Success", command,
                    CiphersConfigTestConstants.SET_CIPHERS_SUCCESS_RESPONSE_MESSGAE);
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
        } catch (final Exception e) {
            logger.info("Exception ocurred while running the test case testSetCiphers_Tls_WithMultipleComEcimNodes_Success " + e.getMessage());
            Assert.fail(e.getMessage());
        }

        logger.info("******* End: testSetCiphers_Tls_WithMultipleComEcimNodes_Success Test ******");
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(5)
    public void testSetCiphers_Ssh_WithSingleCppNode_Success() {
        logger.info("******* Start: testSetCiphers_Ssh_WithSingleCppNode_Success Test ******");

        try {
            nodeSecurityCPPNodesDataSetup.deleteAllNodes();
            nodeSecurityCPPNodesDataSetup.createCPPNodeForCiphersConfig(CiphersConfigTestConstants.ERBS_NODE_NAME, 
                    CiphersConfigTestConstants.SYNC, true);
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM,
                    CiphersConfigTestConstants.SET_CIPHERS_SSH_COMMAND + CiphersConfigTestConstants.ERBS_NE_NAME);
            testSetCiphers("testSetCiphers_Ssh_WithSingleCppNode_Success", command, CiphersConfigTestConstants.SET_CIPHERS_SUCCESS_RESPONSE_MESSGAE);

            nodeSecurityCPPNodesDataSetup.deleteAllNodes();
        } catch (final Exception e) {
            logger.info("Exception ocurred while running the test case testSetCiphers_Ssh_WithSingleCppNode_Success " + e.getMessage());
            Assert.fail(e.getMessage());
        }

        logger.info("******* End: testSetCiphers_Ssh_WithSingleCppNode_Success Test ******");
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(6)
    public void testSetCiphers_Ssh_WithMultipleCppNode_Success() {
        logger.info("******* Start: testSetCiphers_Ssh_WithMultipleCppNode_Success Test ******");

        try {
            nodeSecurityCPPNodesDataSetup.deleteAllNodes();
            nodeSecurityCPPNodesDataSetup.createCPPNodeForCiphersConfig(CiphersConfigTestConstants.ERBS_NODE_NAME, 
                    CiphersConfigTestConstants.SYNC, true);
            nodeSecurityCPPNodesDataSetup.createCPPNodeForCiphersConfig(CiphersConfigTestConstants.ERBS_NODE_NAME_2, 
                    CiphersConfigTestConstants.SYNC, true);
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM, CiphersConfigTestConstants.SET_CIPHERS_SSH_COMMAND
                    + CiphersConfigTestConstants.ERBS_NE_NAME + "," + CiphersConfigTestConstants.ERBS_NE_NAME_2);
            testSetCiphers("testSetCiphers_Ssh_WithMultipleCppNode_Success", command,
                    CiphersConfigTestConstants.SET_CIPHERS_SUCCESS_RESPONSE_MESSGAE);

            nodeSecurityCPPNodesDataSetup.deleteAllNodes();
        } catch (final Exception e) {
            logger.info("Exception ocurred while running the test case testSetCiphers_Ssh_WithMultipleCppNode_Success " + e.getMessage());
            Assert.fail(e.getMessage());
        }

        logger.info("******* End: testSetCiphers_Ssh_WithMultipleCppNode_Success Test ******");
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(7)
    public void testSetCiphers_Tls_WithSingleCppNode_Success() {
        logger.info("******* Start: testSetCiphers_Tls_WithSingleCppNode_Success Test ******");

        try {
            nodeSecurityCPPNodesDataSetup.deleteAllNodes();
            nodeSecurityCPPNodesDataSetup.createCPPNodeForCiphersConfig(CiphersConfigTestConstants.ERBS_NODE_NAME, 
                    CiphersConfigTestConstants.SYNC, true);
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM,
                    CiphersConfigTestConstants.SET_CIPHERS_TLS_COMMAND + CiphersConfigTestConstants.ERBS_NE_NAME);
            testSetCiphers("testSetCiphers_Tls_WithSingleCppNode_Success", command, CiphersConfigTestConstants.SET_CIPHERS_SUCCESS_RESPONSE_MESSGAE);

            nodeSecurityCPPNodesDataSetup.deleteAllNodes();
        } catch (final Exception e) {
            logger.info("Exception ocurred while running the test case testSetCiphers_Tls_WithSingleCppNode_Success " + e.getMessage());
            Assert.fail(e.getMessage());
        }

        logger.info("******* End: testSetCiphers_Tls_WithSingleCppNode_Success Test ******");
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(8)
    public void testSetCiphers_Tls_WithMultipleCppNode_Success() {
        logger.info("******* Start: testSetCiphers_Tls_WithMultipleCppNode_Success Test ******");

        try {
            nodeSecurityCPPNodesDataSetup.deleteAllNodes();
            nodeSecurityCPPNodesDataSetup.createCPPNodeForCiphersConfig(CiphersConfigTestConstants.ERBS_NODE_NAME, 
                    CiphersConfigTestConstants.SYNC, true);
            nodeSecurityCPPNodesDataSetup.createCPPNodeForCiphersConfig(CiphersConfigTestConstants.ERBS_NODE_NAME_2, 
                    CiphersConfigTestConstants.SYNC, true);
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM, CiphersConfigTestConstants.SET_CIPHERS_TLS_COMMAND
                    + CiphersConfigTestConstants.ERBS_NE_NAME + "," + CiphersConfigTestConstants.ERBS_NE_NAME_2);
            testSetCiphers("testSetCiphers_Tls_WithMultipleCppNode_Success", command,
                    CiphersConfigTestConstants.SET_CIPHERS_SUCCESS_RESPONSE_MESSGAE);

            nodeSecurityCPPNodesDataSetup.deleteAllNodes();
        } catch (final Exception e) {
            logger.info("Exception ocurred while running the test case testSetCiphers_Tls_WithMultipleCppNode_Success " + e.getMessage());
            Assert.fail(e.getMessage());
        }

        logger.info("******* End: testSetCiphers_Tls_WithMultipleCppNode_Success Test ******");
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(9)
    public void testSetCiphers_WithFileInput_Success() {
        logger.info("******* Start: testSetCiphers_WithFileInput_Success Test ******");

        try {
            final Map<String, Object> properties = new HashMap<>();
            properties.put("file:", fileUtility.readResourceFile(CiphersConfigTestConstants.SET_CIPHERS_FILE_INPUT));
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCiphersConfig(CiphersConfigTestConstants.DG2_NODE_NAME,
                    CiphersConfigTestConstants.SYNC, true);
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCiphersConfig(CiphersConfigTestConstants.DG2_NODE_NAME_2,
                    CiphersConfigTestConstants.SYNC, true);

            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM, CiphersConfigTestConstants.SET_COMMAND_WITH_FILE_INPUT,
                    properties);
            testSetCiphers("testSetCiphers_WithFileInput_Success", command, CiphersConfigTestConstants.SET_CIPHERS_SUCCESS_RESPONSE_MESSGAE);
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
        } catch (final Exception e) {
            logger.info("Exception ocurred while creating node for testSetCiphers_WithFileInput_Success " + e.getMessage());
            Assert.fail(e.getMessage());
        }

        logger.info("******* End: testSetCiphers_WithFileInput_Success Test ******");
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(10)
    public void testSetCiphers_Ssh_PartialSuccess() {
        logger.info("******* Start: testSetCiphers_Ssh_PartialSuccess Test ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCiphersConfig(CiphersConfigTestConstants.DG2_NODE_NAME,
                    CiphersConfigTestConstants.SYNC, true);
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM, CiphersConfigTestConstants.SET_CIPHERS_SSH_COMMAND
                    + CiphersConfigTestConstants.DG2_NODE_NAME + "," + CiphersConfigTestConstants.ML_INDOOR_NODE_NAME);
            testSetCiphers("testSetCiphers_Ssh_PartialSuccess", command, CiphersConfigTestConstants.SET_CIPHERS_PARTIAL_SUCCESS_RESPONSE_MESSAGE);
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
        } catch (final Exception e) {
            logger.info("Exception ocurred while running the test case testSetCiphers_Ssh_PartialSuccess " + e.getMessage());
            Assert.fail(e.getMessage());
        }

        logger.info("******* End: testSetCiphers_Ssh_PartialSuccess Test ******");
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(11)
    public void testSetCiphers_Tls_PartialSuccess() {
        logger.info("******* Start: testSetCiphers_Tls_PartialSuccess Test ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCiphersConfig(CiphersConfigTestConstants.DG2_NODE_NAME,
                    CiphersConfigTestConstants.SYNC, true);
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM, CiphersConfigTestConstants.SET_CIPHERS_TLS_COMMAND
                    + CiphersConfigTestConstants.DG2_NODE_NAME + "," + CiphersConfigTestConstants.ML_INDOOR_NODE_NAME);
            testSetCiphers("testSetCiphers_Tls_PartialSuccess", command, CiphersConfigTestConstants.SET_CIPHERS_PARTIAL_SUCCESS_RESPONSE_MESSAGE);
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
        } catch (final Exception e) {
            logger.info("Exception ocurred while running the test case testSetCiphers_Tls_PartialSuccess " + e.getMessage());
            Assert.fail(e.getMessage());
        }

        logger.info("******* End: testSetCiphers_Tls_PartialSuccess Test ******");
    }

    @Test
    //@Ignore // Passed
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(12)
    public void testSetCiphers_WithStarAsInput_Failure() {
        logger.info("******* Strart: testSetCiphers_WithStarAsInput Test ******");
        try {
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM, CiphersConfigTestConstants.SET_CIPHERS_TLS_COMMAND + "*");
            testSetCiphers("testSetCiphers_WithStarAsInput", command, CiphersConfigTestConstants.COMMAND_SYNTAX_ERROR);

        } catch (final Exception e) {
            logger.info("Exception ocurred while running the test case testSetCiphers_WithStarAsInput " + e.getMessage());
            Assert.fail(e.getMessage());
        }
        logger.info("******* End: testSetCiphers_WithStarAsInput Test ******");
    }

    @Test
    //@Ignore // Passed
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(13)
    public void testSetCiphers_InvalidCommandSyntax_Failure() {
        logger.info("******* Start: testSetCiphes_InvalidCommandSyntax_Failure Test ******");
        try {
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM, CiphersConfigTestConstants.COMMAND_WITH_INVALID_SYNTAX);
            testSetCiphers("testSetCiphes_InvalidCommandSyntax_Failure", command, CiphersConfigTestConstants.COMMAND_SYNTAX_ERROR);

        } catch (final Exception e) {
            logger.info("Exception ocurred while running the test case testSetCiphes_InvalidCommandSyntax_Failure " + e.getMessage());
            Assert.fail(e.getMessage());
        }
        logger.info("******* End: testSetCiphes_InvalidCommandSyntax_Failure Test ******");
    }

    @Test
    //@Ignore // Passed
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(14)
    public void testSetCiphers_InvalidNodeName_Failure() {

        logger.info("******* Start: testSetCiphers_InvalidNodeName_Failure Test ******");
        try {
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM,
                    CiphersConfigTestConstants.SET_CIPHERS_TLS_COMMAND + "InvalidNodeName");
            testSetCiphers("testSetCiphers_InvalidNodeName_Failure", command,
                    CiphersConfigTestConstants.COMMAND_WITH_INVALID_NODE_NAME_RESPONSE_MESSAGE);

        } catch (final Exception e) {
            logger.info("Exception ocurred while running the test case testSetCiphers_InvalidNodeName_Failure " + e.getMessage());
            Assert.fail(e.getMessage());
        }
        logger.info("******* End: testSetCiphers_InvalidNodeName_Failure Test ******");
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(15)
    public void testSetCiphers_NodeNotInSynch_Failure() {

        logger.info("******* Start: testSetCiphers_NodeNotInSynch_Failure Test ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCiphersConfig(CiphersConfigTestConstants.DG2_NODE_NAME,
                    CiphersConfigTestConstants.UNSYNC, true);

            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM,
                    CiphersConfigTestConstants.SET_CIPHERS_TLS_COMMAND + CiphersConfigTestConstants.DG2_NODE_NAME);
            testSetCiphers("testSetCiphers_NodeNotInSynch_Failure", command,
                    CiphersConfigTestConstants.COMMAND_WITH_NODE_NOT_IN_SYNC_RESPONSE_MESSAGE);
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
        } catch (final Exception e) {
            logger.info("Exception ocurred while running the test case testSetCiphers_NodeNotInSynch_Failure " + e.getMessage());
            Assert.fail(e.getMessage());
        }

        logger.info("******* End: testSetCiphers_NodeNotInSynch_Failure Test ******");
    }

    @Test
    //@Ignore // Passed
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(16)
    public void testSetCiphers_UnassociatedNetworkElement_Failure() {
        logger.info("******* Start: testSetCiphers_UnassociatedNetworkElement_Failure Test ******");

        try {
            nodeSecurityMiniLinkIndoorNodesDataSetup.deleteAllNodes();
            nodeSecurityMiniLinkIndoorNodesDataSetup.insertData();
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM,
                    CiphersConfigTestConstants.SET_CIPHERS_SSH_COMMAND + CiphersConfigTestConstants.ML_INDOOR_NODE_NAME);
            testSetCiphers("testSetCiphers_UnassociatedNetworkElement_Failure", command,
                    CiphersConfigTestConstants.COMMAND_WITH_UNASSOICIATED_NETWORK_ELEMENT_RESPONSE_MESSAGE);
            nodeSecurityMiniLinkIndoorNodesDataSetup.deleteAllNodes();
        } catch (final Exception e) {
            logger.info("Exception ocurred while running the test case testSetCiphers_UnassociatedNetworkElement_Failure " + e.getMessage());
            Assert.fail(e.getMessage());
        }

        logger.info("******* End: testSetCiphers_UnassociatedNetworkElement_Failure Test ******");
    }

    @Test
    @Ignore("No target version information exists for given target model identity 17A-H.1.120 under the target category ERBS")
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(17)
    public void testSetCiphers_UnsupportedNodeReleaseVersionCpp_Failure() {
        logger.info("******* Start: testSetCiphers_UnsupportedNodeReleaseVersionCpp_Failure Test ******");

        try {
            nodeSecurityCPPNodesDataSetup.deleteAllNodes();
            nodeSecurityCPPNodesDataSetup.createCPPNodeForCiphersConfig(CiphersConfigTestConstants.ERBS_NODE_NAME, 
                    CiphersConfigTestConstants.UNSYNC, false);

            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM,
                    CiphersConfigTestConstants.SET_CIPHERS_TLS_COMMAND + CiphersConfigTestConstants.ERBS_NE_NAME);
            testSetCiphers("testSetCiphers_UnsupportedNodeReleaseVersionCpp_Failure", command,
                    CiphersConfigTestConstants.COMMAND_WITH_UNSUPPORTED_NODE_TYPE_RESPONSE_MESSAGE);
            nodeSecurityCPPNodesDataSetup.deleteAllNodes();
        } catch (final Exception e) {
            logger.info("Exception ocurred while running the test case testSetCiphers_UnsupportedNodeReleaseVersionCpp_Failure " + e.getMessage());
            Assert.fail(e.getMessage());
        }

        logger.info("******* End: testSetCiphers_UnsupportedNodeReleaseVersionCpp_Failure Test ******");
    }

    @Test
    @Ignore("No target version information exists for given target model identity 16A-R28CJ under the target category RadioNode")
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(18)
    public void testSetCiphers_UnsupportedNodeReleaseVersionComEcim_Failure() {
        logger.info("******* Start: testSetCiphers_UnsupportedNodeReleaseVersionComEcim_Failure Test ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCiphersConfig(CiphersConfigTestConstants.DG2_NODE_NAME,
                    CiphersConfigTestConstants.SYNC, false);

            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM,
                    CiphersConfigTestConstants.SET_CIPHERS_TLS_COMMAND + CiphersConfigTestConstants.DG2_NODE_NAME);
            testSetCiphers("testSetCiphers_UnsupportedNodeReleaseVersionComEcim_Failure", command,
                    CiphersConfigTestConstants.COMMAND_WITH_UNSUPPORTED_NODE_TYPE_RESPONSE_MESSAGE);
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
        } catch (final Exception e) {
            logger.info(
                    "Exception ocurred while running the test case testSetCiphers_UnsupportedNodeReleaseVersionComEcim_Failure " + e.getMessage());
            Assert.fail(e.getMessage());
        }

        logger.info("******* End: testSetCiphers_UnsupportedNodeReleaseVersionComEcim_Failure Test ******");
    }

    @Test
    //@Ignore // Passed
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(19)
    public void testSetCiphers_InvalidCipherFilterValue_Failure() {
        logger.info("******* Start: testSetCiphers_InvalidCipherFilterValue_Failure Test ******");

        try {
            nodeSecurityCPPNodesDataSetup.deleteAllNodes();
            nodeSecurityCPPNodesDataSetup.createCPPNodeForCiphersConfig(CiphersConfigTestConstants.ERBS_NODE_NAME, 
                    CiphersConfigTestConstants.UNSYNC, true);

            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM,
                    CiphersConfigTestConstants.SET_CIPHERS_TLS_WITH_INVALID_CIPHER_FILTER_COMMAND + CiphersConfigTestConstants.ERBS_NE_NAME);
            testSetCiphers("testSetCiphers_InvalidCipherFilterValue_Failure", command,
                    CiphersConfigTestConstants.INVALID_CIPHER_FILTER_VALUE_RESPONSE_MESSAGE);
            nodeSecurityCPPNodesDataSetup.deleteAllNodes();
        } catch (final Exception e) {
            logger.info("Exception ocurred while running the test case testSetCiphers_InvalidCipherFilterValue_Failure " + e.getMessage());
            Assert.fail(e.getMessage());
        }

        logger.info("******* End: testSetCiphers_InvalidCipherFilterValue_Failure Test ******");
    }

    private void testSetCiphers(final String testCaseName, final Command command, final String expectedResponseMessage) {
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);

        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info(testCaseName + " response :" + row);
            }
        }
        assertTrue(responseDtoReader.messageIsContainedInList(expectedResponseMessage, rowsAsListOfStrings));
    }
}
