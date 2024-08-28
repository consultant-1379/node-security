/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
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
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

public class IssueInitTrustedCertIpSecEnrollmentTask extends WorkflowActionTask {

    private static final long serialVersionUID = 1303184846984824337L;

    /**
     * Key of the trust cert value in the map
     */
    public static final String CPP_TRUST_CERTS_KEY = WorkflowParameterKeys.TRUST_CERTS.toString();

    /**
     * Key of the certificate authority value in the map
     */
    public static final String CPP_TRUSTED_CERTIFICATE_AUTHORITY_KEY = WorkflowParameterKeys.CERTIFICATE_AUTHORITY_ID.toString();

    public static final String SHORT_DESCRIPTION = "Init IPSEC trustInstall";

    /**
     * Constructs IssueInitTrustedCertIpSecEnrollmentTask.
     */
    public IssueInitTrustedCertIpSecEnrollmentTask() {
        super(WorkflowTaskType.CPP_ISSUE_TRUSTED_CERTIFICATE_IPSEC);
        setValue(CPP_TRUST_CERTS_KEY, "");
        setValue(CPP_TRUSTED_CERTIFICATE_AUTHORITY_KEY, "");
        setShortDescription(SHORT_DESCRIPTION);
    }

    /**
     * Constructs IssueInitTrustedCertIpSecEnrollmentTask.
     *
     * @param nodeFDN
     *            fdn of the NE
     * @param trustCert
     *            file or category
     * @param ca
     *            trusted Certificate Authority
     *
     */
    public IssueInitTrustedCertIpSecEnrollmentTask(final String nodeFDN, final String trustCert, final String ca) {
        super(WorkflowTaskType.CPP_ISSUE_TRUSTED_CERTIFICATE_IPSEC, nodeFDN);
        setValue(CPP_TRUST_CERTS_KEY, trustCert);
        setValue(CPP_TRUSTED_CERTIFICATE_AUTHORITY_KEY, ca);
        setShortDescription(SHORT_DESCRIPTION);
    }

    /**
     * Gets the trust certs file.
     *
     * @return trust certs file.
     */
    public String getTrustCerts() {
        return (String) getValue(CPP_TRUST_CERTS_KEY);
    }

    /**
     * Sets the trust certs file.
     *
     * @param trustCerts the trustCerts
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
