/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2019
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.cucumber.helper.ntp;

public class NtpTestConstants {

    public static final String COMMAND_SECADM = "secadm";
    public static final String COMMAND_NTP_LIST = "ntp list --nodelist ";
    public static final String COMMAND_NTP_REMOVE = "ntp remove ";
    public static final String COMMAND_NTP_REMOVE_NODE_NAME_OPTION = "--nodename ";
    public static final String COMMAND_NTP_REMOVE_KEY_ID_OPTION = " --keyidlist 1";
    public static final String COMMAND_NTP_CONFIGURE = "ntp configure --nodelist ";

    private NtpTestConstants() {
        throw new IllegalStateException("Constants class for NTP");
    }

}
