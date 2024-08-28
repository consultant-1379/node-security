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
package com.ericsson.oss.services.nscs.jobs

import java.util.concurrent.locks.Lock

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.WorkflowHandler
import com.ericsson.nms.security.nscs.api.command.NscsCommandType
import com.ericsson.oss.itpf.sdk.cluster.lock.LockManager
import com.ericsson.oss.services.dto.JobStatusRecord
import com.ericsson.oss.services.dto.WfResult
import com.ericsson.oss.services.enums.JobGlobalStatusEnum
import com.ericsson.oss.services.enums.WfStatusEnum
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler
import com.ericsson.oss.services.security.nscs.context.NscsContextService

import spock.lang.Unroll

class NscsJobCacheHandlerImplSpecTest extends CdiSpecification {

    @ObjectUnderTest
    NscsJobCacheHandlerImpl nscsJobCacheHandlerImpl

    @MockedImplementation
    NscsJobCacheHandler nscsJobCacheHandler

    @MockedImplementation
    WorkflowHandler workflowHandler

    @MockedImplementation
    LockManager jobManagementCacheLockManager

    @MockedImplementation
    private NscsContextService nscsContextService

    def "object under test injection" () {
        expect:
        nscsJobCacheHandlerImpl != null
    }

    def "insert job" () {
        given:
        when:
        def record = nscsJobCacheHandlerImpl.insertJob(NscsCommandType.GET_JOB)
        then:
        noExceptionThrown()
        and:
        record != null
    }

    @Unroll
    def "update workflow with null job" () {
        given:
        def jobId = UUID.fromString("11ed9ac1-49ce-40dc-bab0-3f347092da6c")
        def String wfResultUUIDString = jobId.toString() + "1"
        def UUID wfWakeId = UUID.nameUUIDFromBytes(wfResultUUIDString.getBytes())
        and:
        def WfResult result = new WfResult()
        result.setJobId(jobId)
        result.setWfWakeId(wfWakeId)
        result.setStatus(status)
        and:
        def lock = Mock(Lock)
        jobManagementCacheLockManager.getDistributedLock(_) >> lock
        when:
        nscsJobCacheHandlerImpl.updateWorkflow(result)
        then:
        0 * nscsJobCacheHandlerImpl.jobManagementCache.put(_)
        where:
        status << [
            WfStatusEnum.RUNNING,
            WfStatusEnum.ERROR,
            WfStatusEnum.SUCCESS
        ]
    }

    @Unroll
    def "update completed workflow with job with wrong wf counter" () {
        given:
        def JobStatusRecord record = nscsJobCacheHandlerImpl.insertJob(NscsCommandType.GET_JOB)
        def jobId = record.getJobId()
        def String wfResultUUIDString = jobId.toString() + "1"
        def UUID wfWakeId = UUID.nameUUIDFromBytes(wfResultUUIDString.getBytes())
        and:
        def WfResult result = new WfResult()
        result.setJobId(jobId)
        result.setWfWakeId(wfWakeId)
        result.setStatus(status)
        and:
        def lock = Mock(Lock)
        jobManagementCacheLockManager.getDistributedLock(_) >> lock
        when:
        nscsJobCacheHandlerImpl.updateWorkflow(result)
        then:
        0 * nscsJobCacheHandlerImpl.jobManagementCache.put(_)
        where:
        status << [
            WfStatusEnum.ERROR,
            WfStatusEnum.SUCCESS
        ]
    }

    def "get pending and running workflows with not yet existent workflow" () {
        given:
        def JobStatusRecord record = nscsJobCacheHandlerImpl.insertJob(NscsCommandType.GET_JOB)
        record.setNumOfTotWf(1)
        record.setLastStartedWfId(1)
        when:
        def map = nscsJobCacheHandlerImpl.getRunningAndPendingWorkflows(250)
        then:
        noExceptionThrown()
        and:
        map["RUNNING"].isEmpty()
        and:
        map["PENDING"].isEmpty()
    }

    def "clear cache" () {
        given:
        when:
        nscsJobCacheHandlerImpl.clearCache()
        then:
        noExceptionThrown()
    }

    def "remove null job" () {
        given:
        def jobId = UUID.fromString("11ed9ac1-49ce-40dc-bab0-3f347092da6c")
        when:
        nscsJobCacheHandlerImpl.removeJob(jobId)
        then:
        noExceptionThrown()
        and:
        0 * workflowHandler.cancelWorkflowInstance(_)
        0 * nscsJobCacheHandlerImpl.wfManagementCache.remove(_)
    }

    def "remove job with no workflows" () {
        given:
        def JobStatusRecord record = nscsJobCacheHandlerImpl.insertJob(NscsCommandType.GET_JOB)
        when:
        nscsJobCacheHandlerImpl.removeJob(record.getJobId())
        then:
        noExceptionThrown()
        and:
        0 * workflowHandler.cancelWorkflowInstance(_)
    }

    def "insert null workflow batch" () {
        given:
        def lock = Mock(Lock)
        jobManagementCacheLockManager.getDistributedLock(_) >> lock
        def JobStatusRecord record = nscsJobCacheHandlerImpl.insertJob(NscsCommandType.GET_JOB)
        when:
        nscsJobCacheHandlerImpl.insertWorkflowBatch(null)
        then:
        noExceptionThrown()
    }

    def "insert empty workflow batch" () {
        given:
        def lock = Mock(Lock)
        jobManagementCacheLockManager.getDistributedLock(_) >> lock
        def JobStatusRecord record = nscsJobCacheHandlerImpl.insertJob(NscsCommandType.GET_JOB)
        when:
        nscsJobCacheHandlerImpl.insertWorkflowBatch([:])
        then:
        noExceptionThrown()
    }

    def "insert workflow batch with one workflow with invalid items at job insertion and batch insertion" () {
        given:
        def lock = Mock(Lock)
        jobManagementCacheLockManager.getDistributedLock(_) >> lock
        nscsContextService.getNumInvalidItemsContextValue() >> 2
        def JobStatusRecord record = nscsJobCacheHandlerImpl.insertJob(NscsCommandType.GET_JOB)
        and:
        def WfResult result = new WfResult()
        def UUID jobId = record.getJobId()
        def String wfResultUUIDString = jobId.toString() + "1"
        def UUID wfWakeId = UUID.nameUUIDFromBytes(wfResultUUIDString.getBytes())
        result.setWfId("1234")
        result.setWfWakeId(wfWakeId)
        result.setJobId(jobId)
        result.setStatus(WfStatusEnum.PENDING)
        final Map<UUID, WfResult> map = new HashMap<UUID, WfResult>()
        map.put(result.getWfWakeId(), result)
        when:
        nscsJobCacheHandlerImpl.insertWorkflowBatch(map)
        then:
        noExceptionThrown()
    }

    def "insert workflow batch with one workflow without invalid items at job insertion and batch insertion" () {
        given:
        def lock = Mock(Lock)
        jobManagementCacheLockManager.getDistributedLock(_) >> lock
        def JobStatusRecord record = nscsJobCacheHandlerImpl.insertJob(NscsCommandType.GET_JOB)
        and:
        def WfResult result = new WfResult()
        def UUID jobId = record.getJobId()
        def String wfResultUUIDString = jobId.toString() + "1"
        def UUID wfWakeId = UUID.nameUUIDFromBytes(wfResultUUIDString.getBytes())
        result.setWfId("1234")
        result.setWfWakeId(wfWakeId)
        result.setJobId(jobId)
        result.setStatus(WfStatusEnum.PENDING)
        final Map<UUID, WfResult> map = new HashMap<UUID, WfResult>()
        map.put(result.getWfWakeId(), result)
        and:
        nscsContextService.getNumInvalidItemsContextValue() >> 2
        when:
        nscsJobCacheHandlerImpl.insertWorkflowBatch(map)
        then:
        noExceptionThrown()
    }

    def "insert workflow batch with one workflow without invalid items at job insertion and with invalid items at batch insertion" () {
        given:
        def lock = Mock(Lock)
        jobManagementCacheLockManager.getDistributedLock(_) >> lock
        def JobStatusRecord record = nscsJobCacheHandlerImpl.insertJob(NscsCommandType.GET_JOB)
        and:
        def WfResult result = new WfResult()
        def UUID jobId = record.getJobId()
        def String wfResultUUIDString = jobId.toString() + "1"
        def UUID wfWakeId = UUID.nameUUIDFromBytes(wfResultUUIDString.getBytes())
        result.setWfId("1234")
        result.setWfWakeId(wfWakeId)
        result.setJobId(jobId)
        result.setStatus(WfStatusEnum.PENDING)
        final Map<UUID, WfResult> map = new HashMap<UUID, WfResult>()
        map.put(result.getWfWakeId(), result)
        when:
        nscsJobCacheHandlerImpl.insertWorkflowBatch(map)
        then:
        noExceptionThrown()
    }

    @Unroll
    def "remove job with one workflow in status #status" () {
        given:
        def lock = Mock(Lock)
        jobManagementCacheLockManager.getDistributedLock(_) >> lock
        def JobStatusRecord record = nscsJobCacheHandlerImpl.insertJob(NscsCommandType.GET_JOB)
        and:
        def WfResult result = new WfResult()
        def UUID jobId = record.getJobId()
        def String wfResultUUIDString = jobId.toString() + "1"
        def UUID wfWakeId = UUID.nameUUIDFromBytes(wfResultUUIDString.getBytes())
        result.setWfId("1234")
        result.setWfWakeId(wfWakeId)
        result.setJobId(jobId)
        if (status == WfStatusEnum.ERROR || status == WfStatusEnum.SUCCESS) {
            // set start date
            result.setStatus(WfStatusEnum.RUNNING)
        }
        result.setStatus(status)
        final Map<UUID, WfResult> map = new HashMap<UUID, WfResult>()
        map.put(result.getWfWakeId(), result)
        nscsJobCacheHandlerImpl.insertWorkflowBatch(map)
        nscsJobCacheHandlerImpl.updateWorkflow(result)
        when:
        nscsJobCacheHandlerImpl.removeJob(record.getJobId())
        then:
        noExceptionThrown()
        and:
        numOfCancel * workflowHandler.cancelWorkflowInstance(_)
        where:
        status << [
            WfStatusEnum.PENDING,
            WfStatusEnum.RUNNING,
            WfStatusEnum.ERROR,
            WfStatusEnum.SUCCESS
        ]
        numOfCancel << [0, 1, 0, 0]
    }

    @Unroll
    def "remove job with one running workflow with old-style workflow ID #wfId" () {
        given:
        def lock = Mock(Lock)
        jobManagementCacheLockManager.getDistributedLock(_) >> lock
        def JobStatusRecord record = nscsJobCacheHandlerImpl.insertJob(NscsCommandType.GET_JOB)
        and:
        def WfResult result = new WfResult()
        def UUID jobId = record.getJobId()
        def String wfResultUUIDString = jobId.toString() + "1"
        def UUID wfWakeId = UUID.nameUUIDFromBytes(wfResultUUIDString.getBytes())
        result.setWfId(wfId)
        result.setWfWakeId(wfWakeId)
        result.setJobId(jobId)
        result.setStatus(WfStatusEnum.RUNNING)
        final Map<UUID, WfResult> map = new HashMap<UUID, WfResult>()
        map.put(result.getWfWakeId(), result)
        nscsJobCacheHandlerImpl.insertWorkflowBatch(map)
        nscsJobCacheHandlerImpl.updateWorkflow(result)
        when:
        nscsJobCacheHandlerImpl.removeJob(record.getJobId())
        then:
        noExceptionThrown()
        and:
        0 * workflowHandler.cancelWorkflowInstance(_)
        where:
        wfId << [
            null,
            "N/A"
        ]
    }

    def "remove job with one running workflow and with exception on cancel" () {
        given:
        def lock = Mock(Lock)
        jobManagementCacheLockManager.getDistributedLock(_) >> lock
        def JobStatusRecord record = nscsJobCacheHandlerImpl.insertJob(NscsCommandType.GET_JOB)
        and:
        def WfResult result = new WfResult()
        def UUID jobId = record.getJobId()
        def String wfResultUUIDString = jobId.toString() + "1"
        def UUID wfWakeId = UUID.nameUUIDFromBytes(wfResultUUIDString.getBytes())
        result.setWfId("1234");
        result.setWfWakeId(wfWakeId);
        result.setJobId(jobId);
        result.setStatus(WfStatusEnum.RUNNING)
        final Map<UUID, WfResult> map = new HashMap<UUID, WfResult>();
        map.put(result.getWfWakeId(), result);
        nscsJobCacheHandlerImpl.insertWorkflowBatch(map);
        nscsJobCacheHandlerImpl.updateWorkflow(result);
        and:
        workflowHandler.cancelWorkflowInstance(_ as String) >> {String wfId -> throw new Exception()}
        when:
        nscsJobCacheHandlerImpl.removeJob(record.getJobId())
        then:
        noExceptionThrown()
    }

    def "abort null job" () {
        given:
        def jobId = UUID.fromString("11ed9ac1-49ce-40dc-bab0-3f347092da6c")
        when:
        def job = nscsJobCacheHandlerImpl.abortJob(jobId)
        then:
        noExceptionThrown()
        and:
        0 * workflowHandler.cancelWorkflowInstance(_)
        and:
        job == null
    }

    def "abort job already completed" () {
        given:
        def JobStatusRecord record = nscsJobCacheHandlerImpl.insertJob(NscsCommandType.GET_JOB)
        record.setGlobalStatus(JobGlobalStatusEnum.COMPLETED)
        when:
        def job = nscsJobCacheHandlerImpl.abortJob(record.getJobId())
        then:
        noExceptionThrown()
        and:
        0 * workflowHandler.cancelWorkflowInstance(_)
        and:
        job != null
        job.getGlobalStatus() == JobGlobalStatusEnum.COMPLETED
    }

    def "abort job with no workflows" () {
        given:
        def JobStatusRecord record = nscsJobCacheHandlerImpl.insertJob(NscsCommandType.GET_JOB)
        when:
        def job = nscsJobCacheHandlerImpl.abortJob(record.getJobId())
        then:
        noExceptionThrown()
        and:
        0 * workflowHandler.cancelWorkflowInstance(_)
        and:
        job != null
        job.getGlobalStatus() == JobGlobalStatusEnum.PENDING
    }

    @Unroll
    def "abort job with one workflow in status #status" () {
        given:
        def lock = Mock(Lock)
        jobManagementCacheLockManager.getDistributedLock(_) >> lock
        def JobStatusRecord record = nscsJobCacheHandlerImpl.insertJob(NscsCommandType.GET_JOB)
        and:
        def WfResult result = new WfResult()
        def UUID jobId = record.getJobId()
        def String wfResultUUIDString = jobId.toString() + "1"
        def UUID wfWakeId = UUID.nameUUIDFromBytes(wfResultUUIDString.getBytes())
        result.setWfId("1234")
        result.setWfWakeId(wfWakeId)
        result.setJobId(jobId)
        if (status == WfStatusEnum.ERROR || status == WfStatusEnum.SUCCESS) {
            // set start date
            result.setStatus(WfStatusEnum.RUNNING)
        }
        result.setStatus(status)
        final Map<UUID, WfResult> map = new HashMap<UUID, WfResult>()
        map.put(result.getWfWakeId(), result)
        nscsJobCacheHandlerImpl.insertWorkflowBatch(map)
        nscsJobCacheHandlerImpl.updateWorkflow(result)
        when:
        def job = nscsJobCacheHandlerImpl.abortJob(record.getJobId())
        then:
        noExceptionThrown()
        and:
        numOfCancel * workflowHandler.cancelWorkflowInstance(_)
        and:
        job != null
        job.getGlobalStatus() == JobGlobalStatusEnum.COMPLETED
        where:
        status << [
            WfStatusEnum.PENDING,
            WfStatusEnum.RUNNING,
            WfStatusEnum.ERROR,
            WfStatusEnum.SUCCESS
        ]
        numOfCancel << [1, 1, 0, 0]
    }

    @Unroll
    def "abort job with one running workflow with old-style workflow ID #wfId" () {
        given:
        def lock = Mock(Lock)
        jobManagementCacheLockManager.getDistributedLock(_) >> lock
        def JobStatusRecord record = nscsJobCacheHandlerImpl.insertJob(NscsCommandType.GET_JOB)
        and:
        def WfResult result = new WfResult()
        def UUID jobId = record.getJobId()
        def String wfResultUUIDString = jobId.toString() + "1"
        def UUID wfWakeId = UUID.nameUUIDFromBytes(wfResultUUIDString.getBytes())
        result.setWfId(wfId)
        result.setWfWakeId(wfWakeId)
        result.setJobId(jobId)
        result.setStatus(WfStatusEnum.RUNNING)
        final Map<UUID, WfResult> map = new HashMap<UUID, WfResult>()
        map.put(result.getWfWakeId(), result)
        nscsJobCacheHandlerImpl.insertWorkflowBatch(map)
        nscsJobCacheHandlerImpl.updateWorkflow(result)
        when:
        def job = nscsJobCacheHandlerImpl.abortJob(record.getJobId())
        then:
        noExceptionThrown()
        and:
        0 * workflowHandler.cancelWorkflowInstance(_)
        and:
        job != null
        job.getGlobalStatus() == JobGlobalStatusEnum.COMPLETED
        where:
        wfId << [
            null,
            "N/A"
        ]
    }

    def "abort job with one running workflow and with exception on cancel" () {
        given:
        def lock = Mock(Lock)
        jobManagementCacheLockManager.getDistributedLock(_) >> lock
        def JobStatusRecord record = nscsJobCacheHandlerImpl.insertJob(NscsCommandType.GET_JOB)
        and:
        def WfResult result = new WfResult()
        def UUID jobId = record.getJobId()
        def String wfResultUUIDString = jobId.toString() + "1"
        def UUID wfWakeId = UUID.nameUUIDFromBytes(wfResultUUIDString.getBytes())
        result.setWfId("1234");
        result.setWfWakeId(wfWakeId);
        result.setJobId(jobId);
        result.setStatus(WfStatusEnum.RUNNING)
        final Map<UUID, WfResult> map = new HashMap<UUID, WfResult>();
        map.put(result.getWfWakeId(), result);
        nscsJobCacheHandlerImpl.insertWorkflowBatch(map);
        nscsJobCacheHandlerImpl.updateWorkflow(result);
        and:
        workflowHandler.cancelWorkflowInstance(_ as String) >> {String wfId -> throw new Exception()}
        when:
        def job = nscsJobCacheHandlerImpl.abortJob(record.getJobId())
        then:
        noExceptionThrown()
        and:
        job != null
        job.getGlobalStatus() == JobGlobalStatusEnum.COMPLETED
    }
}
