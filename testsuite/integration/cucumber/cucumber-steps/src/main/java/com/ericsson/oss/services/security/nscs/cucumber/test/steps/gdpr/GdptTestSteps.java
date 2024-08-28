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

package com.ericsson.oss.services.security.nscs.cucumber.test.steps.gdpr;

import com.ericsson.nms.security.nscs.cucumber.helper.EServiceProducer;
import com.ericsson.oss.cucumber.arquillian.api.CucumberGlues;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static org.junit.Assert.*;

@CucumberGlues
public class GdptTestSteps {

    @Inject
    private EServiceProducer eServiceProducer;

    private static final Logger LOGGER = LoggerFactory.getLogger(GdptTestSteps.class);
    private static final String TEST_FILENAME = "IMSI_IMEI_FILENAME";
    private static final String TEST_SALT = "enmapache";

    private String hashedFilename = null;

    @Given("^The Gdpr service is available$")
    public void givenGdprServiceIsAvailable() {
        assertNotNull("The GDPR service is null.", eServiceProducer.getGdprService());
    }

    @When("^The hashed filename is returned$")
    public void whenGdprBuildAnonymization() {
        try {
            hashedFilename = eServiceProducer.getGdprService().gdprBuildAnonymization(TEST_FILENAME);
            LOGGER.info("hashed filename : {}", hashedFilename);
        } catch ( Exception e) {
            LOGGER.info(e.getMessage());
        }
    }

    @When("^The hashed filename after overwritten salt is returned$")
    public void whenGdprBuildAnonymizationWithSalt()  {
        try {
            hashedFilename = eServiceProducer.getGdprService().gdprBuildAnonymization(TEST_FILENAME,TEST_SALT);
            LOGGER.info("hashed filename : {}", hashedFilename);
        } catch ( Exception e) {
            LOGGER.info(e.getMessage());
        }
    }

    @Then("^The hashed filename is null$")
    public void thenHashedFilenameIsNull() {
        assertNull(hashedFilename);
    }

    @Then("^The hashed filename is not null$")
    public void thenHashedFilenameIsNullIsNotNull() {
        assertNotNull(hashedFilename);
    }
}