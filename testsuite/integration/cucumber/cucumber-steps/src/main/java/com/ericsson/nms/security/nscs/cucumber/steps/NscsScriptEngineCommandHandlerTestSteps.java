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

import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.cucumber.helper.NodeSecurityCredentialsSetup;
import com.ericsson.nms.security.nscs.cucumber.helper.EServiceProducer;
import com.ericsson.oss.cucumber.arquillian.api.CucumberGlues;
import com.ericsson.oss.itpf.sdk.context.ContextService;
import com.ericsson.oss.services.scriptengine.spi.dtos.Command;
import com.ericsson.oss.services.scriptengine.spi.dtos.CommandResponseDto;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@CucumberGlues
public class NscsScriptEngineCommandHandlerTestSteps {

    @Inject
    private ContextService contextService;

    @Inject
    private EServiceProducer eServiceProducer;

    @Inject
    private NodeSecurityCredentialsSetup nodeSecurityCredentialsSetup;

    private static final Logger LOGGER = LoggerFactory.getLogger(NscsScriptEngineCommandHandlerTestSteps.class);

    private CommandResponseDto commandResponseDto;

    @Given("^A user named: \"(.*)\"$")
    public void setXTorUserInContext(final String userName) throws Exception {
        contextService.setContextValue("X-Tor-UserID", userName);
    }

    @Given("A clean environment")
    public void cleanEnvironment() throws Exception {
        nodeSecurityCredentialsSetup.deleteNodes("OSS_NE_DEF", "NetworkElement");
        nodeSecurityCredentialsSetup.deleteNodes("OSS_NE_DEF", "NetworkFunctionVirtualizationOrchestrator");
    }

    @Given("^A NetworkElement Node named: \"(.*)\"$")
    public void givenNetworkElement(String neName) throws Exception {
        nodeSecurityCredentialsSetup.createNetworkElementWithSecurityFunction(neName);
    }

    @Given("^An NFVO Node named: \"(.*)\"$")
    public void givenNfvoNode(String nfvoName) throws Exception {
        nodeSecurityCredentialsSetup.createNfvoWithSecurityFunction(nfvoName);
    }

    @When("^Execute secadm command: \"(.*)\"$")
    public void givenSecadmCommand(final String commandLine) throws Exception {
        LOGGER.info("************** Given secadm {} Command : **********", commandLine);
        Command command = new Command("secadm", commandLine);
        commandResponseDto = eServiceProducer.getCommandHandler().execute(command);
    }

    @Then("^Command execution failed$")
    public void thenCommandExecutionFailed() {
        String syntaxErrorPrefix = "[Error 10001 : Command syntax error";
        assertTrue(commandResponseDto.getResponseDto().toString().startsWith(syntaxErrorPrefix));
    }

    @Then("^Command execution completed with message containing: \"(.*)\"$")
    public void thenCommandExecutionCompletedWithMessageContaining(final String expectedPartOfMessage) {
        String actualMessage = commandResponseDto.getResponseDto().toString();
        assertTrue("The Command Response : " + actualMessage + " does NOT contain expected String: " + expectedPartOfMessage , actualMessage.contains(expectedPartOfMessage));
    }

}
