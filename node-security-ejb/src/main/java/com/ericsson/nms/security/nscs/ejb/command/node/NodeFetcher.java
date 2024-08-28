package com.ericsson.nms.security.nscs.ejb.command.node;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;

/**
 * Helper class responsible to try fetch a list of nodes.
 * @author emaynes.
 */
@ApplicationScoped
public class NodeFetcher {

    @Inject
    private Logger logger;

    @Inject
    private NscsCMReaderService readerService;

    /**
     * Fetches from the database all nodes according to the provided list
     * of references.
     * @param nodes a NodeReference list
     * @return Instance of NodeList, never returns null.
     */
    public NodeList fetchNodes(final List<NodeReference> nodes) {
        final List<Node> found = new LinkedList<>();
        final List<NodeReference> notFound = new LinkedList<>();

        if ( nodes != null ) {
            for (NodeReference node : nodes) {
                logger.debug("trying to find node {}", node);
                final NormalizableNodeReference normReference = readerService.getNormalizableNodeReference(node);
                logger.debug("    result is {}", normReference);
                if ( normReference == null ) {
                    notFound.add(node);
                } else {
                    found.add(new Node(normReference, node));
                }
            }
        }

        return new NodeList(found, notFound);
    }
}
