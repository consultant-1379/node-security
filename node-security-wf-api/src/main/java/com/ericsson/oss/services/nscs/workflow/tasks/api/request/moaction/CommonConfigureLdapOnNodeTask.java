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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.moaction;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowActionTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

import java.io.Serializable;
import java.util.Map;

/**
 * <p>
 * Workflow task representing the request to perform the ldapConfigure action for COM ECIM node
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.COM_ECIM_CONFIGURE_LDAP_ACTION
 * </p>
 *
 * @author xsrirko
 */
public abstract class CommonConfigureLdapOnNodeTask extends WorkflowActionTask {

    private static final long serialVersionUID = -8649267946370699738L;
    public static final String BASE_DN_KEY = WorkflowParameterKeys.BASE_DN.toString();
    public static final String BIND_DN_KEY = WorkflowParameterKeys.BIND_DN.toString();
    public static final String BIND_PASSWORD_KEY = WorkflowParameterKeys.BIND_PASSWORD.toString();
    public static final String LDAP_SERVER_PORT_KEY = WorkflowParameterKeys.LDAP_SERVER_PORT.toString();
    public static final String LDAP_IP_ADDRESS_KEY = WorkflowParameterKeys.LDAP_IP_ADDRESS.toString();
    public static final String FALLBACK_LDAP_IP_ADDRESS_KEY = WorkflowParameterKeys.FALLBACK_LDAP_IP_ADDRESS.toString();
    public static final String TLS_MODE_KEY = WorkflowParameterKeys.TLS_MODE.toString();
    public static final String USE_TLS_KEY = WorkflowParameterKeys.USE_TLS.toString();
    public static final String USER_LABEL_KEY = WorkflowParameterKeys.USER_LABEL.toString();
    public static final String LDAP_WORKFLOW_CONTEXT = WorkflowParameterKeys.LDAP_WORKFLOW_CONTEXT.toString();

    public CommonConfigureLdapOnNodeTask() {
        super();
    }
    public CommonConfigureLdapOnNodeTask(final WorkflowTaskType taskType) {
        super(taskType);
    }

    public CommonConfigureLdapOnNodeTask(final WorkflowTaskType taskType, final String fdn) {
        super(taskType, fdn);
    }

    /**
     * @return the baseDn
     */
    public String getBaseDn() {
        return (String) getValue(BASE_DN_KEY);
    }

    /**
     * @param baseDn
     *            the baseDn to set
     */
    public void setBaseDn(final String baseDn) {
        setValue(BASE_DN_KEY, baseDn);
    }

    /**
     * @return the bindDn
     */
    public String getBindDn() {
        return (String) getValue(BIND_DN_KEY);
    }

    /**
     * @param bindDn
     *            the bindDn to set
     */
    public void setBindDn(final String bindDn) {
        setValue(BIND_DN_KEY, bindDn);
    }

    /**
     * @return the bindPassword
     */
    public String getBindPassword() {
        return (String) getValue(BIND_PASSWORD_KEY);
    }

    /**
     * @param bindPassword
     *            the bindPassword to set
     */
    public void setBindPassword(final String bindPassword) {
        setValue(BIND_PASSWORD_KEY, bindPassword);
    }

    /**
     * @return the serverPort
     */
    public Integer getServerPort() {
        return (Integer) getValue(LDAP_SERVER_PORT_KEY);
    }

    /**
     * @param serverPort
     *            the serverPort to set
     */
    public void setServerPort(final Integer serverPort) {
        setValue(LDAP_SERVER_PORT_KEY, serverPort);
    }

    /**
     * @return the ldapIpAddress
     */
    public String getLdapIpAddress() {
        return (String) getValue(LDAP_IP_ADDRESS_KEY);
    }

    /**
     * @param ldapIpAddress
     *            the ldapIpAddress to set
     */
    public void setLdapIpAddress(final String ldapIpAddress) {
        setValue(LDAP_IP_ADDRESS_KEY, ldapIpAddress);
    }

    /**
     * @return the fallbackLdapIpAddress
     */
    public String getFallbackLdapIpAddress() {
        return (String) getValue(FALLBACK_LDAP_IP_ADDRESS_KEY);
    }

    /**
     * @param fallbackLdapIpAddress
     *            the fallbackLdapIpAddress to set
     */
    public void setFallbackLdapIpAddress(final String fallbackLdapIpAddress) {
        setValue(FALLBACK_LDAP_IP_ADDRESS_KEY, fallbackLdapIpAddress);
    }

    /**
     * @return the tlsMode
     */
    public String getTlsMode() {
        return (String) getValue(TLS_MODE_KEY);
    }

    /**
     * @param tlsMode
     *            the tlsMode to set
     */
    public void setTlsMode(final String tlsMode) {
        setValue(TLS_MODE_KEY, tlsMode);
    }

    /**
     * @return the useTls
     */
    public Boolean getUseTls() {
        return (Boolean) getValue(USE_TLS_KEY);
    }

    /**
     * @param useTls
     *            the useTls to set
     */
    public void setUseTls(final Boolean useTls) {
        setValue(USE_TLS_KEY, useTls);
    }

    /**
     * @return the userLabel
     */
    public String getUserLabel() {
        return (String) getValue(USER_LABEL_KEY);
    }

    /**
     * @param userLabel
     *            the userLabel to set
     */
    public void setUserLabel(final String userLabel) {
        setValue(USER_LABEL_KEY, userLabel);
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
