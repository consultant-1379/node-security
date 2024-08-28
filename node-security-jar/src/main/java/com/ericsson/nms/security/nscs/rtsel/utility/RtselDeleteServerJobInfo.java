/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.rtsel.utility;

import java.util.List;
import java.util.Set;

/**
 * This class is having the list of nodes and nodeRtselDetails object to start workflow for valid nodes
 * 
 * @author xchowja
 *
 */
public class RtselDeleteServerJobInfo {
    final List<String> nodeFdnsList;
    final Set<String> serverNames;

    /**
     * @param nodeFdnsList the nodeFdnsList
     * @param serverNames the serverNames
     */
    public RtselDeleteServerJobInfo(final List<String> nodeFdnsList, final Set<String> serverNames) {
        super();
        this.nodeFdnsList = nodeFdnsList;
        this.serverNames = serverNames;
    }

    /**
     * @return the nodeFdnsList
     */
    public List<String> getNodeFdnsList() {
        return nodeFdnsList;
    }

    /**
     * @return the serverNames
     */
    public Set<String> getServerNames() {
        return serverNames;
    }

}
