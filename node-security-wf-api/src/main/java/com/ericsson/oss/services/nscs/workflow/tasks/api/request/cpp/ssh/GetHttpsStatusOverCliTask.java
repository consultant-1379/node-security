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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.ssh;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowActionTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;

/**
 * <p>
 * Workflow task representing request to get HTTPS status from the node.
 * </p>
 * 
 * @author edudluk
 *
 */
public class GetHttpsStatusOverCliTask extends WorkflowActionTask {

    private static final long serialVersionUID = -4978059729233681690L;

    public static final String SHORT_DESCRIPTION = "Get HTTPS status over CORBA";

    public GetHttpsStatusOverCliTask() {
        super(WorkflowTaskType.GET_HTTPS_STATUS_CLI);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public GetHttpsStatusOverCliTask(final String fdn) {
        super(WorkflowTaskType.GET_HTTPS_STATUS_CLI, fdn);
        setShortDescription(SHORT_DESCRIPTION);
    }

}
