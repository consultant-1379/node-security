package com.ericsson.nms.security.nscs.api.model;

import java.io.Serializable;

/**
 * This interface represents a reference to a node, compound by the node FDN and name.
 * <p>Ideally this class should be used every time a node identification is needed.</p>
 * @author emaynes.
 */
public interface NodeReference extends Serializable{

    /**
     * Get the node's name represented by this reference
     * @return String with the name of the node. Eg.: ERBS_0001
     */
    String getName();

    /**
     * Get the node's FDN
     * @return String with the FDN of the node. Eg.: MeContext=ERBS_0001
     */
    String getFdn();
}
