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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.data.moget.MOGetService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountData;
import com.ericsson.oss.itpf.security.identitymgmtservices.comaa.ConnectionData;
import com.ericsson.oss.itpf.security.identitymgmtservices.comaa.LdapAddress;

@RunWith(MockitoJUnitRunner.class)
public class LdapConfigurationProviderTest {
    private final String PRIMARY_LDAP_IPV4_ADDRESS = "192.168.0.129";
    private final String FALLBACK_LDAP_IPV4_ADDRESS = "192.168.0.130";
    private final String PRIMARY_LDAP_IPV6_ADDRESS = "2001:cdba:0:0:0:0:3257:9652";
    private final String FALLBACK_LDAP_IPV6_ADDRESS = "2001:cdba:0:0:0:0:3257:9651";
    private final String STARTTLS = "STARTTLS";
    private final String LDAPS = "LDAPS";
    private final Integer LDAP_STARTTLS_PORT = 1389;
    private final Integer LDAP_LDAPS_PORT = 1636;

    @InjectMocks
    LdapConfigurationProvider ldapConfigurationProvider;

    @Mock
    NormalizableNodeReference normalizedReference;

    @Mock
    NscsNodeUtility nodeUtility;

    @Mock
    MOGetService moGetService;

    @Mock
    LdapConfigurationProxy ldapConfigurationProxy;

    @Mock
    IdentityManagementProxy identityManagementProxy;

    @Mock
    PlatformSpecificConfigurationProvider platformSpecificConfigurationProvider;

    private ConnectionData connectionData;

    private ProxyAgentAccountData proxyAgentAccountData;

    @Before
    public void setUp() {
        LdapAddress ipv4AddressData = new LdapAddress(PRIMARY_LDAP_IPV4_ADDRESS, FALLBACK_LDAP_IPV4_ADDRESS);
        LdapAddress ipv6AddressData = new LdapAddress(PRIMARY_LDAP_IPV6_ADDRESS, FALLBACK_LDAP_IPV6_ADDRESS);
        connectionData = new ConnectionData(ipv4AddressData, ipv6AddressData, LDAP_STARTTLS_PORT, LDAP_LDAPS_PORT);
        Mockito.when(ldapConfigurationProxy.getConnectionData()).thenReturn(connectionData);
    }

    /**
     * Test Method to get the Primary Ldap Server IPv4 Address
     */
    @Test
    public void testGetPrimaryLdapServerIPv4Address() {
        Mockito.when(nodeUtility.hasNodeIPv6Address(normalizedReference)).thenReturn(false);
        String ipAddress = ldapConfigurationProvider.getPrimaryLdapServerIPAddress(normalizedReference);
        assertEquals(PRIMARY_LDAP_IPV4_ADDRESS, ipAddress);
    }

    /**
     * Test Method to get the Secondary Ldap Server IPv4 Address
     */
    @Test
    public void testGetSecondaryLdapServerIPv4Address() {
        Mockito.when(nodeUtility.hasNodeIPv6Address(normalizedReference)).thenReturn(false);
        String ipAddress = ldapConfigurationProvider.getSecondaryLdapServerIPAddress(normalizedReference);
        assertEquals(FALLBACK_LDAP_IPV4_ADDRESS, ipAddress);
    }

    /**
     * Test Method to get the Primary Ldap Server IPv6 Address
     */
    @Test
    public void testGetPrimaryLdapServerIPv6Address() {
        Mockito.when(nodeUtility.hasNodeIPv6Address(normalizedReference)).thenReturn(true);
        String ipAddress = ldapConfigurationProvider.getPrimaryLdapServerIPAddress(normalizedReference);
        assertEquals(PRIMARY_LDAP_IPV6_ADDRESS, ipAddress);
    }

    /**
     * Test Method to get the Secondary Ldap Server IPv6 Address
     */
    @Test
    public void testGetSecondaryLdapServerIPv6Address() {
        Mockito.when(nodeUtility.hasNodeIPv6Address(normalizedReference)).thenReturn(true);
        String ipAddress = ldapConfigurationProvider.getSecondaryLdapServerIPAddress(normalizedReference);
        assertEquals(FALLBACK_LDAP_IPV6_ADDRESS, ipAddress);
    }

    /**
     * Test Method to get the Ldap StartTls Port
     */
    @Test
    public void testGetLdapTLSPort() {
        Integer ldapPort = ldapConfigurationProvider.getLdapPort(true, STARTTLS);
        assertEquals(LDAP_STARTTLS_PORT, ldapPort);
    }

    /**
     * Test Method to get the LDAPS Port
     */
    @Test
    public void testGetLDAPSPort() {
        Integer ldapPort = ldapConfigurationProvider.getLdapPort(true, LDAPS);
        assertEquals(LDAP_LDAPS_PORT, ldapPort);
    }

    /**
     * Test Method to check the Ldap port when useTls is false.
     */
    public void testUseTlsFlasePort() {
        Integer ldapPort = ldapConfigurationProvider.getLdapPort(false, LDAPS);
        assertEquals(LDAP_STARTTLS_PORT, ldapPort);
    }

    /**
     * Test Method to get the Total Ldap Server Configuration Details
     */
    @Test
    public void testGetLdapServerConfiguration() {
        proxyAgentAccountData = new ProxyAgentAccountData("cn=ProxyAccount_4,ou=Profiles,dc=apache,dc=com", "osz45rph");
        Mockito.when(identityManagementProxy.createProxyAgentAccount()).thenReturn(proxyAgentAccountData);
        Mockito.when(platformSpecificConfigurationProvider.getBaseDN()).thenReturn("dc=apache,dc=com");
        Map<String, Object> ldapConfiguration = ldapConfigurationProvider.getLdapServerConfiguration();
        assertNotNull(ldapConfiguration);
    }

    @Test
    public void testSuccessfulDeleteProxyAccount() {
        String proxyAccountDn = "cn=ProxyAccount_4,ou=Profiles,dc=apache,dc=com";
        Mockito.when(identityManagementProxy.deleteProxyAgentAccount(proxyAccountDn)).thenReturn(true);
        Boolean deleted = ldapConfigurationProvider.deleteProxyAccount(proxyAccountDn);
        assertEquals(true, deleted);
    }

    @Test
    public void testFailedDeleteProxyAccount() {
        String proxyAccountDn = "cn=ProxyAccount_4,ou=Profiles,dc=apache,dc=com";
        Mockito.when(identityManagementProxy.deleteProxyAgentAccount(proxyAccountDn)).thenReturn(false);
        Boolean deleted = ldapConfigurationProvider.deleteProxyAccount(proxyAccountDn);
        assertEquals(false, deleted);
    }

}
