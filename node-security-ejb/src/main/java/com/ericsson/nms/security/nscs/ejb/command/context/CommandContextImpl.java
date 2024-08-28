package com.ericsson.nms.security.nscs.ejb.command.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.wrapper.MultiErrorNodeException;
import com.ericsson.nms.security.nscs.api.exception.wrapper.NodeException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.ejb.command.context.exception.ExistingInvalidNodesException;
import com.ericsson.nms.security.nscs.ejb.command.node.Node;
import com.ericsson.nms.security.nscs.handler.CommandContext;

/**
 * Implementation of {@link com.ericsson.nms.security.nscs.handler.CommandContext}
 * 
 * @author emaynes.
 * @see com.ericsson.nms.security.nscs.handler.CommandContext
 */
public class CommandContextImpl implements CommandContext {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected List<Node> validNodes = new LinkedList<>();
    protected List<NodeReference> nodesNotFound = new LinkedList<>();
    protected Set<NodeReference> invalidNodes = new HashSet<>();
    protected Map<NscsServiceException, Set<NodeReference>> errors = new HashMap<>();

    /**
     * Creates a new CommandContextImpl with the provided list of validNodes and nodes not found references
     * 
     * @param validNodes
     *            list of Node(s)
     * @param nodesNotFound
     *            list of NodeReferences which could not be found in the data store
     */
    public CommandContextImpl(final List<Node> validNodes, final List<NodeReference> nodesNotFound) {
        this.validNodes = new ArrayList<>(validNodes);
        this.nodesNotFound.addAll(nodesNotFound);
    }

    public CommandContextImpl() {
    }

    public void setValidNodes(final List<Node> validNodes) {
        this.validNodes = validNodes;
    }

    public void setNodesNotFound(final List<NodeReference> nodesNotFound) {
        this.nodesNotFound.addAll(nodesNotFound);
    }

    @Override
    public List<NodeReference> getAllNodes() {
        final List<NodeReference> allNodes = new LinkedList<NodeReference>(validNodes);
        allNodes.addAll(invalidNodes);
        return allNodes;
    }

    @Override
    public List<NormalizableNodeReference> getValidNodes() {
        return new LinkedList<NormalizableNodeReference>(this.validNodes);
    }

    @Override
    public List<NodeReference> getNodesNotFound() {
        return new LinkedList<>(nodesNotFound);
    }

    @Override
    public Set<NodeReference> getInvalidNodes() {
        return Collections.unmodifiableSet(invalidNodes);
    }

    @Override
    public List<NodeReference> toNormalizedRef(final List<? extends NormalizableNodeReference> nodes) {
        List<NodeReference> normNodes = null;
        if (nodes == null) {
            normNodes = new ArrayList<>(0);
        } else {
            normNodes = new ArrayList<>(nodes.size());
            for (NormalizableNodeReference node : nodes) {
                normNodes.add(node.getNormalizedRef());
            }
        }

        return normNodes;
    }

    @Override
    public NodeReference getCommandReference(final NodeReference node) {
        NodeReference cmdRef = null;
        for (Node validNode : validNodes) {
            if (validNode.getFdn().equals(node.getFdn())
                    || (validNode.hasNormalizedRef() && validNode.getNormalizedRef().getFdn().equals(node.getFdn()))) {
                cmdRef = validNode.getSourceReference();
                break;
            }
        }

        if (cmdRef == null) {
            for (NodeReference invalidNode : invalidNodes) {
                if (invalidNode instanceof Node) {
                    final Node invNode = (Node) invalidNode;
                    if (invNode.getFdn().equals(node.getFdn())
                            || (invNode.hasNormalizedRef() && invNode.getNormalizedRef().getFdn().equals(node.getFdn()))) {
                        cmdRef = invNode.getSourceReference();
                        break;
                    }
                }
            }
        }

        return cmdRef;
    }

    @Override
    public void setAsInvalidOrFailed(final Collection<NodeReference> nodes, final NscsServiceException error) {
        for (NodeReference node : nodes) {
            setAsInvalidOrFailed(node, error);
        }
    }

    @Override
    public void setAsInvalidOrFailed(final NodeReference node, final NscsServiceException error) {
        Node invalid = null;
        for (Node validNode : validNodes) {
            if (validNode.getFdn().equals(node.getFdn())
                    || (validNode.hasNormalizedRef() && validNode.getNormalizedRef().getFdn().equals(node.getFdn()))) {
                invalid = validNode;
                break;
            }
        }

        if (invalid == null) {
            addInvalidNode(node, error);
        } else {
            validNodes.remove(invalid);
            invalid.setValid(false);
            addInvalidNode(invalid, error);
        }
    }

    @Override
    public void setAsInvalidOrFailedAndThrow(final Collection<NodeReference> nodes, final NscsServiceException error) {
        setAsInvalidOrFailed(nodes, error);
        if (this.invalidNodes.size() > 0) {
            logger.error("Existing invalid nodes found with error code: {}", error.getErrorCode());
            throw new ExistingInvalidNodesException(error.getMessage());
        }
    }

    @Override
    public void setAsInvalidOrFailedAndThrow(final NodeReference node, final NscsServiceException error) {
        setAsInvalidOrFailed(node, error);
        if (this.invalidNodes.size() > 0) {
            logger.info("Existing invalid nodes found with error code: {}", error.getErrorCode());
            throw new ExistingInvalidNodesException(error.getMessage());
        }
    }

    @Override
    public void setAsValid(NodeReference nodeRef) {
        Node validNode = null;
        if (nodeRef != null) {
            for (NodeReference notFoundNodeRef : this.nodesNotFound) {
                if (notFoundNodeRef.getFdn().equals(nodeRef.getFdn())) {
                    validNode = new Node(nodeRef);
                    break;
                }
            }
        }

        if (validNode != null) {
            this.nodesNotFound.remove(nodeRef);
            this.validNodes.add(validNode);
        } else {
            logger.error("Node {} can't be moved from notFound to valid", (nodeRef != null) ? nodeRef.getFdn() : null);
        }
    }

    public Boolean hasInvalidNode(final Map<String, Integer> nodeCounters) {
        Boolean hasInvalid = false;
        if (errors.keySet().size() == 1) {
            final NscsServiceException original = errors.keySet().iterator().next();
            final List<NodeReference> errorNodes = new LinkedList<>();
            for (NodeReference nodeRef : errors.get(original)) {
                errorNodes.add(getSourceRefIfAvailable(nodeRef));
            }
            nodeCounters.put("VALID", Integer.valueOf(validNodes.size()));
            nodeCounters.put("INVALID", Integer.valueOf(errorNodes.size()));
            hasInvalid = true;
        } else if (errors.keySet().size() > 1) {
            final MultiErrorNodeException multiErrorNodeException = new MultiErrorNodeException();
            for (Map.Entry<NscsServiceException, Set<NodeReference>> exceptionSetEntry : errors.entrySet()) {
                final NscsServiceException original = exceptionSetEntry.getKey();
                for (NodeReference nodeRef : exceptionSetEntry.getValue()) {
                    multiErrorNodeException.addException(getSourceRefIfAvailable(nodeRef), original);
                }
            }
            nodeCounters.put("VALID", Integer.valueOf(validNodes.size()));
            nodeCounters.put("INVALID", Integer.valueOf(multiErrorNodeException.getErrorsSize()));
            hasInvalid = true;
        } else {
            nodeCounters.put("VALID", Integer.valueOf(validNodes.size()));
            nodeCounters.put("INVALID", Integer.valueOf(0));
        }
        return hasInvalid;

    }

    /**
     * Throws an exception if there is any node marked as invalid.
     * 
     * @throws com.ericsson.nms.security.nscs.api.exception.wrapper.NodeException
     *             if there is only one type of exception listed as the cause for invalidating one or more nodes.
     * @throws com.ericsson.nms.security.nscs.api.exception.wrapper.MultiErrorNodeException
     *             if there are more than one exception type as the cause for invalidating nodes.
     */
    public void throwIfHasInvalidNode() {
        if (errors.keySet().size() == 1) {
            final NscsServiceException original = errors.keySet().iterator().next();
            final List<NodeReference> errorNodes = new LinkedList<>();
            for (NodeReference nodeRef : errors.get(original)) {
                errorNodes.add(getSourceRefIfAvailable(nodeRef));
            }
            logger.error("Existing invalid nodes found with error code: {}", original.getErrorCode());
            throw new NodeException(errorNodes, original);
        } else if (errors.keySet().size() > 1) {
            final MultiErrorNodeException multiErrorNodeException = new MultiErrorNodeException();
            NscsServiceException original;
            for (Map.Entry<NscsServiceException, Set<NodeReference>> exceptionSetEntry : errors.entrySet()) {
                original = exceptionSetEntry.getKey();
                for (NodeReference nodeRef : exceptionSetEntry.getValue()) {
                    multiErrorNodeException.addException(getSourceRefIfAvailable(nodeRef), original);
                }
            }
            logger.warn("Existing invalid nodes found with error codes: {}", multiErrorNodeException.getErrorCode());
            throw multiErrorNodeException;
        }
    }

    private NodeReference getSourceRefIfAvailable(final NodeReference nodeRef) {
        NodeReference sourceRef = nodeRef;
        if (nodeRef instanceof Node) {
            sourceRef = ((Node) nodeRef).getSourceReference();
        }
        return sourceRef;
    }

    private void addInvalidNode(final NodeReference node, final NscsServiceException error) {
        invalidNodes.add(node);
        Set<NodeReference> errorNodes = errors.get(error);
        if (errorNodes == null) {
            errorNodes = new HashSet<>();
            errors.put(error, errorNodes);
        }
        errorNodes.add(node);
    }
}
