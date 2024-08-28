package com.ericsson.nms.security.nscs.api.exception;

/**
 * <p>Exception thrown when a NetworkElement MO does not have a MeContext
 * associated to it.</p>
 * @author Mayke Nespoli
 */
public class UnassociatedNetworkElementException extends NscsServiceException {

    private static final long serialVersionUID = -5781748163858025226L;

    public UnassociatedNetworkElementException() {
        super(NscsErrorCodes.MECONTEXT_NOT_FOUND);
        setSuggestedSolution(NscsErrorCodes.PLEASE_CREATE_THE_ME_CONTEXT_CORRESPONDING_TO_THE_SPECIFIED_MO);
    }

    /**
     * @return ErrorType.UNASSOCIATED_NETWORK_ELEMENT_ERROR
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.UNASSOCIATED_NETWORK_ELEMENT_ERROR;
    }
}
