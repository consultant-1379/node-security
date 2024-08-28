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
package com.ericsson.nms.security.nscs.handler.command.utility

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.exception.NscsLdapProxyException
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException
import com.ericsson.oss.services.security.nscs.ldap.proxy.dto.NscsProxyAccount

import spock.lang.Unroll

class LdapProxyResponseBuilderTest extends CdiSpecification {

    @ObjectUnderTest
    private LdapProxyResponseBuilder ldapProxyResponseBuilder

    def 'object under test'() {
        expect:
        ldapProxyResponseBuilder != null
        and:
        ldapProxyResponseBuilder.getNumberOfColumns() == 4
        and:
        ldapProxyResponseBuilder.getResponse() != null
        and:
        ldapProxyResponseBuilder.getCommandResponse() != null
    }

    def 'build ldap proxy get success file response'() {
        given:
        when:
        def response = ldapProxyResponseBuilder.buildLdapProxyGetSuccessResponse("file identifier", "message")
        then:
        response != null
        and:
        response.isDownloadRequestMessageResponseType() == true
    }

    def 'build ldap proxy set confirmation response'() {
        given:
        when:
        def response = ldapProxyResponseBuilder.buildLdapProxySetConfirmationResponse()
        then:
        response != null
        and:
        response.isConfirmationResponseType() == true
    }

    def 'build ldap proxy set success response'() {
        given:
        when:
        def response = ldapProxyResponseBuilder.buildLdapProxySetSuccessResponse(2)
        then:
        response != null
        and:
        response.isMessageResponseType() == true
    }

    @Unroll
    def 'build ldap proxy set failure response with #numofsuccessproxyaccounts success proxy accounts'() {
        given:
        NscsProxyAccount nscsProxyAccount1 = new NscsProxyAccount()
        nscsProxyAccount1.setDn("proxy-1")
        NscsProxyAccount nscsProxyAccount2 = new NscsProxyAccount()
        nscsProxyAccount2.setDn("proxy-2")
        and:
        def Map<NscsProxyAccount, NscsServiceException> failedProxyAccountsMap = new HashMap<>()
        failedProxyAccountsMap.put(nscsProxyAccount1.getDn(), new NscsLdapProxyException("error1"))
        failedProxyAccountsMap.put(nscsProxyAccount2.getDn(), new NscsLdapProxyException("error2", new Exception("caused by")))
        when:
        def response = ldapProxyResponseBuilder.buildLdapProxySetErrorResponse(3, numofsuccessproxyaccounts, failedProxyAccountsMap)
        then:
        response != null
        and:
        response.isNameMultipleValueResponseType() == true
        where:
        numofsuccessproxyaccounts << [0, 1]
    }

    def 'build ldap proxy delete confirmation response'() {
        given:
        when:
        def response = ldapProxyResponseBuilder.buildLdapProxyDeleteConfirmationResponse()
        then:
        response != null
        and:
        response.isConfirmationResponseType() == true
    }

    def 'build ldap proxy delete success response'() {
        given:
        when:
        def response = ldapProxyResponseBuilder.buildLdapProxyDeleteSuccessResponse(2)
        then:
        response != null
        and:
        response.isMessageResponseType() == true
    }

    @Unroll
    def 'build ldap proxy delete failure response with #numofsuccessproxyaccounts success proxy accounts'() {
        given:
        NscsProxyAccount nscsProxyAccount1 = new NscsProxyAccount()
        nscsProxyAccount1.setDn("proxy-1")
        NscsProxyAccount nscsProxyAccount2 = new NscsProxyAccount()
        nscsProxyAccount2.setDn("proxy-2")
        and:
        def Map<NscsProxyAccount, NscsServiceException> failedProxyAccountsMap = new HashMap<>()
        failedProxyAccountsMap.put(nscsProxyAccount1.getDn(), new NscsLdapProxyException("error1"))
        failedProxyAccountsMap.put(nscsProxyAccount2.getDn(), new NscsLdapProxyException("error2", new Exception("caused by")))
        when:
        def response = ldapProxyResponseBuilder.buildLdapProxyDeleteErrorResponse(3, numofsuccessproxyaccounts, failedProxyAccountsMap)
        then:
        response != null
        and:
        response.isNameMultipleValueResponseType() == true
        where:
        numofsuccessproxyaccounts << [0, 1]
    }
}
