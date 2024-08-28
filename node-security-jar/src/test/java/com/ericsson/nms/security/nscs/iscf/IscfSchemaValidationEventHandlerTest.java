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

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.helpers.ValidationEventImpl;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ealemca
 */
public class IscfSchemaValidationEventHandlerTest {

    private IscfSchemaValidationEventHandler instance;
    private ValidationEvent event;
    private final Logger log = LoggerFactory.getLogger(IscfValidatorsGeneratorTest.class);

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        instance = new IscfSchemaValidationEventHandler();
    }

    @After
    public void tearDown() {
        instance = null;
        event = null;
    }

    @Test
    public void testHandleWarningEvent() {
        event = new ValidationEventImpl(ValidationEvent.WARNING, null, null);
        boolean expResult = true;
        boolean result = instance.handleEvent(event);
        assertEquals(expResult, result);
    }

    @Test
    public void testHandleErrorEvent() {
        event = new ValidationEventImpl(ValidationEvent.ERROR, null, null);
        boolean expResult = true;
        boolean result = instance.handleEvent(event);
        assertEquals(expResult, result);
    }

    @Test
    public void testHandleFatalErrorEvent() {
        event = new ValidationEventImpl(ValidationEvent.FATAL_ERROR, null, null);
        boolean expResult = false;
        boolean result = instance.handleEvent(event);
        assertEquals(expResult, result);
    }

    @Test
    public void testHandleNonExistentEvent() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Illegal severity");
        event = new ValidationEventImpl(4, null, null);
        boolean expResult = true;
        boolean result = instance.handleEvent(event);
        assertEquals(expResult, result);
    }

}
