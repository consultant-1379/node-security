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
 * <p>Exception thrown by NodeIsSynchedValidator class when one or more
 * node names in the command are not SYNCHRONIZED</p>
 * <p>Exception thrown by NodeMustHaveSecurityMoValidator when a node doesn't
 * have a Security MO associated to it.</p>
 * Created by egbobcs
 */
public class NodeNotSynchronizedException extends NscsServiceException{

	private static final long serialVersionUID = 1485537658657005895L;

    {{
		setSuggestedSolution(NscsErrorCodes.PLEASE_ENSURE_THE_NODE_SPECIFIED_IS_SYNCHRONIZED);
	}}

	public NodeNotSynchronizedException() {
		super(NscsErrorCodes.THE_NODE_SPECIFIED_IS_NOT_SYNCHRONIZED);
	}
    
	/**
	 * @return ErrorType.NODE_NOT_SYNCHED
	 */
	@Override
	public ErrorType getErrorType() {
		return ErrorType.NODE_NOT_SYNCHED;
	}
}