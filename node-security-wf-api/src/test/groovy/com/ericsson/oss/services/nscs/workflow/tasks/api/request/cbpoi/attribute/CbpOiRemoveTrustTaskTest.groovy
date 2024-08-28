/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.cbpoi.attribute

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType

import spock.lang.Unroll

class CbpOiRemoveTrustTaskTest extends CdiSpecification {

    def "no-args constructor" () {
        given:
        when: "instantiating using no-arg constructor"
        def CbpOiRemoveTrustTask task = new CbpOiRemoveTrustTask()
        then: "task should not be null"
        task != null
        and: "task type should be CBP_OI_REMOVE_TRUST"
        task.getTaskType() == WorkflowTaskType.CBP_OI_REMOVE_TRUST
        and: "node FDN should be null"
        task.getNodeFdn() == null
        and: "short description should be the task short description"
        task.getShortDescription() == CbpOiRemoveTrustTask.SHORT_DESCRIPTION
        and: "output parameters should be null"
        task.getOutputParams() == null
    }

    @Unroll
    def "workflow name constructor" () {
        given:
        when: "instantiating using fdn constructor"
        def CbpOiRemoveTrustTask task = new CbpOiRemoveTrustTask(name)
        then: "task should not be null"
        task != null
        and: "task type should be CBP_OI_REMOVE_TRUST"
        task.getTaskType() == WorkflowTaskType.CBP_OI_REMOVE_TRUST
        and: "short description should be the task short description"
        task.getShortDescription() == CbpOiRemoveTrustTask.SHORT_DESCRIPTION
        and: "output parameters should be null"
        task.getOutputParams() == null
        where:
        name   | expected
        null  | null
        ""    | ""
        "workflowname" | "workflowname"
    }

    @Unroll
    def "set output parameters" () {
        given: "an instance of task"
        def CbpOiRemoveTrustTask task = new CbpOiRemoveTrustTask()
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
