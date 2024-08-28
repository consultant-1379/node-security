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
package com.ericsson.oss.services.gdpr.anonymize;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GdprHashing {
    private static final Logger logger = LoggerFactory.getLogger(GdprAnonymizerImpl.class);

    String gdprBuildHashing(final String toEncrypt, final String algo) throws NoSuchAlgorithmException {
        logger.debug("gdpr BuildHashing : starts");
        final MessageDigest digest = MessageDigest.getInstance(algo);

        final byte[] hash = digest.digest(
                toEncrypt.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().encodeToString(hash);
    }
}