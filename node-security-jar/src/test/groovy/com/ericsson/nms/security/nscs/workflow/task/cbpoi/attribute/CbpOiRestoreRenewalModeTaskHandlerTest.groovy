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
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoException
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskTimeoutException
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cbpoi.attribute.CbpOiRestoreRenewalModeTask
import com.ericsson.oss.services.security.nscs.utils.CbpOiNodeDataSetup

import spock.lang.Unroll

class CbpOiRestoreRenewalModeTaskHandlerTest extends CbpOiNodeDataSetup {

    @ObjectUnderTest
    CbpOiRestoreRenewalModeTaskHandler taskHandler

    private CbpOiRestoreRenewalModeTask task

    private nodeName = "vDU00001"

    def setup() {
        NscsCMReaderService.deploymentEnv = NscsCMReaderService.DeploymentEnvironment.PRODUCTION
    }

    def 'process task executed for first successful enrollment with cmp under asymmetric-key' () {
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
        and: "with asymmetric-keys cmp under asymmetric-keys"
        createAsymmetricKeysCmpUnderAsymmetricKeys()
        and: "with asymmetric-key under asymmetric-keys"
        createAsymmetricKeyUnderAsymmetricKeys("oamNodeCredential")
        and: "with asymmetric-key cmp under asymmetric-key"
        createAsymmetricKeyCmpUnderAsymmetricKey("manual", "1", null)
        and: "task for node"
        task = new CbpOiRestoreRenewalModeTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task with already set output parameters"
        def outputParams = [
            "ASYMMETRIC_KEY_NAME" : "oamNodeCredential",
            "RENEWAL_MODE" : "automatic",
            "MAX_NUM_OF_RETRIES" : "3",
            "REMAINING_NUM_OF_RETRIES" : "3" ]
        task.setOutputParams(outputParams)
        when: "processing the task"
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
        and: "output parameters should not have been changed"
        deserializedTaskResult.getOutputParams().size() == 4
        and: "ASYMMETRIC_KEY_NAME output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEY_NAME") == "oamNodeCredential"
        and: "RENEWAL_MODE output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("RENEWAL_MODE") == 'automatic'
        and: "MAX_NUM_OF_RETRIES output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("MAX_NUM_OF_RETRIES") == '3'
        and: "REMAINING_NUM_OF_RETRIES output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("REMAINING_NUM_OF_RETRIES") == '3'
    }

    @Unroll
    def 'process task executed for further successful enrollment with cmp under asymmetric-key' () {
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
        and: "with asymmetric-keys cmp under asymmetric-keys"
        createAsymmetricKeysCmpUnderAsymmetricKeys()
        and: "with asymmetric-key under asymmetric-keys"
        createAsymmetricKeyUnderAsymmetricKeys("oamNodeCredential")
        and: "with asymmetric-key cmp under asymmetric-key"
        createAsymmetricKeyCmpUnderAsymmetricKey("manual", "1", null)
        and: "task for node"
        task = new CbpOiRestoreRenewalModeTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task with already set output parameters"
        def outputParams = [
            "ASYMMETRIC_KEY_CMP_FDN" : "MeContext="+nodeName+",ManagedElement="+nodeName+",keystore=1,asymmetric-keys=1,asymmetric-key=oamNodeCredential,asymmetric-key\$\$cmp=1",
            "RENEWAL_MODE" : renewalMode,
            "MAX_NUM_OF_RETRIES" : "3",
            "REMAINING_NUM_OF_RETRIES" : "3" ]
        task.setOutputParams(outputParams)
        when: "processing the task"
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
        and: "output parameters should not have been changed"
        deserializedTaskResult.getOutputParams().size() == 4
        and: "ASYMMETRIC_KEY_CMP_FDN output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEY_CMP_FDN") == "MeContext="+nodeName+",ManagedElement="+nodeName+",keystore=1,asymmetric-keys=1,asymmetric-key=oamNodeCredential,asymmetric-key\$\$cmp=1"
        and: "RENEWAL_MODE output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("RENEWAL_MODE") == renewalMode
        and: "MAX_NUM_OF_RETRIES output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("MAX_NUM_OF_RETRIES") == '3'
        and: "REMAINING_NUM_OF_RETRIES output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("REMAINING_NUM_OF_RETRIES") == '3'
        where:
        renewalMode || expected
        null        || null
        "manual"    || null
        "automatic" || null
    }

    def 'process task executed for first successful enrollment without cmp under asymmetric-key' () {
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
        and: "with asymmetric-keys cmp under asymmetric-keys"
        createAsymmetricKeysCmpUnderAsymmetricKeys()
        and: "task for node"
        task = new CbpOiRestoreRenewalModeTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task with already set output parameters"
        def outputParams = [
            "ASYMMETRIC_KEY_NAME" : "oamNodeCredential",
            "RENEWAL_MODE" : "automatic",
            "MAX_NUM_OF_RETRIES" : "3",
            "REMAINING_NUM_OF_RETRIES" : "3" ]
        task.setOutputParams(outputParams)
        when: "processing the task"
        def result = taskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "result should be not null"
        result != null
        and: "deserialized task result should be not null"
        deserializedTaskResult != null
        and: "task result should be ONGOING"
        deserializedTaskResult.getResult() == "ONGOING"
        and: "output parameters should not have been changed"
        deserializedTaskResult.getOutputParams().size() == 4
        and: "ASYMMETRIC_KEY_NAME output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEY_NAME") == "oamNodeCredential"
        and: "RENEWAL_MODE output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("RENEWAL_MODE") == 'automatic'
        and: "MAX_NUM_OF_RETRIES output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("MAX_NUM_OF_RETRIES") == '3'
        and: "REMAINING_NUM_OF_RETRIES output parameter should have been changed"
        deserializedTaskResult.getOutputParams().get("REMAINING_NUM_OF_RETRIES") == '2'
    }

    def 'process task executed for further successful enrollment without cmp under asymmetric-key' () {
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
        and: "with asymmetric-keys cmp under asymmetric-keys"
        createAsymmetricKeysCmpUnderAsymmetricKeys()
        and: "task for node"
        task = new CbpOiRestoreRenewalModeTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task with already set output parameters"
        def outputParams = [
            "ASYMMETRIC_KEY_CMP_FDN" : "MeContext="+nodeName+",ManagedElement="+nodeName+",keystore=1,asymmetric-keys=1,asymmetric-key=oamNodeCredential,asymmetric-key\$\$cmp=1",
            "RENEWAL_MODE" : "automatic",
            "MAX_NUM_OF_RETRIES" : "3",
            "REMAINING_NUM_OF_RETRIES" : "3" ]
        task.setOutputParams(outputParams)
        when: "processing the task"
        def result = taskHandler.processTask(task)
        then: "exception MissingMoException should be thrown"
        MissingMoException e = thrown()
        and:
        e.getMessage() == "Missing asymmetric-key cmp MO"
    }

    def 'process task executed at last attempt for first successful enrollment without cmp under asymmetric-key' () {
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
        and: "with asymmetric-keys cmp under asymmetric-keys"
        createAsymmetricKeysCmpUnderAsymmetricKeys()
        and: "task for node"
        task = new CbpOiRestoreRenewalModeTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task with already set output parameters"
        def outputParams = [
            "ASYMMETRIC_KEY_NAME" : "oamNodeCredential",
            "RENEWAL_MODE" : "automatic",
            "MAX_NUM_OF_RETRIES" : "3",
            "REMAINING_NUM_OF_RETRIES" : "0" ]
        task.setOutputParams(outputParams)
        when: "processing the task"
        def result = taskHandler.processTask(task)
        then: "exception WorkflowTaskTimeoutException should be thrown"
        WorkflowTaskTimeoutException e = thrown()
        and:
        e.getMessage() == "Exceeded max num of [3] retry attempts"
    }

    def 'process task executed for first failed enrollment without cmp under asymmetric-key' () {
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
        and: "with asymmetric-keys cmp under asymmetric-keys"
        createAsymmetricKeysCmpUnderAsymmetricKeys()
        and: "task for node"
        task = new CbpOiRestoreRenewalModeTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task with already set output parameters"
        def outputParams = [
            "ASYMMETRIC_KEY_NAME" : "oamNodeCredential",
            "RENEWAL_MODE" : "automatic" ]
        task.setOutputParams(outputParams)
        when: "processing the task"
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
        and: "output parameters should not have been changed"
        deserializedTaskResult.getOutputParams().size() == 2
        and: "ASYMMETRIC_KEY_NAME output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEY_NAME") == "oamNodeCredential"
        and: "RENEWAL_MODE output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("RENEWAL_MODE") == 'automatic'
        and: "MAX_NUM_OF_RETRIES output parameter should be not present"
        deserializedTaskResult.getOutputParams().get("MAX_NUM_OF_RETRIES") == null
        and: "REMAINING_NUM_OF_RETRIES output parameter should be not present"
        deserializedTaskResult.getOutputParams().get("REMAINING_NUM_OF_RETRIES") == null
    }

    def 'process task executed for first failed enrollment without asymmetric-key name in output params' () {
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
        and: "with asymmetric-keys cmp under asymmetric-keys"
        createAsymmetricKeysCmpUnderAsymmetricKeys()
        and: "task for node"
        task = new CbpOiRestoreRenewalModeTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task with already set output parameters"
        def outputParams = [
            "RENEWAL_MODE" : "automatic" ]
        task.setOutputParams(outputParams)
        when: "processing the task"
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
        and: "output parameters should not have been changed"
        deserializedTaskResult.getOutputParams().size() == 1
        and: "RENEWAL_MODE output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("RENEWAL_MODE") == 'automatic'
        and: "MAX_NUM_OF_RETRIES output parameter should be not present"
        deserializedTaskResult.getOutputParams().get("MAX_NUM_OF_RETRIES") == null
        and: "REMAINING_NUM_OF_RETRIES output parameter should be not present"
        deserializedTaskResult.getOutputParams().get("REMAINING_NUM_OF_RETRIES") == null
    }

    @Unroll
    def 'process task executed for further failed enrollment with cmp under asymmetric-key' () {
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
        and: "with asymmetric-keys cmp under asymmetric-keys"
        createAsymmetricKeysCmpUnderAsymmetricKeys()
        and: "with asymmetric-key under asymmetric-keys"
        createAsymmetricKeyUnderAsymmetricKeys("oamNodeCredential")
        and: "with asymmetric-key cmp under asymmetric-key"
        createAsymmetricKeyCmpUnderAsymmetricKey("manual", "1", null)
        and: "task for node"
        task = new CbpOiRestoreRenewalModeTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task with already set output parameters"
        def outputParams = [
            "ASYMMETRIC_KEY_CMP_FDN" : "MeContext="+nodeName+",ManagedElement="+nodeName+",keystore=1,asymmetric-keys=1,asymmetric-key=oamNodeCredential,asymmetric-key\$\$cmp=1",
            "RENEWAL_MODE" : renewalMode ]
        task.setOutputParams(outputParams)
        when: "processing the task"
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
        and: "output parameters should not have been changed"
        deserializedTaskResult.getOutputParams().size() == 2
        and: "ASYMMETRIC_KEY_CMP_FDN output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEY_CMP_FDN") == "MeContext="+nodeName+",ManagedElement="+nodeName+",keystore=1,asymmetric-keys=1,asymmetric-key=oamNodeCredential,asymmetric-key\$\$cmp=1"
        and: "RENEWAL_MODE output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("RENEWAL_MODE") == renewalMode
        where:
        renewalMode || expected
        null        || null
        "manual"    || null
        "automatic" || null
    }

    @Unroll
    def 'process task executed with asymmetric-key name' () {
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
        and: "with asymmetric-keys cmp under asymmetric-keys"
        createAsymmetricKeysCmpUnderAsymmetricKeys()
        and: "with asymmetric-key under asymmetric-keys"
        createAsymmetricKeyUnderAsymmetricKeys("oamNodeCredential")
        and: "with asymmetric-key cmp under asymmetric-key"
        createAsymmetricKeyCmpUnderAsymmetricKey("manual", "1", null)
        and: "task for node"
        task = new CbpOiRestoreRenewalModeTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task with already set output parameters"
        def outputParams = [
            "ASYMMETRIC_KEY_NAME" : "oamNodeCredential",
            "RENEWAL_MODE" : renewalMode ]
        task.setOutputParams(outputParams)
        when: "processing the task"
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
        and: "output parameters should not have been changed"
        deserializedTaskResult.getOutputParams().size() == 2
        and: "ASYMMETRIC_KEY_NAME output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("ASYMMETRIC_KEY_NAME") == "oamNodeCredential"
        and: "RENEWAL_MODE output parameter should be unchanged"
        deserializedTaskResult.getOutputParams().get("RENEWAL_MODE") == renewalMode
        where:
        renewalMode || expected
        null        || null
        "manual"    || null
        "automatic" || null
    }

    @Unroll
    def 'process task executed with asymmetric-key cmp FDN' () {
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
        and: "with asymmetric-keys cmp under asymmetric-keys"
        createAsymmetricKeysCmpUnderAsymmetricKeys()
        and: "with asymmetric-key under asymmetric-keys"
        createAsymmetricKeyUnderAsymmetricKeys("oamNodeCredential")
        and: "with asymmetric-key cmp under asymmetric-key"
        createAsymmetricKeyCmpUnderAsymmetricKey("manual", "1", null)
        and: "task for node"
        task = new CbpOiRestoreRenewalModeTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task with already set output parameters"
        def outputParams = [
            "ASYMMETRIC_KEY_CMP_FDN" : "MeContext="+nodeName+",ManagedElement="+nodeName+",keystore=1,asymmetric-keys=1,asymmetric-key=oamNodeCredential,asymmetric-key\$\$cmp=1",
            "RENEWAL_MODE" : renewalMode ]
        task.setOutputParams(outputParams)
        when: "processing the task"
        taskHandler.processTask(task)
        then: "no exception should be thrown"
        noExceptionThrown()
        where:
        renewalMode || expected
        null        || null
        "manual"    || null
        "automatic" || null
    }

    def 'process task executed without renewal mode in output parameters' () {
        given: "task for node"
        task = new CbpOiRestoreRenewalModeTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task with already set output parameters without renewalmode"
        def outputParams = [ : ]
        task.setOutputParams(outputParams)
        when: "processing the task"
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
        and: "output parameters should not have been changed"
        deserializedTaskResult.getOutputParams().size() == 0
    }

    def 'process task executed with null output parameters' () {
        given: "task for node"
        task = new CbpOiRestoreRenewalModeTask(nodeName)
        and: "task with certificate type OAM"
        task.setTrustedCertCategory("CORBA_PEERS")
        and: "task with null output parameters"
        def outputParams = null
        task.setOutputParams(outputParams)
        when: "processing the task"
        def result = taskHandler.processTask(task)
        then: "exception UnexpectedErrorException should be thrown"
        UnexpectedErrorException e = thrown()
        and:
        e.getMessage() == "Unexpected Internal Error : Missing internal parameters"
    }
}
