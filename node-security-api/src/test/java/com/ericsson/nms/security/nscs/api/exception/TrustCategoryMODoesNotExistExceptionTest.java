/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
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
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TrustCategoryMODoesNotExistExceptionTest {

    TrustCategoryMODoesNotExistException TrustCategoryMODoesNotExistException;

    TrustCategoryMODoesNotExistException TrustCategoryMODoesNotExistExceptionMessage;

    TrustCategoryMODoesNotExistException TrustCategoryMODoesNotExistExceptionMessageCause;
    TrustCategoryMODoesNotExistException TrustCategoryMODoesNotExistExceptionCause;

    String errorMessage;
    String cause;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

        cause = "MODoesNotExists";
        errorMessage = "Exception Occured";
        TrustCategoryMODoesNotExistException = new TrustCategoryMODoesNotExistException();
        TrustCategoryMODoesNotExistExceptionMessage = new TrustCategoryMODoesNotExistException(errorMessage);
        TrustCategoryMODoesNotExistExceptionMessageCause = new TrustCategoryMODoesNotExistException(errorMessage, new Throwable(cause));
        TrustCategoryMODoesNotExistExceptionCause = new TrustCategoryMODoesNotExistException(new Throwable(cause));

    }

    /**
     * Test method for {@link com.ericsson.nms.security.nscs.api.exception.TrustCategoryMODoesNotExistException#getErrorType()}.
     */
    @Test
    public void testGetErrorType() {

        int expResult = NscsServiceException.ErrorType.TRUST_CATEGORY_MO_DOES_NOT_EXISTS.toInt();
        int result = TrustCategoryMODoesNotExistException.getErrorType().toInt();
        assertEquals(expResult, result);
    }

    /**
     * Test method for {@link com.ericsson.nms.security.nscs.api.exception.TrustCategoryMODoesNotExistException#TrustCategoryMODoesNotExistException()}.
     */
    @Test
    public void testTrustCategoryMODoesNotExistException() {

        assertTrue(TrustCategoryMODoesNotExistException.getSuggestedSolution().equals(NscsErrorCodes.ISSUE_CERT_FOR_TRUST_CATEGORY_MO));

    }

    /**
     * Test method for {@link com.ericsson.nms.security.nscs.api.exception.TrustCategoryMODoesNotExistException#TrustCategoryMODoesNotExistException(java.lang.String)}.
     */
    @Test
    public void testTrustCategoryMODoesNotExistExceptionString() {
        assertTrue(TrustCategoryMODoesNotExistExceptionMessage.getMessage().contains(errorMessage.subSequence(0, errorMessage.length())));
    }

    /**
     * Test method for {@link com.ericsson.nms.security.nscs.api.exception.TrustCategoryMODoesNotExistException#TrustCategoryMODoesNotExistException(java.lang.String, java.lang.Throwable)}.
     */
    @Test
    public void testTrustCategoryMODoesNotExistExceptionStringThrowable() {

        assertTrue(TrustCategoryMODoesNotExistExceptionMessageCause.getMessage().contains(errorMessage.subSequence(0, errorMessage.length())));
    }

    /**
     * Test method for {@link com.ericsson.nms.security.nscs.api.exception.TrustCategoryMODoesNotExistException#TrustCategoryMODoesNotExistException(java.lang.Throwable)}.
     */
    @Test
    public void testTrustCategoryMODoesNotExistExceptionThrowable() {

        assertTrue(TrustCategoryMODoesNotExistExceptionCause.getCause().toString().contains(cause.subSequence(0, cause.length())));

    }

}
