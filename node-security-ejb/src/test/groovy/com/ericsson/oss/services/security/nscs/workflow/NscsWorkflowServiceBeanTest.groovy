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

import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.WorkflowHandler
import com.ericsson.nms.security.nscs.api.model.NodeRef
import com.ericsson.nms.security.nscs.api.model.NodeReference
import com.ericsson.oss.services.nscs.workflow.WfQueryService

import spock.lang.Shared

class NscsWorkflowServiceBeanTest extends CdiSpecification {

    @ObjectUnderTest
    NscsWorkflowServiceBean nscsWorkflowService

    @MockedImplementation
    WfQueryService wfQueryService

    @ImplementationInstance
    WorkflowHandler workflowHandler = [
        cancelWorkflowInstance : { String id ->
            if (id == "notExistingWorkflowId") {
                throw new Exception()
            }
            return
        }
    ] as WorkflowHandler

    @Shared
    NodeReference nodeReference1 = new NodeRef("node1")

    def "object under test should not be null" () {
        expect:
        nscsWorkflowService != null
    }

    def "get status for node list" () {
        given:
        def nodeReferences = [nodeReference1]
        when:
        nscsWorkflowService.getWorkflowsStatus(nodeReferences)
        then:
        1 * wfQueryService.getWorkflowStatus(_)
        and:
        notThrown(Exception)
    }

    def "get workflows stats" () {
        given:
        when:
        nscsWorkflowService.getWorkflowsStats()
        then:
        1 * wfQueryService.getWorkflowRunningInstancesByName()
        and:
        notThrown(Exception)
    }

    def "delete existing workflow" () {
        given:
        when:
        def result = nscsWorkflowService.deleteWorkflowInstance("existingWorkflowId")
        then:
        result.startsWith("Successfully")
        and:
        notThrown(Exception)
    }

    def "delete not existing workflow" () {
        given:
        when:
        def result = nscsWorkflowService.deleteWorkflowInstance("notExistingWorkflowId")
        then:
        result.startsWith("Exception")
        and:
        notThrown(Exception)
    }
}
