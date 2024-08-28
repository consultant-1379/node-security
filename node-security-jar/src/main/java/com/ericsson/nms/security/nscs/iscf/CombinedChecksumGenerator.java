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

import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.api.model.CertSpec;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.util.LinkedList;
import java.util.List;

/**
 * Class for generating the Security Configuration Checksum for IPSec and Security Level ISCF
 *
 * @author ealemca
 */
public class CombinedChecksumGenerator extends BaseChecksumGenerator {

    @Override
    public String getSecurityConfigChecksum(final NodeAIData data)
            throws NoSuchAlgorithmException, UnsupportedEncodingException, CertificateEncodingException {
        final MessageDigest md = MessageDigest.getInstance(IscfConstants.DEFAULT_HASH_ALGORITHM);
        String checksum = "";
        final List<byte[]> digests = new LinkedList<>();
        addLevelOneChecksum(digests, data, md);
        final String levelOne = encode(mergeDigests(digests));
        checksum += levelOne;
        if(SecurityLevel.LEVEL_2.compareTo(data.getWantedSecLevel()) == 0) {
            digests.clear();
            addLevelTwoDigests(digests, data, md);
            final String levelTwo = encode(mergeDigests(digests));
            checksum += "," + levelTwo;
        }
        digests.clear();
        addIpsecChecksum(digests, data, md);
        encode(mergeDigests(digests));
        return checksum;
    }

    private void addLevelOneChecksum(final List<byte[]> digests, final NodeAIData data, final MessageDigest md)
            throws UnsupportedEncodingException {
        addDigest(digests, ("Secure".equals(config.getFileTransferClientMode()) ? "1" : "0"), md);
        addDigest(digests, String.valueOf(!"Secure".equals(config.getTelnetAndFtpServersMode())), md);
        addDigest(digests, data.getWantedSecLevel().toString(), md);
        // TODO: change once Level 3 is supported. For now, these are the same
        addDigest(digests, data.getWantedSecLevel().toString(), md);
    }

//    private void addLevelTwoDigests(final List<byte[]> digests, final NodeAIData data, final MessageDigest md)
//            throws UnsupportedEncodingException, CertificateEncodingException {
//        for(CertSpec spec : data.getSecLevelCertSpecs()) {
//            addDigest(digests, spec.getCertHolder().getEncoded(), md);
//        }
//        addDigest(digests, String.valueOf(data.getSecLevelCertSpecs().size()), md);
//        addDigest(digests, data.getLogonServerAddress(), md);
//    }

}
