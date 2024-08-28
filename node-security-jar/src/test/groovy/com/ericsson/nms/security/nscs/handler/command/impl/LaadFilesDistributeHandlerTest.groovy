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

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException
import com.ericsson.nms.security.nscs.api.exception.InvalidCollectionNameException
import com.ericsson.nms.security.nscs.api.exception.InvalidSavedSearchNameException
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes
import com.ericsson.nms.security.nscs.handler.laad.utility.LaadFilesDistributeConstants
import com.ericsson.oss.services.dto.JobStatusRecord
import com.ericsson.oss.services.enums.JobGlobalStatusEnum
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler

import spock.lang.Shared
import spock.lang.Unroll



/**
 * This class covers positive and negative scenario test cases to distribute LAAD files to the given nodes.
 *
 * @author tcsgoja
 *
 */
class LaadFilesDistributeHandlerTest extends LaadDistributeSetupData{

    @ObjectUnderTest
    private LaadFilesDistributeHandler laadFilesDistributeHandler

    @Shared
    private String allInValidNodesMessage

    @Shared
    private String allValidNodesMessage

    @Shared
    private String[] errorHeader

    @Shared
    private String invalidSvedSearchName

    @Shared
    private JobStatusRecord jobStatusRecord

    @Shared
    private String partialValidNodesMessage

    @Shared
    private String[][] resultAlarmSupervisionDisabledException

    @Shared
    private String[][] resultNetworkElementNotfoundException

    @Shared
    private String[][] resultNodeDoesNotExistException

    @Shared
    private String [][] resultNodeNotCertifiableException

    @Shared
    private String [][] resultNodeNotSynchronizedException

    @Shared
    private String[][] resultUnassociatedNetworkElementException

    @Shared
    private String[][] resultUnsupportedNodeTypeException

    @Inject
    NscsJobCacheHandler nscsJobCacheHandler

    def setupSpec() {

        UUID jobId = UUID.randomUUID()
        jobStatusRecord = new JobStatusRecord()
        jobStatusRecord.setGlobalStatus(JobGlobalStatusEnum.PENDING)
        jobStatusRecord.setUserId("user")
        jobStatusRecord.setJobId(jobId)

        allValidNodesMessage = LaadFilesDistributeConstants.LAAD_DISTRIBUTE_EXECUTED +". Perform 'secadm job get -j " + jobStatusRecord.getJobId().toString() + "' to get progress information.";
        partialValidNodesMessage = String.format(LaadFilesDistributeConstants.LAAD_DISTRIBUTE_PARTIALLY_EXECUTED, jobStatusRecord.getJobId().toString());
        allInValidNodesMessage = LaadFilesDistributeConstants.LAAD_DISTRIBUTE_NOT_EXECUTED;

        errorHeader = [
            "Error Code",
            "Error Detail",
            "Suggested Solution"
        ];
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
        resultNodeNotCertifiableException = [
            errorHeader,
            [
                "10021",
                "Cannot generate a certificate for this kind of node",
                NscsErrorCodes.SPECIFY_A_CERTFIABLE_NODE
            ]
        ]
        resultNodeNotSynchronizedException = [
            errorHeader,
            [
                "10005",
                "The node specified is not synchronized",
                NscsErrorCodes.PLEASE_ENSURE_THE_NODE_SPECIFIED_IS_SYNCHRONIZED
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
        resultAlarmSupervisionDisabledException = [
            errorHeader,
            [
                "10116",
                "Alarm Supervision is not enabled on specified node(s)",
                NscsErrorCodes.ALARM_SUPERVISION_ENABLE_SUGGESTED_SOLUTION
            ]
        ]
        invalidSvedSearchName = NscsErrorCodes.INVALID_SYNTAX_FOR_COLLECTION_NAME
        resultUnsupportedNodeTypeException = [
            errorHeader,
            [
                "10090",
                "Unsupported Node Type",
                LaadFilesDistributeConstants.LAAD_DISTRIBUTE_NODE_TYPE_SUGGESTED_SOLUTION
            ]
        ]
    }

    def "object under test injection" () {
        expect:
        laadFilesDistributeHandler != null
    }

    @Unroll('Initiates LaadFilesDistributeHandler to distribute LAAD files to node in #nodeName in nodelist')
    def 'Distribute LAAD files feature to valid and invalid nodes in nodelist'(){
        given:'nodeName'
        setLaadDistributeCommandData(nodeStatus, nodeName)
        setDataForManagedObject(nodeName)
        setDataForNodeExists(nodeStatus, nodeName, fmAlarmState,isCliCmdSupported)
        nscsJobCacheHandler.insertJob(_) >> jobStatusRecord
        when: 'Execute LAAD files Distribute Handler process method'
        def output = laadFilesDistributeHandler.process(command, context)
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
        nodeStatus               |fmAlarmState|isCliCmdSupported| nodeName            |       expected                |       exception
        'validNode'              | true       | true            |'LTE02ERBS00001'     | allValidNodesMessage          |       'NA'
        'partialValidNodes'      | true       | true            |'LTE02ERBS00002'     | partialValidNodesMessage      |       resultUnassociatedNetworkElementException
        'normNodeNull'           | true       | true            |'LTE02ERBS00003'     | allInValidNodesMessage        |       resultUnassociatedNetworkElementException
        'isNodeExists'           | true       | true            |'LTE02ERBS00005'     | allInValidNodesMessage        |       resultNetworkElementNotfoundException
        'isCertSupportedForNode' | true       | true            |'LTE02ERBS00006'     | allInValidNodesMessage        |       resultNodeNotCertifiableException
        'isNodeSynchronized'     | true       | true            |'LTE02ERBS00007'     | allInValidNodesMessage        |       resultNodeNotSynchronizedException
        'isNodeSynchronized'     | true       | true            |'LTE02ERBS00008'     | allInValidNodesMessage        |       resultNodeNotSynchronizedException
        'validNode'              | false      | true            |'LTE02ERBS00009'     | allInValidNodesMessage        |       resultAlarmSupervisionDisabledException
        'validNode'              | true       | false           |'LTE04dg2ERBS00009'  | allInValidNodesMessage        |       resultUnsupportedNodeTypeException
    }


    @Unroll('Initiates LaadFilesDistributeHandler to distribute LAAD files to node #nodeName in nodefile #filepath')
    def 'Distribute LAAD files feature to valid and invalid nodes in nodefile'(){
        given:'nodeName'
        setNodeFileData(filepath)
        setDataForManagedObject(nodeName)
        setDataForNodeExists(nodeStatus, nodeName, fmAlarmState, true)
        nscsJobCacheHandler.insertJob(_) >> jobStatusRecord
        when: 'Execute LAAD files Distribute Handler process method to node file'
        def output = laadFilesDistributeHandler.process(command, context)
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
        nodeStatus               |fmAlarmState |     nodeName         |       expected           |       exception                            |       filepath
        'validNode'              |true         | 'LTE02ERBS00001'     | allValidNodesMessage     | 'NA'                                       |'src/test/resources/laad/testNodeFile.txt'
        'partialValidNodes'      |true         | 'LTE02ERBS00002'     | partialValidNodesMessage | resultUnassociatedNetworkElementException  |'src/test/resources/laad/testPartialInvalidNodesFile.txt'
        'isNodeExists'           |true         | 'LTE02ERBS00002ee'   | allInValidNodesMessage   | resultNodeDoesNotExistException            |'src/test/resources/laad/testNodeFile.txt'
    }

    @Unroll("Initiates LaadFilesDistributeHandler to distribute LAAD files to node #nodeName in savedsearch #savedsearch")
    def "Distribute LAAD files feature to valid node in savedsearch"() {
        given: "nodeStatus, savedsearch"
        setLaadDistrCmdForSavedSearch(savedsearch, nodeStatus, nodeName)
        when: "Execute Deactivate Rtsel Handler process method"
        nscsJobCacheHandler.insertJob(_) >> jobStatusRecord
        def output = laadFilesDistributeHandler.process(command, context)
        then: "Assert process response"

        def response = output.getMessage()
        assert response.contains(expected)

        where:

        savedsearch     |       nodeStatus                      |     nodeName          |       expected
        'test'          |       'validNode'                     | 'LTE02ERBS00001'      | allValidNodesMessage
    }

    @Unroll("Initiates LaadFilesDistributeHandler to distribute LAAD files to node #nodeName in collection #collection")
    def "Distribute LAAD files feature to valid node in collection"() {
        given: "nodeStatus, savedsearch"
        setLaadDistrCmdForCollection(collection, nodeStatus, nodeName)
        nscsJobCacheHandler.insertJob(_) >> jobStatusRecord
        when: "Execute Deactivate Rtsel Handler process method"
        def output = laadFilesDistributeHandler.process(command, context)
        then: "Assert process response"

        def response = output.getMessage()
        assert response.contains(expected)

        where:

        collection     |       nodeStatus                      |     nodeName          |       expected
        'test'          |       'validNode'                     | 'LTE02ERBS00001'      |       allValidNodesMessage
    }

    @Unroll("Initiates LaadFilesDistributeHandler to distribute LAAD files to invalid savedsearch #savedsearch")
    def "Distribute LAAD files feature to invalid savedsearch"() {
        given: "nodeStatus, savedsearch"
        setLaadDistrCmdForSavedSearchNegative(savedsearch)
        when: "Execute Deactivate Rtsel Handler process method"
        def output = laadFilesDistributeHandler.process(command, context)

        then: "Assert process response"
        def assertion = thrown(exception)

        where:

        savedsearch                      |     exception
        't@est'                          | InvalidSavedSearchNameException
        'test123'                        | InvalidSavedSearchNameException
        'Privatesearch'                  | InvalidSavedSearchNameException
    }



    @Unroll("Initiates LaadFilesDistributeHandler to distribute LAAD files to invalid collection #collection")
    def "Distribute LAAD files feature to invalid collection"() {
        given: "nodeStatus, savedsearch"
        setLaadDistrCmdForCollectionNegative(collection)
        when: "Execute Deactivate Rtsel Handler process method"
        def output = laadFilesDistributeHandler.process(command, context)

        then: "Assert process response"
        def assertion = thrown(exception)

        where:

        collection                      |     exception
        't@est'                         | InvalidCollectionNameException
        'test123'                       | InvalidCollectionNameException
        'Privatesearch'                 | InvalidCollectionNameException
    }


    def responseVerification(NscsNameMultipleValueCommandResponse response, String[][] expectedResults) {
        Iterator iterator = response.iterator()
        while (iterator.hasNext()) {
            NscsNameMultipleValueCommandResponse.Entry entry = (NscsNameMultipleValueCommandResponse.Entry) iterator.next()
            assert expectedResults.contains(entry.getValues())
        }
    }

    @Unroll('Initiates LaadFilesDistributeHandler to distribute LAAD files to node in xml nodefile #filepath')
    def 'Distribute LAAD files feature to node in xml nodefile'(){
        given:'nodeName'
        setNodeXmlFileData(filepath)
        when: 'Execute LAAD files Distribute Handler process method to node file'
        def output = laadFilesDistributeHandler.process(command, context)
        then: 'Assert process response'
        def assertion = thrown(exception)
        where:'Assert status on node'
        filepath                           |     exception
        'src/test/resources/laad/node.xml' | InvalidArgumentValueException
    }
}