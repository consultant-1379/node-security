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

public class UnSupportedNodeReleaseVersionExceptionTest {

    UnSupportedNodeReleaseVersionException unSupportedNodeReleaseVersionException;

    UnSupportedNodeReleaseVersionException unSupportedNodeReleaseVersionExceptionMessage;

    UnSupportedNodeReleaseVersionException unSupportedNodeReleaseVersionExceptionMessageCause;

    UnSupportedNodeReleaseVersionException unSupportedNodeReleaseVersionExceptionCause;

    String errorMessage;
    String cause;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

        cause = "NodeReleaseVersionError";
        errorMessage = "Exception Occured";
        unSupportedNodeReleaseVersionException = new UnSupportedNodeReleaseVersionException();
        unSupportedNodeReleaseVersionExceptionMessage = new UnSupportedNodeReleaseVersionException(errorMessage);
        unSupportedNodeReleaseVersionExceptionMessageCause = new UnSupportedNodeReleaseVersionException(errorMessage, new Throwable(cause));
        unSupportedNodeReleaseVersionExceptionCause = new UnSupportedNodeReleaseVersionException(new Throwable(cause));
    }

    /**
     * Test method for {@link com.ericsson.nms.security.nscs.api.exception.UnSupportedNodeReleaseVersionException#getErrorType()}.
     */
    @Test
    public void testGetErrorType() {
        int expResult = NscsServiceException.ErrorType.UNSUPPORTED_NODE_RELEASE_VERSION.toInt();
        int result = unSupportedNodeReleaseVersionException.getErrorType().toInt();
        assertEquals(expResult, result);
    }

    /**
     * Test method for {@link com.ericsson.nms.security.nscs.api.exception.UnSupportedNodeReleaseVersionException#UnSupportedNodeReleaseVersionException()}.
     */
    @Test
    public void testUnSupportedNodeReleaseVersionException() {

        assertTrue(unSupportedNodeReleaseVersionException.getSuggestedSolution().equals(NscsErrorCodes.USE_VALID_NODE_RELEASE_VERSION));

    }

    /**
     * Test method for {@link com.ericsson.nms.security.nscs.api.exception.UnSupportedNodeReleaseVersionException#UnSupportedNodeReleaseVersionException(java.lang.String)}.
     */
    @Test
    public void testUnSupportedNodeReleaseVersionExceptionString() {
        assertTrue(unSupportedNodeReleaseVersionExceptionMessage.getMessage().contains(errorMessage.subSequence(0, errorMessage.length())));
    }

    /**
     * Test method for {@link com.ericsson.nms.security.nscs.api.exception.UnSupportedNodeReleaseVersionException#UnSupportedNodeReleaseVersionException(java.lang.String, java.lang.Throwable)}.
     */
    @Test
    public void testUnSupportedNodeReleaseVersionExceptionStringThrowable() {
        assertTrue(unSupportedNodeReleaseVersionExceptionMessageCause.getMessage().contains(errorMessage.subSequence(0, errorMessage.length())));
    }

    /**
     * Test method for {@link com.ericsson.nms.security.nscs.api.exception.UnSupportedNodeReleaseVersionException#UnSupportedNodeReleaseVersionException(java.lang.Throwable)}.
     */
    @Test
    public void testUnSupportedNodeReleaseVersionExceptionThrowable() {
        assertTrue(unSupportedNodeReleaseVersionExceptionCause.getCause().toString().contains(cause.subSequence(0, cause.length())));
    }

}
