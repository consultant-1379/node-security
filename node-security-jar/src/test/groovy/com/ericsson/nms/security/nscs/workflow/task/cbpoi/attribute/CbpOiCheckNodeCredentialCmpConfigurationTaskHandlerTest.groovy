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

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.security.nscs.api.enums.DigestAlgorithm
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode
import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfo
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfoImpl
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.oss.itpf.security.pki.common.model.EntityInfo
import com.ericsson.oss.itpf.security.pki.common.model.Subject
import com.ericsson.oss.itpf.security.pki.common.model.SubjectField
import com.ericsson.oss.itpf.security.pki.common.model.SubjectFieldType
import com.ericsson.oss.itpf.security.pki.manager.model.EntityType
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoException
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cbpoi.attribute.CbpOiCheckNodeCredentialCmpConfigurationTask
import com.ericsson.oss.services.security.nscs.utils.CbpOiNodeDataSetup

import spock.lang.Unroll

class CbpOiCheckNodeCredentialCmpConfigurationTaskHandlerTest extends CbpOiNodeDataSetup {

    @ObjectUnderTest
    CbpOiCheckNodeCredentialCmpConfigurationTaskHandler taskHandler

    @MockedImplementation
    NscsCapabilityModelService nscsCapabilityModelService;

    private CbpOiCheckNodeCredentialCmpConfigurationTask task

    private nodeName = "vDU00001"

    def setup() {
        NscsCMReaderService.deploymentEnv = NscsCMReaderService.DeploymentEnvironment.PRODUCTION
        nscsCapabilityModelService.getComEcimDefaultNodeCredentialIds(_) >> ["OAM":"oamNodeCredential"]
        nscsCapabilityModelService.getComEcimDefaultTrustCategoryIds(_) >> ["OAM":"oamTrustCategory"]
    }

    def 'object under test injection' () {
        expect:
        taskHandler != null
    }

    @Unroll
    def 'only keystore with MeContext and ManagedElement (isReissue=#isReissue)' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with keystore under ManagedElement"
        createKeystoreUnderManagedElement()
        and: "task for node"
        task = new CbpOiCheckNodeCredentialCmpConfigurationTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task for issue or reissue"
        if (isReissue) {
            task.setIsReissue("TRUE")
        }
        and: "an enrollment info"
        Entity ee = new Entity();
        ScepEnrollmentInfo scepEnrollmentInfo = new ScepEnrollmentInfoImpl(ee, "https://localhost:8443/app/resource", null, DigestAlgorithm.MD5, 10, "challengePWD", "2048", EnrollmentMode.CMPv2_INITIAL, null, null)
        String serializedScepEnrollmentInfo = NscsObjectSerializer.writeObject(scepEnrollmentInfo)
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : serializedScepEnrollmentInfo , "CMP_SERVER_GROUP_NAME" : "1" ]
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
        and: "output parameters should have been changed"
        deserializedTaskResult.getOutputParams().size() == 7
        and: "ENROLLMENT_INFO output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("ENROLLMENT_INFO") == serializedScepEnrollmentInfo
        and: "CMP_SERVER_GROUP output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("CMP_SERVER_GROUP_NAME") == "1"
        and: "ASYMMETRIC_KEYS_CMP_FDN output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEYS_CMP_FDN") == "MeContext="+nodeName+",ManagedElement="+nodeName+",keystore=1,asymmetric-keys=1,asymmetric-keys\$\$cmp=1"
        and: "ASYMMETRIC_KEY_NAME output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEY_NAME") == "oamNodeCredential"
        and: "RENEWAL_MODE output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("RENEWAL_MODE") == "automatic"
        and: "IS_START_CMP_REQUIRED output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("IS_START_CMP_REQUIRED") == "TRUE"
        and: "TRUSTED_CERTS_NAME output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("TRUSTED_CERTS_NAME") == "oamTrustCategory"
        and: "ASYMMETRIC_KEY_CMP_FDN output parameter should not have been added"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEY_CMP_FDN") == null
        where:
        isReissue << [true, false]
    }

    @Unroll
    def 'only keystore with ManagedElement and without MeContext (isReissue=#isReissue)' () {
        given: "node created with ManagedElement"
        createNodeWithManagedElement(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with keystore under ManagedElement"
        createKeystoreUnderManagedElement()
        and: "task for node"
        task = new CbpOiCheckNodeCredentialCmpConfigurationTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task for issue or reissue"
        if (isReissue) {
            task.setIsReissue("TRUE")
        }
        and: "an enrollment info"
        Entity ee = new Entity();
        ScepEnrollmentInfo scepEnrollmentInfo = new ScepEnrollmentInfoImpl(ee, "https://localhost:8443/app/resource", null, DigestAlgorithm.MD5, 10, "challengePWD", "2048", EnrollmentMode.CMPv2_INITIAL, null, null)
        String serializedScepEnrollmentInfo = NscsObjectSerializer.writeObject(scepEnrollmentInfo)
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : serializedScepEnrollmentInfo , "CMP_SERVER_GROUP_NAME" : "1" ]
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
        and: "output parameters should have been changed"
        deserializedTaskResult.getOutputParams().size() == 7
        and: "ENROLLMENT_INFO output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("ENROLLMENT_INFO") == serializedScepEnrollmentInfo
        and: "CMP_SERVER_GROUP output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("CMP_SERVER_GROUP_NAME") == "1"
        and: "ASYMMETRIC_KEYS_CMP_FDN output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEYS_CMP_FDN") == "ManagedElement="+nodeName+",keystore=1,asymmetric-keys=1,asymmetric-keys\$\$cmp=1"
        and: "ASYMMETRIC_KEY_NAME output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEY_NAME") == "oamNodeCredential"
        and: "RENEWAL_MODE output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("RENEWAL_MODE") == "automatic"
        and: "IS_START_CMP_REQUIRED output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("IS_START_CMP_REQUIRED") == "TRUE"
        and: "TRUSTED_CERTS_NAME output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("TRUSTED_CERTS_NAME") == "oamTrustCategory"
        and: "ASYMMETRIC_KEY_CMP_FDN output parameter should not have been added"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEY_CMP_FDN") == null
        where:
        isReissue << [true, false]
    }

    @Unroll
    def 'only keystore with MeContext and without ManagedElement (isReissue=#isReissue)' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with keystore under MeContext"
        createKeystoreUnderMeContext()
        and: "task for node"
        task = new CbpOiCheckNodeCredentialCmpConfigurationTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task for issue or reissue"
        if (isReissue) {
            task.setIsReissue("TRUE")
        }
        and: "an enrollment info"
        Entity ee = new Entity();
        ScepEnrollmentInfo scepEnrollmentInfo = new ScepEnrollmentInfoImpl(ee, "https://localhost:8443/app/resource", null, DigestAlgorithm.MD5, 10, "challengePWD", "2048", EnrollmentMode.CMPv2_INITIAL, null, null)
        String serializedScepEnrollmentInfo = NscsObjectSerializer.writeObject(scepEnrollmentInfo)
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : serializedScepEnrollmentInfo , "CMP_SERVER_GROUP_NAME" : "1" ]
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
        and: "output parameters should have been changed"
        deserializedTaskResult.getOutputParams().size() == 7
        and: "ENROLLMENT_INFO output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("ENROLLMENT_INFO") == serializedScepEnrollmentInfo
        and: "CMP_SERVER_GROUP output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("CMP_SERVER_GROUP_NAME") == "1"
        and: "ASYMMETRIC_KEYS_CMP_FDN output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEYS_CMP_FDN") == "MeContext="+nodeName+",keystore=1,asymmetric-keys=1,asymmetric-keys\$\$cmp=1"
        and: "ASYMMETRIC_KEY_NAME output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEY_NAME") == "oamNodeCredential"
        and: "RENEWAL_MODE output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("RENEWAL_MODE") == "automatic"
        and: "IS_START_CMP_REQUIRED output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("IS_START_CMP_REQUIRED") == "TRUE"
        and: "TRUSTED_CERTS_NAME output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("TRUSTED_CERTS_NAME") == "oamTrustCategory"
        and: "ASYMMETRIC_KEY_CMP_FDN output parameter should not have been added"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEY_CMP_FDN") == null
        where:
        isReissue << [true, false]
    }

    @Unroll
    def 'only asymmetric-keys (isReissue=#isReissue, isAsymmetricKeysCmpPresent=#isAsymmetricKeysCmpPresent)' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with keystore under ManagedElement"
        createKeystoreUnderManagedElement()
        and: "with asymmetric-keys under keystore"
        createAsymmetricKeysUnderKeystore()
        and: "with or without cmp under asymmetric-keys"
        if (isAsymmetricKeysCmpPresent) {
            createAsymmetricKeysCmpUnderAsymmetricKeys()
        }
        and: "task for node"
        task = new CbpOiCheckNodeCredentialCmpConfigurationTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task for issue or reissue"
        if (isReissue) {
            task.setIsReissue("TRUE")
        }
        and: "an enrollment info"
        Entity ee = buildEndEntity(false)
        ScepEnrollmentInfo scepEnrollmentInfo = new ScepEnrollmentInfoImpl(ee, "https://localhost:8443/app/resource", null, DigestAlgorithm.MD5, 10, "challengePWD", "2048", EnrollmentMode.CMPv2_INITIAL, null, null)
        String serializedScepEnrollmentInfo = NscsObjectSerializer.writeObject(scepEnrollmentInfo)
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : serializedScepEnrollmentInfo , "CMP_SERVER_GROUP_NAME" : "1" ]
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
        and: "output parameters should have been changed"
        deserializedTaskResult.getOutputParams().size() == 7
        and: "ENROLLMENT_INFO output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("ENROLLMENT_INFO") == serializedScepEnrollmentInfo
        and: "CMP_SERVER_GROUP output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("CMP_SERVER_GROUP_NAME") == "1"
        and: "ASYMMETRIC_KEYS_CMP_FDN output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEYS_CMP_FDN") == "MeContext="+nodeName+",ManagedElement="+nodeName+",keystore=1,asymmetric-keys=1,asymmetric-keys\$\$cmp=1"
        and: "RENEWAL_MODE output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("RENEWAL_MODE") == "automatic"
        and: "IS_START_CMP_REQUIRED output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("IS_START_CMP_REQUIRED") == "TRUE"
        and: "ASYMMETRIC_KEY_NAME output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEY_NAME") == "oamNodeCredential"
        and: "TRUSTED_CERTS_NAME output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("TRUSTED_CERTS_NAME") == "oamTrustCategory"
        and: "ASYMMETRIC_KEY_CMP_FDN output parameter should not have been added"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEY_CMP_FDN") == null
        where:
        isReissue << [true, false, true, false]
        isAsymmetricKeysCmpPresent << [true, false, false, true]
    }

    @Unroll
    def 'offline scenario with asymmetric-key (isReissue=#isReissue)' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with keystore under ManagedElement"
        createKeystoreUnderManagedElement()
        and: "with asymmetric-keys under keystore"
        createAsymmetricKeysUnderKeystore()
        and: "with asymmetric-key under asymmetric-keys"
        createAsymmetricKeyUnderAsymmetricKeys("oamNodeCredential")
        and: "task for node"
        task = new CbpOiCheckNodeCredentialCmpConfigurationTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task for issue or reissue"
        if (isReissue) {
            task.setIsReissue("TRUE")
        }
        and: "an enrollment info"
        Entity ee = new Entity();
        ScepEnrollmentInfo scepEnrollmentInfo = new ScepEnrollmentInfoImpl(ee, "https://localhost:8443/app/resource", null, DigestAlgorithm.MD5, 10, "challengePWD", "2048", EnrollmentMode.CMPv2_INITIAL, null, null)
        String serializedScepEnrollmentInfo = NscsObjectSerializer.writeObject(scepEnrollmentInfo)
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : serializedScepEnrollmentInfo , "CMP_SERVER_GROUP_NAME" : "1" ]
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
        and: "output parameters should have been changed"
        deserializedTaskResult.getOutputParams().size() == 7
        and: "ENROLLMENT_INFO output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("ENROLLMENT_INFO") == serializedScepEnrollmentInfo
        and: "CMP_SERVER_GROUP output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("CMP_SERVER_GROUP_NAME") == "1"
        and: "ASYMMETRIC_KEYS_CMP_FDN output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEYS_CMP_FDN") == "MeContext="+nodeName+",ManagedElement="+nodeName+",keystore=1,asymmetric-keys=1,asymmetric-keys\$\$cmp=1"
        and: "ASYMMETRIC_KEY_NAME output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEY_NAME") == "oamNodeCredential"
        and: "RENEWAL_MODE output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("RENEWAL_MODE") == "automatic"
        and: "IS_START_CMP_REQUIRED output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("IS_START_CMP_REQUIRED") == "TRUE"
        and: "TRUSTED_CERTS_NAME output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("TRUSTED_CERTS_NAME") == "oamTrustCategory"
        and: "ASYMMETRIC_KEY_CMP_FDN output parameter should not have been added"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEY_CMP_FDN") == null
        where:
        isReissue << [true, false]
    }

    @Unroll
    def 'offline scenario with unexpected asymmetric-key name (isReissue=#isReissue)' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with keystore under ManagedElement"
        createKeystoreUnderManagedElement()
        and: "with asymmetric-keys under keystore"
        createAsymmetricKeysUnderKeystore()
        and: "with asymmetric-key under asymmetric-keys"
        createAsymmetricKeyUnderAsymmetricKeys("unexpected")
        and: "task for node"
        task = new CbpOiCheckNodeCredentialCmpConfigurationTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task for issue or reissue"
        if (isReissue) {
            task.setIsReissue("TRUE")
        }
        and: "an enrollment info"
        Entity ee = new Entity();
        ScepEnrollmentInfo scepEnrollmentInfo = new ScepEnrollmentInfoImpl(ee, "https://localhost:8443/app/resource", null, DigestAlgorithm.MD5, 10, "challengePWD", "2048", EnrollmentMode.CMPv2_INITIAL, null, null)
        String serializedScepEnrollmentInfo = NscsObjectSerializer.writeObject(scepEnrollmentInfo)
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : serializedScepEnrollmentInfo , "CMP_SERVER_GROUP_NAME" : "1" ]
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
        and: "output parameters should have been changed"
        deserializedTaskResult.getOutputParams().size() == 7
        and: "ENROLLMENT_INFO output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("ENROLLMENT_INFO") == serializedScepEnrollmentInfo
        and: "CMP_SERVER_GROUP output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("CMP_SERVER_GROUP_NAME") == "1"
        and: "ASYMMETRIC_KEYS_CMP_FDN output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEYS_CMP_FDN") == "MeContext="+nodeName+",ManagedElement="+nodeName+",keystore=1,asymmetric-keys=1,asymmetric-keys\$\$cmp=1"
        and: "ASYMMETRIC_KEY_NAME output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEY_NAME") == "oamNodeCredential"
        and: "RENEWAL_MODE output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("RENEWAL_MODE") == "automatic"
        and: "IS_START_CMP_REQUIRED output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("IS_START_CMP_REQUIRED") == "TRUE"
        and: "TRUSTED_CERTS_NAME output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("TRUSTED_CERTS_NAME") == "oamTrustCategory"
        and: "ASYMMETRIC_KEY_CMP_FDN output parameter should not have been added"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEY_CMP_FDN") == null
        where:
        isReissue << [true, false]
    }

    @Unroll
    def 'Day 0 scenario with asymmetric-key cmp (isReissue=#isReissue, isAsymmetricKeysCmpPresent=#isAsymmetricKeysCmpPresent, renewalMode=#renewalMode)' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with keystore under ManagedElement"
        createKeystoreUnderManagedElement()
        and: "with cmp under keystore"
        createCmpUnderKeystore()
        and: "with cmp server groups under cmp"
        createCmpServerGroupsUnderCmp()
        and: "with cmp server group under cmp server groups"
        createCmpServerGroupUnderCmpServerGroups("1")
        and: "with asymmetric-keys under keystore"
        createAsymmetricKeysUnderKeystore()
        and: "with or without cmp under asymmetric-keys"
        if (isAsymmetricKeysCmpPresent) {
            createAsymmetricKeysCmpUnderAsymmetricKeys()
        }
        and: "with asymmetric-key under asymmetric-keys"
        createAsymmetricKeyUnderAsymmetricKeys("oamNodeCredential")
        and: "with asymmetric-key cmp under asymmetric-key"
        createAsymmetricKeyCmpUnderAsymmetricKey(renewalMode, "1", "oamTrustCategory")
        and: "certificates hierarchy under asymmetric-key"
        issueOamCertificateUnderOamNodeCredential()
        and: "task for node"
        task = new CbpOiCheckNodeCredentialCmpConfigurationTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task for issue or reissue"
        if (isReissue) {
            task.setIsReissue("TRUE")
        }
        and: "an enrollment info"
        Entity ee = buildEndEntity(false)
        ScepEnrollmentInfo scepEnrollmentInfo = new ScepEnrollmentInfoImpl(ee, "https://localhost:8443/app/resource", null, DigestAlgorithm.MD5, 10, "challengePWD", "2048", EnrollmentMode.CMPv2_INITIAL, null, null)
        String serializedScepEnrollmentInfo = NscsObjectSerializer.writeObject(scepEnrollmentInfo)
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : serializedScepEnrollmentInfo , "CMP_SERVER_GROUP_NAME" : "1" ]
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
        and: "output parameters should have been changed"
        deserializedTaskResult.getOutputParams().size() == 7
        and: "ENROLLMENT_INFO output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("ENROLLMENT_INFO") == serializedScepEnrollmentInfo
        and: "CMP_SERVER_GROUP output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("CMP_SERVER_GROUP_NAME") == "1"
        and: "ASYMMETRIC_KEYS_CMP_FDN output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEYS_CMP_FDN") == "MeContext="+nodeName+",ManagedElement="+nodeName+",keystore=1,asymmetric-keys=1,asymmetric-keys\$\$cmp=1"
        and: "ASYMMETRIC_KEY_NAME output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEY_NAME") == "oamNodeCredential"
        and: "ASYMMETRIC_KEY_CMP_FDN output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEY_CMP_FDN") == "MeContext="+nodeName+",ManagedElement="+nodeName+",keystore=1,asymmetric-keys=1,asymmetric-key=oamNodeCredential,asymmetric-key\$\$cmp=1"
        and: "RENEWAL_MODE output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("RENEWAL_MODE") == expectedRenewalMode
        and: "TRUSTED_CERTS_NAME output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("TRUSTED_CERTS_NAME") == "oamTrustCategory"
        and: "IS_START_CMP_REQUIRED output parameter should not have been added"
        deserializedTaskResult.getOutputParams().get("IS_START_CMP_REQUIRED") == null
        where:
        renewalMode | isReissue | isAsymmetricKeysCmpPresent || expectedRenewalMode
        "manual"    | true      | true                       || "manual"
        "manual"    | true      | false                      || "manual"
        "manual"    | false     | true                       || "manual"
        "manual"    | false     | false                      || "manual"
        "automatic" | true      | true                       || "automatic"
        "automatic" | true      | false                      || "automatic"
        "automatic" | false     | true                       || "automatic"
        "automatic" | false     | false                      || "automatic"
    }

    @Unroll
    def 'Day 0 scenario with asymmetric-key cmp and possibly changed subject name (isReissue=#isReissue useLocalityName=#useLocalityName)' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with keystore under ManagedElement"
        createKeystoreUnderManagedElement()
        and: "with cmp under keystore"
        createCmpUnderKeystore()
        and: "with cmp server groups under cmp"
        createCmpServerGroupsUnderCmp()
        and: "with cmp server group under cmp server groups"
        createCmpServerGroupUnderCmpServerGroups("1")
        and: "with asymmetric-keys under keystore"
        createAsymmetricKeysUnderKeystore()
        and: "with cmp under asymmetric-keys"
        createAsymmetricKeysCmpUnderAsymmetricKeys()
        and: "with asymmetric-key under asymmetric-keys"
        createAsymmetricKeyUnderAsymmetricKeys("oamNodeCredential")
        and: "with asymmetric-key cmp under asymmetric-key"
        createAsymmetricKeyCmpUnderAsymmetricKey("automatic", "1", "oamTrustCategory")
        and: "certificates hierarchy under asymmetric-key"
        issueOamCertificateUnderOamNodeCredential()
        and: "task for node"
        task = new CbpOiCheckNodeCredentialCmpConfigurationTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task for issue or reissue"
        if (isReissue) {
            task.setIsReissue("TRUE")
        }
        and: "an enrollment info"
        Entity ee = buildEndEntity(useLocalityName)
        ScepEnrollmentInfo scepEnrollmentInfo = new ScepEnrollmentInfoImpl(ee, "https://localhost:8443/app/resource", null, DigestAlgorithm.MD5, 10, "challengePWD", "2048", EnrollmentMode.CMPv2_INITIAL, null, null)
        String serializedScepEnrollmentInfo = NscsObjectSerializer.writeObject(scepEnrollmentInfo)
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : serializedScepEnrollmentInfo , "CMP_SERVER_GROUP_NAME" : "1" ]
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
        and: "output parameters should have been changed"
        deserializedTaskResult.getOutputParams().size() == expectedOutputParamsSize
        and: "ENROLLMENT_INFO output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("ENROLLMENT_INFO") == serializedScepEnrollmentInfo
        and: "CMP_SERVER_GROUP output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("CMP_SERVER_GROUP_NAME") == "1"
        and: "ASYMMETRIC_KEYS_CMP_FDN output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEYS_CMP_FDN") == "MeContext="+nodeName+",ManagedElement="+nodeName+",keystore=1,asymmetric-keys=1,asymmetric-keys\$\$cmp=1"
        and: "ASYMMETRIC_KEY_NAME output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEY_NAME") == "oamNodeCredential"
        and: "ASYMMETRIC_KEY_CMP_FDN output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEY_CMP_FDN") == "MeContext="+nodeName+",ManagedElement="+nodeName+",keystore=1,asymmetric-keys=1,asymmetric-key=oamNodeCredential,asymmetric-key\$\$cmp=1"
        and: "RENEWAL_MODE output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("RENEWAL_MODE") == "automatic"
        and: "TRUSTED_CERTS_NAME output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("TRUSTED_CERTS_NAME") == "oamTrustCategory"
        and: "IS_START_CMP_REQUIRED output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("IS_START_CMP_REQUIRED") == expectedIsStartCmpRequired
        where:
        isReissue | useLocalityName || expectedOutputParamsSize | expectedIsStartCmpRequired
        false     | false           || 7                        | null
        false     | true            || 8                        | "TRUE"
        true      | false           || 7                        | null
        true      | true            || 8                        | "TRUE"
    }

    def 'Day 0 scenario with asymmetric-key cmp and without certificates under asymmetric-key' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with keystore under ManagedElement"
        createKeystoreUnderManagedElement()
        and: "with cmp under keystore"
        createCmpUnderKeystore()
        and: "with cmp server groups under cmp"
        createCmpServerGroupsUnderCmp()
        and: "with cmp server group under cmp server groups"
        createCmpServerGroupUnderCmpServerGroups("1")
        and: "with asymmetric-keys under keystore"
        createAsymmetricKeysUnderKeystore()
        and: "with cmp under asymmetric-keys"
        createAsymmetricKeysCmpUnderAsymmetricKeys()
        and: "with asymmetric-key under asymmetric-keys"
        createAsymmetricKeyUnderAsymmetricKeys("oamNodeCredential")
        and: "with asymmetric-key cmp under asymmetric-key"
        createAsymmetricKeyCmpUnderAsymmetricKey("automatic", "1", "oamTrustCategory")
        and: "task for node"
        task = new CbpOiCheckNodeCredentialCmpConfigurationTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "an enrollment info"
        Entity ee = buildEndEntity(false)
        ScepEnrollmentInfo scepEnrollmentInfo = new ScepEnrollmentInfoImpl(ee, "https://localhost:8443/app/resource", null, DigestAlgorithm.MD5, 10, "challengePWD", "2048", EnrollmentMode.CMPv2_INITIAL, null, null)
        String serializedScepEnrollmentInfo = NscsObjectSerializer.writeObject(scepEnrollmentInfo)
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : serializedScepEnrollmentInfo , "CMP_SERVER_GROUP_NAME" : "1" ]
        task.setOutputParams(outputParams)
        when:
        def result = taskHandler.processTask(task)
        then: "exception MissingMoException should be thrown"
        MissingMoException e = thrown()
        and:
        e.getMessage().startsWith("Missing certificates MO under asymmetric-key ")
    }

    def 'Day 0 scenario with asymmetric-key cmp and without certificate under certificates under asymmetric-key' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with keystore under ManagedElement"
        createKeystoreUnderManagedElement()
        and: "with cmp under keystore"
        createCmpUnderKeystore()
        and: "with cmp server groups under cmp"
        createCmpServerGroupsUnderCmp()
        and: "with cmp server group under cmp server groups"
        createCmpServerGroupUnderCmpServerGroups("1")
        and: "with asymmetric-keys under keystore"
        createAsymmetricKeysUnderKeystore()
        and: "with cmp under asymmetric-keys"
        createAsymmetricKeysCmpUnderAsymmetricKeys()
        and: "with asymmetric-key under asymmetric-keys"
        createAsymmetricKeyUnderAsymmetricKeys("oamNodeCredential")
        and: "with asymmetric-key cmp under asymmetric-key"
        createAsymmetricKeyCmpUnderAsymmetricKey("automatic", "1", "oamTrustCategory")
        and: "with certificates under asymmetric-key"
        createCertificatesUnderAsymmetricKey("oamNodeCredential")
        and: "task for node"
        task = new CbpOiCheckNodeCredentialCmpConfigurationTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "an enrollment info"
        Entity ee = buildEndEntity(false)
        ScepEnrollmentInfo scepEnrollmentInfo = new ScepEnrollmentInfoImpl(ee, "https://localhost:8443/app/resource", null, DigestAlgorithm.MD5, 10, "challengePWD", "2048", EnrollmentMode.CMPv2_INITIAL, null, null)
        String serializedScepEnrollmentInfo = NscsObjectSerializer.writeObject(scepEnrollmentInfo)
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : serializedScepEnrollmentInfo , "CMP_SERVER_GROUP_NAME" : "1" ]
        task.setOutputParams(outputParams)
        when:
        def result = taskHandler.processTask(task)
        then: "exception MissingMoException should be thrown"
        MissingMoException e = thrown()
        and:
        e.getMessage().startsWith("Missing certificate MO ")
    }

    @Unroll
    def 'Day 0 scenario with unexpected asymmetric-key name (isReissue=#isReissue, isAsymmetricKeysCmpPresent=#isAsymmetricKeysCmpPresent, renewalMode=#renewalMode)' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with keystore under ManagedElement"
        createKeystoreUnderManagedElement()
        and: "with cmp under keystore"
        createCmpUnderKeystore()
        and: "with cmp server groups under cmp"
        createCmpServerGroupsUnderCmp()
        and: "with cmp server group under cmp server groups"
        createCmpServerGroupUnderCmpServerGroups("1")
        and: "with asymmetric-keys under keystore"
        createAsymmetricKeysUnderKeystore()
        and: "with or without cmp under asymmetric-keys"
        if (isAsymmetricKeysCmpPresent) {
            createAsymmetricKeysCmpUnderAsymmetricKeys()
        }
        and: "with asymmetric-key under asymmetric-keys"
        createAsymmetricKeyUnderAsymmetricKeys("unexpected")
        and: "with asymmetric-key cmp under asymmetric-key"
        createAsymmetricKeyCmpUnderAsymmetricKey(renewalMode, "1", "oamTrustCategory")
        and: "task for node"
        task = new CbpOiCheckNodeCredentialCmpConfigurationTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task for issue or reissue"
        if (isReissue) {
            task.setIsReissue("TRUE")
        }
        and: "an enrollment info"
        Entity ee = new Entity();
        ScepEnrollmentInfo scepEnrollmentInfo = new ScepEnrollmentInfoImpl(ee, "https://localhost:8443/app/resource", null, DigestAlgorithm.MD5, 10, "challengePWD", "2048", EnrollmentMode.CMPv2_INITIAL, null, null)
        String serializedScepEnrollmentInfo = NscsObjectSerializer.writeObject(scepEnrollmentInfo)
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : serializedScepEnrollmentInfo , "CMP_SERVER_GROUP_NAME" : "1" ]
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
        and: "output parameters should have been changed"
        deserializedTaskResult.getOutputParams().size() == 7
        and: "ENROLLMENT_INFO output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("ENROLLMENT_INFO") == serializedScepEnrollmentInfo
        and: "CMP_SERVER_GROUP output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("CMP_SERVER_GROUP_NAME") == "1"
        and: "ASYMMETRIC_KEYS_CMP_FDN output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEYS_CMP_FDN") == "MeContext="+nodeName+",ManagedElement="+nodeName+",keystore=1,asymmetric-keys=1,asymmetric-keys\$\$cmp=1"
        and: "ASYMMETRIC_KEY_NAME output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEY_NAME") == "oamNodeCredential"
        and: "RENEWAL_MODE output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("RENEWAL_MODE") == expectedRenewalMode
        and: "IS_START_CMP_REQUIRED output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("IS_START_CMP_REQUIRED") == "TRUE"
        and: "TRUSTED_CERTS_NAME output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("TRUSTED_CERTS_NAME") == "oamTrustCategory"
        and: "ASYMMETRIC_KEY_CMP_FDN output parameter should not have been added"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEY_CMP_FDN") == null
        where:
        renewalMode | isReissue | isAsymmetricKeysCmpPresent || expectedRenewalMode
        "manual"    | true      | true                       || "automatic"
        "manual"    | true      | false                      || "automatic"
        "manual"    | false     | true                       || "automatic"
        "manual"    | false     | false                      || "automatic"
        "automatic" | true      | true                       || "automatic"
        "automatic" | true      | false                      || "automatic"
        "automatic" | false     | true                       || "automatic"
        "automatic" | false     | false                      || "automatic"
    }

    @Unroll
    def 'Day 0 scenario with unexpected asymmetric-key cmp attributes (isReissue=#isReissue, isAsymmetricKeysCmpPresent=#isAsymmetricKeysCmpPresent, renewalMode=#renewalMode, cmpServerGroupName=#cmpServerGroupName, trustedCerts=#trustedCerts)' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with keystore under ManagedElement"
        createKeystoreUnderManagedElement()
        and: "with cmp under keystore"
        createCmpUnderKeystore()
        and: "with cmp server groups under cmp"
        createCmpServerGroupsUnderCmp()
        and: "with cmp server group under cmp server groups"
        createCmpServerGroupUnderCmpServerGroups("1")
        and: "with asymmetric-keys under keystore"
        createAsymmetricKeysUnderKeystore()
        and: "with or without cmp under asymmetric-keys"
        if (isAsymmetricKeysCmpPresent) {
            createAsymmetricKeysCmpUnderAsymmetricKeys()
        }
        and: "with asymmetric-key under asymmetric-keys"
        createAsymmetricKeyUnderAsymmetricKeys("oamNodeCredential")
        and: "with asymmetric-key cmp under asymmetric-key"
        createAsymmetricKeyCmpUnderAsymmetricKey(renewalMode, cmpServerGroupName, trustedCerts)
        and: "certificates hierarchy under asymmetric-key"
        issueOamCertificateUnderOamNodeCredential()
        and: "task for node"
        task = new CbpOiCheckNodeCredentialCmpConfigurationTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task for issue or reissue"
        if (isReissue) {
            task.setIsReissue("TRUE")
        }
        and: "an enrollment info"
        Entity ee = buildEndEntity(false)
        ScepEnrollmentInfo scepEnrollmentInfo = new ScepEnrollmentInfoImpl(ee, "https://localhost:8443/app/resource", null, DigestAlgorithm.MD5, 10, "challengePWD", "2048", EnrollmentMode.CMPv2_INITIAL, null, null)
        String serializedScepEnrollmentInfo = NscsObjectSerializer.writeObject(scepEnrollmentInfo)
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : serializedScepEnrollmentInfo , "CMP_SERVER_GROUP_NAME" : "1" ]
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
        and: "output parameters should have been changed"
        deserializedTaskResult.getOutputParams().size() == 8
        and: "ENROLLMENT_INFO output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("ENROLLMENT_INFO") == serializedScepEnrollmentInfo
        and: "CMP_SERVER_GROUP output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("CMP_SERVER_GROUP_NAME") == "1"
        and: "ASYMMETRIC_KEYS_CMP_FDN output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEYS_CMP_FDN") == "MeContext="+nodeName+",ManagedElement="+nodeName+",keystore=1,asymmetric-keys=1,asymmetric-keys\$\$cmp=1"
        and: "ASYMMETRIC_KEY_NAME output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEY_NAME") == "oamNodeCredential"
        and: "RENEWAL_MODE output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("RENEWAL_MODE") == expectedRenewalMode
        and: "IS_START_CMP_REQUIRED output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("IS_START_CMP_REQUIRED") == "TRUE"
        and: "TRUSTED_CERTS_NAME output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("TRUSTED_CERTS_NAME") == "oamTrustCategory"
        and: "ASYMMETRIC_KEY_CMP_FDN output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEY_CMP_FDN") == "MeContext="+nodeName+",ManagedElement="+nodeName+",keystore=1,asymmetric-keys=1,asymmetric-key=oamNodeCredential,asymmetric-key\$\$cmp=1"
        where:
        renewalMode | isReissue | isAsymmetricKeysCmpPresent | cmpServerGroupName | trustedCerts       || expectedRenewalMode
        "manual"    | true      | true                       | "1"                | ""                 || "manual"
        "manual"    | true      | true                       | "1"                | "unexpected"       || "manual"
        "manual"    | true      | true                       | "1"                | null               || "manual"
        "manual"    | true      | true                       | "2"                | "oamTrustCategory" || "manual"
        "manual"    | true      | false                      | "1"                | ""                 || "manual"
        "manual"    | true      | false                      | "1"                | "unexpected"       || "manual"
        "manual"    | true      | false                      | "1"                | null               || "manual"
        "manual"    | true      | false                      | "2"                | "oamTrustCategory" || "manual"
        "manual"    | false     | true                       | "1"                | ""                 || "manual"
        "manual"    | false     | true                       | "1"                | "unexpected"       || "manual"
        "manual"    | false     | true                       | "1"                | null               || "manual"
        "manual"    | false     | true                       | "2"                | "oamTrustCategory" || "manual"
        "manual"    | false     | false                      | "1"                | ""                 || "manual"
        "manual"    | false     | false                      | "1"                | "unexpected"       || "manual"
        "manual"    | false     | false                      | "1"                | null               || "manual"
        "manual"    | false     | false                      | "2"                | "oamTrustCategory" || "manual"
        "automatic" | true      | true                       | "1"                | ""                 || "automatic"
        "automatic" | true      | true                       | "1"                | "unexpected"       || "automatic"
        "automatic" | true      | true                       | "1"                | null               || "automatic"
        "automatic" | true      | true                       | "2"                | "oamTrustCategory" || "automatic"
        "automatic" | true      | false                      | "1"                | ""                 || "automatic"
        "automatic" | true      | false                      | "1"                | "unexpected"       || "automatic"
        "automatic" | true      | false                      | "1"                | null               || "automatic"
        "automatic" | true      | false                      | "2"                | "oamTrustCategory" || "automatic"
        "automatic" | false     | true                       | "1"                | ""                 || "automatic"
        "automatic" | false     | true                       | "1"                | "unexpected"       || "automatic"
        "automatic" | false     | true                       | "1"                | null               || "automatic"
        "automatic" | false     | true                       | "2"                | "oamTrustCategory" || "automatic"
        "automatic" | false     | false                      | "1"                | ""                 || "automatic"
        "automatic" | false     | false                      | "1"                | "unexpected"       || "automatic"
        "automatic" | false     | false                      | "1"                | null               || "automatic"
        "automatic" | false     | false                      | "2"                | "oamTrustCategory" || "automatic"
    }

    @Unroll
    def 'netsim scenario with asymmetric-key cmp (isReissue=#isReissue)' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with keystore under ManagedElement"
        createKeystoreUnderManagedElement()
        and: "with cmp under keystore"
        createCmpUnderKeystore()
        and: "with cmp server groups under cmp"
        createCmpServerGroupsUnderCmp()
        and: "with cmp server group under cmp server groups"
        createCmpServerGroupUnderCmpServerGroups("1")
        and: "with asymmetric-keys under keystore"
        createAsymmetricKeysUnderKeystore()
        and: "with cmp under asymmetric-keys"
        createAsymmetricKeysCmpUnderAsymmetricKeys()
        and: "with asymmetric-key under asymmetric-keys"
        createAsymmetricKeyUnderAsymmetricKeys("oamNodeCredential")
        and: "with asymmetric-key cmp under asymmetric-key"
        createAsymmetricKeyCmpUnderAsymmetricKey("manual", "", "")
        and: "certificates hierarchy under asymmetric-key"
        issueOamCertificateUnderOamNodeCredential()
        and: "task for node"
        task = new CbpOiCheckNodeCredentialCmpConfigurationTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task for issue or reissue"
        if (isReissue) {
            task.setIsReissue("TRUE")
        }
        and: "an enrollment info"
        Entity ee = buildEndEntity(false)
        ScepEnrollmentInfo scepEnrollmentInfo = new ScepEnrollmentInfoImpl(ee, "https://localhost:8443/app/resource", null, DigestAlgorithm.MD5, 10, "challengePWD", "2048", EnrollmentMode.CMPv2_INITIAL, null, null)
        String serializedScepEnrollmentInfo = NscsObjectSerializer.writeObject(scepEnrollmentInfo)
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : serializedScepEnrollmentInfo , "CMP_SERVER_GROUP_NAME" : "1" ]
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
        and: "output parameters should have been changed"
        deserializedTaskResult.getOutputParams().size() == 8
        and: "ENROLLMENT_INFO output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("ENROLLMENT_INFO") == serializedScepEnrollmentInfo
        and: "CMP_SERVER_GROUP output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("CMP_SERVER_GROUP_NAME") == "1"
        and: "ASYMMETRIC_KEYS_CMP_FDN output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEYS_CMP_FDN") == "MeContext="+nodeName+",ManagedElement="+nodeName+",keystore=1,asymmetric-keys=1,asymmetric-keys\$\$cmp=1"
        and: "ASYMMETRIC_KEY_NAME output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEY_NAME") == "oamNodeCredential"
        and: "ASYMMETRIC_KEY_CMP_FDN output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEY_CMP_FDN") == "MeContext="+nodeName+",ManagedElement="+nodeName+",keystore=1,asymmetric-keys=1,asymmetric-key=oamNodeCredential,asymmetric-key\$\$cmp=1"
        and: "RENEWAL_MODE output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("RENEWAL_MODE") == "manual"
        and: "TRUSTED_CERTS_NAME output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("TRUSTED_CERTS_NAME") == "oamTrustCategory"
        and: "IS_START_CMP_REQUIRED output parameter should have been added"
        deserializedTaskResult.getOutputParams().get("IS_START_CMP_REQUIRED") == "TRUE"
        where:
        isReissue << [true, false]
    }

    def 'process task executed with null output parameters' () {
        given: "task for node"
        task = new CbpOiCheckNodeCredentialCmpConfigurationTask(nodeName)
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

    def 'process task executed with null enrollment info in output parameters' () {
        given: "task for node"
        task = new CbpOiCheckNodeCredentialCmpConfigurationTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : null ]
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

    def 'process task executed without enrollment info in output parameters' () {
        given: "task for node"
        task = new CbpOiCheckNodeCredentialCmpConfigurationTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task with already set output parameters"
        def outputParams = [ : ]
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

    def 'process task executed without cmp-server-group name in output parameters' () {
        given: "task for node"
        task = new CbpOiCheckNodeCredentialCmpConfigurationTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "an enrollment info"
        Entity ee = new Entity();
        ScepEnrollmentInfo scepEnrollmentInfo = new ScepEnrollmentInfoImpl(ee, "https://localhost:8443/app/resource", null, DigestAlgorithm.MD5, 10, "challengePWD", "2048", EnrollmentMode.CMPv2_INITIAL, null, null)
        String serializedScepEnrollmentInfo = NscsObjectSerializer.writeObject(scepEnrollmentInfo)
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : serializedScepEnrollmentInfo ]
        task.setOutputParams(outputParams)
        when:
        def result = taskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);
        then: "exception UnexpectedErrorException should be thrown"
        UnexpectedErrorException e = thrown()
        and:
        e.getMessage() == "Unexpected Internal Error : Missing cmp-server-group name internal parameter"
    }

    def 'process task with MeContext and ManagedElement and without keystore' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "task for node"
        task = new CbpOiCheckNodeCredentialCmpConfigurationTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "an enrollment info"
        Entity ee = new Entity();
        ScepEnrollmentInfo scepEnrollmentInfo = new ScepEnrollmentInfoImpl(ee, "https://localhost:8443/app/resource", null, DigestAlgorithm.MD5, 10, "challengePWD", "2048", EnrollmentMode.CMPv2_INITIAL, null, null)
        String serializedScepEnrollmentInfo = NscsObjectSerializer.writeObject(scepEnrollmentInfo)
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : serializedScepEnrollmentInfo , "CMP_SERVER_GROUP_NAME" : "1" ]
        task.setOutputParams(outputParams)
        when:
        def result = taskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);
        then: "exception MissingMoException should be thrown"
        MissingMoException e = thrown()
        and:
        e.getMessage() == "Missing keystore MO for node " + nodeName
    }

    private Entity buildEndEntity(final boolean useLocalityName) {
        def Entity ee = new Entity()
        EntityInfo ei = new EntityInfo()
        Subject subject = buildSubjectName(useLocalityName)
        ei.setSubject(subject);
        ee.setEntityInfo(ei);
        ee.setType(EntityType.ENTITY);
        return ee;
    }

    private Subject buildSubjectName(final boolean useLocalityName) {
        def Subject subject = new Subject();
        List<SubjectField> subjectFields = new ArrayList<SubjectField>();
        SubjectField subjectFieldOU = new SubjectField();
        subjectFieldOU.setType(SubjectFieldType.ORGANIZATION_UNIT)
        subjectFieldOU.setValue("BUCI DUAC NAM");
        subjectFields.add(subjectFieldOU);
        SubjectField subjectFieldO = new SubjectField();
        subjectFieldO.setType(SubjectFieldType.ORGANIZATION)
        subjectFieldO.setValue("ERICSSON");
        subjectFields.add(subjectFieldO);
        SubjectField subjectFieldC = new SubjectField();
        subjectFieldC.setType(SubjectFieldType.COUNTRY_NAME)
        subjectFieldC.setValue("SE");
        subjectFields.add(subjectFieldC);
        SubjectField subjectFieldCN = new SubjectField();
        subjectFieldCN.setType(SubjectFieldType.COMMON_NAME)
        subjectFieldCN.setValue("5G116vDU001-oam");
        subjectFields.add(subjectFieldCN);
        if (useLocalityName) {
            SubjectField subjectFieldL= new SubjectField();
            subjectFieldL.setType(SubjectFieldType.LOCALITY_NAME);
            subjectFieldL.setValue("GENOA");
            subjectFields.add(subjectFieldL);
        }
        subject.setSubjectFields(subjectFields)
        return subject
    }
}
