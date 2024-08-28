package com.ericsson.nms.security.nscs.data.nodereference;

import com.ericsson.nms.security.nscs.api.model.NodeReference;

/**
 * Extension of NodeReference which adds the capability to get access to the associated reference of a node.
 * <p>
 * An instance of NormalizableNodeReference can be either a reference to a MeContext or similar Managed Object (normalizable or mirror or node root)
 * and, in this case, the method <i>getNormalizedRef</i> gives access to a NodeReference that always points to the associated NetworkElement MO
 * (normalized or NE root), or a reference to a NetworkElement MO and, in this case, the method <i>getNormalizableRef</i> gives access to a
 * NodeReference that can point to the associated (if any) MeContext or similar Managed Object (normalizable or mirror or node root)
 * </p>
 *
 * @author emaynes.
 */
public interface NormalizableNodeReference extends NodeReference {

    /**
     * Check if this instance has a Normalized reference, in other words, if this instance is itself a reference to a NetworkElement MO or if it has a
     * NetworkElement MO associated to it
     *
     * @return true if this reference has a normalized reference
     */
    boolean hasNormalizedRef();

    /**
     * Get NodeReference that always points to the associated NetworkElement MO
     *
     * @return the normalized reference
     */
    NodeReference getNormalizedRef();

    /**
     * Check if this instance has a Normalizable reference, in other words, if this instance is itself a reference to a MeContext or similar MO or if
     * it has a MeContext or similar MO associated to it
     *
     * @return true if this reference has a normalizable reference
     */
    boolean hasNormalizableRef();

    /**
     * Get NodeReference that can point to the associated MeContext or similar MO
     *
     * @return the normalizable reference or null if association doesn't exist
     */
    NodeReference getNormalizableRef();

    /**
     * Gets the target category
     *
     * @return the target category
     */
    String getTargetCategory();

    /**
     * Gets the target type
     *
     * @return the target type
     */
    String getNeType();

    /**
     * Get the node OSS model identity
     *
     * @return
     */
    String getOssModelIdentity();
}
