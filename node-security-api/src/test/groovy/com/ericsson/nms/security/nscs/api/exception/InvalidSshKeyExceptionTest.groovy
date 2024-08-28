package com.ericsson.nms.security.nscs.api.exception

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import spock.lang.Unroll

class InvalidSshKeyExceptionTest extends CdiSpecification {

    def "no-args constructor" () {
        given:
        def invalidSshKeyExceptionInstance = new InvalidSshKeyException()
        expect:
        invalidSshKeyExceptionInstance != null
        and:
        invalidSshKeyExceptionInstance.getErrorCode() == (NscsServiceException.ERROR_CODE_START_INT + NscsServiceException.ErrorType.SSH_INVALID_KEY_GENERATED.toInt())
        and:
        invalidSshKeyExceptionInstance.getErrorType() == NscsServiceException.ErrorType.SSH_INVALID_KEY_GENERATED
        and:
        invalidSshKeyExceptionInstance.getLocalizedMessage() == NscsErrorCodes.INVALID_SSH_KEY_GENERATED
        and:
        invalidSshKeyExceptionInstance.getMessage() == NscsErrorCodes.INVALID_SSH_KEY_GENERATED
        and:
        invalidSshKeyExceptionInstance.getCause() == null
        and:
        invalidSshKeyExceptionInstance.getSuggestedSolution() == NscsErrorCodes.PLEASE_CHECK_ONLINE_HELP_FOR_CORRECT_SYNTAX
    }

    @Unroll
    def "constructor with message '#message'" () {
        given:
        def invalidSshKeyExceptionInstance = new InvalidSshKeyException(message)
        expect:
        invalidSshKeyExceptionInstance != null
        and:
        invalidSshKeyExceptionInstance.getErrorCode() == (NscsServiceException.ERROR_CODE_START_INT + NscsServiceException.ErrorType.SSH_INVALID_KEY_GENERATED.toInt())
        and:
        invalidSshKeyExceptionInstance.getErrorType() == NscsServiceException.ErrorType.SSH_INVALID_KEY_GENERATED
        and:
        invalidSshKeyExceptionInstance.getLocalizedMessage() == NscsErrorCodes.INVALID_SSH_KEY_GENERATED + ' : '+ message
        and:
        invalidSshKeyExceptionInstance.getMessage() == NscsErrorCodes.INVALID_SSH_KEY_GENERATED + ' : '+ message
        and:
        invalidSshKeyExceptionInstance.getCause() == null
        and:
        invalidSshKeyExceptionInstance.getSuggestedSolution() == NscsErrorCodes.PLEASE_CHECK_ONLINE_HELP_FOR_CORRECT_SYNTAX
        where:
        message << [null, '', 'my message']
    }

    @Unroll
    def "constructor with message '#message' and cause" () {
        given:
        def cause = new Exception()
        and:
        def invalidSshKeyExceptionInstance = new InvalidSshKeyException(message, cause);
        expect:
        invalidSshKeyExceptionInstance != null
        and:
        invalidSshKeyExceptionInstance.getErrorCode() == (NscsServiceException.ERROR_CODE_START_INT + NscsServiceException.ErrorType.SSH_INVALID_KEY_GENERATED.toInt())
        and:
        invalidSshKeyExceptionInstance.getErrorType() == NscsServiceException.ErrorType.SSH_INVALID_KEY_GENERATED
        and:
        invalidSshKeyExceptionInstance.getLocalizedMessage() == NscsErrorCodes.INVALID_SSH_KEY_GENERATED + ' : '+ message
        and:
        invalidSshKeyExceptionInstance.getMessage() == NscsErrorCodes.INVALID_SSH_KEY_GENERATED + ' : '+ message
        and:
        invalidSshKeyExceptionInstance.getCause() == cause
        and:
        invalidSshKeyExceptionInstance.getSuggestedSolution() == NscsErrorCodes.PLEASE_CHECK_ONLINE_HELP_FOR_CORRECT_SYNTAX
        where:
        message << [null, '', 'my message']
    }
}
