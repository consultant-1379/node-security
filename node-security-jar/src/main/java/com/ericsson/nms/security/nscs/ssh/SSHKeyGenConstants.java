/*------------------------------------------------------------------------------
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
package com.ericsson.nms.security.nscs.ssh;

public class SSHKeyGenConstants {
    public static final String SSH_KEY_TO_BE_CREATED = "ssh_key_to_be_created";
    public static final String SSH_KEY_TO_BE_UPDATED = "ssh_key_to_be_updated";
    public static final String SSH_KEY_TO_BE_DELETED = "ssh_key_to_be_deleted";

    public static final String SSH_KEY_INVALID = "Invalid_Key";
    public static final String SSH_KEY_EMPTY = "";

    public static final String OBFUSCATED_VALID_SSH_KEY = "*******";

    // key in /ericsson/tor/data/global.properties for the ENM UI presentation server
    public static final String UI_PRES_SERVER_KEY = "UI_PRES_SERVER";
    private SSHKeyGenConstants() {}
}
