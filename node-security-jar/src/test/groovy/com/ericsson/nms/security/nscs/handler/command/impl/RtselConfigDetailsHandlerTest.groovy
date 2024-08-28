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

import spock.lang.Unroll

/**
 *This class covers positive and negative scenario test cases for RTSEL Get command on the given nodes.
 *
 *  @author xsusant
 */
class RtselConfigDetailsHandlerTest extends RtselSetupData{

    @ObjectUnderTest
    RtselConfigurationDetailsHandler rtselConfigurationDetailsHandler

    def "object under test injection" () {
        expect:
        rtselConfigurationDetailsHandler != null
    }

    @Unroll("Read RTSEL Feature State and other details on node #nodeName")
    def 'Status of RTSEL Feature on node'() {
        given: 'Intializing command and creating MO hireachy in DPS'
        setcommand(nodeName)
        getManagedObjectfromnscsCMReaderService(nodeName)
        setDataForNodeExists(synchStatus, nodeName)
        when: 'Get RTSEl feature status on node '
        def output = rtselConfigurationDetailsHandler.process(command, context)
        then: 'Response contains status information of RTSEL feature on the given node'
        if(output.isNameMultipleValueResponseType()){
            def response = output.getAdditionalInformation()
            assert response.contains(expected)
        }
        where:'Assert status on node'
        nodeName           | nodeType |    synchStatus          | expected
        'LTE102ERBS00001'  | 'ERBS'   |    'validNodes'  	| 'Rtsel details for 1 valid node(s)   is/are listed below'
        'LTE102ERBS00002'  | 'ERBS'   |   'unSynchronized'      | 'All the given node(s) are invalid, Error Detail(s) for respective node(s) is/are listed below.'
        'LTE102ERBS00003'  | 'jsdfi'  |    'SYNCHRONIZED'       | 'All the given node(s) are invalid, Error Detail(s) for respective node(s) is/are listed below.'
    }
}