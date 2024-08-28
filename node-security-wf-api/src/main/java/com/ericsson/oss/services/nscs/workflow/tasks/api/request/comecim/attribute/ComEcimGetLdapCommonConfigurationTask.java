/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

/**
 * <p>
 * Workflow task representing the request to get the Common Ldap Configuration
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.COM_ECIM_GET_LDAP_COMMON_CONFIG
 * </p>
 *
 * @author xsrirko
 */
public class ComEcimGetLdapCommonConfigurationTask extends WorkflowQueryTask {

    private static final long serialVersionUID = 6710436227234187286L;

    public static final String TLS_MODE_KEY = WorkflowParameterKeys.TLS_MODE.toString();

    public static final String USE_TLS_KEY = WorkflowParameterKeys.USE_TLS.toString();

    public static final String SHORT_DESCRIPTION = "Get common LDAP config";

    public ComEcimGetLdapCommonConfigurationTask() {
        super(WorkflowTaskType.COM_ECIM_GET_LDAP_COMMON_CONFIG);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public ComEcimGetLdapCommonConfigurationTask(final String fdn) {
        super(WorkflowTaskType.COM_ECIM_GET_LDAP_COMMON_CONFIG, fdn);
        setShortDescription(SHORT_DESCRIPTION);
    }

    /**
     * Gets the TlsMode.
     *
     * @return tlsMode.
     */
    public String getTlsMode() {
        return (String) getValue(TLS_MODE_KEY);
    }

    /**
     * Sets the TlsMode.
     *
     * @param tlsMode the tlsMode
     */
    public void setTlsMode(final String tlsMode) {
        setValue(TLS_MODE_KEY, tlsMode);
    }

    /**
     * Gets the UseTls.
     *
     * @return useTls.
     */
    public Boolean getUseTls() {
        return (Boolean) getValue(USE_TLS_KEY);
    }

    /**
     * Sets the UseTls.
     *
     * @param useTls the useTls
     */
    public void setUseTls(final Boolean useTls) {
        setValue(USE_TLS_KEY, useTls);
    }

}
