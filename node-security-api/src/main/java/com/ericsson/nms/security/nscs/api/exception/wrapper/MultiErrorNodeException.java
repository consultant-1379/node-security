package com.ericsson.nms.security.nscs.api.exception.wrapper;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;

/**
 * Exception that wraps a collection of exceptions and the corresponding offending Node.
 * <p>This exception is thrown by the CommandContext implementation when more than one node
 * was invalid or caused an error.</p>
 * @author emaynes.
 */
public class MultiErrorNodeException extends NscsServiceException implements Iterable<MultiErrorNodeException.Entry>{

    private static final long serialVersionUID = 4502927908712545208L;

    private final List<Entry> errors = new LinkedList<>();

    {{
        setSuggestedSolution(NscsErrorCodes.PLEASE_CHECK_SUGGESTED_SOLUTION_FOR_EACH_NODE);
    }}

    public MultiErrorNodeException(){
        super(NscsErrorCodes.THERE_ARE_ISSUES_WITH_MORE_THAN_ONE_OF_THE_NODES_SPECIFIED);
    }

    protected MultiErrorNodeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    @Override
    public ErrorType getErrorType() {
        return ErrorType.MULTIPLE_ERRORS;
    }

    /**
     * Adds a Node reference and the corresponding exception.
     * @param node A node Reference to the offending node
     * @param exception a NscsServiceException caused by the node
     * @return a reference to this instance
     */
    public MultiErrorNodeException addException(final NodeReference node, final NscsServiceException exception) {
        errors.add(new Entry(node, exception));
        return this;
    }

    /**
     * Returns the size of errors list.
     * 
     * @return size of errors list.
     */
    public int getErrorsSize() {
        return errors.size();
    }

    @Override
    public Iterator<Entry> iterator() {
        return errors.iterator();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MultiErrorNodeException{");
        sb.append("errors=").append(errors);
        sb.append('}');
        return sb.toString();
    }

    /**
     * This class is used by MultiErrorNodeException associate
     * a node reference to an exception.
     */
    public static class Entry implements Serializable {
        private final NodeReference node;
        private final NscsServiceException exception;

        public Entry(final NodeReference node, final NscsServiceException exception) {
            this.node = node;
            this.exception = exception;
        }

        public NodeReference getNode() {
            return node;
        }

        public NscsServiceException getException() {
            return exception;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Entry{");
            sb.append("node=").append(node);
            sb.append(", exception=").append(exception);
            sb.append('}');
            return sb.toString();
        }
    }
}
