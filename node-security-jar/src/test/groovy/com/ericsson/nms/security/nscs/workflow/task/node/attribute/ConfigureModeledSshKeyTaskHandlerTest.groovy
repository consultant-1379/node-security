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
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.nms.security.nscs.ldap.utility.PlatformConfigurationReader
import com.ericsson.nms.security.nscs.ssh.SSHKeyGenConstants
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject
import com.ericsson.oss.itpf.security.cryptography.CryptographyService
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.attribute.ConfigureModeledSshKeyTask
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys
import com.ericsson.oss.services.security.nscs.utils.CbpOiNodeDataSetup

import spock.lang.Unroll

class ConfigureModeledSshKeyTaskHandlerTest extends CbpOiNodeDataSetup {

    private static final String ENCRYPTED_SSHKEY_PRIVATE = "encrypted private"
    private static final String KEY_NAME = "enmapache.athtem.eei.ericsson.se"

    @ObjectUnderTest
    ConfigureModeledSshKeyTaskHandler taskHandler

    @MockedImplementation
    private CryptographyService cryptographyService

    @MockedImplementation
    private PlatformConfigurationReader platformConfigurationReader

    private ConfigureModeledSshKeyTask task
    private String secureUserName = "secureUser"

    private nodeName = "NODE"

    def setup() {
        NscsCMReaderService.deploymentEnv = NscsCMReaderService.DeploymentEnvironment.PRODUCTION
        cryptographyService.encrypt(_ as byte[]) >> ENCRYPTED_SSHKEY_PRIVATE.getBytes()
        platformConfigurationReader.getProperty(SSHKeyGenConstants.UI_PRES_SERVER_KEY) >> KEY_NAME
    }

    def 'object under test'() {
        expect:
        taskHandler != null
    }

    def 'process task executed with success for create operation' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with synchronized CmFunction under NetworkElement"
        createCmFunctionUnderNetworkElement("SYNCHRONIZED")
        and: "with SecurityFunction under NetworkElement"
        createSecurityFunctionUnderNetworkElement()
        and: "with NetworkElementSecurity under SecurityFunction"
        createNetworkElementSecurityUnderSecurityFunction(secureUserName, "RSA_1024", null, null)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with system under ManagedElement"
        createSystemUnderManagedElement()
        and: "with authentication under system"
        createAuthenticationUnderSystem()
        and: "with user under authentication"
        createUserUnderAuthentication(secureUserName)
        and: "task for node"
        task = new ConfigureModeledSshKeyTask(nodeName)
        and: "task with created operation"
        task.setSshkeyOperation(SSHKeyGenConstants.SSH_KEY_TO_BE_CREATED)
        and: "task with supported MOM type"
        task.setMomType("EOI")
        and: "task with supported algorithm"
        task.setAlgorithm("RSA_1024")
        when:
        def result = taskHandler.processTask(task)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "result should be not null"
        result != null
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);
        and: "result should be CREATED_ON_NODE"
        deserializedTaskResult.getResult() == "CREATED_ON_NODE"
        and: "output parameters should contain public and encrypted private key"
        deserializedTaskResult.getOutputParams() != null
        deserializedTaskResult.getOutputParams().isEmpty() == false
        deserializedTaskResult.getOutputParams().get(WorkflowOutputParameterKeys.PUBLIC_SSH_KEY.toString()) != null
        deserializedTaskResult.getOutputParams().get(WorkflowOutputParameterKeys.ENCRYPTED_PRIVATE_SSH_KEY.toString()) != null
        and: "authorized-key should be present under user"
        def ManagedObject authkey = findMoByFdn("MeContext="+nodeName+",ManagedElement="+nodeName+",system=1,authentication=1,user="+secureUserName+",authorized-key="+KEY_NAME)
        authkey != null
        authkey.getAttribute("name") == KEY_NAME
        authkey.getAttribute("algorithm") == "ssh-rsa"
        authkey.getAttribute("comment") == "Created by ENM."
        isValidKey(authkey.getAttribute("key-data")) == true
        and: "SSH keys should be unchanged in NetworkElementSecurity MO"
        def ManagedObject nes = findMoByFdn("NetworkElement="+nodeName+",SecurityFunction=1,NetworkElementSecurity=1")
        nes.getAttribute("enmSshPrivateKey") == null
        nes.getAttribute("enmSshPublicKey") == null
        and: "algorithmAndKeySize should be unchanged in NetworkElementSecurity MO"
        nes.getAttribute("algorithmAndKeySize") == "RSA_1024"
    }

    def 'process task executed with success for create operation changing algorithm and key size' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with synchronized CmFunction under NetworkElement"
        createCmFunctionUnderNetworkElement("SYNCHRONIZED")
        and: "with SecurityFunction under NetworkElement"
        createSecurityFunctionUnderNetworkElement()
        and: "with NetworkElementSecurity under SecurityFunction"
        createNetworkElementSecurityUnderSecurityFunction(secureUserName, "RSA_1024", null, null)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with system under ManagedElement"
        createSystemUnderManagedElement()
        and: "with authentication under system"
        createAuthenticationUnderSystem()
        and: "with user under authentication"
        createUserUnderAuthentication(secureUserName)
        and: "task for node"
        task = new ConfigureModeledSshKeyTask(nodeName)
        and: "task with created operation"
        task.setSshkeyOperation(SSHKeyGenConstants.SSH_KEY_TO_BE_CREATED)
        and: "task with supported MOM type"
        task.setMomType("EOI")
        and: "task with supported algorithm"
        task.setAlgorithm("RSA_2048")
        when:
        def result = taskHandler.processTask(task)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "result should be not null"
        result != null
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);
        and: "result should be CREATED_ON_NODE"
        deserializedTaskResult.getResult() == "CREATED_ON_NODE"
        and: "output parameters should contain public and encrypted private key"
        deserializedTaskResult.getOutputParams() != null
        deserializedTaskResult.getOutputParams().isEmpty() == false
        deserializedTaskResult.getOutputParams().get(WorkflowOutputParameterKeys.PUBLIC_SSH_KEY.toString()) != null
        deserializedTaskResult.getOutputParams().get(WorkflowOutputParameterKeys.ENCRYPTED_PRIVATE_SSH_KEY.toString()) != null
        and: "authorized-key should be present under user"
        def ManagedObject authkey = findMoByFdn("MeContext="+nodeName+",ManagedElement="+nodeName+",system=1,authentication=1,user="+secureUserName+",authorized-key="+KEY_NAME)
        authkey != null
        authkey.getAttribute("name") == KEY_NAME
        authkey.getAttribute("algorithm") == "ssh-rsa"
        authkey.getAttribute("comment") == "Created by ENM."
        isValidKey(authkey.getAttribute("key-data")) == true
        and: "SSH keys should be unchanged in NetworkElementSecurity MO"
        def ManagedObject nes = findMoByFdn("NetworkElement="+nodeName+",SecurityFunction=1,NetworkElementSecurity=1")
        nes.getAttribute("enmSshPrivateKey") == null
        nes.getAttribute("enmSshPublicKey") == null
        and: "algorithmAndKeySize should be unchanged in NetworkElementSecurity MO"
        nes.getAttribute("algorithmAndKeySize") == "RSA_1024"
    }

    def 'process task executed with success for update operation' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with synchronized CmFunction under NetworkElement"
        createCmFunctionUnderNetworkElement("SYNCHRONIZED")
        and: "with SecurityFunction under NetworkElement"
        createSecurityFunctionUnderNetworkElement()
        and: "with NetworkElementSecurity under SecurityFunction"
        createNetworkElementSecurityUnderSecurityFunction(secureUserName, "RSA_2048", "private", "public")
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with system under ManagedElement"
        createSystemUnderManagedElement()
        and: "with authentication under system"
        createAuthenticationUnderSystem()
        and: "with user under authentication"
        createUserUnderAuthentication(secureUserName)
        and: "with authorized-key under user"
        createAuthorizedKeyUnderUser(KEY_NAME, "ssh-rsa", "Created by ENM.", "public")
        and: "task for node"
        task = new ConfigureModeledSshKeyTask(nodeName)
        and: "task with update operation"
        task.setSshkeyOperation(SSHKeyGenConstants.SSH_KEY_TO_BE_UPDATED)
        and: "task with supported MOM type"
        task.setMomType("EOI")
        and: "task with supported algorithm"
        task.setAlgorithm("RSA_4096")
        when:
        def result = taskHandler.processTask(task)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "result should be not null"
        result != null
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);
        and: "result should be UPDATED_ON_NODE"
        deserializedTaskResult.getResult() == "UPDATED_ON_NODE"
        and: "output parameters should contain public and encrypted private key"
        deserializedTaskResult.getOutputParams() != null
        deserializedTaskResult.getOutputParams().isEmpty() == false
        deserializedTaskResult.getOutputParams().get(WorkflowOutputParameterKeys.PUBLIC_SSH_KEY.toString()) != null
        deserializedTaskResult.getOutputParams().get(WorkflowOutputParameterKeys.ENCRYPTED_PRIVATE_SSH_KEY.toString()) != null
        and: "authorized-key should be present under user wth changed key"
        def ManagedObject authkey = findMoByFdn("MeContext="+nodeName+",ManagedElement="+nodeName+",system=1,authentication=1,user="+secureUserName+",authorized-key="+KEY_NAME)
        authkey != null
        authkey.getAttribute("name") == KEY_NAME
        authkey.getAttribute("algorithm") == "ssh-rsa"
        authkey.getAttribute("comment") == "Created by ENM."
        isValidKey(authkey.getAttribute("key-data")) == true
        authkey.getAttribute("key-data") != "public"
        and: "SSH keys should be unchanged in NetworkElementSecurity MO"
        def ManagedObject nes = findMoByFdn("NetworkElement="+nodeName+",SecurityFunction=1,NetworkElementSecurity=1")
        nes.getAttribute("enmSshPrivateKey") == "private"
        nes.getAttribute("enmSshPublicKey") == "public"
        and: "algorithmAndKeySize should be unchanged in NetworkElementSecurity MO"
        nes.getAttribute("algorithmAndKeySize") == "RSA_2048"
    }

    def 'process task executed with success for update operation changing algorithm' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with synchronized CmFunction under NetworkElement"
        createCmFunctionUnderNetworkElement("SYNCHRONIZED")
        and: "with SecurityFunction under NetworkElement"
        createSecurityFunctionUnderNetworkElement()
        and: "with NetworkElementSecurity under SecurityFunction"
        createNetworkElementSecurityUnderSecurityFunction(secureUserName, "RSA_4096", "private", "public")
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with system under ManagedElement"
        createSystemUnderManagedElement()
        and: "with authentication under system"
        createAuthenticationUnderSystem()
        and: "with user under authentication"
        createUserUnderAuthentication(secureUserName)
        and: "with authorized-key under user"
        createAuthorizedKeyUnderUser(KEY_NAME, "ssh-rsa", "Created by ENM.", "public")
        and: "task for node"
        task = new ConfigureModeledSshKeyTask(nodeName)
        and: "task with update operation"
        task.setSshkeyOperation(SSHKeyGenConstants.SSH_KEY_TO_BE_UPDATED)
        and: "task with supported MOM type"
        task.setMomType("EOI")
        and: "task with supported algorithm"
        task.setAlgorithm("DSA_1024")
        when:
        def result = taskHandler.processTask(task)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "result should be not null"
        result != null
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);
        and: "result should be UPDATED_ON_NODE"
        deserializedTaskResult.getResult() == "UPDATED_ON_NODE"
        and: "output parameters should contain public and encrypted private key"
        deserializedTaskResult.getOutputParams() != null
        deserializedTaskResult.getOutputParams().isEmpty() == false
        deserializedTaskResult.getOutputParams().get(WorkflowOutputParameterKeys.PUBLIC_SSH_KEY.toString()) != null
        deserializedTaskResult.getOutputParams().get(WorkflowOutputParameterKeys.ENCRYPTED_PRIVATE_SSH_KEY.toString()) != null
        and: "authorized-key should be present under user wth changed key"
        def ManagedObject authkey = findMoByFdn("MeContext="+nodeName+",ManagedElement="+nodeName+",system=1,authentication=1,user="+secureUserName+",authorized-key="+KEY_NAME)
        authkey != null
        authkey.getAttribute("name") == KEY_NAME
        authkey.getAttribute("algorithm") == "ssh-dss"
        authkey.getAttribute("comment") == "Created by ENM."
        isValidKey(authkey.getAttribute("key-data")) == true
        authkey.getAttribute("key-data") != "public"
        and: "SSH keys should be unchanged in NetworkElementSecurity MO"
        def ManagedObject nes = findMoByFdn("NetworkElement="+nodeName+",SecurityFunction=1,NetworkElementSecurity=1")
        nes.getAttribute("enmSshPrivateKey") == "private"
        nes.getAttribute("enmSshPublicKey") == "public"
        and: "algorithmAndKeySize should be unchanged in NetworkElementSecurity MO"
        nes.getAttribute("algorithmAndKeySize") == "RSA_4096"
    }

    def 'process task executed with success for update operation with invalid keys and without authorized-key on node' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with synchronized CmFunction under NetworkElement"
        createCmFunctionUnderNetworkElement("SYNCHRONIZED")
        and: "with SecurityFunction under NetworkElement"
        createSecurityFunctionUnderNetworkElement()
        and: "with NetworkElementSecurity under SecurityFunction"
        createNetworkElementSecurityUnderSecurityFunction(secureUserName, "RSA_1024", SSHKeyGenConstants.SSH_KEY_INVALID, SSHKeyGenConstants.SSH_KEY_INVALID)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with system under ManagedElement"
        createSystemUnderManagedElement()
        and: "with authentication under system"
        createAuthenticationUnderSystem()
        and: "with user under authentication"
        createUserUnderAuthentication(secureUserName)
        and: "task for node"
        task = new ConfigureModeledSshKeyTask(nodeName)
        and: "task with update operation"
        task.setSshkeyOperation(SSHKeyGenConstants.SSH_KEY_TO_BE_UPDATED)
        and: "task with supported MOM type"
        task.setMomType("EOI")
        and: "task with supported algorithm"
        task.setAlgorithm("RSA_1024")
        when:
        def result = taskHandler.processTask(task)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "result should be not null"
        result != null
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);
        and: "result should be CREATED_ON_NODE"
        deserializedTaskResult.getResult() == "CREATED_ON_NODE"
        and: "output parameters should contain public and encrypted private key"
        deserializedTaskResult.getOutputParams() != null
        deserializedTaskResult.getOutputParams().isEmpty() == false
        deserializedTaskResult.getOutputParams().get(WorkflowOutputParameterKeys.PUBLIC_SSH_KEY.toString()) != null
        deserializedTaskResult.getOutputParams().get(WorkflowOutputParameterKeys.ENCRYPTED_PRIVATE_SSH_KEY.toString()) != null
        and: "authorized-key should be present under user wth changed key"
        def ManagedObject authkey = findMoByFdn("MeContext="+nodeName+",ManagedElement="+nodeName+",system=1,authentication=1,user="+secureUserName+",authorized-key="+KEY_NAME)
        authkey != null
        authkey.getAttribute("name") == KEY_NAME
        authkey.getAttribute("algorithm") == "ssh-rsa"
        authkey.getAttribute("comment") == "Created by ENM."
        isValidKey(authkey.getAttribute("key-data")) == true
        and: "SSH keys should be unchanged in NetworkElementSecurity MO"
        def ManagedObject nes = findMoByFdn("NetworkElement="+nodeName+",SecurityFunction=1,NetworkElementSecurity=1")
        nes.getAttribute("enmSshPrivateKey") == SSHKeyGenConstants.SSH_KEY_INVALID
        nes.getAttribute("enmSshPublicKey") == SSHKeyGenConstants.SSH_KEY_INVALID
        and: "algorithmAndKeySize should be unchanged in NetworkElementSecurity MO"
        nes.getAttribute("algorithmAndKeySize") == "RSA_1024"
    }

    def 'process task executed with success for delete operation' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with synchronized CmFunction under NetworkElement"
        createCmFunctionUnderNetworkElement("SYNCHRONIZED")
        and: "with SecurityFunction under NetworkElement"
        createSecurityFunctionUnderNetworkElement()
        and: "with NetworkElementSecurity under SecurityFunction"
        createNetworkElementSecurityUnderSecurityFunction(secureUserName, "RSA_2048", "", "")
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with system under ManagedElement"
        createSystemUnderManagedElement()
        and: "with authentication under system"
        createAuthenticationUnderSystem()
        and: "with user under authentication"
        createUserUnderAuthentication(secureUserName)
        and: "with authorized-key under user"
        createAuthorizedKeyUnderUser(KEY_NAME, "ssh-rsa", "Created by ENM.", "public")
        and: "task for node"
        task = new ConfigureModeledSshKeyTask(nodeName)
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
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);
        and: "result should be DELETED_ON_NODE"
        deserializedTaskResult.getResult() == "DELETED_ON_NODE"
        and: "output parameters should contain null public and encrypted private key"
        deserializedTaskResult.getOutputParams() != null
        deserializedTaskResult.getOutputParams().isEmpty() == false
        deserializedTaskResult.getOutputParams().get(WorkflowOutputParameterKeys.PUBLIC_SSH_KEY.toString()) == null
        deserializedTaskResult.getOutputParams().get(WorkflowOutputParameterKeys.ENCRYPTED_PRIVATE_SSH_KEY.toString()) == null
        and: "autherized-key should be not present under user"
        def ManagedObject authorizedKey = findMoByFdn("MeContext="+nodeName+",ManagedElement="+nodeName+",system=1,authentication=1,user="+secureUserName+",authorized-key="+KEY_NAME)
        authorizedKey == null
        and: "SSH keys should be unchanged in NetworkElementSecurity MO"
        def ManagedObject nes = findMoByFdn("NetworkElement="+nodeName+",SecurityFunction=1,NetworkElementSecurity=1")
        nes.getAttribute("enmSshPrivateKey") == ""
        nes.getAttribute("enmSshPublicKey") == ""
        and: "algorithmAndKeySize should be unchanged in NetworkElementSecurity MO"
        nes.getAttribute("algorithmAndKeySize") == "RSA_2048"
    }

    @Unroll
    def 'process task executed with success for delete operation with node not sync #syncstatus' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with synchronized CmFunction under NetworkElement"
        createCmFunctionUnderNetworkElement(syncstatus)
        and: "with SecurityFunction under NetworkElement"
        createSecurityFunctionUnderNetworkElement()
        and: "with NetworkElementSecurity under SecurityFunction"
        createNetworkElementSecurityUnderSecurityFunction(secureUserName, "RSA_2048", "", "")
        and: "task for node"
        task = new ConfigureModeledSshKeyTask(nodeName)
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
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);
        and: "result should be NOT_DELETED_ON_NOT_IN_SYNC_NODE"
        deserializedTaskResult.getResult() == "NOT_DELETED_ON_NOT_IN_SYNC_NODE"
        and: "output parameters should contain null public and encrypted private key"
        deserializedTaskResult.getOutputParams() != null
        deserializedTaskResult.getOutputParams().isEmpty() == false
        deserializedTaskResult.getOutputParams().get(WorkflowOutputParameterKeys.PUBLIC_SSH_KEY.toString()) == null
        deserializedTaskResult.getOutputParams().get(WorkflowOutputParameterKeys.ENCRYPTED_PRIVATE_SSH_KEY.toString()) == null
        and: "SSH keys should be unchanged in NetworkElementSecurity MO"
        def ManagedObject nes = findMoByFdn("NetworkElement="+nodeName+",SecurityFunction=1,NetworkElementSecurity=1")
        nes.getAttribute("enmSshPrivateKey") == ""
        nes.getAttribute("enmSshPublicKey") == ""
        and: "algorithmAndKeySize should be unchanged in NetworkElementSecurity MO"
        nes.getAttribute("algorithmAndKeySize") == "RSA_2048"
        where:
        syncstatus << [
            "UNSYNCHRONIZED",
            "PENDING",
            "TOPOLOGY"
        ]
    }

    @Unroll
    def 'process task executed with error for #operation operation with node unsynchronized' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with unsynchronized CmFunction under NetworkElement"
        createCmFunctionUnderNetworkElement("UNSYNCHRONIZED")
        and: "with SecurityFunction under NetworkElement"
        createSecurityFunctionUnderNetworkElement()
        and: "with NetworkElementSecurity under SecurityFunction"
        createNetworkElementSecurityUnderSecurityFunction(secureUserName, "RSA_1024", SSHKeyGenConstants.SSH_KEY_INVALID, SSHKeyGenConstants.SSH_KEY_INVALID)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with system under ManagedElement"
        createSystemUnderManagedElement()
        and: "with authentication under system"
        createAuthenticationUnderSystem()
        and: "with user under authentication"
        createUserUnderAuthentication(secureUserName)
        and: "task for node"
        task = new ConfigureModeledSshKeyTask(nodeName)
        and: "task with operation"
        task.setSshkeyOperation(operation)
        and: "task with supported MOM type"
        task.setMomType("EOI")
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
    def 'process task executed with error for #operation operation with unsupported MOM type' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with synchronized CmFunction under NetworkElement"
        createCmFunctionUnderNetworkElement("SYNCHRONIZED")
        and: "with SecurityFunction under NetworkElement"
        createSecurityFunctionUnderNetworkElement()
        and: "with NetworkElementSecurity under SecurityFunction"
        createNetworkElementSecurityUnderSecurityFunction(secureUserName, "RSA_4096", "private", "public")
        and: "task for node"
        task = new ConfigureModeledSshKeyTask(nodeName)
        and: "task with operation"
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
            SSHKeyGenConstants.SSH_KEY_TO_BE_UPDATED,
            SSHKeyGenConstants.SSH_KEY_TO_BE_DELETED
        ]
    }

    @Unroll
    def 'process task executed with error for #operation operation without NetworkElementSecurity MO' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with synchronized CmFunction under NetworkElement"
        createCmFunctionUnderNetworkElement("SYNCHRONIZED")
        and: "with SecurityFunction under NetworkElement"
        createSecurityFunctionUnderNetworkElement()
        and: "task for node"
        task = new ConfigureModeledSshKeyTask(nodeName)
        and: "task with operation"
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
            SSHKeyGenConstants.SSH_KEY_TO_BE_UPDATED,
            SSHKeyGenConstants.SSH_KEY_TO_BE_DELETED
        ]
    }

    @Unroll
    def 'process task executed with error for #operation operation with invalid secure user name #secureusername' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with synchronized CmFunction under NetworkElement"
        createCmFunctionUnderNetworkElement("SYNCHRONIZED")
        and: "with SecurityFunction under NetworkElement"
        createSecurityFunctionUnderNetworkElement()
        and: "with NetworkElementSecurity under SecurityFunction"
        createNetworkElementSecurityUnderSecurityFunction(secureusername, "RSA_1024", "private", "public")
        and: "task for node"
        task = new ConfigureModeledSshKeyTask(nodeName)
        and: "task with operation"
        task.setSshkeyOperation(operation)
        and: "task with supported MOM type"
        task.setMomType("EOI")
        when:
        def result = taskHandler.processTask(task)
        then: "exception should be thrown"
        thrown(WorkflowTaskException.class)
        where:
        secureusername << [null, null, null, "", "", ""]
        operation << [
            SSHKeyGenConstants.SSH_KEY_TO_BE_CREATED,
            SSHKeyGenConstants.SSH_KEY_TO_BE_UPDATED,
            SSHKeyGenConstants.SSH_KEY_TO_BE_DELETED,
            SSHKeyGenConstants.SSH_KEY_TO_BE_CREATED,
            SSHKeyGenConstants.SSH_KEY_TO_BE_UPDATED,
            SSHKeyGenConstants.SSH_KEY_TO_BE_DELETED
        ]
    }

    @Unroll
    def 'process task executed with error for #operation operation without user MO on node' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with unsynchronized CmFunction under NetworkElement"
        createCmFunctionUnderNetworkElement("SYNCHRONIZED")
        and: "with SecurityFunction under NetworkElement"
        createSecurityFunctionUnderNetworkElement()
        and: "with NetworkElementSecurity under SecurityFunction"
        createNetworkElementSecurityUnderSecurityFunction(secureUserName, "RSA_1024", "private", "public")
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with system under ManagedElement"
        createSystemUnderManagedElement()
        and: "with authentication under system"
        createAuthenticationUnderSystem()
        and: "task for node"
        task = new ConfigureModeledSshKeyTask(nodeName)
        and: "task with operation"
        task.setSshkeyOperation(operation)
        and: "task with supported MOM type"
        task.setMomType("EOI")
        and: "task with supported algorithm"
        task.setAlgorithm("RSA_1024")
        when:
        def result = taskHandler.processTask(task)
        then: "exception should be thrown"
        thrown(WorkflowTaskException.class)
        where:
        operation << [
            SSHKeyGenConstants.SSH_KEY_TO_BE_CREATED,
            SSHKeyGenConstants.SSH_KEY_TO_BE_UPDATED,
            SSHKeyGenConstants.SSH_KEY_TO_BE_DELETED
        ]
    }

    @Unroll
    def 'process task executed with error for #operation operation without system MO on node' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with unsynchronized CmFunction under NetworkElement"
        createCmFunctionUnderNetworkElement("SYNCHRONIZED")
        and: "with SecurityFunction under NetworkElement"
        createSecurityFunctionUnderNetworkElement()
        and: "with NetworkElementSecurity under SecurityFunction"
        createNetworkElementSecurityUnderSecurityFunction(secureUserName, "RSA_1024", "private", "public")
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "task for node"
        task = new ConfigureModeledSshKeyTask(nodeName)
        and: "task with operation"
        task.setSshkeyOperation(operation)
        and: "task with supported MOM type"
        task.setMomType("EOI")
        and: "task with supported algorithm"
        task.setAlgorithm("RSA_1024")
        when:
        def result = taskHandler.processTask(task)
        then: "exception should be thrown"
        thrown(WorkflowTaskException.class)
        where:
        operation << [
            SSHKeyGenConstants.SSH_KEY_TO_BE_CREATED,
            SSHKeyGenConstants.SSH_KEY_TO_BE_UPDATED,
            SSHKeyGenConstants.SSH_KEY_TO_BE_DELETED
        ]
    }

    @Unroll
    def 'process task executed with error for #operation operation without authentication MO on node' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with unsynchronized CmFunction under NetworkElement"
        createCmFunctionUnderNetworkElement("SYNCHRONIZED")
        and: "with SecurityFunction under NetworkElement"
        createSecurityFunctionUnderNetworkElement()
        and: "with NetworkElementSecurity under SecurityFunction"
        createNetworkElementSecurityUnderSecurityFunction(secureUserName, "RSA_1024", "private", "public")
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with system under ManagedElement"
        createSystemUnderManagedElement()
        and: "task for node"
        task = new ConfigureModeledSshKeyTask(nodeName)
        and: "task with operation"
        task.setSshkeyOperation(operation)
        and: "task with supported MOM type"
        task.setMomType("EOI")
        and: "task with supported algorithm"
        task.setAlgorithm("RSA_1024")
        when:
        def result = taskHandler.processTask(task)
        then: "exception should be thrown"
        thrown(WorkflowTaskException.class)
        where:
        operation << [
            SSHKeyGenConstants.SSH_KEY_TO_BE_CREATED,
            SSHKeyGenConstants.SSH_KEY_TO_BE_UPDATED,
            SSHKeyGenConstants.SSH_KEY_TO_BE_DELETED
        ]
    }

    @Unroll
    def 'process task executed with error for #operation operation with wrong algorithm and key size' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with unsynchronized CmFunction under NetworkElement"
        createCmFunctionUnderNetworkElement("SYNCHRONIZED")
        and: "with SecurityFunction under NetworkElement"
        createSecurityFunctionUnderNetworkElement()
        and: "with NetworkElementSecurity under SecurityFunction"
        createNetworkElementSecurityUnderSecurityFunction(secureUserName, "RSA_1024", "private", "public")
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with system under ManagedElement"
        createSystemUnderManagedElement()
        and: "with authentication under system"
        createAuthenticationUnderSystem()
        and: "with user under authentication"
        createUserUnderAuthentication(secureUserName)
        and: "task for node"
        task = new ConfigureModeledSshKeyTask(nodeName)
        and: "task with operation"
        task.setSshkeyOperation(operation)
        and: "task with supported MOM type"
        task.setMomType("EOI")
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
    def 'process task executed with error for #operation operation with unsupported algorithm and key size' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with unsynchronized CmFunction under NetworkElement"
        createCmFunctionUnderNetworkElement("SYNCHRONIZED")
        and: "with SecurityFunction under NetworkElement"
        createSecurityFunctionUnderNetworkElement()
        and: "with NetworkElementSecurity under SecurityFunction"
        createNetworkElementSecurityUnderSecurityFunction(secureUserName, "RSA_1024", "private", "public")
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with system under ManagedElement"
        createSystemUnderManagedElement()
        and: "with authentication under system"
        createAuthenticationUnderSystem()
        and: "with user under authentication"
        createUserUnderAuthentication(secureUserName)
        and: "task for node"
        task = new ConfigureModeledSshKeyTask(nodeName)
        and: "task with operation"
        task.setSshkeyOperation(operation)
        and: "task with supported MOM type"
        task.setMomType("EOI")
        and: "task with wrong algorithm"
        task.setAlgorithm("ECDSA_160")
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

    private isValidKey(String key) {
        return key != null && !key.isEmpty() && key != SSHKeyGenConstants.SSH_KEY_INVALID
    }
}
