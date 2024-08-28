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
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

/**
 * <p>
 * Workflow task representing a request to check if the trusted certs are already installed over the node
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.CPP_CHECK_TRUSTED_ALREADY_INSTALLED
 * </p>
 *
 * @author eanbuzz
 */
public class CheckTrustedAlreadyInstalledTask extends WorkflowQueryTask {

    private static final long serialVersionUID = 7559488085678034529L;

    public static final String CPP_TRUST_CERTS_KEY = WorkflowParameterKeys.TRUST_CERTS.toString(); // "TRUST_CERTS";

    public static final String SHORT_DESCRIPTION = "Check IPSEC trustInstall";

    /**
     * Key of the certificate authority value in the map
     */
    public static final String CPP_TRUSTED_CERTIFICATE_AUTHORITY_KEY = WorkflowParameterKeys.CERTIFICATE_AUTHORITY_ID.toString();

    public CheckTrustedAlreadyInstalledTask() {
        super(WorkflowTaskType.CPP_CHECK_TRUSTED_ALREADY_INSTALLED);
        setValue(CPP_TRUST_CERTS_KEY, "");
        setValue(CPP_TRUSTED_CERTIFICATE_AUTHORITY_KEY, "");
        setShortDescription(SHORT_DESCRIPTION);
    }

    public CheckTrustedAlreadyInstalledTask(final String fdn, final String trustCertPath, final String trustedCA) {
        super(WorkflowTaskType.CPP_CHECK_TRUSTED_ALREADY_INSTALLED, fdn);
        setValue(CPP_TRUST_CERTS_KEY, trustCertPath);
        setValue(CPP_TRUSTED_CERTIFICATE_AUTHORITY_KEY, trustedCA);
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

    /**
     * Gets the TrustedCertificateAuthority
     *
     * @return the TrustedCertificateAuthority
     */
    public final String getTrustedCertificateAuthority() {
        return (String) getValue(CPP_TRUSTED_CERTIFICATE_AUTHORITY_KEY);
    }

    /**
     * Sets the TrustedCertificateAuthority
     *
     * @param trustedCertificateAuthority
     *            TrustedCertificateAuthority
     */
    public void setTrustedCertificateAuthority(final String trustedCertificateAuthority) {
        setValue(CPP_TRUSTED_CERTIFICATE_AUTHORITY_KEY, trustedCertificateAuthority);
    }

}
