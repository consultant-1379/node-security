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

class NscsBadRequestExceptionTest extends CdiSpecification {

    def "no-args constructor" () {
        given: "a bad request exception"
        def badRequestException = new NscsBadRequestException();
        expect: " bad request exception should not be null"
        badRequestException != null
        and:
        badRequestException.getErrorCode() == 10128
        and:
        badRequestException.getErrorType() == ErrorType.BAD_REQUEST
        and:
        badRequestException.getLocalizedMessage() == 'Bad request'
        and:
        badRequestException.getMessage() == 'Bad request'
        and:
        badRequestException.getCause() == null
        and:
        badRequestException.getSuggestedSolution() == 'Please provide valid input parameters.'
    }

    @Unroll
    def "message constructor" () {
        given: "a bad request exception"
        def badRequestException = new NscsBadRequestException(message);
        expect: " bad request exception should not be null"
        badRequestException != null
        and:
        badRequestException.getErrorCode() == 10128
        and:
        badRequestException.getErrorType() == ErrorType.BAD_REQUEST
        and:
        badRequestException.getLocalizedMessage() == 'Bad request'+' : '+ message
        and:
        badRequestException.getMessage() == 'Bad request'+' : '+ message
        and:
        badRequestException.getCause() == null
        and:
        badRequestException.getSuggestedSolution() == 'Please provide valid input parameters.'
        where:
        message << [null, '', 'my message']
    }

    @Unroll
    def "message and cause constructor" () {
        given: 'a cause exception'
        def cause = new Exception()
        and: "a bad request exception"
        def badRequestException = new NscsBadRequestException(message, cause);
        expect: " bad request exception should not be null"
        badRequestException != null
        and:
        badRequestException.getErrorCode() == 10128
        and:
        badRequestException.getErrorType() == ErrorType.BAD_REQUEST
        and:
        badRequestException.getLocalizedMessage() == 'Bad request'+' : '+ message
        and:
        badRequestException.getMessage() == 'Bad request'+' : '+ message
        and:
        badRequestException.getCause() == cause
        and:
        badRequestException.getSuggestedSolution() == 'Please provide valid input parameters.'
        where:
        message << [null, '', 'my message']
    }

    @Unroll
    def "message and cause and suggested solution constructor" () {
        given: 'a cause exception'
        def cause = new Exception()
        and: "a bad request exception"
        def badRequestException = new NscsBadRequestException(message, cause, suggested);
        expect: " bad request exception should not be null"
        badRequestException != null
        and:
        badRequestException.getErrorCode() == 10128
        and:
        badRequestException.getErrorType() == ErrorType.BAD_REQUEST
        and:
        badRequestException.getLocalizedMessage() == 'Bad request'+' : '+ message
        and:
        badRequestException.getMessage() == 'Bad request'+' : '+ message
        and:
        badRequestException.getCause() == cause
        and:
        badRequestException.getSuggestedSolution() == expectedsuggested
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
    def "message and suggested solution constructor" () {
        given: "a bad request exception"
        def badRequestException = new NscsBadRequestException((String) message, (String) suggested);
        expect: " bad request exception should not be null"
        badRequestException != null
        and:
        badRequestException.getErrorCode() == 10128
        and:
        badRequestException.getErrorType() == ErrorType.BAD_REQUEST
        and:
        badRequestException.getLocalizedMessage() == 'Bad request'+' : '+ message
        and:
        badRequestException.getMessage() == 'Bad request'+' : '+ message
        and:
        badRequestException.getCause() == null
        and:
        badRequestException.getSuggestedSolution() == expectedsuggested
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
