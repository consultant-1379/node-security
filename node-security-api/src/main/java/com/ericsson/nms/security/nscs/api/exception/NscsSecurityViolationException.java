package com.ericsson.nms.security.nscs.api.exception;

/**
 * <p>Exception thrown by the command parser in case o a syntax error
 * is detected</p>
 * Created by emaynes on 01/05/2014.
 */
public class NscsSecurityViolationException extends NscsServiceException {

	private static final long serialVersionUID = -8268485384767233860L;

	{{
        setSuggestedSolution(NscsErrorCodes.SECURITY_VIOLATION_SUGGESTED_SOLUTION);
    }}

    public NscsSecurityViolationException() {
        super(NscsErrorCodes.SECURITY_VIOLATION_EXCEPTION_MESSAGE);
    }

    public NscsSecurityViolationException(final String message) {
        super(formatMessage(NscsErrorCodes.SECURITY_VIOLATION_EXCEPTION_MESSAGE, message));
    }

    public NscsSecurityViolationException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.SECURITY_VIOLATION_EXCEPTION_MESSAGE, message), cause);
    }

    public NscsSecurityViolationException(final Throwable cause) {
        super(NscsErrorCodes.SECURITY_VIOLATION_EXCEPTION_MESSAGE, cause);
    }

    /**
     * Gets the error type
     *
     * @return ErrorType.SECURITY_VIOLATION_ERROR
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.SECURITY_VIOLATION_ERROR;
    }
}
