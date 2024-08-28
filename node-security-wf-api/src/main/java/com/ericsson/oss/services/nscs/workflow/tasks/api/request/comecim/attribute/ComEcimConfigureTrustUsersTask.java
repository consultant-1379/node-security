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

public class ComEcimConfigureTrustUsersTask extends WorkflowQueryTask {

    private static final long serialVersionUID = 1422887326252372630L;

    public static final String TRUSTED_CERT_CATEGORY_KEY = WorkflowParameterKeys.TRUSTED_CATEGORY.toString();
    public static final String OUTPUT_PARAMS_KEY = WorkflowParameterKeys.OUTPUT_PARAMS.toString();

    public static final String SHORT_DESCRIPTION = "Configure trust users";

    public ComEcimConfigureTrustUsersTask() {
        super(WorkflowTaskType.COM_ECIM_CONFIGURE_TRUST_USERS);
        setValue(TRUSTED_CERT_CATEGORY_KEY, "");
        setShortDescription(SHORT_DESCRIPTION);
    }

    public ComEcimConfigureTrustUsersTask(final String fdn, final String trustCerts) {
        super(WorkflowTaskType.COM_ECIM_CONFIGURE_TRUST_USERS, fdn);
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

}
