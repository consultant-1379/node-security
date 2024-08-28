package com.ericsson.nms.security.nscs.api.exception;

/**
 * <p>Exception thrown by the command parser in case o a syntax error
 * is detected</p>
 * Created by emaynes on 01/05/2014.
 */
public class EntitiesWithValidCategoryForNodesNotFound extends NscsServiceException {

    
	private static final long serialVersionUID = -7494304427215011760L;

    public EntitiesWithValidCategoryForNodesNotFound() {
        super(NscsErrorCodes.NO_NODES_WITH_ENTITIES_WITH_SPECIFIED_CATEGORY);
    }

    public EntitiesWithValidCategoryForNodesNotFound(final String message) {
        super(formatMessage(NscsErrorCodes.NO_NODES_WITH_ENTITIES_WITH_SPECIFIED_CATEGORY, message));
    }

    public EntitiesWithValidCategoryForNodesNotFound(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.NO_NODES_WITH_ENTITIES_WITH_SPECIFIED_CATEGORY, message), cause);
    }

    public EntitiesWithValidCategoryForNodesNotFound(final Throwable cause) {
        super(NscsErrorCodes.NO_NODES_WITH_ENTITIES_WITH_SPECIFIED_CATEGORY, cause);
    }

    /**
     * Gets the error type
     *
     * @return ErrorType.NO_NODES_WITH_ENTITIES_WITH_SPECIFIED_CATEGORY
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.NO_NODES_WITH_ENTITIES_WITH_SPECIFIED_CATEGORY;
    }
}
