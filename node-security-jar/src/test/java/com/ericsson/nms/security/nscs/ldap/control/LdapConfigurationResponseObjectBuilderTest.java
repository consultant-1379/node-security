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

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.ldap.utility.LdapConstants;

@RunWith(MockitoJUnitRunner.class)
public class LdapConfigurationResponseObjectBuilderTest {

    @InjectMocks
    LdapConfigurationResponseObjectBuilder ldapConfigurationResponseObjectBuilder;

    private Map<String, Object> ldapConfiguration;

    @Before
    public void setUp() {
        ldapConfiguration = new HashMap<String, Object>();
        ldapConfiguration.put(LdapConstants.TLS_PORT, "1389");
        ldapConfiguration.put(LdapConstants.LDAPS_PORT, "1636");
        ldapConfiguration.put(LdapConstants.FALLBACK_LDAP_IPV6_ADDRESS, "2001:cdba:0:0:0:0:3257:9652");
        ldapConfiguration.put(LdapConstants.LDAP_IPV6_ADDRESS, "2001:cdba:0:0:0:0:3257:9651");
        ldapConfiguration.put(LdapConstants.FALLBACK_LDAP_IPV4_ADDRESS, "192.168.0.129");
        ldapConfiguration.put(LdapConstants.LDAP_IPV4_ADDRESS, "192.168.0.130");
        ldapConfiguration.put(LdapConstants.BIND_PASSWORD, "tbz64iwu");
        ldapConfiguration.put(LdapConstants.BIND_DN, "cn=ProxyAccount_3,ou=Profiles,dc=apache,dc=com");
        ldapConfiguration.put(LdapConstants.BASE_DN, "dc=apache,dc=com");
    }

    /**
     * Test Method to Build the NscsNameValueCommandResponse using the Ldap Configuation Details
     */
    @Test
    public void testBuild() {
        NscsCommandResponse commandResponse = ldapConfigurationResponseObjectBuilder.build(ldapConfiguration);
        assertTrue(commandResponse.isNameValueResponseType());
    }

}
