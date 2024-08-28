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

import com.ericsson.nms.security.nscs.api.exception.DataAccessException;
import com.ericsson.nms.security.nscs.api.exception.DatabaseUnavailableException;
import com.ericsson.nms.security.nscs.api.exception.NTPKeyNotFoundException;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.util.NtpConstants;
import com.ericsson.nms.security.nscs.utilities.ResponseErrorDetails;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Get ntp key data from ntp service
 *
 * @author xjangop
 */

public class GetNtpKeyDataResponseEntity {

    @Inject
    private NscsLogger nscsLogger;


    public String getNtpKeyDataFromNtpService(final WorkflowQueryTask task, final boolean isNewKeyRequired) {

        final String nodeFdn = task.getNodeFdn();
        nscsLogger.info("GetNtpKeyDataResponseEntity: NodeFdn [{}] status [{}]", nodeFdn, isNewKeyRequired);
        final CloseableHttpClient client = HttpClients.createDefault();
        final HttpPost httpPost = new HttpPost(NtpConstants.NTP_SERVICE_GET_KEY_DATA_URL);
        CloseableHttpResponse response = null;
        final String json = "{\"isNewKeyRequired\":" + isNewKeyRequired + ",\"nodeFdn\": \"" + nodeFdn + "\"}";
        nscsLogger.info("GetNtpKeyDataResponseEntity: Getting Ntp key data from ntpservice url [{}] : with json string=[{}]",
                NtpConstants.NTP_SERVICE_GET_KEY_DATA_URL, json);
        StringEntity entity;
        String responseEntity = null;

        try {
            entity = new StringEntity(json);

            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("host","dhcp-service");

            response = client.execute(httpPost);
            nscsLogger.debug("GetNtpKeyDataResponseEntity: received Http response for NodeFdn:{}", nodeFdn);

            if (isValidResponse(response)) {
                nscsLogger.info("Fetched ntp key data from ntp server for NodeFdn:{}.", nodeFdn);
                responseEntity = EntityUtils.toString(response.getEntity(), "UTF-8");
                nscsLogger.debug("GetNtpKeyDataResponseEntity: received Http responseEntity for NodeFdn:{}", nodeFdn);
                checkResponseEntity(responseEntity, task);
            } else {
                nscsLogger.error("GetNtpKeyDataResponseEntity: Issue while fetching ntp key data from ntp server for NodeFdn:{}.", nodeFdn);
                mapErrorCodeToException(responseEntity, task);
            }



        } catch (IOException ioException) {
            final String errorMessage = NscsLogger.stringifyException(ioException)
                    + " Error occured in GetNtpKeyDataResponseEntity during HttpPost request.";
            nscsLogger.error(errorMessage);
            throw new UnexpectedErrorException("An error occurred while retrieving key details from the NTP service.");
        } finally {
            try {
                client.close();
            } catch (IOException ioException) {
                nscsLogger.error(
                        NscsLogger.stringifyException(ioException) + " Error occured in GetNtpKeyDataResponseEntity while closing HttpClient.");
            }
        }

        return responseEntity;

    }

    private void mapErrorCodeToException(final String responseEntity, final WorkflowQueryTask task) throws IOException {

        nscsLogger.info("GetNtpKeyDataResponseEntity : for Node {}", task.getNodeFdn());
        checkResponseEntity(responseEntity, task);
        final ObjectMapper mapper = new ObjectMapper();
        final ResponseErrorDetails errorDetails = mapper.readValue(responseEntity, ResponseErrorDetails.class);
        final int internalErrorCode = errorDetails.getErrorCode();
        final String errorMessages = errorDetails.getErrorMessage();
        nscsLogger.info("InternalErrorCode {}  and errorMessages: {} from the http request", internalErrorCode, errorMessages);
        final String errorLog = " while fetching ntp key data from ntp server in GetNtpKeyDataResponseEntity.";
        if (internalErrorCode == 23500) {
            final String errorMessage = "An internal error occurred" + errorLog;
            nscsLogger.error(task, errorMessage);
            throw new UnexpectedErrorException("Invalid Ntp key details from ntp service.");

        } else if (internalErrorCode == 23511) {
            final String errorMessage = "Unauthorized user access" + errorLog;
            nscsLogger.error(task, errorMessage);
            throw new DataAccessException("Unauthrozied access to ntp service.");

        } else if (internalErrorCode == 23512) {
            final String errorMessage = "Unable to find the ntp key" + errorLog;
            nscsLogger.error(task, errorMessage);
            throw new NTPKeyNotFoundException("Unable to find the ntp key in ntp service.");

        } else if (internalErrorCode == 23514) {
            final String errorMessage = "A database error occurred" + errorLog;
            nscsLogger.error(task, errorMessage);
            throw new DatabaseUnavailableException("A database error occurred in ntp service.");

        } else {
            final String errorMessage = "An unexpected error occurred" + errorLog;
            nscsLogger.error(task, errorMessage);
            throw new UnexpectedErrorException("An unexpected error occurred in ntp service.");
        }
    }

    private boolean isValidResponse(final CloseableHttpResponse response) {
        final int statusCode = response.getStatusLine().getStatusCode();
        nscsLogger.info("GetNtpKeyDataResponseEntity: StatusCode {} from the get ntp key data http request.", statusCode);

        boolean statusResponse = true;

        if (statusCode >= 400) {
            statusResponse = false;
        }
        return statusResponse;
    }

    private void checkResponseEntity(final String responseEntity, final WorkflowQueryTask task) {
        if (responseEntity == null || responseEntity.isEmpty()) {
            final String errorMessage = "No ntp key details received from ntp service.";
            nscsLogger.error(errorMessage + " For node: " + task.getNodeFdn());
            throw new UnexpectedErrorException(errorMessage);
        }
    }

}