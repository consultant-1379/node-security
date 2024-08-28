/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2019
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.ntp.utility;

import java.io.IOException;

import javax.inject.Inject;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.ericsson.nms.security.nscs.api.exception.DatabaseUnavailableException;
import com.ericsson.nms.security.nscs.api.exception.NTPKeyMappingNotFoundException;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.util.NtpConstants;
import com.ericsson.nms.security.nscs.utilities.ResponseErrorDetails;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Sends ntp configure status to ntp server
 *
 * @author xjangop
 */

public class NtpConfigureStatusSender {

    @Inject
    private NscsLogger nscsLogger;


    public void sendNtpConfigureStatus(final WorkflowQueryTask task, final String status) {

        nscsLogger.info("NtpConfigureStatusSender: invokeNtpServiceJsonPostRest: nodeFdn [{}] status [{}]", task.getNodeFdn(), status);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(NtpConstants.NTP_SERVICE_STATUS_URL);
        int statusCode = 0;
        String json = "{\"status\":\"" + status + "\",\"nodeFdn\": \"" + task.getNodeFdn() + "\"}";
        nscsLogger.info("Sending Ntp Configure Status to url [{}] : with json string=[{}] ", NtpConstants.NTP_SERVICE_STATUS_URL, json);
        StringEntity entity;
        String responseEntity = null;
        try {
            entity = new StringEntity(json);

            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("host","dhcp-service");

            CloseableHttpResponse response = client.execute(httpPost);
            nscsLogger.debug("NtpConfigureStatusSender response : {}", response);
            responseEntity = EntityUtils.toString(response.getEntity(), "UTF-8");
            statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                nscsLogger.info("Ntp configure " + status + " status processed by ntp server for Node: {}", task.getNodeFdn());
            } else {
                nscsLogger.error("Ntp configure " + status + " status not processed by ntp server for Node: {} received HTTP status code: {}",
                        task.getNodeFdn(), statusCode);
                mapErrorCodeToException(responseEntity, task);
            }

        } catch (Exception exception) {
            nscsLogger.error(
                    NscsLogger.stringifyException(exception) + " Error occured in NtpConfigureStatusSender during HttpPost request for Node: {}",
                    task.getNodeFdn());
        }
        finally {
            try {
                client.close();
            } catch (IOException ioException) {
                nscsLogger.error(NscsLogger.stringifyException(ioException)
                        + " Error occured in NtpConfigureStatusSender while closing HttpClient for Node: {}", task.getNodeFdn());
            }
        }
    }

    private void mapErrorCodeToException(final String responseEntity, final WorkflowQueryTask task) throws IOException {

        nscsLogger.info("NtpConfigureStatusSender : responseEntity {}", responseEntity);

        final ObjectMapper mapper = new ObjectMapper();
        final ResponseErrorDetails errorDetails = mapper.readValue(responseEntity, ResponseErrorDetails.class);
        final int internalErrorCode = errorDetails.getErrorCode();
        final String errorMessages = errorDetails.getErrorMessage();
        nscsLogger.error("InternalErrorCode {}  and errorMessages: {} from the http request for Node:{}", internalErrorCode, errorMessages,
                task.getNodeFdn());
        String errorLog = " while sending Ntp configure status in NtpConfigureStatusSender for Node: " + task.getNodeFdn();
        if (internalErrorCode == 23500) {
            final String errorMessage = "An internal error occurred" + errorLog;
            nscsLogger.error(errorMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);

        } else if (internalErrorCode == 23518) {
            final String errorMessage = "Missing mandatory parameters in the request" + errorLog;
            nscsLogger.error(errorMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);

        } else if (internalErrorCode == 23513) {
            final String errorMessage = "No NTP key mapping exists" + errorLog;
            nscsLogger.error(errorMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new NTPKeyMappingNotFoundException(errorMessage);

        } else if (internalErrorCode == 23514) {
            final String errorMessage = "A database error occurred" + errorLog;
            nscsLogger.error(errorMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new DatabaseUnavailableException(errorMessage);

        } else {
            final String errorMessage = "An unexpected error occurred" + errorLog;
            nscsLogger.error(errorMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
    }
}