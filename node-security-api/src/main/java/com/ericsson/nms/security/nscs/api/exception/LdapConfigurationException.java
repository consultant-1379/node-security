/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.exception;

public class LdapConfigurationException extends NscsServiceException {

    /**
     * 
     */
    private static final long serialVersionUID = -2632659328878139669L;

    {
        {
            setSuggestedSolution(NscsErrorCodes.PLEASE_PROVIDE_VALID_INPUT_ENCODING);
        }
    }

    /**
     * Constructs a new LdapConfigurationException with INVALID_FILE_CONTENT as
     * its detail message
     */
    public LdapConfigurationException() {
        super(NscsErrorCodes.LDAP_CONFIGURATION_ERROR);
    }

    /**
     * Constructs a new LdapConfigurationException with INVALID_FILE_CONTENT
     * appended by user message as its detail message
     * 
     * @param message
     *            : User message
     */
    public LdapConfigurationException(final String message) {
        super(formatMessage(NscsErrorCodes.LDAP_CONFIGURATION_ERROR, message));
    }

    /**
     * Constructs a new LdapConfigurationException with the specified detail
     * message and cause.
     * 
     * @param message
     *            : User message
     * @param cause
     *            : {@link Throwable} cause of exception
     */

    public LdapConfigurationException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.LDAP_CONFIGURATION_ERROR, message), cause);
    }

    /**
     * Constructs a new LdapConfigurationException exception with the specified
     * cause
     * 
     * @param cause
     *            : {@link Throwable} cause of exception
     */
    public LdapConfigurationException(final Throwable cause) {
        super(NscsErrorCodes.LDAP_CONFIGURATION_ERROR, cause);
    }

    /**
     * @return ErrorType.LDAP_CONFIGURATION_FAILED
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.LDAP_CONFIGURATION_FAILED;
    }

}
