package com.ericsson.nms.security.nscs.api.model;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;

/**
 * Unit test for NodeRef class
 *
 * @author emaynes.
 */
public class NodeRefTest {

    private static final String NODE_NAME = "nodeName";
    private static final String VIRTUAL_NODE_NAME = "virtualNodeName";
    private static final String CLOUD_NODE_NAME = "cloudNodeName";

    private static final String NETWORK_ELEMENT_FDN = "NetworkElement=" + NODE_NAME;
    private static final String VNFM_FDN = "VirtualNetworkFunctionManager=" + VIRTUAL_NODE_NAME;
    private static final String VIM_FDN = "VirtualInfrastructureManager=" + VIRTUAL_NODE_NAME;
    private static final String CIM_FDN = "CloudInfrastructureManager=" + CLOUD_NODE_NAME;

    private static final String SUB_NETWORK_FDN = "SubNetwork=Athlone";
    private static final String MULTI_SUB_NETWORK_FDN = "SubNetwork=Italy,SubNetwork=Genoa";
    private static final String MULTI_SUB_NETWORK_FDN_WITH_SPACES = "SubNetwork   =Italy,SubNetwork = Genoa";

    private static final String ME_CONTEXT_FDN = "MeContext=" + NODE_NAME;
    private static final String ME_CONTEXT_AND_SUBNET_FDN = SUB_NETWORK_FDN + "," + ME_CONTEXT_FDN;
    private static final String ME_CONTEXT_AND_MULTI_SUBNET_FDN = MULTI_SUB_NETWORK_FDN + "," + ME_CONTEXT_FDN;
    private static final String ME_CONTEXT_AND_MULTI_SUBNET_FDN_WITH_SPACES = MULTI_SUB_NETWORK_FDN_WITH_SPACES + "," + ME_CONTEXT_FDN;

    private static final String MANAGED_ELEMENT_FDN = "ManagedElement=" + NODE_NAME;
    private static final String MANAGED_ELEMENT_AND_SUBNET_FDN = SUB_NETWORK_FDN + "," + MANAGED_ELEMENT_FDN;
    private static final String MANAGED_ELEMENT_AND_MULTI_SUBNET_FDN = MULTI_SUB_NETWORK_FDN + "," + MANAGED_ELEMENT_FDN;

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullName() {
        final NodeRef nodeRef = new NodeRef(null);
        nodeRef.getName();
    }

    @Test(expected = InvalidArgumentValueException.class)
    public void testConstructorWithEmptyNameOrFdn() {
        final NodeRef nodeRef = new NodeRef("");
        nodeRef.getName();
    }

    @Test
    public void testContructorWithValidName() {
        final NodeRef nodeRef = new NodeRef(NODE_NAME);

        assertEquals(NODE_NAME, nodeRef.getName());
        assertEquals(NETWORK_ELEMENT_FDN, nodeRef.getFdn());
    }

    @Test
    public void testContructorWithValidNEFdn() {
        final NodeRef nodeRef = new NodeRef(NETWORK_ELEMENT_FDN);

        assertEquals(NODE_NAME, nodeRef.getName());
        assertEquals(NETWORK_ELEMENT_FDN, nodeRef.getFdn());
    }

    @Test
    public void testContructorWithValidNEChildFdn() {
        final NodeRef nodeRef = new NodeRef(NETWORK_ELEMENT_FDN + ",SecurityFunction=1,NetworkElementSecurity=1");

        assertEquals(NODE_NAME, nodeRef.getName());
        assertEquals(NETWORK_ELEMENT_FDN, nodeRef.getFdn());
    }

    @Test
    public void testContructorWithValidVNFMFdn() {
        final NodeRef nodeRef = new NodeRef(VNFM_FDN);

        assertEquals(VIRTUAL_NODE_NAME, nodeRef.getName());
        assertEquals(VNFM_FDN, nodeRef.getFdn());
    }

    @Test
    public void testContructorWithValidVNFMChildFdn() {
        final NodeRef nodeRef = new NodeRef(VNFM_FDN + ",SecurityFunction=1,NetworkElementSecurity=1");

        assertEquals(VIRTUAL_NODE_NAME, nodeRef.getName());
        assertEquals(VNFM_FDN, nodeRef.getFdn());
    }

    @Test
    public void testContructorWithValidVIMFdn() {
        final NodeRef nodeRef = new NodeRef(VIM_FDN);

        assertEquals(VIRTUAL_NODE_NAME, nodeRef.getName());
        assertEquals(VIM_FDN, nodeRef.getFdn());
    }


    @Test
    public void testContructorWithValidVIMChildFdn() {
        final NodeRef nodeRef = new NodeRef(VIM_FDN + ",SecurityFunction=1,NetworkElementSecurity=1");

        assertEquals(VIRTUAL_NODE_NAME, nodeRef.getName());
        assertEquals(VIM_FDN, nodeRef.getFdn());
    }


    @Test
    public void testContructorWithValidCIMFdn() {
        final NodeRef nodeRef = new NodeRef(CIM_FDN);

        assertEquals(CLOUD_NODE_NAME, nodeRef.getName());
        assertEquals(CIM_FDN, nodeRef.getFdn());
    }


    @Test
    public void testContructorWithValidCIMChildFdn() {
        final NodeRef nodeRef = new NodeRef(CIM_FDN + ",SecurityFunction=1,NetworkElementSecurity=1");

        assertEquals(CLOUD_NODE_NAME, nodeRef.getName());
        assertEquals(CIM_FDN, nodeRef.getFdn());
    }

    @Test
    public void getFdn_withValidNetworkFunctionVirtualizationOrchestratorChildFdnInConstructor_returnsCorrectParentFdn() {
        final NodeRef nodeRef = new NodeRef("NetworkFunctionVirtualizationOrchestrator=myNfvo,SecurityFunction=1,NetworkElementSecurity=1");

        assertEquals("myNfvo", nodeRef.getName());
        assertEquals("NetworkFunctionVirtualizationOrchestrator=myNfvo", nodeRef.getFdn());
    }

    @Test
    public void getFdn_withValidManagementSystemLowerCaseChildFdnInConstructor_returnsCorrectParentFdn() {
        final NodeRef nodeRef = new NodeRef("managementSystem=myMs,SecurityFunction=1,NetworkElementSecurity=1");

        assertEquals("myMs", nodeRef.getName());
        assertEquals("ManagementSystem=myMs", nodeRef.getFdn());
    }

    @Test
    public void getFdn_withValidManagementSystemChildFdnInConstructor_returnsCorrectParentFdn() {
        final NodeRef nodeRef = new NodeRef("ManagementSystem=myMs,SecurityFunction=1,NetworkElementSecurity=1");

        assertEquals("myMs", nodeRef.getName());
        assertEquals("ManagementSystem=myMs", nodeRef.getFdn());
    }

    @Test
    public void getFdn_withValidNetworkFunctionVirtualizationOrchestratorLowerCaseChildFdnInConstructor_returnsCorrectParentFdn() {
        final NodeRef nodeRef = new NodeRef("networkfunctionvirtualizationorchestrator=myNfvo,SecurityFunction=1,NetworkElementSecurity=1");

        assertEquals("myNfvo", nodeRef.getName());
        assertEquals("NetworkFunctionVirtualizationOrchestrator=myNfvo", nodeRef.getFdn());
    }

    @Test
    public void testContructorWithValidMeContextFdn() {
        final NodeRef nodeRef = new NodeRef(ME_CONTEXT_FDN);

        assertEquals(NODE_NAME, nodeRef.getName());
        assertEquals(ME_CONTEXT_FDN, nodeRef.getFdn());
    }

    @Test
    public void testContructorWithValidMeContextAndManagedElementFdn() {
        final NodeRef nodeRef = new NodeRef(ME_CONTEXT_FDN + "," + MANAGED_ELEMENT_FDN);

        assertEquals(NODE_NAME, nodeRef.getName());
        assertEquals(ME_CONTEXT_FDN, nodeRef.getFdn());
    }

    @Test
    public void testContructorWithValidSubnetworkAndMeContextFdn() {
        final NodeRef nodeRef = new NodeRef(ME_CONTEXT_AND_SUBNET_FDN);

        assertEquals(NODE_NAME, nodeRef.getName());
        assertEquals(ME_CONTEXT_AND_SUBNET_FDN, nodeRef.getFdn());
    }

    @Test
    public void testContructorWithValidSubnetworkAndMeContextAndManagedElementFdn() {
        final NodeRef nodeRef = new NodeRef(ME_CONTEXT_AND_SUBNET_FDN + "," + MANAGED_ELEMENT_FDN);

        assertEquals(NODE_NAME, nodeRef.getName());
        assertEquals(ME_CONTEXT_AND_SUBNET_FDN, nodeRef.getFdn());
    }

    @Test
    public void testContructorWithValidMultiSubnetworkAndMeContextFdn() {
        final NodeRef nodeRef = new NodeRef(ME_CONTEXT_AND_MULTI_SUBNET_FDN);

        assertEquals(NODE_NAME, nodeRef.getName());
        assertEquals(ME_CONTEXT_AND_MULTI_SUBNET_FDN, nodeRef.getFdn());
    }

    @Test
    public void testContructorWithValidMultiSubnetworkAndMeContextAndManagedElementFdn() {
        final NodeRef nodeRef = new NodeRef(ME_CONTEXT_AND_MULTI_SUBNET_FDN + "," + MANAGED_ELEMENT_FDN);

        assertEquals(NODE_NAME, nodeRef.getName());
        assertEquals(ME_CONTEXT_AND_MULTI_SUBNET_FDN, nodeRef.getFdn());
    }

    @Test
    public void testContructorWithValidMultiSubnetworkAndMeContextFdnWithSpaces() {
        final NodeRef nodeRef = new NodeRef(ME_CONTEXT_AND_MULTI_SUBNET_FDN_WITH_SPACES);

        assertEquals(NODE_NAME, nodeRef.getName());
        assertEquals(ME_CONTEXT_AND_MULTI_SUBNET_FDN, nodeRef.getFdn());
    }

    @Test
    public void testContructorWithValidMultiSubnetworkAndMeContextAndManagedElementFdnWithSpaces() {
        final NodeRef nodeRef = new NodeRef(ME_CONTEXT_AND_MULTI_SUBNET_FDN_WITH_SPACES + "," + MANAGED_ELEMENT_FDN);

        assertEquals(NODE_NAME, nodeRef.getName());
        assertEquals(ME_CONTEXT_AND_MULTI_SUBNET_FDN, nodeRef.getFdn());
    }

    @Test
    public void testContructorWithValidMultiSubnetworkAndMeContextAndManagedElementAndChildFdnWithSpaces() {
        final NodeRef nodeRef = new NodeRef(ME_CONTEXT_AND_MULTI_SUBNET_FDN_WITH_SPACES + "," + MANAGED_ELEMENT_FDN + ",SystemFunctions=1");

        assertEquals(NODE_NAME, nodeRef.getName());
        assertEquals(ME_CONTEXT_AND_MULTI_SUBNET_FDN, nodeRef.getFdn());
    }

    @Test
    public void testContructorWithValidManagedElementFdn() {
        final NodeRef nodeRef = new NodeRef(MANAGED_ELEMENT_FDN);

        assertEquals(NODE_NAME, nodeRef.getName());
        assertEquals(MANAGED_ELEMENT_FDN, nodeRef.getFdn());
    }

    @Test
    public void testContructorWithValidSubnetworkAndManagedElementFdn() {
        final NodeRef nodeRef = new NodeRef(MANAGED_ELEMENT_AND_SUBNET_FDN);

        assertEquals(NODE_NAME, nodeRef.getName());
        assertEquals(MANAGED_ELEMENT_AND_SUBNET_FDN, nodeRef.getFdn());
    }

    @Test
    public void testContructorWithValidMultiSubnetworkAndManagedElementFdn() {
        final NodeRef nodeRef = new NodeRef(MANAGED_ELEMENT_AND_MULTI_SUBNET_FDN);

        assertEquals(NODE_NAME, nodeRef.getName());
        assertEquals(MANAGED_ELEMENT_AND_MULTI_SUBNET_FDN, nodeRef.getFdn());
    }

    @Test
    public void testContructorWithValidMultiSubnetworkAndManagedElementAndChildFdn() {
        final NodeRef nodeRef = new NodeRef(MANAGED_ELEMENT_AND_MULTI_SUBNET_FDN + ",SystemFunctions=1");

        assertEquals(NODE_NAME, nodeRef.getName());
        assertEquals(MANAGED_ELEMENT_AND_MULTI_SUBNET_FDN, nodeRef.getFdn());
    }

    @Test(expected = InvalidArgumentValueException.class)
    public void testContructorWithInvalidNEFdn() {
        final NodeRef nodeRef = new NodeRef("Foo" + NETWORK_ELEMENT_FDN);
        nodeRef.getName();
    }

    @Test(expected = InvalidArgumentValueException.class)
    public void testContructorWithInvalidMeContextFdn() {
        final NodeRef nodeRef = new NodeRef("Foo" + ME_CONTEXT_FDN);
        nodeRef.getName();
    }

    @Test(expected = InvalidArgumentValueException.class)
    public void testContructorWithInvalidManagedElementContextFdn() {
        final NodeRef nodeRef = new NodeRef("Foo" + MANAGED_ELEMENT_FDN);
        nodeRef.getName();
    }

    @Test
    public void testStringCollectionToRef() {
        final List<NodeReference> expected = new ArrayList<>(2);
        expected.add(new NodeRef(NETWORK_ELEMENT_FDN));
        expected.add(new NodeRef(ME_CONTEXT_FDN));

        final List<NodeReference> from = NodeRef.from(Arrays.asList(NODE_NAME, ME_CONTEXT_FDN));

        assertEquals(expected, from);
    }

    @Test
    public void testStringArrayToRef() {
        final List<NodeReference> expected = new ArrayList<>(2);
        expected.add(new NodeRef(NETWORK_ELEMENT_FDN));
        expected.add(new NodeRef(ME_CONTEXT_FDN));

        final List<NodeReference> from = NodeRef.from(NODE_NAME, ME_CONTEXT_FDN);

        assertEquals(expected, from);
    }

    @Test
    public void testNodeRefToNames() {
        final List<NodeReference> refs = new ArrayList<>(2);
        refs.add(new NodeRef(NETWORK_ELEMENT_FDN));
        refs.add(new NodeRef(ME_CONTEXT_FDN));

        final List<String> names = NodeRef.toNames(refs);

        assertEquals(Arrays.asList(NODE_NAME, NODE_NAME), names);
    }

    @Test
    public void testNodeRefToFdns() {
        final List<NodeReference> refs = new ArrayList<>(2);
        refs.add(new NodeRef(ME_CONTEXT_FDN));
        refs.add(new NodeRef(NETWORK_ELEMENT_FDN));

        final List<String> fdns = NodeRef.toFdns(refs);

        assertEquals(Arrays.asList(ME_CONTEXT_FDN, NETWORK_ELEMENT_FDN), fdns);
    }
}
