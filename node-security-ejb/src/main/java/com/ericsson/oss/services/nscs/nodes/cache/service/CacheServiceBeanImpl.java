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

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.ejb.startup.NscsNodesDataLoaderProcessor;
import com.ericsson.oss.services.cache.NodesConfigurationStatusRecord;
import com.ericsson.oss.services.nscs.interceptors.RbacInterceptor;
import com.ericsson.oss.services.nscs.nodes.cache.service.interfaces.NscsNodesCacheHandler;
import com.ericsson.oss.services.nscs.nodes.interfaces.CacheServiceBean;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class CacheServiceBeanImpl implements CacheServiceBean {

    @Inject
    private Logger logger;

    @Inject
    private NscsNodesDataLoaderProcessor processor;

    @Inject
    private NscsNodesCacheHandler cacheHandler;

    /**
     * Loads from DPS all existent NetworkElement MOs to Node Cache.
     *
     * @deprecated This method is for DEBUG PURPOSES ONLY (to manually load the cache with all existent nodes). If the number of existent
     *             NetworkElement MOs is big, the transaction timeout could be exceeded!
     *
     */
    @Deprecated
    @Override
    @Interceptors(RbacInterceptor.class)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void update() {
        try {
            processor.load();
        } catch (final Exception e) {
            logger.error("Error forcing node cache update from REST: {}", e.getMessage());
        }

        logger.info("Forced node cache update from REST");

    }

    @Override
    @Interceptors(RbacInterceptor.class)
    public int count() {
        return getRecordsFromCache().size();
    }

    @Override
    @Interceptors(RbacInterceptor.class)
    public List<NodesConfigurationStatusRecord> content(final Integer offset, final String limit) throws Exception {

        List<NodesConfigurationStatusRecord> records = getRecordsFromCache();
        try {

            if (limit.equals("*")) {
                records = records.subList(offset, records.size());
            } else if (limit.matches("\\d+")) {
                records = records.subList(offset, Integer.parseInt(limit));
            } else {
                throw new Exception("Exception occurred: Invalid limit");
            }

        } catch (final NumberFormatException e) {
            throw new Exception("Exception occurred:" + e.getMessage());
        }
        return records;
    }

    @Override
    @Interceptors(RbacInterceptor.class)
    public NodesConfigurationStatusRecord getNode(final String nodeName) {
        return cacheHandler.getNode(nodeName);
    }

    @Override
    public void clear() {
        cacheHandler.clear();
    }

    /**
     * Gets all the node records from Node Cache.
     *
     * @return the list of records in Node Cache.
     */
    private List<NodesConfigurationStatusRecord> getRecordsFromCache() {
        return cacheHandler.getAllNodes();
    }

}
