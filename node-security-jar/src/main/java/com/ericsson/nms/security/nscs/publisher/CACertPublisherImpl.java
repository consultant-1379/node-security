/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.publisher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.bouncycastle.openssl.PEMWriter;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.CACertSftpPublisher;
import com.ericsson.nms.security.nscs.api.model.CertSpec;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerException;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerIF;
import com.ericsson.nms.security.nscs.cpp.ipsec.util.SmrsUtils;
import com.ericsson.nms.security.nscs.cpp.model.SmrsAccountInfo;
import com.ericsson.nms.security.smrs.api.exception.SmrsDirectoryException;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.itpf.smrs.SmrsAccount;
import com.ericsson.oss.itpf.smrs.SmrsAddressRequest;
import com.ericsson.oss.itpf.smrs.SmrsService;

/**
 *
 * @author enmadmin
 */
@Stateless
public class CACertPublisherImpl implements CACertSftpPublisher {

    private static final String CERT_BEGIN = "-----BEGIN CERTIFICATE-----";
    private static final String CERT_END = "-----END CERTIFICATE-----";

    @Inject
    Logger log;

    @EJB
    NscsPkiEntitiesManagerIF nscsPkiManager;

    @Inject
    private SmrsUtils smrsUtils;

    @EServiceRef
    SmrsService smrsService;

    @Override
    public void publishCertificates(final String neType, final String neName) throws CertificateException, NscsPkiEntitiesManagerException {
        log.info("publishCertificates()");
        //Fetch latest CA certificates
        final Map<String, List<X509Certificate>> cas = nscsPkiManager.getCAsTrusts();
        final Set<X509Certificate> certs = getCerts(cas);

        final List<SmrsAccountInfo> accountList = getSmrsAccountInfoForCertificate(neType, neName);
        //Publish certificates to each directory
        boolean isFailed = false;
        log.debug("Publishing [{}] certificates to SMRS locations", certs.size());
        for (final X509Certificate cert : randomizeSet(certs)) {
            try {
                uploadCertificateToSmrs(cert, accountList);
            } catch (final IOException ex) {
                log.error("Failed to publish certificate [" + cert.getSerialNumber() + "]", ex);
                isFailed = true;
            }
        }

        if (isFailed) {
            log.debug("CACertPublisherImpl: failed to publish certificates");
            throw new CertificateException();
        }
        log.debug("Total number of [" + certs.size() + "] certificates are published.");
    }

    @Override
    public void publishCertificates(final Set<CertSpec> certSpecs, final String neType, final String neName) throws CertificateException {
        log.info("publishCertificates(Set<CertSpec>)");

        final List<SmrsAccountInfo> accountList = getSmrsAccountInfoForCertificate(neType, neName);
        //Publish certificates to each directory
        boolean isFailed = false;
        log.debug("Publishing [{}] certificates to SMRS locations", certSpecs.size());
        for (final CertSpec certSpec : certSpecs) {
            try {
                uploadCertificateToSmrs(certSpec.getCertHolder(), accountList);
            } catch (final IOException ex) {
                log.error("Failed to publish certificate [" + certSpec.getCertHolder().getSerialNumber() + "]", ex);
                isFailed = true;
            }
        }

        if (isFailed) {
            log.debug("CACertPublisherImpl: failed to publish certificates");
            throw new CertificateException();
        }
        log.debug("Total number of [" + certSpecs.size() + "] certificates are published.");
    }

    @Override
    public Set<CertSpec> getCACertSpecs(final String caDN) throws CertificateException, NscsPkiEntitiesManagerException {
        log.debug("getCACertSpecs([{}])", caDN);
        final Set<CertSpec> certSpecs = new HashSet<>();
        final List<X509Certificate> certs = nscsPkiManager.getCATrusts(caDN);
        if ((certs != null) && !certs.isEmpty()) {
            for (final X509Certificate cert : certs) {
                final CertSpec certSpec = new CertSpec(cert);
                certSpecs.add(certSpec);
            }
        } else {
            // Search DN in external CAs
        }
        return certSpecs;
    }

    private Set<X509Certificate> getCerts(final Map<String, List<X509Certificate>> cas) {
        final Set<X509Certificate> certs = new HashSet<>();
        for (final List<X509Certificate> ca : cas.values()) {
            for (final X509Certificate cert : ca) {
                certs.add(cert);
            }
        }
        return certs;
    }

    public static byte[] objectAsPemBytes(final Object o) throws IOException {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final PEMWriter pemWriter = new PEMWriter(new PrintWriter(stream));
        pemWriter.writeObject(o);
        pemWriter.flush();
        pemWriter.close();
        return stream.toByteArray();
    }

    /**
     * WORKAROUND for FILE RA issue
     */
    private List<X509Certificate> randomizeSet(final Set<X509Certificate> certSet) {
        final List<X509Certificate> certList = new ArrayList<X509Certificate>();
        certList.addAll(certSet);
        Collections.shuffle(certList, new Random());
        log.debug("Shuffled set size is [{}]", certList.size());
        return certList;
    }

    private List<SmrsAccountInfo> getSmrsAccountInfoForCertificate(final String neType, final String neName)
            throws SmrsDirectoryException, CertificateException {

        log.info("Get SMRS Account Info for Certificate without publishing");

        final List<SmrsAccountInfo> accounts = new ArrayList<>();
        final SmrsAccount account = smrsService.getNodeSpecificAccount(nscsPkiManager.getSmrsAccountTypeForNscs(), neType, neName);
        final SmrsAddressRequest smrsAddrReq = new SmrsAddressRequest();
        smrsAddrReq.setAccountType(nscsPkiManager.getSmrsAccountTypeForNscs());
        smrsAddrReq.setNeType(neType);
        log.debug("Certificate account is registered with sftpPublisher: [{}] ", account);
        final String addresses = smrsService.getFileServerAddress(smrsAddrReq);
        final char[] smrsPassword = account.getPassword().toCharArray();
        if (addresses != null) {
            for (final String address : addresses.split(",")) {
                final SmrsAccountInfo accountInfo = new SmrsAccountInfo(account.getUserName(), smrsPassword, address, account.getHomeDirectory(),
                        account.getRelativePath());
                accounts.add(accountInfo);
            }
        }
        return accounts;
    }

    private void uploadCertificateToSmrs(final X509Certificate cert, final List<SmrsAccountInfo> smrsAccountList)
            throws CertificateException, IOException {
        log.info("publishCertificate([" + cert.getSerialNumber() + "])");
        final CertSpec certSpec = new CertSpec(cert);
        final String certFileName = certSpec.getFileName();
        //        final byte[] certBytes = objectAsPemBytes(cert);
        final byte[] certBytes = cert.getEncoded();

        for (final SmrsAccountInfo smrsAccount : smrsAccountList) {
            final String settingUri = smrsUtils.uploadFileToSmrs(smrsAccount, certFileName, certBytes);
            log.debug("Uploaded file to SMRS: {}", settingUri);
        }
    }

}
