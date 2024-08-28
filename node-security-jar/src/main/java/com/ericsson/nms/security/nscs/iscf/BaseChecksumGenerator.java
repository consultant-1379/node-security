/*
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package com.ericsson.nms.security.nscs.iscf;

import com.ericsson.nms.security.nscs.api.enums.DigestAlgorithm;
import com.ericsson.nms.security.nscs.api.model.CertSpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.util.List;
import javax.inject.Inject;

/**
 * Base class containing common functionality for generating Security Configuration Checksums
 * for ISCF
 *
 * @author ealemca
 */
public abstract class BaseChecksumGenerator {

    @Inject
    protected IscfConfigurationBean config;

    /**
     * Calculates the Security Configuration Checksum depending on what Security Level is requested
     * and if IPSec is required.
     *
     * This is calculated in the same manner as in OSS-RC:
     *
     * <blockquote>
     * The checksum(s) of the configuration calculated by the node. The format
     * is: SCC1[,SCC2[,SCC3]],[SCCIPSecCUS] i.e. a comma-separated list of
     * checksums, where the checksum(s) or SL2 and SL3 are present only if
     * applicable. Checksum for IPSec CUS is irrespective of SL. Each SCCx must
     * be encoded in one of these two formats: 1) A string of hex-encoded bytes,
     * e.g. the bytes 0,17,10 is encoded as "00110A" 2) A "SHA1=" header, followed
     * by ":"-separated hex-encoded bytes, e.g. the bytes 0,17,10 is encoded
     * as "SHA1=00:11:0A"
     * </blockquote>
     *
     * The individual SCCs are calculated like so:
     *
     * <pre>
     * SCC1 =  SHA1(MOM_VAL(Security.fileTransferClientMode))
     *         ^ SHA1(MOM_VAL(Security.telnetAndFTPServersActive))
     *         ^ SHA1(MOM_VAL(Security.requestedSecurityLevel))
     *         ^ SHA1(MOM_VAL(Security.operationalSecurityLevel))
     *
     * SCC2 =   SHA1(/c/security/trustedCerts/corba_peers/cert_1)
     *         ^... ^     SHA1(/c/security/trustedCerts/corba_peers/cert_n0)
     *         ^ SHA1(String.valueOf(n0).getBytes(“UTF-8”))
     *         ^ SHA1(MOM_VAL(ManagedElementData.logonServerAddress))
     *
     * SCC3 =   SHA1(/c/security/trustedCerts/local_aa_db_file_signers/cert_1)
     *         ^... ^ SHA1(/c/security/trustedCerts/local_aa_db_file_signers/cert_n1)
     *         ^ SHA1(/c/security/trustedCerts/aa_servers/cert_1)
     *         ^... ^ SHA1(/c/security/trustedCerts/aa_servers/cert_n2)
     *         ^ SHA1(String.valueOf(n1+n2).getBytes(“UTF-8”))
     *         ^ SHA1(/c/security/authorization.xml)
     *         ^ SHA1(/c/security/userdb.xml)
     *         ^ SHA1(MOM_VAL(Security. aAServerIPAddressList[1]))
     *         ^... ^ SHA1(MOM_VAL(Security. aAServerIPAddressList[10]))
     *         ^ SHA1(MOM_VAL(Security.authorizationCacheTimeOut))
     *         ^ SHA1(MOM_VAL(Security. certExpirWarnTime))
     *
     * SCCIPSECCUS =  SHA1(/c/security/trustedCerts/ipsec_peers/cert_1)
     *         ^... ^     SHA1(/c/security/trustedCerts/ipsec_peers/cert_n0)
     *         ^ SHA1(String.valueOf(n0).getBytes("UTF-8"))
     *         ^ SHA1(MOM_VAL(IPSec. certExpirWarnTime))
     *
     * </pre>
     *
     * In English, String values of above settings converted to byte[], SHA-1 hashed then XORed with each other
     *
     * <p>
     * Reference: <a href="http://erilink.ericsson.se/eridoc/erl/objectId/09004cff87876ace?docno=EAB/FJK-09:0037Uen&format=msw8">
     *  CPPSecActivation4AutoIntegration.doc
     *  </a>
     * </p>
     *
     * @param data The node auto integration data gathered by the appropriate
     *             data collector
     * @return The calculated Security Configuration Checksums in Hex format encoded using UTF-8 and
     *         comma-separated
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     * @throws java.security.cert.CertificateEncodingException
     */
    public abstract String getSecurityConfigChecksum(final NodeAIData data)
            throws NoSuchAlgorithmException, UnsupportedEncodingException, CertificateEncodingException;

    protected void addDigest(final List<byte[]> digests, final String text, final MessageDigest md)
            throws UnsupportedEncodingException {
        digests.add(md.digest(text.getBytes(IscfConstants.UTF8_CHARSET)));
    }

    protected void addDigest(final List<byte[]> digests, final byte[] bytes, final MessageDigest md)
            throws UnsupportedEncodingException {
        digests.add(md.digest(bytes));
    }

    protected String encode(final byte[] bytes) {
        return DigestAlgorithm.SHA1.getEnmDigestAlgorithmValue() + "=" + bytesToHex(bytes);
    }

    private String bytesToHex(final byte[] bytes) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            if (i < bytes.length-1)
                sb.append(':');
        }
        return sb.toString().toUpperCase();
    }

    protected byte[] mergeDigests(final List<byte[]> digests) {
        final byte[] result = new byte[digests.get(0).length];
        for (byte[] digest : digests) {
            for (int i = 0; i < digest.length; i++) {
                result[i] ^= digest[i];
            }
        }
        return result;
    }
    
    protected void addIpsecChecksum(final List<byte[]> digests, final NodeAIData data, final MessageDigest md)
            throws UnsupportedEncodingException, CertificateEncodingException {
//        for(CertSpec spec : data.getIpsecCertSpecs()) {
//            addDigest(digests, spec.getCertHolder().getEncoded(), md);
//        }
//        addDigest(digests, String.valueOf(data.getIpsecCertSpecs().size()), md
        addDigest(digests, String.valueOf(data.getIpsecCertExpirWarnTime()), md);
    }
    
    protected void addLevelTwoDigests(final List<byte[]> digests, final NodeAIData data, final MessageDigest md)
            throws UnsupportedEncodingException, CertificateEncodingException {
        for(CertSpec spec : data.getSecLevelCertSpecs()) {
            addDigest(digests, spec.getCertHolder().getEncoded(), md);
        }
        addDigest(digests, String.valueOf(data.getSecLevelCertSpecs().size()), md);
        addDigest(digests, data.getLogonServerAddress(), md);
    }


}
