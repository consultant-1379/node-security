package com.ericsson.nms.security.nscs.api.exception;

/**
 * <p>Exception thrown by the NodeMustBeNormalizableValidator class
 * when one or more nodes in the command don't have NetworkElement MO
 * associated to it.</p>
 * @author Mayke Nespoli
 */
public class NetworkElementNotfoundException extends NscsServiceException {

    private static final long serialVersionUID = -5781748163858025226L;

    public NetworkElementNotfoundException() {
        super(NscsErrorCodes.NETWORK_ELEMENT_NOT_FOUND_FOR_THIS_MECONTEXT);
        setSuggestedSolution(NscsErrorCodes.CREATE_A_NETWORK_ELEMENT_MO_ASSOCIATED_TO_THIS_MECONTEXT);
    }

    /**
     * @return ErrorType.NETWORK_ELEMENT_NOTFOUND_ERROR
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.NETWORK_ELEMENT_NOTFOUND_ERROR;
    }
}
