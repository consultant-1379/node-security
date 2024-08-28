/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2021
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.exception;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.ericsson.nms.security.nscs.api.exception.NscsServiceException.ErrorType;

public class TooManyChildMosExceptionTest {

    @Test
    public void test_TooManyChildMosException() {
        TooManyChildMosException tooManyChildMosException = new TooManyChildMosException();
        assertNotNull(tooManyChildMosException);
        assertEquals("Wrong error type", ErrorType.TOO_MANY_CHILD_MOS, tooManyChildMosException.getErrorType());
        assertEquals("Wrong error message", NscsErrorCodes.TOO_MANY_CHILD_MOS, tooManyChildMosException.getMessage());
    }

    @Test
    public void test_TooManyChildMosException_message() {
        String message = "ldap MO under system MO";
        TooManyChildMosException tooManyChildMosException = new TooManyChildMosException(message);
        assertNotNull(tooManyChildMosException);
        assertEquals("Wrong error type", ErrorType.TOO_MANY_CHILD_MOS, tooManyChildMosException.getErrorType());
        assertEquals("Wrong error message", NscsErrorCodes.TOO_MANY_CHILD_MOS + " : " + message, tooManyChildMosException.getMessage());
    }
}
