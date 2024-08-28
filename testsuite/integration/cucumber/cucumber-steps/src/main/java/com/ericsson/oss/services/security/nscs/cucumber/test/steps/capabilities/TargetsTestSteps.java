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

package com.ericsson.oss.services.security.nscs.cucumber.test.steps.capabilities;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.cucumber.helper.EServiceProducer;
import com.ericsson.oss.cucumber.arquillian.api.CucumberGlues;
import com.ericsson.oss.services.security.nscs.cucumber.test.steps.helper.CapabilityHelper;
import com.ericsson.oss.services.security.nscs.cucumber.test.steps.helper.JsonFileHelper;
import com.ericsson.oss.services.security.nscs.cucumber.test.steps.helper.NscsTargetDefinition;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@CucumberGlues
public class TargetsTestSteps {

    @Inject
    private EServiceProducer eServiceProducer;

    @Inject
    private JsonFileHelper jsonFileHelper;

    @Inject
    private CapabilityHelper capabilityHelper;

    private List<NscsTargetDefinition> expectedTargetDefinitions = new ArrayList<NscsTargetDefinition>();
    private List<NscsTargetDefinition> actualTargetDefinitions = new ArrayList<NscsTargetDefinition>();

    @Given("^The expected targets are read from the JSON file \"(.*)\"$")
    public void givenReadExpectedTargetDefinitions(final String fileName) throws Exception {
        if (expectedTargetDefinitions == null || expectedTargetDefinitions.isEmpty()) {
            expectedTargetDefinitions = capabilityHelper.getTargetDefinitions(fileName);
        }
    }

    @When("^The actual targets are retrieved from the NSCS capability service$")
    public void whenGetActualTargetDefinitions() throws Exception {
        actualTargetDefinitions = getNscsTargetDefinitions();
    }

    @When("^The actual targets are saved in a temporary JSON file \"(.*)\"$")
    public void whenSaveActualTargetDefinitions(final String tmpFileName) throws Exception {
        jsonFileHelper.write(tmpFileName, actualTargetDefinitions);
    }

    @Then("^All expected targets are equal to the actual ones$")
    public void thenExpectedTargetDefinitionsEqualToActual() {
        for (final NscsTargetDefinition exp : expectedTargetDefinitions) {
            boolean found = false;
            for (final NscsTargetDefinition act : actualTargetDefinitions) {
                if (exp.equals(act)) {
                    found = true;
                    break;
                }
            }
            assertTrue("TargetDefinition: NOT FOUND EXPECTED " + exp.toString(), found);
        }
    }

    @Then("^All actual targets are equal to the expected ones$")
    public void thenActualTargetDefinitionsEqualToExpected() {
        for (final NscsTargetDefinition act : actualTargetDefinitions) {
            boolean found = false;
            for (final NscsTargetDefinition exp : expectedTargetDefinitions) {
                if (act.equals(exp)) {
                    found = true;
                    break;
                }
            }
            assertTrue("TargetDefinition: NOT FOUND ACTUAL " + act.toString(), found);
        }
    }

    /**
     * Gets from the NSCS capability service the NSCS target definitions.
     *
     * @return the NSCS target definitions
     */
    private List<NscsTargetDefinition> getNscsTargetDefinitions() {
        final List<NscsTargetDefinition> nscsTargetDefinitions = new ArrayList<>();
        final List<String> targetCategories = eServiceProducer.getNscsModelService().getTargetCategories();
        for (final String targetCategory : targetCategories) {
            final List<String> targetTypes = eServiceProducer.getNscsModelService().getTargetTypes(targetCategory);
            for (final String targetType : targetTypes) {
                final String platform = eServiceProducer.getNscsModelService().getPlatform(targetCategory, targetType);
                final List<String> targetModelIdentities = eServiceProducer.getNscsModelService().getTargetModelIdentities(targetCategory,
                        targetType);
                final Map<String, List<String>> targetReleases = new HashMap<>();
                for (final String targetModelIdentity : targetModelIdentities) {
                    final List<String> releases = eServiceProducer.getNscsModelService().getReleases(targetCategory, targetType, targetModelIdentity);
                    targetReleases.put(targetModelIdentity, releases);
                }
                nscsTargetDefinitions.add(new NscsTargetDefinition(targetCategory, targetType, targetModelIdentities, platform, targetReleases));
            }
        }
        return nscsTargetDefinitions;
    }

}
