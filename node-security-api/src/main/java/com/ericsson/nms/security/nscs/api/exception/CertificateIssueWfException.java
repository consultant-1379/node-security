package com.ericsson.nms.security.nscs.api.exception;

/**
 * <p>Exception thrown by the Issue Certificate Handler in case of a workflow error.
 * </p>
 * @author enmadmin
 */
public class CertificateIssueWfException extends NscsServiceException {

    
	private static final long serialVersionUID = -7494304427215011760L;

	{{
        setSuggestedSolution(NscsErrorCodes.PLEASE_CHECK_ONLINE_HELP_FOR_CORRECT_SYNTAX);
    }}

    public CertificateIssueWfException() {
        super(NscsErrorCodes.CERTIFICATE_ISSUE_WF_FAILED);
    }

    public CertificateIssueWfException(final String message) {
        super(formatMessage(NscsErrorCodes.CERTIFICATE_ISSUE_WF_FAILED, message));
    }

    public CertificateIssueWfException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.CERTIFICATE_ISSUE_WF_FAILED, message), cause);
    }

    public CertificateIssueWfException(final Throwable cause) {
        super(NscsErrorCodes.CERTIFICATE_ISSUE_WF_FAILED, cause);
    }

    /**
     * Gets the error type
     *
     * @return ErrorType.CERTIFICATE_ISSUE_WF_FAILED
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.CERTIFICATE_ISSUE_WF_FAILED;
    }
}
