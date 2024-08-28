/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.handler.ciphersconfig.utility;

import java.util.List;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.entities.NodeCiphers;

public class CipherJobInfo {

    final List<NodeReference> validNodesList;
    final NodeCiphers nodeCiphers;

    /**
     * @return the validNodesList
     */
    public List<NodeReference> getValidNodesList() {
        return validNodesList;
    }

    /**
     * @return the nodeCiphers
     */
    public NodeCiphers getNodeCiphers() {
        return nodeCiphers;
    }

    /**
     * @param validNodesList
     * @param nodeCiphers
     */
    public CipherJobInfo(final List<NodeReference> validNodesList, final NodeCiphers nodeCiphers) {
        super();
        this.validNodesList = validNodesList;
        this.nodeCiphers = nodeCiphers;
    }

}
