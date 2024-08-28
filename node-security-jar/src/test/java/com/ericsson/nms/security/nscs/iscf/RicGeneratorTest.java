/*
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */
package com.ericsson.nms.security.nscs.iscf;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ealemca
 */
@RunWith(MockitoJUnitRunner.class)
public class RicGeneratorTest {

    private RicGenerator instance;

    @Spy
    private final Logger log = LoggerFactory.getLogger(RicGenerator.class);

    @Spy
    private IscfConfigurationBean config;

    @InjectMocks
    RicGenerator beanUnderTest;

    @Test
    public void testGenerateRIC() {
        String ric = beanUnderTest.generateRIC();
        log.debug("First RIC --> {}", ric);
        assertNotNull("generateRIC() returned a null string", ric);
        assertTrue("generateRIC() returned an empty string", ric.length() > 0);
    }

    @Test
    public void testGenerateSalt() {
        byte[] salt = beanUnderTest.generateSalt();
        log.debug("First RIC --> {}", salt);
        assertNotNull("generateSalt() returned a null array", salt);
        assertTrue("generateSalt() returned an empty array", salt.length > 0);
    }

}
