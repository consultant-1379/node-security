/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2024
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.nbi

import java.util.concurrent.locks.Lock

import javax.inject.Inject

import org.slf4j.Logger

import com.ericsson.cds.cdi.support.rule.ImplementationClasses
import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.security.nscs.api.exception.NscsBadRequestException
import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerException
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.nms.security.nscs.enrollmentinfo.request.model.NodeDetails
import com.ericsson.nms.security.nscs.enrollmentinfo.response.EnrollmentInfoProvider
import com.ericsson.nms.security.nscs.enrollmentinfo.response.model.EnrollmentInfo
import com.ericsson.nms.security.nscs.enrollmentinfo.service.EnrollmentInfoServiceException
import com.ericsson.nms.security.nscs.iscf.IscfCancelHandler
import com.ericsson.nms.security.nscs.ldap.utility.PlatformConfigurationReader
import com.ericsson.oss.itpf.sdk.cluster.lock.LockManager
import com.ericsson.oss.itpf.security.identitymgmtservices.IdentityManagementService
import com.ericsson.oss.itpf.security.identitymgmtservices.IdentityManagementServiceException
import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountData
import com.ericsson.oss.itpf.security.identitymgmtservices.comaa.ComAAInfo
import com.ericsson.oss.itpf.security.identitymgmtservices.comaa.ConnectionData
import com.ericsson.oss.itpf.security.identitymgmtservices.comaa.LdapAddress
import com.ericsson.oss.services.security.nscs.nbi.resources.NscsResourceInstance
import com.ericsson.oss.services.security.nscs.utils.EjbNodeDataSetup

import spock.lang.Unroll

class NbiManagerBeanSpecTest extends EjbNodeDataSetup {

    private Lock resourceLock = Mock()

    @ObjectUnderTest
    NbiManagerBean manager

    @Inject
    Logger logger;

    @ImplementationClasses
    def myclass = [
        NbiServiceBean.class
    ]

    @MockedImplementation
    private NscsCapabilityModelService nscsCapabilityModelService

    @MockedImplementation
    private EnrollmentInfoProvider enrollmentInfoProvider

    @MockedImplementation
    private IscfCancelHandler iscfCancelHandler

    @MockedImplementation
    private IdentityManagementService identityManagementService

    @MockedImplementation
    private ComAAInfo comAAInfo

    @MockedImplementation
    private PlatformConfigurationReader platformConfigurationReader

    @ImplementationInstance
    LockManager lockManager = [
        getDistributedLock : { String lockName, String clusterName ->
            return resourceLock
        }
    ] as LockManager

    ProxyAgentAccountData proxyAgentAccountData = new ProxyAgentAccountData("newproxyaccount", "pwd")
    LdapAddress ldapAddress = new LdapAddress("primary", "fallback")
    ConnectionData connectionData = new ConnectionData(ldapAddress, ldapAddress, 1389, 1636)

    def setup() {
        NscsCMReaderService.deploymentEnv = NscsCMReaderService.DeploymentEnvironment.PRODUCTION
        nscsCapabilityModelService.getDefaultEnrollmentMode(_) >> "CMPv2_INITIAL"
    }

    def 'object under test'() {
        expect:
        manager != null
    }

    def 'generate enrollment info without NetworkElement'() {
        given:
        def nodeNameorFdn = "Node1"
        def domainName = "OAM"
        def nodeDetails = new NodeDetails()
        nodeDetails.setNodeFdn(nodeNameorFdn)
        nodeDetails.setCertType(domainName)
        when:
        def res = manager.generateEnrollmentInfo(nodeNameorFdn, domainName, null, nodeDetails)
        then:
        thrown(NscsBadRequestException.class)
    }

    def 'generate enrollment info with NetworkElement, without ConnectivityInfo and IP family'() {
        given:
        def nodeNameorFdn = "Node1"
        def domainName = "OAM"
        def nodeDetails = new NodeDetails()
        nodeDetails.setNodeFdn(nodeNameorFdn)
        nodeDetails.setCertType(domainName)
        createNetworkElement("Shared-CNF", null, nodeNameorFdn, null)
        when:
        def res = manager.generateEnrollmentInfo(nodeNameorFdn, domainName, null, nodeDetails)
        then:
        thrown(NscsBadRequestException.class)
    }

    def 'generate enrollment info with NetworkElement, without ConnectivityInfo and with invalid IP family'() {
        given:
        def nodeNameorFdn = "Node1"
        def domainName = "OAM"
        def nodeDetails = new NodeDetails()
        nodeDetails.setNodeFdn(nodeNameorFdn)
        nodeDetails.setCertType(domainName)
        createNetworkElement("Shared-CNF", null, nodeNameorFdn, null)
        when:
        def res = manager.generateEnrollmentInfo(nodeNameorFdn, domainName, "INVALID", nodeDetails)
        then:
        thrown(NscsBadRequestException.class)
    }

    def 'generate enrollment info with NetworkElement, without ConnectivityInfo, with IP family and without NetworkElementSecurity'() {
        given:
        def nodeNameorFdn = "Node1"
        def domainName = "OAM"
        def nodeDetails = new NodeDetails()
        nodeDetails.setNodeFdn(nodeNameorFdn)
        nodeDetails.setCertType(domainName)
        nodeDetails.setIpVersion(StandardProtocolFamily.INET)
        createNetworkElement("Shared-CNF", null, nodeNameorFdn, null)
        when:
        def res = manager.generateEnrollmentInfo(nodeNameorFdn, domainName, "INET", nodeDetails)
        then:
        thrown(NscsBadRequestException.class)
    }

    @Unroll
    def 'generate enrollment info with NetworkElement, with ConnectivityInfo IP address #ipaddress and without IP family and NetworkElementSecurity'() {
        given:
        def nodeNameorFdn = "Node1"
        def domainName = "OAM"
        def nodeDetails = new NodeDetails()
        nodeDetails.setNodeFdn(nodeNameorFdn)
        nodeDetails.setCertType(domainName)
        createNetworkElement("Shared-CNF", null, nodeNameorFdn, null)
        createConnectivityInformationUnderNetworkElement("Shared-CNF", ipaddress)
        when:
        def res = manager.generateEnrollmentInfo(nodeNameorFdn, domainName, null, nodeDetails)
        then:
        thrown(NscsBadRequestException.class)
        where:
        ipaddress << [
            "1.2.3.4",
            "2001:db8::1:0:0:1"
        ]
    }

    @Unroll
    def 'generate enrollment info with invalid domain #domainname'() {
        given:
        def nodeNameorFdn = "Node1"
        def nodeDetails = new NodeDetails()
        nodeDetails.setNodeFdn(nodeNameorFdn)
        nodeDetails.setCertType(domainname)
        createNetworkElement("Shared-CNF", null, nodeNameorFdn, null)
        createConnectivityInformationUnderNetworkElement("Shared-CNF", "1.2.3.4")
        createSecurityFunctionUnderNetworkElement()
        createNetworkElementSecurityUnderSecurityFunction("secure", "RSA_1024", null, null)
        enrollmentInfoProvider.getEnrollmentInfo(_, _) >> new EnrollmentInfo()
        when:
        def res = manager.generateEnrollmentInfo(nodeNameorFdn, domainname, null, nodeDetails)
        then:
        thrown(NscsBadRequestException.class)
        where:
        domainname << [null, "NOT_OAM"]
    }

    def 'generate enrollment info with invalid algorithm'() {
        given:
        def nodeNameorFdn = "Node1"
        def domainName = "OAM"
        def nodeDetails = new NodeDetails()
        nodeDetails.setNodeFdn(nodeNameorFdn)
        nodeDetails.setCertType(domainName)
        nodeDetails.setKeySize("INVALID")
        createNetworkElement("Shared-CNF", null, nodeNameorFdn, null)
        createConnectivityInformationUnderNetworkElement("Shared-CNF", "1.2.3.4")
        createSecurityFunctionUnderNetworkElement()
        createNetworkElementSecurityUnderSecurityFunction("secure", "RSA_1024", null, null)
        enrollmentInfoProvider.getEnrollmentInfo(_, _) >> new EnrollmentInfo()
        when:
        def res = manager.generateEnrollmentInfo(nodeNameorFdn, domainName, null, nodeDetails)
        then:
        thrown(NscsBadRequestException.class)
    }

    @Unroll
    def 'successful generate enrollment info with valid algorithm #algorithm'() {
        given:
        def nodeNameorFdn = "Node1"
        def domainName = "OAM"
        def nodeDetails = new NodeDetails()
        nodeDetails.setNodeFdn(nodeNameorFdn)
        nodeDetails.setCertType(domainName)
        nodeDetails.setKeySize(algorithm)
        createNetworkElement("Shared-CNF", null, nodeNameorFdn, null)
        createConnectivityInformationUnderNetworkElement("Shared-CNF", "1.2.3.4")
        createSecurityFunctionUnderNetworkElement()
        createNetworkElementSecurityUnderSecurityFunction("secure", "RSA_1024", null, null)
        enrollmentInfoProvider.getEnrollmentInfo(_, _) >> new EnrollmentInfo()
        when:
        def res = manager.generateEnrollmentInfo(nodeNameorFdn, domainName, null, nodeDetails)
        then:
        res != null
        where:
        algorithm << [null, "RSA_2048"]
    }

    @Unroll
    def 'successful generate enrollment info with valid SAN type #santype value #sanvalue'() {
        given:
        def nodeNameorFdn = "Node1"
        def domainName = "OAM"
        def nodeDetails = new NodeDetails()
        nodeDetails.setNodeFdn(nodeNameorFdn)
        nodeDetails.setCertType(domainName)
        nodeDetails.setSubjectAltNameType(santype)
        nodeDetails.setSubjectAltName(sanvalue)
        createNetworkElement("Shared-CNF", null, nodeNameorFdn, null)
        createConnectivityInformationUnderNetworkElement("Shared-CNF", "1.2.3.4")
        createSecurityFunctionUnderNetworkElement()
        createNetworkElementSecurityUnderSecurityFunction("secure", "RSA_1024", null, null)
        enrollmentInfoProvider.getEnrollmentInfo(_, _) >> new EnrollmentInfo()
        when:
        def res = manager.generateEnrollmentInfo(nodeNameorFdn, domainName, null, nodeDetails)
        then:
        res != null
        where:
        santype << [
            null,
            "IPV4",
            "IPV6",
            "RFC822_NAME",
            "DNS_NAME"
        ]
        sanvalue << [
            null,
            "1.2.3.4",
            "2001:db8::1:0:0:1",
            "user@radio.abcdef",
            "value"
        ]
    }

    @Unroll
    def 'generate enrollment info with invalid SAN type #santype value #sanvalue'() {
        given:
        def nodeNameorFdn = "Node1"
        def domainName = "OAM"
        def nodeDetails = new NodeDetails()
        nodeDetails.setNodeFdn(nodeNameorFdn)
        nodeDetails.setCertType(domainName)
        nodeDetails.setSubjectAltNameType(santype)
        nodeDetails.setSubjectAltName(sanvalue)
        createNetworkElement("Shared-CNF", null, nodeNameorFdn, null)
        createConnectivityInformationUnderNetworkElement("Shared-CNF", "1.2.3.4")
        createSecurityFunctionUnderNetworkElement()
        createNetworkElementSecurityUnderSecurityFunction("secure", "RSA_1024", null, null)
        enrollmentInfoProvider.getEnrollmentInfo(_, _) >> new EnrollmentInfo()
        when:
        def res = manager.generateEnrollmentInfo(nodeNameorFdn, domainName, null, nodeDetails)
        then:
        thrown(NscsBadRequestException.class)
        where:
        santype << [
            null,
            "IPV4",
            "IPV4",
            "IPV6",
            "RFC822_NAME",
            "DNS_NAME",
            "INVALID"
        ]
        sanvalue << [
            "value",
            null,
            "2001:db8::1:0:0:1",
            "1.2.3.4",
            "value",
            "user..com",
            "value"
        ]
    }

    @Unroll
    def 'successful generate enrollment info with valid OTP count #otpcount'() {
        given:
        def nodeNameorFdn = "Node1"
        def domainName = "OAM"
        def nodeDetails = new NodeDetails()
        nodeDetails.setNodeFdn(nodeNameorFdn)
        nodeDetails.setCertType(domainName)
        nodeDetails.setOtpCount(otpcount)
        createNetworkElement("Shared-CNF", null, nodeNameorFdn, null)
        createConnectivityInformationUnderNetworkElement("Shared-CNF", "1.2.3.4")
        createSecurityFunctionUnderNetworkElement()
        createNetworkElementSecurityUnderSecurityFunction("secure", "RSA_1024", null, null)
        enrollmentInfoProvider.getEnrollmentInfo(_, _) >> new EnrollmentInfo()
        when:
        def res = manager.generateEnrollmentInfo(nodeNameorFdn, domainName, null, nodeDetails)
        then:
        res != null
        where:
        otpcount << [
            null,
            0,
            1,
            5
        ]
    }

    def 'generate enrollment info with invalid OTP count'() {
        given:
        def nodeNameorFdn = "Node1"
        def domainName = "OAM"
        def nodeDetails = new NodeDetails()
        nodeDetails.setNodeFdn(nodeNameorFdn)
        nodeDetails.setCertType(domainName)
        nodeDetails.setOtpCount(-1)
        createNetworkElement("Shared-CNF", null, nodeNameorFdn, null)
        createConnectivityInformationUnderNetworkElement("Shared-CNF", "1.2.3.4")
        createSecurityFunctionUnderNetworkElement()
        createNetworkElementSecurityUnderSecurityFunction("secure", "RSA_1024", null, null)
        enrollmentInfoProvider.getEnrollmentInfo(_, _) >> new EnrollmentInfo()
        when:
        def res = manager.generateEnrollmentInfo(nodeNameorFdn, domainName, null, nodeDetails)
        then:
        thrown(NscsBadRequestException.class)
    }

    @Unroll
    def 'successful generate enrollment info with valid OTP validity period #otpvalidity'() {
        given:
        def nodeNameorFdn = "Node1"
        def domainName = "OAM"
        def nodeDetails = new NodeDetails()
        nodeDetails.setNodeFdn(nodeNameorFdn)
        nodeDetails.setCertType(domainName)
        nodeDetails.setOtpValidityPeriodInMinutes(otpvalidity)
        createNetworkElement("Shared-CNF", null, nodeNameorFdn, null)
        createConnectivityInformationUnderNetworkElement("Shared-CNF", "1.2.3.4")
        createSecurityFunctionUnderNetworkElement()
        createNetworkElementSecurityUnderSecurityFunction("secure", "RSA_1024", null, null)
        enrollmentInfoProvider.getEnrollmentInfo(_, _) >> new EnrollmentInfo()
        when:
        def res = manager.generateEnrollmentInfo(nodeNameorFdn, domainName, null, nodeDetails)
        then:
        res != null
        where:
        otpvalidity << [
            -1,
            1,
            1440,
            43200,
            null
        ]
    }

    @Unroll
    def 'generate enrollment info with invalid OTP validity period #otpvalidity'() {
        given:
        def nodeNameorFdn = "Node1"
        def domainName = "OAM"
        def nodeDetails = new NodeDetails()
        nodeDetails.setNodeFdn(nodeNameorFdn)
        nodeDetails.setCertType(domainName)
        nodeDetails.setOtpValidityPeriodInMinutes(otpvalidity)
        createNetworkElement("Shared-CNF", null, nodeNameorFdn, null)
        createConnectivityInformationUnderNetworkElement("Shared-CNF", "1.2.3.4")
        createSecurityFunctionUnderNetworkElement()
        createNetworkElementSecurityUnderSecurityFunction("secure", "RSA_1024", null, null)
        enrollmentInfoProvider.getEnrollmentInfo(_, _) >> new EnrollmentInfo()
        when:
        def res = manager.generateEnrollmentInfo(nodeNameorFdn, domainName, null, nodeDetails)
        then:
        thrown(NscsBadRequestException.class)
        where:
        otpvalidity << [
            -2,
            0,
            43201
        ]
    }

    def 'successful generate enrollment info with NetworkElement, ConnectivityInfo and NetworkElementSecurity'() {
        given:
        def nodeNameorFdn = "Node1"
        def domainName = "OAM"
        def nodeDetails = new NodeDetails()
        nodeDetails.setNodeFdn(nodeNameorFdn)
        nodeDetails.setCertType(domainName)
        createNetworkElement("Shared-CNF", null, nodeNameorFdn, null)
        createConnectivityInformationUnderNetworkElement("Shared-CNF", "1.2.3.4")
        createSecurityFunctionUnderNetworkElement()
        createNetworkElementSecurityUnderSecurityFunction("secure", "RSA_1024", null, null)
        enrollmentInfoProvider.getEnrollmentInfo(_, _) >> new EnrollmentInfo()
        when:
        def res = manager.generateEnrollmentInfo(nodeNameorFdn, domainName, null, nodeDetails)
        then:
        res != null
    }

    def 'failed generate enrollment info with NetworkElement, ConnectivityInfo and NetworkElementSecurity'() {
        given:
        def nodeNameorFdn = "Node1"
        def domainName = "OAM"
        def nodeDetails = new NodeDetails()
        nodeDetails.setNodeFdn(nodeNameorFdn)
        nodeDetails.setCertType(domainName)
        createNetworkElement("Shared-CNF", null, nodeNameorFdn, null)
        createConnectivityInformationUnderNetworkElement("Shared-CNF", "1.2.3.4")
        createSecurityFunctionUnderNetworkElement()
        createNetworkElementSecurityUnderSecurityFunction("secure", "RSA_1024", null, null)
        enrollmentInfoProvider.getEnrollmentInfo(_, _) >> {
            throw new EnrollmentInfoServiceException("error")
        }
        when:
        def res = manager.generateEnrollmentInfo(nodeNameorFdn, domainName, null, nodeDetails)
        then:
        thrown(UnexpectedErrorException.class)
    }

    def 'delete enrollment info without NetworkElement'() {
        given:
        def nodeNameorFdn = "Node1"
        def domainName = "OAM"
        when:
        def res = manager.deleteEnrollmentInfo(nodeNameorFdn, domainName)
        then:
        thrown(NscsBadRequestException.class)
    }

    def 'successful delete enrollment info with NetworkElement'() {
        given:
        def nodeNameorFdn = "Node1"
        def domainName = "OAM"
        createNetworkElement("Shared-CNF", null, nodeNameorFdn, null)
        iscfCancelHandler.cancel(_) >> {
            return
        }
        when:
        def res = manager.deleteEnrollmentInfo(nodeNameorFdn, domainName)
        then:
        res != null
    }

    def 'failed delete enrollment info with failed cancel'() {
        given:
        def nodeNameorFdn = "Node1"
        def domainName = "OAM"
        createNetworkElement("Shared-CNF", null, nodeNameorFdn, null)
        iscfCancelHandler.cancel(_) >> {
            throw new NscsPkiEntitiesManagerException('message')
        }
        when:
        def res = manager.deleteEnrollmentInfo(nodeNameorFdn, domainName)
        then:
        thrown(UnexpectedErrorException.class)
    }

    def 'create ldap without NetworkElement'() {
        given:
        def nodeNameorFdn = "Node1"
        when:
        def res = manager.createLdapConfiguration(nodeNameorFdn, null)
        then:
        thrown(NscsBadRequestException.class)
    }

    def 'create ldap with NetworkElement, without ConnectivityInfo and IP family'() {
        given:
        def nodeNameorFdn = "Node1"
        createNetworkElement("Shared-CNF", null, nodeNameorFdn, null)
        when:
        def res = manager.createLdapConfiguration(nodeNameorFdn, null)
        then:
        thrown(NscsBadRequestException.class)
    }

    def 'create ldap with NetworkElement, without ConnectivityInfo and with invalid IP family'() {
        given:
        def nodeNameorFdn = "Node1"
        createNetworkElement("Shared-CNF", null, nodeNameorFdn, null)
        when:
        def res = manager.createLdapConfiguration(nodeNameorFdn, "INVALID")
        then:
        thrown(NscsBadRequestException.class)
    }

    def 'create ldap info with NetworkElement, without ConnectivityInfo, with IP family and without NetworkElementSecurity'() {
        given:
        def nodeNameorFdn = "Node1"
        createNetworkElement("Shared-CNF", null, nodeNameorFdn, null)
        when:
        def res = manager.createLdapConfiguration(nodeNameorFdn, "INET")
        then:
        thrown(NscsBadRequestException.class)
    }

    @Unroll
    def 'create ldap with NetworkElement, with ConnectivityInfo IP address #ipaddress and without IP family and NetworkElementSecurity'() {
        given:
        def nodeNameorFdn = "Node1"
        createNetworkElement("Shared-CNF", null, nodeNameorFdn, null)
        createConnectivityInformationUnderNetworkElement("Shared-CNF", ipaddress)
        when:
        def res = manager.createLdapConfiguration(nodeNameorFdn, null)
        then:
        thrown(NscsBadRequestException.class)
        where:
        ipaddress << [
            "1.2.3.4",
            "2001:db8::1:0:0:1"
        ]
    }

    def 'successful create ldap with NetworkElement, ConnectivityInfo and NetworkElementSecurity without proxyAccountDn attribute'() {
        given:
        def nodeNameorFdn = "Node1"
        createNetworkElement("Shared-CNF", null, nodeNameorFdn, null)
        createConnectivityInformationUnderNetworkElement("Shared-CNF", "1.2.3.4")
        createSecurityFunctionUnderNetworkElement()
        createNetworkElementSecurityUnderSecurityFunction("secure", "RSA_1024", null, null)
        identityManagementService.createProxyAgentAccount() >> proxyAgentAccountData
        comAAInfo.getConnectionData() >> connectionData
        platformConfigurationReader.getProperty("COM_INF_LDAP_ROOT_SUFFIX") >> "base-dn"
        when:
        def res = manager.createLdapConfiguration(nodeNameorFdn, null)
        then:
        res != null
    }

    def 'successful create ldap with NetworkElement, ConnectivityInfo and NetworkElementSecurity with null proxyAccountDn attribute'() {
        given:
        def nodeNameorFdn = "Node1"
        createNetworkElement("Shared-CNF", null, nodeNameorFdn, null)
        createConnectivityInformationUnderNetworkElement("Shared-CNF", "1.2.3.4")
        createSecurityFunctionUnderNetworkElement()
        createNetworkElementSecurityUnderSecurityFunctionWithProxyAccountDnAttribute(null)
        identityManagementService.createProxyAgentAccount() >> proxyAgentAccountData
        comAAInfo.getConnectionData() >> connectionData
        platformConfigurationReader.getProperty("COM_INF_LDAP_ROOT_SUFFIX") >> "base-dn"
        when:
        def res = manager.createLdapConfiguration(nodeNameorFdn, null)
        then:
        res != null
    }

    @Unroll
    def 'successful create ldap with NetworkElement, ConnectivityInfo and NetworkElementSecurity with deleted #deleted not null proxyAccountDn attribute'() {
        given:
        def nodeNameorFdn = "Node1"
        createNetworkElement("Shared-CNF", null, nodeNameorFdn, null)
        createConnectivityInformationUnderNetworkElement("Shared-CNF", "1.2.3.4")
        createSecurityFunctionUnderNetworkElement()
        createNetworkElementSecurityUnderSecurityFunctionWithProxyAccountDnAttribute('oldproxyaccount')
        identityManagementService.createProxyAgentAccount() >> proxyAgentAccountData
        comAAInfo.getConnectionData() >> connectionData
        platformConfigurationReader.getProperty("COM_INF_LDAP_ROOT_SUFFIX") >> "base-dn"
        identityManagementService.deleteProxyAgentAccount("oldproxyaccount") >> deleted
        when:
        def res = manager.createLdapConfiguration(nodeNameorFdn, null)
        then:
        res != null
        where:
        deleted << [true, false]
    }

    def 'successful create ldap with NetworkElement, ConnectivityInfo and NetworkElementSecurity with failed delete of not null proxyAccountDn attribute'() {
        given:
        def nodeNameorFdn = "Node1"
        createNetworkElement("Shared-CNF", null, nodeNameorFdn, null)
        createConnectivityInformationUnderNetworkElement("Shared-CNF", "1.2.3.4")
        createSecurityFunctionUnderNetworkElement()
        createNetworkElementSecurityUnderSecurityFunctionWithProxyAccountDnAttribute('oldproxyaccount')
        identityManagementService.createProxyAgentAccount() >> proxyAgentAccountData
        comAAInfo.getConnectionData() >> connectionData
        platformConfigurationReader.getProperty("COM_INF_LDAP_ROOT_SUFFIX") >> "base-dn"
        identityManagementService.deleteProxyAgentAccount("oldproxyaccount") >> {
            throw new IdentityManagementServiceException()
        }
        when:
        def res = manager.createLdapConfiguration(nodeNameorFdn, null)
        then:
        thrown(UnexpectedErrorException.class)
    }

    def 'failed create ldap with NetworkElement, ConnectivityInfo and NetworkElementSecurity without proxyAccountDn attribute'() {
        given:
        def nodeNameorFdn = "Node1"
        createNetworkElement("Shared-CNF", null, nodeNameorFdn, null)
        createConnectivityInformationUnderNetworkElement("Shared-CNF", "1.2.3.4")
        createSecurityFunctionUnderNetworkElement()
        createNetworkElementSecurityUnderSecurityFunction("secure", "RSA_1024", null, null)
        identityManagementService.createProxyAgentAccount() >> {
            throw new IdentityManagementServiceException()
        }
        when:
        def res = manager.createLdapConfiguration(nodeNameorFdn, null)
        then:
        thrown(UnexpectedErrorException.class)
    }

    def 'delete ldap without NetworkElement'() {
        given:
        def nodeNameorFdn = "Node1"
        when:
        def res = manager.deleteLdapConfiguration(nodeNameorFdn)
        then:
        thrown(NscsBadRequestException.class)
    }

    def 'delete ldap with NetworkElement and without NetworkElementSecurity'() {
        given:
        def nodeNameorFdn = "Node1"
        createNetworkElement("Shared-CNF", null, nodeNameorFdn, null)
        when:
        def res = manager.deleteLdapConfiguration(nodeNameorFdn)
        then:
        thrown(NscsBadRequestException.class)
    }

    def 'successful delete ldap with NetworkElement and NetworkElementSecurity without proxyAccountDn attribute'() {
        given:
        def nodeNameorFdn = "Node1"
        createNetworkElement("Shared-CNF", null, nodeNameorFdn, null)
        createSecurityFunctionUnderNetworkElement()
        createNetworkElementSecurityUnderSecurityFunction("secure", "RSA_1024", null, null)
        when:
        def NscsResourceInstance res = manager.deleteLdapConfiguration(nodeNameorFdn)
        then:
        res != null
        res.getStatus() == "NO_CONTENT"
        res.getResource() == "nodes"
        res.getResourceId() == "Node1"
        res.getSubResource() == "ldap"
        res.getSubResourceId() == null
    }

    def 'successful delete ldap with NetworkElement and NetworkElementSecurity with null proxyAccountDn attribute'() {
        given:
        def nodeNameorFdn = "Node1"
        createNetworkElement("Shared-CNF", null, nodeNameorFdn, null)
        createSecurityFunctionUnderNetworkElement()
        createNetworkElementSecurityUnderSecurityFunctionWithProxyAccountDnAttribute(null)
        when:
        def NscsResourceInstance res = manager.deleteLdapConfiguration(nodeNameorFdn)
        then:
        res != null
        res.getStatus() == "NOT_FOUND"
        res.getResource() == "nodes"
        res.getResourceId() == "Node1"
        res.getSubResource() == "ldap"
        res.getSubResourceId() == null
    }

    def 'successful delete ldap with NetworkElement and NetworkElementSecurity with deleted not null proxyAccountDn attribute'() {
        given:
        def nodeNameorFdn = "Node1"
        createNetworkElement("Shared-CNF", null, nodeNameorFdn, null)
        createSecurityFunctionUnderNetworkElement()
        createNetworkElementSecurityUnderSecurityFunctionWithProxyAccountDnAttribute('oldproxyaccount')
        identityManagementService.deleteProxyAgentAccount("oldproxyaccount") >> true
        when:
        def NscsResourceInstance res = manager.deleteLdapConfiguration(nodeNameorFdn)
        then:
        res != null
        res.getStatus() == "OK"
        res.getResource() == "nodes"
        res.getResourceId() == "Node1"
        res.getSubResource() == "ldap"
        res.getSubResourceId() == "oldproxyaccount"
    }

    def 'successful delete ldap with NetworkElement and NetworkElementSecurity with not deleted not null proxyAccountDn attribute'() {
        given:
        def nodeNameorFdn = "Node1"
        createNetworkElement("Shared-CNF", null, nodeNameorFdn, null)
        createSecurityFunctionUnderNetworkElement()
        createNetworkElementSecurityUnderSecurityFunctionWithProxyAccountDnAttribute('oldproxyaccount')
        identityManagementService.deleteProxyAgentAccount("oldproxyaccount") >> false
        when:
        def NscsResourceInstance res = manager.deleteLdapConfiguration(nodeNameorFdn)
        then:
        res != null
        res.getStatus() == "GONE"
        res.getResource() == "nodes"
        res.getResourceId() == "Node1"
        res.getSubResource() == "ldap"
        res.getSubResourceId() == "oldproxyaccount"
    }

    def 'failed delete ldap with NetworkElement and NetworkElementSecurity with failed delete of not null proxyAccountDn attribute'() {
        given:
        def nodeNameorFdn = "Node1"
        createNetworkElement("Shared-CNF", null, nodeNameorFdn, null)
        createSecurityFunctionUnderNetworkElement()
        createNetworkElementSecurityUnderSecurityFunctionWithProxyAccountDnAttribute('oldproxyaccount')
        identityManagementService.deleteProxyAgentAccount("oldproxyaccount") >> {
            throw new IdentityManagementServiceException()
        }
        when:
        def NscsResourceInstance res = manager.deleteLdapConfiguration(nodeNameorFdn)
        then:
        thrown(UnexpectedErrorException.class)
    }
}