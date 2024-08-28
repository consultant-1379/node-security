package com.ericsson.nms.security.nscs.api.exception;

/**
 * <p>Exception thrown by command handler when no of nodes entered exceeds the supported no</p>
 * Created by ejuhpar on 12/11/2014.
 */
public class MaxNodesExceededException extends NscsServiceException {
    private static final long serialVersionUID = -1L;


    public MaxNodesExceededException(final int max) {
        super(NscsErrorCodes.NUMBER_OF_NODES_SPECIFIED_EXCEEDS_THE_MAXIMUM);
        setSuggestedSolution(String.format(NscsErrorCodes.MAX_NO_NODES_SUPPORTED, max));
    }

    public MaxNodesExceededException(final int max, final Throwable cause) {
        super(NscsErrorCodes.NUMBER_OF_NODES_SPECIFIED_EXCEEDS_THE_MAXIMUM, cause);
        setSuggestedSolution(String.format(NscsErrorCodes.MAX_NO_NODES_SUPPORTED, max));
    }

    @Override
    public ErrorType getErrorType() {
        return ErrorType.MAX_NODES_EXCEEDED_ERROR;
    }
}
