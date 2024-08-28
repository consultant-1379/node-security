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
package com.ericsson.nms.security.nscs.cpp.seclevel.util;

import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;

/**
 * This class hold the constant values related to Cpp Get Security Level
 *
 * @author tcsgoja
 *
 */
public class CppGetSecurityLevelConstants {

    private CppGetSecurityLevelConstants() {
        super();
    }

    public static final String NODE_NAME_HEADER = "Node Name";
    public static final String NODE_SECURITY_LEVEL_HEADER = "Node Security Level";
    public static final String UNDEFINED_SECURITY_LEVEL = "undefined";
    public static final String ERROR_DETAILS_HEADER = "Error Details";
    public static final String NA = "NA";
    public static final int NO_OF_COLUMNS = 4;
    public static final String LOCAL_AA_MODE = "Local AA Mode";
    public static final String SUGGESTED_SOLUTION = "Suggested Solution";
    public static final String USER_AUTHENTICATION_AND_AUTHORIZATION = "userAuthenticationAndAuthorization";
    public static final String LOCALAADATABASE = "LOCAL_AA_DATABASE";
    public static final String LOCALAADATABASEUNCONFIRMED = "LOCAL_AA_DATABASE_UNCONFIRMED";
    public static final String NODE_PASSPHRASE = "NODE_PASSWORD";
    public static final String ACTIVATED = "ACTIVATED";
    public static final String DEACTIVATED = "DEACTIVATED";
    public static final String UNCONFIRMED = "UNCONFIRMED";
    public static final String LOCAL_AA_FEATURE_SUPPORTED_NODE_TYPE_SUGGESTED_SOLUTION = "Check online help for Local AA mode supported node types.";
    public static final String ERROR_IN_READING_OPERATIONAL_SECURITY_LEVEL_ATTRIBUTE_FOR_THE_NODES = "Error occurred while reading Security Level for the given nodes.";
    public static final String COLON_SEPARATOR = " : ";

    public static final String LOCAL_AA_MODE_DETAILS_MSG = LOCAL_AA_MODE + COLON_SEPARATOR + NscsErrorCodes.UNSUPPORTED_NODE_TYPE;
    public static final String LOCAL_AA_MODE_SUGGESTED_SOLUTION = LOCAL_AA_MODE + COLON_SEPARATOR
            + LOCAL_AA_FEATURE_SUPPORTED_NODE_TYPE_SUGGESTED_SOLUTION;

    public static final String NODE_SECURITY_LEVEL_ERROR_DETAILS_MESSAGE = NODE_SECURITY_LEVEL_HEADER + COLON_SEPARATOR
            + "An error occured while reading Operational Security Level attribute.";
    public static final String NODE_SECURITY_LEVEL_SUGGESTED_SOLUTION = NODE_SECURITY_LEVEL_HEADER + COLON_SEPARATOR
            + "Check error log for more details.";
}
