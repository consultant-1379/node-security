package com.ericsson.nms.security.nscs.ssh

import static com.ericsson.nms.security.nscs.ssh.SSHKeyGenConstants.SSH_KEY_TO_BE_CREATED
import static com.ericsson.nms.security.nscs.ssh.SSHKeyGenConstants.SSH_KEY_TO_BE_DELETED
import static com.ericsson.nms.security.nscs.ssh.SSHKeyGenConstants.SSH_KEY_TO_BE_UPDATED

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.exception.CouldNotReadMoAttributeException
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException
import com.ericsson.nms.security.nscs.api.model.NodeRef
import com.ericsson.nms.security.nscs.api.model.NodeReference
import com.ericsson.nms.security.nscs.data.ModelDefinition
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.nms.security.nscs.logger.NscsLogger
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl
import com.ericsson.nms.security.nscs.utilities.MoAttributeHandler
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.TargetTypeInformation
import com.ericsson.oss.services.cm.cmshared.dto.CmObject
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse

import spock.lang.Shared

class SSHKeyNodeValidatorUtilityTest extends CdiSpecification {

    @ObjectUnderTest
    private SSHKeyNodeValidatorUtility sshKeyNodeValidatorUtility

    @MockedImplementation
    private NscsLogger nscsLogger

    @MockedImplementation
    private NscsCMReaderService readerMock

    @ImplementationInstance
    NscsCMReaderService nscsCMReaderServiceMock = [
        exists : { final String fdn ->
            return true
        },
        getNormalizableNodeReference : { final NodeReference node ->
            return new SSHKeyMockNormalizableNodeRef()
        },
        getMOAttribute : {final NodeReference node, final String moType, final String namespace, final String attribute ->
            CmResponse cmResponse = new CmResponse()
            CmObject cmObject = new CmObject()
            Map<String, Object> attributes = new LinkedHashMap(0)
            attributes.put(ModelDefinition.CmFunction.SYNC_STATUS, ModelDefinition.CmFunction.SyncStatusValue.SYNCHRONIZED.name())
            cmObject.setAttributes(attributes)
            Collection<CmObject> cmObjects = new ArrayList<>()
            cmObjects.add(cmObject)
            cmResponse.setTargetedCmObjects(cmObjects)
            return cmResponse
        }
    ] as NscsCMReaderService

    @ImplementationInstance
    NscsCMReaderService nscsCMReaderServiceMock2 = [
        exists : { final String fdn ->
            return true
        },
        getNormalizableNodeReference : { final NodeReference node ->
            return new SSHKeyMockNormalizableNodeRef()
        },
        getMOAttribute : {final NodeReference node, final String moType, final String namespace, final String attribute ->
            CmResponse cmResponse = new CmResponse()
            CmObject cmObject = new CmObject()
            Map<String, Object> attributes = new LinkedHashMap(0)
            attributes.put(ModelDefinition.NetworkElementSecurity.ENM_SSH_PUBLIC_KEY, "not empty")
            cmObject.setAttributes(attributes)

            Collection<CmObject> cmObjects = new ArrayList<>()
            cmObjects.add(cmObject)
            cmResponse.setTargetedCmObjects(cmObjects)
            return cmResponse
        }
    ] as NscsCMReaderService

    @ImplementationInstance
    MoAttributeHandler moAttributeHandlerCreateMock = [
        getMOAttributeValue : { nodeFdn, moType, namespace, attributeName ->
            return null
        }
    ] as MoAttributeHandler

    @ImplementationInstance
    MoAttributeHandler moAttributeHandlerUpdateMock = [
        getMOAttributeValue : { nodeFdn, moType, namespace, attributeName ->
            if (attributeName == ModelDefinition.NetworkElementSecurity.ENM_SSH_PUBLIC_KEY || attributeName == ModelDefinition.NetworkElementSecurity.ENM_SSH_PRIVATE_KEY ) {
                return "DUMMY-SSH-KEY"
            } else if (attributeName == ModelDefinition.NetworkElementSecurity.ALGORITHM_AND_KEY_SIZE) {
                if (scenario == "valid-algo-and-key-size") {
                    return "RSA_1024"
                } else {
                    return "UNSUPPORTED"
                }
            }
        }
    ] as MoAttributeHandler

    @ImplementationInstance
    MoAttributeHandler moAttributeHandlerExceptionRaisedMock = [
        getMOAttributeValue : { nodeFdn, moType, namespace, attributeName ->
            throw new CouldNotReadMoAttributeException("Not read ssh keys")
        }
    ] as MoAttributeHandler

    @ImplementationInstance
    NscsModelServiceImpl nscsModelServiceImpl = [
        getSupportedAlgorithmAndKeySize : {
            return [
                "RSA_1024",
                "RSA_2048",
                "RSA_4096"
            ]
        }
    ] as NscsModelServiceImpl

    @Shared
    private String scenario

    def setup() {
    }

    @Override
    def addAdditionalInjectionProperties(final InjectionProperties injectionProperties) {}

    def "create ssh key happy path "() {
        given:
        final List<NodeReference> inputNodeList = new ArrayList<>()
        NodeReference nodeRef = new NodeRef("node1")
        inputNodeList.add(nodeRef)
        def algorithm = "RSA_1024"
        Map<NodeReference, String> validNodesList = new HashMap<>()
        Map<String, NscsServiceException> invalidNodesErrorMap = new HashMap<>()
        sshKeyNodeValidatorUtility.reader = nscsCMReaderServiceMock
        sshKeyNodeValidatorUtility.moAttributeHandler = moAttributeHandlerCreateMock
        when:
        boolean  isValid = sshKeyNodeValidatorUtility.validateSshKeyInputNodes(inputNodeList, validNodesList,
                invalidNodesErrorMap, SSH_KEY_TO_BE_CREATED, algorithm)
        then:
        isValid
    }

    def "update ssh key with algorithm happy path"() {
        given:
        final List<NodeReference> inputNodeList = new ArrayList<>()
        NodeReference nodeRef = new NodeRef("node1")
        inputNodeList.add(nodeRef)
        def algorithm = "RSA_1024"

        Map<NodeReference, String> validNodesList = new HashMap<>()
        Map<String, NscsServiceException> invalidNodesErrorMap = new HashMap<>()
        sshKeyNodeValidatorUtility.reader = nscsCMReaderServiceMock2
        sshKeyNodeValidatorUtility.moAttributeHandler = moAttributeHandlerUpdateMock
        when:
        boolean  isValid = sshKeyNodeValidatorUtility.validateSshKeyInputNodes(inputNodeList, validNodesList,
                invalidNodesErrorMap, SSH_KEY_TO_BE_UPDATED, algorithm)
        then:
        isValid
    }

    def "update ssh key without algorithm happy path"() {
        given:
        final List<NodeReference> inputNodeList = new ArrayList<>()
        NodeReference nodeRef = new NodeRef("node1")
        inputNodeList.add(nodeRef)
        def algorithm = null
        scenario = "valid-algo-and-key-size"
        Map<NodeReference, String> validNodesList = new HashMap<>()
        Map<String, NscsServiceException> invalidNodesErrorMap = new HashMap<>()
        sshKeyNodeValidatorUtility.reader = nscsCMReaderServiceMock2
        sshKeyNodeValidatorUtility.moAttributeHandler = moAttributeHandlerUpdateMock
        when:
        boolean  isValid = sshKeyNodeValidatorUtility.validateSshKeyInputNodes(inputNodeList, validNodesList,
                invalidNodesErrorMap, SSH_KEY_TO_BE_UPDATED, algorithm)
        then:
        isValid
    }

    def "update ssh key without algorithm with invalid algorithm and key size in NES"() {
        given:
        final List<NodeReference> inputNodeList = new ArrayList<>()
        NodeReference nodeRef = new NodeRef("node1")
        inputNodeList.add(nodeRef)
        def algorithm = null
        scenario = "invalid-algo-and-key-size"
        Map<NodeReference, String> validNodesList = new HashMap<>()
        Map<String, NscsServiceException> invalidNodesErrorMap = new HashMap<>()
        sshKeyNodeValidatorUtility.reader = nscsCMReaderServiceMock2
        sshKeyNodeValidatorUtility.moAttributeHandler = moAttributeHandlerUpdateMock
        when:
        boolean  isValid = sshKeyNodeValidatorUtility.validateSshKeyInputNodes(inputNodeList, validNodesList,
                invalidNodesErrorMap, SSH_KEY_TO_BE_UPDATED, algorithm)
        then:
        !isValid
    }

    def "delete ssh key happy path "() {
        given:
        final List<NodeReference> inputNodeList = new ArrayList<>()
        NodeReference nodeRef = new NodeRef("node1")
        inputNodeList.add(nodeRef)
        def algorithm = null
        scenario = "valid-algo-and-key-size"
        Map<NodeReference, String> validNodesList = new HashMap<>()
        Map<String, NscsServiceException> invalidNodesErrorMap = new HashMap<>()
        sshKeyNodeValidatorUtility.reader = nscsCMReaderServiceMock
        sshKeyNodeValidatorUtility.moAttributeHandler = moAttributeHandlerUpdateMock
        when:
        boolean  isValid = sshKeyNodeValidatorUtility.validateSshKeyInputNodes(inputNodeList, validNodesList,
                invalidNodesErrorMap, SSH_KEY_TO_BE_DELETED, algorithm)
        then:
        isValid
    }

    def "delete ssh key happy path with invalid algorithm and key size in NES"() {
        given:
        final List<NodeReference> inputNodeList = new ArrayList<>()
        NodeReference nodeRef = new NodeRef("node1")
        inputNodeList.add(nodeRef)
        def algorithm = null
        scenario = "invalid-algo-and-key-size"
        Map<NodeReference, String> validNodesList = new HashMap<>()
        Map<String, NscsServiceException> invalidNodesErrorMap = new HashMap<>()
        sshKeyNodeValidatorUtility.reader = nscsCMReaderServiceMock
        sshKeyNodeValidatorUtility.moAttributeHandler = moAttributeHandlerUpdateMock
        when:
        boolean  isValid = sshKeyNodeValidatorUtility.validateSshKeyInputNodes(inputNodeList, validNodesList,
                invalidNodesErrorMap, SSH_KEY_TO_BE_DELETED, algorithm)
        then:
        isValid
    }

    def "when create ssh key is called and node doesn't exist return false"() {
        given:
        final List<NodeReference> inputNodeList = new ArrayList<>()
        NodeReference nodeRef = new NodeRef("node1")
        inputNodeList.add(nodeRef)
        def algorithm = "RSA_1024"
        Map<NodeReference, String> validNodesList = new HashMap<>()
        Map<String, NscsServiceException> invalidNodesErrorMap = new HashMap<>()
        sshKeyNodeValidatorUtility.reader = readerMock
        sshKeyNodeValidatorUtility.moAttributeHandler = moAttributeHandlerCreateMock
        when:
        boolean  isValid = sshKeyNodeValidatorUtility.validateSshKeyInputNodes(inputNodeList, validNodesList,
                invalidNodesErrorMap, SSH_KEY_TO_BE_CREATED, algorithm)
        then:
        !isValid
        and:
        def message = invalidNodesErrorMap.get(nodeRef.getName()).getMessage()
        message == NscsErrorCodes.NETWORK_ELEMENT_NOT_FOUND_FOR_THIS_MECONTEXT
    }

    def "when create ssh key is called and key already generated return false"() {
        given:
        final List<NodeReference> inputNodeList = new ArrayList<>()
        NodeReference nodeRef = new NodeRef("node1")
        inputNodeList.add(nodeRef)
        def algorithm = "RSA_1024"
        Map<NodeReference, String> validNodesList = new HashMap<>()
        Map<String, NscsServiceException> invalidNodesErrorMap = new HashMap<>()
        sshKeyNodeValidatorUtility.reader = nscsCMReaderServiceMock2
        sshKeyNodeValidatorUtility.moAttributeHandler = moAttributeHandlerUpdateMock
        when:
        boolean  isValid = sshKeyNodeValidatorUtility.validateSshKeyInputNodes(inputNodeList, validNodesList,
                invalidNodesErrorMap, SSH_KEY_TO_BE_CREATED, algorithm)
        then:
        !isValid
        and:
        def message = invalidNodesErrorMap.get(nodeRef.getName()).getMessage()
        message == NscsErrorCodes.KEYPAIR_ALREADY_GENERATED
    }

    def "when update ssh key is called and key pair are not generated return false"() {
        given:
        final List<NodeReference> inputNodeList = new ArrayList<>()
        NodeReference nodeRef = new NodeRef("node1")
        inputNodeList.add(nodeRef)
        def algorithm = "RSA_1024"
        Map<NodeReference, String> validNodesList = new HashMap<>()
        Map<String, NscsServiceException> invalidNodesErrorMap = new HashMap<>()
        sshKeyNodeValidatorUtility.reader = nscsCMReaderServiceMock
        sshKeyNodeValidatorUtility.moAttributeHandler = moAttributeHandlerCreateMock

        when:
        boolean  isValid = sshKeyNodeValidatorUtility.validateSshKeyInputNodes(inputNodeList, validNodesList,
                invalidNodesErrorMap, SSH_KEY_TO_BE_UPDATED, algorithm)
        then:
        !isValid
        and:
        def message = invalidNodesErrorMap.get(nodeRef.getName()).getMessage()
        message == NscsErrorCodes.KEYPAIR_NOT_FOUND
    }

    def "when delete ssh key is called and key pair are invalid and forced delete is required return true"() {
        given:
        final List<NodeReference> inputNodeList = new ArrayList<>()
        NodeReference nodeRef = new NodeRef("node1")
        inputNodeList.add(nodeRef)

        Map<NodeReference, String> validNodesList = new HashMap<>()
        Map<String, NscsServiceException> invalidNodesErrorMap = new HashMap<>()
        sshKeyNodeValidatorUtility.reader = nscsCMReaderServiceMock
        sshKeyNodeValidatorUtility.moAttributeHandler = moAttributeHandlerUpdateMock
        
        when:
        boolean  isValid = sshKeyNodeValidatorUtility.validateSshKeyInputNodes(inputNodeList, validNodesList,
                invalidNodesErrorMap, SSH_KEY_TO_BE_DELETED, null)
        then:
        isValid
    }

    def "when delete ssh key is called and exception from reader raised then return false"() {
        given:
        final List<NodeReference> inputNodeList = new ArrayList<>()
        NodeReference nodeRef = new NodeRef("node1")
        inputNodeList.add(nodeRef)
        def algorithm = "RSA_1024"
        Map<NodeReference, String> validNodesList = new HashMap<>()
        Map<String, NscsServiceException> invalidNodesErrorMap = new HashMap<>()
        sshKeyNodeValidatorUtility.reader = nscsCMReaderServiceMock
        sshKeyNodeValidatorUtility.moAttributeHandler = moAttributeHandlerExceptionRaisedMock
        when:
        boolean  isValid = sshKeyNodeValidatorUtility.validateSshKeyInputNodes(inputNodeList, validNodesList,
                invalidNodesErrorMap, SSH_KEY_TO_BE_DELETED, algorithm)
        then:
        !isValid
        and:
        def message = invalidNodesErrorMap.get(nodeRef.getName()).getMessage()
        message == NscsErrorCodes.COULD_NOT_READ_MO_ATTRIBUTES
    }

    def "when invalid ssh key command then return false"() {
        given:
        final List<NodeReference> inputNodeList = new ArrayList<>()
        NodeReference nodeRef = new NodeRef("node1")
        inputNodeList.add(nodeRef)
        def algorithm = "RSA_1024"
        Map<NodeReference, String> validNodesList = new HashMap<>()
        Map<String, NscsServiceException> invalidNodesErrorMap = new HashMap<>()
        sshKeyNodeValidatorUtility.reader = nscsCMReaderServiceMock
        sshKeyNodeValidatorUtility.moAttributeHandler = moAttributeHandlerUpdateMock
        when:
        boolean  isValid = sshKeyNodeValidatorUtility.validateSshKeyInputNodes(inputNodeList, validNodesList,
                invalidNodesErrorMap, "Invalid ssh key command", algorithm)
        then:
        !isValid
        and:
        def message = invalidNodesErrorMap.get(nodeRef.getName()).getMessage()
        message == NscsErrorCodes.INVALID_ARGUMENT_VALUE
    }

    private class SSHKeyMockNormalizableNodeRef implements NormalizableNodeReference {
        @Override
        String getName() {
            return "node1"
        }

        @Override
        String getFdn() {
            return String.format("ManagedElement=%s", "node1")
        }

        @Override
        boolean hasNormalizedRef() {
            return false
        }

        @Override
        NodeReference getNormalizedRef() {
            return new NodeRef("node1")
        }

        @Override
        boolean hasNormalizableRef() {
            return false
        }

        @Override
        NodeReference getNormalizableRef() {
            return null
        }

        @Override
        String getTargetCategory() {
            return TargetTypeInformation.CATEGORY_NODE
        }

        @Override
        String getNeType() {
            return "ERBS"
        }

        @Override
        String getOssModelIdentity() {
            return ""
        }
    }
}
