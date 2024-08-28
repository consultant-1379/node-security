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
 * Workflow task representing a request to get the laad files from the LAAD service and uploads them into SMRS.
 * Default WorkflowTaskType used as WorkflowTaskType.CPP_GET_LAAD_FILES
 *
 * @author xkihari
 */
public class CppGetLaadFilesTask extends WorkflowQueryTask {

    private static final long serialVersionUID = 1079295395308497205L;

    public static final String SHORT_DESCRIPTION = "Get LAAD";

    public CppGetLaadFilesTask() {
        super(WorkflowTaskType.CPP_GET_LAAD_FILES);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public CppGetLaadFilesTask(final String fdn) {
        super(WorkflowTaskType.CPP_GET_LAAD_FILES, fdn);
        setShortDescription(SHORT_DESCRIPTION);
    }

}
