/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.nms.security.nscs.pki;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.pki.NodeEntityCategory;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerException;
import com.ericsson.nms.security.nscs.api.util.NscsPair;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.itpf.security.pki.common.model.CertificateAuthority;
import com.ericsson.oss.itpf.security.pki.common.model.EntityInfo;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.Certificate;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.CertificateIdentifier;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.CertificateStatus;
import com.ericsson.oss.itpf.security.pki.common.model.crl.revocation.RevocationReason;
import com.ericsson.oss.itpf.security.pki.manager.exception.EntityException;
import com.ericsson.oss.itpf.security.pki.manager.exception.PKIBaseException;
import com.ericsson.oss.itpf.security.pki.manager.exception.configuration.PKIConfigurationServiceException;
import com.ericsson.oss.itpf.security.pki.manager.exception.configuration.algorithm.AlgorithmNotFoundException;
import com.ericsson.oss.itpf.security.pki.manager.exception.crl.CRLExtensionException;
import com.ericsson.oss.itpf.security.pki.manager.exception.crl.CRLGenerationException;
import com.ericsson.oss.itpf.security.pki.manager.exception.crl.InvalidCRLGenerationInfoException;
import com.ericsson.oss.itpf.security.pki.manager.exception.crl.UnsupportedCRLVersionException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.EntityAlreadyDeletedException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.EntityAlreadyExistsException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.EntityNotFoundException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.EntityServiceException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.InvalidEntityAttributeException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.InvalidEntityException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.caentity.CANotFoundException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.caentity.InvalidCAException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.category.EntityCategoryNotFoundException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.category.InvalidEntityCategoryException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.endentity.otp.OTPExpiredException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.endentity.otp.OTPNotSetException;
import com.ericsson.oss.itpf.security.pki.manager.exception.external.credentialmgmt.ExternalCredentialMgmtServiceException;
import com.ericsson.oss.itpf.security.pki.manager.exception.profile.InvalidProfileAttributeException;
import com.ericsson.oss.itpf.security.pki.manager.exception.profile.InvalidProfileException;
import com.ericsson.oss.itpf.security.pki.manager.exception.profile.ProfileNotFoundException;
import com.ericsson.oss.itpf.security.pki.manager.exception.profile.ProfileServiceException;
import com.ericsson.oss.itpf.security.pki.manager.exception.revocation.InvalidInvalidityDateException;
import com.ericsson.oss.itpf.security.pki.manager.exception.revocation.IssuerNotFoundException;
import com.ericsson.oss.itpf.security.pki.manager.exception.revocation.RevocationServiceException;
import com.ericsson.oss.itpf.security.pki.manager.exception.revocation.RootCertificateRevocationException;
import com.ericsson.oss.itpf.security.pki.manager.exception.security.certificate.CertificateNotFoundException;
import com.ericsson.oss.itpf.security.pki.manager.exception.security.certificate.CertificateServiceException;
import com.ericsson.oss.itpf.security.pki.manager.exception.security.certificate.ExpiredCertificateException;
import com.ericsson.oss.itpf.security.pki.manager.exception.security.certificate.InvalidCertificateStatusException;
import com.ericsson.oss.itpf.security.pki.manager.exception.security.certificate.RevokedCertificateException;
import com.ericsson.oss.itpf.security.pki.manager.exception.security.certificate.certificatefield.InvalidSubjectException;
import com.ericsson.oss.itpf.security.pki.manager.exception.security.certificate.certificatefield.MissingMandatoryFieldException;
import com.ericsson.oss.itpf.security.pki.manager.exception.security.certificate.certificatefield.certificateextension.InvalidSubjectAltNameExtension;
import com.ericsson.oss.itpf.security.pki.manager.exception.trustdistributionpoint.TrustDistributionPointURLNotDefinedException;
import com.ericsson.oss.itpf.security.pki.manager.exception.trustdistributionpoint.TrustDistributionPointURLNotFoundException;
import com.ericsson.oss.itpf.security.pki.manager.model.CertificateChain;
import com.ericsson.oss.itpf.security.pki.manager.model.EnrollmentInfo;
import com.ericsson.oss.itpf.security.pki.manager.model.EnrollmentType;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityCategory;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityEnrollmentInfo;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityType;
import com.ericsson.oss.itpf.security.pki.manager.model.ProfileType;
import com.ericsson.oss.itpf.security.pki.manager.model.TDPSUrlInfo;
import com.ericsson.oss.itpf.security.pki.manager.model.TrustCAChain;
import com.ericsson.oss.itpf.security.pki.manager.model.TrustedEntityInfo;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.AbstractEntity;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.CAEntity;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entities;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.ExtCA;
import com.ericsson.oss.itpf.security.pki.manager.model.profiles.EntityProfile;
import com.ericsson.oss.itpf.security.pki.manager.model.profiles.TrustProfile;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

/**
 *
 * @author enmadmin
 */
public class NscsPkiEntitiesManagerJar {

    @Inject
    Logger logger;

    @Inject
    private NscsContextService nscsContextService;

    @Inject
    NscsCapabilityModelService capabilityModel;

    @Inject
    PkiApiManagers pkiApiManagers;

    private static final String SMRS_ACCOUNT_TYPE = "CERTIFICATES";
    private static final String NSCS_CONTEXT_USER_NAME = "NSCS";
    private static final Map<NodeEntityCategory, String> entityCategoryMap = new HashMap<>();

    static {
        entityCategoryMap.put(NodeEntityCategory.OAM, "NODE-OAM");
        entityCategoryMap.put(NodeEntityCategory.IPSEC, "NODE-IPSEC");
    }

    private void setContextData() {
        nscsContextService.setUserNameContextValue(NSCS_CONTEXT_USER_NAME);
    }

    public X509Certificate findPkiRootCACertificate(final String caName) throws NscsPkiEntitiesManagerException {

        if (caName != null) {
            final List<CertificateChain> certificateChainList = getCAChain(caName);
            for (final CertificateChain certificateChain : certificateChainList) {
                if (certificateChain.getCertificates() != null && !certificateChain.getCertificates().isEmpty()) {
                    final int sizeChain = certificateChain.getCertificates().size();
                    final Certificate cert = certificateChain.getCertificates().get(sizeChain - 1);
                    logger.debug("Root CA Certificate : " + cert.getSubject() + " SerialNumber: " + cert.getSerialNumber());
                    return cert.getX509Certificate();
                }
            }
        }
        return null;
    }

    /**
     * @param caName
     * @return List<CertificateChain>
     * @throws NscsPkiEntitiesManagerException
     */
    public List<CertificateChain> getCAChain(final String caName) throws NscsPkiEntitiesManagerException {
        logger.info("getCAChain() : entity name[{}]", caName);
        setContextData();

        try {
            return pkiApiManagers.getCACertificateManagementService().getCertificateChainList(caName, CertificateStatus.ACTIVE);
        } catch (CertificateServiceException | InvalidCAException | InvalidCertificateStatusException | InvalidEntityException
                | InvalidEntityAttributeException e) {
            final String err = "getCertificateChainList() : caught PKI exception " + e.getMessage();
            logger.error(err);
            throw new NscsPkiEntitiesManagerException(err);
        }
    }

    public Entity createEntity(final Entity ent) throws NscsPkiEntitiesManagerException {
        setContextData();

        try {
            return pkiApiManagers.getEntityManagementService().createEntity_v1(ent);
        } catch (final PKIBaseException e) {
            final String err = "createEntity() : caught PKI exception " + e.getMessage();
            logger.error(err);
            throw new NscsPkiEntitiesManagerException(err);
        }
    }

    public void deleteEntity(final String subjectName) throws NscsPkiEntitiesManagerException {
        logger.info("deleteEntity() : entity name[{}]", subjectName);
        setContextData();

        final EntityInfo entityInfo = new EntityInfo();
        entityInfo.setName(subjectName);
        final Entity ent = new Entity();
        ent.setType(EntityType.ENTITY);
        ent.setEntityInfo(entityInfo);
        try {
            pkiApiManagers.getEntityManagementService().deleteEntity(ent);
        } catch (final EntityAlreadyDeletedException eax) { // Do not throw exception
            logger.info("deleteEntity() : entity [{}] is already in status DELETED", subjectName);
        } catch (final EntityNotFoundException efx) { // Do not throw exception
            logger.info("deleteEntity() : entity [{}] not found", subjectName);
        } catch (final EntityException e) {
            final String err = "deleteEntity() : caught PKI exception " + e.getMessage();
            logger.error(err);
            throw new NscsPkiEntitiesManagerException(err, e);
        }
    }

    public List<Entity> getEntities() throws NscsPkiEntitiesManagerException {
        setContextData();

        Entities pkiEntities;
        try {
            pkiEntities = pkiApiManagers.getEntityManagementService().getEntities(EntityType.ENTITY);
            return pkiEntities.getEntities();
        } catch (EntityServiceException | InvalidEntityException | InvalidEntityAttributeException e) {
            final String err = "getEntities() : caught PKI exception " + e.getMessage();
            logger.error(err);
            throw new NscsPkiEntitiesManagerException(err, e);
        }

    }

    public <T extends AbstractEntity> T getEntity(final T entity) throws NscsPkiEntitiesManagerException {
        setContextData();

        try {
            return pkiApiManagers.getEntityManagementService().getEntity(entity);
        } catch (EntityNotFoundException | EntityServiceException | InvalidEntityException | InvalidEntityAttributeException e) {
            final String err = "getEntity() : caught PKI exception " + e.getMessage();
            logger.error(err);
            throw new NscsPkiEntitiesManagerException(err, e);
        }
    }

    public <T extends AbstractEntity> T updateEntity(final T entity) throws NscsPkiEntitiesManagerException {
        setContextData();

        try {
            return pkiApiManagers.getEntityManagementService().updateEntity_v1(entity);
        } catch (InvalidSubjectAltNameExtension | InvalidSubjectException | MissingMandatoryFieldException | AlgorithmNotFoundException
                | EntityCategoryNotFoundException | InvalidEntityCategoryException | CRLExtensionException | CRLGenerationException
                | EntityAlreadyExistsException | EntityNotFoundException | EntityServiceException | InvalidCRLGenerationInfoException
                | InvalidEntityException | InvalidEntityAttributeException | InvalidProfileException | ProfileNotFoundException
                | UnsupportedCRLVersionException e) {
            final String err = "updateEntity() : caught PKI exception " + e.getMessage();
            logger.error(err);
            throw new NscsPkiEntitiesManagerException(err, e);
        }
    }

    public Entity getPkiEntity(final String entityName) throws NscsPkiEntitiesManagerException {
        logger.info("getPkiEntity() : entity name[{}]", entityName);
        setContextData();

        final EntityInfo entityInfo = new EntityInfo();
        entityInfo.setName(entityName);
        final Entity ent = new Entity();
        ent.setType(EntityType.ENTITY);
        ent.setEntityInfo(entityInfo);
        return getEntity(ent);
    }

    public CAEntity getCAEntity(final String caEntityName) throws NscsPkiEntitiesManagerException {
        logger.info("getCAEntity() : entity name[{}]", caEntityName);
        setContextData();

        final CAEntity ent = new CAEntity();
        ent.setType(EntityType.CA_ENTITY);
        ent.setCertificateAuthority(new CertificateAuthority());
        ent.getCertificateAuthority().setName(caEntityName);
        return getEntity(ent);
    }

    public EntityProfile getEntityProfile(final String entityProfileName) throws NscsPkiEntitiesManagerException {
        setContextData();

        final EntityProfile ep = new EntityProfile();
        ep.setName(entityProfileName);
        try {
            return pkiApiManagers.getProfileManagementService().getProfile(ep);
        } catch (MissingMandatoryFieldException | InvalidProfileException | InvalidProfileAttributeException | ProfileNotFoundException
                | ProfileServiceException e) {
            final String err = "getProfile() : caught PKI exception " + e.getMessage();
            logger.error(err);
            throw new NscsPkiEntitiesManagerException(err, e);
        }
    }

    public boolean isEntityProfileNameAvailable(final String entityProfileName) throws NscsPkiEntitiesManagerException {
        setContextData();

        try {
            return pkiApiManagers.getProfileManagementService().isProfileNameAvailable(entityProfileName, ProfileType.ENTITY_PROFILE);
        } catch (InvalidProfileException | ProfileServiceException e) {
            final String err = "isProfileNameAvailable() : caught PKI exception " + e.getMessage();
            logger.error(err);
            throw new NscsPkiEntitiesManagerException(err, e);
        }
    }

    // TODO cacheable
    public Map<String, List<X509Certificate>> getCAsTrusts() throws NscsPkiEntitiesManagerException {
        logger.info("NscsPkiEntitiesManager::getCAsTrusts() ");
        setContextData();

        Entities pkiEntities;
        try {
            pkiEntities = pkiApiManagers.getEntityManagementService().getEntities(EntityType.CA_ENTITY);
            final List<CAEntity> caEntities = pkiEntities.getCAEntities();
            final Map<String, List<X509Certificate>> caTrustMap = getCACertificates(caEntities);

            return caTrustMap;
        } catch (EntityServiceException | InvalidEntityException | InvalidEntityAttributeException e) {
            final String err = "getEntities() : caught PKI exception " + e.getMessage();
            logger.error(err);
            throw new NscsPkiEntitiesManagerException(err, e);
        }
    }

    private Map<String, List<X509Certificate>> getCACertificates(final List<CAEntity> caEntities) {
        logger.info("ProxyListCertificate getCACertificates");
        final Map<String, List<X509Certificate>> caTrustMap = new HashMap<>();
        for (final CAEntity cae : caEntities) {
            final List<X509Certificate> x509List = new ArrayList<>();
            logger.info("getting active certificates for CA [{}]", cae.getCertificateAuthority().getName());
            final Certificate certData = cae.getCertificateAuthority().getActiveCertificate();
            if (certData != null) {
                logger.info("certData Status: [{}]", certData.getStatus());
                x509List.add(certData.getX509Certificate());
                caTrustMap.put(cae.getCertificateAuthority().getName(), x509List);
            }
        }

        return caTrustMap;
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

    public String getEntityOTP(final String entityName) throws NscsPkiEntitiesManagerException {
        setContextData();

        try {
            return pkiApiManagers.getEntityManagementService().getOTP(entityName);
        } catch (OTPExpiredException | OTPNotSetException | EntityNotFoundException | EntityServiceException e) {
            final String err = "getOTP() : caught PKI exception " + e.getMessage();
            logger.error(err);
            throw new NscsPkiEntitiesManagerException(err, e);
        }
    }

    public boolean useMockEntityManager() {
        return NscsPkiMockManagement.useMockEntityManager();
    }

    public boolean useMockProfileManager() {
        return NscsPkiMockManagement.useMockProfileManager();
    }

    public boolean useMockCertificateManager() {
        return NscsPkiMockManagement.useMockCertificateManager();
    }

    /**
     * Get from PKI the list of trusted CA pairs (name and isChainRequired) for the given entity profile name. Internal and external trusted CA pairs
     * shall be returned.
     *
     * @param entityProfileName
     *            the entity profile name.
     * @return the list of trusted CA pairs
     * @throws NscsPkiEntitiesManagerException
     */
    public Set<NscsPair<String, Boolean>> getTrustedCAs(final String entityProfileName) throws NscsPkiEntitiesManagerException {

        final String inputParams = "entity profile name [" + entityProfileName + "]";
        logger.info("get TrustedCAs : starts for {}", inputParams);

        final Set<NscsPair<String, Boolean>> trustedCAsPairs = new HashSet<>();
        EntityProfile epOut;
        epOut = getEntityProfile(entityProfileName);
        if (epOut != null) {
            final List<TrustProfile> tpList = epOut.getTrustProfiles();
            logger.info("get TrustedCAs : retrieved [{}] trust profiles for {}", tpList.size(), inputParams);
            for (final TrustProfile tp : tpList) {
                final List<TrustCAChain> caChainList = tp.getTrustCAChains();
                logger.info(
                        "get TrustedCAs : retrieved [{}] internal trust CA chains for trust profile [{}]"
                        , caChainList.size(), tp.getName());
                for (final TrustCAChain trustChain : caChainList) {
                    final String caName = trustChain.getInternalCA().getCertificateAuthority().getName();
                    if (trustedCAsPairs.add(new NscsPair<String, Boolean>(caName, trustChain.isChainRequired()))) {
                        logger.info("get TrustedCAs : added internal trusted CA : caName [{}] isChainRequired [{}]"
                                , caName, trustChain.isChainRequired());
                    } else {
                        logger.info("get TrustedCAs : skipped already added internal trusted CA : caName [{}] isChainRequired [{}]"
                                , caName, trustChain.isChainRequired());
                    }
                }
                final List<ExtCA> extCAs = tp.getExternalCAs();
                for (final ExtCA extCA : extCAs) {
                    final String caName = extCA.getCertificateAuthority().getName();
                    if (trustedCAsPairs.add(new NscsPair<String, Boolean>(caName, false))) {
                        logger.info("get TrustedCAs : added external trusted CA [{}]", caName);
                    } else {
                        logger.info("get TrustedCAs : skipped already added external trusted CA [{}]", caName);
                    }
                }
            }
        }
        logger.info("get TrustedCAs : retrieved  [{}] trusted CAs for {}", trustedCAsPairs.size(), inputParams);
        return trustedCAsPairs;
    }

    /**
     * Get from PKI the set of trusted CA info for the given CA entity specified via the pair (caName and isChainRequired).
     *
     * In case of CA not requiring chain, the returned set contains only info about the active certificate of the trusted CA.
     *
     * In case of CA requiring chain, the returned set contains both info about the active certificate of the trusted CA and info about all
     * certificates of its chain (chain of the active certificate).
     *
     * @param caPair
     *            the CA pair (name and isChainRequired).
     * @return the set of trusted CA info.
     * @throws NscsPkiEntitiesManagerException
     */
    public Set<TrustedEntityInfo> getTrustedCAInfos(final NscsPair<String, Boolean> caPair) throws NscsPkiEntitiesManagerException {
        setContextData();
        final String caName = caPair.getL();
        final Boolean isChainRequired = caPair.getR();
        final String inputParams = "caName [" + caName + "] isChainRequired [" + isChainRequired + "]";
        logger.info("get TrustedCAInfos : starts for {}", inputParams);
        final Set<TrustedEntityInfo> trustedCAInfos = new HashSet<>();
        if (!isChainRequired) {
            List<TrustedEntityInfo> pkiTrustedCAInfosList = null;
            try {
                logger.info("get TrustedCAInfos : getting from PKI trusted CA infos for {}", caName);
                pkiTrustedCAInfosList = pkiApiManagers.getEntityManagementService().getTrustedEntitiesInfo(EntityType.CA_ENTITY, caName);
                logger.info("get TrustedCAInfos : from PKI [{}] trusted CA infos for [{}]"
                            , pkiTrustedCAInfosList.size(), caName);
            } catch (CertificateNotFoundException | EntityNotFoundException | EntityServiceException | TrustDistributionPointURLNotFoundException e) {
                final String errorMsg = NscsLogger.stringifyException(e) + " while getting from PKI trusted CA infos for [" + caName + "]";
                logger.error("get TrustedCAInfos : {}", errorMsg);
                throw new NscsPkiEntitiesManagerException(errorMsg);
            }
            final Set<TrustedEntityInfo> pkiTrustedCAInfos = new HashSet<>(pkiTrustedCAInfosList);
            logger.info("get TrustedCAInfos : after pruning [" + pkiTrustedCAInfos.size() + "] trusted CA infos for [" + caName + "]");
            final Iterator<TrustedEntityInfo> itPkiTrustedCAInfos = pkiTrustedCAInfos.iterator();
            boolean isTrustedCAActiveCertInfoFound = false;
            while (itPkiTrustedCAInfos.hasNext()) {
                final TrustedEntityInfo pkiTrustedCAInfo = itPkiTrustedCAInfos.next();
                if (caName.equals(pkiTrustedCAInfo.getEntityName()) && EntityType.CA_ENTITY.equals(pkiTrustedCAInfo.getEntityType())
                        && CertificateStatus.ACTIVE.equals(pkiTrustedCAInfo.getCertificateStatus())) {
                    if (!isTrustedCAActiveCertInfoFound) {
                        logger.info("get TrustedCAInfos : adding trusted CA active cert info for " + inputParams);
                        logger.info("entityName : " + pkiTrustedCAInfo.getEntityName());
                        logger.info("entityType : " + pkiTrustedCAInfo.getEntityType().name());
                        logger.info("certificateStatus : " + pkiTrustedCAInfo.getCertificateStatus().name());
                        logger.info("certificateSerialNumber : " + pkiTrustedCAInfo.getCertificateSerialNumber());
                        logger.info("subjectDN : " + pkiTrustedCAInfo.getSubjectDN());
                        logger.info("issuerFullDN : " + pkiTrustedCAInfo.getIssuerFullDN());
                        logger.info("issuerDN : " + pkiTrustedCAInfo.getIssuerDN());
                        logger.info("ipv4TrustDistributionPointURL : " + pkiTrustedCAInfo.getIpv4TrustDistributionPointURL());
                        logger.info("ipv6TrustDistributionPointURL : " + pkiTrustedCAInfo.getIpv6TrustDistributionPointURL());
                        isTrustedCAActiveCertInfoFound = true;
                        trustedCAInfos.add(pkiTrustedCAInfo);
                    } else {
                        final String errorMsg = "More than one trusted CA active cert info from PKI for [" + caName + "]";
                        logger.error("get TrustedCAInfos : {}", errorMsg);
                        throw new NscsPkiEntitiesManagerException(errorMsg);
                    }
                }
            }
            if (!isTrustedCAActiveCertInfoFound) {
                final String errorMsg = "No trusted CA active cert info from PKI for [" + caName + "]";
                logger.error("get TrustedCAInfos : {}", errorMsg);
                throw new NscsPkiEntitiesManagerException(errorMsg);
            }
        } else {
            List<List<TrustedEntityInfo>> pkiActiveChainsList = null;
            try {
                logger.info("get TrustedCAInfos : getting from PKI trusted CA active chains infos for [{}]", caName);
                pkiActiveChainsList = pkiApiManagers.getEntityManagementService().getTrustedEntitiesInfoChain(EntityType.CA_ENTITY, caName,
                        CertificateStatus.ACTIVE);
            } catch (CertificateNotFoundException | EntityNotFoundException | EntityServiceException | TrustDistributionPointURLNotFoundException e) {
                final String errorMsg = NscsLogger.stringifyException(e) + " while getting from PKI trusted CA active chains infos for [" + caName
                        + "]";
                logger.error("get TrustedCAInfos : {}", errorMsg);
                throw new NscsPkiEntitiesManagerException(errorMsg);
            }
            final int numPkiActiveChains = pkiActiveChainsList.size();
            if (numPkiActiveChains != 1) {
                final String errorMsg = "Wrong number [" + numPkiActiveChains + "] of trusted CA active chains from PKI for [" + caName + "]";
                logger.error("get TrustedCAInfos : {}", errorMsg);
                throw new NscsPkiEntitiesManagerException(errorMsg);
            }
            logger.info("get TrustedCAInfos : from PKI [{}] trusted CA active chains for [{}]", numPkiActiveChains, caName);
            final Iterator<List<TrustedEntityInfo>> itPkiActiveChainsList = pkiActiveChainsList.iterator();
            final List<TrustedEntityInfo> pkiActiveChainList = itPkiActiveChainsList.next();
            logger.info("get TrustedCAInfos : from PKI [" + pkiActiveChainList.size() + "] trusted CA infos on active chain for [" + caName + "]");
            trustedCAInfos.addAll(pkiActiveChainList);
            logger.info("get TrustedCAInfos : after pruning [" + trustedCAInfos.size() + "] trusted CA infos on active chain for [" + caName + "]");
            boolean isTrustedCAActiveInfoFound = false;
            final Iterator<TrustedEntityInfo> itTrustedCAInfos = trustedCAInfos.iterator();
            while (itTrustedCAInfos.hasNext()) {
                final TrustedEntityInfo trustedCAInfo = itTrustedCAInfos.next();
                if (EntityType.CA_ENTITY.equals(trustedCAInfo.getEntityType()) && caName.equals(trustedCAInfo.getEntityName())
                        && CertificateStatus.ACTIVE.equals(trustedCAInfo.getCertificateStatus())) {
                    if (!isTrustedCAActiveInfoFound) {
                        isTrustedCAActiveInfoFound = true;
                    } else {
                        final String errorMsg = "More than one trusted CA active info on active chain from PKI for [" + caName + "]";
                        logger.error("get TrustedCAInfos : {}", errorMsg);
                        throw new NscsPkiEntitiesManagerException(errorMsg);
                    }
                }
            }
            if (!isTrustedCAActiveInfoFound) {
                final String errorMsg = "No trusted CA active info on active chain from PKI for [" + caName + "]";
                logger.error("get TrustedCAInfos : {}", errorMsg);
                throw new NscsPkiEntitiesManagerException(errorMsg);
            }
        }

        logger.info("get TrustedCAInfos : returning  [" + trustedCAInfos.size() + "] trusted CA infos for " + inputParams);
        return trustedCAInfos;
    }

    public String getSmrsAccountTypeForNscs() {
        return SMRS_ACCOUNT_TYPE;
    }

    /**
     * @param caEntity
     * @return TDPS URL
     * @throws EntityNotFoundException
     * @throws EntityServiceException
     * @throws TrustDistributionPointURLNotDefinedException
     * @throws TrustDistributionPointURLNotFoundException
     */
    public TDPSUrlInfo getTrustDistributionPointUrls(final CAEntity caEntity) throws NscsPkiEntitiesManagerException {
        logger.info("Start getTrustDistributionPointUrl");
        String issuerName = null;
        String caEntityName = null;

        if (caEntity.getCertificateAuthority() == null) {
            logger.warn("PKI getTrustDistributionPointUrl null getCertificateAuthority() for caEntity[{}]", caEntity);
        } else {
            caEntityName = caEntity.getCertificateAuthority().getName();
            if (caEntity.getCertificateAuthority().getIssuer() == null) {
                String activeCertificateIssuer = null;
                // MS9: check active certificate issuer
                if (caEntity.getCertificateAuthority().getActiveCertificate() != null) {
                    if (caEntity.getCertificateAuthority().getActiveCertificate().getIssuer() != null) {
                        activeCertificateIssuer = caEntity.getCertificateAuthority().getActiveCertificate().getIssuer().getName();
                    }
                }
                if ((activeCertificateIssuer != null) && !activeCertificateIssuer.isEmpty()) {
                    issuerName = activeCertificateIssuer;
                } else {
                    issuerName = caEntityName; // self-signed CA
                }
                logger.warn(
                        "PKI getTrustDistributionPointUrl null getCertificateAuthority().getIssuer() for caEntityName[{}], setting issuername equals to caEntityName",
                        caEntityName);
            } else {
                issuerName = caEntity.getCertificateAuthority().getIssuer().getName();
            }
        }
        final CertificateStatus certificateStatus = CertificateStatus.ACTIVE;
        logger.info("PKI getTrustDistributionPointUrl for caEntityName[{}] issuerName[{}] and status[{}]", caEntityName, issuerName,
                certificateStatus);
        setContextData();

        TDPSUrlInfo tdpsUrlInfo = null;
        try {
            tdpsUrlInfo = pkiApiManagers.getEntityManagementService().getTrustDistributionPointUrls(caEntity, issuerName, certificateStatus);
        } catch (final PKIBaseException e) {
            logger.error("PKI getTrustDistributionPointUrl: caught exception[{}] msg[{}] for issuerName[{}] and status[{}]", e.getClass(),
                    e.getMessage(), issuerName, certificateStatus);
            tdpsUrlInfo = null;
        }
        logger.info("getTrustDistributionPointUrl: return [{}]", tdpsUrlInfo);
        return tdpsUrlInfo;

    }

    /**
     * @param issuerName
     *            The name of CA issuer of the certificate to revoke
     * @param serialNumber
     *            The serial number of the certificate to revoke
     * @param reason
     *            The reason of revocation. It must match the value expected in @link RevocationReason
     * @throws NscsPkiEntitiesManagerException
     */

    public void revokeCertificateByIssuerName(final String issuerName, final String serialNumber, final String reason)
            throws NscsPkiEntitiesManagerException {
        final CertificateIdentifier certIdentifier = new CertificateIdentifier();
        certIdentifier.setIssuerName(issuerName);
        certIdentifier.setSerialNumber(serialNumber);

        // need to convert from VALUE of NSCS RevocationReason to PKI
        // RevocationReason
        final com.ericsson.nms.security.nscs.api.enums.RevocationReason nscsRevocationReason = com.ericsson.nms.security.nscs.api.enums.RevocationReason
                .getRevocationReasonFromValue(reason);
        logger.info("converted value [{}] to Nscs RevocationReason [{}]", reason, nscsRevocationReason.name());

        final RevocationReason revocationReason = convertNscsRevocationReasonToPkiRevocationReason(nscsRevocationReason);
        logger.info("converted Nscs RevocationReason [{}] to PKI RevocationReason [{}]", nscsRevocationReason.name(), revocationReason.name());
        setContextData();

        try {
            pkiApiManagers.getPkiRevocationService().revokeCertificateByIssuerName(certIdentifier, revocationReason, null);
        } catch (CertificateNotFoundException | ExpiredCertificateException | RevokedCertificateException | EntityNotFoundException
                | IssuerNotFoundException | RevocationServiceException | RootCertificateRevocationException | EntityAlreadyExistsException
                | InvalidEntityAttributeException | InvalidInvalidityDateException e) {
            final String err = "getOTP() : caught PKI exception " + e.getMessage();
            logger.error(err);
            throw new NscsPkiEntitiesManagerException(err, e);
        }
    }

    private RevocationReason convertNscsRevocationReasonToPkiRevocationReason(
            final com.ericsson.nms.security.nscs.api.enums.RevocationReason nscsRevocationReason) {

        RevocationReason revocationReason = RevocationReason.UNSPECIFIED;
        switch (nscsRevocationReason) {
        case AA_COMPROMISE:
            revocationReason = RevocationReason.AA_COMPROMISE;
            break;
        case AFFILIATION_CHANGED:
            revocationReason = RevocationReason.AFFILIATION_CHANGED;
            break;
        case CA_COMPROMISE:
            revocationReason = RevocationReason.CA_COMPROMISE;
            break;
        case CERTIFICATE_HOLD:
            revocationReason = RevocationReason.CERTIFICATE_HOLD;
            break;
        case CESSATION_OF_OPERATION:
            revocationReason = RevocationReason.CESSATION_OF_OPERATION;
            break;
        case KEY_COMPROMISE:
            revocationReason = RevocationReason.KEY_COMPROMISE;
            break;
        case PRIVILEGE_WITHDRAWN:
            revocationReason = RevocationReason.PRIVILEGE_WITHDRAWN;
            break;
        case REMOVE_FROM_CRL:
            revocationReason = RevocationReason.REMOVE_FROM_CRL;
            break;
        case SUPERSEDED:
            revocationReason = RevocationReason.SUPERSEDED;
            break;
        case UNSPECIFIED:
            revocationReason = RevocationReason.UNSPECIFIED;
            break;
        default:
            break;
        }

        return revocationReason;
    }

    public List<Entity> getEntityListByIssuerName(final String issuerName) throws NscsPkiEntitiesManagerException {
        setContextData();

        try {
            return pkiApiManagers.getEntityManagementService().getEntityListByIssuerName(issuerName);
        } catch (CANotFoundException | InvalidEntityException e) {
            final String err = "getEntityListByIssuerName() : caught PKI exception " + e.getMessage();
            logger.error(err);
            throw new NscsPkiEntitiesManagerException(err, e);
        }
    }

    public static NodeEntityCategory findNodeEntityCategory(final EntityCategory entityCategory) {
        if (entityCategory != null) {
            for (final Entry<NodeEntityCategory, String> entry : entityCategoryMap.entrySet()) {
                if (entry.getValue().equals(entityCategory.getName())) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    /**
     *
     * @param entityName
     * @param entityType
     * @return
     * @throws NscsPkiEntitiesManagerException
     */
    public boolean isEntityNameAvailable(final String entityName, final EntityType entityType) throws NscsPkiEntitiesManagerException {
        logger.info("isEntityNameAvailable {}, EntityType {}", entityName, entityType.name());
        setContextData();

        try {
            return pkiApiManagers.getEntityManagementService().isEntityNameAvailable(entityName, entityType);
        } catch (EntityServiceException | InvalidEntityException e) {
            final String err = "isEntityNameAvailable() : caught PKI exception " + e.getMessage();
            logger.error(err);
            throw new NscsPkiEntitiesManagerException(err, e);
        }
    }

    /**
     *
     * @param caName
     * @return
     * @throws NscsPkiEntitiesManagerException
     */
    public boolean isExtCaNameAvailable(final String caName) throws NscsPkiEntitiesManagerException {
        logger.info("isExtCaEntityNameAvailable [{}]", caName);
        setContextData();

        try {
            return pkiApiManagers.getExtCAManagementService().isExtCANameAvailable(caName);
        } catch (final ExternalCredentialMgmtServiceException e) {
            final String err = "isExtCANameAvailable() : caught PKI exception " + e.getMessage();
            logger.error(err);
            throw new NscsPkiEntitiesManagerException(err, e);
        }
    }

    /**
     * Get list of trusted certificates for a given entity.
     *
     * @param entityName
     *            the entity name.
     * @return the list of trusted certificates.
     * @throws NscsPkiEntitiesManagerException
     */
    public List<Certificate> getTrustCertificates(final String entityName) throws NscsPkiEntitiesManagerException {
        setContextData();

        try {
            return pkiApiManagers.getEntityCertificateManagementService().getTrustCertificates(entityName);
        } catch (final PKIBaseException e) {
            final String err = "getTrustCertificates() : caught PKI exception " + e.getMessage();
            logger.error(err);
            throw new NscsPkiEntitiesManagerException(err, e);
        }
    }

    public List<X509Certificate> getInternalCATrusts(final String caName) throws NscsPkiEntitiesManagerException {
        logger.info("NscsPkiEntitiesManager::getInternalCATrusts() [{}]", caName);
        setContextData();

        List<Certificate> certDataList;
        try {
            certDataList = pkiApiManagers.getCACertificateManagementService().listCertificates_v1(caName, CertificateStatus.ACTIVE);
            final List<X509Certificate> x509List = new ArrayList<>();
            if (certDataList != null) {
                for (final Certificate certData : certDataList) {
                    x509List.add(certData.getX509Certificate());
                }
            }
            return x509List;
        } catch (CertificateNotFoundException | CertificateServiceException | EntityNotFoundException | InvalidEntityAttributeException e) {
            final String err = "listCertificates() : caught PKI exception " + e.getMessage();
            logger.error(err);
            throw new NscsPkiEntitiesManagerException(err, e);
        }
    }

    public List<X509Certificate> getExternalCATrusts(final String caName) throws NscsPkiEntitiesManagerException {
        logger.info("NscsPkiEntitiesManager::getExternalCATrusts() [{}]", caName);
        setContextData();

        List<Certificate> certDataList;
        try {
            certDataList = pkiApiManagers.getExtCACertificateManagementService().listCertificates_v1(caName, CertificateStatus.ACTIVE);
            final List<X509Certificate> x509List = new ArrayList<>();
            if (certDataList != null) {
                for (final Certificate certData : certDataList) {
                    x509List.add(certData.getX509Certificate());
                }
            }
            return x509List;
        } catch (CertificateNotFoundException | CertificateServiceException | EntityNotFoundException | InvalidEntityAttributeException e) {
            final String err = "listCertificates() : caught PKI exception " + e.getMessage();
            logger.error(err);
            throw new NscsPkiEntitiesManagerException(err, e);
        }
    }

    public EntityCategory getPkiEntityCategory(final NodeEntityCategory nodeEntityCategory) throws NscsPkiEntitiesManagerException {
        EntityCategory pkiEntityCategory = null;
        if (nodeEntityCategory != null) {
            final EntityCategory reqEntCat = new EntityCategory();
            reqEntCat.setName(entityCategoryMap.get(nodeEntityCategory));
            try {
                pkiEntityCategory = pkiApiManagers.getConfigurationManagementService().getCategory(reqEntCat);
            } catch (EntityCategoryNotFoundException | PKIConfigurationServiceException ex) {
                final String errMsg = "Caught exception in getting EntityCategory : " + ex;
                logger.error(errMsg);
                throw (new NscsPkiEntitiesManagerException(errMsg));
            }
        }
        return pkiEntityCategory;
    }

    public static String getEntityLog(final Entity ent) {
        String entityLog = "\nEntity: \n";
        if (ent != null) {
            entityLog += "\tType: " + ent.getType() + "\n";
            if (ent.getCategory() != null) {
                entityLog += "\tCategory: " + ent.getCategory().getName() + "\n";
            }
            entityLog += "\tPublishCertificatetoTDPS: " + ent.isPublishCertificatetoTDPS() + "\n";
            if (ent.getEntityInfo() != null) {
                entityLog += "\tName: " + ent.getEntityInfo().getName() + "\n";
                entityLog += "\tOTP: ";
                if ((ent.getEntityInfo().getOTP() != null) && (!ent.getEntityInfo().getOTP().isEmpty())) {
                    entityLog += "*****";
                }
                entityLog += "\n\toTPValidityPeriod: " + ent.getOtpValidityPeriod();
                entityLog += "\n\toTPCount: " + ent.getEntityInfo().getOTPCount() + "\n";
                entityLog += "\tStatus: " + ent.getEntityInfo().getStatus() + "\n";
                entityLog += "\tSubject: " + ent.getEntityInfo().getSubject() + "\n";
                entityLog += "\tSubjectAltName: " + ent.getEntityInfo().getSubjectAltName() + "\n";
                if (ent.getEntityInfo().getIssuer() != null) {
                    entityLog += "\tIssuer: " + ent.getEntityInfo().getIssuer().getName() + "\n";
                }
            }
            if (ent.getEntityInfo().getActiveCertificate() != null) {
                entityLog += "\tActiveCertificate: \n";
                // entityLog += "\t\tIssuer: " +
                // ent.getEntityInfo().getActiveCertificate().getIssuer().getName()
                // + "\n";
                if (ent.getEntityInfo().getActiveCertificate().getIssuer() != null) {
                    entityLog += "\t\tIssuer: " + ent.getEntityInfo().getActiveCertificate().getIssuer().getName() + "\n";
                } else {
                    entityLog += "\t\tIssuer: null \n";
                }

                entityLog += "\t\tSerialNumber: " + ent.getEntityInfo().getActiveCertificate().getSerialNumber() + "\n";
            }
            if (ent.getEntityProfile() != null) {
                entityLog += "\tEntityProfile: \n";
                entityLog += "\t\tName: " + ent.getEntityProfile().getName() + "\n";
                entityLog += "\t\tKeyAlgorithm: " + ent.getEntityProfile().getKeyGenerationAlgorithm() + "\n";
                entityLog += "\t\tSubjectAltName: " + ent.getEntityProfile().getSubjectAltNameExtension() + "\n";
                if (ent.getEntityProfile().getCategory() != null) {
                    entityLog += "\t\tCategory: " + ent.getEntityProfile().getCategory().getName() + "\n";
                }
            }
        }
        return entityLog;
    }

    public static String getEnrollmentInfoLog(final EnrollmentInfo ei) {
        String eiLog = "\nEnrollmentInfo: \n";
        if (ei != null) {
            eiLog += "\tEnrollment URL IPv4: " + ei.getIpv4EnrollmentURL() + "\n";
            eiLog += "\tEnrollment URL IPv6: " + ei.getIpv6EnrollmentURL() + "\n";
            if (ei.getCaCertificate() != null) {
                eiLog += "\tCA Certificate: \n";
                if (ei.getCaCertificate().getSubjectX500Principal() != null) {
                    eiLog += "\t\tSubject: " + ei.getCaCertificate().getSubjectX500Principal().toString() + "\n";
                } else {
                    eiLog += "\t\tSubject: null \n";
                }
                if (ei.getCaCertificate().getIssuerX500Principal() != null) {
                    eiLog += "\t\tIssuer: " + ei.getCaCertificate().getIssuerX500Principal().toString() + "\n";
                } else {
                    eiLog += "\t\tIssuer: null \n";
                }
                eiLog += "\t\tSerial Number: " + ei.getCaCertificate().getSerialNumber() + "\n\n";
            }
        }
        return eiLog;
    }

    public List<Entity> getEntitiesByCategoryWithInvalidCertificate(final Date validity, final int maxNeNumber,
            final NodeEntityCategory... nodeEntityCategories) throws NscsPkiEntitiesManagerException {

        final List<NodeEntityCategory> nodeEntityCategoryList = Arrays.asList(nodeEntityCategories);
        final List<EntityCategory> entityCategoryList = new ArrayList<EntityCategory>();

        for (final NodeEntityCategory nodeEntityCategory : nodeEntityCategoryList) {

            final EntityCategory entityCategory = new EntityCategory();
            final String name1 = entityCategoryMap.get(nodeEntityCategory);
            entityCategory.setName(name1);
            entityCategoryList.add(entityCategory);
        }

        setContextData();

        List<Entity> entities;
        try {
            entities = pkiApiManagers.getEntityManagementCustomService().getEntitiesWithInvalidCertificate(validity, maxNeNumber,
                    entityCategoryList.toArray(new EntityCategory[0]));
        } catch (MissingMandatoryFieldException | EntityCategoryNotFoundException | EntityServiceException e) {
            final String err = "getEntitiesWithInvalidCertificate() : caught PKI exception " + e.getMessage();
            logger.error(err);
            throw new NscsPkiEntitiesManagerException(err, e);
        }
        return entities;
    }

    /**
     * @param ent
     * @param enrollmentType
     * @return
     * @throws NscsPkiEntitiesManagerException
     */
    public EntityEnrollmentInfo createEntityAndGetEnrollmentInfo(final Entity ent, final EnrollmentType enrollmentType)
            throws NscsPkiEntitiesManagerException {
        setContextData();

        try {
            return pkiApiManagers.getEntityManagementService().createEntityAndGetEnrollmentInfo_v1(ent, enrollmentType);
        } catch (InvalidSubjectAltNameExtension | InvalidSubjectException | MissingMandatoryFieldException | AlgorithmNotFoundException
                | EntityCategoryNotFoundException | InvalidEntityCategoryException | CRLExtensionException | CRLGenerationException
                | EntityAlreadyExistsException | EntityNotFoundException | EntityServiceException | InvalidCRLGenerationInfoException
                | InvalidEntityException | InvalidEntityAttributeException | InvalidProfileException | ProfileNotFoundException
                | UnsupportedCRLVersionException e) {
            final String err = "createEntityAndGetEnrollmentInfo() : caught PKI exception " + e.getMessage();
            logger.error(err);
            throw new NscsPkiEntitiesManagerException(e.getMessage(), e);
        }
    }

    /**
     * @param foundEe
     * @param enrollmentType
     * @return
     * @throws NscsPkiEntitiesManagerException
     */
    public EntityEnrollmentInfo updateEntityAndGetEnrollmentInfo(final Entity foundEe, final EnrollmentType enrollmentType)
            throws NscsPkiEntitiesManagerException {

        setContextData();
        try {
            return pkiApiManagers.getEntityManagementService().updateEntityAndGetEnrollmentInfo_v1(foundEe, enrollmentType);
        } catch (InvalidSubjectAltNameExtension | InvalidSubjectException | MissingMandatoryFieldException | AlgorithmNotFoundException
                | EntityCategoryNotFoundException | InvalidEntityCategoryException | CRLExtensionException | CRLGenerationException
                | EntityAlreadyExistsException | EntityNotFoundException | EntityServiceException | InvalidCRLGenerationInfoException
                | InvalidEntityException | InvalidEntityAttributeException | InvalidProfileException | ProfileNotFoundException
                | UnsupportedCRLVersionException e) {
            final String err = "updateEntityAndGetEnrollmentInfo() : caught PKI exception " + e.getMessage();
            logger.error(err);
            throw new NscsPkiEntitiesManagerException(err, e);
        }
    }

    /**
     * This method verifies if certificate exists in PKI with the given subjectDN, serialNumber and issuerDN
     *
     * @param subjectDN
     *            subjectDN with which the certificate to be verified
     * @param serialNumber
     *            serialNumber with which the certificate to be verified
     * @param issuerDN
     *            issuerDN with which the certificate to be verified
     * @return true if certificate is present in PKI else false
     * @throws NscsPkiEntitiesManagerException
     *             when error occurs while fetching the certificate with the given details
     */
    public boolean isCertificateExist(final String subjectDN, final String serialNumber, final String issuerDN)
            throws NscsPkiEntitiesManagerException {
        setContextData();
        try {
            return pkiApiManagers.getEntityCertificateManagementService().isCertificateExist(subjectDN, serialNumber, issuerDN);
        } catch (final CertificateServiceException e) {
            final String err = "verifyCertificate() : caught PKI exception " + e.getMessage();
            logger.error(err);
            throw new NscsPkiEntitiesManagerException(err, e);
        }
    }
}
