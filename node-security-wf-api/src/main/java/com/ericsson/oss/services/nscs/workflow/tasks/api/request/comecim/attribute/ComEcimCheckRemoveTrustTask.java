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

public class ComEcimCheckRemoveTrustTask extends WorkflowQueryTask {

    private static final long serialVersionUID = -4076211306466941583L;

    public static final String CERTIFICATE_SN_KEY = WorkflowParameterKeys.CERTIFICATE_ID.toString();
    public static final String ISSUER_KEY = WorkflowParameterKeys.CERTIFICATE_AUTHORITY_ID.toString();
    public static final String CERTIFICATE_CATEGORY_KEY = WorkflowParameterKeys.TRUSTED_CATEGORY.toString();
    public static final String OUTPUT_PARAMS_KEY = WorkflowParameterKeys.OUTPUT_PARAMS.toString();

    public static final String SHORT_DESCRIPTION = "Remove trust";

    public ComEcimCheckRemoveTrustTask() {
        super(WorkflowTaskType.COM_ECIM_CHECK_REMOVE_TRUST);
        setValue(CERTIFICATE_SN_KEY, "");
        setValue(ISSUER_KEY, "");
        setValue(CERTIFICATE_CATEGORY_KEY, "");
        setValue(OUTPUT_PARAMS_KEY, null);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public ComEcimCheckRemoveTrustTask(final String name) {
        super(WorkflowTaskType.COM_ECIM_CHECK_REMOVE_TRUST, name);
        setValue(CERTIFICATE_SN_KEY, "");
        setValue(ISSUER_KEY, "");
        setValue(CERTIFICATE_CATEGORY_KEY, "");
        setValue(OUTPUT_PARAMS_KEY, null);
        setShortDescription(SHORT_DESCRIPTION);
    }

    /**
     * @return the certificate serial number
     */
    public String getCertificateSN() {
        return (String) getValue(CERTIFICATE_SN_KEY);
    }

    /**
     * @param certificateSN the certificateSN
     */
    public void setCertificateSN(final String certificateSN) {
        setValue(CERTIFICATE_SN_KEY, certificateSN);
    }

    /**
     * @return the issuer
     */
    public String getIssuer() {
        return (String) getValue(ISSUER_KEY);
    }

    /**
     * @param issuer the issuer
     */
    public void setIssuer(final String issuer) {
        setValue(ISSUER_KEY, issuer);
    }

    /**
     * @return the certificate category
     */
    public String getCertCategory() {
        return (String) getValue(CERTIFICATE_CATEGORY_KEY);
    }

    /**
     * @param certCategory the certificate category
     */
    public void setCertCategory(final String certCategory) {
        setValue(CERTIFICATE_CATEGORY_KEY, certCategory);
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
