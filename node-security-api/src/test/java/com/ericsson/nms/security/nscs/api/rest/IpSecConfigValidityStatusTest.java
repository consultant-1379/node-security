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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class IpSecConfigValidityStatusTest {

    private static final String NAME = "ABCabc 123";
    private static final String ELEMENT = "Element";
    private static final String ERROR_MESSAGE = "Error message";
    private static final IpSecValidityErrorCode ERROR_CODE = IpSecValidityErrorCode.IPADDRESS_TYPE;

    @Test
    public void test_Constructor() {
        IpSecConfigValidityStatus status = new IpSecConfigValidityStatus();
        status.setName(NAME);
        status.setIpsecConfigInvalidElements(new ArrayList<IpSecConfigInvalidElement>());
        assertNotNull("IpSecConfigValidityStatus is null", status);
    }

    @Test
    public void test_setName() {
        IpSecConfigValidityStatus status = new IpSecConfigValidityStatus();
        status.setName(NAME);
        assertTrue("Name mismatched", NAME.equals(status.getName()));
    }

    @Test
    public void test_setIpsecConfigInvalidElements() {
        IpSecConfigValidityStatus status = new IpSecConfigValidityStatus();
        status.setName(NAME);
        List<IpSecConfigInvalidElement> list = new ArrayList<IpSecConfigInvalidElement>();
        IpSecConfigInvalidElement invalid = new IpSecConfigInvalidElement(ELEMENT, ERROR_MESSAGE, ERROR_CODE);
        IpSecConfigInvalidElement invalid2 = new IpSecConfigInvalidElement(ELEMENT + "333", ERROR_MESSAGE + " aaaaa", ERROR_CODE);
        list.add(invalid);
        list.add(invalid2);
        status.setIpsecConfigInvalidElements(list);

        assertNotNull("Null invalid element list", status.getIpsecConfigInvalidElements());
        assertTrue("Invalid list size mismatch", status.getIpsecConfigInvalidElements().size() == 2);
        IpSecConfigInvalidElement readData = status.getIpsecConfigInvalidElements().get(0);
        assertTrue("Name mismatched", ELEMENT.equals(readData.getElementName()));
        assertTrue("Error mismatched", ERROR_MESSAGE.equals(readData.getErrorMessage()));
        assertTrue("Error code mismatched", ERROR_CODE.equals(readData.getErrorCode()));

    }

    @Test
    public void test_areEquals() {
        IpSecConfigValidityStatus statusA = new IpSecConfigValidityStatus();
        statusA.setName(NAME);
        List<IpSecConfigInvalidElement> listA = new ArrayList<IpSecConfigInvalidElement>();
        IpSecConfigInvalidElement invalidA1 = new IpSecConfigInvalidElement(ELEMENT, ERROR_MESSAGE, ERROR_CODE);
        IpSecConfigInvalidElement invalidA2 = new IpSecConfigInvalidElement(ELEMENT + "333", ERROR_MESSAGE + " aaaaa", ERROR_CODE);
        listA.add(invalidA1);
        listA.add(invalidA2);
        statusA.setIpsecConfigInvalidElements(listA);

        IpSecConfigValidityStatus statusB = new IpSecConfigValidityStatus();
        statusB.setName(NAME);
        List<IpSecConfigInvalidElement> listB = new ArrayList<IpSecConfigInvalidElement>();
        IpSecConfigInvalidElement invalidB1 = new IpSecConfigInvalidElement(ELEMENT, ERROR_MESSAGE, ERROR_CODE);
        IpSecConfigInvalidElement invalidB2 = new IpSecConfigInvalidElement(ELEMENT + "333", ERROR_MESSAGE + " aaaaa", ERROR_CODE);
        listB.add(invalidB1);
        listB.add(invalidB2);
        statusB.setIpsecConfigInvalidElements(listB);

        assertTrue("Object must be equals", statusB.equals(statusA));
    }

    @Test
    public void test_areNotEquals() {
        IpSecConfigValidityStatus statusA = new IpSecConfigValidityStatus();
        statusA.setName(NAME);
        List<IpSecConfigInvalidElement> listA = new ArrayList<IpSecConfigInvalidElement>();
        IpSecConfigInvalidElement invalidA1 = new IpSecConfigInvalidElement(ELEMENT, ERROR_MESSAGE, ERROR_CODE);
        IpSecConfigInvalidElement invalidA2 = new IpSecConfigInvalidElement(ELEMENT + "333", ERROR_MESSAGE + " aaaaa", ERROR_CODE);
        listA.add(invalidA1);
        listA.add(invalidA2);
        statusA.setIpsecConfigInvalidElements(listA);

        IpSecConfigValidityStatus statusB = new IpSecConfigValidityStatus();
        statusB.setName(NAME + "1");
        List<IpSecConfigInvalidElement> listB = new ArrayList<IpSecConfigInvalidElement>();
        IpSecConfigInvalidElement invalidB1 = new IpSecConfigInvalidElement(ELEMENT, ERROR_MESSAGE, ERROR_CODE);
        IpSecConfigInvalidElement invalidB2 = new IpSecConfigInvalidElement(ELEMENT + "333", ERROR_MESSAGE + " aaaaa", ERROR_CODE);
        listB.add(invalidB1);
        listB.add(invalidB2);
        statusB.setIpsecConfigInvalidElements(listB);

        assertTrue("Object must not be equals", !statusB.equals(statusA));
    }

}
