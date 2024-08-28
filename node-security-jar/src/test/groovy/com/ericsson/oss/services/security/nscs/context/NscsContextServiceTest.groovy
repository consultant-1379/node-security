/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2023
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.context

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.command.NscsCommandType
import com.ericsson.oss.itpf.sdk.context.ContextService

import spock.lang.Unroll

class NscsContextServiceTest extends CdiSpecification {

    @ObjectUnderTest
    private NscsContextService nscsContextService

    @MockedImplementation
    private ContextService contextService

    def 'object under test'() {
        expect:
        nscsContextService != null
        and:
        nscsContextService.getUserIdContextValue() == null
        and:
        nscsContextService.getSourceIpAddrContextValue() == null
        and:
        nscsContextService.getSessionIdContextValue() == null
        and:
        nscsContextService.getCommandTextContextValue() == null
        and:
        nscsContextService.getCommandTypeContextValue() == null
        and:
        nscsContextService.getNumValidItemsContextValue() == null
        and:
        nscsContextService.getNumSkippedItemsContextValue() == null
        and:
        nscsContextService.getNumInvalidItemsContextValue() == null
        and:
        nscsContextService.getNumSuccessItemsContextValue() == null
        and:
        nscsContextService.getNumFailedItemsContextValue() == null
        and:
        nscsContextService.getJobIdContextValue() == null
        and:
        nscsContextService.getProxyAccountNameContextValue() == null
        and:
        nscsContextService.getRestMethodContextValue() == null
        and:
        nscsContextService.getRestUrlFileContextValue() == null
        and:
        nscsContextService.getRestUrlPathContextValue() == null
        and:
        nscsContextService.getRestRequestPayloadContextValue() == null
        and:
        nscsContextService.getErrorDetailContextValue() == null
        and:
        nscsContextService.getClassNameContextValue() == null
        and:
        nscsContextService.getMethodNameContextValue() == null
        and:
        nscsContextService.getInputNodeNameContextValue() == null
    }

    def 'set user ID'() {
        given:
        contextService.getContextValue("X-Tor-UserID") >> "user-id"
        when:
        nscsContextService.setUserIdContextValue("user-id")
        then:
        nscsContextService.getUserIdContextValue() == "user-id"
        and:
        nscsContextService.getSourceIpAddrContextValue() == null
        and:
        nscsContextService.getSessionIdContextValue() == null
        and:
        nscsContextService.getCommandTextContextValue() == null
        and:
        nscsContextService.getCommandTypeContextValue() == null
        and:
        nscsContextService.getNumValidItemsContextValue() == null
        and:
        nscsContextService.getNumSkippedItemsContextValue() == null
        and:
        nscsContextService.getNumInvalidItemsContextValue() == null
        and:
        nscsContextService.getNumSuccessItemsContextValue() == null
        and:
        nscsContextService.getNumFailedItemsContextValue() == null
        and:
        nscsContextService.getJobIdContextValue() == null
        and:
        nscsContextService.getProxyAccountNameContextValue() == null
        and:
        nscsContextService.getRestMethodContextValue() == null
        and:
        nscsContextService.getRestUrlFileContextValue() == null
        and:
        nscsContextService.getRestUrlPathContextValue() == null
        and:
        nscsContextService.getRestRequestPayloadContextValue() == null
        and:
        nscsContextService.getErrorDetailContextValue() == null
        and:
        nscsContextService.getClassNameContextValue() == null
        and:
        nscsContextService.getMethodNameContextValue() == null
        and:
        nscsContextService.getInputNodeNameContextValue() == null
    }

    def 'set sessiom ID'() {
        given:
        contextService.getContextValue("SESSION_ID") >> "session-id"
        when:
        nscsContextService.setSessionIdContextValue("session-id")
        then:
        nscsContextService.getUserIdContextValue() == null
        and:
        nscsContextService.getSourceIpAddrContextValue() == null
        and:
        nscsContextService.getSessionIdContextValue() == "session-id"
        and:
        nscsContextService.getCommandTextContextValue() == null
        and:
        nscsContextService.getCommandTypeContextValue() == null
        and:
        nscsContextService.getNumValidItemsContextValue() == null
        and:
        nscsContextService.getNumSkippedItemsContextValue() == null
        and:
        nscsContextService.getNumInvalidItemsContextValue() == null
        and:
        nscsContextService.getNumSuccessItemsContextValue() == null
        and:
        nscsContextService.getNumFailedItemsContextValue() == null
        and:
        nscsContextService.getJobIdContextValue() == null
        and:
        nscsContextService.getProxyAccountNameContextValue() == null
        and:
        nscsContextService.getRestMethodContextValue() == null
        and:
        nscsContextService.getRestUrlFileContextValue() == null
        and:
        nscsContextService.getRestUrlPathContextValue() == null
        and:
        nscsContextService.getRestRequestPayloadContextValue() == null
        and:
        nscsContextService.getErrorDetailContextValue() == null
        and:
        nscsContextService.getClassNameContextValue() == null
        and:
        nscsContextService.getMethodNameContextValue() == null
        and:
        nscsContextService.getInputNodeNameContextValue() == null
    }

    def 'set source IP address'() {
        given:
        contextService.getContextValue("SOURCE_IP_ADDRESS") >> "1.2.3.4"
        when:
        nscsContextService.setSourceIpAddrContextValue("1.2.3.4")
        then:
        nscsContextService.getUserIdContextValue() == null
        and:
        nscsContextService.getSourceIpAddrContextValue() == "1.2.3.4"
        and:
        nscsContextService.getSessionIdContextValue() == null
        and:
        nscsContextService.getCommandTextContextValue() == null
        and:
        nscsContextService.getCommandTypeContextValue() == null
        and:
        nscsContextService.getNumValidItemsContextValue() == null
        and:
        nscsContextService.getNumSkippedItemsContextValue() == null
        and:
        nscsContextService.getNumInvalidItemsContextValue() == null
        and:
        nscsContextService.getNumSuccessItemsContextValue() == null
        and:
        nscsContextService.getNumFailedItemsContextValue() == null
        and:
        nscsContextService.getJobIdContextValue() == null
        and:
        nscsContextService.getProxyAccountNameContextValue() == null
        and:
        nscsContextService.getRestMethodContextValue() == null
        and:
        nscsContextService.getRestUrlFileContextValue() == null
        and:
        nscsContextService.getRestUrlPathContextValue() == null
        and:
        nscsContextService.getRestRequestPayloadContextValue() == null
        and:
        nscsContextService.getErrorDetailContextValue() == null
        and:
        nscsContextService.getClassNameContextValue() == null
        and:
        nscsContextService.getMethodNameContextValue() == null
        and:
        nscsContextService.getInputNodeNameContextValue() == null
    }

    def 'set username'() {
        given:
        contextService.getContextValue("User.Name") >> "username"
        when:
        nscsContextService.setUserNameContextValue("username")
        then:
        nscsContextService.getUserIdContextValue() == null
        and:
        nscsContextService.getSourceIpAddrContextValue() == null
        and:
        nscsContextService.getSessionIdContextValue() == null
        and:
        nscsContextService.getCommandTextContextValue() == null
        and:
        nscsContextService.getCommandTypeContextValue() == null
        and:
        nscsContextService.getNumValidItemsContextValue() == null
        and:
        nscsContextService.getNumSkippedItemsContextValue() == null
        and:
        nscsContextService.getNumInvalidItemsContextValue() == null
        and:
        nscsContextService.getNumSuccessItemsContextValue() == null
        and:
        nscsContextService.getNumFailedItemsContextValue() == null
        and:
        nscsContextService.getJobIdContextValue() == null
        and:
        nscsContextService.getProxyAccountNameContextValue() == null
        and:
        nscsContextService.getRestMethodContextValue() == null
        and:
        nscsContextService.getRestUrlFileContextValue() == null
        and:
        nscsContextService.getRestUrlPathContextValue() == null
        and:
        nscsContextService.getRestRequestPayloadContextValue() == null
        and:
        nscsContextService.getErrorDetailContextValue() == null
        and:
        nscsContextService.getClassNameContextValue() == null
        and:
        nscsContextService.getMethodNameContextValue() == null
        and:
        nscsContextService.getInputNodeNameContextValue() == null
    }

    @Unroll
    def 'set command text #text'() {
        given:
        def obfuscated = nscsContextService.getObfuscatedCommandText(text)
        contextService.getContextValue("COMMAND_TEXT") >> obfuscated
        when:
        nscsContextService.setCommandTextContextValue(text)
        then:
        nscsContextService.getUserIdContextValue() == null
        and:
        nscsContextService.getSourceIpAddrContextValue() == null
        and:
        nscsContextService.getSessionIdContextValue() == null
        and:
        nscsContextService.getCommandTextContextValue() == obfuscatedtext
        and:
        nscsContextService.getCommandTypeContextValue() == null
        and:
        nscsContextService.getNumValidItemsContextValue() == null
        and:
        nscsContextService.getNumSkippedItemsContextValue() == null
        and:
        nscsContextService.getNumInvalidItemsContextValue() == null
        and:
        nscsContextService.getNumSuccessItemsContextValue() == null
        and:
        nscsContextService.getNumFailedItemsContextValue() == null
        and:
        nscsContextService.getJobIdContextValue() == null
        and:
        nscsContextService.getProxyAccountNameContextValue() == null
        and:
        nscsContextService.getRestMethodContextValue() == null
        and:
        nscsContextService.getRestUrlFileContextValue() == null
        and:
        nscsContextService.getRestUrlPathContextValue() == null
        and:
        nscsContextService.getRestRequestPayloadContextValue() == null
        and:
        nscsContextService.getErrorDetailContextValue() == null
        and:
        nscsContextService.getClassNameContextValue() == null
        and:
        nscsContextService.getMethodNameContextValue() == null
        and:
        nscsContextService.getInputNodeNameContextValue() == null
        where:
        text << [
            " snmp authpriv --auth_algo MD5 --auth_password authpass --priv_algo DES --priv_password privpass --nodelist LTE01dg2ERBS00003",
            " snmp authnopriv --auth_algo MD5 --auth_password authpass --priv_algo DES --priv_password privpass --nodelist LTE01dg2ERBS00003",
            " credentials create --secureusername netsim --secureuserpassword netsim -n LTE01dg2ERBS00003",
            null,
            ""
        ]
        obfuscatedtext << [
            "secadm  snmp authpriv --auth_algo MD5 --auth_password ******* --priv_algo DES --priv_password ******* --nodelist LTE01dg2ERBS00003",
            "secadm  snmp authnopriv --auth_algo MD5 --auth_password ******* --priv_algo DES --priv_password ******* --nodelist LTE01dg2ERBS00003",
            "secadm  credentials create --secureusername ******* --secureuserpassword ******* -n LTE01dg2ERBS00003",
            null,
            null
        ]
    }

    @Unroll
    def 'set command type #commandtype'() {
        given:
        def NscsCommandType cmdtype = (NscsCommandType)commandtype
        def type = cmdtype.name()
        contextService.getContextValue("COMMAND_TYPE") >> type
        when:
        nscsContextService.setCommandTypeContextValue(type)
        then:
        nscsContextService.getUserIdContextValue() == null
        and:
        nscsContextService.getSourceIpAddrContextValue() == null
        and:
        nscsContextService.getSessionIdContextValue() == null
        and:
        nscsContextService.getCommandTextContextValue() == null
        and:
        nscsContextService.getCommandTypeContextValue() == type
        and:
        commandtype == NscsCommandType.valueOf(type)
        and:
        nscsContextService.getNumValidItemsContextValue() == null
        and:
        nscsContextService.getNumSkippedItemsContextValue() == null
        and:
        nscsContextService.getNumInvalidItemsContextValue() == null
        and:
        nscsContextService.getNumSuccessItemsContextValue() == null
        and:
        nscsContextService.getNumFailedItemsContextValue() == null
        and:
        nscsContextService.getJobIdContextValue() == null
        and:
        nscsContextService.getProxyAccountNameContextValue() == null
        and:
        nscsContextService.getRestMethodContextValue() == null
        and:
        nscsContextService.getRestUrlFileContextValue() == null
        and:
        nscsContextService.getRestUrlPathContextValue() == null
        and:
        nscsContextService.getRestRequestPayloadContextValue() == null
        and:
        nscsContextService.getErrorDetailContextValue() == null
        and:
        nscsContextService.getClassNameContextValue() == null
        and:
        nscsContextService.getMethodNameContextValue() == null
        and:
        nscsContextService.getInputNodeNameContextValue() == null
        where:
        commandtype << NscsCommandType.values()
    }

    def 'set num valid items'() {
        given:
        contextService.getContextValue("NUM_VALID_ITEMS") >> 2
        when:
        nscsContextService.setNumValidItemsContextValue(2)
        then:
        nscsContextService.getUserIdContextValue() == null
        and:
        nscsContextService.getSourceIpAddrContextValue() == null
        and:
        nscsContextService.getSessionIdContextValue() == null
        and:
        nscsContextService.getCommandTextContextValue() == null
        and:
        nscsContextService.getNumValidItemsContextValue() == 2
        and:
        nscsContextService.getNumSkippedItemsContextValue() == null
        and:
        nscsContextService.getNumInvalidItemsContextValue() == null
        and:
        nscsContextService.getNumSuccessItemsContextValue() == null
        and:
        nscsContextService.getNumFailedItemsContextValue() == null
        and:
        nscsContextService.getJobIdContextValue() == null
        and:
        nscsContextService.getProxyAccountNameContextValue() == null
        and:
        nscsContextService.getRestMethodContextValue() == null
        and:
        nscsContextService.getRestUrlFileContextValue() == null
        and:
        nscsContextService.getRestUrlPathContextValue() == null
        and:
        nscsContextService.getRestRequestPayloadContextValue() == null
        and:
        nscsContextService.getErrorDetailContextValue() == null
        and:
        nscsContextService.getClassNameContextValue() == null
        and:
        nscsContextService.getMethodNameContextValue() == null
        and:
        nscsContextService.getInputNodeNameContextValue() == null
    }

    def 'set num skipped items'() {
        given:
        contextService.getContextValue("NUM_SKIPPED_ITEMS") >> 5
        when:
        nscsContextService.setNumSkippedItemsContextValue(5)
        then:
        nscsContextService.getUserIdContextValue() == null
        and:
        nscsContextService.getSourceIpAddrContextValue() == null
        and:
        nscsContextService.getSessionIdContextValue() == null
        and:
        nscsContextService.getCommandTextContextValue() == null
        and:
        nscsContextService.getNumValidItemsContextValue() == null
        and:
        nscsContextService.getNumSkippedItemsContextValue() == 5
        and:
        nscsContextService.getNumInvalidItemsContextValue() == null
        and:
        nscsContextService.getNumSuccessItemsContextValue() == null
        and:
        nscsContextService.getNumFailedItemsContextValue() == null
        and:
        nscsContextService.getJobIdContextValue() == null
        and:
        nscsContextService.getProxyAccountNameContextValue() == null
        and:
        nscsContextService.getRestMethodContextValue() == null
        and:
        nscsContextService.getRestUrlFileContextValue() == null
        and:
        nscsContextService.getRestUrlPathContextValue() == null
        and:
        nscsContextService.getRestRequestPayloadContextValue() == null
        and:
        nscsContextService.getErrorDetailContextValue() == null
        and:
        nscsContextService.getClassNameContextValue() == null
        and:
        nscsContextService.getMethodNameContextValue() == null
        and:
        nscsContextService.getInputNodeNameContextValue() == null
    }

    def 'set num invalid items'() {
        given:
        contextService.getContextValue("NUM_INVALID_ITEMS") >> 2
        when:
        nscsContextService.setNumInvalidItemsContextValue(2)
        then:
        nscsContextService.getUserIdContextValue() == null
        and:
        nscsContextService.getSourceIpAddrContextValue() == null
        and:
        nscsContextService.getSessionIdContextValue() == null
        and:
        nscsContextService.getCommandTextContextValue() == null
        and:
        nscsContextService.getNumValidItemsContextValue() == null
        and:
        nscsContextService.getNumSkippedItemsContextValue() == null
        and:
        nscsContextService.getNumInvalidItemsContextValue() == 2
        and:
        nscsContextService.getNumSuccessItemsContextValue() == null
        and:
        nscsContextService.getNumFailedItemsContextValue() == null
        and:
        nscsContextService.getJobIdContextValue() == null
        and:
        nscsContextService.getProxyAccountNameContextValue() == null
        and:
        nscsContextService.getRestMethodContextValue() == null
        and:
        nscsContextService.getRestUrlFileContextValue() == null
        and:
        nscsContextService.getRestUrlPathContextValue() == null
        and:
        nscsContextService.getRestRequestPayloadContextValue() == null
        and:
        nscsContextService.getErrorDetailContextValue() == null
        and:
        nscsContextService.getClassNameContextValue() == null
        and:
        nscsContextService.getMethodNameContextValue() == null
        and:
        nscsContextService.getInputNodeNameContextValue() == null
    }

    def 'set num success items'() {
        given:
        contextService.getContextValue("NUM_SUCCESS_ITEMS") >> 2
        when:
        nscsContextService.setNumSuccessItemsContextValue(2)
        then:
        nscsContextService.getUserIdContextValue() == null
        and:
        nscsContextService.getSourceIpAddrContextValue() == null
        and:
        nscsContextService.getSessionIdContextValue() == null
        and:
        nscsContextService.getCommandTextContextValue() == null
        and:
        nscsContextService.getNumValidItemsContextValue() == null
        and:
        nscsContextService.getNumSkippedItemsContextValue() == null
        and:
        nscsContextService.getNumInvalidItemsContextValue() == null
        and:
        nscsContextService.getNumSuccessItemsContextValue() == 2
        and:
        nscsContextService.getNumFailedItemsContextValue() == null
        and:
        nscsContextService.getJobIdContextValue() == null
        and:
        nscsContextService.getProxyAccountNameContextValue() == null
        and:
        nscsContextService.getRestMethodContextValue() == null
        and:
        nscsContextService.getRestUrlFileContextValue() == null
        and:
        nscsContextService.getRestUrlPathContextValue() == null
        and:
        nscsContextService.getRestRequestPayloadContextValue() == null
        and:
        nscsContextService.getErrorDetailContextValue() == null
        and:
        nscsContextService.getClassNameContextValue() == null
        and:
        nscsContextService.getMethodNameContextValue() == null
        and:
        nscsContextService.getInputNodeNameContextValue() == null
    }

    def 'set num failed items'() {
        given:
        contextService.getContextValue("NUM_FAILED_ITEMS") >> 2
        when:
        nscsContextService.setNumFailedItemsContextValue(2)
        then:
        nscsContextService.getUserIdContextValue() == null
        and:
        nscsContextService.getSourceIpAddrContextValue() == null
        and:
        nscsContextService.getSessionIdContextValue() == null
        and:
        nscsContextService.getCommandTextContextValue() == null
        and:
        nscsContextService.getNumValidItemsContextValue() == null
        and:
        nscsContextService.getNumSkippedItemsContextValue() == null
        and:
        nscsContextService.getNumInvalidItemsContextValue() == null
        and:
        nscsContextService.getNumSuccessItemsContextValue() == null
        and:
        nscsContextService.getNumFailedItemsContextValue() == 2
        and:
        nscsContextService.getJobIdContextValue() == null
        and:
        nscsContextService.getProxyAccountNameContextValue() == null
        and:
        nscsContextService.getRestMethodContextValue() == null
        and:
        nscsContextService.getRestUrlFileContextValue() == null
        and:
        nscsContextService.getRestUrlPathContextValue() == null
        and:
        nscsContextService.getRestRequestPayloadContextValue() == null
        and:
        nscsContextService.getErrorDetailContextValue() == null
        and:
        nscsContextService.getClassNameContextValue() == null
        and:
        nscsContextService.getMethodNameContextValue() == null
        and:
        nscsContextService.getInputNodeNameContextValue() == null
    }

    def 'set job ID'() {
        given:
        def jobId = UUID.fromString("11ed9ac1-49ce-40dc-bab0-3f347092da6c")
        contextService.getContextValue("JOB_ID") >> jobId
        when:
        nscsContextService.setJobIdContextValue(jobId)
        then:
        nscsContextService.getUserIdContextValue() == null
        and:
        nscsContextService.getSourceIpAddrContextValue() == null
        and:
        nscsContextService.getSessionIdContextValue() == null
        and:
        nscsContextService.getCommandTextContextValue() == null
        and:
        nscsContextService.getNumValidItemsContextValue() == null
        and:
        nscsContextService.getNumSkippedItemsContextValue() == null
        and:
        nscsContextService.getNumInvalidItemsContextValue() == null
        and:
        nscsContextService.getNumSuccessItemsContextValue() == null
        and:
        nscsContextService.getNumFailedItemsContextValue() == null
        and:
        nscsContextService.getJobIdContextValue() == jobId
        and:
        nscsContextService.getProxyAccountNameContextValue() == null
        and:
        nscsContextService.getRestMethodContextValue() == null
        and:
        nscsContextService.getRestUrlFileContextValue() == null
        and:
        nscsContextService.getRestUrlPathContextValue() == null
        and:
        nscsContextService.getRestRequestPayloadContextValue() == null
        and:
        nscsContextService.getErrorDetailContextValue() == null
        and:
        nscsContextService.getClassNameContextValue() == null
        and:
        nscsContextService.getMethodNameContextValue() == null
        and:
        nscsContextService.getInputNodeNameContextValue() == null
    }

    def 'set proxy account name'() {
        given:
        def proxyAccountName = "ProxyAccount_5c31e018-1c46-4f59-8b69-a892581b25a0"
        contextService.getContextValue("PROXY_ACCOUNT_NAME") >> proxyAccountName
        when:
        nscsContextService.setProxyAccountNameContextValue(proxyAccountName)
        then:
        nscsContextService.getUserIdContextValue() == null
        and:
        nscsContextService.getSourceIpAddrContextValue() == null
        and:
        nscsContextService.getSessionIdContextValue() == null
        and:
        nscsContextService.getCommandTextContextValue() == null
        and:
        nscsContextService.getNumValidItemsContextValue() == null
        and:
        nscsContextService.getNumSkippedItemsContextValue() == null
        and:
        nscsContextService.getNumInvalidItemsContextValue() == null
        and:
        nscsContextService.getNumSuccessItemsContextValue() == null
        and:
        nscsContextService.getNumFailedItemsContextValue() == null
        and:
        nscsContextService.getJobIdContextValue() == null
        and:
        nscsContextService.getProxyAccountNameContextValue() == proxyAccountName
        and:
        nscsContextService.getRestMethodContextValue() == null
        and:
        nscsContextService.getRestUrlFileContextValue() == null
        and:
        nscsContextService.getRestUrlPathContextValue() == null
        and:
        nscsContextService.getRestRequestPayloadContextValue() == null
        and:
        nscsContextService.getErrorDetailContextValue() == null
        and:
        nscsContextService.getClassNameContextValue() == null
        and:
        nscsContextService.getMethodNameContextValue() == null
        and:
        nscsContextService.getInputNodeNameContextValue() == null
    }

    def 'set rest method'() {
        given:
        def restMethod = "GET"
        contextService.getContextValue("REST_METHOD") >> restMethod
        when:
        nscsContextService.setRestMethodContextValue(restMethod)
        then:
        nscsContextService.getUserIdContextValue() == null
        and:
        nscsContextService.getSourceIpAddrContextValue() == null
        and:
        nscsContextService.getSessionIdContextValue() == null
        and:
        nscsContextService.getCommandTextContextValue() == null
        and:
        nscsContextService.getNumValidItemsContextValue() == null
        and:
        nscsContextService.getNumSkippedItemsContextValue() == null
        and:
        nscsContextService.getNumInvalidItemsContextValue() == null
        and:
        nscsContextService.getNumSuccessItemsContextValue() == null
        and:
        nscsContextService.getNumFailedItemsContextValue() == null
        and:
        nscsContextService.getJobIdContextValue() == null
        and:
        nscsContextService.getProxyAccountNameContextValue() == null
        and:
        nscsContextService.getRestMethodContextValue() == restMethod
        and:
        nscsContextService.getRestUrlFileContextValue() == null
        and:
        nscsContextService.getRestUrlPathContextValue() == null
        and:
        nscsContextService.getRestRequestPayloadContextValue() == null
        and:
        nscsContextService.getErrorDetailContextValue() == null
        and:
        nscsContextService.getClassNameContextValue() == null
        and:
        nscsContextService.getMethodNameContextValue() == null
        and:
        nscsContextService.getInputNodeNameContextValue() == null
    }

    def 'set rest URL file'() {
        given:
        def restUrlFile = "/node-security/model/getTargetInfo?targetCategory=VIM"
        contextService.getContextValue("REST_URL_FILE") >> restUrlFile
        when:
        nscsContextService.setRestUrlFileContextValue(restUrlFile)
        then:
        nscsContextService.getUserIdContextValue() == null
        and:
        nscsContextService.getSourceIpAddrContextValue() == null
        and:
        nscsContextService.getSessionIdContextValue() == null
        and:
        nscsContextService.getCommandTextContextValue() == null
        and:
        nscsContextService.getNumValidItemsContextValue() == null
        and:
        nscsContextService.getNumSkippedItemsContextValue() == null
        and:
        nscsContextService.getNumInvalidItemsContextValue() == null
        and:
        nscsContextService.getNumSuccessItemsContextValue() == null
        and:
        nscsContextService.getNumFailedItemsContextValue() == null
        and:
        nscsContextService.getJobIdContextValue() == null
        and:
        nscsContextService.getProxyAccountNameContextValue() == null
        and:
        nscsContextService.getRestMethodContextValue() == null
        and:
        nscsContextService.getRestUrlFileContextValue() == restUrlFile
        and:
        nscsContextService.getRestUrlPathContextValue() == null
        and:
        nscsContextService.getRestRequestPayloadContextValue() == null
        and:
        nscsContextService.getErrorDetailContextValue() == null
        and:
        nscsContextService.getClassNameContextValue() == null
        and:
        nscsContextService.getMethodNameContextValue() == null
        and:
        nscsContextService.getInputNodeNameContextValue() == null
    }

    def 'set rest URL path'() {
        given:
        def restUrlPath = "/node-security/model/getTargetInfo"
        contextService.getContextValue("REST_URL_PATH") >> restUrlPath
        when:
        nscsContextService.setRestUrlPathContextValue(restUrlPath)
        then:
        nscsContextService.getUserIdContextValue() == null
        and:
        nscsContextService.getSourceIpAddrContextValue() == null
        and:
        nscsContextService.getSessionIdContextValue() == null
        and:
        nscsContextService.getCommandTextContextValue() == null
        and:
        nscsContextService.getNumValidItemsContextValue() == null
        and:
        nscsContextService.getNumSkippedItemsContextValue() == null
        and:
        nscsContextService.getNumInvalidItemsContextValue() == null
        and:
        nscsContextService.getNumSuccessItemsContextValue() == null
        and:
        nscsContextService.getNumFailedItemsContextValue() == null
        and:
        nscsContextService.getJobIdContextValue() == null
        and:
        nscsContextService.getProxyAccountNameContextValue() == null
        and:
        nscsContextService.getRestMethodContextValue() == null
        and:
        nscsContextService.getRestUrlFileContextValue() == null
        and:
        nscsContextService.getRestUrlPathContextValue() == restUrlPath
        and:
        nscsContextService.getRestRequestPayloadContextValue() == null
        and:
        nscsContextService.getErrorDetailContextValue() == null
        and:
        nscsContextService.getClassNameContextValue() == null
        and:
        nscsContextService.getMethodNameContextValue() == null
        and:
        nscsContextService.getInputNodeNameContextValue() == null
    }

    def 'set rest request payload'() {
        given:
        def restRequestPayload = "request-payload"
        contextService.getContextValue("REST_REQUEST_PAYLOAD") >> restRequestPayload
        when:
        nscsContextService.setRestRequestPayloadContextValue(restRequestPayload)
        then:
        nscsContextService.getUserIdContextValue() == null
        and:
        nscsContextService.getSourceIpAddrContextValue() == null
        and:
        nscsContextService.getSessionIdContextValue() == null
        and:
        nscsContextService.getCommandTextContextValue() == null
        and:
        nscsContextService.getNumValidItemsContextValue() == null
        and:
        nscsContextService.getNumSkippedItemsContextValue() == null
        and:
        nscsContextService.getNumInvalidItemsContextValue() == null
        and:
        nscsContextService.getNumSuccessItemsContextValue() == null
        and:
        nscsContextService.getNumFailedItemsContextValue() == null
        and:
        nscsContextService.getJobIdContextValue() == null
        and:
        nscsContextService.getProxyAccountNameContextValue() == null
        and:
        nscsContextService.getRestMethodContextValue() == null
        and:
        nscsContextService.getRestUrlFileContextValue() == null
        and:
        nscsContextService.getRestUrlPathContextValue() == null
        and:
        nscsContextService.getRestRequestPayloadContextValue() == restRequestPayload
        and:
        nscsContextService.getErrorDetailContextValue() == null
        and:
        nscsContextService.getClassNameContextValue() == null
        and:
        nscsContextService.getMethodNameContextValue() == null
        and:
        nscsContextService.getInputNodeNameContextValue() == null
    }

    def 'set error detail'() {
        given:
        def errorDetail = "error-detail"
        contextService.getContextValue("ERROR_DETAIL") >> errorDetail
        when:
        nscsContextService.setErrorDetailContextValue(errorDetail)
        then:
        nscsContextService.getUserIdContextValue() == null
        and:
        nscsContextService.getSourceIpAddrContextValue() == null
        and:
        nscsContextService.getSessionIdContextValue() == null
        and:
        nscsContextService.getCommandTextContextValue() == null
        and:
        nscsContextService.getNumValidItemsContextValue() == null
        and:
        nscsContextService.getNumSkippedItemsContextValue() == null
        and:
        nscsContextService.getNumInvalidItemsContextValue() == null
        and:
        nscsContextService.getNumSuccessItemsContextValue() == null
        and:
        nscsContextService.getNumFailedItemsContextValue() == null
        and:
        nscsContextService.getJobIdContextValue() == null
        and:
        nscsContextService.getProxyAccountNameContextValue() == null
        and:
        nscsContextService.getRestMethodContextValue() == null
        and:
        nscsContextService.getRestUrlFileContextValue() == null
        and:
        nscsContextService.getRestUrlPathContextValue() == null
        and:
        nscsContextService.getRestRequestPayloadContextValue() == null
        and:
        nscsContextService.getErrorDetailContextValue() == errorDetail
        and:
        nscsContextService.getClassNameContextValue() == null
        and:
        nscsContextService.getMethodNameContextValue() == null
        and:
        nscsContextService.getInputNodeNameContextValue() == null
    }

    def 'set class name'() {
        given:
        def className = "className"
        contextService.getContextValue("CLASS_NAME") >> className
        when:
        nscsContextService.setClassNameContextValue(className)
        then:
        nscsContextService.getUserIdContextValue() == null
        and:
        nscsContextService.getSourceIpAddrContextValue() == null
        and:
        nscsContextService.getSessionIdContextValue() == null
        and:
        nscsContextService.getCommandTextContextValue() == null
        and:
        nscsContextService.getNumValidItemsContextValue() == null
        and:
        nscsContextService.getNumSkippedItemsContextValue() == null
        and:
        nscsContextService.getNumInvalidItemsContextValue() == null
        and:
        nscsContextService.getNumSuccessItemsContextValue() == null
        and:
        nscsContextService.getNumFailedItemsContextValue() == null
        and:
        nscsContextService.getJobIdContextValue() == null
        and:
        nscsContextService.getProxyAccountNameContextValue() == null
        and:
        nscsContextService.getRestMethodContextValue() == null
        and:
        nscsContextService.getRestUrlFileContextValue() == null
        and:
        nscsContextService.getRestUrlPathContextValue() == null
        and:
        nscsContextService.getRestRequestPayloadContextValue() == null
        and:
        nscsContextService.getErrorDetailContextValue() == null
        and:
        nscsContextService.getClassNameContextValue() == className
        and:
        nscsContextService.getMethodNameContextValue() == null
        and:
        nscsContextService.getInputNodeNameContextValue() == null
    }

    def 'set method name'() {
        given:
        def methodName = "methodName"
        contextService.getContextValue("METHOD_NAME") >> methodName
        when:
        nscsContextService.setMethodNameContextValue(methodName)
        then:
        nscsContextService.getUserIdContextValue() == null
        and:
        nscsContextService.getSourceIpAddrContextValue() == null
        and:
        nscsContextService.getSessionIdContextValue() == null
        and:
        nscsContextService.getCommandTextContextValue() == null
        and:
        nscsContextService.getNumValidItemsContextValue() == null
        and:
        nscsContextService.getNumSkippedItemsContextValue() == null
        and:
        nscsContextService.getNumInvalidItemsContextValue() == null
        and:
        nscsContextService.getNumSuccessItemsContextValue() == null
        and:
        nscsContextService.getNumFailedItemsContextValue() == null
        and:
        nscsContextService.getJobIdContextValue() == null
        and:
        nscsContextService.getProxyAccountNameContextValue() == null
        and:
        nscsContextService.getRestMethodContextValue() == null
        and:
        nscsContextService.getRestUrlFileContextValue() == null
        and:
        nscsContextService.getRestUrlPathContextValue() == null
        and:
        nscsContextService.getRestRequestPayloadContextValue() == null
        and:
        nscsContextService.getErrorDetailContextValue() == null
        and:
        nscsContextService.getClassNameContextValue() == null
        and:
        nscsContextService.getMethodNameContextValue() == methodName
        and:
        nscsContextService.getInputNodeNameContextValue() == null
    }

    def 'set input node name'() {
        given:
        def inputNodeName = "inputNodeName"
        contextService.getContextValue("INPUT_NODE_NAME") >> inputNodeName
        when:
        nscsContextService.setInputNodeNameContextValue(inputNodeName)
        then:
        nscsContextService.getUserIdContextValue() == null
        and:
        nscsContextService.getSourceIpAddrContextValue() == null
        and:
        nscsContextService.getSessionIdContextValue() == null
        and:
        nscsContextService.getCommandTextContextValue() == null
        and:
        nscsContextService.getNumValidItemsContextValue() == null
        and:
        nscsContextService.getNumSkippedItemsContextValue() == null
        and:
        nscsContextService.getNumInvalidItemsContextValue() == null
        and:
        nscsContextService.getNumSuccessItemsContextValue() == null
        and:
        nscsContextService.getNumFailedItemsContextValue() == null
        and:
        nscsContextService.getJobIdContextValue() == null
        and:
        nscsContextService.getProxyAccountNameContextValue() == null
        and:
        nscsContextService.getRestMethodContextValue() == null
        and:
        nscsContextService.getRestUrlFileContextValue() == null
        and:
        nscsContextService.getRestUrlPathContextValue() == null
        and:
        nscsContextService.getRestRequestPayloadContextValue() == null
        and:
        nscsContextService.getErrorDetailContextValue() == null
        and:
        nscsContextService.getClassNameContextValue() == null
        and:
        nscsContextService.getMethodNameContextValue() == null
        and:
        nscsContextService.getInputNodeNameContextValue() == inputNodeName
    }

    def 'get context data'() {
        given:
        contextService.getContextData() >> ["NUM_VALID_ITEMS":3, "NUM_INVALID_ITEMS":4]
        when:
        def contextData = nscsContextService.getContextData()
        then:
        contextData != null
        and:
        !contextData.isEmpty()
        and:
        contextData.size() == 2
    }

    def 'update items stats for sync command'() {
        given:
        contextService.getContextValue("NUM_VALID_ITEMS") >> 3
        contextService.getContextValue("NUM_INVALID_ITEMS") >> 4
        contextService.getContextValue("NUM_SUCCESS_ITEMS") >> 2
        contextService.getContextValue("NUM_FAILED_ITEMS") >> 1
        when:
        nscsContextService.updateItemsStatsForSyncCommand(3, 4, 2, 1)
        then:
        nscsContextService.getUserIdContextValue() == null
        and:
        nscsContextService.getSourceIpAddrContextValue() == null
        and:
        nscsContextService.getSessionIdContextValue() == null
        and:
        nscsContextService.getCommandTextContextValue() == null
        and:
        nscsContextService.getNumValidItemsContextValue() == 3
        and:
        nscsContextService.getNumSkippedItemsContextValue() == null
        and:
        nscsContextService.getNumInvalidItemsContextValue() == 4
        and:
        nscsContextService.getNumSuccessItemsContextValue() == 2
        and:
        nscsContextService.getNumFailedItemsContextValue() == 1
        and:
        nscsContextService.getJobIdContextValue() == null
        and:
        nscsContextService.getProxyAccountNameContextValue() == null
    }

    def 'init items stats for sync command'() {
        given:
        contextService.getContextValue("NUM_VALID_ITEMS") >> 3
        contextService.getContextValue("NUM_INVALID_ITEMS") >> 4
        when:
        nscsContextService.initItemsStatsForSyncCommand(3, 4)
        then:
        nscsContextService.getUserIdContextValue() == null
        and:
        nscsContextService.getSourceIpAddrContextValue() == null
        and:
        nscsContextService.getSessionIdContextValue() == null
        and:
        nscsContextService.getCommandTextContextValue() == null
        and:
        nscsContextService.getNumValidItemsContextValue() == 3
        and:
        nscsContextService.getNumSkippedItemsContextValue() == null
        and:
        nscsContextService.getNumInvalidItemsContextValue() == 4
        and:
        nscsContextService.getNumSuccessItemsContextValue() == null
        and:
        nscsContextService.getNumFailedItemsContextValue() == null
        and:
        nscsContextService.getJobIdContextValue() == null
        and:
        nscsContextService.getProxyAccountNameContextValue() == null
    }

    def 'update items result stats for sync command'() {
        given:
        contextService.getContextValue("NUM_SUCCESS_ITEMS") >> 2
        contextService.getContextValue("NUM_FAILED_ITEMS") >> 1
        when:
        nscsContextService.updateItemsResultStatsForSyncCommand(2, 1)
        then:
        nscsContextService.getUserIdContextValue() == null
        and:
        nscsContextService.getSourceIpAddrContextValue() == null
        and:
        nscsContextService.getSessionIdContextValue() == null
        and:
        nscsContextService.getCommandTextContextValue() == null
        and:
        nscsContextService.getNumValidItemsContextValue() == null
        and:
        nscsContextService.getNumSkippedItemsContextValue() == null
        and:
        nscsContextService.getNumInvalidItemsContextValue() == null
        and:
        nscsContextService.getNumSuccessItemsContextValue() == 2
        and:
        nscsContextService.getNumFailedItemsContextValue() == 1
        and:
        nscsContextService.getJobIdContextValue() == null
        and:
        nscsContextService.getProxyAccountNameContextValue() == null
    }

    def 'init items stats for async command'() {
        given:
        contextService.getContextValue("NUM_VALID_ITEMS") >> 3
        contextService.getContextValue("NUM_INVALID_ITEMS") >> 4
        when:
        nscsContextService.initItemsStatsForAsyncCommand(3, 4)
        then:
        nscsContextService.getUserIdContextValue() == null
        and:
        nscsContextService.getSourceIpAddrContextValue() == null
        and:
        nscsContextService.getSessionIdContextValue() == null
        and:
        nscsContextService.getCommandTextContextValue() == null
        and:
        nscsContextService.getNumValidItemsContextValue() == 3
        and:
        nscsContextService.getNumSkippedItemsContextValue() == null
        and:
        nscsContextService.getNumInvalidItemsContextValue() == 4
        and:
        nscsContextService.getNumSuccessItemsContextValue() == null
        and:
        nscsContextService.getNumFailedItemsContextValue() == null
        and:
        nscsContextService.getJobIdContextValue() == null
        and:
        nscsContextService.getProxyAccountNameContextValue() == null
    }
}
