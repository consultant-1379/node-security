package com.ericsson.nms.security.nscs.api.exception;


/**
 * <p>Exception thrown by the NodeValidatorUtility class
 * when issuing a certificate is not valid for the specified node.</p>
 * @author enmadmin
 */
public class NodeNotCertifiableException extends NscsServiceException {

	private static final long serialVersionUID = -712799605109695080L;

	public NodeNotCertifiableException() {
        super(NscsErrorCodes.NODE_NOT_CERTIFIABLE);
        setSuggestedSolution(NscsErrorCodes.SPECIFY_A_CERTFIABLE_NODE);
    }

    /**
     * @return ErrorType.NODE_NOT_CERTIFIABLE
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.NODE_NOT_CERTIFIABLE;
    }
}
