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
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfo;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfoImpl;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException;
import com.ericsson.nms.security.nscs.data.MoObject;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition.CertificateContent;
import com.ericsson.nms.security.nscs.data.ModelDefinition.ComEcimManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.IpSec;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.ModelDefinition.TrustCategory;
import com.ericsson.nms.security.nscs.data.ModelDefinition.TrustedCertificate;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl;
import com.ericsson.nms.security.nscs.utilities.ComEcimMoNaming;
import com.ericsson.nms.security.nscs.utilities.NSCSComEcimNodeUtility;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute.ComEcimCheckRemoveTrustTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;

@RunWith(MockitoJUnitRunner.class)
public class ComEcimCheckRemoveTrustTaskHandlerTest {
    private static final Logger logger = LoggerFactory.getLogger(ComEcimCheckRemoveTrustTaskHandlerTest.class);
    @InjectMocks
    private ComEcimCheckRemoveTrustTaskHandler comEcimCheckRemoveTrustTaskHandler;

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

    @Mock
    private ComEcimSetupDataUtil dataUtil;

    private static final Mo MO = Model.ME_CONTEXT.comManagedElement;
    private static final Mo CERT_MO = ((ComEcimManagedElement) MO).systemFunctions.secM.certM;
    private static final String NODE123 = "node123";
    private static final NodeReference NODE = new NodeRef(NODE123);
    private static final String PICO_NODE_NAME = "PICO-123";
    private static final String PICO_NODE_ROOT_FDN = String.format("MeContext=%s,ManagedElement=%s", PICO_NODE_NAME, PICO_NODE_NAME);
    private static final String PICO_NODE_CERTM = String.format("%s,SystemFunctions=1,SecM=1,CertM=1", PICO_NODE_ROOT_FDN);
    private static final String RADIO_NODE_ENROLLMENT_URI = "https://localhost:8443/app/resource";
    private static final String ACTIVATED = "ACTIVATED";
    private static final String ISCF_TEST_FINGERPRINT = "SHA-1 Fingerprint=SO:ME:FI:NG:ER:PR:IN:TT";
    private static final String NODE_TYPE = "ERBS";
    private static final String MODEL_IDENTIFIER = null;
    private static final String SCHEMA = "schema";
    private static final String NAME_SPACE = "namespace";
    private static final String NAME = "name";
    private static final String VERSION = "version";
    private static final String ENROLLMENT_MODE = "CMPv2_INITIAL";
    private static final String ALGORITHM_KEYS = "RSA_4096";
    private static ComEcimCheckRemoveTrustTask comEcimCheckRemoveTrustTask = new ComEcimCheckRemoveTrustTask();
    private static Map<String, Serializable> outputParams = new HashMap<String, Serializable>();
    private static NscsModelInfo nscsModelInfo = new NscsModelInfo(SCHEMA, NAME_SPACE, NAME, VERSION);
    private static Map<String, NscsModelInfo> nscsModelInfos = new HashMap<String, NscsModelInfo>();
    private static NodeModelInformation NodeModelInformation = new NodeModelInformation(MODEL_IDENTIFIER, ModelIdentifierType.MIM_VERSION, NODE_TYPE);
    private static List<String> reservedByUser = new ArrayList<>();
    private static ScepEnrollmentInfo enrollmentInfo;

    @Before
    public void setUp() {

        outputParams.put(WorkflowOutputParameterKeys.ENROLLMENT_MODE.toString(), ENROLLMENT_MODE);
        outputParams.put(WorkflowOutputParameterKeys.ALGORITHM_KEYS.toString(), ALGORITHM_KEYS);
        comEcimCheckRemoveTrustTask.setNode(NODE);
        comEcimCheckRemoveTrustTask.setCertCategory(TrustedCertCategory.CORBA_PEERS.toString());
        comEcimCheckRemoveTrustTask.setCertificateSN("5170857900660139862");
        comEcimCheckRemoveTrustTask.setIssuer("DC=com, DC=ATT, DC=mx, CN=ENM_TEST_CA");
        comEcimCheckRemoveTrustTask.setOutputParams(outputParams);
        nscsModelInfos.put("NodeCredential", nscsModelInfo);
        nscsModelInfos.put("EnrollmentAuthority", nscsModelInfo);
        nscsModelInfos.put("EnrollmentServerGroup", nscsModelInfo);
        nscsModelInfos.put("EnrollmentServer", nscsModelInfo);
        final Entity entity = dataUtil.createEntity(NODE.getFdn());
        try {
            enrollmentInfo = new ScepEnrollmentInfoImpl(entity, RADIO_NODE_ENROLLMENT_URI, null, DigestAlgorithm.MD5, 10, "challengePWD", "2048",
                    EnrollmentMode.CMPv2_VC, null, null);
        } catch (final CertificateEncodingException | NoSuchAlgorithmException e) {
        }
        enrollmentInfo.setPkiRootCertFingerPrint(ISCF_TEST_FINGERPRINT.getBytes());
        reservedByUser.add(null);
        final CmResponse cmResponse = dataUtil.buildCmResponse(NODE123, IpSec.FEATURE_STATE, ACTIVATED);
        Mockito.when(readerService.getNormalizableNodeReference(Mockito.any(NodeReference.class))).thenReturn(mockNormalizableNodeReference);
        Mockito.when(mockNormalizableNodeReference.getNeType()).thenReturn("BSC");
        Mockito.when(mockNormalizableNodeReference.getFdn()).thenReturn(NODE.getFdn());
        Mockito.when(capabilityService.getMirrorRootMo(Mockito.any(NormalizableNodeReference.class))).thenReturn(MO);
        Mockito.when(nscsNodeUtility.getSingleInstanceMoFdn(NODE.getFdn(), CERT_MO)).thenReturn(PICO_NODE_CERTM);
        Mockito.when(comEcimNodeUtility.getNodeCredentialFdn(Mockito.anyString(), Mockito.any(Mo.class), Mockito.anyString(),
                Mockito.eq(mockNormalizableNodeReference))).thenReturn(NODE.getFdn());
        Mockito.when(mockNormalizableNodeReference.getNeType()).thenReturn("HLR-FE");
        Mockito.when(mockNormalizableNodeReference.getNeType()).thenReturn("vHLR-FE");
        Mockito.when(mockNormalizableNodeReference.getNeType()).thenReturn("HLR-FE-BSP");
        Mockito.when(mockNormalizableNodeReference.getNeType()).thenReturn("HLR-FE-IS");


    }

    @Test
    public void testProcessTask() throws CppSecurityServiceException {

        Mockito.when(readerService.getMoObjectByFdn(Mockito.anyString())).thenReturn(mockMoObject);
        Mockito.when(
                nscsModelServiceImpl.getModelInfoList(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Matchers.<String> anyVararg()))
                .thenReturn(null);
        Mockito.when(readerService.getNodeModelInformation(Mockito.anyString())).thenReturn(NodeModelInformation);
        Mockito.when(nscsNodeUtility.getAlgorithmKeys(Mockito.anyString(), Mockito.any(NormalizableNodeReference.class))).thenReturn(AlgorithmKeys.ECDSA_256);
        Mockito.when(comEcimNodeUtility.getTrustCategoryFdn(Mockito.anyString(), Mockito.any(Mo.class), Mockito.anyString(),
                Mockito.eq(mockNormalizableNodeReference))).thenReturn("test");
        Mockito.when(mockMoObject.getAttribute(TrustCategory.TRUSTED_CERTIFICATES)).thenReturn(Arrays.asList(new String[]{"NE_OAM_CA","PKI_ROOT_CA"}));
        Map<String,Object> test = new HashMap<String, Object>();
        test.put(CertificateContent.SERIAL_NUMBER, "5170857900660139862");
        test.put(CertificateContent.ISSUER, "DC=com, DC=ATT, DC=mx, CN=ENM_TEST_CA");
        Mockito.when(mockMoObject.getAttribute(TrustedCertificate.CERTIFICATE_CONTENT)).thenReturn(test);
        Mockito.when(mockMoObject.getAttribute(TrustedCertificate.RESERVED_BY_CATEGORY)).thenReturn(Arrays.asList(new String[0]));
        try {
            comEcimCheckRemoveTrustTaskHandler.processTask(comEcimCheckRemoveTrustTask);
        } catch (final Exception e) {
        }

    }

}
