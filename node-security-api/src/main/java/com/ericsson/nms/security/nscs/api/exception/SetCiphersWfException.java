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
 * Exception thrown by the SetCiphersHandler in case of a work flow error.
 * </p>
 *
 * @author tcsvijc
 *
 */
public class SetCiphersWfException extends NscsServiceException {

    private static final long serialVersionUID = 3892740374902074771L;

    {
        {
            setSuggestedSolution(NscsErrorCodes.SUGGESTED_SOLUTION_CONSULT_ERROR_LOGS);
        }
    }

    public SetCiphersWfException() {
        super(NscsErrorCodes.SET_CIPHERS_WF_FAILED);
    }

    public SetCiphersWfException(final String message) {
        super(formatMessage(NscsErrorCodes.SET_CIPHERS_WF_FAILED, message));
    }

    public SetCiphersWfException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.SET_CIPHERS_WF_FAILED, message), cause);
    }

    public SetCiphersWfException(final Throwable cause) {
        super(NscsErrorCodes.SET_CIPHERS_WF_FAILED, cause);
    }

    /**
     * Gets the error type SET_CIPHERS_WF_FAILED
     *
     * @return ErrorType.SET_CIPHERS_WF_FAILED
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.SET_CIPHERS_WF_FAILED;
    }
}
