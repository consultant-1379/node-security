/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2022
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.workflow

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.exception.NscsBadRequestException

import spock.lang.Unroll

class NscsWorkflowManagerBeanTest extends CdiSpecification {

    @ObjectUnderTest
    NscsWorkflowManagerBean nscsWorkflowManagerBean

    @MockedImplementation
    NscsWorkflowService nscsWorkflowService

    def "object under test should not be null" () {
        expect:
        nscsWorkflowManagerBean != null
    }

    @Unroll
    def "get status for valid node list #nodes" () {
        given:
        when:
        nscsWorkflowManagerBean.getWorkflowsStatus(nodes)
        then:
        1 * nscsWorkflowService.getWorkflowsStatus(_)
        and:
        notThrown(Exception)
        where:
        nodes << [
            "node1",
            "node2&NetworkElement=node1",
            "NetworkElement=node2"
        ]
    }

    @Unroll
    def "get status for invalid node list #nodes" () {
        given:
        when:
        nscsWorkflowManagerBean.getWorkflowsStatus(nodes)
        then:
        0 * nscsWorkflowService.getWorkflowsStatus(_)
        and:
        thrown(NscsBadRequestException)
        where:
        nodes << [
            null,
            "",
            "NetworkElement=",
            "node2;NetworkElement=node1"
        ]
    }

    def "get workflows stats" () {
        given:
        when:
        nscsWorkflowManagerBean.getWorkflowsStats()
        then:
        1 * nscsWorkflowService.getWorkflowsStats()
        and:
        notThrown(Exception)
    }

    @Unroll
    def "delete valid workflow instance ID #id" () {
        given:
        when:
        def result = nscsWorkflowManagerBean.deleteWorkflowInstance(id)
        then:
        1 * nscsWorkflowService.deleteWorkflowInstance(id)
        and:
        notThrown(Exception)
        where:
        id << ["1234"]
    }

    @Unroll
    def "delete invalid workflow instance ID #id" () {
        given:
        when:
        nscsWorkflowManagerBean.deleteWorkflowInstance(id)
        then:
        thrown(NscsBadRequestException)
        where:
        id << [
            null,
            ""
        ]
    }
}
