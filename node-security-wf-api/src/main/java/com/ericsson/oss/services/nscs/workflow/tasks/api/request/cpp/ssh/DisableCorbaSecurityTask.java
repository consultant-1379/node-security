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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.ssh;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowActionTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;

/**
 * <p>
 * Workflow task representing a request to disable corba security on a node
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.CPP_DISABLE_CORBA_SECURITY
 * </p>
 *
 * Created by emaynes on 16/06/2014.
 */
public class DisableCorbaSecurityTask extends WorkflowActionTask {

    private static final long serialVersionUID = 8142547843064116930L;

    public static final String SHORT_DESCRIPTION = "Disable CORBA security";

    public DisableCorbaSecurityTask() {
        super(WorkflowTaskType.CPP_DISABLE_CORBA_SECURITY);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public DisableCorbaSecurityTask(final String fdn) {
        super(WorkflowTaskType.CPP_DISABLE_CORBA_SECURITY, fdn);
        setShortDescription(SHORT_DESCRIPTION);
    }
}
