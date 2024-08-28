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
package com.ericsson.nms.security.nscs.api.enums;

public enum AlgorithmKeys {

    RSA_1024("RSA", 1024),
    RSA_2048("RSA", 2048),
    RSA_3072("RSA", 3072),
    RSA_4096("RSA", 4096),
    RSA_8192("RSA", 8192),
    RSA_16384("RSA", 16384),
    DSA_1024("DSA", 1024),
    ECDSA_160("ECDSA", 160),
    ECDSA_224("ECDSA", 224),
    ECDSA_256("ECDSA", 256),
    ECDSA_384("ECDSA", 384),
    ECDSA_512("ECDSA", 512),
    ECDSA_521("ECDSA", 521);

    private final String algorithm;
    private final int keySize;

    AlgorithmKeys(final String algorithm, final int keySize) {
        this.algorithm = algorithm;
        this.keySize = keySize;
    }

    /*
     * Return the Enum name
     * @see java.lang.Enum#name()
     */
    @Override
    public String toString() {
        return this.name();
    }

    /**
     * Get the Algorithm name
     *
     * @return Algorithm name
     */
    public String getAlgorithm() {
        return this.algorithm;
    }

    /**
     * Get the KeySize integer
     *
     * @return KeySize
     */
    public int getKeySize() {
        return this.keySize;
    }

    public static AlgorithmKeys toAlgorithmKeys(final String algorithm, final Integer keySize) {
        if (algorithm != null && keySize != null) {
            for (AlgorithmKeys algorithmKey : AlgorithmKeys.values()) {
                if (algorithm.equals(algorithmKey.getAlgorithm()) &&
                        (keySize.equals(algorithmKey.getKeySize()))) {
                    return algorithmKey;
                }
            }
        }
        return null;
    }

}
