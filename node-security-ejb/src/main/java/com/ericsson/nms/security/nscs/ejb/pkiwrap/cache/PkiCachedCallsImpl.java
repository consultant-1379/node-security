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
package com.ericsson.nms.security.nscs.ejb.pkiwrap.cache;

import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.AccessTimeout;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.pki.NodeEntityCategory;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerException;
import com.ericsson.nms.security.nscs.api.pki.cache.PkiCachedCalls;
import com.ericsson.nms.security.nscs.api.pki.cache.PkiCachedRecord;
import com.ericsson.nms.security.nscs.api.util.NscsPair;
import com.ericsson.nms.security.nscs.pki.NscsPkiEntitiesManagerJar;
import com.ericsson.oss.itpf.security.pki.manager.model.CertificateChain;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.Certificate;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityCategory;
import com.ericsson.oss.itpf.security.pki.manager.model.TrustedEntityInfo;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.CAEntity;
import com.ericsson.oss.itpf.security.pki.manager.model.profiles.EntityProfile;

@Singleton
@AccessTimeout(value = 600000)
public class PkiCachedCallsImpl implements PkiCachedCalls {

    @Inject
    Logger log;

	public enum PkiCacheNames {
		ENTITY_CATEGORY, ENTITY_PROFILE, TRUST_CAS, INTERNAL_CA_CERTS, EXTERNAL_CA_CERTS, CA_CHAIN, CA_ENTITY, ROOT_CA_CERTS, TRUSTED_ENTITY_INFOS
	}

	private Map<NodeEntityCategory, PkiCachedRecord<EntityCategory>> entityCategoryCache = null;
	private Map<String, PkiCachedRecord<EntityProfile>> entityProfileCache = null;
	private Map<String, PkiCachedRecord<Set<NscsPair<String, Boolean>>>> trustCAsCache = null;
	private Map<String, PkiCachedRecord<List<X509Certificate>>> internalCaCertsCache = null;
	private Map<String, PkiCachedRecord<List<X509Certificate>>> externalCaCertsCache = null;
	private Map<String, PkiCachedRecord<List<CertificateChain>>> caChainCache = null;
	private Map<String, PkiCachedRecord<CAEntity>> caEntityCache = null;
	private Map<String, PkiCachedRecord<X509Certificate>> rootCACertificateCache = null;
	private Map<NscsPair<String, Boolean>, PkiCachedRecord<Set<TrustedEntityInfo>>> trustedEntityInfosCache = null;

	private static long CACHE_DEFAULT_VALIDITY_TIMEOUT = 180000;

	@Inject
	NscsPkiEntitiesManagerJar nscsPkiEntitiesManagerJar;

	@PostConstruct
	public void initialize() {
		this.entityCategoryCache = new ConcurrentHashMap<NodeEntityCategory, PkiCachedRecord<EntityCategory>>();
		this.entityProfileCache = new ConcurrentHashMap<String, PkiCachedRecord<EntityProfile>>();
		this.trustCAsCache = new ConcurrentHashMap<String, PkiCachedRecord<Set<NscsPair<String, Boolean>>>>();
		this.internalCaCertsCache = new ConcurrentHashMap<String, PkiCachedRecord<List<X509Certificate>>>();
		this.externalCaCertsCache = new ConcurrentHashMap<String, PkiCachedRecord<List<X509Certificate>>>();
		this.caChainCache = new ConcurrentHashMap<String, PkiCachedRecord<List<CertificateChain>>>();
		this.caEntityCache = new ConcurrentHashMap<String, PkiCachedRecord<CAEntity>>();
		this.rootCACertificateCache = new ConcurrentHashMap<String, PkiCachedRecord<X509Certificate>>();
		this.trustedEntityInfosCache = new ConcurrentHashMap<NscsPair<String, Boolean>, PkiCachedRecord<Set<TrustedEntityInfo>>>();

		log.info("PKI cache prepared");
	}

	@Override
    public EntityCategory getPkiEntityCategory(final NodeEntityCategory nodeEntityCategory) throws NscsPkiEntitiesManagerException {

		EntityCategory ret = null;
		if (nodeEntityCategory != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Getting category with name : {}", nodeEntityCategory.name());
                }
			final PkiCachedRecord<EntityCategory> rec = this.entityCategoryCache.get(nodeEntityCategory);
			final long currentTime = System.currentTimeMillis();
			if (rec == null || (currentTime - rec.getMillis()) > CACHE_DEFAULT_VALIDITY_TIMEOUT) {
                log.debug("Retrieving Entity Category from pki and refreshing cache");
				ret = nscsPkiEntitiesManagerJar.getPkiEntityCategory(nodeEntityCategory);
				this.entityCategoryCache.put(nodeEntityCategory,
						new PkiCachedRecord<EntityCategory>(System.currentTimeMillis(), ret));
			} else {
                log.debug("Retrieving Entity Category from cache");
				ret = rec.getValue();
			}
		}
		return ret;
	}

	@Override
    public EntityProfile getPkiEntityProfile(final String entityProfileName) throws NscsPkiEntitiesManagerException {
		EntityProfile ret = null;
		if (entityProfileName != null) {
            log.debug("Getting Profile with name {}", entityProfileName);
			final PkiCachedRecord<EntityProfile> rec = this.entityProfileCache.get(entityProfileName);
			final long currentTime = System.currentTimeMillis();

			if (rec == null || (currentTime - rec.getMillis()) > CACHE_DEFAULT_VALIDITY_TIMEOUT) {
                log.debug("Retrieving Entity Profile from pki and refreshing cache");
				ret = nscsPkiEntitiesManagerJar.getEntityProfile(entityProfileName);
				this.entityProfileCache.put(entityProfileName,
						new PkiCachedRecord<EntityProfile>(System.currentTimeMillis(), ret));
			} else {
                log.debug("Retrieving Entity Profile from cache");
				ret = rec.getValue();
			}
		}
		return ret;
	}

	@Override
    public boolean isEntityProfileNameAvailable(final String entityProfileName) throws NscsPkiEntitiesManagerException {
		if (entityProfileName != null) {
            log.debug("Is Available Profile with name {}", entityProfileName);
			final PkiCachedRecord<EntityProfile> rec = this.entityProfileCache.get(entityProfileName);
			final long currentTime = System.currentTimeMillis();

			if (rec == null || (currentTime - rec.getMillis()) > CACHE_DEFAULT_VALIDITY_TIMEOUT) {
                log.debug("Retrieving Is Entity Profile Name Available from pki");
				return nscsPkiEntitiesManagerJar.isEntityProfileNameAvailable(entityProfileName);
			} else {
                log.debug("Retrieving Entity Profile Name Available from cache");
				return false;
			}
		}
		return false;
	}

	@Override
    public Set<NscsPair<String, Boolean>> getTrustedCAs(final String entityProfileName) throws NscsPkiEntitiesManagerException {
		Set<NscsPair<String, Boolean>> ret = null;
		if (entityProfileName != null) {
            log.debug("Getting TrustedCAs with Profile name {}", entityProfileName);
			final PkiCachedRecord<Set<NscsPair<String, Boolean>>> rec = this.trustCAsCache.get(entityProfileName);
			final long currentTime = System.currentTimeMillis();

			if (rec == null || (currentTime - rec.getMillis()) > CACHE_DEFAULT_VALIDITY_TIMEOUT) {
                log.debug("Retrieving TrustedCAs names from pki and refreshing cache");
				ret = nscsPkiEntitiesManagerJar.getTrustedCAs(entityProfileName);
				this.trustCAsCache.put(entityProfileName,
						new PkiCachedRecord<Set<NscsPair<String, Boolean>>>(System.currentTimeMillis(), ret));
			} else {
                log.debug("Retrieving TrustedCAs names from cache");
				ret = rec.getValue();
			}
		}
		return ret;
	}

	@Override
    public List<X509Certificate> getInternalCATrusts(final String caName) throws NscsPkiEntitiesManagerException {
		List<X509Certificate> ret = null;
		if (caName != null) {
            log.debug("Getting internal CA certs with CA name {}", caName);
			final PkiCachedRecord<List<X509Certificate>> rec = this.internalCaCertsCache.get(caName);
			final long currentTime = System.currentTimeMillis();

			if (rec == null || (currentTime - rec.getMillis()) > CACHE_DEFAULT_VALIDITY_TIMEOUT) {
                log.debug("Retrieving internal CA certs from pki and refreshing cache");
					ret = nscsPkiEntitiesManagerJar.getInternalCATrusts(caName);
                this.internalCaCertsCache.put(caName, new PkiCachedRecord<List<X509Certificate>>(System.currentTimeMillis(), ret));
			} else {
                log.debug("Retrieving internal CA certs from cache");
				ret = rec.getValue();
			}
		}
		return ret;
	}

	@Override
    public List<X509Certificate> getExternalCATrusts(final String caName) throws NscsPkiEntitiesManagerException {
		List<X509Certificate> ret = null;
		if (caName != null) {
            log.debug("Getting external CA certs with CA name {}", caName);
			final PkiCachedRecord<List<X509Certificate>> rec = this.externalCaCertsCache.get(caName);
			final long currentTime = System.currentTimeMillis();

			if (rec == null || (currentTime - rec.getMillis()) > CACHE_DEFAULT_VALIDITY_TIMEOUT) {
                log.debug("Retrieving external CA certs from pki and refreshing cache");
					ret = nscsPkiEntitiesManagerJar.getExternalCATrusts(caName);
                this.externalCaCertsCache.put(caName, new PkiCachedRecord<List<X509Certificate>>(System.currentTimeMillis(), ret));
			} else {
                log.debug("Retrieving external CA certs from cache");
				ret = rec.getValue();
			}
		}
		return ret;
	}

	@Override
    public CAEntity getCAEntity(final String subjectName) throws NscsPkiEntitiesManagerException {
		CAEntity ret = null;
		if (subjectName != null) {
            log.debug("Getting CAEntity with name : {}", subjectName);

			final PkiCachedRecord<CAEntity> rec = this.caEntityCache.get(subjectName);
			final long currentTime = System.currentTimeMillis();

			if (rec == null || (currentTime - rec.getMillis()) > CACHE_DEFAULT_VALIDITY_TIMEOUT) {
                log.debug("Retrieving CAEntity from pki and refreshing cache");
				ret = nscsPkiEntitiesManagerJar.getCAEntity(subjectName);
				this.caEntityCache.put(subjectName, new PkiCachedRecord<CAEntity>(System.currentTimeMillis(), ret));
			} else {
                log.debug("Retrieving CAEntity from cache");
				ret = rec.getValue();
			}
		}
		return ret;
	}

	@Override
    public X509Certificate getRootCACertificate(final String caName) throws NscsPkiEntitiesManagerException {
		X509Certificate ret = null;
		if (caName != null) {
            log.debug("Getting RootCACertificate for CAEntity with name : {}", caName);

			final PkiCachedRecord<X509Certificate> rec = this.rootCACertificateCache.get(caName);
			final long currentTime = System.currentTimeMillis();

			if (rec == null || (currentTime - rec.getMillis()) > CACHE_DEFAULT_VALIDITY_TIMEOUT) {
                log.debug("Retrieving RootCACertificate from pki and refreshing cache");
				ret = nscsPkiEntitiesManagerJar.findPkiRootCACertificate(caName);
				this.rootCACertificateCache.put(caName,
						new PkiCachedRecord<X509Certificate>(System.currentTimeMillis(), ret));
			} else {
                log.debug("Retrieving RootCACertificate from cache");
				ret = rec.getValue();
			}
		}
		return ret;
	}

	@Override
    public List<CertificateChain> getCAChain(final String caName) throws NscsPkiEntitiesManagerException {
		List<CertificateChain> ret = null;
		if (caName != null) {
            log.debug("Getting CAChain for CA with name : {}", caName);

			final PkiCachedRecord<List<CertificateChain>> rec = this.caChainCache.get(caName);
			final long currentTime = System.currentTimeMillis();

			if (rec == null || (currentTime - rec.getMillis()) > CACHE_DEFAULT_VALIDITY_TIMEOUT) {
                log.debug("Retrieving CAChain from pki and refreshing cache");
				ret = nscsPkiEntitiesManagerJar.getCAChain(caName);
				this.caChainCache.put(caName,
						new PkiCachedRecord<List<CertificateChain>>(System.currentTimeMillis(), ret));
			} else {
                log.debug("Retrieving CAChain from cache");
				ret = rec.getValue();
			}
		}
		return ret;
	}

    @Override
    public String getRootCAName(final String caName) throws NscsPkiEntitiesManagerException {

        List<CertificateChain> ret = null;
        if (caName != null) {
            log.debug("Getting CAChain for CA with name : {}", caName);

            final PkiCachedRecord<List<CertificateChain>> rec = this.caChainCache.get(caName);
            final long currentTime = System.currentTimeMillis();

            if (rec == null || (currentTime - rec.getMillis()) > CACHE_DEFAULT_VALIDITY_TIMEOUT) {
                log.debug("Retrieving CAChain from pki and refreshing cache");
                ret = nscsPkiEntitiesManagerJar.getCAChain(caName);
                this.caChainCache.put(caName, new PkiCachedRecord<List<CertificateChain>>(System.currentTimeMillis(), ret));
            } else {
                log.debug("Retrieving CAChain from cache");
                ret = rec.getValue();
            }
        }
        final Certificate certificate = getRootCaCertificate(ret);
        String name = null;
        if ((certificate != null) && (certificate.getIssuer() != null)) {
            name = certificate.getIssuer().getName();
        }
        return name;
    }

    private Certificate getRootCaCertificate(List<CertificateChain> certificateChainList) {

        for (final CertificateChain certificateChain : certificateChainList) {
            if (certificateChain.getCertificates() != null && !certificateChain.getCertificates().isEmpty()) {
                final int sizeChain = certificateChain.getCertificates().size();
                final Certificate cert = certificateChain.getCertificates().get(sizeChain - 1);
                log.debug("Root CA Certificate : {} SerialNumber: {}", cert.getSubject(), cert.getSerialNumber());
                return cert;
            }
        }
        return null;
    }

	@Override
    public Set<TrustedEntityInfo> getTrustedCAInfos(final NscsPair<String, Boolean> caPair) throws NscsPkiEntitiesManagerException {
		Set<TrustedEntityInfo> ret = null;
		if (caPair != null) {
			final String caName = caPair.getL();
			final Boolean isChainRequired = caPair.getR();
			final String caParams = "caName [" + caName + "] isChainRequired [" + isChainRequired + "]";
			log.info("get cached TrustedCAInfos : starts for {}", caParams);

			final PkiCachedRecord<Set<TrustedEntityInfo>> rec = this.trustedEntityInfosCache.get(caPair);
			final long currentTime = System.currentTimeMillis();

			if (rec == null || (currentTime - rec.getMillis()) > CACHE_DEFAULT_VALIDITY_TIMEOUT) {
				log.info("get cached TrustedCAInfos : retrieving info from PKI and refreshing cache for {}", caParams);
				ret = nscsPkiEntitiesManagerJar.getTrustedCAInfos(caPair);
				this.trustedEntityInfosCache.put(caPair,
						new PkiCachedRecord<Set<TrustedEntityInfo>>(System.currentTimeMillis(), ret));
			} else {
				log.info("get cached TrustedCAInfos : retrieving info from cache for {}", caParams);
				ret = rec.getValue();
			}
		}
		return ret;
	}

	@Override
	public void purgeAll(final boolean onlyExpired) {
		purge(PkiCacheNames.ENTITY_CATEGORY.name(), onlyExpired);
		purge(PkiCacheNames.ENTITY_PROFILE.name(), onlyExpired);
		purge(PkiCacheNames.TRUST_CAS.name(), onlyExpired);
		purge(PkiCacheNames.INTERNAL_CA_CERTS.name(), onlyExpired);
		purge(PkiCacheNames.EXTERNAL_CA_CERTS.name(), onlyExpired);
		purge(PkiCacheNames.CA_CHAIN.name(), onlyExpired);
		purge(PkiCacheNames.CA_ENTITY.name(), onlyExpired);
		purge(PkiCacheNames.ROOT_CA_CERTS.name(), onlyExpired);
		purge(PkiCacheNames.TRUSTED_ENTITY_INFOS.name(), onlyExpired);
	}

	@Override
	public void purge(final String cacheNameStr, final boolean onlyExpired) {
		final StringBuilder sb = new StringBuilder("Purging cache with name ");
		sb.append(cacheNameStr).append(" : onlyExpired = ").append(onlyExpired);
		log.info(sb.toString());
		final long currentTime = System.currentTimeMillis();
		final PkiCacheNames cacheName = PkiCacheNames.valueOf(cacheNameStr);
		switch (cacheName) {
		case ENTITY_CATEGORY: {
			if (onlyExpired) {
				final Iterator<?> it = entityCategoryCache.entrySet().iterator();
				while (it.hasNext()) {
					final PkiCachedRecord<?> pair = (PkiCachedRecord<?>) ((Map.Entry) it.next()).getValue();
					if (currentTime - pair.getMillis() > CACHE_DEFAULT_VALIDITY_TIMEOUT) {
						it.remove();
					}
				}
			} else {
				entityCategoryCache.clear();
			}
		}
			break;
		case ENTITY_PROFILE: {
			if (onlyExpired) {
				final Iterator<?> it = entityProfileCache.entrySet().iterator();
				while (it.hasNext()) {
					final PkiCachedRecord<?> pair = (PkiCachedRecord<?>) ((Map.Entry) it.next()).getValue();
					if (currentTime - pair.getMillis() > CACHE_DEFAULT_VALIDITY_TIMEOUT) {
						it.remove();
					}
				}
			} else {
				entityProfileCache.clear();
			}
		}
			break;
		case TRUST_CAS: {
			if (onlyExpired) {
				final Iterator<?> it = trustCAsCache.entrySet().iterator();
				while (it.hasNext()) {
					final PkiCachedRecord<?> pair = (PkiCachedRecord<?>) ((Map.Entry) it.next()).getValue();
					if (currentTime - pair.getMillis() > CACHE_DEFAULT_VALIDITY_TIMEOUT) {
						it.remove();
					}
				}
			} else {
				trustCAsCache.clear();
			}
		}
			break;
		case INTERNAL_CA_CERTS: {
			if (onlyExpired) {
				final Iterator<?> it = internalCaCertsCache.entrySet().iterator();
				while (it.hasNext()) {
					final PkiCachedRecord<?> pair = (PkiCachedRecord<?>) ((Map.Entry) it.next()).getValue();
					if (currentTime - pair.getMillis() > CACHE_DEFAULT_VALIDITY_TIMEOUT) {
						it.remove();
					}
				}
			} else {
				internalCaCertsCache.clear();
			}
		}
			break;
		case EXTERNAL_CA_CERTS: {
			if (onlyExpired) {
				final Iterator<?> it = externalCaCertsCache.entrySet().iterator();
				while (it.hasNext()) {
					final PkiCachedRecord<?> pair = (PkiCachedRecord<?>) ((Map.Entry) it.next()).getValue();
					if (currentTime - pair.getMillis() > CACHE_DEFAULT_VALIDITY_TIMEOUT) {
						it.remove();
					}
				}
			} else {
				externalCaCertsCache.clear();
			}
		}
			break;
		case CA_CHAIN: {
			if (onlyExpired) {
				final Iterator<?> it = caChainCache.entrySet().iterator();
				while (it.hasNext()) {
					final PkiCachedRecord<?> pair = (PkiCachedRecord<?>) ((Map.Entry) it.next()).getValue();
					if (currentTime - pair.getMillis() > CACHE_DEFAULT_VALIDITY_TIMEOUT) {
						it.remove();
					}
				}
			} else {
				caChainCache.clear();
			}
		}
			break;
		case CA_ENTITY: {
			if (onlyExpired) {
				final Iterator<?> it = caEntityCache.entrySet().iterator();
				while (it.hasNext()) {
					final PkiCachedRecord<?> pair = (PkiCachedRecord<?>) ((Map.Entry) it.next()).getValue();
					if (currentTime - pair.getMillis() > CACHE_DEFAULT_VALIDITY_TIMEOUT) {
						it.remove();
					}
				}
			} else {
				caEntityCache.clear();
			}
		}
			break;
		case ROOT_CA_CERTS: {
			if (onlyExpired) {
				final Iterator<?> it = rootCACertificateCache.entrySet().iterator();
				while (it.hasNext()) {
					final PkiCachedRecord<?> pair = (PkiCachedRecord<?>) ((Map.Entry) it.next()).getValue();
					if (currentTime - pair.getMillis() > CACHE_DEFAULT_VALIDITY_TIMEOUT) {
						it.remove();
					}
				}
			} else {
				rootCACertificateCache.clear();
			}
		}
			break;
		case TRUSTED_ENTITY_INFOS: {
			if (onlyExpired) {
				final Iterator<?> it = trustedEntityInfosCache.entrySet().iterator();
				while (it.hasNext()) {
					final PkiCachedRecord<?> pair = (PkiCachedRecord<?>) ((Map.Entry) it.next()).getValue();
					if (currentTime - pair.getMillis() > CACHE_DEFAULT_VALIDITY_TIMEOUT) {
						it.remove();
					}
				}
			} else {
				trustedEntityInfosCache.clear();
			}
		}
			break;
		}
	}

	@Schedule(minute = "25,55", hour = "*", persistent = false)
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void timeoutHandler() {
		log.info("purging cache...");
		purgeAll(true);
	}
}
