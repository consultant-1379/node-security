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
package com.ericsson.nms.security.nscs.api.command.types;

import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;

public class LdapProxyGetCommand extends NscsPropertyCommand {

    private static final long serialVersionUID = 8148534851974753869L;

    private static final String INACTIVITY_SECONDS_PROPERTY = "inactivity-seconds";
    private static final String INACTIVITY_HOURS_PROPERTY = "inactivity-hours";
    private static final String INACTIVITY_DAYS_PROPERTY = "inactivity-days";
    private static final String ADMIN_STATUS_PROPERTY = "admin-status";
    private static final String PROXY_LIST_PROPERTY = "proxylist";
    private static final String ALL_PROXIES_VALUE = "*";
    private static final String SUMMARY_PROPERTY = "summary";
    private static final String LEGACY_PROPERTY = "legacy";
    private static final String COUNT_PROPERTY = "count";

    /**
     * This method will return the wanted inactivity period in seconds or null if ALL or if property is missing.
     *
     * @return the inactivity period in seconds or null if ALL or missing
     */
    public String getInactivitySeconds() {
        if (!isAllProxies() && hasProperty(INACTIVITY_SECONDS_PROPERTY)) {
            return (String) getValue(INACTIVITY_SECONDS_PROPERTY);
        }
        return null;
    }

    /**
     * This method will return the wanted inactivity period in hours or null if ALL or if property is missing.
     *
     * @return the inactivity period in hours or null if ALL or missing
     */
    public String getInactivityHours() {
        if (!isAllProxies() && hasProperty(INACTIVITY_HOURS_PROPERTY)) {
            return (String) getValue(INACTIVITY_HOURS_PROPERTY);
        }
        return null;
    }

    /**
     * This method will return the wanted inactivity period in days or null if ALL or if property is missing.
     *
     * @return the inactivity period in days or null if ALL or missing
     */
    public String getInactivityDays() {
        if (!isAllProxies() && hasProperty(INACTIVITY_DAYS_PROPERTY)) {
            return (String) getValue(INACTIVITY_DAYS_PROPERTY);
        }
        return null;
    }

    /**
     * This method will return the wanted administrative status or null if ALL or if property is missing.
     *
     * @return the administrative status or null if ALL or missing
     */
    public String getAdminStatus() {
        if (!isAllProxies() && hasProperty(ADMIN_STATUS_PROPERTY)) {
            return (String) getValue(ADMIN_STATUS_PROPERTY);
        }
        return null;
    }

    /**
     * Returns if get all proxies is requested.
     * 
     * @return true if get all proxies is requested
     */
    public boolean isAllProxies() {
        return ALL_PROXIES_VALUE.equals(getValue(PROXY_LIST_PROPERTY));
    }

    /**
     * Returns if summary is requested.
     * 
     * If specified only global counters of requested proxy accounts shall be returned.
     * 
     * @return true if summary is requested
     */
    public boolean isSummary() {
        return hasProperty(SUMMARY_PROPERTY);
    }

    /**
     * Returns if legacy is requested.
     * 
     * If specified only requested proxy accounts on legacy branch only shall be returned.
     * 
     * @return true if legacy is requested
     */
    public boolean isLegacy() {
        return hasProperty(LEGACY_PROPERTY);
    }

    /**
     * This method will return the wanted count or null if property is missing.
     *
     * @return the count or null if missing
     */
    public String getCount() {
        return getValueString(COUNT_PROPERTY);
    }

}
