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

import com.ericsson.nms.security.nscs.api.model.CertSpec;
import com.ericsson.nms.security.nscs.api.util.NscsPair;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.Certificate;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityType;
import com.ericsson.oss.itpf.security.pki.manager.model.TDPSUrlInfo;
import com.ericsson.oss.itpf.security.pki.manager.model.TrustedEntityInfo;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.CAEntity;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.itpf.security.pki.manager.model.profiles.EntityProfile;

@Local
public interface NscsPkiEntitiesManagerIF {

    /**
     * @param nodeName
     *            the enrollmententityInfo
     * @throws NscsPkiEntitiesManagerException
     *             exception for NscsPkiEntitiesManagerIF
     * @return EnrollmentPartialInfos the enrollmentPartial Information
     */
    EnrollmentPartialInfos getEnrollmentEntityInfo(final Map<Object, Object> enrollmententityInfo) throws NscsPkiEntitiesManagerException;

    X509Certificate findPkiRootCACertificate(String caName) throws NscsPkiEntitiesManagerException;
    
    String findPkiRootCAName(String caName) throws NscsPkiEntitiesManagerException;

	Entity createEntity(Entity ent) throws NscsPkiEntitiesManagerException;

	void deleteEntity(String subjectName) throws NscsPkiEntitiesManagerException;

    List<Entity> getEntities() throws NscsPkiEntitiesManagerException;

    Entity getPkiEntity(String subjectName) throws NscsPkiEntitiesManagerException;

    CAEntity getCAEntity(String subjectName) throws NscsPkiEntitiesManagerException;

    EntityProfile getEntityProfile(String entityProfileName) throws NscsPkiEntitiesManagerException;

    boolean isEntityProfileNameAvailable(String entityProfileName) throws NscsPkiEntitiesManagerException;

    Map<String, List<X509Certificate>> getCAsTrusts() throws NscsPkiEntitiesManagerException;

	List<X509Certificate> getCATrusts(String caName) throws NscsPkiEntitiesManagerException;

	String getEntityOTP(String entityName) throws NscsPkiEntitiesManagerException;

	boolean useMockEntityManager();

	boolean useMockProfileManager();

	boolean useMockCertificateManager();

	/**
	 * Get from PKI the list of trusted CA pairs (name and isChainRequired) for
	 * the given entity profile name. Internal and external trusted CA pairs
	 * shall be returned.
	 * 
	 * @param entityProfileName
	 *            the entity profile name.
	 * @return the list of trusted CA pairs
	 * @throws NscsPkiEntitiesManagerException exception for NscsPkiEntitiesManagerIF
	 */
	Set<NscsPair<String, Boolean>> getTrustedCAs(final String entityProfileName) throws NscsPkiEntitiesManagerException;

	/**
	 * @param entityProfileName the entityProfileName
	 * @return set of CertSpec
	 * @throws NscsPkiEntitiesManagerException exception for NscsPkiEntitiesManagerIF
	 *
	 */
	Set<CertSpec> getTrustCertificatesFromProfile(final String entityProfileName)
			throws NscsPkiEntitiesManagerException;

	String getSmrsAccountTypeForNscs();

	/**
	 * @param caEntity the caEntity
	 * @return TDPS URL the tdps url
	 * @throws NscsPkiEntitiesManagerException exception for NscsPkiEntitiesManagerIF
	 */
    public TDPSUrlInfo getTrustDistributionPointUrls(CAEntity caEntity) throws NscsPkiEntitiesManagerException;

	/**
	 * @param issuerName
	 *            The name of CA issuer of the certificate to revoke
	 * @param serialNumber
	 *            The serial number of the certificate to revoke
	 * @param reason
	 *            The reason of revocation. It must match the value expected
	 *            in @link RevocationReason
	 * @throws NscsPkiEntitiesManagerException exception for NscsPkiEntitiesManagerIF
	 */
    void revokeCertificateByIssuerName(String issuerName, String serialNumber, String reason) throws NscsPkiEntitiesManagerException;

    List<Entity> getEntityListByIssuerName(String issuerName) throws NscsPkiEntitiesManagerException;

	/**
	 *
	 * @param entityName the entityName
	 * @param entityType the entityType
	 * @return if EntityName is available
	 * @throws NscsPkiEntitiesManagerException exception for NscsPkiEntitiesManagerIF
	 *
	 */
    boolean isEntityNameAvailable(String entityName, EntityType entityType) throws NscsPkiEntitiesManagerException;

	/**
	 *
	 * @param entityName the entityName
	 * @return if ExtCaName is available
	 * @throws NscsPkiEntitiesManagerException exception for NscsPkiEntitiesManagerIF
	 */
    public boolean isExtCaNameAvailable(final String entityName) throws NscsPkiEntitiesManagerException;

	/**
	 * Get list of trusted certificates for a given entity.
	 *
	 * @param entityName
	 *            the entity name.
	 * @return the list of trusted certificates.
	 * @throws NscsPkiEntitiesManagerException exception for NscsPkiEntitiesManagerIF
	 */
	List<Certificate> getTrustCertificates(String entityName) throws NscsPkiEntitiesManagerException;

	List<Entity> getEntitiesByCategoryWithInvalidCertificate(Date validity, int maxNeNumber,
			NodeEntityCategory... nodeEntityCategories) throws NscsPkiEntitiesManagerException;

	/**
	 * Get from PKI the trusted CA active certificate info for the given CA
	 * name.
	 * 
	 * @param caName
	 *            the CA name.
	 * @return the trusted CA active certificate info.
	 * @throws NscsPkiEntitiesManagerException exception for NscsPkiEntitiesManagerIF
	 */
	TrustedEntityInfo getTrustedCAInfoByName(final String caName) throws NscsPkiEntitiesManagerException;

	/**
	 * Get from PKI the trusted CAs active certificate info for the given entity
	 * profile name.
	 * 
	 * @param entityProfileName
	 *            the entity profile name.
	 * @return the trusted CAs active certificate info.
	 * @throws NscsPkiEntitiesManagerException exception for NscsPkiEntitiesManagerIF
	 */
	Set<TrustedEntityInfo> getTrustedCAsInfoByEntityProfileName(final String entityProfileName)
			throws NscsPkiEntitiesManagerException;

}