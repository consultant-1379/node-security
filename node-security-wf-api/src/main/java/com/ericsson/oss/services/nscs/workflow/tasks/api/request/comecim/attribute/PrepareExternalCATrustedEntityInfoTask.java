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
import java.util.List;
import java.util.Map;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

/**
 *
 * <p>
 * Workflow task representing a request to prepare trusted entities info from external CA data provided in input xml to distribute over the COM ECIM node
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.EXTERNAL_CA_TRUSTED_CA_CERTIFICATE_INFO
 * </p>
 *
 */
public class PrepareExternalCATrustedEntityInfoTask extends WorkflowQueryTask {

    private static final long serialVersionUID = 6848850468855821437L;

    /**
     * Key of the trustedCertCategory value in the map
     */
    public static final String TRUST_CERTS_KEY = WorkflowParameterKeys.TRUSTED_CATEGORY.toString();
    public static final String SHORT_DESCRIPTION = "Prepare ExtCa TrustInstall";
    /**
     * Key of the externalCaTrustedCertificateInfo value in the map
     */
    public static final String EXTERNAL_TRUSTED_CA_CERTIFICATE_INFO = WorkflowParameterKeys.EXTERNAL_TRUSTED_CA_CERTIFICATE_INFO.toString();

    /**
     * Key of the outPutParams value in the map
     */
    public static final String OUTPUT_PARAMS_KEY = WorkflowParameterKeys.OUTPUT_PARAMS.toString();

    public PrepareExternalCATrustedEntityInfoTask() {
        super(WorkflowTaskType.PREPARE_EXTERNAL_CA_TRUSTED_ENTITY_INFO);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public PrepareExternalCATrustedEntityInfoTask(final String fdn) {
        super(WorkflowTaskType.PREPARE_EXTERNAL_CA_TRUSTED_ENTITY_INFO, fdn);
        setShortDescription(SHORT_DESCRIPTION);
    }

    /**
     * Gets the trust certs.
     *
     * @return trust certS.
     */
    public String getTrustCerts() {
        return (String) getValue(TRUST_CERTS_KEY);
    }

    /**
     * Sets the trust certs. It may be the trust file path or TrustedCertCategory
     *
     * @param trustCerts
     *            trust certs.
     */
    public void setTrustCerts(final String trustCerts) {
        setValue(TRUST_CERTS_KEY, trustCerts);
    }

    /**
     * Sets the externalTrustedCaCertificateInfo
     *
     * @param trustedCACertificates
     *             trustedCACertificates
     */
    public void setExternalTrustedCACertificateInfo(final List<Map<String, String>> trustedCACertificates) {
        setValue(EXTERNAL_TRUSTED_CA_CERTIFICATE_INFO, trustedCACertificates);
    }

    /**
     * @return the externalTrustedCaCertificateInfo
     */
    @SuppressWarnings("unchecked")
        public List<Map<String, String>> getExternalTrustedCACertificateInfo() {
        return (List<Map<String, String>>) getValue(EXTERNAL_TRUSTED_CA_CERTIFICATE_INFO);
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
