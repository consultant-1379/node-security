/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.attribute

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType

import spock.lang.Unroll

class PerformSyncMoActionTaskTest extends CdiSpecification {

    def "no-args constructor" () {
        given:
        when: "instantiating using no-arg constructor"
        def PerformSyncMoActionTask task = new PerformSyncMoActionTask()
        then: "task should not be null"
        task != null
        and: "task type should be PERFORM_SYNC_MO_ACTION"
        task.getTaskType() == WorkflowTaskType.PERFORM_SYNC_MO_ACTION
        and: "node FDN should be null"
        task.getNodeFdn() == null
        and: "short description should be the task short description"
        task.getShortDescription() == PerformSyncMoActionTask.SHORT_DESCRIPTION
        and: "output parameters should be null"
        task.getOutputParams() == null
    }

    @Unroll
    def "fdn constructor" () {
        given:
        when: "instantiating using fdn constructor"
        def PerformSyncMoActionTask task = new PerformSyncMoActionTask(fdn)
        then: "task should not be null"
        task != null
        and: "task type should be PERFORM_SYNC_MO_ACTION"
        task.getTaskType() == WorkflowTaskType.PERFORM_SYNC_MO_ACTION
        and: "node FDN should be #expected"
        task.getNodeFdn() == expected
        and: "short description should be the task short description"
        task.getShortDescription() == PerformSyncMoActionTask.SHORT_DESCRIPTION
        and: "output parameters should be null"
        task.getOutputParams() == null
        where:
        fdn   | expected
        null  | null
        ""    | ""
        "fdn" | "fdn"
    }

    @Unroll
    def "set output parameters" () {
        given: "an instance of task"
        def PerformSyncMoActionTask task = new PerformSyncMoActionTask()
        when: "setting the output parameters to #outputParams"
        task.setOutputParams(outputParams)
        then: "the read output parameters should be #expected"
        task.getOutputParams() == expected
        where:
        outputParams   | expected
        null           | null
        [:]            | [:]
        [par : "val"]  | [par : "val"]
    }
}
