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
package com.ericsson.nms.security.nscs.workflow.task.log;

import java.util.HashMap;
import java.util.UUID;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.instrumentation.NscsInstrumentationService;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.workflow.cache.NscsWorkflowNodeStatusDataHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFActionTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.dto.WfResult;
import com.ericsson.oss.services.enums.WfStatusEnum;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.log.LogErrorTask;

@WFTaskType(WorkflowTaskType.LOG_ERROR)
@Local(WFTaskHandlerInterface.class)
public class LogErrorTaskHandler implements WFActionTaskHandler<LogErrorTask>, WFTaskHandlerInterface {

    private static final String NOT_APPLICABLE = "N/A";

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsInstrumentationService nscsInstrumentationService;

    @EJB
    private NscsWorkflowNodeStatusDataHandler nscsWorkflowNodeStatusDataHandler;

    @Inject
    private NscsJobCacheHandler cacheHandler;

    @Override
    public void processTask(final LogErrorTask task) {

        final String fdn = task.getNodeFdn();
        final String workflowName = task.getWorkflowDefinitionId();
        final String workflowInstanceId = task.getWorkflowInstanceId();
        final String activationStep = task.getActivationStep();
        final String errorDetails = task.getErrorDetails();
        final String jobIdStr = task.getJobid();
        final String wfWakeId = task.getWfWakeId();
        final String additionalInfo = String.format("Workflow failed with error " + errorDetails + " at step " + activationStep + " with jobID: "
                + ((jobIdStr != null) ? jobIdStr : NOT_APPLICABLE) + " and scheduledWFID: " + wfWakeId);

        nscsLogger.workFlowFinishedWithError(workflowName, workflowInstanceId, fdn, additionalInfo);
        nscsWorkflowNodeStatusDataHandler.updateNodeCacheStatusByWorkflow(fdn, workflowName, true);

        if (wfWakeId != null) {
            final WfResult wfResult = cacheHandler.getWfResult(UUID.fromString(wfWakeId));
            wfResult.setStatus(WfStatusEnum.ERROR);
            final String message = wfResult.getMessage();
            wfResult.setMessage(((message != null && !message.isEmpty()) ? message + " [" : "[") + "ERROR: " + errorDetails + "]");
            wfResult.setWfParams(new HashMap<String, Object>());
            cacheHandler.updateWorkflow(wfResult);
            nscsLogger.info("LogErrorTaskHandler ready to update cache for wfResult [{}]", wfResult);

        } else {
            nscsLogger.error("Null wfStatusId");
        }

        nscsInstrumentationService.updateErroredWorkflow(workflowName);
    }
}