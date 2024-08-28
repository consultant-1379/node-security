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

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException
import com.ericsson.nms.security.nscs.api.exception.InvalidCollectionNameException
import com.ericsson.nms.security.nscs.api.exception.InvalidSavedSearchNameException
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes
import com.ericsson.nms.security.nscs.util.NtpConstants
import com.ericsson.oss.services.dto.JobStatusRecord
import com.ericsson.oss.services.enums.JobGlobalStatusEnum
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler

import spock.lang.Shared
import spock.lang.Unroll
/**
 * This class tests NtpConfigureHandler use cases
 *
 * @author xjangop
 *
 */
public class NtpConfigureHandlerTest extends NtpConfigureData{

    @ObjectUnderTest
    private NtpConfigureHandler ntpConfigureHandler

    @Shared
    private String allValidNodesMessage

    @Shared
    private String allInValidNodesMessage

    @Shared
    private String[] errorHeader

    @Shared
    private String invalidSvedSearchName

    @Shared
    private JobStatusRecord jobStatusRecord

    @Shared
    private String partialValidNodesMessage

    @Shared
    private String[][] resultNetworkElementNotfoundException

    @Shared
    private String[][] resultNodeDoesNotExistException

    @Shared
    private String[][] resultUnassociatedNetworkElementException

    @Shared
    private String[][] resultUnsupportedNodeTypeException

    @Inject
    private NscsJobCacheHandler nscsJobCacheHandler

    def setupSpec(){

        UUID jobId = UUID.randomUUID();
        jobStatusRecord = new JobStatusRecord()
        jobStatusRecord.setGlobalStatus(JobGlobalStatusEnum.PENDING)
        jobStatusRecord.setUserId("user")
        jobStatusRecord.setJobId(jobId)

        allValidNodesMessage = NtpConstants.NTP_CONFIG_EXECUTED +". Perform 'secadm job get -j " + jobStatusRecord.getJobId().toString() + "' to get progress information."
        partialValidNodesMessage = String.format(NtpConstants.NTP_CONFIG_PARTIALLY_EXECUTED, jobStatusRecord.getJobId().toString())
        allInValidNodesMessage = NtpConstants.NTP_CONFIG_NOT_EXECUTED

        errorHeader = [
            "Error Code",
            "Error Detail",
            "Suggested Solution"
        ]
        resultUnassociatedNetworkElementException = [
            errorHeader,
            [
                "10016",
                "The MeContext MO does not exist for the associated NetworkElement MO",
                "Please create the MeContext corresponding to the specified MO."
            ]
        ]
        resultNetworkElementNotfoundException = [
            errorHeader,
            [
                "10007",
                "The NetworkElement MO does not exist for the associated MeContext MO",
                "Please create the NetworkElement MO and any other required MOs for the associated MeContext MO."
            ]
        ]
        resultNodeDoesNotExistException = [
            errorHeader,
            [
                "10004",
                "The node specified does not exist",
                NscsErrorCodes.SPECIFY_A_VALID_NODE
            ]
        ]
        invalidSvedSearchName = NscsErrorCodes.INVALID_SYNTAX_FOR_COLLECTION_NAME
        resultUnsupportedNodeTypeException = [
            errorHeader,
            [
                "10090",
                "Unsupported Node Type",
                "Check online help for supported node type(s) for Ntp Configue."
            ]
        ]
    }

    def "object under test injection" () {
        expect:
        ntpConfigureHandler != null
    }

    @Unroll('Initiates NtpConfigureHandler to configure ntp keyids on node in #nodeName in nodelist')
    def 'Ntp Configure feature on valid and invalid nodes in nodelist'(){
        given: 'nodeName'
        setNtpConfigureCommandData(nodeStatus, nodeName)
        setDataForManagedObject(nodeName)
        setDataForNodeExists(nodeStatus, nodeName)
        nscsJobCacheHandler.insertJob(_) >> jobStatusRecord
        when:'Execute Ntp Configure handler process method'
        def output = ntpConfigureHandler.process(command, context)
        then:'Assert process response'
        if(output.isNameMultipleValueResponseType()){
            def response = output.getAdditionalInformation()
            assert response.contains(expected)
            responseVerification(output,exception)
        }else{
            def response = output.getMessage()
            assert response.contains(expected)
        }
        where:'Assert status on node'
        nodeStatus               |  nodeName           |       expected                |       exception
        'validNode'              |'LTE02ERBS00001'     | allValidNodesMessage          |       'NA'
        'partialValidNodes'      |'LTE02ERBS00002'     | partialValidNodesMessage      |       resultUnassociatedNetworkElementException
        'normNodeNull'           |'LTE02ERBS00003'     | allInValidNodesMessage        |       resultUnassociatedNetworkElementException
        'isNodeExists'           |'LTE02ERBS00005'     | allInValidNodesMessage        |       resultNetworkElementNotfoundException
        'validNode'              |'LTE04dg2ERBS00009'  | allValidNodesMessage          |       'NA'
        'partialValidNodes'      |'LTE07dg2ERBS00007'  | partialValidNodesMessage      |       resultUnassociatedNetworkElementException
    }

    @Unroll('Initiates NtpConfigureHandler to configure ntp keyids on node in #nodeName in nodefile #filepath')
    def 'Ntp Configure feature on valid and invalid nodes in nodefile'(){
        given:'nodeName'
        setNodeFileData(filepath)
        setDataForManagedObject(nodeName)
        setDataForNodeExists(nodeStatus, nodeName)
        nscsJobCacheHandler.insertJob(_) >> jobStatusRecord
        when: 'Execute Ntp Configure handler process method to node file'
        def output = ntpConfigureHandler.process(command, context)
        then: 'Assert process response'
        if(output.isNameMultipleValueResponseType()){
            def response = output.getAdditionalInformation()
            assert response.contains(expected)
            responseVerification(output,exception)
        }else{
            def response = output.getMessage()
            assert response.contains(expected)
        }
        where:'Assert status on node'
        nodeStatus              |nodeName              | expected                 | exception                                  |filepath
        'validNode'             | 'LTE02ERBS00001'     | allValidNodesMessage     | 'NA'                                       |'src/test/resources/Ntp/testNodeFile.txt'
        'partialValidNodes'     | 'LTE02ERBS00002'     | partialValidNodesMessage | resultUnassociatedNetworkElementException  |'src/test/resources/Ntp/testPartialInvalidNodesFile.txt'
        'isNodeExists'          | 'LTE02ERBS00002ee'   | allInValidNodesMessage   | resultNetworkElementNotfoundException      |'src/test/resources/Ntp/testNodeInvalidFile.txt'
    }

    @Unroll("Initiates NtpConfigureHandler to configure ntp keyids on node in #nodeName in savedsearch #savedsearch")
    def "Ntp Configure feature on valid node in savedsearch"() {
        given: "nodeStatus, savedsearch"
        setNtpConfigureCmdForSavedSearch(savedsearch, nodeStatus, nodeName)
        when: "Execute Ntp Configure handler process method"
        nscsJobCacheHandler.insertJob(_) >> jobStatusRecord
        def output = ntpConfigureHandler.process(command, context)
        then: "Assert process response"

        def response = output.getMessage()
        assert response.contains(expected)

        where:

        savedsearch     |       nodeStatus                      |     nodeName          |       expected
        'test'          |       'validNode'                     | 'LTE02ERBS00001'      | allValidNodesMessage
    }

    @Unroll("Initiates NtpConfigureHandler to configure ntp keyids on invalid node in #nodeName in savedsearch #savedsearch")
    def "Ntp Configure feature on invalid savedsearch"() {
        given: "nodeStatus, savedsearch"
        setNtpConfigureCmdForSavedSearchNegative(savedsearch)
        when: "Execute Ntp Configure handler process method"
        def output = ntpConfigureHandler.process(command, context)

        then: "Assert process response"
        def assertion = thrown(exception)

        where:

        savedsearch                      |     exception
        't@est'                          | InvalidSavedSearchNameException
        'test123'                        | InvalidSavedSearchNameException
        'Privatesearch'                  | InvalidSavedSearchNameException
    }

    @Unroll("Initiates NtpConfigureHandler to configure ntp keyids on node #nodeName in collection #collection")
    def "Ntp Configure feature on valid node in collection"() {
        given: "nodeStatus, savedsearch"
        setNtpConfigureCmdForCollection(collection, nodeStatus, nodeName)
        nscsJobCacheHandler.insertJob(_) >> jobStatusRecord
        when: "Execute Ntp Configure handler process method"
        def output = ntpConfigureHandler.process(command, context)
        then: "Assert process response"

        def response = output.getMessage()
        assert response.contains(expected)

        where:

        collection     |       nodeStatus                      |     nodeName          |       expected
        'test'          |       'validNode'                     | 'LTE02ERBS00001'      |       allValidNodesMessage
    }

    @Unroll("Initiates NtpConfigureHandler to configure ntp keyids on invalid collection #collection")
    def "Ntp Configure feature on invalid collection"() {
        given: "nodeStatus, savedsearch"
        setNtpConfigureCmdForCollectionNegative(collection)
        when: "Execute Ntp Configure handler process method"
        def output = ntpConfigureHandler.process(command, context)

        then: "Assert process response"
        def assertion = thrown(exception)

        where:

        collection                      |     exception
        't@est'                         | InvalidCollectionNameException
        'test123'                       | InvalidCollectionNameException
        'Privatesearch'                 | InvalidCollectionNameException
    }

    @Unroll('Initiates NtpConfigureHandler to configure ntp keyids on node in xml nodefile #filepath')
    def 'Ntp Configure feature on node in xml nodefile'(){
        given:'nodeName'
        setNodeXmlFileData(filepath)
        when: 'Execute Ntp Configure handler process method to node file'
        def output = ntpConfigureHandler.process(command, context)
        then: 'Assert process response'
        def assertion = thrown(exception)
        where:'Assert status on node'
        filepath                           |     exception
        'src/test/resources/Ntp/node.xml' | InvalidArgumentValueException
    }

    def responseVerification(NscsNameMultipleValueCommandResponse response, String[][] expectedResults) {
        Iterator iterator = response.iterator()
        while (iterator.hasNext()) {
            NscsNameMultipleValueCommandResponse.Entry entry = (NscsNameMultipleValueCommandResponse.Entry) iterator.next()
            assert expectedResults.contains(entry.getValues())
        }
    }
}
