package com.ericsson.nms.security.nscs.ejb.node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.ejb.command.node.Node;
import com.ericsson.nms.security.nscs.ejb.command.node.NodeList;

/**
 * @author emaynes.
 */
@RunWith(MockitoJUnitRunner.class)
public class NodeListTest {

    private static final String ME_NAME1 = "ERBS1";
    private static final String ME_NAME2 = "ERBS2";
    private static final String SOURCE_NAME = "sourcefdn";

    private static final String INVALID_NE_FDN = "NetworkElement=NOM001";

    private NodeReference sourceRef = new NodeRef(SOURCE_NAME);

    private NodeReference invalidRef = new NodeRef(INVALID_NE_FDN);

    private NormalizableNodeReference norNodeRef1;

    private NormalizableNodeReference norNodeRefNonCPP;

    @Before
    public void setup() {
        norNodeRef1 = com.ericsson.nms.security.nscs.MockUtils.createNormalizableNodeRef(ME_NAME1);
        norNodeRefNonCPP = com.ericsson.nms.security.nscs.MockUtils.createNormalizableNodeRef(ME_NAME2);
    }

    @Test
    public void listWithValidAndInvalidNodesTest() {
        final NodeList nodeList = new NodeList(Arrays.asList(new Node(norNodeRef1, sourceRef), new Node(norNodeRefNonCPP, sourceRef)),
                Arrays.asList(invalidRef));

        assertEquals("Invalid nodes should be 1", 1, nodeList.getInvalidNodes().size());
        assertEquals(INVALID_NE_FDN, nodeList.getInvalidNodes().iterator().next().getFdn());
        assertTrue("remaining should be true", nodeList.hasRemaining());

    }

    @Test
    public void extractAllRemainingNodesTest() {
        final NodeList nodeList = new NodeList(Arrays.asList(new Node(norNodeRef1, sourceRef), new Node(norNodeRefNonCPP, sourceRef)),
                Arrays.asList(invalidRef));

        assertEquals("Invalid nodes should be 1", 1, nodeList.getInvalidNodes().size());
        assertTrue("remaining should be true", nodeList.hasRemaining());
        final List<Node> remainingNodes = nodeList.extractAllRemainingNodes();
        assertNotNull("remainingNodes should not be null", remainingNodes);
        assertEquals("There should have 2 nodes in it", 2, remainingNodes.size());

        assertFalse("There should be no one node left", nodeList.hasRemaining());
    }
}
