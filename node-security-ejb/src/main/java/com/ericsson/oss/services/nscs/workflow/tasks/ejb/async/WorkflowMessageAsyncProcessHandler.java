package com.ericsson.oss.services.nscs.workflow.tasks.ejb.async;

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowActionTask;

/**
 * Implementation of AsyncProcessHandler which sends back a message to the workflow instance after task execution. This callback is used to notify the workflow instance if a async task has failed or
 * completed successfully
 * 
 * @author emaynes.
 */
public class WorkflowMessageAsyncProcessHandler implements AsyncProcessHandler<WorkflowActionTask> {

    private final String successMessage;
    private final String errorMessage;
    private final WorkflowHandler workflowHandler;

    /**
     * Creates a new instance of WorkflowMessageAsyncProcessHandler
     * 
     * @param workflowHandler
     *            WorkflowHandler implementation to be used to send the message to workflow
     * @param errorMessage
     *            the message to be sent to workflow in case the task execution has failed. No message will be sent if this is null or blank
     * @param successMessage
     *            the message to be sent to workflow in case the task execution was successful. No message will be sent if this is null or blank
     */
    public WorkflowMessageAsyncProcessHandler(final WorkflowHandler workflowHandler, final String errorMessage, final String successMessage) {
        this.workflowHandler = workflowHandler;
        this.errorMessage = errorMessage;
        this.successMessage = successMessage;
    }

    @Override
    public void onSuccess(final WorkflowActionTask task) {
        if (successMessage != null && successMessage.trim().length() > 0) {
            workflowHandler.dispatchMessage(task.getNode(), successMessage);
        }
    }

    @Override
    public void onError(final WorkflowActionTask task, final Exception e) {
        if (errorMessage != null && errorMessage.trim().length() > 0) {
            workflowHandler.dispatchMessage(task.getNode(), errorMessage);
        }
        if (e instanceof WorkflowTaskException) {
            throw (WorkflowTaskException) e;
        } else if (e != null) {
            throw new UnexpectedErrorException(e);
        }
    }
}
