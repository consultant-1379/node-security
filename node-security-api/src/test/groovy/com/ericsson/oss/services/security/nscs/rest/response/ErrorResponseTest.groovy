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
package com.ericsson.oss.services.security.nscs.rest.response

import com.ericsson.cds.cdi.support.spock.CdiSpecification

import spock.lang.Unroll

class ErrorResponseTest extends CdiSpecification {

    def "no-args constructor" () {
        given: "an error response"
        def errorResponse = new ErrorResponse();
        expect: "error response should not be null"
        errorResponse != null
    }

    @Unroll
    def "user message #userMessage" () {
        given: "an error response"
        def errorResponse = new ErrorResponse();
        when: "set user message to #userMessage"
        errorResponse.setUserMessage(userMessage)
        then: "get user message should return #userMessage"
        errorResponse.getUserMessage() == userMessage
        where:
        userMessage << [
            null,
            "",
            "This is the user message."
        ]
    }

    @Unroll
    def "internal error code #internalErrorCode" () {
        given: "an error response"
        def errorResponse = new ErrorResponse();
        when: "set internal error code to #internalErrorCode"
        errorResponse.setInternalErrorCode(internalErrorCode)
        then: "get internal error code should return #internalErrorCode"
        errorResponse.getInternalErrorCode() == internalErrorCode
        where:
        internalErrorCode << [
            null,
            "",
            "This is the internal error code"
        ]
    }

    @Unroll
    def "developer message #developerMessage" () {
        given: "an error response"
        def errorResponse = new ErrorResponse();
        when: "set developer message to #developerMessage"
        errorResponse.setDeveloperMessage(developerMessage)
        then: "get developer message should return #developerMessage"
        errorResponse.getDeveloperMessage() == developerMessage
        where:
        developerMessage << [
            null,
            "",
            "This is the developer message."
        ]
    }

    @Unroll
    def "error data #errorData" () {
        given: "an error response"
        def errorResponse = new ErrorResponse();
        when: "set error data to #errorData"
        errorResponse.setErrorData(errorData)
        then: "get error data should return #errorData"
        errorResponse.getErrorData() == errorData
        where:
        errorData << [
            null,
            "",
            "These are the error data."
        ]
    }
}
