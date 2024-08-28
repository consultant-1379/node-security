/*------------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2017
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
import com.ericsson.oss.services.nscs.workflow.tasks.log.LogFailureTask;

@WFTaskType(WorkflowTaskType.LOG_FAILURE)
@Local(WFTaskHandlerInterface.class)
public class LogFailureTaskHandler implements WFActionTaskHandler<LogFailureTask>, WFTaskHandlerInterface {

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
    public void processTask(final LogFailureTask task) {

        final String fdn = task.getNodeFdn();
        final String workflowName = task.getWorkflowDefinitionId();
        final String workflowInstanceId = task.getWorkflowInstanceId();
        final String activationStep = task.getActivationStep();
        final String jobIdStr = task.getJobid();
        final String wfStatusId = task.getWfWakeId();
        final String additionalInfo = String.format("Workflow failure at step " + activationStep + " with jobID: "
                + ((jobIdStr != null) ? jobIdStr : NOT_APPLICABLE) + " and scheduledWFID: " + wfStatusId);

        nscsLogger.workFlowFinishedWithError(workflowName, workflowInstanceId, fdn, additionalInfo);
        nscsWorkflowNodeStatusDataHandler.updateNodeCacheStatusByWorkflow(fdn, workflowName, true);

        if (wfStatusId != null) {
            final WfResult wfResult = cacheHandler.getWfResult(UUID.fromString(wfStatusId));
            wfResult.setStatus(WfStatusEnum.ERROR);
            final String message = wfResult.getMessage();
            wfResult.setMessage(((message != null && !message.isEmpty()) ? message + " [" : "[") + "FAILURE]");
            wfResult.setWfParams(new HashMap<String, Object>());
            cacheHandler.updateWorkflow(wfResult);
            nscsLogger.info("LogFailureTaskHandler ready to update cache for wfResult [{}]", wfResult);

        } else {
            nscsLogger.error("Null wfStatusId");
        }

        nscsInstrumentationService.updateFailedWorkflow(workflowName);
    }
}