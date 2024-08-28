package com.ericsson.nms.security.nscs.handler.command.impl

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.command.NscsCommandType
import com.ericsson.nms.security.nscs.api.command.NscsMessageCommandResponse
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse
import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand
import com.ericsson.nms.security.nscs.api.command.types.CppIpSecCommand
import com.ericsson.nms.security.nscs.api.exception.IpSecActionException
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException
import com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.Nodes
import com.ericsson.nms.security.nscs.cpp.ipsec.util.CppIpSecStatusUtility
import com.ericsson.nms.security.nscs.cpp.ipsec.util.IpSecNodeValidatorUtility
import com.ericsson.nms.security.nscs.cpp.ipsec.util.XmlValidatorUtils
import com.ericsson.nms.security.nscs.handler.CommandContext
import com.ericsson.nms.security.nscs.util.FileUtil
import com.ericsson.nms.security.nscs.utilities.Constants
import com.ericsson.oss.services.dto.JobStatusRecord
import com.ericsson.oss.services.enums.JobGlobalStatusEnum
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler
import org.junit.Ignore
import spock.lang.Shared

import javax.inject.Inject

class CppIpSecHandlerFcTest extends CdiSpecification {

    private static final String ALL_VALID_NODES_FORMAT = "Successfully started a job for IPSEC operation."
    private static final String ALL_INVALID_NODES_FORMAT = "All input nodes are invalid, see error details in following table:"
    private static final String PARTIALLY_INVALID_NODES_FORMAT = "Some input nodes are invalid, see error details in following table:"
    private static final String GENERIC_EXCP_MSG = "Generic Exception"
    private static final Integer VLAN_ID = 100

    @ObjectUnderTest
    private CppIpSecHandler cppIpSecHandler

    @Inject
    CppIpSecCommand command

    @MockedImplementation
    private CommandContext context

    @MockedImplementation
    private FileUtil fileUtil

    @Inject
    private NscsJobCacheHandler nscsJobCacheHandler

    @MockedImplementation
    private XmlValidatorUtils xmlValidatorUtils

    @MockedImplementation
    private IpSecNodeValidatorUtility mockNodeValidatorUtil

    @ImplementationInstance
    IpSecNodeValidatorUtility ipSecNodeValidatorUtilityPartially = [
            validateNodes : { inputNodesList , validNodesList, invalidNodesErrorMap ->
                validNodesList.addAll(inputNodesList)
                return false
            }
    ] as IpSecNodeValidatorUtility

    @ImplementationInstance
    CppIpSecStatusUtility cppIpSecStatusUtilityMock = [
            getOamVlanId : { nodeRef ->
                return VLAN_ID
            }
    ] as CppIpSecStatusUtility

    @ImplementationInstance
    CppIpSecStatusUtility cppIpSecStatusUtilityMockGenExcp = [
            getOamVlanId : { nodeRef ->
                throw  new Exception(GENERIC_EXCP_MSG)
            }
    ] as CppIpSecStatusUtility

    @Inject
    CppIpSecStatusUtility cppIpSecStatusUtility

    @Shared
    private JobStatusRecord jobStatusRecord

    final protected Map<String, Object> commandMap = new HashMap<String, Object>()

    def setup() {
        UUID jobId = UUID.randomUUID()
        jobStatusRecord = new JobStatusRecord()
        jobStatusRecord.setGlobalStatus(JobGlobalStatusEnum.PENDING)
        jobStatusRecord.setUserId("user")
        jobStatusRecord.setJobId(jobId)

        setIpSecCommandData("validNode", "LTE02ERBS00009", "src/test/resources/SampleSingleNodeInputFile.xml")
    }

    @Override
    def addAdditionalInjectionProperties(final InjectionProperties injectionProperties) {}

    def "when command is correct and all node are valid it returns a proper response message"() {
        given:
        fileUtil.isValidFileExtension(_ as String, _ as String) >> true
        xmlValidatorUtils.validateXMLSchema(_ as String ) >> true
        nscsJobCacheHandler.insertJob(_ as NscsCommandType) >> jobStatusRecord
        mockNodeValidatorUtil.validateNodes(_ as List<Nodes.Node>, _ as List<Nodes.Node>, _ as Map<String, NscsServiceException>) >> true
        when:
        def response = (NscsMessageCommandResponse)cppIpSecHandler.process(command, context)
        then:
        response.getMessage().contains(ALL_VALID_NODES_FORMAT)
    }

    def "when command is correct and all node are invalid it returns a proper response message"() {
        given:
        fileUtil.isValidFileExtension(_ as String, _ as String) >> true
        xmlValidatorUtils.validateXMLSchema(_ as String ) >> true
        nscsJobCacheHandler.insertJob(_ as NscsCommandType) >> jobStatusRecord
        mockNodeValidatorUtil.validateNodes(_ as List<Nodes.Node>, _ as List<Nodes.Node>, _ as Map<String, NscsServiceException>) >> false
        when:
        def response = (NscsNameMultipleValueCommandResponse)cppIpSecHandler.process(command, context)
        then:
        response.additionalInformation.contains(ALL_INVALID_NODES_FORMAT)
    }

    def "when command is correct and node are partially valid it returns a proper response message"() {
        given:
        fileUtil.isValidFileExtension(_ as String, _ as String) >> true
        xmlValidatorUtils.validateXMLSchema(_ as String ) >> true
        nscsJobCacheHandler.insertJob(_ as NscsCommandType) >> jobStatusRecord
        cppIpSecHandler.ipSecNodeValidatorUtility = ipSecNodeValidatorUtilityPartially
        cppIpSecHandler.ipSecStatusUtility = cppIpSecStatusUtilityMock
        when:
        def response = (NscsNameMultipleValueCommandResponse)cppIpSecHandler.process(command, context)
        then:
        response.additionalInformation.contains(PARTIALLY_INVALID_NODES_FORMAT)
    }

    def "when the input xmlfile has null path it returns CommandSyntaxException "() {
        given:
        fileUtil.isValidFileExtension(_ as String, _ as String) >> true
        setIpSecCommandData("validNode", "LTE02ERBS00009", "")
        when:
        cppIpSecHandler.process(command, context)
        then:
        def e = thrown NscsServiceException
        e.getMessage().contains(NscsErrorCodes.SYNTAX_ERROR)
    }


    def "when the xmlfile doesn't exist it returns IOException "() {
        given:
        fileUtil.isValidFileExtension(_ as String, _ as String) >> true
        setIpSecCommandData("validNode", "LTE02ERBS00009", "src/test/resources/InputFileDoesntExist.xml")
        when:
        cppIpSecHandler.process(command, context)
        then:
        def e = thrown NscsServiceException
        e.getMessage().contains(NscsErrorCodes.SYNTAX_ERROR)
    }

    @Ignore
    def "when the xmlfile is empty it returns CommandSyntaxException "() {
        given:
        fileUtil.isValidFileExtension(_ as String, _ as String) >> true
        setIpSecCommandData("validNode", "LTE02ERBS00009", "src/test/resources/SampleInputFileEmpty.xml")
        when:
        cppIpSecHandler.process(command, context)
        then:
        def e = thrown NscsServiceException
        e.getMessage().contains(NscsErrorCodes.SYNTAX_ERROR)
    }

    def "when the xmlfile is not valid then it returns InvalidInputXMLFileException"() {
        given:
        fileUtil.isValidFileExtension(_ as String, _ as String) >> true
        xmlValidatorUtils.validateXMLSchema(_ as String ) >> false
        when:
        cppIpSecHandler.process(command, context)
        then:
        def e = thrown NscsServiceException
        e.getMessage() == NscsErrorCodes.INVALID_INPUT_XML_FILE
    }

    def "when the xmlfile has no xml extension it returns CommandSyntaxException "() {
        given:
        fileUtil.isValidFileExtension(_ as String, _ as String) >> false
        setIpSecCommandData("validNode", "LTE02ERBS00009", "src/test/resources/testFileAll.txt")
        when:
        cppIpSecHandler.process(command, context)
        then:
        def e = thrown NscsServiceException
        e.getMessage().contains(NscsErrorCodes.INVALID_FILE_TYPE_NOT_XML)
    }

    def "when generic exception raised"() {
        given:
        fileUtil.isValidFileExtension(_ as String, _ as String) >> true
        xmlValidatorUtils.validateXMLSchema(_ as String ) >> true
        nscsJobCacheHandler.insertJob(_ as NscsCommandType) >> jobStatusRecord
        cppIpSecHandler.ipSecNodeValidatorUtility = ipSecNodeValidatorUtilityPartially
        cppIpSecHandler.ipSecStatusUtility = cppIpSecStatusUtilityMockGenExcp
        when:
        cppIpSecHandler.process(command, context)
        then:
        def e = thrown IpSecActionException
        e.getCause().getMessage() == GENERIC_EXCP_MSG
    }

    /**
     * This method creates the input node list for laad distribute command.
     */
    def setIpSecCommandData(final String nodeStatus, final String nodeName,
                            final String filePath){
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
        NscsPropertyCommand.NscsPropertyCommandInvoker commandInvokerValue = NscsPropertyCommand.NscsPropertyCommandInvoker.CLI

        def fileName = new File(filePath)
        properties.put("xmlfile", filePath)
        properties.put(Constants.FILE_NAME, fileName.getName())
        properties.put(Constants.FILE_PATH, filePath)

        commandMap.put(Constants.FILE_NAME, fileName.getName())
        commandMap.put(Constants.FILE_PATH, filePath)

        command.setCommandType(NscsCommandType.CPP_IPSEC)
        command.setCommandInvokerValue(commandInvokerValue)
        command.setProperties(commandMap)
        command.setProperties(properties)
    }

//    byte[] getFileContent(final String filePath) {
//        byte[] fileContent = null
//        try {
//            final Path path = Paths.get(filePath)
//            fileContent = Files.readAllBytes(path)
//        }catch (Exception e) {
//        }
//        return fileContent
//    }
}