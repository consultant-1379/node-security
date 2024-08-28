/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.nms.security.nscs.cucumber.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.cucumber.arquillian.api.CucumberGlues;

import cucumber.api.java.en.*;

@CucumberGlues
public class HelloCucumber {

    private static final Logger logger = LoggerFactory.getLogger(UserManagerSteps.class);

    @Given("^Build Test$")
    public void givenClauseBuild() {
        logger.info("**************Given Build Test***********");
    }

    @When("^Ready Test$")
    public void whenClauseReady() {
        logger.info("**************When Ready Test***********");
    }

    @Then("^Execute Test$")
    public void thenClauseExecute() {
        logger.info("**************Then Execute Test***********");
        //        Assert.assertTrue(0 == 0);
    }

}

