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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowActionTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;

/**
 * <p>
 * Workflow task representing a request to delete the files from SMRS.
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.CPP_DELETE_FILES_SMRS
 * </p>
 *
 * Created by eanbuzz.
 */
public class DeleteFilesSmrsTask extends WorkflowActionTask {

    private static final long serialVersionUID = -6993725360698535423L;

    public static final String SHORT_DESCRIPTION = "Delete SMRS files";

    public DeleteFilesSmrsTask() {
        super(WorkflowTaskType.CPP_DELETE_FILES_SMRS);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public DeleteFilesSmrsTask(final String fdn) {
        super(WorkflowTaskType.CPP_DELETE_FILES_SMRS, fdn);
        setShortDescription(SHORT_DESCRIPTION);
    }
}
