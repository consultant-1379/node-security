package com.ericsson.nms.security.nscs.api.exception;


/**
 * <p>Exception thrown when an entity has not a valid category.</p>
 * @author enmadmin
 */
public class EntityWithValidCategoryNotFoundException extends NscsServiceException {

	private static final long serialVersionUID = -712799605109695080L;

	public EntityWithValidCategoryNotFoundException() {
        super(NscsErrorCodes.ENTITY_WITH_VALID_CATEGORY_NOT_FOUND);
    }

    /**
     * @return ErrorType.ENTITY_WITH_VALID_CATEGORY_NOT_FOUND
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.ENTITY_WITH_VALID_CATEGORY_NOT_FOUND;
    }
}
