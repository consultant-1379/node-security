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
package com.ericsson.nms.security.nscs.handler.command.impl;

import java.security.cert.X509Certificate
import java.util.List
import java.util.Map

import javax.inject.Inject
import javax.xml.bind.*

import org.apache.commons.collections.map.HashedMap

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.*
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.command.NscsCommandType
import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand.NscsPropertyCommandInvoker
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManager
import com.ericsson.nms.security.nscs.api.command.types.TrustDistributeCommand
import com.ericsson.nms.security.nscs.api.command.types.TrustRemoveCommand
import com.ericsson.nms.security.nscs.api.model.NodeRef
import com.ericsson.nms.security.nscs.api.model.NodeReference
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerIF
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService
import com.ericsson.nms.security.nscs.data.*
import com.ericsson.nms.security.nscs.data.ModelDefinition.*
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.nms.security.nscs.handler.CommandContext
import com.ericsson.nms.security.nscs.handler.validation.impl.TrustValidator
import com.ericsson.nms.security.nscs.manager.*
import com.ericsson.nms.security.nscs.utilities.*
import com.ericsson.oss.itpf.security.pki.common.model.CertificateAuthority
import com.ericsson.oss.itpf.security.pki.common.model.EntityInfo
import com.ericsson.oss.itpf.security.pki.manager.model.EntityCategory
import com.ericsson.oss.itpf.security.pki.manager.model.EntityType
import com.ericsson.oss.itpf.security.pki.manager.model.entities.CAEntity
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity
import com.ericsson.oss.services.cm.cmshared.dto.CmObject
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse
import com.ericsson.oss.services.dto.JobStatusRecord
import com.ericsson.oss.services.enums.JobGlobalStatusEnum
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler

/**
 * This class prepares setup data to distribute external ca trusted certificates
 *
 *  @author xkumkam
 *
 */

public class TrustSetupData extends CdiSpecification{

    @Inject
    NscsJobCacheHandler nscsJobCacheHandler

    @MockedImplementation
    CommandContext context

    @Inject
    CmResponse cmResponse

    @Inject
    CmObject cmObject

    @MockedImplementation
    NscsPkiEntitiesManagerIF nscsPkiManager;

    @MockedImplementation
    NodeReference nodeRef

    @MockedImplementation
    NscsCommandManager nscsCommandManager

    @MockedImplementation
    NscsCapabilityModelService nscsCapabilityModelService;

    @MockedImplementation
    NormalizableNodeReference normNodeRef

    @MockedImplementation
    NscsCMReaderService nscsCMReaderService

    @Inject
    NscsNodeUtility nscsNodeUtility

    @Inject
    JobStatusRecord jobStatusRecord

    @Inject
    TrustDistributeCommand command

    @Inject
    TrustRemoveCommand trustRemoveCommand

    @Inject
    TestSetupInitializer testSetupInitializer

    @Inject
    TrustValidator trustValidator;

    @Inject
    XmlValidatorUtility xmlValidatorUtility

    @Inject
    XMLUnMarshallerUtility xmlUnMarshallerUtility

    final protected Map<String, Object> commandMap = new HashMap<String, Object>()
    Map<String,Object> attributes = new HashMap()
    final String neType = "ERBS"
    final String extServProtocol = "TLS_OVER_TCP"
    final String extServerAddress = "10.10.10.1"
    final String serverName = "syslog1"
    final Map<String, List<X509Certificate>> trustsMap = new HashedMap<>()

    /**
     * Customize the injection provider
     *
     * */
    @Override
    public Object addAdditionalInjectionProperties(final InjectionProperties injectionProperties) {
        injectionProperties.autoLocateFrom('com.ericsson.nms.security.nscs.handler.command.impl')
        injectionProperties.autoLocateFrom('com.ericsson.nms.security.nscs.manager')
    }

    def setupJobStatusRecord(){
        UUID jobId = UUID.randomUUID()
        jobStatusRecord.setGlobalStatus(JobGlobalStatusEnum.PENDING)
        jobStatusRecord.setUserId("user")
        jobStatusRecord.setJobId(jobId)
        nscsJobCacheHandler.insertJob(_) >> jobStatusRecord
    }

    def setCommandData(final String trustCategory) {
        command.setCommandType(NscsCommandType.TRUST_DISTRIBUTE)
        NscsPropertyCommandInvoker commandInvokerValue = NscsPropertyCommandInvoker.CLI
        command.setCommandInvokerValue(commandInvokerValue)
        List<String> inputlist = new ArrayList()
        inputlist.add("LTE02ERBS00001")
        commandMap.put("nodelist", inputlist)
        commandMap.put("trustcategory", trustCategory)
        command.setProperties(commandMap)
    }

    def setTrustRemoveCommandData(final String trustCategory) {
        trustRemoveCommand.setCommandType(NscsCommandType.TRUST_REMOVE)
        NscsPropertyCommandInvoker commandInvokerValue = NscsPropertyCommandInvoker.CLI
        trustRemoveCommand.setCommandInvokerValue(commandInvokerValue)
        List<String> inputlist = new ArrayList()
        inputlist.add("LTE02ERBS00001")
        commandMap.put(TrustRemoveCommand.NODE_LIST_PROPERTY, inputlist)
        commandMap.put(TrustRemoveCommand.TRUST_CATEGORY_PROPERTY, trustCategory)
        commandMap.put(TrustRemoveCommand.ISDN_PROPERTY, "CN=ENM_Infrastructure_CA,O=ERICSSON,C=SE,OU=BUCI_DUAC_NAM")
        commandMap.put(TrustRemoveCommand.SERIALNUMBER_PROPERTY, "12345")
        trustRemoveCommand.setProperties(commandMap)
    }

    def setTrustRemoveCommandDataForCertType(final String trustCategory) {
        trustRemoveCommand.setCommandType(NscsCommandType.TRUST_REMOVE)
        NscsPropertyCommandInvoker commandInvokerValue = NscsPropertyCommandInvoker.CLI
        trustRemoveCommand.setCommandInvokerValue(commandInvokerValue)
        List<String> inputlist = new ArrayList()
        inputlist.add("LTE02ERBS00001")
        commandMap.put(TrustRemoveCommand.NODE_LIST_PROPERTY, inputlist)
        commandMap.put(TrustRemoveCommand.CERT_TYPE_PROPERTY, trustCategory)
        commandMap.put(TrustRemoveCommand.ISDN_PROPERTY, "CN=ENM_Infrastructure_CA,O=ERICSSON,C=SE,OU=BUCI_DUAC_NAM")
        commandMap.put(TrustRemoveCommand.SERIALNUMBER_PROPERTY, "12345")
        trustRemoveCommand.setProperties(commandMap)
    }

    def setTrustRemoveCommandDataWithCaName(final String trustCategory) {
        trustRemoveCommand.setCommandType(NscsCommandType.TRUST_REMOVE)
        NscsPropertyCommandInvoker commandInvokerValue = NscsPropertyCommandInvoker.CLI
        trustRemoveCommand.setCommandInvokerValue(commandInvokerValue)
        List<String> inputlist = new ArrayList()
        inputlist.add("LTE02ERBS00001")
        commandMap.put(TrustRemoveCommand.NODE_LIST_PROPERTY, inputlist)
        commandMap.put(TrustRemoveCommand.TRUST_CATEGORY_PROPERTY, trustCategory)
        commandMap.put(TrustRemoveCommand.CA_PROPERTY, "ENM_OAM_CA")
        commandMap.put(TrustRemoveCommand.SERIALNUMBER_PROPERTY, "12345")
        trustRemoveCommand.setProperties(commandMap)
    }

    def setCommandDataForPartialSuccess(final String trustCategory) {
        command.setCommandType(NscsCommandType.TRUST_DISTRIBUTE)
        NscsPropertyCommandInvoker commandInvokerValue = NscsPropertyCommandInvoker.CLI
        command.setCommandInvokerValue(commandInvokerValue)
        List<String> inputlist = new ArrayList()
        inputlist.add("LTE02ERBS00001")
        inputlist.add("Node1234")
        commandMap.put("nodelist", inputlist)
        commandMap.put("trustcategory", trustCategory)
        command.setProperties(commandMap)
    }

    def setTrustRemoveCommandDataForPartialSuccessCase(final String trustCategory) {
        trustRemoveCommand.setCommandType(NscsCommandType.TRUST_REMOVE)
        NscsPropertyCommandInvoker commandInvokerValue = NscsPropertyCommandInvoker.CLI
        trustRemoveCommand.setCommandInvokerValue(commandInvokerValue)
        List<String> inputlist = new ArrayList()
        inputlist.add("LTE02ERBS00001")
        inputlist.add("Node1234")
        commandMap.put(TrustRemoveCommand.NODE_LIST_PROPERTY, inputlist)
        commandMap.put(TrustRemoveCommand.TRUST_CATEGORY_PROPERTY, trustCategory)
        commandMap.put(TrustRemoveCommand.ISDN_PROPERTY, "CN=ENM_Infrastructure_CA,O=ERICSSON,C=SE,OU=BUCI_DUAC_NAM")
        commandMap.put(TrustRemoveCommand.SERIALNUMBER_PROPERTY, "12345")
        trustRemoveCommand.setProperties(commandMap)
    }

    def setCertTypeCommandData(final String trustCategory) {
        command.setCommandType(NscsCommandType.TRUST_DISTRIBUTE)
        NscsPropertyCommandInvoker commandInvokerValue = NscsPropertyCommandInvoker.CLI
        command.setCommandInvokerValue(commandInvokerValue)
        List<String> inputlist = new ArrayList()
        inputlist.add("LTE02ERBS00001")
        commandMap.put("nodelist", inputlist)
        commandMap.put("certtype", trustCategory)
        command.setProperties(commandMap)
    }

    def setCommandDataWithNodeAndCA(final String trustCategory, final String caName) {
        command.setCommandType(NscsCommandType.TRUST_DISTRIBUTE)
        NscsPropertyCommandInvoker commandInvokerValue = NscsPropertyCommandInvoker.CLI
        command.setCommandInvokerValue(commandInvokerValue)
        List<String> inputlist = new ArrayList()
        inputlist.add("LTE02ERBS00001")
        commandMap.put("nodelist", inputlist)
        commandMap.put("trustcategory", trustCategory)
        commandMap.put("ca", caName)
        command.setProperties(commandMap)
    }

    def setCommandDataWithOnlyCA(final String trustCategory, final String caName) {
        command.setCommandType(NscsCommandType.TRUST_DISTRIBUTE)
        NscsPropertyCommandInvoker commandInvokerValue = NscsPropertyCommandInvoker.CLI
        command.setCommandInvokerValue(commandInvokerValue)
        commandMap.put("trustcategory", trustCategory)
        commandMap.put("ca", caName)
        command.setProperties(commandMap)
    }

    def setValidNodes() {
        trustValidator.validateNodesForTrust(_,_,_,_,_) >> true
    }

    def setNodes() {
        setNodeData("SYNCHRONIZED","LTE02ERBS00001")
    }

    def setPartialSuccessNodes() {
        setNodeData("SYNCHRONIZED","LTE02ERBS00001")
    }

    def setValidNodesForEntity() {
        trustValidator.validateNodesFromEntitiesForTrust(_,_,_,_,_) >> true
    }

    def setEntityNameAvailable() {
        nscsPkiManager.isEntityNameAvailable(_,_) >> true
        nscsPkiManager.isExtCaNameAvailable(_) >> true
    }

    def setTrustCertificates(){
        CAEntity caEntity = new CAEntity()
        caEntity.setType(EntityType.CA_ENTITY)
        CertificateAuthority certificateAuthority = new CertificateAuthority()
        certificateAuthority.setId(1234)
        certificateAuthority.setName("ENM_OAM_CA")
        caEntity.setCertificateAuthority(certificateAuthority)
        nscsPkiManager.getCAEntity(_) >> caEntity
        nscsPkiManager.getCAsTrusts() >> trustsMap;
    }

    def setEntityList(final String caName) {
        Entity entity = new Entity();
        EntityInfo entityInfo = new EntityInfo();
        entityInfo.setName(caName)
        entity.setEntityInfo(entityInfo)
        EntityCategory entityCategory = new EntityCategory();
        entityCategory.setName("NODE-OAM");
        entity.setCategory(entityCategory);
        List<Entity> entityList = new ArrayList();
        entityList.add(entity);
        nscsPkiManager.getEntities() >> entityList
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
        nscsCapabilityModelService.isCertTypeSupported(_,_) >> true
        nscsCapabilityModelService.isTrustCategoryTypeSupported(_, _) >> true
        nodeRef.getName() >> nodeName
        List<HashMap<String,Object>> serverFromNode =new ArrayList<HashMap<String,Object>>()
        List<CmObject> cmObjects = new ArrayList<CmObject>(0)
        if(syncStatus.equalsIgnoreCase(syncStatus)){
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
}
