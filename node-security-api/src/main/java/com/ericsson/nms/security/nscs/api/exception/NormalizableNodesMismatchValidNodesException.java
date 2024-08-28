package com.ericsson.nms.security.nscs.api.exception;

/**
 * <p>Exception thrown by the command parser in case o a syntax error
 * is detected</p>
 * Created by emaynes on 01/05/2014.
 */
public class NormalizableNodesMismatchValidNodesException extends NscsServiceException {

    
	private static final long serialVersionUID = -7494304427215011760L;

	{{
        setSuggestedSolution(NscsErrorCodes.PLEASE_CHECK_ONLINE_HELP_FOR_CORRECT_SYNTAX);
    }}

    public NormalizableNodesMismatchValidNodesException() {
        super(NscsErrorCodes.VALID_NODES_NORMALIZABLE_NODES_MISMATCH);
    }

    public NormalizableNodesMismatchValidNodesException(final String message) {
        super(formatMessage(NscsErrorCodes.VALID_NODES_NORMALIZABLE_NODES_MISMATCH, message));
    }

    public NormalizableNodesMismatchValidNodesException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.VALID_NODES_NORMALIZABLE_NODES_MISMATCH, message), cause);
    }

    public NormalizableNodesMismatchValidNodesException(final Throwable cause) {
        super(NscsErrorCodes.VALID_NODES_NORMALIZABLE_NODES_MISMATCH, cause);
    }

    /**
     * Gets the error type
     *
     * @return ErrorType.UNSUPPORTED_NODE_TYPE
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.VALID_NODES_NORMALIZABLE_NODES_MISMATCH;
    }
}
