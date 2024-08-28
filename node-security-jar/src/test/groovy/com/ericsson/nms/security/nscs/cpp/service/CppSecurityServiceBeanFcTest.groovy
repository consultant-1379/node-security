/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.nms.security.nscs.cpp.service

import java.nio.charset.StandardCharsets
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys
import com.ericsson.nms.security.nscs.api.enums.DigestAlgorithm
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode
import com.ericsson.nms.security.nscs.api.iscf.BaseSubjectAltNameDataType
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation.ModelIdentifierType
import com.ericsson.nms.security.nscs.api.model.NodeRef
import com.ericsson.nms.security.nscs.api.model.NodeReference
import com.ericsson.nms.security.nscs.api.model.service.NscsModelInfo
import com.ericsson.nms.security.nscs.api.pki.*
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfo
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.nms.security.nscs.handler.command.impl.MockUtils
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl
import com.ericsson.nms.security.nscs.util.EnrollingInformation
import com.ericsson.nms.security.nscs.utilities.NscsCbpOiNodeUtility
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.TargetTypeInformation
import com.ericsson.oss.itpf.security.pki.common.model.*
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltName
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameField
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameFieldType
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameString
import com.ericsson.oss.itpf.security.pki.manager.model.EnrollmentInfo
import com.ericsson.oss.itpf.security.pki.manager.model.EntityType
import com.ericsson.oss.itpf.security.pki.manager.model.TrustedEntityInfo
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity
import com.ericsson.oss.itpf.security.pki.manager.model.profiles.EntityProfile
import com.ericsson.oss.itpf.smrs.SmrsService
import com.ericsson.oss.services.cm.cmshared.dto.CmObject
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse

import spock.lang.Unroll
import sun.security.x509.X500Name

/**
 *
 * @author epaocas
 */
class CppSecurityServiceBeanFcTest extends CdiSpecification {

    @ObjectUnderTest
    CppSecurityServiceBean cppSecServBean;

    @MockedImplementation
    NscsCMReaderService readerService

    @MockedImplementation
    NscsCapabilityModelService nscsCapabilityModelService

    @MockedImplementation
    NscsModelServiceImpl modelServiceImpl

    @MockedImplementation
    NscsPkiEntitiesManagerIF nscsPkiManager

    @MockedImplementation
    SmrsService smrsService

    @MockedImplementation
    NscsPkiCertificateManager nscsPkiCertificateManager

    @MockedImplementation
    NscsCbpOiNodeUtility nscsCbpOiNodeUtility

    @MockedImplementation
    NscsModelServiceImpl nscsModelServiceImpl

    @Override
    def addAdditionalInjectionProperties(InjectionProperties injectionProperties) {
        injectionProperties.autoLocateFrom('com.ericsson.nms.security.nscs.utilities')
    }

    def nodeFdn = "erbsNode"
    def nodeType = "ERBS"
    def defaultOamEntityProfileName = "MicroRBS_OAM_CHAIN_EP"
    def defaultIpsecEntityProfileName = "MicroRBSIPSec_SAN_CHAIN_EP"

    def CPP_15B_MODEL_INFO = new NodeModelInformation("5.1.63", ModelIdentifierType.MIM_VERSION, nodeType)

    static def certificateBegin = "-----BEGIN CERTIFICATE-----\n";
    static def certificateEnd = "\n-----END CERTIFICATE-----";
    static def certificateString = certificateBegin +
    "MIIEhDCCA2ygAwIBAgIIC5Km6u749ZMwDQYJKoZIhvcNAQELBQAwWDEeMBwGA1UE" +
    "AwwVRU5NX0luZnJhc3RydWN0dXJlX0NBMQswCQYDVQQGEwJTRTEWMBQGA1UECwwN" +
    "QlVDSV9EVUFDX05BTTERMA8GA1UECgwIRVJJQ1NTT04wHhcNMjAwOTI0MTEzMjQy" +
    "WhcNMjgwOTI0MTEwMTQyWjBNMRMwEQYDVQQDDApFTk1fT0FNX0NBMQswCQYDVQQG" +
    "EwJTRTEWMBQGA1UECwwNQlVDSV9EVUFDX05BTTERMA8GA1UECgwIRVJJQ1NTT04w" +
    "ggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCS/YsK8Pc7e8l3z6znLlVX" +
    "rT3mcdnQ+X9n/1flPDFg3q6eEzPD/ZjqIxyp+bJ5bqasm3zkP2F9K3zcB4+f0yJk" +
    "R1u1Qy9MFSeTH94MkQ2Z6oUizl7yYP4uL8ovuYLCW1UyQ79RegC0PhpL3cQ/ZpxJ" +
    "qZHdnuB87zPC5Mj2GGLydkAMnBUZ68iUH0I37YpEreVHeO1dwNOSfGqrXZfIrueJ" +
    "HIksrVuwfenlB9LL5orSw+EtW90uYOjI4sZ659ymI5TF0p+OqDUINbjtD8gi6Kgt" +
    "KfNfyGgAg9KjnuQkjFqpKP5jft2tw+dS+6mc8gwBNyL3ZVGAJVOTliiqw5sRLJOP" +
    "AgMBAAGjggFbMIIBVzCB8wYDVR0fBIHrMIHoMGygaqBohmZodHRwOi8vMTkyLjE2" +
    "OC4wLjE1NTo4MDkyL3BraS1jZHBzP2NhX25hbWU9RU5NX0luZnJhc3RydWN0dXJl" +
    "X0NBJmNhX2NlcnRfc2VyaWFsbnVtYmVyPTY1Njg3YzYzMDYzYWU5MjMweKB2oHSG" +
    "cmh0dHA6Ly9bMjAwMToxYjcwOjgyYTE6MTAzOjoxODFdOjgwOTIvcGtpLWNkcHM/" +
    "Y2FfbmFtZT1FTk1fSW5mcmFzdHJ1Y3R1cmVfQ0EmY2FfY2VydF9zZXJpYWxudW1i" +
    "ZXI9NjU2ODdjNjMwNjNhZTkyMzAdBgNVHQ4EFgQULaYWNZlkq72vBErY+XyfwIYA" +
    "3oYwDwYDVR0TAQH/BAUwAwEB/zAfBgNVHSMEGDAWgBRzB213X2VUnXl5z1LVOfZv" +
    "RKFYbDAOBgNVHQ8BAf8EBAMCAQYwDQYJKoZIhvcNAQELBQADggEBAHF+SshMeYII" +
    "AGiTRVtFr+8WBu12O8g0E4rrxJC/5sNvCri6ZvxCTwKqZBsFAEpLq3Kzcw/1FLBw" +
    "HZi4hetdoCixUw7B1FROouUzTXyz1rkPFl9QxN2lzkcY5oXd0HVyIIDjgHWbH2sM" +
    "2q/pJwwDIZFHpbIR8zncaA3nDM81a6ctF/1aLKMRT0VDp01Wj8p8HLvkp0vUrfW3" +
    "F0x347B+VXXlpjU02vPVa2HBWTEs+sGMOAWE97EGWAzB3eUy7kToA+ACwwuIsYCc" +
    "YeNgX0xHzUhg5raKxqX0zhFCwGgaAEG3RU2nTmkR09D6gZsNWcO4eTvqewCnzdpT" +
    "+ReZuxS+iXM=" + certificateEnd


    def createEntity(
            final String fdn, NodeEntityCategory category,
            final SubjectAltNameFieldType subjectAltNameType, final String subjectAltNameVal) {
        final EntityInfo entityInfo = new EntityInfo();
        entityInfo.setId(1);

        final SubjectAltName subjectAltNameValues = new SubjectAltName();
        if (NodeEntityCategory.IPSEC.equals(category)) {
            final SubjectAltNameField subjectAltNameField = new SubjectAltNameField();
            subjectAltNameField.setType(subjectAltNameType);

            final SubjectAltNameString subjectAltNameValueString = new SubjectAltNameString();
            subjectAltNameValueString.setValue(subjectAltNameVal);
            subjectAltNameField.setValue(subjectAltNameValueString);
            final List<SubjectAltNameField> subjectAltNameValueList = new ArrayList<>();
            subjectAltNameValueList.add(subjectAltNameField);

            subjectAltNameValues.setSubjectAltNameFields(subjectAltNameValueList);
        }
        entityInfo.setName(fdn + "-" + category.toString());

        final Subject subject = new Subject();

        final SubjectField subjectFieldCN = new SubjectField();
        subjectFieldCN.setType(SubjectFieldType.COMMON_NAME);
        subjectFieldCN.setValue(fdn);

        final List<SubjectField> entSubjectFieldList = new ArrayList<>();
        entSubjectFieldList.add(subjectFieldCN);
        subject.setSubjectFields(entSubjectFieldList);

        entityInfo.setSubject(subject);
        entityInfo.setSubjectAltName(subjectAltNameValues);
        entityInfo.setOTP("OTP");

        CertificateAuthority ca = new CertificateAuthority()
        ca.setName("testCA");
        entityInfo.setIssuer(ca);

        final Entity ee = new Entity();
        ee.setType(EntityType.ENTITY);
        final EntityProfile ep = new EntityProfile();
        ep.setActive(true);
        ep.setName("Test_EP");
        ee.setEntityProfile(ep);
        entityInfo.setStatus(EntityStatus.NEW);
        ee.setEntityInfo(entityInfo);

        final Algorithm keyGenerationAlgorithm = new Algorithm();
        keyGenerationAlgorithm.setKeySize((AlgorithmKeys.RSA_2048.getKeySize()));
        ee.setKeyGenerationAlgorithm(keyGenerationAlgorithm);

        return ee;
    }

    def setup() {
        nscsCapabilityModelService.isEnrollmentModeSupported(CPP_15B_MODEL_INFO, EnrollmentMode.CMPv2_VC) >> true
        nscsCapabilityModelService.getDefaultDigestAlgorithm(CPP_15B_MODEL_INFO) >> DigestAlgorithm.SHA256
        nscsCapabilityModelService.isKSandEMSupported(CPP_15B_MODEL_INFO) >> true
        nscsCapabilityModelService.isSynchronousEnrollmentSupported(CPP_15B_MODEL_INFO) >> false
        nscsCapabilityModelService.isEnrollmentRootCAFingerPrintSupported(_, _) >> true
        nscsCapabilityModelService.isCertificateAuthorityDnSupported(CPP_15B_MODEL_INFO) >> true
    }

    def erbsNodeSetup() {
        def node = new NodeRef(nodeFdn)
        def normRef = MockUtils.createNormalizableNodeRef(node.getName())
        readerService.getNormalizedNodeReference(_ as NodeReference) >> normRef
        readerService.getNormalizableNodeReference(_ as NodeReference) >> normRef
        readerService.exists(_ as String) >> true
        def cmResp = Mock(CmResponse)
        def cmObj = Mock(CmObject)
        def nscsModelInfo = Mock(NscsModelInfo)
        Collection<CmObject> cmObjs = new ArrayList<>();
        Map<String, String> attributesEnrollment = new HashMap<>()
        attributesEnrollment.put("enrollmentMode", EnrollmentMode.CMPv2_VC.toString())
        cmObj.getAttributes() >> attributesEnrollment
        cmObjs.add(cmObj)
        cmResp.getCmObjects() >> cmObjs
        readerService.getMOAttribute(_ as String, _ as String, _ as String, _ as String) >> cmResp
        modelServiceImpl.getConnectivityInfo(TargetTypeInformation.CATEGORY_NODE, CPP_15B_MODEL_INFO.getNodeType()) >> nscsModelInfo
    }

    def "Given a node enrolled as OAM, when EnrollmentInfo is generated for AP, then EntityProfile is set to default profile"() {
        given: 'Node exists in DPS'
        erbsNodeSetup()
        and: 'OAM Entity associated to node'
        def nodeEntity = createEntity(nodeFdn, NodeEntityCategory.OAM, null, null)
        def ei = Mock(EnrollmentInfo)
        EnrollmentPartialInfos epi = new EnrollmentPartialInfos(nodeEntity, ei, AlgorithmKeys.RSA_2048)
        nscsPkiManager.getEnrollmentEntityInfo(*_) >> epi
        def cmResp = Mock(CmResponse)
        def cmObj = Mock(CmObject)
        Collection<CmObject> cmObjs = new ArrayList<>();
        Map<String, String> attributesEnrollment = new HashMap<>()
        attributesEnrollment.put("ipAddress", "122.111.111.111")
        cmObj.getAttributes() >> attributesEnrollment
        cmObjs.add(cmObj)
        cmResp.getCmObjects() >> cmObjs
        readerService.getMOAttribute(_,_,_,_) >> cmResp
        nscsCapabilityModelService.getDefaultEntityProfile(CPP_15B_MODEL_INFO, NodeEntityCategory.OAM) >> defaultOamEntityProfileName
        when: 'OAM EnrollmentInfo is generated by AP process'
        ScepEnrollmentInfo sei = cppSecServBean.generateOamEnrollmentInfo(nodeFdn, null, null, null, EnrollmentMode.CMPv2_VC, CPP_15B_MODEL_INFO)

        then: 'EntityProfile in Entity is set to OAM default profile'
        1 * nscsPkiManager.getEnrollmentEntityInfo(*_) >> epi
    }

    def "Given a node enrolled as IPSEC, when EnrollmentInfo is generated for AP, then EntityProfile is set to default profile"() {
        given: 'Node exists in DPS'
        erbsNodeSetup()

        and: 'IPSEC Entity associated to node'
        def sanIpAddress = "10.11.12.13"
        def nodeEntity = createEntity(nodeFdn, NodeEntityCategory.IPSEC, SubjectAltNameFieldType.IP_ADDRESS, sanIpAddress)
        nscsCapabilityModelService.getDefaultEntityProfile(CPP_15B_MODEL_INFO, NodeEntityCategory.IPSEC) >> defaultIpsecEntityProfileName
        def ei = Mock(EnrollmentInfo)
        EnrollmentPartialInfos epi = new EnrollmentPartialInfos(nodeEntity, ei, AlgorithmKeys.RSA_2048)
        BaseSubjectAltNameDataType sanString = new SubjectAltNameStringType(sanIpAddress)
        nscsPkiManager.getEnrollmentEntityInfo(*_) >> epi
        def cmResp = Mock(CmResponse)
        def cmObj = Mock(CmObject)
        Collection<CmObject> cmObjs = new ArrayList<>();
        Map<String, String> attributesEnrollment = new HashMap<>()
        attributesEnrollment.put("ipAddress", "122.111.111.111")
        cmObj.getAttributes() >> attributesEnrollment
        cmObjs.add(cmObj)
        cmResp.getCmObjects() >> cmObjs
        readerService.getMOAttribute(_,_,_,_) >> cmResp
        when: 'IPSEC EnrollmentInfo is generated by AP process'
        cppSecServBean.generateIpsecEnrollmentInfo(nodeFdn, null, sanString, SubjectAltNameFormat.IPV4,
                EnrollmentMode.CMPv2_VC, CPP_15B_MODEL_INFO)
        then: 'EntityProfile in Entity is set to IPSEC default profile'
        1 * nscsPkiManager.getEnrollmentEntityInfo(*_) >> epi
    }

    @Unroll
    def "Given an ERBS node, when IPSec EnrollmentInfo is generated with valid SubjectAltName, then no exception is received"() {
        given: 'ERBS node exists in DPS'
        erbsNodeSetup()
        def enrollmentInfo = Mock(EnrollmentInfo)
        def entity = Mock(Entity)
        def entityInfo = Mock(EntityInfo)
        def issuer = Mock(CertificateAuthority)
        issuer.getName() >> "CA"
        entityInfo.getOTP() >> "OneTime"
        entityInfo.getIssuer() >> issuer
        entity.getEntityInfo() >> entityInfo
        EnrollmentPartialInfos epi = new EnrollmentPartialInfos(entity, enrollmentInfo, AlgorithmKeys.RSA_2048)
        nscsPkiManager.getEnrollmentEntityInfo(*_) >> epi
        def cmResp = Mock(CmResponse)
        def cmObj = Mock(CmObject)
        Collection<CmObject> cmObjs = new ArrayList<>();
        Map<String, String> attributesEnrollment = new HashMap<>()
        attributesEnrollment.put("ipAddress", "122.111.111.111")
        cmObj.getAttributes() >> attributesEnrollment
        cmObjs.add(cmObj)
        cmResp.getCmObjects() >> cmObjs
        readerService.getMOAttribute(_,_,_,_) >> cmResp

        when: 'IPSEC EnrollmentInfo is generated by AP process with valid SAN format/value'
        BaseSubjectAltNameDataType sanString = new SubjectAltNameStringType(subjectAltNameValue)
        cppSecServBean.generateIpsecEnrollmentInfo(nodeFdn, null, sanString, subjectAltNameFormat,
                EnrollmentMode.CMPv2_VC, CPP_15B_MODEL_INFO)

        then: 'No exception is received'
        noExceptionThrown()

        where:
        subjectAltNameFormat      | subjectAltNameValue
        SubjectAltNameFormat.FQDN | "WLK_ENB510.wireless.verizon.com"
        SubjectAltNameFormat.FQDN | "000540_WLK_ENB540-ipsec.verizonwireless.com"
        SubjectAltNameFormat.FQDN | "WLK_ENB510.wireless.verizon.com**"
        SubjectAltNameFormat.FQDN | "!@#\$%^[]()++.&&\\&**"
        SubjectAltNameFormat.FQDN | "//Subject123"
        SubjectAltNameFormat.FQDN | "localhost"

        SubjectAltNameFormat.FQDN | "test-ok-name-with-hyphen.com"
        SubjectAltNameFormat.FQDN | "3com-name-with-numbers-as-first-char-test-ok.com"
        SubjectAltNameFormat.FQDN | "test-ok-domain-starts-with-number.3com"
        SubjectAltNameFormat.FQDN | ".initial-dot.failure.com"
        SubjectAltNameFormat.FQDN | "test-ok.domain-len-one.c."
        SubjectAltNameFormat.FQDN | "test-ok.domain-len-one-with.final-dot.a."
        SubjectAltNameFormat.FQDN | "3com.c."
        SubjectAltNameFormat.FQDN | "test-ok.name-ending-with-hyphen.cd-.net"
        SubjectAltNameFormat.FQDN | "test-ok.aa-.name-ending-by-hyphen.cd.net"
        SubjectAltNameFormat.FQDN | "test-ok.name-starting-by-hyphen.-cd.net"
        SubjectAltNameFormat.FQDN | "test-ok-domain.with-initial-letter.but-with-hyphen.and-numbers-inside.com-3net"
        SubjectAltNameFormat.FQDN | "test-ok-domain-with.numbers.a3"
        SubjectAltNameFormat.FQDN | "test-ok-domain-with.number-and-final-dot.a3."
        SubjectAltNameFormat.FQDN | "3com.com."
        SubjectAltNameFormat.FQDN | "test-ok-domain-len-two.b.dd"
        SubjectAltNameFormat.FQDN | "a5.c-d.net"
        SubjectAltNameFormat.FQDN | "Test-OK-Mix-Of-lowerCase.and-UPPERCase.Cc.dD"
        SubjectAltNameFormat.FQDN | "aaaaaaaaaaaaaaaa-TEST_OK-aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb.cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc.ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd.e"
        SubjectAltNameFormat.FQDN | "test-ok-name-with-only-noe-label"
        SubjectAltNameFormat.FQDN | "test-ok-name-with-only-noe-label."

        SubjectAltNameFormat.FQDN | "?"
        SubjectAltNameFormat.IPV4 | "172.16.0.4"
        SubjectAltNameFormat.IPV4 | "?"
        SubjectAltNameFormat.IPV6 | "2001:cdba:0000:0000:0000:0000:3257:9652"
        SubjectAltNameFormat.IPV6 | "1080::800:200C:417A"
        SubjectAltNameFormat.IPV6 | "?"
        SubjectAltNameFormat.NONE | "  "
    }

    @Unroll
    def "Given an ERBS node, when IPSec EnrollmentInfo is generated with wrong SubjectAltName, then CppSecurityServiceException is received"() {
        given: 'ERBS node exists in DPS'
        erbsNodeSetup()
        def enrollmentInfo = Mock(EnrollmentInfo)
        def entity = Mock(Entity)
        def entityInfo = Mock(EntityInfo)
        def issuer = Mock(CertificateAuthority)
        issuer.getName() >> "CA"
        entityInfo.getOTP() >> "OneTime"
        entityInfo.getIssuer() >> issuer
        entity.getEntityInfo() >> entityInfo
        EnrollmentPartialInfos epi = new EnrollmentPartialInfos(entity, enrollmentInfo, AlgorithmKeys.RSA_2048)
        nscsPkiManager.getEnrollmentEntityInfo(*_) >> epi

        def cmResp = Mock(CmResponse)
        def cmObj = Mock(CmObject)
        Collection<CmObject> cmObjs = new ArrayList<>();
        Map<String, String> attributesEnrollment = new HashMap<>()
        attributesEnrollment.put("ipAddress", "122.111.111.111")
        cmObj.getAttributes() >> attributesEnrollment
        cmObjs.add(cmObj)
        cmResp.getCmObjects() >> cmObjs
        readerService.getMOAttribute(_,_,_,_) >> cmResp

        when: 'IPSEC EnrollmentInfo is generated by AP process with malformed SubjectAltName value'
        BaseSubjectAltNameDataType sanString = new SubjectAltNameStringType(subjectAltNameValue)
        cppSecServBean.generateIpsecEnrollmentInfo(nodeFdn, null, sanString, subjectAltNameFormat,
                EnrollmentMode.CMPv2_VC, CPP_15B_MODEL_INFO)

        then: 'Expected exception is received'
        thrown CppSecurityServiceException

        where:
        subjectAltNameFormat      | subjectAltNameValue
        SubjectAltNameFormat.FQDN | "WLK_ENB510.wireless..verizon.com"
        SubjectAltNameFormat.FQDN | "000540_WLK ENB540-ipsec.verizonwireless.com"

        SubjectAltNameFormat.FQDN | "failure name with spaces"
        SubjectAltNameFormat.FQDN | "failure-double-dot..aa.com"
        SubjectAltNameFormat.FQDN | "-aa.name-starting-by-hyphen.cd.net"
        SubjectAltNameFormat.FQDN | "aaaaaaaaaaaa-FAILURE-aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb.cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc.dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd"
        SubjectAltNameFormat.FQDN | "aaaaaaaaaaaaaaaa-FAILURE-aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb.cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc.dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd."
        SubjectAltNameFormat.FQDN | "aaaaaaaaaaaaaaaa-FAILURE-aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb.cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc.dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd.e.f"
        SubjectAltNameFormat.FQDN | "Failure-Because-Label-Length-is-too-long-aaaaaaaaaaaaaaaaaaaaaaaa.com"
        SubjectAltNameFormat.FQDN | "Failure-because-total-length-exceeded-255-characters-aaaaaaa.bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb.cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc.dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd.eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee\n" +
                "eeeeeeeeeeeeeeeeeeeeeeeeee."

        SubjectAltNameFormat.IPV4 | "172.16.0"
        SubjectAltNameFormat.IPV6 | "172.16.0.4"
        SubjectAltNameFormat.IPV4 | "2001:cdba:0000:0000:0000:0000:3257:9652"
    }

    def "Given a node integrated in ENM, when enrollment is canceled by AP, all entities and accounts are removed"() {
        given: 'Node created in DPS'
        readerService.getTargetType(nodeFdn) >> nodeType

        when: 'enrollment is canceled by AP process'
        cppSecServBean.cancelSCEPEnrollment(nodeFdn)

        then: 'both end entities (OAM / IPSEC) are deleted'
        2 * nscsPkiManager.deleteEntity(_)

        and: 'all certificates are revoked'
        2 * nscsPkiCertificateManager.revokeEntityCertificates(_ as String)

        and: 'SMRS account is deleted'
        1 * smrsService.getNodeSpecificAccount(_, nodeType, nodeFdn)
        1 * smrsService.deleteSmrsAccount(_)
    }

    def "Given a node not integrated in ENM, when enrollment is canceled by AP, only entities are removed"() {
        given: 'Node does not exists in DPS'
        readerService.getTargetType(nodeFdn) >> null

        when: 'enrollment is canceled by AP process'
        cppSecServBean.cancelSCEPEnrollment(nodeFdn)

        then: 'both end entities (OAM / IPSEC) are deleted'
        2 * nscsPkiManager.deleteEntity(_)

        and: 'all certificates are revoked'
        2 * nscsPkiCertificateManager.revokeEntityCertificates(_ as String)

        and: 'SMRS account is not deleted'
        0 * smrsService.deleteSmrsAccount(_)

        and: 'no exception is received'
        noExceptionThrown()
    }

    def "Given CA name, when Trust CA Info is retrieved for CbpOi node, then information is correct"() {

        given: 'CA name from cli command'
        final InputStream targetPkcs7Stream = new ByteArrayInputStream(certificateString.getBytes(StandardCharsets.UTF_8))
        final X509Certificate x509InstalledCertificate =
                (X509Certificate) (CertificateFactory.getInstance("X.509").generateCertificate(targetPkcs7Stream))

        def caName = (new X500Name(x509InstalledCertificate.getSubjectX500Principal().getName())).getCommonName()
        TrustedEntityInfo trustedEntityInfo = Mock()
        trustedEntityInfo.getEntityName() >> caName
        trustedEntityInfo.getCertificateSerialNumber() >> (x509InstalledCertificate.getSerialNumber().toString(16))
        trustedEntityInfo.getIssuerFullDN() >> x509InstalledCertificate.getIssuerDN().toString()
        nscsPkiManager.getTrustedCAInfoByName(caName) >> trustedEntityInfo
        nscsPkiManager.getCATrusts(caName) >> [x509InstalledCertificate]

        when: 'get Trust CA info for CbpOi node'
        def cbpoiTrustedEntityInfo = cppSecServBean.getCbpOiTrustedCAInfoByName(caName)

        then: 'retrieved information is correct'
        cbpoiTrustedEntityInfo.getName() == caName
        cbpoiTrustedEntityInfo.getIssuer() == x509InstalledCertificate.getIssuerDN().toString()
        cbpoiTrustedEntityInfo.getSerialNumber() == x509InstalledCertificate.getSerialNumber()
    }

    def "Given Entity profile name, when Trust CA Info is retrieved for CbpOi node, then information is correct"() {

        given: 'Entity profile name to retrieve trust info from PKI'
        final InputStream targetPkcs7Stream = new ByteArrayInputStream(certificateString.getBytes(StandardCharsets.UTF_8));
        final X509Certificate x509InstalledCertificate =
                (X509Certificate) (CertificateFactory.getInstance("X.509").generateCertificate(targetPkcs7Stream))

        def caName = (new X500Name(x509InstalledCertificate.getSubjectX500Principal().getName())).getCommonName()
        TrustedEntityInfo trustedEntityInfo = Mock()
        trustedEntityInfo.getEntityName() >> caName
        trustedEntityInfo.getCertificateSerialNumber() >> (x509InstalledCertificate.getSerialNumber().toString(16))
        trustedEntityInfo.getIssuerFullDN() >> x509InstalledCertificate.getIssuerDN().toString()
        def trustedEntityInfoSet  = [trustedEntityInfo]
        nscsPkiManager.getTrustedCAInfoByName(caName) >> trustedEntityInfo
        nscsPkiManager.getTrustedCAsInfoByEntityProfileName(defaultOamEntityProfileName) >> trustedEntityInfoSet
        nscsPkiManager.getCATrusts(caName) >> [x509InstalledCertificate]

        when: 'get Trust CA info for CbpOi node with given entity profile'
        def cbpoiTrustedEntityInfoSet = cppSecServBean.getCbpOiTrustedCAsInfoByEntityProfileName(defaultOamEntityProfileName)

        then: 'retrieved information is correct'
        1 == cbpoiTrustedEntityInfoSet.size()
        cbpoiTrustedEntityInfoSet.iterator().next().getName() == caName
        cbpoiTrustedEntityInfoSet.iterator().next().getIssuer() == x509InstalledCertificate.getIssuerDN().toString()
        cbpoiTrustedEntityInfoSet.iterator().next().getSerialNumber() == x509InstalledCertificate.getSerialNumber()
    }

    def "Given CA name, when certificate conversion throws exception, then CppSecurityServiceException is thrown"() {

        given: 'CA name from cli command'
        final InputStream targetPkcs7Stream = new ByteArrayInputStream(certificateString.getBytes(StandardCharsets.UTF_8))
        final X509Certificate x509InstalledCertificate =
                (X509Certificate) (CertificateFactory.getInstance("X.509").generateCertificate(targetPkcs7Stream))

        def caName = (new X500Name(x509InstalledCertificate.getSubjectX500Principal().getName())).getCommonName()
        TrustedEntityInfo trustedEntityInfo = Mock()
        nscsPkiManager.getTrustedCAInfoByName(caName) >> trustedEntityInfo
        nscsPkiManager.getCATrusts(caName) >> [x509InstalledCertificate]
        and: 'CA certificate conversion to Base64 throws exception'
        nscsCbpOiNodeUtility.convertToBase64String(x509InstalledCertificate) >> { throw new IOException("Conversion error")}

        when: 'get Trust CA info for CbpOi node'
        cppSecServBean.getCbpOiTrustedCAInfoByName(caName)

        then: 'CppSecurityServiceException is thrown'
        thrown(CppSecurityServiceException)
    }

    def "Given Entity profile name, when no Trust CA is retrieved from PKI for CbpOi node, then CppSecurityServiceException is thrown"() {

        given: 'trust info from PKI return empty set'
        nscsPkiManager.getTrustedCAsInfoByEntityProfileName(defaultOamEntityProfileName) >> []

        when: 'get Trust CA info for CbpOi node with given entity profile'
        cppSecServBean.getCbpOiTrustedCAsInfoByEntityProfileName(defaultOamEntityProfileName)

        then: 'CppSecurityServiceException is thrown'
        thrown(CppSecurityServiceException)
    }

    def "Given Entity profile name, when PKI throws exception, then CppSecurityServiceException is thrown"() {

        given: 'PKI throws exception in getting Trust info for Entity profile name'
        nscsPkiManager.getTrustedCAsInfoByEntityProfileName(defaultOamEntityProfileName) >> { throw new NscsPkiEntitiesManagerException("PKI error") }

        when: 'get Trust CA info for CbpOi node with given entity profile'
        cppSecServBean.getCbpOiTrustedCAsInfoByEntityProfileName(defaultOamEntityProfileName)

        then: 'CppSecurityServiceException is thrown'
        thrown(CppSecurityServiceException)
    }

    def "Given EnrollingInformation, when ipAddress value is null, then CppSecurityServiceException is thrown"(){
        given: 'EnrollingInformation as input '
        EnrollingInformation enrollInfo = new EnrollingInformation(nodeFdn, null, EnrollmentMode.CMPv2_VC, null, NodeEntityCategory.OAM, null);
        enrollInfo.setSubjectAltName(null);
        enrollInfo.setSubjectAltNameFormat(null);
        enrollInfo.setModelInfo(CPP_15B_MODEL_INFO);
        def node = new NodeRef(nodeFdn)
        def normRef = MockUtils.createNormalizableNodeRef(node.getName())
        readerService.getNormalizedNodeReference(_ as NodeReference) >> normRef
        def nscsModelInfo = Mock(NscsModelInfo)
        nscsModelServiceImpl.getLatestVersionOfNormalizedModel(_) >> nscsModelInfo
        readerService.getNormalizableNodeReference(_) >> normRef

        def cmResp = Mock(CmResponse)
        def cmObj = Mock(CmObject)
        Collection<CmObject> cmObjs = new ArrayList<>();
        Map<String, String> attributesEnrollment = new HashMap<>()
        attributesEnrollment.put("enrollmentMode", "CMPv2_VC")
        attributesEnrollment.put("ipAddress", "null")
        cmObj.getAttributes() >> attributesEnrollment
        cmObjs.add(cmObj)
        cmResp.getCmObjects() >> cmObjs
        readerService.getMOAttribute(_,_,_,_) >> cmResp
        readerService.exists(_) >> true

        def nodeEntity = createEntity(nodeFdn, NodeEntityCategory.OAM, null, null)
        def enrollmentInfo = Mock(EnrollmentInfo)
        EnrollmentPartialInfos epi = new EnrollmentPartialInfos(nodeEntity, enrollmentInfo, AlgorithmKeys.RSA_2048)
        nscsPkiManager.getEnrollmentEntityInfo(*_) >> epi
        nscsCapabilityModelService.getDefaultEntityProfile(CPP_15B_MODEL_INFO, NodeEntityCategory.OAM) >> defaultOamEntityProfileName
        modelServiceImpl.getConnectivityInfo(TargetTypeInformation.CATEGORY_NODE, CPP_15B_MODEL_INFO.getNodeType()) >> nscsModelInfo

        when: 'generate Enrollment information with enrolling information object'
        cppSecServBean.generateEnrollmentInfo(enrollInfo)

        then: 'CppSecurityServiceException is thrown'
        thrown(CppSecurityServiceException)
    }

    def "Given CA name, when PKI throws exception, then CppSecurityServiceException is thrown"() {

        given: 'PKI throws exception in getting Trust info for CA name'
        def caName = "ENM_OAM_CA"
        def issuerDN = "O=Ericsson, CN=ENM_Infrastructure_CA"
        def certificateSerialNumber = "1234"
        TrustedEntityInfo trustedEntityInfo = Mock()
        trustedEntityInfo.getEntityName() >> caName
        trustedEntityInfo.getCertificateSerialNumber() >> certificateSerialNumber
        trustedEntityInfo.getIssuerFullDN() >> issuerDN
        nscsPkiManager.getTrustedCAInfoByName(caName) >> trustedEntityInfo
        nscsPkiManager.getCATrusts(caName) >> { throw new NscsPkiEntitiesManagerException("PKI error") }

        when: 'get Trust CA info for CbpOi node with given entity profile'
        cppSecServBean.getCbpOiTrustedCAInfoByName(caName)

        then: 'CppSecurityServiceException is thrown'
        thrown(CppSecurityServiceException)
    }
}
