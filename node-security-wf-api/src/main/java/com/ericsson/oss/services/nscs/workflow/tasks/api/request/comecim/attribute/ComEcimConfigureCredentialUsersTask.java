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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute;

import java.io.Serializable;
import java.util.Map;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

public class ComEcimConfigureCredentialUsersTask extends WorkflowQueryTask {

    private static final long serialVersionUID = 1992654101257118565L;

    public static final String TRUSTED_CERT_CATEGORY_KEY = WorkflowParameterKeys.TRUSTED_CATEGORY.toString();
    public static final String OUTPUT_PARAMS_KEY = WorkflowParameterKeys.OUTPUT_PARAMS.toString();
    public static final String IS_TRUST_DISTRIBUTION_REQUIRED = WorkflowParameterKeys.IS_TRUST_DISTRIBUTION_REQUIRED.toString();
    public static final String INTERFACE_FDN = WorkflowParameterKeys.EXTERNAL_CA_INTERFACE_FDN.toString();
    public static final String CERTIFICATE_ENROLLMENT_CA = WorkflowParameterKeys.CERTIFICATE_ENROLLMENT_CA.toString();
    public static final String SHORT_DESCRIPTION = "Configure credential users";

    public ComEcimConfigureCredentialUsersTask() {
        super(WorkflowTaskType.COM_ECIM_CONFIGURE_CREDENTIAL_USERS);
        setValue(TRUSTED_CERT_CATEGORY_KEY, "");
        setShortDescription(SHORT_DESCRIPTION);
    }

    public ComEcimConfigureCredentialUsersTask(final String fdn, final String trustCerts) {
        super(WorkflowTaskType.COM_ECIM_CONFIGURE_CREDENTIAL_USERS, fdn);
        setValue(TRUSTED_CERT_CATEGORY_KEY, trustCerts);
        setShortDescription(SHORT_DESCRIPTION);
    }

    /**
     * @return the trustedCertCategory
     */
    public String getTrustedCertCategory() {
        return (String) getValue(TRUSTED_CERT_CATEGORY_KEY);
    }

    /**
     * @param trustedCertCategory
     *            the trustedCertCategory to set
     */
    public void setTrustedCertCategory(final String trustedCertCategory) {
        setValue(TRUSTED_CERT_CATEGORY_KEY, trustedCertCategory);
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
     * @return the isTrustDistributionRequired
     */
    public String getIsTrustDistributionRequired() {
        return (String) getValue(IS_TRUST_DISTRIBUTION_REQUIRED);
    }

    /**
     * @param isTrustDistributionRequired
     *            the isTrustDistributionRequired to set
     */
    public void setIsTrustDistributionRequired(final String isTrustDistributionRequired) {
        setValue(IS_TRUST_DISTRIBUTION_REQUIRED, isTrustDistributionRequired);
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
