package com.ericsson.nms.security.nscs.workflow.task.ssh

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes
import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException
import com.ericsson.nms.security.nscs.api.model.NodeRef
import com.ericsson.nms.security.nscs.api.model.NodeReference
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.nms.security.nscs.logger.NscsLogger
import com.ericsson.nms.security.nscs.ssh.SSHKeyGenCommand
import com.ericsson.nms.security.nscs.utilities.MoAttributeHandler
import com.ericsson.oss.itpf.sdk.eventbus.model.EventSender
import com.ericsson.oss.itpf.security.cryptography.CryptographyService
import com.ericsson.oss.mediation.sec.model.SSHkeyManagementJob
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.ssh.ConfigureSSHKeyGenerationTask

import static com.ericsson.nms.security.nscs.ssh.SSHKeyGenConstants.SSH_KEY_INVALID
import static com.ericsson.nms.security.nscs.ssh.SSHKeyGenConstants.SSH_KEY_TO_BE_CREATED
import static com.ericsson.nms.security.nscs.ssh.SSHKeyGenConstants.SSH_KEY_TO_BE_DELETED

class ConfigureSSHKeyGenerationTaskHandlerFcTest extends CdiSpecification{

    private static final NodeReference NODE = new NodeRef("node123")
    private static final String SSHKEY_PRIVATE = "private"

    @ObjectUnderTest
    private ConfigureSSHKeyGenerationTaskHandler cfgSSHKeyGenTaskHandler

    @MockedImplementation
    private CryptographyService cryptographyService
    @MockedImplementation
    private NormalizableNodeReference normRef
    @MockedImplementation
    private NscsCMReaderService cMReaderService
    @MockedImplementation
    private NscsLogger nscsLogger
    @MockedImplementation
    private ConfigureSSHKeyGenerationTask taskMock

    @ImplementationInstance
    private ConfigureSSHKeyGenerationTask taskMockSSHCreate = [
            getNodeFdn : {
                return NODE.getFdn()
            },
            getNode : {
                return NODE
            },
            getIsCreate : {
                return true
            },
            getSshkeyOperation : {
                return SSH_KEY_TO_BE_CREATED
            },
            getAlgorithm : {
                return AlgorithmKeys.RSA_1024
            }
    ] as ConfigureSSHKeyGenerationTask

    @ImplementationInstance
    private ConfigureSSHKeyGenerationTask taskMockSSHDelete = [
            getNodeFdn : {
                return NODE.getFdn()
            },
            getNode : {
                return NODE
            },
            getIsCreate : {
                return true
            },
            getSshkeyOperation : {
                return SSH_KEY_TO_BE_DELETED
            },
            getAlgorithm : {
                return AlgorithmKeys.RSA_1024
            }
    ] as ConfigureSSHKeyGenerationTask

    @ImplementationInstance
    private EventSender<SSHkeyManagementJob> commandJobSenderMock = [
            send: { sshKeyManagementJob ->
                 throw new IllegalArgumentException("Invalid send command event")
            }
    ] as EventSender<SSHkeyManagementJob>

    @ImplementationInstance
    private MoAttributeHandler moAttributeHandlerMock = [
            getMOAttributeValue: { nodeFdn, moType, namespace, attributeName ->
                return SSH_KEY_INVALID
            }
    ] as MoAttributeHandler

    @ImplementationInstance
    private MoAttributeHandler moAttributeHandlerMockNull = [
            getMOAttributeValue: { nodeFdn, moType, namespace, attributeName ->
                return null
            }
    ] as MoAttributeHandler

    @ImplementationInstance
    private MoAttributeHandler moAttributeHandlerMockPartiallyNull = [
            getMOAttributeValue: { nodeFdn, moType, namespace, String attributeName ->
                if (attributeName.equals("enmSshPublicKey")) {
                    return "test"
                } else {
                    return null
                }
            }
    ] as MoAttributeHandler

    def setup() {
        cryptographyService.encrypt(_ as byte[]) >> SSHKEY_PRIVATE.getBytes()
        normRef.getNormalizedRef() >> NODE
        normRef.getName() >> NODE.getFdn()
    }

    @Override
    def addAdditionalInjectionProperties(final InjectionProperties injectionProperties) {}

    def "when operation is sshkey create/update then task handler working properly "() {
        given:
        cMReaderService.getNormalizedNodeReference(NODE) >> normRef
        when:
        cfgSSHKeyGenTaskHandler.processTask(taskMockSSHCreate)
        then:
        1 * nscsLogger.workFlowTaskHandlerFinishedWithSuccess (_ as ConfigureSSHKeyGenerationTask, "Process completed")
        and:
        cfgSSHKeyGenTaskHandler.sshCommandToExecute == SSHKeyGenCommand.SSH_KEY_CREATE.toString()
    }

    def "when operation is sshkey delete and keys are set to Invalid_Key then task handler working properly "() {
        given:
        cMReaderService.getNormalizedNodeReference(NODE) >> normRef
        cfgSSHKeyGenTaskHandler.moAttributeHandler = moAttributeHandlerMock
        when:
        cfgSSHKeyGenTaskHandler.processTask(taskMockSSHDelete)
        then:
        1 * nscsLogger.workFlowTaskHandlerFinishedWithSuccess (_ as ConfigureSSHKeyGenerationTask, "Process completed")
        and:
        cfgSSHKeyGenTaskHandler.sshCommandToExecute == SSHKeyGenCommand.SSH_KEY_DELETE.toString()
    }

    def "when operation is sshkey delete and keys are set null then task handler working properly "() {
        given:
        cMReaderService.getNormalizedNodeReference(NODE) >> normRef
        cfgSSHKeyGenTaskHandler.moAttributeHandler = moAttributeHandlerMockNull
        when:
        cfgSSHKeyGenTaskHandler.processTask(taskMockSSHDelete)
        then:
        1 * nscsLogger.workFlowTaskHandlerFinishedWithSuccess (_ as ConfigureSSHKeyGenerationTask, "Process completed")
        and:
        cfgSSHKeyGenTaskHandler.sshCommandToExecute == SSHKeyGenCommand.SSH_KEY_DELETE.toString()
    }

    def "when operation is sshkey delete and keys are set partially null then task handler working properly "() {
        given:
        cMReaderService.getNormalizedNodeReference(NODE) >> normRef
        cfgSSHKeyGenTaskHandler.moAttributeHandler = moAttributeHandlerMockPartiallyNull
        when:
        cfgSSHKeyGenTaskHandler.processTask(taskMockSSHDelete)
        then:
        1 * nscsLogger.workFlowTaskHandlerFinishedWithSuccess (_ as ConfigureSSHKeyGenerationTask, "Process completed")
        and:
        cfgSSHKeyGenTaskHandler.sshCommandToExecute == SSHKeyGenCommand.SSH_KEY_DELETE.toString()
    }

    def "with sshkey any operation and node is null then task handler raise exception "() {
        given:
        taskMock.getNode() >> null
        when:
        cfgSSHKeyGenTaskHandler.processTask(taskMock)
        then:
        1 * nscsLogger.workFlowTaskHandlerFinishedWithError (_ as ConfigureSSHKeyGenerationTask, _ as String)
        and:
        UnexpectedErrorException e = thrown()
        e.getMessage().contains("Null NodeReference for node")
    }

    def "with sshkey any operation and algo is null then task handler raise exception "() {
        given:
        taskMock.getNode() >> NODE
        taskMock.getAlgorithm() >> null
        when:
        cfgSSHKeyGenTaskHandler.processTask(taskMock)
        then:
        1 * nscsLogger.workFlowTaskHandlerFinishedWithError (_ as ConfigureSSHKeyGenerationTask, _ as String)
        and:
        UnexpectedErrorException e = thrown()
        e.getMessage().contains("Null AlgorithmKeys for NetworkElementSecurity MO")
    }

    def "with sshkey any operation and normalized reference for NES is null task handler raise exception "() {
        given:
        taskMock.getNode() >> NODE
        taskMock.getAlgorithm() >> AlgorithmKeys.RSA_1024
        cMReaderService.getNormalizedNodeReference(NODE) >> null
        when:
        cfgSSHKeyGenTaskHandler.processTask(taskMock)
        then:
        2 * nscsLogger.workFlowTaskHandlerFinishedWithError (_ as ConfigureSSHKeyGenerationTask, _ as String)
        and:
        UnexpectedErrorException e = thrown()
        e.getMessage().contains(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR) &&
        e.getCause().getMessage().contains("Fetched null NormalizedNodeReference ref for NetworkElementSecurity MO")
    }

    def "with invalid sshkey operation task handler raise exception "() {
        given:
        taskMock.getNode() >> NODE
        taskMock.getAlgorithm() >> AlgorithmKeys.RSA_1024
        taskMock.getSshkeyOperation() >> "Invalid operation"
        cMReaderService.getNormalizedNodeReference(NODE) >> normRef
        when:
        cfgSSHKeyGenTaskHandler.processTask(taskMock)
        then:
        2 * nscsLogger.workFlowTaskHandlerFinishedWithError (_ as ConfigureSSHKeyGenerationTask, _ as String)
        and:
        UnexpectedErrorException e = thrown()
        e.getMessage().contains(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR) &&
                e.getCause().getMessage().contains("SSH KEY: invalid operation for node")
    }

    def "with any sshkey operation and send event generic exception task handler raise an exception "() {
        given:
        cMReaderService.getNormalizedNodeReference(NODE) >> normRef
        cfgSSHKeyGenTaskHandler.commandJobSender = commandJobSenderMock
        when:
        cfgSSHKeyGenTaskHandler.processTask(taskMockSSHCreate)
        then:
        1 * nscsLogger.workFlowTaskHandlerFinishedWithError (_ as ConfigureSSHKeyGenerationTask, _ as String)
        and:
        UnexpectedErrorException e = thrown()
        e.getMessage().contains(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR) &&
        e.getCause().getMessage().contains("Invalid send command event")
    }
}
