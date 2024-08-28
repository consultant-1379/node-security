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

import javax.inject.Inject
import spock.lang.Shared
import spock.lang.Unroll
import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes
import com.ericsson.nms.security.nscs.handler.CommandContext
import com.ericsson.nms.security.nscs.handler.command.utility.ListNtpServerResponseBuilder
import com.ericsson.nms.security.nscs.logger.NscsLogger
import com.ericsson.nms.security.nscs.util.NtpConstants
import com.ericsson.nms.security.nscs.utilities.Constants

/**
 * This class for list ntp key id's
 * @author zkndrsv
 *
 */
public class ListNtpServerHandlerTest extends ListNtpServerSetupData {
    @ObjectUnderTest
    private ListNtpServerHandler listNtpServerHandler

    @MockedImplementation
    private NscsLogger nscsLogger;

    @Inject
    private ListNtpServerResponseBuilder listNtpServerResponseBuilder;

    @MockedImplementation
    private CommandContext commandContext

    @Shared
    private String [][] resultNtpListKeyIdValidNodes

    @Shared
    private String [][] resultNtpListKeyIdValidNodesNoStatus

    @Shared
    private String [][] resultNtpListKeyIdInValidNodes

    @Shared
    private String [] ntpListKeyIdHeader

    @Shared
    private String[][] resultNtpListKeyIdNodeDoesNotExistException

    @Shared
    private String[][] resultNtpListKeyIdNodeUnsynchronized

    def setupSpec() {

        ntpListKeyIdHeader = [
            NtpConstants.KEY_ID,
            NtpConstants.NTP_USER_LABEL,
            NtpConstants.NTP_SERVER_ID,
            NtpConstants.SERVER_ADDRESS_HEADER,
            NtpConstants.SERVICE_STATUS_HEADER,
            NtpConstants.ERROR_DETAILS_HEADER,
            NtpConstants.SUGGESTED_SOLUTION
        ]

        resultNtpListKeyIdValidNodes = [ntpListKeyIdHeader, ["01", "ENM", "ENM","172.25.69.227", NtpConstants.ACTIVATED, "", ""]]
        resultNtpListKeyIdValidNodesNoStatus = [ntpListKeyIdHeader, ["02", NtpConstants.NA, NtpConstants.NA , NtpConstants.NA,NtpConstants.NA, NtpConstants.NTP_SERVER_ERR_MSG + "\n", NtpConstants.NTP_SERVER_SOLUTION + "\n"]]
        resultNtpListKeyIdInValidNodes = [
            ntpListKeyIdHeader,
            [
                NtpConstants.NA,
                NtpConstants.NA,
                NtpConstants.NA,
                NtpConstants.NA,
                NtpConstants.NA,
                NtpConstants.NTP_SERVER_ERR_MSG + "\n",
                NtpConstants.NA + "\n"]
        ]
        resultNtpListKeyIdNodeDoesNotExistException = [
            ntpListKeyIdHeader,
            [NtpConstants.NA, NtpConstants.NA, NtpConstants.NA, NtpConstants.NA,NtpConstants.NA, NscsErrorCodes.MECONTEXT_NOT_FOUND, NscsErrorCodes.PLEASE_CREATE_THE_ME_CONTEXT_CORRESPONDING_TO_THE_SPECIFIED_MO]
        ]
        resultNtpListKeyIdNodeUnsynchronized = [
            ntpListKeyIdHeader,
            [
                NtpConstants.NA,
                NtpConstants.NA,
                NtpConstants.NA,
                NtpConstants.NA,
                NtpConstants.NA,
                NscsErrorCodes.THE_NETWORK_ELEMENT_SPECIFIED_DOES_NOT_EXIST,
                NscsErrorCodes.PLEASE_SPECIFY_A_VALID_NETWORK_ELEMENT_THAT_EXISTS_IN_THE_SYSTEM]
        ]
    }

    @Unroll("Read Ntp list on the node #nodeName #nodeStatus #keyId #ServiceStatus")
    def 'Status of ntp list on node'() {
        ntpListCommandData(nodeStatus, nodeName ,filePath)
        setDataForManagedObject(nodeName)
        setDataForNodeExists(nodeStatus, nodeName, nodeType, keyId, ServiceStatus, UserLabel, NtpServerId,ServerAddress)
        when: 'Ntp list feature status on node '
        def output = listNtpServerHandler.process(command, context)
        then: 'Response contains status information of Ntp key ids on the given node'
        if(output.isNameMultipleValueResponseType()){
            def response = output.getAdditionalInformation()
            responseVerification(output,expectedResults)
        }else{
            def response = output.getMessage()
            assert response.contains(expectedResults)
        }

        where:'Assert status on node'
        nodeName           | nodeType   | nodeStatus          |keyId                 | ServiceStatus          | ServerAddress   | UserLabel        | NtpServerId       | filePath                                         | expectedResults
        'LTE102RNC00001'   | 'RNC'      | 'validNode'         | '01'                 | NtpConstants.ACTIVATED |"172.25.69.227"  | "ENM"            | "ENM"             | 'src/test/resources/Ntp/nodeFileForNTPList.txt'  | resultNtpListKeyIdValidNodes
        'LTE103ERBS00005'  | 'ERBS'     | 'validNode'         | '02'                 | NtpConstants.NA        | NtpConstants.NA | NtpConstants.NA  | NtpConstants.NA   | 'src/test/resources/Ntp/nodeFileForNTPList.txt'  | resultNtpListKeyIdValidNodesNoStatus
        'LTE14ERBS00008'   | 'ERBS'     | 'InvalidNode'       | NtpConstants.NA      | NtpConstants.NA        | NtpConstants.NA | NtpConstants.NA  | NtpConstants.NA   | 'src/test/resources/Ntp/nodeFileForNTPList.txt'  | resultNtpListKeyIdInValidNodes
        'LTE102RNC00009'   | 'RNC'      | 'normNodeNull'      | '03'                 | NtpConstants.ACTIVATED |"172.25.69.227"  | "ENM"            | "ENM"             | 'src/test/resources/Ntp/nodeFileForNTPList.txt'  | resultNtpListKeyIdNodeDoesNotExistException
        'LTE05dg2ERBS0009' | 'RadioNode'| 'validNode'         | '01'                 | NtpConstants.ACTIVATED |"172.25.69.227"  | "ENM"            | "ENM"             | 'src/test/resources/Ntp/nodeFileForNTPList.txt'  | resultNtpListKeyIdValidNodes
        'LTE14ERBS00008'   | 'ERBS'     | 'UNSYNCHRONIZED'    | NtpConstants.NA      | NtpConstants.NA        | NtpConstants.NA | NtpConstants.NA  | NtpConstants.NA   | 'src/test/resources/Ntp/nodeFileForNTPList.txt'  | resultNtpListKeyIdNodeUnsynchronized
    }

    @Unroll("Read Ntp list on the node #nodeName #nodeStatus #keyId #ServiceStatus on nodes in nodefile")
    def 'Status of ntp list on nodes in nodefile'() {
        setNodeFileData(filePath)
        ntpListCommandData(nodeStatus, nodeName ,filePath)
        setDataForManagedObject(nodeName)
        setDataForNodeExists(nodeStatus, nodeName, nodeType, keyId, ServiceStatus, UserLabel, NtpServerId, ServerAddress)
        when: 'Ntp list feature status on node '
        def output = listNtpServerHandler.process(command, context)
        then: 'Response contains status information of Ntp key ids on the given node'
        if(output.isNameMultipleValueResponseType()){
            def response = output.getAdditionalInformation()
            responseVerification(output,expectedResults)
        }else{
            def response = output.getMessage()
            assert response.contains(expectedResults)
        }

        where:'Assert status on node'
        nodeName           | nodeType   | nodeStatus          |keyId                 | ServiceStatus          |ServerAddress    | UserLabel       | NtpServerId     | filePath                                           | expectedResults
        'LTE103ERBS00005'  | 'ERBS'     | 'validNode'         | '01'                 | NtpConstants.ACTIVATED |"172.25.69.227"  | "ENM"           | "ENM"           |'src/test/resources/Ntp/nodeFileForNTPList.txt'     | resultNtpListKeyIdValidNodes
        'LTE14ERBS00008'   | 'ERBS'     | 'InvalidNode'       | NtpConstants.NA      | NtpConstants.NA        | NtpConstants.NA | NtpConstants.NA | NtpConstants.NA |'src/test/resources/Ntp/invalidNodeFileForNTP.txt'  | resultNtpListKeyIdInValidNodes
        'LTE14ERBS00008'   | 'ERBS'     | 'UNSYNCHRONIZED'    | NtpConstants.NA      | NtpConstants.NA        | NtpConstants.NA | NtpConstants.NA | NtpConstants.NA |'src/test/resources/Ntp/invalidNodeFileForNTP.txt'  | resultNtpListKeyIdNodeUnsynchronized
    }

    @Unroll("Initiates NtpConfigureHandler to list ntp keyids on node in #nodeName in savedsearch #savedsearch")
    def "Ntp list feature on valid node in savedsearch"() {
        given: "nodeStatus, savedsearch"
        setNtpListCmdForSavedSearch(savedsearch, nodeStatus, nodeName, nodeType, keyId, ServiceStatus, UserLabel, NtpServerId,ServerAddress)
        when: "Execute Ntp list handler process method"
        def output = listNtpServerHandler.process(command, context)

        then: "Assert process response"
        if(output.isNameMultipleValueResponseType()){
            def response = output.getAdditionalInformation()
            responseVerification(output,expectedResults)
        }else{
            def response = output.getMessage()
            assert response.contains(expectedResults)
        }
        where:
        savedsearch   | nodeType  |nodeStatus    | nodeName        |keyId   |ServiceStatus          |ServerAddress  | UserLabel  |NtpServerId    |expectedResults
        'test'        | 'ERBS'    |'validNode'   |'LTE02ERBS00001' | '01'   |NtpConstants.ACTIVATED |"172.25.69.227"| "ENM"      |"ENM"          |resultNtpListKeyIdValidNodes
    }

    def responseVerification(NscsNameMultipleValueCommandResponse response, String[][] expectedResults) {
        Iterator iterator = response.iterator()
        while (iterator.hasNext()) {
            NscsNameMultipleValueCommandResponse.Entry entry = (NscsNameMultipleValueCommandResponse.Entry) iterator.next()
            assert expectedResults.contains(entry.getValues())
        }
    }}