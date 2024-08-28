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

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.AsyncActionTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowActionTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;

/**
 * <p>
 * Workflow task representing a request to cancel certificate enrollment process
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.CPP_CANCEL_CERT_ENROLLMENT
 * </p>
 *
 * @author emaynes
 */
@AsyncActionTask(errorMessage = "CppCancelCertEnrollmentTaskFailed")
public class CancelCertEnrollmentTask extends WorkflowActionTask {

    private static final long serialVersionUID = -3994403220435537069L;

    public static final String SHORT_DESCRIPTION = "Cancel OAM certEnroll";

    public CancelCertEnrollmentTask() {
        super(WorkflowTaskType.CPP_CANCEL_CERT_ENROLLMENT);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public CancelCertEnrollmentTask(final String fdn) {
        super(WorkflowTaskType.CPP_CANCEL_CERT_ENROLLMENT, fdn);
        setShortDescription(SHORT_DESCRIPTION);
    }
}
