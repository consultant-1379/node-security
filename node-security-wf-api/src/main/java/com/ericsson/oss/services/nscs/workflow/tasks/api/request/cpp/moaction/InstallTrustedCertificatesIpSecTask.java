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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowActionTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;

/**
 * <p>
 * Workflow task representing a request to install trusted certificates for ipsec
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.CPP_INSTALL_TRUSTED_CERTIFICATE_IPSEC
 * </p>
 *
 * Created by emehsau
 */
public class InstallTrustedCertificatesIpSecTask extends WorkflowActionTask {

    private static final long serialVersionUID = -5831966970698294083L;

    /**
     * Key of the trustedCertCategory value in the map
     */
    public static final String CPP_TRUST_CERTS_KEY = "TRUST_CERTS";

    public static final String SHORT_DESCRIPTION = "Init IPSEC trustInstall";

    /**
     * Constructs InstallTrustedCertificatesTask. Sets the rollbackTimeout to default: IPSEC
     */
    public InstallTrustedCertificatesIpSecTask() {
        super(WorkflowTaskType.CPP_INSTALL_TRUSTED_CERTIFICATE_IPSEC);
        setValue(CPP_TRUST_CERTS_KEY, "");
        setShortDescription(SHORT_DESCRIPTION);
    }

    /**
     * Constructs InstallTrustedCertificatesTask.
     *
     * @param nodeFDN
     *            fdn of the NE
     * @param category
     *            of the Trust Store (See TrustedCertCategory class)
     * @throws IllegalArgumentException
     *             if the category String is not represents any TrustedCertCategory
     */
    public InstallTrustedCertificatesIpSecTask(final String nodeFDN, final String category) {
        super(WorkflowTaskType.CPP_INSTALL_TRUSTED_CERTIFICATE_IPSEC, nodeFDN);
        setValue(CPP_TRUST_CERTS_KEY, category);
        setShortDescription(SHORT_DESCRIPTION);
    }

    /**
     * Gets the trust certs.
     *
     * @return trust certS.
     */
    public String getTrustCerts() {
        return (String) getValue(CPP_TRUST_CERTS_KEY);
    }

    /**
     * Sets the trust certs.
     *
     * @param trustCerts
     *            trust certs.
     */
    public void setTrustCerts(final String trustCerts) {
        setValue(CPP_TRUST_CERTS_KEY, trustCerts);
    }

}
