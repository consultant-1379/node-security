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

/**
 * <p>
 * Exception thrown by the CrlCheckEnableHandler or CrlCheckDisableHandler in case of a workflow error.
 * </p>
 * 
 * @author xramdag
 */
public class CrlCheckEnableOrDisableWfException extends NscsServiceException {
    /**
     * 
     */
    private static final long serialVersionUID = -1454985091190151369L;

    {
        {
            setSuggestedSolution(NscsErrorCodes.PLEASE_CHECK_ONLINE_HELP_FOR_CORRECT_SYNTAX);
        }
    }

    public CrlCheckEnableOrDisableWfException() {
        super(NscsErrorCodes.CRLCHECK_ENABLE_OR_DISABLE_WF_FAILED);
    }

    public CrlCheckEnableOrDisableWfException(final String message) {
        super(formatMessage(NscsErrorCodes.CRLCHECK_ENABLE_OR_DISABLE_WF_FAILED, message));
    }

    public CrlCheckEnableOrDisableWfException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.CRLCHECK_ENABLE_OR_DISABLE_WF_FAILED, message), cause);
    }

    public CrlCheckEnableOrDisableWfException(final Throwable cause) {
        super(NscsErrorCodes.CRLCHECK_ENABLE_OR_DISABLE_WF_FAILED, cause);
    }

    /**
     * Gets the error type
     *
     * @return ErrorType.CRLCHECK_ENABLE_OR_DISABLE_WF_FAILED
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.CRLCHECK_ENABLE_OR_DISABLE_WF_FAILED;
    }
}
