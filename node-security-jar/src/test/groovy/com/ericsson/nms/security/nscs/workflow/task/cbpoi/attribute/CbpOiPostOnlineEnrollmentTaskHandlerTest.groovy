/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2021
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
import com.ericsson.oss.itpf.security.pki.common.model.EntityInfo
import com.ericsson.oss.itpf.security.pki.common.model.Subject
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cbpoi.attribute.CbpOiPostOnlineEnrollmentTask
import com.ericsson.oss.services.security.nscs.utils.CbpOiNodeDataSetup

class CbpOiPostOnlineEnrollmentTaskHandlerTest extends CbpOiNodeDataSetup {

    @ObjectUnderTest
    CbpOiPostOnlineEnrollmentTaskHandler taskHandler

    private CbpOiPostOnlineEnrollmentTask task

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

    def 'process task' () {
        given: "task for node"
        task = new CbpOiPostOnlineEnrollmentTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : serializedScepEnrollmentInfo, "CMP_SERVER_GROUP_NAME" : "1", "ASYMMETRIC_KEYS_CMP_FDN" : "MeContext="+nodeName+",ManagedElement="+nodeName+",keystore=1,asymmetric-keys=1,asymmetric-keys\$\$cmp=1", "ASYMMETRIC_KEY_NAME" : "oamNodeCredential" ]
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
        deserializedTaskResult.getOutputParams().size() == 6
        and: "ENROLLMENT_INFO output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("ENROLLMENT_INFO") == serializedScepEnrollmentInfo
        and: "CMP_SERVER_GROUP_NAME output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("CMP_SERVER_GROUP_NAME") == "1"
        and: "ASYMMETRIC_KEYS_CMP_FDN output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEYS_CMP_FDN") == "MeContext="+nodeName+",ManagedElement="+nodeName+",keystore=1,asymmetric-keys=1,asymmetric-keys\$\$cmp=1"
        and: "ASYMMETRIC_KEY_NAME output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEY_NAME") == "oamNodeCredential"
        and: "MAX_NUM_OF_RETRIES output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("MAX_NUM_OF_RETRIES") == "3"
        and: "REMAINING_NUM_OF_RETRIES output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("REMAINING_NUM_OF_RETRIES") == "3"
    }

    def 'process task executed with null output parameters' () {
        given: "task for node"
        task = new CbpOiPostOnlineEnrollmentTask(nodeName)
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
