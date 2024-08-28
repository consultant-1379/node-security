package com.ericsson.nms.security.nscs.utilities

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.command.types.NscsNodeCommand
import com.ericsson.nms.security.nscs.api.exception.InvalidCollectionNameException
import com.ericsson.nms.security.nscs.api.exception.InvalidSavedSearchNameException
import com.ericsson.oss.services.cm.cmshared.dto.CmObject
import com.ericsson.oss.services.security.nscs.context.NscsContextService
import com.ericsson.oss.services.topologyCollectionsService.api.TopologyCollectionsEjbService
import com.ericsson.oss.services.topologyCollectionsService.dto.Category
import com.ericsson.oss.services.topologyCollectionsService.dto.CollectionDTO
import com.ericsson.oss.services.topologyCollectionsService.dto.ManagedObjectDTO
import com.ericsson.oss.services.topologySearchService.service.api.SearchExecutor
import com.ericsson.oss.services.topologySearchService.service.api.dto.NetworkExplorerResponse

import spock.lang.Unroll

class NscsInputNodeRetrievalUtilityTest extends CdiSpecification {

    @ObjectUnderTest
    private NscsInputNodeRetrievalUtility nscsInputNodeRetrievalUtility

    @MockedImplementation
    private NscsContextService contextService

    @MockedImplementation
    private TopologyCollectionsEjbService topologyCollectionsService

    @MockedImplementation
    private SearchExecutor searchExecutor

    @MockedImplementation
    private NscsNodeUtility nscsNodeUtility

    private nodeCommand = mock(NscsNodeCommand)

    def "object under test injection" () {
        expect:
        nscsInputNodeRetrievalUtility != null
    }

    def 'get node reference list from command with invalid saved search' () {
        given:
        contextService.getUserIdContextValue() >> "user-id"
        def savedSearchNameList = ["NODE1", "NODE2"]
        nodeCommand.getSavedSearchNames() >> savedSearchNameList
        //        topologyCollectionsService.getSavedSearchesByName(_ as String, _ as String) >> "NODE"
        when:
        def output = nscsInputNodeRetrievalUtility.getNodeReferenceList(nodeCommand)
        then:
        thrown(InvalidSavedSearchNameException.class)
    }

    @Unroll
    def 'get node reference list from command with node names or expressions #nodes' () {
        given:
        contextService.getUserIdContextValue() >> "user-id"
        def CmObject cmObj = mock(CmObject.class)
        cmObj.getFdn() >> "NetworkElement=NODE"
        def NetworkExplorerResponse networkExplorerResponse = mock(NetworkExplorerResponse.class)
        networkExplorerResponse.getCmObjects()>> [cmObj]
        searchExecutor.search(_, _, _) >> networkExplorerResponse
        nscsNodeUtility.getNodeNameFromFdn(_) >> "NODE"
        nodeCommand.getNodeNamesOrExpressions() >> nodes
        when:
        def output = nscsInputNodeRetrievalUtility.getNodeReferenceList(nodeCommand)
        then:
        output != null
        output.isEmpty() == result
        where:
        nodes << [
            [],
            ["*"],
            ["NODE"],
            ["NODE", "NOD*"]
        ]
        result << [true, false, false, false]
    }

    @Unroll
    def 'getNodesReferenceListFromCollection method success case' () {
        given:
        final List<String> collectionNamesList = new ArrayList<>();
        collectionNamesList.add("test");
        final Set<String> collectionNames = new LinkedHashSet<String>(collectionNamesList);
        Collection<CollectionDTO> collectionDtos = new ArrayList<>()
        List<ManagedObjectDTO> managedObjectDTOs = new ArrayList<>()
        ManagedObjectDTO managedObjectDTO = new ManagedObjectDTO()
        managedObjectDTO.setFdn("LTE02ERBS00001")
        managedObjectDTOs.add(managedObjectDTO)
        CollectionDTO collectionDto=new CollectionDTO("test",Category.PUBLIC,userName);
        collectionDto.setId("1")
        collectionDto.setElements(managedObjectDTOs)
        collectionDtos.add(collectionDto)
        contextService.getUserIdContextValue() >> 'administrator'
        topologyCollectionsService.getCollectionsByName(_,_) >> collectionDtos
        topologyCollectionsService.getCollectionByID(_,_) >> collectionDto
        nscsNodeUtility.getNodeNameFromFdn(_) >> "LTE02ERBS00001"
        properties.put("collection", collectionNamesList)
        when:
        def output = nscsInputNodeRetrievalUtility.getNodesReferenceListFromCollection(collectionNames)
        then:
        def response = output.get(0).getName()
        assert response.equals(expected)
        where:
        userName  | expected
        "administrator" | "LTE02ERBS00001"
        "test" | "LTE02ERBS00001"
        "networkExplorer" | "LTE02ERBS00001"
    }

    @Unroll
    def 'getNodesReferenceListFromCollection method failure case private collection' () {
        given:
        final List<String> collectionNamesList = new ArrayList<>();
        collectionNamesList.add("test");
        final Set<String> collectionNames = new LinkedHashSet<String>(collectionNamesList);
        Collection<CollectionDTO> collectionDtos = new ArrayList<>()
        List<ManagedObjectDTO> managedObjectDTOs = new ArrayList<>()
        ManagedObjectDTO managedObjectDTO = new ManagedObjectDTO()
        if(!emptyfdnsList) {
            managedObjectDTO.setFdn("LTE02ERBS00001")
        }
        managedObjectDTOs.add(managedObjectDTO)
        CollectionDTO collectionDto=new CollectionDTO(userName,category,userName);
        collectionDto.setId("1")
        collectionDto.setElements(managedObjectDTOs)
        collectionDtos.add(collectionDto)
        contextService.getUserIdContextValue() >> 'administrator'
        topologyCollectionsService.getCollectionsByName(_,_) >> collectionDtos
        topologyCollectionsService.getCollectionByID(_,_) >> collectionDto
        nscsNodeUtility.getNodeNameFromFdn(_) >> "LTE02ERBS00001"
        properties.put("collection", collectionNamesList)
        when:
        def output = nscsInputNodeRetrievalUtility.getNodesReferenceListFromCollection(collectionNames)
        then:
        thrown (expected)
        where:
        userName  | expected                      |  emptyfdnsList | category
        "test" | InvalidCollectionNameException   |     false      |  Category.PRIVATE
        "test" | InvalidCollectionNameException   |     true      |   Category.PUBLIC
    }
}
