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


public class NodeIsInWorkflowException extends NscsServiceException{

	private static final long serialVersionUID = -7797984874870804779L;

	{{
		setSuggestedSolution(NscsErrorCodes.PLEASE_WAIT_UNTIL_CURRENT_ACTION_COMPLETE);
	}}

	public NodeIsInWorkflowException() {
		super(NscsErrorCodes.NODE_IS_IN_ONGOING_CONFIGURATION_CHANGE);
	}

	/**
	 * @return ErrorType.NODE_IS_IN_WORKFLOW
	 */
	@Override
	public ErrorType getErrorType() {
		return ErrorType.NODE_IS_IN_WORKFLOW;
	}
}