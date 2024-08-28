/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
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
 * <p>Exception thrown by NodeIsNotAtRequestedLevelValidator class</p>
 * 
 * Created by egbobcs
 */
public class RequestedLevelAlreadySetException extends NscsServiceException {
	
	private static final long serialVersionUID = 4619828417136992518L;

	public RequestedLevelAlreadySetException() {
        super(NscsErrorCodes.REQUESTED_LEVEL_ALREADY_SET_MESSAGE);
        setSuggestedSolution(NscsErrorCodes.REQUESTED_LEVEL_ALREADY_SET_SOLUTION);
    }

    /**
     * @return ErrorType.REQUESTED_LEVEL_ALREADY_SET_ERROR
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.REQUESTED_LEVEL_ALREADY_SET_ERROR;
    }
}
