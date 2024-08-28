package com.ericsson.nms.security.nscs.api.exception;

/**
 * <p>
 * Exception thrown by NodeMustExistsValidator class when a node names in the command is invalid or does not exists
 * </p>
 *
 * @author emaynes
 */
public class InvalidNodeNameException extends NscsServiceException {

    private static final long serialVersionUID = 5152084481683023932L;

    {
        {
            setSuggestedSolution(NscsErrorCodes.PLEASE_SPECIFY_A_VALID_NETWORK_ELEMENT_THAT_EXISTS_IN_THE_SYSTEM);
        }
    }

    public InvalidNodeNameException() {
        super(NscsErrorCodes.THE_NETWORK_ELEMENT_SPECIFIED_DOES_NOT_EXIST);
    }

    public InvalidNodeNameException(final String fdn) {
        super(NscsErrorCodes.THE_NETWORK_ELEMENT_SPECIFIED_DOES_NOT_EXIST + " - " + fdn);
    }

    public InvalidNodeNameException(final String fdn, final String suggestedSolution) {
        super(NscsErrorCodes.THE_NETWORK_ELEMENT_SPECIFIED_DOES_NOT_EXIST + ". " + suggestedSolution);
    }

    /**
     * @return ErrorType.INVALID_NODE_NAME_ERROR
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.INVALID_NODE_NAME_ERROR;
    }
}
