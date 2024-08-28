/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
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
import java.util.Map.Entry;
import java.util.Set;

import com.ericsson.nms.security.nscs.api.command.types.CredentialsCommand;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NetworkElementSecurity;

public class CredentialsHelper {

    public static final String UNDEFINED_CREDENTIALS = "undefined";

    private static final String passWord = "password";

    static final Map<String, String> theAttributes = new HashMap<String, String>();
    static {
        theAttributes.put(CredentialsCommand.ROOT_USER_NAME_PROPERTY, NetworkElementSecurity.ROOT_USER_NAME);
        theAttributes.put(CredentialsCommand.ROOT_USER_PASSWORD_PROPERTY, NetworkElementSecurity.ROOT_USER_PASSWORD);
        theAttributes.put(CredentialsCommand.NORMAL_USER_NAME_PROPERTY, NetworkElementSecurity.NORMAL_USER_NAME);
        theAttributes.put(CredentialsCommand.NORMAL_USER_PASSWORD_PROPERTY, NetworkElementSecurity.NORMAL_USER_PASSWORD);
        theAttributes.put(CredentialsCommand.SECURE_USER_NAME_PROPERTY, NetworkElementSecurity.SECURE_USER_NAME);
        theAttributes.put(CredentialsCommand.SECURE_USER_PASSWORD_PROPERTY, NetworkElementSecurity.SECURE_USER_PASSWORD);
        theAttributes.put(CredentialsCommand.NWIEA_SECURE_USER_NAME_PROPERTY, NetworkElementSecurity.NWIEA_SECURE_USER_NAME);
        theAttributes.put(CredentialsCommand.NWIEA_SECURE_PASSWORD_PROPERTY, NetworkElementSecurity.NWIEA_SECURE_USER_PASSWORD);
        theAttributes.put(CredentialsCommand.NWIEB_SECURE_USER_NAME_PROPERTY, NetworkElementSecurity.NWIEB_SECURE_USER_NAME);
        theAttributes.put(CredentialsCommand.NWIEB_SECURE_PASSWORD_PROPERTY, NetworkElementSecurity.NWIEB_SECURE_USER_PASSWORD);
        theAttributes.put(CredentialsCommand.LDAP_APPLICATION_USER_NAME_PROPERTY, NetworkElementSecurity.LDAP_APPLICATION_USER_NAME);
        theAttributes.put(CredentialsCommand.LDAP_APPLICATION_USER_PASSWORD_PROPERTY, NetworkElementSecurity.LDAP_APPLICATION_USER_PASSWORD);
        theAttributes.put(CredentialsCommand.NODECLI_USER_NAME_PROPERTY, NetworkElementSecurity.NODECLI_USER_NAME);
        theAttributes.put(CredentialsCommand.NODECLI_USER_PASSPHRASE_PROPERTY, NetworkElementSecurity.NODECLI_USER_PASSPHRASE);
    }

    public static Set<Entry<String, String>> entrySet() {
        return theAttributes.entrySet();
    }

    public static Set<String> keySet() {
        return theAttributes.keySet();
    }

    public static String toAttribute(final String cliCommandParam) {
        return theAttributes.get(cliCommandParam);
    }

    public static boolean isPasswordParam(final String cliCommandParam) {
        return cliCommandParam.toLowerCase().contains(passWord);
    }

}
