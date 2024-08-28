package com.ericsson.nms.security.nscs.api.exception;

/**
 * <p>Exception thrown by the Reissue Certificate Handler in case of a workflow error.
 * </p>
 * @author enmadmin
 */
public class CertificateReissueWfException extends NscsServiceException {

    
	private static final long serialVersionUID = -7494304427215011760L;

	{{
        setSuggestedSolution(NscsErrorCodes.PLEASE_CHECK_ONLINE_HELP_FOR_CORRECT_SYNTAX);
    }}

    public CertificateReissueWfException() {
        super(NscsErrorCodes.CERTIFICATE_REISSUE_WF_FAILED);
    }

    public CertificateReissueWfException(final String message) {
        super(formatMessage(NscsErrorCodes.CERTIFICATE_REISSUE_WF_FAILED, message));
    }

    public CertificateReissueWfException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.CERTIFICATE_REISSUE_WF_FAILED, message), cause);
    }

    public CertificateReissueWfException(final Throwable cause) {
        super(NscsErrorCodes.CERTIFICATE_REISSUE_WF_FAILED, cause);
    }

    /**
     * Gets the error type
     *
     * @return ErrorType.CERTIFICATE_REISSUE_WF_FAILED
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.CERTIFICATE_REISSUE_WF_FAILED;
    }
}
