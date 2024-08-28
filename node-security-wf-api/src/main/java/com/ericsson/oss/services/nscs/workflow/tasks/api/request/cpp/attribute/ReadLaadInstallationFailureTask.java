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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;

/**
 * Workflow task representing a request to read localAADatabaseInstallationFailure attribute of the node.
 * Default WorkflowTaskType used as WorkflowTaskType.CPP_READ_LOCALAADATABASE_INSTALLATION_FAILURE
 *
 * @author xkihari
 */
public class ReadLaadInstallationFailureTask extends WorkflowQueryTask {

    private static final long serialVersionUID = 8094123196974353855L;
    public static final String SHORT_DESCRIPTION = "Read LAAD";

    public ReadLaadInstallationFailureTask() {
        super(WorkflowTaskType.CPP_READ_LAAD_INSTALLATION_FAILURE);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public ReadLaadInstallationFailureTask(final String fdn) {
        super(WorkflowTaskType.CPP_READ_LAAD_INSTALLATION_FAILURE, fdn);
        setShortDescription(SHORT_DESCRIPTION);
    }

}
