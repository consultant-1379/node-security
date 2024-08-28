/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.pki.cache;

import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Set;

import javax.ejb.Local;

import com.ericsson.nms.security.nscs.api.pki.NodeEntityCategory;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerException;
import com.ericsson.nms.security.nscs.api.util.NscsPair;
import com.ericsson.oss.itpf.security.pki.manager.model.CertificateChain;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityCategory;
import com.ericsson.oss.itpf.security.pki.manager.model.TrustedEntityInfo;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.CAEntity;
import com.ericsson.oss.itpf.security.pki.manager.model.profiles.EntityProfile;

@Local
public interface PkiCachedCalls {

    /**
     * @param entityCategory the entityCategory
     * @return pki entity category
     * @throws NscsPkiEntitiesManagerException xception for PkiCachedCalls
     */
    EntityCategory getPkiEntityCategory(NodeEntityCategory entityCategory) throws NscsPkiEntitiesManagerException;

    /**
     * @param entityProfileName the entityProfileName
     * @return the pki entiyt profile
     * @throws NscsPkiEntitiesManagerException exception for PkiCachedCalls
     */
    EntityProfile getPkiEntityProfile(String entityProfileName) throws NscsPkiEntitiesManagerException;

    /**
     * @param onlyExpired the onlyExpired
     */
    void purgeAll(boolean onlyExpired);

    /**
     * @param cacheNameStr the cache name
     * @param onlyExpired  the onlyExpired
     */
    void purge(String cacheNameStr, boolean onlyExpired);

    /**
     * Get from cache (or from PKI) the list of trusted CA pairs (name and isChainRequired) for the given entity profile name. Internal and external
     * trusted CA names shall be returned.
     *
     * @param entityProfileName
     *            the entity profile name.
     * @return the list of trusted CA pairs (name and isChainRequired).
     * @throws NscsPkiEntitiesManagerException exception for PkiCachedCalls
     */
    Set<NscsPair<String, Boolean>> getTrustedCAs(final String entityProfileName) throws NscsPkiEntitiesManagerException;

    /**
     * @param subjectName the subjectName
     * @return the CA entity
     * @throws NscsPkiEntitiesManagerException exception for PkiCachedCalls
     */
    CAEntity getCAEntity(String subjectName) throws NscsPkiEntitiesManagerException;

    /**
     * @param caName the ca name
     * @return X509Certificate
     * @throws NscsPkiEntitiesManagerException exception for PkiCachedCalls
     */
    X509Certificate getRootCACertificate(String caName) throws NscsPkiEntitiesManagerException;

    /**
     * @param caName the ca name
     * @return list of X509Certificate
     * @throws NscsPkiEntitiesManagerException exception for PkiCachedCalls
     */
    List<X509Certificate> getInternalCATrusts(String caName) throws NscsPkiEntitiesManagerException;

    /**
     * @param caName the ca name
     * @return list of X509Certificate
     * @throws NscsPkiEntitiesManagerException exception for PkiCachedCalls
     */
    List<X509Certificate> getExternalCATrusts(String caName) throws NscsPkiEntitiesManagerException;

    /**
     * @param caName the ca name
     * @return list of CertificateChain
     * @throws NscsPkiEntitiesManagerException exception for PkiCachedCalls
     */
    List<CertificateChain> getCAChain(String caName) throws NscsPkiEntitiesManagerException;

    /**
     * @param entityProfileName the entityProfileName
     * @return is EntityProfileName is available
     * @throws NscsPkiEntitiesManagerException exception for PkiCachedCalls
     */
    boolean isEntityProfileNameAvailable(String entityProfileName) throws NscsPkiEntitiesManagerException;

    /**
     * Get from cache (or from PKI) the trusted CA infos for the given CA pair (name and isChainRequired).
     *
     * @param caPair
     *            the CA pair.
     * @return the trusted CA infos.
     * @throws NscsPkiEntitiesManagerException exception for PkiCachedCalls
     */
    Set<TrustedEntityInfo> getTrustedCAInfos(final NscsPair<String, Boolean> caPair) throws NscsPkiEntitiesManagerException;

    /**
     * @param caName tha ca name
     * @return pkiRootCaName
     * @throws NscsPkiEntitiesManagerException exception for PkiCachedCalls
     */
    String getRootCAName(String caName) throws NscsPkiEntitiesManagerException;

}
