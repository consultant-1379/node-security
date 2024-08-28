/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.nms.security.nscs.api.exception;

import java.util.List;

/**
 * This exception is thrown when the given node name expression can not be resolved to node names.
 * 
 * @author xpradks
 */
public class InvalidNodeNameExpressionException extends NscsServiceException {

    private static final long serialVersionUID = 7979049171615126928L;

    {
        {
            setSuggestedSolution(NscsErrorCodes.PLEASE_SPECIFY_A_VALID_NODE_NAME_EXPRESSION);
        }
    }

    public InvalidNodeNameExpressionException() {
        super(NscsErrorCodes.INVALID_NODE_NAME_EXPRESSION);
    }

    public InvalidNodeNameExpressionException(final List<String> invalidNames) {
        super(String.format(NscsErrorCodes.INVALID_NODE_NAME_EXPRESSION, invalidNames.toString()));
    }

    public InvalidNodeNameExpressionException(final String message, final List<String> invalidNames) {
        super(String.format(message, invalidNames.toString()));
    }

    public InvalidNodeNameExpressionException(final List<String> invalidNames, final Throwable cause) {
        super(String.format(NscsErrorCodes.INVALID_NODE_NAME_EXPRESSION, invalidNames.toString()), cause);

    }
    public InvalidNodeNameExpressionException(final Throwable cause) {
        super(NscsErrorCodes.INVALID_NODE_NAME_EXPRESSION, cause);
    }
    /**
     * @return ErrorType.INVALID_NODE_NAME_EXPRESSION_ERROR
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.INVALID_NODE_NAME_EXPRESSION_ERROR;
    }
}
