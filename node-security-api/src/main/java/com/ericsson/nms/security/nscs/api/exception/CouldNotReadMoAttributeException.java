package com.ericsson.nms.security.nscs.api.exception;

/**
 * <p>Exception thrown by the command parser in case o a syntax error
 * is detected</p>
 * Created by emaynes on 01/05/2014.
 */
public class CouldNotReadMoAttributeException extends NscsServiceException {

    
	private static final long serialVersionUID = -7494304427215011760L;

	{{
        setSuggestedSolution(NscsErrorCodes.PLEASE_CHECK_ONLINE_HELP_FOR_CORRECT_SYNTAX);
    }}

    public CouldNotReadMoAttributeException() {
        super(NscsErrorCodes.COULD_NOT_READ_MO_ATTRIBUTES);
    }

    public CouldNotReadMoAttributeException(final String message) {
        super(formatMessage(NscsErrorCodes.COULD_NOT_READ_MO_ATTRIBUTES, message));
    }

    public CouldNotReadMoAttributeException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.COULD_NOT_READ_MO_ATTRIBUTES, message), cause);
    }

    public CouldNotReadMoAttributeException(final Throwable cause) {
        super(NscsErrorCodes.COULD_NOT_READ_MO_ATTRIBUTES, cause);
    }

    /**
     * Gets the error type
     *
     * @return ErrorType.UNSUPPORTED_NODE_TYPE
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.COULD_NOT_READ_MO_ATTRIBUTES;
    }
}
