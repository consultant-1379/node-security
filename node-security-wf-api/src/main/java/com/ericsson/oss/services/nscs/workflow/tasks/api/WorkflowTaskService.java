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
package com.ericsson.oss.services.nscs.workflow.tasks.api;

import com.ericsson.oss.itpf.sdk.core.annotation.EService;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowActionTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;

import javax.ejb.Remote;

/**
 * <p>Main interface to provide services to the workflow layer.</p>
 * <p>This interface expects to receive task request which are instances of
 * WorkflowActionTask, WorkflowQueryTask or subclasses of then. WorkflowActionTask or subclasses
 * of it are interpreted as task that do not have a return value, on the other hand WorkflowQueryTask or subclasses
 * of it are tasks that provided a result to the caller.</p>
 * @author emaynes
 * @see com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTask
 */
@Remote
@EService
public interface WorkflowTaskService {

    /**
     * Start processing the requested task from the workflow service
     * 
     * @param task
     *            WorkflowActionTask instance of sub-class instance
     * @throws com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException
     *             or subclass in case of error during task processing.
     */
    void processTask(WorkflowActionTask task);

    /**
     * Start processing the requested task from the workflow service
     * 
     * @param task
     *            WorkflowQueryTask instance of sub-class instance
     * @return String with the result value from the processing
     * @throws com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException
     *             or subclass in case of error during task processing.
     */
    String processTask(WorkflowQueryTask task);
}
