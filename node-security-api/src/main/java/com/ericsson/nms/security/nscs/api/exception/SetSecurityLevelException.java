package com.ericsson.nms.security.nscs.api.exception;

/**
 * <p>Exception thrown by the command handler when setting the security
 * level</p>
 *
 * @author ealemca
 */
public class SetSecurityLevelException extends NscsServiceException {

    private static final long serialVersionUID = 3509806957175133050L;

    {{
        setSuggestedSolution(NscsErrorCodes.SUGGESTED_SOLUTION_CONSULT_ERROR_LOGS);
    }}

    public SetSecurityLevelException() {
        super(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR);
    }

    public SetSecurityLevelException(final String message) {
        super(formatMessage(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR, message));
    }

    public SetSecurityLevelException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR, message), cause);
    }

    public SetSecurityLevelException(final Throwable cause) {
        super(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR, cause);
    }

    /**
     * Gets the error type
     *
     * @return ErrorType.SET_SECURITY_LEVEL_ERROR
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.SET_SECURITY_LEVEL_ERROR;
    }
}
