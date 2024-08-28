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

public class DeactivateHttpsOnNodeTask extends WorkflowActionTask {

    private static final long serialVersionUID = 3720399275355734146L;

    public static final String SHORT_DESCRIPTION = "CORBA Disable https";

    public DeactivateHttpsOnNodeTask() {
        super(WorkflowTaskType.CPP_DISABLE_HTTPS);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public DeactivateHttpsOnNodeTask(final String fdn) {
        super(WorkflowTaskType.CPP_DISABLE_HTTPS, fdn);
        setShortDescription(SHORT_DESCRIPTION);
    }
}
