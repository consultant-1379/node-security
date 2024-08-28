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
package com.ericsson.nms.security.nscs.workflow.task.node.attribute

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.nms.security.nscs.ssh.SSHKeyGenConstants
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.attribute.ConfigureEnmSshKeyTask
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys
import com.ericsson.oss.services.security.nscs.utils.CbpOiNodeDataSetup

import spock.lang.Unroll

class ConfigureEnmSshKeyTaskHandlerTest extends CbpOiNodeDataSetup {

    @ObjectUnderTest
    ConfigureEnmSshKeyTaskHandler taskHandler

    private nodeName = "NODE"
    private ConfigureEnmSshKeyTask task
    private String secureUserName = "secureUser"
    private Map<String, Serializable> outputParams
    private Map<String, Serializable> outputParamsOnDelete

    def setup() {
        NscsCMReaderService.deploymentEnv = NscsCMReaderService.DeploymentEnvironment.PRODUCTION
        outputParams = new HashMap<String, Serializable>();
        outputParams.put(WorkflowOutputParameterKeys.PUBLIC_SSH_KEY.toString(), "public-key");
        outputParams.put(WorkflowOutputParameterKeys.ENCRYPTED_PRIVATE_SSH_KEY.toString(), "encrypted-private-key");
        outputParamsOnDelete = new HashMap<String, Serializable>();
        outputParamsOnDelete.put(WorkflowOutputParameterKeys.PUBLIC_SSH_KEY.toString(), null);
        outputParamsOnDelete.put(WorkflowOutputParameterKeys.ENCRYPTED_PRIVATE_SSH_KEY.toString(), null);
    }

    def 'object under test'() {
        expect:
        taskHandler != null
    }

    def 'process task executed with success for create operation' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with SecurityFunction under NetworkElement"
        createSecurityFunctionUnderNetworkElement()
        and: "with NetworkElementSecurity under SecurityFunction"
        createNetworkElementSecurityUnderSecurityFunction(secureUserName, "RSA_1024", null, null)
        and: "task for node"
        task = new ConfigureEnmSshKeyTask(nodeName)
        and: "task with created operation"
        task.setSshkeyOperation(SSHKeyGenConstants.SSH_KEY_TO_BE_CREATED)
        and: "task with output parameters"
        task.setOutputParams(outputParams)
        and: "task with supported algorithm"
        task.setAlgorithm("RSA_1024")
        when:
        def result = taskHandler.processTask(task)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "result should be not null"
        result != null
        and: "result should be UPDATED_ON_ENM"
        result == "UPDATED_ON_ENM"
        and: "SSH keys should be updated in NetworkElementSecurity MO"
        def ManagedObject nes = findMoByFdn("NetworkElement="+nodeName+",SecurityFunction=1,NetworkElementSecurity=1")
        nes.getAttribute("enmSshPrivateKey") == "encrypted-private-key"
        nes.getAttribute("enmSshPublicKey") == "public-key"
        and: "algorithmAndKeySize should be changed in NetworkElementSecurity MO"
        nes.getAttribute("algorithmAndKeySize") == "RSA_1024"
    }

    def 'process task executed with success for create operation changing algorithm and key size' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with SecurityFunction under NetworkElement"
        createSecurityFunctionUnderNetworkElement()
        and: "with NetworkElementSecurity under SecurityFunction"
        createNetworkElementSecurityUnderSecurityFunction(secureUserName, "RSA_1024", null, null)
        and: "task for node"
        task = new ConfigureEnmSshKeyTask(nodeName)
        and: "task with created operation"
        task.setSshkeyOperation(SSHKeyGenConstants.SSH_KEY_TO_BE_CREATED)
        and: "task with output parameters"
        task.setOutputParams(outputParams)
        and: "task with supported algorithm"
        task.setAlgorithm("RSA_2048")
        when:
        def result = taskHandler.processTask(task)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "result should be not null"
        result != null
        and: "result should be UPDATED_ON_ENM"
        result == "UPDATED_ON_ENM"
        and: "SSH keys should be valid in NetworkElementSecurity MO"
        def ManagedObject nes = findMoByFdn("NetworkElement="+nodeName+",SecurityFunction=1,NetworkElementSecurity=1")
        nes.getAttribute("enmSshPrivateKey") == "encrypted-private-key"
        nes.getAttribute("enmSshPublicKey") == "public-key"
        and: "algorithmAndKeySize should be changed in NetworkElementSecurity MO"
        nes.getAttribute("algorithmAndKeySize") == "RSA_2048"
    }

    def 'process task executed with success for update operation' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with SecurityFunction under NetworkElement"
        createSecurityFunctionUnderNetworkElement()
        and: "with NetworkElementSecurity under SecurityFunction"
        createNetworkElementSecurityUnderSecurityFunction(secureUserName, "RSA_2048", "old-private", "old-public")
        and: "task for node"
        task = new ConfigureEnmSshKeyTask(nodeName)
        and: "task with update operation"
        task.setSshkeyOperation(SSHKeyGenConstants.SSH_KEY_TO_BE_UPDATED)
        and: "task with output parameters"
        task.setOutputParams(outputParams)
        and: "task with supported algorithm"
        task.setAlgorithm("RSA_4096")
        when:
        def result = taskHandler.processTask(task)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "result should be not null"
        result != null
        and: "result should be UPDATED_ON_ENM"
        result == "UPDATED_ON_ENM"
        and: "SSH keys should be valid and updated in NetworkElementSecurity MO"
        def ManagedObject nes = findMoByFdn("NetworkElement="+nodeName+",SecurityFunction=1,NetworkElementSecurity=1")
        nes.getAttribute("enmSshPrivateKey") == "encrypted-private-key"
        nes.getAttribute("enmSshPublicKey") == "public-key"
        and: "algorithmAndKeySize should be changed in NetworkElementSecurity MO"
        nes.getAttribute("algorithmAndKeySize") == "RSA_4096"
    }

    def 'process task executed with success for update operation with invalid keys in NES' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with SecurityFunction under NetworkElement"
        createSecurityFunctionUnderNetworkElement()
        and: "with NetworkElementSecurity under SecurityFunction"
        createNetworkElementSecurityUnderSecurityFunction(secureUserName, "RSA_1024", SSHKeyGenConstants.SSH_KEY_INVALID, SSHKeyGenConstants.SSH_KEY_INVALID)
        and: "task for node"
        task = new ConfigureEnmSshKeyTask(nodeName)
        and: "task with update operation"
        task.setSshkeyOperation(SSHKeyGenConstants.SSH_KEY_TO_BE_UPDATED)
        and: "task with output parameters"
        task.setOutputParams(outputParams)
        and: "task with supported algorithm"
        task.setAlgorithm("RSA_1024")
        when:
        def result = taskHandler.processTask(task)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "result should be not null"
        result != null
        and: "result should be UPDATED_ON_ENM"
        result == "UPDATED_ON_ENM"
        and: "SSH keys should be updated in NetworkElementSecurity MO"
        def ManagedObject nes = findMoByFdn("NetworkElement="+nodeName+",SecurityFunction=1,NetworkElementSecurity=1")
        nes.getAttribute("enmSshPrivateKey") == "encrypted-private-key"
        nes.getAttribute("enmSshPublicKey") == "public-key"
        and: "algorithmAndKeySize should be unchanged in NetworkElementSecurity MO"
        nes.getAttribute("algorithmAndKeySize") == "RSA_1024"
    }

    def 'process task executed with success for delete operation' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with SecurityFunction under NetworkElement"
        createSecurityFunctionUnderNetworkElement()
        and: "with NetworkElementSecurity under SecurityFunction"
        createNetworkElementSecurityUnderSecurityFunction(secureUserName, "RSA_2048", "", "")
        and: "task for node"
        task = new ConfigureEnmSshKeyTask(nodeName)
        and: "task with delete operation"
        task.setSshkeyOperation(SSHKeyGenConstants.SSH_KEY_TO_BE_DELETED)
        and: "task with output parameters"
        task.setOutputParams(outputParamsOnDelete)
        when:
        def result = taskHandler.processTask(task)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "result should be not null"
        result != null
        and: "result should be SKIPPED"
        result == "SKIPPED"
        and: "SSH keys should be unchanged in NetworkElementSecurity MO"
        def ManagedObject nes = findMoByFdn("NetworkElement="+nodeName+",SecurityFunction=1,NetworkElementSecurity=1")
        nes.getAttribute("enmSshPrivateKey") == ""
        nes.getAttribute("enmSshPublicKey") == ""
        and: "algorithmAndKeySize should be unchanged in NetworkElementSecurity MO"
        nes.getAttribute("algorithmAndKeySize") == "RSA_2048"
    }

    def 'process task executed with success for delete operation with null output parameters' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with SecurityFunction under NetworkElement"
        createSecurityFunctionUnderNetworkElement()
        and: "with NetworkElementSecurity under SecurityFunction"
        createNetworkElementSecurityUnderSecurityFunction(secureUserName, "RSA_2048", "", "")
        and: "task for node"
        task = new ConfigureEnmSshKeyTask(nodeName)
        and: "task with delete operation"
        task.setSshkeyOperation(SSHKeyGenConstants.SSH_KEY_TO_BE_DELETED)
        and: "task with null output parameters"
        task.setOutputParams(null)
        when:
        def result = taskHandler.processTask(task)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "result should be not null"
        result != null
        and: "result should be SKIPPED"
        result == "SKIPPED"
        and: "SSH keys should be unchanged in NetworkElementSecurity MO"
        def ManagedObject nes = findMoByFdn("NetworkElement="+nodeName+",SecurityFunction=1,NetworkElementSecurity=1")
        nes.getAttribute("enmSshPrivateKey") == ""
        nes.getAttribute("enmSshPublicKey") == ""
        and: "algorithmAndKeySize should be unchanged in NetworkElementSecurity MO"
        nes.getAttribute("algorithmAndKeySize") == "RSA_2048"
    }

    def 'process task executed with success for delete operation without output parameters' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with SecurityFunction under NetworkElement"
        createSecurityFunctionUnderNetworkElement()
        and: "with NetworkElementSecurity under SecurityFunction"
        createNetworkElementSecurityUnderSecurityFunction(secureUserName, "RSA_2048", "", "")
        and: "task for node"
        task = new ConfigureEnmSshKeyTask(nodeName)
        and: "task with delete operation"
        task.setSshkeyOperation(SSHKeyGenConstants.SSH_KEY_TO_BE_DELETED)
        when:
        def result = taskHandler.processTask(task)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "result should be not null"
        result != null
        and: "result should be SKIPPED"
        result == "SKIPPED"
        and: "SSH keys should be unchanged in NetworkElementSecurity MO"
        def ManagedObject nes = findMoByFdn("NetworkElement="+nodeName+",SecurityFunction=1,NetworkElementSecurity=1")
        nes.getAttribute("enmSshPrivateKey") == ""
        nes.getAttribute("enmSshPublicKey") == ""
        and: "algorithmAndKeySize should be unchanged in NetworkElementSecurity MO"
        nes.getAttribute("algorithmAndKeySize") == "RSA_2048"
    }

    def 'process task executed with error for delete operation without NetworkElementSecurity MO' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with SecurityFunction under NetworkElement"
        createSecurityFunctionUnderNetworkElement()
        and: "task for node"
        task = new ConfigureEnmSshKeyTask(nodeName)
        and: "task with operation"
        task.setSshkeyOperation(SSHKeyGenConstants.SSH_KEY_TO_BE_DELETED)
        and: "task with output parameters"
        task.setOutputParams(outputParamsOnDelete)
        when:
        def result = taskHandler.processTask(task)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "result should be not null"
        result != null
        and: "result should be SKIPPED"
        result == "SKIPPED"
    }

    @Unroll
    def 'process task executed with error for #operation operation with null output parameters' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with SecurityFunction under NetworkElement"
        createSecurityFunctionUnderNetworkElement()
        and: "with NetworkElementSecurity under SecurityFunction"
        createNetworkElementSecurityUnderSecurityFunction(secureUserName, "RSA_4096", "private", "public")
        and: "task for node"
        task = new ConfigureEnmSshKeyTask(nodeName)
        and: "task with operation"
        task.setSshkeyOperation(operation)
        and: "task with null output parameters"
        task.setOutputParams(null)
        and: "task with supported algorithm"
        task.setAlgorithm("RSA_1024")
        when:
        def result = taskHandler.processTask(task)
        then: "exception should be thrown"
        thrown(WorkflowTaskException.class)
        where:
        operation << [
            SSHKeyGenConstants.SSH_KEY_TO_BE_CREATED,
            SSHKeyGenConstants.SSH_KEY_TO_BE_UPDATED
        ]
    }

    @Unroll
    def 'process task executed with error for #operation operation without NetworkElementSecurity MO' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with SecurityFunction under NetworkElement"
        createSecurityFunctionUnderNetworkElement()
        and: "task for node"
        task = new ConfigureEnmSshKeyTask(nodeName)
        and: "task with operation"
        task.setSshkeyOperation(operation)
        and: "task with output parameters"
        task.setOutputParams(outputParams)
        when:
        def result = taskHandler.processTask(task)
        then: "exception should be thrown"
        thrown(WorkflowTaskException.class)
        where:
        operation << [
            SSHKeyGenConstants.SSH_KEY_TO_BE_CREATED,
            SSHKeyGenConstants.SSH_KEY_TO_BE_UPDATED
        ]
    }

    @Unroll
    def 'process task executed with error for #operation operation with wrong algorithm and key size' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with SecurityFunction under NetworkElement"
        createSecurityFunctionUnderNetworkElement()
        and: "with NetworkElementSecurity under SecurityFunction"
        createNetworkElementSecurityUnderSecurityFunction(secureUserName, "RSA_1024", "private", "public")
        and: "task for node"
        task = new ConfigureEnmSshKeyTask(nodeName)
        and: "task with operation"
        task.setSshkeyOperation(operation)
        and: "task with output parameters"
        task.setOutputParams(outputParams)
        and: "task with wrong algorithm"
        task.setAlgorithm("WRONG_1024")
        when:
        def result = taskHandler.processTask(task)
        then: "exception should be thrown"
        thrown(WorkflowTaskException.class)
        where:
        operation << [
            SSHKeyGenConstants.SSH_KEY_TO_BE_CREATED,
            SSHKeyGenConstants.SSH_KEY_TO_BE_UPDATED
        ]
    }

    @Unroll
    def 'process task executed with success for #operation operation with unsupported by node but valid algorithm and key size' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with SecurityFunction under NetworkElement"
        createSecurityFunctionUnderNetworkElement()
        and: "with NetworkElementSecurity under SecurityFunction"
        createNetworkElementSecurityUnderSecurityFunction(secureUserName, "RSA_1024", "private", "public")
        and: "task for node"
        task = new ConfigureEnmSshKeyTask(nodeName)
        and: "task with operation"
        task.setSshkeyOperation(operation)
        and: "task with output parameters"
        task.setOutputParams(outputParams)
        and: "task with unsupported by node but valid algorithm"
        task.setAlgorithm("ECDSA_160")
        when:
        def result = taskHandler.processTask(task)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "result should be not null"
        result != null
        and: "result should be UPDATED_ON_ENM"
        result == "UPDATED_ON_ENM"
        and: "SSH keys should be updated in NetworkElementSecurity MO"
        def ManagedObject nes = findMoByFdn("NetworkElement="+nodeName+",SecurityFunction=1,NetworkElementSecurity=1")
        nes.getAttribute("enmSshPrivateKey") == "encrypted-private-key"
        nes.getAttribute("enmSshPublicKey") == "public-key"
        and: "algorithmAndKeySize should be updated in NetworkElementSecurity MO"
        nes.getAttribute("algorithmAndKeySize") == "ECDSA_160"
        where:
        operation << [
            SSHKeyGenConstants.SSH_KEY_TO_BE_CREATED,
            SSHKeyGenConstants.SSH_KEY_TO_BE_UPDATED
        ]
    }
}
