/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2020
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.utilities;

/**
 * This class models the algorithm to be used when generating the asymmetric key on CBP-OI nodes.
 */
public enum CbpOiAlgorithm {

    RSA1024("0", "rsa1024"),
    RSA2048("1", "rsa2048"),
    RSA3072("2", "rsa3072"),
    RSA4096("3", "rsa4096"),
    RSA7680(null, "rsa7680"),
    RSA15360(null, "rsa15360"),
    ECDSA160("4", null),
    ECDSA192(null, "secp192r1"),
    ECDSA224("5", "secp224r1"),
    ECDSA256("6", "secp256r1"),
    ECDSA384("7", "secp384r1"),
    ECDSA512("8", null),
    ECDSA521("9", "secp521r1"),
    X25519(null, "x25519"),
    X448(null, "x448");

    // The value as defined in ENM enrollment info.
    private final String enrollmentInfoKeySize;

    // The value as defined in CBP OI nodes.
    private final String nodeAlgorithm;

    private CbpOiAlgorithm(final String enrollmentInfoKeySize, final String nodeAlgorithm) {
        this.enrollmentInfoKeySize = enrollmentInfoKeySize;
        this.nodeAlgorithm = nodeAlgorithm;
    }

    /**
     * @return the enrollmentInfoKeySize
     */
    public String getEnrollmentInfoKeySize() {
        return enrollmentInfoKeySize;
    }

    /**
     * @return the nodeAlgorithm
     */
    public String getNodeAlgorithm() {
        return nodeAlgorithm;
    }

    /**
     * Gets the algorithm from given enrollment info key size.
     * 
     * @param enrollmentInfoKeySize
     *            the enrollment info key size.
     * @return the algorithm or null if not supported.
     */
    public static CbpOiAlgorithm fromEnrollmentInfoKeySize(final String enrollmentInfoKeySize) {
        if (enrollmentInfoKeySize == null) {
            return null;
        }
        for (final CbpOiAlgorithm algorithm : CbpOiAlgorithm.values()) {
            if (enrollmentInfoKeySize.equals(algorithm.getEnrollmentInfoKeySize())) {
                return algorithm;
            }
        }
        return null;
    }
}
