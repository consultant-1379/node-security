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
 * Workflow task representing a request to clean the installtrust flag
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.CPP_CLEAR_INSTALL_TRUST_FLAGS
 * </p>
 *
 * Created by emaynes on 24/06/2014.
 */
@AsyncActionTask(errorMessage = "CppClearInstallTrustFlagsTaskFailed")
public class ClearInstallTrustFlagsTask extends WorkflowActionTask {

    private static final long serialVersionUID = 2926116702923576489L;

    public static final String SHORT_DESCRIPTION = "Cancel OAM trustInstall";

    public ClearInstallTrustFlagsTask() {
        super(WorkflowTaskType.CPP_CLEAR_INSTALL_TRUST_FLAGS);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public ClearInstallTrustFlagsTask(final String fdn) {
        super(WorkflowTaskType.CPP_CLEAR_INSTALL_TRUST_FLAGS, fdn);
        setShortDescription(SHORT_DESCRIPTION);
    }
}
