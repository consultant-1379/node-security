package com.ericsson.nms.security.nscs.api.exception;

/**
 * <p>Exception thrown by the NscsService implementation when there is no
 * CommandHandler available to handle an specified NscsCommandType</p>
 * @author emaynes
 */
public class CouldNotFindCommandHandlerException extends NscsSystemException {

    private static final long serialVersionUID = 2980503245684542894L;

    public CouldNotFindCommandHandlerException() {
        super(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR);
    }

    {{
        setSuggestedSolution(NscsErrorCodes.SUGGESTED_SOLUTION_CONSULT_ERROR_LOGS);
    }}

    public CouldNotFindCommandHandlerException(final String message) {
        super(formatMessage(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR, message));
    }

    public CouldNotFindCommandHandlerException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR, message), cause);
    }

    public CouldNotFindCommandHandlerException(final Throwable cause) {
        super(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR, cause);
    }

    /**
     *
     * @return ErrorType.COMMAND_HANDLER_NOT_FOUND_ERROR
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.COMMAND_HANDLER_NOT_FOUND_ERROR;
    }
}
