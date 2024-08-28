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

import java.nio.charset.StandardCharsets

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.Cookie
import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.core.UriInfo

import org.jboss.resteasy.specimpl.MultivaluedMapImpl
import org.slf4j.Logger

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.services.security.nscs.context.NscsContextService

import spock.lang.Unroll

class RestInputInterceptorTest extends CdiSpecification {

    @ObjectUnderTest
    RestInputInterceptor restInputInterceptor

    @MockedImplementation
    private Logger logger

    @MockedImplementation
    private NscsContextService nscsContextService

    @MockedImplementation
    private ContainerRequestContext requestContext

    @MockedImplementation
    private Cookie cookie

    @MockedImplementation
    private UriInfo uriInfo

    def 'object under test'() {
        expect:
        restInputInterceptor != null
    }

    @Unroll
    def 'filter rest without path params and payload with user ID #userid in the context'() {
        given:
        MultivaluedMap<String, String> headers = new MultivaluedMapImpl<>()
        headers.add("X-Forwarded-For", "ip")
        headers.add("X-Tor-UserID", "username")
        requestContext.getHeaders() >> headers
        requestContext.getMethod() >> "POST"
        cookie.toString() >> "cookie"
        Map<String, Cookie> cookies = new HashMap<>()
        cookies.put("iPlanetDirectoryPro", cookie)
        requestContext.getCookies() >> cookies
        URI uri = new URI("https", "enmapache.athtem.eei.ericsson.se", "/node-security/2.0/nodes", null)
        uriInfo.getRequestUri() >> uri
        requestContext.getUriInfo() >> uriInfo
        requestContext.getEntityStream() >> new ByteArrayInputStream('{"nodeNames":["LTE02ERBS00003"]}'.getBytes(StandardCharsets.UTF_8))
        nscsContextService.getUserIdContextValue() >> userid
        when:
        restInputInterceptor.filter(requestContext)
        then:
        count * nscsContextService.setUserIdContextValue("username")
        1 * nscsContextService.setSourceIpAddrContextValue("ip")
        1 * nscsContextService.setSessionIdContextValue("cookie")
        1 * nscsContextService.setRestUrlFileContextValue("/node-security/2.0/nodes")
        1 * nscsContextService.setRestUrlPathContextValue("/node-security/2.0/nodes")
        1 * nscsContextService.setRestMethodContextValue("POST")
        1 * nscsContextService.setRestRequestPayloadContextValue('{"nodeNames":["LTE02ERBS00003"]}')
        where:
        userid << [null, "username"]
        count << [1, 0]
    }

    @Unroll
    def 'filter rest with path params and without payload with user ID #userid in the context'() {
        given:
        MultivaluedMap<String, String> headers = new MultivaluedMapImpl<>()
        headers.add("X-Forwarded-For", "ip")
        headers.add("X-Tor-UserID", "username")
        requestContext.getHeaders() >> headers
        requestContext.getMethod() >> "POST"
        cookie.toString() >> "cookie"
        Map<String, Cookie> cookies = new HashMap<>()
        cookies.put("iPlanetDirectoryPro", cookie)
        requestContext.getCookies() >> cookies
        URI uri = new URI("https", "enmapache.athtem.eei.ericsson.se", "/node-security/credentials/LTE02ERBS00006", null)
        uriInfo.getRequestUri() >> uri
        requestContext.getUriInfo() >> uriInfo
        MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<>()
        pathParams.add("nodelist", "LTE02ERBS00006")
        uriInfo.getPathParameters() >> pathParams
        requestContext.getEntityStream() >> new ByteArrayInputStream(''.getBytes(StandardCharsets.UTF_8))
        nscsContextService.getUserIdContextValue() >> userid
        when:
        restInputInterceptor.filter(requestContext)
        then:
        count * nscsContextService.setUserIdContextValue("username")
        1 * nscsContextService.setSourceIpAddrContextValue("ip")
        1 * nscsContextService.setSessionIdContextValue("cookie")
        1 * nscsContextService.setRestUrlFileContextValue("/node-security/credentials/LTE02ERBS00006")
        1 * nscsContextService.setRestUrlPathContextValue("/node-security/credentials/nodelist")
        1 * nscsContextService.setRestMethodContextValue("POST")
        1 * nscsContextService.setRestRequestPayloadContextValue('')
        where:
        userid << [null, "username"]
        count << [1, 0]
    }

    def 'filter rest without IP source, user ID in the context'() {
        given:
        MultivaluedMap<String, String> headers = new MultivaluedMapImpl<>()
        requestContext.getHeaders() >> headers
        requestContext.getMethod() >> "POST"
        cookie.toString() >> "cookie"
        Map<String, Cookie> cookies = new HashMap<>()
        cookies.put("iPlanetDirectoryPro", cookie)
        requestContext.getCookies() >> cookies
        URI uri = new URI("https", "enmapache.athtem.eei.ericsson.se", "/node-security/2.0/nodes", null)
        uriInfo.getRequestUri() >> uri
        requestContext.getUriInfo() >> uriInfo
        requestContext.getEntityStream() >> new ByteArrayInputStream('{"nodeNames":["LTE02ERBS00003"]}'.getBytes(StandardCharsets.UTF_8))
        nscsContextService.getUserIdContextValue() >> null
        when:
        restInputInterceptor.filter(requestContext)
        then:
        1 * nscsContextService.setUserIdContextValue("")
        1 * nscsContextService.setSourceIpAddrContextValue("")
        1 * nscsContextService.setSessionIdContextValue("cookie")
        1 * nscsContextService.setRestUrlFileContextValue("/node-security/2.0/nodes")
        1 * nscsContextService.setRestUrlPathContextValue("/node-security/2.0/nodes")
        1 * nscsContextService.setRestMethodContextValue("POST")
        1 * nscsContextService.setRestRequestPayloadContextValue('{"nodeNames":["LTE02ERBS00003"]}')
    }

    def 'filter rest without cookies'() {
        given:
        MultivaluedMap<String, String> headers = new MultivaluedMapImpl<>()
        headers.add("X-Forwarded-For", "ip")
        headers.add("X-Tor-UserID", "username")
        requestContext.getHeaders() >> headers
        requestContext.getMethod() >> "POST"
        requestContext.getCookies() >> null
        URI uri = new URI("https", "enmapache.athtem.eei.ericsson.se", "/node-security/2.0/nodes", null)
        uriInfo.getRequestUri() >> uri
        requestContext.getUriInfo() >> uriInfo
        requestContext.getEntityStream() >> new ByteArrayInputStream('{"nodeNames":["LTE02ERBS00003"]}'.getBytes(StandardCharsets.UTF_8))
        nscsContextService.getUserIdContextValue() >> "username"
        when:
        restInputInterceptor.filter(requestContext)
        then:
        0 * nscsContextService.setUserIdContextValue("username")
        1 * nscsContextService.setSourceIpAddrContextValue("ip")
        1 * nscsContextService.setSessionIdContextValue(null)
        1 * nscsContextService.setRestUrlFileContextValue("/node-security/2.0/nodes")
        1 * nscsContextService.setRestUrlPathContextValue("/node-security/2.0/nodes")
        1 * nscsContextService.setRestMethodContextValue("POST")
        1 * nscsContextService.setRestRequestPayloadContextValue('{"nodeNames":["LTE02ERBS00003"]}')
    }

    def 'filter rest with null cookie'() {
        given:
        MultivaluedMap<String, String> headers = new MultivaluedMapImpl<>()
        headers.add("X-Forwarded-For", "ip")
        headers.add("X-Tor-UserID", "username")
        requestContext.getHeaders() >> headers
        requestContext.getMethod() >> "POST"
        Map<String, Cookie> cookies = new HashMap<>()
        cookies.put("iPlanetDirectoryPro", null)
        requestContext.getCookies() >> cookies
        URI uri = new URI("https", "enmapache.athtem.eei.ericsson.se", "/node-security/2.0/nodes", null)
        uriInfo.getRequestUri() >> uri
        requestContext.getUriInfo() >> uriInfo
        requestContext.getEntityStream() >> new ByteArrayInputStream('{"nodeNames":["LTE02ERBS00003"]}'.getBytes(StandardCharsets.UTF_8))
        nscsContextService.getUserIdContextValue() >> "username"
        when:
        restInputInterceptor.filter(requestContext)
        then:
        0 * nscsContextService.setUserIdContextValue("username")
        1 * nscsContextService.setSourceIpAddrContextValue("ip")
        1 * nscsContextService.setSessionIdContextValue(null)
        1 * nscsContextService.setRestUrlFileContextValue("/node-security/2.0/nodes")
        1 * nscsContextService.setRestUrlPathContextValue("/node-security/2.0/nodes")
        1 * nscsContextService.setRestMethodContextValue("POST")
        1 * nscsContextService.setRestRequestPayloadContextValue('{"nodeNames":["LTE02ERBS00003"]}')
    }
}
