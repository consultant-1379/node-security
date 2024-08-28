package com.ericsson.nms.security.nscs.api.exception;

/**
 * <p>Exception thrown by the command parser in case o a syntax error
 * is detected</p>
 * Created by emaynes on 01/05/2014.
 */
public class UnsupportedNodeTypeException extends NscsServiceException {

    
	private static final long serialVersionUID = -7494304427215011760L;

	{{
        setSuggestedSolution(NscsErrorCodes.PLEASE_CHECK_ONLINE_HELP_FOR_CORRECT_SYNTAX);
    }}

    public UnsupportedNodeTypeException() {
        super(NscsErrorCodes.UNSUPPORTED_NODE_TYPE);
    }

    public UnsupportedNodeTypeException(final String message) {
        super(formatMessage(NscsErrorCodes.UNSUPPORTED_NODE_TYPE, message));
    }

    public UnsupportedNodeTypeException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.UNSUPPORTED_NODE_TYPE, message), cause);
    }

    public UnsupportedNodeTypeException(final Throwable cause) {
        super(NscsErrorCodes.UNSUPPORTED_NODE_TYPE, cause);
    }

    /**
     * Gets the error type
     *
     * @return ErrorType.UNSUPPORTED_NODE_TYPE
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.UNSUPPORTED_NODE_TYPE;
    }
}
