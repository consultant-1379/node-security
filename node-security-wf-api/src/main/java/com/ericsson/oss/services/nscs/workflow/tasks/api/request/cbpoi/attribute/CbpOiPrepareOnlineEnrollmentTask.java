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
 * Workflow task representing a request to prepare the action for online enrollment for the given CBP OI (EOI YANG based) node.
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.CBP_OI_PREPARE_ONLINE_ENROLLMENT
 * </p>
 */
public class CbpOiPrepareOnlineEnrollmentTask extends EnrollmentWorkflowQueryTask {

    private static final long serialVersionUID = -4395006064792531036L;

    /**
     * Short description of the task.
     */
    public static final String SHORT_DESCRIPTION = "Prepare online enroll";

    public CbpOiPrepareOnlineEnrollmentTask() {
        super(WorkflowTaskType.CBP_OI_PREPARE_ONLINE_ENROLLMENT, SHORT_DESCRIPTION);
    }

    public CbpOiPrepareOnlineEnrollmentTask(final String fdn) {
        super(WorkflowTaskType.CBP_OI_PREPARE_ONLINE_ENROLLMENT, SHORT_DESCRIPTION, fdn);
    }
}
