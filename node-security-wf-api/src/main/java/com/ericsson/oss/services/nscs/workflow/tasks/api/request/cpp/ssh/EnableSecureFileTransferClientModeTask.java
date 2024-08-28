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
 * Workflow task representing a request to disable secure file transfer mode
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.CPP_ENABLE_SECURE_FILE_TRANSFER_CLIENT_MODE
 * </p>
 *
 * Created by emaynes on 23/06/2014.
 */
public class EnableSecureFileTransferClientModeTask extends WorkflowActionTask {

    private static final long serialVersionUID = -9216266275945000585L;

    public static final String SHORT_DESCRIPTION = "Enable SFTP";

    public EnableSecureFileTransferClientModeTask() {
        super(WorkflowTaskType.CPP_ENABLE_SECURE_FILE_TRANSFER_CLIENT_MODE);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public EnableSecureFileTransferClientModeTask(final String fdn) {
        super(WorkflowTaskType.CPP_ENABLE_SECURE_FILE_TRANSFER_CLIENT_MODE, fdn);
        setShortDescription(SHORT_DESCRIPTION);
    }
}
