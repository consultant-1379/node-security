/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
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

public class OnDemandCrlDownloadWfExceptionTest {
    
    private OnDemandCrlDownloadWfException onDemandCrlDownloadWfException;
    
    private OnDemandCrlDownloadWfException onDemandCrlDownloadWfExceptionMessage;
    
    private OnDemandCrlDownloadWfException onDemandCrlDownloadWfExceptionMessageAndCause;
    
    private OnDemandCrlDownloadWfException onDemandCrlDownloadWfExceptionCause;
    
    private String errorMessage;
    private String cause;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

        cause = "CrlDownloadWorkflowError";
        errorMessage = "Exception Occured";
        onDemandCrlDownloadWfException = new OnDemandCrlDownloadWfException();
        onDemandCrlDownloadWfExceptionMessage = new OnDemandCrlDownloadWfException(errorMessage);
        onDemandCrlDownloadWfExceptionMessageAndCause = new OnDemandCrlDownloadWfException(errorMessage, new Throwable(cause));
        onDemandCrlDownloadWfExceptionCause = new OnDemandCrlDownloadWfException(new Throwable(cause));
    }
    
    /**
     * Test method for {@link com.ericsson.nms.security.nscs.api.exception.UnSupportedNodeReleaseVersionException#getErrorType()}.
     */
    @Test
    public void testGetErrorType() {
        int expResult = NscsServiceException.ErrorType.ON_DEMAND_CRL_DOWNLOAD_WF_FAILED.toInt();
        int result = onDemandCrlDownloadWfException.getErrorType().toInt();
        assertEquals(expResult, result);
    }

    /**
     * Test method for {@link com.ericsson.nms.security.nscs.api.exception.OnDemandCrlDownloadWfException#OnDemandCrlDownloadWfException()}.
     */
    @Test
    public void testOnDemandCrlDownloadWfException() {

        assertTrue(onDemandCrlDownloadWfException.getMessage().equals(NscsErrorCodes.ON_DEMAND_CRL_DOWNLOAD_WF_FAILED));

    }

    /**
     * Test method for {@link com.ericsson.nms.security.nscs.api.exception.OnDemandCrlDownloadWfException#OnDemandCrlDownloadWfException(java.lang.String)}.
     */
    @Test
    public void testOnDemandCrlDownloadWfExceptionString() {
        assertTrue(onDemandCrlDownloadWfExceptionMessage.getMessage().contains(errorMessage.subSequence(0, errorMessage.length())));
    }

    /**
     * Test method for {@link com.ericsson.nms.security.nscs.api.exception.OnDemandCrlDownloadWfException#OnDemandCrlDownloadWfException(java.lang.String, java.lang.Throwable)}.
     */
    @Test
    public void testOnDemandCrlDownloadWfExceptionStringThrowable() {
        assertTrue(onDemandCrlDownloadWfExceptionMessageAndCause.getMessage().contains(errorMessage.subSequence(0, errorMessage.length())));
    }

    /**
     * Test method for {@link com.ericsson.nms.security.nscs.api.exception.OnDemandCrlDownloadWfException#OnDemandCrlDownloadWfException(java.lang.Throwable)}.
     */
    @Test
    public void testOnDemandCrlDownloadWfExceptionThrowable() {
        assertTrue(onDemandCrlDownloadWfExceptionCause.getCause().toString().contains(cause.subSequence(0, cause.length())));
    }


}
