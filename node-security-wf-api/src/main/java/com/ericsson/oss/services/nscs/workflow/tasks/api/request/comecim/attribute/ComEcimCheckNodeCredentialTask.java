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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute;

import java.io.Serializable;
import java.util.Map;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

/**
 * <p>
 * Workflow task representing a request to check if Node Credential MO is existing on the COM ECIM node, depending on the requested Trusted
 * Certificate type [IPSEC, OAM]
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.COM_ECIM_CHECK_TRUSTED_ALREADY_INSTALLED
 * </p>
 *
 * @author elucbot
 */
public class ComEcimCheckNodeCredentialTask extends WorkflowQueryTask {

    private static final long serialVersionUID = 4596490029353060470L;

    public static final String ISREISSUE_KEY = WorkflowParameterKeys.CERTIFICATE_ISREISSUE.toString();
    public static final String ENROLLMENT_MODE_KEY = WorkflowParameterKeys.ENROLLMENT_MODE.toString();
    public static final String TRUSTED_CATEGORY_KEY = WorkflowParameterKeys.TRUSTED_CATEGORY.toString();
    public static final String OUTPUT_PARAMS_KEY = WorkflowParameterKeys.OUTPUT_PARAMS.toString();
    public static final String INTERFACE_FDN = WorkflowParameterKeys.EXTERNAL_CA_INTERFACE_FDN.toString();
    public static final String CERTIFICATE_ENROLLMENT_CA = WorkflowParameterKeys.CERTIFICATE_ENROLLMENT_CA.toString();
    public static final String SHORT_DESCRIPTION = "Check nodeCredential";

    public ComEcimCheckNodeCredentialTask() {
        super(WorkflowTaskType.COM_ECIM_CHECK_NODE_CREDENTIAL);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public ComEcimCheckNodeCredentialTask(final String fdn) {
        super(WorkflowTaskType.COM_ECIM_CHECK_NODE_CREDENTIAL, fdn);
        setShortDescription(SHORT_DESCRIPTION);
    }

    /**
     * @return the isReissue
     */
    public String getIsReissue() {
        return (String) getValue(ISREISSUE_KEY);
    }

    /**
     * @param isReissue
     *            the isReissue to set
     */
    public void setIsReissue(final String isReissue) {
        setValue(ISREISSUE_KEY, isReissue);
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
     * @return the interfaceFdn
     */
    public String getInterfaceFdn() {
        return (String) getValue(INTERFACE_FDN);
    }

    /**
     * @param interfaceFdn
     *            the interfaceFdn to set
     */
    public void setInterfaceFdn(final String interfaceFdn) {
        setValue(INTERFACE_FDN, interfaceFdn);
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
}
