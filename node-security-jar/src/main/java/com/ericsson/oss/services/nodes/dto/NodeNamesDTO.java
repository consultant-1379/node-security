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
package com.ericsson.oss.services.nodes.dto;

import java.util.List;

public class NodeNamesDTO {

    private List<String> nodeNames;

    /**
     * @return the nodeNames
     */
    public List<String> getNodeNames() {
        return nodeNames;
    }

    /**
     * @param nodeNames
     *            the nodeNames to set
     */
    public void setNodeNames(final List<String> nodeNames) {
        this.nodeNames = nodeNames;
    }

}
