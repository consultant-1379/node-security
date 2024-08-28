/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.exception;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LdapConfigureWfExceptionTest {

    private LdapConfigureWfException ldapConfigureWfException;

    private LdapConfigureWfException ldapConfigureWfExceptionMessage;

    private LdapConfigureWfException ldapConfigureWfExceptionMessageAndCause;

    private LdapConfigureWfException ldapConfigureWfExceptionCause;

    private String errorMessage;
    private String cause;

    @Before
    public void setUp() throws Exception {

        cause = "LdapConfigureWorkflowError";
        errorMessage = "Exception Occured";
        ldapConfigureWfException = new LdapConfigureWfException();
        ldapConfigureWfExceptionMessage = new LdapConfigureWfException(errorMessage);
        ldapConfigureWfExceptionMessageAndCause = new LdapConfigureWfException(errorMessage, new Throwable(cause));
        ldapConfigureWfExceptionCause = new LdapConfigureWfException(new Throwable(cause));
    }

    @Test
    public void testGetErrorType() {
        int expResult = NscsServiceException.ErrorType.LDAP_CONFIGURE_WF_FAILED.toInt();
        int result = ldapConfigureWfException.getErrorType().toInt();
        assertEquals(expResult, result);
    }

    @Test
    public void testLdapConfigureWfExceptionTest() {
        assertTrue(ldapConfigureWfException.getMessage().equals(NscsErrorCodes.LDAP_CONFIGURE_WF_FAILED));
    }

    @Test
    public void testLdapConfigureWfExceptionStringTest() {
        assertTrue(ldapConfigureWfExceptionMessage.getMessage().contains(errorMessage.subSequence(0, errorMessage.length())));
    }

    @Test
    public void LdapConfigureWfExceptionStringThrowableTest() {
        assertTrue(ldapConfigureWfExceptionMessageAndCause.getMessage().contains(errorMessage.subSequence(0, errorMessage.length())));
    }

    @Test
    public void LdapConfigureWfExceptionThrowableTest() {
        assertTrue(ldapConfigureWfExceptionCause.getCause().toString().contains(cause.subSequence(0, cause.length())));
    }

}
