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
package com.ericsson.nms.security.nscs.workflow.task.comecim.attribute;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys;
import com.ericsson.nms.security.nscs.api.enums.DigestAlgorithm;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation.ModelIdentifierType;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.model.service.NscsModelInfo;
import com.ericsson.nms.security.nscs.api.model.service.NscsModelServiceException;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfo;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfoImpl;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException;
import com.ericsson.nms.security.nscs.data.MoObject;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition.ComEcimManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.EnrollmentServer;
import com.ericsson.nms.security.nscs.data.ModelDefinition.IpSec;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl;
import com.ericsson.nms.security.nscs.util.CertDetails;
import com.ericsson.nms.security.nscs.util.EnrollingInformation;
import com.ericsson.nms.security.nscs.util.NscsTrustedEntityInfo;
import com.ericsson.nms.security.nscs.utilities.ComEcimMoNaming;
import com.ericsson.nms.security.nscs.utilities.NSCSComEcimNodeUtility;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.oss.itpf.security.pki.common.model.Algorithm;
import com.ericsson.oss.itpf.security.pki.common.model.CertificateAuthority;
import com.ericsson.oss.itpf.security.pki.common.model.EntityInfo;
import com.ericsson.oss.itpf.security.pki.common.model.EntityStatus;
import com.ericsson.oss.itpf.security.pki.common.model.Subject;
import com.ericsson.oss.itpf.security.pki.common.model.SubjectField;
import com.ericsson.oss.itpf.security.pki.common.model.SubjectFieldType;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltName;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameField;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameFieldType;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameString;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityType;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.itpf.security.pki.manager.model.profiles.EntityProfile;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute.ComEcimCheckTrustedCategoryTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(MockitoJUnitRunner.class)
public class ComEcimCheckTrustedCategoryTaskHandlerTest {
    private static final Logger logger = LoggerFactory.getLogger(ComEcimCheckTrustedCategoryTaskHandlerTest.class);
    @InjectMocks
    private ComEcimCheckTrustedCategoryTaskHandler comEcimCheckTrustedCategoryTaskHandler;

    @Mock
    private NscsLogger nscslogger;

    @Mock
    private NscsCMReaderService readerService;

    @Mock
    private NormalizableNodeReference mockNormalizableNodeReference;

    @Mock
    private NscsCMWriterService writerService;

    @Mock
    private NscsCapabilityModelService capabilityService;

    @Mock
    private NSCSComEcimNodeUtility comEcimNodeUtility;

    @Mock
    private NscsNodeUtility nscsNodeUtility;

    @Mock
    private ComEcimMoNaming comEcimMoNaming;

    @Mock
    private MoObject mockMoObject;

    @Mock
    private NscsModelServiceImpl nscsModelServiceImpl;

    @Mock
    private NscsCMWriterService.WriterSpecificationBuilder trustedCertificateSpec;

    private static final ComEcimManagedElement MO = Model.ME_CONTEXT.comManagedElement;
    private static final Mo certMMo = MO.systemFunctions.secM.certM;
    private static final String NODE123 = "Lienb4929";
    private static final NodeReference NODE = new NodeRef(NODE123);
    private static final String PICO_NODE_NAME = "PICO-123";
    private static final String PICO_NODE_ROOT_FDN = String.format("MeContext=%s,ManagedElement=%s", PICO_NODE_NAME, PICO_NODE_NAME);
    private static final String PICO_NODE_CERTM = String.format("%s,SystemFunctions=1,SecM=1,CertM=1", PICO_NODE_ROOT_FDN);
    private static final String IPSEC_ENTITY_PROFILE_NAME = "MicroRBSIPSec_SAN_CHAIN_EP";
    private static final AlgorithmKeys ALGOKEYS = AlgorithmKeys.RSA_2048;
    private static final String RADIO_NODE_ENROLLMENT_URI = "https://localhost:8443/app/resource";
    private static final String ACTIVATED = "ACTIVATED";
    private static final String ISCF_TEST_FINGERPRINT = "SHA-1 Fingerprint=SO:ME:FI:NG:ER:PR:IN:TT";
    private static final String SCHEMA = "schema";
    private static final String NAME_SPACE = "namespace";
    private static final String NAME = "name";
    private static final String VERSION = "version";
    private static ComEcimCheckTrustedCategoryTask comEcimCheckTrustedCategoryTask = new ComEcimCheckTrustedCategoryTask();
    private static NscsModelInfo nscsModelInfo = new NscsModelInfo(SCHEMA, NAME_SPACE, NAME, VERSION);
    private static Map<String, NscsModelInfo> nscsModelInfos = new HashMap<String, NscsModelInfo>();
    private static List<String> reservedByUser = new ArrayList<>();
    private static ScepEnrollmentInfo enrollmentInfo;

    @Before
    public void setUp() {

        comEcimCheckTrustedCategoryTask.setNode(NODE);
        comEcimCheckTrustedCategoryTask.setTrustCerts("CORBA_PEERS");
        comEcimCheckTrustedCategoryTask.setInterfaceFdn(NODE123);
        nscsModelInfos.put("NodeCredential", nscsModelInfo);
        nscsModelInfos.put("EnrollmentAuthority", nscsModelInfo);
        nscsModelInfos.put("EnrollmentServerGroup", nscsModelInfo);
        nscsModelInfos.put("EnrollmentServer", nscsModelInfo);
        final Entity entity = new ComEcimSetupDataUtil().createEntity(NODE.getFdn());
        try {
            enrollmentInfo = new ScepEnrollmentInfoImpl(entity, RADIO_NODE_ENROLLMENT_URI, null, DigestAlgorithm.MD5, 10, "challengePWD", "2048",
                    EnrollmentMode.CMPv2_VC, null, null);
        } catch (final CertificateEncodingException | NoSuchAlgorithmException e) {
        }
        enrollmentInfo.setPkiRootCertFingerPrint(ISCF_TEST_FINGERPRINT.getBytes());
        reservedByUser.add(null);
        final CmResponse cmResponse = new ComEcimSetupDataUtil().buildCmResponse(NODE123, IpSec.FEATURE_STATE, ACTIVATED, false);
        Mockito.when(readerService.getNormalizableNodeReference(Mockito.any(NodeReference.class))).thenReturn(mockNormalizableNodeReference);
        Mockito.when(
                readerService.getMos(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String[].class)))
                .thenReturn(cmResponse);
        Mockito.when(readerService.getMos(Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyVararg())).thenReturn(cmResponse);
        Mockito.when(mockNormalizableNodeReference.getNeType()).thenReturn("RadioNode");
        Mockito.when(mockNormalizableNodeReference.getFdn()).thenReturn(NODE.getFdn());
        Mockito.when(mockNormalizableNodeReference.getOssModelIdentity()).thenReturn("20.Q4-R19A30");
        Mockito.when(capabilityService.getMirrorRootMo(Mockito.any(NormalizableNodeReference.class))).thenReturn(MO);
        Mockito.when(nscsNodeUtility.getSingleInstanceMoFdn(NODE.getFdn(), certMMo)).thenReturn(PICO_NODE_CERTM);
        Mockito.when(comEcimNodeUtility.getNodeCredentialFdn(Mockito.anyString(), Mockito.any(Mo.class), Mockito.anyString(),
                Mockito.eq(mockNormalizableNodeReference))).thenReturn(NODE.getFdn());
    }

    @Test
    public void testProcessTask() {
        Map<String, Serializable> outputParams = new HashMap<String, Serializable>();
        outputParams.put(WorkflowOutputParameterKeys.ENROLLMENT_CA_ENTITY.toString(), "");
        outputParams.put(WorkflowOutputParameterKeys.TRUSTED_CERTIFICATE_FDN_LIST.toString(), "");

        String serializedTrustedEntitiesInfo = "rO0ABXNyABNqYXZhLnV0aWwuQXJyYXlMaXN0eIHSHZnHYZ0DAAFJAARzaXpleHAAAAABdwQAAAABc3IAOWNvbS5lcmljc3Nvbi5ubXMuc2VjdXJpdHkubnNjcy51dGlsLk5zY3NUcnVzdGVkRW50aXR5SW5mbyM/223Q488FAgAETAAGaXNzdWVydAASTGphdmEvbGFuZy9TdHJpbmc7TAAEbmFtZXEAfgADTAAMc2VyaWFsTnVtYmVydAAWTGphdmEvbWF0aC9CaWdJbnRlZ2VyO0wAB3RkcHNVcmxxAH4AA3hwdAAiQ049RU5NX1RFU1RfQ0EsREM9bXgsREM9QVRULERDPWNvbXQAEkVOTV9URVNUX0RDX0VOVElUWXNyABRqYXZhLm1hdGguQmlnSW50ZWdlcoz8nx+pO/sdAwAGSQAIYml0Q291bnRJAAliaXRMZW5ndGhJABNmaXJzdE5vbnplcm9CeXRlTnVtSQAMbG93ZXN0U2V0Qml0SQAGc2lnbnVtWwAJbWFnbml0dWRldAACW0J4cgAQamF2YS5sYW5nLk51bWJlcoaslR0LlOCLAgAAeHD///////////////7////+AAAAAXVyAAJbQqzzF/gGCFTgAgAAeHAAAAAIXDrUrc6/VFJ4dABuaHR0cDovLzEzMS4xNjAuMTQ2LjM2OjgwOTMvcGtpLXJhLXRkcHMvY2FfZW50aXR5L0VOTV9URVNUX0RDX0VOVElUWS81YzNhZDRhZGNlYmY1NDUyL2FjdGl2ZS9FTk1fVEVTVF9EQ19FTlRJVFl4";
        outputParams.put(WorkflowOutputParameterKeys.TRUSTED_CA_ENTITY_LIST.toString(), serializedTrustedEntitiesInfo);
        String serializedEnrollmentCaEntityInfo = "rO0ABXNyADljb20uZXJpY3Nzb24ubm1zLnNlY3VyaXR5Lm5zY3MudXRpbC5Oc2NzVHJ1c3RlZEVudGl0eUluZm8jP9tt0OPPBQIABEwABmlzc3VlcnQAEkxqYXZhL2xhbmcvU3RyaW5nO0wABG5hbWVxAH4AAUwADHNlcmlhbE51bWJlcnQAFkxqYXZhL21hdGgvQmlnSW50ZWdlcjtMAAd0ZHBzVXJscQB+AAF4cHQAIkNOPUVOTV9URVNUX0NBLERDPW14LERDPUFUVCxEQz1jb210ABJFTk1fVEVTVF9EQ19FTlRJVFlzcgAUamF2YS5tYXRoLkJpZ0ludGVnZXKM/J8fqTv7HQMABkkACGJpdENvdW50SQAJYml0TGVuZ3RoSQATZmlyc3ROb256ZXJvQnl0ZU51bUkADGxvd2VzdFNldEJpdEkABnNpZ251bVsACW1hZ25pdHVkZXQAAltCeHIAEGphdmEubGFuZy5OdW1iZXKGrJUdC5TgiwIAAHhw///////////////+/////gAAAAF1cgACW0Ks8xf4BghU4AIAAHhwAAAACFw61K3Ov1RSeHQAbmh0dHA6Ly8xMzEuMTYwLjE0Ni4zNjo4MDkzL3BraS1yYS10ZHBzL2NhX2VudGl0eS9FTk1fVEVTVF9EQ19FTlRJVFkvNWMzYWQ0YWRjZWJmNTQ1Mi9hY3RpdmUvRU5NX1RFU1RfRENfRU5USVRZ";
        outputParams.put(WorkflowOutputParameterKeys.ENROLLMENT_CA_ENTITY.toString(), serializedEnrollmentCaEntityInfo);
        comEcimCheckTrustedCategoryTask.setOutputParams(outputParams);

        try {
            comEcimCheckTrustedCategoryTaskHandler.processTask(comEcimCheckTrustedCategoryTask);
            final String response = comEcimCheckTrustedCategoryTaskHandler.processTask(comEcimCheckTrustedCategoryTask);
            Assert.assertNotNull(response);
        } catch (final Exception e) {
        }

    }

}
