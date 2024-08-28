/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
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
 * Workflow task representing a request to cancel certificate enrollment process for ipsec
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.CPP_CANCEL_CERT_ENROLLMENT_IPSEC
 * </p>
 *
 * Created by emehsau
 */
@AsyncActionTask(errorMessage = "CppCancelCertEnrollmentTaskFailed")
public class CancelCertEnrollmentIpSecTask extends WorkflowActionTask {

    private static final long serialVersionUID = -8718584934135440867L;

    public static final String SHORT_DESCRIPTION = "Cancel IPSEC certEnroll";

    public CancelCertEnrollmentIpSecTask() {
        super(WorkflowTaskType.CPP_CANCEL_CERT_ENROLLMENT_IPSEC);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public CancelCertEnrollmentIpSecTask(final String fdn) {
        super(WorkflowTaskType.CPP_CANCEL_CERT_ENROLLMENT_IPSEC, fdn);
        setShortDescription(SHORT_DESCRIPTION);
    }
}
