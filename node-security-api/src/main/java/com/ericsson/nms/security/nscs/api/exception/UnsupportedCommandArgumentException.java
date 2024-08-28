package com.ericsson.nms.security.nscs.api.exception;

/**
 * <p>Thrown by CommandHandler implementation when an invalid
 * argument value is passed.</p>
 * Created by emaynes on 02/05/2014.
 */
public class UnsupportedCommandArgumentException extends NscsServiceException {

    private static final long serialVersionUID = -3461247315583262113L;

    public UnsupportedCommandArgumentException() {
        super(NscsErrorCodes.UNSUPPORTED_COMMAND_ARGUMENT);
    }

    public UnsupportedCommandArgumentException(final String message, final String suggestedSolution) {
        super(formatMessage(NscsErrorCodes.UNSUPPORTED_COMMAND_ARGUMENT, message));
        setSuggestedSolution(suggestedSolution);
    }

    public UnsupportedCommandArgumentException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.UNSUPPORTED_COMMAND_ARGUMENT, message), cause);

    }

    public UnsupportedCommandArgumentException(final Throwable cause) {
        super(NscsErrorCodes.UNSUPPORTED_COMMAND_ARGUMENT, cause);
    }

    /**
     * @return ErrorType.UNSUPPORTED_COMMAND_ARGUMENT_ERROR
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.UNSUPPORTED_COMMAND_ARGUMENT_ERROR;
    }
}
