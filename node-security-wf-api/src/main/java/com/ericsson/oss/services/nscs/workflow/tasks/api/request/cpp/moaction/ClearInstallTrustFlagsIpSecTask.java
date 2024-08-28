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
 * Workflow task representing a request to clean the install trust flag for ipsec
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.CPP_CLEAR_INSTALL_TRUST_IPSEC_FLAGS
 * </p>
 *
 * Created by emehsau
 */
@AsyncActionTask(errorMessage = "CppIpSecTrustInstalltionTaskFailed")
public class ClearInstallTrustFlagsIpSecTask extends WorkflowActionTask {

    private static final long serialVersionUID = 5481778635703451453L;

    public static final String SHORT_DESCRIPTION = "Cancel IPSEC trustInstall";

    public ClearInstallTrustFlagsIpSecTask() {
        super(WorkflowTaskType.CPP_CLEAR_INSTALL_TRUST_IPSEC_FLAGS);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public ClearInstallTrustFlagsIpSecTask(final String fdn) {
        super(WorkflowTaskType.CPP_CLEAR_INSTALL_TRUST_IPSEC_FLAGS, fdn);
        setShortDescription(SHORT_DESCRIPTION);
    }
}
