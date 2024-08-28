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

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTask;

/**
 * <p>
 * Interface that defines a task handler responsible of actually performing the task required by a WorkflowTaskType
 * </p>
 * @author emaynes on 16/06/2014.
 */
public interface WFTaskHandler<T extends WorkflowTask> extends WFTaskHandlerInterface {
}
