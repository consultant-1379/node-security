/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2021
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
import com.ericsson.oss.itpf.sdk.recording.CommandPhase
import com.ericsson.oss.itpf.sdk.recording.ErrorSeverity
import com.ericsson.oss.itpf.sdk.recording.EventLevel
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder
import com.ericsson.oss.services.dto.JobStatusRecord
import com.ericsson.oss.services.dto.WfResult
import com.ericsson.oss.services.enums.JobGlobalStatusEnum
import com.ericsson.oss.services.security.nscs.command.CommandHandlerStatsFormatter

import spock.lang.Shared
import spock.lang.Unroll

class NscsSystemRecorderTest extends CdiSpecification {

    @ObjectUnderTest
    NscsSystemRecorder nscsSystemRecorder

    @MockedImplementation
    SystemRecorder systemRecorder

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
    }

    def "object under test injection" () {
        expect:
        nscsSystemRecorder != null
    }

    @Unroll
    def "record Job Cache Inserted Event events=#numEvents errors=#numErrors" () {
        given:
        when:
        nscsSystemRecorder.recordJobCacheInsertedEvent(getClass().getSimpleName(), jobId, job)
        then:
        numEvents * nscsSystemRecorder.systemRecorder.recordEvent("JOB_CACHE.INSERTED", EventLevel.DETAILED, "NSCS.NscsSystemRecorderTest","JOB_CACHE_ID_" + jobId, _)
        numErrors * nscsSystemRecorder.systemRecorder.recordError("JOB_CACHE.NOT_EXISTING", ErrorSeverity.ERROR, "NSCS.NscsSystemRecorderTest","JOB_CACHE_ID_" + jobId, _ as String)
        0 * nscsSystemRecorder.systemRecorder.recordEventData(_ as String, _ as Map)
        where:
        job << [
            null,
            jobRecord,
            jobRecordWithoutCalParams
        ]
        numEvents << [0, 1, 1]
        numErrors << [1, 0, 0]
    }

    @Unroll
    def "record Job Cache Updated On Wf Insertion Event events=#numEvents errors=#numErrors" () {
        given:
        when:
        nscsSystemRecorder.recordJobCacheUpdatedOnWfInsertionEvent(getClass().getSimpleName(), jobId, job, 1)
        then:
        numEvents * nscsSystemRecorder.systemRecorder.recordEvent("JOB_CACHE.UPDATED_ON_WF_INSERTION", EventLevel.DETAILED, "NSCS.NscsSystemRecorderTest","JOB_CACHE_ID_" + jobId, _)
        numErrors * nscsSystemRecorder.systemRecorder.recordError("JOB_CACHE.NOT_EXISTING", ErrorSeverity.ERROR, "NSCS.NscsSystemRecorderTest","JOB_CACHE_ID_" + jobId, _ as String)
        0 * nscsSystemRecorder.systemRecorder.recordEventData(_ as String, _ as Map)
        where:
        job << [
            null,
            jobRecord,
            jobRecordWithoutCalParams
        ]
        numEvents << [0, 1, 1]
        numErrors << [1, 0, 0]
    }

    @Unroll
    def "record Job Cache Started Event events=#numEvents errors=#numErrors" () {
        given:
        when:
        nscsSystemRecorder.recordJobCacheStartedEvent(getClass().getSimpleName(), jobId, job)
        then:
        numEvents * nscsSystemRecorder.systemRecorder.recordEvent("JOB_CACHE.STARTED", EventLevel.DETAILED, "NSCS.NscsSystemRecorderTest","JOB_CACHE_ID_" + jobId, _)
        numErrors * nscsSystemRecorder.systemRecorder.recordError("JOB_CACHE.NOT_EXISTING", ErrorSeverity.ERROR, "NSCS.NscsSystemRecorderTest","JOB_CACHE_ID_" + jobId, _ as String)
        0 * nscsSystemRecorder.systemRecorder.recordEventData(_ as String, _ as Map)
        where:
        job << [
            null,
            jobRecord,
            jobRecordWithoutCalParams
        ]
        numEvents << [0, 1, 1]
        numErrors << [1, 0, 0]
    }

    @Unroll
    def "record Job Cache Completed Event job status #status" () {
        given:
        jobRecord.setGlobalStatus(status)
        jobRecordWithoutCalParams.setGlobalStatus(status)
        when:
        nscsSystemRecorder.recordJobCacheCompletedEvent(getClass().getSimpleName(), jobId, job, [])
        then:
        numEvents * nscsSystemRecorder.systemRecorder.recordEvent("JOB_CACHE." + event, EventLevel.DETAILED, "NSCS.NscsSystemRecorderTest","JOB_CACHE_ID_" + jobId, _)
        numErrors * nscsSystemRecorder.systemRecorder.recordError("JOB_CACHE.NOT_EXISTING", ErrorSeverity.ERROR, "NSCS.NscsSystemRecorderTest","JOB_CACHE_ID_" + jobId, _ as String)
        numEventData * nscsSystemRecorder.systemRecorder.recordEventData(_ as String, _ as Map)
        where:
        status << [
            JobGlobalStatusEnum.RUNNING,
            JobGlobalStatusEnum.RUNNING,
            JobGlobalStatusEnum.COMPLETED,
            JobGlobalStatusEnum.RUNNING,
            JobGlobalStatusEnum.COMPLETED
        ]
        job << [
            null,
            jobRecord,
            jobRecord,
            jobRecordWithoutCalParams,
            jobRecordWithoutCalParams
        ]
        event << [
            "UPDATED_ON_WF_COMPLETION",
            "UPDATED_ON_WF_COMPLETION",
            "COMPLETED",
            "UPDATED_ON_WF_COMPLETION",
            "COMPLETED"
        ]
        numEvents << [0, 1, 1, 1, 1]
        numErrors << [1, 0, 0, 0, 0]
        numEventData << [0, 0, 1, 0, 1]
    }

    def "record Job Cache Not Existing Error" () {
        given:
        when:
        nscsSystemRecorder.recordJobCacheNotExistingError(getClass().getSimpleName(), jobId, "error message")
        then:
        1 * nscsSystemRecorder.systemRecorder.recordError("JOB_CACHE.NOT_EXISTING", ErrorSeverity.ERROR, "NSCS.NscsSystemRecorderTest","JOB_CACHE_ID_" + jobId, "error message")
        0 * nscsSystemRecorder.systemRecorder.recordEventData(_ as String, _ as Map)
    }

    def "record Job Cache Wrong Wf Counter Error" () {
        given:
        when:
        nscsSystemRecorder.recordJobCacheWrongWfCounterError(getClass().getSimpleName(), jobId, "error message")
        then:
        1 * nscsSystemRecorder.systemRecorder.recordError("JOB_CACHE.WRONG_WF_COUNTER", ErrorSeverity.ERROR, "NSCS.NscsSystemRecorderTest","JOB_CACHE_ID_" + jobId, "error message")
        0 * nscsSystemRecorder.systemRecorder.recordEventData(_ as String, _ as Map)
    }

    def "record Job Cache Generic Error" () {
        given:
        when:
        nscsSystemRecorder.recordJobCacheGenericError(getClass().getSimpleName(), jobId, "error message")
        then:
        1 * nscsSystemRecorder.systemRecorder.recordError("JOB_CACHE.GENERIC", ErrorSeverity.ERROR, "NSCS.NscsSystemRecorderTest","JOB_CACHE_ID_" + jobId, "error message")
        0 * nscsSystemRecorder.systemRecorder.recordEventData(_ as String, _ as Map)
    }

    @Unroll
    def "record Workflow Cache Updated Event events=#numEvents errors=#numErrors" () {
        given:
        when:
        nscsSystemRecorder.recordWorkflowCacheUpdatedEvent(getClass().getSimpleName(), wfWakeId, wf)
        then:
        numEvents * nscsSystemRecorder.systemRecorder.recordEvent("WORKFLOW_CACHE.UPDATED", EventLevel.DETAILED, "NSCS.NscsSystemRecorderTest","WORKFLOW_CACHE_ID_" + wfWakeId, _)
        numErrors * nscsSystemRecorder.systemRecorder.recordError("WORKFLOW_CACHE.NOT_EXISTING", ErrorSeverity.ERROR, "NSCS.NscsSystemRecorderTest","WORKFLOW_CACHE_ID_" + wfWakeId, _ as String)
        0 * nscsSystemRecorder.systemRecorder.recordEventData(_ as String, _ as Map)
        where:
        wf << [null, wfResult]
        numEvents << [0, 1]
        numErrors << [1, 0]
    }

    def "record Workflow Cache Updated On Wf Insertion" () {
        given:
        when:
        nscsSystemRecorder.recordWorkflowCacheUpdatedOnWfInsertionEvent(getClass().getSimpleName(), 1)
        then:
        1 * nscsSystemRecorder.systemRecorder.recordEvent("WORKFLOW_CACHE.UPDATED_ON_WF_INSERTION", EventLevel.DETAILED, "NSCS.NscsSystemRecorderTest","WORKFLOW_CACHE", _)
        0 * nscsSystemRecorder.systemRecorder.recordEventData(_ as String, _ as Map)
    }

    def "record Workflow Cache Not Existing Error" () {
        given:
        when:
        nscsSystemRecorder.recordWorkflowCacheNotExistingError(getClass().getSimpleName(), wfWakeId, "error message")
        then:
        1 * nscsSystemRecorder.systemRecorder.recordError("WORKFLOW_CACHE.NOT_EXISTING", ErrorSeverity.ERROR, "NSCS.NscsSystemRecorderTest","WORKFLOW_CACHE_ID_" + wfWakeId, "error message")
        0 * nscsSystemRecorder.systemRecorder.recordEventData(_ as String, _ as Map)
    }

    def "record Workflow Cache Not Yet Existing Notice" () {
        given:
        when:
        nscsSystemRecorder.recordWorkflowCacheNotYetExistingNotice(getClass().getSimpleName(), wfWakeId, "notice message")
        then:
        1 * nscsSystemRecorder.systemRecorder.recordError("WORKFLOW_CACHE.NOT_EXISTING", ErrorSeverity.NOTICE, "NSCS.NscsSystemRecorderTest","WORKFLOW_CACHE_ID_" + wfWakeId, "notice message")
        0 * nscsSystemRecorder.systemRecorder.recordEventData(_ as String, _ as Map)
    }

    @Unroll
    def "get Job Cache Resource jobID=#id" () {
        given:
        when:
        def resource = nscsSystemRecorder.getJobCacheResource(id)
        then:
        resource == jobResource
        where:
        id << [null, jobId]
        jobResource << [
            "JOB_CACHE",
            "JOB_CACHE_ID_" + jobId.toString()
        ]
    }

    def 'record command'() {
        given:
        when:
        nscsSystemRecorder.recordCommand("command name", CommandPhase.STARTED, "source", "resource", "additional info")
        then:
        1 * nscsSystemRecorder.systemRecorder.recordCommand("command name", CommandPhase.STARTED, "source", "resource", "additional info")
    }

    def 'record error'() {
        given:
        when:
        nscsSystemRecorder.recordError("error ID", ErrorSeverity.INFORMATIONAL, "source", "resource", "additional info")
        then:
        1 * nscsSystemRecorder.systemRecorder.recordError("error ID", ErrorSeverity.INFORMATIONAL, "source", "resource", "additional info")
    }

    def 'record security event'() {
        given:
        when:
        nscsSystemRecorder.recordSecurityEvent("target", "distinguished name", "additional info", "event type", ErrorSeverity.INFORMATIONAL, "event status")
        then:
        1 * nscsSystemRecorder.systemRecorder.recordSecurityEvent("target", "distinguished name", "additional info", "event type", ErrorSeverity.INFORMATIONAL, "event status")
    }

    def 'record command handler completed'() {
        given:
        def commandHandlerStatsFormatter = new CommandHandlerStatsFormatter()
        and:
        commandHandlerStatsFormatter.setCommandId("COMMAND_IDENTIFIER")
        when:
        nscsSystemRecorder.recordCommandHandlerCompletedEvent(commandHandlerStatsFormatter)
        then:
        1 * nscsSystemRecorder.systemRecorder.recordEventData("NODE_SECURITY.COMMAND_HANDLER_COMPLETED", _ as Map)
    }

    def 'record command handler completed without command identifier'() {
        given:
        def commandHandlerStatsFormatter = new CommandHandlerStatsFormatter()
        when:
        nscsSystemRecorder.recordCommandHandlerCompletedEvent(commandHandlerStatsFormatter)
        then:
        0 * nscsSystemRecorder.systemRecorder.recordEventData("NODE_SECURITY.COMMAND_HANDLER_COMPLETED", _ as Map)
    }

    @Unroll
    def 'record compact audit log for #commandphase'() {
        given:
        when:
        nscsSystemRecorder.recordCompactAudit('user', 'CLI: secadm command', commandphase, 'resource', '1.2.3.4', 'session-id', 'additional-info')
        then:
        1 * nscsSystemRecorder.systemRecorder.recordCompactAudit('user', 'CLI: secadm command', commandphase, 'Node Security', 'resource', '1.2.3.4', 'session-id', 'additional-info')
        where:
        commandphase << [
            CommandPhase.FINISHED_WITH_SUCCESS,
            CommandPhase.FINISHED_WITH_ERROR
        ]
    }
}
