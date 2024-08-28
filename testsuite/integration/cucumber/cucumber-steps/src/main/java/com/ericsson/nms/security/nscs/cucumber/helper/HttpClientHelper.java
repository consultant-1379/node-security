/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.cucumber.helper;


import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class HttpClientHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientHelper.class);
    private static final String CONTAINER_ADDRESS_PROPERTY = "container.ip";

    public HttpResponse invokeRest(final String path) {
        LOGGER.info("-----------invokeRest starts--------------");
        final String url = String.format("http://%s:8080/node-security/%s", getLocalHostAddr(), path);
        HttpGet httpget = null;
        try {
            httpget = new HttpGet(new URL(url).toExternalForm());
        } catch (final MalformedURLException e) {
            LOGGER.error(e.getMessage());
        }
        final HttpClient httpclient = HttpClientBuilder.create().build();
        HttpResponse response = null;
        try {
            response = httpclient.execute(httpget);
        } catch (final ClientProtocolException e) {
            LOGGER.error("ClientProtocolException {}", e.getMessage());
        } catch (final IOException e) {
            LOGGER.error("IOException {}", e.getMessage());
        }
        return response;
    }

    public HttpResponse invokeJsonPostRest(final String path, final String jsonPostData, final Map<String, String> header) {
        LOGGER.info("-----------invokeJsonPostRest starts--------------");
        final String url = String.format("http://%s:8080/node-security/%s", getLocalHostAddr(), path);
        final HttpPost post = new HttpPost(url);
        if (header != null) {
            for (final Map.Entry<String, String> e : header.entrySet()) {
                post.addHeader(e.getKey(), e.getValue());
            }

        }
        post.addHeader("content-type", "application/json");
        final HttpClient httpclient = HttpClientBuilder.create().build();
        StringEntity params = null;
        try {
            params = new StringEntity(jsonPostData);
        } catch (final UnsupportedEncodingException e) {
            LOGGER.error("Exception while building StringEntity, {}", e.getClass().getName());
        }
        post.setEntity(params);
        HttpResponse response = null;
        try {
            response = httpclient.execute(post);
        } catch (final ClientProtocolException e) {
            LOGGER.error("Exception ClientProtocolException while executing http request");
        } catch (final IOException e) {
            LOGGER.error("Exception IOException while executing http request");
        }
        return response;
    }

    private String getLocalHostAddr() {
        String localHostAddress = System.getProperty(CONTAINER_ADDRESS_PROPERTY);
        if ((localHostAddress == null) || localHostAddress.isEmpty()) {
            localHostAddress = "localhost";
        }
        return localHostAddress;
    }
}