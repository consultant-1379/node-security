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
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException

import spock.lang.Unroll

/**
 * This class covers positive and negative scenario test cases to activate RTSEL feature on the given nodes.
 *
 * @author xvadyas
 *
 */
public class RtselActivateHandlerTest extends RtselSetupData {

    @ObjectUnderTest
    ActivateRtselHandler activateRtselHandler

    @Unroll("Initiate ActivateRtselHandler to activate RTSEL feature on node using nodeName #nodeName")
    def "RTSEL feature activated for valid nodes"() {
        given: "nodeStatus, nodeName, filePath"
        setActivateCommandData(filePath)
        setDataForManagedObject(nodeName)
        setDataForNodeExists(nodeStatus, nodeName)
        when: "execute Activate Rtsel Handler process method"
        activateRtselHandler.process(command, context)
        then: "Assert process response"
        where:
        nodeStatus                      |     nodeName	        |			filePath
        'validNode'                     | 'LTE02ERBS00001'      | 'src/test/resources/RTSEL_Activate/Activate.xml'
        'validNode'                     | 'LTE02ERBS00005'      | 'src/test/resources/RTSEL_Activate/Activate_ipv6.xml'
        'partialValidNodes'             | 'LTE02ERBS00002'      | 'src/test/resources/RTSEL_Activate/ActivateScenarios.xml'
        'invalidNodeName'               | 'LTE02ERBS00002ee'    | 'src/test/resources/RTSEL_Activate/ActivateScenarios.xml'
        'isNodeExists'                  | 'LTE02aaERBS00002'    | 'src/test/resources/RTSEL_Activate/ActivateScenarios.xml'
        'isCertificateSupportedForNode' | 'LTE02ERBS00003'      | 'src/test/resources/RTSEL_Activate/ActivateScenarios.xml'
        'isNodeSynchronized'            | 'LTE02ERBS00004'      | 'src/test/resources/RTSEL_Activate/ActivateScenarios.xml'
    }


    @Unroll("Initiates ActivateRtselHandler to activate RTSEL feature incase of exceptions using filePath #filePath")
    def "Exception cases for peforming RTSEL feature on node"() {
        given: "filePath, ExpectedException"
        setActivateCommandData(filePath)
        when:"Execute Activate Rtsel Handler process method"
        activateRtselHandler.process(command,context)
        then:"Exception cases for performing activation of RTSEL feature on nodes"
        def error = thrown(expectedException)
        where:
        filePath                                                          |expectedException
        'src/test/resources/RTSEL_Activate/ActivateMultipleServers.xml'   |NscsServiceException
        'src/test/resources/RTSEL_Activate/ActivateSameServerNames.xml'   |NscsServiceException
        'src/test/resources/RTSEL_Activate/ProtocolAsNull.xml'            |NscsServiceException
        'src/test/resources/RTSEL_Activate/LogLevelAsNull.xml'            |NscsServiceException
        'src/test/resources/RTSEL_Activate/InvalidIPAddress.xml'          |NscsServiceException
        'src/test/resources/RTSEL_Activate/ServerNameAsEmpty.xml'         |NscsServiceException
        'src/test/resources/RTSEL_Activate/Activate_Neg_Case.xml'         |NscsServiceException
    }
}