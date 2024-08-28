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
package com.ericsson.nms.security.nscs.workflow.task.cpp.moaction

import javax.inject.Inject

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.WorkflowHandler
import com.ericsson.nms.security.nscs.api.model.NodeRef
import com.ericsson.nms.security.nscs.api.model.NodeReference
import com.ericsson.nms.security.nscs.logger.NscsLogger
import com.ericsson.nms.security.nscs.timer.IntervalJobAction.JobActionParameters
import com.ericsson.nms.security.nscs.workflow.task.cpp.moaction.TestDoSomethingTaskHandler.DoSomethingIntervalJob
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.TestDoSomethingTask

import spock.lang.Unroll

class TestDoSomethingTaskHandlerTest extends CdiSpecification {

    @ObjectUnderTest
    TestDoSomethingTaskHandler testDoSomethingTaskHandler

    @MockedImplementation
    WorkflowHandler workflowHandler

    @Inject
    NscsLogger nscsLogger

    @Unroll
    def "process task with result '#actionResult'" () {
        given:
        def fdn = "TEST_NODE"
        def task = new TestDoSomethingTask(fdn, actionResult)
        when:
        testDoSomethingTaskHandler.processTask(task)
        then:
        noExceptionThrown()
        where:
        actionResult << [
            "SUCCESS",
            "FAILURE",
            "TIMEOUT"
        ]
    }

    @Unroll
    def "process task with result '#actionResult' throwing exception '#exception'" () {
        given:
        def fdn = "TEST_NODE"
        def task = new TestDoSomethingTask(fdn, actionResult)
        when:
        testDoSomethingTaskHandler.processTask(task)
        then:
        thrown(exception)
        where:
        actionResult || exception
        "UNEXPECTED" || UnexpectedErrorException
        ""           || UnexpectedErrorException
        null         || UnexpectedErrorException
    }

    @Unroll
    def "do interval job with result '#actionResult'" () {
        given:
        def fdn = "TEST_NODE"
        def task = new TestDoSomethingTask(fdn, actionResult)
        def NodeReference nodeRef = new NodeRef(fdn)
        def DoSomethingIntervalJob doSomethingIntervalJob = new DoSomethingIntervalJob(nodeRef, nscsLogger, task)
        final Map<JobActionParameters, Object> params = new HashMap<>();
        params.put(JobActionParameters.WORKFLOW_HANDLER, workflowHandler);
        when:
        def result = doSomethingIntervalJob.doAction(params)
        then:
        noExceptionThrown()
        and:
        result == expected
        where:
        actionResult || expected
        "SUCCESS"    || true
        "FAILURE"    || true
        "TIMEOUT"    || false
    }
}
