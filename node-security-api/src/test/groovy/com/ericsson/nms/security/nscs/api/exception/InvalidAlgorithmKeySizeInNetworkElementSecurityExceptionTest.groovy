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
package com.ericsson.nms.security.nscs.api.exception

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException.ErrorType

import spock.lang.Unroll

class InvalidAlgorithmKeySizeInNetworkElementSecurityExceptionTest extends CdiSpecification {

    def "no-args constructor" () {
        given:
        def exception = new InvalidAlgorithmKeySizeInNetworkElementSecurityException();
        expect:
        exception != null
        and:
        exception.getErrorCode() == 10135
        and:
        exception.getErrorType() == ErrorType.INVALID_ALGORITH_KEY_SIZE_IN_NETWORK_ELEMENT_SECURITY
        and:
        exception.getLocalizedMessage() == NscsErrorCodes.INVALID_ALGORITHM_KEY_SIZE_IN_NES_MO
        and:
        exception.getMessage() == NscsErrorCodes.INVALID_ALGORITHM_KEY_SIZE_IN_NES_MO
        and:
        exception.getCause() == null
        and:
        exception.getSuggestedSolution() == NscsErrorCodes.PLEASE_PERFORM_SSHKEY_UPDATE_WITH_VALID_ALGO_TYPE_SIZE
    }

    @Unroll
    def "constructor with message '#message'" () {
        given:
        def exception = new InvalidAlgorithmKeySizeInNetworkElementSecurityException(message);
        expect:
        exception != null
        and:
        exception.getErrorCode() == 10135
        and:
        exception.getErrorType() == ErrorType.INVALID_ALGORITH_KEY_SIZE_IN_NETWORK_ELEMENT_SECURITY
        and:
        exception.getLocalizedMessage() == NscsErrorCodes.INVALID_ALGORITHM_KEY_SIZE_IN_NES_MO + ' : '+ message
        and:
        exception.getMessage() == NscsErrorCodes.INVALID_ALGORITHM_KEY_SIZE_IN_NES_MO + ' : '+ message
        and:
        exception.getCause() == null
        and:
        exception.getSuggestedSolution() == NscsErrorCodes.PLEASE_PERFORM_SSHKEY_UPDATE_WITH_VALID_ALGO_TYPE_SIZE
        where:
        message << [null, '', 'my message']
    }
}
