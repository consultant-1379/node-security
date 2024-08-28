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

package com.ericsson.nms.security.nscs.iscf

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.Principal
import java.security.SecureRandom
import java.security.Security
import java.util.Set

import javax.enterprise.context.spi.CreationalContext
import javax.enterprise.inject.spi.BeanManager
import javax.inject.Inject;
import javax.security.auth.x500.X500Principal

import org.bouncycastle.asn1.x509.BasicConstraints
import org.bouncycastle.asn1.x509.ExtendedKeyUsage
import org.bouncycastle.asn1.x509.GeneralName
import org.bouncycastle.asn1.x509.GeneralNames
import org.bouncycastle.asn1.x509.KeyPurposeId
import org.bouncycastle.asn1.x509.KeyUsage
import org.bouncycastle.asn1.x509.X509Extensions
import org.bouncycastle.x509.X509V3CertificateGenerator

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.enums.*
import com.ericsson.nms.security.nscs.api.exception.IscfServiceException
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameParam
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse.EnrollmentAuthorityData
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse.SecurityDataContainer
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse.TrustedCertificateData
import com.ericsson.nms.security.nscs.api.model.CertSpec
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation.ModelIdentifierType
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModel
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelMock
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfo
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException
import com.ericsson.nms.security.nscs.pki.NscsPkiUtils
import com.ericsson.nms.security.nscs.util.NscsTrustedEntityInfo
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility
import com.ericsson.oss.itpf.security.pki.common.model.EntityInfo
import com.ericsson.oss.itpf.security.pki.common.model.certificate.*
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity
import com.ericsson.oss.itpf.security.pki.manager.model.profiles.EntityProfile

import spock.lang.Shared
import spock.lang.Unroll
import spock.lang.Ignore

class SecurityDataCollectorFcTest extends CdiSpecification {

    @ObjectUnderTest
    SecurityDataCollector secDataCollector

    @MockedImplementation
    ScepEnrollmentInfo scepInfo

    @MockedImplementation
    CppSecurityService cppSecServ;

    @MockedImplementation
    BeanManager beanManager

    @MockedImplementation
    NscsNodeUtility nscsNodeUtility

    @Inject
    NscsCapabilityModelMock nscsCapabilityModelMock

    @Override
    def addAdditionalInjectionProperties(InjectionProperties injectionProperties) {
        injectionProperties.autoLocateFrom('com.ericsson.nms.security.nscs.capabilitymodel')
    }

    def router6kFdn = "Router6672_00001"
    def r6kModelIdentifier = "18A-R01AA"
    def radioNodeFdn = "Dusgen2_00001"
    def dg2ModelIdentifier = "17A-R01AA"
    def enrollmentCaCertificate = "Enrollment CA X509 certificate".getBytes()
    def pkiRootCaCertificate = "PKI Root CA X509 certificate".getBytes()
    def enrollCaFingerprint = NscsPkiUtils.generateMessageDigest(DigestAlgorithm.SHA1, enrollmentCaCertificate)
    def rootCaFingerprint = NscsPkiUtils.generateMessageDigest(DigestAlgorithm.SHA1, pkiRootCaCertificate)

    @Shared
    private SubjectAltNameParam subjectAltNameParam 

    def setup() {
        subjectAltNameParam = new SubjectAltNameParam(SubjectAltNameFormat.IPV4, 
                                       new SubjectAltNameStringType("11.22.33.44"))
        scepInfo.getServerCertFingerPrint() >> enrollCaFingerprint
        scepInfo.getPkiRootCertFingerPrint() >> rootCaFingerprint
        scepInfo.getFingerPrintAlgorithm() >> DigestAlgorithm.SHA1
        scepInfo.getServerURL() >> "http://192.168.0.155:8091/pkira-cmp"
        scepInfo.getKeySize() >> "2048"
        def creationalContext = Mock(CreationalContext)
        beanManager.createCreationalContext(_) >> (CreationalContext)creationalContext
        beanManager.getReference(_, NscsCapabilityModel.class, creationalContext) >> nscsCapabilityModelMock

    }

    def generateX509Certificate() {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        final X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();

        certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
        certGen.setIssuerDN(new X500Principal("CN=TestIssuerDN"));
        certGen.setNotBefore(new Date(System.currentTimeMillis() - 10000));
        certGen.setNotAfter(new Date(System.currentTimeMillis() + 10000));
        certGen.setSubjectDN(new X500Principal("CN=TestSubjectDN"));

        final KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");
        kpGen.initialize(1024, new SecureRandom());
        final KeyPair pair = kpGen.generateKeyPair();
        certGen.setPublicKey(pair.getPublic());
        certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");

        certGen.addExtension(X509Extensions.BasicConstraints, true, new BasicConstraints(false));
        certGen.addExtension(X509Extensions.KeyUsage, true, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));
        certGen.addExtension(X509Extensions.ExtendedKeyUsage, true, new ExtendedKeyUsage(KeyPurposeId.id_kp_serverAuth));
        certGen.addExtension(X509Extensions.SubjectAlternativeName, false,
                new GeneralNames(new GeneralName(GeneralName.rfc822Name, "test@test.test")));
        return certGen.generateX509Certificate(pair.getPrivate(), "BC");
    }

    @Unroll
    def 'When AP OAM/IPSec security data is generated for RadioNode, CA Certificate fingerprint is set for enrollment' () {

    given: 'RadioNode configured in ENM'
        def modelInfo = new NodeModelInformation(dg2ModelIdentifier, ModelIdentifierType.OSS_IDENTIFIER, 
                                                 "RadioNode");
        scepInfo.getCertificateAuthorityDn() >> "C=SE,OU=BUCI_DUAC_NAM,O=ERICSSON,CN=" + certificateAuthority
        scepInfo.getDistinguishedName() >> "CN=" + entityName + ", C=SE, O=ERICSSON, OU=BUCI DUAC NAM"
        scepInfo.getEnrollmentMode() >> enrollmentMode
        EntityProfile entityProfile = new EntityProfile();
        entityProfile.setName("DUSGen2OAM_CHAIN_EP");
        Entity entity = new Entity();
        entity.setEntityProfile(entityProfile);
        scepInfo.getEntity() >> entity
        nscsNodeUtility.hasNodeIPv6Address(_)>> false
        cppSecServ.generateOamEnrollmentInfo(radioNodeFdn, null, null, null, enrollmentMode, modelInfo) >> scepInfo
        cppSecServ.generateOamEnrollmentInfo(modelInfo,_) >> scepInfo
        cppSecServ.generateIpsecEnrollmentInfo(radioNodeFdn, null, _, _, enrollmentMode, null, modelInfo) >> scepInfo
        Set<NscsTrustedEntityInfo> trustedEntitiesInfo = new HashSet<>();
        BigInteger serialNumber = 112323556
        def x509CertRootCa = generateX509Certificate()
        NscsTrustedEntityInfo nscsTrustedEntityInfo = new NscsTrustedEntityInfo("ENM_PKI_Root_CA",serialNumber,"CN=ENM_PKI_Root_CA,C=SE,O=ERICSSON,OU=BUCI_DUAC_NAM","http://192.168.0.155:8093/pki-ra-tdps/ca_entity/ENM_PKI_Root_CA/628bc527604995e8/active/ENM_PKI_Root_CA",x509CertRootCa,CertificateStatus.ACTIVE)
        trustedEntitiesInfo.add(nscsTrustedEntityInfo)
        cppSecServ.getTrustedCAsInfoByEntityProfileName(_, _) >> trustedEntitiesInfo

    when: 'security data is generated for AP'
        def secDataResp = secDataCollector.getSecurityDataResponse(EnumSet.of(certType),radioNodeFdn, null, sanParam, enrollmentMode, modelInfo);

    then: 'response contains valid security data'
        secDataResp != null
        List<SecurityDataContainer> secDataCont = secDataResp.getSecurityDataContainers()
        List<TrustedCertificateData> trustCertdata = secDataResp.getTrustedCertificateData()
        trustCertdata != null
        trustCertdata.size() > 0
        secDataCont != null
        secDataCont.size() == 1
        SecurityDataContainer securityDataContainer = secDataCont.get(0);
        securityDataContainer.getNodeCredentials() != null
        EnrollmentAuthorityData enrollAuthority = securityDataContainer.getNodeCredentials().getEnrollmentAuthority()
        enrollAuthority != null
        
    and: 'enrollment CA fingerprint is set as fingerprint of Root CA'
        enrollAuthority.getEnrollmentCaFingerprint() != null
        enrollAuthority.getEnrollmentCaFingerprint().equals(CertSpec.bytesToHex(scepInfo.getPkiRootCertFingerPrint()))

    and: 'enrollment CA certificate FDN is not set'
        enrollAuthority.getEnrollmentCaCertificate() == null

    where:
            certType          |       entityName      |  certificateAuthority  |      enrollmentMode           |      sanParam
        CertificateType.OAM   | "Dusgen2_00001-oam"   |  "NE_OAM_CA"           |  EnrollmentMode.CMPv2_VC      |  null
        CertificateType.IPSEC | "Dusgen2_00001-ipsec" |  "NE_IPSec_CA"         |  EnrollmentMode.CMPv2_VC      |  subjectAltNameParam
        CertificateType.OAM   | "Dusgen2_00001-oam"   |  "NE_OAM_CA"           |  EnrollmentMode.CMPv2_INITIAL |  null
        CertificateType.IPSEC | "Dusgen2_00001-ipsec" |  "NE_IPSec_CA"         |  EnrollmentMode.CMPv2_INITIAL |  subjectAltNameParam
    }


    @Unroll
    def 'When OAM/IPSec AP security data is generated for Router6k node, CA Certificate DN is set for enrollment' () {

    given: 'Router6k node configured in ENM'
        def modelInfo = new NodeModelInformation(r6kModelIdentifier, ModelIdentifierType.OSS_IDENTIFIER, 
                                                 "Router6672");
        scepInfo.getCertificateAuthorityDn() >> "C=SE,OU=BUCI_DUAC_NAM,O=ERICSSON,CN=" + certificateAuthority
        scepInfo.getDistinguishedName() >> "CN=" + entityName + ", C=SE, O=ERICSSON, OU=BUCI DUAC NAM"
        scepInfo.getEnrollmentMode() >> enrollmentMode
        cppSecServ.generateOamEnrollmentInfo(router6kFdn, null, null, null, enrollmentMode, modelInfo) >> scepInfo
        cppSecServ.generateOamEnrollmentInfo(modelInfo, _) >> scepInfo
        cppSecServ.generateIpsecEnrollmentInfo(router6kFdn, null, _, _, enrollmentMode, null, modelInfo) >> scepInfo
        EntityProfile entityProfile = new EntityProfile();
        entityProfile.setName("DUSGen2OAM_CHAIN_EP");
        Entity entity = new Entity();
        entity.setEntityProfile(entityProfile);
        scepInfo.getEntity() >> entity
        nscsNodeUtility.hasNodeIPv6Address(_)>> false
        cppSecServ.generateOamEnrollmentInfo(radioNodeFdn, null, null, null, enrollmentMode, modelInfo) >> scepInfo
        cppSecServ.generateOamEnrollmentInfo(modelInfo,_) >> scepInfo
        cppSecServ.generateIpsecEnrollmentInfo(radioNodeFdn, null, _, _, enrollmentMode, null, modelInfo) >> scepInfo
        Set<NscsTrustedEntityInfo> trustedEntitiesInfo = new HashSet<>();
        BigInteger serialNumber = 112323556
        def x509CertRootCa = generateX509Certificate()
        NscsTrustedEntityInfo nscsTrustedEntityInfo = new NscsTrustedEntityInfo("ENM_PKI_Root_CA",serialNumber,"CN=ENM_PKI_Root_CA,C=SE,O=ERICSSON,OU=BUCI_DUAC_NAM","http://192.168.0.155:8093/pki-ra-tdps/ca_entity/ENM_PKI_Root_CA/628bc527604995e8/active/ENM_PKI_Root_CA",x509CertRootCa,CertificateStatus.ACTIVE)
        trustedEntitiesInfo.add(nscsTrustedEntityInfo)
        cppSecServ.getTrustedCAsInfoByEntityProfileName(_, _) >> trustedEntitiesInfo

    when: 'security data is generated for AP'
        def secDataResp = secDataCollector.getSecurityDataResponse(EnumSet.of(certType),
                          router6kFdn, null, sanParam, enrollmentMode, modelInfo);

    then: 'response contains valid security data'
        secDataResp != null
        List<SecurityDataContainer> secDataCont = secDataResp.getSecurityDataContainers()
        List<TrustedCertificateData> trustCertdata = secDataResp.getTrustedCertificateData()
        trustCertdata != null
        trustCertdata.size() > 0
        secDataCont != null
        secDataCont.size() == 1
        secDataCont.get(0).getNodeCredentials() != null
        EnrollmentAuthorityData enrollAuthority = secDataCont.get(0).getNodeCredentials().getEnrollmentAuthority()
        println(enrollAuthority)
        enrollAuthority != null

    and: 'enrollment CA certificate is set as trust FDN of PKI root CA'
        enrollAuthority.getEnrollmentCaCertificate() == null

    and: 'enrollment CA fingerprint is not set'
        enrollAuthority.getEnrollmentCaFingerprint() == null

    where:
            certType          |       entityName         |  certificateAuthority  |      enrollmentMode           |      sanParam
        CertificateType.OAM   | "Router6672_00001-oam"   |  "NE_OAM_CA"           |  EnrollmentMode.CMPv2_VC      |  null
        CertificateType.IPSEC | "Router6672_00001-ipsec" |  "NE_IPSec_CA"         |  EnrollmentMode.CMPv2_VC      |  subjectAltNameParam
        CertificateType.OAM   | "Router6672_00001-oam"   |  "NE_OAM_CA"           |  EnrollmentMode.CMPv2_INITIAL |  null
        CertificateType.IPSEC | "Router6672_00001-ipsec" |  "NE_IPSec_CA"         |  EnrollmentMode.CMPv2_INITIAL |  subjectAltNameParam
    }

    @Unroll
    def 'When OAM/IPSec AP security data is generated with enrollment error, then IscfServiceException is thrown' () {

    given: 'RadioNode configured in ENM'
        def modelInfo = new NodeModelInformation(dg2ModelIdentifier, ModelIdentifierType.OSS_IDENTIFIER, 
                                                 "RadioNode");
        scepInfo.getCertificateAuthorityDn() >> "C=SE,OU=BUCI_DUAC_NAM,O=ERICSSON,CN=" + certificateAuthority
        scepInfo.getDistinguishedName() >> "CN=" + entityName + ", C=SE, O=ERICSSON, OU=BUCI DUAC NAM"
    and:  'enrollment info generation throws exception'
        cppSecServ.generateOamEnrollmentInfo(radioNodeFdn, null, null, null, EnrollmentMode.CMPv2_VC, modelInfo) >> 
            { throw new CppSecurityServiceException("OamEnrollmentInfo failure") }
            cppSecServ.generateOamEnrollmentInfo(modelInfo, _) >> { throw new CppSecurityServiceException("OamEnrollmentInfo failure") }
        cppSecServ.generateIpsecEnrollmentInfo(radioNodeFdn, null, _, _, EnrollmentMode.CMPv2_VC, null, modelInfo) >>
            { throw new CppSecurityServiceException("IpsecEnrollmentInfo failure") }

    when: 'security data is generated for AP'
        def secDataResp = secDataCollector.getSecurityDataResponse(EnumSet.of(certType),
                          radioNodeFdn, null, sanParam, EnrollmentMode.CMPv2_VC, modelInfo);
    
    then: 'IscfServiceException is thrown'
        thrown(IscfServiceException)

    where:
            certType          |       entityName      |  certificateAuthority  |      sanParam
        CertificateType.OAM   | "Dusgen2_00001-oam"   |  "NE_OAM_CA"           |  null
        CertificateType.IPSEC | "Dusgen2_00001-ipsec" |  "NE_IPSec_CA"         |  subjectAltNameParam
   }
}