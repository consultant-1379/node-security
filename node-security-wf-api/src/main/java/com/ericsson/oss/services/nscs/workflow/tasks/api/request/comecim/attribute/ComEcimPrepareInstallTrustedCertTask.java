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

/**
 * <p>
 * Workflow task representing a request to prepare an installTrustedCert action over a COM ECIM node
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.COM_ECIM_PREPARE_INSTALL_TRUSTED_CERT
 * </p>
 *
 * @author emaborz
 */
public class ComEcimPrepareInstallTrustedCertTask extends WorkflowQueryTask {

    private static final long serialVersionUID = -1547109469729494861L;

    /**
     * Key of the trustedCertCategory value in the map
     */
    public static final String TRUSTED_CATEGORY_KEY = WorkflowParameterKeys.TRUSTED_CATEGORY.toString();
    /**
     * Key of the interfaceFdn value in the map
     */
    public static final String INTERFACE_FDN = WorkflowParameterKeys.EXTERNAL_CA_INTERFACE_FDN.toString();

    /**
     * Key of the output parameters in the map
     */
    public static final String OUTPUT_PARAMS_KEY = WorkflowParameterKeys.OUTPUT_PARAMS.toString();

    public static final String SHORT_DESCRIPTION = "Prepare trustInstall";

    public ComEcimPrepareInstallTrustedCertTask() {
        super(WorkflowTaskType.COM_ECIM_PREPARE_INSTALL_TRUSTED_CERT);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public ComEcimPrepareInstallTrustedCertTask(final String fdn) {
        super(WorkflowTaskType.COM_ECIM_PREPARE_INSTALL_TRUSTED_CERT, fdn);
        setShortDescription(SHORT_DESCRIPTION);
    }

    /**
     * @return the trustedCategory
     */
    public String getTrustedCategory() {
        return (String) getValue(TRUSTED_CATEGORY_KEY);
    }

    /**
     * @param trustedCategory
     *            the trustedCategory to set
     */
    public void setTrustedCategory(final String trustedCategory) {
        setValue(TRUSTED_CATEGORY_KEY, trustedCategory);
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

}
