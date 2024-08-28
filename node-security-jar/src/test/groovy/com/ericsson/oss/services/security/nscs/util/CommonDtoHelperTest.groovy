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
package com.ericsson.oss.services.security.nscs.util

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.exception.NscsBadRequestException

import spock.lang.Unroll

class CommonDtoHelperTest extends CdiSpecification {

    def "convert null node list" () {
        given:
        when:
        CommonDtoHelper.fromNodeListDto(null)
        then:
        thrown(NscsBadRequestException)
    }

    @Unroll
    def "convert valid node list #nodes" () {
        given:
        when:
        def nodeList = CommonDtoHelper.fromNodeListDto(nodes)
        then:
        nodeList.containsAll(expectedNodeList)
        and:
        notThrown(Exception)
        where:
        nodes << [
            "node1",
            "node2&NetworkElement=node1",
            "NetworkElement=node2"
        ]
        expectedNodeList << [
            ["node1"],
            [
                "node2",
                "NetworkElement=node1"
            ],
            [
                "NetworkElement=node2"]
        ]
    }

    @Unroll
    def "convert invalid node list #nodes" () {
        given:
        when:
        CommonDtoHelper.fromNodeListDto(nodes)
        then:
        thrown(NscsBadRequestException)
        where:
        nodes << [
            null,
            ""
        ]
    }
}
