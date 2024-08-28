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

import java.nio.charset.StandardCharsets

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.Cookie
import javax.ws.rs.core.MultivaluedHashMap
import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.core.UriInfo

import org.slf4j.Logger

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.services.security.nscs.nbi.logger.NbiLogRecorderDto

class NbiInputInterceptorTest extends CdiSpecification {

    @ObjectUnderTest
    NbiInputInterceptor interceptor

    @MockedImplementation
    Logger logger

    @MockedImplementation
    NbiLogRecorderDto restLogRecorderDto

    @MockedImplementation
    ContainerRequestContext requestContext

    @MockedImplementation
    Cookie cookie

    @MockedImplementation
    UriInfo uriInfo

    def 'object under test'() {
        expect:
        interceptor != null
    }

    def 'filter rest without path params and payload'() {
        given:
        MultivaluedMap<String, String> headers = new MultivaluedHashMap()
        headers.add("X-Forwarded-For", "ip")
        headers.add("X-Tor-UserID", "username")
        requestContext.getHeaders() >> headers
        requestContext.getMethod() >> "POST"
        cookie.toString() >> "cookie"
        Map<String, Cookie> cookies = new HashMap<>()
        cookies.put("iPlanetDirectoryPro", cookie)
        requestContext.getCookies() >> cookies
        URI uri = new URI("https", "enmapache.athtem.eei.ericsson.se", "/oss/nscs/nbi/v1/nodes", null)
        uriInfo.getRequestUri() >> uri
        requestContext.getUriInfo() >> uriInfo
        requestContext.getEntityStream() >> new ByteArrayInputStream('{"subResources":["domains"]}'.getBytes(StandardCharsets.UTF_8))
        when:
        interceptor.filter(requestContext)
        then:
        1 * interceptor.restLogRecorderDto.setUserId("username")
        1 * interceptor.restLogRecorderDto.setSourceIpAddr("ip")
        1 * interceptor.restLogRecorderDto.setSessionId("cookie")
        1 * interceptor.restLogRecorderDto.setMethod("POST")
        1 * interceptor.restLogRecorderDto.setUrlFile("/oss/nscs/nbi/v1/nodes")
        1 * interceptor.restLogRecorderDto.setUrlPath("/oss/nscs/nbi/v1/nodes")
        1 * interceptor.restLogRecorderDto.setRequestPayload('{"subResources":["domains"]}')
    }

    def 'filter rest with path params and without payload'() {
        given:
        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>()
        headers.add("X-Forwarded-For", "ip")
        headers.add("X-Tor-UserID", "username")
        requestContext.getHeaders() >> headers
        requestContext.getMethod() >> "POST"
        cookie.toString() >> "cookie"
        Map<String, Cookie> cookies = new HashMap<>()
        cookies.put("iPlanetDirectoryPro", cookie)
        requestContext.getCookies() >> cookies
        URI uri = new URI("https", "enmapache.athtem.eei.ericsson.se", "/oss/nscs/nbi/v1/nodes/LTE02ERBS00006", null)
        uriInfo.getRequestUri() >> uri
        requestContext.getUriInfo() >> uriInfo
        MultivaluedMap<String, String> pathParams = new MultivaluedHashMap<>()
        pathParams.add("nodeNameOrFdn", "LTE02ERBS00006")
        uriInfo.getPathParameters() >> pathParams
        requestContext.getEntityStream() >> new ByteArrayInputStream(''.getBytes(StandardCharsets.UTF_8))
        when:
        interceptor.filter(requestContext)
        then:
        1 * interceptor.restLogRecorderDto.setUserId("username")
        1 * interceptor.restLogRecorderDto.setSourceIpAddr("ip")
        1 * interceptor.restLogRecorderDto.setSessionId("cookie")
        1 * interceptor.restLogRecorderDto.setMethod("POST")
        1 * interceptor.restLogRecorderDto.setUrlFile("/oss/nscs/nbi/v1/nodes/LTE02ERBS00006")
        1 * interceptor.restLogRecorderDto.setUrlPath("/oss/nscs/nbi/v1/nodes/nodeNameOrFdn")
        1 * interceptor.restLogRecorderDto.setRequestPayload('')
    }

    def 'filter rest with path and query params and without payload'() {
        given:
        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>()
        headers.add("X-Forwarded-For", "ip")
        headers.add("X-Tor-UserID", "username")
        requestContext.getHeaders() >> headers
        requestContext.getMethod() >> "POST"
        cookie.toString() >> "cookie"
        Map<String, Cookie> cookies = new HashMap<>()
        cookies.put("iPlanetDirectoryPro", cookie)
        requestContext.getCookies() >> cookies
        URI uri = new URI("https", "enmapache.athtem.eei.ericsson.se", "/oss/nscs/nbi/v1/nodes/LTE02ERBS00006?ipFamily=INET&domain=OAM", null)
        uriInfo.getRequestUri() >> uri
        requestContext.getUriInfo() >> uriInfo
        MultivaluedMap<String, String> pathParams = new MultivaluedHashMap<>()
        pathParams.add("nodeNameOrFdn", "LTE02ERBS00006")
        uriInfo.getPathParameters() >> pathParams
        requestContext.getEntityStream() >> new ByteArrayInputStream(''.getBytes(StandardCharsets.UTF_8))
        when:
        interceptor.filter(requestContext)
        then:
        1 * interceptor.restLogRecorderDto.setUserId("username")
        1 * interceptor.restLogRecorderDto.setSourceIpAddr("ip")
        1 * interceptor.restLogRecorderDto.setSessionId("cookie")
        1 * interceptor.restLogRecorderDto.setMethod("POST")
        1 * interceptor.restLogRecorderDto.setUrlFile("/oss/nscs/nbi/v1/nodes/LTE02ERBS00006%3FipFamily=INET&domain=OAM")
        1 * interceptor.restLogRecorderDto.setUrlPath("/oss/nscs/nbi/v1/nodes/nodeNameOrFdn%3FipFamily=INET&domain=OAM")
        1 * interceptor.restLogRecorderDto.setRequestPayload('')
    }

    def 'filter rest without IP source, user ID in the context'() {
        given:
        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>()
        requestContext.getHeaders() >> headers
        requestContext.getMethod() >> "POST"
        cookie.toString() >> "cookie"
        Map<String, Cookie> cookies = new HashMap<>()
        cookies.put("iPlanetDirectoryPro", cookie)
        requestContext.getCookies() >> cookies
        URI uri = new URI("https", "enmapache.athtem.eei.ericsson.se", "/oss/nscs/nbi/v1/nodes", null)
        uriInfo.getRequestUri() >> uri
        requestContext.getUriInfo() >> uriInfo
        requestContext.getEntityStream() >> new ByteArrayInputStream('{"subResources":["domains"]}'.getBytes(StandardCharsets.UTF_8))
        when:
        interceptor.filter(requestContext)
        then:
        1 * interceptor.restLogRecorderDto.setUserId("")
        1 * interceptor.restLogRecorderDto.setSourceIpAddr("")
        1 * interceptor.restLogRecorderDto.setSessionId("cookie")
        1 * interceptor.restLogRecorderDto.setMethod("POST")
        1 * interceptor.restLogRecorderDto.setUrlFile("/oss/nscs/nbi/v1/nodes")
        1 * interceptor.restLogRecorderDto.setUrlPath("/oss/nscs/nbi/v1/nodes")
        1 * interceptor.restLogRecorderDto.setRequestPayload('{"subResources":["domains"]}')
    }

    def 'filter rest without cookies'() {
        given:
        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>()
        headers.add("X-Forwarded-For", "ip")
        headers.add("X-Tor-UserID", "username")
        requestContext.getHeaders() >> headers
        requestContext.getMethod() >> "POST"
        requestContext.getCookies() >> null
        URI uri = new URI("https", "enmapache.athtem.eei.ericsson.se", "/oss/nscs/nbi/v1/nodes", null)
        uriInfo.getRequestUri() >> uri
        requestContext.getUriInfo() >> uriInfo
        requestContext.getEntityStream() >> new ByteArrayInputStream('{"subResources":["domains"]}'.getBytes(StandardCharsets.UTF_8))
        when:
        interceptor.filter(requestContext)
        then:
        1 * interceptor.restLogRecorderDto.setUserId("username")
        1 * interceptor.restLogRecorderDto.setSourceIpAddr("ip")
        1 * interceptor.restLogRecorderDto.setSessionId(null)
        1 * interceptor.restLogRecorderDto.setMethod("POST")
        1 * interceptor.restLogRecorderDto.setUrlFile("/oss/nscs/nbi/v1/nodes")
        1 * interceptor.restLogRecorderDto.setUrlPath("/oss/nscs/nbi/v1/nodes")
        1 * interceptor.restLogRecorderDto.setRequestPayload('{"subResources":["domains"]}')
    }

    def 'filter rest with null cookie'() {
        given:
        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>()
        headers.add("X-Forwarded-For", "ip")
        headers.add("X-Tor-UserID", "username")
        requestContext.getHeaders() >> headers
        requestContext.getMethod() >> "POST"
        Map<String, Cookie> cookies = new HashMap<>()
        cookies.put("iPlanetDirectoryPro", null)
        requestContext.getCookies() >> cookies
        URI uri = new URI("https", "enmapache.athtem.eei.ericsson.se", "/oss/nscs/nbi/v1/nodes", null)
        uriInfo.getRequestUri() >> uri
        requestContext.getUriInfo() >> uriInfo
        requestContext.getEntityStream() >> new ByteArrayInputStream('{"subResources":["domains"]}'.getBytes(StandardCharsets.UTF_8))
        when:
        interceptor.filter(requestContext)
        then:
        1 * interceptor.restLogRecorderDto.setUserId("username")
        1 * interceptor.restLogRecorderDto.setSourceIpAddr("ip")
        1 * interceptor.restLogRecorderDto.setSessionId(null)
        1 * interceptor.restLogRecorderDto.setMethod("POST")
        1 * interceptor.restLogRecorderDto.setUrlFile("/oss/nscs/nbi/v1/nodes")
        1 * interceptor.restLogRecorderDto.setUrlPath("/oss/nscs/nbi/v1/nodes")
        1 * interceptor.restLogRecorderDto.setRequestPayload('{"subResources":["domains"]}')
    }
}
