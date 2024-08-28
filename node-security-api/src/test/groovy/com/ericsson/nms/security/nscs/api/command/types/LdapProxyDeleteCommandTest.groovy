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

class LdapProxyDeleteCommandTest extends CdiSpecification {

    def 'delete xmlfile'() {
        given:
        def LdapProxyDeleteCommand command = new LdapProxyDeleteCommand()
        command.setProperties(["xmlfile": "delete.xml"])
        expect:
        command != null
        and:
        command.getXmlFile() == "delete.xml"
        and:
        command.isForce() == false
    }

    def 'forced delete xmlfile'() {
        given:
        def LdapProxyDeleteCommand command = new LdapProxyDeleteCommand()
        command.setProperties(["xmlfile": "delete.xml", "force" : null])
        expect:
        command != null
        and:
        command.getXmlFile() == "delete.xml"
        and:
        command.isForce() == true
    }
}
