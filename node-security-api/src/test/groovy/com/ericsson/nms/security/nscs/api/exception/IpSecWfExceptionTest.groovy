package com.ericsson.nms.security.nscs.api.exception

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import spock.lang.Unroll

class IpSecWfExceptionTest extends CdiSpecification {
    def "no-args constructor" () {
        given:
        def exception = new IpSecWfException();
        expect:
        exception != null
        and:
        exception.getErrorCode() == 10132
        and:
        exception.getErrorType() == NscsServiceException.ErrorType.IPSEC_CONFIGURE_WF_FAILED
        and:
        exception.getLocalizedMessage() == NscsErrorCodes.IPSEC_CONFIGURE_WF_FAILED
        and:
        exception.getMessage() == NscsErrorCodes.IPSEC_CONFIGURE_WF_FAILED
        and:
        exception.getCause() == null
        and:
        exception.getSuggestedSolution() == NscsErrorCodes.SUGGESTED_SOLUTION_CONSULT_ERROR_LOGS
    }

    @Unroll
    def "constructor with message '#message'" () {
        given:
        def exception = new IpSecWfException(message);
        expect:
        exception != null
        and:
        exception.getErrorCode() == 10132
        and:
        exception.getErrorType() == NscsServiceException.ErrorType.IPSEC_CONFIGURE_WF_FAILED
        and:
        exception.getLocalizedMessage() == NscsErrorCodes.IPSEC_CONFIGURE_WF_FAILED + ' : '+ message
        and:
        exception.getMessage() == NscsErrorCodes.IPSEC_CONFIGURE_WF_FAILED + ' : '+ message
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
        def exception = new IpSecWfException(message, cause);
        expect:
        exception != null
        and:
        exception.getErrorCode() == 10132
        and:
        exception.getErrorType() == NscsServiceException.ErrorType.IPSEC_CONFIGURE_WF_FAILED
        and:
        exception.getLocalizedMessage() == NscsErrorCodes.IPSEC_CONFIGURE_WF_FAILED + ' : '+ message
        and:
        exception.getMessage() == NscsErrorCodes.IPSEC_CONFIGURE_WF_FAILED + ' : '+ message
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
        def exception = new IpSecWfException(message, cause, suggested);
        expect:
        exception != null
        and:
        exception.getErrorCode() == 10132
        and:
        exception.getErrorType() == NscsServiceException.ErrorType.IPSEC_CONFIGURE_WF_FAILED
        and:
        exception.getLocalizedMessage() == NscsErrorCodes.IPSEC_CONFIGURE_WF_FAILED + ' : '+ message
        and:
        exception.getMessage() == NscsErrorCodes.IPSEC_CONFIGURE_WF_FAILED + ' : '+ message
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
        def exception = new IpSecWfException((String) message, (String) suggested);
        expect:
        exception != null
        and:
        exception.getErrorCode() == 10132
        and:
        exception.getErrorType() == NscsServiceException.ErrorType.IPSEC_CONFIGURE_WF_FAILED
        and:
        exception.getLocalizedMessage() == NscsErrorCodes.IPSEC_CONFIGURE_WF_FAILED + ' : '+ message
        and:
        exception.getMessage() == NscsErrorCodes.IPSEC_CONFIGURE_WF_FAILED + ' : '+ message
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
