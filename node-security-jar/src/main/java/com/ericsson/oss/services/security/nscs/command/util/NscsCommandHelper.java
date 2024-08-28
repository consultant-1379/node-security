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
 * Auxiliary utility class containing helper methods to manage commands.
 * 
 * It shall not be instantiated hence a private constructor is defined to hide the implicit public one.
 */
public class NscsCommandHelper {

    private static final String CMD_CREDS_CREATE = "creds create";
    private static final String CMD_CREDS_UPDATE = "creds update";
    private static final String CMD_CREDENTIALS_CREATE = "credentials create";
    private static final String CMD_CREDENTIALS_UPDATE = "credentials update";
    private static final String CMD_SNMP_AUTHNOPRIV = "snmp authnopriv";
    private static final String CMD_SNMP_AUTHPRIV = "snmp authpriv";
    private static final String CMD_KEY_BEGIN = " -";
    private static final Integer CMD_NO_CHARACTERS = 7;
    private static final String[] keyCredentials = { "--rootusername", "-rn", "--rootuserpassword", "-rp", "--secureusername", "-sn",
            "--secureuserpassword",
            "-sp", "--normalusername", "-nn", "--normaluserpassword", "-np", "--nwieasecureusername", "-nasn", "--nwieasecureuserpassword", "-nasp",
            "--nwiebsecureusername", "-nbsn", "--nwiebsecureuserpassword", "-nbsp", "--nodecliusername", "-ncn", "--nodecliuserpassword", "-ncp" };
    private static final String[] keyauthPriv = { "--auth_password", "-ap", "--priv_password", "-pp" };

    private NscsCommandHelper() {
        throw new IllegalStateException("Utility class shall not be instantiated.");
    }

    /**
     * Return a string containing the given command text with obfuscated passwords and keys.
     * 
     * @param commandText
     *            the original command text.
     * @return the command text with obfuscated passwords and keys.
     */
    public static String obfuscateCommandText(final String commandText) {
        if (commandText == null) {
            return null;
        }
        final StringBuilder builder = new StringBuilder(commandText);
        if (commandText.contains(CMD_CREDENTIALS_CREATE) || commandText.contains(CMD_CREDENTIALS_UPDATE) || commandText.contains(CMD_CREDS_CREATE)
                || commandText.contains(CMD_CREDS_UPDATE)) {
            replaceValueWithStars(builder, keyCredentials);
        } else if (commandText.contains(CMD_SNMP_AUTHPRIV) || commandText.contains(CMD_SNMP_AUTHNOPRIV)) {
            replaceValueWithStars(builder, keyauthPriv);
        }
        return builder.toString();
    }

    private static void replaceValueWithStars(final StringBuilder builder, final String[] keyList) {
        for (final String key : keyList) {
            final Integer pos = builder.indexOf(key);
            if (pos != -1) {
                final Integer beginPos = pos + key.length() + 1;
                if (builder.length() - beginPos >= 0) {
                    final Integer endPos = builder.substring(beginPos).indexOf(CMD_KEY_BEGIN);
                    if (endPos != -1) {
                        final String newValue = new String(new char[CMD_NO_CHARACTERS]).replace('\0', '*');
                        builder.replace(beginPos, beginPos + endPos, newValue);
                    }
                }
            }
        }
    }

}
