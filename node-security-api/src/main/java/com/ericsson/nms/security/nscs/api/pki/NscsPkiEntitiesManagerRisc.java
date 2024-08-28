/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.pki;

import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Local;

import com.ericsson.nms.security.nscs.api.util.NscsPair;
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

@Local
public interface NscsPkiEntitiesManagerRisc {

    X509Certificate getCachedRootCACertificate(String caName) throws NscsPkiEntitiesManagerException;

    Entity createEntity(Entity ent) throws NscsPkiEntitiesManagerException;

    void deleteEntity(String subjectName) throws NscsPkiEntitiesManagerException;

    List<Entity> getEntities() throws NscsPkiEntitiesManagerException;

    Entity getPkiEntity(String subjectName) throws NscsPkiEntitiesManagerException;

    CAEntity getCachedCAEntity(String subjectName) throws NscsPkiEntitiesManagerException;

    EntityProfile getCachedEntityProfile(String entityProfileName) throws NscsPkiEntitiesManagerException;

    boolean isEntityProfileNameAvailable(String entityProfileName) throws NscsPkiEntitiesManagerException;

    Map<String, List<X509Certificate>> getCAsTrusts() throws NscsPkiEntitiesManagerException;

    List<X509Certificate> getInternalCATrusts(String caName) throws NscsPkiEntitiesManagerException;

    List<X509Certificate> getExternalCATrusts(String caName) throws NscsPkiEntitiesManagerException;

    String getEntityOTP(String entityName) throws NscsPkiEntitiesManagerException;

    boolean useMockEntityManager();

    boolean useMockProfileManager();

    boolean useMockCertificateManager();

    Set<NscsPair<String, Boolean>> getTrustedCAs(String entityProfileName) throws NscsPkiEntitiesManagerException;

    String getSmrsAccountTypeForNscs();

    /**
     * @param caEntity the caEntity
     * @return TDPS URL the tdps url
     * @throws NscsPkiEntitiesManagerException exception for NscsPkiEntitiesManagerRisc
     */
    public TDPSUrlInfo getTrustDistributionPointUrls(CAEntity caEntity) throws NscsPkiEntitiesManagerException;

    /**
     * @param issuerName
     *            The name of CA issuer of the certificate to revoke
     * @param serialNumber
     *            The serial number of the certificate to revoke
     * @param reason
     *            The reason of revocation. It must match the value expected in @link RevocationReason
     * @throws NscsPkiEntitiesManagerException exception for NscsPkiEntitiesManagerRisc
     */
    void revokeCertificateByIssuerName(String issuerName, String serialNumber, String reason) throws NscsPkiEntitiesManagerException;

    List<Entity> getEntityListByIssuerName(String issuerName) throws NscsPkiEntitiesManagerException;

    /**
     *
     * @param entityName the entityName
     * @param entityType the entityType
     * @return if EntityName is available
     * @throws NscsPkiEntitiesManagerException exception for NscsPkiEntitiesManagerRisc
     */
    boolean isEntityNameAvailable(String entityName, EntityType entityType) throws NscsPkiEntitiesManagerException;

    /**
     *
     * @param entityName the entityName
     * @return is ExtCaName is available
     * @throws NscsPkiEntitiesManagerException exception for NscsPkiEntitiesManagerRisc
     */
    public boolean isExtCaNameAvailable(final String entityName) throws NscsPkiEntitiesManagerException;

    /**
     * Get list of trusted certificates for a given entity.
     *
     * @param entityName
     *            the entity name.
     * @return the list of trusted certificates.
     * @throws NscsPkiEntitiesManagerException exception for NscsPkiEntitiesManagerRisc
     */
    List<Certificate> getTrustCertificates(String entityName) throws NscsPkiEntitiesManagerException;

    List<Entity> getEntitiesByCategoryWithInvalidCertificate(Date validity, int maxNeNumber, NodeEntityCategory... nodeEntityCategories)
            throws NscsPkiEntitiesManagerException;

    /**
     * @param ent the ent
     * @return the entity
     * @throws NscsPkiEntitiesManagerException exception for NscsPkiEntitiesManagerRisc
     */
    Entity getEntity(Entity ent) throws NscsPkiEntitiesManagerException;

    /**
     * @param ent the entity
     * @param enrollmentType the enrollmentType
     * @return the EntityEnrollmentInfo
     * @throws NscsPkiEntitiesManagerException exception for NscsPkiEntitiesManagerRisc
     */
    EntityEnrollmentInfo createEntityAndGetEnrollmentInfo(Entity ent, EnrollmentType enrollmentType) throws NscsPkiEntitiesManagerException;

    /**
     * @param foundEe the founf EE
     * @param enrollmentType the enrollmentType
     * @return the EntityEnrollmentInfo
     * @throws NscsPkiEntitiesManagerException exception for NscsPkiEntitiesManagerRisc
     */
    EntityEnrollmentInfo updateEntityAndGetEnrollmentInfo(Entity foundEe, EnrollmentType enrollmentType) throws NscsPkiEntitiesManagerException;

    /**
     * @param entityCategory the entityCategory
     * @return the EntityCategory
     * @throws NscsPkiEntitiesManagerException exception for NscsPkiEntitiesManagerRisc
     */
    EntityCategory getCachedEntityCategory(NodeEntityCategory entityCategory) throws NscsPkiEntitiesManagerException;

    /**
     * @param name the name
     * @return list of CertificateChain
     * @throws NscsPkiEntitiesManagerException exception for NscsPkiEntitiesManagerRisc
     */
    List<CertificateChain> getCachedCAChain(String name) throws NscsPkiEntitiesManagerException;

    /**
     * Get from PKI the trusted CA infos for the given CA pair (name and isChainRequired).
     * 
     * @param caPair
     *            the CA pair.
     * @return the trusted CA infos.
     * @throws NscsPkiEntitiesManagerException exception for NscsPkiEntitiesManagerRisc
     */
    Set<TrustedEntityInfo> getTrustedCAInfos(final NscsPair<String, Boolean> caPair) throws NscsPkiEntitiesManagerException;

    /**
     * @param caName the caName
     * @return pkiRootCaName
     * @throws NscsPkiEntitiesManagerException exception for NscsPkiEntitiesManagerRisc
     */
    String getCachedRootCAName(String caName) throws NscsPkiEntitiesManagerException;

    /**
     * Verifies if certificate exists in PKI with the given subjectDN, serialNumber and issuerDN
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
    boolean isCertificateExist(final String subjectDN, final String serialNumber, final String issuerDN) throws NscsPkiEntitiesManagerException;

}