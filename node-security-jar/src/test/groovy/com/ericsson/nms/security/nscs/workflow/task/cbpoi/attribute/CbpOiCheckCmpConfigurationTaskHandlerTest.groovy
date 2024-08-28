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
import com.ericsson.nms.security.nscs.api.exception.DataAccessException
import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfo
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfoImpl
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cbpoi.attribute.CbpOiCheckCmpConfigurationTask
import com.ericsson.oss.services.security.nscs.utils.CbpOiNodeDataSetup

import spock.lang.Unroll

class CbpOiCheckCmpConfigurationTaskHandlerTest extends CbpOiNodeDataSetup {

    @ObjectUnderTest
    CbpOiCheckCmpConfigurationTaskHandler taskHandler

    @MockedImplementation
    NscsCapabilityModelService nscsCapabilityModelService;

    private CbpOiCheckCmpConfigurationTask task

    private nodeName = "vDU00001"
    private enrollmentServerCA = "CN=NE_OAM_CA, C=SE, OU=BUCI DUAC NAM, O=ERICSSON"
    private certificateAuthorityName = "CN=NE_OAM_CA,C=SE,OU=BUCI DUAC NAM,O=ERICSSON"

    def setup() {
        NscsCMReaderService.deploymentEnv = NscsCMReaderService.DeploymentEnvironment.PRODUCTION
        nscsCapabilityModelService.getDefaultEnrollmentCaTrustCategoryId(_) >> ["OAM":"oamCmpCaTrustCategory"]
    }

    def 'process task executed with success with MeContext and ManagedElement' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "task for node"
        task = new CbpOiCheckCmpConfigurationTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "an enrollment info"
        Entity ee = new Entity();
        ScepEnrollmentInfo scepEnrollmentInfo = new ScepEnrollmentInfoImpl(ee, "https://localhost:8443/app/resource", null, DigestAlgorithm.MD5, 10, "challengePWD", "2048", EnrollmentMode.CMPv2_INITIAL, null, "NE_OAM_CA")
        scepEnrollmentInfo.setCertificateAuthorityDn(enrollmentServerCA)
        String serializedScepEnrollmentInfo = NscsObjectSerializer.writeObject(scepEnrollmentInfo)
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : serializedScepEnrollmentInfo ]
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
        and: "output parameters should be the original ones plus the CMP_SERVER_GROUP_NAME"
        deserializedTaskResult.getOutputParams().size() == 2
        deserializedTaskResult.getOutputParams().get("ENROLLMENT_INFO") == serializedScepEnrollmentInfo
        deserializedTaskResult.getOutputParams().get("CMP_SERVER_GROUP_NAME") == "1"
    }

    def 'process task executed with success with ManagedElement' () {
        given: "node created with MeContext"
        createNodeWithManagedElement(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "task for node"
        task = new CbpOiCheckCmpConfigurationTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "an enrollment info"
        Entity ee = new Entity();
        ScepEnrollmentInfo scepEnrollmentInfo = new ScepEnrollmentInfoImpl(ee, "https://localhost:8443/app/resource", null, DigestAlgorithm.MD5, 10, "challengePWD", "2048", EnrollmentMode.CMPv2_INITIAL, null, "NE_OAM_CA")
        scepEnrollmentInfo.setCertificateAuthorityDn(enrollmentServerCA)
        String serializedScepEnrollmentInfo = NscsObjectSerializer.writeObject(scepEnrollmentInfo)
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : serializedScepEnrollmentInfo ]
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
        and: "output parameters should be the original ones plus the CMP_SERVER_GROUP_NAME"
        deserializedTaskResult.getOutputParams().size() == 2
        deserializedTaskResult.getOutputParams().get("ENROLLMENT_INFO") == serializedScepEnrollmentInfo
        deserializedTaskResult.getOutputParams().get("CMP_SERVER_GROUP_NAME") == "1"
    }

    def 'process task executed with success with MeContext and without ManagedElement for node supporting ManagedElement as node root MO' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "task for node"
        task = new CbpOiCheckCmpConfigurationTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "an enrollment info"
        Entity ee = new Entity();
        ScepEnrollmentInfo scepEnrollmentInfo = new ScepEnrollmentInfoImpl(ee, "https://localhost:8443/app/resource", null, DigestAlgorithm.MD5, 10, "challengePWD", "2048", EnrollmentMode.CMPv2_INITIAL, null, "NE_OAM_CA")
        scepEnrollmentInfo.setCertificateAuthorityDn(enrollmentServerCA)
        String serializedScepEnrollmentInfo = NscsObjectSerializer.writeObject(scepEnrollmentInfo)
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : serializedScepEnrollmentInfo ]
        task.setOutputParams(outputParams)
        when:
        def result = taskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);
        then: "DataAccessException exception should be thrown"
        thrown(DataAccessException)
    }

    def 'process task executed with success with MeContext and without ManagedElement for node not supporting ManagedElement as node root MO' () {
        given: "node created with MeContext"
        createNodeWithMeContext(SHARED_CNF_TARGET_TYPE, SHARED_CNF_TARGET_MODEL_IDENTITY, nodeName)
        and: "task for node"
        task = new CbpOiCheckCmpConfigurationTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "an enrollment info"
        Entity ee = new Entity();
        ScepEnrollmentInfo scepEnrollmentInfo = new ScepEnrollmentInfoImpl(ee, "https://localhost:8443/app/resource", null, DigestAlgorithm.MD5, 10, "challengePWD", "2048", EnrollmentMode.CMPv2_INITIAL, null, "NE_OAM_CA")
        scepEnrollmentInfo.setCertificateAuthorityDn(enrollmentServerCA)
        String serializedScepEnrollmentInfo = NscsObjectSerializer.writeObject(scepEnrollmentInfo)
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : serializedScepEnrollmentInfo ]
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
        and: "output parameters should be the original ones plus the CMP_SERVER_GROUP_NAME"
        deserializedTaskResult.getOutputParams().size() == 2
        deserializedTaskResult.getOutputParams().get("ENROLLMENT_INFO") == serializedScepEnrollmentInfo
        deserializedTaskResult.getOutputParams().get("CMP_SERVER_GROUP_NAME") == "1"
    }

    def 'process task executed with success with keystore under ManagedElement' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with keystore under ManagedElement"
        createKeystoreUnderManagedElement()
        and: "task for node"
        task = new CbpOiCheckCmpConfigurationTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "an enrollment info"
        Entity ee = new Entity();
        ScepEnrollmentInfo scepEnrollmentInfo = new ScepEnrollmentInfoImpl(ee, "https://localhost:8443/app/resource", null, DigestAlgorithm.MD5, 10, "challengePWD", "2048", EnrollmentMode.CMPv2_INITIAL, null, "NE_OAM_CA")
        scepEnrollmentInfo.setCertificateAuthorityDn(enrollmentServerCA)
        String serializedScepEnrollmentInfo = NscsObjectSerializer.writeObject(scepEnrollmentInfo)
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : serializedScepEnrollmentInfo ]
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
        and: "output parameters should be the original ones plus the CMP_SERVER_GROUP_NAME"
        deserializedTaskResult.getOutputParams().size() == 2
        deserializedTaskResult.getOutputParams().get("ENROLLMENT_INFO") == serializedScepEnrollmentInfo
        deserializedTaskResult.getOutputParams().get("CMP_SERVER_GROUP_NAME") == "1"
    }

    def 'process task executed with success with keystore under MeContext' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with keystore under MeContext"
        createKeystoreUnderMeContext()
        and: "task for node"
        task = new CbpOiCheckCmpConfigurationTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "an enrollment info"
        Entity ee = new Entity();
        ScepEnrollmentInfo scepEnrollmentInfo = new ScepEnrollmentInfoImpl(ee, "https://localhost:8443/app/resource", null, DigestAlgorithm.MD5, 10, "challengePWD", "2048", EnrollmentMode.CMPv2_INITIAL, null, "NE_OAM_CA")
        scepEnrollmentInfo.setCertificateAuthorityDn(enrollmentServerCA)
        String serializedScepEnrollmentInfo = NscsObjectSerializer.writeObject(scepEnrollmentInfo)
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : serializedScepEnrollmentInfo ]
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
        and: "output parameters should be the original ones plus the CMP_SERVER_GROUP_NAME"
        deserializedTaskResult.getOutputParams().size() == 2
        deserializedTaskResult.getOutputParams().get("ENROLLMENT_INFO") == serializedScepEnrollmentInfo
        deserializedTaskResult.getOutputParams().get("CMP_SERVER_GROUP_NAME") == "1"
    }

    def 'process task executed with success with keystore cmp' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with keystore under ManagedElement"
        createKeystoreUnderManagedElement()
        and: "with cmp under keystore"
        createCmpUnderKeystore()
        and: "task for node"
        task = new CbpOiCheckCmpConfigurationTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "an enrollment info"
        Entity ee = new Entity();
        ScepEnrollmentInfo scepEnrollmentInfo = new ScepEnrollmentInfoImpl(ee, "https://localhost:8443/app/resource", null, DigestAlgorithm.MD5, 10, "challengePWD", "2048", EnrollmentMode.CMPv2_INITIAL, null, "NE_OAM_CA")
        scepEnrollmentInfo.setCertificateAuthorityDn(enrollmentServerCA)
        String serializedScepEnrollmentInfo = NscsObjectSerializer.writeObject(scepEnrollmentInfo)
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : serializedScepEnrollmentInfo ]
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
        and: "output parameters should be the original ones plus the CMP_SERVER_GROUP_NAME"
        deserializedTaskResult.getOutputParams().size() == 2
        deserializedTaskResult.getOutputParams().get("ENROLLMENT_INFO") == serializedScepEnrollmentInfo
        deserializedTaskResult.getOutputParams().get("CMP_SERVER_GROUP_NAME") == "1"
    }

    def 'process task executed with success with certificate authorities' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with keystore under ManagedElement"
        createKeystoreUnderManagedElement()
        and: "with cmp under keystore"
        createCmpUnderKeystore()
        and: "with certificate authorities under cmp"
        createCertificateAuthoritiesUnderCmp()
        and: "task for node"
        task = new CbpOiCheckCmpConfigurationTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "an enrollment info"
        Entity ee = new Entity();
        ScepEnrollmentInfo scepEnrollmentInfo = new ScepEnrollmentInfoImpl(ee, "https://localhost:8443/app/resource", null, DigestAlgorithm.MD5, 10, "challengePWD", "2048", EnrollmentMode.CMPv2_INITIAL, null, "NE_OAM_CA")
        scepEnrollmentInfo.setCertificateAuthorityDn(enrollmentServerCA)
        String serializedScepEnrollmentInfo = NscsObjectSerializer.writeObject(scepEnrollmentInfo)
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : serializedScepEnrollmentInfo ]
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
        and: "output parameters should be the original ones plus the CMP_SERVER_GROUP_NAME"
        deserializedTaskResult.getOutputParams().size() == 2
        deserializedTaskResult.getOutputParams().get("ENROLLMENT_INFO") == serializedScepEnrollmentInfo
        deserializedTaskResult.getOutputParams().get("CMP_SERVER_GROUP_NAME") == "1"
    }

    @Unroll
    def 'process task executed with success with certificate authority' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with keystore under ManagedElement"
        createKeystoreUnderManagedElement()
        and: "with cmp under keystore"
        createCmpUnderKeystore()
        and: "with certificate authorities under cmp"
        createCertificateAuthoritiesUnderCmp()
        and: "with certificate authority under certificate authorities"
        createCertificateAuthorityUnderCertificateAuthorities(name)
        and: "task for node"
        task = new CbpOiCheckCmpConfigurationTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "an enrollment info"
        Entity ee = new Entity();
        ScepEnrollmentInfo scepEnrollmentInfo = new ScepEnrollmentInfoImpl(ee, "https://localhost:8443/app/resource", null, DigestAlgorithm.MD5, 10, "challengePWD", "2048", EnrollmentMode.CMPv2_INITIAL, null, "NE_OAM_CA")
        scepEnrollmentInfo.setCertificateAuthorityDn(enrollmentServerCA)
        String serializedScepEnrollmentInfo = NscsObjectSerializer.writeObject(scepEnrollmentInfo)
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : serializedScepEnrollmentInfo ]
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
        deserializedTaskResult.getResult() == expected
        and: "output parameters should be the original ones plus the CMP_SERVER_GROUP_NAME"
        deserializedTaskResult.getOutputParams().size() == 2
        deserializedTaskResult.getOutputParams().get("ENROLLMENT_INFO") == serializedScepEnrollmentInfo
        deserializedTaskResult.getOutputParams().get("CMP_SERVER_GROUP_NAME") == "1"
        where:
        name                                               || expected
        "CN=NE_OAM_CA,C=SE,OU=BUCI_DUAC_NAM,O=ERICSSON"    || "PASSED"
        "CN=NE_OAM_CA,C=SE,OU=BUCI DUAC NAM,O=ERICSSON"    || "PASSED"
        "CN=NE_OAM_CA, C=SE, OU=BUCI_DUAC_NAM, O=ERICSSON" || "PASSED"
        "CN=NE_OAM_CA, C=SE, OU=BUCI DUAC NAM, O=ERICSSON" || "PASSED"
        "O=ERICSSON,OU=BUCI_DUAC_NAM,C=SE,CN=NE_OAM_CA"    || "PASSED"
        "O=ERICSSON,OU=BUCI DUAC NAM,C=SE,CN=NE_OAM_CA"    || "PASSED"
        "CN=NE_OAM_CA,C=SE,O=KI,OU=RANI"                   || "PASSED"
        "CN=NE_OAM_CA, C=SE, O=KI, OU=RANI"                || "PASSED"
    }

    def 'process task executed with success with certificate authority and cmp server groups' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with keystore under ManagedElement"
        createKeystoreUnderManagedElement()
        and: "with cmp under keystore"
        createCmpUnderKeystore()
        and: "with certificate authorities under cmp"
        createCertificateAuthoritiesUnderCmp()
        and: "with certificate authority under certificate authorities"
        createCertificateAuthorityUnderCertificateAuthorities(certificateAuthorityName)
        and: "with cmp server groups under cmp"
        createCmpServerGroupsUnderCmp()
        and: "task for node"
        task = new CbpOiCheckCmpConfigurationTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "an enrollment info"
        Entity ee = new Entity();
        ScepEnrollmentInfo scepEnrollmentInfo = new ScepEnrollmentInfoImpl(ee, "https://localhost:8443/app/resource", null, DigestAlgorithm.MD5, 10, "challengePWD", "2048", EnrollmentMode.CMPv2_INITIAL, null, "NE_OAM_CA")
        scepEnrollmentInfo.setCertificateAuthorityDn(enrollmentServerCA)
        String serializedScepEnrollmentInfo = NscsObjectSerializer.writeObject(scepEnrollmentInfo)
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : serializedScepEnrollmentInfo ]
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
        and: "output parameters should be the original ones plus the CMP_SERVER_GROUP_NAME"
        deserializedTaskResult.getOutputParams().size() == 2
        deserializedTaskResult.getOutputParams().get("ENROLLMENT_INFO") == serializedScepEnrollmentInfo
        deserializedTaskResult.getOutputParams().get("CMP_SERVER_GROUP_NAME") == "1"
    }

    @Unroll
    def 'process task executed with success with certificate authority and cmp server group' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with keystore under ManagedElement"
        createKeystoreUnderManagedElement()
        and: "with cmp under keystore"
        createCmpUnderKeystore()
        and: "with certificate authorities under cmp"
        createCertificateAuthoritiesUnderCmp()
        and: "with certificate authority under certificate authorities"
        createCertificateAuthorityUnderCertificateAuthorities(certificateAuthorityName)
        and: "with cmp server groups under cmp"
        createCmpServerGroupsUnderCmp()
        and: "with cmp server group under cmp server groups"
        createCmpServerGroupUnderCmpServerGroups(name)
        and: "task for node"
        task = new CbpOiCheckCmpConfigurationTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "an enrollment info"
        Entity ee = new Entity();
        ScepEnrollmentInfo scepEnrollmentInfo = new ScepEnrollmentInfoImpl(ee, "https://localhost:8443/app/resource", null, DigestAlgorithm.MD5, 10, "challengePWD", "2048", EnrollmentMode.CMPv2_INITIAL, null, "NE_OAM_CA")
        scepEnrollmentInfo.setCertificateAuthorityDn(enrollmentServerCA)
        String serializedScepEnrollmentInfo = NscsObjectSerializer.writeObject(scepEnrollmentInfo)
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : serializedScepEnrollmentInfo ]
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
        deserializedTaskResult.getResult() == expectedResult
        and: "output parameters should be the original ones plus the CMP_SERVER_GROUP_NAME"
        deserializedTaskResult.getOutputParams().size() == 2
        deserializedTaskResult.getOutputParams().get("ENROLLMENT_INFO") == serializedScepEnrollmentInfo
        deserializedTaskResult.getOutputParams().get("CMP_SERVER_GROUP_NAME") == expectedName
        where:
        name || expectedResult | expectedName
        "1"  || "PASSED"       | "1"
        "2"  || "PASSED"       | "1"
    }

    def 'process task executed with success with certificate authority and cmp server' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with keystore under ManagedElement"
        createKeystoreUnderManagedElement()
        and: "with cmp under keystore"
        createCmpUnderKeystore()
        and: "with certificate authorities under cmp"
        createCertificateAuthoritiesUnderCmp()
        and: "with certificate authority under certificate authorities"
        createCertificateAuthorityUnderCertificateAuthorities(certificateAuthorityName)
        and: "with cmp server groups under cmp"
        createCmpServerGroupsUnderCmp()
        and: "with cmp server group under cmp server groups"
        createCmpServerGroupUnderCmpServerGroups("1")
        and: "with cmp server under cmp server group"
        createCmpServerUnderCmpServerGroup()
        and: "task for node"
        task = new CbpOiCheckCmpConfigurationTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "an enrollment info"
        Entity ee = new Entity();
        ScepEnrollmentInfo scepEnrollmentInfo = new ScepEnrollmentInfoImpl(ee, "https://localhost:8443/app/resource", null, DigestAlgorithm.MD5, 10, "challengePWD", "2048", EnrollmentMode.CMPv2_INITIAL, null, "NE_OAM_CA")
        scepEnrollmentInfo.setCertificateAuthorityDn(enrollmentServerCA)
        String serializedScepEnrollmentInfo = NscsObjectSerializer.writeObject(scepEnrollmentInfo)
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : serializedScepEnrollmentInfo ]
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
        and: "output parameters should be the original ones plus the CMP_SERVER_GROUP_NAME"
        deserializedTaskResult.getOutputParams().size() == 2
        deserializedTaskResult.getOutputParams().get("ENROLLMENT_INFO") == serializedScepEnrollmentInfo
        deserializedTaskResult.getOutputParams().get("CMP_SERVER_GROUP_NAME") == "1"
    }

    def 'netsim scenario - node with certificate authority and cmp server' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with keystore under ManagedElement"
        createKeystoreUnderManagedElement()
        and: "with cmp under keystore"
        createCmpUnderKeystore()
        and: "with certificate authorities under cmp"
        createCertificateAuthoritiesUnderCmp()
        and: "with certificate authority under certificate authorities"
        createCertificateAuthorityUnderCertificateAuthorities("CN=NE_OAM_CA,C=SE,O=KI,OU=RANI")
        and: "with cmp server groups under cmp"
        createCmpServerGroupsUnderCmp()
        and: "with cmp server group under cmp server groups"
        createCmpServerGroupUnderCmpServerGroups("halla-enm")
        and: "with cmp server under cmp server group"
        createCmpServerUnderCmpServerGroup()
        and: "task for node"
        task = new CbpOiCheckCmpConfigurationTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "an enrollment info"
        Entity ee = new Entity();
        ScepEnrollmentInfo scepEnrollmentInfo = new ScepEnrollmentInfoImpl(ee, "https://localhost:8443/app/resource", null, DigestAlgorithm.MD5, 10, "challengePWD", "2048", EnrollmentMode.CMPv2_INITIAL, null, "NE_OAM_CA")
        scepEnrollmentInfo.setCertificateAuthorityDn(enrollmentServerCA)
        String serializedScepEnrollmentInfo = NscsObjectSerializer.writeObject(scepEnrollmentInfo)
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : serializedScepEnrollmentInfo ]
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
        and: "output parameters should be the original ones plus the CMP_SERVER_GROUP_NAME"
        deserializedTaskResult.getOutputParams().size() == 2
        deserializedTaskResult.getOutputParams().get("ENROLLMENT_INFO") == serializedScepEnrollmentInfo
        deserializedTaskResult.getOutputParams().get("CMP_SERVER_GROUP_NAME") == "1"
    }

    def 'process task executed with null output parameters' () {
        given: "node created with MeContext"
        createNodeWithManagedElement(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "task for node"
        task = new CbpOiCheckCmpConfigurationTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task with null output parameters"
        def outputParams = null
        task.setOutputParams(outputParams)
        when:
        def result = taskHandler.processTask(task)
        then: "exception UnexpectedErrorException should be thrown"
        UnexpectedErrorException e = thrown()
        and:
        e.getMessage() == "Unexpected Internal Error : Missing internal parameters"
    }

    def 'process task executed with null enrollment info in output parameters' () {
        given: "node created with MeContext"
        createNodeWithManagedElement(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "task for node"
        task = new CbpOiCheckCmpConfigurationTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task with already set output parameters"
        def outputParams = [ "ENROLLMENT_INFO" : null ]
        task.setOutputParams(outputParams)
        when:
        def result = taskHandler.processTask(task)
        then: "exception UnexpectedErrorException should be thrown"
        UnexpectedErrorException e = thrown()
        and:
        e.getMessage() == "Unexpected Internal Error : Missing enrollment info internal parameter"
    }

    def 'process task executed without enrollment info in output parameters' () {
        given: "node created with MeContext"
        createNodeWithManagedElement(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "task for node"
        task = new CbpOiCheckCmpConfigurationTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task with already set output parameters"
        def outputParams = [ : ]
        task.setOutputParams(outputParams)
        when:
        def result = taskHandler.processTask(task)
        then: "exception UnexpectedErrorException should be thrown"
        UnexpectedErrorException e = thrown()
        and:
        e.getMessage() == "Unexpected Internal Error : Missing enrollment info internal parameter"
    }

    @Unroll
    def 'process task failed with wrong or unsupported enrollment server CA #name from PKI' () {
        given: "node created with MeContext"
        createNodeWithManagedElement(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "task for node"
        task = new CbpOiCheckCmpConfigurationTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "an enrollment info"
        Entity ee = new Entity();
        ScepEnrollmentInfo scepEnrollmentInfo = new ScepEnrollmentInfoImpl(ee, "https://localhost:8443/app/resource", null, DigestAlgorithm.MD5, 10, "challengePWD", "2048", EnrollmentMode.CMPv2_INITIAL, null, "NE_OAM_CA")
        scepEnrollmentInfo.setCertificateAuthorityDn(name)
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
        where:
        name                                | expected
        null                                | null
        ""                                  | null
        "CN"                                | null
        "="                                 | null
        "=myAttrValue"                      | null
        ","                                 | null
        "CN=,"                              | null
        "DNQ=myDnQualifier"                 | null
        "DNQUALIFIER=myDnQualifier"         | null
        "TITLE=myTitle"                     | null
        "GN=myGivenName"                    | null
        "domainComponent=myDomainComponent" | null
    }
}
