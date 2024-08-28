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
package com.ericsson.nms.security.nscs.ldap.control;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.ldap.utility.LdapConstants;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountData;
import com.ericsson.oss.itpf.security.identitymgmtservices.comaa.ConnectionData;

/**
 * <p>
 * Provides Ldap Server Configuration Details
 * </p>
 * 
 * @author xsrirko
 * 
 */
public class LdapConfigurationProvider {

    @Inject
    private IdentityManagementProxy identityManagementProxy;

    @Inject
    private PlatformSpecificConfigurationProvider platformSpecificConfigurationProvider;

    @Inject
    private LdapConfigurationProxy ldapConfigurationProxy;

    @Inject
    private NscsNodeUtility nscsNodeUtility;

    /**
     * This method provides Ldap Server Primary IP address by validating the node (IPv4/IPv6)
     * 
     * @param normalizedReference
     * @return
     */
    public String getPrimaryLdapServerIPAddress(final NormalizableNodeReference normalizedReference) {
        if (hasNodeIPv6Address(normalizedReference)) {
            return ldapConfigurationProxy.getConnectionData().getIpv6AddressData().getPrimary();
        } else {
            return ldapConfigurationProxy.getConnectionData().getIpv4AddressData().getPrimary();
        }
    }

    /**
     * This method provides Ldap Server Secondary IP address by validating the node (IPv4/IPv6)
     * 
     * @param normalizedReference
     * @return
     */
    public String getSecondaryLdapServerIPAddress(final NormalizableNodeReference normalizedReference) {
        if (hasNodeIPv6Address(normalizedReference)) {
            return ldapConfigurationProxy.getConnectionData().getIpv6AddressData().getFallback();
        } else {
            return ldapConfigurationProxy.getConnectionData().getIpv4AddressData().getFallback();
        }
    }

    /**
     * This method provides Ldap Server Port
     * 
     * @param useTls
     * @param tlsMode
     * @return
     */
    public Integer getLdapPort(final Boolean useTls, final String tlsMode) {
        Integer ldapPort = null;

        if (!useTls) {
            ldapPort = ldapConfigurationProxy.getConnectionData().getLdapTlsPort();
        } else if (useTls && tlsMode.equalsIgnoreCase((String) LdapConstants.STARTTLS)) {
            ldapPort = ldapConfigurationProxy.getConnectionData().getLdapTlsPort();
        } else if (useTls && tlsMode.equalsIgnoreCase((String) LdapConstants.LDAPS)) {
            ldapPort = ldapConfigurationProxy.getConnectionData().getLdapsPort();
        }

        return ldapPort;
    }

    /**
     * This method provides the Total Ldap Server Configuration Details
     * 
     * @return
     */
    public Map<String, Object> getLdapServerConfiguration() {
        final ProxyAgentAccountData proxyAgentAccountData = identityManagementProxy.createProxyAgentAccount();
        final ConnectionData connectionData = ldapConfigurationProxy.getConnectionData();

        final Map<String, Object> ldapParameters = new HashMap<String, Object>();
        ldapParameters.put(LdapConstants.BIND_DN, proxyAgentAccountData.getUserDN());
        ldapParameters.put(LdapConstants.BIND_PASSWORD, proxyAgentAccountData.getUserPassword());
        ldapParameters.put(LdapConstants.BASE_DN, platformSpecificConfigurationProvider.getBaseDN());
        ldapParameters.put(LdapConstants.LDAP_IPV4_ADDRESS, connectionData.getIpv4AddressData().getPrimary());
        ldapParameters.put(LdapConstants.FALLBACK_LDAP_IPV4_ADDRESS, connectionData.getIpv4AddressData().getFallback());
        ldapParameters.put(LdapConstants.LDAP_IPV6_ADDRESS, connectionData.getIpv6AddressData().getPrimary());
        ldapParameters.put(LdapConstants.FALLBACK_LDAP_IPV6_ADDRESS, connectionData.getIpv6AddressData().getFallback());
        ldapParameters.put(LdapConstants.TLS_PORT, String.valueOf(connectionData.getLdapTlsPort()));
        ldapParameters.put(LdapConstants.LDAPS_PORT, String.valueOf(connectionData.getLdapsPort()));

        return ldapParameters;
    }

    /**
     * Delete the specified proxy account.
     * 
     * @param proxyAccountDn
     *            the proxy account.
     * @return true if proxy account successfully deleted, false otherwise.
     */
    public Boolean deleteProxyAccount(final String proxyAccountDn) {
        return identityManagementProxy.deleteProxyAgentAccount(proxyAccountDn);
    }

    /**
     * Check if given node has a valid IPv6 address.
     * 
     * @param nodeRef
     *            the node reference
     * @return true if valid IPv6 address, false otherwise.
     */
    private boolean hasNodeIPv6Address(final NormalizableNodeReference nodeRef) {
        boolean hasIPv6Address = false;
        try {
            hasIPv6Address = nscsNodeUtility.hasNodeIPv6Address(nodeRef);
        } catch (IllegalArgumentException e) {
        }
        return hasIPv6Address;
    }
}
