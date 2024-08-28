/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2022
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

class NscsLdapProxyExceptionTest extends CdiSpecification {

    def "no-args constructor" () {
        given:
        def exception = new NscsLdapProxyException();
        expect:
        exception != null
        and:
        exception.getErrorCode() == 10130
        and:
        exception.getErrorType() == ErrorType.LDAP_PROXY_FAILED
        and:
        exception.getLocalizedMessage() == NscsErrorCodes.LDAP_PROXY_ERROR
        and:
        exception.getMessage() == NscsErrorCodes.LDAP_PROXY_ERROR
        and:
        exception.getCause() == null
        and:
        exception.getSuggestedSolution() == NscsErrorCodes.SUGGESTED_SOLUTION_CONSULT_ERROR_LOGS
    }

    @Unroll
    def "constructor with message '#message'" () {
        given:
        def exception = new NscsLdapProxyException(message);
        expect:
        exception != null
        and:
        exception.getErrorCode() == 10130
        and:
        exception.getErrorType() == ErrorType.LDAP_PROXY_FAILED
        and:
        exception.getLocalizedMessage() == NscsErrorCodes.LDAP_PROXY_ERROR + ' : '+ message
        and:
        exception.getMessage() == NscsErrorCodes.LDAP_PROXY_ERROR + ' : '+ message
        and:
        exception.getCause() == null
        and:
        exception.getSuggestedSolution() == NscsErrorCodes.SUGGESTED_SOLUTION_CONSULT_ERROR_LOGS
        where:
        message << [null, '', 'my message']
    }

    @Unroll
    def "constructor with message '#message' and cause" () {
        given:
        def cause = new Exception()
        and:
        def exception = new NscsLdapProxyException(message, cause);
        expect:
        exception != null
        and:
        exception.getErrorCode() == 10130
        and:
        exception.getErrorType() == ErrorType.LDAP_PROXY_FAILED
        and:
        exception.getLocalizedMessage() == NscsErrorCodes.LDAP_PROXY_ERROR + ' : '+ message
        and:
        exception.getMessage() == NscsErrorCodes.LDAP_PROXY_ERROR + ' : '+ message
        and:
        exception.getCause() == cause
        and:
        exception.getSuggestedSolution() == NscsErrorCodes.SUGGESTED_SOLUTION_CONSULT_ERROR_LOGS
        where:
        message << [null, '', 'my message']
    }

    @Unroll
    def "constructor with message '#message' and cause and #suggested" () {
        given:
        def cause = new Exception()
        and:
        def exception = new NscsLdapProxyException(message, cause, suggested);
        expect:
        exception != null
        and:
        exception.getErrorCode() == 10130
        and:
        exception.getErrorType() == ErrorType.LDAP_PROXY_FAILED
        and:
        exception.getLocalizedMessage() == NscsErrorCodes.LDAP_PROXY_ERROR + ' : '+ message
        and:
        exception.getMessage() == NscsErrorCodes.LDAP_PROXY_ERROR + ' : '+ message
        and:
        exception.getCause() == cause
        and:
        exception.getSuggestedSolution() == expectedsuggested
        where:
        message << [
            null,
            '',
            'my message',
            null,
            'my message'
        ]
        suggested << [
            'my-suggestion',
            'my-suggestion',
            null,
            null,
            'my-suggestion'
        ]
        expectedsuggested << [
            'my-suggestion',
            'my-suggestion',
            '',
            '',
            'my-suggestion'
        ]
    }

    @Unroll
    def "constructor with message '#message' and #suggested" () {
        given:
        def exception = new NscsLdapProxyException((String) message, (String) suggested);
        expect:
        exception != null
        and:
        exception.getErrorCode() == 10130
        and:
        exception.getErrorType() == ErrorType.LDAP_PROXY_FAILED
        and:
        exception.getLocalizedMessage() == NscsErrorCodes.LDAP_PROXY_ERROR + ' : '+ message
        and:
        exception.getMessage() == NscsErrorCodes.LDAP_PROXY_ERROR + ' : '+ message
        and:
        exception.getCause() == null
        and:
        exception.getSuggestedSolution() == expectedsuggested
        where:
        message << [
            null,
            '',
            'my message',
            null,
            'my message'
        ]
        suggested << [
            'my-suggestion',
            'my-suggestion',
            null,
            null,
            'my-suggestion'
        ]
        expectedsuggested << [
            'my-suggestion',
            'my-suggestion',
            '',
            '',
            'my-suggestion'
        ]
    }
}
