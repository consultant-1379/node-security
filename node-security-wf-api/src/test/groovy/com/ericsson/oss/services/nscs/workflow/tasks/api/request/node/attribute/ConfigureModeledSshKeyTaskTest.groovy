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
import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType

import spock.lang.Shared
import spock.lang.Unroll

class ConfigureModeledSshKeyTaskTest extends CdiSpecification {

    private String NODE_NAME = "NODE"

    @Shared
    private List<String> algorithmKeys = AlgorithmKeys.values()

    def 'constructor empty'() {
        given:
        def task = new ConfigureModeledSshKeyTask()
        expect:
        task.getTaskType() == WorkflowTaskType.CONFIGURE_MODELED_SSH_KEY
        task.getShortDescription() == ConfigureModeledSshKeyTask.SHORT_DESCRIPTION
        task.getNode() == null
        task.getNodeFdn() == null
        task.getSshkeyOperation() == null
        task.getAlgorithm() == null
        task.getIsModeledSshKey() == null
        task.getMomType() == null
    }

    def 'constructor by node'() {
        given:
        def task = new ConfigureModeledSshKeyTask(NODE_NAME)
        expect:
        task.getTaskType() == WorkflowTaskType.CONFIGURE_MODELED_SSH_KEY
        task.getShortDescription() == ConfigureModeledSshKeyTask.SHORT_DESCRIPTION
        task.getNode() != null
        task.getNodeFdn() == NODE_NAME
        task.getSshkeyOperation() == null
        task.getAlgorithm() == null
        task.getIsModeledSshKey() == null
        task.getMomType() == null
    }

    @Unroll
    def 'set SSH key operation #operation'() {
        given:
        def task = new ConfigureModeledSshKeyTask(NODE_NAME)
        when:
        task.setSshkeyOperation(operation)
        then:
        task.getTaskType() == WorkflowTaskType.CONFIGURE_MODELED_SSH_KEY
        task.getShortDescription() == ConfigureModeledSshKeyTask.SHORT_DESCRIPTION
        task.getNode() != null
        task.getNodeFdn() == NODE_NAME
        task.getSshkeyOperation() == operation
        task.getAlgorithm() == null
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
    def 'set SSH keys generation algorithm #algorithmKey'() {
        given:
        def task = new ConfigureModeledSshKeyTask(NODE_NAME)
        when:
        def algorithm = algorithmKey.name()
        task.setAlgorithm(algorithm)
        then:
        task.getTaskType() == WorkflowTaskType.CONFIGURE_MODELED_SSH_KEY
        task.getShortDescription() == ConfigureModeledSshKeyTask.SHORT_DESCRIPTION
        task.getNode() != null
        task.getNodeFdn() == NODE_NAME
        task.getSshkeyOperation() == null
        task.getAlgorithm() == algorithm
        task.getIsModeledSshKey() == null
        task.getMomType() == null
        where:
        algorithmKey << algorithmKeys
    }

    @Unroll
    def 'set is modeled SSH key #ismodeled'() {
        given:
        def task = new ConfigureModeledSshKeyTask(NODE_NAME)
        when:
        task.setIsModeledSshKey(ismodeled)
        then:
        task.getTaskType() == WorkflowTaskType.CONFIGURE_MODELED_SSH_KEY
        task.getShortDescription() == ConfigureModeledSshKeyTask.SHORT_DESCRIPTION
        task.getNode() != null
        task.getNodeFdn() == NODE_NAME
        task.getSshkeyOperation() == null
        task.getAlgorithm() == null
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
        def task = new ConfigureModeledSshKeyTask(NODE_NAME)
        when:
        task.setMomType(momtype)
        then:
        task.getTaskType() == WorkflowTaskType.CONFIGURE_MODELED_SSH_KEY
        task.getShortDescription() == ConfigureModeledSshKeyTask.SHORT_DESCRIPTION
        task.getNode() != null
        task.getNodeFdn() == NODE_NAME
        task.getSshkeyOperation() == null
        task.getAlgorithm() == null
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
