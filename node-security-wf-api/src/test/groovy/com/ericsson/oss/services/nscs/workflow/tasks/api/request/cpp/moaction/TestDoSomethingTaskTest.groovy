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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType

import spock.lang.Unroll

class TestDoSomethingTaskTest extends CdiSpecification {

    def "no-args constructor" () {
        given:
        when: "instantiating using no-arg constructor"
        def TestDoSomethingTask task = new TestDoSomethingTask()
        then: "task should not be null"
        task != null
        and: "task type should be TEST_DO_SOMETHING"
        task.getTaskType() == WorkflowTaskType.TEST_DO_SOMETHING
        and: "node FDN should be null"
        task.getNodeFdn() == null
        and: "short description should be the task short description"
        task.getShortDescription() == TestDoSomethingTask.SHORT_DESCRIPTION
        and: "action result should be SUCCESS"
        task.getActionResult() == "SUCCESS"
    }

    @Unroll
    def "fdn and action result '#actionResult' constructor" () {
        given:
        when: "instantiating using fdn and action result constructor"
        def fdn = "TEST_NODE"
        def TestDoSomethingTask task = new TestDoSomethingTask(fdn, actionResult)
        then: "task should not be null"
        task != null
        and: "task type should be TEST_DO_SOMETHING"
        task.getTaskType() == WorkflowTaskType.TEST_DO_SOMETHING
        and: "node FDN should be fdn"
        task.getNodeFdn() == "TEST_NODE"
        and: "short description should be the task short description"
        task.getShortDescription() == TestDoSomethingTask.SHORT_DESCRIPTION
        and: "action result should be '#actionResult'"
        task.getActionResult() == actionResult
        where:
        actionResult << [
            "SUCCESS",
            "TIMEOUT",
            "FAILURE",
            "OTHER",
            "",
            null
        ]
    }

    @Unroll
    def "set action result to '#actionResult'" () {
        given:
        when: "instantiating using no-args constructor"
        def TestDoSomethingTask task = new TestDoSomethingTask()
        task.setActionResult(actionResult)
        then: "task should not be null"
        task != null
        and: "task type should be TEST_DO_SOMETHING"
        task.getTaskType() == WorkflowTaskType.TEST_DO_SOMETHING
        and: "node FDN should be null"
        task.getNodeFdn() == null
        and: "short description should be the task short description"
        task.getShortDescription() == TestDoSomethingTask.SHORT_DESCRIPTION
        and: "action result should be '#actionResult'"
        task.getActionResult() == actionResult
        where:
        actionResult << [
            "SUCCESS",
            "TIMEOUT",
            "FAILURE",
            "OTHER",
            "",
            null
        ]
    }
}
