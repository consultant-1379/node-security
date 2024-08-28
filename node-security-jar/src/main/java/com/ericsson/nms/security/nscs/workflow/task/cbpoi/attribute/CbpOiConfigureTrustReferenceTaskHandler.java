/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.workflow.task.cbpoi.attribute;

import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cbpoi.attribute.CbpOiConfigureTrustReferenceTask;

import javax.ejb.Local;
import javax.inject.Inject;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

@WFTaskType(WorkflowTaskType.CBPOI_CONFIGURE_SERVICES_TRUST_REFERENCE)
@Local(WFTaskHandlerInterface.class)
public class CbpOiConfigureTrustReferenceTaskHandler implements WFQueryTaskHandler<CbpOiConfigureTrustReferenceTask>, WFTaskHandlerInterface {

    @Inject
    NscsLogger nscsLogger;

    private static final String DONE = "DONE";

    @Override
    public String processTask(final CbpOiConfigureTrustReferenceTask task) {
        nscsLogger.workFlowTaskHandlerStarted(task);

     /*
         * Extract output parameters possibly set by previous handlers
         */
        nscsLogger.info(task, "Extracting output parameters received from previous handler");
        final Map<String, Serializable> outputParams = task.getOutputParams();

        final String result = DONE;
        final String successMessage = "Successfully completed : Services Trust Reference state is [" + result + "]";
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage);

        return prepareReplyDone(task, result, outputParams);
    }

    private String prepareReplyDone(final CbpOiConfigureTrustReferenceTask task, final String result, final Map<String, Serializable> outputParams) {
        nscsLogger.debug(task, "Preparing workflow task result with value [{}]", result);
        final WorkflowQueryTaskResult workflowTaskResult = new WorkflowQueryTaskResult(result, outputParams);
        return serializeWorkflowObject(task, workflowTaskResult);
    }

    private <T extends Serializable> String serializeWorkflowObject(final CbpOiConfigureTrustReferenceTask task, T object) {
        String serializedResult;
        nscsLogger.debug(task, "Serializing workflow task result");
        try {
            serializedResult = NscsObjectSerializer.writeObject(object);
        } catch (final IOException exc) {
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, "Received exception: " + exc.getMessage());
            throw new UnexpectedErrorException(NscsLogger.stringifyException(exc) + " while serializing task output params");
        }
        nscsLogger.debug(task, "Workflow task result has been correctly serialized");
        return serializedResult;
    }

}
