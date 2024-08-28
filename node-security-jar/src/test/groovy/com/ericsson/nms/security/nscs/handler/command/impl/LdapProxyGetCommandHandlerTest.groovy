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

import java.nio.charset.StandardCharsets

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse
import com.ericsson.nms.security.nscs.api.command.NscsDownloadRequestMessageCommandResponse
import com.ericsson.nms.security.nscs.api.command.types.LdapProxyGetCommand
import com.ericsson.nms.security.nscs.api.exception.NscsLdapProxyException
import com.ericsson.nms.security.nscs.handler.CommandContext
import com.ericsson.nms.security.nscs.handler.command.utility.LdapProxyResponseBuilder
import com.ericsson.nms.security.nscs.ldap.control.IdentityManagementProxy
import com.ericsson.nms.security.nscs.logger.NscsLogger
import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountGetData
import com.ericsson.oss.services.security.nscs.ldap.proxy.NscsLdapProxyHelper
import com.ericsson.oss.services.security.nscs.ldap.proxy.dto.NscsProxyAccountsData

import spock.lang.Shared

class LdapProxyGetCommandHandlerTest extends CdiSpecification {

    @ObjectUnderTest
    private LdapProxyGetCommandHandler commandHandler

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

    private ProxyAgentAccountGetData proxyAgentAccountGetData = mock(ProxyAgentAccountGetData.class)
    private NscsProxyAccountsData ncsProxyAccountsData = mock(NscsProxyAccountsData.class)
    private NscsDownloadRequestMessageCommandResponse successfulResponse = new NscsDownloadRequestMessageCommandResponse(0, "", "")

    @Shared
    private byte[] emptyFileContents = "".getBytes(StandardCharsets.UTF_8)

    def "object under test injection" () {
        expect:
        commandHandler != null
    }

    def 'successful ldap proxy get all summary' () {
        given:
        LdapProxyGetCommand command = new LdapProxyGetCommand()
        command.setProperties(["proxylist": "*", "summary": null])
        and:
        identityManagementProxy.getAllProxyAccounts(_, _, _) >> proxyAgentAccountGetData
        and:
        nscsLdapProxyHelper.fromIdmsDto(proxyAgentAccountGetData) >> ncsProxyAccountsData
        and:
        nscsLdapProxyHelper.getXmlFromNscsProxyAccountsData(_) >> "success"
        and:
        ldapProxyResponseBuilder.buildLdapProxyGetSuccessResponse(_, _) >> successfulResponse
        when:
        NscsCommandResponse response = commandHandler.process(command, commandContext)
        then:
        notThrown(Exception.class)
        and:
        response != null
        and:
        response.isDownloadRequestMessageResponseType() == true
    }

    def 'successful ldap proxy get all legacy' () {
        given:
        LdapProxyGetCommand command = new LdapProxyGetCommand()
        command.setProperties(["proxylist": "*", "legacy": null])
        and:
        identityManagementProxy.getAllProxyAccounts(_, _, _) >> proxyAgentAccountGetData
        and:
        nscsLdapProxyHelper.fromIdmsDto(proxyAgentAccountGetData) >> ncsProxyAccountsData
        and:
        nscsLdapProxyHelper.getXmlFromNscsProxyAccountsData(_) >> "success"
        and:
        ldapProxyResponseBuilder.buildLdapProxyGetSuccessResponse(_, _) >> successfulResponse
        when:
        NscsCommandResponse response = commandHandler.process(command, commandContext)
        then:
        notThrown(Exception.class)
        and:
        response != null
        and:
        response.isDownloadRequestMessageResponseType() == true
    }

    def 'successful ldap proxy get all' () {
        given:
        LdapProxyGetCommand command = new LdapProxyGetCommand()
        command.setProperties(["proxylist": "*"])
        and:
        identityManagementProxy.getAllProxyAccounts(_, _, _) >> proxyAgentAccountGetData
        and:
        nscsLdapProxyHelper.fromIdmsDto(proxyAgentAccountGetData) >> ncsProxyAccountsData
        and:
        nscsLdapProxyHelper.getXmlFromNscsProxyAccountsData(_) >> "success"
        and:
        ldapProxyResponseBuilder.buildLdapProxyGetSuccessResponse(_, _) >> successfulResponse
        when:
        NscsCommandResponse response = commandHandler.process(command, commandContext)
        then:
        notThrown(Exception.class)
        and:
        response != null
        and:
        response.isDownloadRequestMessageResponseType() == true
    }

    def 'empty ldap proxy get all' () {
        given:
        LdapProxyGetCommand command = new LdapProxyGetCommand()
        command.setProperties(["proxylist": "*"])
        and:
        identityManagementProxy.getAllProxyAccounts(_, _, _) >> proxyAgentAccountGetData
        and:
        nscsLdapProxyHelper.fromIdmsDto(proxyAgentAccountGetData) >> ncsProxyAccountsData
        and:
        nscsLdapProxyHelper.getXmlFromNscsProxyAccountsData(_) >> ""
        and:
        ldapProxyResponseBuilder.buildLdapProxyGetSuccessResponse(_, _) >> successfulResponse
        when:
        NscsCommandResponse response = commandHandler.process(command, commandContext)
        then:
        notThrown(Exception.class)
        and:
        response != null
        and:
        response.isDownloadRequestMessageResponseType() == true
    }

    def 'successful ldap proxy get by admin status' () {
        given:
        LdapProxyGetCommand command = new LdapProxyGetCommand()
        command.setProperties(["admin-status": "DISABLED"])
        and:
        identityManagementProxy.getAllProxyAccountsByAdminStatus(_, _, _) >> proxyAgentAccountGetData
        and:
        nscsLdapProxyHelper.fromIdmsDto(proxyAgentAccountGetData) >> ncsProxyAccountsData
        and:
        nscsLdapProxyHelper.getXmlFromNscsProxyAccountsData(_) >> "success"
        and:
        ldapProxyResponseBuilder.buildLdapProxyGetSuccessResponse(_, _) >> successfulResponse
        when:
        NscsCommandResponse response = commandHandler.process(command, commandContext)
        then:
        notThrown(Exception.class)
        and:
        response != null
        and:
        response.isDownloadRequestMessageResponseType() == true
    }

    def 'successful ldap proxy get by inactivity seconds' () {
        given:
        LdapProxyGetCommand command = new LdapProxyGetCommand()
        command.setProperties(["inactivity-seconds": "20"])
        and:
        identityManagementProxy.getAllProxyAccountsByInactivityPeriod(_, _, _) >> proxyAgentAccountGetData
        and:
        nscsLdapProxyHelper.fromIdmsDto(proxyAgentAccountGetData) >> ncsProxyAccountsData
        and:
        nscsLdapProxyHelper.getXmlFromNscsProxyAccountsData(_) >> "success"
        and:
        ldapProxyResponseBuilder.buildLdapProxyGetSuccessResponse(_, _) >> successfulResponse
        when:
        NscsCommandResponse response = commandHandler.process(command, commandContext)
        then:
        notThrown(Exception.class)
        and:
        response != null
        and:
        response.isDownloadRequestMessageResponseType() == true
    }

    def 'successful ldap proxy get by inactivity hours' () {
        given:
        LdapProxyGetCommand command = new LdapProxyGetCommand()
        command.setProperties(["inactivity-hours": "2"])
        and:
        identityManagementProxy.getAllProxyAccountsByInactivityPeriod(_, _, _) >> proxyAgentAccountGetData
        and:
        nscsLdapProxyHelper.fromIdmsDto(proxyAgentAccountGetData) >> ncsProxyAccountsData
        and:
        nscsLdapProxyHelper.getXmlFromNscsProxyAccountsData(_) >> "success"
        and:
        ldapProxyResponseBuilder.buildLdapProxyGetSuccessResponse(_, _) >> successfulResponse
        when:
        NscsCommandResponse response = commandHandler.process(command, commandContext)
        then:
        notThrown(Exception.class)
        and:
        response != null
        and:
        response.isDownloadRequestMessageResponseType() == true
    }

    def 'successful ldap proxy get by inactivity days' () {
        given:
        LdapProxyGetCommand command = new LdapProxyGetCommand()
        command.setProperties(["inactivity-days": "7"])
        and:
        identityManagementProxy.getAllProxyAccountsByInactivityPeriod(_, _, _) >> proxyAgentAccountGetData
        and:
        nscsLdapProxyHelper.fromIdmsDto(proxyAgentAccountGetData) >> ncsProxyAccountsData
        and:
        nscsLdapProxyHelper.getXmlFromNscsProxyAccountsData(_) >> "success"
        and:
        ldapProxyResponseBuilder.buildLdapProxyGetSuccessResponse(_, _) >> successfulResponse
        when:
        NscsCommandResponse response = commandHandler.process(command, commandContext)
        then:
        notThrown(Exception.class)
        and:
        response != null
        and:
        response.isDownloadRequestMessageResponseType() == true
    }

    def 'failed ldap proxy get due to missing filter option' () {
        given:
        LdapProxyGetCommand command = new LdapProxyGetCommand()
        when:
        NscsCommandResponse response = commandHandler.process(command, commandContext)
        then:
        thrown(NscsLdapProxyException.class)
    }

    def 'failed ldap proxy get all due to IDMS exception' () {
        given:
        LdapProxyGetCommand command = new LdapProxyGetCommand()
        command.setProperties(["proxylist": "*"])
        and:
        identityManagementProxy.getAllProxyAccounts(_, _, _) >> { throw new NscsLdapProxyException("this is the exception message") }
        when:
        NscsCommandResponse response = commandHandler.process(command, commandContext)
        then:
        thrown(NscsLdapProxyException.class)
    }

    def 'failed ldap proxy get all due to NSCS Proxy Helper exception' () {
        given:
        LdapProxyGetCommand command = new LdapProxyGetCommand()
        command.setProperties(["proxylist": "*"])
        and:
        identityManagementProxy.getAllProxyAccounts(_, _, _) >> proxyAgentAccountGetData
        and:
        nscsLdapProxyHelper.fromIdmsDto(proxyAgentAccountGetData) >> { throw new NscsLdapProxyException("this is the exception message") }
        when:
        NscsCommandResponse response = commandHandler.process(command, commandContext)
        then:
        thrown(NscsLdapProxyException.class)
    }

    def 'failed ldap proxy get all due to NSCS Proxy Helper marshal exception' () {
        given:
        LdapProxyGetCommand command = new LdapProxyGetCommand()
        command.setProperties(["proxylist": "*"])
        and:
        identityManagementProxy.getAllProxyAccountsByInactivityPeriod(_, _, _) >> proxyAgentAccountGetData
        and:
        nscsLdapProxyHelper.fromIdmsDto(proxyAgentAccountGetData) >> ncsProxyAccountsData
        and:
        nscsLdapProxyHelper.getXmlFromNscsProxyAccountsData(_) >> { throw new NscsLdapProxyException("this is the exception message") }
        when:
        NscsCommandResponse response = commandHandler.process(command, commandContext)
        then:
        thrown(NscsLdapProxyException.class)
    }

    def 'failed ldap proxy get all due to unexpected exception' () {
        given:
        LdapProxyGetCommand command = new LdapProxyGetCommand()
        command.setProperties(["proxylist": "*"])
        and:
        identityManagementProxy.getAllProxyAccounts(_, _, _) >> { throw new Exception("this is the exception message") }
        when:
        NscsCommandResponse response = commandHandler.process(command, commandContext)
        then:
        thrown(NscsLdapProxyException.class)
    }
}
