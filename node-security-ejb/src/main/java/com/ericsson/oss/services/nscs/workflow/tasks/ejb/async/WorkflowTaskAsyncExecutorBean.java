package com.ericsson.oss.services.nscs.workflow.tasks.ejb.async;

import com.ericsson.nms.security.nscs.workflow.task.WFActionTaskHandler;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowActionTask;
import org.slf4j.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * Bean to execute a workflow task handler asynchronously
 * @author emaynes.
 */
@Stateless
public class WorkflowTaskAsyncExecutorBean {

    @Inject
    private Logger log;

    /**
     * Executes the given workflow handler with the give task instance asynchronously.
     * @param workflowHandler WFActionTaskHandler implementation to be executed
     * @param task WorkflowActionTask implementation to be passed to the workflowHandler
     * @param asyncProcessHandler if present, this callback will be executed after the workflow handler has completed.
     */
    @Asynchronous
    public void processTaskAsync(final WFActionTaskHandler<WorkflowActionTask> workflowHandler, final WorkflowActionTask task, final AsyncProcessHandler asyncProcessHandler){

        try {
            log.debug("Invoking Task handler [{}] inside async executor", workflowHandler.getClass().getName());
            workflowHandler.processTask(task);
            log.debug("Task handler [{}] finished successfully", workflowHandler.getClass().getName());
            if ( asyncProcessHandler != null ) {
                asyncProcessHandler.onSuccess(task);
            }
        } catch (Exception e) {
            log.warn("Error during processTaskAsync execution", e);
            if ( asyncProcessHandler != null ) {
                asyncProcessHandler.onError(task, e);
            }
        }
    }
}
