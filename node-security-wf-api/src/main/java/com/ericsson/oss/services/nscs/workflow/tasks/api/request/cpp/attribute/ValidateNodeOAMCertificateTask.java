/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
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
 * Work flow task to check whether the node has a valid certificate issued by ENM CA.
 * </p>
 * 
 * @author tcsnapa
 */
public class ValidateNodeOAMCertificateTask extends WorkflowQueryTask {

    private static final long serialVersionUID = 2281684217694297310L;
    public static final String SHORT_DESCRIPTION = "Validate node's OAM cert";

    public ValidateNodeOAMCertificateTask() {
        super(WorkflowTaskType.CPP_VALIDATE_NODE_OAM_CERTIFICATE);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public ValidateNodeOAMCertificateTask(final String fdn) {
        super(WorkflowTaskType.CPP_VALIDATE_NODE_OAM_CERTIFICATE, fdn);
        setShortDescription(SHORT_DESCRIPTION);
    }
}
