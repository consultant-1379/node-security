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

/**
 * Holds constant values related to ISCF XML generation. May be used by observers of
 * configuration changes or can be used directly
 *
 * @author ealemca
 */
public final class IscfConstants {

    private IscfConstants() {}

    public final static String UTF8_CHARSET = "UTF-8";
    public final static boolean ENCRYPT_FLAG = true;
    public final static boolean DECRYPT_FLAG = false;
    public final static String ISCF_XSD_FILENAME = "ISCF.xsd";
    public final static String FILE_FORMAT_VERSION = "1.0";
    public final static String DEFAULT_HASH_ALGORITHM = "SHA-1";
    public final static String DEFAULT_HMAC_ALGORITHM = "HMacSHA1";
    public final static Integer DEFAULT_ENROLLMENT_TIME_LIMIT = 1800;

}
