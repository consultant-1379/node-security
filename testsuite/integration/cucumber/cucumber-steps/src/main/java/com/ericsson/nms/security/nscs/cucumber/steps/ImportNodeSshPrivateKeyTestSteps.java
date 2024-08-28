/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.cucumber.steps;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.cucumber.helper.EServiceProducer;
import com.ericsson.nms.security.nscs.cucumber.helper.SshkeyImportSetup;
import com.ericsson.oss.cucumber.arquillian.api.CucumberGlues;
import com.ericsson.oss.services.scriptengine.spi.dtos.Command;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 *
 * @author zkttmnk
 */
@CucumberGlues
public class ImportNodeSshPrivateKeyTestSteps {

    @Inject
    private EServiceProducer eServiceProducer;

    @Inject
    private SshkeyImportSetup sshkeyImportSetup;

    private static final Logger logger = LoggerFactory.getLogger(ImportNodeSshPrivateKeyTestSteps.class);
    private static final String COMMAND_SECADM = "secadm ";
    private static final String COMMAND_SSHKEY_IMPORT_WITH_PARAMETERS = "sshkey import --sshprivatekeyfile file:NodeSshPrivateKeyValidFile.txt --nodename ";
    private static final String PRIVATE_KEY_FILE_FOR_SSHKEY_IMPORT = "NodeSshPrivateKeyValidFile.txt";

    private String responseMessage;

    @Given("^A vECE neType Node named: \"(.*)\"$")
    public void givenNetworkElement(final String neName) throws Exception {
        sshkeyImportSetup.createNetworkElementWithSecurityFunctionAndMeContext(neName);
    }

    @When("^Execute sshkey import on the network element \"(.*)\" using sshprivatekey file and sshprivatekeyfile option$")
    public void performSshkeyImportOnNetworkElement(final String neName) throws IOException {

        final String commandLine = COMMAND_SSHKEY_IMPORT_WITH_PARAMETERS + neName;
        logger.info("Executing secadm command: {}", commandLine);

        final Map<String, Object> properties = new HashMap<>();
        properties.put("file:", readResourceFile(PRIVATE_KEY_FILE_FOR_SSHKEY_IMPORT));
        properties.put("fileName", PRIVATE_KEY_FILE_FOR_SSHKEY_IMPORT);

        final Command command = new Command(COMMAND_SECADM, commandLine, properties);
        responseMessage = eServiceProducer.getCommandHandler().execute(command).getResponseDto().toString();
        logger.info("responseMessage for {} :\n {}", commandLine, responseMessage);
    }

    @Then("^Verify the response of sshkey import on the network element \"(.*)\"$")
    public void verifySshkeyImportSuccessTest(final String neName) {
        logger.info("responseMessage for sshkey import command: {} ", responseMessage);
        if (responseMessage.contains("SshPrivatekey import command executed Successfully")) {
            logger.info("Sshkey Import Successful for network element {}, Command response {}", neName, responseMessage);
        } else {
            logger.info("Sshkey Import failed for network element{}, Command response {}", neName, responseMessage);
            Assert.fail("Command execution failed for the network element " + neName);
        }
    }

    @Then("^Verify the response of sshkey import on the network element \"(.*)\" which do not have secure credentials defined$")
    public void verifySshkeyImportFailureTest(final String neName) {
        logger.info("responseMessage for sshkey import command for ne {} : {} ", neName, responseMessage);
        if (responseMessage.contains("Node Ssh Private Key import failed")
                && responseMessage.contains("The node specified requires the node credentials to be defined")) {
            logger.info("Command execution successful for the network element  {}, Command response {}", neName, responseMessage);
        } else {
            Assert.fail("Command execution failed for the network element " + neName);
        }
    }

    public byte[] readResourceFile(final String resourcePath) throws IOException {

        byte[] resourceContent = null;
        final InputStream inps = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (inps == null) {
            return resourceContent;
        }
        if (inps.available() > 0) {
            resourceContent = new byte[inps.available()];
            while (inps.read(resourceContent) > 0) {
                logger.debug("Reading resource for sshkey import command input");
            }
        }
        return resourceContent;
    }
}