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

import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.cucumber.helper.*;
import com.ericsson.nms.security.nscs.cucumber.helper.ntp.NtpTestConstants;
import com.ericsson.oss.cucumber.arquillian.api.CucumberGlues;
import com.ericsson.oss.services.scriptengine.spi.dtos.Command;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.api.java.en.Given;

@CucumberGlues
public class NtpRemoveTestSteps {

    @Inject
    private NodeSecurityRadioNodesDataSetup dataSetup;

    @Inject
    private EServiceProducer eServiceProducer;

    @Inject
    private JobMonitorUtil jobMonitorUtil;

    private static final Logger logger = LoggerFactory.getLogger(NtpRemoveTestSteps.class);

    private String responseMessage;

    private Command getJobStatuscommandForNtpRemoval;

    @Given("^A network element : \"([^\"]*)\"$")
    public void createNetworkElement(final String neName) {
        logger.info("Creating Network element with name {}", dataSetup);
        try {
            dataSetup.createNode(neName, NodeModelDefs.SyncStatusValue.SYNCHRONIZED.name(), SecurityLevel.LEVEL_1);
        } catch (final Exception e) {
            logger.info("exception:: {}", e);
            Assert.fail("Error while creating node " + neName);
        }
    }

    @When("^Perform ntp remove on the node \"(.*)\" using secadm command$")
    public void performNtpRemovalOnInvalidNetworkElement(final String neName) {
        final String commandLine = NtpTestConstants.COMMAND_NTP_REMOVE + NtpTestConstants.COMMAND_NTP_REMOVE_NODE_NAME_OPTION + neName
                + NtpTestConstants.COMMAND_NTP_REMOVE_KEY_ID_OPTION;
        logger.info("Executing secadm command: {}", commandLine);

        final Command command = new Command(NtpTestConstants.COMMAND_SECADM, commandLine);
        responseMessage = eServiceProducer.getCommandHandler().execute(command).getResponseDto().toString();

        if (responseMessage.contains("NTP")) {
            logger.info("response message: [{}]", responseMessage);

        } else {
            Assert.fail("Command execution failed for the node" + neName);
        }
    }

    @When("^Perform ntp remove on the node \"(.*)\" using invalid secadm command$")
    public void performNtpRemovalForInvalidCommand(final String neName) {
        final String commandLine = NtpTestConstants.COMMAND_NTP_REMOVE + NtpTestConstants.COMMAND_NTP_REMOVE_NODE_NAME_OPTION + neName;
        logger.info("Executing secadm command: {}", commandLine);

        final Command command = new Command(NtpTestConstants.COMMAND_SECADM, commandLine);
        responseMessage = eServiceProducer.getCommandHandler().execute(command).getResponseDto().toString();

        if (responseMessage.contains("Command syntax error")) {
            logger.info("response message: [{}]", responseMessage);

        } else {
            Assert.fail("Command execution failed for the node" + neName);
        }
    }

    @When("^Perform ntp remove use-case on the node \"(.*)\" using secadm command$")
    public void performNtpRemovalOnNetworkElement(final String neName) {

        final String commandLine = NtpTestConstants.COMMAND_NTP_REMOVE + NtpTestConstants.COMMAND_NTP_REMOVE_NODE_NAME_OPTION + neName
                + NtpTestConstants.COMMAND_NTP_REMOVE_KEY_ID_OPTION;
        logger.info("Executing secadm command: {}", commandLine);

        final Command command = new Command(NtpTestConstants.COMMAND_SECADM, commandLine);
        logger.info("secadm command executed: {}", eServiceProducer);
        responseMessage = eServiceProducer.getCommandHandler().execute(command).getResponseDto().toString();
        logger.info("secadm command completed: {}", command);
        if (responseMessage.contains("NTP")) {
            logger.info("response message: [{}]", responseMessage);
            final String getJobCommandString = jobMonitorUtil.getJobCommandString(responseMessage);
            if (getJobCommandString != null) {
                getJobStatuscommandForNtpRemoval = new Command("secadm", getJobCommandString);
                logger.info("Perform ntp remove on network element completed. Command response {}", getJobCommandString);
            } else {
                Assert.fail("Command execution failed for the network element " + neName);
            }

        } else {
            Assert.fail("Command execution failed for the node" + neName);
        }
    }

    @Then("^Verify that ntp remove on the network element \"(.*)\" failed with message \"(.*)\"$")
    public void verifyNtpRemovalNodeValidationTest(final String neName, final String expectedWfStatus) {

        if (responseMessage.contains(expectedWfStatus)) {
            logger.info("Node Validated successfully for ntp remove on network element '{}' with error message as '{}' ", neName, expectedWfStatus);
            Assert.assertNotNull(responseMessage);
        } else {
            Assert.fail("Node Validation failed for ntp remove");
        }
    }

    @Then("^Verify that ntp remove on the network element \"(.*)\" passed with message \"(.*)\"$")
    public void verifyNtpRemovalTest(final String neName, final String expectedWfStatus) {
        if (jobMonitorUtil.isWorkflowStarted(getJobStatuscommandForNtpRemoval)) {
            boolean isWfSuccess = false;
            int i = 1;
            do {
                logger.info("Monitoring the workflow job status with 10 iterations for every 5 seconds");
                responseMessage = jobMonitorUtil.getJobStatus(getJobStatuscommandForNtpRemoval);
                jobMonitorUtil.sleep(5000);
                if (responseMessage.contains(expectedWfStatus)) {
                    isWfSuccess = true;
                    logger.info("Workflow completed for the node {} with matched expected result '{}'", neName, expectedWfStatus);
                    break;
                }
                i++;
            } while (i <= 10);
            if (!isWfSuccess) {
                logger.error("Workflow completed for the node {} with mismatched expected result {}. Job response {}", neName, expectedWfStatus,
                        responseMessage);
                Assert.fail("Workflow completed for the node " + neName + " with mismatched expected result " + expectedWfStatus);
            }
        }
    }
}
