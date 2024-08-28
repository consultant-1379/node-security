package com.ericsson.nms.security.nscs.handler.command.impl

import javax.inject.Inject

import org.apache.commons.collections.map.HashedMap

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse
import com.ericsson.nms.security.nscs.api.command.NscsCommandType
import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand.NscsPropertyCommandInvoker
import com.ericsson.nms.security.nscs.api.command.types.NtpRemoveCommand
import com.ericsson.nms.security.nscs.api.model.NodeRef
import com.ericsson.nms.security.nscs.api.model.NodeReference
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService
import com.ericsson.nms.security.nscs.data.ModelDefinition
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.nms.security.nscs.data.moget.MOGetServiceFactory
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.nms.security.nscs.enrollmentinfo.utility.FileConstants
import com.ericsson.nms.security.nscs.handler.CommandContext
import com.ericsson.nms.security.nscs.util.FileUtil
import com.ericsson.nms.security.nscs.utilities.Constants
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility
import com.ericsson.oss.itpf.datalayer.dps.DataBucket
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject
import com.ericsson.oss.itpf.datalayer.dps.stub.RuntimeConfigurableDps
import com.ericsson.oss.itpf.sdk.context.ContextService
import com.ericsson.oss.services.cm.cmshared.dto.CmObject
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse
import com.ericsson.oss.services.topologyCollectionsService.api.TopologyCollectionsEjbService
import com.ericsson.oss.services.topologySearchService.service.api.SearchExecutor

class NtpRemoveSetupData extends CdiSpecification{

    @Inject
    NtpRemoveCommand command

    @MockedImplementation
    CommandContext context

    @Inject
    NscsCommandResponse nscsCommandResponse

    @Inject
    ManagedObject managedObject

    @MockedImplementation
    FileUtil fileUtil

    @Inject
    RuntimeConfigurableDps runtimeDps

    @MockedImplementation
    NodeReference nodeRef

    @Inject
    NormalizableNodeReference normNodeRef

    @MockedImplementation
    NscsCMReaderService nscsMockCMReaderServ

    @MockedImplementation
    NscsCapabilityModelService nscsCapabilityModelService

    @MockedImplementation
    MOGetServiceFactory moGetServiceFactory

    @Inject
    CmResponse cmResponse

    @Inject
    TestSetupInitializer testSetupInitializer

    @Inject
    CmObject cmObject

    @Inject
    NscsNodeUtility nscsNodeUtility

    @Inject
    NscsCMReaderService nscsCmReaderService

    @Inject
    ContextService contextService

    @Inject
    TopologyCollectionsEjbService topologyCollectionsService

    @Inject
    SearchExecutor searchExecutor

    final protected Map<String, Object> commandMap = new HashMap<String, Object>()
    final Map<String, Object> properties = new HashMap()
    Map<String,Object> attributes = new HashMap()
    Map<String,String> attributesList = new HashedMap()
    final String neType = "ERBS"
    final String namespace = "ERBS_NODE_MODEL"
    final String platformType = "CPP"
    final String syncStatus = "syncStatus"
    final String synch = "SYNCHRONIZED"
    final String currentServiceState = "currentServiceState"

    def setCommandData(final List<String> keyIdList,final String nodeName) {
        command.setCommandType(NscsCommandType.NTP_REMOVE)
        NscsPropertyCommandInvoker commandInvokerValue = NscsPropertyCommandInvoker.CLI
        command.setCommandInvokerValue(commandInvokerValue)
        commandMap.put("nodename", nodeName)
        commandMap.put("keyid", keyIdList)
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
    def setDataForNodeExists(final String nodeStatus, final String nodeName, final boolean fmAlarmState, final boolean isCliCmdSupported){
        NodeReference nodeReference = new NodeRef(nodeName)
        nodeRef.getFdn() >> "NetworkElement=" + nodeName
        switch(nodeStatus){
            case 'validNode' :
                setNodeData('SYNCHRONIZED', nodeName, fmAlarmState, isCliCmdSupported)
                break
            case 'partialValidNodes' :
                setNodeData('SYNCHRONIZED', nodeName, fmAlarmState, isCliCmdSupported)
                break
            case 'normNodeNull' :
                nscsMockCMReaderServ.getNormalizableNodeReference(nodeReference) >> null
                nscsMockCMReaderServ.exists(nodeRef.getFdn()) >> true
                break
            case 'isNodeExists' :
                nscsMockCMReaderServ.getNormalizableNodeReference(nodeReference) >> normNodeRef
                normNodeRef.getNormalizedRef() >> nodeRef
                break
            case 'isCertSupportedForNode' :
                nscsMockCMReaderServ.getNormalizableNodeReference(nodeReference) >> normNodeRef
                normNodeRef.getNormalizedRef() >> nodeRef
                nscsMockCMReaderServ.exists(nodeRef.getFdn()) >> true
                break
            case 'isNodeSynchronized' :
                setNodeData('UNSYNCHRONIZED', nodeName, fmAlarmState, isCliCmdSupported)
                break
            default :
                break
        }
    }

    /**
     * This method is used to set the data for node synchronization.
     */
    def setNodeData(final String syncStatus, final String nodeName, final boolean fmAlarmState, final boolean isCliCmdSupported) {

        normNodeRef.getNormalizedRef() >> nodeRef
        NodeReference nodeReference = new NodeRef(nodeName)
        nscsMockCMReaderServ.getNormalizableNodeReference(nodeReference) >> normNodeRef
        nodeRef.getFdn() >> "NetworkElement=" + nodeName
        nscsMockCMReaderServ.exists(_) >> true
        nscsCapabilityModelService.isCertificateManagementSupported(normNodeRef) >> true
        nodeRef.getName() >> nodeName
        moGetServiceFactory.validateNodeForNtpMO(_)>> true
        List<CmObject> cmObjects = new ArrayList<CmObject>(0)
        if(syncStatus.equalsIgnoreCase(synch)){
            cmResponse = new CmResponse()
            attributes.put(ModelDefinition.CmFunction.SYNC_STATUS, syncStatus)

            if(fmAlarmState){
                attributes.put(currentServiceState, "IN_SERVICE")
            }
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
        nscsCapabilityModelService.isCliCommandSupported(normNodeRef, _) >> isCliCmdSupported
    }

    /**
     * This method is used to read the content from the input xml file for ntp remove command.
     */
    def setCommandDataForxmlFile(final String filePath) {

        def fileContent = testSetupInitializer.getFileContent(filePath)
        commandMap.put(FileConstants.FILE_URI, fileContent)
        commandMap.put("xmlfile", "file:ntpremove.xml")
        commandMap.put(Constants.FILE_PATH, filePath)
        commandMap.put(Constants.FILE_NAME,'ntpremove.xml')
        NscsPropertyCommandInvoker commandInvokerValue = NscsPropertyCommandInvoker.CLI
        command.setCommandType(NscsCommandType.NTP_REMOVE)
        command.setCommandInvokerValue(commandInvokerValue)
        command.setProperties(commandMap)
    }

    /**
     * This method is used to read the content from the input xml file for ntp remove command.
     */
    def setCommandDataForInvalidxmlFile(final String filePath) {

        def fileContent = testSetupInitializer.getFileContent(filePath)
        commandMap.put(FileConstants.FILE_URI, fileContent)
        commandMap.put("xmlfile", "file:ntpremove.xml")
        commandMap.put(Constants.FILE_PATH, filePath)
        commandMap.put(Constants.FILE_NAME, "ntpremove.xml")
        NscsPropertyCommandInvoker commandInvokerValue = NscsPropertyCommandInvoker.CLI
        command.setCommandType(NscsCommandType.NTP_REMOVE)
        command.setCommandInvokerValue(commandInvokerValue)
        command.setProperties(commandMap)
    }
}
