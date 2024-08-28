/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2023
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.nscs.nodes

import javax.ejb.EJBException
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.exception.RbacException
import com.ericsson.oss.services.nodes.dto.NodeNamesDTO
import com.ericsson.oss.services.nscs.nodes.interfaces.NscsNodesListHandler
import com.ericsson.oss.services.security.nscs.context.NscsContextService

import spock.lang.Unroll

class NodesV2RestResourceTest extends CdiSpecification {

    @ObjectUnderTest
    NodesV2RestResource nodesV2RestResource

    @MockedImplementation
    NscsNodesListHandler nodesListHandler

    @MockedImplementation
    NodeNamesDTO nodeNamesDTO

    @MockedImplementation
    NscsContextService nscsContextService

    def 'object under test'() {
        expect:
        nodesV2RestResource != null
    }

    def 'fetch'() {
        given:
        nodesListHandler.getNodes(nodeNamesDTO) >> []
        when:
        Response response = nodesV2RestResource.fetch(nodeNamesDTO)
        then:
        noExceptionThrown()
        and:
        response.getStatusInfo().getStatusCode() == Status.OK.getStatusCode()
        and:
        0 * nodesV2RestResource.nscsContextService.setErrorDetailContextValue(_ as String)
    }

    def 'fetch with exception RBAC exception'() {
        given:
        nodesListHandler.getNodes(nodeNamesDTO) >> {
            throw new RbacException("security violation exception")
        }
        when:
        Response response = nodesV2RestResource.fetch(nodeNamesDTO)
        then:
        noExceptionThrown()
        and:
        response.getStatusInfo().getStatusCode() == Status.INTERNAL_SERVER_ERROR.getStatusCode()
        and:
        1 * nodesV2RestResource.nscsContextService.setErrorDetailContextValue("Security violation exception.")
    }

    @Unroll
    def 'fetch with exception message #message caused by RBAC exception'() {
        given:
        nodesListHandler.getNodes(nodeNamesDTO) >> {
            throw new EJBException(message, new RbacException("security violation exception"))
        }
        when:
        Response response = nodesV2RestResource.fetch(nodeNamesDTO)
        then:
        noExceptionThrown()
        and:
        response.getStatusInfo().getStatusCode() == Status.INTERNAL_SERVER_ERROR.getStatusCode()
        and:
        1 * nodesV2RestResource.nscsContextService.setErrorDetailContextValue("Security violation exception.")
        where:
        message << ["EJB exception", null]
    }

    @Unroll
    def 'fetch with exception message #message'() {
        given:
        nodesListHandler.getNodes(nodeNamesDTO) >> {
            throw new EJBException(message)
        }
        when:
        Response response = nodesV2RestResource.fetch(nodeNamesDTO)
        then:
        noExceptionThrown()
        and:
        response.getStatusInfo().getStatusCode() == Status.INTERNAL_SERVER_ERROR.getStatusCode()
        and:
        1 * nodesV2RestResource.nscsContextService.setErrorDetailContextValue(expectedmessage)
        where:
        message << ["EJB exception", null]
        expectedmessage << [
            "EJB exception",
            "Exception [javax.ejb.EJBException] occurred."
        ]
    }
}
