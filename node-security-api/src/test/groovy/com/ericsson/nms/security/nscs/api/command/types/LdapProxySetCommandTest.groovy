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

class LdapProxySetCommandTest extends CdiSpecification {

    def 'set admin status for xmlfile command'() {
        given:
        LdapProxySetCommand command = new LdapProxySetCommand()
        command.setProperties(["admin-status": "DISABLED", "xmlfile": "update.xml"])
        expect:
        command.getAdminStatus() == "DISABLED"
        and:
        command.getXmlFile() == "update.xml"
        and:
        command.isForce() == false
    }

    def 'forced set admin status for xmlfile command'() {
        given:
        LdapProxySetCommand command = new LdapProxySetCommand()
        command.setProperties(["admin-status": "DISABLED", "xmlfile": "update.xml", "force": null])
        expect:
        command.getAdminStatus() == "DISABLED"
        and:
        command.getXmlFile() == "update.xml"
        and:
        command.isForce() == true
    }
}
