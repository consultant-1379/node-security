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
package com.ericsson.nms.security.nscs.integration.jee.test.command;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.integration.jee.test.rest.RestHelper;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.AccessControlHelper;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.FileUtility;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.ResponseDtoReader;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityDataSetup;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityDataSetup.IpAddressVersion;
import com.ericsson.oss.itpf.sdk.context.ContextService;
import com.ericsson.oss.services.scriptengine.spi.CommandHandler;
import com.ericsson.oss.services.scriptengine.spi.dtos.Command;
import com.ericsson.oss.services.scriptengine.spi.dtos.CommandResponseDto;

public class JobIdManagementTest implements JobIdManagementTests {

    private static final String CERT_ISSUE = "certificate issue -ct OAM -xf file:file-cert.xml";
    private static final String NODE_NAME = "LTE01ERBS00001";
    private static final String MESSAGE_NO_JOB_FOUND = "No job found";
    private static final String NODE_NOT_SYNC = "The node specified is not synchronized";
    public static final String COMMAND_SECADM = "secadm";
    public static final String COMMAND_JOB_GET = "job get";
    public static final String COMMAND_PARAMENTER_ALL = "--all";
    private static final String STATE_PENDING = "PENDING";

    @Inject
    private ContextService ctxService;

    @Inject
    Logger logger;

    @Inject
    CommandHandler commandHandler;

    @Inject
    NodeSecurityDataSetup dataSetup;

    @Inject
    ResponseDtoReader responseDtoReader;

    @Inject
    private FileUtility fileUtility;

    @Override
    public void certIssueRest_sync() throws Exception {

        performJobCacheResetRest();

        AccessControlHelper.setupUser("toruser1");
        try {
            dataSetup.deleteAllNodes();
            dataSetup.createNode(NODE_NAME, "SYNCHRONIZED", SecurityLevel.LEVEL_2, IpAddressVersion.IPv4);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ctxService.setContextValue("X-Tor-UserID", "Administrator");

        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("file:", fileUtility.readResourceFile("file-cert.xml"));

        final Command command = new Command("secadm", CERT_ISSUE, properties);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);
        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        String stringResponse = rowsAsListOfStrings.get(0);

        Pattern pattern = Pattern.compile("([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})");
        Matcher matcher = pattern.matcher(stringResponse);
        if (matcher.find()) {

            String stringUUID = matcher.group(1);

            //"jobId":"297fc1d5-7f5c-4e33-8df9-3ea531998497","userId":"Administrator","commandId":"CERTIFICATE_ISSUE","startDate":1474364962636,"endDate":null,"globalStatus":"RUNNING","statusMap":{"82a49ba2-7f17-11e6-84ac-080027d5f202":{"wfId":"82a49ba2-7f17-11e6-84ac-080027d5f202","nodeName":"LTE01ERBS00001-NE","status":"RUNNING","message":null,"startDate":1474364964535,"endDate":null}}}]
            HttpResponse response = performJobCacheGetRest(stringUUID);
            stringResponse = EntityUtils.toString(response.getEntity());
            logger.info("[{}.{}]: {} ", getClass(), "testJobIdManagement: ", stringResponse);
            assertTrue(stringResponse.contains("\"globalStatus\":\"" + STATE_PENDING + "\""));
            //trying to test the workflow end
            logger.info("[{}.{}]: {} ", getClass(), "testJobIdManagement", "waiting for next scheduling slot");

        }
        performJobCacheResetRest();
        try {
            dataSetup.deleteAllNodes();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void certIssueRest_notSync() throws Exception {

        performJobCacheResetRest();

        AccessControlHelper.setupUser("toruser1");
        try {
            dataSetup.deleteAllNodes();
            dataSetup.createNode(NODE_NAME, "UNSYNCHRONIZED", SecurityLevel.LEVEL_2, IpAddressVersion.IPv4);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ctxService.setContextValue("X-Tor-UserID", "Administrator");

        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("file:", fileUtility.readResourceFile("file-cert.xml"));

        Command command = new Command("secadm", CERT_ISSUE, properties);
        CommandResponseDto commandResponseDto = commandHandler.execute(command);
        List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        String stringResponse = rowsAsListOfStrings.get(0);
        assertTrue(responseDtoReader.messageIsContainedInList(NODE_NOT_SYNC, rowsAsListOfStrings));

        //now launch the --all comamnd and get no job found message
        command = new Command(COMMAND_SECADM, COMMAND_JOB_GET + " " + COMMAND_PARAMENTER_ALL);
        commandResponseDto = commandHandler.execute(command);
        rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        assertTrue(responseDtoReader.messageIsContainedInList(MESSAGE_NO_JOB_FOUND, rowsAsListOfStrings));

        try {
            dataSetup.deleteAllNodes();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Delete the job id cache
     */
    private void performJobCacheResetRest() {
        final Map<String, String> header = new HashMap<String, String>() {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            {
                put("X-Tor-UserID", "Administrator");
            }
        };

        HttpResponse response = null;
        logger.info("-----------performJobCacheResetRest starts--------------");
        final String url = RestHelper.getRestHttpUrl(RestHelper.NODE_SECURITY_JOB_PATH);
        final HttpDelete delete = new HttpDelete(url);
        if (header != null) {
            for (final Entry<String, String> e : header.entrySet()) {
                delete.addHeader(e.getKey(), e.getValue());
            }

        }
        //post.addHeader("content-type", "application/json");
        final HttpClient httpclient = HttpClientBuilder.create().build();
        try {
            response = httpclient.execute(delete);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            logger.error("Exception ClientProtocolException while executing http request [{}]", delete.getURI().toString());
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Exception IOException while executing http request [{}]", delete.getURI().toString());
        }
    }

    private HttpResponse performJobCacheGetRest(String path) {

        if (path == null)
            path = "";

        final Map<String, String> header = new HashMap<String, String>() {

            private static final long serialVersionUID = 1L;

            {
                put("X-Tor-UserID", "Administrator");
            }
        };

        HttpResponse response = null;
        logger.info("-----------performJobCacheGetRest starts--------------");
        final String url = RestHelper.getRestHttpUrl(RestHelper.NODE_SECURITY_JOB_PATH) + "/" + path;
        final HttpGet get = new HttpGet(url);
        if (header != null) {
            for (final Entry<String, String> e : header.entrySet()) {
                get.addHeader(e.getKey(), e.getValue());
            }

        }
        final HttpClient httpclient = HttpClientBuilder.create().build();

        try {
            response = httpclient.execute(get);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            logger.error("Exception ClientProtocolException while executing http request [{}]", get.getURI().toString());
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Exception IOException while executing http request [{}]", get.getURI().toString());
        }
        return response;
    }

    private HttpResponse performJobCacheCreateRest() {
        final Map<String, String> header = new HashMap<String, String>() {

            private static final long serialVersionUID = 1L;

            {
                put("X-Tor-UserID", "Administrator");
            }
        };

        HttpResponse response = null;
        logger.info("-----------performJobCacheCreateRest starts--------------");
        final String url = RestHelper.getRestHttpUrl(RestHelper.NODE_SECURITY_JOB_PATH);
        final HttpPost post = new HttpPost(url);
        if (header != null) {
            for (final Entry<String, String> e : header.entrySet()) {
                post.addHeader(e.getKey(), e.getValue());
            }

        }
        final HttpClient httpclient = HttpClientBuilder.create().build();

        try {
            response = httpclient.execute(post);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            logger.error("Exception ClientProtocolException while executing http request [{}]", post.getURI().toString());
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Exception IOException while executing http request [{}]", post.getURI().toString());
        }
        return response;
    }

}
