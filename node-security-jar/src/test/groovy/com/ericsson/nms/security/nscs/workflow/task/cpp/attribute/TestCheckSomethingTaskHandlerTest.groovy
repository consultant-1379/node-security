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
package com.ericsson.nms.security.nscs.workflow.task.cpp.attribute

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskFailureException
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskTimeoutException
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.TestCheckSomethingTask

import spock.lang.Unroll

class TestCheckSomethingTaskHandlerTest extends CdiSpecification {

    @ObjectUnderTest
    TestCheckSomethingTaskHandler testCheckSomethingTaskHandler

    @Unroll
    def "process task with result '#checkResult'" () {
        given:
        def fdn = "TEST_NODE"
        def task = new TestCheckSomethingTask(fdn, checkResult)
        when:
        def result = testCheckSomethingTaskHandler.processTask(task)
        then:
        noExceptionThrown()
        and:
        result == checkResult
        where:
        checkResult << [
            "CHECK_OK",
            "CHECK_NOK",
            "OTHER",
            "",
            null
        ]
    }

    @Unroll
    def "process task with result '#checkResult' throwing exception '#exception'" () {
        given:
        def fdn = "TEST_NODE"
        def task = new TestCheckSomethingTask(fdn, checkResult)
        when:
        def result = testCheckSomethingTaskHandler.processTask(task)
        then:
        thrown(exception)
        where:
        checkResult     || exception
        "THROW_TIMEOUT" || WorkflowTaskTimeoutException
        "THROW_FAILURE" || WorkflowTaskFailureException
        "THROW_ERROR"   || UnexpectedErrorException
    }
}
