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

import com.ericsson.nms.security.nscs.cucumber.helper.HttpClientHelper;
import com.ericsson.oss.cucumber.arquillian.api.CucumberGlues;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@CucumberGlues
public class GdprRestTestSteps {

    @Inject
    HttpClientHelper httpClientHelper;

    private static final Logger LOGGER = LoggerFactory.getLogger(GdptTestSteps.class);
    private HttpResponse response;
    final Map<String, String> header = new HashMap<>();

    @Given("^Http Header for Gdpr Rest was built$")
    public void givenRestGdprAnonymize()  {
        header.put("X-Tor-UserID", "Administrator");
    }

    @When("^The Rest Post is sent$")
    public void whenRestGdprAnonymize() {
        LOGGER.info("-----------testRestGdprAnonymize starts--------------");

        final String jsonData = "{\"filename\": \"IMSI_IMEI_FILENAME_TEST\", \"salt\": \"enmapache\"}";
        response = httpClientHelper.invokeJsonPostRest("gdpr/anonymizer/anonymize", jsonData, header);
        LOGGER.info("-----------testRestGdprAnonymize finish--------------");
    }

    @When("^The Rest Post is sent with salt overwritten$")
    public void whenRestGdprAnonymizeWithSalt() throws IOException {
        LOGGER.info("-----------testRestGdprAnonymizeWithSalt starts--------------");

        final String jsonData = "{\"filename\": \"IMSI_IMEI_FILENAME_TEST\", \"salt\": \"enmapache\"}";
        response = httpClientHelper.invokeJsonPostRest("gdpr/anonymizer/anonymizeWithSalt", jsonData, header);
        if(LOGGER.isInfoEnabled()) {
            LOGGER.info("-----------testRestGdprAnonymizeWithSalt hashed name :{}", EntityUtils.toString(response.getEntity()));
        }

        LOGGER.info("-----------testRestGdprAnonymizeWithSalt finish--------------");
    }

    @Then("^The Rest response is (.*)$")
    public void thenGdprHttpResponse(int rspExpected) {
        assertEquals(rspExpected, response.getStatusLine().getStatusCode());
    }
}
