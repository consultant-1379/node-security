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
package com.ericsson.nms.security.nscs.api.command.types

import com.ericsson.cds.cdi.support.spock.CdiSpecification

class LdapProxyGetCommandTest extends CdiSpecification {

    def 'get all proxies command'() {
        given:
        LdapProxyGetCommand command = new LdapProxyGetCommand()
        command.setProperties(["proxylist": "*"])
        expect:
        command.isAllProxies() == true
        and:
        command.getInactivitySeconds() == null
        and:
        command.getInactivityHours() == null
        and:
        command.getInactivityDays() == null
        and:
        command.getAdminStatus() == null
        and:
        command.isSummary() == false
        and:
        command.isLegacy() == false
        and:
        command.getCount() == null
    }

    def 'get all proxies summary command'() {
        given:
        LdapProxyGetCommand command = new LdapProxyGetCommand()
        command.setProperties(["proxylist": "*", "summary": ""])
        expect:
        command.isAllProxies() == true
        and:
        command.getInactivitySeconds() == null
        and:
        command.getInactivityHours() == null
        and:
        command.getInactivityDays() == null
        and:
        command.getAdminStatus() == null
        and:
        command.isSummary() == true
        and:
        command.isLegacy() == false
        and:
        command.getCount() == null
    }

    def 'get all legacy proxies command'() {
        given:
        LdapProxyGetCommand command = new LdapProxyGetCommand()
        command.setProperties(["proxylist": "*", "legacy": ""])
        expect:
        command.isAllProxies() == true
        and:
        command.getInactivitySeconds() == null
        and:
        command.getInactivityHours() == null
        and:
        command.getInactivityDays() == null
        and:
        command.getAdminStatus() == null
        and:
        command.isSummary() == false
        and:
        command.isLegacy() == true
        and:
        command.getCount() == null
    }

    def 'get all legacy proxies summary command'() {
        given:
        LdapProxyGetCommand command = new LdapProxyGetCommand()
        command.setProperties(["proxylist": "*", "summary": "", "legacy": ""])
        expect:
        command.isAllProxies() == true
        and:
        command.getInactivitySeconds() == null
        and:
        command.getInactivityHours() == null
        and:
        command.getInactivityDays() == null
        and:
        command.getAdminStatus() == null
        and:
        command.isSummary() == true
        and:
        command.isLegacy() == true
        and:
        command.getCount() == null
    }

    def 'get all proxies by inactivity seconds command'() {
        given:
        LdapProxyGetCommand command = new LdapProxyGetCommand()
        command.setProperties(["proxylist": "*", "inactivity-seconds": "20"])
        expect:
        command.isAllProxies() == true
        and:
        command.getInactivitySeconds() == null
        and:
        command.getInactivityHours() == null
        and:
        command.getInactivityDays() == null
        and:
        command.getAdminStatus() == null
        and:
        command.isSummary() == false
        and:
        command.isLegacy() == false
        and:
        command.getCount() == null
    }

    def 'get proxies by inactivity seconds command'() {
        given:
        LdapProxyGetCommand command = new LdapProxyGetCommand()
        command.setProperties(["inactivity-seconds": "20"])
        expect:
        command.isAllProxies() == false
        and:
        command.getInactivitySeconds() == "20"
        and:
        command.getInactivityHours() == null
        and:
        command.getInactivityDays() == null
        and:
        command.getAdminStatus() == null
        and:
        command.isSummary() == false
        and:
        command.isLegacy() == false
        and:
        command.getCount() == null
    }

    def 'get proxies by inactivity seconds summary command'() {
        given:
        LdapProxyGetCommand command = new LdapProxyGetCommand()
        command.setProperties(["inactivity-seconds": "20", "summary": ""])
        expect:
        command.isAllProxies() == false
        and:
        command.getInactivitySeconds() == "20"
        and:
        command.getInactivityHours() == null
        and:
        command.getInactivityDays() == null
        and:
        command.getAdminStatus() == null
        and:
        command.isSummary() == true
        and:
        command.isLegacy() == false
        and:
        command.getCount() == null
    }

    def 'get proxies by inactivity seconds legacy command'() {
        given:
        LdapProxyGetCommand command = new LdapProxyGetCommand()
        command.setProperties(["inactivity-seconds": "20", "legacy": ""])
        expect:
        command.isAllProxies() == false
        and:
        command.getInactivitySeconds() == "20"
        and:
        command.getInactivityHours() == null
        and:
        command.getInactivityDays() == null
        and:
        command.getAdminStatus() == null
        and:
        command.isSummary() == false
        and:
        command.isLegacy() == true
        and:
        command.getCount() == null
    }

    def 'get all proxies by inactivity hours command'() {
        given:
        LdapProxyGetCommand command = new LdapProxyGetCommand()
        command.setProperties(["proxylist": "*", "inactivity-hours": "2"])
        expect:
        command.isAllProxies() == true
        and:
        command.getInactivitySeconds() == null
        and:
        command.getInactivityHours() == null
        and:
        command.getInactivityDays() == null
        and:
        command.getAdminStatus() == null
        and:
        command.isSummary() == false
        and:
        command.isLegacy() == false
        and:
        command.getCount() == null
    }

    def 'get proxies by inactivity hours command'() {
        given:
        LdapProxyGetCommand command = new LdapProxyGetCommand()
        command.setProperties(["inactivity-hours": "2"])
        expect:
        command.isAllProxies() == false
        and:
        command.getInactivitySeconds() == null
        and:
        command.getInactivityHours() == "2"
        and:
        command.getInactivityDays() == null
        and:
        command.getAdminStatus() == null
        and:
        command.isSummary() == false
        and:
        command.isLegacy() == false
        and:
        command.getCount() == null
    }

    def 'get proxies by inactivity hours summary command'() {
        given:
        LdapProxyGetCommand command = new LdapProxyGetCommand()
        command.setProperties(["inactivity-hours": "2", "summary": ""])
        expect:
        command.isAllProxies() == false
        and:
        command.getInactivitySeconds() == null
        and:
        command.getInactivityHours() == "2"
        and:
        command.getInactivityDays() == null
        and:
        command.getAdminStatus() == null
        and:
        command.isSummary() == true
        and:
        command.isLegacy() == false
        and:
        command.getCount() == null
    }

    def 'get proxies by inactivity hours legacy command'() {
        given:
        LdapProxyGetCommand command = new LdapProxyGetCommand()
        command.setProperties(["inactivity-hours": "2", "legacy": ""])
        expect:
        command.isAllProxies() == false
        and:
        command.getInactivitySeconds() == null
        and:
        command.getInactivityHours() == "2"
        and:
        command.getInactivityDays() == null
        and:
        command.getAdminStatus() == null
        and:
        command.isSummary() == false
        and:
        command.isLegacy() == true
        and:
        command.getCount() == null
    }

    def 'get all proxies by inactivity days command'() {
        given:
        LdapProxyGetCommand command = new LdapProxyGetCommand()
        command.setProperties(["proxylist": "*", "inactivity-days": "7"])
        expect:
        command.isAllProxies() == true
        and:
        command.getInactivitySeconds() == null
        and:
        command.getInactivityHours() == null
        and:
        command.getInactivityDays() == null
        and:
        command.getAdminStatus() == null
        and:
        command.isSummary() == false
        and:
        command.isLegacy() == false
        and:
        command.getCount() == null
    }

    def 'get proxies by inactivity days command'() {
        given:
        LdapProxyGetCommand command = new LdapProxyGetCommand()
        command.setProperties(["inactivity-days": "7"])
        expect:
        command.isAllProxies() == false
        and:
        command.getInactivitySeconds() == null
        and:
        command.getInactivityHours() == null
        and:
        command.getInactivityDays() == "7"
        and:
        command.getAdminStatus() == null
        and:
        command.isSummary() == false
        and:
        command.isLegacy() == false
        and:
        command.getCount() == null
    }

    def 'get proxies by inactivity days summary command'() {
        given:
        LdapProxyGetCommand command = new LdapProxyGetCommand()
        command.setProperties(["inactivity-days": "7", "summary": ""])
        expect:
        command.isAllProxies() == false
        and:
        command.getInactivitySeconds() == null
        and:
        command.getInactivityHours() == null
        and:
        command.getInactivityDays() == "7"
        and:
        command.getAdminStatus() == null
        and:
        command.isSummary() == true
        and:
        command.isLegacy() == false
        and:
        command.getCount() == null
    }

    def 'get proxies by inactivity days legacy command'() {
        given:
        LdapProxyGetCommand command = new LdapProxyGetCommand()
        command.setProperties(["inactivity-days": "7", "legacy": ""])
        expect:
        command.isAllProxies() == false
        and:
        command.getInactivitySeconds() == null
        and:
        command.getInactivityHours() == null
        and:
        command.getInactivityDays() == "7"
        and:
        command.getAdminStatus() == null
        and:
        command.isSummary() == false
        and:
        command.isLegacy() == true
        and:
        command.getCount() == null
    }

    def 'get all proxies by admin status command'() {
        given:
        LdapProxyGetCommand command = new LdapProxyGetCommand()
        command.setProperties(["proxylist": "*", "admin-status": "DISABLED"])
        expect:
        command.isAllProxies() == true
        and:
        command.getInactivitySeconds() == null
        and:
        command.getInactivityHours() == null
        and:
        command.getInactivityDays() == null
        and:
        command.getAdminStatus() == null
        and:
        command.isSummary() == false
        and:
        command.isLegacy() == false
        and:
        command.getCount() == null
    }

    def 'get proxies by admin status command'() {
        given:
        LdapProxyGetCommand command = new LdapProxyGetCommand()
        command.setProperties(["admin-status": "DISABLED"])
        expect:
        command.isAllProxies() == false
        and:
        command.getInactivitySeconds() == null
        and:
        command.getInactivityHours() == null
        and:
        command.getInactivityDays() == null
        and:
        command.getAdminStatus() == "DISABLED"
        and:
        command.isSummary() == false
        and:
        command.isLegacy() == false
        and:
        command.getCount() == null
    }

    def 'get proxies by admin status summary command'() {
        given:
        LdapProxyGetCommand command = new LdapProxyGetCommand()
        command.setProperties(["admin-status": "DISABLED", "summary": ""])
        expect:
        command.isAllProxies() == false
        and:
        command.getInactivitySeconds() == null
        and:
        command.getInactivityHours() == null
        and:
        command.getInactivityDays() == null
        and:
        command.getAdminStatus() == "DISABLED"
        and:
        command.isSummary() == true
        and:
        command.isLegacy() == false
        and:
        command.getCount() == null
    }

    def 'get proxies by admin status legacy command'() {
        given:
        LdapProxyGetCommand command = new LdapProxyGetCommand()
        command.setProperties(["admin-status": "DISABLED", "legacy": ""])
        expect:
        command.isAllProxies() == false
        and:
        command.getInactivitySeconds() == null
        and:
        command.getInactivityHours() == null
        and:
        command.getInactivityDays() == null
        and:
        command.getAdminStatus() == "DISABLED"
        and:
        command.isSummary() == false
        and:
        command.isLegacy() == true
        and:
        command.getCount() == null
    }

    def 'get all proxies by count command'() {
        given:
        LdapProxyGetCommand command = new LdapProxyGetCommand()
        command.setProperties(["proxylist": "*", "count": "100000"])
        expect:
        command.isAllProxies() == true
        and:
        command.getInactivitySeconds() == null
        and:
        command.getInactivityHours() == null
        and:
        command.getInactivityDays() == null
        and:
        command.getAdminStatus() == null
        and:
        command.isSummary() == false
        and:
        command.isLegacy() == false
        and:
        command.getCount() == "100000"
    }
}
