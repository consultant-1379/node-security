/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.cucumber.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.cucumber.arquillian.api.CucumberGlues;

import cucumber.api.java.en.*;

@CucumberGlues
public class UserManagerSteps {

    //    @Inject
    //    private UserManager userManager = null;

    private static final String ECIM_USER_NAME = "ECIM_User";
    private static final Logger logger = LoggerFactory.getLogger(UserManagerSteps.class);

    //    private List<User> ECIMUsers;

    @Given("^InjectUserManager$")
    public void givenClause() {
        logger.info("**************     UserManager Given       ***********");
        //        if (userManager == null) {
        //            logger.info("userManager NULL !!!!!!!!!!!!!!");
        //            Assert.fail();
        //        }

    }

    @When("^GetUsers$")
    public void whenClause() {
        logger.info("**************     UserManager When        ***********");
        //        final List<User> ECIMUsers = userManager.getUsers(UserType.ECIM.getDbName());
        //        if (ECIMUsers == null) {
        //            logger.info("ECIMUsers NULL !!!!!!!!!!!!!!");
        //            Assert.fail();
        //        }

    }

    @Then("^CheckUsers$")
    public void thenClause() {
        logger.info("**************     UserManager Then        ***********");
        //        Assert.assertEquals(ECIMUsers.get(0).getUserName(), ECIM_USER_NAME);
        //        Assert.assertEquals(ECIMUsers.size(), 1);

    }

}