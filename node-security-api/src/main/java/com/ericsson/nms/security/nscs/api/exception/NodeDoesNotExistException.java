/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.exception;

/**
 * <p>
 * Exception thrown when a node names in the command is invalid or does not exists
 * </p>
 * 
 * @author xramdag
 */
public class NodeDoesNotExistException extends NscsServiceException {
    /**
     * 
     */
    private static final long serialVersionUID = 434418916618432028L;

    {
        {
            setSuggestedSolution(NscsErrorCodes.SPECIFY_A_VALID_NODE);
        }
    }

    public NodeDoesNotExistException() {
        super(NscsErrorCodes.THE_NODE_SPECIFIED_DOES_NOT_EXIST);
    }

    public NodeDoesNotExistException(final String fdn) {
        super(NscsErrorCodes.THE_NODE_SPECIFIED_DOES_NOT_EXIST + " - " + fdn);
    }

    public NodeDoesNotExistException(final String fdn, final String suggestedSolution) {
        super(NscsErrorCodes.THE_NODE_SPECIFIED_DOES_NOT_EXIST + " - " + fdn + ". " + suggestedSolution);
    }

    /**
     * @return ErrorType.INVALID_NODE_NAME_ERROR
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.INVALID_NODE_NAME_ERROR;
    }
}
