package com.ericsson.nms.security.nscs.api.exception;

/**
 * <p>Exception thrown by the command parser in case no node is found in 
 * the system.</p>
 */
public class NoNodesFoundException extends NscsServiceException {

   
	private static final long serialVersionUID = -3334055607517639022L;

    public NoNodesFoundException() {
        super(NscsErrorCodes.NO_VALID_NODE_FOUND);
    }

    public NoNodesFoundException(final String message) {
        super(formatMessage(message, NscsErrorCodes.NO_VALID_NODE_FOUND));
    }

    public NoNodesFoundException(final String message, final Throwable cause) {
        super(formatMessage(message, NscsErrorCodes.NO_VALID_NODE_FOUND), cause);
    }

    public NoNodesFoundException(final Throwable cause) {
        super(NscsErrorCodes.NO_VALID_NODE_FOUND, cause);
    }

    /**
     * Gets the error type
     *
     * @return ErrorType.NO_VALID_NODE_FOUND
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.NO_VALID_NODE_FOUND;
    }
}
