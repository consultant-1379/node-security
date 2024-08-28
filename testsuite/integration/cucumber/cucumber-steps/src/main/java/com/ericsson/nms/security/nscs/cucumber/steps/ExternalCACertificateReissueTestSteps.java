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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.cucumber.helper.EServiceProducer;
import com.ericsson.nms.security.nscs.cucumber.helper.JobMonitorUtil;
import com.ericsson.oss.cucumber.arquillian.api.CucumberGlues;
import com.ericsson.oss.services.scriptengine.spi.dtos.Command;

import cucumber.api.java.en.*;

/**
 * This class consist of Cucumber test cases Certificate reissue usecase.
 *
 * @author xvekkar
 */
@CucumberGlues
public class ExternalCACertificateReissueTestSteps {

    @Inject
    private EServiceProducer eServiceProducer;

    @Inject
    private JobMonitorUtil jobMonitorUtil;

    private static final Logger logger = LoggerFactory.getLogger(ExternalCACertificateReissueTestSteps.class);
    private static final String COMMAND_SECADM = "secadm ";
    private static final String COMMAND_CERTIFICATE_ISSUE = "certificate issue --certtype IPSEC --xmlfile file:";
    private static final String COMMAND_CERTIFICATE_REISSUE = "certificate reissue --certtype IPSEC --nodelist ";
    private static final String EXTERNAL_CA_OPTION = " --extca";
    private static final String XML_FILE_FOR_ISSUE_BY_INTERNAL_CA = "internalCaEnrollment.xml";
    private static final String XML_FILE_FOR_ISSUE_BY_EXTERNAL_CA = "externalCaEnrollment.xml";
    private static final String XML_FILE_FOR_ISSUE_BY_EXTERNAL_CA_MULTIPLE_NODES = "externalCaEnrollmentMultipleNodes.xml";

    private String responseMessage;

    private Command getJobStatuscommandForCertificateEnrollment;

    @Given("^Certificate enrollment on the network element \"([^\"]*)\" using legacy secadm command finishes with job status \"([^\"]*)\"$")
    public void performLegacyCertificateEnrollment(final String neName, final String expectedJobStatus) throws IOException {
        final String commandLine = COMMAND_CERTIFICATE_ISSUE + XML_FILE_FOR_ISSUE_BY_INTERNAL_CA;
        logger.info("Executing secadm command: {}", commandLine);
        final Map<String, Object> properties = new HashMap<>();
        properties.put("file:", readResourceFile(XML_FILE_FOR_ISSUE_BY_INTERNAL_CA));
        final Command command = new Command(COMMAND_SECADM, commandLine, properties);
        responseMessage = eServiceProducer.getCommandHandler().execute(command).getResponseDto().toString();
        if (responseMessage.contains("Successfully started a job")) {
            final String getJobCommandString = jobMonitorUtil.getJobCommandString(responseMessage);
            if (getJobCommandString != null) {
                getJobStatuscommandForCertificateEnrollment = new Command("secadm", getJobCommandString);
                logger.info("Perform certificate enrollment to network element completed. Command response {}", getJobCommandString);
            } else {
                Assert.fail("Command execution failed for the network element " + neName);
            }
        } else {
            Assert.fail("Command execution failed for the network element " + neName);
        }

        if (jobMonitorUtil.isWorkflowStarted(getJobStatuscommandForCertificateEnrollment)) {
            boolean isWfSuccess = false;
            int i = 1;
            do {
                logger.info("Monitoring the workflow job status with 30 iterations for every 5 seconds");
                responseMessage = jobMonitorUtil.getJobStatus(getJobStatuscommandForCertificateEnrollment);
                jobMonitorUtil.sleep(5000);
                if (responseMessage.contains(expectedJobStatus)) {
                    isWfSuccess = true;
                    logger.info("Workflow completed for the node {} with matched expected result '{}'", neName, expectedJobStatus);
                    break;
                }
                i++;
            } while (i <= 30);
            if (!isWfSuccess) {
                logger.error("Workflow completed for the node {} with mismatched expected result {}. Job response {}", neName, expectedJobStatus,
                        responseMessage);
                Assert.fail("Workflow completed for the node " + neName + " with mismatched expected result " + expectedJobStatus);
            }
        }
    }

    @Given("^Certificate enrollment on the network element \"([^\"]*)\" using external CA issue command finishes with job status \"([^\"]*)\"$")
    public void performExtCaCertificateEnrollment(final String neName, final String expectedJobStatus) throws IOException {
        final String commandLine;
        final Map<String, Object> properties = new HashMap<>();
        commandLine = COMMAND_CERTIFICATE_ISSUE + XML_FILE_FOR_ISSUE_BY_EXTERNAL_CA + EXTERNAL_CA_OPTION;
        logger.info("Executing secadm command: {}", commandLine);
        properties.put("file:", readResourceFile(XML_FILE_FOR_ISSUE_BY_EXTERNAL_CA));

        final Command command = new Command(COMMAND_SECADM, commandLine, properties);
        responseMessage = eServiceProducer.getCommandHandler().execute(command).getResponseDto().toString();
        if (responseMessage.contains("Successfully started a job")) {
            final String getJobCommandString = jobMonitorUtil.getJobCommandString(responseMessage);
            if (getJobCommandString != null) {
                getJobStatuscommandForCertificateEnrollment = new Command("secadm", getJobCommandString);
                logger.info("Perform certificate enrollment on network element completed. Command response {}", getJobCommandString);
            } else {
                Assert.fail("Command execution failed for the network element " + neName);
            }
        } else {
            Assert.fail("Command execution failed for the network element " + neName);
        }

        if (jobMonitorUtil.isWorkflowStarted(getJobStatuscommandForCertificateEnrollment)) {
            boolean isWfSuccess = false;
            int i = 1;
            do {
                logger.info("Monitoring the workflow job status with 30 iterations for every 5 seconds");
                responseMessage = jobMonitorUtil.getJobStatus(getJobStatuscommandForCertificateEnrollment);
                jobMonitorUtil.sleep(5000);
                if (responseMessage.contains(expectedJobStatus)) {
                    isWfSuccess = true;
                    logger.info("Workflow completed for the node {} with matched expected result '{}'", neName, expectedJobStatus);
                    break;
                }
                i++;
            } while (i <= 30);
            if (!isWfSuccess) {
                logger.error("Workflow completed for the node {} with mismatched expected result {}. Job response {}", neName, expectedJobStatus,
                        responseMessage);
                Assert.fail("Workflow completed for the node " + neName + " with mismatched expected result " + expectedJobStatus);
            }
        }
    }

    @When("^Certificate reissue is executed on Network Element \"([^\"]*)\"$")
    public void performCertificateReissue(final String neName) {
        final String commandLine;
        commandLine = COMMAND_CERTIFICATE_REISSUE + neName;
        logger.info("Executing secadm command: {}", commandLine);
        final Command command = new Command(COMMAND_SECADM, commandLine);
        responseMessage = eServiceProducer.getCommandHandler().execute(command).getResponseDto().toString();
        if (responseMessage.contains("Successfully started a job")) {
            final String getJobCommandString = jobMonitorUtil.getJobCommandString(responseMessage);
            if (getJobCommandString != null) {
                getJobStatuscommandForCertificateEnrollment = new Command("secadm", getJobCommandString);
                logger.info("Perform certificate reissue on network element completed. Command response {}", getJobCommandString);
            } else {
                Assert.fail("Command execution failed for the network element " + neName);
            }
        } else {
            Assert.fail("Command execution failed for the network element " + neName);
        }
    }

    @Then("^Verify the status of certificate reissue on the network element \"(.*)\" with job status \"(.*)\"$")
    public void verifyCertificateReissue(final String neName, final String expectedJobStatus) {
        if (jobMonitorUtil.isWorkflowStarted(getJobStatuscommandForCertificateEnrollment)) {
            boolean isWfSuccess = false;
            int i = 1;
            do {
                logger.info("Monitoring the workflow job status with 30 iterations for every 5 seconds");
                responseMessage = jobMonitorUtil.getJobStatus(getJobStatuscommandForCertificateEnrollment);
                jobMonitorUtil.sleep(5000);
                if (responseMessage.contains(expectedJobStatus)) {
                    isWfSuccess = true;
                    logger.info("Workflow completed for the node {} with matched expected result '{}'", neName, expectedJobStatus);
                    break;
                }
                i++;
            } while (i <= 30);
            if (!isWfSuccess) {
                logger.error("Workflow completed for the node {} with mismatched expected result {}. Job response {}", neName, expectedJobStatus,
                        responseMessage);
                Assert.fail("Workflow completed for the node " + neName + " with mismatched expected result " + expectedJobStatus);
            }
        }
    }

    @Given("^Certificate enrollment on the network elements \"(.*)\" and \"(.*)\" using external CA issue command finishes with job status \"(.*)\"$")
    public void performExternalCACertificateReissue(final String neName, final String networkElement, final String expectedJobStatus)
            throws IOException {
        final String commandLine;
        final Map<String, Object> properties = new HashMap<>();
        commandLine = COMMAND_CERTIFICATE_ISSUE + XML_FILE_FOR_ISSUE_BY_EXTERNAL_CA_MULTIPLE_NODES + EXTERNAL_CA_OPTION;
        logger.info("Executing secadm command: {}", commandLine);
        properties.put("file:", readResourceFile(XML_FILE_FOR_ISSUE_BY_EXTERNAL_CA_MULTIPLE_NODES));
        final Command command = new Command(COMMAND_SECADM, commandLine, properties);
        responseMessage = eServiceProducer.getCommandHandler().execute(command).getResponseDto().toString();
        if (responseMessage.contains("Successfully started a job")) {
            final String getJobCommandString = jobMonitorUtil.getJobCommandString(responseMessage);
            if (getJobCommandString != null) {
                getJobStatuscommandForCertificateEnrollment = new Command("secadm", getJobCommandString);
                logger.info("Perform certificate enrollment on network elements completed. Command response {}", getJobCommandString);
            } else {
                Assert.fail("Command execution failed for the network element " + neName + " and " + networkElement);
            }
        } else {
            Assert.fail("Command execution failed for the network element " + neName + " and " + networkElement);
        }

        if (jobMonitorUtil.isWorkflowStarted(getJobStatuscommandForCertificateEnrollment)) {
            boolean isWfSuccess = false;
            int i = 1;
            do {
                logger.info("Monitoring the workflow job status with 30 iterations for every 5 seconds");
                responseMessage = jobMonitorUtil.getJobStatus(getJobStatuscommandForCertificateEnrollment);
                jobMonitorUtil.sleep(5000);
                if (responseMessage.contains(expectedJobStatus)) {
                    isWfSuccess = true;
                    logger.info("Workflow completed for the node {} with matched expected result '{}'", neName, expectedJobStatus);
                    break;
                }
                i++;
            } while (i <= 30);
            if (!isWfSuccess) {
                logger.error("Workflow completed for the node {} with mismatched expected result {}. Job response {}", neName, expectedJobStatus,
                        responseMessage);
                Assert.fail("Workflow completed for the node " + neName + " with mismatched expected result " + expectedJobStatus);
            }
        }
    }

    @When("^Certificate reissue is executed on Network Elements \"(.*)\" and \"(.*)\"$")
    public void performCertificateReissue(final String neName, final String networkElement) {
        final String commandLine;
        commandLine = COMMAND_CERTIFICATE_REISSUE + neName + ", " + networkElement;
        logger.info("Executing secadm command: {}", commandLine);
        final Command command = new Command(COMMAND_SECADM, commandLine);
        responseMessage = eServiceProducer.getCommandHandler().execute(command).getResponseDto().toString();
        if (responseMessage.contains("Successfully started a job")) {
            final String getJobCommandString = jobMonitorUtil.getJobCommandString(responseMessage);
            if (getJobCommandString != null) {
                getJobStatuscommandForCertificateEnrollment = new Command("secadm", getJobCommandString);
                logger.info("Perform certificate reissue on network element completed. Command response {}", getJobCommandString);
            } else {
                Assert.fail("Command execution failed for the network element " + neName);
            }
        } else {
            Assert.fail("Command execution failed for the network element " + neName);
        }
    }

    @Then("^Verify the status of certificate reissue on the network elements \"([^\"]*)\" and \"([^\"]*)\" with job status \"([^\"]*)\"$")
    public void verifyCertificateReissue(final String neName, final String networkElement, final String expectedJobStatus) {
        if (jobMonitorUtil.isWorkflowStarted(getJobStatuscommandForCertificateEnrollment)) {
            boolean isWfSuccess = false;
            int i = 1;
            do {
                logger.info("Monitoring the workflow job status with 30 iterations for every 5 seconds");
                responseMessage = jobMonitorUtil.getJobStatus(getJobStatuscommandForCertificateEnrollment);
                jobMonitorUtil.sleep(5000);
                if (responseMessage.contains(expectedJobStatus)) {
                    isWfSuccess = true;
                    logger.info("Workflow completed for the node {} with matched expected result '{}'", neName, expectedJobStatus);
                    break;
                }
                i++;
            } while (i <= 30);
            if (!isWfSuccess) {
                logger.error("Workflow completed for the node {} with mismatched expected result {}. Job response {}", neName, expectedJobStatus,
                        responseMessage);
                Assert.fail("Workflow completed for the node " + neName + " with mismatched expected result " + expectedJobStatus);
            }
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
                logger.debug("Reading resource for command input");
            }
        }
        return resourceContent;
    }
}
