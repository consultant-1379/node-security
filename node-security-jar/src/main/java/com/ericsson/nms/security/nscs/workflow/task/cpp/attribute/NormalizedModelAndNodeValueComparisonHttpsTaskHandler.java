/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
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

import com.ericsson.nms.security.nscs.handler.command.utility.WebServerStatus;
import com.ericsson.nms.security.nscs.workflow.task.WFActionTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.NormalizedModelAndNodeValueComparisonHttpsTask;

/**
 * 
 * @author edudluk
 *
 */
@WFTaskType(WorkflowTaskType.CPP_COMPARE_HTTPS)
@Local(WFTaskHandlerInterface.class)
public class NormalizedModelAndNodeValueComparisonHttpsTaskHandler
        implements WFActionTaskHandler<NormalizedModelAndNodeValueComparisonHttpsTask>, WFTaskHandlerInterface {

    @Inject
    private NormalizedModelAndNodeValueComparisonTaskHandler utilityHandler;

    @Override
    public void processTask(NormalizedModelAndNodeValueComparisonHttpsTask task) {
        utilityHandler.processTask(task, WebServerStatus.HTTPS);

    }

}
