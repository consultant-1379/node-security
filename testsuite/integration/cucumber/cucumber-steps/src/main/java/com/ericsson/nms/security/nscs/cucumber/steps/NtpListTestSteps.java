/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2019
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.cucumber.steps;

import javax.inject.Inject;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.cucumber.helper.EServiceProducer;
import com.ericsson.nms.security.nscs.cucumber.helper.NodeSecurityCPPNodesDataSetup;
import com.ericsson.nms.security.nscs.cucumber.helper.NodeModelDefs;
import com.ericsson.nms.security.nscs.cucumber.helper.NodeSecurityDataConstants;
import com.ericsson.nms.security.nscs.cucumber.helper.ntp.NtpTestConstants;
import com.ericsson.oss.cucumber.arquillian.api.CucumberGlues;
import com.ericsson.oss.services.scriptengine.spi.dtos.Command;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.api.java.en.Given;


@CucumberGlues
public class NtpListTestSteps {

    private static final Logger log = LoggerFactory.getLogger(NtpListTestSteps.class);

    @Inject
    private EServiceProducer eServiceProducer;

    @Inject
    private NodeSecurityCPPNodesDataSetup dataSetUp;

    private String responseMessage;

    @Given("^A network element: \"(.*)\" with ossModelIdentity: \"(.*)\"$")
    public void createCppNENtp(final String neName, final String ossModelIdentity) {
        log.info("Creating Network element with name {}", neName);
        try {
            dataSetUp.createNode(neName, NodeModelDefs.SyncStatusValue.SYNCHRONIZED.name(), ossModelIdentity);
        } catch (final Exception e) {
            Assert.fail("CppNtpListTestSteps: Error while creating node " + neName);
        }
    }

    @When("^Perform Ntp List on the node \"(.*)\" using secadm command$")
    public void performNtpListToNENtp(final String neName) {
        final String commandLine = NtpTestConstants.COMMAND_NTP_LIST + neName;
        log.info("Executing secadm command: {}", commandLine);

        final Command command = new Command(NtpTestConstants.COMMAND_SECADM, commandLine);
        responseMessage = eServiceProducer.getCommandHandler().execute(command).getResponseDto().toString();

        log.info("Perform Ntp List to NE completed. Command response {}", responseMessage);

    }

    @Then("^Verify Ntp List on the node \"(.*)\" passed with output as \"(.*)\"$")
    public void verifyNtpListToNENtp(final String neName, final String output) {
        log.info("Verifying ntp list to the node ");

        if (responseMessage.contains("keyId") && responseMessage.contains("7693")) {
            Assert.assertTrue(responseMessage.contains("7693") && responseMessage.contains("keyId"));
            log.info("The Ntp Key ID is {} for the node {}", NodeSecurityDataConstants.NTP_KEY_ID_VALUE, neName);
        } else {
            Assert.fail("Ntp list failed for network element ");
        }
    }

}
