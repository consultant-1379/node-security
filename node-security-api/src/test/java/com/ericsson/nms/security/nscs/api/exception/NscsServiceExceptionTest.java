/*
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package com.ericsson.nms.security.nscs.api.exception;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ealemca
 */
public class NscsServiceExceptionTest {

    private final Logger log = LoggerFactory.getLogger(NscsServiceException.class);

    public NscsServiceExceptionTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getErrorCode method, of class NscsServiceException.
     */
    @Test
    public void testGetErrorCode() {
        log.info("Testing NscsServiceException.getErrorCode()");
        NscsServiceException instance = new NscsServiceExceptionImpl();
        int expResult = NscsServiceException.ERROR_CODE_START_INT +
                NscsServiceException.ErrorType.UNDEFINED.toInt();
        int result = instance.getErrorCode();
        assertEquals(expResult, result);
    }

    /**
     * Test of getErrorCode method, of class NscsServiceException using String.valueOf().
     */
    @Test
    public void testGetErrorCodeUsingStringValue() {
        log.info("Testing NscsServiceException.getErrorCode() using String.valueOf()");
        NscsServiceException instance = new NscsServiceExceptionImpl();
        String expResult = String.valueOf(NscsServiceException.ErrorType.UNDEFINED.toInt());
        String result = instance.getErrorType().toString();
        assertEquals(expResult, result);
    }

    /**
     * Test of getErrorType method, of class NscsServiceException.
     */
//    @Test
    public void testGetErrorType() {
        System.out.println("getErrorType");
        NscsServiceException instance = new NscsServiceExceptionImpl();
        NscsServiceException.ErrorType expResult = null;
        NscsServiceException.ErrorType result = instance.getErrorType();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSuggestedSolution method, of class NscsServiceException.
     */
//    @Test
    public void testGetSuggestedSolution() {
        System.out.println("getSuggestedSolution");
        NscsServiceException instance = new NscsServiceExceptionImpl();
        String expResult = "";
        String result = instance.getSuggestedSolution();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setSuggestedSolution method, of class NscsServiceException.
     */
//    @Test
    public void testSetSuggestedSolution_String() {
        System.out.println("setSuggestedSolution");
        String suggestedSolution = "";
        NscsServiceException instance = new NscsServiceExceptionImpl();
        NscsServiceException expResult = null;
        NscsServiceException result = instance.setSuggestedSolution(suggestedSolution);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setSuggestedSolution method, of class NscsServiceException.
     */
//    @Test
    public void testSetSuggestedSolution_String_ObjectArr() {
        System.out.println("setSuggestedSolution");
        String suggestedSolution = "";
        Object[] args = null;
        NscsServiceException instance = new NscsServiceExceptionImpl();
        NscsServiceException expResult = null;
        NscsServiceException result = instance.setSuggestedSolution(suggestedSolution, args);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of formatMessage method, of class NscsServiceException.
     */
//    @Test
    public void testFormatMessage() {
        System.out.println("formatMessage");
        String part1 = "";
        String part2 = "";
        String expResult = "";
        String result = NscsServiceException.formatMessage(part1, part2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of equals method, of class NscsServiceException.
     */
    @Test
    public void testEquals() {
        log.info("Testing NscsServiceException.equals()");
        NscsServiceException instance1 = new NscsServiceExceptionImpl();
        NscsServiceException instance2 = new NscsServiceExceptionImpl();
        boolean expResult = true;
        boolean result = instance1.equals(instance2);
        assertEquals(expResult, result);
        assertTrue(instance1.equals(instance1));
        NscsServiceException instance3 = new NscsServiceExceptionImpl2();
        assertFalse( instance1.equals(instance3) );
        assertFalse( instance1.equals(new String()) );
        
    }

    /**
     * Test of hashCode method, of class NscsServiceException.
     */
    @Test
    public void testHashCode() {
        log.info("Testing NscsServiceException.hashCode()");
        NscsServiceException instance = new NscsServiceExceptionImpl();
        int expResult = instance.getErrorType().hashCode();
        int result = instance.hashCode();
        assertEquals(expResult, result);
    }

    public class NscsServiceExceptionImpl extends NscsServiceException {

        @Override
        public ErrorType getErrorType() {
            return NscsServiceException.ErrorType.UNDEFINED;
        }
    }

    public class NscsServiceExceptionImpl2 extends NscsServiceException {

        @Override
        public ErrorType getErrorType() {
            return NscsServiceException.ErrorType.COMMAND_SYNTAX_ERROR;
        }
    }

}
