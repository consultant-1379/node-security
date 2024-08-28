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

/**
 * <p>
 * Exception thrown by the ConfigureLdapHandler or ReconfigureLdapHandler in case of a workflow error.
 * </p>
 */

public class LdapConfigureWfException extends NscsServiceException {

    private static final long serialVersionUID = 2887846172970293024L;

    public LdapConfigureWfException() {
        super(NscsErrorCodes.LDAP_CONFIGURE_WF_FAILED);
        setSuggestedSolutionLocal(NscsErrorCodes.PLEASE_CHECK_ONLINE_HELP_FOR_CORRECT_SYNTAX);
    }

    public LdapConfigureWfException(final String message) {
        super(formatMessage(NscsErrorCodes.LDAP_CONFIGURE_WF_FAILED, message));
        setSuggestedSolutionLocal(NscsErrorCodes.PLEASE_CHECK_ONLINE_HELP_FOR_CORRECT_SYNTAX);
    }

    public LdapConfigureWfException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.LDAP_CONFIGURE_WF_FAILED, message), cause);
        setSuggestedSolutionLocal(NscsErrorCodes.PLEASE_CHECK_ONLINE_HELP_FOR_CORRECT_SYNTAX);
    }

    public LdapConfigureWfException(final Throwable cause) {
        super(NscsErrorCodes.LDAP_CONFIGURE_WF_FAILED, cause);
        setSuggestedSolutionLocal(NscsErrorCodes.PLEASE_CHECK_ONLINE_HELP_FOR_CORRECT_SYNTAX);

    }

    /**
     * Gets the error type
     *
     * @return ErrorType.LDAP_CONFIGURE_WF_FAILED
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.LDAP_CONFIGURE_WF_FAILED;
    }

    private NscsServiceException setSuggestedSolutionLocal(final String suggestedSolution) {
        return super.setSuggestedSolution(suggestedSolution);
    }
}
