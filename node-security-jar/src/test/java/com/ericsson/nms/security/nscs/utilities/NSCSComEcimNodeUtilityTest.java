package com.ericsson.nms.security.nscs.utilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.util.HashMap;
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

import com.ericsson.nms.security.nscs.api.enums.DigestAlgorithm;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfoImpl;
import com.ericsson.nms.security.nscs.data.MoObject;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.ModelDefinition.ComEcimManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.command.impl.MockUtils;
import com.ericsson.nms.security.nscs.ldap.utility.LdapConstants;
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;

@RunWith(MockitoJUnitRunner.class)
public class NSCSComEcimNodeUtilityTest {

    private static final String RADIO_NODE_NAME = "RADIO-NODE-123";
    private static final String RADIO_NODE_ROOT_FDN = String.format("ManagedElement=%s", RADIO_NODE_NAME);
    private static final String RADIO_NODE_ENROLLMENT_URI = "https://localhost:8443/app/resource";
    private static final String PICO_NODE_NAME = "PICO-123";
    private static final String PICO_NODE_ROOT_FDN = String.format("MeContext=%s,ManagedElement=%s", PICO_NODE_NAME, PICO_NODE_NAME);
    private static final String PICO_NODE_CERTM = String.format("%s,SystemFunctions=1,SecM=1,CertM=1", PICO_NODE_ROOT_FDN);

    @Spy
    private final Logger logger = LoggerFactory.getLogger(NSCSComEcimNodeUtility.class);

    @Mock
    private NscsNodeUtility nscsNodeUtility;

    @Mock
    private ComEcimMoNaming comEcimMoNaming;

    @Mock
    private NscsCapabilityModelService nscsCapabilityModelService;

    @Mock
    private NscsCMReaderService reader;

    @Mock
    private NscsModelServiceImpl nscsModelServiceImpl;

    @InjectMocks
    private NSCSComEcimNodeUtility beanUnderTest;

    private ScepEnrollmentInfoImpl enrollmentInfo;

    private NormalizableNodeReference radioNodeNormNodeRef = MockUtils.createNormalizableNodeRef("RadioNode", "RadioNode_NSCSComEcimNodeUtility");
    private NormalizableNodeReference msrbsv1NormNodeRef = MockUtils.createNormalizableNodeRef("MSRBS_V1", "PicoNode_NSCSComEcimNodeUtility");

    @Before
    public void setup() {
        final Entity ee = new Entity();
        try {
            enrollmentInfo = new ScepEnrollmentInfoImpl(ee, RADIO_NODE_ENROLLMENT_URI, null, DigestAlgorithm.MD5, 10, "challengePWD", "2048",
                    EnrollmentMode.CMPv2_VC, null, null);
        } catch (final CertificateEncodingException e) {
        } catch (final NoSuchAlgorithmException e) {
        }
        when(comEcimMoNaming.getDefaultName("NodeCredential", "OAM", radioNodeNormNodeRef)).thenReturn("oamNodeCredential");
        when(comEcimMoNaming.getDefaultName("NodeCredential", "IPSEC", radioNodeNormNodeRef)).thenReturn("ipsecNodeCredential");
        when(comEcimMoNaming.getDefaultName("NodeCredential", "OAM", msrbsv1NormNodeRef)).thenReturn("1");
        when(comEcimMoNaming.getDefaultName("NodeCredential", "IPSEC", msrbsv1NormNodeRef)).thenReturn("2");
        when(comEcimMoNaming.getDefaultName("TrustCategory", "OAM", radioNodeNormNodeRef)).thenReturn("oamTrustCategory");
        when(comEcimMoNaming.getDefaultName("TrustCategory", "IPSEC", radioNodeNormNodeRef)).thenReturn("ipsecTrustCategory");
        when(comEcimMoNaming.getDefaultName("TrustCategory", "OAM", msrbsv1NormNodeRef)).thenReturn("1");
        when(comEcimMoNaming.getDefaultName("TrustCategory", "IPSEC", msrbsv1NormNodeRef)).thenReturn("2");

    }

    @Test(expected = UnexpectedErrorException.class)
    public void testGetCertificateTypeFromNullTrustedCertCategory() {
        NscsNodeUtility.getCertificateTypeFromTrustedCertCategory(null);
    }

    @Test(expected = UnexpectedErrorException.class)
    public void testGetCertificateTypeFromInvalidTrustedCertCategory() {
        NscsNodeUtility.getCertificateTypeFromTrustedCertCategory("INVALID");
    }

    @Test
    public void testGetCertificateTypeFromCorbaPeersTrustedCertCategory() {
        assertEquals("OAM", NscsNodeUtility.getCertificateTypeFromTrustedCertCategory("CORBA_PEERS"));
    }

    @Test
    public void testGetCertificateTypeFromIpsecTrustedCertCategory() {
        assertEquals("IPSEC", NscsNodeUtility.getCertificateTypeFromTrustedCertCategory("IPSEC"));
    }

    @Test
    public void testGetKeySizeFromNullEnrollmentInfo() {
        assertNull(beanUnderTest.getKeySizeFromEnrollmentInfo(null));
    }

    @Test
    public void testGetKeySizeFromInvalidEnrollmentInfo() {
        enrollmentInfo.setKeySize("INVALID");
        assertEquals("RSA_2048", beanUnderTest.getKeySizeFromEnrollmentInfo(enrollmentInfo));
    }

    @Test
    public void testGetKeySizeFromValidEnrollmentInfo() {
        final Map<String, String> keyLengthsAndSizes = new HashMap<String, String>();
        keyLengthsAndSizes.put("0", "RSA_1024");
        keyLengthsAndSizes.put("1", "RSA_2048");
        keyLengthsAndSizes.put("2", "RSA_3072");
        keyLengthsAndSizes.put("3", "RSA_4096");
        keyLengthsAndSizes.put("4", "ECDSA_160");
        keyLengthsAndSizes.put("5", "ECDSA_224");
        keyLengthsAndSizes.put("6", "ECDSA_256");
        keyLengthsAndSizes.put("7", "ECDSA_384");
        keyLengthsAndSizes.put("8", "ECDSA_512");
        keyLengthsAndSizes.put("9", "ECDSA_521");

        for (final String length : keyLengthsAndSizes.keySet()) {
            enrollmentInfo.setKeySize(length);
            assertEquals(keyLengthsAndSizes.get(length), beanUnderTest.getKeySizeFromEnrollmentInfo(enrollmentInfo));
        }
    }

    @Test
    public void testGetNodeCredentialFdnWithNullMirrorRootFdn() {
        assertNull(beanUnderTest.getNodeCredentialFdn(null, Model.ME_CONTEXT.comManagedElement, "OAM", radioNodeNormNodeRef));
    }

    @Test
    public void testGetNodeCredentialFdnWithNullRootMo() {
        final Mo rootMo = null;
        assertNull(beanUnderTest.getNodeCredentialFdn(RADIO_NODE_ROOT_FDN, rootMo, "OAM", radioNodeNormNodeRef));
    }

    @Test
    public void testGetNodeCredentialFdnWithNullCertType() {
        assertNull(beanUnderTest.getNodeCredentialFdn(RADIO_NODE_ROOT_FDN, Model.ME_CONTEXT.comManagedElement, null, radioNodeNormNodeRef));
    }

    @Test
    public void testGetNodeCredntialFdnForPicoAndIPSEC() {
        final Mo rootMo = Model.ME_CONTEXT.comManagedElement;
        final Mo certMMo = ((ComEcimManagedElement) rootMo).systemFunctions.secM.certM;
        final String nodeCredentialFdn = PICO_NODE_CERTM + ",NodeCredential=2";
        Mockito.when(nscsCapabilityModelService.isIkev2PolicyProfileSupported(msrbsv1NormNodeRef)).thenReturn(false);
        Mockito.when(nscsNodeUtility.getSingleInstanceMoFdn(Mockito.any(String.class), Mockito.eq(certMMo))).thenReturn(PICO_NODE_CERTM);
        final MoObject nodeCredentialsMoObj = org.mockito.Mockito.mock(MoObject.class);
        Mockito.when(reader.getMoObjectByFdn(nodeCredentialFdn)).thenReturn(nodeCredentialsMoObj);
        final String nodeCredentialIPSEC = beanUnderTest.getNodeCredentialFdn(PICO_NODE_ROOT_FDN, rootMo, "IPSEC", msrbsv1NormNodeRef);
        assertEquals(nodeCredentialFdn, nodeCredentialIPSEC);
    }

    @Test
    public void testGetNodeCredntialFdnForPicoAndIPSECNotCreated() {
        final Mo rootMo = Model.ME_CONTEXT.comManagedElement;
        final Mo certMMo = ((ComEcimManagedElement) rootMo).systemFunctions.secM.certM;
        final String nodeCredentialFdn = PICO_NODE_CERTM + ",NodeCredential=2";
        Mockito.when(nscsCapabilityModelService.isIkev2PolicyProfileSupported(msrbsv1NormNodeRef)).thenReturn(false);
        Mockito.when(nscsNodeUtility.getSingleInstanceMoFdn(Mockito.any(String.class), Mockito.eq(certMMo))).thenReturn(PICO_NODE_CERTM);
        Mockito.when(reader.getMoObjectByFdn(nodeCredentialFdn)).thenReturn(null);
        final String nodeCredentialIPSEC = beanUnderTest.getNodeCredentialFdn(PICO_NODE_ROOT_FDN, rootMo, "IPSEC", msrbsv1NormNodeRef);
        assertNull(nodeCredentialIPSEC);
    }

    @Test
    public void testGetTrustCategoryFdnForPicoAndIPSEC() {
        final Mo rootMo = Model.ME_CONTEXT.comManagedElement;
        final Mo certMMo = ((ComEcimManagedElement) rootMo).systemFunctions.secM.certM;
        final String trustCategoryFdn = PICO_NODE_CERTM + ",TrustCategory=2";
        Mockito.when(nscsCapabilityModelService.isIkev2PolicyProfileSupported(msrbsv1NormNodeRef)).thenReturn(false);
        Mockito.when(nscsNodeUtility.getSingleInstanceMoFdn(Mockito.any(String.class), Mockito.eq(certMMo))).thenReturn(PICO_NODE_CERTM);
        final MoObject nodeCredentialsMoObj = org.mockito.Mockito.mock(MoObject.class);
        Mockito.when(reader.getMoObjectByFdn(trustCategoryFdn)).thenReturn(nodeCredentialsMoObj);
        final String trustCategoryPSEC = beanUnderTest.getTrustCategoryFdn(PICO_NODE_ROOT_FDN, rootMo, "IPSEC", msrbsv1NormNodeRef);
        assertEquals(trustCategoryFdn, trustCategoryPSEC);
    }

    @Test
    public void testGetTrustCategoryFdnForPicoAndIPSECNotCreated() {
        final Mo rootMo = Model.ME_CONTEXT.comManagedElement;
        final Mo certMMo = ((ComEcimManagedElement) rootMo).systemFunctions.secM.certM;
        final String trustCategoryFdn = PICO_NODE_CERTM + ",TrustCategory=2";
        Mockito.when(nscsCapabilityModelService.isIkev2PolicyProfileSupported(msrbsv1NormNodeRef)).thenReturn(false);
        Mockito.when(nscsNodeUtility.getSingleInstanceMoFdn(Mockito.any(String.class), Mockito.eq(certMMo))).thenReturn(PICO_NODE_CERTM);
        Mockito.when(reader.getMoObjectByFdn(trustCategoryFdn)).thenReturn(null);
        final String trustCategoryIPSEC = beanUnderTest.getTrustCategoryFdn(PICO_NODE_ROOT_FDN, rootMo, "IPSEC", msrbsv1NormNodeRef);
        assertNull(trustCategoryIPSEC);
    }

    @Test
    public void testGetLdapMoFdn() {
        Mockito.when(nscsCapabilityModelService.getLdapMoName(radioNodeNormNodeRef)).thenReturn(LdapConstants.COMECIM_LDAP_MO);
        Mockito.when(nscsNodeUtility.getSingleInstanceMoFdn(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn("Ldap");
        String ldapMoName = beanUnderTest.getLdapMoFdn(radioNodeNormNodeRef);
        assertEquals(LdapConstants.COMECIM_LDAP_MO, ldapMoName);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTrustedCertificateReservedByMoAttributeForNullNormNodeRef() {
        beanUnderTest.getTrustedCertificateReservedByMoAttribute(null);
    }

    @Test
    public void testGetTrustedCertificateReservedByMoAttributeForNodeSupportingReservedBy() {
        Mockito.when(nscsModelServiceImpl.isMoAttributeExists(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        String reservedByMoAttribute = beanUnderTest.getTrustedCertificateReservedByMoAttribute(radioNodeNormNodeRef);
        assertEquals(ModelDefinition.TrustedCertificate.RESERVED_BY, reservedByMoAttribute);
    }

    @Test
    public void testGetTrustedCertificateReservedByMoAttributeForNodeNotSupportingReservedBy() {
        Mockito.when(nscsModelServiceImpl.isMoAttributeExists(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString())).thenReturn(false);
        String reservedByMoAttribute = beanUnderTest.getTrustedCertificateReservedByMoAttribute(radioNodeNormNodeRef);
        assertEquals(ModelDefinition.TrustedCertificate.RESERVED_BY_CATEGORY, reservedByMoAttribute);
    }
}
