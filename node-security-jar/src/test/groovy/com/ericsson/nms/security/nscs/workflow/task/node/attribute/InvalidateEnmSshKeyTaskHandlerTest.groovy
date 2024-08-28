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
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.attribute.InvalidateEnmSshKeyTask
import com.ericsson.oss.services.security.nscs.utils.CbpOiNodeDataSetup

import spock.lang.Unroll

class InvalidateEnmSshKeyTaskHandlerTest extends CbpOiNodeDataSetup {

    @ObjectUnderTest
    InvalidateEnmSshKeyTaskHandler taskHandler

    private InvalidateEnmSshKeyTask task

    private nodeName = "NODE"

    def setup() {
        NscsCMReaderService.deploymentEnv = NscsCMReaderService.DeploymentEnvironment.PRODUCTION
    }

    def 'object under test'() {
        expect:
        taskHandler != null
    }

    @Unroll
    def 'process task executed with success for operation #operation' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with SecurityFunction under NetworkElement"
        createSecurityFunctionUnderNetworkElement()
        and: "with NetworkElementSecurity under SecurityFunction"
        createNetworkElementSecurityUnderSecurityFunction("secureuser", "RSA_2048", "private", "public")
        and: "task for node"
        task = new InvalidateEnmSshKeyTask(nodeName)
        and: "task with delete operation"
        task.setSshkeyOperation(operation)
        when:
        def result = taskHandler.processTask(task)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "result should be not null"
        result != null
        and: "result should be UPDATED_ON_ENM"
        result == "UPDATED_ON_ENM"
        and: "SSH keys should be invalid in NetworkElementSecurity MO"
        def ManagedObject nes = findMoByFdn("NetworkElement="+nodeName+",SecurityFunction=1,NetworkElementSecurity=1")
        nes.getAttribute("enmSshPrivateKey") == SSHKeyGenConstants.SSH_KEY_INVALID
        nes.getAttribute("enmSshPublicKey") == SSHKeyGenConstants.SSH_KEY_INVALID
        and: "algorithmAndKeySize should be unchanged in NetworkElementSecurity MO"
        nes.getAttribute("algorithmAndKeySize") == "RSA_2048"
        where:
        operation << [
            SSHKeyGenConstants.SSH_KEY_TO_BE_CREATED,
            SSHKeyGenConstants.SSH_KEY_TO_BE_UPDATED
        ]
    }

    def 'process task executed with success for delete operation' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with SecurityFunction under NetworkElement"
        createSecurityFunctionUnderNetworkElement()
        and: "with NetworkElementSecurity under SecurityFunction"
        createNetworkElementSecurityUnderSecurityFunction("secureuser", "RSA_2048", "private", "public")
        and: "task for node"
        task = new InvalidateEnmSshKeyTask(nodeName)
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
        nes.getAttribute("enmSshPrivateKey") == "private"
        nes.getAttribute("enmSshPublicKey") == "public"
        and: "algorithmAndKeySize should be unchanged in NetworkElementSecurity MO"
        nes.getAttribute("algorithmAndKeySize") == "RSA_2048"
    }

    @Unroll
    def 'process task executed with error for #operation operation without NetworkElementSecurity MO' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with SecurityFunction under NetworkElement"
        createSecurityFunctionUnderNetworkElement()
        and: "task for node"
        task = new InvalidateEnmSshKeyTask(nodeName)
        and: "task with operation"
        task.setSshkeyOperation(operation)
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
    def 'process task executed with error for #operation operation without NetworkElement MO' () {
        given: "task for node"
        task = new InvalidateEnmSshKeyTask(nodeName)
        and: "task with operation"
        task.setSshkeyOperation(operation)
        when:
        def result = taskHandler.processTask(task)
        then: "exception should be thrown"
        thrown(Exception.class)
        where:
        operation << [
            SSHKeyGenConstants.SSH_KEY_TO_BE_CREATED,
            SSHKeyGenConstants.SSH_KEY_TO_BE_UPDATED,
            SSHKeyGenConstants.SSH_KEY_TO_BE_DELETED
        ]
    }
}
