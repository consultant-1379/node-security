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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.integration.jee.test.utils.FileUtility;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.IntegrationTestBase;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.ResponseDtoReader;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityRadioNodesDataSetup;
import com.ericsson.oss.services.scriptengine.spi.CommandHandler;
import com.ericsson.oss.services.scriptengine.spi.dtos.Command;
import com.ericsson.oss.services.scriptengine.spi.dtos.CommandResponseDto;
import java.io.InputStream;

/**
 *This class will have test cases to test set ciphers command on nodes with xml file input.
 *
 */
@RunWith(Arquillian.class)
@Stateless
public class SetCiphersWithXmlFileInputTest extends IntegrationTestBase {

    @Inject
    private CommandHandler commandHandler;

    @Inject
    private NodeSecurityRadioNodesDataSetup nodeSecurityRadioNodesDataSetup;

    @Inject
    private FileUtility fileUtility;

    @Inject
    private Logger logger;

    private ResponseDtoReader responseDtoReader = new ResponseDtoReader();

    @Test
    @Ignore("To Be investigated")
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(1)
    public void testSetCiphers_SshAndTlsForSingleNode_Success() {
        logger.info("******* Start: testSetCiphers_SshAndTlsForSingleNode_Success Test ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCiphersConfig(CiphersConfigTestConstants.DG2_NODE_NAME,
                    CiphersConfigTestConstants.SYNC, true);
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM, CiphersConfigTestConstants.SET_CIPHERS_WITH_XML_FILE_INPUT_COMMAND,
                    getProperties(CiphersConfigTestConstants.SET_SSH_AND_TLS_CIPHERS_FOR_SINGLE_NODE));
            testSetCiphers("testSetCiphers_SshAndTlsForSingleNode_Success", command, CiphersConfigTestConstants.SET_CIPHERS_SUCCESS_RESPONSE_MESSGAE);

            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
        } catch (Exception e) {
            logger.info("Exception ocurred while running the test case testSetCiphers_SshAndTlsForSingleNode_Success " + e.getMessage());
            Assert.fail(e.getMessage());
        }
        logger.info("******* End: testSetCiphers_SshAndTlsForSingleNode_Success Test ******");

    }

    @Test
    @Ignore("To Be investigated")
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(2)
    public void testSetCiphers_SshAndTlsForMultipleNodes_Success() {
        logger.info("******* Start: testSetCiphers_SshAndTlsForMultipleNodes_Success Test ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCiphersConfig(CiphersConfigTestConstants.DG2_NODE_NAME,
                    CiphersConfigTestConstants.SYNC, true);
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCiphersConfig(CiphersConfigTestConstants.DG2_NODE_NAME_2,
                    CiphersConfigTestConstants.SYNC, true);
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM, CiphersConfigTestConstants.SET_CIPHERS_WITH_XML_FILE_INPUT_COMMAND,
                    getProperties(CiphersConfigTestConstants.SET_SSH_AND_TLS_CIPHERS_FOR_MULTIPLE_NODES));
            testSetCiphers("testSetCiphers_SshAndTlsForMultipleNodes_Success", command, CiphersConfigTestConstants.SET_CIPHERS_SUCCESS_RESPONSE_MESSGAE);

            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
        } catch (Exception e) {
            logger.info("Exception ocurred while running the test case testSetCiphers_SshAndTlsForMultipleNodes_Success " + e.getMessage());
            Assert.fail(e.getMessage());
        }
        logger.info("******* End: testSetCiphers_SshAndTlsForMultipleNodes_Success Test ******");

    }

    @Test
    @Ignore("To Be investigated")
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(3)
    public void testSetCiphers_OnlySshForSingleNode_Success() {
        logger.info("******* Start: testSetCiphers_OnlySshForSingleNode_Success Test ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCiphersConfig(CiphersConfigTestConstants.DG2_NODE_NAME,
                    CiphersConfigTestConstants.SYNC, true);
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM, CiphersConfigTestConstants.SET_CIPHERS_WITH_XML_FILE_INPUT_COMMAND,
                    getProperties(CiphersConfigTestConstants.SET_ONLY_SSH_CIPHERS_FOR_SINGLE_NODE));
            testSetCiphers("testSetCiphers_OnlySshForSingleNode_Success", command, CiphersConfigTestConstants.SET_CIPHERS_SUCCESS_RESPONSE_MESSGAE);

            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
        } catch (Exception e) {
            logger.info("Exception ocurred while running the test case testSetCiphers_OnlySshForSingleNode_Success " + e.getMessage());
            Assert.fail(e.getMessage());
        }
        logger.info("******* End: testSetCiphers_OnlySshForSingleNode_Success Test ******");

    }

    @Test
    @Ignore("To Be investigated")
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(4)
    public void testSetCiphers_OnlyTlsForSingleNode_Success() {
        logger.info("******* Start: testSetCiphers_OnlyTlsForSingleNode_Success Test ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCiphersConfig(CiphersConfigTestConstants.DG2_NODE_NAME,
                    CiphersConfigTestConstants.SYNC, true);
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM, CiphersConfigTestConstants.SET_CIPHERS_WITH_XML_FILE_INPUT_COMMAND,
                    getProperties(CiphersConfigTestConstants.SET_ONLY_TLS_CIPHERS_FOR_SINGLE_NODE));
            testSetCiphers("testSetCiphers_OnlyTlsForSingleNode_Success", command, CiphersConfigTestConstants.SET_CIPHERS_SUCCESS_RESPONSE_MESSGAE);

            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
        } catch (Exception e) {
            logger.info("Exception ocurred while running the test case testSetCiphers_OnlyTlsForSingleNode_Success " + e.getMessage());
            Assert.fail(e.getMessage());
        }
        logger.info("******* End: testSetCiphers_OnlyTlsForSingleNode_Success Test ******");

    }

    @Test
    @Ignore("To Be investigated")
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(5)
    public void testSetCiphers_OnlySshForMultipleNodes_Success() {
        logger.info("******* Start: testSetCiphers_OnlySshForMultipleNodes_Success Test ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCiphersConfig(CiphersConfigTestConstants.DG2_NODE_NAME,
                    CiphersConfigTestConstants.SYNC, true);
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCiphersConfig(CiphersConfigTestConstants.DG2_NODE_NAME_2,
                    CiphersConfigTestConstants.SYNC, true);
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM, CiphersConfigTestConstants.SET_CIPHERS_WITH_XML_FILE_INPUT_COMMAND,
                    getProperties(CiphersConfigTestConstants.SET_ONLY_SSH_CIPHERS_FOR_MULTIPLE_NODES));
            testSetCiphers("testSetCiphers_OnlySshForMultipleNodes_Success", command, CiphersConfigTestConstants.SET_CIPHERS_SUCCESS_RESPONSE_MESSGAE);

            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
        } catch (Exception e) {
            logger.info("Exception ocurred while running the test case testSetCiphers_OnlySshForMultipleNodes_Success " + e.getMessage());
            Assert.fail(e.getMessage());
        }
        logger.info("******* End: testSetCiphers_OnlySshForMultipleNodes_Success Test ******");

    }

    @Test
    @Ignore("To Be investigated")
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(6)
    public void testSetCiphers_OnlyTlsForMultipleNodes_Success() {
        logger.info("******* Start: testSetCiphers_OnlyTlsForMultipleNodes_Success Test ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCiphersConfig(CiphersConfigTestConstants.DG2_NODE_NAME,
                    CiphersConfigTestConstants.SYNC, true);
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCiphersConfig(CiphersConfigTestConstants.DG2_NODE_NAME_2,
                    CiphersConfigTestConstants.SYNC, true);
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM, CiphersConfigTestConstants.SET_CIPHERS_WITH_XML_FILE_INPUT_COMMAND,
                    getProperties(CiphersConfigTestConstants.SET_ONLY_TLS_CIPHERS_FOR_MULTIPLE_NODES));
            testSetCiphers("testSetCiphers_OnlyTlsForMultipleNodes_Success", command, CiphersConfigTestConstants.SET_CIPHERS_SUCCESS_RESPONSE_MESSGAE);

            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
        } catch (Exception e) {
            logger.info("Exception ocurred while running the test case testSetCiphers_OnlyTlsForMultipleNodes_Success " + e.getMessage());
            Assert.fail(e.getMessage());
        }
        logger.info("******* End: testSetCiphers_OnlyTlsForMultipleNodes_Success Test ******");

    }

    @Test
    @Ignore("To Be investigated")
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(7)
    public void testSetCiphers_OnlyKexForSingleNode_Success() {
        logger.info("******* Start: testSetCiphers_OnlyKexForSingleNode_Success Test ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCiphersConfig(CiphersConfigTestConstants.DG2_NODE_NAME,
                    CiphersConfigTestConstants.SYNC, true);
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM, CiphersConfigTestConstants.SET_CIPHERS_WITH_XML_FILE_INPUT_COMMAND,
                    getProperties(CiphersConfigTestConstants.SET_ONLY_KEX_CIPHERS_FOR_SINGLE_NODE));
            testSetCiphers("testSetCiphers_OnlyKexForSingleNode_Success", command, CiphersConfigTestConstants.SET_CIPHERS_SUCCESS_RESPONSE_MESSGAE);

            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
        } catch (Exception e) {
            logger.info("Exception ocurred while running the test case testSetCiphers_OnlyKexForSingleNode_Success " + e.getMessage());
            Assert.fail(e.getMessage());
        }
        logger.info("******* End: testSetCiphers_OnlyKexForSingleNode_Success Test ******");

    }

    @Test
    @Ignore("To Be investigated")
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(8)
    public void testSetCiphers_OnlyMacForSingleNode_Success() {
        logger.info("******* Start: testSetCiphers_OnlyMacForSingleNode_Success Test ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCiphersConfig(CiphersConfigTestConstants.DG2_NODE_NAME,
                    CiphersConfigTestConstants.SYNC, true);
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM, CiphersConfigTestConstants.SET_CIPHERS_WITH_XML_FILE_INPUT_COMMAND,
                    getProperties(CiphersConfigTestConstants.SET_ONLY_MAC_CIPHERS_FOR_SINGLE_NODE));
            testSetCiphers("testSetCiphers_OnlyMacForSingleNode_Success", command, CiphersConfigTestConstants.SET_CIPHERS_SUCCESS_RESPONSE_MESSGAE);

            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
        } catch (Exception e) {
            logger.info("Exception ocurred while running the test case testSetCiphers_OnlyMacForSingleNode_Success " + e.getMessage());
            Assert.fail(e.getMessage());
        }
        logger.info("******* End: testSetCiphers_OnlyMacForSingleNode_Success Test ******");

    }

    @Test
    @Ignore("To Be investigated")
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(9)
    public void testSetCiphers_OnlyEncForSingleNode_Success() {
        logger.info("******* Start: testSetCiphers_OnlyEncForSingleNode_Success Test ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCiphersConfig(CiphersConfigTestConstants.DG2_NODE_NAME,
                    CiphersConfigTestConstants.SYNC, true);
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM, CiphersConfigTestConstants.SET_CIPHERS_WITH_XML_FILE_INPUT_COMMAND,
                    getProperties(CiphersConfigTestConstants.SET_ONLY_ENC_CIPHERS_FOR_SINGLE_NODE));
            testSetCiphers("testSetCiphers_OnlyEncForSingleNode_Success", command, CiphersConfigTestConstants.SET_CIPHERS_SUCCESS_RESPONSE_MESSGAE);

            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
        } catch (Exception e) {
            logger.info("Exception ocurred while running the test case testSetCiphers_OnlyEncForSingleNode_Success " + e.getMessage());
            Assert.fail(e.getMessage());
        }
        logger.info("******* End: testSetCiphers_OnlyEncForSingleNode_Success Test ******");

    }

    @Test
    @Ignore("To Be investigated")
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(10)
    public void testSetCiphers_KexAndMacForSingleNode_Success() {
        logger.info("******* Start: testSetCiphers_KexAndMacForSingleNode_Success Test ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCiphersConfig(CiphersConfigTestConstants.DG2_NODE_NAME,
                    CiphersConfigTestConstants.SYNC, true);
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM, CiphersConfigTestConstants.SET_CIPHERS_WITH_XML_FILE_INPUT_COMMAND,
                    getProperties(CiphersConfigTestConstants.SET_KEX_AND_MAC_CIPHERS_FOR_SINGLE_NODE));
            testSetCiphers("testSetCiphers_KexAndMacForSingleNode_Success", command, CiphersConfigTestConstants.SET_CIPHERS_SUCCESS_RESPONSE_MESSGAE);

            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
        } catch (Exception e) {
            logger.info("Exception ocurred while running the test case testSetCiphers_KexAndMacForSingleNode_Success " + e.getMessage());
            Assert.fail(e.getMessage());
        }
        logger.info("******* End: testSetCiphers_KexAndMacForSingleNode_Success Test ******");

    }

    @Test
    @Ignore("To Be investigated")
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(11)
    public void testSetCiphers_KexAndEncForSingleNode_Success() {
        logger.info("******* Start: testSetCiphers_KexAndEncForSingleNode_Success Test ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCiphersConfig(CiphersConfigTestConstants.DG2_NODE_NAME,
                    CiphersConfigTestConstants.SYNC, true);
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM, CiphersConfigTestConstants.SET_CIPHERS_WITH_XML_FILE_INPUT_COMMAND,
                    getProperties(CiphersConfigTestConstants.SET_KEX_AND_ENC_CIPHERS_FOR_SINGLE_NODE));
            testSetCiphers("testSetCiphers_KexAndEncForSingleNode_Success", command, CiphersConfigTestConstants.SET_CIPHERS_SUCCESS_RESPONSE_MESSGAE);

            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
        } catch (Exception e) {
            logger.info("Exception ocurred while running the test case testSetCiphers_KexAndEncForSingleNode_Success " + e.getMessage());
            Assert.fail(e.getMessage());
        }
        logger.info("******* End: testSetCiphers_KexAndEncForSingleNode_Success Test ******");

    }

    @Test
    @Ignore("To Be investigated")
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(12)
    public void testSetCiphers_MacAndEncForSingleNode_Success() {
        logger.info("******* Start: testSetCiphers_MacAndEncForSingleNode_Success Test ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCiphersConfig(CiphersConfigTestConstants.DG2_NODE_NAME,
                    CiphersConfigTestConstants.SYNC, true);
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM, CiphersConfigTestConstants.SET_CIPHERS_WITH_XML_FILE_INPUT_COMMAND,
                    getProperties(CiphersConfigTestConstants.SET_MAC_AND_ENC_CIPHERS_FOR_SINGLE_NODE));
            testSetCiphers("testSetCiphers_MacAndEncForSingleNode_Success", command, CiphersConfigTestConstants.SET_CIPHERS_SUCCESS_RESPONSE_MESSGAE);

            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
        } catch (Exception e) {
            logger.info("Exception ocurred while running the test case testSetCiphers_MacAndEncForSingleNode_Success " + e.getMessage());
            Assert.fail(e.getMessage());
        }
        logger.info("******* End: testSetCiphers_MacAndEncForSingleNode_Success Test ******");

    }

    @Test
    //@Ignore // Passed
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(13)
    public void testSetCiphers_DuplicateSshForSingleNode_Failure() {
        logger.info("******* Start: testSetCiphers_DuplicateSshForSingleNode_Failure Test ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCiphersConfig(CiphersConfigTestConstants.DG2_NODE_NAME,
                    CiphersConfigTestConstants.SYNC,  true);
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM, CiphersConfigTestConstants.SET_CIPHERS_WITH_XML_FILE_INPUT_COMMAND,
                    getProperties(CiphersConfigTestConstants.SET_DUPLICATE_SSH_CIPHERS_FOR_SINGLE_NODE));
            testSetCiphers("testSetCiphers_DuplicateSshForSingleNode_Failure", command, CiphersConfigTestConstants.INPUT_XML_WITH_DUPLICATE_SSH_PROTOCOL_RESPONSE_MESSAGE);

            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
        } catch (Exception e) {
            logger.info("Exception ocurred while running the test case testSetCiphers_DuplicateSshForSingleNode_Failure " + e.getMessage());
            Assert.fail(e.getMessage());
        }
        logger.info("******* End: testSetCiphers_DuplicateSshForSingleNode_Failure Test ******");

    }

    @Test
    //@Ignore // Passed
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(14)
    public void testSetCiphers_DuplicateTlsForSingleNode_Failure() {
        logger.info("******* Start: testSetCiphers_DuplicateTlsForSingleNode_Failure Test ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCiphersConfig(CiphersConfigTestConstants.DG2_NODE_NAME,
                    CiphersConfigTestConstants.SYNC, true);
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM, CiphersConfigTestConstants.SET_CIPHERS_WITH_XML_FILE_INPUT_COMMAND,
                    getProperties(CiphersConfigTestConstants.SET_DUPLICATE_TLS_CIPHERS_FOR_SINGLE_NODE));
            testSetCiphers("testSetCiphers_DuplicateTlsForSingleNode_Failure", command, CiphersConfigTestConstants.INPUT_XML_WITH_DUPLICATE_TLS_PROTOCOL_RESPONSE_MESSAGE);

            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
        } catch (Exception e) {
            logger.info("Exception ocurred while running the test case testSetCiphers_DuplicateTlsForSingleNode_Failure " + e.getMessage());
            Assert.fail(e.getMessage());
        }
        logger.info("******* End: testSetCiphers_DuplicateTlsForSingleNode_Failure Test ******");

    }

    @Test
    //@Ignore // Passed
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(15)
    public void testSetCiphers_DuplicateSshTlsForSingleNode_Failure() {
        logger.info("******* Start: testSetCiphers_DuplicateSshTlsForSingleNode_Failure Test ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCiphersConfig(CiphersConfigTestConstants.DG2_NODE_NAME,
                    CiphersConfigTestConstants.SYNC, true);
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM, CiphersConfigTestConstants.SET_CIPHERS_WITH_XML_FILE_INPUT_COMMAND,
                    getProperties(CiphersConfigTestConstants.SET_DUPLICATE_TLS_SSH_CIPHERS_FOR_SINGLE_NODE));
            testSetCiphers("testSetCiphers_DuplicateSshTlsForSingleNode_Failure", command, CiphersConfigTestConstants.INPUT_XML_WITH_DUPLICATE_TLS_SSH_PROTOCOL_RESPONSE_MESSAGE);

            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
        } catch (Exception e) {
            logger.info("Exception ocurred while running the test case testSetCiphers_DuplicateSshTlsForSingleNode_Failure " + e.getMessage());
            Assert.fail(e.getMessage());
        }
        logger.info("******* End: testSetCiphers_DuplicateSshTlsForSingleNode_Failure Test ******");

    }

    @Test
    //@Ignore // Passed
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(16)
    public void testSetCiphers_InvalidInputXmlFile_Failure() {
        logger.info("******* Start: testSetCiphers_InvalidInputXmlFile_Failure Test ******");

        try {
            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
            nodeSecurityRadioNodesDataSetup.createComEcimNodeForCiphersConfig(CiphersConfigTestConstants.DG2_NODE_NAME,
                    CiphersConfigTestConstants.SYNC, true);
            final Command command = new Command(CiphersConfigTestConstants.COMMAND_SECADM, CiphersConfigTestConstants.SET_CIPHERS_WITH_XML_FILE_INPUT_COMMAND,
                    getProperties(CiphersConfigTestConstants.SET_INVALID_INPUT_XML));
            testSetCiphers("testSetCiphers_InvalidInputXmlFile_Failure", command, CiphersConfigTestConstants.INVALID_INPUT_XML_RESPONSE_MESSAGE);

            nodeSecurityRadioNodesDataSetup.deleteAllNodes();
        } catch (Exception e) {
            logger.info("Exception ocurred while running the test case testSetCiphers_InvalidInputXmlFile_Failure " + e.getMessage());
            Assert.fail(e.getMessage());
        }
        logger.info("******* End: testSetCiphers_InvalidInputXmlFile_Failure Test ******");

    }

    private Map<String, Object> getProperties(final String resourceName) throws UnsupportedEncodingException, IOException {
        final Map<String, Object> properties = new HashMap<>();
        
        InputStream inps = fileUtility.getResourceInputStream(resourceName);
        if (inps == null) {
            logger.info("getResourceProperties() : null InputStream");
            return properties;
        }
        byte[] resourceContent = null;
        logger.debug("getResourceProperties() : InputStream.available()={}", inps.available());
        if (inps.available() > 0) {
            resourceContent = new byte[inps.available()];
            inps.read(resourceContent);
            properties.put("file:", resourceContent);
        }
        logger.info("getResourceProperties() : " + resourceName + " [{}]", new String(resourceContent));

        return properties;
    }

    private void testSetCiphers(final String testCaseName, final Command command, final String expectedResponseMessage) {
        logger.info("testSetCiphers testName [{}], Command [{}]", testCaseName, command);
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
