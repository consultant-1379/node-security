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
package com.ericsson.nms.security.nscs.cpp.ipsec.util.validator;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.ericsson.nms.security.nscs.api.rest.IpSecValidityErrorCode;

public class IpSecNodesErrorHandlerDataTest {

    private static final String ERROR_MESSAGE = "Error message";
    private static final IpSecValidityErrorCode ERROR_CODE = IpSecValidityErrorCode.IPADDRESS_TYPE;

    @Test
    public void test_Contructor() {
        IpSecNodesErrorHandlerData handler = new IpSecNodesErrorHandlerData();
        handler.setErrorMessage(ERROR_MESSAGE);
        handler.setErrorCode(ERROR_CODE);
        assertNotNull("IpSecNodesErrorHandlerData is null", handler);
    }

    @Test
    public void test_ContructorWithParameter() {
        IpSecNodesErrorHandlerData handler = new IpSecNodesErrorHandlerData(ERROR_CODE, ERROR_MESSAGE);
        assertNotNull("IpSecNodesErrorHandlerData is null", handler);
        assertTrue("Error message mismatch", ERROR_MESSAGE.equals(handler.getErrorMessage()));
        assertTrue("Error code mismatch", ERROR_CODE.equals(handler.getErrorCode()));
    }

    @Test
    public void test_setErrorMessage() {
        IpSecNodesErrorHandlerData handler = new IpSecNodesErrorHandlerData();
        handler.setErrorMessage(ERROR_MESSAGE);
        assertTrue("Error message mismatch", ERROR_MESSAGE.equals(handler.getErrorMessage()));
    }

    @Test
    public void test_setErrorCode() {
        IpSecNodesErrorHandlerData handler = new IpSecNodesErrorHandlerData();
        handler.setErrorCode(ERROR_CODE);
        assertTrue("Error code mismatch", ERROR_CODE.equals(handler.getErrorCode()));

    }

}
