/*-----------------------------------------------------------------------------
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
package com.ericsson.nms.security.nscs.workflow.task.cpp.moaction;

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.timer.IntervalJobAction;
import com.ericsson.nms.security.nscs.timer.IntervalJobService;
import com.ericsson.nms.security.nscs.workflow.task.WFActionTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.nms.security.nscs.workflow.task.cpp.WFMessageConstants;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.TestDoSomethingTask;

import javax.ejb.Local;
import javax.inject.Inject;
import java.util.Map;

/**
 * Task handler for WorkflowTaskType.TEST_DO_SOMETHING
 *
 * Workflow task handler representing a request to perform a generic action.
 *
 */
@WFTaskType(WorkflowTaskType.TEST_DO_SOMETHING)
@Local(WFTaskHandlerInterface.class)
public class TestDoSomethingTaskHandler implements WFActionTaskHandler<TestDoSomethingTask>, WFTaskHandlerInterface {

    private static final int POLL_INTERVAL = 100;
    private static final int FIRST_POLL_DELAY = 100;
    private static final int POLL_TIMES = 1;

    @Inject
    private NscsLogger logger;

    @EServiceRef
    private IntervalJobService intervalJob;

    @Override
    public void processTask(final TestDoSomethingTask task) {
        logger.workFlowTaskHandlerStarted(task);
        final NodeReference nodeRef = new NodeRef(task.getNodeFdn());
        final String actionResult = task.getActionResult();
        if (("SUCCESS".equals(actionResult)) || ("FAILURE".equals(actionResult))) {
            intervalJob.createIntervalJob(FIRST_POLL_DELAY, POLL_INTERVAL, POLL_TIMES, new DoSomethingIntervalJob(nodeRef, logger, task));
            logger.workFlowTaskHandlerFinishedWithSuccess(task, null);
        } else if ("TIMEOUT".equals(actionResult)) {
            logger.workFlowTaskHandlerFinishedWithSuccess(task, null);
        } else {
            logger.workFlowTaskHandlerFinishedWithError(task, actionResult);
            throw new UnexpectedErrorException(actionResult);
        }
    }

    public static class DoSomethingIntervalJob implements IntervalJobAction {

        private final NodeReference nodeRef;
        private final NscsLogger log;
        private final TestDoSomethingTask task;

        public DoSomethingIntervalJob(final NodeReference node, final NscsLogger nscsLogger, final TestDoSomethingTask task) {
            this.nodeRef = node;
            this.log = nscsLogger;
            this.task = task;
        }

        @Override
        public boolean doAction(final Map<JobActionParameters, Object> params) {
            final WorkflowHandler handler = (WorkflowHandler) params.get(JobActionParameters.WORKFLOW_HANDLER);
            final String actionResult = task.getActionResult();
            log.info("The action result is {}", actionResult);
            if ("SUCCESS".equals(actionResult)) {
                handler.dispatchMessage(nodeRef, WFMessageConstants.TEST_ACTION_SUCCESS);
                return true;
            } else if ("FAILURE".equals(actionResult)) {
                handler.dispatchMessage(nodeRef, WFMessageConstants.TEST_ACTION_FAILED);
                return true;
            } else {
                return false;
            }
        }
    }

}
