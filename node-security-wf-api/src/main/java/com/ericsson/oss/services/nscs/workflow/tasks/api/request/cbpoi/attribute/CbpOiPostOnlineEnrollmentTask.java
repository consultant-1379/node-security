/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2021
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
 * Workflow task representing a request to perform needed operations after a successful action for online enrollment for the given CBP OI (EOI YANG
 * based) node.
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.CBP_OI_POST_ONLINE_ENROLLMENT
 * </p>
 */
public class CbpOiPostOnlineEnrollmentTask extends EnrollmentWorkflowQueryTask {

    private static final long serialVersionUID = 7578636609146021269L;

    /**
     * Short description of the task.
     */
    public static final String SHORT_DESCRIPTION = "Post online enroll";

    public CbpOiPostOnlineEnrollmentTask() {
        super(WorkflowTaskType.CBP_OI_POST_ONLINE_ENROLLMENT, SHORT_DESCRIPTION);
    }

    public CbpOiPostOnlineEnrollmentTask(final String fdn) {
        super(WorkflowTaskType.CBP_OI_POST_ONLINE_ENROLLMENT, SHORT_DESCRIPTION, fdn);
    }
}
