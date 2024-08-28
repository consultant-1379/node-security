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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.pki;

import java.io.Serializable;
import java.util.Map;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

public class RevokeNodeCertificateTask extends WorkflowQueryTask {

    private static final long serialVersionUID = 3676644956915565333L;

    public static final String CERTIFICATE_ID = WorkflowParameterKeys.CERTIFICATE_ID.toString();
    public static final String CERTIFICATE_AUTHORITY_ID = WorkflowParameterKeys.CERTIFICATE_AUTHORITY_ID.toString();
    public static final String TRUSTED_CATEGORY_KEY = WorkflowParameterKeys.TRUSTED_CATEGORY.toString();
    public static final String CERTIFICATE_ENROLLMENT_CA = WorkflowParameterKeys.CERTIFICATE_ENROLLMENT_CA.toString();
    public static final String REVOCATION_REASON = WorkflowParameterKeys.REVOCATION_REASON.toString();
    public static final String OUTPUT_PARAMS_KEY = WorkflowParameterKeys.OUTPUT_PARAMS.toString();

    public static final String SHORT_DESCRIPTION = "Revoke cert";

    public RevokeNodeCertificateTask() {
        super(WorkflowTaskType.REVOKE_NODE_CERTIFICATE);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public RevokeNodeCertificateTask(final String fdn) {
        super(WorkflowTaskType.REVOKE_NODE_CERTIFICATE, fdn);
        setValue(CERTIFICATE_ID, "");
        setValue(CERTIFICATE_AUTHORITY_ID, "");
        setValue(REVOCATION_REASON, "");
        setShortDescription(SHORT_DESCRIPTION);
    }

    /**
     * Gets the certificate id.
     * 
     * @return certificate id.
     */
    public String getCertificateId() {
        return (String) getValue(CERTIFICATE_ID);
    }

    /**
     * Sets the certificate id.
     * 
     * @param certificateId the certificateId
     */
    public void setCertificateId(final String certificateId) {
        setValue(CERTIFICATE_ID, certificateId);
    }

    /**
     * Gets the certificate Authority id.
     * 
     * @return certificate Authority id.
     */
    public String getCertificateAuthorityId() {
        return (String) getValue(CERTIFICATE_AUTHORITY_ID);
    }

    /**
     * Sets the certificate Authority id.
     * 
     * @param certificateAuthorityId the certificateAuthorityId
     */
    public void setCertificateAuthorityId(final String certificateAuthorityId) {
        setValue(CERTIFICATE_AUTHORITY_ID, certificateAuthorityId);
    }

    /**
     * @return the trustedCertCategory
     */
    public String getTrustedCertCategory() {
        return (String) getValue(TRUSTED_CATEGORY_KEY);
    }

    /**
     * @param trustedCertCategory
     *            the trustedCertCategory to set
     */
    public void setTrustedCertCategory(final String trustedCertCategory) {
        setValue(TRUSTED_CATEGORY_KEY, trustedCertCategory);
    }

    /**
     * @return the certificateEnrollmentCa
     */
    public String getCertificateEnrollmentCa() {
        return (String) getValue(CERTIFICATE_ENROLLMENT_CA);
    }

    /**
     * @param certificateEnrollmentCa
     *            the certificateEnrollmentCa to set
     */
    public void setCertificateEnrollmentCa(final String certificateEnrollmentCa) {
        setValue(CERTIFICATE_ENROLLMENT_CA, certificateEnrollmentCa);
    }

    /**
     * Gets the RevocationReason.
     * 
     * @return RevocationReason.
     */
    public String getRevocationReason() {
        return (String) getValue(REVOCATION_REASON);
    }

    /**
     * Sets the RevocationReason.
     * 
     * @param revocationReason the revocationReason
     */
    public void setRevocationReason(final String revocationReason) {
        setValue(REVOCATION_REASON, revocationReason);
    }

    /**
     * @return the outputParams
     */
    @SuppressWarnings("unchecked")
    public Map<String, Serializable> getOutputParams() {
        return (Map<String, Serializable>) getValue(OUTPUT_PARAMS_KEY);
    }

    /**
     * @param outputParams
     *            the outputParams to set
     */
    public void setOutputParams(final Map<String, Serializable> outputParams) {
        setValue(OUTPUT_PARAMS_KEY, outputParams);
    }

}
