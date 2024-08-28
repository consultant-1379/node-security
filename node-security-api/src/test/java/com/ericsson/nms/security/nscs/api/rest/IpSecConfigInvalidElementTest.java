/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.rest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IpSecConfigInvalidElementTest {

    private static final String ELEMENT = "Element";
    private static final String ERROR_MESSAGE = "Error message";
    private static final IpSecValidityErrorCode ERROR_CODE = IpSecValidityErrorCode.IPADDRESS_TYPE;

    @Test
    public void test_Constructor() {
        IpSecConfigInvalidElement invalidElement = new IpSecConfigInvalidElement();
        assertNotNull("Null object", invalidElement);
    }

    @Test
    public void test_ConstructorWithParameters() {
        IpSecConfigInvalidElement invalidElement = new IpSecConfigInvalidElement(ELEMENT, ERROR_MESSAGE, ERROR_CODE);
        assertNotNull("Null object", invalidElement);
        assertTrue("Name mismatch", ELEMENT.equals(invalidElement.getElementName()));
        assertTrue("Error message mismatch", ERROR_MESSAGE.equals(invalidElement.getErrorMessage()));
        assertTrue("Error code mismatch", ERROR_CODE.equals(invalidElement.getErrorCode()));
    }

    @Test
    public void test_setElementName() {
        IpSecConfigInvalidElement invalidElement = new IpSecConfigInvalidElement();
        assertNotNull("Null object", invalidElement);
        invalidElement.setElementName(ELEMENT);
        assertTrue("Name mismatch", ELEMENT.equals(invalidElement.getElementName()));
    }

    @Test
    public void test_setErrorCode() {
        IpSecConfigInvalidElement invalidElement = new IpSecConfigInvalidElement();
        assertNotNull("Null object", invalidElement);
        invalidElement.setErrorCode(ERROR_CODE);
        assertTrue("Error code mismatch", ERROR_CODE.equals(invalidElement.getErrorCode()));
    }

    @Test
    public void test_setErrorMessage() {
        IpSecConfigInvalidElement invalidElement = new IpSecConfigInvalidElement();
        assertNotNull("Null object", invalidElement);
        invalidElement.setErrorMessage(ERROR_MESSAGE);
        assertTrue("Error code mismatch", ERROR_MESSAGE.equals(invalidElement.getErrorMessage()));
    }

}
