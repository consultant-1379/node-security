package com.ericsson.nms.security.nscs.api.exception;


/**
 * <p>Exception thrown when an entity related to the specified node does not exist.</p>
 * @author enmadmin
 */
public class EntityForNodeNotFoundException extends NscsServiceException {

	private static final long serialVersionUID = -712799605109695080L;

	public EntityForNodeNotFoundException() {
        super(NscsErrorCodes.ENTITY_FOR_NODE_NOT_FOUND);
    }

    /**
     * @return ErrorType.ENTITY_FOR_NODE_NOT_FOUND
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.ENTITY_FOR_NODE_NOT_FOUND;
    }
}
