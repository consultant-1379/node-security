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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;

/**
 * <p>
 * Workflow task representing a request to read certEnrollState attribute of the node
 * </p>
 * <p>
 * Default WorkflowTaskType used is {@link WorkflowTaskType#CPP_READ_CERT_ENROLL_IPSEC_STATE}
 * </p>
 *
 * @author emehsau
 */
public class ReadCertEnrollStateIpSecTask extends WorkflowQueryTask {

    private static final long serialVersionUID = -4029086760643833481L;

    public static final String SHORT_DESCRIPTION = "Read IPSEC certEnroll";

    public ReadCertEnrollStateIpSecTask() {
        super(WorkflowTaskType.CPP_READ_CERT_ENROLL_IPSEC_STATE);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public ReadCertEnrollStateIpSecTask(final String fdn) {
        super(WorkflowTaskType.CPP_READ_CERT_ENROLL_IPSEC_STATE, fdn);
        setShortDescription(SHORT_DESCRIPTION);
    }
}
