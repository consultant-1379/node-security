package com.ericsson.nms.security.nscs.interceptor

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.logger.NscsLogger
import com.ericsson.oss.services.security.nscs.context.NscsContextService

import javax.interceptor.InvocationContext

class EjbLoggerInterceptorTest extends CdiSpecification {

    @ObjectUnderTest
    EjbLoggerInterceptor ejbLoggerInterceptor

    @MockedImplementation
    NscsLogger nscsLogger

    @MockedImplementation
    NscsContextService nscsContextService


    def 'object under test'() {
        expect:
        ejbLoggerInterceptor != null
    }

    def 'ejb invocation started and finished with success'() {
        given: "Mocked invocation context"
        Closure closure = this.&toString
          def InvocationContext ctx = Mock(InvocationContext) {
              getMethod() >> closure.owner.class.getMethod('toString')
          }
        ctx.proceed() >> new Object()
        when:
        def Object result = ejbLoggerInterceptor.remoteEjbLogger(ctx)
        then:
        1 * ejbLoggerInterceptor.nscsLogger.remoteEjbStarted()
        1 * ejbLoggerInterceptor.nscsLogger.remoteEjbFinishedWithSuccess()
        and:
        result != null
    }

        def 'ejb invocation started and finished with exception no error message'() {
        given:
        Closure closure = this.&toString
        def InvocationContext ctx = Mock(InvocationContext) {
            getMethod() >> closure.owner.class.getMethod('toString')
        }
        ctx.proceed() >> {throw new Exception()}
        when:
        def Object result = ejbLoggerInterceptor.remoteEjbLogger(ctx)
        then:
        1 * ejbLoggerInterceptor.nscsLogger.remoteEjbStarted()
        1 * ejbLoggerInterceptor.nscsContextService.setErrorDetailContextValue("Exception [java.lang.Exception] occurred.")
        1 * ejbLoggerInterceptor.nscsLogger.remoteEjbFinishedWithError()
        and:
        thrown(Exception.class)
    }

        def 'rest finished with exception with error message'() {
        given:
        Closure closure = this.&toString
        def InvocationContext ctx = Mock(InvocationContext) {
            getMethod() >> closure.owner.class.getMethod('toString')
        }
        ctx.proceed() >> {throw new Exception("error message")}
        when:
        def Object result = ejbLoggerInterceptor.remoteEjbLogger(ctx)
        then:
        1 * ejbLoggerInterceptor.nscsLogger.remoteEjbStarted()
        1 * ejbLoggerInterceptor.nscsContextService.setErrorDetailContextValue("error message")
        1 * ejbLoggerInterceptor.nscsLogger.remoteEjbFinishedWithError()
        and:
        thrown(Exception.class)
    }
}
