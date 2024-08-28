package com.ericsson.oss.services.security.nscs.workflow.task.util

import static org.junit.Assert.*

import javax.inject.Inject

import org.junit.Test

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.nms.security.nscs.ldap.control.IdentityManagementProxy
import com.ericsson.nms.security.nscs.ldap.control.LdapConfigurationProvider
import com.ericsson.nms.security.nscs.ldap.utility.LdapConstants
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.attribute.CommonLdapConfigurationTask
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys

import spock.lang.Unroll

class LdapWorkflowHelperTest extends CdiSpecification {

    private static final String BASE_DN_UNDER_TEST = "dc=acme,dc=com"
    private static final String BIND_CRD_UNDER_TEST = "xso53wwv"
    private static final String BIND_DN_UNDER_TEST = "cn=ProxyAccount_2,ou=proxyagent,ou=com,dc=acme,dc=com"
    private static final String PRIMARY_LDAP_IPV4_ADDRESS_UNDER_TEST = "192.168.0.129"
    private static final String FALLBACK_LDAP_IPV4_ADDRESS_UNDER_TEST = "192.168.0.130"
    private static final String PRIMARY_LDAP_IPV6_ADDRESS_UNDER_TEST = "2001:cdba:0:0:0:0:3257:9652"
    private static final String FALLBACK_LDAP_IPV6_ADDRESS_UNDER_TEST = "2001:cdba:0:0:0:0:3257:9651"
    private static final String LDAP_STARTTLS_PORT_UNDER_TEST = "1389"
    private static final String LDAP_LDAPS_PORT_UNDER_TEST = "1636"
    private static final Integer LDAP_PORT_UNDER_TEST = 1389
    private static final Integer LDAPS_PORT_UNDER_TEST = 1636
    
    @ObjectUnderTest
    private LdapWorkflowHelper ldapWorkflowHelper

    @MockedImplementation
    private LdapConfigurationProvider ldapConfigurationProvider

    @MockedImplementation
    private NscsNodeUtility nscsNodeUtility

    @MockedImplementation
    private IdentityManagementProxy identityManagementProxy

    private nodeName = "NODENAME"
    private CommonLdapConfigurationTask task
    private NormalizableNodeReference normalizable = mock(NormalizableNodeReference)

    def setup() {
        task = new CommonLdapConfigurationTask(nodeName)
        final Map<String, Object> ldapParameters = new HashMap<String, Object>();
        ldapParameters.put(LdapConstants.BASE_DN, BASE_DN_UNDER_TEST)
        ldapParameters.put(LdapConstants.BIND_DN, BIND_DN_UNDER_TEST)
        ldapParameters.put(LdapConstants.BIND_PASSWORD, BIND_CRD_UNDER_TEST)
        ldapParameters.put(LdapConstants.LDAP_IPV4_ADDRESS, PRIMARY_LDAP_IPV4_ADDRESS_UNDER_TEST)
        ldapParameters.put(LdapConstants.FALLBACK_LDAP_IPV4_ADDRESS, FALLBACK_LDAP_IPV4_ADDRESS_UNDER_TEST)
        ldapParameters.put(LdapConstants.LDAP_IPV6_ADDRESS, PRIMARY_LDAP_IPV6_ADDRESS_UNDER_TEST)
        ldapParameters.put(LdapConstants.FALLBACK_LDAP_IPV6_ADDRESS, FALLBACK_LDAP_IPV6_ADDRESS_UNDER_TEST)
        ldapParameters.put(LdapConstants.TLS_PORT, LDAP_STARTTLS_PORT_UNDER_TEST)
        ldapParameters.put(LdapConstants.LDAPS_PORT, LDAP_LDAPS_PORT_UNDER_TEST)
        ldapConfigurationProvider.getLdapServerConfiguration() >> ldapParameters
    }

    def 'object under test'() {
        expect:
        ldapWorkflowHelper != null
    }

    @Unroll
    def 'get ldap configuration for is TLS #istls and TLS mode #tlsmode and is IPv6 #isipv6'() {
        given:
        task.setIsTls(istls)
        task.setTlsMode(tlsmode)
        and:
        nscsNodeUtility.hasNodeIPv6Address(_ as NormalizableNodeReference) >> isipv6
        when:
        ldapWorkflowHelper.getLdapConfiguration(task, normalizable)
        then:
        noExceptionThrown()
        and:
        task.getLdapWorkflowContext().get(WorkflowOutputParameterKeys.BASE_DN.toString()) == BASE_DN_UNDER_TEST
        task.getLdapWorkflowContext().get(WorkflowOutputParameterKeys.BIND_DN.toString()) == BIND_DN_UNDER_TEST
        task.getLdapWorkflowContext().get(WorkflowOutputParameterKeys.BIND_PASSWORD.toString()) == BIND_CRD_UNDER_TEST
        task.getLdapWorkflowContext().get(WorkflowOutputParameterKeys.LDAP_IP_ADDRESS.toString()) == expectedipaddr
        task.getLdapWorkflowContext().get(WorkflowOutputParameterKeys.FALLBACK_LDAP_IP_ADDRESS.toString()) == expectedfallbackipaddr
        task.getLdapWorkflowContext().get(WorkflowOutputParameterKeys.LDAP_SERVER_PORT.toString()) == expectedserverport
        where:
        istls << [
            true,
            false,
            false,
            true,
            true,
            false,
            false,
            true
        ]
        tlsmode << [
            "LDAPS",
            "STARTTLS",
            "LDAPS",
            "STARTTLS",
            "LDAPS",
            "STARTTLS",
            "LDAPS",
            "STARTTLS"
        ]
        isipv6 << [
            true,
            true,
            true,
            true,
            false,
            false,
            false,
            false
        ]
        expectedipaddr << [
            PRIMARY_LDAP_IPV6_ADDRESS_UNDER_TEST,
            PRIMARY_LDAP_IPV6_ADDRESS_UNDER_TEST,
            PRIMARY_LDAP_IPV6_ADDRESS_UNDER_TEST,
            PRIMARY_LDAP_IPV6_ADDRESS_UNDER_TEST,
            PRIMARY_LDAP_IPV4_ADDRESS_UNDER_TEST,
            PRIMARY_LDAP_IPV4_ADDRESS_UNDER_TEST,
            PRIMARY_LDAP_IPV4_ADDRESS_UNDER_TEST,
            PRIMARY_LDAP_IPV4_ADDRESS_UNDER_TEST
        ]
        expectedfallbackipaddr << [
            FALLBACK_LDAP_IPV6_ADDRESS_UNDER_TEST,
            FALLBACK_LDAP_IPV6_ADDRESS_UNDER_TEST,
            FALLBACK_LDAP_IPV6_ADDRESS_UNDER_TEST,
            FALLBACK_LDAP_IPV6_ADDRESS_UNDER_TEST,
            FALLBACK_LDAP_IPV4_ADDRESS_UNDER_TEST,
            FALLBACK_LDAP_IPV4_ADDRESS_UNDER_TEST,
            FALLBACK_LDAP_IPV4_ADDRESS_UNDER_TEST,
            FALLBACK_LDAP_IPV4_ADDRESS_UNDER_TEST
        ]
        expectedserverport << [
            LDAPS_PORT_UNDER_TEST,
            LDAP_PORT_UNDER_TEST,
            LDAP_PORT_UNDER_TEST,
            LDAP_PORT_UNDER_TEST,
            LDAPS_PORT_UNDER_TEST,
            LDAP_PORT_UNDER_TEST,
            LDAP_PORT_UNDER_TEST,
            LDAP_PORT_UNDER_TEST
        ]
    }
    
    @Unroll
    def 'delete proxy account with result #isdeleted'() {
        given:
        identityManagementProxy.deleteProxyAgentAccount(_ as String) >> isdeleted
        when:
        def isDeleted = ldapWorkflowHelper.deleteProxyAccount(BIND_DN_UNDER_TEST)
        then:
        isDeleted == isdeleted
        where:
        isdeleted << [true, false]
    }
}
