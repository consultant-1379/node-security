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
package com.ericsson.nms.security.nscs.workflow.task.cpp.attribute;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskFailureException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskTimeoutException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.TestCheckSomethingTask;

/**
 * Task handler for WorkflowTaskType.TEST_CHECK_SOMETHING
 *
 * Workflow task handler representing a request to perform a generic query.
 *
 */
@WFTaskType(WorkflowTaskType.TEST_CHECK_SOMETHING)
@Local(WFTaskHandlerInterface.class)
public class TestCheckSomethingTaskHandler implements WFQueryTaskHandler<TestCheckSomethingTask>, WFTaskHandlerInterface {

    @Inject
    private NscsLogger logger;

    @Override
    public String processTask(final TestCheckSomethingTask task) {
        logger.workFlowTaskHandlerStarted(task);

        final String checkResult = task.getCheckResult();
        if ("CHECK_OK".equals(checkResult)) {
            logger.workFlowTaskHandlerFinishedWithSuccess(task, null);
        } else if ("THROW_TIMEOUT".equals(checkResult)) {
            logger.workFlowTaskHandlerFinishedWithError(task, checkResult);
            throw new WorkflowTaskTimeoutException(checkResult);
        } else if ("THROW_FAILURE".equals(checkResult)) {
            logger.workFlowTaskHandlerFinishedWithError(task, checkResult);
            throw new WorkflowTaskFailureException(checkResult);
        } else if ("THROW_ERROR".equals(checkResult)) {
            logger.workFlowTaskHandlerFinishedWithError(task, checkResult);
            throw new UnexpectedErrorException(checkResult);
        } else {
            // success but returns a result different from CHECK_OK
            logger.workFlowTaskHandlerFinishedWithSuccess(task, checkResult);
        }
        return checkResult;
    }

}
