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
package com.ericsson.nms.security.nscs.workflow.task.comecim.attribute

import java.io.ObjectInputStream
import java.io.Serializable
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateEncodingException

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys
import com.ericsson.nms.security.nscs.api.enums.DigestAlgorithm
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation.ModelIdentifierType
import com.ericsson.nms.security.nscs.api.model.NodeRef
import com.ericsson.nms.security.nscs.api.model.NodeReference
import com.ericsson.nms.security.nscs.api.model.service.NscsModelInfo
import com.ericsson.nms.security.nscs.api.model.service.NscsModelServiceException
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfo
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfoImpl
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException
import com.ericsson.nms.security.nscs.data.MoObject
import com.ericsson.nms.security.nscs.data.Model
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.nms.security.nscs.data.NscsCMWriterService
import com.ericsson.nms.security.nscs.data.ModelDefinition.CertificateContent;
import com.ericsson.nms.security.nscs.data.ModelDefinition.ComEcimManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.EnrollmentServer;
import com.ericsson.nms.security.nscs.data.ModelDefinition.IpSec;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.ModelDefinition.TrustCategory;
import com.ericsson.nms.security.nscs.data.ModelDefinition.TrustedCertificate;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.nms.security.nscs.logger.NscsLogger
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl
import com.ericsson.nms.security.nscs.util.NscsTrustedEntityInfo
import com.ericsson.nms.security.nscs.utilities.NSCSComEcimNodeUtility;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.nms.security.nscs.utilities.ComEcimMoNaming;
import com.ericsson.oss.itpf.security.pki.common.model.Algorithm
import com.ericsson.oss.itpf.security.pki.common.model.CertificateAuthority
import com.ericsson.oss.itpf.security.pki.common.model.EntityInfo
import com.ericsson.oss.itpf.security.pki.common.model.EntityStatus
import com.ericsson.oss.itpf.security.pki.common.model.Subject
import com.ericsson.oss.itpf.security.pki.common.model.SubjectField
import com.ericsson.oss.itpf.security.pki.common.model.SubjectFieldType
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltName
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameField
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameFieldType
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameString
import com.ericsson.oss.itpf.security.pki.manager.model.EntityType
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity
import com.ericsson.oss.itpf.security.pki.manager.model.profiles.EntityProfile
import com.ericsson.oss.services.cm.cmshared.dto.CmObject
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse
import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute.ComEcimCheckTrustedAlreadyInstalledTask
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys

class ComEcimCheckTrustedAlreadyInstalledTaskHandlerTest extends CdiSpecification{
    @ObjectUnderTest
    private ComEcimCheckTrustedAlreadyInstalledTaskHandler comEcimCheckTrustedAlreadyInstalledTaskHandler

    @MockedImplementation
    private NscsLogger nscslogger

    @MockedImplementation
    private NscsCMReaderService readerService

    @MockedImplementation
    private NormalizableNodeReference mockNormalizableNodeReference

    @MockedImplementation
    private NscsCMWriterService writerService

    @MockedImplementation
    private NscsCapabilityModelService capabilityService

    @MockedImplementation
    private NSCSComEcimNodeUtility comEcimNodeUtility

    @MockedImplementation
    private NscsNodeUtility nscsNodeUtility

    @MockedImplementation
    private ComEcimMoNaming comEcimMoNaming

    @MockedImplementation
    private MoObject mockMoObject

    @MockedImplementation
    private NscsModelServiceImpl nscsModelServiceImpl

    @MockedImplementation
    private NscsCMWriterService.WriterSpecificationBuilder trustedCertificateSpec

    @MockedImplementation
    NscsTrustedEntityInfo nscsTrustedEntityInfo

    private static final String NODE123 = "node123"
    private static final NodeReference NODE = new NodeRef(NODE123)
    private static final String PICO_NODE_NAME = "PICO-123"
    private static final String PICO_NODE_ROOT_FDN = String.format("MeContext=%s,ManagedElement=%s", PICO_NODE_NAME, PICO_NODE_NAME)
    private static final String PICO_NODE_CERTM = String.format("%s,SystemFunctions=1,SecM=1,CertM=1", PICO_NODE_ROOT_FDN)
    private static final String IPSEC_ENTITY_PROFILE_NAME = "MicroRBSIPSec_SAN_CHAIN_EP"
    private static final AlgorithmKeys ALGOKEYS = AlgorithmKeys.RSA_2048
    private static final String RADIO_NODE_ENROLLMENT_URI = "https://localhost:8443/app/resource"
    private static final String ACTIVATED = "ACTIVATED"
    private static final String ISCF_TEST_FINGERPRINT = "SHA-1 Fingerprint=SO:ME:FI:NG:ER:PR:IN:TT"
    private static final String NODE_TYPE = "ERBS"
    private static final String MODEL_IDENTIFIER = null
    private static final String SCHEMA = "schema"
    private static final String NAME_SPACE = "namespace"
    private static final String NAME = "name"
    private static final String VERSION = "version"
    private static final String ENROLLMENT_MODE = "CMPv2_INITIAL"
    private static final String ALGORITHM_KEYS = "RSA_4096"
    private static ComEcimCheckTrustedAlreadyInstalledTask comEcimCheckTrustedAlreadyInstalledTask = new ComEcimCheckTrustedAlreadyInstalledTask()
    private static Map<String, Serializable> outputParams = new HashMap<String, Serializable>()
    private static NscsModelInfo nscsModelInfo = new NscsModelInfo(SCHEMA, NAME_SPACE, NAME, VERSION)
    private static Map<String, NscsModelInfo> nscsModelInfos = new HashMap<String, NscsModelInfo>()
    private static NodeModelInformation NodeModelInformation = new NodeModelInformation(MODEL_IDENTIFIER, ModelIdentifierType.MIM_VERSION, NODE_TYPE)
    private static List<String> reservedByUser = new ArrayList<>()
    private static ScepEnrollmentInfo enrollmentInfo

 def setup(){

        outputParams.put(WorkflowOutputParameterKeys.ENROLLMENT_MODE.toString(), ENROLLMENT_MODE)
        outputParams.put(WorkflowOutputParameterKeys.ALGORITHM_KEYS.toString(), ALGORITHM_KEYS)
        comEcimCheckTrustedAlreadyInstalledTask.setNode(NODE)
        comEcimCheckTrustedAlreadyInstalledTask.setTrustCerts("CORBA_PEERS")
        comEcimCheckTrustedAlreadyInstalledTask.setOutputParams(outputParams)
        nscsModelInfos.put("NodeCredential", nscsModelInfo)
        nscsModelInfos.put("EnrollmentAuthority", nscsModelInfo)
        nscsModelInfos.put("EnrollmentServerGroup", nscsModelInfo)
        nscsModelInfos.put("EnrollmentServer", nscsModelInfo)
        final Entity entity = createEntity(NODE.getFdn())
        try {
            enrollmentInfo = new ScepEnrollmentInfoImpl(entity, RADIO_NODE_ENROLLMENT_URI, null, DigestAlgorithm.MD5, 10, "challengePWD", "2048",
                    EnrollmentMode.CMPv2_VC, null, null)
        } catch (final CertificateEncodingException | NoSuchAlgorithmException e) {
        }
        enrollmentInfo.setPkiRootCertFingerPrint(ISCF_TEST_FINGERPRINT.getBytes())
        reservedByUser.add(null)
        final CmResponse cmResponse = buildCmResponse(NODE123, "featureState", ACTIVATED)
        readerService.getMos(_, _, _, _) >> cmResponse
        readerService.getNormalizableNodeReference(_) >> mockNormalizableNodeReference
        mockNormalizableNodeReference.getNeType() >> "BSC"
        mockNormalizableNodeReference.getFdn() >> NODE.getFdn()
        capabilityService.getMirrorRootMo(_) >> Model.ME_CONTEXT.comManagedElement
        nscsNodeUtility.getSingleInstanceMoFdn(NODE.getFdn(), _) >> PICO_NODE_CERTM
        comEcimNodeUtility.getNodeCredentialFdn(_, _,_,_) >> NODE.getFdn()
        mockNormalizableNodeReference.getNeType() >> "HLR-FE"
        mockNormalizableNodeReference.getNeType() >> "vHLR-FE"
        mockNormalizableNodeReference.getNeType() >> "HLR-FE-BSP"
        mockNormalizableNodeReference.getNeType() >> "HLR-FE-IS"

    }


    def " test Process Task" () {
        given:

        when:
        def serializedTrustedEntitiesInfo = "rO0ABXNyABFqYXZhLnV0aWwuSGFzaFNldLpEhZWWuLc0AwAAeHB3DAAAABA/QAAAAAAAAXNyADljb20uZXJpY3Nzb24ubm1zLnNlY3VyaXR5Lm5zY3MudXRpbC5Oc2NzVHJ1c3RlZEVudGl0eUluZm8jP9tt0OPPBQIABEwABmlzc3VlcnQAEkxqYXZhL2xhbmcvU3RyaW5nO0wABG5hbWVxAH4AA0wADHNlcmlhbE51bWJlcnQAFkxqYXZhL21hdGgvQmlnSW50ZWdlcjtMAAd0ZHBzVXJscQB+AAN4cHQAIkNOPUVOTV9URVNUX0NBLERDPW14LERDPUFUVCxEQz1jb210ABJFTk1fVEVTVF9EQ19FTlRJVFlzcgAUamF2YS5tYXRoLkJpZ0ludGVnZXKM/J8fqTv7HQMABkkACGJpdENvdW50SQAJYml0TGVuZ3RoSQATZmlyc3ROb256ZXJvQnl0ZU51bUkADGxvd2VzdFNldEJpdEkABnNpZ251bVsACW1hZ25pdHVkZXQAAltCeHIAEGphdmEubGFuZy5OdW1iZXKGrJUdC5TgiwIAAHhw///////////////+/////gAAAAF1cgACW0Ks8xf4BghU4AIAAHhwAAAACFw61K3Ov1RSeHQAbmh0dHA6Ly8xMzEuMTYwLjE0Ni4zNjo4MDkzL3BraS1yYS10ZHBzL2NhX2VudGl0eS9FTk1fVEVTVF9EQ19FTlRJVFkvNWMzYWQ0YWRjZWJmNTQ1Mi9hY3RpdmUvRU5NX1RFU1RfRENfRU5USVRZeA=="
        outputParams.put(WorkflowOutputParameterKeys.TRUSTED_ENTITY_INFO.toString(), serializedTrustedEntitiesInfo);
        def serializedEnrollmentCaEntityInfo = "rO0ABXNyADljb20uZXJpY3Nzb24ubm1zLnNlY3VyaXR5Lm5zY3MudXRpbC5Oc2NzVHJ1c3RlZEVudGl0eUluZm8jP9tt0OPPBQIABEwABmlzc3VlcnQAEkxqYXZhL2xhbmcvU3RyaW5nO0wABG5hbWVxAH4AAUwADHNlcmlhbE51bWJlcnQAFkxqYXZhL21hdGgvQmlnSW50ZWdlcjtMAAd0ZHBzVXJscQB+AAF4cHQAIkNOPUVOTV9URVNUX0NBLERDPW14LERDPUFUVCxEQz1jb210ABJFTk1fVEVTVF9EQ19FTlRJVFlzcgAUamF2YS5tYXRoLkJpZ0ludGVnZXKM/J8fqTv7HQMABkkACGJpdENvdW50SQAJYml0TGVuZ3RoSQATZmlyc3ROb256ZXJvQnl0ZU51bUkADGxvd2VzdFNldEJpdEkABnNpZ251bVsACW1hZ25pdHVkZXQAAltCeHIAEGphdmEubGFuZy5OdW1iZXKGrJUdC5TgiwIAAHhw///////////////+/////gAAAAF1cgACW0Ks8xf4BghU4AIAAHhwAAAACFw61K3Ov1RSeHQAbmh0dHA6Ly8xMzEuMTYwLjE0Ni4zNjo4MDkzL3BraS1yYS10ZHBzL2NhX2VudGl0eS9FTk1fVEVTVF9EQ19FTlRJVFkvNWMzYWQ0YWRjZWJmNTQ1Mi9hY3RpdmUvRU5NX1RFU1RfRENfRU5USVRZ"
        outputParams.put(WorkflowOutputParameterKeys.ENROLLMENT_CA_ENTITY.toString(), serializedEnrollmentCaEntityInfo);


        readerService.getMoObjectByFdn(NODE.getFdn()) >> mockMoObject

        List<String> trustCategoryTrustedCertificates = new ArrayList()
        trustCategoryTrustedCertificates.add("MeContext=node123")
        mockMoObject.getAttribute("trustedCertificates") >> trustCategoryTrustedCertificates
        readerService.getNodeModelInformation(NODE.getFdn())>> NodeModelInformation
        comEcimNodeUtility.getTrustCategoryFdn(_, _, _, _) >> "CN=ENM_TEST_CA,DC=mx,DC=ATT,DC=com"
        readerService.getMoObjectByFdn(_) >> mockMoObject

        nscsNodeUtility.getAlgorithmKeys(_, _) >> AlgorithmKeys.ECDSA_256
        nscsNodeUtility.getCertificateTypeFromTrustedCertCategory("CORBA_PEERS") >> "OAM"

            def response = comEcimCheckTrustedAlreadyInstalledTaskHandler.processTask(comEcimCheckTrustedAlreadyInstalledTask)
      then:
            Assert.assertNotNull(response)


    }

    def " test Process Task_without_TrustedEntitiesInfo" () {
        given:


        when:
        def serializedTrustedEntitiesInfo = "rO0ABXNyABFqYXZhLnV0aWwuSGFzaFNldLpEhZWWuLc0AwAAeHB3DAAAABA/QAAAAAAAAHg="
        outputParams.put(WorkflowOutputParameterKeys.TRUSTED_ENTITY_INFO.toString(), serializedTrustedEntitiesInfo);
        def serializedEnrollmentCaEntityInfo = "rO0ABXNyADljb20uZXJpY3Nzb24ubm1zLnNlY3VyaXR5Lm5zY3MudXRpbC5Oc2NzVHJ1c3RlZEVudGl0eUluZm8jP9tt0OPPBQIABEwABmlzc3VlcnQAEkxqYXZhL2xhbmcvU3RyaW5nO0wABG5hbWVxAH4AAUwADHNlcmlhbE51bWJlcnQAFkxqYXZhL21hdGgvQmlnSW50ZWdlcjtMAAd0ZHBzVXJscQB+AAF4cHQAIkNOPUVOTV9URVNUX0NBLERDPW14LERDPUFUVCxEQz1jb210ABJFTk1fVEVTVF9EQ19FTlRJVFlzcgAUamF2YS5tYXRoLkJpZ0ludGVnZXKM/J8fqTv7HQMABkkACGJpdENvdW50SQAJYml0TGVuZ3RoSQATZmlyc3ROb256ZXJvQnl0ZU51bUkADGxvd2VzdFNldEJpdEkABnNpZ251bVsACW1hZ25pdHVkZXQAAltCeHIAEGphdmEubGFuZy5OdW1iZXKGrJUdC5TgiwIAAHhw///////////////+/////gAAAAF1cgACW0Ks8xf4BghU4AIAAHhwAAAACFw61K3Ov1RSeHQAbmh0dHA6Ly8xMzEuMTYwLjE0Ni4zNjo4MDkzL3BraS1yYS10ZHBzL2NhX2VudGl0eS9FTk1fVEVTVF9EQ19FTlRJVFkvNWMzYWQ0YWRjZWJmNTQ1Mi9hY3RpdmUvRU5NX1RFU1RfRENfRU5USVRZ"
        outputParams.put(WorkflowOutputParameterKeys.ENROLLMENT_CA_ENTITY.toString(), serializedEnrollmentCaEntityInfo);

        readerService.getMoObjectByFdn(NODE.getFdn()) >> mockMoObject

        List<String> trustCategoryTrustedCertificates = new ArrayList()
        trustCategoryTrustedCertificates.add("MeContext=node123")
        mockMoObject.getAttribute("trustedCertificates") >> trustCategoryTrustedCertificates
        readerService.getNodeModelInformation(NODE.getFdn())>> NodeModelInformation
        comEcimNodeUtility.getTrustCategoryFdn(_, _, _, _) >> "CN=ENM_TEST_CA,DC=mx,DC=ATT,DC=com"
        readerService.getMoObjectByFdn(_) >> mockMoObject

        nscsNodeUtility.getAlgorithmKeys(_, _) >> AlgorithmKeys.ECDSA_256
        nscsNodeUtility.getCertificateTypeFromTrustedCertCategory("CORBA_PEERS") >> "OAM"

            def response = comEcimCheckTrustedAlreadyInstalledTaskHandler.processTask(comEcimCheckTrustedAlreadyInstalledTask)
      then:
            Assert.assertNotNull(response)


    }

    private Entity createEntity(final String fdn) {
        final EntityInfo entityInfo = new EntityInfo()
        final CertificateAuthority certificateAuthority = new CertificateAuthority()
        certificateAuthority.setName("NE_OAM_CA")
        certificateAuthority.setIssuer(certificateAuthority)
        entityInfo.setName(fdn)
        entityInfo.setId(1)

        final SubjectAltNameField subjectAltNameField = new SubjectAltNameField()
        subjectAltNameField.setType(SubjectAltNameFieldType.IP_ADDRESS)

        final SubjectAltNameString subjectAltNameValueString = new SubjectAltNameString()
        subjectAltNameValueString.setValue("12.13.14.15")
        subjectAltNameField.setValue(subjectAltNameValueString)
        final List<SubjectAltNameField> subjectAltNameValueList = new ArrayList<>()
        subjectAltNameValueList.add(subjectAltNameField)

        final SubjectAltName subjectAltNameValues = new SubjectAltName()
        subjectAltNameValues.setSubjectAltNameFields(subjectAltNameValueList)

        final Subject subject = new Subject()

        final SubjectField subjectFieldCN = new SubjectField()
        subjectFieldCN.setType(SubjectFieldType.COMMON_NAME)
        subjectFieldCN.setValue(fdn)

        final List<SubjectField> entSubjectFieldList = new ArrayList<>()
        entSubjectFieldList.add(subjectFieldCN)
        subject.setSubjectFields(entSubjectFieldList)

        entityInfo.setSubject(subject)
        entityInfo.setSubjectAltName(subjectAltNameValues)
        entityInfo.setOTP("OTP")

        final Entity ee = new Entity()
        ee.setType(EntityType.ENTITY)
        final EntityProfile ep = new EntityProfile()
        ep.setActive(true)
        ep.setName(IPSEC_ENTITY_PROFILE_NAME)
        ee.setEntityProfile(ep)
        entityInfo.setStatus(EntityStatus.NEW)
        ee.setEntityInfo(entityInfo)
        entityInfo.setIssuer(certificateAuthority.getIssuer())

        final Algorithm keyGenerationAlgorithm = new Algorithm()
        keyGenerationAlgorithm.setKeySize((ALGOKEYS.getKeySize()))
        ee.setKeyGenerationAlgorithm(keyGenerationAlgorithm)

        return ee

    }

    private CmResponse buildCmResponse(final String nodeName, final String attribute, final Object expectedValue) {
        final CmResponse cmResponse = new CmResponse()
        final Map<String, Object> attributesMap = new HashMap<>()

        attributesMap.put(attribute, expectedValue)
        attributesMap.put("protocol", "CMP")
        attributesMap.put("uri", "uri")

        Map<String, Object> certificateContent = new HashMap<>();
        certificateContent.put("issuer", "CN=ENM_TEST_CA,DC=mx,DC=ATT,DC=com")
        certificateContent.put("serialNumber", "6645858043101664338")
        attributesMap.put("certificateContent", certificateContent)

        List<String> reservedByCategory = new ArrayList<>();
        reservedByCategory.add("CN=ENM_TEST_CA,DC=mx,DC=ATT,DC=com")
        attributesMap.put("reservedByCategory", reservedByCategory)

        final Collection<CmObject> cmObjects = new ArrayList<>(1)
        final CmObject cmObject = new CmObject()
        cmObject.setAttributes(attributesMap)
        cmObject.setFdn("MeContext=" + nodeName)
        cmObjects.add(cmObject)

        cmResponse.setTargetedCmObjects(cmObjects)
        cmResponse.setStatusCode(0)
        cmResponse.setTargetedCmObjects(cmObjects)
        return cmResponse

    }
}
