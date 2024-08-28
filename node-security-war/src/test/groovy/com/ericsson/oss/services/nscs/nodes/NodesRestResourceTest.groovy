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
import com.ericsson.nms.security.nscs.cpp.seclevel.dto.SecLevelDTO
import com.ericsson.nms.security.nscs.model.SecurityLevelSwitchStatus
import com.ericsson.nms.security.nscs.rest.local.service.NodeSecuritySeviceLocal
import com.ericsson.oss.itpf.sdk.security.accesscontrol.SecurityViolationException
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.InvalidNodeException
import com.ericsson.oss.services.security.nscs.context.NscsContextService

import spock.lang.Unroll

class NodesRestResourceTest extends CdiSpecification {

    @ObjectUnderTest
    NodesRestResource nodesRestResource

    @MockedImplementation
    SecLevelDTO secLevelDTO

    @MockedImplementation
    NodeSecuritySeviceLocal nodeSecuritySeviceLocal

    @MockedImplementation
    NscsContextService nscsContextService

    def 'object under test'() {
        expect:
        nodesRestResource != null
    }

    def 'change security level'() {
        given:
        secLevelDTO.getNodeNames() >> ["LTE02ERBS00001"]
        secLevelDTO.getWantedSecLevel() >> "2"
        SecurityLevelSwitchStatus securityLevelSwitchStatus = new SecurityLevelSwitchStatus("LTE02ERBS00001", "code", "message")
        nodeSecuritySeviceLocal.changeSecurityLevel(_, _) >> [securityLevelSwitchStatus]
        when:
        Response response = nodesRestResource.changeSecurityLevel(secLevelDTO)
        then:
        noExceptionThrown()
        and:
        response.getStatusInfo().getStatusCode() == Status.OK.getStatusCode()
        and:
        0 * nodesRestResource.nscsContextService.setErrorDetailContextValue(_ as String)
    }

    def 'change security level returning empty list'() {
        given:
        secLevelDTO.getNodeNames() >> ["LTE02ERBS00001"]
        secLevelDTO.getWantedSecLevel() >> "2"
        nodeSecuritySeviceLocal.changeSecurityLevel(_, _) >> []
        when:
        Response response = nodesRestResource.changeSecurityLevel(secLevelDTO)
        then:
        noExceptionThrown()
        and:
        response.getStatusInfo().getStatusCode() == Status.OK.getStatusCode()
        and:
        0 * nodesRestResource.nscsContextService.setErrorDetailContextValue(_ as String)
    }

    def 'change security level with exception security violation'() {
        given:
        secLevelDTO.getNodeNames() >> ["LTE02ERBS00001"]
        secLevelDTO.getWantedSecLevel() >> "2"
        nodeSecuritySeviceLocal.changeSecurityLevel(_, _) >> {
            throw new SecurityViolationException("security violation exception")
        }
        when:
        Response response = nodesRestResource.changeSecurityLevel(secLevelDTO)
        then:
        noExceptionThrown()
        and:
        response.getStatusInfo().getStatusCode() == Status.BAD_REQUEST.getStatusCode()
        and:
        1 * nodesRestResource.nscsContextService.setErrorDetailContextValue("Security violation exception.")
    }

    @Unroll
    def 'change security level with exception message #message caused by security violation'() {
        given:
        secLevelDTO.getNodeNames() >> ["LTE02ERBS00001"]
        secLevelDTO.getWantedSecLevel() >> "2"
        nodeSecuritySeviceLocal.changeSecurityLevel(_, _) >> {
            throw new EJBException(message, new SecurityViolationException("security violation exception"))
        }
        when:
        Response response = nodesRestResource.changeSecurityLevel(secLevelDTO)
        then:
        noExceptionThrown()
        and:
        response.getStatusInfo().getStatusCode() == Status.BAD_REQUEST.getStatusCode()
        and:
        1 * nodesRestResource.nscsContextService.setErrorDetailContextValue("Security violation exception.")
        where:
        message << ["EJB exception", null]
    }

    def 'change security level with exception invalid node'() {
        given:
        secLevelDTO.getNodeNames() >> ["LTE02ERBS00001"]
        secLevelDTO.getWantedSecLevel() >> "2"
        nodeSecuritySeviceLocal.changeSecurityLevel(_, _) >> {
            throw new InvalidNodeException("empty node list")
        }
        when:
        Response response = nodesRestResource.changeSecurityLevel(secLevelDTO)
        then:
        noExceptionThrown()
        and:
        response.getStatusInfo().getStatusCode() == Status.BAD_REQUEST.getStatusCode()
        and:
        1 * nodesRestResource.nscsContextService.setErrorDetailContextValue("Empty node list.")
    }

    @Unroll
    def 'change security level with exception message #message'() {
        given:
        secLevelDTO.getNodeNames() >> ["LTE02ERBS00001"]
        secLevelDTO.getWantedSecLevel() >> "2"
        nodeSecuritySeviceLocal.changeSecurityLevel(_, _) >> {
            throw new EJBException(message)
        }
        when:
        Response response = nodesRestResource.changeSecurityLevel(secLevelDTO)
        then:
        noExceptionThrown()
        and:
        response.getStatusInfo().getStatusCode() == Status.BAD_REQUEST.getStatusCode()
        and:
        1 * nodesRestResource.nscsContextService.setErrorDetailContextValue(expectedmessage)
        where:
        message << ["EJB exception", null]
        expectedmessage << [
            "EJB exception",
            "Exception [javax.ejb.EJBException] occurred."
        ]
    }
}
