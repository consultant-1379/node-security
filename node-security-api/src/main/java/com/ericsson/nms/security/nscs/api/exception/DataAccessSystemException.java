package com.ericsson.nms.security.nscs.api.exception;

/**
 * <p>Exception thrown by the Data access layer when a data operation fails due to a system error</p>
 * Created by emaynes on 02/05/2014.
 */
public class DataAccessSystemException extends NscsSystemException {

    /**
     *
     */
    private static final long serialVersionUID = -2256145721353800675L;
    private String statusMessage;

    public DataAccessSystemException(final String statusMessage) {
        super(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR);
        this.statusMessage = statusMessage;
    }

    {{
        setSuggestedSolution(" An error occurred while executing the command on the system. Consult the error and command logs for more information.");
    }}

    public DataAccessSystemException(final String message, final String statusMessage) {
        super(formatMessage(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR, message));
        this.statusMessage = statusMessage;
    }

    public DataAccessSystemException(final String message, final Throwable cause, final String statusMessage) {
        super(formatMessage(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR, message), cause);
        this.statusMessage = statusMessage;
    }

    public DataAccessSystemException(final Throwable cause, final String statusMessage) {
        super(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR, cause);
        this.statusMessage = statusMessage;
    }

    public DataAccessSystemException(final Throwable cause) {
        super(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR, cause);
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * @return ErrorType.DATA_ACCESS_SYSTEM_ERROR
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.DATA_ACCESS_SYSTEM_ERROR;
    }

    @Override
    public String toString() {
        return formatMessage(getMessage(), statusMessage);
    }
}
