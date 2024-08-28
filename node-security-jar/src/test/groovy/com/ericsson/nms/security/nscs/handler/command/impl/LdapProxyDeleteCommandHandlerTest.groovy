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
import com.ericsson.nms.security.nscs.api.command.types.LdapProxyDeleteCommand
import com.ericsson.nms.security.nscs.api.exception.LdapConfigurationException
import com.ericsson.nms.security.nscs.api.exception.NscsLdapProxyException
import com.ericsson.nms.security.nscs.handler.CommandContext
import com.ericsson.nms.security.nscs.handler.command.utility.LdapProxyResponseBuilder
import com.ericsson.nms.security.nscs.ldap.control.IdentityManagementProxy
import com.ericsson.nms.security.nscs.logger.NscsLogger
import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountDetails
import com.ericsson.oss.itpf.security.identitymgmtservices.enums.ProxyAgentAccountAdminStatus
import com.ericsson.oss.services.security.nscs.ldap.proxy.NscsLdapProxyHelper
import com.ericsson.oss.services.security.nscs.ldap.proxy.dto.NscsProxyAccount

class LdapProxyDeleteCommandHandlerTest extends CdiSpecification {

    @ObjectUnderTest
    private LdapProxyDeleteCommandHandler commandHandler

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

    private ProxyAgentAccountDetails disabledProxyAgentAccountDetails = mock(ProxyAgentAccountDetails.class)
    private ProxyAgentAccountDetails enabledProxyAgentAccountDetails = mock(ProxyAgentAccountDetails.class)
    private NscsProxyAccount nscsProxyAccount1 = mock(NscsProxyAccount.class)
    private NscsProxyAccount nscsProxyAccount2 = mock(NscsProxyAccount.class)
    private NscsProxyAccount nscsProxyAccount3 = mock(NscsProxyAccount.class)
    private NscsConfirmationCommandResponse confirmationResponse = new NscsConfirmationCommandResponse("this is the confirmation message")
    private NscsMessageCommandResponse successResponse = NscsCommandResponse.message("success")
    private NscsNameMultipleValueCommandResponse failedResponse = new NscsNameMultipleValueCommandResponse(4)

    def "object under test injection" () {
        expect:
        commandHandler != null
    }

    def 'unforced ldap proxy delete with xmlfile' () {
        given:
        LdapProxyDeleteCommand command = new LdapProxyDeleteCommand()
        command.setProperties(["xmlfile": "delete.xml"])
        and:
        ldapProxyResponseBuilder.buildLdapProxyDeleteConfirmationResponse() >> confirmationResponse
        when:
        NscsCommandResponse response = commandHandler.process(command, commandContext)
        then:
        notThrown(Exception.class)
        and:
        response != null
        and:
        response.isConfirmationResponseType() == true
    }

    def 'successful forced ldap proxy delete with xmlfile' () {
        given:
        LdapProxyDeleteCommand command = new LdapProxyDeleteCommand()
        command.setProperties(["xmlfile": "delete.xml", "force": null])
        and:
        nscsLdapProxyHelper.getNscsProxyAccountsFromCommand(_) >> [
            nscsProxyAccount1,
            nscsProxyAccount2,
            nscsProxyAccount3
        ]
        and:
        nscsLdapProxyHelper.toIdmsProxyAccountDN(_) >> "proxy"
        disabledProxyAgentAccountDetails.getAdminStatus() >> ProxyAgentAccountAdminStatus.DISABLED
        identityManagementProxy.getProxyAccountDetails(_) >> disabledProxyAgentAccountDetails
        identityManagementProxy.deleteProxyAgentAccount(_) >> true
        and:
        ldapProxyResponseBuilder.buildLdapProxyDeleteSuccessResponse(_) >> successResponse
        when:
        NscsCommandResponse response = commandHandler.process(command, commandContext)
        then:
        notThrown(Exception.class)
        and:
        response != null
        and:
        response.isMessageResponseType() == true
    }

    def 'all failed forced ldap proxy delete with xmlfile' () {
        given:
        LdapProxyDeleteCommand command = new LdapProxyDeleteCommand()
        command.setProperties(["xmlfile": "delete.xml", "force": null])
        and:
        nscsProxyAccount1.getDn() >> "proxy-1"
        nscsProxyAccount2.getDn() >> "proxy-2"
        nscsProxyAccount3.getDn() >> "proxy-3"
        nscsLdapProxyHelper.getNscsProxyAccountsFromCommand(_) >> [
            nscsProxyAccount1,
            nscsProxyAccount2,
            nscsProxyAccount3
        ]
        and:
        nscsLdapProxyHelper.toIdmsProxyAccountDN("proxy-1") >> "proxy-1"
        nscsLdapProxyHelper.toIdmsProxyAccountDN("proxy-2") >> "proxy-2"
        nscsLdapProxyHelper.toIdmsProxyAccountDN("proxy-3") >> "proxy-3"
        disabledProxyAgentAccountDetails.getAdminStatus() >> ProxyAgentAccountAdminStatus.DISABLED
        identityManagementProxy.getProxyAccountDetails("proxy-1") >> disabledProxyAgentAccountDetails
        enabledProxyAgentAccountDetails.getAdminStatus() >> ProxyAgentAccountAdminStatus.ENABLED
        identityManagementProxy.getProxyAccountDetails("proxy-2") >> enabledProxyAgentAccountDetails
        identityManagementProxy.getProxyAccountDetails("proxy-3") >> {
            throw new NscsLdapProxyException("exception")
        }
        identityManagementProxy.deleteProxyAgentAccount("proxy-1") >> {
            throw new LdapConfigurationException("exception")
        }
        and:
        ldapProxyResponseBuilder.buildLdapProxyDeleteErrorResponse(_, _, _) >> failedResponse
        when:
        NscsCommandResponse response = commandHandler.process(command, commandContext)
        then:
        notThrown(Exception.class)
        and:
        response != null
        and:
        response.isNameMultipleValueResponseType() == true
    }

    def 'all failed forced ldap proxy delete with xmlfile and with delete returning false' () {
        given:
        LdapProxyDeleteCommand command = new LdapProxyDeleteCommand()
        command.setProperties(["xmlfile": "delete.xml", "force": null])
        and:
        nscsProxyAccount1.getDn() >> "proxy-1"
        nscsLdapProxyHelper.getNscsProxyAccountsFromCommand(_) >> [
            nscsProxyAccount1
        ]
        and:
        nscsLdapProxyHelper.toIdmsProxyAccountDN("proxy-1") >> "proxy-1"
        disabledProxyAgentAccountDetails.getAdminStatus() >> ProxyAgentAccountAdminStatus.DISABLED
        identityManagementProxy.getProxyAccountDetails("proxy-1") >> disabledProxyAgentAccountDetails
        identityManagementProxy.deleteProxyAgentAccount("proxy-1") >> false
        and:
        ldapProxyResponseBuilder.buildLdapProxyDeleteErrorResponse(_, _, _) >> failedResponse
        when:
        NscsCommandResponse response = commandHandler.process(command, commandContext)
        then:
        notThrown(Exception.class)
        and:
        response != null
        and:
        response.isNameMultipleValueResponseType() == true
    }

    def 'partially successful forced ldap proxy delete with xmlfile' () {
        given:
        LdapProxyDeleteCommand command = new LdapProxyDeleteCommand()
        command.setProperties(["xmlfile": "delete.xml", "force": null])
        and:
        nscsProxyAccount1.getDn() >> "proxy-1"
        nscsProxyAccount2.getDn() >> "proxy-2"
        nscsProxyAccount3.getDn() >> "proxy-3"
        nscsLdapProxyHelper.getNscsProxyAccountsFromCommand(_) >> [
            nscsProxyAccount1,
            nscsProxyAccount2,
            nscsProxyAccount3
        ]
        and:
        nscsLdapProxyHelper.toIdmsProxyAccountDN("proxy-1") >> "proxy-1"
        nscsLdapProxyHelper.toIdmsProxyAccountDN("proxy-2") >> "proxy-2"
        nscsLdapProxyHelper.toIdmsProxyAccountDN("proxy-3") >> "proxy-3"
        disabledProxyAgentAccountDetails.getAdminStatus() >> ProxyAgentAccountAdminStatus.DISABLED
        identityManagementProxy.getProxyAccountDetails("proxy-1") >> disabledProxyAgentAccountDetails
        enabledProxyAgentAccountDetails.getAdminStatus() >> ProxyAgentAccountAdminStatus.ENABLED
        identityManagementProxy.getProxyAccountDetails("proxy-2") >> enabledProxyAgentAccountDetails
        identityManagementProxy.getProxyAccountDetails("proxy-3") >> {
            throw new NscsLdapProxyException("exception")
        }
        identityManagementProxy.deleteProxyAgentAccount("proxy-1") >> true
        and:
        ldapProxyResponseBuilder.buildLdapProxyDeleteErrorResponse(_, _, _) >> failedResponse
        when:
        NscsCommandResponse response = commandHandler.process(command, commandContext)
        then:
        notThrown(Exception.class)
        and:
        response != null
        and:
        response.isNameMultipleValueResponseType() == true
    }

    def 'partially successful forced ldap proxy delete with xmlfile due to invalid DN' () {
        given:
        LdapProxyDeleteCommand command = new LdapProxyDeleteCommand()
        command.setProperties(["xmlfile": "delete.xml", "force": null])
        and:
        nscsProxyAccount1.getDn() >> "proxy-1"
        nscsProxyAccount2.getDn() >> "proxy-2"
        nscsProxyAccount3.getDn() >> "proxy-3"
        nscsLdapProxyHelper.getNscsProxyAccountsFromCommand(_) >> [
            nscsProxyAccount1,
            nscsProxyAccount2,
            nscsProxyAccount3
        ]
        and:
        nscsLdapProxyHelper.toIdmsProxyAccountDN("proxy-1") >> "proxy-1"
        nscsLdapProxyHelper.toIdmsProxyAccountDN("proxy-2") >> "proxy-2"
        nscsLdapProxyHelper.toIdmsProxyAccountDN("proxy-3") >> { throw new NscsLdapProxyException("invalid proxy") }
        disabledProxyAgentAccountDetails.getAdminStatus() >> ProxyAgentAccountAdminStatus.DISABLED
        identityManagementProxy.getProxyAccountDetails("proxy-1") >> disabledProxyAgentAccountDetails
        enabledProxyAgentAccountDetails.getAdminStatus() >> ProxyAgentAccountAdminStatus.ENABLED
        identityManagementProxy.getProxyAccountDetails("proxy-2") >> enabledProxyAgentAccountDetails
        identityManagementProxy.deleteProxyAgentAccount("proxy-1") >> true
        and:
        ldapProxyResponseBuilder.buildLdapProxyDeleteErrorResponse(_, _, _) >> failedResponse
        when:
        NscsCommandResponse response = commandHandler.process(command, commandContext)
        then:
        notThrown(Exception.class)
        and:
        response != null
        and:
        response.isNameMultipleValueResponseType() == true
    }

    def 'failed ldap proxy delete with xmlfile due to invalid XML file' () {
        given:
        LdapProxyDeleteCommand command = new LdapProxyDeleteCommand()
        command.setProperties(["xmlfile": "delete.xml"])
        and:
        nscsLdapProxyHelper.getNscsProxyAccountsFromCommand(_) >> {
            throw new NscsLdapProxyException("invalid XML")
        }
        when:
        NscsCommandResponse response = commandHandler.process(command, commandContext)
        then:
        thrown(NscsLdapProxyException.class)
    }

    def 'failed forced ldap proxy delete with xmlfile due to unexpected exception' () {
        given:
        LdapProxyDeleteCommand command = new LdapProxyDeleteCommand()
        command.setProperties(["xmlfile": "delete.xml", "force": null])
        and:
        nscsLdapProxyHelper.getNscsProxyAccountsFromCommand(_) >> {
            throw new Exception("unexpected")
        }
        when:
        NscsCommandResponse response = commandHandler.process(command, commandContext)
        then:
        thrown(NscsLdapProxyException.class)
    }
}
