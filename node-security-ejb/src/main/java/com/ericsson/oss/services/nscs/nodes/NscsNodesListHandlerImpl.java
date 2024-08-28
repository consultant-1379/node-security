/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.nscs.nodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.data.DpsNodeLoader;
import com.ericsson.oss.services.cache.NodesConfigurationStatusRecord;
import com.ericsson.oss.services.nodes.dto.NodeNamesDTO;
import com.ericsson.oss.services.nodes.dto.NodesDTO;
import com.ericsson.oss.services.nodes.dto.NodesFilterDTO;
import com.ericsson.oss.services.nodes.dto.interfaces.Filter;
import com.ericsson.oss.services.nscs.interceptors.RbacInterceptor;
import com.ericsson.oss.services.nscs.nodes.cache.service.interfaces.NscsNodesCacheHandler;
import com.ericsson.oss.services.nscs.nodes.dao.CollectionsDAO;
import com.ericsson.oss.services.nscs.nodes.dao.SavedSearchDAO;
import com.ericsson.oss.services.nscs.nodes.interfaces.NscsNodesListHandler;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

@Stateless
public class NscsNodesListHandlerImpl implements NscsNodesListHandler {
    private static final Logger logger = LoggerFactory.getLogger(NscsNodesListHandlerImpl.class);

    @Inject
    private NscsNodesCacheHandler cacheHandler;

    @Inject
    private CollectionsDAO collectionsDao;

    @Inject
    private SavedSearchDAO savedSearchDao;

    @Inject
    private Filter<NodesConfigurationStatusRecord, NodesFilterDTO> filterManager;

    @Inject
    private DpsNodeLoader nodeLoader;

    @Inject
    private NscsContextService nscsContextService;

    @Override
    @Interceptors(RbacInterceptor.class)
    public List<NodesConfigurationStatusRecord> getPage(final NodesDTO dto, final String userId) {
        logger.debug("Invoking getPage() method");
        int end;
        List<NodesConfigurationStatusRecord> data = new ArrayList<>();

        try {
            if ((dto.getCollectionIds() != null && !dto.getCollectionIds().isEmpty())
                    || (dto.getSavedSearches() != null && !dto.getSavedSearches().isEmpty())) {

                data = getNodesData(dto, userId);
                // TORF 11624 recalculate limit as offset +limit
                final int limit = dto.getOffset() + dto.getLimit();
                end = Math.min(limit, data.size());
                data = data.subList(dto.getOffset(), end);
            }
        } catch (final Exception e) {
            logger.error("getPage() Error Occurred [ {} ] message[ {} ]", e.getClass().getCanonicalName(), e.getMessage());
        }
        return data;
    }

    @Override
    @Interceptors(RbacInterceptor.class)
    public int getCount(final NodesDTO dto, final String userId) {
        logger.debug("Invoking count() method");
        List<NodesConfigurationStatusRecord> data = new ArrayList<>();

        try {
            if ((dto.getCollectionIds() != null && !dto.getCollectionIds().isEmpty())
                    || (dto.getSavedSearches() != null && !dto.getSavedSearches().isEmpty())) {

                data = getNodesData(dto, userId);
            }
        } catch (final Exception e) {
            logger.error("getCount() Error Occurred [ {} ] message[ {} ]", e.getClass().getCanonicalName(), e.getMessage());
        }
        return data.size();
    }

    private List<NodesConfigurationStatusRecord> getNodesData(final NodesDTO dto, final String userId) {
        logger.debug("Invoking getNodesData() method");
        final List<Map<String, Object>> topologyMap = new ArrayList<>();
        final List<NodesConfigurationStatusRecord> nodes = new LinkedList<>();

        // fill topology map from input collection and/or savedsearch
        fillTopologyMap(dto, userId, topologyMap);

        for (final Map<String, Object> m : topologyMap) {
            if (m != null && m.get("name") != null) {
                final String nodeName = (m.get("name")).toString();
                NodesConfigurationStatusRecord outDto = cacheHandler.getNode(nodeName);

                //if selected node is not in cache, add it
                if (outDto == null) {
                    outDto = addNodeInCache(nodeName);
                    logger.debug("Node cache empty for node [{}], adding it to cache and returning to DTO", nodeName);
                }

                //fill output record with node information and apply filtering if needed
                fillOutputRecord(dto, outDto, nodes);
            } else {
                logger.error("Null value for key [{}] in topologyMap map", "name");
            }
        }
        Collections.sort(nodes);
        return nodes;
    }

    private void fillTopologyMap (final NodesDTO dto, final String userId, List<Map<String, Object>> topologyMap) {
        if (dto.getCollectionIds() != null) {
            List<Map<String, Object>> collection = collectionsDao.getCollectionsByPoIds(dto.getCollectionIds(), userId);
            topologyMap.addAll(collection);
        }

        if (dto.getSavedSearches() != null) {
            topologyMap.addAll(savedSearchDao.getSavedSearchesByPoIds(dto.getSavedSearches(), userId));
        }
    }

    private void fillOutputRecord (final NodesDTO dto,
                                   final NodesConfigurationStatusRecord outDto,
                                   final List<NodesConfigurationStatusRecord> nodes) {
        if (outDto != null && !nodes.contains(outDto)
                && (dto.getFilter() == null ||
                (dto.getFilter() != null && filterManager.apply(outDto, dto.getFilter())))
        ) {
            nodes.add(outDto);
        }
    }

    private NodesConfigurationStatusRecord addNodeInCache(final String nodeName) {
        logger.debug("Node cache empty for node [{}], adding it to cache and returning to DTO", nodeName);
        NodesConfigurationStatusRecord outDto = null;
        final NodesConfigurationStatusRecord record = nodeLoader.getNode(nodeName);
        if (record != null) {
            updateCache(record);
            outDto = record;
        }
        logger.debug("Node [{}], added to cache and returning to DTO", nodeName);
        return outDto;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private void updateCache(final NodesConfigurationStatusRecord record) {
        cacheHandler.insertOrUpdateNode(record.getName(), record);
    }

    @Override
    @Interceptors(RbacInterceptor.class)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<NodesConfigurationStatusRecord> getNodes(final NodeNamesDTO dto) {
        final List<NodesConfigurationStatusRecord> result = new ArrayList<>();
        final List<String> nodeNames = dto.getNodeNames();

        Integer valid = 0;
        Integer skipped = 0;
        Integer invalid = 0;
        Integer success = 0;
        for (final String nodeName : nodeNames) {
            NodesConfigurationStatusRecord record = cacheHandler.getNode(nodeName);
            if (record != null) {
                result.add(record);
                skipped++;
            } else {
                record = nodeLoader.getNode(nodeName);
                if (record != null) {
                    updateCache(record);
                    result.add(record);
                    valid++;
                    success++;
                } else {
                    invalid++;
                }
            }
        }
        nscsContextService.setNumValidItemsContextValue(valid);
        nscsContextService.setNumSkippedItemsContextValue(skipped);
        nscsContextService.setNumSuccessItemsContextValue(success);
        nscsContextService.setNumInvalidItemsContextValue(invalid);

        return result;
    }
}
