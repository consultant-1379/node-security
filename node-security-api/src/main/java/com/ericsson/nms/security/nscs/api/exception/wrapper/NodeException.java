package com.ericsson.nms.security.nscs.api.exception.wrapper;

import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;

import java.util.Arrays;
import java.util.List;

/**
 * Exception that wraps a collection of offending Nodes and the corresponding exception.
 * <p>This exception is thrown by the CommandContext implementation when one or more nodes
 * were invalid or caused an error, but all of them caused the same exception.</p>
 * @author emaynes.
 */
public class NodeException extends MultiErrorNodeException{

    private static final long serialVersionUID = 989834360222965707L;

    private NscsServiceException original;

    public NodeException(final NodeReference node, final NscsServiceException cause) {
        this(Arrays.asList(node), cause);
    }

    /**
     * Creates a new NodeException based on alist of NodeReferences
     * @param nodes List of offending nodes
     * @param cause the exception raised during the processing of the nodes
     */
    public NodeException(final List<? extends NodeReference> nodes, final NscsServiceException cause) {
        super(cause.getMessage(), cause);
        this.original = cause;
        this.setSuggestedSolution(cause.getSuggestedSolution());
        for (NodeReference node : nodes) {
            super.addException(node, cause);
        }

    }

    @Override
    public NodeException addException(final NodeReference node, final NscsServiceException exception) {
        if ( ! original.equals(exception) ) {
            throw new IllegalArgumentException("Exception has to be of the same initial type");
        }
        super.addException(node, exception);
        return this;
    }

    @Override
    public ErrorType getErrorType() {
        return original.getErrorType();
    }
}
