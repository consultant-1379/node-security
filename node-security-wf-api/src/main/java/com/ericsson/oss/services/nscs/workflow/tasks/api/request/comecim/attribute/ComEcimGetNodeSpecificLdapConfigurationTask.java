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

import java.io.Serializable;
import java.util.Map;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

/**
 * <p>
 * Workflow task representing the request to get the Node specific Ldap Configuration
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.COM_ECIM_GET_LDAP_NODE_CONFIG
 * </p>
 *
 * @author xsrirko
 */
public class ComEcimGetNodeSpecificLdapConfigurationTask extends WorkflowQueryTask {

    private static final long serialVersionUID = -2147990762820433463L;

    public static final String USE_TLS_KEY = WorkflowParameterKeys.USE_TLS.toString();
    public static final String LDAP_WORKFLOW_CONTEXT = WorkflowParameterKeys.LDAP_WORKFLOW_CONTEXT.toString();

    public static final String SHORT_DESCRIPTION = "Get node LDAP config";

    public ComEcimGetNodeSpecificLdapConfigurationTask() {
        super(WorkflowTaskType.COM_ECIM_GET_LDAP_NODE_CONFIG);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public ComEcimGetNodeSpecificLdapConfigurationTask(final String fdn) {
        super(WorkflowTaskType.COM_ECIM_GET_LDAP_NODE_CONFIG, fdn);
        setShortDescription(SHORT_DESCRIPTION);
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

    /**
     * @return the ldapWorkFlowContext
     */
    @SuppressWarnings("unchecked")
    public Map<String, Serializable> getLdapWorkFlowContext() {
        return (Map<String, Serializable>) getValue(LDAP_WORKFLOW_CONTEXT);
    }

    /**
     * @param outputParams
     *            the ldapWorkFlowContext to set
     */
    public void setLdapWorkFlowContext(final Map<String, Serializable> outputParams) {
        setValue(LDAP_WORKFLOW_CONTEXT, outputParams);
    }

}
