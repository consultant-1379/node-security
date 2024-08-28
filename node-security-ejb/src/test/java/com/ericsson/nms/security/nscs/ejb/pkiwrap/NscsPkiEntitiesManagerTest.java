/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.nms.security.nscs.ejb.pkiwrap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation.ModelIdentifierType;
import com.ericsson.nms.security.nscs.api.pki.EnrollmentPartialInfos;
import com.ericsson.nms.security.nscs.api.pki.NodeEntityCategory;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerException;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerIF;
import com.ericsson.nms.security.nscs.api.util.NscsPair;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.ejb.pkiwrap.cache.PkiCachedCallsImpl;
import com.ericsson.nms.security.nscs.pki.NscsPkiEntitiesManagerJar;
import com.ericsson.nms.security.nscs.pki.NscsPkiUtils;
import com.ericsson.nms.security.nscs.pki.PkiApiManagers;
import com.ericsson.oss.itpf.security.pki.common.model.Algorithm;
import com.ericsson.oss.itpf.security.pki.common.model.CertificateAuthority;
import com.ericsson.oss.itpf.security.pki.common.model.EntityInfo;
import com.ericsson.oss.itpf.security.pki.common.model.EntityStatus;
import com.ericsson.oss.itpf.security.pki.common.model.Subject;
import com.ericsson.oss.itpf.security.pki.common.model.SubjectField;
import com.ericsson.oss.itpf.security.pki.common.model.SubjectFieldType;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.CertificateStatus;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltName;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameField;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameFieldType;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameString;
import com.ericsson.oss.itpf.security.pki.manager.certificatemanagement.api.CACertificateManagementService;
import com.ericsson.oss.itpf.security.pki.manager.configurationmanagement.api.PKIConfigurationManagementService;
import com.ericsson.oss.itpf.security.pki.manager.exception.configuration.PKIConfigurationServiceException;
import com.ericsson.oss.itpf.security.pki.manager.exception.configuration.algorithm.AlgorithmNotFoundException;
import com.ericsson.oss.itpf.security.pki.manager.exception.crl.CRLExtensionException;
import com.ericsson.oss.itpf.security.pki.manager.exception.crl.CRLGenerationException;
import com.ericsson.oss.itpf.security.pki.manager.exception.crl.InvalidCRLGenerationInfoException;
import com.ericsson.oss.itpf.security.pki.manager.exception.crl.UnsupportedCRLVersionException;
import com.ericsson.oss.itpf.security.pki.manager.exception.enrollment.EnrollmentURLNotFoundException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.EntityAlreadyDeletedException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.EntityAlreadyExistsException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.EntityInUseException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.EntityNotFoundException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.EntityServiceException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.InvalidEntityAttributeException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.InvalidEntityException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.category.EntityCategoryNotFoundException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.category.InvalidEntityCategoryException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.endentity.otp.OTPExpiredException;
import com.ericsson.oss.itpf.security.pki.manager.exception.profile.InvalidProfileAttributeException;
import com.ericsson.oss.itpf.security.pki.manager.exception.profile.InvalidProfileException;
import com.ericsson.oss.itpf.security.pki.manager.exception.profile.ProfileNotFoundException;
import com.ericsson.oss.itpf.security.pki.manager.exception.profile.ProfileServiceException;
import com.ericsson.oss.itpf.security.pki.manager.exception.security.certificate.certificatefield.InvalidSubjectException;
import com.ericsson.oss.itpf.security.pki.manager.exception.security.certificate.certificatefield.MissingMandatoryFieldException;
import com.ericsson.oss.itpf.security.pki.manager.exception.security.certificate.certificatefield.certificateextension.InvalidSubjectAltNameExtension;
import com.ericsson.oss.itpf.security.pki.manager.exception.trustdistributionpoint.TrustDistributionPointURLNotFoundException;
import com.ericsson.oss.itpf.security.pki.manager.model.EnrollmentType;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityCategory;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityEnrollmentInfo;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityType;
import com.ericsson.oss.itpf.security.pki.manager.model.ProfileType;
import com.ericsson.oss.itpf.security.pki.manager.model.TrustCAChain;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.CAEntity;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.itpf.security.pki.manager.model.profiles.CertificateProfile;
import com.ericsson.oss.itpf.security.pki.manager.model.profiles.EntityProfile;
import com.ericsson.oss.itpf.security.pki.manager.model.profiles.TrustProfile;
import com.ericsson.oss.itpf.security.pki.manager.profilemanagement.api.EntityManagementService;
import com.ericsson.oss.itpf.security.pki.manager.profilemanagement.api.ProfileManagementService;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

/**
 *
 * @author enmadmin
 */
@RunWith(MockitoJUnitRunner.class)
public class NscsPkiEntitiesManagerTest {

    @Spy
    private final Logger log = LoggerFactory.getLogger(NscsPkiEntitiesManagerIF.class);

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Mock
    EntityManagementService pkiEntManager;

    @Mock
    ProfileManagementService pkiProfManager;

    @Mock
    CACertificateManagementService pkiCACertManager;

    @Mock
    NscsCapabilityModelService capabilityModel;

    @Mock
    PKIConfigurationManagementService pkiConfigManager;

    @Mock
    private NscsContextService ctxService;

    @InjectMocks
    PkiCachedCallsImpl pkiCachedCalls;

    @InjectMocks
    PkiApiManagers pkiApiManagers;

    @InjectMocks
    NscsPkiEntitiesManagerJar nscsPkiEntitiesManagerJar;

    @InjectMocks
    NscsPkiEntitiesManagerRiscBean nscsPkiEntitiesManagerRisc;

    @InjectMocks
    NscsPkiEntitiesManager nscsPkiEntitiesManager;

    @Mock
    EntityProfile entityProfile;

    @Mock
    private NscsCMReaderService readerService;

    @Mock
    NormalizableNodeReference normNodeRef;

    private static final String nodeFdn = "node1";
    private static final String nodeSerialNumber = "C123456";
    private static final String ipsecProfileName = "MicroRBSIPSec_SAN_CHAIN_EP";
    private static final String oamProfileName = "MicroRBSOAM_CHAIN_EP";
    private static final String entityCategoryOam = "NODE-OAM";
    private static final String entityCategoryIpsec = "NODE-IPSEC";
    private static final String subjectCountry = "IT";
    private static final String subjectOrg = "Marconi";
    private static final String subjectOu = "DST7420";
    private static final NodeModelInformation CPP_13B_MODEL_INFO = new NodeModelInformation("4.1.189", ModelIdentifierType.MIM_VERSION, "ERBS");
    private static final NodeModelInformation CPP_15B_MODEL_INFO = new NodeModelInformation("5.1.63", ModelIdentifierType.MIM_VERSION, "ERBS");
    private static final String MSRBSV1_OSS_MODEL_IDENTITY = "397-5538-555";
    private static final NodeModelInformation PICO_MODEL_INFO = new NodeModelInformation(MSRBSV1_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, "MSRBS_V1");
    private static final NodeModelInformation RADIO_MODEL_INFO = new NodeModelInformation("397-5538-366", ModelIdentifierType.OSS_IDENTIFIER,
            "RadioNode");
    private static final SubjectAltNameStringType subjectAltNameString = new SubjectAltNameStringType("12.13.14.15");
    private static final int INITIAL_OTP_COUNT = 5;
    private static final int VIRTUAL_OTP_COUNT = 2;
    private static final Integer OTP_VALIDITY = 0;
    Map<Object, Object> enrollmentEntityInfo =  new HashMap<>();
    
    @Before
    public void setUp() {
        enrollmentEntityInfo.put("NodeName", nodeFdn);
        enrollmentEntityInfo.put("CommonName", null);
        enrollmentEntityInfo.put("EnrollmentMode", EnrollmentMode.SCEP);
        enrollmentEntityInfo.put("EntityProfileName", oamProfileName);
        enrollmentEntityInfo.put("SubjectAltName", null);
        enrollmentEntityInfo.put("SubjectAltNameFormat", null);
        enrollmentEntityInfo.put("AlgorithmKeys", null);
        enrollmentEntityInfo.put("EntityCategory", NodeEntityCategory.OAM);
        enrollmentEntityInfo.put("ModelInfo", CPP_15B_MODEL_INFO);
    }
    private EntityEnrollmentInfo buildEntityEnrollmentInfo(final NodeEntityCategory entityCategory) {
        final EntityEnrollmentInfo eei = new EntityEnrollmentInfo();
        eei.setEntity(buildEntity(entityCategory));
        eei.setEnrollmentInfo(null);
        return eei;
    }

    private Entity buildEntity(final NodeEntityCategory entityCategory) {
        final EntityInfo entityInfo = new EntityInfo();
        String entNodeFdn = nodeFdn;
        if (entityCategory != null) {
            entNodeFdn += ("-" + entityCategory.toString());
        }
        entityInfo.setName(entNodeFdn);
        entityInfo.setSubject(buildSubject(entNodeFdn));
        entityInfo.setOTP("1q2w3e4r5t6y76u");
        final Entity entity = new Entity();
        entity.setEntityInfo(entityInfo);
        if ((entityCategory != null) && (entityCategory.equals(NodeEntityCategory.IPSEC))) {
            entity.setCategory(buildEntityCategory(entityCategoryIpsec));
            entityInfo.setSubjectAltName(buildSubjectAltName());
            entity.setEntityProfile(buildEntityProfile(NodeEntityCategory.IPSEC));
        } else {
            entity.setEntityProfile(buildEntityProfile(NodeEntityCategory.OAM));
            entity.setCategory(buildEntityCategory(entityCategoryOam));
        }
        return entity;
    }

    private Subject buildSubject(final String commonName) {
        final Subject subject = new Subject();
        final List<SubjectField> entSubjectFieldList = new ArrayList<>();

        final SubjectField subjectFieldCountry = new SubjectField();
        subjectFieldCountry.setType(SubjectFieldType.COUNTRY_NAME);
        subjectFieldCountry.setValue(subjectCountry);

        final SubjectField subjectFieldOrg = new SubjectField();
        subjectFieldOrg.setType(SubjectFieldType.ORGANIZATION);
        subjectFieldOrg.setValue(subjectOrg);

        final SubjectField subjectFieldOu = new SubjectField();
        subjectFieldOu.setType(SubjectFieldType.ORGANIZATION_UNIT);
        subjectFieldOu.setValue(subjectOu);

        if (commonName != null) {
            final SubjectField subjectFieldCn = new SubjectField();
            subjectFieldCn.setType(SubjectFieldType.COMMON_NAME);
            subjectFieldCn.setValue(commonName);
            entSubjectFieldList.add(subjectFieldCn);
        }

        entSubjectFieldList.add(subjectFieldCountry);
        entSubjectFieldList.add(subjectFieldOrg);
        entSubjectFieldList.add(subjectFieldOu);
        subject.setSubjectFields(entSubjectFieldList);

        return subject;
    }

    private SubjectAltName buildSubjectAltName() {

        final SubjectAltNameField subjectAltNameField = new SubjectAltNameField();
        subjectAltNameField.setType(SubjectAltNameFieldType.IP_ADDRESS);

        final SubjectAltNameString subjectAltNameValueString = new SubjectAltNameString();
        subjectAltNameValueString.setValue("22.23.24.25");
        subjectAltNameField.setValue(subjectAltNameValueString);
        final List<SubjectAltNameField> subjectAltNameValueList = new ArrayList<>();
        subjectAltNameValueList.add(subjectAltNameField);

        final SubjectAltName subjectAltNameValues = new SubjectAltName();
        subjectAltNameValues.setSubjectAltNameFields(subjectAltNameValueList);

        return subjectAltNameValues;
    }

    private EntityProfile buildEntityProfile(final NodeEntityCategory entityCategory) {
        final EntityProfile ep = new EntityProfile();

        ep.setActive(true);
        ep.setSubject(buildSubject(null));

        final TrustProfile tp = new TrustProfile();
        final List<TrustProfile> tpList = new ArrayList<>();
        final TrustCAChain tcac = new TrustCAChain();
        final CAEntity internalCA = new CAEntity();
        final CertificateAuthority certificateAuthority = new CertificateAuthority();
        certificateAuthority.setName("MyInternalCA");
        internalCA.setCertificateAuthority(certificateAuthority);
        tcac.setChainRequired(false);
        tcac.setInternalCA(internalCA);
        final List<TrustCAChain> tcacList = new ArrayList<>();
        tcacList.add(tcac);
        tp.setTrustCAChains(tcacList);
        tpList.add(tp);
        ep.setTrustProfiles(tpList);
        if (entityCategory.equals(NodeEntityCategory.IPSEC)) {
            ep.setSubjectAltNameExtension(buildSubjectAltName());
            ep.setCategory(buildEntityCategory(entityCategoryIpsec));
            ep.setName(ipsecProfileName);
        } else {
            ep.setCategory(buildEntityCategory(entityCategoryOam));
            ep.setName(oamProfileName);
        }
        ep.setCertificateProfile(buildCertificateProfile());
        return ep;
    }

    private CertificateProfile buildCertificateProfile() {
        CertificateProfile cp = new CertificateProfile();
        List<Algorithm> keyGenAlgos = new ArrayList<Algorithm>();
        Algorithm al1 = new Algorithm();
        al1.setName("RSA");
        al1.setKeySize(2048);
        Algorithm al2 = new Algorithm();
        al2.setName("RSA");
        al2.setKeySize(1024);
        keyGenAlgos.add(al1);
        keyGenAlgos.add(al2);
        cp.setKeyGenerationAlgorithms(keyGenAlgos);
        return cp;
    }

    private EntityCategory buildEntityCategory(final String categoryName) {
        final EntityCategory entCat = new EntityCategory();
        entCat.setName(categoryName);
        entCat.setId(1);
        entCat.setModifiable(false);
        return entCat;
    }

    @Before
    public void setup() {
        pkiCachedCalls.initialize();
        try {
            final Field pkiApiManagersField = NscsPkiEntitiesManagerJar.class.getDeclaredField("pkiApiManagers");
            pkiApiManagersField.setAccessible(true);
            pkiApiManagersField.set(nscsPkiEntitiesManagerJar, pkiApiManagers);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            final Field nscsPkiEntitiesManagerJarField = NscsPkiEntitiesManagerRiscBean.class.getDeclaredField("nscsPkiEntitiesManagerJar");
            nscsPkiEntitiesManagerJarField.setAccessible(true);
            nscsPkiEntitiesManagerJarField.set(nscsPkiEntitiesManagerRisc, nscsPkiEntitiesManagerJar);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            final Field nscsPkiEntitiesManagerJarField = PkiCachedCallsImpl.class.getDeclaredField("nscsPkiEntitiesManagerJar");
            nscsPkiEntitiesManagerJarField.setAccessible(true);
            nscsPkiEntitiesManagerJarField.set(pkiCachedCalls, nscsPkiEntitiesManagerJar);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            final Field pkiCachedCallsField = NscsPkiEntitiesManagerRiscBean.class.getDeclaredField("pkiCachedCalls");
            pkiCachedCallsField.setAccessible(true);
            pkiCachedCallsField.set(nscsPkiEntitiesManagerRisc, pkiCachedCalls);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            final Field nscsPkiEntitiesManagerRiscField = NscsPkiEntitiesManager.class.getDeclaredField("nscsPkiEntitiesManagerRisc");
            nscsPkiEntitiesManagerRiscField.setAccessible(true);
            nscsPkiEntitiesManagerRiscField.set(nscsPkiEntitiesManager, nscsPkiEntitiesManagerRisc);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void testGetEnrollmentEntityInfo_oam_createEntity()
            throws NscsPkiEntitiesManagerException, EntityServiceException, InvalidEntityException, InvalidSubjectAltNameExtension,
            InvalidSubjectException, MissingMandatoryFieldException, AlgorithmNotFoundException, EntityCategoryNotFoundException,
            InvalidEntityCategoryException, CRLExtensionException, CRLGenerationException, EntityAlreadyExistsException, EntityNotFoundException,
            InvalidCRLGenerationInfoException, InvalidEntityAttributeException, InvalidProfileException, ProfileNotFoundException,
            UnsupportedCRLVersionException, PKIConfigurationServiceException, InvalidProfileAttributeException, ProfileServiceException {
        final EntityEnrollmentInfo eei = buildEntityEnrollmentInfo(NodeEntityCategory.OAM);
        final ArgumentCaptor<Entity> entityCaptor = ArgumentCaptor.forClass(Entity.class);
        final ArgumentCaptor<EnrollmentType> enrollTypeCaptor = ArgumentCaptor.forClass(EnrollmentType.class);
        final EntityProfile expectedEp = buildEntityProfile(NodeEntityCategory.OAM);
        when(pkiEntManager.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(true);
        when(pkiProfManager.getProfile(any(EntityProfile.class))).thenReturn(expectedEp);
        when(pkiEntManager.createEntityAndGetEnrollmentInfo_v1(any(Entity.class), eq(EnrollmentType.scep))).thenReturn(eei);
        when(pkiConfigManager.getCategory(any(EntityCategory.class))).thenReturn(buildEntityCategory(entityCategoryOam));
        when(capabilityModel.isConfiguredSubjectNameUsedForEnrollment(CPP_15B_MODEL_INFO)).thenReturn(true);
        when(capabilityModel.getDefaultInitialOtpCount(CPP_15B_MODEL_INFO)).thenReturn(Integer.toString(INITIAL_OTP_COUNT));
        when(capabilityModel.getDefaultOtpValidityPeriodInMinutes(CPP_15B_MODEL_INFO)).thenReturn(Integer.toString(OTP_VALIDITY));
        Map<Object, Object> enrollmentEntityInfo =  new HashMap<>();
        enrollmentEntityInfo.put("NodeName", nodeFdn);
        enrollmentEntityInfo.put("CommonName", null);
        enrollmentEntityInfo.put("EnrollmentMode", EnrollmentMode.SCEP);
        enrollmentEntityInfo.put("EntityProfileName", oamProfileName);
        enrollmentEntityInfo.put("SubjectAltName", null);
        enrollmentEntityInfo.put("SubjectAltNameFormat", null);
        enrollmentEntityInfo.put("AlgorithmKeys", AlgorithmKeys.RSA_2048);
        enrollmentEntityInfo.put("EntityCategory", NodeEntityCategory.OAM);
        enrollmentEntityInfo.put("ModelInfo", CPP_15B_MODEL_INFO);
        final EnrollmentPartialInfos epi = nscsPkiEntitiesManager.getEnrollmentEntityInfo(enrollmentEntityInfo);

        verify(pkiEntManager).createEntityAndGetEnrollmentInfo_v1(entityCaptor.capture(), enrollTypeCaptor.capture());
        assertEquals(enrollTypeCaptor.getValue(), EnrollmentType.scep);
        final Entity capturedEnt = entityCaptor.getValue();
        assertNotNull(capturedEnt);
        assertEquals(eei.getEntity().getEntityInfo().getName(), capturedEnt.getEntityInfo().getName());
        assertEquals(buildSubject(eei.getEntity().getEntityInfo().getName()), capturedEnt.getEntityInfo().getSubject());
        assertEquals(INITIAL_OTP_COUNT, capturedEnt.getEntityInfo().getOTPCount());
        assertEquals(OTP_VALIDITY, capturedEnt.getOtpValidityPeriod());
        assertEquals(expectedEp, capturedEnt.getEntityProfile());
        assertEquals(buildEntityCategory(entityCategoryOam), capturedEnt.getCategory());
        assertNull(capturedEnt.getEntityInfo().getSubjectAltName());

        assertEquals(eei.getEntity(), epi.getEndEntity());
    }

    @Test
    public void testGetEnrollmentEntityInfo_ipsec_createEntity()
            throws NscsPkiEntitiesManagerException, EntityServiceException, InvalidEntityException, InvalidSubjectAltNameExtension,
            InvalidSubjectException, MissingMandatoryFieldException, AlgorithmNotFoundException, EntityCategoryNotFoundException,
            InvalidEntityCategoryException, CRLExtensionException, CRLGenerationException, EntityAlreadyExistsException, EntityNotFoundException,
            InvalidCRLGenerationInfoException, InvalidEntityAttributeException, InvalidProfileException, ProfileNotFoundException,
            UnsupportedCRLVersionException, PKIConfigurationServiceException, InvalidProfileAttributeException, ProfileServiceException {
        final EntityEnrollmentInfo eei = buildEntityEnrollmentInfo(NodeEntityCategory.IPSEC);
        final ArgumentCaptor<Entity> entityCaptor = ArgumentCaptor.forClass(Entity.class);
        final ArgumentCaptor<EnrollmentType> enrollTypeCaptor = ArgumentCaptor.forClass(EnrollmentType.class);
        final EntityProfile expectedEp = buildEntityProfile(NodeEntityCategory.IPSEC);

        when(pkiEntManager.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(true);

        when(pkiProfManager.getProfile(any(EntityProfile.class))).thenReturn(expectedEp);
        when(pkiEntManager.createEntityAndGetEnrollmentInfo_v1(any(Entity.class), eq(EnrollmentType.scep))).thenReturn(eei);
        when(pkiConfigManager.getCategory(any(EntityCategory.class))).thenReturn(buildEntityCategory(entityCategoryIpsec));
        when(capabilityModel.isConfiguredSubjectNameUsedForEnrollment(CPP_15B_MODEL_INFO)).thenReturn(true);
        when(capabilityModel.getDefaultInitialOtpCount(CPP_15B_MODEL_INFO)).thenReturn(Integer.toString(INITIAL_OTP_COUNT));
        when(capabilityModel.getDefaultOtpValidityPeriodInMinutes(CPP_15B_MODEL_INFO)).thenReturn(Integer.toString(OTP_VALIDITY));

        Map<Object, Object> enrollmentEntityInfo =  new HashMap<>();
        enrollmentEntityInfo.put("NodeName", nodeFdn);
        enrollmentEntityInfo.put("CommonName", null);
        enrollmentEntityInfo.put("EnrollmentMode", EnrollmentMode.SCEP);
        enrollmentEntityInfo.put("EntityProfileName", ipsecProfileName);
        enrollmentEntityInfo.put("SubjectAltName", subjectAltNameString);
        enrollmentEntityInfo.put("SubjectAltNameFormat", SubjectAltNameFormat.IPV4);
        enrollmentEntityInfo.put("AlgorithmKeys", AlgorithmKeys.RSA_2048);
        enrollmentEntityInfo.put("EntityCategory", NodeEntityCategory.IPSEC);
        enrollmentEntityInfo.put("ModelInfo", CPP_15B_MODEL_INFO);
        final EnrollmentPartialInfos epi = nscsPkiEntitiesManager.getEnrollmentEntityInfo(enrollmentEntityInfo);

        verify(pkiEntManager).createEntityAndGetEnrollmentInfo_v1(entityCaptor.capture(), enrollTypeCaptor.capture());
        assertEquals(enrollTypeCaptor.getValue(), EnrollmentType.scep);
        final Entity capturedEnt = entityCaptor.getValue();
        assertNotNull(capturedEnt);
        assertEquals(eei.getEntity().getEntityInfo().getName(), capturedEnt.getEntityInfo().getName());
        assertEquals(buildSubject(eei.getEntity().getEntityInfo().getName()), capturedEnt.getEntityInfo().getSubject());
        assertEquals(INITIAL_OTP_COUNT, capturedEnt.getEntityInfo().getOTPCount());
        assertEquals(OTP_VALIDITY, capturedEnt.getOtpValidityPeriod());
        assertEquals(expectedEp, capturedEnt.getEntityProfile());
        assertEquals(buildEntityCategory(entityCategoryIpsec), capturedEnt.getCategory());
        assertNotNull(capturedEnt.getEntityInfo().getSubjectAltName());
        assertEquals(eei.getEntity(), epi.getEndEntity());
    }

    @Test
    public void testGetEnrollmentEntityInfo_oam_createEntity_nullKeys_model13B()
            throws NscsPkiEntitiesManagerException, EntityServiceException, InvalidEntityException, InvalidSubjectAltNameExtension,
            InvalidSubjectException, MissingMandatoryFieldException, AlgorithmNotFoundException, EntityCategoryNotFoundException,
            InvalidEntityCategoryException, CRLExtensionException, CRLGenerationException, EntityAlreadyExistsException, EntityNotFoundException,
            InvalidCRLGenerationInfoException, InvalidEntityAttributeException, InvalidProfileException, ProfileNotFoundException,
            UnsupportedCRLVersionException, PKIConfigurationServiceException, InvalidProfileAttributeException, ProfileServiceException {
        final EntityEnrollmentInfo eei = buildEntityEnrollmentInfo(NodeEntityCategory.OAM);
        final ArgumentCaptor<Entity> entityCaptor = ArgumentCaptor.forClass(Entity.class);

        when(pkiEntManager.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(true);

        when(pkiProfManager.getProfile(any(EntityProfile.class))).thenReturn(buildEntityProfile(NodeEntityCategory.OAM));
        when(pkiEntManager.createEntityAndGetEnrollmentInfo_v1(any(Entity.class), eq(EnrollmentType.scep))).thenReturn(eei);
        when(pkiConfigManager.getCategory(any(EntityCategory.class))).thenReturn(buildEntityCategory(entityCategoryOam));
        when(capabilityModel.getDefaultAlgorithmKeys(CPP_13B_MODEL_INFO)).thenReturn(AlgorithmKeys.RSA_1024);
        when(capabilityModel.getDefaultInitialOtpCount(CPP_13B_MODEL_INFO)).thenReturn(Integer.toString(INITIAL_OTP_COUNT));
        when(capabilityModel.getDefaultOtpValidityPeriodInMinutes(CPP_13B_MODEL_INFO)).thenReturn(Integer.toString(OTP_VALIDITY));

        Map<Object, Object> enrollmentEntityInfo =  new HashMap<>();
        enrollmentEntityInfo.put("NodeName", nodeFdn);
        enrollmentEntityInfo.put("CommonName", null);
        enrollmentEntityInfo.put("EnrollmentMode", EnrollmentMode.SCEP);
        enrollmentEntityInfo.put("EntityProfileName", oamProfileName);
        enrollmentEntityInfo.put("SubjectAltName", null);
        enrollmentEntityInfo.put("SubjectAltNameFormat", null);
        enrollmentEntityInfo.put("AlgorithmKeys", null);
        enrollmentEntityInfo.put("EntityCategory", NodeEntityCategory.OAM);
        enrollmentEntityInfo.put("ModelInfo", CPP_13B_MODEL_INFO);
        final EnrollmentPartialInfos epi = nscsPkiEntitiesManager.getEnrollmentEntityInfo(enrollmentEntityInfo);

        verify(pkiEntManager).createEntityAndGetEnrollmentInfo_v1(entityCaptor.capture(), eq(EnrollmentType.scep));
        assertEquals(AlgorithmKeys.RSA_1024, epi.getKeySize());

        final Entity capturedEnt = entityCaptor.getValue();
        assertNotNull(capturedEnt);
        assertEquals(eei.getEntity(), epi.getEndEntity());
    }

    @Test
    public void testGetEnrollmentEntityInfo_oam_createEntity_nullKeys_model15B()
            throws NscsPkiEntitiesManagerException, EntityServiceException, InvalidEntityException, InvalidSubjectAltNameExtension,
            InvalidSubjectException, MissingMandatoryFieldException, AlgorithmNotFoundException, EntityCategoryNotFoundException,
            InvalidEntityCategoryException, CRLExtensionException, CRLGenerationException, EntityAlreadyExistsException, EntityNotFoundException,
            InvalidCRLGenerationInfoException, InvalidEntityAttributeException, InvalidProfileException, ProfileNotFoundException,
            UnsupportedCRLVersionException, PKIConfigurationServiceException, InvalidProfileAttributeException, ProfileServiceException {
        final EntityEnrollmentInfo eei = buildEntityEnrollmentInfo(NodeEntityCategory.OAM);
        final ArgumentCaptor<Entity> entityCaptor = ArgumentCaptor.forClass(Entity.class);

        when(pkiEntManager.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(true);
        when(pkiProfManager.getProfile(any(EntityProfile.class))).thenReturn(buildEntityProfile(NodeEntityCategory.OAM));
        when(pkiEntManager.createEntityAndGetEnrollmentInfo_v1(any(Entity.class), eq(EnrollmentType.scep))).thenReturn(eei);
        when(pkiConfigManager.getCategory(any(EntityCategory.class))).thenReturn(buildEntityCategory(entityCategoryOam));
        when(capabilityModel.getDefaultAlgorithmKeys(CPP_15B_MODEL_INFO)).thenReturn(AlgorithmKeys.RSA_2048);
        when(capabilityModel.isConfiguredSubjectNameUsedForEnrollment(CPP_15B_MODEL_INFO)).thenReturn(true);
        when(capabilityModel.getDefaultInitialOtpCount(CPP_15B_MODEL_INFO)).thenReturn(Integer.toString(INITIAL_OTP_COUNT));
        when(capabilityModel.getDefaultOtpValidityPeriodInMinutes(CPP_15B_MODEL_INFO)).thenReturn(Integer.toString(OTP_VALIDITY));

        Map<Object, Object> enrollmentEntityInfo =  new HashMap<>();
        enrollmentEntityInfo.put("NodeName", nodeFdn);
        enrollmentEntityInfo.put("CommonName", null);
        enrollmentEntityInfo.put("EnrollmentMode", EnrollmentMode.SCEP);
        enrollmentEntityInfo.put("EntityProfileName", oamProfileName);
        enrollmentEntityInfo.put("SubjectAltName", null);
        enrollmentEntityInfo.put("SubjectAltNameFormat", null);
        enrollmentEntityInfo.put("AlgorithmKeys", null);
        enrollmentEntityInfo.put("EntityCategory", NodeEntityCategory.OAM);
        enrollmentEntityInfo.put("ModelInfo", CPP_15B_MODEL_INFO);
        final EnrollmentPartialInfos epi = nscsPkiEntitiesManager.getEnrollmentEntityInfo(enrollmentEntityInfo);

        verify(pkiEntManager).createEntityAndGetEnrollmentInfo_v1(entityCaptor.capture(), eq(EnrollmentType.scep));
        assertEquals(AlgorithmKeys.RSA_2048, epi.getKeySize());

        final Entity capturedEnt = entityCaptor.getValue();
        assertNotNull(capturedEnt);
        assertEquals(eei.getEntity(), epi.getEndEntity());
    }

    @Test
    public void testGetEnrollmentEntityInfo_createEntity_nullProfileName()
            throws NscsPkiEntitiesManagerException, EntityServiceException, InvalidEntityException, InvalidSubjectAltNameExtension,
            InvalidSubjectException, MissingMandatoryFieldException, AlgorithmNotFoundException, EntityCategoryNotFoundException,
            InvalidEntityCategoryException, CRLExtensionException, CRLGenerationException, EntityAlreadyExistsException, EntityNotFoundException,
            InvalidCRLGenerationInfoException, InvalidEntityAttributeException, InvalidProfileException, ProfileNotFoundException,
            UnsupportedCRLVersionException, PKIConfigurationServiceException, InvalidProfileAttributeException, ProfileServiceException {
        final EntityEnrollmentInfo eei = buildEntityEnrollmentInfo(NodeEntityCategory.IPSEC);
        final ArgumentCaptor<Entity> entityCaptor = ArgumentCaptor.forClass(Entity.class);
        final EntityProfile expectedEp = buildEntityProfile(NodeEntityCategory.IPSEC);

        when(pkiEntManager.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(true);
        when(capabilityModel.getDefaultEntityProfile(CPP_15B_MODEL_INFO, NodeEntityCategory.IPSEC)).thenReturn(ipsecProfileName);
        when(pkiProfManager.getProfile(any(EntityProfile.class))).thenReturn(expectedEp);
        when(pkiEntManager.createEntityAndGetEnrollmentInfo_v1(any(Entity.class), eq(EnrollmentType.scep))).thenReturn(eei);
        when(pkiConfigManager.getCategory(any(EntityCategory.class))).thenReturn(buildEntityCategory(entityCategoryIpsec));
        when(capabilityModel.isConfiguredSubjectNameUsedForEnrollment(CPP_15B_MODEL_INFO)).thenReturn(true);
        when(capabilityModel.getDefaultInitialOtpCount(CPP_15B_MODEL_INFO)).thenReturn(Integer.toString(INITIAL_OTP_COUNT));
        when(capabilityModel.getDefaultOtpValidityPeriodInMinutes(CPP_15B_MODEL_INFO)).thenReturn(Integer.toString(OTP_VALIDITY));

        enrollmentEntityInfo.put("NodeName", nodeFdn);
        enrollmentEntityInfo.put("CommonName", null);
        enrollmentEntityInfo.put("EnrollmentMode", EnrollmentMode.SCEP);
        enrollmentEntityInfo.put("EntityProfileName", null);
        enrollmentEntityInfo.put("SubjectAltName", subjectAltNameString);
        enrollmentEntityInfo.put("SubjectAltNameFormat", SubjectAltNameFormat.IPV4);
        enrollmentEntityInfo.put("AlgorithmKeys", AlgorithmKeys.RSA_2048);
        enrollmentEntityInfo.put("EntityCategory", NodeEntityCategory.IPSEC);
        enrollmentEntityInfo.put("ModelInfo", CPP_15B_MODEL_INFO);
        final EnrollmentPartialInfos epi = nscsPkiEntitiesManager.getEnrollmentEntityInfo(enrollmentEntityInfo);

        verify(pkiEntManager).createEntityAndGetEnrollmentInfo_v1(entityCaptor.capture(), eq(EnrollmentType.scep));
        verify(capabilityModel).getDefaultEntityProfile(CPP_15B_MODEL_INFO, NodeEntityCategory.IPSEC);
        final Entity capturedEnt = entityCaptor.getValue();
        assertNotNull(capturedEnt);
        assertEquals(INITIAL_OTP_COUNT, capturedEnt.getEntityInfo().getOTPCount());
        assertEquals(OTP_VALIDITY, capturedEnt.getOtpValidityPeriod());
        assertEquals(expectedEp, capturedEnt.getEntityProfile());
        assertEquals(buildEntityCategory(entityCategoryIpsec), capturedEnt.getCategory());
        assertEquals(eei.getEntity(), epi.getEndEntity());
    }

    @Test(expected = NscsPkiEntitiesManagerException.class)
    public void testGetEnrollmentEntityInfo_createEntity_wrongProfileName()
            throws NscsPkiEntitiesManagerException, EntityServiceException, InvalidEntityException, InvalidProfileException, ProfileServiceException {
        when(pkiEntManager.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(true);
        when(pkiProfManager.isProfileNameAvailable(anyString(), eq(ProfileType.ENTITY_PROFILE))).thenReturn(true);

        enrollmentEntityInfo.put("EntityProfileName", null);
        enrollmentEntityInfo.put("SubjectAltName", subjectAltNameString);
        enrollmentEntityInfo.put("SubjectAltNameFormat", SubjectAltNameFormat.IPV4);
        enrollmentEntityInfo.put("AlgorithmKeys", AlgorithmKeys.RSA_2048);
        enrollmentEntityInfo.put("EntityCategory", NodeEntityCategory.IPSEC);
        enrollmentEntityInfo.put("ModelInfo", CPP_15B_MODEL_INFO);
        System.out.println(enrollmentEntityInfo);
        final EnrollmentPartialInfos epi = nscsPkiEntitiesManager.getEnrollmentEntityInfo(enrollmentEntityInfo);
    }

    @Test
    public void testGetEnrollmentEntityInfo_createEntity_wrongProfileCategory()
            throws NscsPkiEntitiesManagerException, EntityServiceException, InvalidEntityException, InvalidProfileException, ProfileServiceException,
            MissingMandatoryFieldException, InvalidProfileAttributeException, ProfileNotFoundException {

        when(pkiEntManager.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(true);
        when(pkiProfManager.isProfileNameAvailable(ipsecProfileName, ProfileType.ENTITY_PROFILE)).thenReturn(false);
        when(pkiProfManager.getProfile(any(EntityProfile.class))).thenReturn(buildEntityProfile(NodeEntityCategory.IPSEC));
        exception.expect(NscsPkiEntitiesManagerException.class);
        when(capabilityModel.getDefaultInitialOtpCount(CPP_15B_MODEL_INFO)).thenReturn(Integer.toString(INITIAL_OTP_COUNT));
        when(capabilityModel.getDefaultOtpValidityPeriodInMinutes(CPP_15B_MODEL_INFO)).thenReturn(Integer.toString(OTP_VALIDITY));

        enrollmentEntityInfo.put("AlgorithmKeys", AlgorithmKeys.RSA_2048);
        enrollmentEntityInfo.put("EntityCategory", NodeEntityCategory.OAM);
        enrollmentEntityInfo.put("ModelInfo", CPP_15B_MODEL_INFO);
        final EnrollmentPartialInfos epi = nscsPkiEntitiesManager.getEnrollmentEntityInfo(enrollmentEntityInfo);
    }

    @Test
    public void testGetEnrollmentEntityInfo_createEntity_wrongEnrollmentMode()
            throws NscsPkiEntitiesManagerException, EntityServiceException, InvalidEntityException {
        when(pkiEntManager.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(true);
        exception.expect(NscsPkiEntitiesManagerException.class);

        enrollmentEntityInfo.put("EnrollmentMode", EnrollmentMode.OFFLINE_PKCS12);
        enrollmentEntityInfo.put("EntityProfileName", null);
        enrollmentEntityInfo.put("SubjectAltName", subjectAltNameString);
        enrollmentEntityInfo.put("SubjectAltNameFormat", SubjectAltNameFormat.IPV4);
        enrollmentEntityInfo.put("AlgorithmKeys", AlgorithmKeys.RSA_2048);
        enrollmentEntityInfo.put("EntityCategory", NodeEntityCategory.IPSEC);
        enrollmentEntityInfo.put("ModelInfo", CPP_15B_MODEL_INFO);
        final EnrollmentPartialInfos epi = nscsPkiEntitiesManager.getEnrollmentEntityInfo(enrollmentEntityInfo);
    }

    @Test
    public void testGetEnrollmentEntityInfo_ipsec_createEntity_null_subjectAltName()
            throws NscsPkiEntitiesManagerException, EntityServiceException, InvalidEntityException, InvalidProfileException, ProfileServiceException,
            MissingMandatoryFieldException, InvalidProfileAttributeException, ProfileNotFoundException, InvalidSubjectAltNameExtension,
            InvalidSubjectException, AlgorithmNotFoundException, EntityCategoryNotFoundException, InvalidEntityCategoryException,
            CRLExtensionException, CRLGenerationException, EntityAlreadyExistsException, EntityNotFoundException, InvalidCRLGenerationInfoException,
            InvalidEntityAttributeException, UnsupportedCRLVersionException, PKIConfigurationServiceException {
        final EntityEnrollmentInfo eei = buildEntityEnrollmentInfo(NodeEntityCategory.IPSEC);
        final EntityProfile entityProfile = buildEntityProfile(NodeEntityCategory.IPSEC);

        when(pkiEntManager.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(true);
        when(pkiProfManager.isProfileNameAvailable(ipsecProfileName, ProfileType.ENTITY_PROFILE)).thenReturn(false);
        when(pkiProfManager.getProfile(any(EntityProfile.class))).thenReturn(entityProfile);
        when(pkiEntManager.createEntityAndGetEnrollmentInfo_v1(any(Entity.class), eq(EnrollmentType.scep))).thenReturn(eei);
        when(pkiConfigManager.getCategory(any(EntityCategory.class))).thenReturn(buildEntityCategory(entityCategoryIpsec));
        when(capabilityModel.getDefaultInitialOtpCount(CPP_15B_MODEL_INFO)).thenReturn(Integer.toString(INITIAL_OTP_COUNT));
        when(capabilityModel.getDefaultOtpValidityPeriodInMinutes(CPP_15B_MODEL_INFO)).thenReturn(Integer.toString(OTP_VALIDITY));

        enrollmentEntityInfo.put("EntityProfileName", ipsecProfileName);
        enrollmentEntityInfo.put("SubjectAltNameFormat", SubjectAltNameFormat.IPV4);
        enrollmentEntityInfo.put("AlgorithmKeys", AlgorithmKeys.RSA_2048);
        enrollmentEntityInfo.put("EntityCategory", NodeEntityCategory.IPSEC);
        final EnrollmentPartialInfos epi = nscsPkiEntitiesManager.getEnrollmentEntityInfo(enrollmentEntityInfo);

        final Entity ee = epi.getEndEntity();
        assertNotNull(ee);
        final SubjectAltName subjectAltName = ee.getEntityInfo().getSubjectAltName();
        assertNotNull(subjectAltName);
        assertEquals(1, subjectAltName.getSubjectAltNameFields().size());
        assertEquals(SubjectAltNameFieldType.IP_ADDRESS, subjectAltName.getSubjectAltNameFields().get(0).getType());

        assertEquals(entityProfile.getSubjectAltNameExtension().getSubjectAltNameFields().get(0).getValue(),
                subjectAltName.getSubjectAltNameFields().get(0).getValue());
    }

    @Test
    public void testGetEnrollmentEntityInfo_oam_createEntity_subjectAltName()
            throws NscsPkiEntitiesManagerException, EntityServiceException, InvalidEntityException, InvalidSubjectAltNameExtension,
            InvalidSubjectException, MissingMandatoryFieldException, AlgorithmNotFoundException, EntityCategoryNotFoundException,
            InvalidEntityCategoryException, CRLExtensionException, CRLGenerationException, EntityAlreadyExistsException, EntityNotFoundException,
            InvalidCRLGenerationInfoException, InvalidEntityAttributeException, InvalidProfileException, ProfileNotFoundException,
            UnsupportedCRLVersionException, PKIConfigurationServiceException, InvalidProfileAttributeException, ProfileServiceException {
        final EntityEnrollmentInfo eei = buildEntityEnrollmentInfo(NodeEntityCategory.OAM);
        final ArgumentCaptor<Entity> entityCaptor = ArgumentCaptor.forClass(Entity.class);

        when(pkiEntManager.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(true);
        when(pkiProfManager.getProfile(any(EntityProfile.class))).thenReturn(buildEntityProfile(NodeEntityCategory.OAM));
        when(pkiEntManager.createEntityAndGetEnrollmentInfo_v1(any(Entity.class), eq(EnrollmentType.scep))).thenReturn(eei);
        when(pkiConfigManager.getCategory(any(EntityCategory.class))).thenReturn(buildEntityCategory(entityCategoryOam));
        when(capabilityModel.isConfiguredSubjectNameUsedForEnrollment(CPP_15B_MODEL_INFO)).thenReturn(true);
        when(capabilityModel.getDefaultInitialOtpCount(CPP_15B_MODEL_INFO)).thenReturn(Integer.toString(INITIAL_OTP_COUNT));
        when(capabilityModel.getDefaultOtpValidityPeriodInMinutes(CPP_15B_MODEL_INFO)).thenReturn(Integer.toString(OTP_VALIDITY));

        enrollmentEntityInfo.put("EntityProfileName", ipsecProfileName);
        enrollmentEntityInfo.put("SubjectAltName", subjectAltNameString);
        enrollmentEntityInfo.put("SubjectAltNameFormat", SubjectAltNameFormat.IPV4);
        enrollmentEntityInfo.put("AlgorithmKeys", AlgorithmKeys.RSA_2048);
        final EnrollmentPartialInfos epi = nscsPkiEntitiesManager.getEnrollmentEntityInfo(enrollmentEntityInfo);

        verify(pkiEntManager).createEntityAndGetEnrollmentInfo_v1(entityCaptor.capture(), eq(EnrollmentType.scep));
        final Entity capturedEnt = entityCaptor.getValue();
        assertNotNull(capturedEnt);
        assertNull(capturedEnt.getEntityInfo().getSubjectAltName());
        assertEquals(buildEntityCategory(entityCategoryOam), capturedEnt.getCategory());
        assertEquals(eei.getEntity(), epi.getEndEntity());
    }

    @Test
    public void testGetEnrollmentEntityInfo_createEntity_nullCategory() throws NscsPkiEntitiesManagerException, EntityServiceException,
            InvalidEntityException, InvalidProfileException, ProfileServiceException, MissingMandatoryFieldException,
            InvalidProfileAttributeException, ProfileNotFoundException, EntityCategoryNotFoundException, PKIConfigurationServiceException {
        when(pkiEntManager.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(true);
        when(pkiProfManager.isProfileNameAvailable(ipsecProfileName, ProfileType.ENTITY_PROFILE)).thenReturn(false);
        when(pkiProfManager.getProfile(any(EntityProfile.class))).thenReturn(buildEntityProfile(NodeEntityCategory.IPSEC));
        when(pkiConfigManager.getCategory(any(EntityCategory.class))).thenThrow(new EntityCategoryNotFoundException());
        when(capabilityModel.getDefaultInitialOtpCount(CPP_15B_MODEL_INFO)).thenReturn(Integer.toString(INITIAL_OTP_COUNT));
        when(capabilityModel.getDefaultOtpValidityPeriodInMinutes(CPP_15B_MODEL_INFO)).thenReturn(Integer.toString(OTP_VALIDITY));
        when(readerService.getNormalizableNodeReference(any())).thenReturn(normNodeRef);
        when(capabilityModel.getDefaultOtpValidityPeriodInMinutes(CPP_15B_MODEL_INFO)).thenReturn("60");
        try {
            enrollmentEntityInfo.put("EntityProfileName", ipsecProfileName);
            enrollmentEntityInfo.put("SubjectAltName", null);
            enrollmentEntityInfo.put("SubjectAltNameFormat", null);
            enrollmentEntityInfo.put("AlgorithmKeys", AlgorithmKeys.RSA_2048);
            enrollmentEntityInfo.put("EntityCategory", NodeEntityCategory.IPSEC);
            enrollmentEntityInfo.put("ModelInfo", CPP_15B_MODEL_INFO);
            final EnrollmentPartialInfos epi = nscsPkiEntitiesManager.getEnrollmentEntityInfo(enrollmentEntityInfo);
        } catch (final NscsPkiEntitiesManagerException e) {
            assertTrue(e.getMessage().contains("EntityCategoryNotFoundException"));
        }
    }

    @Test
    @Ignore
    public void testGetEnrollmentEntityInfo_createEntity_rollback()
            throws NscsPkiEntitiesManagerException, EntityServiceException, InvalidEntityException, InvalidProfileException, ProfileServiceException,
            MissingMandatoryFieldException, InvalidProfileAttributeException, ProfileNotFoundException, InvalidSubjectAltNameExtension,
            InvalidSubjectException, AlgorithmNotFoundException, EntityCategoryNotFoundException, InvalidEntityCategoryException,
            CRLExtensionException, CRLGenerationException, EntityAlreadyExistsException, InvalidCRLGenerationInfoException,
            InvalidEntityAttributeException, UnsupportedCRLVersionException, OTPExpiredException, EntityNotFoundException,
            EnrollmentURLNotFoundException, TrustDistributionPointURLNotFoundException, EntityAlreadyDeletedException, EntityInUseException {
        final Entity ent = buildEntity(NodeEntityCategory.IPSEC);
        final ArgumentCaptor<Entity> entityCaptor = ArgumentCaptor.forClass(Entity.class);

        when(pkiEntManager.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(true).thenReturn(false);
        when(pkiProfManager.isProfileNameAvailable(ipsecProfileName, ProfileType.ENTITY_PROFILE)).thenReturn(false);
        when(pkiProfManager.getProfile(any(EntityProfile.class))).thenReturn(buildEntityProfile(NodeEntityCategory.IPSEC));
        when(pkiEntManager.createEntity(any(Entity.class))).thenReturn(ent);
        when(pkiCachedCalls.getPkiEntityCategory(any(NodeEntityCategory.class))).thenReturn(buildEntityCategory(entityCategoryIpsec));
        when(pkiEntManager.getEnrollmentInfo(eq(EnrollmentType.scep), any(Entity.class))).thenThrow(EntityServiceException.class);
        when(capabilityModel.isConfiguredSubjectNameUsedForEnrollment(CPP_15B_MODEL_INFO)).thenReturn(true);

        try {
            enrollmentEntityInfo.put("EntityProfileName", ipsecProfileName);
            enrollmentEntityInfo.put("SubjectAltName", subjectAltNameString);
            enrollmentEntityInfo.put("SubjectAltNameFormat", SubjectAltNameFormat.IPV4);
            enrollmentEntityInfo.put("AlgorithmKeys", AlgorithmKeys.RSA_2048);
            enrollmentEntityInfo.put("EntityCategory", NodeEntityCategory.IPSEC);
            enrollmentEntityInfo.put("ModelInfo", CPP_15B_MODEL_INFO);
            final EnrollmentPartialInfos epi = nscsPkiEntitiesManager.getEnrollmentEntityInfo(enrollmentEntityInfo);
        } catch (final NscsPkiEntitiesManagerException e) {
        }

        verify(pkiEntManager).createEntity(entityCaptor.capture());
        final Entity createdEnt = entityCaptor.getValue();
        assertNotNull(createdEnt);

        verify(pkiEntManager).getEnrollmentInfo(EnrollmentType.scep, ent);
        // Verify rollback of entity creation
        verify(pkiEntManager).deleteEntity(entityCaptor.capture());
        final Entity deletedEnt = entityCaptor.getValue();
        assertNotNull(deletedEnt);
        assertEquals(createdEnt.getEntityInfo().getName(), deletedEnt.getEntityInfo().getName());
    }

    @Test
    @Ignore
    public void testGetEnrollmentEntityInfo_createEntity_rollback_exception()
            throws NscsPkiEntitiesManagerException, EntityServiceException, InvalidEntityException, InvalidProfileException, ProfileServiceException,
            MissingMandatoryFieldException, InvalidProfileAttributeException, ProfileNotFoundException, InvalidSubjectAltNameExtension,
            InvalidSubjectException, AlgorithmNotFoundException, EntityCategoryNotFoundException, InvalidEntityCategoryException,
            CRLExtensionException, CRLGenerationException, EntityAlreadyExistsException, InvalidCRLGenerationInfoException,
            InvalidEntityAttributeException, UnsupportedCRLVersionException, OTPExpiredException, EntityNotFoundException,
            EnrollmentURLNotFoundException, TrustDistributionPointURLNotFoundException {
        final Entity ent = buildEntity(NodeEntityCategory.IPSEC);
        when(pkiEntManager.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(true);
        when(pkiProfManager.isProfileNameAvailable(ipsecProfileName, ProfileType.ENTITY_PROFILE)).thenReturn(false);
        when(pkiProfManager.getProfile(any(EntityProfile.class))).thenReturn(buildEntityProfile(NodeEntityCategory.IPSEC));
        when(pkiEntManager.createEntity(any(Entity.class))).thenReturn(ent);
        when(pkiCachedCalls.getPkiEntityCategory(any(NodeEntityCategory.class))).thenReturn(buildEntityCategory(entityCategoryIpsec));
        when(pkiEntManager.getEnrollmentInfo(eq(EnrollmentType.scep), any(Entity.class))).thenThrow(EntityServiceException.class);
        exception.expect(NscsPkiEntitiesManagerException.class);

        enrollmentEntityInfo.put("EntityProfileName", ipsecProfileName);
        enrollmentEntityInfo.put("SubjectAltName", subjectAltNameString);
        enrollmentEntityInfo.put("SubjectAltNameFormat", SubjectAltNameFormat.IPV4);
        enrollmentEntityInfo.put("AlgorithmKeys", AlgorithmKeys.RSA_2048);
        enrollmentEntityInfo.put("EntityCategory", NodeEntityCategory.IPSEC);
        enrollmentEntityInfo.put("ModelInfo", CPP_15B_MODEL_INFO);
        final EnrollmentPartialInfos epi = nscsPkiEntitiesManager.getEnrollmentEntityInfo(enrollmentEntityInfo);
    }

    @Test
    public void testGetEnrollmentEntityInfo_updateEntity()
            throws NscsPkiEntitiesManagerException, EntityServiceException, InvalidEntityException, InvalidProfileException, ProfileServiceException,
            MissingMandatoryFieldException, InvalidProfileAttributeException, ProfileNotFoundException, EntityNotFoundException,
            InvalidEntityAttributeException, InvalidSubjectAltNameExtension, InvalidSubjectException, AlgorithmNotFoundException,
            EntityCategoryNotFoundException, InvalidEntityCategoryException, CRLExtensionException, CRLGenerationException,
            EntityAlreadyExistsException, InvalidCRLGenerationInfoException, UnsupportedCRLVersionException, PKIConfigurationServiceException {

        final EntityEnrollmentInfo eei = buildEntityEnrollmentInfo(NodeEntityCategory.IPSEC);

        when(pkiEntManager.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(false);
        when(pkiProfManager.isProfileNameAvailable(ipsecProfileName, ProfileType.ENTITY_PROFILE)).thenReturn(false);
        when(pkiProfManager.getProfile(any(EntityProfile.class))).thenReturn(buildEntityProfile(NodeEntityCategory.IPSEC));
        when(pkiEntManager.getEntity(any(Entity.class))).thenReturn(eei.getEntity());
        when(pkiConfigManager.getCategory(any(EntityCategory.class))).thenReturn(buildEntityCategory(entityCategoryIpsec));
        when(pkiEntManager.updateEntityAndGetEnrollmentInfo_v1(any(Entity.class), eq(EnrollmentType.scep))).thenReturn(eei);
        when(capabilityModel.getDefaultInitialOtpCount(CPP_15B_MODEL_INFO)).thenReturn(Integer.toString(INITIAL_OTP_COUNT));
        when(capabilityModel.getDefaultOtpValidityPeriodInMinutes(CPP_15B_MODEL_INFO)).thenReturn(Integer.toString(OTP_VALIDITY));
        enrollmentEntityInfo.put("EntityProfileName", ipsecProfileName);
        enrollmentEntityInfo.put("SubjectAltName", subjectAltNameString);
        enrollmentEntityInfo.put("SubjectAltNameFormat", SubjectAltNameFormat.IPV4);
        enrollmentEntityInfo.put("AlgorithmKeys", AlgorithmKeys.RSA_2048);
        enrollmentEntityInfo.put("EntityCategory", NodeEntityCategory.IPSEC);
        enrollmentEntityInfo.put("ModelInfo", CPP_15B_MODEL_INFO);
        final EnrollmentPartialInfos epi = nscsPkiEntitiesManager.getEnrollmentEntityInfo(enrollmentEntityInfo);

        verify(pkiEntManager).updateEntityAndGetEnrollmentInfo_v1(any(Entity.class), eq(EnrollmentType.scep));
        assertEquals(eei.getEntity(), epi.getEndEntity());
    }

    @Test
    @Ignore
    public void testGetEnrollmentEntityInfo_updateEntity_rollback()
            throws NscsPkiEntitiesManagerException, EntityServiceException, InvalidEntityException, InvalidProfileException, ProfileServiceException,
            MissingMandatoryFieldException, InvalidProfileAttributeException, ProfileNotFoundException, EntityNotFoundException,
            InvalidEntityAttributeException, InvalidSubjectAltNameExtension, InvalidSubjectException, AlgorithmNotFoundException,
            EntityCategoryNotFoundException, InvalidEntityCategoryException, CRLExtensionException, CRLGenerationException,
            EntityAlreadyExistsException, InvalidCRLGenerationInfoException, UnsupportedCRLVersionException, OTPExpiredException,
            EnrollmentURLNotFoundException, TrustDistributionPointURLNotFoundException {
        final Entity ent = buildEntity(NodeEntityCategory.IPSEC);

        when(pkiEntManager.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(false);
        when(pkiProfManager.isProfileNameAvailable(ipsecProfileName, ProfileType.ENTITY_PROFILE)).thenReturn(false);
        when(pkiProfManager.getProfile(any(EntityProfile.class))).thenReturn(buildEntityProfile(NodeEntityCategory.IPSEC));
        when(pkiEntManager.getEntity(any(Entity.class))).thenReturn(ent);
        when(pkiEntManager.updateEntity_v1(any(Entity.class))).thenReturn(ent);
        when(pkiCachedCalls.getPkiEntityCategory(any(NodeEntityCategory.class))).thenReturn(buildEntityCategory(entityCategoryIpsec));
        when(pkiEntManager.getEnrollmentInfo(eq(EnrollmentType.scep), any(Entity.class))).thenThrow(EntityServiceException.class);

        try {
            enrollmentEntityInfo.put("EntityProfileName", ipsecProfileName);
            enrollmentEntityInfo.put("SubjectAltName", subjectAltNameString);
            enrollmentEntityInfo.put("SubjectAltNameFormat", SubjectAltNameFormat.IPV4);
            enrollmentEntityInfo.put("AlgorithmKeys", AlgorithmKeys.RSA_2048);
            enrollmentEntityInfo.put("EntityCategory", NodeEntityCategory.IPSEC);
            enrollmentEntityInfo.put("ModelInfo", CPP_15B_MODEL_INFO);
            final EnrollmentPartialInfos epi = nscsPkiEntitiesManager.getEnrollmentEntityInfo(enrollmentEntityInfo);
        } catch (final NscsPkiEntitiesManagerException e) {
        }

        verify(pkiEntManager).getEnrollmentInfo(EnrollmentType.scep, ent);
        verify(pkiEntManager, times(2)).updateEntity_v1(any(Entity.class));
    }

    @Test
    public void testGetEnrollmentEntityInfo_oam_picoRbs()
            throws NscsPkiEntitiesManagerException, EntityServiceException, InvalidEntityException, InvalidSubjectAltNameExtension,
            InvalidSubjectException, MissingMandatoryFieldException, AlgorithmNotFoundException, EntityCategoryNotFoundException,
            InvalidEntityCategoryException, CRLExtensionException, CRLGenerationException, EntityAlreadyExistsException, EntityNotFoundException,
            InvalidCRLGenerationInfoException, InvalidEntityAttributeException, InvalidProfileException, ProfileNotFoundException,
            UnsupportedCRLVersionException, PKIConfigurationServiceException, InvalidProfileAttributeException, ProfileServiceException {
        final EntityEnrollmentInfo eei = buildEntityEnrollmentInfo(NodeEntityCategory.OAM);
        final ArgumentCaptor<Entity> entityCaptor = ArgumentCaptor.forClass(Entity.class);
        final ArgumentCaptor<EnrollmentType> enrollTypeCaptor = ArgumentCaptor.forClass(EnrollmentType.class);
        final EntityProfile expectedEp = buildEntityProfile(NodeEntityCategory.OAM);

        when(pkiEntManager.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(true);
        when(pkiProfManager.getProfile(any(EntityProfile.class))).thenReturn(expectedEp);
        when(pkiEntManager.createEntityAndGetEnrollmentInfo_v1(any(Entity.class), eq(EnrollmentType.scep))).thenReturn(eei);
        when(pkiConfigManager.getCategory(any(EntityCategory.class))).thenReturn(buildEntityCategory(entityCategoryOam));
        when(capabilityModel.isConfiguredSubjectNameUsedForEnrollment(PICO_MODEL_INFO)).thenReturn(false);
        when(capabilityModel.getDefaultInitialOtpCount(PICO_MODEL_INFO)).thenReturn(Integer.toString(VIRTUAL_OTP_COUNT));
        when(capabilityModel.getDefaultOtpValidityPeriodInMinutes(PICO_MODEL_INFO)).thenReturn(Integer.toString(OTP_VALIDITY));

        enrollmentEntityInfo.put("AlgorithmKeys", AlgorithmKeys.RSA_2048);
        enrollmentEntityInfo.put("EntityCategory", NodeEntityCategory.OAM);
        enrollmentEntityInfo.put("ModelInfo", PICO_MODEL_INFO);
        final EnrollmentPartialInfos epi = nscsPkiEntitiesManager.getEnrollmentEntityInfo(enrollmentEntityInfo);

        verify(pkiEntManager).createEntityAndGetEnrollmentInfo_v1(entityCaptor.capture(), enrollTypeCaptor.capture());
        assertEquals(enrollTypeCaptor.getValue(), EnrollmentType.scep);
        final Entity capturedEnt = entityCaptor.getValue();
        assertNotNull(capturedEnt);
        final SubjectAltName capturedSan = capturedEnt.getEntityInfo().getSubjectAltName();
        assertEquals(eei.getEntity().getEntityInfo().getName(), capturedEnt.getEntityInfo().getName());
        assertEquals(buildSubject(eei.getEntity().getEntityInfo().getName()), capturedEnt.getEntityInfo().getSubject());
        assertEquals(VIRTUAL_OTP_COUNT, capturedEnt.getEntityInfo().getOTPCount());
        assertEquals(OTP_VALIDITY, capturedEnt.getOtpValidityPeriod());
        assertEquals(expectedEp, capturedEnt.getEntityProfile());
        assertEquals(buildEntityCategory(entityCategoryOam), capturedEnt.getCategory());
        assertNull(capturedSan);
    }

    @Test
    public void testGetEnrollmentEntityInfo_oam_picoRbs_nodeSerialNumber()
            throws NscsPkiEntitiesManagerException, EntityServiceException, InvalidEntityException, InvalidSubjectAltNameExtension,
            InvalidSubjectException, MissingMandatoryFieldException, AlgorithmNotFoundException, EntityCategoryNotFoundException,
            InvalidEntityCategoryException, CRLExtensionException, CRLGenerationException, EntityAlreadyExistsException, EntityNotFoundException,
            InvalidCRLGenerationInfoException, InvalidEntityAttributeException, InvalidProfileException, ProfileNotFoundException,
            UnsupportedCRLVersionException, PKIConfigurationServiceException, InvalidProfileAttributeException, ProfileServiceException {
        final EntityEnrollmentInfo eei = buildEntityEnrollmentInfo(NodeEntityCategory.OAM);
        final ArgumentCaptor<Entity> entityCaptor = ArgumentCaptor.forClass(Entity.class);
        final ArgumentCaptor<EnrollmentType> enrollTypeCaptor = ArgumentCaptor.forClass(EnrollmentType.class);
        final EntityProfile expectedEp = buildEntityProfile(NodeEntityCategory.OAM);

        when(pkiEntManager.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(true);
        when(pkiProfManager.getProfile(any(EntityProfile.class))).thenReturn(expectedEp);
        when(pkiEntManager.createEntityAndGetEnrollmentInfo_v1(any(Entity.class), eq(EnrollmentType.cmp))).thenReturn(eei);
        when(pkiConfigManager.getCategory(any(EntityCategory.class))).thenReturn(buildEntityCategory(entityCategoryOam));
        when(capabilityModel.isConfiguredSubjectNameUsedForEnrollment(PICO_MODEL_INFO)).thenReturn(false);
        when(capabilityModel.getDefaultInitialOtpCount(PICO_MODEL_INFO)).thenReturn(Integer.toString(VIRTUAL_OTP_COUNT));
        when(capabilityModel.getDefaultOtpValidityPeriodInMinutes(PICO_MODEL_INFO)).thenReturn(Integer.toString(OTP_VALIDITY));

        enrollmentEntityInfo.put("NodeName", nodeFdn);
        enrollmentEntityInfo.put("CommonName", nodeSerialNumber);
        enrollmentEntityInfo.put("EnrollmentMode", EnrollmentMode.CMPv2_VC);
        enrollmentEntityInfo.put("EntityProfileName", oamProfileName);
        enrollmentEntityInfo.put("SubjectAltName", null);
        enrollmentEntityInfo.put("SubjectAltNameFormat", null);
        enrollmentEntityInfo.put("AlgorithmKeys", AlgorithmKeys.RSA_2048);
        enrollmentEntityInfo.put("EntityCategory", NodeEntityCategory.OAM);
        enrollmentEntityInfo.put("ModelInfo", PICO_MODEL_INFO);
        final EnrollmentPartialInfos epi = nscsPkiEntitiesManager.getEnrollmentEntityInfo(enrollmentEntityInfo);

        verify(pkiEntManager).createEntityAndGetEnrollmentInfo_v1(entityCaptor.capture(), enrollTypeCaptor.capture());
        assertEquals(enrollTypeCaptor.getValue(), EnrollmentType.cmp);
        final Entity capturedEnt = entityCaptor.getValue();
        assertNotNull(capturedEnt);
        final SubjectAltName capturedSan = capturedEnt.getEntityInfo().getSubjectAltName();
        assertEquals(eei.getEntity().getEntityInfo().getName(), capturedEnt.getEntityInfo().getName());
        assertEquals(buildSubject(nodeSerialNumber + "." + subjectOrg + "." + subjectCountry), capturedEnt.getEntityInfo().getSubject());

        assertEquals(VIRTUAL_OTP_COUNT, capturedEnt.getEntityInfo().getOTPCount());
        assertEquals(OTP_VALIDITY, capturedEnt.getOtpValidityPeriod());
        assertEquals(expectedEp, capturedEnt.getEntityProfile());
        assertEquals(buildEntityCategory(entityCategoryOam), capturedEnt.getCategory());
        assertNotNull(capturedSan);
        assertEquals(capturedSan.getSubjectAltNameFields().get(0).getType(), SubjectAltNameFieldType.DNS_NAME);
        assertEquals(((SubjectAltNameString) (capturedSan.getSubjectAltNameFields().get(0).getValue())).getValue(),
                nodeSerialNumber + "." + subjectOrg + "." + subjectCountry);
    }

    @Test
    public void testGetEnrollmentEntityInfo_oam_picoRbs_nodeCommonName()
            throws NscsPkiEntitiesManagerException, EntityServiceException, InvalidEntityException, InvalidSubjectAltNameExtension,
            InvalidSubjectException, MissingMandatoryFieldException, AlgorithmNotFoundException, EntityCategoryNotFoundException,
            InvalidEntityCategoryException, CRLExtensionException, CRLGenerationException, EntityAlreadyExistsException, EntityNotFoundException,
            InvalidCRLGenerationInfoException, InvalidEntityAttributeException, InvalidProfileException, ProfileNotFoundException,
            UnsupportedCRLVersionException, PKIConfigurationServiceException, InvalidProfileAttributeException, ProfileServiceException {

        final EntityEnrollmentInfo eei = buildEntityEnrollmentInfo(NodeEntityCategory.OAM);
        final ArgumentCaptor<Entity> entityCaptor = ArgumentCaptor.forClass(Entity.class);
        final ArgumentCaptor<EnrollmentType> enrollTypeCaptor = ArgumentCaptor.forClass(EnrollmentType.class);
        final EntityProfile expectedEp = buildEntityProfile(NodeEntityCategory.OAM);

        when(pkiEntManager.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(true);
        when(pkiProfManager.getProfile(any(EntityProfile.class))).thenReturn(expectedEp);
        when(pkiEntManager.createEntityAndGetEnrollmentInfo_v1(any(Entity.class), eq(EnrollmentType.cmp))).thenReturn(eei);
        when(pkiConfigManager.getCategory(any(EntityCategory.class))).thenReturn(buildEntityCategory(entityCategoryOam));
        when(capabilityModel.isConfiguredSubjectNameUsedForEnrollment(PICO_MODEL_INFO)).thenReturn(false);
        when(capabilityModel.getDefaultInitialOtpCount(PICO_MODEL_INFO)).thenReturn(Integer.toString(VIRTUAL_OTP_COUNT));
        when(capabilityModel.getDefaultOtpValidityPeriodInMinutes(PICO_MODEL_INFO)).thenReturn(Integer.toString(OTP_VALIDITY));

        enrollmentEntityInfo.put("NodeName", nodeFdn);
        enrollmentEntityInfo.put("CommonName", (nodeSerialNumber + "." + subjectOrg + "." + subjectCountry));
        enrollmentEntityInfo.put("EnrollmentMode",  EnrollmentMode.CMPv2_VC);
        enrollmentEntityInfo.put("EntityProfileName", oamProfileName);
        enrollmentEntityInfo.put("SubjectAltName", null);
        enrollmentEntityInfo.put("SubjectAltNameFormat", null);
        enrollmentEntityInfo.put("AlgorithmKeys", AlgorithmKeys.RSA_2048);
        enrollmentEntityInfo.put("EntityCategory", NodeEntityCategory.OAM);
        enrollmentEntityInfo.put("ModelInfo", PICO_MODEL_INFO);
        final EnrollmentPartialInfos epi = nscsPkiEntitiesManager.getEnrollmentEntityInfo(enrollmentEntityInfo);

        verify(pkiEntManager).createEntityAndGetEnrollmentInfo_v1(entityCaptor.capture(), enrollTypeCaptor.capture());
        assertEquals(enrollTypeCaptor.getValue(), EnrollmentType.cmp);
        final Entity capturedEnt = entityCaptor.getValue();
        assertNotNull(capturedEnt);
        assertEquals(eei.getEntity().getEntityInfo().getName(), capturedEnt.getEntityInfo().getName());
        assertEquals(buildSubject(nodeSerialNumber + "." + subjectOrg + "." + subjectCountry), capturedEnt.getEntityInfo().getSubject());
        final SubjectAltName capturedSan = capturedEnt.getEntityInfo().getSubjectAltName();
        assertEquals(VIRTUAL_OTP_COUNT, capturedEnt.getEntityInfo().getOTPCount());
        assertEquals(OTP_VALIDITY, capturedEnt.getOtpValidityPeriod());
        assertEquals(expectedEp, capturedEnt.getEntityProfile());
        assertEquals(buildEntityCategory(entityCategoryOam), capturedEnt.getCategory());
        assertNotNull(capturedSan);
        assertEquals(capturedSan.getSubjectAltNameFields().get(0).getType(), SubjectAltNameFieldType.DNS_NAME);

        assertEquals(((SubjectAltNameString) (capturedSan.getSubjectAltNameFields().get(0).getValue())).getValue(),
                nodeSerialNumber + "." + subjectOrg + "." + subjectCountry);
    }

    @Test
    public void testGetEnrollmentEntityInfo_oam_nodeCommonNameFailingWithSCEP()
            throws NscsPkiEntitiesManagerException, EntityServiceException, InvalidEntityException, InvalidSubjectAltNameExtension,
            InvalidSubjectException, MissingMandatoryFieldException, AlgorithmNotFoundException, EntityCategoryNotFoundException,
            InvalidEntityCategoryException, CRLExtensionException, CRLGenerationException, EntityAlreadyExistsException, EntityNotFoundException,
            InvalidCRLGenerationInfoException, InvalidEntityAttributeException, InvalidProfileException, ProfileNotFoundException,
            UnsupportedCRLVersionException, PKIConfigurationServiceException, InvalidProfileAttributeException, ProfileServiceException {
        final EntityEnrollmentInfo eei = buildEntityEnrollmentInfo(NodeEntityCategory.OAM);
        final ArgumentCaptor<Entity> entityCaptor = ArgumentCaptor.forClass(Entity.class);
        final ArgumentCaptor<EnrollmentType> enrollTypeCaptor = ArgumentCaptor.forClass(EnrollmentType.class);
        final EntityProfile expectedEp = buildEntityProfile(NodeEntityCategory.OAM);

        when(pkiEntManager.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(true);
        when(pkiProfManager.getProfile(any(EntityProfile.class))).thenReturn(expectedEp);
        when(pkiEntManager.createEntityAndGetEnrollmentInfo(any(Entity.class), eq(EnrollmentType.scep))).thenReturn(eei);
        when(pkiConfigManager.getCategory(any(EntityCategory.class))).thenReturn(buildEntityCategory(entityCategoryOam));
        when(capabilityModel.isConfiguredSubjectNameUsedForEnrollment(RADIO_MODEL_INFO)).thenReturn(false);
        when(capabilityModel.getDefaultInitialOtpCount(RADIO_MODEL_INFO)).thenReturn(Integer.toString(INITIAL_OTP_COUNT));
        when(capabilityModel.getDefaultOtpValidityPeriodInMinutes(RADIO_MODEL_INFO)).thenReturn(Integer.toString(OTP_VALIDITY));

        try {
            enrollmentEntityInfo.put("NodeName", nodeFdn);
            enrollmentEntityInfo.put("CommonName", (nodeSerialNumber + "." + subjectOrg + "." + subjectCountry));
            enrollmentEntityInfo.put("EnrollmentMode", EnrollmentMode.SCEP);
            enrollmentEntityInfo.put("EntityProfileName", oamProfileName);
            enrollmentEntityInfo.put("SubjectAltName", null);
            enrollmentEntityInfo.put("SubjectAltNameFormat", null);
            enrollmentEntityInfo.put("AlgorithmKeys", AlgorithmKeys.RSA_2048);
            enrollmentEntityInfo.put("EntityCategory", NodeEntityCategory.OAM);
            enrollmentEntityInfo.put("ModelInfo", RADIO_MODEL_INFO);
            final EnrollmentPartialInfos epi = nscsPkiEntitiesManager.getEnrollmentEntityInfo(enrollmentEntityInfo);
        } catch (final NscsPkiEntitiesManagerException e) {
            final String errorMsg = e.getMessage();
            assertTrue(errorMsg.contains("SCEP"));
            assertTrue(errorMsg.contains("CommonName"));
        }

    }

    @Test
    public void testGetEnrollmentEntityInfo_ipsec_picoRbs_nodeSerialNumber()
            throws NscsPkiEntitiesManagerException, EntityServiceException, InvalidEntityException, InvalidSubjectAltNameExtension,
            InvalidSubjectException, MissingMandatoryFieldException, AlgorithmNotFoundException, EntityCategoryNotFoundException,
            InvalidEntityCategoryException, CRLExtensionException, CRLGenerationException, EntityAlreadyExistsException, EntityNotFoundException,
            InvalidCRLGenerationInfoException, InvalidEntityAttributeException, InvalidProfileException, ProfileNotFoundException,
            UnsupportedCRLVersionException, PKIConfigurationServiceException, InvalidProfileAttributeException, ProfileServiceException {
        final EntityEnrollmentInfo eei = buildEntityEnrollmentInfo(NodeEntityCategory.IPSEC);
        final ArgumentCaptor<Entity> entityCaptor = ArgumentCaptor.forClass(Entity.class);
        final ArgumentCaptor<EnrollmentType> enrollTypeCaptor = ArgumentCaptor.forClass(EnrollmentType.class);
        final EntityProfile expectedEp = buildEntityProfile(NodeEntityCategory.IPSEC);

        when(pkiEntManager.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(true);
        when(pkiProfManager.getProfile(any(EntityProfile.class))).thenReturn(expectedEp);
        when(pkiEntManager.createEntityAndGetEnrollmentInfo_v1(any(Entity.class), eq(EnrollmentType.cmp))).thenReturn(eei);
        when(pkiConfigManager.getCategory(any(EntityCategory.class))).thenReturn(buildEntityCategory(entityCategoryIpsec));
        when(capabilityModel.isConfiguredSubjectNameUsedForEnrollment(PICO_MODEL_INFO)).thenReturn(false);

        when(capabilityModel.getDefaultInitialOtpCount(PICO_MODEL_INFO)).thenReturn(Integer.toString(VIRTUAL_OTP_COUNT));
        when(capabilityModel.getDefaultOtpValidityPeriodInMinutes(PICO_MODEL_INFO)).thenReturn(Integer.toString(OTP_VALIDITY));

        enrollmentEntityInfo.put("NodeName", nodeFdn);
        enrollmentEntityInfo.put("CommonName", nodeSerialNumber);
        enrollmentEntityInfo.put("EnrollmentMode", EnrollmentMode.CMPv2_VC);
        enrollmentEntityInfo.put("EntityProfileName", ipsecProfileName);
        enrollmentEntityInfo.put("SubjectAltName", null);
        enrollmentEntityInfo.put("SubjectAltNameFormat", null);
        enrollmentEntityInfo.put("AlgorithmKeys", AlgorithmKeys.RSA_2048);
        enrollmentEntityInfo.put("EntityCategory", NodeEntityCategory.IPSEC);
        enrollmentEntityInfo.put("ModelInfo", PICO_MODEL_INFO);
        final EnrollmentPartialInfos epi = nscsPkiEntitiesManager.getEnrollmentEntityInfo(enrollmentEntityInfo);

        verify(pkiEntManager).createEntityAndGetEnrollmentInfo_v1(entityCaptor.capture(), enrollTypeCaptor.capture());
        assertEquals(enrollTypeCaptor.getValue(), EnrollmentType.cmp);
        final Entity capturedEnt = entityCaptor.getValue();
        assertNotNull(capturedEnt);
        assertEquals(eei.getEntity().getEntityInfo().getName(), capturedEnt.getEntityInfo().getName());
        assertEquals(buildSubject(nodeSerialNumber + "." + subjectOrg + "." + subjectCountry), capturedEnt.getEntityInfo().getSubject());
        final SubjectAltName capturedSan = capturedEnt.getEntityInfo().getSubjectAltName();
        assertEquals(VIRTUAL_OTP_COUNT, capturedEnt.getEntityInfo().getOTPCount());
        assertEquals(OTP_VALIDITY, capturedEnt.getOtpValidityPeriod());
        assertEquals(expectedEp, capturedEnt.getEntityProfile());
        assertEquals(buildEntityCategory(entityCategoryIpsec), capturedEnt.getCategory());
        assertNotNull(capturedSan);
        assertEquals(capturedSan.getSubjectAltNameFields().get(0).getType(), SubjectAltNameFieldType.DNS_NAME);
        assertEquals(((SubjectAltNameString) (capturedSan.getSubjectAltNameFields().get(0).getValue())).getValue(),
                nodeSerialNumber + "." + subjectOrg + "." + subjectCountry);
    }

    @Test
    public void testGetEnrollmentEntityInfo_ipsec_picoRbs_forceSan()
            throws NscsPkiEntitiesManagerException, EntityServiceException, InvalidEntityException, InvalidSubjectAltNameExtension,
            InvalidSubjectException, MissingMandatoryFieldException, AlgorithmNotFoundException, EntityCategoryNotFoundException,
            InvalidEntityCategoryException, CRLExtensionException, CRLGenerationException, EntityAlreadyExistsException, EntityNotFoundException,
            InvalidCRLGenerationInfoException, InvalidEntityAttributeException, InvalidProfileException, ProfileNotFoundException,
            UnsupportedCRLVersionException, PKIConfigurationServiceException, InvalidProfileAttributeException, ProfileServiceException {

        final EntityEnrollmentInfo eei = buildEntityEnrollmentInfo(NodeEntityCategory.IPSEC);
        final ArgumentCaptor<Entity> entityCaptor = ArgumentCaptor.forClass(Entity.class);
        final ArgumentCaptor<EnrollmentType> enrollTypeCaptor = ArgumentCaptor.forClass(EnrollmentType.class);
        final EntityProfile expectedEp = buildEntityProfile(NodeEntityCategory.IPSEC);

        when(pkiEntManager.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(true);
        when(pkiProfManager.getProfile(any(EntityProfile.class))).thenReturn(expectedEp);
        when(pkiEntManager.createEntityAndGetEnrollmentInfo_v1(any(Entity.class), eq(EnrollmentType.cmp))).thenReturn(eei);
        when(pkiConfigManager.getCategory(any(EntityCategory.class))).thenReturn(buildEntityCategory(entityCategoryIpsec));
        when(capabilityModel.isConfiguredSubjectNameUsedForEnrollment(PICO_MODEL_INFO)).thenReturn(false);
        when(capabilityModel.getDefaultInitialOtpCount(PICO_MODEL_INFO)).thenReturn(Integer.toString(VIRTUAL_OTP_COUNT));
        when(capabilityModel.getDefaultOtpValidityPeriodInMinutes(PICO_MODEL_INFO)).thenReturn(Integer.toString(OTP_VALIDITY));
        enrollmentEntityInfo.put("NodeName", nodeFdn);
        enrollmentEntityInfo.put("CommonName", nodeSerialNumber);
        enrollmentEntityInfo.put("EnrollmentMode",  EnrollmentMode.CMPv2_VC);
        enrollmentEntityInfo.put("EntityProfileName", ipsecProfileName);
        enrollmentEntityInfo.put("SubjectAltName", subjectAltNameString);
        enrollmentEntityInfo.put("SubjectAltNameFormat", SubjectAltNameFormat.IPV4);
        enrollmentEntityInfo.put("AlgorithmKeys", AlgorithmKeys.RSA_2048);
        enrollmentEntityInfo.put("EntityCategory", NodeEntityCategory.IPSEC);
        enrollmentEntityInfo.put("ModelInfo", PICO_MODEL_INFO);
        final EnrollmentPartialInfos epi = nscsPkiEntitiesManager.getEnrollmentEntityInfo(enrollmentEntityInfo);

        verify(pkiEntManager).createEntityAndGetEnrollmentInfo_v1(entityCaptor.capture(), enrollTypeCaptor.capture());
        assertEquals(enrollTypeCaptor.getValue(), EnrollmentType.cmp);
        final Entity capturedEnt = entityCaptor.getValue();
        assertNotNull(capturedEnt);
        assertEquals(eei.getEntity().getEntityInfo().getName(), capturedEnt.getEntityInfo().getName());
        assertEquals(buildSubject(nodeSerialNumber + "." + subjectOrg + "." + subjectCountry), capturedEnt.getEntityInfo().getSubject());
        final SubjectAltName capturedSan = capturedEnt.getEntityInfo().getSubjectAltName();
        assertEquals(VIRTUAL_OTP_COUNT, capturedEnt.getEntityInfo().getOTPCount());
        assertEquals(OTP_VALIDITY, capturedEnt.getOtpValidityPeriod());
        assertEquals(expectedEp, capturedEnt.getEntityProfile());
        assertEquals(buildEntityCategory(entityCategoryIpsec), capturedEnt.getCategory());
        assertNotNull(capturedSan);
    }

    @Test
    public void testGetEnrollmentEntityInfo_ipsec_RadioNode_nodeSerialNumber()
            throws NscsPkiEntitiesManagerException, EntityServiceException, InvalidEntityException, InvalidSubjectAltNameExtension,
            InvalidSubjectException, MissingMandatoryFieldException, AlgorithmNotFoundException, EntityCategoryNotFoundException,
            InvalidEntityCategoryException, CRLExtensionException, CRLGenerationException, EntityAlreadyExistsException, EntityNotFoundException,
            InvalidCRLGenerationInfoException, InvalidEntityAttributeException, InvalidProfileException, ProfileNotFoundException,
            UnsupportedCRLVersionException, PKIConfigurationServiceException, InvalidProfileAttributeException, ProfileServiceException {
        final EntityEnrollmentInfo eei = buildEntityEnrollmentInfo(NodeEntityCategory.IPSEC);
        final ArgumentCaptor<Entity> entityCaptor = ArgumentCaptor.forClass(Entity.class);
        final ArgumentCaptor<EnrollmentType> enrollTypeCaptor = ArgumentCaptor.forClass(EnrollmentType.class);
        final EntityProfile expectedEp = buildEntityProfile(NodeEntityCategory.IPSEC);

        when(pkiEntManager.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(true);
        when(pkiProfManager.getProfile(any(EntityProfile.class))).thenReturn(expectedEp);
        when(pkiEntManager.createEntityAndGetEnrollmentInfo_v1(any(Entity.class), eq(EnrollmentType.cmp))).thenReturn(eei);
        when(pkiConfigManager.getCategory(any(EntityCategory.class))).thenReturn(buildEntityCategory(entityCategoryIpsec));
        when(capabilityModel.isConfiguredSubjectNameUsedForEnrollment(RADIO_MODEL_INFO)).thenReturn(true);
        when(capabilityModel.getDefaultInitialOtpCount(RADIO_MODEL_INFO)).thenReturn(Integer.toString(INITIAL_OTP_COUNT));
        when(capabilityModel.getDefaultOtpValidityPeriodInMinutes(RADIO_MODEL_INFO)).thenReturn(Integer.toString(OTP_VALIDITY));
        enrollmentEntityInfo.put("NodeName", nodeFdn);
        enrollmentEntityInfo.put("CommonName", nodeSerialNumber);
        enrollmentEntityInfo.put("EnrollmentMode", EnrollmentMode.CMPv2_VC);
        enrollmentEntityInfo.put("EntityProfileName", ipsecProfileName);
        enrollmentEntityInfo.put("SubjectAltName", subjectAltNameString);
        enrollmentEntityInfo.put("SubjectAltNameFormat", SubjectAltNameFormat.IPV4);
        enrollmentEntityInfo.put("AlgorithmKeys", AlgorithmKeys.RSA_2048);
        enrollmentEntityInfo.put("EntityCategory", NodeEntityCategory.IPSEC);
        enrollmentEntityInfo.put("ModelInfo", RADIO_MODEL_INFO);
        final EnrollmentPartialInfos epi = nscsPkiEntitiesManager.getEnrollmentEntityInfo(enrollmentEntityInfo);

        verify(pkiEntManager).createEntityAndGetEnrollmentInfo_v1(entityCaptor.capture(), enrollTypeCaptor.capture());
        assertEquals(enrollTypeCaptor.getValue(), EnrollmentType.cmp);
        final Entity capturedEnt = entityCaptor.getValue();
        assertNotNull(capturedEnt);
        assertEquals(eei.getEntity().getEntityInfo().getName(), capturedEnt.getEntityInfo().getName());
        assertEquals(buildSubject(nodeSerialNumber), capturedEnt.getEntityInfo().getSubject());
        final SubjectAltName capturedSan = capturedEnt.getEntityInfo().getSubjectAltName();
        assertEquals(expectedEp, capturedEnt.getEntityProfile());
        assertEquals(buildEntityCategory(entityCategoryIpsec), capturedEnt.getCategory());
    }

    @Test
    public void testDeleteEntity_ActiveEntity() throws NscsPkiEntitiesManagerException, EntityServiceException, InvalidEntityException,
            EntityAlreadyDeletedException, EntityInUseException, EntityNotFoundException, InvalidEntityAttributeException {
        final Entity ent = buildEntity(NodeEntityCategory.IPSEC);
        ent.getEntityInfo().setStatus(EntityStatus.ACTIVE);
        when(pkiEntManager.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(false);
        when(pkiEntManager.getEntity(any(Entity.class))).thenReturn(ent);
        nscsPkiEntitiesManager.deleteEntity(NscsPkiUtils.getEntityNameFromFdn(NodeEntityCategory.IPSEC, nodeFdn));
        verify(pkiEntManager).deleteEntity(any(Entity.class));
    }

    @Test
    public void testDeleteEntity_DeletedEntity() throws NscsPkiEntitiesManagerException, EntityServiceException, InvalidEntityException,
            EntityAlreadyDeletedException, EntityInUseException, EntityNotFoundException, InvalidEntityAttributeException {
        final Entity ent = buildEntity(NodeEntityCategory.IPSEC);
        ent.getEntityInfo().setStatus(EntityStatus.DELETED);
        when(pkiEntManager.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(false);
        when(pkiEntManager.getEntity(any(Entity.class))).thenReturn(ent);
        nscsPkiEntitiesManager.deleteEntity(NscsPkiUtils.getEntityNameFromFdn(NodeEntityCategory.IPSEC, nodeFdn));
        verify(pkiEntManager, never()).deleteEntity(any(Entity.class));
    }

    @Test
    public void testDeleteEntity_NoEntity() throws NscsPkiEntitiesManagerException, EntityServiceException, InvalidEntityException,
            EntityAlreadyDeletedException, EntityInUseException, EntityNotFoundException, InvalidEntityAttributeException {
        when(pkiEntManager.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(true);
        nscsPkiEntitiesManager.deleteEntity(NscsPkiUtils.getEntityNameFromFdn(NodeEntityCategory.IPSEC, nodeFdn));
        verify(pkiEntManager, never()).deleteEntity(any(Entity.class));
    }

    /**
     * Test of getTrustCertificatesFromProfile method, of class NscsPkiEntitiesManagerJar.
     */
    @Test
    public void getTrustCertificatesFromProfile() throws Exception {
        when(pkiProfManager.getProfile(any(EntityProfile.class))).thenReturn(buildEntityProfile(NodeEntityCategory.OAM));
        when(pkiCACertManager.getCertificateChainList(anyString(), eq(CertificateStatus.ACTIVE))).thenReturn(null);
    }

    /**
     * Test of findPkiRootCACertificate method, of class NscsPkiEntitiesManagerJar.
     */
    @Test
    public void testFindPkiRootCACertificate() throws Exception {
    }

    /**
     * Test of createEntity method, of class NscsPkiEntitiesManagerJar.
     */
    @Test
    public void testCreateEntity() throws Exception {
    }

    /**
     * Test of getEntities method, of class NscsPkiEntitiesManagerJar.
     */
    @Test
    public void testGetEntities() throws Exception {
    }

    /**
     * Test of getEntity method, of class NscsPkiEntitiesManagerJar.
     */
    @Test
    public void testGetEntity() throws Exception {
    }

    /**
     * Test of updateEntity method, of class NscsPkiEntitiesManagerJar.
     */
    @Test
    public void testUpdateEntity() throws Exception {
    }

    /**
     * Test of getPkiEntity method, of class NscsPkiEntitiesManagerJar.
     */
    @Test
    public void testGetPkiEntity() throws Exception {
    }

    /**
     * Test of getCAEntity method, of class NscsPkiEntitiesManagerJar.
     */
    @Test
    public void testGetCAEntity() throws Exception {
    }

    /**
     * Test of getEntityProfile method, of class NscsPkiEntitiesManagerCISCJar.
     */
    @Test
    public void testGetEntityProfile() throws Exception {
        final EntityProfile expectedEp = buildEntityProfile(NodeEntityCategory.OAM);
        when(pkiProfManager.getProfile(any(EntityProfile.class))).thenReturn(expectedEp);
        final EntityProfile actualEp = nscsPkiEntitiesManager.getEntityProfile(oamProfileName);
        assertEquals(expectedEp, actualEp);
    }

    /**
     * Test of isEntityProfileNameAvailable method, of class NscsPkiEntitiesManageCISCrJar.
     */
    @Test
    public void testIsEntityProfileNameAvailableTrue() throws Exception {
        when(pkiCachedCalls.isEntityProfileNameAvailable(oamProfileName)).thenReturn(true);
        final boolean isAvailable = nscsPkiEntitiesManager.isEntityProfileNameAvailable(oamProfileName);
        assertTrue(isAvailable);
    }

    @Test
    public void testIsEntityProfileNameAvailableFalse() throws Exception {
        when(pkiCachedCalls.isEntityProfileNameAvailable(oamProfileName)).thenReturn(false);
        final boolean isAvailable = nscsPkiEntitiesManager.isEntityProfileNameAvailable(oamProfileName);
        assertFalse(isAvailable);
    }

    /**
     * Test of getCAsTrusts method, of class NscsPkiEntitiesManagerJar.
     */
    @Test
    public void testGetCAsTrusts() throws Exception {
    }

    /**
     * Test of getCACertificates method, of class NscsPkiEntitiesManagerJar.
     */
    @Test
    public void testGetCACertificates() throws Exception {
    }

    /**
     * Test of getCATrusts method, of class NscsPkiEntitiesManagerJar.
     */
    @Test
    public void testGetCATrusts() throws Exception {
    }

    /**
     * Test of getEntityOTP method, of class NscsPkiEntitiesManagerJar.
     */
    @Test
    public void testGetEntityOTP() throws Exception {
    }

    /**
     * Test of useMockEntityManager method, of class NscsPkiEntitiesManagerJar.
     */
    @Test
    public void testUseMockEntityManager() throws Exception {
    }

    /**
     * Test of useMockProfileManager method, of class NscsPkiEntitiesManagerJar.
     */
    @Test
    public void testUseMockProfileManager() throws Exception {
    }

    /**
     * Test of useMockCertificateManager method, of class NscsPkiEntitiesManagerJar.
     */
    @Test
    public void testUseMockCertificateManager() throws Exception {
    }

    /**
     * Test of getTrustedCAs method, of class NscsPkiEntitiesManagerJar.
     */
    @Test
    public void testGetTrustedCAs() throws Exception {
        final EntityProfile entityProfile = buildEntityProfile(NodeEntityCategory.OAM);

        when(pkiProfManager.isProfileNameAvailable(oamProfileName, ProfileType.ENTITY_PROFILE)).thenReturn(false);
        when(pkiProfManager.getProfile(any(EntityProfile.class))).thenReturn(entityProfile);

        final Set<NscsPair<String, Boolean>> ret = nscsPkiEntitiesManager.getTrustedCAs(oamProfileName);

        assertEquals(1, ret.size());
        final Iterator<NscsPair<String, Boolean>> it = ret.iterator();
        final NscsPair<String, Boolean> myPair = it.next();
        assertFalse(myPair.getR());
        assertEquals("MyInternalCA", myPair.getL());
    }

    @Test
    public void testGetEnrollmentEntityInfo_ipsec_createEntity_unSupportedKeyAlgorithm() {
        final EntityEnrollmentInfo eei = buildEntityEnrollmentInfo(NodeEntityCategory.IPSEC);
        final EntityProfile expectedEp = buildEntityProfile(NodeEntityCategory.IPSEC);

        when(pkiEntManager.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(true);

        when(pkiProfManager.getProfile(any(EntityProfile.class))).thenReturn(expectedEp);
        when(pkiEntManager.createEntityAndGetEnrollmentInfo_v1(any(Entity.class), eq(EnrollmentType.scep))).thenReturn(eei);
        when(pkiConfigManager.getCategory(any(EntityCategory.class))).thenReturn(buildEntityCategory(entityCategoryIpsec));
        when(capabilityModel.isConfiguredSubjectNameUsedForEnrollment(CPP_15B_MODEL_INFO)).thenReturn(true);
        when(capabilityModel.getDefaultInitialOtpCount(CPP_15B_MODEL_INFO)).thenReturn("5");

        try {
            enrollmentEntityInfo.put("EntityProfileName", ipsecProfileName);
            enrollmentEntityInfo.put("SubjectAltName", subjectAltNameString);
            enrollmentEntityInfo.put("SubjectAltNameFormat", SubjectAltNameFormat.IPV4);
            enrollmentEntityInfo.put("AlgorithmKeys", AlgorithmKeys.RSA_4096);
            enrollmentEntityInfo.put("EntityCategory", NodeEntityCategory.IPSEC);
            enrollmentEntityInfo.put("ModelInfo", CPP_15B_MODEL_INFO);
            nscsPkiEntitiesManager.getEnrollmentEntityInfo(enrollmentEntityInfo);
        } catch (NscsPkiEntitiesManagerException nscsPkiEntitiesManagerException) {
            assertEquals("The given Key Algorithm [" + AlgorithmKeys.RSA_4096 + "] is not in supported list of Entity Profile [" + ipsecProfileName + "]. " + "Accepted Key Algorithms are "
                    + convertKeyAlgorithmsToString(expectedEp.getCertificateProfile().getKeyGenerationAlgorithms()), nscsPkiEntitiesManagerException.getMessage());
        }
    }

    @Test
    public void testGetEnrollmentEntityInfo_oam_createEntity_unSupportedKeyAlgorithm() {
        final EntityEnrollmentInfo eei = buildEntityEnrollmentInfo(NodeEntityCategory.OAM);
        final EntityProfile expectedEp = buildEntityProfile(NodeEntityCategory.OAM);

        when(pkiEntManager.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(true);
        when(pkiProfManager.getProfile(any(EntityProfile.class))).thenReturn(expectedEp);
        when(pkiEntManager.createEntityAndGetEnrollmentInfo_v1(any(Entity.class), eq(EnrollmentType.scep))).thenReturn(eei);
        when(pkiConfigManager.getCategory(any(EntityCategory.class))).thenReturn(buildEntityCategory(entityCategoryOam));
        when(capabilityModel.isConfiguredSubjectNameUsedForEnrollment(CPP_15B_MODEL_INFO)).thenReturn(true);
        when(capabilityModel.getDefaultInitialOtpCount(CPP_15B_MODEL_INFO)).thenReturn("5");
        try{
            enrollmentEntityInfo.put("AlgorithmKeys", AlgorithmKeys.ECDSA_256);
        nscsPkiEntitiesManager.getEnrollmentEntityInfo(enrollmentEntityInfo);
        } catch (NscsPkiEntitiesManagerException nscsPkiEntitiesManagerException) {
            assertEquals("The given Key Algorithm [" + AlgorithmKeys.ECDSA_256 + "] is not in supported list of Entity Profile [" + oamProfileName + "]. " + "Accepted Key Algorithms are "
                    + convertKeyAlgorithmsToString(expectedEp.getCertificateProfile().getKeyGenerationAlgorithms()), nscsPkiEntitiesManagerException.getMessage());
        }
    }

    @Test
    public void testGetEnrollmentEntityInfo_oam_createEntity_nullKeys_model13B_unSupportedDefaultKeyAlgorithm() {
        final EntityEnrollmentInfo eei = buildEntityEnrollmentInfo(NodeEntityCategory.OAM);
        final EntityProfile expectedEp = buildEntityProfile(NodeEntityCategory.OAM);

        when(pkiEntManager.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(true);
        when(pkiProfManager.getProfile(any(EntityProfile.class))).thenReturn(expectedEp);
        when(pkiEntManager.createEntityAndGetEnrollmentInfo_v1(any(Entity.class), eq(EnrollmentType.scep))).thenReturn(eei);
        when(pkiConfigManager.getCategory(any(EntityCategory.class))).thenReturn(buildEntityCategory(entityCategoryOam));
        when(capabilityModel.getDefaultAlgorithmKeys(CPP_13B_MODEL_INFO)).thenReturn(AlgorithmKeys.RSA_3072);
        when(capabilityModel.getDefaultInitialOtpCount(CPP_13B_MODEL_INFO)).thenReturn("5");

        try {
            enrollmentEntityInfo.put("ModelInfo", CPP_13B_MODEL_INFO);
            nscsPkiEntitiesManager.getEnrollmentEntityInfo(enrollmentEntityInfo);
        } catch (NscsPkiEntitiesManagerException nscsPkiEntitiesManagerException) {
            assertEquals("The given Key Algorithm [" + AlgorithmKeys.RSA_3072 + "] is not in supported list of Entity Profile [" + oamProfileName + "]. " + "Accepted Key Algorithms are "
                    + convertKeyAlgorithmsToString(expectedEp.getCertificateProfile().getKeyGenerationAlgorithms()), nscsPkiEntitiesManagerException.getMessage());
        }

    }

    @Test
    public void testGetEnrollmentEntityInfo_oam_createEntity_nullKeys_model15B_unSupportedDefaultKeyAlgorithm() {
        final EntityEnrollmentInfo eei = buildEntityEnrollmentInfo(NodeEntityCategory.OAM);
        final EntityProfile expectedEp = buildEntityProfile(NodeEntityCategory.OAM);

        when(pkiEntManager.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(true);
        when(pkiProfManager.getProfile(any(EntityProfile.class))).thenReturn(expectedEp);
        when(pkiEntManager.createEntityAndGetEnrollmentInfo_v1(any(Entity.class), eq(EnrollmentType.scep))).thenReturn(eei);
        when(pkiConfigManager.getCategory(any(EntityCategory.class))).thenReturn(buildEntityCategory(entityCategoryOam));
        when(capabilityModel.getDefaultAlgorithmKeys(CPP_15B_MODEL_INFO)).thenReturn(AlgorithmKeys.ECDSA_256);
        when(capabilityModel.isConfiguredSubjectNameUsedForEnrollment(CPP_15B_MODEL_INFO)).thenReturn(true);
        when(capabilityModel.getDefaultInitialOtpCount(CPP_15B_MODEL_INFO)).thenReturn("5");
        try {
            nscsPkiEntitiesManager.getEnrollmentEntityInfo(enrollmentEntityInfo);
        } catch (NscsPkiEntitiesManagerException nscsPkiEntitiesManagerException) {
            assertEquals("The given Key Algorithm [" + AlgorithmKeys.ECDSA_256 + "] is not in supported list of Entity Profile [" + oamProfileName + "]. " + "Accepted Key Algorithms are "
                    + convertKeyAlgorithmsToString(expectedEp.getCertificateProfile().getKeyGenerationAlgorithms()), nscsPkiEntitiesManagerException.getMessage());
        }
    }

    @Test
    public void testGetEnrollmentEntityInfo_createEntity_nullProfileName_unSupportedKeyAlgorithm() {
        final EntityEnrollmentInfo eei = buildEntityEnrollmentInfo(NodeEntityCategory.IPSEC);
        final EntityProfile expectedEp = buildEntityProfile(NodeEntityCategory.IPSEC);

        when(pkiEntManager.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(true);
        when(capabilityModel.getDefaultEntityProfile(CPP_15B_MODEL_INFO, NodeEntityCategory.IPSEC)).thenReturn(ipsecProfileName);
        when(pkiProfManager.getProfile(any(EntityProfile.class))).thenReturn(expectedEp);
        when(pkiEntManager.createEntityAndGetEnrollmentInfo_v1(any(Entity.class), eq(EnrollmentType.scep))).thenReturn(eei);
        when(pkiConfigManager.getCategory(any(EntityCategory.class))).thenReturn(buildEntityCategory(entityCategoryIpsec));
        when(capabilityModel.isConfiguredSubjectNameUsedForEnrollment(CPP_15B_MODEL_INFO)).thenReturn(true);
        when(capabilityModel.getDefaultInitialOtpCount(CPP_15B_MODEL_INFO)).thenReturn("5");
        try {
            enrollmentEntityInfo.put("SubjectAltName", subjectAltNameString);
            enrollmentEntityInfo.put("SubjectAltNameFormat", SubjectAltNameFormat.IPV4);
            enrollmentEntityInfo.put("AlgorithmKeys", AlgorithmKeys.RSA_4096);
            enrollmentEntityInfo.put("EntityCategory", NodeEntityCategory.IPSEC);
            enrollmentEntityInfo.put("ModelInfo", CPP_15B_MODEL_INFO);
            nscsPkiEntitiesManager.getEnrollmentEntityInfo(enrollmentEntityInfo);
        } catch (NscsPkiEntitiesManagerException nscsPkiEntitiesManagerException) {
            assertEquals("The given Key Algorithm [" + AlgorithmKeys.RSA_4096 + "] is not in supported list of Entity Profile [" + ipsecProfileName + "]. " + "Accepted Key Algorithms are "
                    + convertKeyAlgorithmsToString(expectedEp.getCertificateProfile().getKeyGenerationAlgorithms()), nscsPkiEntitiesManagerException.getMessage());
        }
    }

    @Test
    public void testGetEnrollmentEntityInfo_createEntity_nullProfileName_nullKeys_model15B_unSupportedDefaultKeyAlgorithm() {
        final EntityEnrollmentInfo eei = buildEntityEnrollmentInfo(NodeEntityCategory.IPSEC);
        final EntityProfile expectedEp = buildEntityProfile(NodeEntityCategory.IPSEC);

        when(pkiEntManager.isEntityNameAvailable(anyString(), eq(EntityType.ENTITY))).thenReturn(true);
        when(capabilityModel.getDefaultEntityProfile(CPP_15B_MODEL_INFO, NodeEntityCategory.IPSEC)).thenReturn(ipsecProfileName);
        when(pkiProfManager.getProfile(any(EntityProfile.class))).thenReturn(expectedEp);
        when(pkiEntManager.createEntityAndGetEnrollmentInfo_v1(any(Entity.class), eq(EnrollmentType.scep))).thenReturn(eei);
        when(capabilityModel.getDefaultAlgorithmKeys(CPP_15B_MODEL_INFO)).thenReturn(AlgorithmKeys.ECDSA_256);
        when(pkiConfigManager.getCategory(any(EntityCategory.class))).thenReturn(buildEntityCategory(entityCategoryIpsec));
        when(capabilityModel.isConfiguredSubjectNameUsedForEnrollment(CPP_15B_MODEL_INFO)).thenReturn(true);
        when(capabilityModel.getDefaultInitialOtpCount(CPP_15B_MODEL_INFO)).thenReturn("5");
        try {
            enrollmentEntityInfo.put("EntityProfileName", null);
            enrollmentEntityInfo.put("SubjectAltName", subjectAltNameString);
            enrollmentEntityInfo.put("SubjectAltNameFormat", SubjectAltNameFormat.IPV4);
            enrollmentEntityInfo.put("EntityCategory", NodeEntityCategory.IPSEC);
            enrollmentEntityInfo.put("ModelInfo", CPP_15B_MODEL_INFO);
            nscsPkiEntitiesManager.getEnrollmentEntityInfo(enrollmentEntityInfo);
        } catch (NscsPkiEntitiesManagerException nscsPkiEntitiesManagerException) {
            assertEquals("The given Key Algorithm [" + AlgorithmKeys.ECDSA_256 + "] is not in supported list of Entity Profile [" + ipsecProfileName + "]. " + "Accepted Key Algorithms are "
                    + convertKeyAlgorithmsToString(expectedEp.getCertificateProfile().getKeyGenerationAlgorithms()), nscsPkiEntitiesManagerException.getMessage());
        }
    }

    private List<String> convertKeyAlgorithmsToString(final List<Algorithm> algorithms) {

        final List<String> supportedAlgorithmAndKeySizeValues = new ArrayList<>();
        if (algorithms != null) {
            for (final Algorithm algorithm : algorithms) {
                final AlgorithmKeys algorithmKey = AlgorithmKeys.toAlgorithmKeys(algorithm.getName(), algorithm.getKeySize());
                if (algorithmKey != null) {
                    supportedAlgorithmAndKeySizeValues.add(algorithmKey.toString());
                }
            }

        }
        return supportedAlgorithmAndKeySizeValues;
    }
    /**
     * Test of getSmrsAccountTypeForNscs method, of class NscsPkiEntitiesManagerJar.
     */
    @Test
    public void testGetSmrsAccountTypeForNscs() throws Exception {
    }

    /**
     * Test of getTrustDistributionPointUrls method, of class NscsPkiEntitiesManagerJar.
     */
    @Test
    public void testGetTrustDistributionPointUrls() throws Exception {
    }

    /**
     * Test of revokeCertificateByIssuerName method, of class NscsPkiEntitiesManagerJar.
     */
    @Test
    public void testRevokeCertificateByIssuerName() throws Exception {
    }

    /**
     * Test of getEntityListByIssuerName method, of class NscsPkiEntitiesManagerJar.
     */
    @Test
    public void testGetEntityListByIssuerName() throws Exception {
    }

    /**
     * Test of findNodeEntityCategory method, of class NscsPkiEntitiesManagerJar.
     */
    @Test
    public void testFindNodeEntityCategory() throws Exception {
    }

    /**
     * Test of isEntityNameAvailable method, of class NscsPkiEntitiesManagerJar.
     */
    @Test
    public void testIsEntityNameAvailable() throws Exception {
    }

    /**
     * Test of getTrustCertificates method, of class NscsPkiEntitiesManagerJar.
     */
    @Test
    public void testGetTrustCertificates() throws Exception {
    }

    /**
     * Test of getPkiEntityCategory method, of class NscsPkiEntitiesManagerJar.
     */
    @Test
    public void testGetPkiEntityCategory() throws Exception {
    }

    /**
     * Test of getEntityLog method, of class NscsPkiEntitiesManagerJar.
     */
    @Test
    public void testGetEntityLog() throws Exception {
    }

    /**
     * Test of getEnrollmentInfoLog method, of class NscsPkiEntitiesManagerJar.
     */
    @Test
    public void testGetEnrollmentInfoLog() throws Exception {
    }

    /**
     * Test of getEntitiesByCategoryWithInvalidCertificate method, of class NscsPkiEntitiesManagerJar.
     */
    @Test
    public void testGetEntitiesByCategoryWithInvalidCertificate() throws Exception {
    }

    /**
     * Test of createEntityAndGetEnrollmentInfo method, of class NscsPkiEntitiesManagerJar.
     */
    @Test
    public void testCreateEntityAndGetEnrollmentInfo() throws Exception {
    }

    /**
     * Test of updateEntityAndGetEnrollmentInfo method, of class NscsPkiEntitiesManagerJar.
     */
    @Test
    public void testUpdateEntityAndGetEnrollmentInfo() throws Exception {
    }
}
