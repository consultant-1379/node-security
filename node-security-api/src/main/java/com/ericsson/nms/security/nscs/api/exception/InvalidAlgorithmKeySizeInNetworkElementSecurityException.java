/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2024
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
 * Exception thrown when the algorithmAndKeySize attribute of NetworkElementSecurity MO contains an invalid value.
 */
public class InvalidAlgorithmKeySizeInNetworkElementSecurityException extends NscsServiceException {

    private static final long serialVersionUID = -3015892807168259456L;

    /**
     * Construct a new exception with INVALID_ALGORITHM_KEY_SIZE_IN_NES_MO as its detail message
     */
    public InvalidAlgorithmKeySizeInNetworkElementSecurityException() {
        super(NscsErrorCodes.INVALID_ALGORITHM_KEY_SIZE_IN_NES_MO);
        setSuggestedSolutionLocal(NscsErrorCodes.PLEASE_PERFORM_SSHKEY_UPDATE_WITH_VALID_ALGO_TYPE_SIZE);
    }

    /**
     * Construct a new exception with INVALID_ALGORITHM_KEY_SIZE_IN_NES_MO appended by user message as its detail message
     * 
     * @param message
     *            the user message
     */
    public InvalidAlgorithmKeySizeInNetworkElementSecurityException(final String message) {
        super(formatMessage(NscsErrorCodes.INVALID_ALGORITHM_KEY_SIZE_IN_NES_MO, message));
        setSuggestedSolutionLocal(NscsErrorCodes.PLEASE_PERFORM_SSHKEY_UPDATE_WITH_VALID_ALGO_TYPE_SIZE);
    }

    /**
     * @return ErrorType.INVALID_ALGORITH_KEY_SIZE_IN_NETWORK_ELEMENT_SECURITY
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.INVALID_ALGORITH_KEY_SIZE_IN_NETWORK_ELEMENT_SECURITY;
    }

    private void setSuggestedSolutionLocal(final String suggestedSolution) {
        super.setSuggestedSolution(suggestedSolution);
    }

}
