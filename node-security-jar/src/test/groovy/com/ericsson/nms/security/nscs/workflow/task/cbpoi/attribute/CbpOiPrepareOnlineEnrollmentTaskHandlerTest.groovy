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
import com.ericsson.nms.security.nscs.api.enums.DigestAlgorithm
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode
import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfo
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfoImpl
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoAction
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoActionWithParams
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoActions
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoParams
import com.ericsson.oss.itpf.security.pki.common.model.EntityInfo
import com.ericsson.oss.itpf.security.pki.common.model.Subject
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cbpoi.attribute.CbpOiPrepareOnlineEnrollmentTask
import com.ericsson.oss.services.security.nscs.utils.CbpOiNodeDataSetup

import spock.lang.Unroll

class CbpOiPrepareOnlineEnrollmentTaskHandlerTest extends CbpOiNodeDataSetup {

    @ObjectUnderTest
    CbpOiPrepareOnlineEnrollmentTaskHandler taskHandler

    private CbpOiPrepareOnlineEnrollmentTask task

    private String serializedScepEnrollmentInfo

    private nodeName = "vDU00001"

    def setup() {
        Subject subject = new Subject()
        subject.fromASN1String("CN="+nodeName+"-oam")
        EntityInfo ei = new EntityInfo()
        ei.setSubject(subject)
        Entity ee = new Entity();
        ee.setEntityInfo(ei)
        ScepEnrollmentInfo scepEnrollmentInfo = new ScepEnrollmentInfoImpl(ee, "https://localhost:8091", null, DigestAlgorithm.MD5, 10, "otp", "1", EnrollmentMode.CMPv2_INITIAL, null, null)
        serializedScepEnrollmentInfo = NscsObjectSerializer.writeObject(scepEnrollmentInfo)
    }

    def 'object under test injection' () {
        expect:
        taskHandler != null
    }

    @Unroll
    def 'renew-cmp required (isReissue=#isReissue)' () {
        given: "task for node"
        task = new CbpOiPrepareOnlineEnrollmentTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task for issue or reissue"
        if (isReissue) {
            task.setIsReissue("TRUE")
        }
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : serializedScepEnrollmentInfo, "ASYMMETRIC_KEY_CMP_FDN" : "MeContext="+nodeName+",ManagedElement="+nodeName+",keystore=1,asymmetric-keys=1,asymmetric-key=oamNodeCredential,asymmetric-key\$\$cmp=1" ]
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
        and: "task result should be DONE"
        deserializedTaskResult.getResult() == "DONE"
        and: "output parameters should have been changed"
        deserializedTaskResult.getOutputParams().size() == 3
        and: "ENROLLMENT_INFO output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("ENROLLMENT_INFO") == serializedScepEnrollmentInfo
        and: "ASYMMETRIC_KEY_CMP_FDN output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEY_CMP_FDN") == "MeContext="+nodeName+",ManagedElement="+nodeName+",keystore=1,asymmetric-keys=1,asymmetric-key=oamNodeCredential,asymmetric-key\$\$cmp=1"
        and: "MO_ACTIONS output parameter should have been added"
        def String serializedMoActions = deserializedTaskResult.getOutputParams().get("MO_ACTIONS")
        serializedMoActions != null
        and: "deserialized MO actions should be not null"
        def WorkflowMoActions moActions = NscsObjectSerializer.readObject(serializedMoActions)
        moActions != null
        and: "MO action list should be not null and should contain one action"
        def List<WorkflowMoAction> moActionList = moActions.getTargetActions()
        moActionList != null && !moActionList.isEmpty() && moActionList.size() == 1
        and: "MO action should be not null"
        def WorkflowMoActionWithParams action = moActionList.get(0)
        action != null
        and: "MO action should be renew-cmp"
        action.getTargetAction().getAction() == "renew-cmp"
        and: "MO action target FDN should be ASYMMETRIC_KEY_CMP_FDN"
        action.getTargetMoFdn() == "MeContext="+nodeName+",ManagedElement="+nodeName+",keystore=1,asymmetric-keys=1,asymmetric-key=oamNodeCredential,asymmetric-key\$\$cmp=1"
        and: "MO action should have attributes"
        def WorkflowMoParams moParams = action.getTargetActionParams()
        moParams != null
        and: "MO action should contain expected number of attributes"
        moParams.getParamMap().size() == 1
        and: "MO action should contain algorithm attribute with expected value"
        moParams.getParamMap().get("algorithm").getParam() == "rsa2048"
        where:
        isReissue << [true, false]
    }

    @Unroll
    def 'start-cmp required (isReissue=#isReissue)' () {
        given: "task for node"
        task = new CbpOiPrepareOnlineEnrollmentTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task for issue or reissue"
        if (isReissue) {
            task.setIsReissue("TRUE")
        }
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : serializedScepEnrollmentInfo, "CMP_SERVER_GROUP_NAME" : "1", "ASYMMETRIC_KEYS_CMP_FDN" : "MeContext="+nodeName+",ManagedElement="+nodeName+",keystore=1,asymmetric-keys=1,asymmetric-keys\$\$cmp=1", "ASYMMETRIC_KEY_NAME" : "oamNodeCredential", "IS_START_CMP_REQUIRED"  : "TRUE", "TRUSTED_CERTS_NAME" : "oamTrustCategory" ]
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
        and: "task result should be DONE"
        deserializedTaskResult.getResult() == "DONE"
        and: "output parameters should have been changed"
        deserializedTaskResult.getOutputParams().size() == 7
        and: "ENROLLMENT_INFO output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("ENROLLMENT_INFO") == serializedScepEnrollmentInfo
        and: "CMP_SERVER_GROUP_NAME output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("CMP_SERVER_GROUP_NAME") == "1"
        and: "ASYMMETRIC_KEYS_CMP_FDN output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEYS_CMP_FDN") == "MeContext="+nodeName+",ManagedElement="+nodeName+",keystore=1,asymmetric-keys=1,asymmetric-keys\$\$cmp=1"
        and: "ASYMMETRIC_KEY_NAME output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEY_NAME") == "oamNodeCredential"
        and: "IS_START_CMP_REQUIRED output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("IS_START_CMP_REQUIRED") == "TRUE"
        and: "TRUSTED_CERTS_NAME output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("TRUSTED_CERTS_NAME") == "oamTrustCategory"
        and: "MO_ACTIONS output parameter should have been added"
        def String serializedMoActions = deserializedTaskResult.getOutputParams().get("MO_ACTIONS")
        serializedMoActions != null
        and: "deserialized MO actions should be not null"
        def WorkflowMoActions moActions = NscsObjectSerializer.readObject(serializedMoActions)
        moActions != null
        and: "MO action list should be not null and should contain one action"
        def List<WorkflowMoAction> moActionList = moActions.getTargetActions()
        moActionList != null && !moActionList.isEmpty() && moActionList.size() == 1
        and: "MO action should be not null"
        def WorkflowMoActionWithParams action = moActionList.get(0)
        action != null
        and: "MO action should be start-cmp"
        action.getTargetAction().getAction() == "start-cmp"
        and: "MO action target FDN should be ASYMMETRIC_KEYS_CMP_FDN"
        action.getTargetMoFdn() == "MeContext="+nodeName+",ManagedElement="+nodeName+",keystore=1,asymmetric-keys=1,asymmetric-keys\$\$cmp=1"
        and: "MO action should have attributes"
        def WorkflowMoParams moParams = action.getTargetActionParams()
        moParams != null
        and: "MO action should contain expected number of attributes"
        moParams.getParamMap().size() == 7
        and: "MO action should contain algorithm attribute with expected value"
        moParams.getParamMap().get("algorithm").getParam() == "rsa2048"
        and: "MO action should contain certificate-name attribute with expected value"
        moParams.getParamMap().get("certificate-name").getParam() == "oamNodeCredential"
        and: "MO action should contain cmp-server-group attribute with expected value"
        moParams.getParamMap().get("cmp-server-group").getParam() == "1"
        and: "MO action should contain name attribute with expected value"
        moParams.getParamMap().get("name").getParam() == "oamNodeCredential"
        and: "MO action should contain password attribute with expected value"
        moParams.getParamMap().get("password").getParam() == "otp"
        and: "MO action should contain subject attribute with expected value"
        moParams.getParamMap().get("subject").getParam() == "CN="+nodeName+"-oam"
        and: "MO action should not contain subject-alternative-names attribute"
        moParams.getParamMap().get("subject-alternatives-names") == null
        and: "MO action should contain trusted-certs attribute with expected value"
        moParams.getParamMap().get("trusted-certs").getParam() == "oamTrustCategory"
        where:
        isReissue << [true, false]
    }

    def 'process task executed with null output parameters' () {
        given: "task for node"
        task = new CbpOiPrepareOnlineEnrollmentTask(nodeName)
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

    def 'start-cmp with null enrollment info in output parameters' () {
        given: "task for node"
        task = new CbpOiPrepareOnlineEnrollmentTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : null, "CMP_SERVER_GROUP_NAME" : "1", "ASYMMETRIC_KEYS_CMP_FDN" : "MeContext="+nodeName+",ManagedElement="+nodeName+",keystore=1,asymmetric-keys=1,asymmetric-keys\$\$cmp=1", "ASYMMETRIC_KEY_NAME" : "oamNodeCredential", "IS_START_CMP_REQUIRED"  : "TRUE", "TRUSTED_CERTS_NAME" : "oamTrustCategory" ]
        task.setOutputParams(outputParams)
        when:
        def result = taskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);
        then: "exception UnexpectedErrorException should be thrown"
        UnexpectedErrorException e = thrown()
        and:
        e.getMessage() == "Unexpected Internal Error : Missing enrollment info internal parameter"
    }

    def 'start-cmp without enrollment info in output parameters' () {
        given: "task for node"
        task = new CbpOiPrepareOnlineEnrollmentTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task with already set output parameters"
        def outputParams = [ "CMP_SERVER_GROUP_NAME" : "1", "ASYMMETRIC_KEYS_CMP_FDN" : "MeContext="+nodeName+",ManagedElement="+nodeName+",keystore=1,asymmetric-keys=1,asymmetric-keys\$\$cmp=1", "ASYMMETRIC_KEY_NAME" : "oamNodeCredential", "IS_START_CMP_REQUIRED"  : "TRUE", "TRUSTED_CERTS_NAME" : "oamTrustCategory" ]
        task.setOutputParams(outputParams)
        when:
        def result = taskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);
        then: "exception UnexpectedErrorException should be thrown"
        UnexpectedErrorException e = thrown()
        and:
        e.getMessage() == "Unexpected Internal Error : Missing enrollment info internal parameter"
    }

    def 'start-cmp without CMP_SERVER_GROUP_NAME in output parameters' () {
        given: "task for node"
        task = new CbpOiPrepareOnlineEnrollmentTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : serializedScepEnrollmentInfo, "ASYMMETRIC_KEYS_CMP_FDN" : "MeContext="+nodeName+",ManagedElement="+nodeName+",keystore=1,asymmetric-keys=1,asymmetric-keys\$\$cmp=1", "ASYMMETRIC_KEY_NAME" : "oamNodeCredential", "IS_START_CMP_REQUIRED"  : "TRUE", "TRUSTED_CERTS_NAME" : "oamTrustCategory" ]
        task.setOutputParams(outputParams)
        when:
        def result = taskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);
        then: "exception UnexpectedErrorException should be thrown"
        UnexpectedErrorException e = thrown()
        and:
        e.getMessage() == "Unexpected Internal Error : Missing CMP_SERVER_GROUP_NAME internal parameter"
    }

    @Unroll
    def 'start-cmp with invalid CMP_SERVER_GROUP_NAME (#cmpServerGroupName) in output parameters' () {
        given: "task for node"
        task = new CbpOiPrepareOnlineEnrollmentTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : serializedScepEnrollmentInfo, "CMP_SERVER_GROUP_NAME" : cmpServerGroupName, "ASYMMETRIC_KEYS_CMP_FDN" : "MeContext="+nodeName+",ManagedElement="+nodeName+",keystore=1,asymmetric-keys=1,asymmetric-keys\$\$cmp=1", "ASYMMETRIC_KEY_NAME" : "oamNodeCredential", "IS_START_CMP_REQUIRED"  : "TRUE", "TRUSTED_CERTS_NAME" : "oamTrustCategory" ]
        task.setOutputParams(outputParams)
        when:
        def result = taskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);
        then: "exception UnexpectedErrorException should be thrown"
        UnexpectedErrorException e = thrown()
        and:
        e.getMessage() == expected
        where:
        cmpServerGroupName || expected
        null               || "Unexpected Internal Error : Missing CMP_SERVER_GROUP_NAME internal parameter"
        ""                 || "Unexpected Internal Error : Missing CMP_SERVER_GROUP_NAME internal parameter"
    }

    def 'start-cmp without ASYMMETRIC_KEYS_CMP_FDN in output parameters' () {
        given: "task for node"
        task = new CbpOiPrepareOnlineEnrollmentTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : serializedScepEnrollmentInfo, "CMP_SERVER_GROUP_NAME" : "1", "ASYMMETRIC_KEY_NAME" : "oamNodeCredential", "IS_START_CMP_REQUIRED"  : "TRUE", "TRUSTED_CERTS_NAME" : "oamTrustCategory" ]
        task.setOutputParams(outputParams)
        when:
        def result = taskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);
        then: "exception UnexpectedErrorException should be thrown"
        UnexpectedErrorException e = thrown()
        and:
        e.getMessage() == "Unexpected Internal Error : Missing ASYMMETRIC_KEYS_CMP_FDN internal parameter"
    }

    @Unroll
    def 'start-cmp with invalid ASYMMETRIC_KEYS_CMP_FDN (#asymmetricKeysCmpFdn) in output parameters' () {
        given: "task for node"
        task = new CbpOiPrepareOnlineEnrollmentTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : serializedScepEnrollmentInfo, "CMP_SERVER_GROUP_NAME" : "1", "ASYMMETRIC_KEYS_CMP_FDN" : asymmetricKeysCmpFdn, "ASYMMETRIC_KEY_NAME" : "oamNodeCredential", "IS_START_CMP_REQUIRED"  : "TRUE", "TRUSTED_CERTS_NAME" : "oamTrustCategory" ]
        task.setOutputParams(outputParams)
        when:
        def result = taskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);
        then: "exception UnexpectedErrorException should be thrown"
        UnexpectedErrorException e = thrown()
        and:
        e.getMessage() == expected
        where:
        asymmetricKeysCmpFdn || expected
        null                 || "Unexpected Internal Error : Missing ASYMMETRIC_KEYS_CMP_FDN internal parameter"
        ""                   || "Unexpected Internal Error : Missing ASYMMETRIC_KEYS_CMP_FDN internal parameter"
    }

    def 'start-cmp without ASYMMETRIC_KEY_NAME in output parameters' () {
        given: "task for node"
        task = new CbpOiPrepareOnlineEnrollmentTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : serializedScepEnrollmentInfo, "CMP_SERVER_GROUP_NAME" : "1", "ASYMMETRIC_KEYS_CMP_FDN" : "MeContext="+nodeName+",ManagedElement="+nodeName+",keystore=1,asymmetric-keys=1,asymmetric-keys\$\$cmp=1", "IS_START_CMP_REQUIRED"  : "TRUE", "TRUSTED_CERTS_NAME" : "oamTrustCategory" ]
        task.setOutputParams(outputParams)
        when:
        def result = taskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);
        then: "exception UnexpectedErrorException should be thrown"
        UnexpectedErrorException e = thrown()
        and:
        e.getMessage() == "Unexpected Internal Error : Missing ASYMMETRIC_KEY_NAME internal parameter"
    }

    @Unroll
    def 'start-cmp with invalid ASYMMETRIC_KEY_NAME (#asymmetricKeyName) in output parameters' () {
        given: "task for node"
        task = new CbpOiPrepareOnlineEnrollmentTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : serializedScepEnrollmentInfo, "CMP_SERVER_GROUP_NAME" : "1", "ASYMMETRIC_KEYS_CMP_FDN" : "MeContext="+nodeName+",ManagedElement="+nodeName+",keystore=1,asymmetric-keys=1,asymmetric-keys\$\$cmp=1", "ASYMMETRIC_KEY_NAME" : asymmetricKeyName, "IS_START_CMP_REQUIRED"  : "TRUE", "TRUSTED_CERTS_NAME" : "oamTrustCategory" ]
        task.setOutputParams(outputParams)
        when:
        def result = taskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);
        then: "exception UnexpectedErrorException should be thrown"
        UnexpectedErrorException e = thrown()
        and:
        e.getMessage() == expected
        where:
        asymmetricKeyName || expected
        null              || "Unexpected Internal Error : Missing ASYMMETRIC_KEY_NAME internal parameter"
        ""                || "Unexpected Internal Error : Missing ASYMMETRIC_KEY_NAME internal parameter"
    }

    def 'start-cmp without TRUSTED_CERTS_NAME in output parameters' () {
        given: "task for node"
        task = new CbpOiPrepareOnlineEnrollmentTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : serializedScepEnrollmentInfo, "CMP_SERVER_GROUP_NAME" : "1", "ASYMMETRIC_KEYS_CMP_FDN" : "MeContext="+nodeName+",ManagedElement="+nodeName+",keystore=1,asymmetric-keys=1,asymmetric-keys\$\$cmp=1", "ASYMMETRIC_KEY_NAME" : "oamNodeCredential", "IS_START_CMP_REQUIRED" : "TRUE" ]
        task.setOutputParams(outputParams)
        when:
        def result = taskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);
        then: "exception UnexpectedErrorException should be thrown"
        UnexpectedErrorException e = thrown()
        and:
        e.getMessage() == "Unexpected Internal Error : Missing TRUSTED_CERTS_NAME internal parameter"
    }

    @Unroll
    def 'start-cmp with invalid TRUSTED_CERTS_NAME (#trustedCertsName) in output parameters' () {
        given: "task for node"
        task = new CbpOiPrepareOnlineEnrollmentTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : serializedScepEnrollmentInfo, "CMP_SERVER_GROUP_NAME" : "1", "ASYMMETRIC_KEYS_CMP_FDN" : "MeContext="+nodeName+",ManagedElement="+nodeName+",keystore=1,asymmetric-keys=1,asymmetric-keys\$\$cmp=1", "ASYMMETRIC_KEY_NAME" : "oamNodeCredential", "IS_START_CMP_REQUIRED"  : "TRUE", "TRUSTED_CERTS_NAME" : trustedCertsName ]
        task.setOutputParams(outputParams)
        when:
        def result = taskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);
        then: "exception UnexpectedErrorException should be thrown"
        UnexpectedErrorException e = thrown()
        and:
        e.getMessage() == expected
        where:
        trustedCertsName || expected
        null             || "Unexpected Internal Error : Missing TRUSTED_CERTS_NAME internal parameter"
        ""               || "Unexpected Internal Error : Missing TRUSTED_CERTS_NAME internal parameter"
    }
}
