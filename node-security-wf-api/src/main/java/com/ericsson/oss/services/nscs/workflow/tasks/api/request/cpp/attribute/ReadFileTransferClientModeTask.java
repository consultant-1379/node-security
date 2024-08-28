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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;

/**
 * <p>
 * Workflow task representing a request to read file transfer client mode of the node
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.CPP_READ_FILE_TRANSFER_CLIENT_MODE
 * </p>
 *
 * @author emaynes on 23/06/2014.
 */
public class ReadFileTransferClientModeTask extends WorkflowQueryTask {

    private static final long serialVersionUID = 2605096800144534682L;

    public static final String SHORT_DESCRIPTION = "Read ftpMode";

    public ReadFileTransferClientModeTask() {
        super(WorkflowTaskType.CPP_READ_FILE_TRANSFER_CLIENT_MODE);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public ReadFileTransferClientModeTask(final String fdn) {
        super(WorkflowTaskType.CPP_READ_FILE_TRANSFER_CLIENT_MODE, fdn);
        setShortDescription(SHORT_DESCRIPTION);
    }
}
