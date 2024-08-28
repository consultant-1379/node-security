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
 * Exception thrown by the ActivateFtpesHandler or DeactivateFtpesHandler in case of a workflow error.
 * </p>
 */

public class FtpesActivateOrDeactivateWfException extends NscsServiceException {
    {
        setSuggestedSolution(NscsErrorCodes.PLEASE_CHECK_ONLINE_HELP_FOR_CORRECT_SYNTAX);
    }

    private static final long serialVersionUID = 2887846172970293024L;

    public FtpesActivateOrDeactivateWfException() {
        super(NscsErrorCodes.FTPES_ACTIVATE_OR_DEACTIVATE_WF_FAILED);
    }

    public FtpesActivateOrDeactivateWfException(final String message) {
        super(formatMessage(NscsErrorCodes.FTPES_ACTIVATE_OR_DEACTIVATE_WF_FAILED, message));
    }

    public FtpesActivateOrDeactivateWfException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.FTPES_ACTIVATE_OR_DEACTIVATE_WF_FAILED, message), cause);
    }

    public FtpesActivateOrDeactivateWfException(final Throwable cause) {
        super(NscsErrorCodes.FTPES_ACTIVATE_OR_DEACTIVATE_WF_FAILED, cause);
    }

    /**
     * Gets the error type
     *
     * @return ErrorType.FTPES_ACTIVATE_OR_DEACTIVATE_WF_FAILED
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.FTPES_ACTIVATE_OR_DEACTIVATE_WF_FAILED;
    }
}
