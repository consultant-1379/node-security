/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2021
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

class TestWfsExceptionTest extends CdiSpecification {

    def "no-args constructor" () {
        given:
        when: "instantiating using no-arg constructor"
        def TestWfsException exception = new TestWfsException()
        then: "exception should not be null"
        exception != null
        and:
        exception.getMessage() == "Unexpected Internal Error"
        and:
        exception.getErrorCode() == 10099
        and:
        exception.getErrorType() == ErrorType.UNEXPECTED_ERROR
        and:
        exception.getSuggestedSolution() == NscsErrorCodes.PLEASE_CHECK_ONLINE_HELP_FOR_CORRECT_SYNTAX
    }

    @Unroll
    def "fields constructor message '#message'" () {
        given:
        when: "instantiated using fields constructor message"
        def TestWfsException exception = new TestWfsException(message)
        then: "exception should not be null"
        exception != null
        and:
        exception.getMessage() == "Unexpected Internal Error : " + message
        and:
        exception.getErrorCode() == 10099
        and:
        exception.getErrorType() == ErrorType.UNEXPECTED_ERROR
        and:
        exception.getSuggestedSolution() == NscsErrorCodes.PLEASE_CHECK_ONLINE_HELP_FOR_CORRECT_SYNTAX
        where:
        message << [
            "developer message",
            ''
        ]
    }

    @Unroll
    def "fields constructor message '#message' and throwable" () {
        given:
        when: "instantiated using fields constructor message and throwable"
        def TestWfsException exception = new TestWfsException(message, new Throwable())
        then: "exception should not be null"
        exception != null
        and:
        exception.getMessage() == "Unexpected Internal Error : " + message
        and:
        exception.getErrorCode() == 10099
        and:
        exception.getErrorType() == ErrorType.UNEXPECTED_ERROR
        and:
        exception.getSuggestedSolution() == NscsErrorCodes.PLEASE_CHECK_ONLINE_HELP_FOR_CORRECT_SYNTAX
        where:
        message << [
            "developer message",
            ''
        ]
    }
    def "fields constructor throwable" () {
        given:
        when: "instantiated using fields constructor throwable"
        def TestWfsException exception = new TestWfsException(new Throwable())
        then: "exception should not be null"
        exception != null
        and:
        exception.getMessage() == "Unexpected Internal Error"
        and:
        exception.getErrorCode() == 10099
        and:
        exception.getErrorType() == ErrorType.UNEXPECTED_ERROR
        and:
        exception.getSuggestedSolution() == NscsErrorCodes.PLEASE_CHECK_ONLINE_HELP_FOR_CORRECT_SYNTAX
    }
}
