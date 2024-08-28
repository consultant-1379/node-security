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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.cucumber.helper.EServiceProducer;
import com.ericsson.oss.cucumber.arquillian.api.CucumberGlues;
import com.ericsson.oss.services.security.nscs.cucumber.test.steps.helper.CapabilityHelper;
import com.ericsson.oss.services.security.nscs.cucumber.test.steps.helper.ComparatorHelper;
import com.ericsson.oss.services.security.nscs.cucumber.test.steps.helper.JsonFileHelper;
import com.ericsson.oss.services.security.nscs.cucumber.test.steps.helper.NscsCapabilityDefinition;
import com.ericsson.oss.services.security.nscs.cucumber.test.steps.helper.NscsCapabilitySupportDefinition;
import com.ericsson.oss.services.security.nscs.cucumber.test.steps.helper.NscsTargetDefinition;
import com.ericsson.oss.services.security.nscs.cucumber.test.steps.helper.NscsTargetParameter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@CucumberGlues
public class CapabilitiesTestSteps {

    @Inject
    private EServiceProducer eServiceProducer;

    @Inject
    private JsonFileHelper jsonFileHelper;

    @Inject
    private CapabilityHelper capabilityHelper;

    private static final Logger LOGGER = LoggerFactory.getLogger(CapabilitiesTestSteps.class);

    private List<NscsCapabilityDefinition> expectedCapabilityDefinitions = new ArrayList<NscsCapabilityDefinition>();
    private List<NscsCapabilityDefinition> actualCapabilityDefinitions = new ArrayList<NscsCapabilityDefinition>();
    private List<NscsCapabilitySupportDefinition> expectedCapabilitySupportDefinitions = new ArrayList<NscsCapabilitySupportDefinition>();
    private List<NscsCapabilitySupportDefinition> actualCapabilitySupportDefinitions = new ArrayList<NscsCapabilitySupportDefinition>();

    @Given("^The NSCS model service is available$")
    public void givenNscsModelServiceIsAvailable() {
        assertNotNull("The NSCS model service is null.", eServiceProducer.getNscsModelService());
    }

    @Given("^The NSCS mock capability model is not used$")
    public void givenNscsMockCapabilityModelIsNotUsed() {
        assertFalse("The NSCS mock capability model is used.", eServiceProducer.getNscsModelService().isMockCapabilityModelUsed());
    }

    @Given("^The expected NSCS capability definitions are retrieved from the JSON file \"(.*)\"$")
    public void givenGetExpectedCapabilityDefinitions(final String fileName) throws Exception {
        if (expectedCapabilityDefinitions == null || expectedCapabilityDefinitions.isEmpty()) {
            final InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(fileName + ".json");
            Assert.assertNotNull("null input stream.", inputStream);
            final ObjectMapper readMapper = new ObjectMapper();
            final TypeReference<List<NscsCapabilityDefinition>> typeRef = new TypeReference<List<NscsCapabilityDefinition>>() {
            };
            try {
                expectedCapabilityDefinitions = readMapper.readValue(inputStream, typeRef);
            } catch (final IOException e) {
                Assert.fail("I/O exception reading input stream : " + e.getMessage());
            }
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (final IOException e) {
                Assert.fail("I/O exception closing input stream : " + e.getMessage());
            }
        }
    }

    @When("^The actual NSCS capability definitions are retrieved from the NSCS capability service$")
    public void whenGetActualCapabilityDefinitions() throws Exception {
        final Map<String, List<String>> nscsCapabilities = eServiceProducer.getNscsModelService().getNscsCapabilities();
        for (final String function : nscsCapabilities.keySet()) {
            for (final String name : nscsCapabilities.get(function)) {
                final Object defaultValue = eServiceProducer.getNscsModelService().getDefaultValue(function, name);
                final NscsCapabilityDefinition capabilityDefinition = new NscsCapabilityDefinition(function, name, defaultValue);
                actualCapabilityDefinitions.add(capabilityDefinition);
            }
        }
    }

    @When("^The actual NSCS capability definitions are saved in a temporary JSON file \"(.*)\"$")
    public void whenSaveActualCapabilityDefinitions(final String tmpFileName) throws Exception {
        jsonFileHelper.write(tmpFileName, actualCapabilityDefinitions);
    }

    @Then("^All expected NSCS capability definitions are equal to the actual ones$")
    public void thenExpectedCapabilityDefinitionsEqualToActual() {
        for (final NscsCapabilityDefinition exp : expectedCapabilityDefinitions) {
            boolean found = false;
            for (final NscsCapabilityDefinition act : actualCapabilityDefinitions) {
                if (exp.equals(act)) {
                    found = true;
                    break;
                }
            }
            assertTrue("CapabilityDefinition: NOT FOUND EXPECTED " + exp.toString(), found);
        }
    }

    @Then("^All actual NSCS capability definitions are equal to the expected ones$")
    public void thenActualCapabilityDefinitionsEqualToExpected() {
        for (final NscsCapabilityDefinition act : actualCapabilityDefinitions) {
            boolean found = false;
            for (final NscsCapabilityDefinition exp : expectedCapabilityDefinitions) {
                if (act.equals(exp)) {
                    found = true;
                    break;
                }
            }
            assertTrue("CapabilityDefinition: NOT FOUND ACTUAL " + act.toString(), found);
        }
    }

    @Given("^The expected NSCS capability support definitions are retrieved from the JSON file \"(.*)\"$")
    public void givenGetExpectedCapabilitySupportDefinitions(final String fileName) throws Exception {
        if (expectedCapabilitySupportDefinitions == null || expectedCapabilitySupportDefinitions.isEmpty()) {
            final InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(fileName + ".json");
            Assert.assertNotNull("null input stream.", inputStream);
            final ObjectMapper readMapper = new ObjectMapper();
            final TypeReference<List<NscsCapabilitySupportDefinition>> typeRef = new TypeReference<List<NscsCapabilitySupportDefinition>>() {
            };
            try {
                expectedCapabilitySupportDefinitions = readMapper.readValue(inputStream, typeRef);
            } catch (final IOException e) {
                Assert.fail("I/O exception reading input stream : " + e.getMessage());
            }
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (final IOException e) {
                Assert.fail("I/O exception closing input stream : " + e.getMessage());
            }
        }
    }

    @When("^The actual NSCS capability support definitions are retrieved for all expected targets read from JSON file \"(.*)\"$")
    public void whenGetActualCapabilitySupportDefinitions(final String fileName) throws Exception {
        final List<NscsTargetDefinition> nscsTargetDefinitions = capabilityHelper.getTargetDefinitions(fileName);
        final Map<String, List<String>> nscsCapabilities = eServiceProducer.getNscsModelService().getNscsCapabilities();
        for (final String function : nscsCapabilities.keySet()) {
            for (final String name : nscsCapabilities.get(function)) {
                final Object defaultValue = eServiceProducer.getNscsModelService().getDefaultValue(function, name);
                for (final NscsTargetDefinition nscsTargetDefinition : nscsTargetDefinitions) {
                    final String targetCategory = nscsTargetDefinition.getTargetCategory();
                    final String targetType = nscsTargetDefinition.getTargetType();
                    if (nscsTargetDefinition.getTargetModelIdentities() == null || nscsTargetDefinition.getTargetModelIdentities().isEmpty()) {
                        final Object value = eServiceProducer.getNscsModelService().getCapabilityValue(targetCategory, targetType, null, function,
                                name);
                        final NscsCapabilitySupportDefinition capabilitySupportDefinition = new NscsCapabilitySupportDefinition(function, name,
                                defaultValue, targetCategory, targetType, null, value);
                        actualCapabilitySupportDefinitions.add(capabilitySupportDefinition);
                    } else {
                        for (final String targetModelIdentity : nscsTargetDefinition.getTargetModelIdentities()) {
                            final Object value = eServiceProducer.getNscsModelService().getCapabilityValue(targetCategory, targetType,
                                    targetModelIdentity, function, name);
                            addActualCapabilitySupportDefinition(function, name, defaultValue, targetCategory, targetType, targetModelIdentity,
                                    value);
                        }
                    }
                }
            }
        }
    }

    @When("^The actual NSCS capability support definitions are saved in a temporary JSON file \"(.*)\"$")
    public void whenSaveActualCapabilitySupportDefinitions(final String tmpFileName) throws Exception {
        jsonFileHelper.write(tmpFileName, actualCapabilitySupportDefinitions);
    }

    @Then("^All expected NSCS capability support definitions are equal to the actual ones$")
    public void thenExpectedCapabilitySupportDefinitionsEqualToActual() {
        for (final NscsCapabilitySupportDefinition exp : expectedCapabilitySupportDefinitions) {
            boolean found = false;
            for (final NscsCapabilitySupportDefinition act : actualCapabilitySupportDefinitions) {
                if (exp.equals(act)) {
                    found = true;
                    break;
                }
            }
            assertTrue("CapabilitySupportDefinition: NOT FOUND EXPECTED " + exp.toString(), found);
        }
    }

    @Then("^All actual NSCS capability support definitions are equal to the expected ones$")
    public void thenActualCapabilitySupportDefinitionsEqualToExpected() {
        for (final NscsCapabilitySupportDefinition act : actualCapabilitySupportDefinitions) {
            boolean found = false;
            for (final NscsCapabilitySupportDefinition exp : expectedCapabilitySupportDefinitions) {
                if (act.equals(exp)) {
                    found = true;
                    break;
                }
            }
            assertTrue("CapabilitySupportDefinition: NOT FOUND ACTUAL " + act.toString(), found);
        }
    }

    /**
     *
     * @param function
     * @param name
     * @param defaultValue
     * @param targetCategory
     * @param targetType
     * @param targetModelIdentity
     * @param value
     */
    private void addActualCapabilitySupportDefinition(final String function, final String name, final Object defaultValue,
            final String targetCategory, final String targetType, final String targetModelIdentity, final Object value) {
        LOGGER.debug("************* Adding to actual capabilitysupport: {}, {}, {}, {}, {}, {}, {}", function, name,
                defaultValue != null ? defaultValue.toString() : defaultValue, targetCategory, targetType, targetModelIdentity,
                value != null ? value.toString() : value);
        boolean found = false;
        for (final NscsCapabilitySupportDefinition actualCapabilitySupportDefinition : actualCapabilitySupportDefinitions) {
            final String actualFunction = actualCapabilitySupportDefinition.getFunction();
            final String actualName = actualCapabilitySupportDefinition.getName();
            final Object actualDefaultValue = actualCapabilitySupportDefinition.getDefaultValue();
            final String actualTargetCategory = actualCapabilitySupportDefinition.getTargetParameter().getTargetCategory();
            final String actualTargetType = actualCapabilitySupportDefinition.getTargetParameter().getTargetType();
            final Object actualValue = actualCapabilitySupportDefinition.getTargetParameter().getValue();
            final List<String> actualTargetModelIdentities = actualCapabilitySupportDefinition.getTargetParameter().getTargetModelIdentities();
            if (actualFunction.equals(function) && actualName.equals(name) && ComparatorHelper.equalValue(actualDefaultValue, defaultValue)
                    && actualTargetCategory.equals(targetCategory) && actualTargetType.equals(targetType)
                    && ComparatorHelper.equalValue(actualValue, value)) {
                final List<String> targetModelIdentities = new ArrayList<>(actualTargetModelIdentities);
                targetModelIdentities.add(targetModelIdentity);
                final NscsTargetParameter newTargetParam = new NscsTargetParameter(targetCategory, targetType, targetModelIdentities, value);
                actualCapabilitySupportDefinition.setTargetParameter(newTargetParam);
                found = true;
                break;
            }
        }
        if (!found) {
            final NscsCapabilitySupportDefinition actualCapabilitySupportDefinition = new NscsCapabilitySupportDefinition(function, name,
                    defaultValue, targetCategory, targetType, Arrays.asList(targetModelIdentity), value);
            actualCapabilitySupportDefinitions.add(actualCapabilitySupportDefinition);
        }
    }

}
