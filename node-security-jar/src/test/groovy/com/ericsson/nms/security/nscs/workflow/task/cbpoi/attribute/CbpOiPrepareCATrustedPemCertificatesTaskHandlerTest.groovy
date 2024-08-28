/*--------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *--------------------------------------------------------------------*/

package com.ericsson.nms.security.nscs.workflow.task.cbpoi.attribute

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.enums.DigestAlgorithm
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode
import com.ericsson.nms.security.nscs.api.enums.TrustCategoryType
import com.ericsson.nms.security.nscs.api.exception.NscsCapabilityModelException
import com.ericsson.nms.security.nscs.api.model.NodeReference
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerException
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerIF
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfo
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfoImpl
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.nms.security.nscs.logger.NscsLogger
import com.ericsson.oss.itpf.security.pki.manager.model.TrustedEntityInfo
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cbpoi.attribute.CbpOiPrepareCATrustedPemCertificatesTask
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys
import spock.lang.Shared
import spock.lang.Unroll

import java.nio.charset.StandardCharsets
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

class CbpOiPrepareCATrustedPemCertificatesTaskHandlerTest extends CdiSpecification {

    @ObjectUnderTest
    CbpOiPrepareCATrustedPemCertificatesTaskHandler taskHandler

    @MockedImplementation
    NscsCapabilityModelService nscsCapabilityModelService;

    @MockedImplementation
    NscsCMReaderService readerService;

    @MockedImplementation
    NscsLogger nscsLogger

    @MockedImplementation
    NscsPkiEntitiesManagerIF nscsPkiManager

    def addAdditionalInjectionProperties(InjectionProperties injectionProperties) {
        injectionProperties.autoLocateFrom('com.ericsson.nms.security.nscs.cpp.service')
    }


    Map<String, Serializable> outputParams = new HashMap<>()
    static def enrollmentCaName = "NE_OAM_CA"
    static def enmOamCaName = "ENM_OAM_CA"
    static def rootCaName = "ENM_Pki_Root_CA"
    static def infrastructureCaName = "ENM_Infrastructure_CA"
    static def nodeName = "cloud257-vdu"
    static def oamTrustCategory = "oamTrustCategory"
    static def oamCmpCaTrustCategory = "oamCmpCaTrustCategory"
    static def entityProfileName = "DUSGen2OAM_CHAIN_EP"
    ScepEnrollmentInfo enrollmentInfo = new ScepEnrollmentInfoImpl(null, "Enrollment URL", null, DigestAlgorithm.SHA1,
    0, "pass", "256", EnrollmentMode.CMPv2_VC, null, enrollmentCaName)

    static def certificateBegin = "-----BEGIN CERTIFICATE-----\n";
    static def certificateEnd = "\n-----END CERTIFICATE-----";
    static def certificatePemString = certificateBegin +
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

    @Shared
    X509Certificate x509InstalledCertificate

    @Shared
    def task = new CbpOiPrepareCATrustedPemCertificatesTask()

    def setup() {
        outputParams.clear()
        task.getParameters().clear()
        NodeReference nodeRef = Mock()
        NormalizableNodeReference normNode = Mock()
        enrollmentInfo.setPkiRootCertificateAuthorityDn("O=Ericsson,CN=" + rootCaName)
        def comEcimMoDefaultNames = [:]
        comEcimMoDefaultNames[TrustCategoryType.OAM.toString()] = oamTrustCategory
        def enrollCaMoDefaultNames = [:]
        enrollCaMoDefaultNames[TrustCategoryType.OAM.toString()] = oamCmpCaTrustCategory
        task.setNode(nodeRef)
        readerService.getNormalizableNodeReference(_) >> normNode
        normNode.getFdn() >> nodeName
        nscsCapabilityModelService.getDefaultEnrollmentCaTrustCategoryId(_) >> enrollCaMoDefaultNames
        nscsCapabilityModelService.getComEcimDefaultTrustCategoryIds(_) >> comEcimMoDefaultNames
        nscsCapabilityModelService.getDefaultEntityProfile(_, _) >> entityProfileName
        final InputStream targetPkcs7Stream = new ByteArrayInputStream(certificatePemString.getBytes(StandardCharsets.UTF_8))
        x509InstalledCertificate =
                (X509Certificate) (CertificateFactory.getInstance("X.509").generateCertificate(targetPkcs7Stream))
    }


    @Unroll
    def 'When handler receives Enrollment Info, then trust for enrollment CA is retrieved from PKI'() {
        given: 'task handler receives Enrollment Info in output parameters'
        String enrollmentInfoStr = NscsObjectSerializer.writeObject(enrollmentInfo)
        outputParams.put(WorkflowOutputParameterKeys.ENROLLMENT_INFO.toString(), enrollmentInfoStr)
        task.setValue(CbpOiPrepareCATrustedPemCertificatesTask.TRUSTED_CERTIFICATE_AUTHORITY_KEY, "")
        task.setOutputParams(outputParams)
        nscsCapabilityModelService.isEnrollmentRootCACertificateSupported(_, _) >> isRootCACertificateSupported
        nscsCapabilityModelService.isEnrollmentCACertificateSupported(_, _) >> isEnrollmentCACertificateSupported
        TrustedEntityInfo enrollmentTrustedEntityInfo = Mock()
        enrollmentTrustedEntityInfo.getEntityName() >>> [enrollmentCa, enmOamCaName]
        def trustedEntityInfoSet = [enrollmentTrustedEntityInfo]
        nscsPkiManager.getTrustedCAInfoByName(_) >> enrollmentTrustedEntityInfo
        nscsPkiManager.getTrustedCAsInfoByEntityProfileName(entityProfileName) >> trustedEntityInfoSet

        when: 'task handler is invoked'
        taskHandler.processTask(task)

        then: 'CA trust for current enrollment CA is retrieved from PKI'
        1 * nscsPkiManager.getCATrusts(enrollmentCa) >> [x509InstalledCertificate]

        and: 'CA trust for OAM is retrieved from PKI'
        1 * nscsPkiManager.getCATrusts(enmOamCaName) >> [x509InstalledCertificate]

        where:
        enrollmentCa     | isRootCACertificateSupported | isEnrollmentCACertificateSupported
        enrollmentCaName | false                        | true
        rootCaName       | true                         | false
    }

    def 'When handler receives Enrollment Info, and Capability Model throws exception, then WorkflowTaskException is thrown'() {
        given: 'task handler receives Enrollment Info in output param'
        String enrollmentInfoStr = NscsObjectSerializer.writeObject(enrollmentInfo)
        outputParams.put(WorkflowOutputParameterKeys.ENROLLMENT_INFO.toString(), enrollmentInfoStr)
        task.setValue(CbpOiPrepareCATrustedPemCertificatesTask.TRUSTED_CERTIFICATE_AUTHORITY_KEY, "")
        task.setOutputParams(outputParams)
        and: 'Capability Model throws exception'
        nscsCapabilityModelService.isEnrollmentRootCACertificateSupported(_, _) >>  {throw new NscsCapabilityModelException("")}
        when: 'task handler is invoked'
        taskHandler.processTask(task)
        then: 'WorkflowTaskException is thrown'
        thrown(WorkflowTaskException)
    }

    def 'When handler receives Enrollment Info, and PKI throws exception, then WorkflowTaskException is thrown'() {
        given: 'task handler receives Enrollment Info in output param'
        String enrollmentInfoStr = NscsObjectSerializer.writeObject(enrollmentInfo)
        outputParams.put(WorkflowOutputParameterKeys.ENROLLMENT_INFO.toString(), enrollmentInfoStr)
        task.setValue(CbpOiPrepareCATrustedPemCertificatesTask.TRUSTED_CERTIFICATE_AUTHORITY_KEY, "")
        task.setOutputParams(outputParams)
        nscsCapabilityModelService.isEnrollmentRootCACertificateSupported(_, _) >>  false
        nscsCapabilityModelService.isEnrollmentCACertificateSupported(_, _) >> true
        and: 'PKI throws exception'
        nscsPkiManager.getTrustedCAInfoByName(enrollmentCaName) >> {throw new NscsPkiEntitiesManagerException("")}
        when: 'task handler is invoked'
        taskHandler.processTask(task)
        then: 'WorkflowTaskException is thrown'
        thrown(WorkflowTaskException)
    }

    def 'When handler receives output params with empty Enrollment Info, then info is logged'() {
        given: 'task handler receives empty Enrollment Info in output param'
        outputParams.clear()
        outputParams.put(WorkflowOutputParameterKeys.ENROLLMENT_INFO.toString(), "")
        task.setOutputParams(outputParams)
        task.setTrustedCertificateAuthority("")
        TrustedEntityInfo enrollmentTrustedEntityInfo = Mock()
        enrollmentTrustedEntityInfo.getEntityName() >> enmOamCaName
        def trustedEntityInfoSet = [enrollmentTrustedEntityInfo]
        nscsPkiManager.getTrustedCAInfoByName(enmOamCaName) >> enrollmentTrustedEntityInfo
        nscsPkiManager.getTrustedCAsInfoByEntityProfileName(entityProfileName) >> trustedEntityInfoSet
        nscsPkiManager.getCATrusts(enmOamCaName) >> [x509InstalledCertificate]
        when: 'task handler is invoked'
        taskHandler.processTask(task)
        then: 'info is logged'
        1 * nscsLogger.info(task,"Enrollment info not set in output params")
        and: 'CA trust for enrollment CA is not retrieved'
        0 * nscsPkiManager.getTrustedCAInfoByName(enrollmentCaName)
    }

    def 'When handler receives trust CA name from command, then CA trust is retrieved'() {
        given: 'task handler receives CA name key'
        task.setTrustedCertificateAuthority(infrastructureCaName)
        TrustedEntityInfo enrollmentTrustedEntityInfo = Mock()
        enrollmentTrustedEntityInfo.getEntityName() >> infrastructureCaName
        nscsPkiManager.getCATrusts(infrastructureCaName) >> [x509InstalledCertificate]
        when: 'task handler is invoked'
        taskHandler.processTask(task)
        then: 'CA trust for cli command CA is retrieved'
        1 * nscsPkiManager.getTrustedCAInfoByName(infrastructureCaName) >> enrollmentTrustedEntityInfo
    }

    def 'When handler receives trust CA name from command, and PKI throws exception, then WorkflowTaskException is thrown'() {
        given: 'task handler receives CA name key without Enrollment Info'
        task.setValue(CbpOiPrepareCATrustedPemCertificatesTask.TRUSTED_CERTIFICATE_AUTHORITY_KEY, (Object)infrastructureCaName)
        and: 'PKI throws exception'
        nscsPkiManager.getTrustedCAInfoByName(infrastructureCaName) >> {throw new NscsPkiEntitiesManagerException("")}
        when: 'task handler is invoked'
        taskHandler.processTask(task)
        then: 'WorkflowTaskException is thrown'
        thrown(WorkflowTaskException)
    }

    def 'When handler receives reissue flag, then CA name from command is ignored'() {
    given: 'task handler receives reissue flag key and CA name'
        task.setValue(CbpOiPrepareCATrustedPemCertificatesTask.TRUSTED_CERTIFICATE_AUTHORITY_KEY, infrastructureCaName)
        task.setValue(CbpOiPrepareCATrustedPemCertificatesTask.ISREISSUE_KEY, "true")
        task.setValue(CbpOiPrepareCATrustedPemCertificatesTask.ENTITY_PROFILE_KEY, entityProfileName)
        nscsCapabilityModelService.isEnrollmentRootCACertificateSupported(_,_) >> false
        nscsCapabilityModelService.isEnrollmentCACertificateSupported(_,_) >> true
        TrustedEntityInfo enrollmentTrustedEntityInfo = Mock()
        enrollmentTrustedEntityInfo.getEntityName() >> enmOamCaName
        def trustedEntityInfoSet = [enrollmentTrustedEntityInfo]
        nscsPkiManager.getTrustedCAInfoByName(enmOamCaName) >> enrollmentTrustedEntityInfo
        nscsPkiManager.getTrustedCAsInfoByEntityProfileName(entityProfileName) >> trustedEntityInfoSet
        nscsPkiManager.getCATrusts(enmOamCaName) >> [x509InstalledCertificate]
    when: 'task handler is invoked'
        taskHandler.processTask(task)
    then: 'CA trust for cli command CA name is ignored'
        0 * nscsPkiManager.getTrustedCAInfoByName(infrastructureCaName)
    and: 'Entity Profile is not retrieved from PKI'
        0 * nscsCapabilityModelService.getDefaultEntityProfile(_, _)
    }

    def 'When handler cannot get any trusted entity, then workflow exception is thrown'() {
    given: 'task handler does not receive parameters'
        task.setValue(CbpOiPrepareCATrustedPemCertificatesTask.TRUSTED_CERTIFICATE_AUTHORITY_KEY, infrastructureCaName)
        TrustedEntityInfo enrollmentTrustedEntityInfo = Mock()
        enrollmentTrustedEntityInfo.getEntityName() >> infrastructureCaName
        nscsPkiManager.getTrustedCAInfoByName(infrastructureCaName) >> enrollmentTrustedEntityInfo
        nscsPkiManager.getCATrusts(infrastructureCaName) >> []
    when: 'task handler is invoked'
        taskHandler.processTask(task)
    then: 'exception is thrown'
        thrown(WorkflowTaskException)
    }

}

