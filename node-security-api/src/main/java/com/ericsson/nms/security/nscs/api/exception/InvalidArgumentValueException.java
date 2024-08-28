package com.ericsson.nms.security.nscs.api.exception;

/**
 * <p>Exception thrown by the command parser in case o a syntax error
 * is detected</p>
 * Created by emaynes on 01/05/2014.
 */
public class InvalidArgumentValueException extends NscsServiceException {

    
	private static final long serialVersionUID = -7494304427215011760L;

	{{
        setSuggestedSolution(NscsErrorCodes.PLEASE_CHECK_ONLINE_HELP_FOR_CORRECT_SYNTAX);
    }}

    public InvalidArgumentValueException() {
        super(NscsErrorCodes.INVALID_ARGUMENT_VALUE);
    }

    public InvalidArgumentValueException(final String message) {
        super(formatMessage(NscsErrorCodes.INVALID_ARGUMENT_VALUE, message));
    }

    public InvalidArgumentValueException(final String message, final String messageDescription) {
        super(formatMessage(message, messageDescription));
    }

    public InvalidArgumentValueException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.INVALID_ARGUMENT_VALUE, message), cause);
    }

    public InvalidArgumentValueException(final Throwable cause) {
        super(NscsErrorCodes.INVALID_ARGUMENT_VALUE, cause);
    }

    /**
     * Gets the error type
     *
     * @return ErrorType.UNSUPPORTED_NODE_TYPE
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.INVALID_ARGUMENT_VALUE;
    }
}
