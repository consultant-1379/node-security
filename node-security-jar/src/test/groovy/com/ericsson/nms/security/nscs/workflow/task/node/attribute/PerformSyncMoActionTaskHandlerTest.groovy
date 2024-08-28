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
package com.ericsson.nms.security.nscs.workflow.task.node.attribute

import com.ericsson.cds.cdi.support.rule.ImplementationClasses
import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException
import com.ericsson.nms.security.nscs.data.moaction.MOActionServiceBean
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithoutParameter
import com.ericsson.nms.security.nscs.logger.NscsLogger
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoAction
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoActionState
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoActionWithParams
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoActionWithoutParams
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoActions
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoParams
import com.ericsson.oss.services.cm.cmshared.dto.ActionSpecification
import com.ericsson.oss.services.cm.cmshared.dto.CmObject
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse
import com.ericsson.oss.services.cm.cmwriter.api.CmWriterService
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskFailureException
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.attribute.PerformSyncMoActionTask

class PerformSyncMoActionTaskHandlerTest extends CdiSpecification {

    @ObjectUnderTest
    PerformSyncMoActionTaskHandler taskHandler

    @ImplementationClasses
    def myClasses = [
        NscsLogger.class,
        MOActionServiceBean.class
    ]

    @ImplementationInstance
    CmWriterService writer = [
        performAction : { String fdn, ActionSpecification spec ->
            if ("oamNodeCredential" == fdn) {
                CmResponse response = Mock(CmResponse)
                CmObject obj = Mock(CmObject)
                response.getStatusCode() >> 1
                response.getCmObjects() >> [obj]
                return response
            } else {
                CmResponse response = Mock(CmResponse)
                response.getStatusCode() >> -1
                response.getStatusMessage() >> "error"
                return response
            }
        }
    ] as CmWriterService

    private PerformSyncMoActionTask task
    private nodeName = "vDU00001"

    private WorkflowMoAction moActionWithParams
    private WorkflowMoAction invalidMoActionWithParams
    private WorkflowMoAction notPendingMoActionWithParams
    private WorkflowMoAction failingMoActionWithParams
    private WorkflowMoAction moActionWithoutParams
    private WorkflowMoAction failingMoActionWithoutParams

    def setup() {
        moActionWithParams = buildMoActionWithParams()
        invalidMoActionWithParams = buildInvalidMoActionWithParams()
        notPendingMoActionWithParams = buildNotPendingMoActionWithParams()
        failingMoActionWithParams = buildFailingMoActionWithParams()
        moActionWithoutParams = buildMoActionWithoutParams()
        failingMoActionWithoutParams = buildFailingMoActionWithoutParams()
    }

    def 'process task executed with success for an action with params' () {
        given: "task for node"
        task = new PerformSyncMoActionTask(nodeName)
        and: "task with already set output parameters with MO_ACTIONS containing a single action with params"
        String serializedMoActions = buildSerializedMoActions(moActionWithParams)
        def outputParams = [ "MO_ACTIONS" : serializedMoActions ]
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
        and: "task result should be FINISHED_WITH_SUCCESS"
        deserializedTaskResult.getResult() == "FINISHED_WITH_SUCCESS"
        and: "output parameters should be unchanged"
        deserializedTaskResult.getOutputParams().size() == 1
        and: "MO_ACTIONS output parameter should have been changed"
        deserializedTaskResult.getOutputParams().containsKey("MO_ACTIONS")
        WorkflowMoActions moActions = NscsObjectSerializer.readObject(deserializedTaskResult.getOutputParams().get("MO_ACTIONS"));
        moActions.getTargetActions().isEmpty() == true
    }

    def 'process task executed for an invalid action with params' () {
        given: "task for node"
        task = new PerformSyncMoActionTask(nodeName)
        and: "task with already set output parameters with MO_ACTIONS containing a single invalid action with params"
        String serializedMoActions = buildSerializedMoActions(invalidMoActionWithParams)
        def outputParams = [ "MO_ACTIONS" : serializedMoActions ]
        task.setOutputParams(outputParams)
        when:
        def result = taskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);
        then: "exception UnexpectedErrorException should be thrown"
        UnexpectedErrorException e = thrown()
        and:
        e.getMessage().startsWith("Unexpected Internal Error : No MO action parameters for action")
    }

    def 'process task executed for a not pending action with params' () {
        given: "task for node"
        task = new PerformSyncMoActionTask(nodeName)
        and: "task with already set output parameters with MO_ACTIONS containing a single invalid action with params"
        String serializedMoActions = buildSerializedMoActions(notPendingMoActionWithParams)
        def outputParams = [ "MO_ACTIONS" : serializedMoActions ]
        task.setOutputParams(outputParams)
        when:
        def result = taskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);
        then: "exception UnexpectedErrorException should be thrown"
        UnexpectedErrorException e = thrown()
        and:
        e.getMessage().startsWith("Unexpected Internal Error : Not pending sync MO action")
    }

    def 'process task executed for a failing action with params' () {
        given: "task for node"
        task = new PerformSyncMoActionTask(nodeName)
        and: "task with already set output parameters with MO_ACTIONS containing a single failing action with params"
        String serializedMoActions = buildSerializedMoActions(failingMoActionWithParams)
        def outputParams = [ "MO_ACTIONS" : serializedMoActions ]
        task.setOutputParams(outputParams)
        when:
        def result = taskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);
        then: "exception UnexpectedErrorException should be thrown"
        WorkflowTaskFailureException e = thrown()
        and:
        e.getMessage().contains("while performing action")
    }

    def 'process task executed with success for an action without params' () {
        given: "task for node"
        task = new PerformSyncMoActionTask(nodeName)
        and: "task with already set output parameters with MO_ACTIONS containing a single action without params"
        String serializedMoActions = buildSerializedMoActions(moActionWithoutParams)
        def outputParams = [ "MO_ACTIONS" : serializedMoActions ]
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
        and: "task result should be FINISHED_WITH_SUCCESS"
        deserializedTaskResult.getResult() == "FINISHED_WITH_SUCCESS"
        and: "output parameters should be unchanged"
        deserializedTaskResult.getOutputParams().size() == 1
        and: "MO_ACTIONS output parameter should have been changed"
        deserializedTaskResult.getOutputParams().containsKey("MO_ACTIONS")
        WorkflowMoActions moActions = NscsObjectSerializer.readObject(deserializedTaskResult.getOutputParams().get("MO_ACTIONS"));
        moActions.getTargetActions().isEmpty() == true
    }

    def 'process task executed for a failing action without params' () {
        given: "task for node"
        task = new PerformSyncMoActionTask(nodeName)
        and: "task with already set output parameters with MO_ACTIONS containing a single failing action without params"
        String serializedMoActions = buildSerializedMoActions(failingMoActionWithoutParams)
        def outputParams = [ "MO_ACTIONS" : serializedMoActions ]
        task.setOutputParams(outputParams)
        when:
        def result = taskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);
        then: "exception UnexpectedErrorException should be thrown"
        WorkflowTaskFailureException e = thrown()
        and:
        e.getMessage().contains("while performing action")
    }

    def 'process task executed with success for two actions' () {
        given: "task for node"
        task = new PerformSyncMoActionTask(nodeName)
        and: "task with already set output parameters with MO_ACTIONS containing two actions"
        String serializedMoActions = buildSerializedMoActions(moActionWithParams, moActionWithoutParams)
        def outputParams = [ "MO_ACTIONS" : serializedMoActions ]
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
        and: "task result should be FINISHED_WITH_PENDING"
        deserializedTaskResult.getResult() == "FINISHED_WITH_PENDING"
        and: "output parameters should be unchanged"
        deserializedTaskResult.getOutputParams().size() == 1
        and: "MO_ACTIONS output parameter should have been changed"
        deserializedTaskResult.getOutputParams().containsKey("MO_ACTIONS")
        WorkflowMoActions moActions = NscsObjectSerializer.readObject(deserializedTaskResult.getOutputParams().get("MO_ACTIONS"));
        moActions.getTargetActions().size() == 1
    }

    def 'process task executed with null output parameters' () {
        given: "task for node"
        task = new PerformSyncMoActionTask(nodeName)
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

    def 'process task executed with no MO_ACTIONS in output parameters' () {
        given: "task for node"
        task = new PerformSyncMoActionTask(nodeName)
        and: "task with already set output parameters with null MO_ACTIONS"
        def outputParams = [ : ]
        task.setOutputParams(outputParams)
        when:
        def result = taskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);
        then: "exception UnexpectedErrorException should be thrown"
        UnexpectedErrorException e = thrown()
        and:
        e.getMessage() == "Unexpected Internal Error : Missing sync MO actions internal parameter"
    }

    def 'process task executed with null MO_ACTIONS in output parameters' () {
        given: "task for node"
        task = new PerformSyncMoActionTask(nodeName)
        and: "task with already set output parameters with null MO_ACTIONS"
        def outputParams = [ "MO_ACTIONS" : null ]
        task.setOutputParams(outputParams)
        when:
        def result = taskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);
        then: "exception UnexpectedErrorException should be thrown"
        UnexpectedErrorException e = thrown()
        and:
        e.getMessage() == "Unexpected Internal Error : Missing sync MO actions internal parameter"
    }

    def 'process task executed for an empty list of actions' () {
        given: "task for node"
        task = new PerformSyncMoActionTask(nodeName)
        and: "task with already set output parameters with MO_ACTIONS containing a single invalid action with params"
        String serializedMoActions = buildSerializedMoActions()
        def outputParams = [ "MO_ACTIONS" : serializedMoActions ]
        task.setOutputParams(outputParams)
        when:
        def result = taskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);
        then: "exception UnexpectedErrorException should be thrown"
        UnexpectedErrorException e = thrown()
        and:
        e.getMessage() == "Unexpected Internal Error : No sync MO actions to be performed"
    }

    private static WorkflowMoParams getEnrollmentParams() {
        final WorkflowMoParams params = new WorkflowMoParams()
        params.addParam("challengePassword", "otp", true)
        return params
    }

    private String buildSerializedMoActions(WorkflowMoAction ... targetActions) {
        WorkflowMoActions moActions = new WorkflowMoActions()
        for (WorkflowMoAction targetAction : targetActions) {
            moActions.addTargetAction(targetAction)
        }
        return NscsObjectSerializer.writeObject(moActions)
    }

    private WorkflowMoAction buildMoActionWithParams() {
        return new WorkflowMoActionWithParams("oamNodeCredential", MoActionWithParameter.ComEcim_NodeCredential_startOnlineEnrollment, getEnrollmentParams(),
                0)
    }

    private WorkflowMoAction buildInvalidMoActionWithParams() {
        return new WorkflowMoActionWithParams("oamNodeCredential", MoActionWithParameter.ComEcim_NodeCredential_startOnlineEnrollment, null,
                0)
    }

    private WorkflowMoAction buildNotPendingMoActionWithParams() {
        WorkflowMoAction action =
                new WorkflowMoActionWithParams("oamNodeCredential", MoActionWithParameter.ComEcim_NodeCredential_startOnlineEnrollment, null,
                0)
        action.setState(WorkflowMoActionState.PERFORMING_IT)
        return action
    }

    private WorkflowMoAction buildFailingMoActionWithParams() {
        return new WorkflowMoActionWithParams("failingNodeCredential", MoActionWithParameter.ComEcim_NodeCredential_startOnlineEnrollment, getEnrollmentParams(),
                0)
    }

    private WorkflowMoAction buildMoActionWithoutParams() {
        return new WorkflowMoActionWithoutParams("oamNodeCredential", MoActionWithoutParameter.ComEcim_CertM_cancel, 0);
    }

    private WorkflowMoAction buildFailingMoActionWithoutParams() {
        return new WorkflowMoActionWithoutParams("failingNodeCredential", MoActionWithoutParameter.ComEcim_CertM_cancel, 0);
    }
}
