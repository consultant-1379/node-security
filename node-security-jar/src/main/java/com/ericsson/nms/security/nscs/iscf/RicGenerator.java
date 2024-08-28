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

import java.security.SecureRandom;
import java.util.Random;
import javax.inject.Inject;

/**
 * Generates a random string to be used as the cryptographic key when encrypting ISCF data
 *
 * @author ealemca
 */
public class RicGenerator {

    /**
     * The set of valid characters from which to choose a random alpha-numeric String
     */
    private static final char[] VALID_CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456879".toCharArray();

    @Inject
    IscfConfigurationBean config;

    /**
     * Generate a random alpha-numeric String to be used as a key for encrypting ISCF content
     *
     * @return The random alpha-numeric String used as a key for encrypting ISCF content
     */
    public String generateRIC() {
        final SecureRandom srand = new SecureRandom();
        final Random rand = new Random();
        final int numChars = config.getRbsIntegrityCodeLength();
        char[] buff = new char[numChars];
        for (int i = 0; i < numChars; ++i) {
            if ((i % 10) == 0) {
                // Up to 10 purely random chars can be selected before running out
                // of entropy, so we must re-seed
                rand.setSeed(srand.nextLong());
            }
            buff[i] = VALID_CHARACTERS[rand.nextInt(VALID_CHARACTERS.length)];
        }
        return new String(buff);
    }

    /**
     * Generate a random value to be used as a salt for encrypting ISCF content
     *
     * @return The random value to be used as a salt for encrypting ISCF content
     */
    public byte[] generateSalt() {
        final byte[] saltBytes = new byte[config.getSaltLength()];
        new SecureRandom().nextBytes(saltBytes);
        return saltBytes;
    }

}
