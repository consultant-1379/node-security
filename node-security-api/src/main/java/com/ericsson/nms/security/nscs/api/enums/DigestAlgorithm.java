/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.nms.security.nscs.api.enums;

/**
 *
 * @author enmadmin
 */
public enum DigestAlgorithm {
    
    SHA1("SHA-1", "SHA1"),
    SHA256("SHA-256", "SHA256"),
    SHA512("SHA-512", "SHA512"),
    MD5("MD5", "MD5");

    private final String standardDigestAlgorithmValue;
    private final String enmDigestAlgorithmValue;

    private DigestAlgorithm(final String standardAlgorithmValue, String enmAlgorithmValue) {
        this.standardDigestAlgorithmValue = standardAlgorithmValue;
        this.enmDigestAlgorithmValue = enmAlgorithmValue;
    }

    public String getStandardDigestAlgorithmValue() {
        return this.standardDigestAlgorithmValue;
    }

    public String getEnmDigestAlgorithmValue() {
        return this.enmDigestAlgorithmValue;
    }

    public String getDigestValuePrefix() {
        return getEnmDigestAlgorithmValue() + " Fingerprint=";
    }
    
    @Override
    public String toString() {
        return this.standardDigestAlgorithmValue;
    }
    
}
