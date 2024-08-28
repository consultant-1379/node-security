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
 * Workflow task representing a request to read trustedCertificateInstallationIpSecState attribute of the node
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.CPP_READ_TRUSTED_CERTIFICATE_IPSEC_INSTALLATION_STATE
 * </p>
 *
 * @author emehsau
 */
public class ReadTrustedCertificateInstallationIpSecStateTask extends WorkflowQueryTask {

    private static final long serialVersionUID = 3247854043637754689L;

    public static final String SHORT_DESCRIPTION = "Read IPSEC trustInstall";

    public ReadTrustedCertificateInstallationIpSecStateTask() {
        super(WorkflowTaskType.CPP_READ_TRUSTED_CERTIFICATE_IPSEC_INSTALLATION_STATE);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public ReadTrustedCertificateInstallationIpSecStateTask(final String fdn) {
        super(WorkflowTaskType.CPP_READ_TRUSTED_CERTIFICATE_IPSEC_INSTALLATION_STATE, fdn);
        setShortDescription(SHORT_DESCRIPTION);
    }
}
