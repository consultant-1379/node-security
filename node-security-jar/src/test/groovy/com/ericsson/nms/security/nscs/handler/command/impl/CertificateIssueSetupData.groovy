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

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand.NscsPropertyCommandInvoker
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManager
import com.ericsson.nms.security.nscs.api.command.types.CertificateIssueCommand
import com.ericsson.nms.security.nscs.handler.CommandContext
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl
import com.ericsson.nms.security.nscs.node.certificate.validator.CertificateIssueValidator
import com.ericsson.nms.security.nscs.rtsel.utility.GetRtselConfigurationDetailsImpl
import com.ericsson.nms.security.nscs.rtsel.utility.RtselConfigurationDetailsResponseBuilder
import com.ericsson.nms.security.nscs.utilities.*
import com.ericsson.oss.itpf.common.flow.modeling.modelservice.typed.Path
import com.ericsson.oss.itpf.datalayer.dps.DataBucket
import com.ericsson.oss.itpf.datalayer.dps.stub.RuntimeConfigurableDps
import com.ericsson.oss.services.cm.cmshared.dto.CmObject
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse
import com.ericsson.oss.services.dto.JobStatusRecord
import com.ericsson.nms.security.nscs.api.enums.CertificateType
import com.ericsson.nms.security.nscs.api.model.NodeRef
import com.ericsson.nms.security.nscs.api.model.NodeReference
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.EnrollmentDetails
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.Nodes.Node
import com.ericsson.nms.security.nscs.cpp.ipsec.util.XmlOperatorUtils
import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse
import com.ericsson.nms.security.nscs.api.command.NscsCommandType
import com.ericsson.nms.security.nscs.enrollmentinfo.utility.FileConstants

import java.nio.file.*
import java.util.List
import java.util.Map

import com.ericsson.nms.security.nscs.data.*
import com.ericsson.oss.services.enums.JobGlobalStatusEnum
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler

import org.apache.commons.collections.map.HashedMap

import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService

import java.io.Serializable


/**
 * This class prepares setup data to initiate the CertificateIssueHandler
 *
 *  @author xkumkam
 *
 */
class CertificateIssueSetupData extends CdiSpecification{

	@Inject
	CertificateIssueCommand command

	@MockedImplementation
	CommandContext context

	@Inject
	CmResponse cmResponse

	@Inject
	CmObject cmObject

	@Inject
	CertificateIssueHandler certificateIssueHandler

	@Inject
	CertificateIssueValidator certificateIssueValidator;

	@Inject
	NscsCommandManager commandManager;

	@Inject
	NscsNodeUtility nscsNodeUtility

	@Inject
	TestSetupInitializer testSetupInitializer;

	@Inject
	RuntimeConfigurableDps runtimeDps

	@Inject
	JobStatusRecord jobStatusRecord

	@Inject
	NormalizableNodeReference normNodeRef

	@Inject
	NscsJobCacheHandler nscsJobCacheHandler

	@MockedImplementation
	NscsModelServiceImpl nscsModelServiceImpl

	@Inject
	EnrollmentDetails enrollDetails;

	@Inject
	XMLUnMarshallerUtility xmlUnMarshallerUtility;

	@Inject
	XmlOperatorUtils xmlOperatorUtils;

	@Inject
	XmlValidatorUtility xmlValidatorUtility;

	@MockedImplementation
	NodeReference nodeRef

	@MockedImplementation
	NscsCMReaderService nscsCMReaderService

	@MockedImplementation
	NscsCapabilityModelService nscsCapabilityModelService

	@Inject
	NscsCommandResponse nscsCommandResponse


	final protected Map<String, Object> commandMap = new HashMap<String, Object>()
	final Map<String, Object> properties = new HashMap()
	final String neType = "RadioNode"
	final String namespace = "OSS_NE_DEF"
	final String platformType = "null"
	final String syncStatus = "syncStatus"
	final String synch = "SYNCHRONIZED"
	Map<String,Object> attributes = new HashMap()
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
	 * This method is used to read the content from the input xml file for Certificate Issue command.
	 */

	def setcommand(final String certType, final String extcaOption){
		properties.put("certtype", certType);
		properties.put("extca", extcaOption);
		command.setCommandType(NscsCommandType.CERTIFICATE_ISSUE)
		NscsPropertyCommandInvoker commandInvokerValue = NscsPropertyCommandInvoker.CLI;
		command.setCommandInvokerValue(commandInvokerValue)
		command.setProperties(properties)
		commandManager.validateCertTypeValue(_) >> certType
		commandManager.isEnrollmentModeSupportedForNodeList(_, _) >> true
	}


	/**
	 * This method is used to read the content from the input xml file.
	 *
	 */
	def setFileData(final String filePath) {

		byte[] fileContent = testSetupInitializer.getFileContent(filePath)
		properties.put(FileConstants.FILE_URI, fileContent)
		final String propertyKey="file:"
	}

	/**
	 * This method is used to set the data for different node validations.
	 */

	def setDataForNodeExists(final String nodeStatus, final String nodeName, final String filePath){
		NodeReference nodeReference = new NodeRef(nodeName)
		nodeRef.getFdn() >> "NetworkElement=" + nodeName
		switch(nodeStatus){
			case 'validNode' :
				setNodeData("SYNCHRONIZED",nodeName)
				break
			case 'invalidNode' :
				setNodeData("UNSYNCHRONIZED",nodeName)
				break
			default :
				break
		}
	}

	/**
	 * This method is used to set the data for node
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
		nscsModelServiceImpl.isExtCAOperationSupported(_, _, _) >> true
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
