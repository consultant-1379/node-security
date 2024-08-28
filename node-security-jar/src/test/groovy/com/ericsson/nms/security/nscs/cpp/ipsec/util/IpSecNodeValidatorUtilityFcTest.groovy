/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2023
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.cpp.ipsec.util

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.exception.IpSecActionException
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException
import com.ericsson.nms.security.nscs.api.model.NodeRef
import com.ericsson.nms.security.nscs.api.model.NodeReference
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService
import com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.Nodes
import com.ericsson.nms.security.nscs.data.ModelDefinition
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.TargetTypeInformation
import com.ericsson.oss.services.cm.cmshared.dto.CmObject
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse
import com.ericsson.oss.services.nscs.workflow.WfQueryService

class IpSecNodeValidatorUtilityFcTest extends CdiSpecification {
    @ObjectUnderTest
    IpSecNodeValidatorUtility ipSecNodeValidatorUtility

    @MockedImplementation
    CppIpSecOperationUtility cppIpSecStatusUtility

    def cppIpSecConfigurationTypeResponse = new CppIpSecConfigurationTypeResponse()

    @ImplementationInstance
    NscsCMReaderService nscsCMReaderServiceMock = [
            exists : { final String fdn ->
                return true
            },
            getNormalizableNodeReference : { final NodeReference node ->
                return new MockNormalizableNodeRef()
            },
            getMOAttribute : {final NodeReference node, final String moType,
                final String namespace, final String attribute ->
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
    NscsCMReaderService nscsCMReaderServiceMockExcp = [
            exists : { final String fdn ->
                return false
            },
            getNormalizableNodeReference : { final NodeReference node ->
                return new MockNormalizableNodeRef()
            },
            getMOAttribute : {final NodeReference node, final String moType,
                              final String namespace, final String attribute ->
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
    NscsCapabilityModelService nscsCapabilityModelServiceMock = [
            getMirrorRootMo : { final NormalizableNodeReference normNodeRef ->
                return new ModelDefinition.MeContext().managedElement
            }
    ] as NscsCapabilityModelService

    WfQueryService wfQueryServiceMock = [
            isWorkflowInProgress : { final NodeReference nodeRef ->
                return false
            }
    ] as WfQueryService

    private Nodes.Node nodeData_Enable_Conf1

    def setup() {
        nodeData_Enable_Conf1 = new Nodes.Node()
        nodeData_Enable_Conf1.setNodeFdn("node1")
        nodeData_Enable_Conf1.setSubAltName("ALT_SUB_NAME_001")
        setupIpSecConfigurationOne()
    }

    @Override
    def addAdditionalInjectionProperties(final InjectionProperties injectionProperties) {
        injectionProperties.autoLocateFrom("com.ericsson.nms.security.nscs.capabilitymodel.service")
    }

    def "if the node validation of the IpSec operation type fails then it throws IpSecActionException "() {
        given:
        cppIpSecConfigurationTypeResponse.setValid(false)
        cppIpSecConfigurationTypeResponse.setMessage("No change required")
        cppIpSecConfigurationTypeResponse.setSuggestedSolution("Deactivate configuration")

        cppIpSecStatusUtility.checkIpSecOperation(_ as Nodes.Node) >>  cppIpSecConfigurationTypeResponse
        when:
        ipSecNodeValidatorUtility.validateNodeForIpSecConfigurationType(nodeData_Enable_Conf1)
        then:
        def e = thrown(IpSecActionException)
        e.suggestedSolution.contains("Deactivate configuration")
    }

    def "if the node validation for IpSec command status is correct then no excp is thrown" () {
        given:
        final List<NormalizableNodeReference> validNodesList = new ArrayList<NormalizableNodeReference>()
        final Map<NodeReference, NscsServiceException> invalidNodesErrorMap = new HashMap<>()
        final List<NodeReference> uniqueNodes = new ArrayList<>()
        NodeReference nodeRef = new NodeRef("node1")
        uniqueNodes.add(nodeRef)
        ipSecNodeValidatorUtility.reader = nscsCMReaderServiceMock
        ipSecNodeValidatorUtility.nscsCapabilityModelService = nscsCapabilityModelServiceMock
        ipSecNodeValidatorUtility.wfQuery = wfQueryServiceMock
        when:
        ipSecNodeValidatorUtility.validateNodesForIpsecStatus(uniqueNodes, validNodesList, invalidNodesErrorMap)
        then:
        notThrown(Exception.class)
    }

    def "if the node validation for IpSec command status fails then the invalid error map is filled   " () {
        given:
        final List<NormalizableNodeReference> validNodesList = new ArrayList<NormalizableNodeReference>()
        final Map<NodeReference, NscsServiceException> invalidNodesErrorMap = new HashMap<>()
        final List<NodeReference> uniqueNodes = new ArrayList<>()
        NodeReference nodeRef = new NodeRef("node1")
        uniqueNodes.add(nodeRef)
        ipSecNodeValidatorUtility.reader = nscsCMReaderServiceMockExcp
        when:
        ipSecNodeValidatorUtility.validateNodesForIpsecStatus(uniqueNodes, validNodesList, invalidNodesErrorMap)
        then:
        def message = invalidNodesErrorMap.get(nodeRef).getMessage()
        message == "The NetworkElement MO does not exist for the associated MeContext MO"
    }

    def "if the node validation fails then return false" () {
        given:
        final List<NormalizableNodeReference> validNodesList = new ArrayList<NormalizableNodeReference>()
        final Map<NodeReference, NscsServiceException> invalidNodesErrorMap = new HashMap<>()
        setupIpSecConfigurationOne()
        final List<Nodes.Node> uniqueNodes = new ArrayList<>()
        uniqueNodes.add(nodeData_Enable_Conf1)
        ipSecNodeValidatorUtility.reader = nscsCMReaderServiceMockExcp
        ipSecNodeValidatorUtility.nscsCapabilityModelService = nscsCapabilityModelServiceMock
        ipSecNodeValidatorUtility.wfQuery = wfQueryServiceMock
        cppIpSecStatusUtility.checkIpSecOperation(_ as Nodes.Node) >>  true
        when:
        def isValidNodes = ipSecNodeValidatorUtility.validateNodes(uniqueNodes, validNodesList, invalidNodesErrorMap)
        then:
        !isValidNodes
    }

    def "if the node validation fails then return true" () {
        given:
        final List<NormalizableNodeReference> validNodesList = new ArrayList<NormalizableNodeReference>()
        final Map<NodeReference, NscsServiceException> invalidNodesErrorMap = new HashMap<>()
        setupIpSecConfigurationOne()
        final List<Nodes.Node> uniqueNodes = new ArrayList<>()
        uniqueNodes.add(nodeData_Enable_Conf1)
        ipSecNodeValidatorUtility.reader = nscsCMReaderServiceMock
        ipSecNodeValidatorUtility.nscsCapabilityModelService = nscsCapabilityModelServiceMock
        ipSecNodeValidatorUtility.wfQuery = wfQueryServiceMock

        cppIpSecConfigurationTypeResponse.setValid(true)
        cppIpSecConfigurationTypeResponse.setMessage("")
        cppIpSecConfigurationTypeResponse.setSuggestedSolution("")

        cppIpSecStatusUtility.checkIpSecOperation(_ as Nodes.Node) >>  cppIpSecConfigurationTypeResponse
        when:
        def isValidNodes = ipSecNodeValidatorUtility.validateNodes(uniqueNodes, validNodesList, invalidNodesErrorMap)
        then:
        isValidNodes
    }

    private void setupIpSecConfigurationOne() {
        final Nodes.Node.EnableOMConfiguration1 enableConf1 = new Nodes.Node.EnableOMConfiguration1()
        enableConf1.setRemoveTrustOnFailure(true)
        enableConf1.setTrustedCertificateFilePath("/home/smrs/abc.pem")
        enableConf1.setDnsServer1("1.1.1.1")
        enableConf1.setDnsServer2("2.2.2.2")
        enableConf1.setIpAddressOaMInner("3.3.3.3")
        enableConf1.setNetworkPrefixLength(32L)
        enableConf1.setIpAccessHostEtId("abcdef")
        enableConf1.setDefaultrouter0("192.168.20.1")
        enableConf1.setIpAddressOaMOuter("192.168.32.2")
        enableConf1.setRemoteIpAddress("192.168.99.8")
        enableConf1.setPeerOaMIpAddress("192.168.51.1")
        enableConf1.setPeerIdentityIdFqdn("192.168.2.2")
        enableConf1.setPeerIdentityIdType("idType")
        enableConf1.setTsLocalIpAddressMask("192.168.1.23")
        final Nodes.Node.EnableOMConfiguration1.TsRemoteIpAddressRanges tsRemoteIpAddressRanges1 =
                new Nodes.Node.EnableOMConfiguration1.TsRemoteIpAddressRanges()
        tsRemoteIpAddressRanges1.setMask("20")
        tsRemoteIpAddressRanges1.setIpAddress("192.168.86.2")
        enableConf1.getTsRemoteIpAddressRanges().add(tsRemoteIpAddressRanges1)
        nodeData_Enable_Conf1.setEnableOMConfiguration1(enableConf1)
    }

    private class MockNormalizableNodeRef implements NormalizableNodeReference {
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
