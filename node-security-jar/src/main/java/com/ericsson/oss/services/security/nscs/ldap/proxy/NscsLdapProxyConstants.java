/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2022
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.ldap.proxy;

/**
 * Auxiliary class containing constants for ldap proxy management.
 */
public final class NscsLdapProxyConstants {

    // to restrict instantiation
    private NscsLdapProxyConstants() {
    }

    // ldap proxy
    public static final String LDAP_PROXY_DN_PARAM = "DN";
    public static final String LDAP_PROXY_ACCOUNT_DN_REGEX = "cn=ProxyAccount_[^,]+,ou=proxyagent[^,]*,.+";
    public static final String LDAP_PROXY_GET_ALLOWED_DN_VALUES = "any valid proxy account DN.";
    public static final String LDAP_PROXY_ADMIN_STATUS_PARAM = "admin status";
    public static final String LDAP_PROXY_ADMIN_STATUS_ENABLED = "ENABLED";
    public static final String LDAP_PROXY_ADMIN_STATUS_DISABLED = "DISABLED";
    public static final String LDAP_PROXY_SCHEMA_XSD = "LdapProxySchema.xsd";
    public static final String LDAP_PROXY_JAXB_EXCEPTION_UNMARSHALING_CLASS_FORMAT = "JAXBException occurred while unmarshaling object of class %s";
    public static final String LDAP_PROXY_SAX_EXCEPTION_CREATING_SCHEMA_FROM_FILE_FORMAT = "SAXException occurred while creating schema from file %s";
    public static final String LDAP_PROXY_NO_PROXY_ACCOUNT_ELEMENTS_IN_XML_FILE = "No proxy account elements in XML file";
    public static final String LDAP_PROXY_NULL_OR_EMPTY_XML_FILE_CONTENT = "null or empty XML file content";
    public static final String LDAP_PROXY_WARNING_PLEASE_CHECK_ONLINE_HELP = "Please check online help for more details about command consequences.";

    // ldap proxy get
    public static final String LDAP_PROXY_GET_SUCCESS = "Successfully generated proxy account(s) file.";
    public static final String LDAP_PROXY_GET_FAILURE = "Failed generation of proxy account(s) file.";
    public static final String LDAP_PROXY_GET_FILENAME_SIZE_FORMAT = "file %s (size = %s bytes)";
    public static final String LDAP_PROXY_GET_SUCCESSFULLY_GENERATED_FILE_FORMAT = "Successfully generated %s";
    public static final String LDAP_PROXY_GET_IO_EXCEPTION_FILE_FORMAT = "IOException occurred while creating file identifier for %s";
    public static final String LDAP_PROXY_GET_JAXB_EXCEPTION_MARSHALING_CLASS_FORMAT = "JAXBException occurred while marshaling object of class %s";
    public static final String LDAP_PROXY_GET_BASE_FILENAME = "ldap_proxy_get_";
    public static final String LDAP_PROXY_GET_ALL = "all";
    public static final String LDAP_PROXY_GET_SUMMARY = "_summary";
    public static final String LDAP_PROXY_GET_LEGACY = "_legacy";
    public static final String LDAP_PROXY_GET_INACTIVITY_PERIOD_PARAM = "inactivity period";
    public static final String LDAP_PROXY_GET_INACTIVITY_DAYS = "days";
    public static final String LDAP_PROXY_GET_INACTIVITY_HOURS = "hours";
    public static final String LDAP_PROXY_GET_INACTIVITY_SECONDS = "seconds";
    public static final String LDAP_PROXY_GET_INACTIVITY_FORMAT = "inactive_for_%s_%s";
    public static final String LDAP_PROXY_GET_COUNT_PARAM = "count";
    public static final String LDAP_PROXY_GET_EXCEPTION_READING_PROXY_ACCOUNTS_BY_PARAM_FORMAT = "Exception occurred reading proxy accounts by %s [%s] with isLegacy %s and isSummary %s";
    public static final String LDAP_PROXY_GET_EXCEPTION_READING_ALL_PROXY_ACCOUNTS_FORMAT = "Exception occurred reading all proxy accounts with isLegacy %s and isSummary %s";
    public static final String LDAP_PROXY_GET_EXCEPTION_READING_PROXY_ACCOUNT_BY_DN_FORMAT = "Exception occurred reading proxy account by DN [%s]";
    public static final String LDAP_PROXY_GET_INVALID_PARAMETER_VALUE_FORMAT = "invalid %s [%s]";
    public static final String LDAP_PROXY_GET_ALLOWED_VALUES_FORMAT = "allowed values are %s";
    public static final String LDAP_PROXY_GET_ALLOWED_ADMIN_STATUS_VALUES = String.format("[%s, %s]", LDAP_PROXY_ADMIN_STATUS_DISABLED,
            LDAP_PROXY_ADMIN_STATUS_ENABLED);
    public static final String LDAP_PROXY_GET_ALLOWED_INACTIVITY_PERIOD_VALUES = "any integer greater than zero";
    public static final String LDAP_PROXY_GET_ALLOWED_COUNT_VALUES = "any integer greater than or equal to zero";
    public static final Long LDAP_PROXY_GET_MILLIS_IN_SECOND = 1000L;
    public static final Long LDAP_PROXY_GET_MILLIS_IN_HOUR = 60 * 60 * LDAP_PROXY_GET_MILLIS_IN_SECOND;
    public static final Long LDAP_PROXY_GET_MILLIS_IN_DAY = 24 * LDAP_PROXY_GET_MILLIS_IN_HOUR;
    public static final String LDAP_PROXY_GET_NEVER = "NEVER";
    public static final String LDAP_PROXY_GET_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String LDAP_PROXY_GET_NO_VALID_FILTER_OPTIONS = "no valid filter option specified.";

    // ldap proxy set
    public static final String LDAP_PROXY_SET_SUCCESS = "Successfully updated proxy account(s).";
    public static final String LDAP_PROXY_SET_FAILURE = "Failed update of proxy account(s).";
    public static final String LDAP_PROXY_SET_ALL_SUCCESS_FORMAT = "Successfully updated all %s proxy accounts.";
    public static final String LDAP_PROXY_SET_PARTIAL_SUCCESS_FORMAT = "Successfully updated %s of %s proxy accounts.";
    public static final String LDAP_PROXY_SET_ALL_FAILED_FORMAT = "None of %s proxy accounts successfully updated.";
    public static final String LDAP_PROXY_SET_EXCEPTION_UPDATING_PROXY_ACCOUNT_DN_PARAM_VALUE_FORMAT = "Exception occurred updating proxy account %s %s to [%s]";
    public static final String LDAP_PROXY_SET_WARNING_UPDATE_PROXY_ACCOUNT_ADMIN_STATUS_CONFIRMATION = "Are you sure you want to update the administrative status of the specified proxy account(s)?";

    // ldap proxy delete
    public static final String LDAP_PROXY_DELETE_SUCCESS = "Successfully deleted proxy account(s).";
    public static final String LDAP_PROXY_DELETE_FAILURE = "Failed delete of proxy account(s).";
    public static final String LDAP_PROXY_DELETE_ALL_SUCCESS_FORMAT = "Successfully deleted all %s proxy accounts.";
    public static final String LDAP_PROXY_DELETE_PARTIAL_SUCCESS_FORMAT = "Successfully deleted %s of %s proxy accounts.";
    public static final String LDAP_PROXY_DELETE_ALL_FAILED_FORMAT = "None of %s proxy accounts successfully deleted.";
    public static final String LDAP_PROXY_DELETE_WARNING_DELETE_PROXY_ACCOUNT_CONFIRMATION = "Are you sure you want to delete the specified proxy account(s)?";

}
