/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.handler.command.impl;

import javax.inject.Inject

import org.apache.commons.collections.map.HashedMap

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.command.NscsCommandType
import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand.NscsPropertyCommandInvoker
import com.ericsson.nms.security.nscs.api.command.types.RtselCommand
import com.ericsson.nms.security.nscs.api.model.NodeRef
import com.ericsson.nms.security.nscs.api.model.NodeReference
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService
import com.ericsson.nms.security.nscs.data.*
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.nms.security.nscs.enrollmentinfo.utility.FileConstants
import com.ericsson.nms.security.nscs.handler.CommandContext
import com.ericsson.nms.security.nscs.rtsel.utility.GetRtselConfigurationDetailsImpl
import com.ericsson.nms.security.nscs.rtsel.utility.RtselConfigurationDetailsResponseBuilder
import com.ericsson.nms.security.nscs.rtsel.utility.RtselConstants
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility
import com.ericsson.oss.itpf.datalayer.dps.DataBucket
import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject
import com.ericsson.oss.itpf.datalayer.dps.stub.RuntimeConfigurableDps
import com.ericsson.oss.services.cm.cmshared.dto.CmObject
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse
import com.ericsson.oss.services.dto.JobStatusRecord
import com.ericsson.oss.services.enums.JobGlobalStatusEnum
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler
import com.ericsson.oss.services.security.nscs.context.NscsContextService
import com.ericsson.oss.services.topologyCollectionsService.api.TopologyCollectionsEjbService
import com.ericsson.oss.services.topologyCollectionsService.dto.Category;
import com.ericsson.oss.services.topologyCollectionsService.dto.CollectionDTO
import com.ericsson.oss.services.topologyCollectionsService.dto.ManagedObjectDTO
import com.ericsson.oss.services.topologyCollectionsService.dto.SavedSearchDTO
import com.ericsson.oss.services.topologySearchService.service.api.SearchExecutor
import com.ericsson.oss.services.topologySearchService.service.api.dto.MoTypeAttributesMapping
import com.ericsson.oss.services.topologySearchService.service.api.dto.NetworkExplorerResponse
import com.ericsson.oss.services.topologySearchService.service.datastructs.FixedSizeSet
/**
 * This class prepares setupdata to initiate the ActivateRtselHandler and DeactivateRtselHandler
 *
 *  @author xvadyas
 *
 */

public class RtselSetupData extends CdiSpecification {

    @Inject
    RtselCommand command

    @Inject
    RuntimeConfigurableDps runtimeDps

    @Inject
    ManagedObject managedObject

    @Inject
    CmResponse cmResponse

    @Inject
    DataPersistenceService dataPersistenceService

    @Inject
    NormalizableNodeReference normNodeRef

    @Inject
    NscsJobCacheHandler nscsJobCacheHandler

    @Inject
    CmObject cmObject

    @Inject
    JobStatusRecord jobStatusRecord

    @Inject
    TestSetupInitializer testSetupInitializer

    @Inject
    NscsNodeUtility nscsNodeUtility

    @MockedImplementation
    NodeReference nodeRef

    @MockedImplementation
    CommandContext context

    @Inject
    SavedSearchDTO savedSearchDTO

    @MockedImplementation
    NscsCMReaderService nscsCMReaderService

    @MockedImplementation
    NscsCapabilityModelService nscsCapabilityModelService

    @Inject
    NodeReference nodeReference

    @Inject
    RtselConfigurationDetailsResponseBuilder rtselConfigurationDetailsResponseBuilder

    @Inject
    GetRtselConfigurationDetailsImpl getRtselConfigurationDetailsImpl

    @Inject
    RtselConstants rtselConstants

    @Inject
    ManagedObject mo

    @Inject
    NscsContextService contextService

    @Inject
    TopologyCollectionsEjbService topologyCollectionsService;

    @Inject
    SearchExecutor searchExecutor;

    @Inject
    NscsCMReaderService nscsCmReaderService;

    final protected Map<String, Object> commandMap = new HashMap<String, Object>()
    Map<String,Object> attributes = new HashMap()
    Map<String,String> attributesList = new HashedMap()
    final String neType = "ERBS"
    final String namespace = "ERBS_NODE_MODEL"
    final String platformType = "CPP"
    final String syncStatus = "syncStatus"
    final String synch = "SYNCHRONIZED"
    final String extServProtocol = "TLS_OVER_TCP"
    final String extServerAddress = "10.10.10.1"
    final String serverName = "syslog1"


    /**
     * Customize the injection provider
     * 
     * */
    @Override
    public Object addAdditionalInjectionProperties(final InjectionProperties injectionProperties) {
        injectionProperties.autoLocateFrom('com.ericsson.nms.security.nscs.handler.command.impl')
    }

    /**
     * This method creates the JobId for response builder.
     */
    def setup(){
        runtimeDps = cdiInjectorRule.getService(RuntimeConfigurableDps.class)
        UUID jobId = UUID.randomUUID()
        jobStatusRecord.setGlobalStatus(JobGlobalStatusEnum.PENDING)
        jobStatusRecord.setUserId("user")
        jobStatusRecord.setJobId(jobId)
    }

    /**
     * This method is used to read the content from the input xml file for RTSEL ACTIVATE command.
     */
    def setActivateCommandData(final String filePath) {
        def fileContent = testSetupInitializer.getFileContent(filePath)
        commandMap.put(FileConstants.FILE_URI, fileContent)
        NscsPropertyCommandInvoker commandInvokerValue = NscsPropertyCommandInvoker.CLI
        command.setCommandType(NscsCommandType.RTSEL_ACTIVATE)
        command.setCommandInvokerValue(commandInvokerValue)
        command.setProperties(commandMap)
    }

    /**
     * This method creates the input node list for RTSEL DEACTIVATE command.
     */
    def setDeactivateCommandData(final String nodeStatus, final String nodeName){
        final Map<String, Object> properties = new HashMap()
        List<String> inputlist = new ArrayList()
        if(nodeStatus.equalsIgnoreCase('partialValidNodes')){
            inputlist.add("LTE02ERBS00001")
            inputlist.add("LTE02ERBS00002")
        }
        else{
            inputlist.add(nodeName)
        }
        properties.put("nodelist", inputlist)
        NscsPropertyCommandInvoker commandInvokerValue = NscsPropertyCommandInvoker.CLI
        command.setCommandType(NscsCommandType.RTSEL_DEACTIVATE)
        command.setCommandInvokerValue(commandInvokerValue)
        command.setProperties(commandMap)
        command.setProperties(properties)
        command.getNodes()
    }



    /**
     * This method is used to read the content from the input xml file for RTSEL DELETE command.
     */
    def setDeleteCommandData(final String filePath) {
        def fileContent = testSetupInitializer.getFileContent(filePath)
        commandMap.put(FileConstants.FILE_URI, fileContent)
        NscsPropertyCommandInvoker commandInvokerValue = NscsPropertyCommandInvoker.CLI
        command.setCommandType(NscsCommandType.RTSEL_DELETE)
        command.setCommandInvokerValue(commandInvokerValue)
        command.setProperties(commandMap)
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
            case 'validNodes' :
                setNodeDataforGet('SYNCHRONIZED', nodeName)
                break
            case 'unSynchronized' :
                setNodeDataforGet('UNSYNCHRONIZED', nodeName)
                break
            case 'partialValidNodes' :
                setNodeData('SYNCHRONIZED', nodeName)
                break
            case 'normNodeNull' :
                nscsCMReaderService.getNormalizableNodeReference(nodeReference) >> null
                nscsCMReaderService.exists(nodeRef.getFdn()) >> true
                break
            case 'invalidNodeName' :
                nscsCMReaderService.getNormalizableNodeReference(nodeReference) >> normNodeRef
                break
            case 'isNodeExists' :
                nscsCMReaderService.getNormalizableNodeReference(nodeReference) >> normNodeRef
                normNodeRef.getNormalizedRef() >> nodeRef
                break
            case 'isCertificateSupportedForNode' :
                nscsCMReaderService.getNormalizableNodeReference(nodeReference) >> normNodeRef
                normNodeRef.getNormalizedRef() >> nodeRef
                nscsCMReaderService.exists(nodeRef.getFdn()) >> true
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
        nscsCMReaderService.getNormalizableNodeReference(nodeReference) >> normNodeRef
        nodeRef.getFdn() >> "NetworkElement=" + nodeName
        nscsCMReaderService.exists(_) >> true
        nscsCapabilityModelService.isCertificateManagementSupported(normNodeRef) >> true
        nodeRef.getName() >> nodeName
        List<HashMap<String,Object>> serverFromNode =new ArrayList<HashMap<String,Object>>()
        List<CmObject> cmObjects = new ArrayList<CmObject>(0)
        if(syncStatus.equalsIgnoreCase(synch)){
            cmResponse = new CmResponse()
            attributes.put(ModelDefinition.CmFunction.SYNC_STATUS, syncStatus)
            Map<String,Object> attributes1=new HashMap()
            attributes1.put("extServProtocol", extServProtocol)
            attributes1.put("extServerAddress", extServerAddress)
            attributes1.put("serverName", serverName)
            List<Map<String,Object>> attribute3=new ArrayList<Map<String,Object>>()
            attribute3.add(attributes1)
            attributes.put(ModelDefinition.RealTimeSecLog.EXT_SERVER_LIST_CONFIG,attribute3)
            cmObjects = testSetupInitializer.createCmObjects(syncStatus, nodeName, attributes)
            cmResponse.addCmObjects(cmObjects)
            cmResponse.getCmObjects().iterator().next() >> cmObject
            attributes.putAll(cmObject.getAttributes()) >> attributes
            serverFromNode.add(attributes.get(ModelDefinition.RealTimeSecLog.EXT_SERVER_LIST_CONFIG))
            attributes.get(_) >> serverFromNode.add(attributes)
        }else{

            cmResponse.addCmObjects(cmObjects)
        }
        nscsCMReaderService.getMOAttribute(_,_,_,_) >> cmResponse
        normNodeRef.getFdn() >> nodeName
        def mirrorRootFdn = normNodeRef.getFdn()
        nscsNodeUtility.getSingleInstanceMoFdn(_,_,_,_,_) >> cmObject.getFdn()
        nscsCMReaderService.getMos(_, _, _, _) >> cmResponse
        normNodeRef.getNeType() >> neType
        nscsCapabilityModelService.isCliCommandSupported(normNodeRef, _) >> true
        nscsJobCacheHandler.insertJob(_) >> jobStatusRecord
    }

    /**
     * This method prepares command data
     * */
    def setcommand(final def nodeName ){
        final Map<String, Object> properties = new HashMap()
        final LinkedList<String> nodeList = new LinkedList()
        nodeList.add(nodeName)
        properties.put("nodelist", nodeList)
        command.setCommandType(NscsCommandType.RTSEL_GET)
        NscsPropertyCommandInvoker commandInvokerValue = NscsPropertyCommandInvoker.CLI;
        command.setCommandInvokerValue(commandInvokerValue)
        command.setProperties(properties)
        command.getNodes()
    }

    /**
     * This method creates MO hierarchy
     * */
    def getManagedObjectfromnscsCMReaderService(final def nodeName) {

        def neFdn = "NetworkElement=" + nodeName
        def meContextFdn = "MeContext="+nodeName
        def managedElementFdn = meContextFdn+",ManagedElement=1"
        def securityFunctionFdn = neFdn+",SecurityFunction=1"
        def networkElementSecurityFdn = securityFunctionFdn+",NetworkElementSecurity=1"
        def cmFunctionFdn = neFdn+",CmFunction=1"
        def neAttrMap = [neType:neType, platformType:platformType, ossPrefix:meContextFdn]
        def neMo = runtimeDps.addManagedObject().withFdn(neFdn).namespace("OSS_NE_DEF").addAttributes(neAttrMap).build();
        def meContextAttrMap = [neType:neType, platformType:platformType]
        def meContextMo = runtimeDps.addManagedObject().withFdn(meContextFdn).namespace("OSS_TOP").addAttributes(meContextAttrMap).build();
        def managedElementMo = runtimeDps.addManagedObject().withFdn(managedElementFdn).parent(meContextMo).build();
        def systemFunctionsMo = runtimeDps.addManagedObject().withFdn("SystemFunctions=1").namespace(namespace).parent(managedElementMo).build();
        def securityMo = runtimeDps.addManagedObject().withFdn("Security=1").namespace(namespace).parent(systemFunctionsMo).build();
        def realTimeSecLogMo = runtimeDps.addManagedObject().withFdn("RealTimeSecLog=1").namespace(namespace).parent(securityMo).build();
        neMo.addAssociation("anypo", realTimeSecLogMo)
        neMo.addAssociation("nodeRootRef", meContextMo)
    }

    /**
     * This method is used to set the data for node synchronization.
     */
    def setNodeDataforGet(final String syncStatus, final String nodeName) {
        normNodeRef.getNormalizedRef() >> nodeRef
        NodeReference nodeReference = new NodeRef(nodeName)
        nscsCMReaderService.getNormalizableNodeReference(nodeReference) >> normNodeRef
        nodeRef.getFdn() >> "NetworkElement=" + nodeName
        nscsCMReaderService.exists(_) >> true
        nscsCapabilityModelService.isCertificateManagementSupported(normNodeRef) >> true
        nodeRef.getName() >> nodeName

        List<HashMap<String,Object>> serverFromNode =new ArrayList<HashMap<String,Object>>()
        List<CmObject> cmObjects = new ArrayList<CmObject>(0)
        if(syncStatus.equalsIgnoreCase(synch)){

            cmResponse = new CmResponse()
            attributes.put(ModelDefinition.CmFunction.SYNC_STATUS, syncStatus)
            Map<String,Object> attributes1=new HashMap()
            attributes1.put("extServProtocol", extServProtocol)
            attributes1.put("extServerAddress", extServerAddress)
            attributes1.put("serverName", serverName)
            List<Map<String,Object>> attribute3=new ArrayList<Map<String,Object>>()
            attribute3.add(attributes1)
            attributes.put(ModelDefinition.RealTimeSecLog.EXT_SERVER_LIST_CONFIG,attribute3)
            attributes.put(rtselConstants.FEATURESTATE,"")
            attributes.put(rtselConstants.EXT_SERVER_LOGLEVEL,"")
            attributes.put(rtselConstants.CONN_TIMEOUT,"")
            attributes.put(rtselConstants.EXT_SERVER_APPNAME,"")
            attributes.put(rtselConstants.STATUS,"")
            attributes.put(rtselConstants.NOT_APPLICABLE,"")
            cmObjects = testSetupInitializer.createCmObjects(syncStatus, nodeName, attributes)
            cmResponse.addCmObjects(cmObjects)

            cmResponse.getCmObjects().iterator().next() >> cmObject

            attributes.putAll(cmObject.getAttributes()) >> attributes

            serverFromNode.add(attributes.get(ModelDefinition.RealTimeSecLog.EXT_SERVER_LIST_CONFIG))

            attributes.get(_) >> serverFromNode.add(attributes)
        } else {
            cmResponse.addCmObjects(cmObjects)
        }
        nscsCMReaderService.getMOAttribute(_,_,_,_) >> cmResponse
        normNodeRef.getFdn() >> nodeName
        def mirrorRootFdn = normNodeRef.getFdn()
        nscsNodeUtility.getSingleInstanceMoFdn(_,_,_,_,_) >> cmObject.getFdn()
        nscsCMReaderService.getMos(_, _, _, _) >> cmResponse
        normNodeRef.getNeType() >> neType
        nscsCapabilityModelService.isCliCommandSupported(normNodeRef, _) >> true
    }

    /**
     * This method creates the savedsearch for RTSEL DEACTIVATE command.
     */
    def setDeactivateCommandDataForSS(final String nodeNameorSSorCO){
        final Map<String, Object> properties = new HashMap()
        List<String> inputlist = new ArrayList()
        List<String> savedSearchNamesList = new ArrayList()
        List<String> collectionNamesList = new ArrayList()
        Set<String> attributes= new HashSet<String>()
        Set<MoTypeAttributesMapping> attributeMappings=new HashSet<MoTypeAttributesMapping>()
        Collection<SavedSearchDTO> savedSearchDtos = new ArrayList<SavedSearchDTO>()
        savedSearchNamesList.add(nodeNameorSSorCO)
        SavedSearchDTO ssDto=new SavedSearchDTO("test","ENM",Category.PUBLIC,"Query");
        savedSearchDtos.add(ssDto)
        contextService.getUserIdContextValue() >> 'userID'
        topologyCollectionsService.getSavedSearchesByName(_,_) >> savedSearchDtos
        CmObject cmObj=new CmObject()
        cmObj.setFdn("LTE02ERBS00001")
        FixedSizeSet<CmObject> cmObjSet=new FixedSizeSet<CmObject>(10)
        cmObjSet.add(cmObj)
        NetworkExplorerResponse neR=new NetworkExplorerResponse(10,10,cmObjSet,attributes,attributeMappings)
        searchExecutor.search(_,_,_) >> neR
        properties.put("savedsearch", savedSearchNamesList)
        NscsPropertyCommandInvoker commandInvokerValue = NscsPropertyCommandInvoker.CLI
        command.setCommandType(NscsCommandType.RTSEL_DEACTIVATE)
        command.setCommandInvokerValue(commandInvokerValue)
        command.setProperties(commandMap)
        command.setProperties(properties)
        command.getSavedSearchNames()
    }

    /**
     * This method creates the collection for RTSEL DEACTIVATE command.
     */
    def setDeactivateCommandDataForCO(final String CO){
        final Map<String, Object> properties = new HashMap()
        List<String> inputlist = new ArrayList()
        List<String> collectionNamesList = new ArrayList()
        List<String> savedSearchNamesList = new ArrayList()
        Set<String> attributes= new HashSet<String>()
        Set<MoTypeAttributesMapping> attributeMappings=new HashSet<MoTypeAttributesMapping>()
        Collection<CollectionDTO> collectionDtos = new ArrayList<SavedSearchDTO>()
        List<ManagedObjectDTO> managedObjectDTOs = new ArrayList<ManagedObjectDTO>()
        ManagedObjectDTO managedObjectDTO = new ManagedObjectDTO()
        managedObjectDTO.setFdn("LTE02ERBS00001")
        managedObjectDTOs.add(managedObjectDTO)
        collectionNamesList.add("test")
        CollectionDTO collectionDto=new CollectionDTO("test",Category.PUBLIC,"administrator");
        collectionDto.setId("1")
        collectionDto.setElements(managedObjectDTOs)
        collectionDtos.add(collectionDto)
        contextService.getUserIdContextValue() >> 'administrator'
        topologyCollectionsService.getCollectionsByName(_,_) >> collectionDtos
        topologyCollectionsService.getCollectionByID(_,_) >> collectionDto
        nscsNodeUtility.getNodeNameFromFdn(_) >> "LTE02ERBS00001"
        properties.put("collection", collectionNamesList)
        NscsPropertyCommandInvoker commandInvokerValue = NscsPropertyCommandInvoker.CLI
        command.setCommandType(NscsCommandType.RTSEL_DEACTIVATE)
        command.setCommandInvokerValue(commandInvokerValue)
        command.setProperties(commandMap)
        command.setProperties(properties)
        command.getCollectionNames()
    }
    /**
     * This method creates the Negative savedsearch data for RTSEL DEACTIVATE command.
     */
    def setDeactivateCommandDataForSSNegative(final String savedSeachName){
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
        command.setCommandType(NscsCommandType.RTSEL_DEACTIVATE)
        command.setCommandInvokerValue(commandInvokerValue)
        command.setProperties(commandMap)
        command.setProperties(properties)
        command.getSavedSearchNames()
    }
    /**
     * This method creates the Negative collection data for RTSEL DEACTIVATE command.
     */
    def setDeactivateCommandDataForCONegative(final String collectionName){
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
            topologyCollectionsService.getCollectionByID(_,_) >> collectionDto
        }
        NscsPropertyCommandInvoker commandInvokerValue = NscsPropertyCommandInvoker.CLI
        command.setCommandType(NscsCommandType.RTSEL_DEACTIVATE)
        command.setCommandInvokerValue(commandInvokerValue)
        command.setProperties(commandMap)
        command.setProperties(properties)
        command.getCollectionNames()
    }
}
