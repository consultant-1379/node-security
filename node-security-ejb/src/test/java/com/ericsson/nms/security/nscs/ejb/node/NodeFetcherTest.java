package com.ericsson.nms.security.nscs.ejb.node;

import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.ejb.command.node.NodeFetcher;
import com.ericsson.nms.security.nscs.ejb.command.node.NodeList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;

/**
 * Unit test for {@link com.ericsson.nms.security.nscs.ejb.command.node.NodeFetcher}
 * @author emaynes.
 */
@RunWith(MockitoJUnitRunner.class)
public class NodeFetcherTest {

    @Spy
    Logger logger = LoggerFactory.getLogger(NodeFetcher.class);

    @Mock
    private NscsCMReaderService readerService;

    @Mock
    private NormalizableNodeReference normNodeRef;

    @InjectMocks
    NodeFetcher nodeFetcher;

    @Test
    public void fetchNodeTest() {
        final NodeReference validRef = new NodeRef("valid");
        final NodeReference invalidRef = new NodeRef("invalid");
        doReturn(validRef.getName()).when(normNodeRef).getName();
        doReturn(validRef.getFdn()).when(normNodeRef).getFdn();
        doReturn(normNodeRef).when(readerService).getNormalizableNodeReference(validRef);
        doReturn(null).when(readerService).getNormalizableNodeReference(invalidRef);

        NodeList nodeList = nodeFetcher.fetchNodes(Arrays.asList(validRef, invalidRef));

        assertNotNull("NodeFetcher should never return null", nodeList);
        assertEquals("There should be one valid node", 1, nodeList.extractAllRemainingNodes().size());
        assertEquals("There should be one invalid node", 1, nodeList.getInvalidNodes().size());
    }

    @Test
    public void neverReturnNullTest() {
        NodeList nodeList = nodeFetcher.fetchNodes(null);

        assertNotNull("NodeFetcher should never return null", nodeList);
    }
}
