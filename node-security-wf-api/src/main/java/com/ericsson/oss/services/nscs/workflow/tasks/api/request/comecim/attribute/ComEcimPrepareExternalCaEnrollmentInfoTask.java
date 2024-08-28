/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute;

import java.io.Serializable;
import java.util.Map;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

/**
 * <p>
 * Workflow task type to prepare enrollment Information using the External CA details.
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.COM_ECIM_CHECK_AND_UPDATE_ENDENTITY
 * </p>
 *
 * @author xsrirko
 */

public class ComEcimPrepareExternalCaEnrollmentInfoTask extends WorkflowQueryTask {

    private static final long serialVersionUID = 5479785685781684994L;

    public static final String CERTIFICATE_ENROLLMENT_CA = WorkflowParameterKeys.CERTIFICATE_ENROLLMENT_CA.toString();
    public static final String CERTIFICATE_SUBJECT_DN = WorkflowParameterKeys.EXTERNAL_CA_CERTIFICATE_SUBJECT_DN.toString();
    public static final String CERTIFICATE_AUTHORITY_DN = WorkflowParameterKeys.EXTERNAL_CA_CERTIFICATE_AUTHORITY_DN.toString();
    public static final String CA_CERTIFICATE = WorkflowParameterKeys.EXTERNAL_CA_CERTIFICATE.toString();
    public static final String ENROLLMENT_SERVER_URL = WorkflowParameterKeys.EXTERNAL_CA_ENROLLMENT_SERVER_URL.toString();
    public static final String CHALLENGE_PASSWORD = WorkflowParameterKeys.EXTERNAL_CA_CHALLENGE_PASSWORD.toString();

    public static final String SUB_ALT_NAME_KEY = WorkflowParameterKeys.IPSEC_SUB_ALT_NAME.toString();
    public static final String SUB_ALT_NAME_TYPE_KEY = WorkflowParameterKeys.IPSEC_SUB_ALT_NAME_TYPE.toString();
    public static final String ENROLLMENT_MODE_KEY = WorkflowParameterKeys.ENROLLMENT_MODE.toString();
    public static final String OUTPUT_PARAMS_KEY = WorkflowParameterKeys.OUTPUT_PARAMS.toString();
    public static final String TRUSTED_CATEGORY_KEY = WorkflowParameterKeys.TRUSTED_CATEGORY.toString();
    public static final String KEY_ALGORITHM_KEY = WorkflowParameterKeys.KEY_SSHKEYS_GENERATION_ALGORITHM.toString();

    public static final String SHORT_DESCRIPTION = "Prepare ExtCA Enroll";

    public ComEcimPrepareExternalCaEnrollmentInfoTask() {
        super(WorkflowTaskType.COM_ECIM_PREPARE_EXT_CA_ENROLLMENT_INFO);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public ComEcimPrepareExternalCaEnrollmentInfoTask(final String fdn) {
        super(WorkflowTaskType.COM_ECIM_PREPARE_EXT_CA_ENROLLMENT_INFO, fdn);
        setShortDescription(SHORT_DESCRIPTION);
    }

    /**
     * @return the certificateEnrollmentCa
     */
    public String getCertificateEnrollemntCa() {
        return (String) getValue(CERTIFICATE_ENROLLMENT_CA);
    }

    /**
     * @param certificateEnrollmentCa
     *            the certificateEnrollmentCa to set
     */
    public void setCertificateEnrollemntCa(final String certificateEnrollmentCa) {
        setValue(CERTIFICATE_ENROLLMENT_CA, certificateEnrollmentCa);
    }

    /**
     * @return the certificateAuthoirtyDn
     */
    public String getCertificateAuthorityDn() {
        return (String) getValue(CERTIFICATE_AUTHORITY_DN);
    }

    /**
     * @param certificateAuthorityDn
     *            the certificateAuthorityDn to set
     */
    public void setCertificateAuthorityDn(final String certificateAuthorityDn) {
        setValue(CERTIFICATE_AUTHORITY_DN, certificateAuthorityDn);
    }

    /**
     * @return the certificateSubjectDn
     */
    public String getCertificateSubjectDn() {
        return (String) getValue(CERTIFICATE_SUBJECT_DN);
    }

    /**
     * @param certificateSubjectDn
     *            the certificateSubjectDn to set
     */
    public void setCertificateSubjectDn(final String certificateSubjectDn) {
        setValue(CERTIFICATE_SUBJECT_DN, certificateSubjectDn);
    }

    /**
     * @return the caCertificate
     */
    public String getCaCertificate() {
        return (String) getValue(CA_CERTIFICATE);
    }

    /**
     * @param caCertificate
     *            the caCertificate to set
     */
    public void setCaCertifiate(final String caCertificate) {
        setValue(CA_CERTIFICATE, caCertificate);
    }

    /**
     * @return the enrollmentServerUrl
     */
    public String getEnrollmentServerUrl() {
        return (String) getValue(ENROLLMENT_SERVER_URL);
    }

    /**
     * @param enrollmentServerUrl
     *            the enrollmentServerUrl to set
     */
    public void setEnrollmentServerUrl(final String enrollmentServerUrl) {
        setValue(ENROLLMENT_SERVER_URL, enrollmentServerUrl);
    }

    /**
     * @return the challengePassword
     */
    public String getChallengePassword() {
        return (String) getValue(CHALLENGE_PASSWORD);
    }

    /**
     * @param challengePassword
     *            the challengePassword to set
     */
    public void setChallengePassword(final String challengePassword) {
        setValue(CHALLENGE_PASSWORD, challengePassword);
    }

    /**
     * @return the subjectAltName
     */
    public String getSubjectAltName() {
        return (String) getValue(SUB_ALT_NAME_KEY);
    }

    /**
     * @param subjectAltName
     *            the subjectAltName to set
     */
    public void setSubjectAltName(final String subjectAltName) {
        setValue(SUB_ALT_NAME_KEY, subjectAltName);
    }

    /**
     * @return the subjectAltNameType
     */
    public String getSubjectAltNameType() {
        return (String) getValue(SUB_ALT_NAME_TYPE_KEY);
    }

    /**
     * @param subjectAltNameType
     *            the subjectAltNameType to set
     */
    public void setSubjectAltNameType(final String subjectAltNameType) {
        setValue(SUB_ALT_NAME_TYPE_KEY, subjectAltNameType);
    }

    /**
     * @return the enrollmentMode
     */
    public String getEnrollmentMode() {
        return (String) getValue(ENROLLMENT_MODE_KEY);
    }

    /**
     * @param enrollmentMode
     *            the enrollmentMode to set
     */
    public void setEnrollmentMode(final String enrollmentMode) {
        setValue(ENROLLMENT_MODE_KEY, enrollmentMode);
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
     * @return the keyAlgorithm
     */
    public String getKeyAlgorithm() {
        return (String) getValue(KEY_ALGORITHM_KEY);
    }

    /**
     * @param keyAlgorithm
     *            the keyAlgorithm to set
     */
    public void setKeyAlgorithm(final String keyAlgorithm) {
        setValue(KEY_ALGORITHM_KEY, keyAlgorithm);
    }
}
