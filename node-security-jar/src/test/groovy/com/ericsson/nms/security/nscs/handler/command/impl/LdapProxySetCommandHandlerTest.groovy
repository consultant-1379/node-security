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
package com.ericsson.nms.security.nscs.handler.command.impl

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse
import com.ericsson.nms.security.nscs.api.command.NscsConfirmationCommandResponse
import com.ericsson.nms.security.nscs.api.command.NscsMessageCommandResponse
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse
import com.ericsson.nms.security.nscs.api.command.types.LdapProxySetCommand
import com.ericsson.nms.security.nscs.api.exception.NscsLdapProxyException
import com.ericsson.nms.security.nscs.handler.CommandContext
import com.ericsson.nms.security.nscs.handler.command.utility.LdapProxyResponseBuilder
import com.ericsson.nms.security.nscs.ldap.control.IdentityManagementProxy
import com.ericsson.nms.security.nscs.logger.NscsLogger
import com.ericsson.oss.itpf.security.identitymgmtservices.enums.ProxyAgentAccountAdminStatus
import com.ericsson.oss.services.security.nscs.ldap.proxy.NscsLdapProxyHelper
import com.ericsson.oss.services.security.nscs.ldap.proxy.dto.NscsProxyAccount

import spock.lang.Unroll

class LdapProxySetCommandHandlerTest extends CdiSpecification {

    @ObjectUnderTest
    private LdapProxySetCommandHandler commandHandler

    @MockedImplementation
    private NscsLogger nscsLogger

    @MockedImplementation
    private CommandContext commandContext

    @MockedImplementation
    private IdentityManagementProxy identityManagementProxy;

    @MockedImplementation
    private NscsLdapProxyHelper nscsLdapProxyHelper;

    @MockedImplementation
    private LdapProxyResponseBuilder ldapProxyResponseBuilder;

    private NscsProxyAccount nscsProxyAccount1 = mock(NscsProxyAccount.class)
    private NscsProxyAccount nscsProxyAccount2 = mock(NscsProxyAccount.class)
    private NscsConfirmationCommandResponse confirmationResponse = new NscsConfirmationCommandResponse("this is the confirmation message")
    private NscsMessageCommandResponse successResponse = NscsCommandResponse.message("success")
    private NscsNameMultipleValueCommandResponse failedResponse = new NscsNameMultipleValueCommandResponse(2)

    def "object under test injection" () {
        expect:
        commandHandler != null
    }

    @Unroll
    def 'unforced ldap proxy set admin status #adminstatus with xmlfile' () {
        given:
        LdapProxySetCommand command = new LdapProxySetCommand()
        command.setProperties(["admin-status": admin, "xmlfile": "update.xml"])
        and:
        nscsLdapProxyHelper.toIdmsAdminStatus(_) >> adminstatus
        and:
        ldapProxyResponseBuilder.buildLdapProxySetConfirmationResponse() >> confirmationResponse
        when:
        NscsCommandResponse response = commandHandler.process(command, commandContext)
        then:
        notThrown(Exception.class)
        and:
        response != null
        and:
        response.isConfirmationResponseType() == true
        where:
        admin << ["DISABLED", "ENABLED"]
        adminstatus << [
            ProxyAgentAccountAdminStatus.DISABLED,
            ProxyAgentAccountAdminStatus.ENABLED
        ]
    }

    @Unroll
    def 'successful forced ldap proxy set admin status #adminstatus with xmlfile' () {
        given:
        LdapProxySetCommand command = new LdapProxySetCommand()
        command.setProperties(["admin-status": admin, "xmlfile": "update.xml", "force": null])
        and:
        nscsLdapProxyHelper.getNscsProxyAccountsFromCommand(_) >> [
            nscsProxyAccount1,
            nscsProxyAccount2
        ]
        and:
        nscsLdapProxyHelper.toIdmsAdminStatus(_) >> adminstatus
        and:
        nscsLdapProxyHelper.toIdmsProxyAccountDN(_) >> "proxy"
        identityManagementProxy.updateProxyAccountAdminStatus(_, _) >> true
        and:
        ldapProxyResponseBuilder.buildLdapProxySetSuccessResponse(_) >> successResponse
        when:
        NscsCommandResponse response = commandHandler.process(command, commandContext)
        then:
        notThrown(Exception.class)
        and:
        response != null
        and:
        response.isMessageResponseType() == true
        where:
        admin << ["DISABLED", "ENABLED"]
        adminstatus << [
            ProxyAgentAccountAdminStatus.DISABLED,
            ProxyAgentAccountAdminStatus.ENABLED
        ]
    }

    @Unroll
    def 'all failed forced ldap proxy set admin status #adminstatus with xmlfile' () {
        given:
        LdapProxySetCommand command = new LdapProxySetCommand()
        command.setProperties(["admin-status": admin, "xmlfile": "update.xml", "force": null])
        and:
        nscsProxyAccount1.getDn() >> "proxy-1"
        nscsProxyAccount2.getDn() >> "proxy-2"
        nscsLdapProxyHelper.getNscsProxyAccountsFromCommand(_) >> [
            nscsProxyAccount1,
            nscsProxyAccount2
        ]
        and:
        nscsLdapProxyHelper.toIdmsAdminStatus(_) >> adminstatus
        and:
        nscsLdapProxyHelper.toIdmsProxyAccountDN("proxy-1") >> "proxy-1"
        nscsLdapProxyHelper.toIdmsProxyAccountDN("proxy-2") >> "proxy-2"
        identityManagementProxy.updateProxyAccountAdminStatus("proxy-1", adminstatus) >> false
        identityManagementProxy.updateProxyAccountAdminStatus("proxy-2", adminstatus) >> {
            throw new NscsLdapProxyException("exception")
        }
        and:
        ldapProxyResponseBuilder.buildLdapProxySetErrorResponse(_, _, _) >> failedResponse
        when:
        NscsCommandResponse response = commandHandler.process(command, commandContext)
        then:
        notThrown(Exception.class)
        and:
        response != null
        and:
        response.isNameMultipleValueResponseType() == true
        where:
        admin << ["DISABLED", "ENABLED"]
        adminstatus << [
            ProxyAgentAccountAdminStatus.DISABLED,
            ProxyAgentAccountAdminStatus.ENABLED
        ]
    }

    @Unroll
    def 'partially successful forced ldap proxy set admin status #adminstatus with xmlfile' () {
        given:
        LdapProxySetCommand command = new LdapProxySetCommand()
        command.setProperties(["admin-status": admin, "xmlfile": "update.xml", "force": null])
        and:
        nscsProxyAccount1.getDn() >> "proxy-1"
        nscsProxyAccount2.getDn() >> "proxy-2"
        nscsLdapProxyHelper.getNscsProxyAccountsFromCommand(_) >> [
            nscsProxyAccount1,
            nscsProxyAccount2
        ]
        and:
        nscsLdapProxyHelper.toIdmsAdminStatus(_) >> adminstatus
        and:
        nscsLdapProxyHelper.toIdmsProxyAccountDN("proxy-1") >> "proxy-1"
        nscsLdapProxyHelper.toIdmsProxyAccountDN("proxy-2") >> "proxy-2"
        identityManagementProxy.updateProxyAccountAdminStatus("proxy-1", adminstatus) >> true
        identityManagementProxy.updateProxyAccountAdminStatus("proxy-2", adminstatus) >> {
            throw new NscsLdapProxyException("exception")
        }
        and:
        ldapProxyResponseBuilder.buildLdapProxySetErrorResponse(_, _, _) >> failedResponse
        when:
        NscsCommandResponse response = commandHandler.process(command, commandContext)
        then:
        notThrown(Exception.class)
        and:
        response != null
        and:
        response.isNameMultipleValueResponseType() == true
        where:
        admin << ["DISABLED", "ENABLED"]
        adminstatus << [
            ProxyAgentAccountAdminStatus.DISABLED,
            ProxyAgentAccountAdminStatus.ENABLED
        ]
    }

    @Unroll
    def 'partially successful forced ldap proxy set admin status #adminstatus with xmlfile due to invalid DN' () {
        given:
        LdapProxySetCommand command = new LdapProxySetCommand()
        command.setProperties(["admin-status": admin, "xmlfile": "update.xml", "force": null])
        and:
        nscsProxyAccount1.getDn() >> "proxy-1"
        nscsProxyAccount2.getDn() >> "proxy-2"
        nscsLdapProxyHelper.getNscsProxyAccountsFromCommand(_) >> [
            nscsProxyAccount1,
            nscsProxyAccount2
        ]
        and:
        nscsLdapProxyHelper.toIdmsAdminStatus(_) >> adminstatus
        and:
        nscsLdapProxyHelper.toIdmsProxyAccountDN("proxy-1") >> "proxy-1"
        nscsLdapProxyHelper.toIdmsProxyAccountDN("proxy-2") >> {
            throw new NscsLdapProxyException("invalid proxy")
        }
        identityManagementProxy.updateProxyAccountAdminStatus("proxy-1", adminstatus) >> true
        and:
        ldapProxyResponseBuilder.buildLdapProxySetErrorResponse(_, _, _) >> failedResponse
        when:
        NscsCommandResponse response = commandHandler.process(command, commandContext)
        then:
        notThrown(Exception.class)
        and:
        response != null
        and:
        response.isNameMultipleValueResponseType() == true
        where:
        admin << ["DISABLED", "ENABLED"]
        adminstatus << [
            ProxyAgentAccountAdminStatus.DISABLED,
            ProxyAgentAccountAdminStatus.ENABLED
        ]
    }

    def 'failed ldap proxy set admin status with xmlfile due to invalid XML file' () {
        given:
        LdapProxySetCommand command = new LdapProxySetCommand()
        command.setProperties(["admin-status": "DISABLED", "xmlfile": "update.xml"])
        and:
        nscsLdapProxyHelper.getNscsProxyAccountsFromCommand(_) >> {
            throw new NscsLdapProxyException("invalid XML")
        }
        when:
        NscsCommandResponse response = commandHandler.process(command, commandContext)
        then:
        thrown(NscsLdapProxyException.class)
    }

    def 'failed ldap proxy set admin status with xmlfile due to invalid admin status' () {
        given:
        LdapProxySetCommand command = new LdapProxySetCommand()
        command.setProperties(["admin-status": "INVALID", "xmlfile": "update.xml"])
        and:
        nscsLdapProxyHelper.toIdmsAdminStatus(_) >> {
            throw new NscsLdapProxyException("invalid admin status")
        }
        when:
        NscsCommandResponse response = commandHandler.process(command, commandContext)
        then:
        thrown(NscsLdapProxyException.class)
    }

    def 'failed forced ldap proxy set admin status with xmlfile due to unexpected exception' () {
        given:
        LdapProxySetCommand command = new LdapProxySetCommand()
        command.setProperties(["admin-status": "INVALID", "xmlfile": "update.xml", "force": null])
        and:
        nscsLdapProxyHelper.toIdmsAdminStatus(_) >> { throw new Exception("unexpected exception") }
        when:
        NscsCommandResponse response = commandHandler.process(command, commandContext)
        then:
        thrown(NscsLdapProxyException.class)
    }
}
