/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2023
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.interceptor

import javax.interceptor.InvocationContext

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.logger.NscsLogger
import com.ericsson.oss.services.security.nscs.context.NscsContextService

class RestLoggerInterceptorTest extends CdiSpecification {

    @ObjectUnderTest
    RestLoggerInterceptor restLoggerInterceptor

    @MockedImplementation
    InvocationContext invocationContext

    @MockedImplementation
    NscsLogger nscsLogger

    @MockedImplementation
    NscsContextService nscsContextService

    def 'object under test'() {
        expect:
        restLoggerInterceptor != null
    }

    def 'rest finished with success'() {
        given:
        invocationContext.proceed() >> new Object()
        when:
        def Object result = restLoggerInterceptor.restFinished(invocationContext)
        then:
        noExceptionThrown()
        and:
        result != null
        and:
        1 * restLoggerInterceptor.nscsLogger.restFinishedWithSuccess()
    }

    def 'rest finished with exception no error message'() {
        given:
        invocationContext.proceed() >> {throw new Exception()}
        when:
        def Object result = restLoggerInterceptor.restFinished(invocationContext)
        then:
        1 * restLoggerInterceptor.nscsContextService.setErrorDetailContextValue("Exception [java.lang.Exception] occurred.")
        1 * restLoggerInterceptor.nscsLogger.restFinishedWithError()
        and:
        thrown(Exception.class)
    }

    def 'rest finished with exception with error message'() {
        given:
        invocationContext.proceed() >> {throw new Exception("error message")}
        when:
        def Object result = restLoggerInterceptor.restFinished(invocationContext)
        then:
        1* restLoggerInterceptor.nscsContextService.setErrorDetailContextValue("error message")
        1 * restLoggerInterceptor.nscsLogger.restFinishedWithError()
        and:
        thrown(Exception.class)
    }
}
