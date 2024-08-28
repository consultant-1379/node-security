package com.ericsson.nms.security.nscs.api.exception;


/**
 * <p>Exception thrown when an entity has not an active certificate.</p>
 * @author enmadmin
 */
public class EntityWithActiveCertificateNotFoundException extends NscsServiceException {

	private static final long serialVersionUID = -712799605109695080L;

	public EntityWithActiveCertificateNotFoundException() {
        super(NscsErrorCodes.ENTITY_WITH_ACTIVE_CERTIFICATE_NOT_FOUND);
    }

    /**
     * @return ErrorType.ENTITY_WITH_ACTIVE_CERTIFICATE_NOT_FOUND
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.ENTITY_WITH_ACTIVE_CERTIFICATE_NOT_FOUND;
    }
}
