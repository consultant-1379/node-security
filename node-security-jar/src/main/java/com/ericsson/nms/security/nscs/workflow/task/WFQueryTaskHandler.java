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
package com.ericsson.nms.security.nscs.workflow.task;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;

/**
 * <p>
 * Branch of {@link com.ericsson.nms.security.nscs.workflow.task.WFTaskHandler} that defines a task handler capable returning a result.
 * </p>
 * @author emaynes on 16/06/2014.
 */
public interface WFQueryTaskHandler<T extends WorkflowQueryTask> extends WFTaskHandler<T> {
    /**
     * Method responsible to perform the task received
     * @param task WorkflowActionTask or subclass instance to be performed
     * @return String with the result of the task processing
     */
    String processTask(T task);
}
