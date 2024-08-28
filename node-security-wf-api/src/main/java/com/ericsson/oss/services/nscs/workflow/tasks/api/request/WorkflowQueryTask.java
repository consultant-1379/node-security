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
 * Defines a branch of workflow tasks that retuns a value as result of the task processing
 * </p>
 *
 * @author emaynes on 13/06/2014.
 */
public class WorkflowQueryTask extends WorkFlowNodeTask {

    private static final long serialVersionUID = 2315399951743113846L;

    /**
     * WorkflowQueryTask constructor
     *
     * @param taskType
     *            the WorkflowTaskType of this WorkflowActionTask
     */
    public WorkflowQueryTask(final WorkflowTaskType taskType) {
        super(taskType);
    }

    /**
     * WorkflowQueryTask constructor
     */
    public WorkflowQueryTask() {
    }

    /**
     * WorkflowQueryTask constructor
     *
     * @param fdn
     *            FND of the target node
     */
    public WorkflowQueryTask(final String fdn) {
        super(fdn);
    }

    /**
     * WorkflowQueryTask constructor
     *
     * @param taskType
     *            the WorkflowTaskType of this WorkflowActionTask
     * @param fdn
     *            FND of the target node
     */
    public WorkflowQueryTask(final WorkflowTaskType taskType, final String fdn) {
        super(taskType, fdn);
    }
}
