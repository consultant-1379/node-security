/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.handler.command.impl

import javax.inject.Inject

import org.apache.commons.collections.map.HashedMap

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.command.NscsCommandType
import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand.NscsPropertyCommandInvoker
import com.ericsson.nms.security.nscs.api.command.types.CppSecurityLevelCommand
import com.ericsson.nms.security.nscs.api.model.NodeRef
import com.ericsson.nms.security.nscs.api.model.NodeReference
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService
import com.ericsson.nms.security.nscs.cpp.seclevel.util.CppGetResponseBuilder
import com.ericsson.nms.security.nscs.cpp.seclevel.util.CppGetSecurityLevelConstants
import com.ericsson.nms.security.nscs.cpp.seclevel.util.CppGetSecurityLevelDetails
import com.ericsson.nms.security.nscs.data.ModelDefinition
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.nms.security.nscs.data.ModelDefinition.MeContext
import com.ericsson.nms.security.nscs.data.ModelDefinition.Security
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.nms.security.nscs.handler.CommandContext
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl
import com.ericsson.nms.security.nscs.utilities.NormalizedNodeUtils
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility
import com.ericsson.oss.itpf.datalayer.dps.DataBucket
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject
import com.ericsson.oss.itpf.datalayer.dps.stub.RuntimeConfigurableDps
import com.ericsson.oss.itpf.sdk.context.ContextService
import com.ericsson.oss.services.cm.cmreader.api.CmReaderService
import com.ericsson.oss.services.cm.cmshared.dto.CmObject
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse



/**
 * This class prepares setupdata to initiate the CppGetSecurityLevelHandler
 *
 * @author tcsgoja
 *
 */
public class CPPGetSecurityLevelSetupData extends CdiSpecification {

    @Inject
    CmObject cmObject

    @Inject
    CmResponse cmResponse

    @Inject
    CppSecurityLevelCommand command

    @MockedImplementation
    CommandContext context

    @Inject
    ContextService contextService

    @Inject
    ManagedObject managedObject

    @Inject
    NodeReference nodeRef

    @Inject
    NormalizableNodeReference normNode

    @MockedImplementation
    NormalizableNodeReference normNodeRef

    @MockedImplementation
    NscsCapabilityModelService nscsCapabilityModelService

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

    @Inject
    private CppGetResponseBuilder cppGetResponseBuilder;

    @Inject
    private CppGetSecurityLevelDetails cppGetSecurityLevelDetails;

    @Inject
    private NormalizedNodeUtils cppGetSecurityLevelUtility;

    final protected Map<String, Object> commandMap = new HashMap<String, Object>()
    Map<String,Object> attributes = new HashMap()
    Map<String,String> attributesList = new HashedMap()
    final String neType = "ERBS"
    final String namespace = "ERBS_NODE_MODEL"
    final String platformType = "CPP"
    final String syncStatus = "syncStatus"
    final String synch = "SYNCHRONIZED"
    public static final String USER_AUTHENTIACATION_AND_AUTHORIZATION = "userAuthenticationAndAuthorization"

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

    /**
     * This method creates the input node list for SL Get command.
     */
    def slGetCommandData(final String nodeStatus, final String nodeName){
        final Map<String, Object> properties = new HashMap()
        String invalidNode ="LTE02ERBS00022"
        String partialValidNodes = "partialValidNodes"
        String nodeList = "nodelist"

        List<String> inputlist = new ArrayList()
        if(nodeStatus.equalsIgnoreCase(partialValidNodes)){
            inputlist.add(nodeName)
            inputlist.add(invalidNode)
        }
        else{
            inputlist.add(nodeName)
        }
        properties.put(nodeList, inputlist)
        NscsPropertyCommandInvoker commandInvokerValue = NscsPropertyCommandInvoker.CLI
        command.setCommandType(NscsCommandType.CPP_GET_SL)
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
    def setDataForNodeExists(final String nodeStatus, final String nodeName, final String nodeType, final String securityLevel, final String localAAStatus, final boolean isCliCmdSupported){
        NodeReference nodeReference = new NodeRef(nodeName)
        nodeRef.getFdn() >> "NetworkElement=" + nodeName

        switch(nodeStatus){
            case 'validNode' :
                setSlGetCommand('SYNCHRONIZED', nodeName, nodeType, securityLevel, localAAStatus, isCliCmdSupported)
                break
            case 'partialValidNodes' :
                setSlGetCommand('SYNCHRONIZED',  nodeName, nodeType, securityLevel, localAAStatus, isCliCmdSupported)
                break
            case 'normNodeNull' :
                nscsMockCMReaderServ.getNormalizableNodeReference(nodeReference) >> null
                nscsMockCMReaderServ.exists(nodeRef.getFdn()) >> true
                break
            case 'isNodeSynchronized' :
                setSlGetCommand('UNSYNCHRONIZED',  nodeName, nodeType, securityLevel, localAAStatus, isCliCmdSupported)
                break
            default :
                break
        }
    }

    /**
     * This method is used to set the data for node synchronization.
     */
    def setSlGetCommand(final String syncStatus, final String nodeName, final String nodeType, final String securityLevel, final String localAAStatus, final boolean isCliCmdSupported) {

        MeContext ME_CONTEXT = new MeContext();

        final String[] requestedLocalAAAttributes = { CppGetSecurityLevelConstants.USER_AUTHENTICATION_AND_AUTHORIZATION };
        normNodeRef.getNormalizedRef() >> nodeRef
        NodeReference nodeReference = new NodeRef(nodeName)
        nscsMockCMReaderServ.getNormalizableNodeReference(nodeReference) >> normNodeRef
        nodeRef.getFdn() >> "NetworkElement=" + nodeName
        nscsMockCMReaderServ.exists(_) >> true
        nscsCapabilityModelService.isCertificateManagementSupported(normNodeRef) >> true
        nscsCapabilityModelService.getMirrorRootMo(_) >> ME_CONTEXT.managedElement
        nodeRef.getName() >> nodeName
        List<CmObject> cmObjects = new ArrayList<CmObject>(0)
        if(syncStatus.equalsIgnoreCase(synch) | syncStatus.equalsIgnoreCase("UNSYNCHRONIZED")){
            cmResponse = new CmResponse()
            attributes.put(ModelDefinition.CmFunction.SYNC_STATUS, syncStatus)
            attributes.put(USER_AUTHENTIACATION_AND_AUTHORIZATION,localAAStatus)
            attributes.put(Security.OPERATIONAL_SECURITY_LEVEL,securityLevel)
            cmObjects = createCmObjectsForCPPGetSecurityLevel(syncStatus, nodeName, attributes)
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
        final List<NormalizableNodeReference> normNodesList = new ArrayList<>();
        normNodesList.add(normNodeRef);
        cppGetSecurityLevelUtility.getNormalizedNodes(_, _) >> normNodesList
        normNodeRef.getFdn() >> nodeName
        normNodeRef.getName() >> nodeName
        def mirrorRootFdn = normNodeRef.getFdn()
        nscsNodeUtility.getSingleInstanceMoFdn(_, _, _, requestedLocalAAAttributes) >> localAAStatus
        nscsMockCMReaderServ.getMos(_, _, _, _) >> cmResponse
        normNodeRef.getNeType() >> nodeType
        nscsCapabilityModelService.isCliCommandSupported(normNodeRef, _) >> isCliCmdSupported
    }

    /**
     * This method creates cmobjects used to validate the node synchronization.
     *
     */
    public List<CmObject> createCmObjectsForCPPGetSecurityLevel(final String syncStatus, final String nodeName, final Map<String,String> attributes) {

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
}
