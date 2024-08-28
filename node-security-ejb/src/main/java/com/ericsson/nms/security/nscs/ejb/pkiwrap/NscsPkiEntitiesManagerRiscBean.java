/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.nms.security.nscs.ejb.pkiwrap;

import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.pki.NodeEntityCategory;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerException;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerRisc;
import com.ericsson.nms.security.nscs.api.pki.cache.PkiCachedCalls;
import com.ericsson.nms.security.nscs.api.util.NscsPair;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.pki.NscsPkiEntitiesManagerJar;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.Certificate;
import com.ericsson.oss.itpf.security.pki.manager.model.CertificateChain;
import com.ericsson.oss.itpf.security.pki.manager.model.EnrollmentType;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityCategory;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityEnrollmentInfo;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityType;
import com.ericsson.oss.itpf.security.pki.manager.model.TDPSUrlInfo;
import com.ericsson.oss.itpf.security.pki.manager.model.TrustedEntityInfo;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.CAEntity;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.itpf.security.pki.manager.model.profiles.EntityProfile;

/**
 *
 * @author enmadmin
 */
@Stateless
public class NscsPkiEntitiesManagerRiscBean implements NscsPkiEntitiesManagerRisc {

    @Inject
    Logger logger;

    @Inject
    NscsPkiEntitiesManagerJar nscsPkiEntitiesManagerJar;

    @Inject
    NscsCapabilityModelService capabilityModel;

    @Inject
    PkiCachedCalls pkiCachedCalls;

    private static final Map<NodeEntityCategory, String> entityCategoryMap = new HashMap<>();

    static {
        entityCategoryMap.put(NodeEntityCategory.OAM, "NODE-OAM");
        entityCategoryMap.put(NodeEntityCategory.IPSEC, "NODE-IPSEC");
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public X509Certificate getCachedRootCACertificate(final String caName) throws NscsPkiEntitiesManagerException {
        return pkiCachedCalls.getRootCACertificate(caName);
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String getCachedRootCAName(final String caName) throws NscsPkiEntitiesManagerException {
        return pkiCachedCalls.getRootCAName(caName);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Entity createEntity(final Entity ent) throws NscsPkiEntitiesManagerException {
        return nscsPkiEntitiesManagerJar.createEntity(ent);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteEntity(final String subjectName) throws NscsPkiEntitiesManagerException {
        nscsPkiEntitiesManagerJar.deleteEntity(subjectName);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<Entity> getEntities() throws NscsPkiEntitiesManagerException {
        return nscsPkiEntitiesManagerJar.getEntities();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Entity getPkiEntity(final String entityName) throws NscsPkiEntitiesManagerException {
        return nscsPkiEntitiesManagerJar.getPkiEntity(entityName);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public CAEntity getCachedCAEntity(final String caEntityName) throws NscsPkiEntitiesManagerException {
        return pkiCachedCalls.getCAEntity(caEntityName);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public EntityProfile getCachedEntityProfile(final String entityProfileName) throws NscsPkiEntitiesManagerException {
        return pkiCachedCalls.getPkiEntityProfile(entityProfileName);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean isEntityProfileNameAvailable(final String entityProfileName) throws NscsPkiEntitiesManagerException {
        return pkiCachedCalls.isEntityProfileNameAvailable(entityProfileName);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Map<String, List<X509Certificate>> getCAsTrusts() throws NscsPkiEntitiesManagerException {
        // TODO understand why another getCaTrusts
        return nscsPkiEntitiesManagerJar.getCAsTrusts();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<X509Certificate> getInternalCATrusts(final String caName) throws NscsPkiEntitiesManagerException {
        return pkiCachedCalls.getInternalCATrusts(caName);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<X509Certificate> getExternalCATrusts(final String caName) throws NscsPkiEntitiesManagerException {
        return pkiCachedCalls.getExternalCATrusts(caName);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String getEntityOTP(final String entityName) throws NscsPkiEntitiesManagerException {
        // TODO Is it necessary?
        return nscsPkiEntitiesManagerJar.getEntityOTP(entityName);
    }

    @Override
    public boolean useMockEntityManager() {
        return nscsPkiEntitiesManagerJar.useMockEntityManager();
    }

    @Override
    public boolean useMockProfileManager() {
        return nscsPkiEntitiesManagerJar.useMockProfileManager();
    }

    @Override
    public boolean useMockCertificateManager() {
        return nscsPkiEntitiesManagerJar.useMockCertificateManager();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Set<NscsPair<String, Boolean>> getTrustedCAs(final String entityProfileName) throws NscsPkiEntitiesManagerException {
        return pkiCachedCalls.getTrustedCAs(entityProfileName);
    }

    @Override
    public String getSmrsAccountTypeForNscs() {
        return nscsPkiEntitiesManagerJar.getSmrsAccountTypeForNscs();
    }

    @Override
    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public TDPSUrlInfo getTrustDistributionPointUrls(final CAEntity caEntity) throws NscsPkiEntitiesManagerException {
        // TODO cacheable ?
        return nscsPkiEntitiesManagerJar.getTrustDistributionPointUrls(caEntity);

    }

    @Override
    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public void revokeCertificateByIssuerName(final String issuerName, final String serialNumber, final String reason)
            throws NscsPkiEntitiesManagerException {
        nscsPkiEntitiesManagerJar.revokeCertificateByIssuerName(issuerName, serialNumber, reason);
    }

    @Override
    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public List<Entity> getEntityListByIssuerName(final String issuerName) throws NscsPkiEntitiesManagerException {
        return nscsPkiEntitiesManagerJar.getEntityListByIssuerName(issuerName);
    }

    @Override
    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public boolean isEntityNameAvailable(final String entityName, final EntityType entityType) throws NscsPkiEntitiesManagerException {
        return nscsPkiEntitiesManagerJar.isEntityNameAvailable(entityName, entityType);
    }

    @Override
    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public boolean isExtCaNameAvailable(final String entityName) throws NscsPkiEntitiesManagerException {
        return nscsPkiEntitiesManagerJar.isExtCaNameAvailable(entityName);
    }

    @Override
    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public List<Certificate> getTrustCertificates(final String entityName) throws NscsPkiEntitiesManagerException {
        // TODO cacheable ?
        return nscsPkiEntitiesManagerJar.getTrustCertificates(entityName);
    }

    @Override
    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public List<Entity> getEntitiesByCategoryWithInvalidCertificate(final Date validity, final int maxNeNumber,
                                                                    final NodeEntityCategory... nodeEntityCategories)
            throws NscsPkiEntitiesManagerException {
        // TODO move up?
        return nscsPkiEntitiesManagerJar.getEntitiesByCategoryWithInvalidCertificate(validity, maxNeNumber, nodeEntityCategories);
    }

    @Override
    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public Entity getEntity(final Entity entity) throws NscsPkiEntitiesManagerException {
        return nscsPkiEntitiesManagerJar.getEntity(entity);
    }

    @Override
    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public EntityEnrollmentInfo createEntityAndGetEnrollmentInfo(final Entity ent, final EnrollmentType enrollmentType)
            throws NscsPkiEntitiesManagerException {
        return nscsPkiEntitiesManagerJar.createEntityAndGetEnrollmentInfo(ent, enrollmentType);
    }

    @Override
    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public EntityEnrollmentInfo updateEntityAndGetEnrollmentInfo(final Entity foundEe, final EnrollmentType enrollmentType)
            throws NscsPkiEntitiesManagerException {
        return nscsPkiEntitiesManagerJar.updateEntityAndGetEnrollmentInfo(foundEe, enrollmentType);
    }

    @Override
    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public EntityCategory getCachedEntityCategory(final NodeEntityCategory entityCategory) throws NscsPkiEntitiesManagerException {
        return pkiCachedCalls.getPkiEntityCategory(entityCategory);
    }

    @Override
    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public List<CertificateChain> getCachedCAChain(final String caName) throws NscsPkiEntitiesManagerException {
        return pkiCachedCalls.getCAChain(caName);
    }

    @Override
    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public Set<TrustedEntityInfo> getTrustedCAInfos(final NscsPair<String, Boolean> caPair) throws NscsPkiEntitiesManagerException {
        return pkiCachedCalls.getTrustedCAInfos(caPair);
    }

    @Override
    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public boolean isCertificateExist(String subjectDN, String serialNumber, String issuerDN) throws NscsPkiEntitiesManagerException {
        return nscsPkiEntitiesManagerJar.isCertificateExist(subjectDN, serialNumber, issuerDN);
    }
}
