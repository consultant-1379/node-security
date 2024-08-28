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
import java.util.Map;
import java.util.UUID;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.instrumentation.NscsInstrumentationService;
import com.ericsson.nms.security.nscs.handler.command.utility.NscsGetJobResponseBuilder;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.workflow.cache.NscsWorkflowNodeStatusDataHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFActionTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.dto.WfResult;
import com.ericsson.oss.services.enums.WfStatusEnum;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.log.LogSuccessTask;

@WFTaskType(WorkflowTaskType.LOG_SUCCESS)
@Local(WFTaskHandlerInterface.class)
public class LogSuccessTaskHandler implements WFActionTaskHandler<LogSuccessTask>, WFTaskHandlerInterface {

    private static final String NOT_APPLICABLE = "N/A";

    @Inject
    private NscsLogger nscsLogger;

    @EJB
    private NscsWorkflowNodeStatusDataHandler nscsWorkflowNodeStatusDataHandler;

    @Inject
    private NscsJobCacheHandler cacheHandler;

    @Inject
    private NscsInstrumentationService nscsInstrumentationService;

    @Override
    public void processTask(final LogSuccessTask task) {

        final String fdn = task.getNodeFdn();
        final String workflowName = task.getWorkflowDefinitionId();
        final String workflowInstanceId = task.getWorkflowInstanceId();
        final String activationStep = task.getActivationStep();
        final String jobIdStr = task.getJobid();
        final String wfStatusId = task.getWfWakeId();
        final String additionalInfo = String.format("Workflow successfully completed with jobID: " + (jobIdStr != null ? jobIdStr : NOT_APPLICABLE)
                + ", wfStatusId: " + wfStatusId + ", activationStep: " + activationStep);
        Long duration = 0L;

        nscsLogger.workFlowFinishedWithSuccess(workflowName, workflowInstanceId, fdn, additionalInfo);

        nscsWorkflowNodeStatusDataHandler.updateNodeCacheStatusByWorkflow(fdn, workflowName, true);

        if (wfStatusId != null) {
            final WfResult wfResult = cacheHandler.getWfResult(UUID.fromString(wfStatusId));
            wfResult.setStatus(WfStatusEnum.SUCCESS);
            wfResult.setMessage("[OK]");

            Map<String, Object> wfParams = wfResult.getWfParams();

            if (wfParams == null || wfParams.isEmpty()) {
                nscsLogger.info("WorkFlow params map is NULL or EMPTY", wfResult);

                wfResult.setWfParams(new HashMap<String, Object>());

            } else {
                nscsLogger.info("WorkFlow params map is NOT EMPTY", wfResult);

                if (wfParams.containsKey(NscsGetJobResponseBuilder.WORKFLOW_RESULT)) {

                    final Object columnValue = wfParams.get(NscsGetJobResponseBuilder.WORKFLOW_RESULT);

                    final HashMap<String, Object> limitedWfParams = new HashMap<>();
                    limitedWfParams.put(NscsGetJobResponseBuilder.WORKFLOW_RESULT, columnValue);

                    wfResult.setWfParams(limitedWfParams);

                } else {
                    wfResult.setWfParams(new HashMap<String, Object>());
                }

            }
            wfParams = null;

            cacheHandler.updateWorkflow(wfResult);
            duration = wfResult.getEndDate().getTime() - wfResult.getStartDate().getTime();
            nscsLogger.info("LogSuccessTaskHandler ready to update cache for wfResult [{}] duration [{}]", wfResult, duration);
        } else {
            nscsLogger.error("Null wfStatusId");
        }

        nscsInstrumentationService.updateSuccessfulWorkflow(workflowName, duration);
    }

}