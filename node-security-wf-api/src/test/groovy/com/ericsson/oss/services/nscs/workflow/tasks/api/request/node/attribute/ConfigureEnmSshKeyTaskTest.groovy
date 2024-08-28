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
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys

import spock.lang.Shared
import spock.lang.Unroll

class ConfigureEnmSshKeyTaskTest extends CdiSpecification {

    private String NODE_NAME = "NODE"

    @Shared
    private List<String> algorithmKeys = AlgorithmKeys.values()

    def 'empty constructor'() {
        given:
        def task = new ConfigureEnmSshKeyTask()
        expect:
        task.getTaskType() == WorkflowTaskType.CONFIGURE_ENM_SSH_KEY
        task.getShortDescription() == ConfigureEnmSshKeyTask.SHORT_DESCRIPTION
        task.getNode() == null
        task.getNodeFdn() == null
        task.getSshkeyOperation() == null
        task.getAlgorithm() == null
        task.getOutputParams() == null
    }

    def 'constructor by node'() {
        given:
        def task = new ConfigureEnmSshKeyTask(NODE_NAME)
        expect:
        task.getTaskType() == WorkflowTaskType.CONFIGURE_ENM_SSH_KEY
        task.getShortDescription() == ConfigureEnmSshKeyTask.SHORT_DESCRIPTION
        task.getNode() != null
        task.getNodeFdn() == NODE_NAME
        task.getSshkeyOperation() == null
        task.getAlgorithm() == null
        task.getOutputParams() == null
    }

    @Unroll
    def 'set SSH key operation #operation'() {
        given:
        def task = new ConfigureEnmSshKeyTask(NODE_NAME)
        when:
        task.setSshkeyOperation(operation)
        then:
        task.getTaskType() == WorkflowTaskType.CONFIGURE_ENM_SSH_KEY
        task.getShortDescription() == ConfigureEnmSshKeyTask.SHORT_DESCRIPTION
        task.getNode() != null
        task.getNodeFdn() == NODE_NAME
        task.getSshkeyOperation() == operation
        task.getAlgorithm() == null
        task.getOutputParams() == null
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
        def task = new ConfigureEnmSshKeyTask(NODE_NAME)
        when:
        def algorithm = algorithmKey.name()
        task.setAlgorithm(algorithm)
        then:
        task.getTaskType() == WorkflowTaskType.CONFIGURE_ENM_SSH_KEY
        task.getShortDescription() == ConfigureEnmSshKeyTask.SHORT_DESCRIPTION
        task.getNode() != null
        task.getNodeFdn() == NODE_NAME
        task.getSshkeyOperation() == null
        task.getAlgorithm() == algorithm
        task.getOutputParams() == null
        where:
        algorithmKey << algorithmKeys
    }

    @Unroll
    def 'set output parameters public #publickey private #privatekey'() {
        given:
        def task = new ConfigureEnmSshKeyTask(NODE_NAME)
        def Map<String, Serializable> outputParams = new HashMap<>()
        outputParams.put(WorkflowOutputParameterKeys.PUBLIC_SSH_KEY.toString(), publickey)
        outputParams.put(WorkflowOutputParameterKeys.ENCRYPTED_PRIVATE_SSH_KEY.toString(), privatekey)
        when:
        task.setOutputParams(outputParams)
        then:
        task.getTaskType() == WorkflowTaskType.CONFIGURE_ENM_SSH_KEY
        task.getShortDescription() == ConfigureEnmSshKeyTask.SHORT_DESCRIPTION
        task.getNode() != null
        task.getNodeFdn() == NODE_NAME
        task.getSshkeyOperation() == null
        task.getAlgorithm() == null
        def expectedParams = task.getOutputParams()
        expectedParams != null
        expectedParams[WorkflowOutputParameterKeys.PUBLIC_SSH_KEY.toString()] == publickey
        expectedParams[WorkflowOutputParameterKeys.ENCRYPTED_PRIVATE_SSH_KEY.toString()] == privatekey
        where:
        publickey << [
            "public-key",
            null,
            "public-key",
            null
        ]
        privatekey << [
            "private-key",
            "private-key",
            null,
            null
        ]
    }
}
