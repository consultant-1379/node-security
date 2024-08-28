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
package com.ericsson.nms.security.nscs.workflow.scheduler

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.WorkflowHandler
import com.ericsson.nms.security.nscs.ejb.credential.MembershipListener
import com.ericsson.oss.services.dto.WfResult
import com.ericsson.oss.services.enums.WfStatusEnum
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler
import com.ericsson.oss.services.wfs.api.instance.WorkflowInstance

class WorkflowSchedulerProcessorTest extends CdiSpecification {

    @ObjectUnderTest
    WorkflowSchedulerProcessor workflowSchedulerProcessor

    @MockedImplementation
    MembershipListener membershipListener

    @MockedImplementation
    NscsJobCacheHandler nscsJobCacheHandler

    @MockedImplementation
    WorkflowHandler workflowHandler

    def "object under test injection" () {
        expect:
        workflowSchedulerProcessor != null
    }

    def "schedule update on slave" () {
        given:
        membershipListener.isMaster() >> false
        when:
        workflowSchedulerProcessor.scheduledUpdate()
        then:
        noExceptionThrown()
        and:
        0 * workflowSchedulerProcessor.cacheHandler.updateWorkflow(_)
    }

    def "schedule update on master with no workflows" () {
        given:
        membershipListener.isMaster() >> true
        and:
        nscsJobCacheHandler.getRunningAndPendingWorkflows(_) >> [
            "RUNNING" : [], "PENDING" : []]
        when:
        workflowSchedulerProcessor.scheduledUpdate()
        then:
        noExceptionThrown()
        and:
        0 * workflowSchedulerProcessor.cacheHandler.updateWorkflow(_)
    }

    def "schedule update on master with one pending workflow and successful dispatch" () {
        given:
        membershipListener.isMaster() >> true
        and:
        def WfResult result = new WfResult()
        def String jobId = "11ed9ac1-49ce-40dc-bab0-3f347092da6c"
        def String wfResultUUIDString = jobId + "1"
        def UUID wfWakeId = UUID.nameUUIDFromBytes(wfResultUUIDString.getBytes())
        result.setWfId("NA")
        result.setWfWakeId(wfWakeId)
        result.setJobId(UUID.fromString(jobId))
        result.setStatus(WfStatusEnum.PENDING)
        and:
        nscsJobCacheHandler.getRunningAndPendingWorkflows(_) >> [
            "RUNNING" : [], "PENDING" : [result]]
        and:
        def wfInst = new WorkflowInstance("defID", "instId", "busKey")
        workflowHandler.dispatch(_) >> wfInst
        when:
        workflowSchedulerProcessor.scheduledUpdate()
        then:
        noExceptionThrown()
        and:
        2 * workflowSchedulerProcessor.cacheHandler.updateWorkflow(_)
    }

    def "schedule update on master with one pending workflow and failed dispatch" () {
        given:
        membershipListener.isMaster() >> true
        and:
        def WfResult result = new WfResult()
        def String jobId = "11ed9ac1-49ce-40dc-bab0-3f347092da6c"
        def String wfResultUUIDString = jobId + "1"
        def UUID wfWakeId = UUID.nameUUIDFromBytes(wfResultUUIDString.getBytes())
        result.setWfId("NA")
        result.setWfWakeId(wfWakeId)
        result.setJobId(UUID.fromString(jobId))
        result.setStatus(WfStatusEnum.PENDING)
        and:
        nscsJobCacheHandler.getRunningAndPendingWorkflows(_) >> [
            "RUNNING" : [], "PENDING" : [result]]
        and:
        workflowHandler.dispatch(_) >> { _ ->
            throw new Exception()
        }
        when:
        workflowSchedulerProcessor.scheduledUpdate()
        then:
        noExceptionThrown()
        and:
        2 * workflowSchedulerProcessor.cacheHandler.updateWorkflow(_)
    }

    def "schedule update with unexpected exception" () {
        given:
        membershipListener.isMaster() >> { -> throw new Exception() }
        when:
        workflowSchedulerProcessor.scheduledUpdate()
        then:
        noExceptionThrown()
        and:
        0 * workflowSchedulerProcessor.cacheHandler.updateWorkflow(_)
    }
}
