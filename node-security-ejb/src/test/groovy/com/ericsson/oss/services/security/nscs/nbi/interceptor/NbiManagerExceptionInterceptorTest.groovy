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
package com.ericsson.oss.services.security.nscs.nbi.interceptor

import java.lang.reflect.Method

import javax.interceptor.InvocationContext

import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.exception.NscsBadRequestException
import com.ericsson.nms.security.nscs.api.exception.NscsSecurityViolationException
import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException
import com.ericsson.oss.itpf.sdk.security.accesscontrol.SecurityViolationException

class NbiManagerExceptionInterceptorTest extends CdiSpecification {

    @ObjectUnderTest
    NbiManagerExceptionInterceptor interceptor

    final Method method = this.getClass().getMethods()[0]

    @ImplementationInstance
    InvocationContext invocationContext = [
        proceed : {
            if (scenario == "success") {
                return result
            } else if (scenario == "security-violation") {
                throw new SecurityViolationException(scenario)
            } else if (scenario == "nscs-service") {
                throw new NscsBadRequestException(scenario)
            } else {
                throw new Exception(scenario)
            }
        }
    ] as InvocationContext

    private Object result = new Object()
    private String scenario

    def 'object under test'() {
        expect:
        interceptor != null
    }

    def 'manager invoked with success'() {
        given:
        scenario = "success"
        when:
        def Object result = interceptor.logException(invocationContext)
        then:
        noExceptionThrown()
        and:
        result != null
    }

    def 'manager throws SecurityViolationException'() {
        given:
        scenario = "security-violation"
        when:
        def Object result = interceptor.logException(invocationContext)
        then:
        thrown(NscsSecurityViolationException.class)
    }

    def 'manager throws NscsServiceException'() {
        given:
        scenario = "nscs-service"
        when:
        def Object result = interceptor.logException(invocationContext)
        then:
        thrown(NscsBadRequestException.class)
    }

    def 'manager throws Exception'() {
        given:
        scenario = "exception"
        when:
        def Object result = interceptor.logException(invocationContext)
        then:
        thrown(UnexpectedErrorException.class)
    }
}
