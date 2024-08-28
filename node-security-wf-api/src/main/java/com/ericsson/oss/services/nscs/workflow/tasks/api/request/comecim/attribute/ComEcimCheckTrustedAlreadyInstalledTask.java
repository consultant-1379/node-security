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
 * Workflow task representing a request to check if the trusted certs are already installed over the COM ECIM node
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.COM_ECIM_CHECK_TRUSTED_ALREADY_INSTALLED
 * </p>
 *
 * @author elucbot
 */
public class ComEcimCheckTrustedAlreadyInstalledTask extends WorkflowQueryTask {

    private static final long serialVersionUID = 5077295591739975937L;

    /**
     * Key of the trustedCertCategory value in the map
     */
    public static final String TRUST_CERTS_KEY = WorkflowParameterKeys.TRUSTED_CATEGORY.toString();

    /**
     * Key of the output parameters in the map
     */
    public static final String OUTPUT_PARAMS_KEY = WorkflowParameterKeys.OUTPUT_PARAMS.toString();

    public static final String SHORT_DESCRIPTION = "Check trustInstall";

    public ComEcimCheckTrustedAlreadyInstalledTask() {
        super(WorkflowTaskType.COM_ECIM_CHECK_TRUSTED_ALREADY_INSTALLED);
        setValue(TRUST_CERTS_KEY, "");
        setShortDescription(SHORT_DESCRIPTION);
    }

    public ComEcimCheckTrustedAlreadyInstalledTask(final String fdn, final String trustCerts) {
        super(WorkflowTaskType.COM_ECIM_CHECK_TRUSTED_ALREADY_INSTALLED, fdn);
        setValue(TRUST_CERTS_KEY, trustCerts);
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
