/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.workflow.task.cbpoi.attribute

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cbpoi.attribute.CbpOiConfigureNodeCredentialServicesTask
import com.ericsson.oss.services.security.nscs.utils.CbpOiNodeDataSetup

class CbpOiConfigureNodeCredentialServicesTaskHandlerTest extends CbpOiNodeDataSetup {

    @ObjectUnderTest
    CbpOiConfigureNodeCredentialServicesTaskHandler taskHandler

    private CbpOiConfigureNodeCredentialServicesTask task

    private nodeName = "vDU00001"

    def setup() {
        NscsCMReaderService.deploymentEnv = NscsCMReaderService.DeploymentEnvironment.PRODUCTION
    }

    def 'process task executed with success' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "task for node"
        task = new CbpOiConfigureNodeCredentialServicesTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task with already set output parameters"
        def outputParams = [ "ASYMMETRIC_KEY_NAME" : "oamNodeCredential" ]
        task.setOutputParams(outputParams)
        when:
        def result = taskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "result should be not null"
        result != null
        and: "deserialized task result should be not null"
        deserializedTaskResult != null
        and: "task result should be PASSED"
        deserializedTaskResult.getResult() == "PASSED"
        and: "output parameters should be the original ones"
        deserializedTaskResult.getOutputParams().size() == 1
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEY_NAME") == "oamNodeCredential"
    }

    def 'process task executed with null output parameters' () {
        given: "task for node"
        task = new CbpOiConfigureNodeCredentialServicesTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task with null output parameters"
        def outputParams = null
        task.setOutputParams(outputParams)
        when:
        def result = taskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);
        then: "exception UnexpectedErrorException should be thrown"
        UnexpectedErrorException e = thrown()
        and:
        e.getMessage() == "Unexpected Internal Error : Missing internal parameters"
    }
}
