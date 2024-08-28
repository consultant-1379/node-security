package com.ericsson.nms.security.nscs.ejb.command.node;

import java.util.LinkedList;
import java.util.List;

import com.ericsson.nms.security.nscs.api.model.NodeReference;

/**
 * Returned by {@link NodeFetcher} it contains a list of nodes which were found and a list of nodes which were not found.
 * <p>
 * Also allows you to extract a sub-list of nodes based on platform and node type
 * </p>
 * 
 * @author emaynes.
 */
public class NodeList {

    private List<Node> validNodes = new LinkedList<>();
    private List<NodeReference> invalidNodes = new LinkedList<>();

    /**
     * Crates a new instance of NodeList
     * 
     * @param validNodes
     *            list of nodes found
     * @param invalidNodes
     *            list of nodes not found
     */
    public NodeList(final List<Node> validNodes, final List<NodeReference> invalidNodes) {
        this.validNodes = new LinkedList<>(validNodes);
        this.invalidNodes = new LinkedList<>(invalidNodes);
    }

    /**
     * Gets the list of nodes which were not found
     * 
     * @return list of invalid node references
     */
    public List<NodeReference> getInvalidNodes() {
        return new LinkedList<>(this.invalidNodes);
    }

    /**
     * Gets all available nodes in the list of valid nodes.
     * <p>
     * Be aware the the nodes are actually removed from the list.
     * </p>
     * 
     * @return list of Nodes
     */
    public List<Node> extractAllRemainingNodes() {
        final LinkedList<Node> nodes = new LinkedList<>(this.validNodes);
        this.validNodes.clear();
        return nodes;
    }

    public boolean hasRemaining() {
        return !this.validNodes.isEmpty();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("NodeList{");
        sb.append("validNodes=").append(validNodes);
        sb.append(", invalidNodes=").append(invalidNodes);
        sb.append('}');
        return sb.toString();
    }
}
