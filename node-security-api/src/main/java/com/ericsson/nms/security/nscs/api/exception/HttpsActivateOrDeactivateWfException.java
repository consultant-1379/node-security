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

/**
 * <p>
 * Exception thrown by the ActivateHttpsHandler or DeactivateHttpsHandler in case of a workflow error.
 * </p>
 * 
 * @author ekrzsia
 */
public class HttpsActivateOrDeactivateWfException extends NscsServiceException {

    {
        setSuggestedSolution(NscsErrorCodes.PLEASE_CHECK_ONLINE_HELP_FOR_CORRECT_SYNTAX);
    }

    private static final long serialVersionUID = 2887846172970293024L;

    public HttpsActivateOrDeactivateWfException() {
        super(NscsErrorCodes.HTTPS_ACTIVATE_OR_DEACTIVATE_WF_FAILED);
    }

    public HttpsActivateOrDeactivateWfException(final String message) {
        super(formatMessage(NscsErrorCodes.HTTPS_ACTIVATE_OR_DEACTIVATE_WF_FAILED, message));
    }

    public HttpsActivateOrDeactivateWfException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.HTTPS_ACTIVATE_OR_DEACTIVATE_WF_FAILED, message), cause);
    }

    public HttpsActivateOrDeactivateWfException(final Throwable cause) {
        super(NscsErrorCodes.HTTPS_ACTIVATE_OR_DEACTIVATE_WF_FAILED, cause);
    }

    /**
     * Gets the error type
     *
     * @return ErrorType.HTTPS_ACTIVATE_OR_DEACTIVATE_WF_FAILED
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.HTTPS_ACTIVATE_OR_DEACTIVATE_WF_FAILED;
    }
}
