package com.ericsson.nms.security.nscs.api.exception;

/**
 * <p>Exception thrown by the Trust Distribute Handler in case of a workflow error.
 * </p>
 * @author enmadmin
 */
public class TrustDistributeWfException extends NscsServiceException {

	private static final long serialVersionUID = -584653946476990070L;

	{{
        setSuggestedSolution(NscsErrorCodes.PLEASE_CHECK_ONLINE_HELP_FOR_CORRECT_SYNTAX);
    }}

    public TrustDistributeWfException() {
        super(NscsErrorCodes.TRUST_DISTRIBUTE_WF_FAILED);
    }

    public TrustDistributeWfException(final String message) {
        super(formatMessage(NscsErrorCodes.TRUST_DISTRIBUTE_WF_FAILED, message));
    }

    public TrustDistributeWfException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.TRUST_DISTRIBUTE_WF_FAILED, message), cause);
    }

    public TrustDistributeWfException(final Throwable cause) {
        super(NscsErrorCodes.TRUST_DISTRIBUTE_WF_FAILED, cause);
    }

    /**
     * Gets the error type
     *
     * @return ErrorType.TRUST_DISTRIBUTE_WF_FAILED
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.TRUST_DISTRIBUTE_WF_FAILED;
    }
}
