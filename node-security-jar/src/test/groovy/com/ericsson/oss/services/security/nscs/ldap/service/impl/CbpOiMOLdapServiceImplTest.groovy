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

import javax.inject.Inject

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.security.nscs.api.model.NodeRef
import com.ericsson.nms.security.nscs.api.model.NodeReference
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoException
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.attribute.CommonLdapConfigurationTask
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys
import com.ericsson.oss.services.security.nscs.utils.CbpOiNodeDataSetup

import spock.lang.Unroll

class CbpOiMOLdapServiceImplTest extends CbpOiNodeDataSetup {

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
    private CbpOiMOLdapServiceImpl cbpOiMOLdapServiceImpl

    @Inject
    NscsCMReaderService readerService

    private nodeName = "vDU00001"
    private CommonLdapConfigurationTask task
    private NodeReference nodeReference

    def setup() {
        NscsCMReaderService.deploymentEnv = NscsCMReaderService.DeploymentEnvironment.PRODUCTION
        task = new CommonLdapConfigurationTask(nodeName)
        task.setUserLabel(USER_LABEL_UNDER_TEST)
        nodeReference = new NodeRef(nodeName)
    }

    def 'object under test injection'() {
        expect:
        cbpOiMOLdapServiceImpl != null
    }

    def 'validate task for isTls true'() {
        given:
        task.setIsTls(true)
        def NormalizableNodeReference normalizable = mock(NormalizableNodeReference)
        when:
        cbpOiMOLdapServiceImpl.validateLdapConfiguration(task, normalizable)
        then:
        notThrown(IllegalArgumentException.class)
    }

    def 'validate task for isTls false'() {
        given:
        task.setIsTls(false)
        def NormalizableNodeReference normalizable = mock(NormalizableNodeReference)
        when:
        cbpOiMOLdapServiceImpl.validateLdapConfiguration(task, normalizable)
        then:
        thrown(IllegalArgumentException.class)
    }

    def 'no system with MeContext and ManagedElement' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: 'normalizable node reference is set'
        def NormalizableNodeReference normalizable = readerService.getNormalizableNodeReference(nodeReference)
        when:
        cbpOiMOLdapServiceImpl.ldapConfigure(task, normalizable)
        then: "exception should be thrown"
        thrown(MissingMoException.class)
    }

    def 'no system with MeContext and without ManagedElement' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: 'normalizable node reference is set'
        def NormalizableNodeReference normalizable = readerService.getNormalizableNodeReference(nodeReference)
        when:
        cbpOiMOLdapServiceImpl.ldapConfigure(task, normalizable)
        then: "exception should be thrown"
        thrown(MissingMoException.class)
    }

    @Unroll
    def 'only system with MeContext and ManagedElement and tlsMode #tlsmode' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with system under ManagedElement"
        createSystemUnderManagedElement()
        and: 'normalizable node reference is set'
        def NormalizableNodeReference normalizable = readerService.getNormalizableNodeReference(nodeReference)
        and:
        setCommonLdapConfigurationTask(task, tlsmode)
        when:
        cbpOiMOLdapServiceImpl.ldapConfigure(task, normalizable)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "system,ldap hierarchy should have been created"
        def String systemLdapFdn = "MeContext="+nodeName+",ManagedElement="+nodeName+",system=1,"+SYSTEM_LDAP_SCOPED_TYPE+"=1"
        checkSystemLdapHierarchy(systemLdapFdn, tlsmode)
        and: "task should not contain previous bind DN in its LDAP workflow context"
        task.getLdapWorkflowContext().get(WorkflowOutputParameterKeys.PREVIOUS_BIND_DN.toString()) == null
        where:
        tlsmode << ["LDAPS", "STARTTLS"]
    }

    @Unroll
    def 'only system with MeContext and without ManagedElement and tlsMode #tlsmode' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with system under MeContext"
        createSystemUnderMeContext()
        and: 'normalizable node reference is set'
        def NormalizableNodeReference normalizable = readerService.getNormalizableNodeReference(nodeReference)
        and:
        setCommonLdapConfigurationTask(task, tlsmode)
        when:
        cbpOiMOLdapServiceImpl.ldapConfigure(task, normalizable)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "system,ldap hierarchy should have been created"
        def String systemLdapFdn = "MeContext="+nodeName+",system=1,"+SYSTEM_LDAP_SCOPED_TYPE+"=1"
        checkSystemLdapHierarchy(systemLdapFdn, tlsmode)
        and: "task should not contain previous bind DN in its LDAP workflow context"
        task.getLdapWorkflowContext().get(WorkflowOutputParameterKeys.PREVIOUS_BIND_DN.toString()) == null
        where:
        tlsmode << ["LDAPS", "STARTTLS"]
    }

    @Unroll
    def 'only system,ldap,security with base DN #basedn and with MeContext and ManagedElement and tlsMode #tlsmode' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with system under ManagedElement"
        createSystemUnderManagedElement()
        and: "with ldap under system"
        createLdapUnderSystem("1")
        and: "with security under ldap"
        createSecurityUnderSystemLdap("1", basedn)
        and: 'normalizable node reference is set'
        def NormalizableNodeReference normalizable = readerService.getNormalizableNodeReference(nodeReference)
        and:
        setCommonLdapConfigurationTask(task, tlsmode)
        when: "task is processed"
        cbpOiMOLdapServiceImpl.ldapConfigure(task, normalizable)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "system,ldap hierarchy should have been created"
        def String systemLdapFdn = "MeContext="+nodeName+",ManagedElement="+nodeName+",system=1,"+SYSTEM_LDAP_SCOPED_TYPE+"=1"
        checkSystemLdapHierarchy(systemLdapFdn, tlsmode)
        and: "task should not contain previous bind DN in its LDAP workflow context"
        task.getLdapWorkflowContext().get(WorkflowOutputParameterKeys.PREVIOUS_BIND_DN.toString()) == null
        where:
        tlsmode << [
            "LDAPS",
            "STARTTLS",
            "LDAPS",
            "STARTTLS"
        ]
        basedn << [
            BASE_DN_UNDER_TEST,
            BASE_DN_UNDER_TEST,
            PREVIOUS_BASE_DN_UNDER_TEST,
            PREVIOUS_BASE_DN_UNDER_TEST
        ]
    }

    def 'only system,ldap with MeContext and ManagedElement' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with system under ManagedElement"
        createSystemUnderManagedElement()
        and: "with ldap under system"
        createLdapUnderSystem("1")
        and: 'normalizable node reference is set'
        def NormalizableNodeReference normalizable = readerService.getNormalizableNodeReference(nodeReference)
        when: "task is processed"
        cbpOiMOLdapServiceImpl.ldapConfigure(task, normalizable)
        then: "exception should be thrown"
        thrown(MissingMoException.class)
    }

    @Unroll
    def 'only system,ldap,security,tls with base DN #basedn and with MeContext and ManagedElement and tlsMode #tlsmode' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with system under ManagedElement"
        createSystemUnderManagedElement()
        and: "with ldap under system"
        createLdapUnderSystem("1")
        and: "with security under ldap"
        createSecurityUnderSystemLdap("1", basedn)
        and: "with tls under security"
        createTlsUnderSecurity("1")
        and: 'normalizable node reference is set'
        def NormalizableNodeReference normalizable = readerService.getNormalizableNodeReference(nodeReference)
        and:
        setCommonLdapConfigurationTask(task, tlsmode)
        when: "task is processed"
        cbpOiMOLdapServiceImpl.ldapConfigure(task, normalizable)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "system,ldap hierarchy should have been created"
        def String systemLdapFdn = "MeContext="+nodeName+",ManagedElement="+nodeName+",system=1,"+SYSTEM_LDAP_SCOPED_TYPE+"=1"
        checkSystemLdapHierarchy(systemLdapFdn, tlsmode)
        and: "task should not contain previous bind DN in its LDAP workflow context"
        task.getLdapWorkflowContext().get(WorkflowOutputParameterKeys.PREVIOUS_BIND_DN.toString()) == null
        where:
        tlsmode << [
            "LDAPS",
            "STARTTLS",
            "LDAPS",
            "STARTTLS"
        ]
        basedn << [
            BASE_DN_UNDER_TEST,
            BASE_DN_UNDER_TEST,
            PREVIOUS_BASE_DN_UNDER_TEST,
            PREVIOUS_BASE_DN_UNDER_TEST
        ]
    }

    @Unroll
    def 'only system,ldap,security,simple-authenticated with bind DN #binddn and cred #bindcrd and with MeContext and ManagedElement and tlsMode #tlsmode' () {
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
        and: "with simple-authenticated under security"
        createSimpleAuthenticatedUnderSecurity("1", binddn, bindcrd)
        and: 'normalizable node reference is set'
        def NormalizableNodeReference normalizable = readerService.getNormalizableNodeReference(nodeReference)
        and:
        setCommonLdapConfigurationTask(task, tlsmode)
        when: "task is processed"
        cbpOiMOLdapServiceImpl.ldapConfigure(task, normalizable)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "system,ldap hierarchy should have been created"
        def String systemLdapFdn = "MeContext="+nodeName+",ManagedElement="+nodeName+",system=1,"+SYSTEM_LDAP_SCOPED_TYPE+"=1"
        checkSystemLdapHierarchy(systemLdapFdn, tlsmode)
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
        bindcrd << [
            null,
            null,
            "",
            "",
            PREVIOUS_BIND_CRD_UNDER_TEST,
            PREVIOUS_BIND_CRD_UNDER_TEST
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

    @Unroll
    def 'only system,ldap,servers with MeContext and ManagedElement and tlsMode #tlsmode' () {
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
        and: "with primary server under ldap"
        createServerUnderSystemLdap("primary")
        and: "with fallback server under ldap"
        createServerUnderSystemLdap("fallback")
        and: 'normalizable node reference is set'
        def NormalizableNodeReference normalizable = readerService.getNormalizableNodeReference(nodeReference)
        and:
        setCommonLdapConfigurationTask(task, tlsmode)
        when: "task is processed"
        cbpOiMOLdapServiceImpl.ldapConfigure(task, normalizable)
        then: "exception should be thrown"
        thrown(MissingMoException.class)
        where:
        tlsmode << [
            "LDAPS",
            "STARTTLS",
        ]
    }

    @Unroll
    def 'only system,ldap,server,tcp with primary IP #primaryip and fallback IP #fallbackip with MeContext and ManagedElement and tlsMode #tlsmode' () {
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
        and: "with primary server under ldap"
        createServerUnderSystemLdap("primary")
        and: "with tcp under primary server"
        createTcpUnderServer("primary", "1", primaryip)
        and: "with fallback server under ldap"
        createServerUnderSystemLdap("fallback")
        and: "with tcp under fallback server"
        createTcpUnderServer("fallback", "1", fallbackip)
        and: 'normalizable node reference is set'
        def NormalizableNodeReference normalizable = readerService.getNormalizableNodeReference(nodeReference)
        and:
        setCommonLdapConfigurationTask(task, tlsmode)
        when: "task is processed"
        cbpOiMOLdapServiceImpl.ldapConfigure(task, normalizable)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "system,ldap hierarchy should have been created"
        def String systemLdapFdn = "MeContext="+nodeName+",ManagedElement="+nodeName+",system=1,"+SYSTEM_LDAP_SCOPED_TYPE+"=1"
        checkSystemLdapHierarchy(systemLdapFdn, tlsmode)
        and: "task should not contain previous bind DN in its LDAP workflow context"
        task.getLdapWorkflowContext().get(WorkflowOutputParameterKeys.PREVIOUS_BIND_DN.toString()) == null
        where:
        tlsmode << [
            "LDAPS",
            "STARTTLS",
            "LDAPS",
            "STARTTLS"
        ]
        primaryip << [
            LDAP_IP_ADDRESS_UNDER_TEST,
            LDAP_IP_ADDRESS_UNDER_TEST,
            PREVIOUS_LDAP_IP_ADDRESS_UNDER_TEST,
            PREVIOUS_LDAP_IP_ADDRESS_UNDER_TEST
        ]
        fallbackip << [
            FALLBACK_LDAP_IP_ADDRESS_UNDER_TEST,
            FALLBACK_LDAP_IP_ADDRESS_UNDER_TEST,
            PREVIOUS_FALLBACK_LDAP_IP_ADDRESS_UNDER_TEST,
            PREVIOUS_FALLBACK_LDAP_IP_ADDRESS_UNDER_TEST
        ]
    }

    @Unroll
    def 'only system,ldap,server,tcp with extra servers with MeContext and ManagedElement and tlsMode #tlsmode' () {
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
        and: "with primary server under ldap"
        createServerUnderSystemLdap("primary")
        and: "with tcp under primary server"
        createTcpUnderServer("primary", "1", LDAP_IP_ADDRESS_UNDER_TEST)
        and: "with fallback server under ldap"
        createServerUnderSystemLdap("fallback")
        and: "with tcp under fallback server"
        createTcpUnderServer("fallback", "1", FALLBACK_LDAP_IP_ADDRESS_UNDER_TEST)
        and: "with extra primary server under ldap"
        createServerUnderSystemLdap("1")
        and: "with tcp under extra primary server"
        createTcpUnderServer("1", "1", PREVIOUS_LDAP_IP_ADDRESS_UNDER_TEST)
        and: "with extra fallback server under ldap"
        createServerUnderSystemLdap("2")
        and: "with tcp under extra fallback server"
        createTcpUnderServer("2", "1", PREVIOUS_FALLBACK_LDAP_IP_ADDRESS_UNDER_TEST)
        and: 'normalizable node reference is set'
        def NormalizableNodeReference normalizable = readerService.getNormalizableNodeReference(nodeReference)
        and:
        setCommonLdapConfigurationTask(task, tlsmode)
        when: "task is processed"
        cbpOiMOLdapServiceImpl.ldapConfigure(task, normalizable)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "system,ldap hierarchy should have been created"
        def String systemLdapFdn = "MeContext="+nodeName+",ManagedElement="+nodeName+",system=1,"+SYSTEM_LDAP_SCOPED_TYPE+"=1"
        checkSystemLdapHierarchy(systemLdapFdn, tlsmode)
        and: "task should not contain previous bind DN in its LDAP workflow context"
        task.getLdapWorkflowContext().get(WorkflowOutputParameterKeys.PREVIOUS_BIND_DN.toString()) == null
        where:
        tlsmode << [
            "LDAPS",
            "STARTTLS",
        ]
    }

    @Unroll
    def 'only system,ldap,server,tcp,ldaps/ldap with MeContext and ManagedElement and previous TLS mode #prevtlsmode and TLS mode #tlsmode' () {
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
        and: "with primary server under ldap"
        createServerUnderSystemLdap("primary")
        and: "with tcp under primary server"
        createTcpUnderServer("primary", "1", LDAP_IP_ADDRESS_UNDER_TEST)
        and: "with ldaps/ldap under tcp"
        if ("LDAPS" == prevtlsmode) {
            createLdapsUnderTcp("primary", "1", 1636)
        } else {
            createTcpLdapUnderTcp("primary", "1", 1389)
        }
        and: "with fallback server under ldap"
        createServerUnderSystemLdap("fallback")
        and: "with tcp under fallback server"
        createTcpUnderServer("fallback", "1", FALLBACK_LDAP_IP_ADDRESS_UNDER_TEST)
        and: "with ldaps/ldap under tcp"
        if ("LDAPS" == prevtlsmode) {
            createLdapsUnderTcp("fallback", "1", 1636)
        } else {
            createTcpLdapUnderTcp("fallback", "1", 1389)
        }
        and: 'normalizable node reference is set'
        def NormalizableNodeReference normalizable = readerService.getNormalizableNodeReference(nodeReference)
        and:
        setCommonLdapConfigurationTask(task, tlsmode)
        when: "task is processed"
        cbpOiMOLdapServiceImpl.ldapConfigure(task, normalizable)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "system,ldap hierarchy should have been created"
        def String systemLdapFdn = "MeContext="+nodeName+",ManagedElement="+nodeName+",system=1,"+SYSTEM_LDAP_SCOPED_TYPE+"=1"
        checkSystemLdapHierarchy(systemLdapFdn, tlsmode)
        and: "task should not contain previous bind DN in its LDAP workflow context"
        task.getLdapWorkflowContext().get(WorkflowOutputParameterKeys.PREVIOUS_BIND_DN.toString()) == null
        where:
        tlsmode << [
            "LDAPS",
            "STARTTLS",
            "LDAPS",
            "STARTTLS"
        ]
        prevtlsmode << [
            "LDAPS",
            "LDAPS",
            "STARTTLS",
            "STARTTLS"
        ]
    }

    @Unroll
    def 'complete reconfiguration with MeContext and ManagedElement and previous TLS mode #prevtlsmode and TLS mode #tlsmode' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with system under ManagedElement"
        createSystemUnderManagedElement()
        and: "with ldap under system"
        createLdapUnderSystem("1")
        and: "with security under ldap"
        createSecurityUnderSystemLdap("1", PREVIOUS_BASE_DN_UNDER_TEST)
        and: "with simple-authenticated under security"
        createSimpleAuthenticatedUnderSecurity("1", PREVIOUS_BIND_DN_UNDER_TEST, PREVIOUS_BIND_CRD_UNDER_TEST)
        and: "with primary server under ldap"
        createServerUnderSystemLdap("primary")
        and: "with tcp under primary server"
        createTcpUnderServer("primary", "1", LDAP_IP_ADDRESS_UNDER_TEST)
        and: "with ldaps/ldap under tcp"
        if ("LDAPS" == prevtlsmode) {
            createLdapsUnderTcp("primary", "1", 1636)
        } else {
            createTcpLdapUnderTcp("primary", "1", 1389)
        }
        and: "with fallback server under ldap"
        createServerUnderSystemLdap("fallback")
        and: "with tcp under fallback server"
        createTcpUnderServer("fallback", "1", FALLBACK_LDAP_IP_ADDRESS_UNDER_TEST)
        and: "with ldaps/ldap under tcp"
        if ("LDAPS" == prevtlsmode) {
            createLdapsUnderTcp("fallback", "1", 1636)
        } else {
            createTcpLdapUnderTcp("fallback", "1", 1389)
        }
        and: 'normalizable node reference is set'
        def NormalizableNodeReference normalizable = readerService.getNormalizableNodeReference(nodeReference)
        and:
        setCommonLdapConfigurationTask(task, tlsmode)
        when: "task is processed"
        cbpOiMOLdapServiceImpl.ldapConfigure(task, normalizable)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "system,ldap hierarchy should have been created"
        def String systemLdapFdn = "MeContext="+nodeName+",ManagedElement="+nodeName+",system=1,"+SYSTEM_LDAP_SCOPED_TYPE+"=1"
        checkSystemLdapHierarchy(systemLdapFdn, tlsmode)
        and: "task should contain previous bind DN in its LDAP workflow context"
        task.getLdapWorkflowContext().get(WorkflowOutputParameterKeys.PREVIOUS_BIND_DN.toString()) == PREVIOUS_BIND_DN_UNDER_TEST
        where:
        tlsmode << [
            "LDAPS",
            "STARTTLS",
            "LDAPS",
            "STARTTLS"
        ]
        prevtlsmode << [
            "LDAPS",
            "LDAPS",
            "STARTTLS",
            "STARTTLS"
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

    boolean checkSystemLdapHierarchy(String systemLdapFdn, String tlsMode) {
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
        if (simpleAuthenticatedMO.getAttribute("bind-password") != BIND_CRD_UNDER_TEST) {
            return false;
        }
        String tlsFdn = securityFdn + ",tls=1"
        ManagedObject tlsMO = findMoByFdn(tlsFdn)
        if (tlsMO == null) {
            return false;
        }
        String primaryServerFdn = systemLdapFdn + ",server=primary"
        if (checkServerHierarchy(primaryServerFdn, "primary", LDAP_IP_ADDRESS_UNDER_TEST, tlsMode) == false) {
            return false
        }
        String fallbackServerFdn = systemLdapFdn + ",server=fallback"
        if (checkServerHierarchy(fallbackServerFdn, "fallback", FALLBACK_LDAP_IP_ADDRESS_UNDER_TEST, tlsMode) == false) {
            return false
        }

        return true
    }

    private checkServerHierarchy(String serverFdn, String serverName, String serverAddress, String tlsMode) {
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
        if ("LDAPS" == tlsMode) {
            String ldapsFdn = tcpFdn + ",ldaps=1"
            ManagedObject ldapsMO = findMoByFdn(ldapsFdn)
            if (ldapsMO == null) {
                return false;
            }
            if (ldapsMO.getAttribute("port") != LDAP_SERVER_PORT_UNDER_TEST) {
                return false;
            }
        } else if ("STARTTLS" == tlsMode) {
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
