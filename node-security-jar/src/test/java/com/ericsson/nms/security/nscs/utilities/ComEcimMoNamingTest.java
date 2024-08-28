package com.ericsson.nms.security.nscs.utilities;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
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

import com.ericsson.nms.security.nscs.api.exception.DataAccessException;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.command.impl.MockUtils;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.TargetTypeInformation;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;

@RunWith(MockitoJUnitRunner.class)
public class ComEcimMoNamingTest {

    private static final String RADIO_NODE_NAME = "LTEdg2ERBS12345";
    private static final String RADIO_NODE_ROOT_FDN = "ManagedElement=" + RADIO_NODE_NAME;
    private static final String RADIO_NODE_SYSTEM_FUNCTIONS_FDN = RADIO_NODE_ROOT_FDN + ",SystemFunctions=1";
    private static final String RADIO_NODE_SEC_M_FDN = RADIO_NODE_SYSTEM_FUNCTIONS_FDN + ",SecM=1";
    private static final String RADIO_NODE_CERT_M_FDN = RADIO_NODE_SEC_M_FDN + ",CertM=1";
    private static final String RADIO_NODE_OAM_ENROLLMENT_SERVER_GROUP_FDN = RADIO_NODE_CERT_M_FDN + ",EnrollmentServerGroup=1";
    private static final String RADIO_NODE_IPSEC_ENROLLMENT_SERVER_GROUP_FDN = RADIO_NODE_CERT_M_FDN + ",EnrollmentServerGroup=2";

    @Spy
    private final Logger logger = LoggerFactory.getLogger(NSCSComEcimNodeUtility.class);

    @Mock
    private NscsCMReaderService reader;

    @Mock
    private CmResponse netconfTlsCmResponse;

    @Mock
    private List<CmObject> netconfTlsCmObjs;

    @Mock
    private Iterator<CmObject> netconfTlsIterator;

    @Mock
    private CmObject netconfTlsCmObj;

    @Mock
    private CmResponse ikev2PolicyProfileCmResponse;

    @Mock
    private List<CmObject> ikev2PolicyProfileCmObjs;

    @Mock
    private Iterator<CmObject> ikev2PolicyProfileIterator;

    @Mock
    private CmObject ikev2PolicyProfileCmObj;

    @Mock
    private CmResponse certMCmResponse;

    @Mock
    private List<CmObject> certMCmObjs;

    @Mock
    private Iterator<CmObject> certMIterator;

    @Mock
    private CmObject certMCmObj;

    @Mock
    private CmResponse nodeCredentialCmResponse;

    @Mock
    private CmResponse enrollmentServerCmResponse;

    @Mock
    private NscsNodeUtility nscsNodeUtility;

    @Mock
    private NscsCapabilityModelService nscsCapabilityModel;

    @InjectMocks
    private ComEcimMoNaming beanUnderTest;

    private NormalizableNodeReference unknownNodeNormNodeRef = MockUtils.createNormalizableNodeRef("UNKNOWNN", "Unknown_ComEcimMoNaming");
    private NormalizableNodeReference radioNodeNormNodeRef = MockUtils.createNormalizableNodeRef("RadioNode", "RadioNode_ComEcimMoNaming");
    private NormalizableNodeReference msrbsv1NormNodeRef = MockUtils.createNormalizableNodeRef("MSRBS_V1", "PicoNode_ComEcimMoNaming");

    private Mo secMMo = Model.COM_MANAGED_ELEMENT.systemFunctions.secM;
    private Mo certMMo = Model.COM_MANAGED_ELEMENT.systemFunctions.secM.certM;
    private Mo nodeCredentialMo = Model.COM_MANAGED_ELEMENT.systemFunctions.secM.certM.nodeCredential;
    private Mo trustCategoryMo = Model.COM_MANAGED_ELEMENT.systemFunctions.secM.certM.trustCategory;
    private Mo enrollmentAuthorityMo = Model.COM_MANAGED_ELEMENT.systemFunctions.secM.certM.enrollmentAuthority;
    private Mo enrollmentServerGroupMo = Model.COM_MANAGED_ELEMENT.systemFunctions.secM.certM.enrollmentServerGroup;
    private Mo enrollmentServerMo = Model.COM_MANAGED_ELEMENT.systemFunctions.secM.certM.enrollmentServerGroup.enrollmentServer;
    private Mo sysMMo = Model.COM_MANAGED_ELEMENT.systemFunctions.sysM;
    private Mo netconfTlsMo = Model.COM_MANAGED_ELEMENT.systemFunctions.sysM.netconfTls;
    private Mo transportMo = Model.COM_MANAGED_ELEMENT.transport;
    private Mo ikev2PolicyProfileMo = Model.COM_MANAGED_ELEMENT.transport.ikev2PolicyProfile;

    static Map<List<String>, String> theMoNames = new HashMap<List<String>, String>();

    static {
        theMoNames.put(null, "3");
        // Old default values
        theMoNames.put(Arrays.asList("oamNodeCredential"), "3");
        theMoNames.put(Arrays.asList("ipsecNodeCredential"), "3");
        theMoNames.put(Arrays.asList("oamNodeCredential", "3"), "4");
        theMoNames.put(Arrays.asList("ipsecNodeCredential", "3"), "4");
        theMoNames.put(Arrays.asList("oamNodeCredential", "ipsecNodeCredential"), "3");
        theMoNames.put(Arrays.asList("oamNodeCredential", "ipsecNodeCredential", "2"), "3");
        theMoNames.put(Arrays.asList("oamNodeCredential", "ipsecNodeCredential", "1"), "3");
        theMoNames.put(Arrays.asList("oamNodeCredential", "ipsecNodeCredential", "1", "3"), "4");
        theMoNames.put(Arrays.asList("oamNodeCredential", "ipsecNodeCredential", "1", "2", "3"), "4");
        theMoNames.put(Arrays.asList("oamNodeCredential", "ipsecNodeCredential", "1", "3", "4"), "5");
        theMoNames.put(Arrays.asList("oamNodeCredential", "ipsecNodeCredential", "2", "3", "4"), "5");
        // New default values
        theMoNames.put(Arrays.asList("1"), "3");
        theMoNames.put(Arrays.asList("2"), "3");
        theMoNames.put(Arrays.asList("1", "3"), "4");
        theMoNames.put(Arrays.asList("2", "3"), "4");
        theMoNames.put(Arrays.asList("1", "2"), "3");
        theMoNames.put(Arrays.asList("1", "2", "3"), "4");
        theMoNames.put(Arrays.asList("1", "2", "4"), "3");
        theMoNames.put(Arrays.asList("1", "2", "3", "4"), "5");
        theMoNames.put(Arrays.asList("1", "2", "3", "4", "5"), "6");
        theMoNames.put(Arrays.asList("1", "2", "3", "5", "6"), "4");
        theMoNames.put(Arrays.asList("1", "2", "3", "4", "6"), "5");
    };

    @Before
    public void setup() {
        final Map<String, String> defaultComEcimDefaultNodeCredentialId = new HashMap<String, String>();
        defaultComEcimDefaultNodeCredentialId.put("OAM", "oamNodeCredential");
        defaultComEcimDefaultNodeCredentialId.put("IPSEC", "ipsecNodeCredential");
        final Map<String, String> msrbsV1DefaultNodeCredentialId = new HashMap<String, String>();
        msrbsV1DefaultNodeCredentialId.put("OAM", "1");
        msrbsV1DefaultNodeCredentialId.put("IPSEC", "2");
        when(nscsCapabilityModel.getComEcimDefaultNodeCredentialIds(unknownNodeNormNodeRef)).thenReturn(defaultComEcimDefaultNodeCredentialId);
        when(nscsCapabilityModel.getComEcimDefaultNodeCredentialIds(radioNodeNormNodeRef)).thenReturn(defaultComEcimDefaultNodeCredentialId);
        when(nscsCapabilityModel.getComEcimDefaultNodeCredentialIds(msrbsv1NormNodeRef)).thenReturn(msrbsV1DefaultNodeCredentialId);
        when(nscsCapabilityModel.getComEcimDefaultNodeCredentialIds(Mockito.any(NodeModelInformation.class)))
                .thenReturn(msrbsV1DefaultNodeCredentialId);
        final Map<String, String> defaultComEcimDefaultTrustCategoryId = new HashMap<String, String>();
        defaultComEcimDefaultTrustCategoryId.put("OAM", "oamTrustCategory");
        defaultComEcimDefaultTrustCategoryId.put("IPSEC", "ipsecTrustCategory");
        final Map<String, String> msrbsV1DefaultTrustCategoryId = new HashMap<String, String>();
        msrbsV1DefaultTrustCategoryId.put("OAM", "1");
        msrbsV1DefaultTrustCategoryId.put("IPSEC", "2");
        when(nscsCapabilityModel.getComEcimDefaultTrustCategoryIds(unknownNodeNormNodeRef)).thenReturn(defaultComEcimDefaultTrustCategoryId);
        when(nscsCapabilityModel.getComEcimDefaultTrustCategoryIds(radioNodeNormNodeRef)).thenReturn(defaultComEcimDefaultTrustCategoryId);
        when(nscsCapabilityModel.getComEcimDefaultTrustCategoryIds(msrbsv1NormNodeRef)).thenReturn(msrbsV1DefaultTrustCategoryId);
        final List<String> supportedNeTypes = Arrays.asList("ERBS", "RadioNode", "MSRBS_V1", "Router6672", "CISCO-ASR900");
        when(nscsCapabilityModel.getTargetTypes(TargetTypeInformation.CATEGORY_NODE)).thenReturn(supportedNeTypes);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetDefaultNameWithNullMoType() {
        when(unknownNodeNormNodeRef.getNeType()).thenReturn(null);
        final String moType = null;
        beanUnderTest.getDefaultName(moType, "OAM", unknownNodeNormNodeRef);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetDefaultNameWithNullParam() {
        beanUnderTest.getDefaultName(nodeCredentialMo.type(), null, unknownNodeNormNodeRef);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetDefaultNameWithEmptyParam() {
        beanUnderTest.getDefaultName(nodeCredentialMo.type(), "", unknownNodeNormNodeRef);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetDefaultNameWithInvalidMoType() {
        beanUnderTest.getDefaultName(certMMo.type(), "OAM", unknownNodeNormNodeRef);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetDefaultNameWithInvalidCertTypeParam() {
        beanUnderTest.getDefaultName(nodeCredentialMo.type(), "OEM", unknownNodeNormNodeRef);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetDefaultNameWithValidEnrollmentProtocolAsInvalidCertType() {
        beanUnderTest.getDefaultName(nodeCredentialMo.type(), "CMP", unknownNodeNormNodeRef);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetDefaultNameWithInvalidEnrollmentProtocolParam() {
        beanUnderTest.getDefaultName(enrollmentServerMo.type(), "CMPv2", radioNodeNormNodeRef);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetDefaultNameWithValidCertTypeAsInvalidEnrollmentProtocol() {
        beanUnderTest.getDefaultName(enrollmentServerMo.type(), "OAM", radioNodeNormNodeRef);
    }

    @Test
    public void testGetDefaultNameWithValidMoAndValidCertificateType() {
        final NormalizableNodeReference node = null;
        when(unknownNodeNormNodeRef.getNeType()).thenReturn(null);
        assertEquals("oamNodeCredential", beanUnderTest.getDefaultName(nodeCredentialMo.type(), "OAM", unknownNodeNormNodeRef));
        assertEquals("oamTrustCategory", beanUnderTest.getDefaultName(trustCategoryMo.type(), "OAM", unknownNodeNormNodeRef));
        assertEquals("1", beanUnderTest.getDefaultName(enrollmentAuthorityMo.type(), "OAM", node));
        assertEquals("1", beanUnderTest.getDefaultName(enrollmentServerGroupMo.type(), "OAM", node));
        assertEquals("ipsecNodeCredential", beanUnderTest.getDefaultName(nodeCredentialMo.type(), "IPSEC", unknownNodeNormNodeRef));
        assertEquals("ipsecTrustCategory", beanUnderTest.getDefaultName(trustCategoryMo.type(), "IPSEC", unknownNodeNormNodeRef));
        assertEquals("2", beanUnderTest.getDefaultName(enrollmentAuthorityMo.type(), "IPSEC", node));
        assertEquals("2", beanUnderTest.getDefaultName(enrollmentServerGroupMo.type(), "IPSEC", node));
    }

    @Test
    public void testGetDefaultNameWithValidMoAndValidCertificateTypePicoRbs() {
        assertEquals("1", beanUnderTest.getDefaultName(nodeCredentialMo.type(), "OAM", msrbsv1NormNodeRef));
        assertEquals("1", beanUnderTest.getDefaultName(trustCategoryMo.type(), "OAM", msrbsv1NormNodeRef));
        assertEquals("1", beanUnderTest.getDefaultName(enrollmentAuthorityMo.type(), "OAM", msrbsv1NormNodeRef));
        assertEquals("1", beanUnderTest.getDefaultName(enrollmentServerGroupMo.type(), "OAM", msrbsv1NormNodeRef));
        assertEquals("2", beanUnderTest.getDefaultName(nodeCredentialMo.type(), "IPSEC", msrbsv1NormNodeRef));
        assertEquals("2", beanUnderTest.getDefaultName(trustCategoryMo.type(), "IPSEC", msrbsv1NormNodeRef));
        assertEquals("2", beanUnderTest.getDefaultName(enrollmentAuthorityMo.type(), "IPSEC", msrbsv1NormNodeRef));
        assertEquals("2", beanUnderTest.getDefaultName(enrollmentServerGroupMo.type(), "IPSEC", msrbsv1NormNodeRef));
    }

    @Test
    public void testGetDefaultNameWithValidMoTypeAndValidEnrollmentProtocol() {
        final NormalizableNodeReference node = null;
        assertEquals("1", beanUnderTest.getDefaultName(enrollmentServerMo.type(), "CMP", node));
        assertEquals("2", beanUnderTest.getDefaultName(enrollmentServerMo.type(), "SCEP", node));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNameWithNullMoType() {
        final String moType = null;
        ComEcimMoNaming.getName(moType);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNameWithInvalidMoType() {
        ComEcimMoNaming.getName(nodeCredentialMo.type());
    }

    @Test
    public void testGetNameWithValidMoType() {
        assertEquals("1", ComEcimMoNaming.getName(secMMo.type()));
        assertEquals("1", ComEcimMoNaming.getName(certMMo.type()));
        assertEquals("1", ComEcimMoNaming.getName(sysMMo.type()));
        assertEquals("1", ComEcimMoNaming.getName(netconfTlsMo.type()));
        assertEquals("1", ComEcimMoNaming.getName(transportMo.type()));
        assertEquals("1", ComEcimMoNaming.getName(ikev2PolicyProfileMo.type()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetFirstAvailableNameWithNullMoTypeAndNullParentFdn() {
        beanUnderTest.getFirstAvailableName(null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetFirstAvailableNameWithNullMoType() {
        beanUnderTest.getFirstAvailableName(null, null, RADIO_NODE_CERT_M_FDN);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetFirstAvailableNameWithNullParentFdn() {
        beanUnderTest.getFirstAvailableName(nodeCredentialMo.type(), null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetFirstAvailableNameWithEmptyParentFdn() {
        beanUnderTest.getFirstAvailableName(nodeCredentialMo.type(), null, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetFirstAvailableWithInvalidMoType() {
        beanUnderTest.getFirstAvailableName(transportMo.type(), null, RADIO_NODE_CERT_M_FDN);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = UnexpectedErrorException.class)
    public void testGetFirstAvailableNameWithExceptionOnCmResponse() {
        when(reader.getMos(eq(RADIO_NODE_ROOT_FDN), eq(nodeCredentialMo.type()), eq(nodeCredentialMo.namespace())))
                .thenThrow(DataAccessException.class);
        beanUnderTest.getFirstAvailableName(nodeCredentialMo.type(), nodeCredentialMo.namespace(), RADIO_NODE_ROOT_FDN);
    }

    @Test(expected = UnexpectedErrorException.class)
    public void testGetFirstAvailableNameWithNullCmResponse() {
        when(reader.getMos(eq(RADIO_NODE_ROOT_FDN), eq(nodeCredentialMo.type()), eq(nodeCredentialMo.namespace()))).thenReturn(null);
        beanUnderTest.getFirstAvailableName(nodeCredentialMo.type(), nodeCredentialMo.namespace(), RADIO_NODE_ROOT_FDN);
    }

    @Test(expected = UnexpectedErrorException.class)
    public void testGetFirstAvailableNameWithNullCmObjects() {
        when(reader.getMos(eq(RADIO_NODE_ROOT_FDN), eq(nodeCredentialMo.type()), eq(nodeCredentialMo.namespace())))
                .thenReturn(nodeCredentialCmResponse);
        when(nodeCredentialCmResponse.getCmObjects()).thenReturn(null);
        beanUnderTest.getFirstAvailableName(nodeCredentialMo.type(), nodeCredentialMo.namespace(), RADIO_NODE_ROOT_FDN);
    }

    @Test
    public void testGetFirstAvailableNameWithValidNodeCredentialResponse() {

        when(reader.getMos(eq(RADIO_NODE_ROOT_FDN), eq(nodeCredentialMo.type()), eq(nodeCredentialMo.namespace())))
                .thenReturn(nodeCredentialCmResponse);
        for (final Map.Entry<List<String>, String> entry : theMoNames.entrySet()) {
            final List<CmObject> cmObjs = new ArrayList<CmObject>();
            final List<String> existingMos = entry.getKey();
            if (existingMos != null) {
                final Iterator<String> it = existingMos.iterator();
                while (it.hasNext()) {
                    final CmObject cmObj = new CmObject();
                    cmObj.setName(it.next());
                    cmObjs.add(cmObj);
                }
            }
            when(nodeCredentialCmResponse.getCmObjects()).thenReturn(cmObjs);
            final String moName = beanUnderTest.getFirstAvailableName(nodeCredentialMo.type(), nodeCredentialMo.namespace(), RADIO_NODE_ROOT_FDN);
            assertEquals(entry.getValue(), moName);
        }
    }

    @Test
    public void testGetFirstAvailableNameWithValidOamEnrollmentServerResponse() {

        when(reader.getMos(eq(RADIO_NODE_OAM_ENROLLMENT_SERVER_GROUP_FDN), eq(enrollmentServerMo.type()), eq(enrollmentServerMo.namespace())))
                .thenReturn(enrollmentServerCmResponse);
        for (final Map.Entry<List<String>, String> entry : theMoNames.entrySet()) {
            final List<CmObject> cmObjs = new ArrayList<CmObject>();
            final List<String> existingMos = entry.getKey();
            if (existingMos != null) {
                final Iterator<String> it = existingMos.iterator();
                while (it.hasNext()) {
                    final CmObject cmObj = new CmObject();
                    cmObj.setName(it.next());
                    cmObjs.add(cmObj);
                }
            }
            when(enrollmentServerCmResponse.getCmObjects()).thenReturn(cmObjs);
            final String moName = beanUnderTest.getFirstAvailableName(enrollmentServerMo.type(), enrollmentServerMo.namespace(),
                    RADIO_NODE_OAM_ENROLLMENT_SERVER_GROUP_FDN);
            assertEquals(entry.getValue(), moName);
        }
    }

    @Test
    public void testGetFirstAvailableNameWithValidIpsecEnrollmentServerResponse() {

        when(reader.getMos(eq(RADIO_NODE_IPSEC_ENROLLMENT_SERVER_GROUP_FDN), eq(enrollmentServerMo.type()), eq(enrollmentServerMo.namespace())))
                .thenReturn(enrollmentServerCmResponse);
        for (final Map.Entry<List<String>, String> entry : theMoNames.entrySet()) {
            final List<CmObject> cmObjs = new ArrayList<CmObject>();
            final List<String> existingMos = entry.getKey();
            if (existingMos != null) {
                final Iterator<String> it = existingMos.iterator();
                while (it.hasNext()) {
                    final CmObject cmObj = new CmObject();
                    cmObj.setName(it.next());
                    cmObjs.add(cmObj);
                }
            }
            when(enrollmentServerCmResponse.getCmObjects()).thenReturn(cmObjs);
            final String moName = beanUnderTest.getFirstAvailableName(enrollmentServerMo.type(), enrollmentServerMo.namespace(),
                    RADIO_NODE_IPSEC_ENROLLMENT_SERVER_GROUP_FDN);
            assertEquals(entry.getValue(), moName);
        }
    }
}
