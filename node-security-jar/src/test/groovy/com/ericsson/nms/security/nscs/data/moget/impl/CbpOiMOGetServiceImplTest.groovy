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
package com.ericsson.nms.security.nscs.data.moget.impl

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest;
import com.ericsson.nms.security.nscs.api.model.NodeRef
import com.ericsson.nms.security.nscs.api.model.NodeReference
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithoutParameter
import com.ericsson.nms.security.nscs.data.moget.param.CertStateInfo
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.nms.security.nscs.util.CertDetails
import com.ericsson.oss.services.security.nscs.utils.CbpOiNodeDataSetup

import spock.lang.Unroll

class CbpOiMOGetServiceImplTest extends CbpOiNodeDataSetup {

    @ObjectUnderTest
    private CbpOiMOGetServiceImpl cbpOiMOGetServiceImpl

    @MockedImplementation
    private NscsCapabilityModelService nscsCapabilityModelService

    private nodeName = "5G116vDU001"

    def setup() {
        NscsCMReaderService.deploymentEnv = NscsCMReaderService.DeploymentEnvironment.PRODUCTION
        nscsCapabilityModelService.getComEcimDefaultTrustCategoryIds(_) >> ["OAM":"oamTrustCategory", "IPSEC":"ipsecTrustCategory"]
        nscsCapabilityModelService.getDefaultEnrollmentCaTrustCategoryId(_) >> ["OAM":"oamCmpCaTrustCategory", "IPSEC":"ipsecCmpCaTrustCategory"]
        nscsCapabilityModelService.getComEcimDefaultNodeCredentialIds(_) >> ["OAM":"oamNodeCredential", "IPSEC":"ipsecNodeCredential"]
    }

    def 'object under test injection' () {
        expect:
        cbpOiMOGetServiceImpl != null
    }

    @Unroll
    def 'get #trustcategory trusted certificates with MeContext and ManagedElement' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with truststore under ManagedElement"
        createTruststoreUnderManagedElement()
        and: "with OAM trust category under truststore"
        createCertificatesUnderTruststore("oamTrustCategory")
        and: "with OAM trusted certificates distributed"
        distributeOamCertificatesUnderOamTrustCategories()
        and:
        NodeReference nodeRef = new NodeRef(nodeName)
        when: "the certificates are read for the node"
        CertStateInfo certStateInfo = cbpOiMOGetServiceImpl.getTrustCertificateStateInfo(nodeRef, trustcategory)
        then:
        certStateInfo != null
        and:
        def List<CertDetails> certificates = certStateInfo.getCertificates()
        certificates != null
        and:
        certificates.size() == 4
        and:
        for (CertDetails certificate : certificates) {
            assert (CertDetails.matchesDN(certificate.getSubject(), ENM_PKI_Root_CA_SUBJECT) ||
            CertDetails.matchesDN(certificate.getSubject(), ENM_Infrastructure_CA_SUBJECT) ||
            CertDetails.matchesDN(certificate.getSubject(), ENM_OAM_CA_SUBJECT) ||
            CertDetails.matchesDN(certificate.getSubject(), NE_OAM_CA_SUBJECT))
            assert (CertDetails.matchesSN(certificate.getSerial().toString(), ENM_PKI_Root_CA_SN) ||
            CertDetails.matchesSN(certificate.getSerial().toString(), ENM_Infrastructure_CA_SN) ||
            CertDetails.matchesSN(certificate.getSerial().toString(), ENM_OAM_CA_SN )||
            CertDetails.matchesSN(certificate.getSerial().toString(), NE_OAM_CA_SN))
            assert (CertDetails.matchesDN(certificate.getIssuer(), ENM_PKI_Root_CA_ISSUER) ||
            CertDetails.matchesDN(certificate.getIssuer(), ENM_Infrastructure_CA_ISSUER) ||
            CertDetails.matchesDN(certificate.getIssuer(), ENM_OAM_CA_ISSUER) ||
            CertDetails.matchesDN(certificate.getIssuer(), NE_OAM_CA_ISSUER))
        }
        where:
        trustcategory << ["OAM"]
    }

    @Unroll
    def 'get #trustcategory trusted certificates with ManagedElement' () {
        given: "node created with ManagedElement"
        createNodeWithManagedElement(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with truststore under ManagedElement"
        createTruststoreUnderManagedElement()
        and: "with OAM trust category under truststore"
        createCertificatesUnderTruststore("oamTrustCategory")
        and: "with OAM trusted certificates distributed"
        distributeOamCertificatesUnderOamTrustCategories()
        and:
        NodeReference nodeRef = new NodeRef(nodeName)
        when: "the certificates are read for the node"
        CertStateInfo certStateInfo = cbpOiMOGetServiceImpl.getTrustCertificateStateInfo(nodeRef, trustcategory)
        then:
        certStateInfo != null
        and:
        def List<CertDetails> certificates = certStateInfo.getCertificates()
        certificates != null
        and:
        certificates.size() == 4
        and:
        for (CertDetails certificate : certificates) {
            assert (CertDetails.matchesDN(certificate.getSubject(), ENM_PKI_Root_CA_SUBJECT) ||
            CertDetails.matchesDN(certificate.getSubject(), ENM_Infrastructure_CA_SUBJECT) ||
            CertDetails.matchesDN(certificate.getSubject(), ENM_OAM_CA_SUBJECT) ||
            CertDetails.matchesDN(certificate.getSubject(), NE_OAM_CA_SUBJECT))
            assert (CertDetails.matchesSN(certificate.getSerial().toString(), ENM_PKI_Root_CA_SN) ||
            CertDetails.matchesSN(certificate.getSerial().toString(), ENM_Infrastructure_CA_SN) ||
            CertDetails.matchesSN(certificate.getSerial().toString(), ENM_OAM_CA_SN )||
            CertDetails.matchesSN(certificate.getSerial().toString(), NE_OAM_CA_SN))
            assert (CertDetails.matchesDN(certificate.getIssuer(), ENM_PKI_Root_CA_ISSUER) ||
            CertDetails.matchesDN(certificate.getIssuer(), ENM_Infrastructure_CA_ISSUER) ||
            CertDetails.matchesDN(certificate.getIssuer(), ENM_OAM_CA_ISSUER) ||
            CertDetails.matchesDN(certificate.getIssuer(), NE_OAM_CA_ISSUER))
        }
        where:
        trustcategory << ["OAM"]
    }

    @Unroll
    def 'get #trustcategory trusted certificates with MeContext and without ManagedElement' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with truststore under MeContext"
        createTruststoreUnderMeContext()
        and: "with OAM trust category under truststore"
        createCertificatesUnderTruststore("oamTrustCategory")
        and: "with OAM trusted certificates distributed"
        distributeOamCertificatesUnderOamTrustCategories()
        and:
        NodeReference nodeRef = new NodeRef(nodeName)
        when: "the certificates are read for the node"
        CertStateInfo certStateInfo = cbpOiMOGetServiceImpl.getTrustCertificateStateInfo(nodeRef, trustcategory)
        then:
        certStateInfo != null
        and:
        def List<CertDetails> certificates = certStateInfo.getCertificates()
        certificates != null
        and:
        certificates.size() == 4
        and:
        for (CertDetails certificate : certificates) {
            assert (CertDetails.matchesDN(certificate.getSubject(), ENM_PKI_Root_CA_SUBJECT) ||
            CertDetails.matchesDN(certificate.getSubject(), ENM_Infrastructure_CA_SUBJECT) ||
            CertDetails.matchesDN(certificate.getSubject(), ENM_OAM_CA_SUBJECT) ||
            CertDetails.matchesDN(certificate.getSubject(), NE_OAM_CA_SUBJECT))
            assert (CertDetails.matchesSN(certificate.getSerial().toString(), ENM_PKI_Root_CA_SN) ||
            CertDetails.matchesSN(certificate.getSerial().toString(), ENM_Infrastructure_CA_SN) ||
            CertDetails.matchesSN(certificate.getSerial().toString(), ENM_OAM_CA_SN )||
            CertDetails.matchesSN(certificate.getSerial().toString(), NE_OAM_CA_SN))
            assert (CertDetails.matchesDN(certificate.getIssuer(), ENM_PKI_Root_CA_ISSUER) ||
            CertDetails.matchesDN(certificate.getIssuer(), ENM_Infrastructure_CA_ISSUER) ||
            CertDetails.matchesDN(certificate.getIssuer(), ENM_OAM_CA_ISSUER) ||
            CertDetails.matchesDN(certificate.getIssuer(), NE_OAM_CA_ISSUER))
        }
        where:
        trustcategory << ["OAM"]
    }

    @Unroll
    def 'get #trustcategory trusted certificates with MeContext and ManagedElement and without truststore' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and:
        NodeReference nodeRef = new NodeRef(nodeName)
        when: "the certificates are read for the node"
        CertStateInfo certStateInfo = cbpOiMOGetServiceImpl.getTrustCertificateStateInfo(nodeRef, trustcategory)
        then:
        certStateInfo != null
        and:
        def List<CertDetails> certificates = certStateInfo.getCertificates()
        certificates != null
        and:
        certificates.size() == 1
        and:
        certificates.get(0).getSubject() == null
        certificates.get(0).getSerial() == null
        certificates.get(0).getIssuer() == null
        where:
        trustcategory << ["OAM"]
    }

    @Unroll
    def 'get #trustcategory trusted certificates with MeContext and ManagedElement and without trusted certificates' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with truststore under ManagedElement"
        createTruststoreUnderManagedElement()
        and: "with OAM trust category under truststore"
        createCertificatesUnderTruststore("oamTrustCategory")
        when: "the certificates are read for the node"
        NodeReference nodeRef = new NodeRef(nodeName)
        CertStateInfo certStateInfo = cbpOiMOGetServiceImpl.getTrustCertificateStateInfo(nodeRef, trustcategory)
        then:
        certStateInfo != null
        and:
        def List<CertDetails> certificates = certStateInfo.getCertificates()
        certificates != null
        and:
        certificates.size() == 1
        and:
        certificates.get(0).getSubject() == null
        certificates.get(0).getSerial() == null
        certificates.get(0).getIssuer() == null
        where:
        trustcategory << ["OAM"]
    }

    @Unroll
    def 'get trusted certificates with wrong trust category #trustcategory' () {
        given:
        NodeReference nodeRef = new NodeRef(nodeName)
        when: "the certificates are read for the node"
        CertStateInfo certStateInfo = cbpOiMOGetServiceImpl.getTrustCertificateStateInfo(nodeRef, trustcategory)
        then:
        certStateInfo == null
        where:
        trustcategory << ["", null]
    }

    def 'get trusted certificates with wrong node reference' () {
        given:
        NodeReference nodeRef = null
        when: "the certificates are read for the node"
        CertStateInfo certStateInfo = cbpOiMOGetServiceImpl.getTrustCertificateStateInfo(nodeRef, "OAM")
        then:
        certStateInfo == null
    }

    def 'get OAM certificate with MeContext and ManagedElement' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with keystore under ManagedElement"
        createKeystoreUnderManagedElement()
        and: "with asymmetric-keys under keystore"
        createAsymmetricKeysUnderKeystore()
        and: "with asymmetric-keys under keystore"
        createAsymmetricKeyUnderAsymmetricKeys("oamNodeCredential")
        and: "with OAM certificate issued"
        issueOamCertificateUnderOamNodeCredential()
        and:
        NodeReference nodeRef = new NodeRef(nodeName)
        when: "the OAM certificate is read for the node"
        CertStateInfo certStateInfo = cbpOiMOGetServiceImpl.getCertificateIssueStateInfo(nodeRef, "OAM")
        then:
        certStateInfo != null
        and:
        def List<CertDetails> certificates = certStateInfo.getCertificates()
        certificates != null
        and:
        certificates.size() == 1
        and:
        def CertDetails certificate = certificates.get(0)
        CertDetails.matchesDN(certificate.getSubject(), OAM_NODE_CREDENTIAL_SUBJECT)
        CertDetails.matchesSN(certificate.getSerial().toString(), OAM_NODE_CREDENTIAL_SN)
        CertDetails.matchesDN(certificate.getIssuer(), OAM_NODE_CREDENTIAL_ISSUER)
    }

    def 'get OAM certificate with ManagedElement' () {
        given: "node created with ManagedElement"
        createNodeWithManagedElement(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with keystore under ManagedElement"
        createKeystoreUnderManagedElement()
        and: "with asymmetric-keys under keystore"
        createAsymmetricKeysUnderKeystore()
        and: "with asymmetric-keys under keystore"
        createAsymmetricKeyUnderAsymmetricKeys("oamNodeCredential")
        and: "with OAM certificate issued"
        issueOamCertificateUnderOamNodeCredential()
        and:
        NodeReference nodeRef = new NodeRef(nodeName)
        when: "the OAM certificate is read for the node"
        CertStateInfo certStateInfo = cbpOiMOGetServiceImpl.getCertificateIssueStateInfo(nodeRef, "OAM")
        then:
        certStateInfo != null
        and:
        def List<CertDetails> certificates = certStateInfo.getCertificates()
        certificates != null
        and:
        certificates.size() == 1
        and:
        def CertDetails certificate = certificates.get(0)
        CertDetails.matchesDN(certificate.getSubject(), OAM_NODE_CREDENTIAL_SUBJECT)
        CertDetails.matchesSN(certificate.getSerial().toString(), OAM_NODE_CREDENTIAL_SN)
        CertDetails.matchesDN(certificate.getIssuer(), OAM_NODE_CREDENTIAL_ISSUER)
    }

    def 'get OAM certificate with MeContext and without ManagedElement' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with keystore under MeContext"
        createKeystoreUnderMeContext()
        and: "with asymmetric-keys under keystore"
        createAsymmetricKeysUnderKeystore()
        and: "with asymmetric-keys under keystore"
        createAsymmetricKeyUnderAsymmetricKeys("oamNodeCredential")
        and: "with OAM certificate issued"
        issueOamCertificateUnderOamNodeCredential()
        and:
        NodeReference nodeRef = new NodeRef(nodeName)
        when: "the OAM certificate is read for the node"
        CertStateInfo certStateInfo = cbpOiMOGetServiceImpl.getCertificateIssueStateInfo(nodeRef, "OAM")
        then:
        certStateInfo != null
        and:
        def List<CertDetails> certificates = certStateInfo.getCertificates()
        certificates != null
        and:
        certificates.size() == 1
        and:
        def CertDetails certificate = certificates.get(0)
        CertDetails.matchesDN(certificate.getSubject(), OAM_NODE_CREDENTIAL_SUBJECT)
        CertDetails.matchesSN(certificate.getSerial().toString(), OAM_NODE_CREDENTIAL_SN)
        CertDetails.matchesDN(certificate.getIssuer(), OAM_NODE_CREDENTIAL_ISSUER)
    }

    def 'get OAM certificate with MeContext and ManagedElement and without keystore' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and:
        NodeReference nodeRef = new NodeRef(nodeName)
        when: "the OAM certificate is read for the node"
        CertStateInfo certStateInfo = cbpOiMOGetServiceImpl.getCertificateIssueStateInfo(nodeRef, "OAM")
        then:
        certStateInfo != null
        and:
        def List<CertDetails> certificates = certStateInfo.getCertificates()
        certificates != null
        and:
        certificates.size() == 1
        and:
        certificates.get(0).getSubject() == null
        certificates.get(0).getSerial() == null
        certificates.get(0).getIssuer() == null
    }

    def 'get OAM certificate with MeContext and ManagedElement and without asymmetric-keys' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with keystore under ManagedElement"
        createKeystoreUnderManagedElement()
        and:
        NodeReference nodeRef = new NodeRef(nodeName)
        when: "the OAM certificate is read for the node"
        CertStateInfo certStateInfo = cbpOiMOGetServiceImpl.getCertificateIssueStateInfo(nodeRef, "OAM")
        then:
        certStateInfo != null
        and:
        def List<CertDetails> certificates = certStateInfo.getCertificates()
        certificates != null
        and:
        certificates.size() == 1
        and:
        certificates.get(0).getSubject() == null
        certificates.get(0).getSerial() == null
        certificates.get(0).getIssuer() == null
    }

    def 'get OAM certificate with MeContext and ManagedElement and without asymmetric-key' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with keystore under ManagedElement"
        createKeystoreUnderManagedElement()
        and: "with asymmetric-keys under keystore"
        createAsymmetricKeysUnderKeystore()
        and:
        NodeReference nodeRef = new NodeRef(nodeName)
        when: "the OAM certificate is read for the node"
        CertStateInfo certStateInfo = cbpOiMOGetServiceImpl.getCertificateIssueStateInfo(nodeRef, "OAM")
        then:
        certStateInfo != null
        and:
        def List<CertDetails> certificates = certStateInfo.getCertificates()
        certificates != null
        and:
        certificates.size() == 1
        and:
        certificates.get(0).getSubject() == null
        certificates.get(0).getSerial() == null
        certificates.get(0).getIssuer() == null
    }

    def 'get OAM certificate with MeContext and ManagedElement and without certificates' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with keystore under ManagedElement"
        createKeystoreUnderManagedElement()
        and: "with asymmetric-keys under keystore"
        createAsymmetricKeysUnderKeystore()
        and: "with asymmetric-keys under keystore"
        createAsymmetricKeyUnderAsymmetricKeys("oamNodeCredential")
        and:
        NodeReference nodeRef = new NodeRef(nodeName)
        when: "the OAM certificate is read for the node"
        CertStateInfo certStateInfo = cbpOiMOGetServiceImpl.getCertificateIssueStateInfo(nodeRef, "OAM")
        then:
        certStateInfo != null
        and:
        def List<CertDetails> certificates = certStateInfo.getCertificates()
        certificates != null
        and:
        certificates.size() == 1
        and:
        certificates.get(0).getSubject() == null
        certificates.get(0).getSerial() == null
        certificates.get(0).getIssuer() == null
    }

    @Unroll
    def 'get OAM certificate with wrong certificate type #certtype' () {
        given:
        NodeReference nodeRef = new NodeRef(nodeName)
        when: "the certificate is read for the node"
        CertStateInfo certStateInfo = cbpOiMOGetServiceImpl.getCertificateIssueStateInfo(nodeRef, certtype)
        then:
        certStateInfo == null
        where:
        certtype << ["", null]
    }

    def 'get OAM certificate with wrong node reference' () {
        given:
        NodeReference nodeRef = null
        when: "the OAM certificate is read for the node"
        CertStateInfo certStateInfo = cbpOiMOGetServiceImpl.getCertificateIssueStateInfo(nodeRef, "OAM")
        then:
        certStateInfo == null
    }

    def "getSecurityLevel verification for vDU node"() {
        given:
        NormalizableNodeReference normalizable = mock(NormalizableNodeReference)
        when:
        cbpOiMOGetServiceImpl.getSecurityLevel(normalizable, "SYNCHRONIZED")
        then:
        noExceptionThrown()
    }

    def "getIpsecConfig verification for vDU node"() {
        given:
        NormalizableNodeReference normalizable = mock(NormalizableNodeReference)
        when:
        cbpOiMOGetServiceImpl.getIpsecConfig(normalizable, "SYNCHRONIZED")
        then:
        noExceptionThrown()
    }

    def "getMoActionState verification for vDU node"() {
        given:
        NormalizableNodeReference normalizable = mock(NormalizableNodeReference)
        and:
        MoActionWithoutParameter action = MoActionWithoutParameter.ComEcim_NodeCredential_cancelEnrollment;
        when:
        cbpOiMOGetServiceImpl.getMoActionState(normalizable.getFdn(), action)
        then:
        noExceptionThrown()
    }

    def "getMoActionwithParameterState verification for vDU node"() {
        given:
        NormalizableNodeReference normalizable = mock(NormalizableNodeReference)
        and:
        MoActionWithParameter action = MoActionWithParameter.IpSec_initCertEnrollment;
        when:
        cbpOiMOGetServiceImpl.getMoActionState(normalizable.getFdn(), action)
        then:
        noExceptionThrown()
    }

    def "getCrlCheckStatus verification for vDU node"() {
        given:
        NormalizableNodeReference normalizable = mock(NormalizableNodeReference)
        when:
        cbpOiMOGetServiceImpl.getCrlCheckStatus(normalizable, "OAM")
        then:
        noExceptionThrown()
    }

    def "validateNodeForCrlCheckMO verification for vDU node"() {
        given:
        NormalizableNodeReference normalizable = mock(NormalizableNodeReference)
        when:
        cbpOiMOGetServiceImpl.validateNodeForCrlCheckMO(normalizable, "OAM")
        then:
        noExceptionThrown()
    }

    def "listNtpServerDetails verification for vDU node"() {
        given:
        NormalizableNodeReference normalizable = mock(NormalizableNodeReference)
        when:
        cbpOiMOGetServiceImpl.listNtpServerDetails(normalizable)
        then:
        noExceptionThrown()
    }

    def "validateNodeForNtp verification for vDU node"() {
        given:
        NormalizableNodeReference normalizable = mock(NormalizableNodeReference)
        when:
        cbpOiMOGetServiceImpl.validateNodeForNtp(normalizable)
        then:
        noExceptionThrown()
    }

    def "getNodeSupportedFormatOfKeyAlgorithm verification for vDU node"  () {
        given:
        NodeReference nodeRef = new NodeRef(nodeName)
        when:
        String keyAlgorithm = cbpOiMOGetServiceImpl.getNodeSupportedFormatOfKeyAlgorithm(nodeRef, "1")
        then:
        assert(keyAlgorithm.equals("rsa2048"))
    }
}
