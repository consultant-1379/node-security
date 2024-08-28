package com.ericsson.nms.security.nscs.api.exception;

/**
 * <p>Exception thrown by the command parser in case o a syntax error
 * is detected</p>
 * Created by emaynes on 01/05/2014.
 */
public class InvalidCategoryException extends NscsServiceException {

    
	private static final long serialVersionUID = -7494304427215011760L;

	{{
        setSuggestedSolution(NscsErrorCodes.PLEASE_ENSURE_ENTITY_HAS_VALID_CATEGORY);
    }}

    public InvalidCategoryException() {
        super(NscsErrorCodes.INVALID_ENTITY_CATEGORY);
    }

    public InvalidCategoryException(final String message) {
        super(formatMessage(NscsErrorCodes.INVALID_ENTITY_CATEGORY, message));
    }

    public InvalidCategoryException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.INVALID_ENTITY_CATEGORY, message), cause);
    }

    public InvalidCategoryException(final Throwable cause) {
        super(NscsErrorCodes.INVALID_ENTITY_CATEGORY, cause);
    }

    /**
     * Gets the error type
     *
     * @return ErrorType.UNSUPPORTED_NODE_TYPE
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.INVALID_ENTITY_CATEGORY;
    }
}
