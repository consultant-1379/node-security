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
package com.ericsson.oss.services.security.nscs.interceptor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;

import com.ericsson.oss.services.security.nscs.context.NscsContextService;

@Provider
public class RestInputInterceptor implements ContainerRequestFilter {
    private static final String FORWARDED_HEADER = "X-Forwarded-For";
    private static final String SSO_COOKIE_NAME = "iPlanetDirectoryPro";
    private static final String USER_NAME = "X-Tor-UserID";

    @Inject
    private Logger logger;

    @Inject
    private NscsContextService nscsContextService;

    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {
        final String sessionId = getSessionIdFromRequestContext(requestContext);
        final MultivaluedMap<String, String> headers = requestContext.getHeaders();
        final String sourceIpAddr = headers.get(FORWARDED_HEADER) != null ? headers.get(FORWARDED_HEADER).get(0) : "";
        logger.debug("REST_CAL sourceIpAddr [{}]", sourceIpAddr);
        final String userId = headers.get(USER_NAME) != null ? headers.get(USER_NAME).get(0) : "";
        logger.debug("REST_CAL userId from request context [{}]", userId);
        final String restMethod = requestContext.getMethod();
        logger.debug("REST_CAL restMethod [{}]", restMethod);
        final UriInfo uriInfo = requestContext.getUriInfo();
        // the absolute URL object, the address has all the parts required to reach the desired resource
        final URL absoluteUrl = uriInfo.getRequestUri().toURL();
        // the file name of the resource (whatever follows after the hostname in a URL).
        // It can include both path and query parameters or just a file name
        final String resourceFile = absoluteUrl.getFile();
        logger.debug("REST_CAL resourceFilename [{}]", resourceFile);
        // the resource path
        final String resourcePath = getResourcePath(uriInfo, absoluteUrl);
        logger.debug("REST_CAL resourcePath [{}]", resourcePath);
        // request payload
        final String requestPayload = getRequestPayload(requestContext);
        logger.debug("REST_CAL requestPayload [{}]", requestPayload);

        if (nscsContextService.getUserIdContextValue() == null) {
            logger.warn("REST_CAL setting not yet set userId [{}] in the context!", userId);
            nscsContextService.setUserIdContextValue(userId);
        } else {
            logger.debug("REST_CAL already set userId in context [{}]", nscsContextService.getUserIdContextValue());
        }
        nscsContextService.setSourceIpAddrContextValue(sourceIpAddr);
        nscsContextService.setSessionIdContextValue(sessionId);
        nscsContextService.setRestUrlFileContextValue(resourceFile);
        nscsContextService.setRestUrlPathContextValue(resourcePath);
        nscsContextService.setRestMethodContextValue(restMethod);
        nscsContextService.setRestRequestPayloadContextValue(requestPayload);
    }

    /**
     * Get the request payload from the request context.
     * 
     * Read the entity and set the entity back to the request, to avoid accidentally consuming it in the filter.
     * 
     * @param requestContext
     * @return
     */
    private String getRequestPayload(final ContainerRequestContext requestContext) {
        final String restRequestPayload = new BufferedReader(new InputStreamReader(requestContext.getEntityStream(), StandardCharsets.UTF_8)).lines()
                .collect(Collectors.joining("\n"));
        requestContext.setEntityStream(new ByteArrayInputStream(restRequestPayload.getBytes(StandardCharsets.UTF_8)));
        return restRequestPayload;
    }

    /**
     * Get the resource path. In case of path parameters, the actual values are replaced with the keys.
     * 
     * @param uriInfo
     *            the URI info.
     * @param absoluteUrl
     *            the absolute URL.
     * @return the resource path.
     */
    private String getResourcePath(final UriInfo uriInfo, final URL absoluteUrl) {
        String resourcePath = absoluteUrl.getPath();
        logger.debug("REST_CAL initial resourcePath [{}]", resourcePath);
        final MultivaluedMap<String, String> pathParams = uriInfo.getPathParameters();
        if (pathParams != null) {
            for (final Entry<String, List<String>> param : pathParams.entrySet()) {
                resourcePath = resourcePath.replace(param.getValue().get(0), param.getKey());
            }
        }
        return resourcePath;
    }

    /**
     * Get session ID from the given request context.
     * 
     * @param requestContext
     *            the request context.
     * @return the session ID.
     */
    private String getSessionIdFromRequestContext(final ContainerRequestContext requestContext) {
        String sessionId = null;
        Cookie sessionCookie = null;
        final Map<String, Cookie> cookies = requestContext.getCookies();
        if (cookies != null) {
            sessionCookie = cookies.get(SSO_COOKIE_NAME);
            if (sessionCookie != null) {
                sessionId = sessionCookie.toString();
            }
        }
        return sessionId;
    }

}
