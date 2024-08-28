/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
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
import com.ericsson.nms.security.nscs.cucumber.helper.JobMonitorUtil;
import com.ericsson.nms.security.nscs.cucumber.helper.laad.LaadTestDataConstants;
import com.ericsson.oss.cucumber.arquillian.api.CucumberGlues;
import com.ericsson.oss.services.scriptengine.spi.dtos.Command;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@CucumberGlues
public class TrustDistributionForLaadTestSteps {
    private static final Logger log = LoggerFactory.getLogger(TrustDistributionForLaadTestSteps.class);
    private Command getJobStatuscommandForTrustDistribute;
    private Command getJobStatuscommandForTrustRemove;

    @Inject
    private EServiceProducer eServiceProducer;

    @Inject
    private JobMonitorUtil jobMonitorUtil;

    @When("^Distribute certificates with LAAD trust category to the node \"(.*)\" using secadm command$")
    public void performTrustDistributionToNE(final String neName) {
        final String commandLine = LaadTestDataConstants.COMMAND_TRUST_DISTRIBUTE + neName;
        log.info("Executing secadm command: {}", commandLine);

        Command command = new Command(LaadTestDataConstants.COMMAND_SECADM, commandLine);
        final String responseMessage = eServiceProducer.getCommandHandler().execute(command).getResponseDto().toString();

        if (responseMessage.contains("Successfully started a job")) {

            final String getJobCommandString = jobMonitorUtil.getJobCommandString(responseMessage);
            if (getJobCommandString != null) {
                getJobStatuscommandForTrustDistribute = new Command("secadm", getJobCommandString);
                log.info("Perform trust distribution to NE completed. Command response {}", getJobCommandString);
            } else {
                Assert.fail("Command execution failed for the network element " + neName);
            }

        } else {
            Assert.fail("Command execution failed for the network element " + neName);
        }
    }

    @Then("^Verify that trust distribution job should be completed for the node \"(.*)\" with job status \"(.*)\"$")
    public void verifyTrustDistribution(final String neName, final String expectedWfStatus) {
        if (jobMonitorUtil.isWorkflowStarted(getJobStatuscommandForTrustDistribute)) {
            String responseMessage = "";
            boolean isWfSuccess = false;
            int i = 1;
            do {
                log.info("Monitoring the workflow job status with 30 iterations for every 5 seconds");
                responseMessage = jobMonitorUtil.getJobStatus(getJobStatuscommandForTrustDistribute);
                jobMonitorUtil.sleep(5000);
                if (responseMessage.contains(expectedWfStatus)) {
                    isWfSuccess=true;
                    log.info("Workflow completed for the node {} with matched expected result {}", neName, expectedWfStatus);
                    break;
                }
                i++;
            } while (i <= 30);
            if (!isWfSuccess) {
                log.error("Workflow completed for the node {} with mismatched expected result {}. Job response {}", neName, expectedWfStatus,
                        responseMessage);
                Assert.fail("Workflow completed for the node " + neName + " with mismatched expected result " + expectedWfStatus);
            }
        }
    }

    @Then("^Trust get with category LAAD on the node \"(.*)\" should return CA certificate with CN \"(.*)\"$")
    public void verifyTrustGetStatus(final String neName, final String caName) {

        final String commandLine = LaadTestDataConstants.COMMAND_TRUST_GET + neName;

        Command command = new Command(LaadTestDataConstants.COMMAND_SECADM, commandLine);
        final String responseMessage = eServiceProducer.getCommandHandler().execute(command).getResponseDto().toString();
        if (responseMessage.contains(caName)) {
            log.info("Command executed for the node {} with expeced CA certificate {} on the node", neName, responseMessage);
        } else {
            Assert.fail("Trust get failed for the node " + neName);
        }
    }

    @When("^Remove certificates with LAAD trust category to the node \"(.*)\" using secadm command$")
    public void performTrustRemove(final String neName) {

        final String commandLine = LaadTestDataConstants.COMMAND_TRUST_REMOVE + neName;
        log.info("Executing secadm command: {}", commandLine);

        Command command = new Command(LaadTestDataConstants.COMMAND_SECADM, commandLine);
        final String responseMessage = eServiceProducer.getCommandHandler().execute(command).getResponseDto().toString();
        log.info("Trust remove response for the network element {} is {}", neName, responseMessage);

        if (responseMessage.contains("Successfully started a job")) {
            final String getJobCommandString = jobMonitorUtil.getJobCommandString(responseMessage);
            if (getJobCommandString != null) {
                getJobStatuscommandForTrustRemove = new Command("secadm", getJobCommandString);
                log.info("Perform trust removal to node completed. Command response {}", getJobCommandString);
            } else {
                Assert.fail("Command execution failed for the node " + neName);
            }
        } else {
            Assert.fail("Command execution failed for the node " + neName);
        }
    }

    @Then("^Verify that trust remove job should be completed for the node  \"(.*)\" with the work flow status \"(.*)\"$")
    public void verifyTrustRemove(final String neName, final String expectedWfStatus) {
        if (jobMonitorUtil.isWorkflowStarted(getJobStatuscommandForTrustRemove)) {
            String responseMessage = "";
            int i = 1;
            do {
                log.info("Monitoring the workflow job status with 20 iterations for every 5 seconds");
                responseMessage = jobMonitorUtil.getJobStatus(getJobStatuscommandForTrustRemove);
                jobMonitorUtil.sleep(5000);
                if (responseMessage.contains(expectedWfStatus)) {
                    log.info("Workflow completed for the node {} with matched expected result {}. Job response {}", neName, expectedWfStatus,
                            responseMessage);
                    break;
                }
                i++;
            } while (i <= 20);
            if (!responseMessage.contains(expectedWfStatus)) {
                Assert.fail("Workflow completed for the node " + neName + " with mismatched expected result " + expectedWfStatus);
            }
        }
    }
}