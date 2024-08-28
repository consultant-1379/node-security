/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2019
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;

/**
 * Workflow task representing a request to validate the Node sync status
 *
 * Default WorkflowTaskType used is WorkflowTaskType.CHECK_NODE_SYNC_STATUS
 *
 * @author xkihari
 */
public class CheckNodeSyncStatusTask extends WorkflowQueryTask {

    private static final long serialVersionUID = 8094123196974353855L;
    public static final String SHORT_DESCRIPTION = "Check Node Sync Status";

    public CheckNodeSyncStatusTask() {
        super(WorkflowTaskType.CHECK_NODE_SYNC_STATUS);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public CheckNodeSyncStatusTask(final String fdn) {
        super(WorkflowTaskType.CHECK_NODE_SYNC_STATUS, fdn);
        setShortDescription(SHORT_DESCRIPTION);
    }

}
