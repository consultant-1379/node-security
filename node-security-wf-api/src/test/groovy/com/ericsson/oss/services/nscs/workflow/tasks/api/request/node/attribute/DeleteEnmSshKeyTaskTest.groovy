/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2024
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.attribute

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType

import spock.lang.Unroll

class DeleteEnmSshKeyTaskTest extends CdiSpecification {

    private String NODE_NAME = "NODE"

    def 'constructor empty'() {
        given:
        def task = new DeleteEnmSshKeyTask()
        expect:
        task.getTaskType() == WorkflowTaskType.DELETE_ENM_SSH_KEY
        task.getShortDescription() == DeleteEnmSshKeyTask.SHORT_DESCRIPTION
        task.getNode() == null
        task.getNodeFdn() == null
        task.getSshkeyOperation() == null
        task.getIsModeledSshKey() == null
        task.getMomType() == null
    }

    def 'constructor by node'() {
        given:
        def task = new DeleteEnmSshKeyTask(NODE_NAME)
        expect:
        task.getTaskType() == WorkflowTaskType.DELETE_ENM_SSH_KEY
        task.getShortDescription() == DeleteEnmSshKeyTask.SHORT_DESCRIPTION
        task.getNode() != null
        task.getNodeFdn() == NODE_NAME
        task.getSshkeyOperation() == null
        task.getIsModeledSshKey() == null
        task.getMomType() == null
    }

    @Unroll
    def 'set SSH key operation #operation'() {
        given:
        def task = new DeleteEnmSshKeyTask(NODE_NAME)
        when:
        task.setSshkeyOperation(operation)
        then:
        task.getTaskType() == WorkflowTaskType.DELETE_ENM_SSH_KEY
        task.getShortDescription() == DeleteEnmSshKeyTask.SHORT_DESCRIPTION
        task.getNode() != null
        task.getNodeFdn() == NODE_NAME
        task.getSshkeyOperation() == operation
        task.getIsModeledSshKey() == null
        task.getMomType() == null
        where:
        operation << [
            "ssh_key_to_be_created",
            "ssh_key_to_be_updated",
            "ssh_key_to_be_deleted"
        ]
    }

    @Unroll
    def 'set is modeled SSH key #ismodeled'() {
        given:
        def task = new DeleteEnmSshKeyTask(NODE_NAME)
        when:
        task.setIsModeledSshKey(ismodeled)
        then:
        task.getTaskType() == WorkflowTaskType.DELETE_ENM_SSH_KEY
        task.getShortDescription() == DeleteEnmSshKeyTask.SHORT_DESCRIPTION
        task.getNode() != null
        task.getNodeFdn() == NODE_NAME
        task.getSshkeyOperation() == null
        task.getIsModeledSshKey() == expected
        task.getMomType() == null
        where:
        ismodeled << [
            null,
            "FALSE",
            "TRUE"
        ]
        expected << [
            null,
            "FALSE",
            "TRUE"
        ]
    }

    @Unroll
    def 'set MOM type #momtype'() {
        given:
        def task = new DeleteEnmSshKeyTask(NODE_NAME)
        when:
        task.setMomType(momtype)
        then:
        task.getTaskType() == WorkflowTaskType.DELETE_ENM_SSH_KEY
        task.getShortDescription() == DeleteEnmSshKeyTask.SHORT_DESCRIPTION
        task.getNode() != null
        task.getNodeFdn() == NODE_NAME
        task.getSshkeyOperation() == null
        task.getIsModeledSshKey() == null
        task.getMomType() == momtype
        where:
        momtype << [
            null,
            "ECIM",
            "EOI"
        ]
    }
}
