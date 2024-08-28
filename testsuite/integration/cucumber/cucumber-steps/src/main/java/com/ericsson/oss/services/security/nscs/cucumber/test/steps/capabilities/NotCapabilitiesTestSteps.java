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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
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
import com.ericsson.oss.services.security.nscs.cucumber.test.steps.helper.NscsTargetDefinition;
import com.ericsson.oss.services.security.nscs.cucumber.test.steps.helper.NscsTargetParameter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@CucumberGlues
public class NotCapabilitiesTestSteps {

    @Inject
    private EServiceProducer eServiceProducer;

    @Inject
    private JsonFileHelper jsonFileHelper;

    @Inject
    private CapabilityHelper capabilityHelper;

    private static final Logger LOGGER = LoggerFactory.getLogger(NotCapabilitiesTestSteps.class);

    private Map<String, List<NscsTargetParameter>> expectedTargetParameters = new HashMap<>();
    private Map<String, List<NscsTargetParameter>> actualTargetParameters = new HashMap<>();

    @Given("^The expected values are retrieved from JSON file \"(.*)\"$")
    public void givenGetExpectedTargetParameter(final String targetParameter) throws Exception {
        LOGGER.info("******* The expected values are retrieved from JSON file \"{}\" *******", targetParameter);
        List<NscsTargetParameter> expectedTargetParameter = expectedTargetParameters.get(targetParameter);
        if (expectedTargetParameter == null || expectedTargetParameter.isEmpty()) {
            final String jsonFilename = targetParameter + ".json";
            final InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(targetParameter + ".json");
            Assert.assertNotNull(jsonFilename + ": null input stream.", inputStream);
            final ObjectMapper readMapper = new ObjectMapper();
            final TypeReference<Collection<NscsTargetParameter>> typeRef = new TypeReference<Collection<NscsTargetParameter>>() {
            };
            try {
                expectedTargetParameter = readMapper.readValue(inputStream, typeRef);
                Assert.assertNotNull("null expected target parameters.", expectedTargetParameter);
                Assert.assertFalse("empty expected target parameters.", expectedTargetParameter.isEmpty());
                expectedTargetParameters.put(targetParameter, expectedTargetParameter);
            } catch (final IOException e) {
                Assert.fail(jsonFilename + ": no such file.");
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (final IOException e) {
                    Assert.fail(jsonFilename + ": I/O exception closing the InputStream.");
                }
            }
        }
    }

    @When("^The actual values for \"(.*)\" are retrieved for all targets in JSON file \"(.*)\" from NSCS capability service invoking method \"(.*)\"$")
    public void whenGetActualTargetParameter(final String targetParameter, final String fileName, final String methodName) throws Exception {
        LOGGER.info(
                "******* The actual values for \"{}\" are retrieved for all targets in JSON file \"{}\" from NSCS capability service invoking method \"{}\" *******",
                targetParameter, fileName, methodName);
        Method method = null;
        Method methodWithTargetModelIdentity = null;
        try {
            method = eServiceProducer.getNscsModelService().getClass().getMethod(methodName, String.class, String.class);
        } catch (final Exception e) {
            try {
                methodWithTargetModelIdentity = eServiceProducer.getNscsModelService().getClass().getMethod(methodName, String.class, String.class,
                        String.class);
            } catch (final Exception e1) {
                method = null;
            }
        }
        Assert.assertTrue(methodName + ": no such method.", method != null || methodWithTargetModelIdentity != null);
        Assert.assertFalse(methodName + ": ambiguous method.", method != null && methodWithTargetModelIdentity != null);

        final List<NscsTargetDefinition> nscsTargetDefinitions = capabilityHelper.getTargetDefinitions(fileName);
        final List<NscsTargetParameter> actualTargetParameter = actualTargetParameters.get(targetParameter) != null
                ? actualTargetParameters.get(targetParameter) : new ArrayList<NscsTargetParameter>();
        for (final NscsTargetDefinition nscsTargetDefinition : nscsTargetDefinitions) {
            final String targetCategory = nscsTargetDefinition.getTargetCategory();
            final String targetType = nscsTargetDefinition.getTargetType();
            final List<String> targetModelIdentities = nscsTargetDefinition.getTargetModelIdentities();
            Object value = null;
            if (method != null) {
                try {
                    value = method.invoke(eServiceProducer.getNscsModelService(), targetCategory, targetType);
                } catch (final Exception e) {
                    LOGGER.error("Exception [" + e.getClass().getCanonicalName() + " - " + e.getMessage() + "] invoking method [" + methodName
                            + "] with args [" + targetCategory + ", " + targetType + "]");
                }
                final NscsTargetParameter actualTargetParam = new NscsTargetParameter(targetCategory, targetType, targetModelIdentities, value);
                actualTargetParameter.add(actualTargetParam);
            } else {
                if (targetModelIdentities != null) {
                    for (final String targetModelIdentity : targetModelIdentities) {
                        try {
                            value = methodWithTargetModelIdentity.invoke(eServiceProducer.getNscsModelService(), targetCategory, targetType,
                                    targetModelIdentity);
                        } catch (final Exception e) {
                            LOGGER.error("Exception [" + e.getClass().getCanonicalName() + " - " + e.getMessage() + "] invoking method [" + methodName
                                    + "] with args [" + targetCategory + ", " + targetType + ", " + targetModelIdentity + "]");
                        }
                        addActualTargetParameter(actualTargetParameter, targetCategory, targetType, targetModelIdentity, value);
                    }
                } else {
                    try {
                        value = methodWithTargetModelIdentity.invoke(eServiceProducer.getNscsModelService(), targetCategory, targetType,
                                targetModelIdentities);
                    } catch (final Exception e) {
                        LOGGER.error("Exception [" + e.getClass().getCanonicalName() + " - " + e.getMessage() + "] invoking method [" + methodName
                                + "] with args [" + targetCategory + ", " + targetType + ", " + null + "]");
                    }
                    final NscsTargetParameter actualTargetParam = new NscsTargetParameter(targetCategory, targetType, targetModelIdentities, value);
                    actualTargetParameter.add(actualTargetParam);
                }
            }
        }
        actualTargetParameters.put(targetParameter, actualTargetParameter);
    }

    @When("^The actual values are saved in a temporary JSON file \"(.*)\"$")
    public void whenSaveActualTargetParameter(final String targetParameter) throws Exception {
        LOGGER.info("******* The actual values are saved in a temporary JSON file \"{}\" *******", targetParameter);
        jsonFileHelper.write(targetParameter, actualTargetParameters.get(targetParameter));
    }

    @Then("^All expected values for \"(.*)\" are equal to the actual ones$")
    public void thenExpectedTargetParameterEqualToActual(final String targetParameter) {
        LOGGER.info("******* All expected values for \"{}\" are equal to the actual ones *******", targetParameter);
        final List<NscsTargetParameter> expectedTargetParameter = expectedTargetParameters.get(targetParameter);
        final List<NscsTargetParameter> actualTargetParameter = actualTargetParameters.get(targetParameter);
        for (final NscsTargetParameter expectedTargetParam : expectedTargetParameter) {
            boolean found = false;
            for (final NscsTargetParameter actualTargetParam : actualTargetParameter) {
                if (actualTargetParam.equals(expectedTargetParam)) {
                    found = true;
                    break;
                }
            }
            assertTrue(targetParameter + ": NOT FOUND EXPECTED target parameter " + expectedTargetParam.toString(), found);
        }
    }

    /**
     * @param targetParameter
     * @param targetCategory
     * @param targetType
     * @param targetModelIdentity
     * @param value
     */
    private void addActualTargetParameter(final List<NscsTargetParameter> actualTargetParameter, final String targetCategory, final String targetType,
            final String targetModelIdentity, final Object value) {
        LOGGER.info("************* Adding to actual: {}, {}, {}, {}", targetCategory, targetType, targetModelIdentity,
                value != null ? value.toString() : value);
        boolean found = false;
        for (final NscsTargetParameter actualTargetParam : actualTargetParameter) {
            if (actualTargetParam.getTargetCategory().equals(targetCategory) && actualTargetParam.getTargetType().equals(targetType)
                    && ComparatorHelper.equalValue(actualTargetParam.getValue(), value)) {
                final List<String> targetModelIdentities = new ArrayList<>(actualTargetParam.getTargetModelIdentities());
                targetModelIdentities.add(targetModelIdentity);
                actualTargetParam.setTargetModelIdentities(targetModelIdentities);
                found = true;
                break;
            }
        }
        if (!found) {
            final NscsTargetParameter actualTargetParam = new NscsTargetParameter(targetCategory, targetType, Arrays.asList(targetModelIdentity),
                    value);
            actualTargetParameter.add(actualTargetParam);
        }
    }

}
