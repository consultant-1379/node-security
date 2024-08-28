/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.integration.jee.test.command;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.integration.jee.test.rest.RestHelper;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.ResponseDtoReader;
import com.ericsson.oss.services.scriptengine.spi.CommandHandler;
import com.ericsson.oss.services.scriptengine.spi.dtos.Command;
import com.ericsson.oss.services.scriptengine.spi.dtos.CommandResponseDto;

public class GetJobCommadTest implements GetJobCommadTests {

    private static final String MESSAGE_NO_JOB_FOUND = "No job found";
    private static final String MESSAGE_INVALID_JOB_IDENTIFIER = "Invalid job identifier";

    private static final String REST_CREATE_JOB_ID_MESSAGE = "Created job";
    private static final String REST_COMMAND_EXECUTED_SUCCESSFULLY = "Command Executed Successfully";
    private static final String STATE_RUNNING = "RUNNING";
    private static final String STATE_COMPLETED = "COMPLETED";
    private static final String STATE_PENDING = "PENDING";

    private static final String WF_STATE_SUCCESS = "SUCCESS";
    private static final String WF_STATE_RUNNING = "RUNNING";
    private static final String WF_STATE_ERROR = "ERROR";
    private static final String WF_STATE_PENDING = "PENDING";

    private static final String COMMAND_HEADER_JOB_ID = "Job Id";
    private static final String COMMAND_HEADER_JOB_COMMAND_ID = "Command Id";
    private static final String COMMAND_HEADER_JOB_USER = "Job User";
    private static final String COMMAND_HEADER_JOB_STATUS = "Job Status";
    private static final String COMMAND_HEADER_JOB_START_DATE = "Job Start Date";
    private static final String COMMAND_HEADER_JOB_END_DATE = "Job End Date";
    private static final String COMMAND_HEADER_JOB_NODE_NAME = "Node Name";
    private static final String COMMAND_HEADER_JOB_WF_STATUS = "Workflow Status";
    private static final String COMMAND_HEADER_JOB_WF_START_DATE = "Workflow Start Date";
    private static final String COMMAND_HEADER_JOB_WF_DURATION = "Workflow Duration";
    public final static String TEST_NODE = "LTE03ERBS00003";

    @Inject
    Logger logger;

    @Inject
    CommandHandler commandHandler;

    @Inject
    ResponseDtoReader responseDtoReader;

    public static final String COMMAND_SECADM = "secadm";

    public static final String COMMAND_JOB_GET = "job get";

    public static final String COMMAND_PARAMETER_ALL = "--all";

    public static final String COMMAND_PARAMETER_JOB_LIST_SHORT = "-j";

    public static final String COMMAND_PARAMETER_WF_FILTER = "-wf";

    public static final String COMMAND_PARAMETER_JOB_LIST_LONG = "--joblist";

    @Override
    public void jobGetListOfJobsCommand_NotExistingJobs() {
        logger.info("*** jobGetListOfJobsCommand_NotExistingJobs Test ***");

        performJobCacheResetRest();

        final UUID jobId = UUID.randomUUID();
        final UUID jobId2 = UUID.randomUUID();
        final UUID jobId3 = UUID.randomUUID();

        final Command command = new Command(COMMAND_SECADM, COMMAND_JOB_GET + " " + COMMAND_PARAMETER_JOB_LIST_SHORT + " " + jobId.toString() + ","
                + jobId2.toString() + "," + jobId3.toString());
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);
        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("jobGetListOfJobsCommand_NotExistingJobs Command Response :: " + row);
            }
        }
        assertTrue(responseDtoReader.messageIsContainedInList(MESSAGE_NO_JOB_FOUND, rowsAsListOfStrings));

    }

    @Override
    public void getSingleJobCommand_ShortVersion_ExistingJob() throws Exception {

        final String methodName = "getSingleJobCommand_ShortVersion_ExistingJob";
        logger.info("*** " + methodName + " Test ***");
        //create Job
        final HttpResponse response = performJobCacheCreateRest();

        final String stringResponse = EntityUtils.toString(response.getEntity());

        logger.info(methodName + " Response: " + stringResponse);
        assertTrue(stringResponse.contains(REST_CREATE_JOB_ID_MESSAGE));
        final String stringUUID = stringResponse.substring(stringResponse.lastIndexOf(" ") + 1);
        performSingleGetCommand(methodName, stringUUID, STATE_PENDING, true);
    }

    @Override
    public void getSingleJobCommand_LongVersion_ExistingJob() throws Exception {

        final String methodName = "getSingleJobCommand_LongVersion_ExistingJob";
        logger.info("*** " + methodName + " Test ***");

        //create Job
        final HttpResponse response = performJobCacheCreateRest();

        final String stringResponse = EntityUtils.toString(response.getEntity());

        logger.info(methodName + " Response: " + stringResponse);
        assertTrue(stringResponse.contains(REST_CREATE_JOB_ID_MESSAGE));
        final String stringUUID = stringResponse.substring(stringResponse.lastIndexOf(" ") + 1);
        performSingleGetCommand(methodName, stringUUID, STATE_PENDING, false);
    }

    @Override
    public void getSingleJobCommand_FilteredJob() throws Exception {

        final String methodName = "getSingleJobCommand_FilteredJob";
        logger.info("*** " + methodName + " Test ***");

        //create workflows state
        performJobCacheResetRest();
        //create Job
        HttpResponse response = performJobCacheCreateRest();
        String stringResponse = EntityUtils.toString(response.getEntity());
        logger.info(methodName + " Response: " + stringResponse);
        assertTrue(stringResponse.contains(REST_CREATE_JOB_ID_MESSAGE));
        String stringUUID = stringResponse.substring(stringResponse.lastIndexOf(" ") + 1);
        response = performJobCacheUpdateRest(stringUUID, TEST_NODE, WF_STATE_PENDING);
        performFilteredSingleGetCommand(methodName, stringUUID, WF_STATE_PENDING);

        performJobCacheResetRest();
        //create Job
        response = performJobCacheCreateRest();
        stringResponse = EntityUtils.toString(response.getEntity());
        logger.info(methodName + " Response: " + stringResponse);
        assertTrue(stringResponse.contains(REST_CREATE_JOB_ID_MESSAGE));
        stringUUID = stringResponse.substring(stringResponse.lastIndexOf(" ") + 1);
        response = performJobCacheUpdateRest(stringUUID, TEST_NODE, WF_STATE_RUNNING);
        performFilteredSingleGetCommand(methodName, stringUUID, WF_STATE_RUNNING);

        performJobCacheResetRest();
        //create Job
        response = performJobCacheCreateRest();
        stringResponse = EntityUtils.toString(response.getEntity());
        logger.info(methodName + " Response: " + stringResponse);
        assertTrue(stringResponse.contains(REST_CREATE_JOB_ID_MESSAGE));
        stringUUID = stringResponse.substring(stringResponse.lastIndexOf(" ") + 1);
        response = performJobCacheUpdateRest(stringUUID, TEST_NODE, WF_STATE_SUCCESS);
        performFilteredSingleGetCommand(methodName, stringUUID, WF_STATE_SUCCESS);

        performJobCacheResetRest();
        //create Job
        response = performJobCacheCreateRest();
        stringResponse = EntityUtils.toString(response.getEntity());
        logger.info(methodName + " Response: " + stringResponse);
        assertTrue(stringResponse.contains(REST_CREATE_JOB_ID_MESSAGE));
        stringUUID = stringResponse.substring(stringResponse.lastIndexOf(" ") + 1);
        response = performJobCacheUpdateRest(stringUUID, TEST_NODE, WF_STATE_ERROR);
        performFilteredSingleGetCommand(methodName, stringUUID, WF_STATE_ERROR);

    }

    @Override
    public void getSingleJobCommand_LongVersion_ExistingCompletedJob() throws Exception {

        final String methodName = "getSingleJobCommand_LongVersion_ExistingCompletedJob";
        logger.info("*** " + methodName + " Test ***");

        //create Job
        HttpResponse response = performJobCacheCreateRest();

        final String stringResponse = EntityUtils.toString(response.getEntity());

        logger.info(methodName + " Response: " + stringResponse);
        assertTrue(stringResponse.contains(REST_CREATE_JOB_ID_MESSAGE));
        final String stringUUID = stringResponse.substring(stringResponse.lastIndexOf(" ") + 1);

        //update the job id with workflow running -> success status pippo
        response = performJobCacheUpdateRest(stringUUID, TEST_NODE, WF_STATE_RUNNING);
        performSingleGetCommand(methodName, stringUUID, STATE_RUNNING, true);

        response = performJobCacheUpdateRest(stringUUID, TEST_NODE, WF_STATE_SUCCESS);
        performSingleGetCommand(methodName, stringUUID, STATE_COMPLETED, true);
    }

    @Override
    public void getSingleJobCommand_ShortVersion_NotExistingJob() throws Exception {

//        AccessControlHelper.setupUser("toruser1");
        final String methodName = "getSingleJobCommand_ShortVersion_NotExistingJob";
        logger.info("*** " + methodName + " Test ***");

        //create Job
        final HttpResponse response = performJobCacheCreateRest();
        final String stringResponse = EntityUtils.toString(response.getEntity());
        logger.info(methodName + " Response: " + stringResponse);
        assertTrue(stringResponse.contains(REST_CREATE_JOB_ID_MESSAGE));

        final UUID jobId = UUID.randomUUID();
        performSingleGetCommand_NotExistingJobId(methodName, jobId.toString(), true);
    }

    @Override
    public void getSingleJobCommand_LongVersion_NotExistingJob() throws Exception {
        final String methodName = "getSingleJobCommand_LongVersion_NotExistingJob";
        logger.info("*** " + methodName + " Test ***");
        //create Job
        final HttpResponse response = performJobCacheCreateRest();
        final String stringResponse = EntityUtils.toString(response.getEntity());
        logger.info(methodName + " Response: " + stringResponse);
        assertTrue(stringResponse.contains(REST_CREATE_JOB_ID_MESSAGE));

        final UUID jobId = UUID.randomUUID();
        performSingleGetCommand_NotExistingJobId(methodName, jobId.toString(), false);

    }

    @Override
    public void getSingleJobCommand_InvalidJobIdFormat() throws Exception {
        final String methodName = "getSingleJobCommand_InvalidJobIdFormat";
        logger.info("*** " + methodName + " Test ***");
        //create Job
        final HttpResponse response = performJobCacheCreateRest();
        final String stringResponse = EntityUtils.toString(response.getEntity());
        logger.info(methodName + " Response: " + stringResponse);
        assertTrue(stringResponse.contains(REST_CREATE_JOB_ID_MESSAGE));

        final UUID jobId = UUID.randomUUID();
        //get info for a job id malformed
        performSingleGetCommand_InvalidJobIdFormat(methodName, jobId.toString() + "asdasd", false);
    }

    @Override
    public void getListOfJobsCommand_NotEmptyJobList() throws Exception {
        final String methodName = "getListOfJobsCommand_NotEmptyJobList";
        logger.info("*** " + methodName + " Test ***");

        performJobCacheResetRest();

        final List<String> uuids = new ArrayList<String>();
        HttpResponse response = null;
        String stringResponse = null;
        String stringUUID = null;
        final int numOfJobs = 3;
        String listOfJobIds = "";
        for (int i = 0; i < numOfJobs; i++) {
            //create Job
            response = performJobCacheCreateRest();
            stringResponse = EntityUtils.toString(response.getEntity());

            logger.info(methodName + " Response: " + stringResponse);
            assertTrue(stringResponse.contains(REST_CREATE_JOB_ID_MESSAGE));
            stringUUID = stringResponse.substring(stringResponse.lastIndexOf(" ") + 1);
            uuids.add(stringUUID);
            response = performJobCacheUpdateRest(stringUUID, TEST_NODE, WF_STATE_SUCCESS);
            listOfJobIds += stringUUID + ",";
        }
        //remove last comma ,
        listOfJobIds = listOfJobIds.substring(0, listOfJobIds.length() - 1);

        final Command command = new Command(COMMAND_SECADM, COMMAND_JOB_GET + " " + COMMAND_PARAMETER_JOB_LIST_SHORT + " " + listOfJobIds);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);
        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        int headerCount = 0;
        for (final String row : rowsAsListOfStrings) {
            logger.info(methodName + " Command Response:" + row);
            if (headerCount == 0) {
                //check header titles are present in row
                checkHeaderRowColumnsNames(row);
            } else if (headerCount > 0 && headerCount <= numOfJobs) {
                //check single job id data
                assertTrue("Missing expected UUID: " + uuids.get(headerCount - 1), row.contains(uuids.get(headerCount - 1)));
                assertTrue("Missing expected status: " + STATE_COMPLETED, row.contains(STATE_COMPLETED));
            }
            headerCount++;
        }
        assertTrue(responseDtoReader.messageIsContainedInList(REST_COMMAND_EXECUTED_SUCCESSFULLY, rowsAsListOfStrings));
    }

    @Override
    public void getAllJobsCommand_NotEmptyJobList() throws Exception {
        final String methodName = "getAllJobsCommand_NotEmptyJobList";
        logger.info("*** " + methodName + " Test ***");

        performJobCacheResetRest();

        final List<String> uuids = new ArrayList<String>();
        HttpResponse response = null;
        String stringResponse = null;
        String stringUUID = null;
        final int numOfJobs = 3;
        for (int i = 0; i < numOfJobs; i++) {
            //create Job
            response = performJobCacheCreateRest();
            Thread.sleep(1000);

            stringResponse = EntityUtils.toString(response.getEntity());

            logger.info(methodName + " Response: " + stringResponse);
            assertTrue(stringResponse.contains(REST_CREATE_JOB_ID_MESSAGE));
            stringUUID = stringResponse.substring(stringResponse.lastIndexOf(" ") + 1);
            uuids.add(stringUUID);
            response = performJobCacheUpdateRest(stringUUID, TEST_NODE, WF_STATE_SUCCESS);
            logger.info("workflow update Response: " + response);

        }

        Thread.sleep(5000);

        final Command command = new Command(COMMAND_SECADM, COMMAND_JOB_GET + " " + COMMAND_PARAMETER_ALL);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);
        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        int headerCount = 0;

        for (final String row : rowsAsListOfStrings) {
            logger.info("LOOP " + methodName + " Command Response:" + row);
        }

        for (final String row : rowsAsListOfStrings) {
            logger.info(methodName + " Command Response:" + row);
            if (headerCount == 0) {
                //check header titles are present in row
                checkHeaderRowColumnsNames(row);
            } else if (headerCount > 0 && headerCount <= numOfJobs) {
                //check single job id data
                assertTrue("Missing expected UUID: " + uuids.get(headerCount - 1), row.contains(uuids.get(headerCount - 1)));
                assertTrue("Missing expected status: " + STATE_COMPLETED, row.contains(STATE_COMPLETED));
            }
            headerCount++;
        }
        assertTrue(responseDtoReader.messageIsContainedInList(REST_COMMAND_EXECUTED_SUCCESSFULLY, rowsAsListOfStrings));

    }

    /**
     * Perform get job id command with invalid format
     *
     */
    private void performSingleGetCommand_InvalidJobIdFormat(final String methodName, final String stringUUID, final boolean isShortVersion) {
        final String shortOrLongJobParameter = (isShortVersion) ? COMMAND_PARAMETER_JOB_LIST_SHORT : COMMAND_PARAMETER_JOB_LIST_LONG;

        final String commandString = COMMAND_JOB_GET + " " + shortOrLongJobParameter + " " + stringUUID;
        logger.info(methodName + " stringUUID: " + stringUUID + " command to be executed: " + commandString);
        final Command command = new Command(COMMAND_SECADM, commandString);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);
        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info(methodName + " Command Response :: " + row);
            }
        }
        assertTrue(responseDtoReader.messageIsContainedInList(NscsErrorCodes.PLEASE_PROVIDE_VALID_JOB_ID, rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList(MESSAGE_INVALID_JOB_IDENTIFIER, rowsAsListOfStrings));
    }

    @Override
    public void getAllJobsCommand_EmptyJobList() throws Exception {
        logger.info("*** getAllJobsCommand_EmptyJobList Test ***");

        performJobCacheResetRest();

        final Command command = new Command(COMMAND_SECADM, COMMAND_JOB_GET + " " + COMMAND_PARAMETER_ALL);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);
        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info("getAllJobsCommand_EmptyJobList Command Response :: " + row);
            }
        }
        assertTrue(responseDtoReader.messageIsContainedInList(MESSAGE_NO_JOB_FOUND, rowsAsListOfStrings));

    }

    private void performSingleGetCommand_NotExistingJobId(final String methodName, final String stringUUID, final boolean isShortVersion) {
        final String shortOrLongJobParameter = (isShortVersion) ? COMMAND_PARAMETER_JOB_LIST_SHORT : COMMAND_PARAMETER_JOB_LIST_LONG;

        final String commandString = COMMAND_JOB_GET + " " + shortOrLongJobParameter + " " + stringUUID;
        logger.info(methodName + " stringUUID: " + stringUUID + " command to be executed: " + commandString);
        final Command command = new Command(COMMAND_SECADM, commandString);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);
        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        for (final String row : rowsAsListOfStrings) {
            if (row != null) {
                logger.info(methodName + " Command Response :: " + row);
            }
        }
        assertTrue(responseDtoReader.messageIsContainedInList(NscsErrorCodes.PLEASE_PROVIDE_VALID_JOB_ID, rowsAsListOfStrings));
        assertTrue(responseDtoReader.messageIsContainedInList(MESSAGE_NO_JOB_FOUND, rowsAsListOfStrings));
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
        } catch (final ClientProtocolException e) {
            e.printStackTrace();
            logger.error("Exception ClientProtocolException while executing http request [{}]", post.getURI().toString());
        } catch (final IOException e) {
            e.printStackTrace();
            logger.error("Exception IOException while executing http request [{}]", post.getURI().toString());
        }
        return response;
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
        final HttpClient httpclient = HttpClientBuilder.create().build();
        try {
            response = httpclient.execute(delete);
        } catch (final ClientProtocolException e) {
            e.printStackTrace();
            logger.error("Exception ClientProtocolException while executing http request [{}]", delete.getURI().toString());
        } catch (final IOException e) {
            e.printStackTrace();
            logger.error("Exception IOException while executing http request [{}]", delete.getURI().toString());
        }
    }

    private HttpResponse performJobCacheUpdateRest(final String stringUUID, final String nodeName, final String wfStateSuccess) {

        final Map<String, String> header = new HashMap<String, String>() {

            private static final long serialVersionUID = 1L;

            {
                put("X-Tor-UserID", "Administrator");
            }
        };

        HttpResponse response = null;
        final String url = RestHelper.getRestHttpUrl(RestHelper.NODE_SECURITY_JOB_PATH) + "/" + stringUUID + "/" + nodeName + "/" + wfStateSuccess;
        logger.info("-----------performJobCacheUpdateRest starts url [{}] --------------", url);
        final HttpPut put = new HttpPut(url);
        if (header != null) {
            for (final Entry<String, String> e : header.entrySet()) {
                put.addHeader(e.getKey(), e.getValue());
            }

        }
        final HttpClient httpclient = HttpClientBuilder.create().build();
        try {
            response = httpclient.execute(put);
        } catch (final ClientProtocolException e) {
            e.printStackTrace();
            logger.error("Exception ClientProtocolException while executing http request [{}]", put.getURI().toString());
        } catch (final IOException e) {
            e.printStackTrace();
            logger.error("Exception IOException while executing http request [{}]", put.getURI().toString());
        }
        return response;

    }

    /**
     * Check if column name are present
     *
     */
    private void checkHeaderRowColumnsNames(final String row) {
        final List<String> headerRowList = new ArrayList<String>();
        headerRowList.add(row);
        logger.info("Header row to check " + row);
        assertTrue("Missing header column name " + COMMAND_HEADER_JOB_ID,
                responseDtoReader.messageIsContainedInList(COMMAND_HEADER_JOB_ID, headerRowList));
        assertTrue("Missing header column name " + COMMAND_HEADER_JOB_COMMAND_ID,
                responseDtoReader.messageIsContainedInList(COMMAND_HEADER_JOB_COMMAND_ID, headerRowList));
        assertTrue("Missing header column name " + COMMAND_HEADER_JOB_USER,
                responseDtoReader.messageIsContainedInList(COMMAND_HEADER_JOB_USER, headerRowList));
        assertTrue("Missing header column name " + COMMAND_HEADER_JOB_STATUS,
                responseDtoReader.messageIsContainedInList(COMMAND_HEADER_JOB_STATUS, headerRowList));
        assertTrue("Missing header column name " + COMMAND_HEADER_JOB_START_DATE,
                responseDtoReader.messageIsContainedInList(COMMAND_HEADER_JOB_START_DATE, headerRowList));
        assertTrue("Missing header column name " + COMMAND_HEADER_JOB_END_DATE,
                responseDtoReader.messageIsContainedInList(COMMAND_HEADER_JOB_END_DATE, headerRowList));
        //  assertTrue("Missing header column name " + COMMAND_HEADER_JOB_WF_ID, responseDtoReader.messageIsContainedInList(COMMAND_HEADER_JOB_WF_ID, headerRowList));
        assertTrue("Missing header column name " + COMMAND_HEADER_JOB_NODE_NAME,
                responseDtoReader.messageIsContainedInList(COMMAND_HEADER_JOB_NODE_NAME, headerRowList));
        assertTrue("Missing header column name " + COMMAND_HEADER_JOB_WF_STATUS,
                responseDtoReader.messageIsContainedInList(COMMAND_HEADER_JOB_WF_STATUS, headerRowList));
        assertTrue("Missing header column name " + COMMAND_HEADER_JOB_WF_START_DATE,
                responseDtoReader.messageIsContainedInList(COMMAND_HEADER_JOB_WF_START_DATE, headerRowList));
        assertTrue("Missing header column name " + COMMAND_HEADER_JOB_WF_DURATION,
                responseDtoReader.messageIsContainedInList(COMMAND_HEADER_JOB_WF_DURATION, headerRowList));
    }

    /*
     * Perform single get command
     *
     */
    private void performSingleGetCommand(final String methodName, final String stringUUID, final String status, final boolean isShortVersion) {

        final String shortOrLongJobParameter = (isShortVersion) ? COMMAND_PARAMETER_JOB_LIST_SHORT : COMMAND_PARAMETER_JOB_LIST_LONG;
        final String commandString = COMMAND_JOB_GET + " " + shortOrLongJobParameter + " " + stringUUID;
        logger.info(methodName + " stringUUID: " + stringUUID + " command to be executed: " + commandString);
        final Command command = new Command(COMMAND_SECADM, commandString);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);
        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        int headerCount = 0;
        for (final String row : rowsAsListOfStrings) {
            logger.info(methodName + " Command Response:" + row);
            if (headerCount == 0) {
                //check header titles are present in row
                checkHeaderRowColumnsNames(row);
            } else if (headerCount == 1) {
                //check single job id data
                assertTrue("Missing expected UUID: " + stringUUID, row.contains(stringUUID));
                assertTrue("Missing expected status: " + status, row.contains(status));
            }
            headerCount++;
        }
        assertTrue(responseDtoReader.messageIsContainedInList(REST_COMMAND_EXECUTED_SUCCESSFULLY, rowsAsListOfStrings));
    }

    /*
     * Perform single get command
     *
     */
    private void performFilteredSingleGetCommand(final String methodName, final String stringUUID, final String wfStatus) {

        final String commandString = COMMAND_JOB_GET + " " + COMMAND_PARAMETER_JOB_LIST_SHORT + " " + stringUUID + " " + COMMAND_PARAMETER_WF_FILTER
                + " " + wfStatus;
        logger.info(methodName + " stringUUID: " + stringUUID + " command to be executed: " + commandString);
        final Command command = new Command(COMMAND_SECADM, commandString);
        final CommandResponseDto commandResponseDto = commandHandler.execute(command);
        final List<String> rowsAsListOfStrings = responseDtoReader.extractListOfRowsFromCommandResponseDto(commandResponseDto);
        int headerCount = 0;

        for (final String row : rowsAsListOfStrings) {

            logger.info(methodName + " Command Response:" + row);
            if (headerCount == 0) {
                //check header titles are present in row
                checkHeaderRowColumnsNames(row);
            } else if (headerCount == 1) {
                assertTrue("Missing expected UUID: " + stringUUID, row.contains(stringUUID));
                assertTrue("Missing expected status: " + wfStatus, row.contains(wfStatus));
            }

            if (headerCount > 1) {
                break;
            } else {
                headerCount++;
            }
        }
        assertTrue(responseDtoReader.messageIsContainedInList(REST_COMMAND_EXECUTED_SUCCESSFULLY, rowsAsListOfStrings));

    }
}
