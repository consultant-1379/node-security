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

import java.util.HashMap;
import java.util.Map;

import com.ericsson.nms.security.nscs.api.enums.CertificateType;
import com.ericsson.nms.security.nscs.data.ModelDefinition;

/**
 * Auxiliary class to determine the name of CBP OI MOs.
 *
 */
public class CbpOiMoNaming {

    private static final String CBPOI_SERVER_NAME_PRIMARY = "primary";
    private static final String CBPOI_SERVER_NAME_FALLBACK = "fallback";

    private CbpOiMoNaming() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Map containing association between specific MO type and the default name of its instances according to given certificate type.
     */
    static final Map<String, Map<String, String>> theDefaultMoNamesByCertType = new HashMap<>();

    static {
        // CertificateType-dependent names
        final Map<String, String> cmpServerGroupNames = new HashMap<>();
        cmpServerGroupNames.put(CertificateType.OAM.name(), "1");

        theDefaultMoNamesByCertType.put(ModelDefinition.CMP_SERVER_GROUP_TYPE, cmpServerGroupNames);
    }

    /**
     * Returns the fixed name of an MO of given type.
     *
     * @param moType
     *            the MO type.
     * @return the fixed MO name.
     */
    public static String getName(final String moType) {

        String moName = null;
        String illegalArgumentMessage = null;

        if (moType != null) {
            switch (moType) {
            case ModelDefinition.SYSTEM_TYPE:
                // break intentionally omitted
            case ModelDefinition.LDAP_TYPE:
                // break intentionally omitted
            case ModelDefinition.CBP_OI_SECURITY_TYPE:
                // break intentionally omitted
            case ModelDefinition.CBP_OI_TLS_TYPE:
                // break intentionally omitted
            case ModelDefinition.SIMPLE_AUTHENTICATED_TYPE:
                // break intentionally omitted
            case ModelDefinition.TCP_TYPE:
                // break intentionally omitted
            case ModelDefinition.LDAPS_TYPE:
                // break intentionally omitted
            case ModelDefinition.KEYSTORE_TYPE:
                // break intentionally omitted
            case ModelDefinition.CMP_TYPE:
                // break intentionally omitted
            case ModelDefinition.CERTIFICATE_AUTHORITIES_TYPE:
                // break intentionally omitted
            case ModelDefinition.CMP_SERVER_GROUPS_TYPE:
                // break intentionally omitted
            case ModelDefinition.CMP_SERVER_TYPE:
                // break intentionally omitted
            case ModelDefinition.TRUSTSTORE_TYPE:
                // break intentionally omitted
            case ModelDefinition.ASYMMETRIC_KEYS_TYPE:
                // break intentionally omitted
            case ModelDefinition.KEYSTORE_CERTIFICATES_TYPE:
                moName = "1";
                break;
            default:
                illegalArgumentMessage = String.format("invalid moType [%s]", moType);
                break;
            }
        } else {
            illegalArgumentMessage = "null moType";
        }

        if (illegalArgumentMessage != null) {
            throw new IllegalArgumentException(illegalArgumentMessage);
        }

        return moName;
    }

    /**
     * Returns the name of an MO of given type for the given certificate type.
     *
     * @param moType
     *            the MO type.
     * @param certificateType
     *            the certificate type.
     * @return the MO name or null if name not defined for the given MO type.
     */
    public static String getNameByCertificateType(final String moType, final String certificateType) {

        String moName = null;
        String illegalArgumentMessage = null;

        if (moType != null && certificateType != null) {
            if (ModelDefinition.CMP_SERVER_GROUP_TYPE.equals(moType)) {
                moName = theDefaultMoNamesByCertType.get(moType).get(certificateType);
            } else {
                illegalArgumentMessage = String.format("invalid moType [%s]", moType);
            }
        } else {
            illegalArgumentMessage = String.format("invalid moType [%s] or certificateType [%s]", moType, certificateType);
        }

        if (illegalArgumentMessage != null) {
            throw new IllegalArgumentException(illegalArgumentMessage);
        }

        return moName;
    }

    /**
     * Returns the name of the server MO according to given primary flag.
     * 
     * @param isPrimary
     *            true if primary server, false if fallback server.
     * @return the name of the server MO.
     */
    public static String getServerName(final Boolean isPrimary) {
        if (isPrimary) {
            return CBPOI_SERVER_NAME_PRIMARY;
        } else {
            return CBPOI_SERVER_NAME_FALLBACK;
        }
    }
}
