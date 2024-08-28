/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.handler.command.impl

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.security.nscs.api.exception.InvalidFileContentException
import com.ericsson.nms.security.nscs.api.exception.InvalidInputXMLFileException

import spock.lang.Unroll

/**
 * This class covers positive and Negative scenario test cases for deleting external syslog servers on nodes.
 * 
 * @author zvetsni
 */
class RtselDeleteServerHandlerTest extends RtselSetupData {

    @ObjectUnderTest
    RtselDeleteServerHandler rtselDeleteServerHandler

    def "object under test injection" () {
        expect:
        rtselDeleteServerHandler != null
    }

    @Unroll("Initiate RtselDeleteServerHandler for performing delete operation of external syslog servers on node using nodeFdn #nodeFdn")
    def "External syslog servers deleted for valid nodes"(){
        given:"filePath, nodeFdn, nodeStatus, ExpectedMessage"
        setDeleteCommandData(filePath)
        setDataForManagedObject(nodeFdn)
        setDataForNodeExists(nodeStatus, nodeFdn)
        when:"Execute Rtsel Delete Handler process method"
        rtselDeleteServerHandler.process(command, context)
        then:"verifiying the job details and knowing the status"
        where:
        filePath                                               | nodeFdn            | nodeStatus
        "src/test/resources/RTSEL_Delete/SuccessScenarios.xml" | 'LTE08ERBS00001'   | 'validNode'
        "src/test/resources/RTSEL_Delete/ValidateNode.xml"     | 'LTE08ERBS00002'   | 'partialValidNodes'
        "src/test/resources/RTSEL_Delete/ValidateNode.xml"     | 'LTE08ERBS00002ee' | 'invalidNodeName'
        "src/test/resources/RTSEL_Delete/ValidateNode.xml"     | 'LTE08aaERBS00002' | 'isNodeExists'
        "src/test/resources/RTSEL_Delete/ValidateNode.xml"     | 'LTE08ERBS00003'   | 'isCertificateSupportedForNode'
        "src/test/resources/RTSEL_Delete/ValidateNode.xml"     | 'LTE08ERBS00004'   | 'isNodeSynchronized'
    }

    @Unroll("Initiates RtselDeleteServerHandler to delete external syslog servers incase of exceptions using filePath #filePath")
    def "Exception cases for performing deletion of external syslog servers on nodes"() {
        given: "filePath, ExpectedException"
        setDeleteCommandData(filePath)
        when:"Execute Rtsel Delete Handler process method"
        rtselDeleteServerHandler.process(command, context)
        then:"Expected response are thrown as a response"
        def error = thrown(expectedException)
        where:
        filePath                                                  | expectedException
        "src/test/resources/RTSEL_Delete/ServerNameIsEmpty.xml"   | InvalidInputXMLFileException
        "src/test/resources/RTSEL_Delete/NodeNameIsEmpty.xml"     | InvalidInputXMLFileException
        "src/test/resources/RTSEL_Delete/MultipleServerNames.xml" | InvalidInputXMLFileException
        null                                                      | InvalidFileContentException
    }
}