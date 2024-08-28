/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2023
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.command.util;

/**
 * Auxiliary utility class containing constants to manage commands.
 * 
 * It shall not be instantiated hence a private constructor is defined to hide the implicit public one.
 */
public class NscsCommandConstants {

    public static final String SECADM_COMMAND_PREFIX = "secadm ";
    public static final String GENERIC_SECADM_COMMAND_BODY = "...";

    private NscsCommandConstants() {
        throw new IllegalStateException("Utility class shall not be instantiated.");
    }

}
