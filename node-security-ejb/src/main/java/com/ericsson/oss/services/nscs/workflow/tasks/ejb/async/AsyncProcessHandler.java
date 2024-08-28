package com.ericsson.oss.services.nscs.workflow.tasks.ejb.async;


import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowActionTask;

/**
 * Interface defines a callback to be executed after a async WorkflowActionTask
 * has completed.
 * @author emaynes.
 */
public interface AsyncProcessHandler <T extends WorkflowActionTask> {

    /**
     * This method will be called if task execution has completed without
     * throwing any exceptions.
     * @param task the WorkflowActionTask used during the task execution
     */
    void onSuccess(T task);

    /**
     * This method will be called if a exception was thrown during task execution.
     * @param task the WorkflowActionTask used during the task execution
     * @param e exception thrown
     */
    void onError(T task, Exception e);
}
