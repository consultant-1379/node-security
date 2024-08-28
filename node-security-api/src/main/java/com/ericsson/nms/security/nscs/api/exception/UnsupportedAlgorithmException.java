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
 * This exception will be thrown when user provides Unsupported Algorithms for ciphers configuration.
 * 
 * @author xchowja
 *
 */
public class UnsupportedAlgorithmException extends NscsServiceException {

    private static final long serialVersionUID = -7494304427215011760L;

    {
        {
            setSuggestedSolution(NscsErrorCodes.SPECIFY_A_SUPPORTED_ALGORITHM);
        }
    }

    public UnsupportedAlgorithmException() {
        super(NscsErrorCodes.UNSUPPORTED_ALGORITHM);
    }

    public UnsupportedAlgorithmException(final String message) {
        super(formatMessage(NscsErrorCodes.UNSUPPORTED_ALGORITHM, message));
    }

    public UnsupportedAlgorithmException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.UNSUPPORTED_ALGORITHM, message), cause);
    }

    public UnsupportedAlgorithmException(final Throwable cause) {
        super(NscsErrorCodes.UNSUPPORTED_ALGORITHM, cause);
    }

    /**
     * Gets the error type
     *
     * @return ErrorType.UNSUPPORTED_ALGORITHM
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.UNSUPPORTED_ALGORITHM;
    }
}
