package com.ericsson.oss.services.nscs.nodes.cache.service.interfaces;

import java.util.List;
import java.util.Map;

import com.ericsson.oss.services.cache.NodesConfigurationStatusRecord;
import com.ericsson.oss.services.nscs.nodes.interfaces.CacheObserver;

public interface NscsNodesCacheHandler {



    NodesConfigurationStatusRecord getNode(String nodeFdn);

    void updateNodeStatusDataCache(Map<String, NodesConfigurationStatusRecord> nodeStatusTable);

    //Cache<String, NodesConfigurationStatusRecord> getNodeStatusDataCache();

   // void notifyAllObservers(NodesConfigurationStatusRecord node);

    //void attach(CacheObserver observer);

    void clear();

    /**
     * @param nodeName
     * @param NodesConfigurationStatusRecord
     */
    void insertOrUpdateNode(String nodeName, NodesConfigurationStatusRecord NodesConfigurationStatusRecord);

    /**
     * @return
     */
    List<NodesConfigurationStatusRecord> getAllNodes();

    /**
     * @param name
     */
    void removeEntryFromNodeStatusCache(String name);

    /**
     * @return true if cache is empty;
     */
    boolean isCacheEmpty();

}