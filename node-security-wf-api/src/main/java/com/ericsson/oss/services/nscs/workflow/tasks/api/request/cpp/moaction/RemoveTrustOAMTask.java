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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

public class RemoveTrustOAMTask extends WorkflowQueryTask {

    private static final long serialVersionUID = 6368791987234658292L;

    public static final String CERTIFICATE_SN = WorkflowParameterKeys.CERTIFICATE_ID.toString();
    public static final String ISSUER = WorkflowParameterKeys.CERTIFICATE_AUTHORITY_ID.toString();
    public static final String CERTIFICATE_CATEGORY = WorkflowParameterKeys.TRUSTED_CATEGORY.toString();

    public static final String SHORT_DESCRIPTION = "Remove OAM trusted";

    public RemoveTrustOAMTask() {
        super(WorkflowTaskType.CPP_REMOVE_TRUST_OAM);
        setValue(CERTIFICATE_SN, "");
        setValue(ISSUER, "");
        setValue(CERTIFICATE_CATEGORY, "");
        setShortDescription(SHORT_DESCRIPTION);
    }

    public RemoveTrustOAMTask(final String name) {
        super(WorkflowTaskType.CPP_REMOVE_TRUST_OAM, name);
        setValue(CERTIFICATE_SN, "");
        setValue(ISSUER, "");
        setValue(CERTIFICATE_CATEGORY, "");
        setShortDescription(SHORT_DESCRIPTION);
    }

    /**
     * @return the certificate serial Number
     */
    public String getCertificateSN() {
        return (String) getValue(CERTIFICATE_SN);
    }

    /**
     * @param certificateSN the certificateSN
     */
    public void setCertificateSN(final String certificateSN) {
        setValue(CERTIFICATE_SN, certificateSN);
    }

    /**
     * @return the issuer
     */
    public String getIssuer() {
        return (String) getValue(ISSUER);
    }

    /**
     * @param issuer the issuer
     */
    public void setIssuer(final String issuer) {
        setValue(ISSUER, issuer);
    }

    /**
     * @return the certificate category
     */
    public String getCertCategory() {
        return (String) getValue(CERTIFICATE_CATEGORY);
    }

    /**
     * @param certCategory the certCategory
     */
    public void setCertCategory(final String certCategory) {
        setValue(CERTIFICATE_CATEGORY, certCategory);
    }

}
