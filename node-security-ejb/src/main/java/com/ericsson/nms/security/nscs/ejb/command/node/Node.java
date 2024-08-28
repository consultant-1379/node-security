package com.ericsson.nms.security.nscs.ejb.command.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;

/**
 * Implementation of {@link com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference} used by command handling infrastructure
 * (ContextImpl and NscsServiceBean) to key track of nodes.
 *
 * @author emaynes.
 */
public class Node implements NormalizableNodeReference {

    private static final long serialVersionUID = -8822413825539679748L;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String fdn;
    private final String name;
    private NormalizableNodeReference normalizableReference;
    private NodeReference sourceReference;
    private boolean valid = true;

    /**
     * Constructor used to create a valid instance of Node. At this level valid means that both normalizable (MeContext or similar) MO and normalized
     * MO (NetworkElement) exist.
     *
     * @param normalizableReference
     *            the normalizable reference of the node
     * @param sourceReference
     *            the reference representing the name/FDN used in the command line.
     */
    public Node(final NormalizableNodeReference normalizableReference, final NodeReference sourceReference) {
        this.normalizableReference = normalizableReference;
        this.name = normalizableReference.getName();
        this.fdn = normalizableReference.getFdn();
        this.sourceReference = sourceReference;
    }

    /**
     * Constructor used to create an <b>invalid</b> instance of Node. At this level invalid means that normalizable MO doesn't exist.
     *
     * @param node
     *            the node reference. This should be the same name/FDN used in the command line.
     */
    public Node(final NodeReference node) {
        if (node != null) {
            this.fdn = node.getFdn();
            this.name = node.getName();
        } else {
            this.fdn = null;
            this.name = null;
        }
        this.valid = false;
        this.normalizableReference = null;
        this.sourceReference = node;
    }

    @Override
    public boolean hasNormalizedRef() {
        if (valid) {
            return (normalizableReference != null && normalizableReference.hasNormalizedRef());
        } else {
            return (this.sourceReference != null);
        }
    }

    @Override
    public NodeReference getNormalizedRef() {
        if (valid) {
            if (this.normalizableReference == null) {
                final StringBuilder sb = new StringBuilder("Can't translate valid node [");
                sb.append(this.fdn);
                sb.append("] to NetworkElement form");
                logger.warn(sb.toString());
                throw new UnsupportedOperationException(sb.toString());
            }
            return normalizableReference.getNormalizedRef();
        } else {
            if (this.sourceReference == null) {
                final StringBuilder sb = new StringBuilder("Can't translate invalid node [");
                sb.append(this.fdn);
                sb.append("] to NetworkElement form");
                logger.warn(sb.toString());
                throw new UnsupportedOperationException(sb.toString());
            }
            return this.sourceReference;
        }
    }

    @Override
    public boolean hasNormalizableRef() {
        if (valid) {
            return (normalizableReference != null);
        } else {
            return false;
        }
    }

    @Override
    public NodeReference getNormalizableRef() {
        return this.normalizableReference;
    }

    @Override
    public String getName() {
        return this.valid ? name : sourceReference.getName();
    }

    @Override
    public String getFdn() {
        return this.valid ? fdn : sourceReference.getFdn();
    }

    @Override
    public String getTargetCategory() {
        if (valid) {
            return this.normalizableReference.getTargetCategory();
        }
        return null;
    }

    @Override
    public String getNeType() {
        if (valid && this.normalizableReference != null) {
            return this.normalizableReference.getNeType();
        }
        return null;
    }

    @Override
    public String getOssModelIdentity() {
        if (valid && this.normalizableReference != null) {
            return this.normalizableReference.getOssModelIdentity();
        }
        return null;
    }

    /**
     * Gets whether this node is classified as valid in the current context.
     *
     * @return true if the node is classified as valid.
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Sets whether this node is classified as valid in the current context.
     */
    public void setValid(final boolean valid) {
        this.valid = valid;
    }

    /**
     * Returns the reference which was used in the command line.
     *
     * @return the reference (name/FDN) used in the command line.
     */
    public NodeReference getSourceReference() {
        return sourceReference;
    }

    /**
     * Sets the reference used in the command line.
     */
    public void setSourceReference(final NodeReference sourceReference) {
        this.sourceReference = sourceReference;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Node node = (Node) o;

        if (valid != node.valid) {
            return false;
        }
        if (!fdn.equals(node.fdn)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = fdn.hashCode();
        result = 31 * result + (valid ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Node{" + "fdn='" + fdn + '\'' + ", normalizableReference=" + normalizableReference + ", name='" + name + '\'' + ", valid='" + valid
                + '\'' + '}';
    }
}
