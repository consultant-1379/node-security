/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.exception;

public class IpSecMoNotFoundException extends NscsServiceException {

	private static final long serialVersionUID = -193293247084354247L;

	public IpSecMoNotFoundException() {
        super(NscsErrorCodes.IP_SEC_NOT_FOUND_FOR_THIS_NODE);
        setSuggestedSolution(NscsErrorCodes.CREATE_A_IPSEC_MO_ASSOCIATED_TO_THIS_NODE);
    }

    /**
     * @return ErrorType.IP_SEC_NOT_FOUND_ERROR
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.IP_SEC_NOT_FOUND_ERROR;
    }
}