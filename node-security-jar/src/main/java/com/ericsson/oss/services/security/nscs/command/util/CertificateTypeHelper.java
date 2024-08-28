/*------------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2018
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.command.util;

import java.util.Arrays;
import java.util.List;

/**
 * Auxiliary class to manage certificate type.
 */
public class CertificateTypeHelper {

    static final List<String> validCertificateTypes = Arrays.asList("IPSEC", "OAM");

    /**
     * Checks if the given certificate type is valid or not.
     *
     * @param certificateType
     *            the certificate type value.
     * @return true if the given certificate type is valid, false otherwise.
     * @throws IllegalArgumentException
     *             if the given certificate type is null.
     */
    public static boolean isCertificateTypeValid(final String certificateType) throws IllegalArgumentException {
        if (certificateType == null) {
            throw new IllegalArgumentException("null certificate type.");
        }
        return validCertificateTypes.contains(certificateType);
    }

    /**
     * Gets the list of valid certificate types.
     *
     * @return the valid certificate types.
     */
    public static List<String> getValidCertificateTypes() {
        return validCertificateTypes;
    }

}
