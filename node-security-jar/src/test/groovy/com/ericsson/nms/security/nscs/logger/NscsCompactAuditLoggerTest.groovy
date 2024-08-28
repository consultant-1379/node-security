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
package com.ericsson.nms.security.nscs.logger

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.handler.command.utility.PasswordHelper
import com.ericsson.oss.itpf.sdk.recording.CommandPhase
import com.ericsson.oss.services.dto.JobStatusRecord
import com.ericsson.oss.services.dto.WfResult
import com.ericsson.oss.services.enums.JobGlobalStatusEnum
import com.ericsson.oss.services.enums.WfStatusEnum
import com.ericsson.oss.services.security.nscs.context.NscsContextService

import spock.lang.Shared
import spock.lang.Unroll

class NscsCompactAuditLoggerTest extends CdiSpecification {

    @ObjectUnderTest
    NscsCompactAuditLogger nscsCompactAuditLogger

    @MockedImplementation
    private NscsContextService nscsContextService

    @MockedImplementation
    private PasswordHelper passwordHelper

    @MockedImplementation
    NscsSystemRecorder nscsSystemRecorder

    @Shared
    def jobId = UUID.fromString("11ed9ac1-49ce-40dc-bab0-3f347092da6c")
    String wfWakeIdString = jobId.toString() + 1
    def wfWakeId = UUID.nameUUIDFromBytes(wfWakeIdString.getBytes())

    @Shared
    def jobRecord = new JobStatusRecord();

    @Shared
    def jobRecordWithoutCalParams = new JobStatusRecord();

    @Shared
    def wfResult = new WfResult()

    @Shared
    nodeCacheRestPostCompactAuditLogged = [
        "/node-security/2.0/nodes"
    ]

    @Shared
    nodeRestPostCompactAuditLogged = [
        "/node-security/nodes/seclevel"
    ]

    @Shared
    unsupportedRestNotCompactAuditLogged = [
        "/node-security/nodes/notlogged"
    ]

    def setup() {
        jobRecord.setCommandId("command");
        jobRecord.setGlobalStatus(JobGlobalStatusEnum.PENDING);
        jobRecord.setUserId("user");
        jobRecord.setJobId(jobId);
        jobRecord.setInsertDate(new Date());
        jobRecord.setCommandName("secadm command")
        jobRecord.setSessionId("session-id")
        jobRecord.setSourceIP("1.2.3.4")
        jobRecord.setNumOfInvalid(2)
        jobRecordWithoutCalParams.setCommandId("command");
        jobRecordWithoutCalParams.setGlobalStatus(JobGlobalStatusEnum.PENDING);
        jobRecordWithoutCalParams.setUserId("user");
        jobRecordWithoutCalParams.setJobId(jobId);
        jobRecordWithoutCalParams.setInsertDate(new Date());
        passwordHelper.encryptEncode(_) >> "session-id"
        passwordHelper.decryptDecode(_) >> "session-id"
    }

    def 'object under test' () {
        expect:
        nscsCompactAuditLogger != null
    }

    def 'record compact audit logged sync node command finished with success'() {
        given:
        nscsContextService.getUserIdContextValue() >> 'user'
        nscsContextService.getSessionIdContextValue() >> 'session-id'
        nscsContextService.getSourceIpAddrContextValue() >> '1.2.3.4'
        nscsContextService.getCommandTypeContextValue() >> "CREATE_CREDENTIALS"
        nscsContextService.getNumValidItemsContextValue() >> 3
        nscsContextService.getNumSuccessItemsContextValue() >> 3
        nscsContextService.getNumFailedItemsContextValue() >> 0
        nscsContextService.getNumInvalidItemsContextValue() >> 0
        when:
        nscsCompactAuditLogger.recordCommandFinishedWithSuccessCompactAudit("secadm ", "command")
        then:
        1 * nscsCompactAuditLogger.nscsSystemRecorder.recordCompactAudit('user', 'CLI: secadm command', CommandPhase.FINISHED_WITH_SUCCESS, 'Node', '1.2.3.4', 'session-id', _ as String)
    }

    def 'record compact audit logged sync get proxy account command finished with success'() {
        given:
        nscsContextService.getUserIdContextValue() >> 'user'
        nscsContextService.getSessionIdContextValue() >> 'session-id'
        nscsContextService.getSourceIpAddrContextValue() >> '1.2.3.4'
        nscsContextService.getCommandTypeContextValue() >> "LDAP_PROXY_GET"
        nscsContextService.getNumValidItemsContextValue() >> 3
        nscsContextService.getNumSuccessItemsContextValue() >> 3
        nscsContextService.getNumFailedItemsContextValue() >> 0
        nscsContextService.getNumInvalidItemsContextValue() >> 0
        when:
        nscsCompactAuditLogger.recordCommandFinishedWithSuccessCompactAudit("secadm ", "command")
        then:
        1 * nscsCompactAuditLogger.nscsSystemRecorder.recordCompactAudit('user', 'CLI: secadm command', CommandPhase.FINISHED_WITH_SUCCESS, 'Proxy Account', '1.2.3.4', 'session-id', '')
    }

    def 'record compact audit logged sync get job command finished with success'() {
        given:
        nscsContextService.getUserIdContextValue() >> 'user'
        nscsContextService.getSessionIdContextValue() >> 'session-id'
        nscsContextService.getSourceIpAddrContextValue() >> '1.2.3.4'
        nscsContextService.getCommandTypeContextValue() >> "GET_JOB"
        nscsContextService.getNumValidItemsContextValue() >> 3
        nscsContextService.getNumSuccessItemsContextValue() >> 3
        nscsContextService.getNumFailedItemsContextValue() >> 0
        nscsContextService.getNumInvalidItemsContextValue() >> 0
        when:
        nscsCompactAuditLogger.recordCommandFinishedWithSuccessCompactAudit("secadm ", "command")
        then:
        1 * nscsCompactAuditLogger.nscsSystemRecorder.recordCompactAudit('user', 'CLI: secadm command', CommandPhase.FINISHED_WITH_SUCCESS, 'Job', '1.2.3.4', 'session-id', '')
    }

    def 'record compact audit logged sync capability command finished with success with cmdType #cmdtype'() {
        given:
        nscsContextService.getUserIdContextValue() >> 'user'
        nscsContextService.getSessionIdContextValue() >> 'session-id'
        nscsContextService.getSourceIpAddrContextValue() >> '1.2.3.4'
        nscsContextService.getCommandTypeContextValue() >> "CAPABILITY_GET"
        nscsContextService.getNumValidItemsContextValue() >> 3
        nscsContextService.getNumSuccessItemsContextValue() >> 3
        nscsContextService.getNumFailedItemsContextValue() >> 0
        nscsContextService.getNumInvalidItemsContextValue() >> 0
        when:
        nscsCompactAuditLogger.recordCommandFinishedWithSuccessCompactAudit("secadm ", "command")
        then:
        1 * nscsCompactAuditLogger.nscsSystemRecorder.recordCompactAudit('user', 'CLI: secadm command', CommandPhase.FINISHED_WITH_SUCCESS, 'Capability', '1.2.3.4', 'session-id', '')
    }

    def 'record compact audit logged sync unknown command finished with success'() {
        given:
        nscsContextService.getUserIdContextValue() >> 'user'
        nscsContextService.getSessionIdContextValue() >> 'session-id'
        nscsContextService.getSourceIpAddrContextValue() >> '1.2.3.4'
        nscsContextService.getCommandTypeContextValue() >> "TEST_COMMAND"
        nscsContextService.getNumValidItemsContextValue() >> 3
        nscsContextService.getNumSuccessItemsContextValue() >> 3
        nscsContextService.getNumFailedItemsContextValue() >> 0
        nscsContextService.getNumInvalidItemsContextValue() >> 0
        when:
        nscsCompactAuditLogger.recordCommandFinishedWithSuccessCompactAudit("secadm ", "command")
        then:
        1 * nscsCompactAuditLogger.nscsSystemRecorder.recordCompactAudit('user', 'CLI: secadm command', CommandPhase.FINISHED_WITH_SUCCESS, 'Unknown', '1.2.3.4', 'session-id', '')
    }

    @Unroll
    def 'record compact audit logged forced sync command finished with success with cmdType #cmdtype'() {
        given:
        nscsContextService.getUserIdContextValue() >> 'user'
        nscsContextService.getSessionIdContextValue() >> 'session-id'
        nscsContextService.getSourceIpAddrContextValue() >> '1.2.3.4'
        nscsContextService.getCommandTypeContextValue() >> cmdtype
        nscsContextService.getNumValidItemsContextValue() >> 3
        nscsContextService.getNumSuccessItemsContextValue() >> 3
        nscsContextService.getNumFailedItemsContextValue() >> 0
        nscsContextService.getNumInvalidItemsContextValue() >> 0
        when:
        nscsCompactAuditLogger.recordCommandFinishedWithSuccessCompactAudit("secadm ", "command --force")
        then:
        1 * nscsCompactAuditLogger.nscsSystemRecorder.recordCompactAudit('user', 'CLI: secadm command --force', CommandPhase.FINISHED_WITH_SUCCESS, 'Proxy Account', '1.2.3.4', 'session-id', _ as String)
        where:
        cmdtype << [
            "LDAP_PROXY_SET",
            "LDAP_PROXY_DELETE"
        ]
    }

    @Unroll
    def 'record compact audit logged not forced sync command finished with success with cmdType #cmdtype'() {
        given:
        nscsContextService.getUserIdContextValue() >> 'user'
        nscsContextService.getSessionIdContextValue() >> 'session-id'
        nscsContextService.getSourceIpAddrContextValue() >> '1.2.3.4'
        nscsContextService.getCommandTypeContextValue() >> cmdtype
        nscsContextService.getNumValidItemsContextValue() >> 3
        nscsContextService.getNumSuccessItemsContextValue() >> 3
        nscsContextService.getNumFailedItemsContextValue() >> 0
        nscsContextService.getNumInvalidItemsContextValue() >> 0
        when:
        nscsCompactAuditLogger.recordCommandFinishedWithSuccessCompactAudit("secadm ", "command")
        then:
        0 * nscsCompactAuditLogger.nscsSystemRecorder.recordCompactAudit(_ as String, _ as String, _ as CommandPhase, _ as String, _ as String, _ as String, _ as String)
        where:
        cmdtype << [
            "LDAP_PROXY_SET",
            "LDAP_PROXY_DELETE"
        ]
    }

    @Unroll
    def 'record compact audit logged forced async command finished with success with no valid and no invalid with cmdType #cmdtype'() {
        given:
        nscsContextService.getUserIdContextValue() >> 'user'
        nscsContextService.getSessionIdContextValue() >> 'session-id'
        nscsContextService.getSourceIpAddrContextValue() >> '1.2.3.4'
        nscsContextService.getCommandTypeContextValue() >> cmdtype
        nscsContextService.getNumValidItemsContextValue() >> 0
        nscsContextService.getNumSuccessItemsContextValue() >> 3
        nscsContextService.getNumFailedItemsContextValue() >> 0
        nscsContextService.getNumInvalidItemsContextValue() >> 0
        when:
        nscsCompactAuditLogger.recordCommandFinishedWithSuccessCompactAudit("secadm ", "command --force")
        then:
        1 * nscsCompactAuditLogger.nscsSystemRecorder.recordCompactAudit('user', 'CLI: secadm command --force', CommandPhase.FINISHED_WITH_ERROR, 'Job', '1.2.3.4', 'session-id', _ as String)
        where:
        cmdtype << [
            "LDAP_RENEW"
        ]
    }

    def 'record compact audit logged sync not bulk ldap configure manual command finished with success'() {
        given:
        nscsContextService.getUserIdContextValue() >> 'user'
        nscsContextService.getSessionIdContextValue() >> 'session-id'
        nscsContextService.getSourceIpAddrContextValue() >> '1.2.3.4'
        nscsContextService.getCommandTypeContextValue() >> "LDAP_CONFIGURATION"
        nscsContextService.getProxyAccountNameContextValue() >> "ProxyAccount_5c31e018-1c46-4f59-8b69-a892581b25a0"
        when:
        nscsCompactAuditLogger.recordCommandFinishedWithSuccessCompactAudit("secadm ", "ldap configure --manual")
        then:
        1 * nscsCompactAuditLogger.nscsSystemRecorder.recordCompactAudit('user', 'CLI: secadm ldap configure --manual', CommandPhase.FINISHED_WITH_SUCCESS, 'Proxy Account', '1.2.3.4', 'session-id', _ as String)
    }

    def 'record compact audit logged sync not bulk ldap configure manual command finished with success without proxy account name'() {
        given:
        nscsContextService.getUserIdContextValue() >> 'user'
        nscsContextService.getSessionIdContextValue() >> 'session-id'
        nscsContextService.getSourceIpAddrContextValue() >> '1.2.3.4'
        nscsContextService.getCommandTypeContextValue() >> "LDAP_CONFIGURATION"
        nscsContextService.getProxyAccountNameContextValue() >> null
        when:
        nscsCompactAuditLogger.recordCommandFinishedWithSuccessCompactAudit("secadm ", "ldap configure --manual")
        then:
        1 * nscsCompactAuditLogger.nscsSystemRecorder.recordCompactAudit('user', 'CLI: secadm ldap configure --manual', CommandPhase.FINISHED_WITH_SUCCESS, 'Proxy Account', '1.2.3.4', 'session-id', 'null')
    }

    @Unroll
    def 'record compact audit logged not forced async command finished with success with cmdType #cmdtype'() {
        given:
        nscsContextService.getUserIdContextValue() >> 'user'
        nscsContextService.getSessionIdContextValue() >> 'session-id'
        nscsContextService.getSourceIpAddrContextValue() >> '1.2.3.4'
        nscsContextService.getCommandTypeContextValue() >> cmdtype
        nscsContextService.getNumValidItemsContextValue() >> 0
        nscsContextService.getNumSuccessItemsContextValue() >> 3
        nscsContextService.getNumFailedItemsContextValue() >> 0
        nscsContextService.getNumInvalidItemsContextValue() >> 0
        nscsContextService.getJobIdContextValue() >> null
        when:
        nscsCompactAuditLogger.recordCommandFinishedWithSuccessCompactAudit("secadm ", "command")
        then:
        0 * nscsCompactAuditLogger.nscsSystemRecorder.recordCompactAudit(_ as String, _ as String, _ as CommandPhase, _ as String, _ as String, _ as String, _ as String)
        where:
        cmdtype << [
            "LDAP_RENEW"
        ]
    }

    @Unroll
    def 'record compact audit logged sync command finished with success with v #valid i #invalid s #success f #failed'() {
        given:
        nscsContextService.getUserIdContextValue() >> 'user'
        nscsContextService.getSessionIdContextValue() >> 'session-id'
        nscsContextService.getSourceIpAddrContextValue() >> '1.2.3.4'
        nscsContextService.getCommandTypeContextValue() >> "CREATE_CREDENTIALS"
        nscsContextService.getNumValidItemsContextValue() >> valid
        nscsContextService.getNumSuccessItemsContextValue() >> success
        nscsContextService.getNumFailedItemsContextValue() >> failed
        nscsContextService.getNumInvalidItemsContextValue() >> invalid
        nscsContextService.getJobIdContextValue() >> null
        when:
        nscsCompactAuditLogger.recordCommandFinishedWithSuccessCompactAudit("secadm ", "command")
        then:
        1 * nscsCompactAuditLogger.nscsSystemRecorder.recordCompactAudit('user', 'CLI: secadm command', _ as CommandPhase, 'Node', '1.2.3.4','session-id', _ as String)
        where:
        success << [
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            1,
            1,
            1,
            1,
            1,
            1,
            1,
            1
        ]
        failed << [
            null,
            null,
            null,
            null,
            1,
            1,
            1,
            1,
            null,
            null,
            null,
            null,
            1,
            1,
            1,
            1
        ]
        invalid << [
            null,
            null,
            1,
            1,
            null,
            null,
            1,
            1,
            null,
            null,
            1,
            1,
            null,
            null,
            1,
            1
        ]
        valid << [
            null,
            1,
            null,
            1,
            null,
            1,
            null,
            1,
            null,
            1,
            null,
            1,
            null,
            1,
            null,
            1
        ]
    }
    //
    def 'record compact audit logged async command finished with success with valid null'() {
        given:
        nscsContextService.getUserIdContextValue() >> 'user'
        nscsContextService.getSessionIdContextValue() >> 'session-id'
        nscsContextService.getSourceIpAddrContextValue() >> '1.2.3.4'
        nscsContextService.getCommandTypeContextValue() >> "CERTIFICATE_ISSUE"
        nscsContextService.getNumValidItemsContextValue() >> null
        nscsContextService.getNumSuccessItemsContextValue() >> null
        nscsContextService.getNumFailedItemsContextValue() >> null
        nscsContextService.getNumInvalidItemsContextValue() >> 1
        nscsContextService.getJobIdContextValue() >> UUID.fromString("11ed9ac1-49ce-40dc-bab0-3f347092da6c")
        when:
        nscsCompactAuditLogger.recordCommandFinishedWithSuccessCompactAudit("secadm ", "command")
        then:
        1 * nscsCompactAuditLogger.nscsSystemRecorder.recordCompactAudit('user', 'CLI: secadm command', CommandPhase.FINISHED_WITH_SUCCESS, 'Job', '1.2.3.4','session-id', _ as String)
    }

    @Unroll
    def 'record compact audit logged async command finished with success with valid #valid'() {
        given:
        nscsContextService.getUserIdContextValue() >> 'user'
        nscsContextService.getSessionIdContextValue() >> 'session-id'
        nscsContextService.getSourceIpAddrContextValue() >> '1.2.3.4'
        nscsContextService.getCommandTypeContextValue() >> "CERTIFICATE_ISSUE"
        nscsContextService.getNumValidItemsContextValue() >> valid
        nscsContextService.getNumSuccessItemsContextValue() >> null
        nscsContextService.getNumFailedItemsContextValue() >> null
        nscsContextService.getNumInvalidItemsContextValue() >> 1
        nscsContextService.getJobIdContextValue() >> UUID.fromString("11ed9ac1-49ce-40dc-bab0-3f347092da6c")
        when:
        nscsCompactAuditLogger.recordCommandFinishedWithSuccessCompactAudit("secadm ", "command")
        then:
        numcal * nscsCompactAuditLogger.nscsSystemRecorder.recordCompactAudit('user', 'CLI: secadm command', _ as CommandPhase, 'Job', '1.2.3.4','session-id', _ as String)
        where:
        valid << [
            -1,
            0,
            1
        ]
        numcal << [
            1,
            1,
            1
        ]
    }

    def 'record compact audit logged async command finished with success with valid and no job ID'() {
        given:
        nscsContextService.getUserIdContextValue() >> 'user'
        nscsContextService.getSessionIdContextValue() >> 'session-id'
        nscsContextService.getSourceIpAddrContextValue() >> '1.2.3.4'
        nscsContextService.getCommandTypeContextValue() >> "CERTIFICATE_ISSUE"
        nscsContextService.getNumValidItemsContextValue() >> 2
        nscsContextService.getNumSuccessItemsContextValue() >> null
        nscsContextService.getNumFailedItemsContextValue() >> null
        nscsContextService.getNumInvalidItemsContextValue() >> 1
        nscsContextService.getJobIdContextValue() >> null
        when:
        nscsCompactAuditLogger.recordCommandFinishedWithSuccessCompactAudit("secadm ", "command")
        then:
        1 * nscsCompactAuditLogger.nscsSystemRecorder.recordCompactAudit('user', 'CLI: secadm command', CommandPhase.FINISHED_WITH_SUCCESS, 'Job', '1.2.3.4','session-id', _ as String)
    }

    def 'record compact audit logged command finished with success without cmdType'() {
        given:
        nscsContextService.getUserIdContextValue() >> 'user'
        nscsContextService.getSessionIdContextValue() >> 'session-id'
        nscsContextService.getSourceIpAddrContextValue() >> '1.2.3.4'
        nscsContextService.getCommandTypeContextValue() >> null
        nscsContextService.getNumValidItemsContextValue() >> 3
        nscsContextService.getNumSuccessItemsContextValue() >> 3
        nscsContextService.getNumFailedItemsContextValue() >> 0
        nscsContextService.getNumInvalidItemsContextValue() >> 0
        when:
        nscsCompactAuditLogger.recordCommandFinishedWithSuccessCompactAudit("secadm ", "command")
        then:
        0 * nscsCompactAuditLogger.nscsSystemRecorder.recordCompactAudit(_ as String, _ as String, _ as CommandPhase, _ as String, _ as String, _ as String, _ as String)
    }

    def 'record not compact audit logged command finished with success'() {
        given:
        nscsContextService.getUserIdContextValue() >> 'user'
        nscsContextService.getSessionIdContextValue() >> 'session-id'
        nscsContextService.getSourceIpAddrContextValue() >> '1.2.3.4'
        nscsContextService.getCommandTypeContextValue() >> "SET_ENROLLMENT"
        nscsContextService.getNumValidItemsContextValue() >> 3
        nscsContextService.getNumSuccessItemsContextValue() >> 3
        nscsContextService.getNumFailedItemsContextValue() >> 0
        nscsContextService.getNumInvalidItemsContextValue() >> 0
        when:
        nscsCompactAuditLogger.recordCommandFinishedWithSuccessCompactAudit("secadm ", "command")
        then:
        0 * nscsCompactAuditLogger.nscsSystemRecorder.recordCompactAudit(_ as String, _ as String, _ as CommandPhase, _ as String, _ as String, _ as String, _ as String)
    }

    def 'record compact audit log command finished with error due to syntax error'() {
        given:
        nscsContextService.getUserIdContextValue() >> 'user'
        nscsContextService.getSessionIdContextValue() >> 'session-id'
        nscsContextService.getSourceIpAddrContextValue() >> '1.2.3.4'
        nscsContextService.getCommandTypeContextValue() >> null
        when:
        nscsCompactAuditLogger.recordCommandFinishedWithErrorCompactAudit("secadm", " command", "syntax error")
        then:
        1 * nscsCompactAuditLogger.nscsSystemRecorder.recordCompactAudit(_ as String, _ as String, _ as CommandPhase, _ as String, _ as String, _ as String, _ as String)
    }

    def 'record compact audit logged command finished with error'() {
        given:
        nscsContextService.getUserIdContextValue() >> 'user'
        nscsContextService.getSessionIdContextValue() >> 'session-id'
        nscsContextService.getSourceIpAddrContextValue() >> '1.2.3.4'
        nscsContextService.getCommandTypeContextValue() >> "CERTIFICATE_ISSUE"
        when:
        nscsCompactAuditLogger.recordCommandFinishedWithErrorCompactAudit("secadm", " certificate issue", "error message")
        then:
        1 * nscsCompactAuditLogger.nscsSystemRecorder.recordCompactAudit(_ as String, _ as String, _ as CommandPhase, _ as String, _ as String, _ as String, _ as String)
    }

    def 'record compact audit logged command finished with error with only invalid'() {
        given:
        nscsContextService.getUserIdContextValue() >> 'user'
        nscsContextService.getSessionIdContextValue() >> 'session-id'
        nscsContextService.getSourceIpAddrContextValue() >> '1.2.3.4'
        nscsContextService.getCommandTypeContextValue() >> "CERTIFICATE_ISSUE"
        nscsContextService.getNumValidItemsContextValue() >> 0
        nscsContextService.getNumSuccessItemsContextValue() >> 0
        nscsContextService.getNumFailedItemsContextValue() >> 0
        nscsContextService.getNumInvalidItemsContextValue() >> 3
        when:
        nscsCompactAuditLogger.recordCommandFinishedWithErrorCompactAudit("secadm", " certificate issue", "error message")
        then:
        1 * nscsCompactAuditLogger.nscsSystemRecorder.recordCompactAudit(_ as String, _ as String, _ as CommandPhase, _ as String, _ as String, _ as String, _ as String)
    }

    def 'record not compact audit logged command finished with error'() {
        given:
        nscsContextService.getUserIdContextValue() >> 'user'
        nscsContextService.getCommandTypeContextValue() >> "GET_CERT_ENROLL_STATE"
        when:
        nscsCompactAuditLogger.recordCommandFinishedWithErrorCompactAudit("secadm", " certificate get", "error message")
        then:
        0 * nscsCompactAuditLogger.nscsSystemRecorder.recordCompactAudit(_ as String, _ as String, _ as CommandPhase, _ as String, _ as String, _ as String, _ as String)
    }

    @Unroll
    def 'record compact audit logged command finished with error without CAL parameters in the context #userid #sessionid #sourceip'() {
        given:
        nscsContextService.getUserIdContextValue() >> userid
        nscsContextService.getSessionIdContextValue() >> sessionid
        nscsContextService.getSourceIpAddrContextValue() >> sourceip
        nscsContextService.getCommandTypeContextValue() >> "CERTIFICATE_ISSUE"
        when:
        nscsCompactAuditLogger.recordCommandFinishedWithErrorCompactAudit("secadm", " certificate issue", "error message")
        then:
        0 * nscsCompactAuditLogger.nscsSystemRecorder.recordCompactAudit(_ as String, _ as String, _ as CommandPhase, _ as String, _ as String, _ as String, _ as String)
        where:
        userid << [
            null,
            null,
            null,
            null,
            "user-id",
            "user-id",
            "user-id"
        ]
        sessionid << [
            null,
            null,
            "session-id",
            "session-id",
            null,
            null,
            "session-id"
        ]
        sourceip << [
            null,
            "1.2.3.4",
            null,
            "1.2.3.4",
            null,
            "1.2.3.4",
            null
        ]
    }

    def 'record null job compact audit log'() {
        given:
        when:
        nscsCompactAuditLogger.recordJobCacheCompletedCompactAudit(null, null)
        then:
        0 * nscsCompactAuditLogger.nscsSystemRecorder.recordCompactAudit(_ as String, _ as String, _ as CommandPhase,_ as String, _ as String, _ as String, _ as String)
    }

    def 'record job cache running compact audit log'() {
        given:
        jobRecord.setGlobalStatus(JobGlobalStatusEnum.RUNNING)
        when:
        nscsCompactAuditLogger.recordJobCacheCompletedCompactAudit(jobRecord, [])
        then:
        0 * nscsCompactAuditLogger.nscsSystemRecorder.recordCompactAudit(_ as String, _ as String, _ as CommandPhase, _ as String, _ as String, _ as String, _ as String)
    }

    def 'record job cache completed compact audit log'() {
        given:
        jobRecord.setCommandId("CERTIFICATE_ISSUE");
        jobRecord.setGlobalStatus(JobGlobalStatusEnum.COMPLETED)
        when:
        nscsCompactAuditLogger.recordJobCacheCompletedCompactAudit(jobRecord, [])
        then:
        1 * nscsCompactAuditLogger.nscsSystemRecorder.recordCompactAudit(_ as String, _ as String, _ as CommandPhase, _ as String, _ as String, _ as String, _ as String)
    }

    @Unroll
    def 'record job cache without CAL parameters completed compact audit log #userid #sessionid #sourceip #cmdtext'() {
        given:
        def record = new JobStatusRecord();
        record.setCommandId("CERTIFICATE_ISSUE");
        record.setGlobalStatus(JobGlobalStatusEnum.COMPLETED)
        record.setUserId(userid);
        record.setJobId(jobId);
        record.setInsertDate(new Date());
        record.setCommandName(cmdtext)
        record.setSessionId(sessionid)
        record.setSourceIP(sourceip)
        when:
        nscsCompactAuditLogger.recordJobCacheCompletedCompactAudit(record, [])
        then:
        0 * nscsCompactAuditLogger.nscsSystemRecorder.recordCompactAudit(_ as String, _ as String, _ as CommandPhase, _ as String, _ as String, _ as String, _ as String)
        where:
        cmdtext << [
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            "secadm certificate issue",
            "secadm certificate issue",
            "secadm certificate issue",
            "secadm certificate issue",
            "secadm certificate issue",
            "secadm certificate issue",
            "secadm certificate issue"
        ]
        userid << [
            null,
            null,
            null,
            null,
            "user-id",
            "user-id",
            "user-id",
            null,
            null,
            null,
            null,
            "user-id",
            "user-id",
            "user-id"
        ]
        sessionid << [
            null,
            null,
            "session-id",
            "session-id",
            null,
            null,
            "session-id",
            null,
            null,
            "session-id",
            "session-id",
            null,
            null,
            "session-id"
        ]
        sourceip << [
            null,
            "1.2.3.4",
            null,
            "1.2.3.4",
            null,
            "1.2.3.4",
            null,
            null,
            "1.2.3.4",
            null,
            "1.2.3.4",
            null,
            "1.2.3.4",
            null
        ]
    }

    @Unroll
    def 'record job cache completed compact audit log with #invalid invalid and 2 wf #status1 and #status2'() {
        given:
        def record = new JobStatusRecord();
        record.setCommandId("CERTIFICATE_ISSUE");
        record.setGlobalStatus(JobGlobalStatusEnum.COMPLETED)
        record.setUserId("user-id");
        record.setJobId(jobId);
        record.setInsertDate(new Date());
        record.setCommandName("secadm certificate issue")
        record.setSessionId("session-id")
        record.setSourceIP("1.2.3.4")
        record.setNumOfInvalid(invalid)
        record.setNumOfTotWf(2)
        and:
        def wf1 = new WfResult()
        wf1.setJobId(jobId)
        def wf1Id = UUID.fromString("11ed9ac1-49ce-40dc-bab0-3f347092da6d")
        wf1.setWfWakeId(wf1Id)
        wf1.setStatus(status1)
        def wf2 = new WfResult()
        wf2.setJobId(jobId)
        def wf2Id = UUID.fromString("11ed9ac1-49ce-40dc-bab0-3f347092da6e")
        wf2.setWfWakeId(wf2Id)
        wf2.setStatus(status2)
        when:
        nscsCompactAuditLogger.recordJobCacheCompletedCompactAudit(record, [wf1, wf2])
        then:
        1 * nscsCompactAuditLogger.nscsSystemRecorder.recordCompactAudit(_ as String, _ as String, _ as CommandPhase, _ as String, _ as String, _ as String, _ as String)
        where:
        invalid << [-1, -1, 0, 0, 1, 1]
        status1 << [
            WfStatusEnum.SUCCESS,
            WfStatusEnum.SUCCESS,
            WfStatusEnum.SUCCESS,
            WfStatusEnum.SUCCESS,
            WfStatusEnum.SUCCESS,
            WfStatusEnum.SUCCESS
        ]
        status2 << [
            WfStatusEnum.SUCCESS,
            WfStatusEnum.ERROR,
            WfStatusEnum.SUCCESS,
            WfStatusEnum.ERROR,
            WfStatusEnum.SUCCESS,
            WfStatusEnum.ERROR
        ]
    }

    def 'record job cache completed not compact audit logged'() {
        given:
        jobRecord.setCommandId("TEST_COMMAND");
        jobRecord.setGlobalStatus(JobGlobalStatusEnum.COMPLETED)
        when:
        nscsCompactAuditLogger.recordJobCacheCompletedCompactAudit(jobRecord, [])
        then:
        0 * nscsCompactAuditLogger.nscsSystemRecorder.recordCompactAudit(_ as String, _ as String, _ as CommandPhase, _ as String, _ as String, _ as String, _ as String)
    }

    @Unroll
    def 'record compact audit logged REST POST finished with success with url path #urlpath'() {
        given:
        nscsContextService.getUserIdContextValue() >> 'user'
        nscsContextService.getSessionIdContextValue() >> 'session-id'
        nscsContextService.getSourceIpAddrContextValue() >> '1.2.3.4'
        nscsContextService.getRestMethodContextValue() >> "POST"
        nscsContextService.getRestUrlFileContextValue() >> urlpath
        nscsContextService.getRestUrlPathContextValue() >> urlpath
        nscsContextService.getNumValidItemsContextValue() >> 3
        nscsContextService.getNumSuccessItemsContextValue() >> 3
        nscsContextService.getNumFailedItemsContextValue() >> 0
        nscsContextService.getNumInvalidItemsContextValue() >> 0
        when:
        nscsCompactAuditLogger.recordRestFinishedWithSuccessCompactAudit()
        then:
        1 * nscsCompactAuditLogger.nscsSystemRecorder.recordCompactAudit('user', _ as String, CommandPhase.FINISHED_WITH_SUCCESS, _ as String, '1.2.3.4', 'session-id', _ as String)
        where:
        urlpath << nodeCacheRestPostCompactAuditLogged + nodeRestPostCompactAuditLogged
    }

    @Unroll
    def 'record compact audit logged REST POST or GET finished with success v #valid i #invalid s #skipped s #success f #failed'() {
        given:
        nscsContextService.getUserIdContextValue() >> 'user'
        nscsContextService.getSessionIdContextValue() >> 'session-id'
        nscsContextService.getSourceIpAddrContextValue() >> '1.2.3.4'
        nscsContextService.getRestMethodContextValue() >> "POST"
        nscsContextService.getRestUrlFileContextValue() >> "/node-security/2.0/nodes"
        nscsContextService.getRestUrlPathContextValue() >> "/node-security/2.0/nodes"
        nscsContextService.getNumValidItemsContextValue() >> valid
        nscsContextService.getNumSuccessItemsContextValue() >> success
        nscsContextService.getNumFailedItemsContextValue() >> failed
        nscsContextService.getNumInvalidItemsContextValue() >> invalid
        nscsContextService.getNumSkippedItemsContextValue() >> skipped
        nscsContextService.getErrorDetailContextValue() >> null
        when:
        nscsCompactAuditLogger.recordRestFinishedWithSuccessCompactAudit()
        then:
        count * nscsCompactAuditLogger.nscsSystemRecorder.recordCompactAudit('user', _ as String, _ as CommandPhase, _ as String, '1.2.3.4', 'session-id', _ as String)
        where:
        valid << [null, null, null, 1, 0, 1, 0]
        invalid << [null, null, 1, null, 0, 0, 1]
        skipped << [null, 1, 0, 0, 1, 0, 0]
        success << [null, 0, 0, 1, 0, 1, 0]
        failed << [null, 0, 0, 0, 0, 0, 0]
        count << [0, 0, 1, 1, 0, 1, 1]
    }

    @Unroll
    def 'record compact audit logged REST POST finished with success path #urlpath and error detail'() {
        given:
        nscsContextService.getUserIdContextValue() >> 'user'
        nscsContextService.getSessionIdContextValue() >> 'session-id'
        nscsContextService.getSourceIpAddrContextValue() >> '1.2.3.4'
        nscsContextService.getRestMethodContextValue() >> "POST"
        nscsContextService.getRestUrlFileContextValue() >> urlpath
        nscsContextService.getRestUrlPathContextValue() >> urlpath
        nscsContextService.getErrorDetailContextValue() >> "error detail"
        when:
        nscsCompactAuditLogger.recordRestFinishedWithSuccessCompactAudit()
        then:
        1 * nscsCompactAuditLogger.nscsSystemRecorder.recordCompactAudit('user', _ as String, CommandPhase.FINISHED_WITH_ERROR, _ as String, '1.2.3.4', 'session-id', _ as String)
        where:
        urlpath << nodeCacheRestPostCompactAuditLogged + nodeRestPostCompactAuditLogged
    }

    @Unroll
    def 'record compact audit logged REST GET finished with success with url path #urlpath'() {
        given:
        nscsContextService.getUserIdContextValue() >> 'user'
        nscsContextService.getSessionIdContextValue() >> 'session-id'
        nscsContextService.getSourceIpAddrContextValue() >> '1.2.3.4'
        nscsContextService.getRestMethodContextValue() >> "GET"
        nscsContextService.getRestUrlFileContextValue() >> urlpath
        nscsContextService.getRestUrlPathContextValue() >> urlpath
        nscsContextService.getNumValidItemsContextValue() >> 3
        nscsContextService.getNumSuccessItemsContextValue() >> 3
        nscsContextService.getNumFailedItemsContextValue() >> 0
        nscsContextService.getNumInvalidItemsContextValue() >> 0
        when:
        nscsCompactAuditLogger.recordRestFinishedWithSuccessCompactAudit()
        then:
        0 * nscsCompactAuditLogger.nscsSystemRecorder.recordCompactAudit('user', _ as String, _ as CommandPhase, _ as String, '1.2.3.4', 'session-id', _ as String)
        where:
        urlpath << nodeCacheRestPostCompactAuditLogged + nodeRestPostCompactAuditLogged
    }

    def 'record compact audit logged REST finished with success without url path'() {
        given:
        nscsContextService.getRestUrlPathContextValue() >> null
        when:
        nscsCompactAuditLogger.recordRestFinishedWithSuccessCompactAudit()
        then:
        0 * nscsCompactAuditLogger.nscsSystemRecorder.recordCompactAudit(_ as String, _ as String, _ as CommandPhase, _ as String, _ as String, _ as String, _ as String)
    }

    @Unroll
    def 'record not compact audit logged REST POST finished with success path #urlpath'() {
        given:
        nscsContextService.getRestMethodContextValue() >> "POST"
        nscsContextService.getRestUrlPathContextValue() >> urlpath
        when:
        nscsCompactAuditLogger.recordRestFinishedWithSuccessCompactAudit()
        then:
        0 * nscsCompactAuditLogger.nscsSystemRecorder.recordCompactAudit(_ as String, _ as String, _ as CommandPhase, _ as String, _ as String, _ as String, _ as String)
        where:
        urlpath << unsupportedRestNotCompactAuditLogged
    }

    @Unroll
    def 'record compact audit logged REST POST finished with error path #urlpath'() {
        given:
        nscsContextService.getUserIdContextValue() >> 'user'
        nscsContextService.getSessionIdContextValue() >> 'session-id'
        nscsContextService.getSourceIpAddrContextValue() >> '1.2.3.4'
        nscsContextService.getRestMethodContextValue() >> "POST"
        nscsContextService.getRestUrlFileContextValue() >> urlpath
        nscsContextService.getRestUrlPathContextValue() >> urlpath
        nscsContextService.getErrorDetailContextValue() >> "error message"
        when:
        nscsCompactAuditLogger.recordRestFinishedWithErrorCompactAudit()
        then:
        1 * nscsCompactAuditLogger.nscsSystemRecorder.recordCompactAudit('user', _ as String, CommandPhase.FINISHED_WITH_ERROR, _ as String, '1.2.3.4', 'session-id', _ as String)
        where:
        urlpath << nodeCacheRestPostCompactAuditLogged + nodeRestPostCompactAuditLogged
    }

    def 'record compact audit logged REST POST finished with error without path'() {
        given:
        nscsContextService.getUserIdContextValue() >> 'user'
        nscsContextService.getSessionIdContextValue() >> 'session-id'
        nscsContextService.getSourceIpAddrContextValue() >> '1.2.3.4'
        nscsContextService.getRestMethodContextValue() >> "POST"
        nscsContextService.getErrorDetailContextValue() >> "error message"
        when:
        nscsCompactAuditLogger.recordRestFinishedWithErrorCompactAudit()
        then:
        0 * nscsCompactAuditLogger.nscsSystemRecorder.recordCompactAudit('user', _ as String, CommandPhase.FINISHED_WITH_ERROR, _ as String, '1.2.3.4', 'session-id', _ as String)
    }

    @Unroll
    def 'record not compact audit logged REST POST finished with error path #urlpath'() {
        given:
        nscsContextService.getRestMethodContextValue() >> "POST"
        nscsContextService.getRestUrlPathContextValue() >> urlpath
        when:
        nscsCompactAuditLogger.recordRestFinishedWithErrorCompactAudit()
        then:
        0 * nscsCompactAuditLogger.nscsSystemRecorder.recordCompactAudit(_ as String, _ as String, _ as CommandPhase, _ as String, _ as String, _ as String, _ as String)
        where:
        urlpath << unsupportedRestNotCompactAuditLogged
    }
}
