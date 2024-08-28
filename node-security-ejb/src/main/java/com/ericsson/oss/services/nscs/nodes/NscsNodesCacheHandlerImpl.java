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
package com.ericsson.oss.services.nscs.nodes;

import java.util.*;

import javax.cache.Cache;
import javax.cache.Cache.Entry;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.ericsson.oss.services.nscs.nodes.cache.service.NscsCacheWebpushObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.cache.annotation.NamedCache;
import com.ericsson.oss.services.cache.NodesConfigurationStatusRecord;
import com.ericsson.oss.services.nscs.nodes.cache.service.interfaces.NscsNodesCacheHandler;

/**
 *
 * @author egicass Nodes cache handler implementation
 */

@ApplicationScoped
public class NscsNodesCacheHandlerImpl implements NscsNodesCacheHandler {

    private static final Logger logger = LoggerFactory.getLogger(NscsNodesCacheHandlerImpl.class);
    public static final String CACHE_NAME = "NodeSecurityReplicatedCache";



    @Inject
    @NamedCache(CACHE_NAME)
    private Cache<String, NodesConfigurationStatusRecord> nodeStatusDataCache;

    private boolean isNodeStatusCacheUpdated = false;

    @Inject
    NscsCacheWebpushObserver observer;


    @Override
    public void updateNodeStatusDataCache(final Map<String, NodesConfigurationStatusRecord> nodeStatusTable) {
        nodeStatusDataCache.putAll(nodeStatusTable);
        setNodeStatusCacheUpdated(true);
    }

    @Override
    public void insertOrUpdateNode(final String nodeName, final NodesConfigurationStatusRecord nodesConfigurationStatusRecord) {
        logger.debug("Node Staus record: {} received in addEntryToNodeStatusCache for nodeFdn: {} ", nodesConfigurationStatusRecord, nodeName);
        try {
            nodeStatusDataCache.put(nodeName, nodesConfigurationStatusRecord);
            observer.update(nodesConfigurationStatusRecord);

        } catch (final Exception e) {
            logger.error(String.format("Error inserting: [%s] ", nodeName), e);
        }
    }

    public NodesConfigurationStatusRecord getEntryFromNodeStatusCache(final String nodeName) {

        NodesConfigurationStatusRecord nodeStatusRecord = null;
        try {

            nodeStatusRecord = nodeStatusDataCache.get(nodeName);

        } catch (final Exception e) {
            logger.error("Error getting [{}]", nodeName);

        }

        return nodeStatusRecord;
    }

    @Override
    public void removeEntryFromNodeStatusCache(final String nodeName) {
        NodesConfigurationStatusRecord nodeStatusRecord = null;
        try {

            nodeStatusRecord = getEntryFromNodeStatusCache(nodeName);
            if (nodeStatusRecord != null) {
                logger.debug("removeEntryFromNodeStatusTable:Removing the neName: {}", nodeName);
                nodeStatusDataCache.remove(nodeName);
            }
        } catch (final Exception e) {
            logger.error("Exception removing [{}]", nodeName);
        }
    }

    @Override
    public void clear() {
        nodeStatusDataCache.removeAll();
    }

    public void setNodeStatusCacheUpdated(final boolean status) {
        isNodeStatusCacheUpdated = status;
    }

    public boolean isNodeStatusCacheUpdated() {
        return isNodeStatusCacheUpdated;
    }

    /**
     * @param nodeName
     * @return
     */
    @Override
    public NodesConfigurationStatusRecord getNode(final String nodeName) {
        return nodeStatusDataCache.get(nodeName);
    }

    @Override
    public List<NodesConfigurationStatusRecord> getAllNodes() {
        final List<NodesConfigurationStatusRecord> result = new ArrayList<NodesConfigurationStatusRecord>();
        final Iterator<Entry<String, NodesConfigurationStatusRecord>> iterator = nodeStatusDataCache.iterator();
        while (iterator.hasNext()) {
            result.add(iterator.next().getValue());
        }
        return result;
    }

    @Override
    public boolean isCacheEmpty() {
        boolean result = true;

        final Iterator<Entry<String, NodesConfigurationStatusRecord>> iterator = nodeStatusDataCache.iterator();

        try {
            result = !iterator.hasNext();
        } catch (final Exception e) {
            logger.info("[{}] is empty", getClass());
        }

        return result;
    }

}
