/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.util;

import com.ericsson.oss.services.cache.NodesConfigurationStatusRecord;

public class CacheWebPushServiceEvent {

    private static final long serialVersionUID = 2735139089868197958L;

    private NodesConfigurationStatusRecord node;

    public NodesConfigurationStatusRecord getNode() {
        return node;
    }

    public void setNode(NodesConfigurationStatusRecord node) {
        this.node = node;
    }
}
