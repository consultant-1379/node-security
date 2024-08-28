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
package com.ericsson.oss.services.nscs.nodes.cache.service;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.util.CacheWebPushServiceEvent;
import org.slf4j.Logger;

import com.ericsson.oss.services.cache.NodesConfigurationStatusRecord;
import com.ericsson.oss.services.nscs.nodes.interfaces.CacheObserver;

/**
 * @author egicass
 */
public class NscsCacheWebpushObserver implements CacheObserver {

    @Inject
    Logger logger;

    @Inject
    Event<CacheWebPushServiceEvent> nodeEvent;


    /* (non-Javadoc)
     * @see com.ericsson.oss.services.nscs.nodes.interfaces.CacheObserver#update()
     */
    @Override
    public void update(NodesConfigurationStatusRecord node) {

        final CacheWebPushServiceEvent cacheWebPushServiceEvent = new CacheWebPushServiceEvent();
        cacheWebPushServiceEvent.setNode(node);
        nodeEvent.fire(cacheWebPushServiceEvent);

        logger.debug("Cache update happened on node [{}]", node.getName());
    }

}
