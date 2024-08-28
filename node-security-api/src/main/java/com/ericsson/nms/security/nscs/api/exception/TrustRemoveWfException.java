package com.ericsson.nms.security.nscs.api.exception;

/**
 * <p>Exception thrown by the Trust Distribute Handler in case of a workflow error.
 * </p>
 * @author enmadmin
 */
public class TrustRemoveWfException extends NscsServiceException {

	private static final long serialVersionUID = 1553988557811955875L;

	{{
        setSuggestedSolution(NscsErrorCodes.PLEASE_CHECK_ONLINE_HELP_FOR_CORRECT_SYNTAX);
    }}

    public TrustRemoveWfException() {
        super(NscsErrorCodes.TRUST_REMOVE_WF_FAILED);
    }

    public TrustRemoveWfException(final String message) {
        super(formatMessage(NscsErrorCodes.TRUST_REMOVE_WF_FAILED, message));
    }

    public TrustRemoveWfException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.TRUST_REMOVE_WF_FAILED, message), cause);
    }

    public TrustRemoveWfException(final Throwable cause) {
        super(NscsErrorCodes.TRUST_REMOVE_WF_FAILED, cause);
    }

    /**
     * Gets the error type
     *
     * @return ErrorType.TRUST_REMOVE_WF_FAILED
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.TRUST_REMOVE_WF_FAILED;
    }
}
