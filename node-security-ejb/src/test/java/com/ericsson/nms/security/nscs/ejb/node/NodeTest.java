package com.ericsson.nms.security.nscs.ejb.node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.MockUtils;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.ejb.command.node.Node;

/**
 * Unit test for {@link com.ericsson.nms.security.nscs.ejb.command.node.Node} class
 *
 * @author emaynes.
 */
@RunWith(MockitoJUnitRunner.class)
public class NodeTest {

    private static final String ME_NAME = "ERBS1";
    private static final String ME_FDN = "MeContext=" + ME_NAME;
    private static final String NE_NAME = "ERBS0001";
    private static final String NE_FDN = "NetworkElement=" + NE_NAME;
    private static final String NE_OSS_MODEL_IDENTITY = "397-5538-488";

    private static final String SOURCE_NAME = "cli_name";

    private NodeReference sourceRef = new NodeRef(SOURCE_NAME);
    private NodeReference normRef = new NodeRef(NE_FDN);

    private NormalizableNodeReference normalizableNodeReference = MockUtils.createNormNodeRefWithMeContext(ME_NAME, "ERBS", NE_OSS_MODEL_IDENTITY,
            normRef, null);

    @Before
    public void setup() {
    }

    @Test
    public void validNodeTest() {
        final Node node = new Node(normalizableNodeReference, sourceRef);

        assertEquals(ME_FDN, node.getFdn());
        assertEquals(ME_NAME, node.getName());
        assertTrue("Node should be valid", node.isValid());
        assertTrue("Should have a normalized version", node.hasNormalizedRef());
        assertTrue("Should have a normalizable version", node.hasNormalizableRef());
        assertEquals(NE_FDN, node.getNormalizedRef().getFdn());
        assertEquals(NE_NAME, node.getNormalizedRef().getName());
        assertEquals(SOURCE_NAME, node.getSourceReference().getName());
        assertEquals("ERBS", node.getNeType());
        assertEquals(NE_OSS_MODEL_IDENTITY, node.getOssModelIdentity());
        assertNull(node.getTargetCategory());
    }

    @Test
    public void invalidNodeTest() {
        final Node node = new Node(sourceRef);
        assertEquals(SOURCE_NAME, node.getName());
        assertEquals(SOURCE_NAME, node.getNormalizedRef().getName());
        assertFalse("Should be a invalid node", node.isValid());
        assertTrue("Should have a normalized version", node.hasNormalizedRef());
        assertFalse("Should not have a normalizable version", node.hasNormalizableRef());
        assertNull(node.getNeType());
        assertNull(node.getOssModelIdentity());
        assertNull(node.getTargetCategory());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void invalidNodeNormRefErrorTest() {
        final Node node = new Node(null);

        node.getNormalizedRef();
    }

    @Test
    public void invalidNodeNormRefTest() {
        final Node node = new Node(sourceRef);
        assertEquals(node.getNormalizedRef(), sourceRef);
    }

    @Test
    public void validToInvalidNodeTest() {
        final Node node = new Node(normalizableNodeReference, sourceRef);
        node.setValid(false);
        assertFalse("Should be a invalid node", node.isValid());
        assertEquals(SOURCE_NAME, node.getName());
    }
}
