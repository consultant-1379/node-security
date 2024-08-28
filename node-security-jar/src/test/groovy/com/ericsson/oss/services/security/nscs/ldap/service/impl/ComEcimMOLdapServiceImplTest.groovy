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
package com.ericsson.oss.services.security.nscs.ldap.service.impl

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoException
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.attribute.CommonLdapConfigurationTask
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys
import com.ericsson.oss.services.security.nscs.utils.ComEcimNodeDataSetup

import spock.lang.Unroll

class ComEcimMOLdapServiceImplTest extends ComEcimNodeDataSetup {

    private static final String USER_LABEL_UNDER_TEST = "test groovy"

    private static final String BIND_CRD_UNDER_TEST = "xso53wwv"
    private static final String BIND_DN_UNDER_TEST = "cn=ProxyAccount_2,ou=proxyagent,ou=com,dc=acme,dc=com"
    private static final String BASE_DN_UNDER_TEST = "dc=acme,dc=com"
    private static final Integer LDAP_SERVER_PORT_UNDER_TEST = 1636
    private static final Integer LDAP_SERVER_PORT_UNDER_TEST_LDAP = 1389
    private static final String LDAP_IP_ADDRESS_UNDER_TEST = "10.129.10.246"
    private static final String FALLBACK_LDAP_IP_ADDRESS_UNDER_TEST = "10.129.10.247"

    private static final String PREVIOUS_BASE_DN_UNDER_TEST = "dc=other,dc=com"
    private static final String PREVIOUS_BIND_DN_UNDER_TEST = "cn=ProxyAccount_1,ou=proxyagent,ou=com,dc=acme,dc=com"
    private static final String PREVIOUS_BIND_CRD_UNDER_TEST = "abcdefgh"
    private static final String PREVIOUS_LDAP_IP_ADDRESS_UNDER_TEST = "1.2.3.4"
    private static final String PREVIOUS_FALLBACK_LDAP_IP_ADDRESS_UNDER_TEST = "1.2.3.5"

    @ObjectUnderTest
    private ComEcimMOLdapServiceImpl comEcimMOLdapServiceImpl

    private nodeName = "LTE01dg2ERBS00001"
    private CommonLdapConfigurationTask task
    private NormalizableNodeReference normalizable = mock(NormalizableNodeReference)

    def setup() {
        NscsCMReaderService.deploymentEnv = NscsCMReaderService.DeploymentEnvironment.PRODUCTION
        normalizable.getFdn() >> "MeContext=" + nodeName
        normalizable.getTargetCategory() >> "NODE"
        normalizable.getNeType() >> RADIONODE_TARGET_TYPE
        normalizable.getOssModelIdentity() >> RADIONODE_TARGET_MODEL_IDENTITY
        task = new CommonLdapConfigurationTask(nodeName)
        task.setUserLabel(USER_LABEL_UNDER_TEST)
    }

    def 'object under test injection'() {
        expect:
        comEcimMOLdapServiceImpl != null
    }

    @Unroll
    def 'validate task for is TLS #isTls'() {
        given:
        task.setIsTls(isTls)
        when:
        comEcimMOLdapServiceImpl.validateLdapConfiguration(task, normalizable)
        then:
        notThrown(IllegalArgumentException.class)
        where:
        isTls << [true, false]
    }

    def 'no ldap with MeContext and ManagedElement' () {
        given: "node created with MeContext"
        createNodeWithMeContext(RADIONODE_TARGET_TYPE, RADIONODE_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(RADIONODE_TARGET_TYPE, nodeName)
        when:
        comEcimMOLdapServiceImpl.ldapConfigure(task, normalizable)
        then: "exception should be thrown"
        thrown(MissingMoException.class)
    }

    @Unroll
    def 'ldap with bind DN #binddn with MeContext and ManagedElement and tlsMode #tlsmode' () {
        given: "node created with MeContext"
        createNodeWithMeContext(RADIONODE_TARGET_TYPE, RADIONODE_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(RADIONODE_TARGET_TYPE, nodeName)
        and: "with SystemFunctions under ManagedElement"
        createSystemFunctionsUnderManagedElement()
        and: "with SecM under SystemFunctions"
        createSecMUnderSystemFunctions()
        and: "with UserManagement under SecM"
        createUserManagementUnderSecM()
        and: "with LdapAuthenticationMethod under UserManagement"
        createLdapAuthenticationMethodUnderUserManagement()
        and: "with Ldap under LdapAuthenticationMethod with bind DN #binddn"
        createLdapUnderLdapAuthenticationMethod(binddn)
        and:
        setCommonLdapConfigurationTask(task, tlsmode)
        when: "task is processed"
        comEcimMOLdapServiceImpl.ldapConfigure(task, normalizable)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "ldap should have been updated"
        def String ldapFdn = "MeContext="+nodeName+",ManagedElement="+nodeName+",SystemFunctions=1,SecM=1,UserManagement=1,LdapAuthenticationMethod=1,Ldap=1"
        checkLdap(ldapFdn, tlsmode)
        and: "task should contain previous bind DN in its LDAP workflow context"
        task.getLdapWorkflowContext().get(WorkflowOutputParameterKeys.PREVIOUS_BIND_DN.toString()) == prevbinddn
        where:
        tlsmode << [
            "LDAPS",
            "STARTTLS",
            "LDAPS",
            "STARTTLS",
            "LDAPS",
            "STARTTLS"
        ]
        binddn << [
            null,
            null,
            "",
            "",
            PREVIOUS_BIND_DN_UNDER_TEST,
            PREVIOUS_BIND_DN_UNDER_TEST
        ]
        prevbinddn << [
            null,
            null,
            null,
            null,
            PREVIOUS_BIND_DN_UNDER_TEST,
            PREVIOUS_BIND_DN_UNDER_TEST
        ]
    }

    private setCommonLdapConfigurationTask(CommonLdapConfigurationTask task, String tlsMode) {
        task.setTlsMode(tlsMode);
        task.setLdapWorkflowContext(buildLdapWorkflowContext(tlsMode));
    }

    private Map<String, Serializable> buildLdapWorkflowContext(String tlsMode) {
        final Map<String, Serializable> ldapWorkflowContext = new HashMap();
        ldapWorkflowContext.put(WorkflowOutputParameterKeys.BASE_DN.toString(), BASE_DN_UNDER_TEST);
        ldapWorkflowContext.put(WorkflowOutputParameterKeys.BIND_DN.toString(), BIND_DN_UNDER_TEST);
        ldapWorkflowContext.put(WorkflowOutputParameterKeys.BIND_PASSWORD.toString(), BIND_CRD_UNDER_TEST);
        ldapWorkflowContext.put(WorkflowOutputParameterKeys.LDAP_IP_ADDRESS.toString(), LDAP_IP_ADDRESS_UNDER_TEST);
        ldapWorkflowContext.put(WorkflowOutputParameterKeys.FALLBACK_LDAP_IP_ADDRESS.toString(), FALLBACK_LDAP_IP_ADDRESS_UNDER_TEST);
        if (tlsMode == "LDAPS") {
            ldapWorkflowContext.put(WorkflowOutputParameterKeys.LDAP_SERVER_PORT.toString(), LDAP_SERVER_PORT_UNDER_TEST);
        } else {
            ldapWorkflowContext.put(WorkflowOutputParameterKeys.LDAP_SERVER_PORT.toString(), LDAP_SERVER_PORT_UNDER_TEST_LDAP);
        }
        return ldapWorkflowContext;
    }

    boolean checkLdap(String ldapFdn, String tlsMode) {
        ManagedObject ldapMO = findMoByFdn(ldapFdn)
        if (ldapMO == null) {
            return false;
        }
        if (ldapMO.getAttribute("baseDn") != BASE_DN_UNDER_TEST) {
            return false;
        }
        if (ldapMO.getAttribute("bindDn") != BIND_DN_UNDER_TEST) {
            return false;
        }
        if (ldapMO.getAttribute("ldapIpAddress") != LDAP_IP_ADDRESS_UNDER_TEST) {
            return false;
        }
        if (ldapMO.getAttribute("fallbackLdapIpAddress") != FALLBACK_LDAP_IP_ADDRESS_UNDER_TEST) {
            return false;
        }
        if (tlsMode == "LDAPS") {
            if (ldapMO.getAttribute("serverPort") != LDAP_SERVER_PORT_UNDER_TEST) {
                return false;
            }
        } else {
            if (ldapMO.getAttribute("serverPort") != LDAP_SERVER_PORT_UNDER_TEST_LDAP) {
                return false;
            }
        }

        return true
    }
}
