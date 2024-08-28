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

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.exception.NscsBadRequestException
import com.ericsson.nms.security.nscs.api.model.NodeRef
import com.ericsson.nms.security.nscs.api.model.NodeReference

import spock.lang.Shared
import spock.lang.Unroll

class WorkflowDtoHelperTest extends CdiSpecification {

    @Shared
    NodeReference nodeReference1 = new NodeRef("node1")

    @Shared
    NodeReference nodeReference2 = new NodeRef("NetworkElement=node2")

    def "convert null node list" () {
        given:
        when:
        WorkflowDtoHelper.fromNodeListDto(null)
        then:
        thrown(NscsBadRequestException)
    }

    @Unroll
    def "convert valid node list #nodes" () {
        given:
        when:
        def nodeReferences = WorkflowDtoHelper.fromNodeListDto(nodes)
        then:
        nodeReferences == expectedNodeReferences
        and:
        notThrown(Exception)
        where:
        nodes << [
            "node1",
            "node2&NetworkElement=node1",
            "NetworkElement=node2"
        ]
        expectedNodeReferences << [
            [nodeReference1],
            [
                nodeReference2,
                nodeReference1
            ],
            [nodeReference2]
        ]
    }

    @Unroll
    def "convert invalid node list #nodes" () {
        given:
        when:
        WorkflowDtoHelper.fromNodeListDto(nodes)
        then:
        thrown(NscsBadRequestException)
        where:
        nodes << [
            null,
            "",
            "NetworkElement=",
            "node2;NetworkElement=node1"
        ]
    }

    @Unroll
    def "convert valid workflow instance ID #id" () {
        given:
        when:
        def instanceId = WorkflowDtoHelper.fromWorkflowInstanceIdDto(id)
        then:
        instanceId == expectedInstanceId
        and:
        notThrown(Exception)
        where:
        id << ["1234"]
        expectedInstanceId << ["1234"]
    }

    @Unroll
    def "convert invalid workflow instance ID #id" () {
        given:
        when:
        WorkflowDtoHelper.fromWorkflowInstanceIdDto(id)
        then:
        thrown(NscsBadRequestException)
        where:
        id << [
            null,
            ""
        ]
    }
}
