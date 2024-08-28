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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request;

/**
 * <p>
 * Defines a branch of workflow tasks that do NOT generate (return) a result after the processing
 * </p>
 * Created by emaynes on 13/06/2014.
 */
public class WorkflowActionTask extends WorkFlowNodeTask {

    private static final long serialVersionUID = 6709322367479413990L;

    /**
     * WorkflowActionTask constructor
     * @param taskType the WorkflowTaskType of this WorkflowActionTask
     */
    public WorkflowActionTask(final WorkflowTaskType taskType) {
        super(taskType);
    }

    /**
     * WorkflowActionTask constructor
     */
    public WorkflowActionTask() {
    }

    /**
     * WorkflowActionTask constructor
     * @param fdn FND of the target node
     */
    public WorkflowActionTask(final String fdn) {
        super(fdn);
    }

    /**
     * WorkflowActionTask constructor
     * @param taskType the WorkflowTaskType of this WorkflowActionTask
     * @param fdn FND of the target node
     */
    public WorkflowActionTask(final WorkflowTaskType taskType, final String fdn) {
        super(taskType, fdn);
    }
}
