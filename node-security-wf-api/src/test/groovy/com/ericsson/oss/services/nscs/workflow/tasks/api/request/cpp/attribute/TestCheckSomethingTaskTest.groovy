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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType

import spock.lang.Unroll

class TestCheckSomethingTaskTest extends CdiSpecification {

    def "no-args constructor" () {
        given:
        when: "instantiating using no-arg constructor"
        def TestCheckSomethingTask task = new TestCheckSomethingTask()
        then: "task should not be null"
        task != null
        and: "task type should be TEST_CHECK_SOMETHING"
        task.getTaskType() == WorkflowTaskType.TEST_CHECK_SOMETHING
        and: "node FDN should be null"
        task.getNodeFdn() == null
        and: "short description should be the task short description"
        task.getShortDescription() == TestCheckSomethingTask.SHORT_DESCRIPTION
        and: "check result should be CHECK_OK"
        task.getCheckResult() == "CHECK_OK"
    }

    @Unroll
    def "fdn and check result '#checkResult' constructor" () {
        given:
        when: "instantiating using fdn and check result constructor"
        def fdn = "TEST_NODE"
        def TestCheckSomethingTask task = new TestCheckSomethingTask(fdn, checkResult)
        then: "task should not be null"
        task != null
        and: "task type should be TEST_CHECK_SOMETHING"
        task.getTaskType() == WorkflowTaskType.TEST_CHECK_SOMETHING
        and: "node FDN should be fdn"
        task.getNodeFdn() == "TEST_NODE"
        and: "short description should be the task short description"
        task.getShortDescription() == TestCheckSomethingTask.SHORT_DESCRIPTION
        and: "check result should be '#checkResult'"
        task.getCheckResult() == checkResult
        where:
        checkResult << [
            "CHECK_OK",
            "CHECK_NOK",
            "THROW_TIMEOUT",
            "THROW_FAILURE",
            "THROW_ERROR",
            "OTHER",
            "",
            null
        ]
    }

    @Unroll
    def "set check result to '#checkResult" () {
        given:
        when: "instantiating using no-args constructor"
        def fdn = "TEST_NODE"
        def TestCheckSomethingTask task = new TestCheckSomethingTask()
        task.setCheckResult(checkResult)
        then: "task should not be null"
        task != null
        and: "task type should be TEST_CHECK_SOMETHING"
        task.getTaskType() == WorkflowTaskType.TEST_CHECK_SOMETHING
        and: "node FDN should be null"
        task.getNodeFdn() == null
        and: "short description should be the task short description"
        task.getShortDescription() == TestCheckSomethingTask.SHORT_DESCRIPTION
        and: "check result should be '#checkResult'"
        task.getCheckResult() == checkResult
        where:
        checkResult << [
            "CHECK_OK",
            "CHECK_NOK",
            "THROW_TIMEOUT",
            "THROW_FAILURE",
            "THROW_ERROR",
            "OTHER",
            "",
            null
        ]
    }
}
