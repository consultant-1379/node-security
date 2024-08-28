package com.ericsson.nms.security.nscs.ejb.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.exception.InvalidNodeNameException;
import com.ericsson.nms.security.nscs.api.exception.NodeNotSynchronizedException;
import com.ericsson.nms.security.nscs.api.exception.wrapper.MultiErrorNodeException;
import com.ericsson.nms.security.nscs.api.exception.wrapper.NodeException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.ejb.command.context.CommandContextImpl;
import com.ericsson.nms.security.nscs.ejb.command.node.Node;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

/**
 * @author emaynes.
 */
@RunWith(MockitoJUnitRunner.class)
public class CommandContextImplTest {

    private static final String VALID1_NAME="ERBS01";
    private static final String VALID1_FDN="MeContext=" + VALID1_NAME;
    private static final String VALID1_NORM_FDN="NetworkElement=" + VALID1_NAME;

    private static final String VALID2_NAME="ERBS02";
    private static final String VALID2_FDN="MeContext=" + VALID2_NAME;
    private static final String VALID2_NORM_FDN="NetworkElement=" + VALID2_NAME;

    @Mock
    private Node validNode1;

    @Mock
    private Node validNode2;

    @Mock
    private NscsContextService nscsContextService;

    @Before
    public void setup(){
        doReturn(VALID1_FDN).when(validNode1).getFdn();
        doReturn(VALID1_NAME).when(validNode1).getName();
        doReturn(new NodeRef(VALID1_NORM_FDN)).when(validNode1).getNormalizedRef();
        doReturn(true).when(validNode1).hasNormalizedRef();
        doReturn(new NodeRef(VALID1_NORM_FDN)).when(validNode1).getSourceReference();
        doReturn(true).when(validNode1).isValid();

        doReturn(VALID2_FDN).when(validNode2).getFdn();
        doReturn(VALID2_NAME).when(validNode2).getName();
        doReturn(new NodeRef(VALID2_NORM_FDN)).when(validNode2).getNormalizedRef();
        doReturn(true).when(validNode2).hasNormalizedRef();
        doReturn(new NodeRef(VALID2_FDN)).when(validNode2).getSourceReference();
        doReturn(true).when(validNode2).isValid();
        //        doNothing().when(nscsContextService).setNumInvalidItemsContextValue(Mockito.anyObject());
        //        doNothing().when(nscsContextService).setNumValidItemsContextValue(Mockito.anyObject());
    }

    @Test
    public void getValidNodesTest(){
        CommandContextImpl context = new CommandContextImpl(Arrays.asList(validNode1, validNode2), new LinkedList<NodeReference>());

        List<NormalizableNodeReference> validNodes = context.getValidNodes();
        assertEquals(2, validNodes.size());

        context.setAsInvalidOrFailed(validNode1, new InvalidNodeNameException());

        validNodes = context.getValidNodes();
        assertEquals(1, validNodes.size());
    }

    @Test
    public void getAllNodesTest(){
        CommandContextImpl context = new CommandContextImpl(Arrays.asList(validNode1, validNode2), new LinkedList<NodeReference>());

        List<NodeReference> allNodes = context.getAllNodes();

        assertEquals(2, allNodes.size());
    }

    @Test
    public void getNodesNotFoundTest(){
        NodeReference notFound = new NodeRef("NetworkElement=ERBS1010");
        CommandContextImpl context = new CommandContextImpl(Arrays.asList(validNode1, validNode2), Arrays.asList(notFound));

        List<NodeReference> nodesNotFound = context.getNodesNotFound();

        assertEquals(1, nodesNotFound.size());
        assertEquals(notFound.getFdn(), nodesNotFound.iterator().next().getFdn());
    }

    @Test
    public void getInvalidNodesTest(){
        CommandContextImpl context = new CommandContextImpl(Arrays.asList(validNode1, validNode2), new LinkedList<NodeReference>());

        context.setAsInvalidOrFailed(validNode1, new InvalidNodeNameException());

        Set<NodeReference> invalidNodes = context.getInvalidNodes();

        assertEquals(1, invalidNodes.size());
    }

    @Test
    public void toNormalizedRefTest(){
        CommandContextImpl context = new CommandContextImpl(Arrays.asList(validNode1, validNode2), new LinkedList<NodeReference>());

        List<NodeReference> nodeReferences = context.toNormalizedRef(Arrays.asList(validNode1, validNode2));

        assertEquals(2, nodeReferences.size());

        assertEquals(validNode1.getNormalizedRef().getFdn(), nodeReferences.iterator().next().getFdn());
    }

    @Test
    public void getCommandReferenceTest(){
        CommandContextImpl context = new CommandContextImpl(Arrays.asList(validNode1, validNode2), new LinkedList<NodeReference>());

        NodeReference commandReference = context.getCommandReference(new NodeRef(VALID1_FDN));
        assertEquals(VALID1_NORM_FDN, commandReference.getFdn());

        commandReference = context.getCommandReference(new NodeRef(VALID1_NORM_FDN));
        assertEquals(VALID1_NORM_FDN, commandReference.getFdn());

        commandReference = context.getCommandReference(new NodeRef(VALID2_FDN));
        assertEquals(VALID2_FDN, commandReference.getFdn());

        commandReference = context.getCommandReference(new NodeRef(VALID2_NORM_FDN));
        assertEquals(VALID2_FDN, commandReference.getFdn());


        context.setAsInvalidOrFailed(validNode1, new InvalidNodeNameException());
        commandReference = context.getCommandReference(new NodeRef(VALID1_NORM_FDN));
        assertEquals(VALID1_NORM_FDN, commandReference.getFdn());

    }

    @Test
    public void hasInvalidWithoutInvalidTest() {
        CommandContextImpl context = new CommandContextImpl(Arrays.asList(validNode1, validNode2), new LinkedList<NodeReference>());

        Map<String, Integer> itemCounters = new HashMap<>();
        Boolean hasInvalidNodes = context.hasInvalidNode(itemCounters);
        assertFalse(hasInvalidNodes);
        assertTrue(Integer.valueOf(2).equals(itemCounters.get("VALID")));
        assertTrue(Integer.valueOf(0).equals(itemCounters.get("INVALID")));
    }

    @Test
    public void hasInvalidWithOneTypeOfExceptionTest() {
        CommandContextImpl context = new CommandContextImpl(Arrays.asList(validNode1, validNode2), new LinkedList<NodeReference>());

        context.setAsInvalidOrFailed(validNode1, new InvalidNodeNameException());
        context.setAsInvalidOrFailed(validNode2, new InvalidNodeNameException());

        Map<String, Integer> itemCounters = new HashMap<>();
        Boolean hasInvalidNodes = context.hasInvalidNode(itemCounters);
        assertTrue(hasInvalidNodes);
        assertTrue(Integer.valueOf(0).equals(itemCounters.get("VALID")));
        assertTrue(Integer.valueOf(2).equals(itemCounters.get("INVALID")));
    }

    @Test
    public void hasValidAndInvalidWithOneTypeOfExceptionTest() {
        CommandContextImpl context = new CommandContextImpl(Arrays.asList(validNode1, validNode2), new LinkedList<NodeReference>());

        context.setAsInvalidOrFailed(validNode1, new InvalidNodeNameException());

        Map<String, Integer> itemCounters = new HashMap<>();
        Boolean hasInvalidNodes = context.hasInvalidNode(itemCounters);
        assertTrue(hasInvalidNodes);
        assertTrue(Integer.valueOf(1).equals(itemCounters.get("VALID")));
        assertTrue(Integer.valueOf(1).equals(itemCounters.get("INVALID")));
    }

    @Test
    public void hasInvalidWithTwoTypeOfExceptionTest() {
        CommandContextImpl context = new CommandContextImpl(Arrays.asList(validNode1, validNode2), new LinkedList<NodeReference>());

        context.setAsInvalidOrFailed(validNode1, new InvalidNodeNameException());
        context.setAsInvalidOrFailed(validNode2, new NodeNotSynchronizedException());

        Map<String, Integer> itemCounters = new HashMap<>();
        Boolean hasInvalidNodes = context.hasInvalidNode(itemCounters);
        assertTrue(hasInvalidNodes);
        assertTrue(Integer.valueOf(0).equals(itemCounters.get("VALID")));
        assertTrue(Integer.valueOf(2).equals(itemCounters.get("INVALID")));
    }

    @Test
    public void throwIfHasInvalidWithoutInvalidTest() {
        CommandContextImpl context = new CommandContextImpl(Arrays.asList(validNode1, validNode2), new LinkedList<NodeReference>());

        context.throwIfHasInvalidNode();
    }

    @Test(expected = NodeException.class)
    public void throwIfHasInvalidWithOneTypeOfExceptionTest() {
        CommandContextImpl context = new CommandContextImpl(Arrays.asList(validNode1, validNode2), new LinkedList<NodeReference>());

        context.setAsInvalidOrFailed(validNode1, new InvalidNodeNameException());
        context.setAsInvalidOrFailed(validNode2, new InvalidNodeNameException());

        context.throwIfHasInvalidNode();
    }

    @Test(expected = NodeException.class)
    public void throwIfHasValidAndInvalidWithOneTypeOfExceptionTest() {
        CommandContextImpl context = new CommandContextImpl(Arrays.asList(validNode1, validNode2), new LinkedList<NodeReference>());

        context.setAsInvalidOrFailed(validNode1, new InvalidNodeNameException());

        context.throwIfHasInvalidNode();
    }

    @Test(expected = MultiErrorNodeException.class)
    public void throwIfHasInvalidWithTwoTypeOfExceptionTest() {
        CommandContextImpl context = new CommandContextImpl(Arrays.asList(validNode1, validNode2), new LinkedList<NodeReference>());

        context.setAsInvalidOrFailed(validNode1, new InvalidNodeNameException());
        context.setAsInvalidOrFailed(validNode2, new NodeNotSynchronizedException());

        context.throwIfHasInvalidNode();
    }


}
