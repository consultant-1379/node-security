package com.ericsson.nms.security.nscs.ejb.startup;

import java.util.Map;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.data.DpsNodeLoader;
import com.ericsson.oss.services.cache.NodesConfigurationStatusRecord;
import com.ericsson.oss.services.nscs.nodes.cache.service.interfaces.NscsNodesCacheHandler;
import com.ericsson.oss.services.nscs.nodes.interfaces.CacheObserver;

/**
 * Used by the cache service rest
 */
public class NscsNodesDataLoaderProcessor {

    Logger logger = LoggerFactory.getLogger(NscsNodesDataLoaderProcessor.class);

    @EJB
    private DpsNodeLoader loader;

    @Inject
    NscsNodesCacheHandler cacheHandler;

    @Inject
    CacheObserver cacheUpdateObserver;

    /**
     * Loads from DPS all existent NetworkElement MOs to Node Cache.
     *
     * @deprecated This method is for DEBUG PURPOSES ONLY (to manually load the cache with all existent nodes). If the number of existent
     *             NetworkElement MOs is big, the transaction timeout could be exceeded!
     *
     */
    @Deprecated
    public void load() throws Exception {

        logger.info("Forced cache reload");

        final Map<String, NodesConfigurationStatusRecord> nodeStatusTable = loader.getAllNodes();
        cacheHandler.updateNodeStatusDataCache(nodeStatusTable);

        logger.info("Updated nodes cache");

    }

}
