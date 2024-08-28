/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2023
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.workflow.task.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.ldap.control.IdentityManagementProxy;
import com.ericsson.nms.security.nscs.ldap.control.LdapConfigurationProvider;
import com.ericsson.nms.security.nscs.ldap.utility.LdapConstants;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.attribute.CommonLdapConfigurationTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;

/**
 * Auxiliary class containing helper utilities for LDAP workflow task handlers.
 */
public class LdapWorkflowHelper {

    @Inject
    private LdapConfigurationProvider ldapConfigurationProvider;

    @Inject
    private NscsNodeUtility nscsNodeUtility;

    @Inject
    private IdentityManagementProxy identityManagementProxy;

    /**
     * Get the LDAP configuration parameters for the given node and store them in the LDAP workflow context of the given task.
     * 
     * @param task
     *            the task.
     * @param normalizable
     *            the normalizable node reference.
     */
    public void getLdapConfiguration(final CommonLdapConfigurationTask task, final NormalizableNodeReference normalizable) {

        final Boolean isTls = task.getIsTls();
        final String tlsMode = task.getTlsMode();

        final boolean isIPv6 = nscsNodeUtility.hasNodeIPv6Address(normalizable);

        final Map<String, Object> ldapServerConfiguration = ldapConfigurationProvider.getLdapServerConfiguration();

        final Map<String, Serializable> ldapWorkFlowContext = new HashMap<>();
        ldapWorkFlowContext.put(WorkflowOutputParameterKeys.LDAP_SERVER_PORT.toString(), getLdapServerPort(isTls, tlsMode, ldapServerConfiguration));
        ldapWorkFlowContext.put(WorkflowOutputParameterKeys.LDAP_IP_ADDRESS.toString(),
                getPrimaryLdapServerIPAddress(isIPv6, ldapServerConfiguration));
        ldapWorkFlowContext.put(WorkflowOutputParameterKeys.FALLBACK_LDAP_IP_ADDRESS.toString(),
                getSecondaryLdapServerIPAddress(isIPv6, ldapServerConfiguration));
        ldapWorkFlowContext.put(WorkflowOutputParameterKeys.BIND_DN.toString(), (String) ldapServerConfiguration.get(LdapConstants.BIND_DN));
        ldapWorkFlowContext.put(WorkflowOutputParameterKeys.BIND_PASSWORD.toString(), (String) ldapServerConfiguration.get(LdapConstants.BIND_PASSWORD));
        ldapWorkFlowContext.put(WorkflowOutputParameterKeys.BASE_DN.toString(), (String) ldapServerConfiguration.get(LdapConstants.BASE_DN));
        task.setLdapWorkflowContext(ldapWorkFlowContext);
    }

    /**
     * Delete the given proxy account.
     * 
     * @param proxyAccountDn
     *            the proxy account to delete.
     * @return the result of the delete operation.
     */
    public Boolean deleteProxyAccount(final String proxyAccountDn) {
        return identityManagementProxy.deleteProxyAgentAccount(proxyAccountDn);
    }

    /**
     * Get LDAP server port.
     * 
     * @param isTls
     *            true if TLS is used, false otherwise.
     * @param tlsMode
     *            the TLS mode (STARTTLS or LDAPS) meaningful only if TLS is used.
     * @param ldapServerConfiguration
     *            the LDAP server configuration parameters.
     * @return the LDAP server port.
     */
    private Integer getLdapServerPort(final Boolean isTls, final String tlsMode, final Map<String, Object> ldapServerConfiguration) {
        Integer ldapPort = null;

        if (!isTls) {
            ldapPort = Integer.valueOf((String) ldapServerConfiguration.get(LdapConstants.TLS_PORT));
        } else if (tlsMode.equalsIgnoreCase((String) LdapConstants.STARTTLS)) {
            ldapPort = Integer.valueOf((String) ldapServerConfiguration.get(LdapConstants.TLS_PORT));
        } else if (tlsMode.equalsIgnoreCase((String) LdapConstants.LDAPS)) {
            ldapPort = Integer.valueOf((String) ldapServerConfiguration.get(LdapConstants.LDAPS_PORT));
        }

        return ldapPort;
    }

    /**
     * Get primary LDAP server IP address.
     * 
     * @param isIPv6
     *            true if node has IPv6 address, false if node has IPv4 address.
     * @param ldapServerConfiguration
     *            the LDAP server configuration parameters.
     * @return the primary LDAP server IP address.
     */
    private String getPrimaryLdapServerIPAddress(final boolean isIPv6, final Map<String, Object> ldapServerConfiguration) {
        if (isIPv6) {
            return (String) ldapServerConfiguration.get(LdapConstants.LDAP_IPV6_ADDRESS);
        }
        return (String) ldapServerConfiguration.get(LdapConstants.LDAP_IPV4_ADDRESS);
    }

    /**
     * Get secondary (fallback) LDAP server IP address.
     * 
     * @param isIPv6
     *            true if node has IPv6 address, false if node has IPv4 address.
     * @param ldapServerConfiguration
     *            the LDAP server configuration parameters.
     * @return the secondary (fallback) LDAP server IP address.
     */
    private String getSecondaryLdapServerIPAddress(final boolean isIPv6, final Map<String, Object> ldapServerConfiguration) {
        if (isIPv6) {
            return (String) ldapServerConfiguration.get(LdapConstants.FALLBACK_LDAP_IPV6_ADDRESS);
        }
        return (String) ldapServerConfiguration.get(LdapConstants.FALLBACK_LDAP_IPV4_ADDRESS);
    }

}
