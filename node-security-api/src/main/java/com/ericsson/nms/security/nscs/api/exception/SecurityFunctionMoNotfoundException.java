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
 * <p>Exception thrown by the NodeMustHaveNetworkElementSecurityFunctionMoValidator class
 * when one or more nodes in the command don't have NetworkElementSecurityFunction MO
 * associated to it.</p>
 * Created by xpawpio
 */
public class SecurityFunctionMoNotfoundException extends NscsServiceException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8301496160377504525L;

	public SecurityFunctionMoNotfoundException() {
        super(NscsErrorCodes.SECURITY_FUNCTION_NOT_FOUND_FOR_THIS_NODE);
        setSuggestedSolution(NscsErrorCodes.CREATE_A_SECURITY_FUNCTION_MO_ASSOCIATED_TO_THIS_NODE);
    }

    /**
     * @return ErrorType.SECURITY_FUNCTION_NOTFOUND_ERROR
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.SECURITY_FUNCTION_NOTFOUND_ERROR;
    }
}
