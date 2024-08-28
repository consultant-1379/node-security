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
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import org.slf4j.Logger;

/**
 * Class for creating ISCF &lt;validators&gt; content of the ISCF XML file used
 * during auto-integration of a node. Content includes the hash of the ISCF body
 * and the Hash-based Message Authentication code.
 *
 * @author ealemca
 */
public class IscfValidatorsGenerator {

    @Inject
    private Logger log;

    /**
     * Returns an MD5 digest of a String
     *
     * @param text
     * @return
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     */
    public byte[] getChecksum(final String text) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        log.info("Calculating hash for ISCF XML content validators");
        final MessageDigest md = MessageDigest.getInstance(IscfConstants.DEFAULT_HASH_ALGORITHM);
        return md.digest(text.getBytes(IscfConstants.UTF8_CHARSET));
    }

    /**
     * Calculates a Hash-based Message Authentication Code for a String based on
     * a provided key
     *
     * @param text
     * @param key
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws UnsupportedEncodingException
     */
    public byte[] getHmac(final String text, final byte[] key)
            throws NoSuchAlgorithmException,
            InvalidKeyException,
            UnsupportedEncodingException {
        
        log.trace("Calculating HMAC for ISCF XML content validators");
        final Mac hMac = Mac.getInstance(IscfConstants.DEFAULT_HMAC_ALGORITHM);
        final Key secretKey = new SecretKeySpec(key, 0, key.length, IscfConstants.DEFAULT_HMAC_ALGORITHM);
        hMac.init(secretKey);
        return hMac.doFinal(text.getBytes(IscfConstants.UTF8_CHARSET));
 
    }

}
