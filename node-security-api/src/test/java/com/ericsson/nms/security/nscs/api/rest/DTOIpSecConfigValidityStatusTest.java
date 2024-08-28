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

public class DTOIpSecConfigValidityStatusTest {

    private static final String NAME = "ABCabc 123";
    private static final String ELEMENT = "Element";
    private static final String ERROR_MESSAGE = "Error message";
    private static final IpSecValidityErrorCode ERROR_CODE = IpSecValidityErrorCode.IPADDRESS_TYPE;

    @Test
    public void test_Constructor() {
        DTOIpSecConfigValidityStatus status = new DTOIpSecConfigValidityStatus();
        assertNotNull("Null object", status);
    }

    @Test
    public void test_ConstructorWithParameters() {
        DTOIpSecConfigValidityStatus status = new DTOIpSecConfigValidityStatus(new ArrayList<IpSecConfigValidityStatus>());
        assertNotNull("Null object", status);
        assertNotNull("Null list object", status.getIpSecConfigValidityStatus());
    }

    @Test
    public void test_setIpSecConfigValidityStatus() {
        DTOIpSecConfigValidityStatus dtoStatus = new DTOIpSecConfigValidityStatus();
        List<IpSecConfigValidityStatus> list = new ArrayList<IpSecConfigValidityStatus>();

        IpSecConfigValidityStatus status = new IpSecConfigValidityStatus();
        status.setName(NAME);
        List<IpSecConfigInvalidElement> invalidElementList = new ArrayList<IpSecConfigInvalidElement>();
        IpSecConfigInvalidElement invalid = new IpSecConfigInvalidElement(ELEMENT, ERROR_MESSAGE, ERROR_CODE);
        IpSecConfigInvalidElement invalid2 = new IpSecConfigInvalidElement(ELEMENT + "333", ERROR_MESSAGE + " aaaaa", ERROR_CODE);
        invalidElementList.add(invalid);
        invalidElementList.add(invalid2);

        status.setIpsecConfigInvalidElements(invalidElementList);
        list.add(status);
        dtoStatus.setIpSecConfigValidityStatus(list);

        assertNotNull("Null list object", dtoStatus.getIpSecConfigValidityStatus());
        assertTrue("Invalid list size", dtoStatus.getIpSecConfigValidityStatus().size() == 1);
    }

}
