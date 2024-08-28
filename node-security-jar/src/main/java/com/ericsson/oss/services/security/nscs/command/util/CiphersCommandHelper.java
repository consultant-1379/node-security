/*-----------------------------------------------------------------------------
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

import java.util.HashMap;
import java.util.Map;

/**
 * Auxiliary class to manage 'secadm ciphers' commands.
 *
 * @author emaborz
 *
 */
public class CiphersCommandHelper {
    static final Map<String, String> cipherMoNames = new HashMap<String, String>();
    static {
        cipherMoNames.put("SSH/SFTP", "Ssh");
        cipherMoNames.put("SSL/HTTPS/TLS", "Tls");
    }

    public static Map<String, String> getCipherMoNames() {
        return cipherMoNames;
    }
}
