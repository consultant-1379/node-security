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
import com.ericsson.nms.security.nscs.cucumber.helper.NodeModelDefs;
import com.ericsson.nms.security.nscs.cucumber.helper.NodeSecurityCPPNodesDataSetup;
import com.ericsson.nms.security.nscs.cucumber.helper.laad.LaadTestDataConstants;
import com.ericsson.oss.cucumber.arquillian.api.CucumberGlues;
import com.ericsson.oss.services.scriptengine.spi.dtos.Command;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@CucumberGlues
public class LaadDistributionTestSteps {
    private static final Logger log = LoggerFactory.getLogger(LaadDistributionTestSteps.class);
    private Command getJobStatuscommand;

    @Inject
    private EServiceProducer eServiceProducer;

    @Inject
    private NodeSecurityCPPNodesDataSetup dataSetUp;

    @Inject
    private JobMonitorUtil jobMonitorUtil;

    @Given("^No dangling network elements$")
    public void clearDanglingNEs() {
        try {
            dataSetUp.deleteAllNodes();
        } catch (Exception e) {
            Assert.fail("Error while deleting Nodes from Network data base. " + e.getMessage());
        }
    }

    @Given("^A CPP network element:  \"(.*)\"$")
    public void createCppNE(final String neName) {
        log.info("Creating Network element with name {}", neName);
        try {
            dataSetUp.createNode(neName, NodeModelDefs.SyncStatusValue.SYNCHRONIZED.name());
        } catch (Exception e) {
            Assert.fail("Error while creating node " + neName);
        }
    }

    @When("^Perform LAAD distribution to the node \"(.*)\" using secadm command$")
    public void performLaadDistributionToNE(final String neName) {
        final String commandLine = LaadTestDataConstants.COMMAND_LAAD_DISTRIBUTE + neName;
        log.info("Executing secadm command: {}", commandLine);

        Command command = new Command(LaadTestDataConstants.COMMAND_SECADM, commandLine);
        final String responseMessage = eServiceProducer.getCommandHandler().execute(command).getResponseDto().toString();

        if (responseMessage.contains("Successfully started a job")) {

            final String getJobCommandString = jobMonitorUtil.getJobCommandString(responseMessage);
            if (getJobCommandString != null) {
                getJobStatuscommand = new Command("secadm", getJobCommandString);
                log.info("Perform LAAD distribution to NE completed. Command response {}", getJobCommandString);
            } else {
                Assert.fail("Command execution failed for the network element " + neName);
            }

        } else {
            Assert.fail("Command execution failed for the network element " + neName);
        }
    }

    @Then("^Verify that LAAD distribution job should be completed for the node \"(.*)\" with the work flow status \"(.*)\" and job status \"(.*)\"$")
    public void verifyLaadDistribution(final String neName, final String expectedWfStatus, final String expectedJobStatus) {
        if (jobMonitorUtil.isWorkflowStarted(getJobStatuscommand)) {
            boolean isWfSuccess = false;
            String responseMessage = "";
            int i = 1;
            do {
                log.info("Monitoring the workflow job status with 30 iterations for every 5 seconds");
                responseMessage = jobMonitorUtil.getJobStatus(getJobStatuscommand);
                jobMonitorUtil.sleep(5000);
                if (responseMessage.contains(expectedWfStatus) && responseMessage.contains(expectedJobStatus)) {
                    isWfSuccess = true;
                    log.info("Workflow completed for the node {} with matched expected result '{}'", neName, expectedWfStatus);
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
}