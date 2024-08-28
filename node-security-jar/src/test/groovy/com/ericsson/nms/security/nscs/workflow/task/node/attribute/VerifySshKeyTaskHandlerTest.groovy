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

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.nms.security.nscs.ssh.SSHKeyGenConstants
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.attribute.VerifySshKeyTask
import com.ericsson.oss.services.security.nscs.dps.NscsDpsUtils

import spock.lang.Unroll

class VerifySshKeyTaskHandlerTest extends CdiSpecification {

    @ObjectUnderTest
    VerifySshKeyTaskHandler taskHandler

    @MockedImplementation
    NscsCMReaderService readerService

    @MockedImplementation
    NscsDpsUtils nscsDpsUtils

    private VerifySshKeyTask task
    private String secureUserName = "secureUser"

    private nodeName = "NODE"
    private NormalizableNodeReference normalizableNodeRef = mock(NormalizableNodeReference.class)

    def setup() {
        readerService.getNormalizableNodeReference(_) >> normalizableNodeRef
    }

    def 'object under test'() {
        expect:
        taskHandler != null
    }

    def 'process task executed with success for delete operation' () {
        given: "task for node"
        task = new VerifySshKeyTask(nodeName)
        and: "task with delete operation"
        task.setSshkeyOperation(SSHKeyGenConstants.SSH_KEY_TO_BE_DELETED)
        and: "task with supported MOM type"
        task.setMomType("EOI")
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
    def 'process task executed with success and result SUCCESSFUL for operation #operation' () {
        given: "DPS stub"
        def ManagedObject networkElementMO = mock(ManagedObject.class)
        nscsDpsUtils.getNormalizedRootMo(normalizableNodeRef) >> networkElementMO
        def ManagedObject networkElementSecurityMO = mock(ManagedObject.class)
        nscsDpsUtils.getNetworkElementSecurityMO(networkElementMO) >> networkElementSecurityMO
        def ManagedObject userMO = mock(ManagedObject.class)
        userMO.getAttributes(_) >> [:]
        nscsDpsUtils.getUserMO(_, _) >> userMO
        and: "task for node"
        task = new VerifySshKeyTask(nodeName)
        and: "task with created operation"
        task.setSshkeyOperation(operation)
        and: "task with supported MOM type"
        task.setMomType("EOI")
        when:
        def result = taskHandler.processTask(task)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "result should be not null"
        result != null
        and: "result should be SUCCESSFUL"
        result == "SUCCESSFUL"
        where:
        operation << [
            SSHKeyGenConstants.SSH_KEY_TO_BE_CREATED,
            SSHKeyGenConstants.SSH_KEY_TO_BE_UPDATED
        ]
    }

    @Unroll
    def 'process task executed with success and result FAILED for operation #operation' () {
        given: "DPS stub"
        def ManagedObject networkElementMO = mock(ManagedObject.class)
        nscsDpsUtils.getNormalizedRootMo(normalizableNodeRef) >> networkElementMO
        def ManagedObject networkElementSecurityMO = mock(ManagedObject.class)
        nscsDpsUtils.getNetworkElementSecurityMO(networkElementMO) >> networkElementSecurityMO
        def ManagedObject userMO = mock(ManagedObject.class)
        userMO.getAttributes(_) >> { throw new Exception() }
        nscsDpsUtils.getUserMO(_, _) >> userMO
        and: "task for node"
        task = new VerifySshKeyTask(nodeName)
        and: "task with created operation"
        task.setSshkeyOperation(operation)
        and: "task with supported MOM type"
        task.setMomType("EOI")
        when:
        def result = taskHandler.processTask(task)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "result should be not null"
        result != null
        and: "result should be FAILED"
        result == "FAILED"
        where:
        operation << [
            SSHKeyGenConstants.SSH_KEY_TO_BE_CREATED,
            SSHKeyGenConstants.SSH_KEY_TO_BE_UPDATED
        ]
    }

    @Unroll
    def 'process task executed with error due to null NES for operation #operation' () {
        given: "DPS stub"
        def ManagedObject networkElementMO = mock(ManagedObject.class)
        nscsDpsUtils.getNormalizedRootMo(normalizableNodeRef) >> networkElementMO
        nscsDpsUtils.getNetworkElementSecurityMO(networkElementMO) >> null
        and: "task for node"
        task = new VerifySshKeyTask(nodeName)
        and: "task with created operation"
        task.setSshkeyOperation(operation)
        and: "task with supported MOM type"
        task.setMomType("EOI")
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
    def 'process task executed with error due to wrong MOM for operation #operation' () {
        given: "DPS stub"
        def ManagedObject networkElementMO = mock(ManagedObject.class)
        nscsDpsUtils.getNormalizedRootMo(normalizableNodeRef) >> networkElementMO
        def ManagedObject networkElementSecurityMO = mock(ManagedObject.class)
        nscsDpsUtils.getNetworkElementSecurityMO(networkElementMO) >> networkElementSecurityMO
        def ManagedObject userMO = mock(ManagedObject.class)
        userMO.getAttributes(_) >> { throw new Exception() }
        nscsDpsUtils.getUserMO(_, _) >> userMO
        and: "task for node"
        task = new VerifySshKeyTask(nodeName)
        and: "task with created operation"
        task.setSshkeyOperation(operation)
        and: "task with unsupported MOM type"
        task.setMomType("ECIM")
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
    def 'process task executed with error due to missing user MO for operation #operation' () {
        given: "DPS stub"
        def ManagedObject networkElementMO = mock(ManagedObject.class)
        nscsDpsUtils.getNormalizedRootMo(normalizableNodeRef) >> networkElementMO
        def ManagedObject networkElementSecurityMO = mock(ManagedObject.class)
        nscsDpsUtils.getNetworkElementSecurityMO(networkElementMO) >> networkElementSecurityMO
        nscsDpsUtils.getUserMO(_, _) >> null
        and: "task for node"
        task = new VerifySshKeyTask(nodeName)
        and: "task with created operation"
        task.setSshkeyOperation(operation)
        and: "task with supported MOM type"
        task.setMomType("EOI")
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
    def 'process task executed with error due to unexpected exception for operation #operation' () {
        given: "DPS stub"
        nscsDpsUtils.getNormalizedRootMo(normalizableNodeRef) >> {throw new Exception()}
        and: "task for node"
        task = new VerifySshKeyTask(nodeName)
        and: "task with created operation"
        task.setSshkeyOperation(operation)
        and: "task with supported MOM type"
        task.setMomType("EOI")
        when:
        def result = taskHandler.processTask(task)
        then: "exception should be thrown"
        thrown(Exception.class)
        where:
        operation << [
            SSHKeyGenConstants.SSH_KEY_TO_BE_CREATED,
            SSHKeyGenConstants.SSH_KEY_TO_BE_UPDATED
        ]
    }
}
