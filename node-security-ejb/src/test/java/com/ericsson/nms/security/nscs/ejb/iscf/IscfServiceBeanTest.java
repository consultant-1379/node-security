/*
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package com.ericsson.nms.security.nscs.ejb.iscf;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import com.ericsson.oss.services.security.nscs.context.NscsContextService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.enums.CertificateType;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.api.instrumentation.NscsInstrumentationService;
import com.ericsson.nms.security.nscs.api.iscf.IpsecArea;
import com.ericsson.nms.security.nscs.api.iscf.NodeIdentifier;
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameParam;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation.ModelIdentifierType;
import com.ericsson.nms.security.nscs.iscf.CombinedIscfGenerator;
import com.ericsson.nms.security.nscs.iscf.IpsecIscfGenerator;
import com.ericsson.nms.security.nscs.iscf.IscfCancelHandler;
import com.ericsson.nms.security.nscs.iscf.IscfGeneratorFactory;
import com.ericsson.nms.security.nscs.iscf.IscfServiceValidators;
import com.ericsson.nms.security.nscs.iscf.SecurityDataCollector;
import com.ericsson.nms.security.nscs.iscf.SecurityLevelIscfGenerator;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;

/**
 * Class for testing implementation if IscfService interface. This tests all use cases available to clients using this interface:
 *
 * <ul>
 * <li>Test generate ISCF for Security Level only</li>
 * <li>Test generate ISCF for IPSec only</li>
 * <li>Test generate ISCF for Security Level and IPSec</li>
 * <li>Test functionality for stopping auto integration for a node and clearing PKI data generated for this node</li>
 * </ul>
 * 
 * @author ealemca
 */
public class IscfServiceBeanTest {

    private final static String CPP_NODE_TYPE = "ERBS";
    private final static String MSRBS_NODE_TYPE = "RadioNode";
    private final static String CPP_MIM_VERSION = "E.1.63";
    private final static String MSRBS_NODE_FDN = "RadioNode01";
    private final static NodeIdentifier nodeId = new NodeIdentifier(MSRBS_NODE_FDN, null);
    private final static NodeModelInformation CPP_MODEL_INFO = new NodeModelInformation(CPP_MIM_VERSION, ModelIdentifierType.MIM_VERSION, CPP_NODE_TYPE);
    private final static NodeModelInformation MSRBS_MODEL_INFO = new NodeModelInformation(null, null, MSRBS_NODE_TYPE);
    private final static SubjectAltNameStringType subjectAltNameString = new SubjectAltNameStringType("127.0.0.1");
    private final static SubjectAltNameParam subjectAltNameParam = new SubjectAltNameParam(SubjectAltNameFormat.IPV4, subjectAltNameString);

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Spy
    private final Logger log = LoggerFactory.getLogger(IscfServiceBean.class);

    @Mock
    NscsInstrumentationService nscsInstrumentationService;

    @Mock
    IscfServiceValidators iscfServiceValidators;

    @Mock
    SecurityLevelIscfGenerator secLevelGenerator;

    @Mock
    IpsecIscfGenerator ipsecGenerator;

    @Mock
    CombinedIscfGenerator combinedGenerator;

    @Mock
    IscfGeneratorFactory generatorFactory;

    @Mock
    IscfCancelHandler cancelHandler;

    @Mock
    private SecurityDataCollector securityDataCollector;
    
    @Mock
    private NscsNodeUtility nscsNodeUtility;

    @Mock
    private NscsContextService nscsContextService;

    @InjectMocks
    IscfServiceBean beanUnderTest;

    @Before
    public void setup() throws Exception {
        initMocks(this);
        doReturn(secLevelGenerator).when(generatorFactory).getSecLevelGenerator(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(SecurityLevel.class),
                Mockito.any(SecurityLevel.class), Mockito.any(EnrollmentMode.class), Mockito.any(NodeModelInformation.class));
        doReturn(ipsecGenerator).when(generatorFactory).getIpsecGenerator(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(SubjectAltNameParam.class),
                Mockito.any(HashSet.class), Mockito.any(EnrollmentMode.class), Mockito.any(NodeModelInformation.class));
        doReturn(combinedGenerator).when(generatorFactory).getCombinedGenerator(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(SecurityLevel.class),
                Mockito.any(SecurityLevel.class), Mockito.any(String.class), Mockito.any(SubjectAltNameParam.class), Mockito.any(HashSet.class), Mockito.any(EnrollmentMode.class),
                Mockito.any(NodeModelInformation.class));
        when(nscsNodeUtility.getNodeNameFromFdn(MSRBS_NODE_FDN)).thenReturn(MSRBS_NODE_FDN);
    }

    @Test
    public void testGenerateOAMLevel2Only() throws Exception {
        log.debug("Test generate O&M Security Level2 data only");
        beanUnderTest.generate("IllogicalName", "ERBS01", SecurityLevel.LEVEL_2, SecurityLevel.LEVEL_2, EnrollmentMode.SCEP, CPP_MODEL_INFO);
        verify(generatorFactory).getSecLevelGenerator("IllogicalName", "ERBS01", SecurityLevel.LEVEL_2, SecurityLevel.LEVEL_2, EnrollmentMode.SCEP, CPP_MODEL_INFO);
        verify(secLevelGenerator).generate();
    }

    @Test
    public void testGenerateIpsecOAMOnly() throws Exception {
        log.debug("Test generate IPSec O&M data only");
        Set<IpsecArea> wantedIpsecAreas = new HashSet<>();
        wantedIpsecAreas.add(IpsecArea.OM);
        beanUnderTest.generate("IllogicalName", "ERBS01", "label", subjectAltNameParam, wantedIpsecAreas, EnrollmentMode.SCEP, CPP_MODEL_INFO);
        verify(generatorFactory).getIpsecGenerator("IllogicalName", "ERBS01", "label", subjectAltNameParam, wantedIpsecAreas, EnrollmentMode.SCEP, CPP_MODEL_INFO);
        verify(ipsecGenerator).generate();
    }

    @Test
    public void testGenerateIpsecTrafficOnly() throws Exception {
        log.debug("Test generate IPSec Traffic data only");
        Set<IpsecArea> wantedIpsecAreas = new HashSet<>();
        wantedIpsecAreas.add(IpsecArea.TRANSPORT);
        beanUnderTest.generate("IllogicalName", "ERBS01", "label", subjectAltNameParam, wantedIpsecAreas, EnrollmentMode.SCEP, CPP_MODEL_INFO);
        verify(generatorFactory).getIpsecGenerator("IllogicalName", "ERBS01", "label", subjectAltNameParam, wantedIpsecAreas, EnrollmentMode.SCEP, CPP_MODEL_INFO);
        verify(ipsecGenerator).generate();
    }

    @Test
    public void testGenerateIpsecTrafficAndOAM() throws Exception {
        log.debug("Test generate IPSec Traffic and O&M data");
        Set<IpsecArea> wantedIpsecAreas = new HashSet<>();
        wantedIpsecAreas.add(IpsecArea.TRANSPORT);
        wantedIpsecAreas.add(IpsecArea.OM);
        beanUnderTest.generate("IllogicalName", "ERBS01", "label", subjectAltNameParam, wantedIpsecAreas, EnrollmentMode.SCEP, CPP_MODEL_INFO);
        verify(generatorFactory).getIpsecGenerator("IllogicalName", "ERBS01", "label", subjectAltNameParam, wantedIpsecAreas, EnrollmentMode.SCEP, CPP_MODEL_INFO);
        verify(ipsecGenerator).generate();
    }

    @Test
    public void testGenerateComboOAMLevel2AndIPSecTrafficAndOAM() throws Exception {
        log.debug("Test generate como OAM Level2 and IPSec Traffic and OAM");
        Set<IpsecArea> wantedIpsecAreas = new HashSet<>();
        wantedIpsecAreas.add(IpsecArea.TRANSPORT);
        wantedIpsecAreas.add(IpsecArea.OM);
        beanUnderTest.generate("IllogicalName", "ERBS01", SecurityLevel.LEVEL_2, SecurityLevel.LEVEL_2, "label", subjectAltNameParam, wantedIpsecAreas, EnrollmentMode.SCEP, CPP_MODEL_INFO);
        verify(generatorFactory).getCombinedGenerator("IllogicalName", "ERBS01", SecurityLevel.LEVEL_2, SecurityLevel.LEVEL_2, "label", subjectAltNameParam, wantedIpsecAreas, EnrollmentMode.SCEP,
                CPP_MODEL_INFO);
    }

    @Test
    public void testCancelIpsecOnly() throws Exception {
        log.debug("Test delete IPSec data only");
        beanUnderTest.cancel("ERBS01");
        verify(cancelHandler).cancel("ERBS01");
    }

    @Test
    public void testGenerateSecurityDataOam_noEntitiesCreated() throws Exception {
       when(securityDataCollector.isNodeEntityCreated(CertificateType.OAM, MSRBS_NODE_FDN)).thenReturn(false);
       when(securityDataCollector.isNodeEntityCreated(CertificateType.IPSEC, MSRBS_NODE_FDN)).thenReturn(false);
        //        exception.expect(IscfServiceException.class);
        beanUnderTest.generateSecurityDataOam(nodeId, EnrollmentMode.SCEP, MSRBS_MODEL_INFO);
        verify(securityDataCollector).getSecurityDataResponse(EnumSet.of(CertificateType.OAM), MSRBS_NODE_FDN, null, null, EnrollmentMode.SCEP, MSRBS_MODEL_INFO);
    }

    @Test
    public void testGenerateSecurityDataOam_entityOamCreated() throws Exception {
        when(securityDataCollector.isNodeEntityCreated(CertificateType.OAM, MSRBS_NODE_FDN)).thenReturn(true);
        when(securityDataCollector.isNodeEntityCreated(CertificateType.IPSEC, MSRBS_NODE_FDN)).thenReturn(false);
        when(securityDataCollector.getSecurityDataResponse(eq(EnumSet.of(CertificateType.IPSEC)), eq(MSRBS_NODE_FDN), anyString(), any(SubjectAltNameParam.class), eq(EnrollmentMode.SCEP),
                any(NodeModelInformation.class))).thenReturn(new SecurityDataResponse(new ArrayList(), new ArrayList()));
        beanUnderTest.generateSecurityDataOam(nodeId, EnrollmentMode.SCEP, MSRBS_MODEL_INFO);
        verify(securityDataCollector).getSecurityDataResponse(EnumSet.of(CertificateType.OAM), MSRBS_NODE_FDN, null, null, EnrollmentMode.SCEP, MSRBS_MODEL_INFO);
    }

    @Test
    public void testGenerateSecurityDataOam_entityIpsecCreated() throws Exception {
        when(securityDataCollector.isNodeEntityCreated(CertificateType.OAM, MSRBS_NODE_FDN)).thenReturn(false);
        when(securityDataCollector.isNodeEntityCreated(CertificateType.IPSEC, MSRBS_NODE_FDN)).thenReturn(true);
        //        exception.expect(IscfServiceException.class);
        beanUnderTest.generateSecurityDataOam(nodeId, EnrollmentMode.SCEP, MSRBS_MODEL_INFO);
        verify(securityDataCollector).getSecurityDataResponse(EnumSet.of(CertificateType.OAM), MSRBS_NODE_FDN, null, null, EnrollmentMode.SCEP, MSRBS_MODEL_INFO);
    }

    @Test
    public void testGenerateSecurityDataOam_bothEntitiesCreated() throws Exception {
        when(securityDataCollector.isNodeEntityCreated(CertificateType.OAM, MSRBS_NODE_FDN)).thenReturn(true);
        when(securityDataCollector.isNodeEntityCreated(CertificateType.IPSEC, MSRBS_NODE_FDN)).thenReturn(true);
        beanUnderTest.generateSecurityDataOam(nodeId, EnrollmentMode.SCEP, MSRBS_MODEL_INFO);
        verify(securityDataCollector).getSecurityDataResponse(EnumSet.of(CertificateType.OAM), MSRBS_NODE_FDN, null, null, EnrollmentMode.SCEP, MSRBS_MODEL_INFO);
    }

    @Test
    public void testGenerateSecurityDataIpsec_noEntitiesCreated() throws Exception {
        when(securityDataCollector.isNodeEntityCreated(CertificateType.OAM, MSRBS_NODE_FDN)).thenReturn(false);
        when(securityDataCollector.isNodeEntityCreated(CertificateType.IPSEC, MSRBS_NODE_FDN)).thenReturn(false);
        when(securityDataCollector.getSecurityDataResponse(eq(EnumSet.of(CertificateType.IPSEC)), eq(MSRBS_NODE_FDN), anyString(), any(SubjectAltNameParam.class), eq(EnrollmentMode.SCEP),
                any(NodeModelInformation.class))).thenReturn(new SecurityDataResponse(new ArrayList(), new ArrayList()));
        beanUnderTest.generateSecurityDataIpsec(nodeId, null, EnrollmentMode.SCEP, MSRBS_MODEL_INFO);
        verify(securityDataCollector).getSecurityDataResponse(EnumSet.of(CertificateType.IPSEC), MSRBS_NODE_FDN, null, null, EnrollmentMode.SCEP, MSRBS_MODEL_INFO);
    }

    @Test
    public void testGenerateSecurityDataIpsec_entityOamCreated() throws Exception {
        when(securityDataCollector.isNodeEntityCreated(CertificateType.OAM, MSRBS_NODE_FDN)).thenReturn(true);
        when(securityDataCollector.isNodeEntityCreated(CertificateType.IPSEC, MSRBS_NODE_FDN)).thenReturn(false);
        beanUnderTest.generateSecurityDataIpsec(nodeId, null, EnrollmentMode.SCEP, MSRBS_MODEL_INFO);
        verify(securityDataCollector).getSecurityDataResponse(EnumSet.of(CertificateType.IPSEC), MSRBS_NODE_FDN, null, null, EnrollmentMode.SCEP, MSRBS_MODEL_INFO);
    }

    @Test
    public void testGenerateSecurityDataIpsec_entityIpsecCreated() throws Exception {
        when(securityDataCollector.isNodeEntityCreated(CertificateType.OAM, MSRBS_NODE_FDN)).thenReturn(false);
        when(securityDataCollector.isNodeEntityCreated(CertificateType.IPSEC, MSRBS_NODE_FDN)).thenReturn(true);
        when(securityDataCollector.getSecurityDataResponse(eq(EnumSet.of(CertificateType.IPSEC)), eq(MSRBS_NODE_FDN), anyString(), any(SubjectAltNameParam.class), eq(EnrollmentMode.SCEP),
                any(NodeModelInformation.class))).thenReturn(new SecurityDataResponse(new ArrayList(), new ArrayList()));
        beanUnderTest.generateSecurityDataIpsec(nodeId, null, EnrollmentMode.SCEP, MSRBS_MODEL_INFO);
        verify(securityDataCollector).getSecurityDataResponse(EnumSet.of(CertificateType.IPSEC), MSRBS_NODE_FDN, null, null, EnrollmentMode.SCEP, MSRBS_MODEL_INFO);
    }

    @Test
    public void testGenerateSecurityDataIpsec_bothEntitiesCreated() throws Exception {
        when(securityDataCollector.isNodeEntityCreated(CertificateType.OAM, MSRBS_NODE_FDN)).thenReturn(true);
        when(securityDataCollector.isNodeEntityCreated(CertificateType.IPSEC, MSRBS_NODE_FDN)).thenReturn(true);
        beanUnderTest.generateSecurityDataIpsec(nodeId, null, EnrollmentMode.SCEP, MSRBS_MODEL_INFO);
        verify(securityDataCollector).getSecurityDataResponse(EnumSet.of(CertificateType.IPSEC), MSRBS_NODE_FDN, null, null, EnrollmentMode.SCEP, MSRBS_MODEL_INFO);
    }

    @Test
    public void testGenerateSecurityDataCombo_entityOamCreated() throws Exception {
        when(securityDataCollector.isNodeEntityCreated(CertificateType.OAM, MSRBS_NODE_FDN)).thenReturn(true);
        when(securityDataCollector.isNodeEntityCreated(CertificateType.IPSEC, MSRBS_NODE_FDN)).thenReturn(false);
        beanUnderTest.generateSecurityDataCombo(nodeId, null, EnrollmentMode.SCEP, MSRBS_MODEL_INFO);
        verify(securityDataCollector).getSecurityDataResponse(EnumSet.allOf(CertificateType.class), MSRBS_NODE_FDN, null, null, EnrollmentMode.SCEP, MSRBS_MODEL_INFO);
    }

    @Test
    public void testGenerateSecurityDataCombo_entityIpsecCreated() throws Exception {
        when(securityDataCollector.isNodeEntityCreated(CertificateType.OAM, MSRBS_NODE_FDN)).thenReturn(false);
        when(securityDataCollector.isNodeEntityCreated(CertificateType.IPSEC, MSRBS_NODE_FDN)).thenReturn(true);
        beanUnderTest.generateSecurityDataCombo(nodeId, null, EnrollmentMode.SCEP, MSRBS_MODEL_INFO);
        verify(securityDataCollector).getSecurityDataResponse(EnumSet.allOf(CertificateType.class), MSRBS_NODE_FDN, null, null, EnrollmentMode.SCEP, MSRBS_MODEL_INFO);
    }

    @Test
    public void testGenerateSecurityDataCombo_noEntitiesCreated() throws Exception {
        when(securityDataCollector.isNodeEntityCreated(CertificateType.OAM, MSRBS_NODE_FDN)).thenReturn(false);
        when(securityDataCollector.isNodeEntityCreated(CertificateType.IPSEC, MSRBS_NODE_FDN)).thenReturn(false);
        when(securityDataCollector.getSecurityDataResponse(eq(EnumSet.allOf(CertificateType.class)), eq(MSRBS_NODE_FDN), anyString(), any(SubjectAltNameParam.class), eq(EnrollmentMode.SCEP),
                any(NodeModelInformation.class))).thenReturn(new SecurityDataResponse(new ArrayList(), new ArrayList()));
        beanUnderTest.generateSecurityDataCombo(nodeId, null, EnrollmentMode.SCEP, MSRBS_MODEL_INFO);
        verify(securityDataCollector).getSecurityDataResponse(EnumSet.allOf(CertificateType.class), MSRBS_NODE_FDN, null, null, EnrollmentMode.SCEP, MSRBS_MODEL_INFO);
    }

    @Test
    public void testGenerateSecurityDataCombo_bothEntitiesCreated() throws Exception {
        when(securityDataCollector.isNodeEntityCreated(CertificateType.OAM, MSRBS_NODE_FDN)).thenReturn(true);
        when(securityDataCollector.isNodeEntityCreated(CertificateType.IPSEC, MSRBS_NODE_FDN)).thenReturn(true);
        when(securityDataCollector.getSecurityDataResponse(eq(EnumSet.allOf(CertificateType.class)), eq(MSRBS_NODE_FDN), anyString(), any(SubjectAltNameParam.class), eq(EnrollmentMode.SCEP),
                any(NodeModelInformation.class))).thenReturn(new SecurityDataResponse(new ArrayList(), new ArrayList()));
        beanUnderTest.generateSecurityDataCombo(nodeId, null, EnrollmentMode.SCEP, MSRBS_MODEL_INFO);
        verify(securityDataCollector).getSecurityDataResponse(EnumSet.allOf(CertificateType.class), MSRBS_NODE_FDN, null, null, EnrollmentMode.SCEP, MSRBS_MODEL_INFO);
    }

}
