/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2020
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.cbpoi.attribute;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.EnrollmentWorkflowQueryTask;

/**
 * <p>
 * Workflow task representing a request to restore the renewal mode to original value present before online enrollment for the given CBP OI (EOI YANG
 * based) node.
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.CBP_OI_RESTORE_RENEWAL_MODE
 * </p>
 */
public class CbpOiRestoreRenewalModeTask extends EnrollmentWorkflowQueryTask {

    private static final long serialVersionUID = -7376132768791841557L;

    /**
     * Short description of the task.
     */
    public static final String SHORT_DESCRIPTION = "Restore renewal";

    public CbpOiRestoreRenewalModeTask() {
        super(WorkflowTaskType.CBP_OI_RESTORE_RENEWAL_MODE, SHORT_DESCRIPTION);
    }

    public CbpOiRestoreRenewalModeTask(final String fdn) {
        super(WorkflowTaskType.CBP_OI_RESTORE_RENEWAL_MODE, SHORT_DESCRIPTION, fdn);
    }
}
