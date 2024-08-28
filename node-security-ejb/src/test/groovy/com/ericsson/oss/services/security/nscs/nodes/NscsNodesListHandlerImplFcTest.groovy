package com.ericsson.oss.services.security.nscs.nodes

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.exception.DataAccessException
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes
import com.ericsson.nms.security.nscs.data.DpsNodeLoader
import com.ericsson.oss.services.cache.NodesConfigurationStatusRecord
import com.ericsson.oss.services.nodes.dto.NodeNamesDTO
import com.ericsson.oss.services.nodes.dto.NodesDTO
import com.ericsson.oss.services.nodes.dto.NodesFilterDTO
import com.ericsson.oss.services.nodes.dto.interfaces.Filter
import com.ericsson.oss.services.nscs.nodes.NscsNodesListHandlerImpl
import com.ericsson.oss.services.nscs.nodes.cache.service.interfaces.NscsNodesCacheHandler
import com.ericsson.oss.services.nscs.nodes.dao.CollectionsDAO
import com.ericsson.oss.services.nscs.nodes.dao.SavedSearchDAO

class NscsNodesListHandlerImplFcTest extends CdiSpecification {

    private static final String ATTRIBUTE_NAME = "name"
    private static final String NODE_NAME_1 = "LTE03ERBS00001"
    private static final String IPADDR_NODE_1 = "10.10.10.1"
    private static final String SYNC_STATUS_NODE_1 = "SYNCHRONIZED"

    @ObjectUnderTest
    NscsNodesListHandlerImpl nscsNodesListHandler

    def userId = "Administrator"
    def inDto = new NodesDTO()
    List<Map<String, Object>> moWithAttributesList = new ArrayList<>()
    List<Map<String, Object>> moWithAttributesListEmpty = new ArrayList<>()

    NodesConfigurationStatusRecord nodesCfgStatusRecord = new NodesConfigurationStatusRecord()

    def allNodesDto = new NodeNamesDTO()
    def iFilterMock = new NodesFilterDTO()

    @ImplementationInstance
    CollectionsDAO collectionsDAOMock = [
            getCollectionsByPoIds: { List<Long> persistenceObjectIds, String userId ->
                return moWithAttributesList
            }
    ] as CollectionsDAO

    @ImplementationInstance
    CollectionsDAO collectionsDAOMockExcp = [
            getCollectionsByPoIds: { List<Long> persistenceObjectIds, String userId ->
                throw new DataAccessException(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR)
            }
    ] as CollectionsDAO

    @ImplementationInstance
    CollectionsDAO collectionsDAOMockEmpty = [
            getCollectionsByPoIds: { List<Long> persistenceObjectIds, String userId ->
                return moWithAttributesListEmpty
            }
    ] as CollectionsDAO

    @ImplementationInstance
    SavedSearchDAO savedSearchDAOMock = [
            getSavedSearchesByPoIds: { List<Long> persistenceObjectIds, String userId ->
                return moWithAttributesList
            }
    ] as SavedSearchDAO

    @ImplementationInstance
    NscsNodesCacheHandler nodesCacheHandlerMock = [
            getNode: { String nodeName ->
                return nodesCfgStatusRecord
            }
    ] as NscsNodesCacheHandler

    @ImplementationInstance
    NscsNodesCacheHandler nodesCacheHandlerMockEmpty = [
            getNode: { String nodeName ->
                return null
            },
            insertOrUpdateNode: { String nodeName, NodesConfigurationStatusRecord record ->
                return
            }
    ] as NscsNodesCacheHandler

    @ImplementationInstance
    DpsNodeLoader dpsNodeLoaderMock = [
            getNode : { String nodeName
                return nodesCfgStatusRecord
            }
    ] as DpsNodeLoader

    @ImplementationInstance
    DpsNodeLoader dpsNodeLoaderMockEmpty = [
            getNode : { String nodeName
                return null
            }
    ] as DpsNodeLoader

    Filter<NodesConfigurationStatusRecord, NodesFilterDTO> filterManagerMock = [
            apply : { final NodesConfigurationStatusRecord source, final NodesFilterDTO dto ->
                return true
            }
    ] as Filter<NodesConfigurationStatusRecord, NodesFilterDTO>

    @Override
    def addAdditionalInjectionProperties(InjectionProperties injectionProperties) {}

    def setup () {
        inDto.setOffset(0)
        inDto.setLimit(15)

        Map<String, Object> moAttributes = new HashMap<>()
        moAttributes.put(ATTRIBUTE_NAME, NODE_NAME_1)
        moWithAttributesList.add(moAttributes)

        Map<String, Object> moAttributesEmpty = new HashMap<>()
        moWithAttributesListEmpty.add(moAttributesEmpty)

        nodesCfgStatusRecord.setName(NODE_NAME_1)
        nodesCfgStatusRecord.setIpaddress(IPADDR_NODE_1)
        nodesCfgStatusRecord.setSyncstatus(SYNC_STATUS_NODE_1)

        List<String> nodeNames = new ArrayList<>()
        nodeNames.add(NODE_NAME_1)
        allNodesDto.setNodeNames(nodeNames)
    }

    def "getPage with Collection" () {
        given:
        List<Long>collectionIds = Collections.singletonList(1L)
        inDto.setCollectionIds(collectionIds)
        nscsNodesListHandler.collectionsDao = collectionsDAOMock
        nscsNodesListHandler.savedSearchDao = savedSearchDAOMock
        nscsNodesListHandler.cacheHandler = nodesCacheHandlerMock
        when:
        List<NodesConfigurationStatusRecord> outNodeRecord = nscsNodesListHandler.getPage(inDto, userId)
        then:
        outNodeRecord.get(0).getName() == NODE_NAME_1 &&
        outNodeRecord.get(0).getIpaddress() == IPADDR_NODE_1 &&
        outNodeRecord.get(0).getSyncstatus() == SYNC_STATUS_NODE_1
    }

    def "getPage with savedSearch" () {
        given:
        List<Long> savedSearches = Collections.singletonList(1L)
        inDto.setSavedSearches(savedSearches)
        nscsNodesListHandler.collectionsDao = collectionsDAOMock
        nscsNodesListHandler.savedSearchDao = savedSearchDAOMock
        nscsNodesListHandler.cacheHandler = nodesCacheHandlerMock
        when:
        List<NodesConfigurationStatusRecord> outNodeRecord = nscsNodesListHandler.getPage(inDto, userId)
        then:
        outNodeRecord.get(0).getName() == NODE_NAME_1 &&
        outNodeRecord.get(0).getIpaddress() == IPADDR_NODE_1 &&
        outNodeRecord.get(0).getSyncstatus() == SYNC_STATUS_NODE_1
    }

    def "getPage with Collection and node not cached" () {
        given:
        List<Long>collectionIds = Collections.singletonList(1L)
        inDto.setCollectionIds(collectionIds)
        nscsNodesListHandler.collectionsDao = collectionsDAOMock
        nscsNodesListHandler.savedSearchDao = savedSearchDAOMock
        nscsNodesListHandler.cacheHandler = nodesCacheHandlerMockEmpty
        nscsNodesListHandler.nodeLoader = dpsNodeLoaderMock
        when:
        List<NodesConfigurationStatusRecord> outNodeRecord = nscsNodesListHandler.getPage(inDto, userId)
        then:
        outNodeRecord.get(0).getName() == NODE_NAME_1 &&
                outNodeRecord.get(0).getIpaddress() == IPADDR_NODE_1 &&
                outNodeRecord.get(0).getSyncstatus() == SYNC_STATUS_NODE_1
    }

    def "getPage with Collection and node both not in cache and in dps" () {
        given:
        List<Long>collectionIds = Collections.singletonList(1L)
        inDto.setCollectionIds(collectionIds)
        nscsNodesListHandler.collectionsDao = collectionsDAOMock
        nscsNodesListHandler.savedSearchDao = savedSearchDAOMock
        nscsNodesListHandler.cacheHandler = nodesCacheHandlerMockEmpty
        nscsNodesListHandler.nodeLoader = dpsNodeLoaderMockEmpty
        when:
        List<NodesConfigurationStatusRecord> outNodeRecord = nscsNodesListHandler.getPage(inDto, userId)
        then:
        outNodeRecord.size() == 0
    }

    def "getPage with Collection raising an exception" () {
        given:
        List<Long>collectionIds = Collections.singletonList(1L)
        inDto.setCollectionIds(collectionIds)
        nscsNodesListHandler.collectionsDao = collectionsDAOMockExcp
        when:
        List<NodesConfigurationStatusRecord> outNodeRecord = nscsNodesListHandler.getPage(inDto, userId)
        then:
        outNodeRecord.size() == 0
    }

    def "getCount with node size greater than 0 and only collections" () {
        given:
        List<Long>collectionIds = Collections.singletonList(1L)
        inDto.setCollectionIds(collectionIds)
        nscsNodesListHandler.collectionsDao = collectionsDAOMock
        nscsNodesListHandler.savedSearchDao = savedSearchDAOMock
        nscsNodesListHandler.cacheHandler = nodesCacheHandlerMock
        when:
        int nodeCount = nscsNodesListHandler.getCount(inDto, userId)
        then:
        nodeCount == moWithAttributesList.size()
    }

    def "getCount with node size greater than 0 and only savedSearch" () {
        given:
        List<Long>saveSearchIds = Collections.singletonList(1L)
        inDto.setSavedSearches(saveSearchIds)
        nscsNodesListHandler.collectionsDao = collectionsDAOMock
        nscsNodesListHandler.savedSearchDao = savedSearchDAOMock
        nscsNodesListHandler.cacheHandler = nodesCacheHandlerMock
        when:
        int nodeCount = nscsNodesListHandler.getCount(inDto, userId)
        then:
        nodeCount == moWithAttributesList.size()
    }

    def "getCount raising an exception" () {
        given:
        List<Long>collectionIds = Collections.singletonList(1L)
        inDto.setCollectionIds(collectionIds)
        nscsNodesListHandler.collectionsDao = collectionsDAOMockExcp
        when:
        int nodeCount = nscsNodesListHandler.getCount(inDto, userId)
        then:
        nodeCount == 0
    }

    def "getCount with input node list empty" () {
        given:
        List<Long>collectionIds = Collections.singletonList(1L)
        inDto.setCollectionIds(collectionIds)
        nscsNodesListHandler.collectionsDao = collectionsDAOMockEmpty
        nscsNodesListHandler.cacheHandler = nodesCacheHandlerMock
        when:
        int nodeCount = nscsNodesListHandler.getCount(inDto, userId)
        then:
        nodeCount == 0
    }

    def "getCount with input filter" () {
        given:
        List<Long>collectionIds = Collections.singletonList(1L)
        inDto.setCollectionIds(collectionIds)
        inDto.setFilter(iFilterMock)
        nscsNodesListHandler.collectionsDao = collectionsDAOMock
        nscsNodesListHandler.cacheHandler = nodesCacheHandlerMock
        nscsNodesListHandler.filterManager = filterManagerMock
        when:
        int nodeCount = nscsNodesListHandler.getCount(inDto, userId)
        then:
        nodeCount == moWithAttributesList.size()
    }

    def "get all with nodes already cached" () {
        given:
        nscsNodesListHandler.cacheHandler = nodesCacheHandlerMock
        when:
        List<NodesConfigurationStatusRecord> outNodeRecord = nscsNodesListHandler.getNodes(allNodesDto)
        then:
        outNodeRecord.get(0).getName() == NODE_NAME_1 &&
        outNodeRecord.get(0).getIpaddress() == IPADDR_NODE_1 &&
        outNodeRecord.get(0).getSyncstatus() == SYNC_STATUS_NODE_1
    }

    def "get all with nodes not cached" () {
        given:
        nscsNodesListHandler.cacheHandler = nodesCacheHandlerMockEmpty
        nscsNodesListHandler.nodeLoader = dpsNodeLoaderMock
        when:
        List<NodesConfigurationStatusRecord> outNodeRecord = nscsNodesListHandler.getNodes(allNodesDto)
        then:
        outNodeRecord.get(0).getName() == NODE_NAME_1 &&
        outNodeRecord.get(0).getIpaddress() == IPADDR_NODE_1 &&
        outNodeRecord.get(0).getSyncstatus() == SYNC_STATUS_NODE_1
    }

    def "get all with no nodes both in cache and in dps" () {
        given:
        nscsNodesListHandler.cacheHandler = nodesCacheHandlerMockEmpty
        nscsNodesListHandler.nodeLoader = dpsNodeLoaderMockEmpty
        when:
        List<NodesConfigurationStatusRecord> outNodeRecord = nscsNodesListHandler.getNodes(allNodesDto)
        then:
        outNodeRecord.size() == 0
    }
}
