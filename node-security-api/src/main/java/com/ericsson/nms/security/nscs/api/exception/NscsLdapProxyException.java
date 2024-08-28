/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2022
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.exception;

public class NscsLdapProxyException extends NscsServiceException {

    private static final long serialVersionUID = -3685126364686535903L;

    /**
     * Constructs an exception with default message.
     */
    public NscsLdapProxyException() {
        super(NscsErrorCodes.LDAP_PROXY_ERROR);
    }

    /**
     * Constructs an exception with given user message.
     * 
     * @param message
     *            the user message.
     */
    public NscsLdapProxyException(final String message) {
        super(formatMessage(NscsErrorCodes.LDAP_PROXY_ERROR, message));
    }

    /**
     * Constructs an exception with given user message and cause.
     * 
     * @param message
     *            the user message.
     * @param cause
     *            the cause.
     */
    public NscsLdapProxyException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.LDAP_PROXY_ERROR, message), cause);
    }

    /**
     * Constructs an exception with given user message and cause and suggested solution.
     * 
     * @param message
     *            the user message.
     * @param cause
     *            the cause.
     * @param suggestedSolution
     *            the suggested solution.
     */
    public NscsLdapProxyException(final String message, final Throwable cause, final String suggestedSolution) {
        super(formatMessage(NscsErrorCodes.LDAP_PROXY_ERROR, message), cause);
        setSuggestedSolutionLocal(suggestedSolution);
    }

    /**
     * Constructs an exception with given user message and suggested solution.
     * 
     * @param message
     *            the user message.
     * @param suggestedSolution
     *            the suggested solution.
     */
    public NscsLdapProxyException(final String message, final String suggestedSolution) {
        super(formatMessage(NscsErrorCodes.LDAP_PROXY_ERROR, message));
        setSuggestedSolutionLocal(suggestedSolution);
    }

    @Override
    public ErrorType getErrorType() {
        return ErrorType.LDAP_PROXY_FAILED;
    }

    private void setSuggestedSolutionLocal(final String suggestedSolution) {
        super.setSuggestedSolution(suggestedSolution);
    }

}
