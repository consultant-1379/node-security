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
import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder

import spock.lang.Unroll

class NbiLoggerInterceptorTest extends CdiSpecification {

    @ObjectUnderTest
    NbiLoggerInterceptor interceptor

    final Method method = this.getClass().getMethods()[0]

    @ImplementationInstance
    InvocationContext invocationContext = [
        proceed : {
            if (scenario == "success") {
                return result
            } else {
                throw new Exception("error message")
            }
        },
        getMethod : {
            return method
        }
    ] as InvocationContext

    @MockedImplementation
    SystemRecorder systemRecorder

    private Object result = new Object()
    private String scenario

    def 'object under test'() {
        expect:
        interceptor != null
    }

    @Unroll
    def 'rest #urlpath method #httpmethod finished with success with CAL disabled'() {
        given:
        scenario = "success"
        interceptor.nbiLogRecorderDto.setUrlPath(urlpath)
        interceptor.nbiLogRecorderDto.setMethod(httpmethod)
        systemRecorder.isCompactAuditEnabled() >> false
        when:
        def Object result = interceptor.restResultLog(invocationContext)
        then:
        noExceptionThrown()
        and:
        result != null
        where:
        urlpath << [
            "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/credentials",
            "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/snmp",
            "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/credentials",
            "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/snmp",
            "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/domains/domainName",
            "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/domains/domainName",
            "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/domains/domainName",
            "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/ldap",
            "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/ldap",
            "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/ldap"
        ]
        httpmethod << [
            "PUT",
            "PUT",
            "POST",
            "POST",
            "POST",
            "PUT",
            "DELETE",
            "POST",
            "PUT",
            "DELETE"
        ]
    }

    @Unroll
    def 'rest #urlpath method #httpmethod finished with error with CAL disabled'() {
        given:
        scenario = "failure"
        interceptor.nbiLogRecorderDto.setUrlPath(urlpath)
        interceptor.nbiLogRecorderDto.setMethod(httpmethod)
        systemRecorder.isCompactAuditEnabled() >> false
        when:
        def Object result = interceptor.restResultLog(invocationContext)
        then:
        thrown(Exception.class)
        where:
        urlpath << [
            "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/credentials",
            "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/snmp",
            "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/credentials",
            "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/snmp",
            "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/domains/domainName",
            "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/domains/domainName",
            "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/domains/domainName",
            "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/ldap",
            "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/ldap",
            "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/ldap"
        ]
        httpmethod << [
            "PUT",
            "PUT",
            "POST",
            "POST",
            "POST",
            "PUT",
            "DELETE",
            "POST",
            "PUT",
            "DELETE"
        ]
    }

    @Unroll
    def 'rest #urlpath method #httpmethod finished with success with CAL enabled'() {
        given:
        scenario = "success"
        interceptor.nbiLogRecorderDto.setUrlPath(urlpath)
        interceptor.nbiLogRecorderDto.setMethod(httpmethod)
        systemRecorder.isCompactAuditEnabled() >> true
        when:
        def Object result = interceptor.restResultLog(invocationContext)
        then:
        noExceptionThrown()
        and:
        result != null
        where:
        urlpath << [
            "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/credentials",
            "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/snmp",
            "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/credentials",
            "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/snmp",
            "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/domains/domainName",
            "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/domains/domainName",
            "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/domains/domainName",
            "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/ldap",
            "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/ldap",
            "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/ldap"
        ]
        httpmethod << [
            "PUT",
            "PUT",
            "POST",
            "POST",
            "POST",
            "PUT",
            "DELETE",
            "POST",
            "PUT",
            "DELETE"
        ]
    }

    @Unroll
    def 'rest #urlpath method #httpmethod finished with error with CAL enabled'() {
        given:
        scenario = "failure"
        interceptor.nbiLogRecorderDto.setUrlPath(urlpath)
        interceptor.nbiLogRecorderDto.setMethod(httpmethod)
        systemRecorder.isCompactAuditEnabled() >> true
        when:
        def Object result = interceptor.restResultLog(invocationContext)
        then:
        thrown(Exception.class)
        where:
        urlpath << [
            "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/credentials",
            "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/snmp",
            "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/credentials",
            "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/snmp",
            "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/domains/domainName",
            "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/domains/domainName",
            "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/domains/domainName",
            "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/ldap",
            "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/ldap",
            "/oss/nscs/nbi/v1/nodes/nodeNameOrFdn/ldap"
        ]
        httpmethod << [
            "PUT",
            "PUT",
            "POST",
            "POST",
            "POST",
            "PUT",
            "DELETE",
            "POST",
            "PUT",
            "DELETE"
        ]
    }

    @Unroll
    def 'rest not yet supported method #httpmethod finished with success with CAL disabled'() {
        given:
        scenario = "success"
        interceptor.nbiLogRecorderDto.setUrlPath("/oss/nscs/nbi/v1/not-yet-supported")
        interceptor.nbiLogRecorderDto.setMethod(httpmethod)
        systemRecorder.isCompactAuditEnabled() >> false
        when:
        def Object result = interceptor.restResultLog(invocationContext)
        then:
        noExceptionThrown()
        and:
        result != null
        where:
        httpmethod << [
            "GET",
            "PUT",
            "POST",
            "DELETE"
        ]
    }
}