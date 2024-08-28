/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2020
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

/**
 * <p>
 * Workflow task representing a request to possibly reconfigure the services using the enrolled node credential for the given CBP OI (EOI YANG based)
 * node.
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.CBP_OI_CONFIGURE_NODE_CREDENTIAL_SERVICES
 * </p>
 */
public class CbpOiConfigureNodeCredentialServicesTask extends WorkflowQueryTask {

    private static final long serialVersionUID = 2385285964712043577L;

    /**
     * Key of the output parameters parameter in the workflow parameters map.
     */
    public static final String OUTPUT_PARAMS_KEY = WorkflowParameterKeys.OUTPUT_PARAMS.toString();

    /**
     * Key of the is trust distribution required flag parameter in the workflow parameters map.
     */
    public static final String IS_TRUST_DISTRIBUTION_REQUIRED = WorkflowParameterKeys.IS_TRUST_DISTRIBUTION_REQUIRED.toString();

    /**
     * Key of the certificate type parameter in the workflow parameters map.
     */
    public static final String TRUSTED_CATEGORY_KEY = WorkflowParameterKeys.TRUSTED_CATEGORY.toString();

    /**
     * Short description of the task.
     */
    public static final String SHORT_DESCRIPTION = "Prepare online enroll";

    public CbpOiConfigureNodeCredentialServicesTask() {
        super(WorkflowTaskType.CBP_OI_CONFIGURE_NODE_CREDENTIAL_SERVICES);
        setShortDescriptionLocal(SHORT_DESCRIPTION);
    }

    public CbpOiConfigureNodeCredentialServicesTask(final String fdn) {
        super(WorkflowTaskType.CBP_OI_CONFIGURE_NODE_CREDENTIAL_SERVICES, fdn);
        setShortDescriptionLocal(SHORT_DESCRIPTION);
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

    private void setShortDescriptionLocal(final String shortDescription) {
        super.setShortDescription(shortDescription);
    }
}
