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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;

/**
 * <p>
 * Workflow task representing a request to read certEnrollState attribute of the node
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.CPP_READ_CERT_ENROLL_STATE
 * </p>
 *
 * @author emaynes
 */
public class ReadCertEnrollStateTask extends WorkflowQueryTask {

    private static final long serialVersionUID = 2605096800144534682L;

    public static final String SHORT_DESCRIPTION = "Read OAM certEnroll";

    public ReadCertEnrollStateTask() {
        super(WorkflowTaskType.CPP_READ_CERT_ENROLL_STATE);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public ReadCertEnrollStateTask(final String fdn) {
        super(WorkflowTaskType.CPP_READ_CERT_ENROLL_STATE, fdn);
        setShortDescription(SHORT_DESCRIPTION);
    }
}
