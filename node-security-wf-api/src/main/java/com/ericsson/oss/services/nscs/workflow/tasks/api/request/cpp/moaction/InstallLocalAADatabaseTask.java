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

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowActionTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;

/**
 * Workflow task representing a request to install laad files on the node
 * Default WorkflowTaskType used as WorkflowTaskType.CPP_INSTALL_LAAD_ACTION
 *
 * Created by xkihari
 */
public class InstallLocalAADatabaseTask extends WorkflowActionTask {

    private static final long serialVersionUID = 6014761763376586773L;

    public static final String SHORT_DESCRIPTION = "Install LAAD";

    public InstallLocalAADatabaseTask() {
        super(WorkflowTaskType.CPP_INSTALL_LAAD_ACTION);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public InstallLocalAADatabaseTask(final String nodeName) {
        super(WorkflowTaskType.CPP_INSTALL_LAAD_ACTION, nodeName);
        setShortDescription(SHORT_DESCRIPTION);
    }

}