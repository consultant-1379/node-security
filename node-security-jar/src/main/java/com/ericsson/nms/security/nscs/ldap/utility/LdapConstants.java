/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.ldap.utility;

public class LdapConstants {

    // Following constant might get replaced with PIB configurable parameters. Need wider discussion about merits of having them as PIB configurables.
    public static final Integer LDAP_STARTTLS_PORT = 1389;
    public static final Integer LDAP_LDAPS_PORT = 1636;
    public static final String COM_INF_LDAP_ROOT_SUFFIX = "COM_INF_LDAP_ROOT_SUFFIX";
    public static final String PRIMARY_LDAP_IP_ADDRESS = "sec1";
    public static final String SECONDARY_LDAP_IP_ADDRESS = "sec2";
    public static final String CLEAR_TEXT = "cleartext";
    public static final String PASSWORD = "password";
    public static final String CONFIGURATION_JAVA_PROPERTIES = "configuration.java.properties";
    public static final String GLOBAL_PROPERTIES_PATH = "/ericsson/tor/data/global.properties";
    public static final Object STARTTLS = "STARTTLS";
    public static final Object LDAPS = "LDAPS";
    public static final String MANUAL = "manual";
    public static final String LDAP_ID = "ldapId";
    public static final String USE_TLS = "useTls";
    public static final String TLS_MODE = "tlsMode";
    public static final String USER_LABEL = "userLabel";
    public static final String TLS_PORT = "tlsPort";
    public static final String LDAPS_PORT = "ldapsPort";
    public static final String LDAP_IPV4_ADDRESS = "ldapIpv4Address";
    public static final String FALLBACK_LDAP_IPV4_ADDRESS = "fallbackLdapIpv4Address";
    public static final String LDAP_IPV6_ADDRESS = "ldapIpv6Address";
    public static final String FALLBACK_LDAP_IPV6_ADDRESS = "fallbackLdapIpv6Address";
    public static final String BIND_DN = "bindDn";
    public static final String BIND_PASSWORD = "bindPassword";
    public static final String BASE_DN = "baseDn";
    public static final String VALUE = "VALUE";
    public static final String PROPERTY = "PROPERTY";
    public static final String COMECIM_LDAP_MO = "Ldap";
    public static final String VDU_LDAP_MO = "ldap";
    public static final String COMECIM_DEFAULT_OTP_VALIDITY_PERIOD_IN_MINUTES = "30";
    public static final String VDU_DEFAULT_OTP_VALIDITY_PERIOD_IN_MINUTES = "60";
    public static final String VDU_DEFAULT_ENROLL_CA_TRUST_CATEGORY_ID = "oamCmpCaTrustCategory";
    public static final String LDAP_RENEW_WARNING_RENEW_LDAP_CONFIGURATION_CONFIRMATION = "Are you sure you want to renew the LDAP configuration on the specified node(s)?";
    public static final String LDAP_RENEW_WARNING_PLEASE_CHECK_ONLINE_HELP = "Please check online help for more details about command consequences.";
}
