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
import com.ericsson.nms.security.nscs.cucumber.helper.JobMonitorUtil;
import com.ericsson.nms.security.nscs.cucumber.helper.ntp.NtpTestConstants;
import com.ericsson.oss.cucumber.arquillian.api.CucumberGlues;
import com.ericsson.oss.services.scriptengine.spi.dtos.Command;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@CucumberGlues
public class NtpConfigureTestSteps {

    private static final Logger log = LoggerFactory.getLogger(NtpConfigureTestSteps.class);
    private Command getJobStatuscommand;

    @Inject
    private EServiceProducer eServiceProducer;

    @Inject
    private JobMonitorUtil jobMonitorUtil;

    @When("^Perform Ntp Configure on the node \"(.*)\" using secadm command$")
    public void performNtpConfigure(final String neName) {
        final String commandLine = NtpTestConstants.COMMAND_NTP_CONFIGURE + neName;
        log.info("Executing secadm command: {}", commandLine);

        final Command command = new Command(NtpTestConstants.COMMAND_SECADM, commandLine);
        final String responseMessage = eServiceProducer.getCommandHandler().execute(command).getResponseDto().toString();

        if (responseMessage.contains("Successfully started a job")) {

            final String getJobCommandString = jobMonitorUtil.getJobCommandString(responseMessage);
            if (getJobCommandString != null) {
                getJobStatuscommand = new Command("secadm", getJobCommandString);
                log.info("Ntp Configure for the Network Element {} completed. Command response {}", neName, getJobCommandString);
            } else {
                Assert.fail("Command execution failed for the network element " + neName);
            }

        } else {
            Assert.fail("Command execution failed for the network element " + neName);
        }
    }

    @Then("^Verify the Ntp Configure job on the node \"(.*)\" with the work flow status \"(.*)\" and job status \"(.*)\"$")
    public void verifyNtpConfigure(final String neName, final String expectedWfStatus, final String expectedJobStatus) {
        if (jobMonitorUtil.isWorkflowStarted(getJobStatuscommand)) {
            boolean isWfSuccess = false;
            String responseMessage = "";
            int i = 1;
            do {
                log.info("Monitoring the workflow job status with 10 iterations for every 5 seconds");
                responseMessage = jobMonitorUtil.getJobStatus(getJobStatuscommand);
                jobMonitorUtil.sleep(5000);
                if (responseMessage.contains(expectedWfStatus) && responseMessage.contains(expectedJobStatus)) {
                    isWfSuccess = true;
                    log.info("Workflow completed for the node {} with matched expected result '{}'", neName, expectedWfStatus);
                    break;
                }
                i++;
            } while (i <= 10);
            if (!isWfSuccess) {
                log.error("Workflow completed for the node {} with mismatched expected result {}. Job response {}", neName, expectedWfStatus,
                        responseMessage);
                Assert.fail("Workflow completed for the node " + neName + " with mismatched expected result " + expectedWfStatus);
            }
        }
    }

}
