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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.util.LinkedList;
import java.util.List;

/**
 * Class for generating the Security Configuration Checksum for IPSec only
 * @author ealemca
 */
public class IpsecChecksumGenerator extends BaseChecksumGenerator {

    @Override
    public String getSecurityConfigChecksum(final NodeAIData data)
            throws NoSuchAlgorithmException, UnsupportedEncodingException, CertificateEncodingException {
        final MessageDigest md = MessageDigest.getInstance(IscfConstants.DEFAULT_HASH_ALGORITHM);
        final List<byte[]> digests = new LinkedList<>();
        addIpsecChecksum(digests, data, md);
        return encode(mergeDigests(digests));
    }

}
