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

public final class GdprConstantsUtils {

    /**
     * hash algorithm used for hiding input data
     */
    public static final String GDPR_HASH_ALGO = "SHA-256";

    /**
     * key in global.properties file to be searched for getting identifier of the ENM installation
     * Identifier of the ENM installation will be a sort of salt to be combined with input data before scrambling
     */
    public static final String GDPR_GLB_PROP_ID_KEY = "web_host_default";

    /**
     * Exception message propagated when something wrong while getting identifier of the ENM installation
     */
    public static final String GDPR_GLB_PROP_ID_NOT_READ = "not possible to retrieve info from global properties file";
    public static final String GDPR_GLB_PROP_ID_NOT_OK = "identifier not well formatted";
    public static final String GDPR_INVALID_INPUT_PARMS = "invalid input params null";

    private GdprConstantsUtils () {}
}
