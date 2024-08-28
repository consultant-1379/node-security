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

import java.lang.annotation.*;

import javax.inject.Qualifier;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;

/**
 * <p>
 * Annotation used by implementations of {@link com.ericsson.nms.security.nscs.workflow.task.WFTaskHandler} to specify to which
 * {@link com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType} the handler is associated to.
 * </p>
 * @author emaynes on 16/06/2014.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
@Documented
public @interface WFTaskType {
    WorkflowTaskType value();
}
