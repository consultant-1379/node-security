package com.ericsson.nms.security.nscs.api.exception

import static org.junit.Assert.*

import org.junit.Test

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException.ErrorType

import spock.lang.Unroll

class GenerateEnrollmentInfoExceptionTest extends CdiSpecification {

    def "no-args constructor" () {
        given:
        def generateEnrollmentInfoException = new GenerateEnrollmentInfoException()
        expect:
        generateEnrollmentInfoException != null
        and:
        generateEnrollmentInfoException.getErrorCode() == 10131
        and:
        generateEnrollmentInfoException.getErrorType() == ErrorType.GENERATE_ENROLLMENT_INFO_FAILED
        and:
        generateEnrollmentInfoException.getLocalizedMessage() == NscsErrorCodes.GENERATE_ENROLLMENT_INFO_ERROR
        and:
        generateEnrollmentInfoException.getMessage() == NscsErrorCodes.GENERATE_ENROLLMENT_INFO_ERROR
        and:
        generateEnrollmentInfoException.getCause() == null
        and:
        generateEnrollmentInfoException.getSuggestedSolution() == NscsErrorCodes.SUGGESTED_SOLUTION_CONSULT_ERROR_LOGS
    }

    @Unroll
    def "message constructor" () {
        given:
        def generateEnrollmentInfoException = new GenerateEnrollmentInfoException(message)
        expect:
        generateEnrollmentInfoException != null
        and:
        generateEnrollmentInfoException.getErrorCode() == 10131
        and:
        generateEnrollmentInfoException.getErrorType() == ErrorType.GENERATE_ENROLLMENT_INFO_FAILED
        and:
        generateEnrollmentInfoException.getLocalizedMessage() == NscsErrorCodes.GENERATE_ENROLLMENT_INFO_ERROR+' : '+ message
        and:
        generateEnrollmentInfoException.getMessage() == NscsErrorCodes.GENERATE_ENROLLMENT_INFO_ERROR+' : '+ message
        and:
        generateEnrollmentInfoException.getCause() == null
        and:
        generateEnrollmentInfoException.getSuggestedSolution() == NscsErrorCodes.SUGGESTED_SOLUTION_CONSULT_ERROR_LOGS
        where:
        message << [null, '', 'my message']
    }

    @Unroll
    def "message and cause constructor" () {
        given:
        def cause = new Exception()
        and:
        def generateEnrollmentInfoException = new GenerateEnrollmentInfoException(message, cause);
        expect:
        generateEnrollmentInfoException != null
        and:
        generateEnrollmentInfoException.getErrorCode() == 10131
        and:
        generateEnrollmentInfoException.getErrorType() == ErrorType.GENERATE_ENROLLMENT_INFO_FAILED
        and:
        generateEnrollmentInfoException.getLocalizedMessage() == NscsErrorCodes.GENERATE_ENROLLMENT_INFO_ERROR+' : '+ message
        and:
        generateEnrollmentInfoException.getMessage() == NscsErrorCodes.GENERATE_ENROLLMENT_INFO_ERROR+' : '+ message
        and:
        generateEnrollmentInfoException.getCause() == cause
        and:
        generateEnrollmentInfoException.getSuggestedSolution() == NscsErrorCodes.SUGGESTED_SOLUTION_CONSULT_ERROR_LOGS
        where:
        message << [null, '', 'my message']
    }

    @Unroll
    def "message and cause and suggested solution constructor" () {
        given:
        def cause = new Exception()
        and:
        def generateEnrollmentInfoException = new GenerateEnrollmentInfoException(message, cause, suggested);
        expect:
        generateEnrollmentInfoException != null
        and:
        generateEnrollmentInfoException.getErrorCode() == 10131
        and:
        generateEnrollmentInfoException.getErrorType() == ErrorType.GENERATE_ENROLLMENT_INFO_FAILED
        and:
        generateEnrollmentInfoException.getLocalizedMessage() == NscsErrorCodes.GENERATE_ENROLLMENT_INFO_ERROR+' : '+ message
        and:
        generateEnrollmentInfoException.getMessage() == NscsErrorCodes.GENERATE_ENROLLMENT_INFO_ERROR+' : '+ message
        and:
        generateEnrollmentInfoException.getCause() == cause
        and:
        generateEnrollmentInfoException.getSuggestedSolution() == expectedsuggested
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
        given:
        def generateEnrollmentInfoException = new GenerateEnrollmentInfoException((String) message, (String) suggested);
        expect:
        generateEnrollmentInfoException != null
        and:
        generateEnrollmentInfoException.getErrorCode() == 10131
        and:
        generateEnrollmentInfoException.getErrorType() == ErrorType.GENERATE_ENROLLMENT_INFO_FAILED
        and:
        generateEnrollmentInfoException.getLocalizedMessage() == NscsErrorCodes.GENERATE_ENROLLMENT_INFO_ERROR+' : '+ message
        and:
        generateEnrollmentInfoException.getMessage() == NscsErrorCodes.GENERATE_ENROLLMENT_INFO_ERROR+' : '+ message
        and:
        generateEnrollmentInfoException.getCause() == null
        and:
        generateEnrollmentInfoException.getSuggestedSolution() == expectedsuggested
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
