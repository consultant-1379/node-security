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
import com.ericsson.nms.security.nscs.api.command.types.ListNtpCommand
import com.ericsson.nms.security.nscs.api.model.NodeRef
import com.ericsson.nms.security.nscs.api.model.NodeReference
import com.ericsson.nms.security.nscs.capabilitymodel.service.*
import com.ericsson.nms.security.nscs.data.*
import com.ericsson.nms.security.nscs.data.ModelDefinition.MeContext
import com.ericsson.nms.security.nscs.data.moget.*
import com.ericsson.nms.security.nscs.data.moget.impl.CppMOGetServiceImpl
import com.ericsson.nms.security.nscs.data.moget.param.NtpServer
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.nms.security.nscs.enrollmentinfo.utility.FileConstants
import com.ericsson.nms.security.nscs.handler.CommandContext
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl
import com.ericsson.nms.security.nscs.util.FileUtil
import com.ericsson.nms.security.nscs.util.NtpConstants
import com.ericsson.nms.security.nscs.utilities.*
import com.ericsson.oss.itpf.datalayer.dps.DataBucket
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject
import com.ericsson.oss.itpf.datalayer.dps.stub.RuntimeConfigurableDps
import com.ericsson.oss.services.cm.cmreader.api.CmReaderService
import com.ericsson.oss.services.cm.cmshared.dto.CmObject
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse
import com.ericsson.oss.services.security.nscs.context.NscsContextService
import com.ericsson.oss.services.topologyCollectionsService.api.TopologyCollectionsEjbService
import com.ericsson.oss.services.topologyCollectionsService.dto.Category
import com.ericsson.oss.services.topologyCollectionsService.dto.MoTypeAttributesMapping
import com.ericsson.oss.services.topologyCollectionsService.dto.SavedSearchDTO
import com.ericsson.oss.services.topologySearchService.service.api.SearchExecutor
import com.ericsson.oss.services.topologySearchService.service.api.dto.NetworkExplorerResponse
import com.ericsson.oss.services.topologySearchService.service.datastructs.FixedSizeSet

/**
 * This class for list ntp key id's setup data
 * @author 1639556
 *
 */
public class ListNtpServerSetupData extends CdiSpecification{

    @Inject
    CmObject cmObject

    @Inject
    CmResponse cmResponse

    @Inject
    ListNtpCommand command

    @MockedImplementation
    CommandContext context

    @Inject
    NscsContextService contextService

    @Inject
    ManagedObject managedObject

    @Inject
    NodeReference nodeRef

    @Inject
    NormalizableNodeReference normNode

    @MockedImplementation
    NormalizableNodeReference normNodeRef

    @MockedImplementation
    NscsCMReaderService nscsMockCMReaderServ

    @Inject
    NscsNodeUtility nscsNodeUtility

    @Inject
    RuntimeConfigurableDps runtimeDps

    @Inject
    TestSetupInitializer testSetupInitializer

    @MockedImplementation
    CmReaderService reader;

    @MockedImplementation
    NscsModelServiceImpl nscsModelServiceImpl

    @MockedImplementation
    NscsCapabilityModelBean nscsCapabilityModelBean

    @MockedImplementation
    FileUtil fileUtil

    @Inject
    private NscsInputNodeRetrievalUtility nscsInputNodeRetrievalUtility;

    @MockedImplementation
    private NscsCapabilityModelService capabilityService

    @MockedImplementation
    private MOGetServiceFactory moGetServiceFactory


    @MockedImplementation
    private BeanManager beanManager;

    @MockedImplementation
    private Bean<?> bean;

    @MockedImplementation
    private CreationalContext creationalContext;

    @MockedImplementation
    private CppMOGetServiceImpl cppMOGetServiceImpl

    @MockedImplementation
    private NscsCapabilityModelService nscsCapabilityModelService;

    @Inject
    private TopologyCollectionsEjbService topologyCollectionsService;

    @Inject
    private SearchExecutor searchExecutor;
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

    final protected Map<String, Object> commandMap = new HashMap<String, Object>()
    Map<String,Object> attributes = new HashMap()
    Map<String,String> attributesList = new HashedMap()
    final String neType = "ERBS"
    final String namespace = "ERBS_NODE_MODEL"
    final String platformType = "CPP"
    final String syncStatus = "syncStatus"
    final String synch = "SYNCHRONIZED"
    /**
     * This method creates the input node list for ntp list command.
     */
    def ntpListCommandData(final String nodeStatus, final String nodeName , final String filePath){
        final Map<String, Object> properties = new HashMap()
        def fileName = new File(filePath)
        def fileContent = testSetupInitializer.getFileContent(filePath)
        commandMap.put(Constants.FILE_NAME, fileName.getName())
        commandMap.put(Constants.FILE_PATH, filePath)
        List<String> inputlist = new ArrayList()
        String invalidNode ="LTE02ERBS00022"
        String partialValidNodes = "partialValidNodes"
        String nodeList = "nodelist"
        inputlist.add(nodeName)
        properties.put(nodeList, inputlist)
        NscsPropertyCommandInvoker commandInvokerValue = NscsPropertyCommandInvoker.CLI
        command.setCommandType(NscsCommandType.NTP_LIST)
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
     * This method is used to convert the byte array file content to list for laad distribute command.
     */
    def readFileContent(final byte[] fileContent){

        List<String> result = Arrays.asList(new String(fileContent).split("\\n"));

        return result
    }
    /**
     * This method is used to set the data for different node validations.
     */
    def setDataForNodeExists(final String nodeStatus, final String nodeName, final String nodeType, final String keyId, final String serviceStatus, final String userLabel, final String ntpServerId,final String serverAddress){
        NodeReference nodeReference = new NodeRef(nodeName)
        nodeRef.getFdn() >> "NetworkElement=" + nodeName

        switch(nodeStatus){
            case 'validNode' :
                setNtpListCommand('SYNCHRONIZED', nodeName, nodeType, keyId, serviceStatus, userLabel, ntpServerId, serverAddress)
                break
            case 'InvalidNode' :
                setNtpListCommand('SYNCHRONIZED',  nodeName, nodeType, keyId, serviceStatus, userLabel, ntpServerId, serverAddress)
                break
            case 'partialValidNodes' :
                setNtpListCommand('SYNCHRONIZED',  nodeName, nodeType, keyId, serviceStatus, userLabel, ntpServerId, serverAddress)
                break
            case 'normNodeNull' :
                nscsMockCMReaderServ.getNormalizableNodeReference(nodeReference) >> null
                nscsMockCMReaderServ.exists(nodeRef.getFdn()) >> true
                break
            case 'isNodeSynchronized' :
                setNtpListCommand('UNSYNCHRONIZED',  nodeName, nodeType, keyId, serviceStatus, userLabel, ntpServerId, serverAddress)
                break
            case 'isNodeExists' :
                nscsMockCMReaderServ.getNormalizableNodeReference(nodeReference) >> normNodeRef
                normNodeRef.getNormalizedRef() >> nodeRef
                break
            default :
                break
        }
    }

    /**
     * This method is used to set the data for node synchronization.
     */
    def setNtpListCommand(final String syncStatus, final String nodeName, final String nodeType, final String keyId, final String serviceStatus, final String userLabel, final String ntpServerId, final String serverAddress) {

        MeContext ME_CONTEXT = new MeContext();
        final String[] requestedAttrsNtpServer = [
            NtpConstants.INSTALLED_NTP_KEY_IDS
        ];
        final String[] requestedAttrsNtpServiceStatus = [NtpConstants.SERVICE_STATUS];
        final String[] requestedAttrsNtpUserLabel = [NtpConstants.NTP_USER_LABEL];
        final String[] requestedAttrsNtpServerId = [NtpConstants.NTP_SERVER_ID];
        final String[] requestedAttrsNtpServerAddress = [
            NtpConstants.SERVER_ADDRESS_HEADER
        ];
        normNodeRef.getNormalizedRef() >> nodeRef
        NodeReference nodeReference = new NodeRef(nodeName)
        nscsMockCMReaderServ.getNormalizableNodeReference(nodeReference) >> normNodeRef
        nscsCapabilityModelService.getMirrorRootMo(_) >> ME_CONTEXT.managedElement
        nodeRef.getFdn() >> "NetworkElement=" + nodeName
        nodeRef.getName() >> nodeName
        nscsMockCMReaderServ.exists(_) >> true
        List<NtpServer> listNtpServerKeyIds = new ArrayList<>()
        NtpServer listNtpServerObj = new NtpServer()
        listNtpServerObj.setKeyId(keyId)
        listNtpServerObj.setServiceStatus(serviceStatus)
        listNtpServerObj.setUserLabel(userLabel)
        listNtpServerObj.setNtpServerId(ntpServerId)
        listNtpServerObj.setServerAddress(serverAddress)
        listNtpServerKeyIds.addAll(listNtpServerObj)
        moGetServiceFactory.validateNodeForNtp(normNodeRef) >> true
        moGetServiceFactory.listNtpServerDetails(normNodeRef) >> listNtpServerKeyIds
        List<CmObject> cmObjects = new ArrayList<CmObject>(0)
        if(syncStatus.equalsIgnoreCase(synch) | syncStatus.equalsIgnoreCase("UNSYNCHRONIZED")){
            cmResponse = new CmResponse()
            attributes.put(ModelDefinition.CmFunction.SYNC_STATUS, syncStatus)
            attributes.put(NtpConstants.INSTALLED_NTP_KEY_IDS,keyId)
            attributes.put(NtpConstants.SERVICE_STATUS,serviceStatus)
            attributes.put(NtpConstants.NTP_USER_LABEL,userLabel)
            attributes.put(NtpConstants.NTP_SERVER_ID,ntpServerId)
            attributes.put(NtpConstants.SERVER_ADDRESS_HEADER,serverAddress)
            cmObjects = createCmObjectsForNtpList(syncStatus, nodeName, attributes)
            cmResponse.addCmObjects(cmObjects)
            cmResponse.getCmObjects().iterator().next() >> cmObject
            attributes.putAll(cmObject.getAttributes()) >> attributes
        }else{

            cmResponse.addCmObjects(cmObjects)
        }
        reader.search(_, _) >> cmResponse
        nscsMockCMReaderServ.getMOAttribute(_,_,_,_) >> cmResponse
        nscsMockCMReaderServ.getMOAttribute(_, _, _, _,_) >> cmResponse
        nscsModelServiceImpl.isMoAttributeExists(_, _, _, _, _, _) >> true
        nscsMockCMReaderServ.getMOAttributes(_, _, _, _, _) >> cmResponse
        final List<NodeReference> inputNodes = new ArrayList<>();
        inputNodes.add(normNodeRef);
        nscsInputNodeRetrievalUtility.getNodeReferenceList(_) >> inputNodes
        normNodeRef.getFdn() >> nodeName
        normNodeRef.getName() >> nodeName
        def mirrorRootFdn = normNodeRef.getFdn()
        nscsNodeUtility.getSingleInstanceMoFdn(_, _, _, requestedAttrsNtpServer) >> keyId
        nscsNodeUtility.getSingleInstanceMoFdn(_, _, _, requestedAttrsNtpServiceStatus) >> serviceStatus
        nscsNodeUtility.getSingleInstanceMoFdn(_, _, _, requestedAttrsNtpUserLabel) >> userLabel
        nscsNodeUtility.getSingleInstanceMoFdn(_, _, _, requestedAttrsNtpServerId) >> ntpServerId
        nscsNodeUtility.getSingleInstanceMoFdn(_, _, _, requestedAttrsNtpServerAddress) >> serverAddress
        nscsMockCMReaderServ.getMos(_, _, _, _) >> cmResponse
        normNodeRef.getNeType() >> nodeType
    }

    /**
     * This method creates cmobjects used to validate the node synchronization.
     *
     */
    public List<CmObject> createCmObjectsForNtpList(final String syncStatus, final String nodeName, final Map<String,String> attributes) {

        final String namespace = "OSS_NE_CM_DEF"
        final int poId = 1111
        final String cmFunction = "CmFunction"

        final List<CmObject> cmObjects = new ArrayList<CmObject>(0)
        cmObject = new CmObject()
        cmObject.setFdn('MeContext='+nodeName+',ManagedElement=1,SystemFunctions=1,Security=1')
        cmObject.setName(nodeName)
        cmObject.setNamespace(namespace)
        cmObject.setPoId(poId)
        cmObject.setType(cmFunction)
        cmObject.setAttributes(attributes)
        cmObjects.add(cmObject)
        return cmObjects
    }
    /**
     * This method is used to read the content from the input text file for laad distribute command.
     */
    def setNodeFileData(final String filePath) {
        def fileContent = testSetupInitializer.getFileContent(filePath)
        commandMap.put(FileConstants.FILE_URI, filePath)

        final Map<String, Object> properties = new HashMap()
        properties.put("nodelist", readFileContent(fileContent))
        NscsPropertyCommandInvoker commandInvokerValue = NscsPropertyCommandInvoker.CLI
        command.setCommandType(NscsCommandType.NTP_LIST)
        command.setCommandInvokerValue(commandInvokerValue)
        command.setProperties(commandMap)
        command.setProperties(properties)
    }

    /**
     * This method creates the savedsearch for Ntp list command.
     */
    def setNtpListCmdForSavedSearch(final String nodeNameorSS,final String nodeStatus, final String nodeName,final String nodeType, final String keyId, final String serviceStatus, final String userLabel, final String ntpServerId, final String serverAddress){
        final Map<String, Object> properties = new HashMap()
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
        command.setCommandType(NscsCommandType.NTP_LIST)
        command.setCommandInvokerValue(commandInvokerValue)
        command.setProperties(commandMap)
        command.setProperties(properties)

        setDataForNodeExists(nodeStatus, nodeName, nodeType, keyId, serviceStatus, userLabel, ntpServerId, serverAddress)
    }
}
