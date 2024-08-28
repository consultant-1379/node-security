package com.ericsson.nms.security.nscs.handler.command.impl

import static com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelConstants.NSCS_EOI_MOM

import javax.inject.Inject

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.command.NscsCommandType
import com.ericsson.nms.security.nscs.api.command.NscsMessageCommandResponse
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManager
import com.ericsson.nms.security.nscs.api.command.types.KeyGeneratorCommand
import com.ericsson.nms.security.nscs.api.exception.KeyGenerationHandlerException
import com.ericsson.nms.security.nscs.api.exception.NetworkElementNotfoundException
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException
import com.ericsson.nms.security.nscs.api.exception.SshKeyWfException
import com.ericsson.nms.security.nscs.api.model.NodeRef
import com.ericsson.nms.security.nscs.api.model.NodeReference
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.nms.security.nscs.handler.CommandContext
import com.ericsson.nms.security.nscs.logger.NscsLogger
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl
import com.ericsson.nms.security.nscs.ssh.SSHKeyNodeValidatorUtility
import com.ericsson.nms.security.nscs.utilities.NscsInputNodeRetrievalUtility
import com.ericsson.oss.services.dto.JobStatusRecord
import com.ericsson.oss.services.enums.JobGlobalStatusEnum
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler

import spock.lang.Shared

class SSHKeyCommandHandlerHelperTest extends CdiSpecification {

    private static final String ALL_VALID_NODES_FORMAT = "Successfully started a job for creating SSH key."
    private static final String ALL_INVALID_NODES_FORMAT = "All input nodes are invalid, see error details in following table:"
    private static final String PARTIALLY_INVALID_NODES_FORMAT = "Some input nodes are invalid, see error details in following table:"

    @ObjectUnderTest
    private SSHKeyCommandHandlerHelper sshKeyCommandHandlerHelper

    @Inject
    KeyGeneratorCommand command

    @MockedImplementation
    NscsLogger nscsLogger

    @MockedImplementation
    private CommandContext context

    @Inject
    private NscsJobCacheHandler nscsJobCacheHandler

    @MockedImplementation
    private NormalizableNodeReference normalizableNodeReferenceMock
    
    @MockedImplementation
    private NscsCMReaderService reader
    
    @MockedImplementation
    private NscsCommandManager nscsCommandManagerMock
    
    @ImplementationInstance
    private NscsInputNodeRetrievalUtility nscsInputNodeRetrievalUtilityMock = [
        getNodeReferenceList : { command ->
            return inputNodesList
        }
    ] as NscsInputNodeRetrievalUtility

    @ImplementationInstance
    private SSHKeyNodeValidatorUtility sshkeyNodeValidatorUtility = [
        validateSshKeyInputNodes : { List inputNodesList , Map validNodesAlgorithmMap, Map invalidNodesErrorMap, String isCreate, String algorithm ->
            if (scenario == "all-valid") {
                for (NodeReference inputNode : inputNodesList) {
                    validNodesAlgorithmMap.put(inputNode, algorithm)
                }
                return true
            } else if (scenario == "all-invalid") {
                for (NodeReference inputNode : inputNodesList) {
                    invalidNodesErrorMap.put(inputNode.getName(), new NetworkElementNotfoundException())
                }
                return false
            } else {
                for (NodeReference inputNode : inputNodesList) {
                    if (inputNode.getName() == "valid") {
                        validNodesAlgorithmMap.put(inputNode, algorithm)
                    } else {
                        invalidNodesErrorMap.put(inputNode.getName(), new NetworkElementNotfoundException())
                    }
                }
            return false
        }
        }
    ] as SSHKeyNodeValidatorUtility

    @ImplementationInstance
    private NscsCommandManager nscsCommandManagerMockWfsException = [
        executeSshKeyWorkflows : { requests, jobStatusRecord ->
            throw new SshKeyWfException()
        }
    ] as NscsCommandManager

    @ImplementationInstance
    private NscsCommandManager nscsCommandManagerMockExcp = [
        executeSshKeyWorkflows : { requests, jobStatusRecord ->
            throw new NullPointerException("Send Null Pointer Exception")
        }
    ] as NscsCommandManager

    @ImplementationInstance
    private NscsCapabilityModelService capabilityModelServiceMock = [
        getMomType : { nodeRef ->
            return NSCS_EOI_MOM
        }
    ] as NscsCapabilityModelService

    @MockedImplementation
    private NscsModelServiceImpl nscsModelServiceImpl

    @Shared
    private JobStatusRecord jobStatusRecord
    @Shared
    private List<NodeReference>inputNodesList
    @Shared
    private String scenario

    def setup() {
        UUID jobId = UUID.randomUUID()
        jobStatusRecord = new JobStatusRecord()
        jobStatusRecord.setGlobalStatus(JobGlobalStatusEnum.PENDING)
        jobStatusRecord.setUserId("user")
        jobStatusRecord.setJobId(jobId)

        NodeReference validNode = new NodeRef("valid")
        NodeReference invalidNode = new NodeRef("invalid")
        inputNodesList = new ArrayList<>()
        inputNodesList.add(validNode)
        inputNodesList.add(invalidNode)

        sshKeyCommandHandlerHelper.nscsCommandManager = nscsCommandManagerMock
        
        reader.getNormalizableNodeReference(_ as NodeReference) >> normalizableNodeReferenceMock
    }

    @Override
    def addAdditionalInjectionProperties(final InjectionProperties injectionProperties) {}

    def "when ssh key command is correct and all node are valid it returns a proper response message"() {
        given:
        scenario = "all-valid"
        command.setProperties(["algorithm-type-size": "RSA_2048"])
        nscsJobCacheHandler.insertJob(_ as NscsCommandType) >> jobStatusRecord
        when:
        def response = (NscsMessageCommandResponse)sshKeyCommandHandlerHelper.processSshKey(command, context,
                SSHKeyCommandHandlerHelper.SSHKeyConfigurationMode.CREATE_SSH_KEY)
        then:
        response.getMessage().contains(ALL_VALID_NODES_FORMAT)
    }

    def "when ssh key command is correct and all node are invalid it returns a proper response message"() {
        given:
        scenario = "all-invalid"
        command.setProperties(["algorithm-type-size": "RSA_2048"])
        nscsJobCacheHandler.insertJob(_ as NscsCommandType) >> jobStatusRecord
        when:
        def response = (NscsNameMultipleValueCommandResponse)sshKeyCommandHandlerHelper.processSshKey(command, context,
                SSHKeyCommandHandlerHelper.SSHKeyConfigurationMode.CREATE_SSH_KEY)
        then:
        response.additionalInformation.contains(ALL_INVALID_NODES_FORMAT)
    }

    def "when ssh key command is correct without algo, node are partially valid,it returns a proper response message"() {
        given:
        scenario = "partially-valid"
        nscsJobCacheHandler.insertJob(_ as NscsCommandType) >> jobStatusRecord
        when:
        def response = (NscsNameMultipleValueCommandResponse)sshKeyCommandHandlerHelper.processSshKey(command, context,
                SSHKeyCommandHandlerHelper.SSHKeyConfigurationMode.UPDATE_SSH_KEY)
        then:
        response.additionalInformation.contains(PARTIALLY_INVALID_NODES_FORMAT)
    }

    def "when ssh key command is correct with algo, node are partially valid,it returns a proper response message"() {
        given:
        scenario = "partially-valid"
        command.setProperties(["algorithm-type-size": "RSA_2048"])
        nscsJobCacheHandler.insertJob(_ as NscsCommandType) >> jobStatusRecord
        when:
        def response = (NscsNameMultipleValueCommandResponse)sshKeyCommandHandlerHelper.processSshKey(command, context,
                SSHKeyCommandHandlerHelper.SSHKeyConfigurationMode.CREATE_SSH_KEY)
        then:
        response.additionalInformation.contains(PARTIALLY_INVALID_NODES_FORMAT)
    }

    def "when workflow fails it returns SshKeyWfException "() {
        given:
        nscsJobCacheHandler.insertJob(_ as NscsCommandType) >> jobStatusRecord
        sshKeyCommandHandlerHelper.nscsCommandManager = nscsCommandManagerMockWfsException
        when:
        sshKeyCommandHandlerHelper.processSshKey(command, context,
                SSHKeyCommandHandlerHelper.SSHKeyConfigurationMode.CREATE_SSH_KEY)
        then:
        def e = thrown NscsServiceException
        e.getMessage().contains(NscsErrorCodes.SSH_KEY_WF_FAILED) && e.getErrorType() == NscsServiceException.ErrorType.SSH_KEY_WF_FAILED
    }

    def "when workflow fails it returns generic Exception "() {
        given:
        nscsJobCacheHandler.insertJob(_ as NscsCommandType) >> jobStatusRecord
        sshKeyCommandHandlerHelper.nscsCommandManager = nscsCommandManagerMockExcp
        when:
        sshKeyCommandHandlerHelper.processSshKey(command, context,
                SSHKeyCommandHandlerHelper.SSHKeyConfigurationMode.CREATE_SSH_KEY)
        then:
        def e = thrown KeyGenerationHandlerException
        e.getMessage().contains(NscsErrorCodes.KEYGEN_HANDLER_ERROR) && e.getErrorType() == NscsServiceException.ErrorType.KEYGEN_HANDLER_ERROR
    }
}
