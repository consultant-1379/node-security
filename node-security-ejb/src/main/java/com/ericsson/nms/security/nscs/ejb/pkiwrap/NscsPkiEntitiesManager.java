/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.nms.security.nscs.ejb.pkiwrap;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.iscf.BaseSubjectAltNameDataType;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.model.CertSpec;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.api.pki.EnrollmentPartialInfos;
import com.ericsson.nms.security.nscs.api.pki.NodeEntityCategory;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerException;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerIF;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerRisc;
import com.ericsson.nms.security.nscs.api.util.NscsPair;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.enrollmentinfo.utility.EnrollmentInfoConstants;
import com.ericsson.nms.security.nscs.pki.NscsPkiEntitiesManagerJar;
import com.ericsson.nms.security.nscs.pki.NscsPkiUtils;
import com.ericsson.oss.itpf.security.pki.common.model.Algorithm;
import com.ericsson.oss.itpf.security.pki.common.model.CertificateAuthority;
import com.ericsson.oss.itpf.security.pki.common.model.EntityInfo;
import com.ericsson.oss.itpf.security.pki.common.model.EntityStatus;
import com.ericsson.oss.itpf.security.pki.common.model.Subject;
import com.ericsson.oss.itpf.security.pki.common.model.SubjectField;
import com.ericsson.oss.itpf.security.pki.common.model.SubjectFieldType;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.Certificate;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.AbstractSubjectAltNameFieldValue;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltName;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameField;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameFieldType;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameString;
import com.ericsson.oss.itpf.security.pki.manager.model.CertificateChain;
import com.ericsson.oss.itpf.security.pki.manager.model.EnrollmentInfo;
import com.ericsson.oss.itpf.security.pki.manager.model.EnrollmentType;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityCategory;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityEnrollmentInfo;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityType;
import com.ericsson.oss.itpf.security.pki.manager.model.TDPSUrlInfo;
import com.ericsson.oss.itpf.security.pki.manager.model.TrustCAChain;
import com.ericsson.oss.itpf.security.pki.manager.model.TrustedEntityInfo;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.CAEntity;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.ExtCA;
import com.ericsson.oss.itpf.security.pki.manager.model.profiles.EntityProfile;
import com.ericsson.oss.itpf.security.pki.manager.model.profiles.TrustProfile;
/**
 *
 * @author enmadmin
 */
@Stateless
public class NscsPkiEntitiesManager implements NscsPkiEntitiesManagerIF {

    @Inject
    Logger logger;

    @Inject
    NscsPkiEntitiesManagerRisc nscsPkiEntitiesManagerRisc;

    @Inject
    NscsCapabilityModelService capabilityModel;

    private static final int OTP_LENGTH = 21;

    //	private static final int INITIAL_OTP_COUNT = 5;
    //	private static final int VIRTUAL_NE_OTP_COUNT = 2;

    private static final Map<NodeEntityCategory, String> entityCategoryMap = new HashMap<>();

    static {
        entityCategoryMap.put(NodeEntityCategory.OAM, "NODE-OAM");
        entityCategoryMap.put(NodeEntityCategory.IPSEC, "NODE-IPSEC");
    }

    /**
     * @param enrollmentEntityInfo
     * @throws com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerException
     * @return the com.ericsson.nms.security.nscs.api.pki.EnrollmentPartialInfos
     */
    @Override
    @SuppressWarnings("squid:S3776")
    public EnrollmentPartialInfos getEnrollmentEntityInfo(final Map<Object, Object> enrollmentEntityInfo)
            throws NscsPkiEntitiesManagerException {

        String nodeName = (String) enrollmentEntityInfo.get(EnrollmentInfoConstants.NODE_NAME);
        String nodeCommonName = (String) enrollmentEntityInfo.get(EnrollmentInfoConstants.COMMON_NAME);
        EnrollmentMode enrollmentMode = (EnrollmentMode) enrollmentEntityInfo.get(EnrollmentInfoConstants.ENROLLMENT_MODE);
        String entityProfileName = (String) enrollmentEntityInfo.get(EnrollmentInfoConstants.ENTITY_PROFILE_NAME);
        BaseSubjectAltNameDataType nscsSubjectAltNameValue = (BaseSubjectAltNameDataType) enrollmentEntityInfo.get(EnrollmentInfoConstants.SUBJECT_ALT_NAME);
        SubjectAltNameFormat subjectAltNameFormat = (SubjectAltNameFormat) enrollmentEntityInfo.get(EnrollmentInfoConstants.SUBJECT_ALT_NAME_FORMAT);
        AlgorithmKeys algorithmKeys = (AlgorithmKeys) enrollmentEntityInfo.get(EnrollmentInfoConstants.ALGORITHM_KEYS);
        NodeEntityCategory entityCategory = (NodeEntityCategory) enrollmentEntityInfo.get(EnrollmentInfoConstants.ENTITY_CATEGORY);
        NodeModelInformation modelInfo = (NodeModelInformation) enrollmentEntityInfo.get(EnrollmentInfoConstants.MODEL_INFO);
        Integer otpCount = (Integer) enrollmentEntityInfo.get(EnrollmentInfoConstants.OTP_COUNT);
        Integer otpValidityPeriodInMinutes = (Integer) enrollmentEntityInfo.get(EnrollmentInfoConstants.OTP_VALIDITY_PERIOD_IN_MINUTES);

        logger.info(
                "getEnrollmentEntityInfo() : nodeName [{}], commonName[{}] enrollmentMode[{}], entityProfileName[{}], subjectAltName[{}], subjectAltNameFormat[{}], algorithmKeys[{}], entityCategory[{}], modelInfo[{}], otpCount[{}], otpValidity[{}]",
                nodeName, nodeCommonName, (enrollmentMode == null) ? "null" : enrollmentMode.name(), entityProfileName, nscsSubjectAltNameValue,
                (subjectAltNameFormat == null) ? "null" : subjectAltNameFormat.name(), (algorithmKeys == null) ? "null" : algorithmKeys.name(),
                entityCategory, modelInfo, otpCount, otpValidityPeriodInMinutes);

        final EnrollmentType enrollmentType = NscsPkiUtils.convertEnrollmentModeToPkiFormat(enrollmentMode);
        if (enrollmentType == null) {
            final String errorMsg = String.format("Convert from NSCS to PKI failed for EnrollmentMode %s",
                    (enrollmentMode != null ? enrollmentMode.name() : "NULL"));
            logger.error(errorMsg);
            throw new NscsPkiEntitiesManagerException(errorMsg);
        }

        SubjectAltNameFieldType pkiSubjectAltNameFormat = null;
        if (subjectAltNameFormat != null) {
            pkiSubjectAltNameFormat = NscsPkiUtils.convertSubjectAltNameFormatToPkiFormat(subjectAltNameFormat);
        }
        final AbstractSubjectAltNameFieldValue pkiSubjectAltNameValue = NscsPkiUtils.convertSubjectAltNameValueToPkiFormat(subjectAltNameFormat,
                nscsSubjectAltNameValue);

        final boolean isPicoRbsNode = !capabilityModel.isConfiguredSubjectNameUsedForEnrollment(modelInfo);

        Entity foundEe = null;
        // final Entity backupEe = null;
        EnrollmentInfo enrollmentInfo = null;

        final Entity ent = new Entity();
        ent.setType(EntityType.ENTITY);

        String extNodeFdn = nodeName;

        if (entityCategory != null) {
            extNodeFdn = extNodeFdn + "-" + entityCategory.toString();
        }

        final EntityInfo entityInfo = new EntityInfo();
        entityInfo.setName(extNodeFdn);
        
        // if OTP count is not provided as input, the default value is used for both create and update of End Entity
        if (otpCount == null) {
            otpCount = getDefaultOtpCount(modelInfo);
        }
        entityInfo.setOTPCount(otpCount);
        ent.setEntityInfo(entityInfo);

        logger.debug("Invoking isEntityNameAvailable() on PKIManager for node [{}]", extNodeFdn);

        // Check whether entity exists (if its name is NOT available) or not (if
        // its name is available)
        boolean entityAlreadyExists;
        entityAlreadyExists = !(nscsPkiEntitiesManagerRisc.isEntityNameAvailable(extNodeFdn, EntityType.ENTITY));
        if (entityAlreadyExists) {

            // Entity name is not available, so the entity exists
            logger.debug("Entity with name [{}] is present", extNodeFdn);

            logger.debug("Invoking getEntity() on PKIManager for Entity [{}]", NscsPkiEntitiesManagerJar.getEntityLog(ent));
            foundEe = nscsPkiEntitiesManagerRisc.getEntity(ent);
            if (foundEe == null) {
                final String errorMsg = String.format("getEntity() : retrieved null Entity.");
                logger.error(errorMsg);
                throw new NscsPkiEntitiesManagerException(errorMsg);
            }
            logger.debug("getEntity() : pki.getEntity done.");
        } else {
            // Entity name is available, so the entity does not exist
            logger.debug("Entity with name [{}] is NOT present", extNodeFdn);
        }

        logger.debug("getEnrollmentEntityInfo() : pki.getEndEntity done.");

        final Algorithm keyGenAlgorithm = new Algorithm();

        if (!entityAlreadyExists) {
            logger.info("No Entity found for [{}]", extNodeFdn);

            if (entityProfileName == null || entityProfileName.isEmpty()) {
                // Read default from Capability Model...
                entityProfileName = capabilityModel.getDefaultEntityProfile(modelInfo, entityCategory);
                logger.debug("Reading default value [{}] for entityProfileName from Capability Model for node [{}], entityCategory [{}], neType [{}]",
                        entityProfileName, extNodeFdn, entityCategory.name(), modelInfo.getNodeType());
            }
            if (algorithmKeys == null) {
                // Read default from Capability Model...
                algorithmKeys = capabilityModel.getDefaultAlgorithmKeys(modelInfo);
                logger.debug("Reading default value [{}] for algorithmKeys from Capability Model for node [{}], entityCategory [{}], neType [{}]",
                        algorithmKeys.name(), extNodeFdn, entityCategory.name(), modelInfo.getNodeType());
            }

            EntityProfile epOut = null;
            epOut = nscsPkiEntitiesManagerRisc.getCachedEntityProfile(entityProfileName);
            if (epOut == null) {
                final String errorMsg = String.format("Failed to get profile: %s", entityProfileName);
                logger.error(errorMsg);
                throw new NscsPkiEntitiesManagerException(errorMsg);
            }

            // Check if profile category is compatible with node entity category
            if (!(entityCategoryMap.get(entityCategory).equals(epOut.getCategory().getName()))) {
                final String errorMsg = String.format("Profile category not compatible with entity category");
                logger.error(errorMsg);
                throw new NscsPkiEntitiesManagerException(errorMsg);
            }
            final Subject subject = buildEntitySubjectFromProfile(extNodeFdn, nodeCommonName, null, epOut, isPicoRbsNode, enrollmentType);
            logger.debug("Entity Subject [{}]", subject);
            entityInfo.setSubject(subject);

            // Build subjectAltName for Entity
            entityInfo.setSubjectAltName(buildSubjectAltName(epOut, pkiSubjectAltNameFormat, pkiSubjectAltNameValue, isPicoRbsNode ? subject : null));

            entityInfo.setSubject(subject);

            entityInfo.setOTP(randomPassword(OTP_LENGTH));

            validateSupportedAlgorithmAndKeySize(algorithmKeys.toString(), epOut);

            ent.setEntityInfo(entityInfo);
            ent.setEntityProfile(epOut);

            // Set keyGenerationAlgorithm to Entity
            keyGenAlgorithm.setName(algorithmKeys.getAlgorithm());
            keyGenAlgorithm.setKeySize(algorithmKeys.getKeySize());
            ent.setKeyGenerationAlgorithm(keyGenAlgorithm);

            // If End Entity does not yet exist, OTP validity period is set to user-provided value or, if not specified, to default value
            if (otpValidityPeriodInMinutes == null) {
                otpValidityPeriodInMinutes = getDefaultOtpValidityPeriodInMinutes(modelInfo);
            }
            ent.setOtpValidityPeriod(otpValidityPeriodInMinutes);
            
            // Set EntityCategory
            // ent.setCategory(pkiCachedCalls.getPkiEntityCategory(entityCategory));
            ent.setCategory(nscsPkiEntitiesManagerRisc.getCachedEntityCategory(entityCategory));

            logger.debug("Invoking createEntityAndGetEnrollmentInfo() for Entity [{}]", NscsPkiEntitiesManagerJar.getEntityLog(ent));

            final EntityEnrollmentInfo eei = nscsPkiEntitiesManagerRisc.createEntityAndGetEnrollmentInfo(ent, enrollmentType);
            logger.debug("Successfully Invoked createEntityAndGetEnrollmentInfo(): returned Entity[{}]",
                    NscsPkiEntitiesManagerJar.getEntityLog(eei.getEntity()));

            enrollmentInfo = eei.getEnrollmentInfo();
            logger.debug("get EnrollmentInfo [{}]", NscsPkiEntitiesManagerJar.getEnrollmentInfoLog(enrollmentInfo));
            foundEe = eei.getEntity();
        } else {
            logger.info("Entity found for [{}]", extNodeFdn);

            Subject subjectUpdated = null;
            EntityProfile epOut = null;
            boolean isEntityProfileUpdated = false;
            if (entityProfileName != null && !entityProfileName.isEmpty()) {
                epOut = nscsPkiEntitiesManagerRisc.getCachedEntityProfile(entityProfileName);
                if ((epOut != null) && (foundEe != null)) {
                    // Check if profile category is compatible with node entity category
                    if (!(foundEe.getCategory().getName().equals(epOut.getCategory().getName()))) {

                        final String errorMsg = String.format("Profile category[%s] not compatible with entity category[%s]",
                                foundEe.getCategory().getName(), epOut.getCategory().getName());

                        logger.error(errorMsg);
                        throw new NscsPkiEntitiesManagerException(errorMsg);
                    }
                    logger.debug("Setting new value [{}] for entityProfileName from Entity", epOut);
                    foundEe.setEntityProfile(epOut);
                    isEntityProfileUpdated = true;
                }
            } else {
                logger.debug("No changes for entityProfileName in Entity");
                epOut = foundEe.getEntityProfile();
            }
            final Subject subjectCurrent = foundEe.getEntityInfo().getSubject();
            final String currentCommonName = getFieldFromSubject(subjectCurrent, SubjectFieldType.COMMON_NAME);
            subjectUpdated = buildEntitySubjectFromProfile(extNodeFdn, nodeCommonName, currentCommonName, epOut, isPicoRbsNode, enrollmentType);
            final boolean isSubjectChanged = !(subjectUpdated.equals(subjectCurrent));
            if (isSubjectChanged) {
                foundEe.getEntityInfo().setSubject(subjectUpdated);
            }

            // Update SAN if :
            // new SAN is received as parameter OR
            // Subject has been updated (only for PICO nodes)
            if (((pkiSubjectAltNameFormat != null) && (pkiSubjectAltNameValue != null)) || (isPicoRbsNode && isSubjectChanged)) {
                // logger.debug("Setting new subjectAltNameFormat [{}] and
                // subjectAltNameValue [{}] for Entity",
                // pkiSubjectAltNameFormat.name(),
                // pkiSubjectAltNameValue.toString());

                if (foundEe.getEntityInfo().getSubjectAltName() == null) {
                    logger.warn("getSubjectAltNameValues() is NULL for Entity [{}]", extNodeFdn);
                } else {
                    logger.warn("TODO getSubjectAltNameValue() is valid for Entity [{}], but data will be reset anyway", extNodeFdn);
                }

                // Build subjectAltName for Entity
                foundEe.getEntityInfo().setSubjectAltName(buildSubjectAltName(foundEe.getEntityProfile(), pkiSubjectAltNameFormat,
                        pkiSubjectAltNameValue, isPicoRbsNode ? subjectUpdated : null));
            } else {
                logger.debug("No changes for subjectAltNameFormat or subjectAltNameValue in Entity");
            }

            if (algorithmKeys == null) {
                // Read value from Entity
                if (foundEe.getKeyGenerationAlgorithm() != null) {
                    algorithmKeys = NscsPkiUtils.convertKeySizetoNscsFormat(foundEe.getKeyGenerationAlgorithm().getName(),
                            foundEe.getKeyGenerationAlgorithm().getKeySize());
                    if (isEntityProfileUpdated) {
                        try {
                            validateSupportedAlgorithmAndKeySize(algorithmKeys.toString(), epOut);
                        } catch (NscsPkiEntitiesManagerException nscsPkiEntitiesManagerException) {
                            algorithmKeys = capabilityModel.getDefaultAlgorithmKeys(modelInfo);
                            validateAndSetKeyGenerationAlgorithm(algorithmKeys, epOut, foundEe);
                        }
                    }
                    logger.debug("Value [{}] returned as algorithmKeys from Entity", (algorithmKeys == null) ? "null" : algorithmKeys.name());
                } else {
                    algorithmKeys = capabilityModel.getDefaultAlgorithmKeys(modelInfo);
                    validateAndSetKeyGenerationAlgorithm(algorithmKeys, epOut, foundEe);
                }
            } else {
                validateAndSetKeyGenerationAlgorithm(algorithmKeys, epOut, foundEe);
            }

            if (entityCategory != null) {
                // PAOLA: Chiamo PKI
                final EntityCategory pkiEntityCategory = nscsPkiEntitiesManagerRisc.getCachedEntityCategory(entityCategory);
                if (!foundEe.getCategory().equals(pkiEntityCategory)) {
                    foundEe.setCategory(pkiEntityCategory);
                }
            }
            foundEe.getEntityInfo().setOTP(randomPassword(OTP_LENGTH));
            // If End Entity already exists, OTP count is reset to user-provided value or, if not specified, to default value
            foundEe.getEntityInfo().setOTPCount(otpCount);
            // If End Entity already exists, OTP validity period is set to user-provided value only if it is specified, otherwise it is not updated
            if (otpValidityPeriodInMinutes != null) {
                foundEe.setOtpValidityPeriod(otpValidityPeriodInMinutes);
            }

            logger.debug("Invoking updateEntityAndGetEnrollmentInfo() for Entity [{}]", NscsPkiEntitiesManagerJar.getEntityLog(foundEe));
            final EntityEnrollmentInfo eei = nscsPkiEntitiesManagerRisc.updateEntityAndGetEnrollmentInfo(foundEe, enrollmentType);
            logger.debug("Successfully Invoked updateEntityAndGetEnrollmentInfo(): returned Entity[{}]",
                    NscsPkiEntitiesManagerJar.getEntityLog(eei.getEntity()));

            enrollmentInfo = eei.getEnrollmentInfo();
            foundEe = eei.getEntity();

        }

        final EnrollmentPartialInfos enrollPartialInfo = new EnrollmentPartialInfos(foundEe, enrollmentInfo, algorithmKeys);
        logger.info("getEnrollmentEntityInfo() returns: Entity[{}], EnrollmentServerInfo[{}], KeySize[{}]",
                NscsPkiEntitiesManagerJar.getEntityLog(enrollPartialInfo.getEndEntity()),
                NscsPkiEntitiesManagerJar.getEnrollmentInfoLog(enrollPartialInfo.getEnrollmentServerInfo()), enrollPartialInfo.getKeySize());
        return enrollPartialInfo;
    }

    /**
     * @param entityName
     * @param commonName
     * @param enrollmentMode
     */
    private void validateSubjectForEnrollmentType(final String entityName, final String commonName, final EnrollmentType enrollmentType)
            throws NscsPkiEntitiesManagerException {

        if ((!entityName.equals(commonName)) && enrollmentType == EnrollmentType.scep) {
            final String errorMsg = String.format("[" + entityName + "] Enrollment Type SCEP doesn't support CommonName option");
            logger.error(errorMsg);
            throw new NscsPkiEntitiesManagerException(errorMsg);
        }
    }

    /**
     * Get default OTP count from NSCS Capability Model for the given node model info.
     * @param nodeModelInfo the node model info.
     * @return the default OTP count.
     * @throws {@ link NscsPkiEntitiesManagerException} if conversion to Integer fails.
     */
    private Integer getDefaultOtpCount(final NodeModelInformation nodeModelInfo) throws NscsPkiEntitiesManagerException {
        Integer otpCount = null;
        final String otpCountStr = capabilityModel.getDefaultInitialOtpCount(nodeModelInfo);
        try {
            otpCount = Integer.parseInt(otpCountStr);
        } catch (final NumberFormatException numberFormatException) {
            final String errorMsg = "Parse Error: Exception occurred while parsing otpCount [" + otpCountStr + "]";
            logger.error(errorMsg);
            throw new NscsPkiEntitiesManagerException(errorMsg);
        }
        return otpCount;
    }

    /**
     * Get default OTP validity period in minutes from NSCS Capability Model for the given node model info.
     * @param nodeModelInfo the node model info.
     * @return the default OTP validity period in minutes.
     * @throws {@ link NscsPkiEntitiesManagerException} if conversion to Integer fails.
     */
    private Integer getDefaultOtpValidityPeriodInMinutes(final NodeModelInformation nodeModelInfo) throws NscsPkiEntitiesManagerException {
        Integer otpValidityPeriodInMinutes = null;
        final String otpValidityPeriodInMinutesStr = capabilityModel.getDefaultOtpValidityPeriodInMinutes(nodeModelInfo);
        try {
            otpValidityPeriodInMinutes = Integer.parseInt(otpValidityPeriodInMinutesStr);
        } catch (final NumberFormatException numberFormatException) {
            final String errorMsg = "Parse Error: Exception occurred while parsing otpValidityPeriodInMinutes [" + otpValidityPeriodInMinutesStr + "]";
            logger.error(errorMsg);
            throw new NscsPkiEntitiesManagerException(errorMsg);
        }
        return otpValidityPeriodInMinutes;
    }

    /**
     *
     * @param caName
     * @return
     * @throws NscsPkiEntitiesManagerException
     */
    @Override
    public X509Certificate findPkiRootCACertificate(final String caName) throws NscsPkiEntitiesManagerException {
        return nscsPkiEntitiesManagerRisc.getCachedRootCACertificate(caName);
    }

    /**
     *
     * @param caName
     * @return pkiRootCaName
     * @throws NscsPkiEntitiesManagerException
     */
    @Override
    public String findPkiRootCAName(final String caName) throws NscsPkiEntitiesManagerException {
        return nscsPkiEntitiesManagerRisc.getCachedRootCAName(caName);
    }

    @Override
    public Entity createEntity(final Entity ent) throws NscsPkiEntitiesManagerException {
        return nscsPkiEntitiesManagerRisc.createEntity(ent);
    }

    @Override
    public void deleteEntity(final String subjectName) throws NscsPkiEntitiesManagerException {

        if (!nscsPkiEntitiesManagerRisc.isEntityNameAvailable(subjectName, EntityType.ENTITY)) {
            final Entity ent = new Entity();
            final EntityInfo ei = new EntityInfo();
            ei.setName(subjectName);
            ent.setEntityInfo(ei);
            ent.setType(EntityType.ENTITY);
            final Entity delEnt = nscsPkiEntitiesManagerRisc.getEntity(ent);
            if ((delEnt != null) && (delEnt.getEntityInfo() != null)) {
                if (!EntityStatus.DELETED.equals(delEnt.getEntityInfo().getStatus())) {
                    nscsPkiEntitiesManagerRisc.deleteEntity(subjectName);
                } else {
                    logger.info("deleteEntity() : Entity already in DELETED status");
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.nms.security.nscs.ejb.pkiwrap.NscsPkiEntitiesManagerIF# getEntities()
     */
    @Override
    public List<Entity> getEntities() throws NscsPkiEntitiesManagerException {
        return nscsPkiEntitiesManagerRisc.getEntities();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.nms.security.nscs.ejb.pkiwrap.NscsPkiEntitiesManagerIF# getPkiEntity(java.lang.String)
     */
    @Override
    public Entity getPkiEntity(final String subjectName) throws NscsPkiEntitiesManagerException {
        return nscsPkiEntitiesManagerRisc.getPkiEntity(subjectName);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.nms.security.nscs.ejb.pkiwrap.NscsPkiEntitiesManagerIF# getCAEntity(java.lang.String)
     */
    @Override
    public CAEntity getCAEntity(final String subjectName) throws NscsPkiEntitiesManagerException {
        return nscsPkiEntitiesManagerRisc.getCachedCAEntity(subjectName);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.nms.security.nscs.ejb.pkiwrap.NscsPkiEntitiesManagerIF# getEntityProfile(java.lang.String)
     */
    @Override
    public EntityProfile getEntityProfile(final String entityProfileName) throws NscsPkiEntitiesManagerException {
        return nscsPkiEntitiesManagerRisc.getCachedEntityProfile(entityProfileName);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.nms.security.nscs.ejb.pkiwrap.NscsPkiEntitiesManagerIF# isEntityProfileNameAvailable(java.lang.String)
     */
    @Override
    public boolean isEntityProfileNameAvailable(final String entityProfileName) throws NscsPkiEntitiesManagerException {
        return nscsPkiEntitiesManagerRisc.isEntityProfileNameAvailable(entityProfileName);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.nms.security.nscs.ejb.pkiwrap.NscsPkiEntitiesManagerIF# getCAsTrusts()
     */
    @Override
    public Map<String, List<X509Certificate>> getCAsTrusts() throws NscsPkiEntitiesManagerException {
        return nscsPkiEntitiesManagerRisc.getCAsTrusts();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.nms.security.nscs.ejb.pkiwrap.NscsPkiEntitiesManagerIF# getCACertificates(java.util.List)
     */
    // @Override
    // public Map<String, List<X509Certificate>> getCACertificates(final
    // List<CAEntity> caEntities) {
    // return nscsPkiEntitiesManagerRisc.getCACertificates(caEntities);
    // }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.nms.security.nscs.ejb.pkiwrap.NscsPkiEntitiesManagerIF# getCATrusts(java.lang.String)
     */
    @Override
    public List<X509Certificate> getCATrusts(final String caName) throws NscsPkiEntitiesManagerException {
        logger.info("NscsPkiEntitiesManager::getCATrusts() [{}]", caName);

        List<X509Certificate> x509List;
        x509List = nscsPkiEntitiesManagerRisc.getInternalCATrusts(caName);

        if ((x509List == null) || x509List.isEmpty()) {
            x509List = nscsPkiEntitiesManagerRisc.getExternalCATrusts(caName);
        }
        return x509List;
    }

    // public List<String> getTrustedCAsCPPCorba() {
    // logger.info("NscsPkiEntitiesManager::getTrustedCAsCPPCorba() ");
    // return getTrustedCAs(getOamEntityProfileName());
    // }
    //
    // public List<String> getTrustedCAsCPPIpSec() {
    // logger.info("NscsPkiEntitiesManager::getTrustedCAsCPPIpSec() ");
    // return getTrustedCAs(getIpsecEntityProfileName());
    // }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.nms.security.nscs.ejb.pkiwrap.NscsPkiEntitiesManagerIF# getEntityOTP(java.lang.String)
     */
    @Override
    public String getEntityOTP(final String entityName) throws NscsPkiEntitiesManagerException {
        return nscsPkiEntitiesManagerRisc.getEntityOTP(entityName);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.nms.security.nscs.ejb.pkiwrap.NscsPkiEntitiesManagerIF# useMockEntityManager()
     */
    @Override
    public boolean useMockEntityManager() {
        return nscsPkiEntitiesManagerRisc.useMockEntityManager();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.nms.security.nscs.ejb.pkiwrap.NscsPkiEntitiesManagerIF# useMockProfileManager()
     */
    @Override
    public boolean useMockProfileManager() {
        return nscsPkiEntitiesManagerRisc.useMockProfileManager();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.nms.security.nscs.ejb.pkiwrap.NscsPkiEntitiesManagerIF# useMockCertificateManager()
     */
    @Override
    public boolean useMockCertificateManager() {
        return nscsPkiEntitiesManagerRisc.useMockCertificateManager();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.nms.security.nscs.ejb.pkiwrap.NscsPkiEntitiesManagerIF# getTrustedCAs(java.lang.String)
     */
    @Override
    public Set<NscsPair<String, Boolean>> getTrustedCAs(final String entityProfileName) throws NscsPkiEntitiesManagerException {
        return nscsPkiEntitiesManagerRisc.getTrustedCAs(entityProfileName);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.nms.security.nscs.ejb.pkiwrap.NscsPkiEntitiesManagerIF# getTrustedCAs(java.lang.String)
     */
    @Override
    public Set<CertSpec> getTrustCertificatesFromProfile(final String entityProfileName) throws NscsPkiEntitiesManagerException {

        logger.info("NscsPkiEntitiesManager::getTrustedCertificates({}) ", entityProfileName);
        final Set<CertSpec> trustedCertSpecs = new HashSet<>();
        EntityProfile epOut;
        epOut = nscsPkiEntitiesManagerRisc.getCachedEntityProfile(entityProfileName);
        if (epOut != null) {
            final List<TrustProfile> tpList = epOut.getTrustProfiles();
            logger.info("getTrustedCertificates({}) retrieved {} TrustProfiles", entityProfileName, tpList.size());

            for (final TrustProfile tp : tpList) {
                final List<TrustCAChain> caChainList = tp.getTrustCAChains();
                logger.info("getTrustedCertificates({}) retrieved {} TrustCAChains", entityProfileName, caChainList.size());
                // Certificate activeCert;
                List<X509Certificate> certDataList;

                for (final TrustCAChain trustCAChain : caChainList) {
                    final CertificateAuthority certAuthority = trustCAChain.getInternalCA().getCertificateAuthority();
                    // activeCert = certAuthority.getActiveCertificate();
                    certDataList = nscsPkiEntitiesManagerRisc.getInternalCATrusts(certAuthority.getName());

                    if ((certDataList != null) && !certDataList.isEmpty()) {
                        logger.info("getInternalCATrusts retrieved {} certificates", certDataList.size());
                        try {
                            trustedCertSpecs.add(new CertSpec(certDataList.get(0)));
                        } catch (final CertificateException ex) {
                            final String errorMsg = "Exception in getX509Certificate(): " + ex.getMessage();
                            logger.error(errorMsg);
                            throw new NscsPkiEntitiesManagerException(errorMsg);
                        }
                        if (trustCAChain.isChainRequired()) {
                            final List<CertificateChain> trustCAChainList = nscsPkiEntitiesManagerRisc.getCachedCAChain(certAuthority.getName());
                            if ((trustCAChainList != null) && !trustCAChainList.isEmpty()) {
                                for (final CertificateChain certChain : trustCAChainList) {
                                    for (final Certificate cert : certChain.getCertificates()) {
                                        if (cert != null) {
                                            try {
                                                trustedCertSpecs.add(new CertSpec(cert.getX509Certificate()));
                                            } catch (final CertificateException ex) {
                                                final String errorMsg = "Exception in getX509Certificate(): " + ex.getMessage();
                                                logger.error(errorMsg);
                                                throw new NscsPkiEntitiesManagerException(errorMsg);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                final List<ExtCA> externalCAs = tp.getExternalCAs();
                logger.info("getTrustedCertificates({}) retrieved {} ExternalCAs", entityProfileName, externalCAs.size());
                for (final ExtCA extCA : externalCAs) {
                    // activeCert =
                    // extCA.getCertificateAuthority().getActiveCertificate();
                    // final ExtCA retExtCa =
                    // extCAManagementService.getExtCA(extCA);
                    if (extCA != null) {
                        // activeCert = certAuthority.getActiveCertificate();
                        certDataList = nscsPkiEntitiesManagerRisc.getExternalCATrusts(extCA.getCertificateAuthority().getName());
                        if ((certDataList != null) && !certDataList.isEmpty()) {
                            logger.info("getExternalCATrusts retrieved {} certificates", certDataList.size());
                            try {
                                trustedCertSpecs.add(new CertSpec(certDataList.get(0)));
                            } catch (final CertificateException ex) {
                                final String errorMsg = "Exception in getX509Certificate(): " + ex.getMessage();
                                logger.error(errorMsg);
                                throw new NscsPkiEntitiesManagerException(errorMsg);
                            }
                        }
                    }
                }
            }
        }
        logger.info("getTrustedCertificates({}) retrieved {} certificates", entityProfileName, trustedCertSpecs.size());
        return trustedCertSpecs;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.nms.security.nscs.ejb.pkiwrap.NscsPkiEntitiesManagerIF# getSmrsAccountTypeForNscs()
     */
    @Override
    public String getSmrsAccountTypeForNscs() {
        return nscsPkiEntitiesManagerRisc.getSmrsAccountTypeForNscs();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.nms.security.nscs.ejb.pkiwrap.NscsPkiEntitiesManagerIF#
     * getTrustDistributionPointUrl(com.ericsson.oss.itpf.security.pki.manager. model.entities.CAEntity)
     */
    @Override
    public TDPSUrlInfo getTrustDistributionPointUrls(final CAEntity caEntity) throws NscsPkiEntitiesManagerException {
        return nscsPkiEntitiesManagerRisc.getTrustDistributionPointUrls(caEntity);

    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.nms.security.nscs.ejb.pkiwrap.NscsPkiEntitiesManagerIF# revokeCertificateByIssuerName(java.lang.String, java.lang.String,
     * java.lang.String)
     */
    @Override
    public void revokeCertificateByIssuerName(final String issuerName, final String serialNumber, final String reason)
            throws NscsPkiEntitiesManagerException {
        nscsPkiEntitiesManagerRisc.revokeCertificateByIssuerName(issuerName, serialNumber, reason);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.nms.security.nscs.ejb.pkiwrap.NscsPkiEntitiesManagerIF# getEntityListByIssuerName(java.lang.String)
     */
    @Override
    public List<Entity> getEntityListByIssuerName(final String issuerName) throws NscsPkiEntitiesManagerException {
        return nscsPkiEntitiesManagerRisc.getEntityListByIssuerName(issuerName);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.nms.security.nscs.ejb.pkiwrap.NscsPkiEntitiesManagerIF# isEntityNameAvailable(java.lang.String,
     * com.ericsson.oss.itpf.security.pki.manager.model.EntityType)
     */
    @Override
    public boolean isEntityNameAvailable(final String entityName, final EntityType entityType) throws NscsPkiEntitiesManagerException {
        return nscsPkiEntitiesManagerRisc.isEntityNameAvailable(entityName, entityType);
    }

    @Override
    public boolean isExtCaNameAvailable(final String entityName) throws NscsPkiEntitiesManagerException {
        return nscsPkiEntitiesManagerRisc.isExtCaNameAvailable(entityName);
    }

    /**
     * Get list of trusted certificates for a given entity.
     *
     * @param entityName
     *            the entity name.
     * @return the list of trusted certificates.
     * @throws NscsPkiEntitiesManagerException
     */
    @Override
    public List<Certificate> getTrustCertificates(final String entityName) throws NscsPkiEntitiesManagerException {
        return nscsPkiEntitiesManagerRisc.getTrustCertificates(entityName);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.nms.security.nscs.ejb.pkiwrap.NscsPkiEntitiesManagerIF# getEntitiesByCategoryWithInvalidCertificate(java.util.Date, int,
     * com.ericsson.nms.security.nscs.cpp.service.CppSecurityService. NodeEntityCategory)
     */
    @Override
    public List<Entity> getEntitiesByCategoryWithInvalidCertificate(final Date validity, final int maxNeNumber,
            final NodeEntityCategory... nodeEntityCategories) throws NscsPkiEntitiesManagerException {

        return nscsPkiEntitiesManagerRisc.getEntitiesByCategoryWithInvalidCertificate(validity, maxNeNumber, nodeEntityCategories);
    }

    @Override
    public TrustedEntityInfo getTrustedCAInfoByName(final String caName) throws NscsPkiEntitiesManagerException {
        final Set<TrustedEntityInfo> trustedCAInfos = nscsPkiEntitiesManagerRisc.getTrustedCAInfos(new NscsPair<String, Boolean>(caName, false));
        final Iterator<TrustedEntityInfo> itTrustedCAInfos = trustedCAInfos.iterator();
        return itTrustedCAInfos.next();
    }

    @Override
    public Set<TrustedEntityInfo> getTrustedCAsInfoByEntityProfileName(final String entityProfileName) throws NscsPkiEntitiesManagerException {
        final Set<TrustedEntityInfo> trustedCAsInfo = new HashSet<TrustedEntityInfo>();
        final Set<NscsPair<String, Boolean>> trustedCAsPair = nscsPkiEntitiesManagerRisc.getTrustedCAs(entityProfileName);
        for (final NscsPair<String, Boolean> trustedCAPair : trustedCAsPair) {
            final Set<TrustedEntityInfo> trustedCAInfos = nscsPkiEntitiesManagerRisc.getTrustedCAInfos(trustedCAPair);
            trustedCAsInfo.addAll(trustedCAInfos);
        }
        return trustedCAsInfo;
    }


    private void validateAndSetKeyGenerationAlgorithm(final AlgorithmKeys algorithmKeys, final EntityProfile entityProfile, final Entity foundEe)
            throws NscsPkiEntitiesManagerException {

        validateSupportedAlgorithmAndKeySize(algorithmKeys.toString(), entityProfile);
        // Set keyGenerationAlgorithm to Entity
        final Algorithm keyGenAlgorithm = new Algorithm();
        keyGenAlgorithm.setName(algorithmKeys.getAlgorithm());
        keyGenAlgorithm.setKeySize(algorithmKeys.getKeySize());
        foundEe.setKeyGenerationAlgorithm(keyGenAlgorithm);
        logger.debug("Modified keyGenerationAlgorithm in Entity");
    }

    private Subject buildEntitySubjectFromProfile(final String entityName, final String commonNamePrefix, final String currentCommonName,
            final EntityProfile ep, final boolean isPicoRbsNode, final EnrollmentType enrollmentType) throws NscsPkiEntitiesManagerException {

        logger.info("buildEntitySubjectFromProfile: entityName[{}]  commonName[{}] currentCommonName[{}]", entityName, commonNamePrefix,
                currentCommonName);
        List<SubjectField> epSubjectFieldList = null;
        Subject epSubject = null;
        if (ep != null) {
            epSubject = ep.getSubject();
            if (epSubject != null) {
                epSubjectFieldList = epSubject.getSubjectFields();
            }
        }
        String commonNameValue;
        if (currentCommonName == null || currentCommonName.isEmpty()) {
            commonNameValue = entityName;
        } else {
            commonNameValue = currentCommonName;
        }
        if ((commonNamePrefix != null) && !commonNamePrefix.isEmpty()) {
            commonNameValue = commonNamePrefix;
            if (isPicoRbsNode) {
                // For PicoRBS nodes
                // CN=SerialNumber.<EP_ORGANIZZATION>.<EP_COUNTRY_NAME>
                // Check if input parameter already contains Org and Country
                // fields
                final int dotCount = commonNamePrefix.length() - commonNamePrefix.replace(".", "").length();
                if (dotCount < 2) {
                    final String orgValue = getFieldFromSubject(epSubject, SubjectFieldType.ORGANIZATION);
                    final String countryValue = getFieldFromSubject(epSubject, SubjectFieldType.COUNTRY_NAME);
                    if ((orgValue != null) && !orgValue.isEmpty()) {
                        commonNameValue += "." + orgValue;
                    }
                    if ((countryValue != null) && !countryValue.isEmpty()) {
                        commonNameValue += "." + countryValue;
                    }
                }
            }
        }

        validateSubjectForEnrollmentType(entityName, commonNameValue, enrollmentType);

        final SubjectField subjectFieldCN = new SubjectField();
        subjectFieldCN.setType(SubjectFieldType.COMMON_NAME);
        subjectFieldCN.setValue(commonNameValue);

        final List<SubjectField> entSubjectFieldList = new ArrayList<>();
        entSubjectFieldList.add(subjectFieldCN);

        if ((epSubjectFieldList != null) && (!epSubjectFieldList.isEmpty())) {
            final ListIterator<SubjectField> it = epSubjectFieldList.listIterator();
            SubjectField subjField;
            while (it.hasNext()) {
                subjField = it.next();
                if (!(subjField.getType().equals(SubjectFieldType.COMMON_NAME))) {
                    entSubjectFieldList.add(subjField);
                }
            }
        }

        final Subject subject = new Subject();
        subject.setSubjectFields(entSubjectFieldList);

        return subject;
    }

    private List<SubjectAltNameField> validateSubjectAltNameValueFromProfile(final EntityProfile ep,
            final SubjectAltNameFieldType subjectAltNameFormat, final AbstractSubjectAltNameFieldValue subjectAltNameValue)
            throws NscsPkiEntitiesManagerException {
        List<SubjectAltNameField> retSubjectAltNameFieldList = null;

        if ((ep != null) && (subjectAltNameFormat != null)) {
            final SubjectAltName epSubjectAltNameValues = ep.getSubjectAltNameExtension();

            if (epSubjectAltNameValues != null) {

                // SubjectAltName is specified in profile
                final List<SubjectAltNameField> epSubjectAltNameFieldList = epSubjectAltNameValues.getSubjectAltNameFields();

                if ((epSubjectAltNameFieldList != null) && !epSubjectAltNameFieldList.isEmpty()) {

                    // if (subjectAltNameFormat == null) {
                    // throw new NscsPkiEntitiesManagerException("Null
                    // subjectAltName format");
                    // }

                    for (final SubjectAltNameField epSubjectAltNameField : epSubjectAltNameFieldList) {
                        if (subjectAltNameFormat.equals(epSubjectAltNameField.getType())) {
                            final SubjectAltNameField retSubjectAltNameField = new SubjectAltNameField();
                            retSubjectAltNameField.setType(subjectAltNameFormat);
                            if (subjectAltNameValue != null) { // subjectAltNameValue
                                // passed as
                                // parameter
                                retSubjectAltNameField.setValue(subjectAltNameValue);
                            } else { // subjectAltNameValue not passed as
                                         // parameter
                                final AbstractSubjectAltNameFieldValue epSanv = epSubjectAltNameField.getValue();
                                if (epSanv.getClass().equals(SubjectAltNameString.class)) {
                                    final SubjectAltNameString epSanvString = (SubjectAltNameString) epSanv;
                                    if (!(epSanvString.getValue().equals("?"))) {
                                        retSubjectAltNameField.setValue(epSubjectAltNameField.getValue());
                                    } else {
                                        break;
                                    }
                                }
                            }
                            retSubjectAltNameFieldList = new ArrayList<>();
                            retSubjectAltNameFieldList.add(retSubjectAltNameField);
                            break;
                        }
                    }
                }
            }
        }
        return retSubjectAltNameFieldList;
    }

    private List<SubjectAltNameField> buildSubjectAltNameValueFromEntitySubject(final Subject entitySubject) {
        logger.debug("buildSubjectAltNameValueFromEntitySubject: EntitySubject[{}]", entitySubject);
        List<SubjectAltNameField> retSubjectAltNameFieldList = null;
        if (entitySubject != null) {
            // For picoRbs default subjectAltName=subjectCurrent.COMMON_NAME
            String subjectAltNameValue = null;
            for (final SubjectField sf : entitySubject.getSubjectFields()) {
                if (sf.getType().equals(SubjectFieldType.COMMON_NAME)) {
                    subjectAltNameValue = sf.getValue();
                    // Check if COMMON_NAME can be a valid DNS name
                    final int dotCount = subjectAltNameValue.length() - subjectAltNameValue.replace(".", "").length();
                    if (dotCount < 2) {
                        // COMMON_NAME is not valid DNS name
                        subjectAltNameValue = null;
                    }
                    break;
                }
            }
            if ((subjectAltNameValue != null) && !subjectAltNameValue.isEmpty()) {
                final SubjectAltNameField retSubjectAltNameField = new SubjectAltNameField();
                final SubjectAltNameString sanString = new SubjectAltNameString();
                sanString.setValue(subjectAltNameValue);
                retSubjectAltNameField.setType(SubjectAltNameFieldType.DNS_NAME); // default
                                                                                  // for
                                                                                  // pico
                                                                                  // nodes
                retSubjectAltNameField.setValue(sanString);
                retSubjectAltNameFieldList = new ArrayList<>();
                retSubjectAltNameFieldList.add(retSubjectAltNameField);
            }
        }
        return retSubjectAltNameFieldList;
    }

    private String getFieldFromSubject(final Subject subject, final SubjectFieldType field) {
        String fieldValue = null;
        if ((subject != null) && (field != null)) {
            final List<SubjectField> subjectFieldList = subject.getSubjectFields();
            if ((subjectFieldList != null) && (!subjectFieldList.isEmpty())) {
                final ListIterator<SubjectField> it = subjectFieldList.listIterator();
                SubjectField subjField;
                while (it.hasNext()) {
                    subjField = it.next();
                    if (subjField.getType().equals(field)) {
                        fieldValue = subjField.getValue();
                        break;
                    }
                }
            }
        }
        return fieldValue;
    }

    private SubjectAltName buildSubjectAltName(final EntityProfile ep, final SubjectAltNameFieldType pkiSubjectAltNameFormat,
            final AbstractSubjectAltNameFieldValue pkiSubjectAltNameValue, final Subject entitySubject) throws NscsPkiEntitiesManagerException {
        logger.debug("buildSubjectAltName: SubjectAltNameFormat[{}] SubjectAltNameValue[{}] EntitySubject[{}]", pkiSubjectAltNameFormat,
                pkiSubjectAltNameValue, entitySubject);
        SubjectAltName subjectAltNameValues = null;
        List<SubjectAltNameField> subjectAltNameFieldList = null;
        if ((entitySubject != null)) {
            if (!entitySubject.getSubjectFields().isEmpty() && ((pkiSubjectAltNameFormat == null) || (pkiSubjectAltNameValue == null))) {
                // For picoRbs default subjectAltName=subjectCurrent.COMMON_NAME
                subjectAltNameFieldList = buildSubjectAltNameValueFromEntitySubject(entitySubject);
            }
        }
        if (subjectAltNameFieldList == null) {
            subjectAltNameFieldList = validateSubjectAltNameValueFromProfile(ep, pkiSubjectAltNameFormat, pkiSubjectAltNameValue);
        }
        if ((subjectAltNameFieldList != null) && (!subjectAltNameFieldList.isEmpty())) {
            subjectAltNameValues = new SubjectAltName();
            subjectAltNameValues.setSubjectAltNameFields(subjectAltNameFieldList);
        }
        return subjectAltNameValues;
    }

    private static String randomPassword(final int length) {
        final Random rand = new Random();
        final StringBuilder buf = new StringBuilder();
        final StringBuilder chars = new StringBuilder();
        for (int i = 48; i <= 57; i++) {
            chars.append((char) i);
        }
        for (int i = 65; i <= 90; i++) {
            chars.append((char) i);
        }
        for (int i = 97; i <= 122; i++) {
            chars.append((char) i);
        }
        final String charsToPassword = chars.toString();

        for (int i = 0; i < length; i++) {
            buf.append(charsToPassword.charAt((rand.nextInt(charsToPassword.length()))));
        }
        return buf.toString();
    }

    private void validateSupportedAlgorithmAndKeySize(final String value, final EntityProfile entityProfile) throws NscsPkiEntitiesManagerException {

        final List<Algorithm> algorithms = entityProfile.getCertificateProfile().getKeyGenerationAlgorithms();
        final List<String> supportedAlgorithmAndKeySizeValues = new ArrayList<>();
        if (algorithms != null) {
            for (final Algorithm algorithm : algorithms) {
                final AlgorithmKeys algorithmKey = AlgorithmKeys.toAlgorithmKeys(algorithm.getName(), algorithm.getKeySize());
                logger.debug("Supported Algorithm with name [{}] and keySize [{}] is [{}]", algorithm.getName(), algorithm.getKeySize(), algorithmKey);
                if (algorithmKey != null) {
                    supportedAlgorithmAndKeySizeValues.add(algorithmKey.toString());
                }
            }

            if (!supportedAlgorithmAndKeySizeValues.contains(value)) {
                logger.error(NscsErrorCodes.KEY_ALGORITHM_NOT_SUPPORTED_BY_ENTITY_PROFILE, value, entityProfile.getName(), supportedAlgorithmAndKeySizeValues);
                throw new NscsPkiEntitiesManagerException("The given Key Algorithm [" + value + "] is not in supported list of Entity Profile [" + entityProfile.getName() + "]. "
                        + "Accepted Key Algorithms are " + supportedAlgorithmAndKeySizeValues);
            }

        } else {
            logger.error("entityProfile.getCertificateProfile().getKeyGenerationAlgorithms() is null!!!");
        }
    }
}
