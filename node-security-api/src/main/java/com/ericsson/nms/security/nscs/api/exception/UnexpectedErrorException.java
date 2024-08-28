package com.ericsson.nms.security.nscs.api.exception;

/**
 * Exception thrown whenever an unexpected error occurs.
 * Created by emaynes on 08/05/2014.
 */
public class UnexpectedErrorException extends NscsSystemException {

    private static final long serialVersionUID = -6332342803267090637L;

    {{
        setSuggestedSolution(NscsErrorCodes.SUGGESTED_SOLUTION_CONSULT_ERROR_LOGS);
    }}

    public UnexpectedErrorException() {
        super(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR);
    }

    public UnexpectedErrorException(final String message) {
        super(formatMessage(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR, message));
    }

    public UnexpectedErrorException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR, message), cause);
    }

    public UnexpectedErrorException(final Throwable cause) {
        super(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR, cause);
    }

    /**
     *
     * @return return ErrorType.UNEXPECTED_ERROR
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.UNEXPECTED_ERROR;
    }
}
