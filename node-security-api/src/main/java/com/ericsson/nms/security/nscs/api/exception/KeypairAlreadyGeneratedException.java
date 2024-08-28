package com.ericsson.nms.security.nscs.api.exception;

/**
 * <p>Exception thrown by the command parser in case o a syntax error
 * is detected</p>
 * Created by emaynes on 01/05/2014.
 */
public class KeypairAlreadyGeneratedException extends NscsServiceException {

    
	private static final long serialVersionUID = -7494304427215011760L;

	{{
        setSuggestedSolution(NscsErrorCodes.PLEASE_CHECK_ONLINE_HELP_FOR_CORRECT_SYNTAX);
    }}

    public KeypairAlreadyGeneratedException() {
        super(NscsErrorCodes.KEYPAIR_ALREADY_GENERATED);
    }

    public KeypairAlreadyGeneratedException(final String message) {
        super(formatMessage(NscsErrorCodes.KEYPAIR_ALREADY_GENERATED, message));
    }

    public KeypairAlreadyGeneratedException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.KEYPAIR_ALREADY_GENERATED, message), cause);
    }

    public KeypairAlreadyGeneratedException(final Throwable cause) {
        super(NscsErrorCodes.KEYPAIR_ALREADY_GENERATED, cause);
    }

    /**
     * Gets the error type
     *
     * @return ErrorType.UNSUPPORTED_NODE_TYPE
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.KEYPAIR_ALREADY_GENERATED;
    }
}
