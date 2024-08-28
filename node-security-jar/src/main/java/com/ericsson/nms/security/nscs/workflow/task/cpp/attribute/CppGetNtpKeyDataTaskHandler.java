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
import java.util.HashMap;
import java.util.Map;
import javax.ejb.Local;
import javax.inject.Inject;
import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.ntp.utility.GetNtpKeyDataResponseEntity;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.CppGetNtpKeyDataTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;

/**
 * Task handler for WorkflowTaskType.CPP_GET_NTP_KEY_DATA
 *
 * Fetches the NTP key data from ntp service to be configured on the given node
 *
 * @author xkihari
 */
@WFTaskType(WorkflowTaskType.CPP_GET_NTP_KEY_DATA)
@Local(WFTaskHandlerInterface.class)
public class CppGetNtpKeyDataTaskHandler implements WFQueryTaskHandler<CppGetNtpKeyDataTask>, WFTaskHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private GetNtpKeyDataResponseEntity ntpKeyDataResponseEntity;

    @Override
    public String processTask(final CppGetNtpKeyDataTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);
        String responseEntity = null;
        String ntpKeyData = null;
        try {

            responseEntity = ntpKeyDataResponseEntity.getNtpKeyDataFromNtpService(task, false);
            nscsLogger.info("Cpp Get Ntp Key Data received ResponseEntity for Node {}", task.getNodeFdn());
            ntpKeyData = serializeResult(task, responseEntity);

        } catch (final Exception exception) {
            final String errorMessage = "CppGetNtpKeyDataTaskHandler: " + NscsLogger.stringifyException(exception);
            nscsLogger.error(errorMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new WorkflowTaskException("Error while getting ntp key data from ntp service. " + exception.getMessage());
        }
        return ntpKeyData;
    }

    private String serializeResult(final CppGetNtpKeyDataTask task, final String ntpKeyData) {
        String encodedWfQueryTaskResult = null;
        Map<String, Serializable> outputParams = task.getOutputParams();
        if (outputParams == null) {
            nscsLogger.info(task, "CppGetNtpKeyDataTaskHandler: Initializing output params!");
            outputParams = new HashMap<>();
        }

        outputParams.put(WorkflowOutputParameterKeys.NTP_KEY.toString(), ntpKeyData);
        final WorkflowQueryTaskResult wfQueryTaskResult = new WorkflowQueryTaskResult(outputParams);
        try {
            encodedWfQueryTaskResult = NscsObjectSerializer.writeObject(wfQueryTaskResult);
        } catch (final IOException ioException) {
            final String errorMessage = "CppGetNtpKeyDataTaskHandler: " + NscsLogger.stringifyException(ioException);
            nscsLogger.error(errorMessage);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException("Error while serializing Ntp key data parameter.");
        }
        final String successMessage = "Successfully fetched NtpKeyData for Node: " + task.getNodeFdn();
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage);

        return encodedWfQueryTaskResult;
    }

}
