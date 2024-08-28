/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.integration.jee.test.setup;

import com.ericsson.nms.security.nscs.integration.jee.test.rest.RestHelper;
import java.io.IOException;
import java.net.URL;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

public class PKIHelper {

	public static void changeToDummyImpl() throws ClientProtocolException, IOException {
                final String updateUrl = RestHelper.getRestHttpUrl("/pki-manager/changeImpl/DummyImpl");
		final HttpGet httpget = new HttpGet(new URL(updateUrl).toExternalForm());
		final HttpClient httpclient = HttpClientBuilder.create().build();
		httpclient.execute(httpget);
	}
}
