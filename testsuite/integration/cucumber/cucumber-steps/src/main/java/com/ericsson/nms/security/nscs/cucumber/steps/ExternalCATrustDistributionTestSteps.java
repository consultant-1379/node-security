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
public class ExternalCATrustDistributionTestSteps {

    @Inject
    private NodeSecurityRadioNodesDataSetup dataSetup;

    @Inject
    private EServiceProducer eServiceProducer;

    @Inject
    private JobMonitorUtil jobMonitorUtil;

    private static final Logger logger = LoggerFactory
            .getLogger(ExternalCATrustDistributionTestSteps.class);
    private static final String COMMAND_SECADM = "secadm ";
    private static final String COMMAND_TRUST_DISTRIBUTE_LEGACY = "trust distribute --trustcategory IPSEC --nodelist ";
    private static final String COMMAND_TRUST_DISTRIBUTE_EXTCA = "trust distribute --trustcategory IPSEC --xmlfile file:trust.xml";
    private static final String TRUST_DISTRIBUTE_EXTERNAL_CA_OPTION = " --extca";
    private static final String XML_FILE_FOR_TRUST = "trust.xml";
    private static final String XML_FILE_FOR_TRUST_MULTIPLE_NODES = "trustMultipleNodes.xml";

    private String responseMessage;

    private Command getJobStatuscommandForTrustDistribute;

    @Given("^A network element: \"([^\"]*)\" and sync status: \"([^\"]*)\"$")
    public void createNetworkElement(final String neName, final Boolean syncStatus) {
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

    @When("^Execute trust distribution on the network element \"(.*)\" using legacy secadm command$")
    public void performTrustDistributionOnNetworkElement(final String neName) {
        final String commandLine = COMMAND_TRUST_DISTRIBUTE_LEGACY + neName;
        logger.info("Executing secadm command: {}", commandLine);

        final Command command = new Command(COMMAND_SECADM, commandLine);
        responseMessage = eServiceProducer.getCommandHandler().execute(command).getResponseDto()
                .toString();

        if (responseMessage.contains("Successfully started a job")) {

            final String getJobCommandString = jobMonitorUtil.getJobCommandString(responseMessage);
            if (getJobCommandString != null) {
                getJobStatuscommandForTrustDistribute = new Command("secadm", getJobCommandString);
                logger.info(
                        "Perform trust distribution to network element completed. Command response {}",
                        getJobCommandString);
            } else {
                Assert.fail("Command execution failed for the network element " + neName);
            }

        } else {
            Assert.fail("Command execution failed for the network element " + neName);
        }
    }

    @When("^Execute trust distribution on the network element \"(.*)\" using xml file and extca option$")
    public void performExternalCATrustDistributeOnNetworkElement(final String neName)
            throws IOException {

        final String commandLine = COMMAND_TRUST_DISTRIBUTE_EXTCA
                + TRUST_DISTRIBUTE_EXTERNAL_CA_OPTION;
        logger.info("Executing secadm command: {}", commandLine);
        final Map<String, Object> properties = new HashMap<>();
        properties.put("file:", readResourceFile(XML_FILE_FOR_TRUST));
        final Command command = new Command(COMMAND_SECADM, commandLine, properties);
        responseMessage = eServiceProducer.getCommandHandler().execute(command).getResponseDto()
                .toString();

        if (responseMessage.contains("Successfully started a job")) {

            final String getJobCommandString = jobMonitorUtil.getJobCommandString(responseMessage);
            if (getJobCommandString != null) {
                getJobStatuscommandForTrustDistribute = new Command("secadm", getJobCommandString);
                logger.info(
                        "Perform trust distribution to network element completed. Command response {}",
                        getJobCommandString);
            } else {
                Assert.fail("Command execution failed for the network element " + neName);
            }

        } else {
            Assert.fail("Command execution failed for the network element " + neName);
        }
    }

    @Then("^Verify the status of Trust distribution on the network element \"(.*)\" with job status \"(.*)\"$")
    public void verifyTrustDistributionSuccessTest(final String neName,
            final String expectedJobStatus) {
        if (jobMonitorUtil.isWorkflowStarted(getJobStatuscommandForTrustDistribute)) {
            boolean isWfSuccess = false;
            int i = 1;
            do {
                logger.info("Monitoring the workflow job status with 30 iterations for every 5 seconds");
                responseMessage = jobMonitorUtil
                        .getJobStatus(getJobStatuscommandForTrustDistribute);
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

    @When("^Execute trust distribution on multiple network element \"(.*)\" and \"(.*)\" using xml file and extca option$")
    public void performExternalCATrustDistributeOnNetworkElement(final String neName,
            final String networkElement) throws IOException {

        final String commandLine = COMMAND_TRUST_DISTRIBUTE_EXTCA
                + TRUST_DISTRIBUTE_EXTERNAL_CA_OPTION;
        logger.info("Executing secadm command: {}", commandLine);
        final Map<String, Object> properties = new HashMap<>();
        properties.put("file:", readResourceFile(XML_FILE_FOR_TRUST_MULTIPLE_NODES));
        final Command command = new Command(COMMAND_SECADM, commandLine, properties);
        responseMessage = eServiceProducer.getCommandHandler().execute(command).getResponseDto()
                .toString();
        logger.info("performExternalCATrustDistributeOnNetworkElementmultiple::::: {}",
                responseMessage);
        if (responseMessage.contains("Successfully started a job")) {

            final String getJobCommandString = jobMonitorUtil.getJobCommandString(responseMessage);
            if (getJobCommandString != null) {
                getJobStatuscommandForTrustDistribute = new Command("secadm", getJobCommandString);
                logger.info(
                        "Perform trust distribution to network element completed. Command response {}",
                        getJobCommandString);
            } else {
                Assert.fail("Command execution failed for the network elements " + neName + " and "
                        + networkElement);
            }
        } else {
            Assert.fail("Command execution failed for the network elements " + neName + " and "
                    + networkElement);
        }
    }

    @Then("^Verify the status of Trust distribution on network elements \"(.*)\" and \"(.*)\" with job status \"(.*)\"$")
    public void verifyTrustDistributionSuccessTest(final String neName,
            final String networkElement, final String expectedJobStatus) {
        if (jobMonitorUtil.isWorkflowStarted(getJobStatuscommandForTrustDistribute)) {
            boolean isWfSuccess = false;
            int i = 1;
            do {
                logger.info("Monitoring the workflow job status with 30 iterations for every 5 seconds");
                responseMessage = jobMonitorUtil
                        .getJobStatus(getJobStatuscommandForTrustDistribute);
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
                Assert.fail("Workflow completed for the nodes " + neName + " and " + networkElement
                        + " with mismatched expected result " + expectedJobStatus);
            }
        }
    }

    @When("^Execute trust distribution on the network element \"(.*)\" with xml and without extca option$")
    public void invaildExternalCATrustDistributionCommand(final String neName) throws IOException {

        final String commandLine = COMMAND_TRUST_DISTRIBUTE_EXTCA;
        logger.info("Executing secadm command: {}", commandLine);
        final Map<String, Object> properties = new HashMap<>();
        properties.put("file:", readResourceFile(XML_FILE_FOR_TRUST));
        final Command command = new Command(COMMAND_SECADM, commandLine, properties);
        responseMessage = eServiceProducer.getCommandHandler().execute(command).getResponseDto()
                .toString();

    }

    @When("^Execute trust distribution on the network element \"(.*)\" with extca option and without xml$")
    public void invaildLagecyTrustDistributionCommand(final String neName) {

        final String commandLine = COMMAND_TRUST_DISTRIBUTE_LEGACY + neName
                + TRUST_DISTRIBUTE_EXTERNAL_CA_OPTION;
        logger.info("Executing secadm command: {}", commandLine);

        final Command command = new Command(COMMAND_SECADM, commandLine);
        responseMessage = eServiceProducer.getCommandHandler().execute(command).getResponseDto()
                .toString();
    }

    @When("^Execute trust distribution on unsynchronized network element \"(.*)\"$")
    public void trustDistributionOnUnsynchronizedNetworkElement(final String neName)
            throws IOException {

        final String commandLine = COMMAND_TRUST_DISTRIBUTE_EXTCA
                + TRUST_DISTRIBUTE_EXTERNAL_CA_OPTION;
        logger.info("Executing secadm command: {}", commandLine);
        final Map<String, Object> properties = new HashMap<>();
        properties.put("file:", readResourceFile(XML_FILE_FOR_TRUST));
        final Command command = new Command(COMMAND_SECADM, commandLine, properties);
        responseMessage = eServiceProducer.getCommandHandler().execute(command).getResponseDto()
                .toString();

    }

    @Then("^Verify that Trust distribution on the network element \"(.*)\" fails with message \"(.*)\"$")
    public void verifyTrustDistributionNegativeTest(final String neName,
            final String expectedWfStatus) {

        if (responseMessage.contains(expectedWfStatus)) {
            logger.info(
                    "Command verifed successfully for trust distribution on network element '{}' with error message as '{}' ",
                    neName, expectedWfStatus);
            Assert.assertNotNull(responseMessage);
        } else {
            Assert.fail("Command verification failed for trust distribution");
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