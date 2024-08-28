package com.ericsson.nms.security.nscs.handler.command.impl;

import static org.junit.Assert.*

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse
import com.ericsson.nms.security.nscs.api.command.types.GetTrustCertInstallStateCommand
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException
import com.ericsson.nms.security.nscs.api.model.NodeRef
import com.ericsson.nms.security.nscs.api.model.NodeReference
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService
import com.ericsson.nms.security.nscs.data.*
import com.ericsson.nms.security.nscs.data.ModelDefinition.CmFunction
import com.ericsson.nms.security.nscs.data.moget.MOGetServiceFactory
import com.ericsson.nms.security.nscs.data.moget.param.CertStateInfo
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.nms.security.nscs.handler.CommandContext
import com.ericsson.nms.security.nscs.logger.NscsLogger
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility
import com.ericsson.oss.services.cm.cmshared.dto.CmObject
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse

class GetTrustCertInstallStateHandlerTest extends CdiSpecification {
    private static final String NODE_NAME = "ERBS_001"
    private static final String FDN = "MeContext=" + NODE_NAME
    private static final NormalizableNodeReference nodeReference = MockUtils.createNormalizableNodeRef(NODE_NAME)
    private static final String warningMessageResponse=' [WARNING : The command with --certtype option will be deprecated in the future. Use --trustcategory property instead of --certtype]'

    @ObjectUnderTest
    GetTrustCertInstallStateHandler getTrustCertInstallStateHandler;

    @MockedImplementation
    private NscsLogger nscsLogger

    @MockedImplementation
    private NscsCapabilityModelService nscsCapabilityModelService

    @MockedImplementation
    private CommandContext commandContext

    @MockedImplementation
    private NscsCMReaderService reader

    @MockedImplementation
    private MOGetServiceFactory moGetServiceFactory;

    private List<NodeReference> nodes = new ArrayList<>()
    private List<String> nodeList = new ArrayList<>();
    private NodeRef nodeRef=new NodeRef(FDN)

    def setup(){
        nodeList.add(FDN);
        nodes.add(nodeRef)
        reader.getNormalizableNodeReference(new NodeRef(FDN)) >> { return nodeReference }
    }

    def "When everything is correct task should return success response"() {
        given:
        GetTrustCertInstallStateCommand command =new GetTrustCertInstallStateCommand()
        Map<String, Object> prop=new HashMap<>()
        prop.put("trustcategory", trustcategory)
        prop.put("nodelist", nodeList)
        if(certtype== null){
            command.getProperties().containsKey(GetTrustCertInstallStateCommand.CERT_TYPE_PROPERTY) >> false
        }else{
            prop.put('certtype', certtype)
        }
        command.setProperties(prop)
        reader.exists(_) >> true
        nscsCapabilityModelService.isCliCommandSupported(_, 'trust') >> true
        nscsCapabilityModelService.isCertificateManagementSupported(_) >> true
        nscsCapabilityModelService.isTrustCategoryTypeSupported(_, _) >> true
        Map<String, Object> cmAttrs=new HashMap<>()
        cmAttrs.put(CmFunction.SYNC_STATUS,ModelDefinition.CmFunction.SyncStatusValue.SYNCHRONIZED.name())
        CmObject cmObject = new CmObject()
        cmObject.setAttributes(cmAttrs)

        CmResponse cmResponse=new CmResponse()
        cmResponse.addCmObjects(Arrays.asList(cmObject))
        reader.getMOAttribute(_ , Model.NETWORK_ELEMENT.cmFunction.type(), Model.NETWORK_ELEMENT.cmFunction.namespace(), CmFunction.SYNC_STATUS) >> cmResponse
        CertStateInfo trustCertificateInfo = new CertStateInfo(nodeRef.getName())
        moGetServiceFactory.getTrustCertificateStateInfo(nodeRef, trustcategory) >> trustCertificateInfo
        when:
        NscsNameMultipleValueCommandResponse response =  getTrustCertInstallStateHandler.process(command, commandContext)
        then:
        println trustcategory
        println certtype
        println response.getAdditionalInformation()
        println response.getResponseTitle()
        boolean result= response.getAdditionalInformation().equals(expectedResult)
        println result
        assert result
        where:
        trustcategory   |certtype   | expectedResult
        null                |"OAM"     | warningMessageResponse
        null                |"IPSEC"    | warningMessageResponse
        "LAAD"            | null       | ""
        "IPSEC"            | null       | ""
        "OAM"            | null       | ""
    }

    def "Exception cases for getting trusted certificates of nodes"() {
        given:
        GetTrustCertInstallStateCommand command =new GetTrustCertInstallStateCommand()
        Map<String, Object> prop=new HashMap<>()
        prop.put("trustcategory", trustcategory)
        prop.put("certtype", certtype)
        prop.put("nodelist", nodeList)
        when:
        getTrustCertInstallStateHandler.process(command, commandContext);
        then:
        def error = thrown(expectedException)
        where:
        trustcategory   |certtype  | expectedException
        null            |"LAAD"    | InvalidArgumentValueException
        "LAADS"         |null      | InvalidArgumentValueException
        "LAADS"         |"dummyType"      | InvalidArgumentValueException
    }
}
