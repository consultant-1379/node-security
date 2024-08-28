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
package com.ericsson.nms.security.nscs

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.services.security.nscs.workflow.NscsWorkflowManager

class WorkflowRestResourceTest extends CdiSpecification {

    @ObjectUnderTest
    WorkflowRestResource workflowRestResource

    @MockedImplementation
    NscsWorkflowManager nscsWorkflowManager

    def "get workflows status"() {
        given:
        def nodeList = "NODE1&NODE2"
        when:
        workflowRestResource.getWfStatus(nodeList)
        then:
        1 * nscsWorkflowManager.getWorkflowsStatus(nodeList)
    }

    def "get workflows stats"() {
        given:
        when:
        workflowRestResource.getWfStats()
        then:
        1 * nscsWorkflowManager.getWorkflowsStats()
    }

    def "delete workflow instance"() {
        given:
        def instanceId = "1234"
        when:
        workflowRestResource.deleteWfInstance(instanceId)
        then:
        1 * nscsWorkflowManager.deleteWorkflowInstance(instanceId)
    }
}
