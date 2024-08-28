package com.ericsson.nms.security.nscs.api.exception;

/**
 * <p>Exception thrown by a Validator or CommandHandler when an
 * unexpected NscsCommand is received.</p>
 * Created by emaynes on 02/05/2014.
 */
public class UnexpectedCommandTypeException extends NscsSystemException {

    private static final long serialVersionUID = 5098556219794152476L;

    public UnexpectedCommandTypeException() {
        super(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR);
    }

    {{
        setSuggestedSolution(NscsErrorCodes.SUGGESTED_SOLUTION_CONSULT_ERROR_LOGS);
    }}

    public UnexpectedCommandTypeException(final String message) {
        super(formatMessage(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR, message));
    }

    public UnexpectedCommandTypeException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR, message), cause);
    }

    public UnexpectedCommandTypeException(final Throwable cause) {
        super(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR, cause);
    }

    /**
     * @return ErrorType.UNEXPECTED_COMMAND_TYPE
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.UNEXPECTED_COMMAND_TYPE;
    }
}
