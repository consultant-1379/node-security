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

public class IpSecWfException extends NscsServiceException {

    private static final long serialVersionUID = 2887846172970293024L;

    public IpSecWfException() {
        super(NscsErrorCodes.IPSEC_CONFIGURE_WF_FAILED);
    }

    public IpSecWfException(final String message) {
        super(formatMessage(NscsErrorCodes.IPSEC_CONFIGURE_WF_FAILED, message));
    }

    public IpSecWfException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.IPSEC_CONFIGURE_WF_FAILED, message), cause);
    }

    public IpSecWfException(final String message, final Throwable cause, final String suggestedSolution) {
        super(formatMessage(NscsErrorCodes.IPSEC_CONFIGURE_WF_FAILED, message), cause);
        setSuggestedSolutionLocal(suggestedSolution);
    }

    public IpSecWfException(final String message, final String suggestedSolution) {
        super(formatMessage(NscsErrorCodes.IPSEC_CONFIGURE_WF_FAILED, message));
        setSuggestedSolutionLocal(suggestedSolution);
    }

    /**
     * Gets the error type
     *
     * @return ErrorType.LDAP_CONFIGURE_WF_FAILED
     */
    @Override
    public ErrorType getErrorType() {
            return ErrorType.IPSEC_CONFIGURE_WF_FAILED;
    }

    private NscsServiceException setSuggestedSolutionLocal(final String suggestedSolution) {
        return super.setSuggestedSolution(suggestedSolution);
    }
}
