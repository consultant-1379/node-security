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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.cbpoi.attribute;

import java.io.Serializable;
import java.util.Map;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

public class CbpOiCheckRemoveTrustTask extends WorkflowQueryTask {

    private static final long serialVersionUID = -5659976396238413557L;

    public static final String CERTIFICATE_SERIAL_NUMBER_KEY = WorkflowParameterKeys.CERTIFICATE_ID.toString();
    public static final String ISSUER_KEY = WorkflowParameterKeys.CERTIFICATE_AUTHORITY_ID.toString();
    public static final String CERTIFICATE_CATEGORY_KEY = WorkflowParameterKeys.TRUSTED_CATEGORY.toString();
    public static final String OUTPUT_PARAMS_KEY = WorkflowParameterKeys.OUTPUT_PARAMS.toString();

    public static final String SHORT_DESCRIPTION = "Check Remove trust";

    public CbpOiCheckRemoveTrustTask() {
        super(WorkflowTaskType.CBP_OI_CHECK_REMOVE_TRUST);
        setShortDescriptionLocal(SHORT_DESCRIPTION);
    }

    public CbpOiCheckRemoveTrustTask(final String name) {
        super(WorkflowTaskType.CBP_OI_CHECK_REMOVE_TRUST, name);
        setShortDescriptionLocal(SHORT_DESCRIPTION);
    }

    /**
     * @return the certificate serial number
     */
    public String getCertificateSerialNumber() {
        return (String) getValue(CERTIFICATE_SERIAL_NUMBER_KEY);
    }

    /**
     * @param certificateSN
     *            the certificate Serial Number
     */
    public void setCertificateSerialNumber(final String certificateSN) {
        setValue(CERTIFICATE_SERIAL_NUMBER_KEY, certificateSN);
    }

    /**
     * @return the issuerDn
     */
    public String getIssuer() {
        return (String) getValue(ISSUER_KEY);
    }

    /**
     * @param issuerDn
     *            the issuerDn value
     */
    public void setIssuer(final String issuer) {
        setValue(ISSUER_KEY, issuer);
    }

    /**
     * @return the trusted certificate category
     */
    public String getCertCategory() {
        return (String) getValue(CERTIFICATE_CATEGORY_KEY);
    }

    /**
     * @param certificateCategory
     *            the trusted certificate category
     */
    public void setCertCategory(final String certificateCategory) {
        setValue(CERTIFICATE_CATEGORY_KEY, certificateCategory);
    }

    /**
     * @return outputParams
     */
    @SuppressWarnings("unchecked")
    public Map<String, Serializable> getOutputParams() {
        return (Map<String, Serializable>) getValue(OUTPUT_PARAMS_KEY);
    }

    /**
     * @param outputParams
     *            the output parameters to be set
     */
    public void setOutputParams(final Map<String, Serializable> outputParams) {
        setValue(OUTPUT_PARAMS_KEY, outputParams);
    }

    private void setShortDescriptionLocal(final String shortDescription) {
        super.setShortDescription(shortDescription);
    }

}
