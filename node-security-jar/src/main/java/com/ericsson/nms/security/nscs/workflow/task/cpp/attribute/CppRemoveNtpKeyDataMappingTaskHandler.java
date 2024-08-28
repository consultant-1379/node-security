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
package com.ericsson.nms.security.nscs.workflow.task.cpp.attribute;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.util.NtpConstants;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.CPPRemoveNtpKeyDataMappingTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;

/**
 * <p>
 * Task handler for WorkflowTaskType.CPP_REMOVE_NTP_KEY_DATA_MAPPING
 * </p>
 * <p>
 * Removes key Id and node mapping from the NTP service database where NTP server with the associated given keyid is removed on the node and the same key id is mapped in NTP Server in same
 * environment.
 * </p>
 * 
 * @author xvekkar
 */
@WFTaskType(WorkflowTaskType.CPP_REMOVE_NTP_KEY_DATA_MAPPING)
@Local(WFTaskHandlerInterface.class)
public class CppRemoveNtpKeyDataMappingTaskHandler implements WFQueryTaskHandler<CPPRemoveNtpKeyDataMappingTask>, WFTaskHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Override
    public String processTask(final CPPRemoveNtpKeyDataMappingTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);
        final Map<String, Serializable> outputParams = task.getOutputParams();
        if (outputParams == null) {
            final String errorMessage = "Missing internal parameters";
            nscsLogger.info(task, "Output parameter is null: ", errorMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        final String serializedNodeNtpKeyIdInfo = (String) outputParams.get(WorkflowOutputParameterKeys.MAPPING_TO_BE_REMOVED_FOR_KEY_IDS.toString());
        final List<Integer> mappingToBeRemovedForKeyId = NscsObjectSerializer.readObject(serializedNodeNtpKeyIdInfo);

        nscsLogger.info("Removing NTP key data mapping between keyids: [{}] and node: [{}]", mappingToBeRemovedForKeyId, task.getNode().getName());
        for (final Integer keyId : mappingToBeRemovedForKeyId) {
            invokeJsonPostRestRemoveMapping(task, keyId);
        }
        final String successMessage = "Successfully removed key id Mapping in NTP service for removed keys.";
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage);
        return "success";
    }

    private void invokeJsonPostRestRemoveMapping(final CPPRemoveNtpKeyDataMappingTask task, final Integer keyId) {

        nscsLogger.info("Invoking rest call to remove keyids [{}]  and nodename [{}] mapping in ntp server", task.getNode().getFdn(), keyId);
        final String url = String.format(NtpConstants.NTP_SERVICE_REMOVE_MAPPING);
        nscsLogger.debug("url to remove keyid and node mapping in NTP server {}", url);
        final HttpPost httpPost = new HttpPost(url);
        int statusCode = 0;
        final String json = "{\"keyId\":" + keyId + ",\"nodeFdn\": \"" + task.getNode().getFdn() + "\"}";
        StringEntity entity;
        CloseableHttpClient client = null;
        try {
            client = HttpClients.createDefault();
            entity = new StringEntity(json);

            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("host","dhcp-service");

            final CloseableHttpResponse response = client.execute(httpPost);
            statusCode = response.getStatusLine().getStatusCode();
            client.close();
        } catch (IOException e) {
            nscsLogger.info("Exception raised during mapping removal for node [{}] and key Id [{}] with exception[{}] ", task.getNode().getFdn(), keyId, e);
        } finally {
            if(client != null) {
            try {
                client.close();
            } catch (IOException e) {
                    nscsLogger.info("Exception raised during closing connection : [{}]", e);
            }
            }
        }
        if (statusCode == 200) {
            nscsLogger.info("Successfully executed remove mapping request for node [{}] and key Id [{}] with status code . [{}]", task.getNode().getFdn(), keyId, statusCode);
        } else {
            nscsLogger.error("Remove mapping request has been failed for node [{}] and key Id [{}] with status code . [{}]", task.getNode().getFdn(), keyId, statusCode);
            final String errorMessage = "Remove mapping request has been failed on node for key Id: " + keyId;
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new WorkflowTaskException(errorMessage);
        }
    }
}
