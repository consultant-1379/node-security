/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2019
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.handler.command.impl

import javax.enterprise.context.spi.CreationalContext
import javax.enterprise.inject.spi.Bean
import javax.enterprise.inject.spi.BeanManager
import javax.inject.Inject

import org.apache.commons.collections.map.HashedMap

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.command.NscsCommandType
import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand.NscsPropertyCommandInvoker
import com.ericsson.nms.security.nscs.api.command.types.NtpConfigureCommand
import com.ericsson.nms.security.nscs.api.model.NodeRef
import com.ericsson.nms.security.nscs.api.model.NodeReference
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService
import com.ericsson.nms.security.nscs.data.ModelDefinition
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.nms.security.nscs.data.moget.MOGetServiceFactory
import com.ericsson.nms.security.nscs.data.moget.impl.CppMOGetServiceImpl
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.nms.security.nscs.enrollmentinfo.utility.FileConstants
import com.ericsson.nms.security.nscs.handler.CommandContext
import com.ericsson.nms.security.nscs.ntp.utility.NTPConfigureResponseBuilder
import com.ericsson.nms.security.nscs.util.ExportCacheItemsHolder
import com.ericsson.nms.security.nscs.util.FileUtil
import com.ericsson.nms.security.nscs.utilities.Constants
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility
import com.ericsson.oss.itpf.datalayer.dps.DataBucket
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject
import com.ericsson.oss.itpf.datalayer.dps.stub.RuntimeConfigurableDps
import com.ericsson.oss.services.cm.cmshared.dto.CmObject
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse
import com.ericsson.oss.services.security.nscs.context.NscsContextService
import com.ericsson.oss.services.topologyCollectionsService.api.TopologyCollectionsEjbService
import com.ericsson.oss.services.topologyCollectionsService.dto.*
import com.ericsson.oss.services.topologySearchService.service.api.SearchExecutor
import com.ericsson.oss.services.topologySearchService.service.api.dto.NetworkExplorerResponse
import com.ericsson.oss.services.topologySearchService.service.datastructs.FixedSizeSet

/**
 * This class prepares setup data to initiate the NtpConfigureHandler
 *
 * @author xjangop
 *
 */

class NtpConfigureData extends CdiSpecification{


    @Inject
    CmObject cmObject

    @Inject
    CmResponse cmResponse

    @Inject
    NtpConfigureCommand command

    @MockedImplementation
    CommandContext context

    @Inject
    NscsContextService contextService

    @Inject
    ManagedObject managedObject

    @MockedImplementation
    NodeReference nodeRef

    @Inject
    NormalizableNodeReference normNodeRef


    @MockedImplementation
    NscsCapabilityModelService nscsCapabilityModelService

    @Inject
    NscsCMReaderService nscsCmReaderService

    @MockedImplementation
    NscsCMReaderService nscsMockCMReaderServ

    @Inject
    NscsNodeUtility nscsNodeUtility

    @Inject
    RuntimeConfigurableDps runtimeDps

    @Inject
    SavedSearchDTO savedSearchDTO

    @Inject
    SearchExecutor searchExecutor;

    @Inject
    TestSetupInitializer testSetupInitializer

    @Inject
    TopologyCollectionsEjbService topologyCollectionsService;

    @MockedImplementation
    ExportCacheItemsHolder exportCacheItemsHolder;

    @Inject
    FileUtil fileUtil

    @MockedImplementation
    private MOGetServiceFactory moGetServiceFactory;

    @MockedImplementation
    private BeanManager beanManager;

    @MockedImplementation
    private Bean<?> bean;

    @MockedImplementation
    private CreationalContext creationalContext;

    @MockedImplementation
    private CppMOGetServiceImpl cppMOGetServiceImpl

    @Inject
    private NTPConfigureResponseBuilder ntpConfigureResponseBuilder;

    final protected Map<String, Object> commandMap = new HashMap<String, Object>()
    Map<String,Object> attributes = new HashMap()
    Map<String,String> attributesList = new HashedMap()
    final String neType = "ERBS"
    final String namespace = "ERBS_NODE_MODEL"
    final String platformType = "CPP"
    final String syncStatus = "syncStatus"
    final String synch = "SYNCHRONIZED"
    final String currentServiceState = "currentServiceState"

    /**
     * This method creates the runtime dps.
     */
    def setup(){
        runtimeDps = cdiInjectorRule.getService(RuntimeConfigurableDps.class)
    }

    /**
     * Customize the injection provider
     *
     * */
    @Override
    public Object addAdditionalInjectionProperties(final InjectionProperties injectionProperties) {
        injectionProperties.autoLocateFrom('com.ericsson.nms.security.nscs.handler.command.impl')
    }

    def setNtpConfigureCommandData(final String nodeStatus,final String nodeName){
        final Map<String,Object> properties = new HashMap();
        String invalidNode ="LTE02ERBS00022"
        String partialValidNodes = "partialValidNodes"
        String ndoeList = "nodelist"

        List<String> inputlist = new ArrayList()
        if(nodeStatus.equalsIgnoreCase(partialValidNodes)){
            inputlist.add(nodeName)
            inputlist.add(invalidNode)
        }
        else{
            inputlist.add(nodeName)
        }
        properties.put(ndoeList, inputlist)
        NscsPropertyCommandInvoker commandInvokerValue = NscsPropertyCommandInvoker.CLI
        command.setCommandType(NscsCommandType.NTP_CONFIGURE)
        command.setCommandInvokerValue(commandInvokerValue)
        command.setProperties(commandMap)
        command.setProperties(properties)
    }
    /**
     * This method creates the managed object for the given node.
     */
    def setDataForManagedObject(final String nodeName) {
        managedObject = runtimeDps.addManagedObject().withFdn("NetworkElement=" + nodeName).build()
        managedObject.setAttribute("neType", neType)
        managedObject.setAttribute("namespace", namespace)
        managedObject.setAttribute("platformType",platformType)
        managedObject.setAttribute(ModelDefinition.CmFunction.SYNC_STATUS, syncStatus)

        DataBucket liveBucket = runtimeDps.build().getLiveBucket()
        ManagedObject MeContext = liveBucket.getMibRootBuilder().namespace("OSS_TOP").name(nodeName).type("MeContext").create()
        ManagedObject myRootObject = liveBucket.getMibRootBuilder().namespace("OSS_NE_DEF").type("NetworkElement").parent(MeContext).create()
        ManagedObject managedElement = liveBucket.getMibRootBuilder().namespace("OSS_NE_CM_DEF").type("CmFunction").parent(myRootObject).create()
        managedObject.addAssociation("association", managedElement)
    }

    /**
     * This method is used to set the data for different node validations.
     */
    def setDataForNodeExists(final String nodeStatus, final String nodeName){
        NodeReference nodeReference = new NodeRef(nodeName)
        nodeRef.getFdn() >> "NetworkElement=" + nodeName
        switch(nodeStatus){
            case 'validNode' :
                setNodeData('SYNCHRONIZED', nodeName)
                break
            case 'partialValidNodes' :
                setNodeData('SYNCHRONIZED', nodeName)
                break
            case 'normNodeNull' :
                nscsMockCMReaderServ.getNormalizableNodeReference(nodeReference) >> null
                nscsMockCMReaderServ.exists(nodeRef.getFdn()) >> true
                break
            case 'isNodeExists' :
                normNodeRef.getNormalizedRef() >> nodeRef
                nscsMockCMReaderServ.getNormalizableNodeReference(nodeReference) >> normNodeRef
                nscsMockCMReaderServ.exists(nodeRef.getFdn()) >> false
                break
            case 'isNodeSynchronized' :
                setNodeData('UNSYNCHRONIZED', nodeName)
                break
            default :
                break
        }
    }

    /**
     * This method is used to set the data for node synchronization.
     */
    def setNodeData(final String syncStatus, final String nodeName) {

        normNodeRef.getNormalizedRef() >> nodeRef
        NodeReference nodeReference = new NodeRef(nodeName)
        nscsMockCMReaderServ.getNormalizableNodeReference(nodeReference) >> normNodeRef
        nodeRef.getFdn() >> "NetworkElement=" + nodeName
        nscsMockCMReaderServ.exists(_) >> true
        nodeRef.getName() >> nodeName
        moGetServiceFactory.validateNodeForNtp(normNodeRef) >> true
        List<CmObject> cmObjects = new ArrayList<CmObject>(0)
        if(syncStatus.equalsIgnoreCase(synch)){
            cmResponse = new CmResponse()
            attributes.put(ModelDefinition.CmFunction.SYNC_STATUS, syncStatus)
            Map<String,Object> attributes1=new HashMap()
            List<Map<String,Object>> attribute3=new ArrayList<Map<String,Object>>()
            attribute3.add(attributes1)
            cmObjects = testSetupInitializer.createCmObjects(syncStatus, nodeName, attributes)
            cmResponse.addCmObjects(cmObjects)
            cmResponse.getCmObjects().iterator().next() >> cmObject
            attributes.putAll(cmObject.getAttributes()) >> attributes
        }else{

            cmResponse.addCmObjects(cmObjects)
        }
        nscsMockCMReaderServ.getMOAttribute(_,_,_,_) >> cmResponse
        normNodeRef.getFdn() >> nodeName
        def mirrorRootFdn = normNodeRef.getFdn()
        nscsNodeUtility.getSingleInstanceMoFdn(_,_,_,_,_) >> cmObject.getFdn()
        nscsMockCMReaderServ.getMos(_, _, _, _) >> cmResponse
        normNodeRef.getNeType() >> neType
    }

    /**
     * This method creates the savedsearch for Ntp configure command.
     */
    def setNtpConfigureCmdForSavedSearch(final String nodeNameorSS,final String nodeStatus, final String nodeName){
        final Map<String, Object> properties = new HashMap()
        List<String> inputlist = new ArrayList()
        List<String> savedSearchNamesList = new ArrayList()
        Set<String> attributes= new HashSet<String>()
        Set<MoTypeAttributesMapping> attributeMappings=new HashSet<MoTypeAttributesMapping>()
        Collection<SavedSearchDTO> savedSearchDtos = new ArrayList<SavedSearchDTO>()
        savedSearchNamesList.add(nodeNameorSS)
        SavedSearchDTO ssDto=new SavedSearchDTO("test","ENM",Category.PUBLIC,"Query");
        savedSearchDtos.add(ssDto)
        contextService.getUserIdContextValue() >> 'userID'
        topologyCollectionsService.getSavedSearchesByName(_,_) >> savedSearchDtos
        CmObject cmObj=new CmObject()
        cmObj.setFdn(nodeName)
        FixedSizeSet<CmObject> cmObjSet=new FixedSizeSet<CmObject>(10)
        cmObjSet.add(cmObj)
        NetworkExplorerResponse neR=new NetworkExplorerResponse(10,10,cmObjSet,attributes,attributeMappings)
        searchExecutor.search(_,_,_) >> neR
        properties.put("savedsearch", savedSearchNamesList)
        NscsPropertyCommandInvoker commandInvokerValue = NscsPropertyCommandInvoker.CLI
        command.setCommandType(NscsCommandType.NTP_CONFIGURE)
        command.setCommandInvokerValue(commandInvokerValue)
        command.setProperties(commandMap)
        command.setProperties(properties)

        setDataForNodeExists(nodeStatus, nodeName)
    }

    /**
     * This method creates the negative savedsearch data for Ntp configure command.
     */
    def setNtpConfigureCmdForSavedSearchNegative(final String savedSeachName){
        final Map<String, Object> properties = new HashMap()
        List<String> inputlist = new ArrayList()
        List<String> savedSearchNamesList = new ArrayList()
        savedSearchNamesList.add(savedSeachName)
        properties.put("savedsearch", savedSearchNamesList)
        if(savedSeachName.contains('Privatesearch')){
            Collection<SavedSearchDTO> savedSearchDtos = new ArrayList<SavedSearchDTO>()
            savedSearchNamesList.add(savedSeachName)
            SavedSearchDTO ssDto=new SavedSearchDTO("test","ENM",Category.PRIVATE,"Query");
            savedSearchDtos.add(ssDto)
            contextService.getUserIdContextValue() >> 'userID'
            topologyCollectionsService.getSavedSearchesByName(_,_) >> savedSearchDtos
        }
        NscsPropertyCommandInvoker commandInvokerValue = NscsPropertyCommandInvoker.CLI
        command.setCommandType(NscsCommandType.NTP_CONFIGURE)
        command.setCommandInvokerValue(commandInvokerValue)
        command.setProperties(commandMap)
        command.setProperties(properties)
    }

    /**
     * This method creates the collection for Ntp configure command.
     */
    def setNtpConfigureCmdForCollection(final String colName,final String nodeStatus, final String nodeName){
        final Map<String, Object> properties = new HashMap()
        List<String> inputlist = new ArrayList()
        List<String> collectionNamesList = new ArrayList()
        Set<String> attributes= new HashSet<String>()
        Collection<CollectionDTO> collectionDtos = new ArrayList<SavedSearchDTO>()
        List<ManagedObjectDTO> managedObjectDTOs = new ArrayList<ManagedObjectDTO>()
        ManagedObjectDTO managedObjectDTO = new ManagedObjectDTO()
        managedObjectDTO.setFdn("LTE02ERBS00001")
        managedObjectDTOs.add(managedObjectDTO)
        collectionNamesList.add(colName)
        CollectionDTO collectionDto=new CollectionDTO("test",Category.PUBLIC,"administrator");
        collectionDto.setId("1")
        collectionDto.setElements(managedObjectDTOs)
        collectionDtos.add(collectionDto)
        contextService.getUserIdContextValue() >> 'administrator'
        topologyCollectionsService.getCollectionsByName(_,_) >> collectionDtos
        topologyCollectionsService.getCollectionByID(_,_) >> collectionDto
        nscsNodeUtility.getNodeNameFromFdn(_) >> nodeName
        properties.put("collection", collectionNamesList)
        NscsPropertyCommandInvoker commandInvokerValue = NscsPropertyCommandInvoker.CLI
        command.setCommandType(NscsCommandType.NTP_CONFIGURE)
        command.setCommandInvokerValue(commandInvokerValue)
        command.setProperties(commandMap)
        command.setProperties(properties)

        setDataForNodeExists(nodeStatus,nodeName)
    }

    /**
     * This method creates the negative collection data for Ntp configure command.
     */
    def setNtpConfigureCmdForCollectionNegative(final String collectionName){
        final Map<String, Object> properties = new HashMap()
        List<String> inputlist = new ArrayList()
        CmObject cmObjResponse=new CmObject()
        List<String> collectionNamesList = new ArrayList()
        Map<String,String> moAttributes=new HashMap<String,String>()
        CmResponse cmResponseforPoIds= new CmResponse()
        Collection<CollectionDTO> collectionDtos = new ArrayList<SavedSearchDTO>()
        collectionNamesList.add(collectionName)
        properties.put("collection", collectionNamesList)
        if(collectionName.contains('Invalidcollection')){
            CollectionDTO collectionDto=new CollectionDTO("test",Category.PRIVATE,"ENM");
            collectionDto.setId("1")
            collectionDtos.add(collectionDto)
            contextService.getUserIdContextValue() >> 'userID'
            topologyCollectionsService.getCollectionsByName(_,_) >> collectionDtos
            moAttributes.put(Constants.ATTRIBUTE_USERID, "userID")
            moAttributes.put(Constants.ATTRIBUTE_CATEGORY, Constants.PRIVATE_CATEGORY)
            moAttributes.put(Constants.ATTRIBUTE_MO_LIST_COLLECTION, Arrays.asList(1,2,3))
            cmObjResponse.setAttributes(moAttributes)
            cmResponseforPoIds.addCmObjects(Arrays.asList(cmObjResponse))
            nscsCmReaderService.getPosByPoIds(_) >> cmResponseforPoIds
        }
        NscsPropertyCommandInvoker commandInvokerValue = NscsPropertyCommandInvoker.CLI
        command.setCommandType(NscsCommandType.NTP_CONFIGURE)
        command.setCommandInvokerValue(commandInvokerValue)
        command.setProperties(commandMap)
        command.setProperties(properties)
    }


    /**
     * This method is used to read the content from the input text file for Ntp configure command.
     */
    def setNodeFileData(final String filePath) {
        def fileContent = testSetupInitializer.getFileContent(filePath)
        commandMap.put(FileConstants.FILE_URI, filePath)

        final Map<String, Object> properties = new HashMap()
        properties.put("nodelist", readFileContent(fileContent))
        NscsPropertyCommandInvoker commandInvokerValue = NscsPropertyCommandInvoker.CLI
        command.setCommandType(NscsCommandType.NTP_CONFIGURE)
        command.setCommandInvokerValue(commandInvokerValue)
        command.setProperties(commandMap)
        command.setProperties(properties)
    }

    /**
     * This method is used to convert the byte array file content to list for Ntp configure command.
     */
    def readFileContent(final byte[] fileContent){

        List<String> result = Arrays.asList(new String(fileContent).split("\\n"));

        return result
    }

    /**
     * This method is used to read the content from the input xml file for Ntp configure command.
     */
    def setNodeXmlFileData(final String filePath) {
        def fileName = new File(filePath)
        def fileContent = testSetupInitializer.getFileContent(filePath)
        commandMap.put(Constants.FILE_NAME, fileName.getName())
        commandMap.put(Constants.FILE_PATH, filePath)
        NscsPropertyCommandInvoker commandInvokerValue = NscsPropertyCommandInvoker.CLI
        command.setCommandType(NscsCommandType.NTP_CONFIGURE)
        command.setCommandInvokerValue(commandInvokerValue)
        command.setProperties(commandMap)
    }
}
