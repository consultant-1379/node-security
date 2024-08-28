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
package com.ericsson.oss.services.security.nscs.interceptor

import java.lang.reflect.Method

import javax.interceptor.InvocationContext

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder
import com.ericsson.oss.itpf.sdk.security.accesscontrol.SecurityViolationException

class NscsSecurityViolationHandledInterceptorTest extends CdiSpecification {

    String result = "done"
    Method method = GroovyMock(Method)

    @ObjectUnderTest
    NscsSecurityViolationHandledInterceptor nscsSecurityViolationHandledInterceptor

    SystemRecorder systemRecorder = Mock(SystemRecorder)

    def setup() {
        nscsSecurityViolationHandledInterceptor.systemRecorder = systemRecorder
    }

    def "object under test should not be null" () {
        expect:
        nscsSecurityViolationHandledInterceptor != null
    }

    def "EJB method does not throw exception" () {
        given: "an invocation context"
        InvocationContext invocationContext = GroovyMock(InvocationContext, {
            proceed() >> result
            getMethod() >> method
        })
        when:
        def res = nscsSecurityViolationHandledInterceptor.toSecurityViolationException(invocationContext)
        then:
        notThrown(Exception)
        and:
        res == result
    }

    def "EJB method throws exception" () {
        given: "an invocation context"
        InvocationContext invocationContext = GroovyMock(InvocationContext, {
            proceed() >> { throw new SecurityViolationException() }
            getMethod() >> method
        })
        when:
        nscsSecurityViolationHandledInterceptor.toSecurityViolationException(invocationContext)
        then:
        thrown(Exception)
    }
}
