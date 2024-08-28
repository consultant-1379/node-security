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
 * Exception thrown by the RTSEL CommnadHandlers in case of a work flow error.
 * </p>
 *
 * @author xchowja
 *
 */
public class RtselWfException extends NscsServiceException {

    private static final long serialVersionUID = 3892740374902074771L;

    {
        {
            setSuggestedSolution(NscsErrorCodes.SUGGESTED_SOLUTION_CONSULT_ERROR_LOGS);
        }
    }

    public RtselWfException() {
        super(NscsErrorCodes.RTSEL_WF_FAILED);
    }

    public RtselWfException(final String message) {
        super(formatMessage(NscsErrorCodes.RTSEL_WF_FAILED, message));
    }

    public RtselWfException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.RTSEL_WF_FAILED, message), cause);
    }

    public RtselWfException(final Throwable cause) {
        super(NscsErrorCodes.RTSEL_WF_FAILED, cause);
    }

    /**
     * Gets the error type RTSEL_WF_FAILED
     *
     * @return ErrorType.RTSEL_WF_FAILED
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.RTSEL_WF_FAILED;
    }
}
