package com.ericsson.nms.security.nscs.handler.command.impl

import static org.junit.Assert.*

import org.junit.Test

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.command.types.LdapRenewCommand
import com.ericsson.nms.security.nscs.handler.CommandContext

class RenewLdapHandlerTest extends CdiSpecification {

    @ObjectUnderTest
    private RenewLdapHandler renewLdapHandler

    @MockedImplementation
    private LdapCommandHandlerHelper ldapCommandHandlerHelper

    @MockedImplementation
    private CommandContext context

    private LdapRenewCommand command

    def 'object under test'() {
        expect:
        renewLdapHandler != null
    }

    def 'process command'() {
        given:
        command = new LdapRenewCommand()
        when:
        def response = renewLdapHandler.process(command, context)
        then:
        1 * ldapCommandHandlerHelper.processActivate(command, context, LdapCommandHandlerHelper.LDAPConfigurationMode.LDAP_RENEW)
    }
}
