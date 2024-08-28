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
 * Workflow task representing a request to read trustedCertificateInstallationFailure attribute of the node
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.CPP_READ_TRUSTED_CERTIFICATE_INTALLATION_FAILURE
 * </p>
 *
 * @author emaynes
 */
public class ReadTrustedCertificateInstallationFailureTask extends WorkflowQueryTask {

    private static final long serialVersionUID = 2605096800144534682L;

    public static final String SHORT_DESCRIPTION = "Read OAM trustInstall";

    public ReadTrustedCertificateInstallationFailureTask() {
        super(WorkflowTaskType.CPP_READ_TRUSTED_CERTIFICATE_INTALLATION_FAILURE);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public ReadTrustedCertificateInstallationFailureTask(final String fdn) {
        super(WorkflowTaskType.CPP_READ_TRUSTED_CERTIFICATE_INTALLATION_FAILURE, fdn);
        setShortDescription(SHORT_DESCRIPTION);
    }
}
