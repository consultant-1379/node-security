package com.ericsson.nms.security.nscs.api.exception;

/**
 * <p>Exception thrown by the Data access layer when a data operation fails</p>
 * Created by emaynes on 02/05/2014.
 */
public class DataAccessException extends NscsServiceException {

    /**
     * 
     */
    private static final long serialVersionUID = -2256145721353800675L;
    private String statusMessage;

    public DataAccessException(final String statusMessage) {
        super(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR);
        this.statusMessage = statusMessage;
    }

    {{
        setSuggestedSolution(NscsErrorCodes.SUGGESTED_SOLUTION_CONSULT_ERROR_LOGS);
    }}


    public DataAccessException(final String message, final String statusMessage) {
        super(formatMessage(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR, message));
        this.statusMessage = statusMessage;
    }

    public DataAccessException(final String message, final Throwable cause, final String statusMessage) {
        super(formatMessage(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR, message), cause);
        this.statusMessage = statusMessage;
    }

    public DataAccessException(final Throwable cause, final String statusMessage) {
        super(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR, cause);
        this.statusMessage = statusMessage;
    }

    public DataAccessException(final Throwable cause) {
        super(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR, cause);
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * @return ErrorType.DATA_ACCESS_ERROR
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.DATA_ACCESS_ERROR;
    }

    @Override
    public String toString() {
        return formatMessage(getMessage(), statusMessage);
    }
}
