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
import com.ericsson.nms.security.nscs.api.exception.InvalidCollectionNameException
import com.ericsson.nms.security.nscs.api.exception.InvalidSavedSearchNameException

import spock.lang.Unroll

/**
 * This class covers positive and negative scenario test cases to deactivate RTSEL feature on the given nodes.
 *
 * @author zkakven
 *
 */
public class DeactivateRtselHandlerTest extends RtselSetupData {

    @ObjectUnderTest
    DeactivateRtselHandler deactivateRtselHandler

    def "object under test injection" () {
        expect:
        deactivateRtselHandler != null
    }

    @Unroll("Initiates DeactivateRtselHandler to deactivate RTSEL feature on node using #nodeName")
    def "RTSEL feature activated for valid nodes"() {
        given: "nodeStatus, nodeName"
        setDeactivateCommandData(nodeStatus, nodeName)
        setDataForManagedObject(nodeName)
        setDataForNodeExists(nodeStatus, nodeName)
        when: "Execute Deactivate Rtsel Handler process method"
        deactivateRtselHandler.process(command, context)
        then: "Assert process response"
        where:
        nodeStatus                      | nodeName
        'validNode'                     | 'LTE02ERBS00001'
        'partialValidNodes'             | 'LTE02ERBS00002'
        'normNodeNull'                  | 'LTE02ERBS00003'
        'invalidNodeName'               | 'LTE02ERBS0000444444'
        'isNodeExists'                  | 'LTE02ERBS00005'
        'isCertificateSupportedForNode' | 'LTE02ERBS00006'
        'isNodeSynchronized'            | 'LTE02ERBS00007'
    }

    @Unroll("Initiates DeactivateRtselHandler to deactivate RTSEL feature on node using #savedsearch")
    def "RTSEL feature activated for validsavedsearch"() {
        given: "nodeStatus, savedsearch"
        setDeactivateCommandDataForSS(savedsearch)
        when: "Execute Deactivate Rtsel Handler process method"
        deactivateRtselHandler.process(command, context)
        then: "Assert process response"
        where:
        savedsearch << ['test']
    }

    @Unroll("Initiates DeactivateRtselHandler to deactivate RTSEL feature on node using #Collection")
    def "RTSEL feature activated for valid Collection"() {
        given: "nodeStatus, savedsearch"
        setDeactivateCommandDataForCO(collection)
        when: "Execute Deactivate Rtsel Handler process method"
        deactivateRtselHandler.process(command, context)
        then: "Assert process response"
        where:
        collection << ['test']
    }

    @Unroll("Initiates DeactivateRtselHandler to deactivate RTSEL feature on node using #savedsearch")
    def "RTSEL feature activated for invalidsavedsearch negativescenarios"() {
        given: "nodeStatus, savedsearch"
        setDeactivateCommandDataForSSNegative(savedsearch)
        when: "Execute Deactivate Rtsel Handler process method"
        deactivateRtselHandler.process(command, context)
        then: "Assert process response"
        def assertion = thrown(expectedException)
        where:
        savedsearch     | expectedException
        't@est'		| InvalidSavedSearchNameException
        'test'		| InvalidSavedSearchNameException
        'Privatesearch'	| InvalidSavedSearchNameException
    }

    @Unroll("Initiates DeactivateRtselHandler to deactivate RTSEL feature on node using #collection")
    def "RTSEL feature activated for invalidcollection negativescenarios"() {
        given: "nodeStatus, savedsearch"
        setDeactivateCommandDataForCONegative(collection)
        when: "Execute Deactivate Rtsel Handler process method"
        deactivateRtselHandler.process(command, context)
        then: "Assert process response"
        def assertion = thrown(expectedException)
        where:
        collection | expectedException
        't@est'    | InvalidCollectionNameException
        'test'     | InvalidCollectionNameException
    }
}