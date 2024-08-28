package com.ericsson.nms.security.nscs.certificate.info;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.MoObject;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition.CertificateContent;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NodeCredential;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moget.impl.ComEcimMOGetServiceImpl;
import com.ericsson.nms.security.nscs.data.moget.param.CertStateInfo;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.command.impl.MockUtils;
import com.ericsson.nms.security.nscs.utilities.NSCSComEcimNodeUtility;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;

@RunWith(MockitoJUnitRunner.class)
public class NscsComEcimCertificateStateInfoTest {

    private static final String RADIO_NODE_NAME = "RADIO-NODE-123";
    private static final String RADIO_NODE_TYPE = "RadioNode";
    private static final String RADIO_NODE_FDN = String.format("NetworkElement=%s", RADIO_NODE_NAME);
    private static final String RADIO_NODE_ROOT_FDN = String.format("ManagedElement=%s", RADIO_NODE_NAME);
    private static final String RADIO_NODE_CERT_M_FDN = String.format("%s,,SystemFunctions=1,SecM=1,CertM=1", RADIO_NODE_ROOT_FDN);
    private static final String RADIO_NODE_OAM_NODE_CREDENTIAL_FDN = String.format("%s,NodeCredential=oamNodeCredential", RADIO_NODE_CERT_M_FDN);
    private static final String RADIO_NODE_OAM_TRUST_CATEGORY_FDN = String.format("%s,TrustCategory=oamTrustCategory", RADIO_NODE_CERT_M_FDN);
    private static final String RADIO_NODE_IPSEC_NODE_CREDENTIAL_FDN = String.format("%s,NodeCredential=ipsecNodeCredential", RADIO_NODE_CERT_M_FDN);
    private static final String RADIO_NODE_IPSEC_TRUST_CATEGORY_FDN = String.format("%s,TrustCategory=ipsecTrustCategory", RADIO_NODE_CERT_M_FDN);
    private final NodeReference radioNodeRef = new NodeRef(RADIO_NODE_NAME);
    private final CertStateInfo notAvailableGetCertStateInfo = new CertStateInfo(RADIO_NODE_FDN);

    @Spy
    private final Logger logger = LoggerFactory.getLogger(ComEcimMOGetServiceImpl.class);

    @Mock
    private NscsCMReaderService reader;

    @Mock
    private NscsCapabilityModelService capabilityModel;

    @Mock
    private NSCSComEcimNodeUtility nscsComEcimNodeUtility;

    @Mock
    private MoObject nodeCredentialMoObj;

    @Mock
    private CmResponse certMCmResponse;

    @Mock
    private List<CmObject> certMCmObjs;

    @Mock
    private Iterator<CmObject> certMIterator;

    @Mock
    private CmObject certMCmObj;

    @Mock
    private NscsNodeUtility nscsNodeUtility;

    @InjectMocks
    private ComEcimMOGetServiceImpl beanUnderTest;

    private NormalizableNodeReference radioNormNodeRef;

    @Before
    public void setup() {

        radioNormNodeRef = MockUtils.createNormNodeRef(RADIO_NODE_NAME, RADIO_NODE_TYPE, null, radioNodeRef, reader);
        final Mo rootMo = Model.ME_CONTEXT.comManagedElement;
        when(capabilityModel.getMirrorRootMo(eq(radioNormNodeRef))).thenReturn(rootMo);
        when(nscsComEcimNodeUtility.getNodeCredentialFdn(RADIO_NODE_ROOT_FDN, rootMo, "OAM", radioNormNodeRef))
                .thenReturn(RADIO_NODE_OAM_NODE_CREDENTIAL_FDN);
        when(nscsComEcimNodeUtility.getNodeCredentialFdn(RADIO_NODE_ROOT_FDN, rootMo, "IPSEC", radioNormNodeRef))
                .thenReturn(RADIO_NODE_IPSEC_NODE_CREDENTIAL_FDN);
        when(nscsComEcimNodeUtility.getTrustCategoryFdn(RADIO_NODE_ROOT_FDN, rootMo, "OAM", radioNormNodeRef))
                .thenReturn(RADIO_NODE_OAM_TRUST_CATEGORY_FDN);
        when(nscsComEcimNodeUtility.getTrustCategoryFdn(RADIO_NODE_ROOT_FDN, rootMo, "IPSEC", radioNormNodeRef))
                .thenReturn(RADIO_NODE_IPSEC_TRUST_CATEGORY_FDN);
    }

    private void checkCertEnrollInfo(final CertStateInfo expected, final CertStateInfo actual, final boolean isCheckAll) {
        assertEquals(expected.getState(), actual.getState());
        if (isCheckAll) {
            assertEquals(expected.getErrorMsg(), actual.getErrorMsg());
        }
    }

    @Test
    public void testGetCertificateIssueStateInfoWithNullNodeReference() {
        assertNull(beanUnderTest.getCertificateIssueStateInfo(null, "OAM"));
    }

    @Test
    public void testGetCertificateIssueStateInfoWithNullCertType() {
        assertNull(beanUnderTest.getCertificateIssueStateInfo(radioNodeRef, null));
    }

    @Test
    public void testGetCertificateIssueStateInfoWithNodeReferenceAndCertType() {
        final Map<String, Object> enrollmentProgress = new HashMap<>();
        final CertStateInfo idleGetCertStateInfo = new CertStateInfo(RADIO_NODE_FDN);
        idleGetCertStateInfo.setState("IDLE");
        Mockito.when(reader.getMoObjectByFdn(Mockito.anyString())).thenReturn(nodeCredentialMoObj);
        Mockito.when(nodeCredentialMoObj.getAttribute(NodeCredential.ENROLLMENT_PROGRESS)).thenReturn(enrollmentProgress);
        final CertStateInfo actual = beanUnderTest.getCertificateIssueStateInfo(radioNodeRef, "OAM");
        checkCertEnrollInfo(idleGetCertStateInfo, actual, false);
    }

    @Test
    public void testGetCertificateIssueStateInfoWithNodeReferenceAndCertTypeWithIssuerAndSubjectDN() {
        final Map<String, Object> enrollmentProgress = new HashMap<>();
        final Map<String, Object> certificateContent = new HashMap<>();
        certificateContent.put(CertificateContent.ISSUER, "CN=ENM_TEST_CA,domainComponent=mx,domainComponent=ATT,domainComponent=com");
        certificateContent.put(CertificateContent.SERIAL_NUMBER, "6695858043101664338");
        certificateContent.put(CertificateContent.SUBJECT_DIST_NAME, "CN=ENM_TEST_CA,domainComponent=mx,domainComponent=ATT,domainComponent=com");
        final CertStateInfo idleGetCertStateInfo = new CertStateInfo(RADIO_NODE_FDN);
        idleGetCertStateInfo.setState("IDLE");
        Mockito.when(reader.getMoObjectByFdn(Mockito.anyString())).thenReturn(nodeCredentialMoObj);
        Mockito.when(nodeCredentialMoObj.getAttribute(NodeCredential.ENROLLMENT_PROGRESS)).thenReturn(enrollmentProgress);
        Mockito.when(nodeCredentialMoObj.getAttribute(NodeCredential.CERTIFICATE_CONTENT)).thenReturn(certificateContent);
        final CertStateInfo actual = beanUnderTest.getCertificateIssueStateInfo(radioNodeRef, "OAM");
        checkCertEnrollInfo(idleGetCertStateInfo, actual, false);
    }

    @Test
    public void testGetOamCertificateIssueStateInfoWithNotExistentNodeCredential() {
        when(reader.getMoObjectByFdn(eq(RADIO_NODE_OAM_NODE_CREDENTIAL_FDN))).thenReturn(null);
        final CertStateInfo actual = beanUnderTest.getCertificateIssueStateInfo(radioNodeRef, "OAM");
        checkCertEnrollInfo(notAvailableGetCertStateInfo, actual, false);
    }

    @Test
    public void testGetTrustCertificateStateInfoWithNullNodeReference() {
        assertNull(beanUnderTest.getTrustCertificateStateInfo(null, "OAM"));
    }

    @Test
    public void testGetTrustCertificateStateInfoWithNullCertType() {
        assertNull(beanUnderTest.getTrustCertificateStateInfo(radioNodeRef, null));
    }

    @Test
    public void testGetTrustCertificateStateInfoWithNodeReferenceAndCertType() {
        final Map<String, Object> installTrustProgress = new HashMap<String, Object>();
        final Map<String, Object> certMAttributes = Mockito.mock(HashMap.class);
        when(nscsNodeUtility.getSingleInstanceMoFdn(Mockito.anyString(), Mockito.any(Mo.class), Mockito.anyMap(), Mockito.anyString())).thenReturn(RADIO_NODE_CERT_M_FDN);
        when(certMAttributes.get(Mockito.anyString())).thenReturn(installTrustProgress);
        final CertStateInfo actual = beanUnderTest.getTrustCertificateStateInfo(radioNodeRef, "OAM");
        checkCertEnrollInfo(notAvailableGetCertStateInfo, actual, false);
    }
}
