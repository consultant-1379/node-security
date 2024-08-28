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

import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.cucumber.helper.EServiceProducer;
import com.ericsson.nms.security.nscs.cucumber.helper.JobMonitorUtil;
import com.ericsson.nms.security.nscs.cucumber.helper.NodeModelDefs;
import com.ericsson.nms.security.nscs.cucumber.helper.NodeSecurityRadioNodesDataSetup;
import com.ericsson.oss.cucumber.arquillian.api.CucumberGlues;
import com.ericsson.oss.services.scriptengine.spi.dtos.Command;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 *
 * @author xvekkar
 */
@CucumberGlues
public class ExternalCACertificateEnrollmentTestSteps {

    @Inject
    private NodeSecurityRadioNodesDataSetup dataSetup;

    @Inject
    private EServiceProducer eServiceProducer;

    @Inject
    private JobMonitorUtil jobMonitorUtil;

    private static final Logger logger = LoggerFactory
            .getLogger(ExternalCACertificateEnrollmentTestSteps.class);
    private static final String COMMAND_SECADM = "secadm ";
    private static final String COMMAND_CERTIFICATE_ISSUE = "certificate issue --certtype IPSEC --xmlfile file:";
    private static final String EXTERNAL_CA_OPTION = " --extca";
    private static final String XML_FILE_FOR_ISSUE_BY_INTERNAL_CA = "internalCaEnrollment.xml";
    private static final String XML_FILE_FOR_ISSUE_BY_EXTERNAL_CA = "externalCaEnrollment.xml";
    private static final String XML_FILE_FOR_ISSUE_BY_EXTERNAL_CA_MULTIPLE_NODES = "externalCaEnrollmentMultipleNodes.xml";
    private static final String XML_FILE_FOR_ISSUE_BY_EXTERNAL_CA_NO_TRUST_DISTRIBUTION = "externalCaEnrollmentNoTrust.xml";

    private String responseMessage;

    private Command getJobStatuscommandForCertificateEnrollment;

    @Given("^Another network element: \"([^\"]*)\" and sync status: \"([^\"]*)\"$")
    public void createNetworkElementForMultipleCase(final String neName, final Boolean syncStatus) {
        logger.info("Creating Network element with name {}", neName);
        try {
            if (syncStatus) {
                dataSetup.createNode(neName, NodeModelDefs.SyncStatusValue.SYNCHRONIZED.name(),
                        SecurityLevel.LEVEL_1);
            } else {
                dataSetup.createNode(neName, NodeModelDefs.SyncStatusValue.UNSYNCHRONIZED.name(),
                        SecurityLevel.LEVEL_1);
            }
        } catch (final Exception e) {
            logger.info("exception:: {}", e);
            Assert.fail("Error while creating node " + neName);
        }
    }

    @When("^Execute certificate enrollment on the network element \"([^\"]*)\" using legacy secadm command$")
    public void performLegacyCertificateEnrollment(final String neName) throws IOException {
        final String commandLine = COMMAND_CERTIFICATE_ISSUE + XML_FILE_FOR_ISSUE_BY_INTERNAL_CA;
        logger.info("Executing secadm command: {}", commandLine);
        final Map<String, Object> properties = new HashMap<>();
        properties.put("file:", readResourceFile(XML_FILE_FOR_ISSUE_BY_INTERNAL_CA));
        final Command command = new Command(COMMAND_SECADM, commandLine, properties);
        responseMessage = eServiceProducer.getCommandHandler().execute(command).getResponseDto()
                .toString();
        logger.info("performLegacyCertificateEnrollment::::: {}", responseMessage);
        if (responseMessage.contains("Successfully started a job")) {
            final String getJobCommandString = jobMonitorUtil.getJobCommandString(responseMessage);
            if (getJobCommandString != null) {
                getJobStatuscommandForCertificateEnrollment = new Command("secadm",
                        getJobCommandString);
                logger.info(
                        "Perform certificate enrollment to network element completed. Command response {}",
                        getJobCommandString);
            } else {
                Assert.fail("Command execution failed for the network element " + neName);
            }
        } else {
            Assert.fail("Command execution failed for the network element " + neName);
        }
    }

    @When("^Execute certificate enrollment on the network element \"([^\"]*)\" using extca option and TrustedCAInfo \"([^\"]*)\"$")
    public void performExternalCACertificateEnrollment(final String neName,
            final Boolean isTrustedCAInfoProvided) throws IOException {
        final String commandLine;
        final Map<String, Object> properties = new HashMap<>();
        if (isTrustedCAInfoProvided) {
            commandLine = COMMAND_CERTIFICATE_ISSUE + XML_FILE_FOR_ISSUE_BY_EXTERNAL_CA
                    + EXTERNAL_CA_OPTION;
            logger.info("Executing secadm command: {}", commandLine);
            properties.put("file:", readResourceFile(XML_FILE_FOR_ISSUE_BY_EXTERNAL_CA));
        } else {
            commandLine = COMMAND_CERTIFICATE_ISSUE
                    + XML_FILE_FOR_ISSUE_BY_EXTERNAL_CA_NO_TRUST_DISTRIBUTION + EXTERNAL_CA_OPTION;
            logger.info("Executing secadm command: {}", commandLine);
            properties.put("file:",
                    readResourceFile(XML_FILE_FOR_ISSUE_BY_EXTERNAL_CA_NO_TRUST_DISTRIBUTION));
        }
        final Command command = new Command(COMMAND_SECADM, commandLine, properties);
        responseMessage = eServiceProducer.getCommandHandler().execute(command).getResponseDto()
                .toString();
        logger.info("performExternalCACertificateEnrollment::::: {}", responseMessage);
        if (responseMessage.contains("Successfully started a job")) {
            final String getJobCommandString = jobMonitorUtil.getJobCommandString(responseMessage);
            if (getJobCommandString != null) {
                getJobStatuscommandForCertificateEnrollment = new Command("secadm",
                        getJobCommandString);
                logger.info(
                        "Perform certificate enrollment on network element completed. Command response {}",
                        getJobCommandString);
            } else {
                Assert.fail("Command execution failed for the network element " + neName);
            }
        } else {
            Assert.fail("Command execution failed for the network element " + neName);
        }
    }

    @Then("^Verify the status of certificate enrollment on the network element \"(.*)\" with job status \"(.*)\"$")
    public void verifyCertificateEnrollmentSuccessTest(final String neName,
            final String expectedJobStatus) {
        if (jobMonitorUtil.isWorkflowStarted(getJobStatuscommandForCertificateEnrollment)) {
            boolean isWfSuccess = false;
            int i = 1;
            do {
                logger.info("Monitoring the workflow job status with 30 iterations for every 5 seconds");
                responseMessage = jobMonitorUtil
                        .getJobStatus(getJobStatuscommandForCertificateEnrollment);
                jobMonitorUtil.sleep(5000);
                if (responseMessage.contains(expectedJobStatus)) {
                    isWfSuccess = true;
                    logger.info(
                            "Workflow completed for the node {} with matched expected result '{}'",
                            neName, expectedJobStatus);
                    break;
                }
                i++;
            } while (i <= 30);
            if (!isWfSuccess) {
                logger.error(
                        "Workflow completed for the node {} with mismatched expected result {}. Job response {}",
                        neName, expectedJobStatus, responseMessage);
                Assert.fail("Workflow completed for the node " + neName
                        + " with mismatched expected result " + expectedJobStatus);
            }
        }
    }

    @When("^Execute certificate enrollment on the network elements \"([^\"]*)\" and \"([^\"]*)\" using extca option$")
    public void performExternalCACertificateEnrollment(final String neName,
            final String networkElement) throws IOException {
        final String commandLine;
        final Map<String, Object> properties = new HashMap<>();
        commandLine = COMMAND_CERTIFICATE_ISSUE + XML_FILE_FOR_ISSUE_BY_EXTERNAL_CA_MULTIPLE_NODES
                + EXTERNAL_CA_OPTION;
        logger.info("Executing secadm command: {}", commandLine);
        properties.put("file:", readResourceFile(XML_FILE_FOR_ISSUE_BY_EXTERNAL_CA_MULTIPLE_NODES));
        final Command command = new Command(COMMAND_SECADM, commandLine, properties);
        responseMessage = eServiceProducer.getCommandHandler().execute(command).getResponseDto()
                .toString();
        logger.info("performExternalCACertificateEnrollment::::: {}", responseMessage);
        if (responseMessage.contains("Successfully started a job")) {
            final String getJobCommandString = jobMonitorUtil.getJobCommandString(responseMessage);
            if (getJobCommandString != null) {
                getJobStatuscommandForCertificateEnrollment = new Command("secadm",
                        getJobCommandString);
                logger.info(
                        "Perform certificate enrollment on network elements completed. Command response {}",
                        getJobCommandString);
            } else {
                Assert.fail("Command execution failed for the network element " + neName + " and "
                        + networkElement);
            }
        } else {
            Assert.fail("Command execution failed for the network element " + neName + " and "
                    + networkElement);
        }
    }

    @Then("^Verify the status of certificate enrollment on the network elements \"(.*)\" and \"([^\"]*)\" with job status \"(.*)\"$")
    public void verifyCertificateEnrollmentSuccessTest(final String neName,
            final String networkElement, final String expectedJobStatus) {
        if (jobMonitorUtil.isWorkflowStarted(getJobStatuscommandForCertificateEnrollment)) {
            boolean isWfSuccess = false;
            int i = 1;
            do {
                logger.info("Monitoring the workflow job status with 30 iterations for every 5 seconds");
                responseMessage = jobMonitorUtil
                        .getJobStatus(getJobStatuscommandForCertificateEnrollment);
                jobMonitorUtil.sleep(5000);
                if (responseMessage.contains(expectedJobStatus)) {
                    isWfSuccess = true;
                    logger.info(
                            "Workflow completed for the nodes {} and {} with matched expected result '{}'",
                            neName, networkElement, expectedJobStatus);
                    break;
                }
                i++;
            } while (i <= 30);
            if (!isWfSuccess) {
                logger.error(
                        "Workflow completed for the nodes {} and {} with mismatched expected result {}. Job response {}",
                        neName, networkElement, expectedJobStatus, responseMessage);
                Assert.fail("Workflow completed for the node " + neName + " and " + networkElement
                        + " with mismatched expected result " + expectedJobStatus);
            }
        }
    }

    @When("^Execute certificate enrollment on the network element \"(.*)\" using legacy secadm command and new xml$")
    public void xmlValidationForLegacyEnrollmentCommand(final String neName) throws IOException {

        final String commandLine = COMMAND_CERTIFICATE_ISSUE + XML_FILE_FOR_ISSUE_BY_EXTERNAL_CA;
        logger.info("Executing secadm command: {}", commandLine);
        final Map<String, Object> properties = new HashMap<>();
        properties.put("file:", readResourceFile(XML_FILE_FOR_ISSUE_BY_EXTERNAL_CA));
        final Command command = new Command(COMMAND_SECADM, commandLine, properties);
        responseMessage = eServiceProducer.getCommandHandler().execute(command).getResponseDto()
                .toString();
        logger.info("xmlValidationForLegacyEnrollmentCommand::::: {}", responseMessage);
    }

    @When("^Execute certificate enrollment on the network element \"([^\"]*)\" using new secadm command and old xml$")
    public void xmlValidationForNewEnrollmentCommand(final String neName) throws IOException {

        final String commandLine = COMMAND_CERTIFICATE_ISSUE + XML_FILE_FOR_ISSUE_BY_INTERNAL_CA
                + EXTERNAL_CA_OPTION;
        logger.info("Executing secadm command: {}", commandLine);
        final Map<String, Object> properties = new HashMap<>();
        properties.put("file:", readResourceFile(XML_FILE_FOR_ISSUE_BY_INTERNAL_CA));
        final Command command = new Command(COMMAND_SECADM, commandLine, properties);
        responseMessage = eServiceProducer.getCommandHandler().execute(command).getResponseDto()
                .toString();
        logger.info("xmlValidationForNewEnrollmentCommand::::: {}", responseMessage);
    }

    @When("^Execute certificate enrollment when network element does not exist$")
    public void certificateEnrollmentWhenNetworkElementDoesNotExist() throws IOException {

        final String commandLine = COMMAND_CERTIFICATE_ISSUE + XML_FILE_FOR_ISSUE_BY_EXTERNAL_CA
                + EXTERNAL_CA_OPTION;
        logger.info("Executing secadm command: {}", commandLine);
        final Map<String, Object> properties = new HashMap<>();
        properties.put("file:", readResourceFile(XML_FILE_FOR_ISSUE_BY_EXTERNAL_CA));
        final Command command = new Command(COMMAND_SECADM, commandLine, properties);
        responseMessage = eServiceProducer.getCommandHandler().execute(command).getResponseDto()
                .toString();
        logger.info("certificateEnrollmentWhenNetworkElementDoesNotExist::::: {}", responseMessage);
    }

    @Then("^Verify that certificate enrollment on the network element \"([^\"]*)\" fails with message \"([^\"]*)\"$")
    public void verifyCertificateEnrollmentNegativeTest(final String neName,
            final String expectedWfStatus) {

        if (responseMessage.contains(expectedWfStatus)) {
            logger.info(
                    "Command verifed successfully for certificate enrollment on network element '{}' with error message as '{}' ",
                    neName, expectedWfStatus);
            Assert.assertNotNull(responseMessage);
        } else {
            Assert.fail("Command verification failed for certificate enrollment");
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