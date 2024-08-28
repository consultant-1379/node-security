/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.exception;


public class InvalidAlarmSupervisionStateException extends NscsServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6382431396457143569L;

	
	{{
		setSuggestedSolution(NscsErrorCodes.PLEASE_ENSURE_THE_ALARMSUPERVISION_SET_TRUE);
	}}

	public InvalidAlarmSupervisionStateException() {
		super(NscsErrorCodes.THE_NODE_SPECIFIED_IS_NOT_SUPERVISED);
	}
	
	/* (non-Javadoc)
	 * @see com.ericsson.nms.security.nscs.api.exception.NscsServiceException#getErrorType()
	 */
	@Override
	public ErrorType getErrorType() {
		return ErrorType.NODE_NOT_SUPERVISED;
	}

}
