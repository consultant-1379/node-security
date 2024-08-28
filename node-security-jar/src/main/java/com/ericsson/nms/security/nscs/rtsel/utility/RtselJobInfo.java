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

import com.ericsson.nms.security.nscs.api.rtsel.NodeInfoDetails;
import com.ericsson.nms.security.nscs.rtsel.request.model.NodeRtselConfig;

/**
 * This class is used to map all Rtsel details related to a particular jobId.
 * @author xchowja
 * 
 */
public class RtselJobInfo {
    final List<NodeInfoDetails> nodeInfoDetailsList;
    final NodeRtselConfig nodeRtselConfig;

    /**
     * @param nodeInfoDetailsList the nodeInfoDetailsList
     * @param nodeRtselConfig the nodeRtselConfig
     *
     */
    public RtselJobInfo(List<NodeInfoDetails> nodeInfoDetailsList, NodeRtselConfig nodeRtselConfig) {
        super();
        this.nodeInfoDetailsList = nodeInfoDetailsList;
        this.nodeRtselConfig = nodeRtselConfig;
    }

    /**
     * @return the nodeInfoDetailsList
     */
    public List<NodeInfoDetails> getNodeInfoDetailsList() {
        return nodeInfoDetailsList;
    }

    /**
     * @return the nodeRtselConfig
     */
    public NodeRtselConfig getNodeRtselConfig() {
        return nodeRtselConfig;
    }

}
