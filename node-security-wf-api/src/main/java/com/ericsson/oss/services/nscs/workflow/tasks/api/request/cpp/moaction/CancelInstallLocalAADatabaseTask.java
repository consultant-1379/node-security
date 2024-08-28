/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.AsyncActionTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowActionTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;

/**
 * Workflow task representing a request to clear the localAADatabaseInstallationFailure flag
 * Default WorkflowTaskType used as WorkflowTaskType.CPP_CANCEL_INSTAL_LAAD_DATABASE_FAILURE
 *
 * Created by xkihari
 */
@AsyncActionTask(errorMessage = "CppCancelInstallLocalaaDatabaseTaskFailed")
public class CancelInstallLocalAADatabaseTask extends WorkflowActionTask {

    private static final long serialVersionUID = -3567064510131862049L;
    public static final String SHORT_DESCRIPTION = "Cancel LAAD";

    public CancelInstallLocalAADatabaseTask() {
        super(WorkflowTaskType.CPP_CANCEL_INSTALL_LAAD_FAILURE);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public CancelInstallLocalAADatabaseTask(final String fdn) {
        super(WorkflowTaskType.CPP_CANCEL_INSTALL_LAAD_FAILURE, fdn);
        setShortDescription(SHORT_DESCRIPTION);
    }

}
