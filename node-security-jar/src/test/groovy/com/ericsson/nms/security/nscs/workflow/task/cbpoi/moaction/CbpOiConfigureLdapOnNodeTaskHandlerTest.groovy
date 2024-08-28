/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.workflow.task.cbpoi.moaction

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoException
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cbpoi.moaction.CbpOiConfigureLdapOnNodeTask
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.moaction.ComEcimConfigureLdapOnNodeTask
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys
import com.ericsson.oss.services.security.nscs.utils.CbpOiNodeDataSetup

import spock.lang.Unroll

class CbpOiConfigureLdapOnNodeTaskHandlerTest extends CbpOiNodeDataSetup {

    private static final String nodeName = "LTE44dg2ERBS00001"

    private static final String TLS_MODE_KEY_UNDER_TEST = "LDAPS"
    private static final Boolean USE_TLS_KEY_UNDER_TEST = true
    private static final String USER_LABEL_KEY_UNDER_TEST = "test groovy"

    private static final String BIND_PASSWORD_UNDER_TEST = "xso53wwv"
    private static final String BIND_DN_UNDER_TEST = "cn=ProxyAccount_1,ou=proxyagent,ou=com,dc=rani-venm-1,dc=com"
    private static final String BASE_DN_UNDER_TEST = "dc=rani-venm-1,dc=com"
    private static final Integer LDAP_SERVER_PORT_UNDER_TEST = 1636
    private static final Integer LDAP_SERVER_PORT_UNDER_TEST_LDAP = 1389
    private static final String LDAP_IP_ADDRESS_UNDER_TEST = "10.129.10.246"
    private static final String FALLBACK_LDAP_IP_ADDRESS_UNDER_TEST = "10.129.10.247"

    @ObjectUnderTest
    CbpOiConfigureLdapOnNodeTaskHandler cbpOiConfigureLdapOnNodeTaskHandler

    private CbpOiConfigureLdapOnNodeTask cbpOiConfigureLdapOnNodeTask
    private Map<String, Serializable> ldapWorkFlowContext = new HashMap<>()

    def setup() {
        cbpOiConfigureLdapOnNodeTask = new CbpOiConfigureLdapOnNodeTask()
        cbpOiConfigureLdapOnNodeTask.parameters.put(CbpOiConfigureLdapOnNodeTask.NODE_FDN_PARAMETER, "NetworkElement=" + nodeName)
        cbpOiConfigureLdapOnNodeTask.parameters.put(ComEcimConfigureLdapOnNodeTask.TLS_MODE_KEY ,TLS_MODE_KEY_UNDER_TEST)
        cbpOiConfigureLdapOnNodeTask.parameters.put(ComEcimConfigureLdapOnNodeTask.USE_TLS_KEY,USE_TLS_KEY_UNDER_TEST)
        cbpOiConfigureLdapOnNodeTask.parameters.put(ComEcimConfigureLdapOnNodeTask.USER_LABEL_KEY, USER_LABEL_KEY_UNDER_TEST)

        ldapWorkFlowContext.put(WorkflowParameterKeys.BIND_PASSWORD.toString(), BIND_PASSWORD_UNDER_TEST)
        ldapWorkFlowContext.put(WorkflowParameterKeys.BIND_DN.toString(), BIND_DN_UNDER_TEST)
        ldapWorkFlowContext.put(WorkflowParameterKeys.BASE_DN.toString(), BASE_DN_UNDER_TEST)
        ldapWorkFlowContext.put(WorkflowParameterKeys.LDAP_SERVER_PORT.toString(), LDAP_SERVER_PORT_UNDER_TEST)
        ldapWorkFlowContext.put(WorkflowParameterKeys.LDAP_IP_ADDRESS.toString(), LDAP_IP_ADDRESS_UNDER_TEST)
        ldapWorkFlowContext.put(WorkflowParameterKeys.FALLBACK_LDAP_IP_ADDRESS.toString(), FALLBACK_LDAP_IP_ADDRESS_UNDER_TEST)

        cbpOiConfigureLdapOnNodeTask.parameters.put("ldapWorkFlowContext",ldapWorkFlowContext)

        NscsCMReaderService.deploymentEnv = NscsCMReaderService.DeploymentEnvironment.PRODUCTION
    }

    def 'object under test injection' () {
        expect:
        cbpOiConfigureLdapOnNodeTaskHandler != null
    }

    @Unroll
    def 'configure with MeContext and ManagedElement and system - ldap mode #ldapmode' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with system under ManagedElement"
        createSystemUnderManagedElement()
        and: "task with ldap mode #ldapmode"
        cbpOiConfigureLdapOnNodeTask.parameters.put(ComEcimConfigureLdapOnNodeTask.TLS_MODE_KEY , ldapmode)
        if (ldapmode == "LDAP") {
            ldapWorkFlowContext.put(WorkflowParameterKeys.LDAP_SERVER_PORT.toString(), LDAP_SERVER_PORT_UNDER_TEST_LDAP)
        }
        when: "task is processed"
        cbpOiConfigureLdapOnNodeTaskHandler.processTask(cbpOiConfigureLdapOnNodeTask)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "ldap MO hierarchy should be created as expected under system MO"
        def String systemLdapFdn = "MeContext="+nodeName+",ManagedElement="+nodeName+",system=1,"+SYSTEM_LDAP_SCOPED_TYPE+"=1"
        checkSystemLdapHierarchy(systemLdapFdn, ldapmode)
        where:
        ldapmode << ["LDAPS", "LDAP"]
    }

    @Unroll
    def 'configure with ManagedElement and system - ldap mode #ldapmode' () {
        given: "node created with ManagedElement"
        createNodeWithManagedElement(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with system under ManagedElement"
        createSystemUnderManagedElement()
        and: "task with ldap mode #ldapmode"
        cbpOiConfigureLdapOnNodeTask.parameters.put(ComEcimConfigureLdapOnNodeTask.TLS_MODE_KEY , ldapmode)
        if (ldapmode == "LDAP") {
            ldapWorkFlowContext.put(WorkflowParameterKeys.LDAP_SERVER_PORT.toString(), LDAP_SERVER_PORT_UNDER_TEST_LDAP)
        }
        when: "task is processed"
        cbpOiConfigureLdapOnNodeTaskHandler.processTask(cbpOiConfigureLdapOnNodeTask)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "ldap MO hierarchy should be created as expected under system MO"
        def String systemLdapFdn = "ManagedElement="+nodeName+",system=1,"+SYSTEM_LDAP_SCOPED_TYPE+"=1"
        checkSystemLdapHierarchy(systemLdapFdn, ldapmode) == true
        where:
        ldapmode << ["LDAPS", "LDAP"]
    }

    @Unroll
    def 'configure with MeContext and system - ldap mode #ldapmode' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with system under MeContext"
        createSystemUnderMeContext()
        and: "task with ldap mode #ldapmode"
        cbpOiConfigureLdapOnNodeTask.parameters.put(ComEcimConfigureLdapOnNodeTask.TLS_MODE_KEY , ldapmode)
        if (ldapmode == "LDAP") {
            ldapWorkFlowContext.put(WorkflowParameterKeys.LDAP_SERVER_PORT.toString(), LDAP_SERVER_PORT_UNDER_TEST_LDAP)
        }
        when: "task is processed"
        cbpOiConfigureLdapOnNodeTaskHandler.processTask(cbpOiConfigureLdapOnNodeTask)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "ldap MO hierarchy should be created as expected under system MO"
        def String systemLdapFdn = "MeContext="+nodeName+",system=1,"+SYSTEM_LDAP_SCOPED_TYPE+"=1"
        checkSystemLdapHierarchy(systemLdapFdn, ldapmode) == true
        where:
        ldapmode << ["LDAPS", "LDAP"]
    }

    @Unroll
    def 'configure with MeContext and ManagedElement and tls - ldap mode #ldapmode' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with system under ManagedElement"
        createSystemUnderManagedElement()
        and: "with ldap under system"
        createLdapUnderSystem("1")
        and: "with security under ldap"
        createSecurityUnderSystemLdap("1", BASE_DN_UNDER_TEST)
        and: "with tls under security"
        createTlsUnderSecurity("1")
        and: "task with ldap mode #ldapmode"
        cbpOiConfigureLdapOnNodeTask.parameters.put(ComEcimConfigureLdapOnNodeTask.TLS_MODE_KEY , ldapmode)
        if (ldapmode == "LDAP") {
            ldapWorkFlowContext.put(WorkflowParameterKeys.LDAP_SERVER_PORT.toString(), LDAP_SERVER_PORT_UNDER_TEST_LDAP)
        }
        when: "task is processed"
        cbpOiConfigureLdapOnNodeTaskHandler.processTask(cbpOiConfigureLdapOnNodeTask)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "ldap MO hierarchy should be created as expected under system MO"
        def String systemLdapFdn = "MeContext="+nodeName+",ManagedElement="+nodeName+",system=1,"+SYSTEM_LDAP_SCOPED_TYPE+"=1"
        checkSystemLdapHierarchy(systemLdapFdn, ldapmode) == true
        where:
        ldapmode << ["LDAPS", "LDAP"]
    }

    @Unroll
    def 'reconfigure with MeContext and ManagedElement and same system hierarchy - ldap mode #ldapmode' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with system under ManagedElement"
        createSystemUnderManagedElement()
        and: "with ldap hierarchy under system with same configuration"
        createSystemLdapHierarchyWithSameConfiguration(ldapmode)
        and: "task with ldap mode #ldapmode"
        cbpOiConfigureLdapOnNodeTask.parameters.put(ComEcimConfigureLdapOnNodeTask.TLS_MODE_KEY , ldapmode)
        if (ldapmode == "LDAP") {
            ldapWorkFlowContext.put(WorkflowParameterKeys.LDAP_SERVER_PORT.toString(), LDAP_SERVER_PORT_UNDER_TEST_LDAP)
        }
        when: "task is processed"
        cbpOiConfigureLdapOnNodeTaskHandler.processTask(cbpOiConfigureLdapOnNodeTask)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "ldap MO hierarchy should be created as expected under system MO"
        def String systemLdapFdn = "MeContext="+nodeName+",ManagedElement="+nodeName+",system=1,"+SYSTEM_LDAP_SCOPED_TYPE+"=1"
        checkSystemLdapHierarchy(systemLdapFdn, ldapmode) == true
        where:
        ldapmode << [
            "LDAPS",
            "LDAP"
        ]
    }

    @Unroll
    def 'reconfigure with MeContext and ManagedElement and other system hierarchy - ldap mode from #oldldapmode to #ldapmode' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with system under ManagedElement"
        createSystemUnderManagedElement()
        and: "with ldap hierarchy under system with other configuration - #oldldapmode"
        String oldPrimaryName = "primary-1"
        String oldFallbackName = "fallback-1"
        createSystemLdapHierarchyWithOtherConfiguration(oldPrimaryName, oldFallbackName, oldldapmode)
        and: "task with ldap mode #ldapmode"
        cbpOiConfigureLdapOnNodeTask.parameters.put(ComEcimConfigureLdapOnNodeTask.TLS_MODE_KEY , ldapmode)
        if (ldapmode == "LDAP") {
            ldapWorkFlowContext.put(WorkflowParameterKeys.LDAP_SERVER_PORT.toString(), LDAP_SERVER_PORT_UNDER_TEST_LDAP)
        }
        when: "task is processed"
        cbpOiConfigureLdapOnNodeTaskHandler.processTask(cbpOiConfigureLdapOnNodeTask)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "ldap MO hierarchy should be created as expected under system MO"
        def String systemLdapFdn = "MeContext="+nodeName+",ManagedElement="+nodeName+",system=1,"+SYSTEM_LDAP_SCOPED_TYPE+"=1"
        checkSystemLdapHierarchy(systemLdapFdn, ldapmode) == true
        and: 'old primary server should not be present'
        def String oldPrimaryFdn = "MeContext="+nodeName+",ManagedElement="+nodeName+",system=1,"+SYSTEM_LDAP_SCOPED_TYPE+"=1,server="+oldPrimaryName
        ManagedObject oldPrimaryMO = findMoByFdn(oldPrimaryFdn)
        oldPrimaryMO == null
        and: 'old fallback server should not be present'
        def String oldFallbackFdn = "MeContext="+nodeName+",ManagedElement="+nodeName+",system=1,"+SYSTEM_LDAP_SCOPED_TYPE+"=1,server="+oldFallbackName
        ManagedObject oldFallbackMO = findMoByFdn(oldFallbackFdn)
        oldFallbackMO == null
        where:
        oldldapmode << [
            "LDAP",
            "LDAP",
            "LDAPS",
            "LDAPS"
        ]
        ldapmode << [
            "LDAPS",
            "LDAP",
            "LDAPS",
            "LDAP"
        ]
    }

    @Unroll
    def 'configure ldap with wrong input parameters' () {
        given: "task with use TLS #usetls"
        cbpOiConfigureLdapOnNodeTask.parameters.put(ComEcimConfigureLdapOnNodeTask.USE_TLS_KEY, usetls)
        and: "task with base DN  #basedn"
        ldapWorkFlowContext.put(WorkflowParameterKeys.BASE_DN.toString(), basedn)
        when: "task is processed"
        cbpOiConfigureLdapOnNodeTaskHandler.processTask(cbpOiConfigureLdapOnNodeTask)
        then: "exception should be thrown"
        thrown(IllegalArgumentException)
        where:
        usetls << [false, true, true]
        basedn << [
            "dc=rani-venm-1,dc=com",
            "",
            null
        ]
    }

    def 'configure ldap without system MO' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        when: "task is processed"
        cbpOiConfigureLdapOnNodeTaskHandler.processTask(cbpOiConfigureLdapOnNodeTask)
        then: "exception should be thrown"
        thrown(MissingMoException)
    }

    def 'configure ldap without security MO' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with system under ManagedElement"
        createSystemUnderManagedElement()
        and: "with ldap under system"
        createLdapUnderSystem("1")
        when: "task is processed"
        cbpOiConfigureLdapOnNodeTaskHandler.processTask(cbpOiConfigureLdapOnNodeTask)
        then: "exception should be thrown"
        thrown(MissingMoException)
    }

    void createSystemLdapHierarchyWithSameConfiguration(String ldapMode) {
        createLdapUnderSystem("1")
        createSecurityUnderSystemLdap("1", BASE_DN_UNDER_TEST)
        createSimpleAuthenticatedUnderSecurity("1", BIND_DN_UNDER_TEST, BIND_PASSWORD_UNDER_TEST)
        createTlsUnderSecurity("1")
        createServerUnderSystemLdap("primary")
        createTcpUnderServer("primary", "1", LDAP_IP_ADDRESS_UNDER_TEST)
        if ("LDAPS" == ldapMode) {
            createLdapsUnderTcp("primary", "1", 1636)
        } else if ("LDAP" == ldapMode) {
            createTcpLdapUnderTcp("primary", "1", 1389)
        }
        createServerUnderSystemLdap("fallback")
        createTcpUnderServer("fallback", "1", FALLBACK_LDAP_IP_ADDRESS_UNDER_TEST)
        if ("LDAPS" == ldapMode) {
            createLdapsUnderTcp("fallback", "1", 1636)
        } else if ("LDAP" == ldapMode) {
            createTcpLdapUnderTcp("fallback", "1", 1389)
        }
    }

    void createSystemLdapHierarchyWithOtherConfiguration(String otherPrimaryName, String otherFallbackName, String otherLdapMode) {
        createLdapUnderSystem("1")
        String otherBaseDn = "dc=rani-venm-2,dc=com"
        createSecurityUnderSystemLdap("1", otherBaseDn)
        String otherBindDn = "cn=ProxyAccount_2,ou=proxyagent,ou=com," + otherBaseDn
        String otherBindCrd = "abcdefg"
        createSimpleAuthenticatedUnderSecurity("1", otherBindDn, otherBindCrd)
        createTlsUnderSecurity("1")
        String otherPrimaryAddress = "10.130.10.246"
        String otherFallbackAddress = "10.130.10.247"
        createServerUnderSystemLdap(otherPrimaryName)
        createTcpUnderServer(otherPrimaryName, "1", otherPrimaryAddress)
        if ("LDAPS" == otherLdapMode) {
            createLdapsUnderTcp(otherPrimaryName, "1", 1636)
        } else if ("LDAP" == otherLdapMode) {
            createTcpLdapUnderTcp(otherPrimaryName, "1", 1389)
        }
        createServerUnderSystemLdap(otherFallbackName)
        createTcpUnderServer(otherFallbackName, "1", otherFallbackAddress)
        if ("LDAPS" == otherLdapMode) {
            createLdapsUnderTcp(otherFallbackName, "1", 1636)
        } else if ("LDAP" == otherLdapMode) {
            createTcpLdapUnderTcp(otherFallbackName, "1", 1389)
        }
    }

    boolean checkSystemLdapHierarchy(String systemLdapFdn, String ldapMode) {
        ManagedObject systemLdapMO = findMoByFdn(systemLdapFdn)
        if (systemLdapMO == null) {
            return false;
        }
        String securityFdn = systemLdapFdn + ",security=1"
        ManagedObject securityMO = findMoByFdn(securityFdn)
        if (securityMO == null) {
            return false;
        }
        if (securityMO.getAttribute("user-base-dn") != BASE_DN_UNDER_TEST) {
            return false;
        }
        String simpleAuthenticatedFdn = securityFdn + ",simple-authenticated=1"
        ManagedObject simpleAuthenticatedMO = findMoByFdn(simpleAuthenticatedFdn)
        if (simpleAuthenticatedMO == null) {
            return false;
        }
        if (simpleAuthenticatedMO.getAttribute("bind-dn") != BIND_DN_UNDER_TEST) {
            return false;
        }
        if (simpleAuthenticatedMO.getAttribute("bind-password") != BIND_PASSWORD_UNDER_TEST) {
            return false;
        }
        String tlsFdn = securityFdn + ",tls=1"
        ManagedObject tlsMO = findMoByFdn(tlsFdn)
        if (tlsMO == null) {
            return false;
        }
        String primaryServerFdn = systemLdapFdn + ",server=primary"
        if (checkServerHierarchy(primaryServerFdn, "primary", LDAP_IP_ADDRESS_UNDER_TEST, ldapMode) == false) {
            return false
        }
        String fallbackServerFdn = systemLdapFdn + ",server=fallback"
        if (checkServerHierarchy(fallbackServerFdn, "fallback", FALLBACK_LDAP_IP_ADDRESS_UNDER_TEST, ldapMode) == false) {
            return false
        }

        return true
    }

    private checkServerHierarchy(String serverFdn, String serverName, String serverAddress, String ldapMode) {
        ManagedObject serverMO = findMoByFdn(serverFdn)
        if (serverMO == null) {
            return false;
        }
        if (serverMO.getAttribute("name") != serverName) {
            return false;
        }
        String tcpFdn = serverFdn + ",tcp=1"
        ManagedObject tcpMO = findMoByFdn(tcpFdn)
        if (tcpMO == null) {
            return false;
        }
        if (tcpMO.getAttribute("address") != serverAddress) {
            return false;
        }
        if ("LDAPS" == ldapMode) {
            String ldapsFdn = tcpFdn + ",ldaps=1"
            ManagedObject ldapsMO = findMoByFdn(ldapsFdn)
            if (ldapsMO == null) {
                return false;
            }
            if (ldapsMO.getAttribute("port") != LDAP_SERVER_PORT_UNDER_TEST) {
                return false;
            }
        } else if ("LDAP" == ldapMode) {
            String tcpLdapFdn = tcpFdn + ","+TCP_LDAP_SCOPED_TYPE+"=1"
            ManagedObject tcpLdapMO = findMoByFdn(tcpLdapFdn)
            if (tcpLdapMO == null) {
                return false;
            }
            if (tcpLdapMO.getAttribute("port") != LDAP_SERVER_PORT_UNDER_TEST_LDAP) {
                return false;
            }
        } else {
            return false;
        }
    }
}
