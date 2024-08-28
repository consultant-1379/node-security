package com.ericsson.nms.security.nscs.api.exception;

/**
 * <p>Exception thrown by the command parser in case o a syntax error
 * is detected</p>
 * Created by emaynes on 01/05/2014.
 */
public class KeyGenerationHandlerException extends NscsServiceException {

    
	private static final long serialVersionUID = -7494304427215011760L;

//	{{
//        setSuggestedSolution("");
//    }}

    public KeyGenerationHandlerException() {
        super(NscsErrorCodes.KEYGEN_HANDLER_ERROR);
    }

    public KeyGenerationHandlerException(final String message) {
        super(formatMessage(NscsErrorCodes.KEYGEN_HANDLER_ERROR, message));
    }

    public KeyGenerationHandlerException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.KEYGEN_HANDLER_ERROR, message), cause);
    }

    public KeyGenerationHandlerException(final Throwable cause) {
        super(NscsErrorCodes.KEYGEN_HANDLER_ERROR, cause);
    }

    /**
     * Gets the error type
     *
     * @return ErrorType.UNSUPPORTED_NODE_TYPE
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.KEYGEN_HANDLER_ERROR;
    }
}
