package com.ericsson.nms.security.nscs.api.exception;

/**
 * <p>Exception thrown by the command parser in case o a syntax error
 * is detected</p>
 * Created by emaynes on 01/05/2014.
 */
public class CommandSyntaxException extends NscsServiceException {

    private static final long serialVersionUID = 3509806957175133050L;

    {{
        setSuggestedSolution(NscsErrorCodes.PLEASE_CHECK_ONLINE_HELP_FOR_CORRECT_SYNTAX);
    }}

    public CommandSyntaxException() {
        super(NscsErrorCodes.SYNTAX_ERROR);
    }

    public CommandSyntaxException(final String message) {
        super(formatMessage(NscsErrorCodes.SYNTAX_ERROR, message));
    }

    public CommandSyntaxException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.SYNTAX_ERROR, message), cause);
    }

    public CommandSyntaxException(final Throwable cause) {
        super(NscsErrorCodes.SYNTAX_ERROR, cause);
    }

    /**
     * Gets the error type
     *
     * @return ErrorType.COMMAND_SYNTAX_ERROR
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.COMMAND_SYNTAX_ERROR;
    }
}
