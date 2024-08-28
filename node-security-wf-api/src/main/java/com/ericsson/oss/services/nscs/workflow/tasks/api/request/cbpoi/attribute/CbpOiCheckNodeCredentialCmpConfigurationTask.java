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
 * Workflow task representing a request to perform a check on the node credential CMP enrollment configuration on the given CBP OI (EOI YANG based)
 * node.
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.CBP_OI_CHECK_NODE_CREDENTIAL_CMP_CONFIG
 * </p>
 */
public class CbpOiCheckNodeCredentialCmpConfigurationTask extends EnrollmentWorkflowQueryTask {

    private static final long serialVersionUID = -4395006064792531036L;

    /**
     * Short description of the task.
     */
    public static final String SHORT_DESCRIPTION = "Check NC CMP config";

    public CbpOiCheckNodeCredentialCmpConfigurationTask() {
        super(WorkflowTaskType.CBP_OI_CHECK_NODE_CREDENTIAL_CMP_CONFIG, SHORT_DESCRIPTION);
    }

    public CbpOiCheckNodeCredentialCmpConfigurationTask(final String fdn) {
        super(WorkflowTaskType.CBP_OI_CHECK_NODE_CREDENTIAL_CMP_CONFIG, SHORT_DESCRIPTION, fdn);
    }
}
