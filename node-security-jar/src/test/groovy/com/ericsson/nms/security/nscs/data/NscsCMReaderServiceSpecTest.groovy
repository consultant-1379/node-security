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
package com.ericsson.nms.security.nscs.data

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.security.nscs.api.model.NodeRef
import com.ericsson.nms.security.nscs.api.model.NodeReference
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.oss.services.security.nscs.utils.NodeDataSetup

import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Unroll

class NscsCMReaderServiceSpecTest extends NodeDataSetup {

    @ObjectUnderTest
    NscsCMReaderService nscsCMReaderService

    private static final String nodeName = "NODENAME"

    @Shared
    nodes = [
        nodeName,
        "NetworkElement="+nodeName,
        "MeContext="+nodeName,
        "ManagedElement="+nodeName
    ]

    @Shared
    virtualNodes = [
        "VirtualNetworkFunctionManager="+nodeName,
        "NetworkFunctionVirtualizationOrchestrator="+nodeName,
        "VirtualInfrastructureManager="+nodeName,
        "ManagementSystem="+nodeName,
        "CloudInfrastructureManager="+nodeName
    ]

    @Shared
    neTypesRequiringMeContext = [
        //        "ERBS",
        //        "cRAN"
    ]

    @Shared
    neTypesNotRequiringMeContext = [
        "ERBS",
        "cRAN",
        "RadioNode",
        "SGSN-MME",
        "vDU",
        "vCU-CP",
        "vCU-UP"
    ]

    @Shared
    managedElementNamespacesRequiringMeContext = [
        //        "ERBS_NODE_MODEL",
        //        "CranTop"
    ]

    @Shared
    managedElementNamespacesNotRequiringMeContext = [
        "ERBS_NODE_MODEL",
        "CranTop",
        "ComTop",
        "SgsnMmeTop",
        "VduTop",
        "VcuCpTop",
        "VcuUpTop"
    ]

    def 'object under test injection' () {
        expect:
        nscsCMReaderService != null
    }

    def 'get normalizable node reference for unsupported type MO' () {
        given:
        createUnsupportedTypeMo(nodeName)
        and:
        def nameOrFdn = "UnsupportedType="+nodeName
        def NodeReference nodeReference = mock(NodeRef.class)
        nodeReference.getFdn() >> nameOrFdn
        when:
        NormalizableNodeReference normalizableNodeReference = nscsCMReaderService.getNormalizableNodeReference(nodeReference)
        then:
        normalizableNodeReference == null
    }

    def 'get normalized node reference for unsupported type MO' () {
        given:
        createUnsupportedTypeMo(nodeName)
        and:
        def nameOrFdn = "UnsupportedType="+nodeName
        def NodeReference nodeReference = mock(NodeRef.class)
        nodeReference.getFdn() >> nameOrFdn
        when:
        NormalizableNodeReference normalizedNodeReference = nscsCMReaderService.getNormalizedNodeReference(nodeReference)
        then:
        normalizedNodeReference == null
    }

    @Unroll
    def 'get normalizable node reference for not existing node #nameOrFdn' () {
        given:
        NodeReference nodeReference = new NodeRef(nameOrFdn)
        when:
        NormalizableNodeReference normalizableNodeReference = nscsCMReaderService.getNormalizableNodeReference(nodeReference)
        then:
        normalizableNodeReference == null
        where:
        nameOrFdn << nodes + virtualNodes
    }

    @Unroll
    def 'get normalized node reference for not existing node #nameOrFdn' () {
        given:
        NodeReference nodeReference = new NodeRef(nameOrFdn)
        when:
        NormalizableNodeReference normalizedNodeReference = nscsCMReaderService.getNormalizedNodeReference(nodeReference)
        then:
        normalizedNodeReference == null
        where:
        nameOrFdn << nodes
    }

    def 'get normalizable node reference for VNFM' () {
        given:
        createVirtualNetworkFunctionManager(nodeName)
        and:
        def nameOrFdn = "VirtualNetworkFunctionManager="+nodeName
        NodeReference nodeReference = new NodeRef(nameOrFdn)
        when:
        NormalizableNodeReference normalizableNodeReference = nscsCMReaderService.getNormalizableNodeReference(nodeReference)
        then:
        normalizableNodeReference != null
        and:
        normalizableNodeReference.getFdn() == nameOrFdn
        and:
        normalizableNodeReference.getName() == nodeName
        and:
        normalizableNodeReference.getTargetCategory() == "VNFM"
        and:
        normalizableNodeReference.getNeType() == "ECM"
        and:
        normalizableNodeReference.getOssModelIdentity() == null
        and:
        normalizableNodeReference.getNormalizableRef() != null
        and:
        normalizableNodeReference.getNormalizableRef().getFdn() == nameOrFdn
        and:
        normalizableNodeReference.getNormalizableRef().getName() == nodeName
        and:
        normalizableNodeReference.getNormalizedRef() != null
        and:
        normalizableNodeReference.getNormalizedRef().getFdn() == nameOrFdn
        and:
        normalizableNodeReference.getNormalizedRef().getName() == nodeName
    }

    def 'get normalized node reference for VNFM' () {
        given:
        createVirtualNetworkFunctionManager(nodeName)
        and:
        def nameOrFdn = "VirtualNetworkFunctionManager="+nodeName
        NodeReference nodeReference = new NodeRef(nameOrFdn)
        when:
        NormalizableNodeReference normalizedNodeReference = nscsCMReaderService.getNormalizedNodeReference(nodeReference)
        then:
        normalizedNodeReference != null
        and:
        normalizedNodeReference.getFdn() == nameOrFdn
        and:
        normalizedNodeReference.getName() == nodeName
        and:
        normalizedNodeReference.getTargetCategory() == "VNFM"
        and:
        normalizedNodeReference.getNeType() == "ECM"
        and:
        normalizedNodeReference.getOssModelIdentity() == null
        and:
        normalizedNodeReference.getNormalizableRef() != null
        and:
        normalizedNodeReference.getNormalizableRef().getFdn() == nameOrFdn
        and:
        normalizedNodeReference.getNormalizableRef().getName() == nodeName
        and:
        normalizedNodeReference.getNormalizedRef() != null
        and:
        normalizedNodeReference.getNormalizedRef().getFdn() == nameOrFdn
        and:
        normalizedNodeReference.getNormalizedRef().getName() == nodeName
    }

    def 'get normalizable node reference for NFVO' () {
        given:
        createNetworkFunctionVirtualizationOrchestrator(nodeName)
        and:
        def nameOrFdn = "NetworkFunctionVirtualizationOrchestrator="+nodeName
        NodeReference nodeReference = new NodeRef(nameOrFdn)
        when:
        NormalizableNodeReference normalizableNodeReference = nscsCMReaderService.getNormalizableNodeReference(nodeReference)
        then:
        normalizableNodeReference != null
        and:
        normalizableNodeReference.getFdn() == nameOrFdn
        and:
        normalizableNodeReference.getName() == nodeName
        and:
        normalizableNodeReference.getTargetCategory() == "NFVO"
        and:
        normalizableNodeReference.getNeType() == "ECM"
        and:
        normalizableNodeReference.getOssModelIdentity() == null
        and:
        normalizableNodeReference.getNormalizableRef() != null
        and:
        normalizableNodeReference.getNormalizableRef().getFdn() == nameOrFdn
        and:
        normalizableNodeReference.getNormalizableRef().getName() == nodeName
        and:
        normalizableNodeReference.getNormalizedRef() != null
        and:
        normalizableNodeReference.getNormalizedRef().getFdn() == nameOrFdn
        and:
        normalizableNodeReference.getNormalizedRef().getName() == nodeName
    }

    def 'get normalized node reference for NFVO' () {
        given:
        createNetworkFunctionVirtualizationOrchestrator(nodeName)
        and:
        def nameOrFdn = "NetworkFunctionVirtualizationOrchestrator="+nodeName
        NodeReference nodeReference = new NodeRef(nameOrFdn)
        when:
        NormalizableNodeReference normalizedNodeReference = nscsCMReaderService.getNormalizedNodeReference(nodeReference)
        then:
        normalizedNodeReference != null
        and:
        normalizedNodeReference.getFdn() == nameOrFdn
        and:
        normalizedNodeReference.getName() == nodeName
        and:
        normalizedNodeReference.getTargetCategory() == "NFVO"
        and:
        normalizedNodeReference.getNeType() == "ECM"
        and:
        normalizedNodeReference.getOssModelIdentity() == null
        and:
        normalizedNodeReference.getNormalizableRef() != null
        and:
        normalizedNodeReference.getNormalizableRef().getFdn() == nameOrFdn
        and:
        normalizedNodeReference.getNormalizableRef().getName() == nodeName
        and:
        normalizedNodeReference.getNormalizedRef() != null
        and:
        normalizedNodeReference.getNormalizedRef().getFdn() == nameOrFdn
        and:
        normalizedNodeReference.getNormalizedRef().getName() == nodeName
    }

    def 'get normalizable node reference for VIM' () {
        given:
        createVirtualInfrastructureManager(nodeName)
        and:
        def nameOrFdn = "VirtualInfrastructureManager="+nodeName
        NodeReference nodeReference = new NodeRef(nameOrFdn)
        when:
        NormalizableNodeReference normalizableNodeReference = nscsCMReaderService.getNormalizableNodeReference(nodeReference)
        then:
        normalizableNodeReference != null
        and:
        normalizableNodeReference.getFdn() == nameOrFdn
        and:
        normalizableNodeReference.getName() == nodeName
        and:
        normalizableNodeReference.getTargetCategory() == "VIM"
        and:
        normalizableNodeReference.getNeType() == "ECEE"
        and:
        normalizableNodeReference.getOssModelIdentity() == null
        and:
        normalizableNodeReference.getNormalizableRef() != null
        and:
        normalizableNodeReference.getNormalizableRef().getFdn() == nameOrFdn
        and:
        normalizableNodeReference.getNormalizableRef().getName() == nodeName
        and:
        normalizableNodeReference.getNormalizedRef() != null
        and:
        normalizableNodeReference.getNormalizedRef().getFdn() == nameOrFdn
        and:
        normalizableNodeReference.getNormalizedRef().getName() == nodeName
    }

    def 'get normalized node reference for VIM' () {
        given:
        createVirtualInfrastructureManager(nodeName)
        and:
        def nameOrFdn = "VirtualInfrastructureManager="+nodeName
        NodeReference nodeReference = new NodeRef(nameOrFdn)
        when:
        NormalizableNodeReference normalizedNodeReference = nscsCMReaderService.getNormalizedNodeReference(nodeReference)
        then:
        normalizedNodeReference != null
        and:
        normalizedNodeReference.getFdn() == nameOrFdn
        and:
        normalizedNodeReference.getName() == nodeName
        and:
        normalizedNodeReference.getTargetCategory() == "VIM"
        and:
        normalizedNodeReference.getNeType() == "ECEE"
        and:
        normalizedNodeReference.getOssModelIdentity() == null
        and:
        normalizedNodeReference.getNormalizableRef() != null
        and:
        normalizedNodeReference.getNormalizableRef().getFdn() == nameOrFdn
        and:
        normalizedNodeReference.getNormalizableRef().getName() == nodeName
        and:
        normalizedNodeReference.getNormalizedRef() != null
        and:
        normalizedNodeReference.getNormalizedRef().getFdn() == nameOrFdn
        and:
        normalizedNodeReference.getNormalizedRef().getName() == nodeName
    }

    def 'get normalizable node reference for CIM' () {
        given:
        createCloudInfrastructureManager(nodeName)
        and:
        def nameOrFdn = "CloudInfrastructureManager="+nodeName
        NodeReference nodeReference = new NodeRef(nameOrFdn)
        when:
        NormalizableNodeReference normalizableNodeReference = nscsCMReaderService.getNormalizableNodeReference(nodeReference)
        then:
        normalizableNodeReference != null
        and:
        normalizableNodeReference.getFdn() == nameOrFdn
        and:
        normalizableNodeReference.getName() == nodeName
        and:
        normalizableNodeReference.getTargetCategory() == "CIM"
        and:
        normalizableNodeReference.getNeType() == "CCD"
        and:
        normalizableNodeReference.getOssModelIdentity() == null
        and:
        normalizableNodeReference.getNormalizableRef() != null
        and:
        normalizableNodeReference.getNormalizableRef().getFdn() == nameOrFdn
        and:
        normalizableNodeReference.getNormalizableRef().getName() == nodeName
        and:
        normalizableNodeReference.getNormalizedRef() != null
        and:
        normalizableNodeReference.getNormalizedRef().getFdn() == nameOrFdn
        and:
        normalizableNodeReference.getNormalizedRef().getName() == nodeName
    }

    def 'get normalized node reference for CIM' () {
        given:
        createCloudInfrastructureManager(nodeName)
        and:
        def nameOrFdn = "CloudInfrastructureManager="+nodeName
        NodeReference nodeReference = new NodeRef(nameOrFdn)
        when:
        NormalizableNodeReference normalizedNodeReference = nscsCMReaderService.getNormalizedNodeReference(nodeReference)
        then:
        normalizedNodeReference != null
        and:
        normalizedNodeReference.getFdn() == nameOrFdn
        and:
        normalizedNodeReference.getName() == nodeName
        and:
        normalizedNodeReference.getTargetCategory() == "CIM"
        and:
        normalizedNodeReference.getNeType() == "CCD"
        and:
        normalizedNodeReference.getOssModelIdentity() == null
        and:
        normalizedNodeReference.getNormalizableRef() != null
        and:
        normalizedNodeReference.getNormalizableRef().getFdn() == nameOrFdn
        and:
        normalizedNodeReference.getNormalizableRef().getName() == nodeName
        and:
        normalizedNodeReference.getNormalizedRef() != null
        and:
        normalizedNodeReference.getNormalizedRef().getFdn() == nameOrFdn
        and:
        normalizedNodeReference.getNormalizedRef().getName() == nodeName
    }

    def 'get normalizable node reference for MS' () {
        given:
        createManagementSystem(nodeName)
        and:
        def nameOrFdn = "ManagementSystem="+nodeName
        NodeReference nodeReference = new NodeRef(nameOrFdn)
        when:
        NormalizableNodeReference normalizableNodeReference = nscsCMReaderService.getNormalizableNodeReference(nodeReference)
        then:
        normalizableNodeReference != null
        and:
        normalizableNodeReference.getFdn() == nameOrFdn
        and:
        normalizableNodeReference.getName() == nodeName
        and:
        normalizableNodeReference.getTargetCategory() == "MS"
        and:
        normalizableNodeReference.getNeType() == "ENM"
        and:
        normalizableNodeReference.getOssModelIdentity() == null
        and:
        normalizableNodeReference.getNormalizableRef() != null
        and:
        normalizableNodeReference.getNormalizableRef().getFdn() == nameOrFdn
        and:
        normalizableNodeReference.getNormalizableRef().getName() == nodeName
        and:
        normalizableNodeReference.getNormalizedRef() != null
        and:
        normalizableNodeReference.getNormalizedRef().getFdn() == nameOrFdn
        and:
        normalizableNodeReference.getNormalizedRef().getName() == nodeName
    }

    def 'get normalized node reference for MS' () {
        given:
        createManagementSystem(nodeName)
        and:
        def nameOrFdn = "ManagementSystem="+nodeName
        NodeReference nodeReference = new NodeRef(nameOrFdn)
        when:
        NormalizableNodeReference normalizedNodeReference = nscsCMReaderService.getNormalizedNodeReference(nodeReference)
        then:
        normalizedNodeReference != null
        and:
        normalizedNodeReference.getFdn() == nameOrFdn
        and:
        normalizedNodeReference.getName() == nodeName
        and:
        normalizedNodeReference.getTargetCategory() == "MS"
        and:
        normalizedNodeReference.getNeType() == "ENM"
        and:
        normalizedNodeReference.getOssModelIdentity() == null
        and:
        normalizedNodeReference.getNormalizableRef() != null
        and:
        normalizedNodeReference.getNormalizableRef().getFdn() == nameOrFdn
        and:
        normalizedNodeReference.getNormalizableRef().getName() == nodeName
        and:
        normalizedNodeReference.getNormalizedRef() != null
        and:
        normalizedNodeReference.getNormalizedRef().getFdn() == nameOrFdn
        and:
        normalizedNodeReference.getNormalizedRef().getName() == nodeName
    }

    @Unroll
    def 'get normalizable node reference for #neType node name with NetworkElement and MeContext' () {
        given:
        createNodeWithMeContext(neType, null, nodeName, null)
        and:
        def nameOrFdn = nodeName
        NodeReference nodeReference = new NodeRef(nameOrFdn)
        when:
        NormalizableNodeReference normalizableNodeReference = nscsCMReaderService.getNormalizableNodeReference(nodeReference)
        then:
        normalizableNodeReference != null
        and:
        normalizableNodeReference.getFdn() == "MeContext="+nodeName
        and:
        normalizableNodeReference.getName() == nodeName
        and:
        normalizableNodeReference.getTargetCategory() == "NODE"
        and:
        normalizableNodeReference.getNeType() == neType
        and:
        normalizableNodeReference.getOssModelIdentity() == null
        and:
        normalizableNodeReference.getNormalizableRef() != null
        and:
        normalizableNodeReference.getNormalizableRef().getFdn() == "MeContext="+nodeName
        and:
        normalizableNodeReference.getNormalizableRef().getName() == nodeName
        and:
        normalizableNodeReference.getNormalizedRef() != null
        and:
        normalizableNodeReference.getNormalizedRef().getFdn() == "NetworkElement="+nodeName
        and:
        normalizableNodeReference.getNormalizedRef().getName() == nodeName
        where:
        neType << neTypesNotRequiringMeContext + neTypesRequiringMeContext
    }

    @Unroll
    def 'get normalized node reference for #neType node name with NetworkElement and MeContext' () {
        given:
        createNodeWithMeContext(neType, null, nodeName, null)
        and:
        def nameOrFdn = nodeName
        NodeReference nodeReference = new NodeRef(nameOrFdn)
        when:
        NormalizableNodeReference normalizedNodeReference = nscsCMReaderService.getNormalizedNodeReference(nodeReference)
        then:
        normalizedNodeReference != null
        and:
        normalizedNodeReference.getFdn() == "NetworkElement="+nodeName
        and:
        normalizedNodeReference.getName() == nodeName
        and:
        normalizedNodeReference.getTargetCategory() == "NODE"
        and:
        normalizedNodeReference.getNeType() == neType
        and:
        normalizedNodeReference.getOssModelIdentity() == null
        and:
        normalizedNodeReference.getNormalizableRef() != null
        and:
        normalizedNodeReference.getNormalizableRef().getFdn() == "MeContext="+nodeName
        and:
        normalizedNodeReference.getNormalizableRef().getName() == nodeName
        and:
        normalizedNodeReference.getNormalizedRef() != null
        and:
        normalizedNodeReference.getNormalizedRef().getFdn() == "NetworkElement="+nodeName
        and:
        normalizedNodeReference.getNormalizedRef().getName() == nodeName
        where:
        neType << neTypesNotRequiringMeContext + neTypesRequiringMeContext
    }

    @Unroll
    def 'get normalizable node reference for #neType node name not requiring MeContext with NetworkElement and ManagedElement' () {
        given:
        createNodeWithManagedElement(neType, null, nodeName, null, managedElementNamespace)
        and:
        def nameOrFdn = nodeName
        NodeReference nodeReference = new NodeRef(nameOrFdn)
        when:
        NormalizableNodeReference normalizableNodeReference = nscsCMReaderService.getNormalizableNodeReference(nodeReference)
        then:
        normalizableNodeReference != null
        and:
        normalizableNodeReference.getFdn() == "ManagedElement="+nodeName
        and:
        normalizableNodeReference.getName() == nodeName
        and:
        normalizableNodeReference.getTargetCategory() == "NODE"
        and:
        normalizableNodeReference.getNeType() == neType
        and:
        normalizableNodeReference.getOssModelIdentity() == null
        and:
        normalizableNodeReference.getNormalizableRef() != null
        and:
        normalizableNodeReference.getNormalizableRef().getFdn() == "ManagedElement="+nodeName
        and:
        normalizableNodeReference.getNormalizableRef().getName() == nodeName
        and:
        normalizableNodeReference.getNormalizedRef() != null
        and:
        normalizableNodeReference.getNormalizedRef().getFdn() == "NetworkElement="+nodeName
        and:
        normalizableNodeReference.getNormalizedRef().getName() == nodeName
        where:
        neType << neTypesNotRequiringMeContext
        managedElementNamespace << managedElementNamespacesNotRequiringMeContext
    }

    @Unroll
    def 'get normalized node reference for #neType node name not requiring MeContext with NetworkElement and ManagedElement' () {
        given:
        createNodeWithManagedElement(neType, null, nodeName, null, managedElementNamespace)
        and:
        def nameOrFdn = nodeName
        NodeReference nodeReference = new NodeRef(nameOrFdn)
        when:
        NormalizableNodeReference normalizedNodeReference = nscsCMReaderService.getNormalizedNodeReference(nodeReference)
        then:
        normalizedNodeReference != null
        and:
        normalizedNodeReference.getFdn() == "NetworkElement="+nodeName
        and:
        normalizedNodeReference.getName() == nodeName
        and:
        normalizedNodeReference.getTargetCategory() == "NODE"
        and:
        normalizedNodeReference.getNeType() == neType
        and:
        normalizedNodeReference.getOssModelIdentity() == null
        and:
        normalizedNodeReference.getNormalizableRef() != null
        and:
        normalizedNodeReference.getNormalizableRef().getFdn() == "ManagedElement="+nodeName
        and:
        normalizedNodeReference.getNormalizableRef().getName() == nodeName
        and:
        normalizedNodeReference.getNormalizedRef() != null
        and:
        normalizedNodeReference.getNormalizedRef().getFdn() == "NetworkElement="+nodeName
        and:
        normalizedNodeReference.getNormalizedRef().getName() == nodeName
        where:
        neType << neTypesNotRequiringMeContext
        managedElementNamespace << managedElementNamespacesNotRequiringMeContext
    }

    @Ignore
    @Unroll
    def 'get normalizable node reference for #neType node name requiring MeContext with NetworkElement and ManagedElement' () {
        given:
        createNodeWithManagedElement(neType, null, nodeName, null, managedElementNamespace)
        and:
        def nameOrFdn = nodeName
        NodeReference nodeReference = new NodeRef(nameOrFdn)
        when:
        NormalizableNodeReference normalizableNodeReference = nscsCMReaderService.getNormalizableNodeReference(nodeReference)
        then:
        normalizableNodeReference == null
        where:
        neType << neTypesRequiringMeContext
        managedElementNamespace << managedElementNamespacesRequiringMeContext
    }

    @Ignore
    @Unroll
    def 'get normalized node reference for #neType node name requiring MeContext with NetworkElement and ManagedElement' () {
        given:
        createNodeWithManagedElement(neType, null, nodeName, null, managedElementNamespace)
        and:
        def nameOrFdn = nodeName
        NodeReference nodeReference = new NodeRef(nameOrFdn)
        when:
        NormalizableNodeReference normalizedNodeReference = nscsCMReaderService.getNormalizedNodeReference(nodeReference)
        then:
        normalizedNodeReference != null
        and:
        normalizedNodeReference.getFdn() == "NetworkElement="+nodeName
        and:
        normalizedNodeReference.getName() == nodeName
        and:
        normalizedNodeReference.getTargetCategory() == "NODE"
        and:
        normalizedNodeReference.getNeType() == neType
        and:
        normalizedNodeReference.getOssModelIdentity() == null
        and:
        normalizedNodeReference.getNormalizableRef() == null
        and:
        normalizedNodeReference.getNormalizedRef() != null
        and:
        normalizedNodeReference.getNormalizedRef().getFdn() == "NetworkElement="+nodeName
        and:
        normalizedNodeReference.getNormalizedRef().getName() == nodeName
        where:
        neType << neTypesRequiringMeContext
        managedElementNamespace << managedElementNamespacesRequiringMeContext
    }

    @Unroll
    def 'get normalizable node reference for #neType NetworkElement with NetworkElement and MeContext' () {
        given:
        createNodeWithMeContext(neType, null, nodeName, null)
        and:
        def nameOrFdn = "NetworkElement="+nodeName
        NodeReference nodeReference = new NodeRef(nameOrFdn)
        when:
        NormalizableNodeReference normalizableNodeReference = nscsCMReaderService.getNormalizableNodeReference(nodeReference)
        then:
        normalizableNodeReference != null
        and:
        normalizableNodeReference.getFdn() == "MeContext="+nodeName
        and:
        normalizableNodeReference.getName() == nodeName
        and:
        normalizableNodeReference.getTargetCategory() == "NODE"
        and:
        normalizableNodeReference.getNeType() == neType
        and:
        normalizableNodeReference.getOssModelIdentity() == null
        and:
        normalizableNodeReference.getNormalizableRef() != null
        and:
        normalizableNodeReference.getNormalizableRef().getFdn() == "MeContext="+nodeName
        and:
        normalizableNodeReference.getNormalizableRef().getName() == nodeName
        and:
        normalizableNodeReference.getNormalizedRef() != null
        and:
        normalizableNodeReference.getNormalizedRef().getFdn() == "NetworkElement="+nodeName
        and:
        normalizableNodeReference.getNormalizedRef().getName() == nodeName
        where:
        neType << neTypesNotRequiringMeContext + neTypesRequiringMeContext
    }

    @Unroll
    def 'get normalized node reference for #neType NetworkElement with NetworkElement and MeContext' () {
        given:
        createNodeWithMeContext(neType, null, nodeName, null)
        and:
        def nameOrFdn = "NetworkElement="+nodeName
        NodeReference nodeReference = new NodeRef(nameOrFdn)
        when:
        NormalizableNodeReference normalizableNodeReference = nscsCMReaderService.getNormalizableNodeReference(nodeReference)
        then:
        normalizableNodeReference != null
        and:
        normalizableNodeReference.getFdn() == "MeContext="+nodeName
        and:
        normalizableNodeReference.getName() == nodeName
        and:
        normalizableNodeReference.getTargetCategory() == "NODE"
        and:
        normalizableNodeReference.getNeType() == neType
        and:
        normalizableNodeReference.getOssModelIdentity() == null
        and:
        normalizableNodeReference.getNormalizableRef() != null
        and:
        normalizableNodeReference.getNormalizableRef().getFdn() == "MeContext="+nodeName
        and:
        normalizableNodeReference.getNormalizableRef().getName() == nodeName
        and:
        normalizableNodeReference.getNormalizedRef() != null
        and:
        normalizableNodeReference.getNormalizedRef().getFdn() == "NetworkElement="+nodeName
        and:
        normalizableNodeReference.getNormalizedRef().getName() == nodeName
        where:
        neType << neTypesNotRequiringMeContext + neTypesRequiringMeContext
    }

    @Unroll
    def 'get normalizable node reference for #neType NetworkElement not requiring MeContext with NetworkElement and ManagedElement' () {
        given:
        createNodeWithManagedElement(neType, null, nodeName, null, managedElementNamespace)
        and:
        def nameOrFdn = "NetworkElement="+nodeName
        NodeReference nodeReference = new NodeRef(nameOrFdn)
        when:
        NormalizableNodeReference normalizableNodeReference = nscsCMReaderService.getNormalizableNodeReference(nodeReference)
        then:
        normalizableNodeReference != null
        and:
        normalizableNodeReference.getFdn() == "ManagedElement="+nodeName
        and:
        normalizableNodeReference.getName() == nodeName
        and:
        normalizableNodeReference.getTargetCategory() == "NODE"
        and:
        normalizableNodeReference.getNeType() == neType
        and:
        normalizableNodeReference.getOssModelIdentity() == null
        and:
        normalizableNodeReference.getNormalizableRef() != null
        and:
        normalizableNodeReference.getNormalizableRef().getFdn() == "ManagedElement="+nodeName
        and:
        normalizableNodeReference.getNormalizableRef().getName() == nodeName
        and:
        normalizableNodeReference.getNormalizedRef() != null
        and:
        normalizableNodeReference.getNormalizedRef().getFdn() == "NetworkElement="+nodeName
        and:
        normalizableNodeReference.getNormalizedRef().getName() == nodeName
        where:
        neType << neTypesNotRequiringMeContext
        managedElementNamespace << managedElementNamespacesNotRequiringMeContext
    }

    @Unroll
    def 'get normalized node reference for #neType NetworkElement not requiring MeContext with NetworkElement and ManagedElement' () {
        given:
        createNodeWithManagedElement(neType, null, nodeName, null, managedElementNamespace)
        and:
        def nameOrFdn = "NetworkElement="+nodeName
        NodeReference nodeReference = new NodeRef(nameOrFdn)
        when:
        NormalizableNodeReference normalizedNodeReference = nscsCMReaderService.getNormalizedNodeReference(nodeReference)
        then:
        normalizedNodeReference != null
        and:
        normalizedNodeReference.getFdn() == "NetworkElement="+nodeName
        and:
        normalizedNodeReference.getName() == nodeName
        and:
        normalizedNodeReference.getTargetCategory() == "NODE"
        and:
        normalizedNodeReference.getNeType() == neType
        and:
        normalizedNodeReference.getOssModelIdentity() == null
        and:
        normalizedNodeReference.getNormalizableRef() != null
        and:
        normalizedNodeReference.getNormalizableRef().getFdn() == "ManagedElement="+nodeName
        and:
        normalizedNodeReference.getNormalizableRef().getName() == nodeName
        and:
        normalizedNodeReference.getNormalizedRef() != null
        and:
        normalizedNodeReference.getNormalizedRef().getFdn() == "NetworkElement="+nodeName
        and:
        normalizedNodeReference.getNormalizedRef().getName() == nodeName
        where:
        neType << neTypesNotRequiringMeContext
        managedElementNamespace << managedElementNamespacesNotRequiringMeContext
    }

    @Ignore
    @Unroll
    def 'get normalizable node reference for #neType NetworkElement requiring MeContext with NetworkElement and ManagedElement' () {
        given:
        createNodeWithManagedElement(neType, null, nodeName, null, managedElementNamespace)
        and:
        def nameOrFdn = "NetworkElement="+nodeName
        NodeReference nodeReference = new NodeRef(nameOrFdn)
        when:
        NormalizableNodeReference normalizableNodeReference = nscsCMReaderService.getNormalizableNodeReference(nodeReference)
        then:
        normalizableNodeReference == null
        where:
        neType << neTypesRequiringMeContext
        managedElementNamespace << managedElementNamespacesRequiringMeContext
    }

    @Ignore
    @Unroll
    def 'get normalized node reference for #neType NetworkElement requiring MeContext with NetworkElement and ManagedElement' () {
        given:
        createNodeWithManagedElement(neType, null, nodeName, null, managedElementNamespace)
        and:
        def nameOrFdn = "NetworkElement="+nodeName
        NodeReference nodeReference = new NodeRef(nameOrFdn)
        when:
        NormalizableNodeReference normalizedNodeReference = nscsCMReaderService.getNormalizedNodeReference(nodeReference)
        then:
        normalizedNodeReference != null
        and:
        normalizedNodeReference.getFdn() == "NetworkElement="+nodeName
        and:
        normalizedNodeReference.getName() == nodeName
        and:
        normalizedNodeReference.getTargetCategory() == "NODE"
        and:
        normalizedNodeReference.getNeType() == neType
        and:
        normalizedNodeReference.getOssModelIdentity() == null
        and:
        normalizedNodeReference.getNormalizableRef() == null
        and:
        normalizedNodeReference.getNormalizedRef() != null
        and:
        normalizedNodeReference.getNormalizedRef().getFdn() == "NetworkElement="+nodeName
        and:
        normalizedNodeReference.getNormalizedRef().getName() == nodeName
        where:
        neType << neTypesRequiringMeContext
        managedElementNamespace << managedElementNamespacesRequiringMeContext
    }

    @Unroll
    def 'get normalizable node reference for #neType ManagedElement not requiring MeContext with NetworkElement and ManagedElement' () {
        given:
        createNodeWithManagedElement(neType, null, nodeName, null, managedElementNamespace)
        and:
        def nameOrFdn = "ManagedElement="+nodeName
        NodeReference nodeReference = new NodeRef(nameOrFdn)
        when:
        NormalizableNodeReference normalizableNodeReference = nscsCMReaderService.getNormalizableNodeReference(nodeReference)
        then:
        normalizableNodeReference != null
        and:
        normalizableNodeReference.getFdn() == "ManagedElement="+nodeName
        and:
        normalizableNodeReference.getName() == nodeName
        and:
        normalizableNodeReference.getTargetCategory() == "NODE"
        and:
        normalizableNodeReference.getNeType() == neType
        and:
        normalizableNodeReference.getOssModelIdentity() == null
        and:
        normalizableNodeReference.getNormalizableRef() != null
        and:
        normalizableNodeReference.getNormalizableRef().getFdn() == "ManagedElement="+nodeName
        and:
        normalizableNodeReference.getNormalizableRef().getName() == nodeName
        and:
        normalizableNodeReference.getNormalizedRef() != null
        and:
        normalizableNodeReference.getNormalizedRef().getFdn() == "NetworkElement="+nodeName
        and:
        normalizableNodeReference.getNormalizedRef().getName() == nodeName
        where:
        neType << neTypesNotRequiringMeContext
        managedElementNamespace << managedElementNamespacesNotRequiringMeContext
    }

    @Unroll
    def 'get normalized node reference for #neType ManagedElement not requiring MeContext with NetworkElement and ManagedElement' () {
        given:
        createNodeWithManagedElement(neType, null, nodeName, null, managedElementNamespace)
        and:
        def nameOrFdn = "ManagedElement="+nodeName
        NodeReference nodeReference = new NodeRef(nameOrFdn)
        when:
        NormalizableNodeReference normalizedNodeReference = nscsCMReaderService.getNormalizedNodeReference(nodeReference)
        then:
        normalizedNodeReference != null
        and:
        normalizedNodeReference.getFdn() == "NetworkElement="+nodeName
        and:
        normalizedNodeReference.getName() == nodeName
        and:
        normalizedNodeReference.getTargetCategory() == "NODE"
        and:
        normalizedNodeReference.getNeType() == neType
        and:
        normalizedNodeReference.getOssModelIdentity() == null
        and:
        normalizedNodeReference.getNormalizableRef() != null
        and:
        normalizedNodeReference.getNormalizableRef().getFdn() == "ManagedElement="+nodeName
        and:
        normalizedNodeReference.getNormalizableRef().getName() == nodeName
        and:
        normalizedNodeReference.getNormalizedRef() != null
        and:
        normalizedNodeReference.getNormalizedRef().getFdn() == "NetworkElement="+nodeName
        and:
        normalizedNodeReference.getNormalizedRef().getName() == nodeName
        where:
        neType << neTypesNotRequiringMeContext
        managedElementNamespace << managedElementNamespacesNotRequiringMeContext
    }

    @Ignore
    @Unroll
    def 'get normalizable node reference for #neType ManagedElement requiring MeContext with NetworkElement and ManagedElement' () {
        given:
        createNodeWithManagedElement(neType, null, nodeName, null, managedElementNamespace)
        and:
        def nameOrFdn = "ManagedElement="+nodeName
        NodeReference nodeReference = new NodeRef(nameOrFdn)
        when:
        NormalizableNodeReference normalizableNodeReference = nscsCMReaderService.getNormalizableNodeReference(nodeReference)
        then:
        normalizableNodeReference != null
        and:
        normalizableNodeReference.getFdn() == "ManagedElement="+nodeName
        and:
        normalizableNodeReference.getName() == nodeName
        and:
        normalizableNodeReference.getTargetCategory() == "NODE"
        and:
        normalizableNodeReference.getNeType() == neType
        and:
        normalizableNodeReference.getOssModelIdentity() == null
        and:
        normalizableNodeReference.getNormalizableRef() != null
        and:
        normalizableNodeReference.getNormalizableRef().getFdn() == "ManagedElement="+nodeName
        and:
        normalizableNodeReference.getNormalizableRef().getName() == nodeName
        and:
        normalizableNodeReference.getNormalizedRef() != null
        and:
        normalizableNodeReference.getNormalizedRef().getFdn() == "NetworkElement="+nodeName
        and:
        normalizableNodeReference.getNormalizedRef().getName() == nodeName
        where:
        neType << neTypesRequiringMeContext
        managedElementNamespace << managedElementNamespacesRequiringMeContext
    }

    @Ignore
    @Unroll
    def 'get normalized node reference for #neType ManagedElement requiring MeContext with NetworkElement and ManagedElement' () {
        given:
        createNodeWithManagedElement(neType, null, nodeName, null, managedElementNamespace)
        and:
        def nameOrFdn = "ManagedElement="+nodeName
        NodeReference nodeReference = new NodeRef(nameOrFdn)
        when:
        NormalizableNodeReference normalizedNodeReference = nscsCMReaderService.getNormalizedNodeReference(nodeReference)
        then:
        normalizedNodeReference != null
        and:
        normalizedNodeReference.getFdn() == "NetworkElement="+nodeName
        and:
        normalizedNodeReference.getName() == nodeName
        and:
        normalizedNodeReference.getTargetCategory() == "NODE"
        and:
        normalizedNodeReference.getNeType() == neType
        and:
        normalizedNodeReference.getOssModelIdentity() == null
        and:
        normalizedNodeReference.getNormalizableRef() != null
        and:
        normalizedNodeReference.getNormalizableRef().getFdn() == "ManagedElement="+nodeName
        and:
        normalizedNodeReference.getNormalizableRef().getName() == nodeName
        and:
        normalizedNodeReference.getNormalizedRef() != null
        and:
        normalizedNodeReference.getNormalizedRef().getFdn() == "NetworkElement="+nodeName
        and:
        normalizedNodeReference.getNormalizedRef().getName() == nodeName
        where:
        neType << neTypesRequiringMeContext
        managedElementNamespace << managedElementNamespacesRequiringMeContext
    }

    @Unroll
    def 'get normalizable node reference for #neType MeContext with NetworkElement and MeContext' () {
        given:
        createNodeWithMeContext(neType, null, nodeName, null)
        and:
        def nameOrFdn = "MeContext="+nodeName
        NodeReference nodeReference = new NodeRef(nameOrFdn)
        when:
        NormalizableNodeReference normalizableNodeReference = nscsCMReaderService.getNormalizableNodeReference(nodeReference)
        then:
        normalizableNodeReference != null
        and:
        normalizableNodeReference.getFdn() == "MeContext="+nodeName
        and:
        normalizableNodeReference.getName() == nodeName
        and:
        normalizableNodeReference.getTargetCategory() == "NODE"
        and:
        normalizableNodeReference.getNeType() == neType
        and:
        normalizableNodeReference.getOssModelIdentity() == null
        and:
        normalizableNodeReference.getNormalizableRef() != null
        and:
        normalizableNodeReference.getNormalizableRef().getFdn() == "MeContext="+nodeName
        and:
        normalizableNodeReference.getNormalizableRef().getName() == nodeName
        and:
        normalizableNodeReference.getNormalizedRef() != null
        and:
        normalizableNodeReference.getNormalizedRef().getFdn() == "NetworkElement="+nodeName
        and:
        normalizableNodeReference.getNormalizedRef().getName() == nodeName
        where:
        neType << neTypesNotRequiringMeContext + neTypesRequiringMeContext
    }

    @Unroll
    def 'get normalized node reference for #neType MeContext with NetworkElement and MeContext' () {
        given:
        createNodeWithMeContext(neType, null, nodeName, null)
        and:
        def nameOrFdn = "MeContext="+nodeName
        NodeReference nodeReference = new NodeRef(nameOrFdn)
        when:
        NormalizableNodeReference normalizedNodeReference = nscsCMReaderService.getNormalizedNodeReference(nodeReference)
        then:
        normalizedNodeReference != null
        and:
        normalizedNodeReference.getFdn() == "NetworkElement="+nodeName
        and:
        normalizedNodeReference.getName() == nodeName
        and:
        normalizedNodeReference.getTargetCategory() == "NODE"
        and:
        normalizedNodeReference.getNeType() == neType
        and:
        normalizedNodeReference.getOssModelIdentity() == null
        and:
        normalizedNodeReference.getNormalizableRef() != null
        and:
        normalizedNodeReference.getNormalizableRef().getFdn() == "MeContext="+nodeName
        and:
        normalizedNodeReference.getNormalizableRef().getName() == nodeName
        and:
        normalizedNodeReference.getNormalizedRef() != null
        and:
        normalizedNodeReference.getNormalizedRef().getFdn() == "NetworkElement="+nodeName
        and:
        normalizedNodeReference.getNormalizedRef().getName() == nodeName
        where:
        neType << neTypesNotRequiringMeContext + neTypesRequiringMeContext
    }

    @Unroll
    def 'get normalizable node reference for #neType MeContext with MeContext only' () {
        given:
        createMeContext(neType, nodeName)
        and:
        def nameOrFdn = "MeContext="+nodeName
        NodeReference nodeReference = new NodeRef(nameOrFdn)
        when:
        NormalizableNodeReference normalizableNodeReference = nscsCMReaderService.getNormalizableNodeReference(nodeReference)
        then:
        normalizableNodeReference != null
        and:
        normalizableNodeReference.getFdn() == "MeContext="+nodeName
        and:
        normalizableNodeReference.getName() == nodeName
        and:
        normalizableNodeReference.getTargetCategory() == "NODE"
        and:
        normalizableNodeReference.getNeType() == neType
        and:
        normalizableNodeReference.getOssModelIdentity() == null
        and:
        normalizableNodeReference.getNormalizableRef() != null
        and:
        normalizableNodeReference.getNormalizableRef().getFdn() == "MeContext="+nodeName
        and:
        normalizableNodeReference.getNormalizableRef().getName() == nodeName
        where:
        neType << neTypesNotRequiringMeContext + neTypesRequiringMeContext
    }

    @Unroll
    def 'get normalized node reference for #neType MeContext with MeContext only' () {
        given:
        createMeContext(neType, nodeName)
        and:
        def nameOrFdn = "MeContext="+nodeName
        NodeReference nodeReference = new NodeRef(nameOrFdn)
        when:
        NormalizableNodeReference normalizableNodeReference = nscsCMReaderService.getNormalizedNodeReference(nodeReference)
        then:
        normalizableNodeReference == null
        where:
        neType << neTypesNotRequiringMeContext + neTypesRequiringMeContext
    }

    @Unroll
    def 'get normalizable node reference normalizedRef for #neType MeContext with MeContext only' () {
        given:
        createMeContext(neType, nodeName)
        and:
        def nameOrFdn = "MeContext="+nodeName
        NodeReference nodeReference = new NodeRef(nameOrFdn)
        when:
        NormalizableNodeReference normalizableNodeReference = nscsCMReaderService.getNormalizableNodeReference(nodeReference)
        and:
        normalizableNodeReference.getNormalizedRef()
        then:
        thrown(IllegalStateException.class)
        where:
        neType << neTypesNotRequiringMeContext + neTypesRequiringMeContext
    }

    @Unroll
    def 'get normalizable node reference for #managedElementNamespace ManagedElement with ManagedElement only' () {
        given:
        createManagedElement(nodeName, managedElementNamespace)
        and:
        def nameOrFdn = "ManagedElement="+nodeName
        NodeReference nodeReference = new NodeRef(nameOrFdn)
        when:
        NormalizableNodeReference normalizableNodeReference = nscsCMReaderService.getNormalizableNodeReference(nodeReference)
        then:
        normalizableNodeReference != null
        and:
        normalizableNodeReference.getFdn() == "ManagedElement="+nodeName
        and:
        normalizableNodeReference.getName() == nodeName
        and:
        normalizableNodeReference.getTargetCategory() == "NODE"
        and:
        normalizableNodeReference.getNeType() == null
        and:
        normalizableNodeReference.getOssModelIdentity() == null
        and:
        normalizableNodeReference.getNormalizableRef() != null
        and:
        normalizableNodeReference.getNormalizableRef().getFdn() == "ManagedElement="+nodeName
        and:
        normalizableNodeReference.getNormalizableRef().getName() == nodeName
        where:
        managedElementNamespace << managedElementNamespacesNotRequiringMeContext + managedElementNamespacesRequiringMeContext
    }

    @Unroll
    def 'get normalized node reference for #managedElementNamespace ManagedElement with ManagedElement only' () {
        given:
        createManagedElement(nodeName, managedElementNamespace)
        and:
        def nameOrFdn = "ManagedElement="+nodeName
        NodeReference nodeReference = new NodeRef(nameOrFdn)
        when:
        NormalizableNodeReference normalizableNodeReference = nscsCMReaderService.getNormalizedNodeReference(nodeReference)
        then:
        normalizableNodeReference == null
        where:
        managedElementNamespace << managedElementNamespacesNotRequiringMeContext + managedElementNamespacesRequiringMeContext
    }

    @Unroll
    def 'get normalizable node reference normalizedRef for #managedElementNamespace ManagedElement with ManagedElement only' () {
        given:
        createManagedElement(nodeName, managedElementNamespace)
        and:
        def nameOrFdn = "ManagedElement="+nodeName
        NodeReference nodeReference = new NodeRef(nameOrFdn)
        when:
        NormalizableNodeReference normalizableNodeReference = nscsCMReaderService.getNormalizableNodeReference(nodeReference)
        and:
        normalizableNodeReference.getNormalizedRef()
        then:
        thrown(IllegalStateException.class)
        where:
        managedElementNamespace << managedElementNamespacesNotRequiringMeContext + managedElementNamespacesRequiringMeContext
    }

    @Unroll
    def 'get normalizable node reference for #neType NetworkElement with NetworkElement only' () {
        given:
        createNetworkElement(neType, null, nodeName, null)
        and:
        def nameOrFdn = "NetworkElement="+nodeName
        NodeReference nodeReference = new NodeRef(nameOrFdn)
        when:
        NormalizableNodeReference normalizableNodeReference = nscsCMReaderService.getNormalizableNodeReference(nodeReference)
        then:
        normalizableNodeReference == null
        where:
        neType << neTypesNotRequiringMeContext + neTypesRequiringMeContext
    }

    @Unroll
    def 'get normalized node reference for #neType NetworkElement with NetworkElement only' () {
        given:
        createNetworkElement(neType, null, nodeName, null)
        and:
        def nameOrFdn = "NetworkElement="+nodeName
        NodeReference nodeReference = new NodeRef(nameOrFdn)
        when:
        NormalizableNodeReference normalizedNodeReference = nscsCMReaderService.getNormalizedNodeReference(nodeReference)
        then:
        normalizedNodeReference != null
        and:
        normalizedNodeReference.getFdn() == "NetworkElement="+nodeName
        and:
        normalizedNodeReference.getName() == nodeName
        and:
        normalizedNodeReference.getTargetCategory() == "NODE"
        and:
        normalizedNodeReference.getNeType() == neType
        and:
        normalizedNodeReference.getOssModelIdentity() == null
        and:
        normalizedNodeReference.getNormalizableRef() == null
        and:
        normalizedNodeReference.getNormalizedRef() != null
        and:
        normalizedNodeReference.getNormalizedRef().getFdn() == "NetworkElement="+nodeName
        and:
        normalizedNodeReference.getNormalizedRef().getName() == nodeName
        where:
        neType << neTypesNotRequiringMeContext + neTypesRequiringMeContext
    }

    def 'get target po for existent MO'() {
        given:
        createNetworkElement("RadioNode", null, nodeName, null)
        when:
        def targetPo = nscsCMReaderService.getTargetPO("NetworkElement="+nodeName)
        then:
        targetPo != null
        and:
        targetPo.getCategory() == "NODE"
        and:
        targetPo.getType() == "RadioNode"
        and:
        targetPo.getModelIdentity() == null
        and:
        targetPo.getName() == nodeName
    }

    def 'get target po for not existent MO'() {
        given:
        createNetworkElement("RadioNode", null, nodeName, null)
        when:
        def targetPo = nscsCMReaderService.getTargetPO("MeContext="+nodeName)
        then:
        thrown(IllegalArgumentException)
    }

    def 'get target po for existent MO without target PO'() {
        given:
        createNetworkElementWithoutTarget("RadioNode", null, nodeName, null)
        when:
        def targetPo = nscsCMReaderService.getTargetPO("NetworkElement="+nodeName)
        then:
        targetPo == null
    }
}
