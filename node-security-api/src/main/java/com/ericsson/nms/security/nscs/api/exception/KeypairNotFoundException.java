package com.ericsson.nms.security.nscs.api.exception;

/**
 * <p>Exception thrown by the command parser in case o a syntax error
 * is detected</p>
 * Created by emaynes on 01/05/2014.
 */
public class KeypairNotFoundException extends NscsServiceException {

    
	private static final long serialVersionUID = -7494304427215011760L;

	{{
        setSuggestedSolution(NscsErrorCodes.PLEASE_CHECK_ONLINE_HELP_FOR_CORRECT_SYNTAX);
    }}

    public KeypairNotFoundException() {
        super(NscsErrorCodes.KEYPAIR_NOT_FOUND);
    }

    public KeypairNotFoundException(final String message) {
        super(formatMessage(NscsErrorCodes.KEYPAIR_NOT_FOUND, message));
    }

    public KeypairNotFoundException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.KEYPAIR_NOT_FOUND, message), cause);
    }

    public KeypairNotFoundException(final Throwable cause) {
        super(NscsErrorCodes.KEYPAIR_NOT_FOUND, cause);
    }

    /**
     * Gets the error type
     *
     * @return ErrorType.UNSUPPORTED_NODE_TYPE
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.KEYPAIR_NOT_FOUND;
    }
}
