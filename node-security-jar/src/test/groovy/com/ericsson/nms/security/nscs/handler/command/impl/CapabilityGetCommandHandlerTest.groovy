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
package com.ericsson.nms.security.nscs.handler.command.impl;

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse
import com.ericsson.nms.security.nscs.api.command.types.CapabilityGetCommand
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelConstants
import com.ericsson.nms.security.nscs.handler.CommandContext
import com.ericsson.nms.security.nscs.logger.NscsLogger
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl

import spock.lang.Unroll

class CapabilityGetCommandHandlerTest extends CdiSpecification {

    @ObjectUnderTest
    private CapabilityGetCommandHandler capabilityGetCommandHandler

    @MockedImplementation
    private NscsLogger nscsLogger

    @MockedImplementation
    private CommandContext commandContext

    @MockedImplementation
    private NscsModelServiceImpl nscsModelServiceImpl

    @MockedImplementation
    private NscsCapabilityModelConstants nscsCapabilityModelConstants

    def setup() {
        nscsModelServiceImpl.getTargetCategories() >> ["TC_1", "TC_2", "TC_3"]
        nscsModelServiceImpl.getTargetTypes("TC_1") >> ["TT_1", "TT_2"]
        nscsModelServiceImpl.getTargetTypes("TC_2") >> ["TT_3", "TT_4"]
        nscsModelServiceImpl.getTargetTypes("TC_3") >> ["TT_4"]
        nscsModelServiceImpl.getTargetModelIdentities("TC_1", "TT_1") >> ["TMI_1", "TMI_2"]
        nscsModelServiceImpl.getTargetModelIdentities("TC_1", "TT_2") >> ["TMI_3"]
        nscsModelServiceImpl.getTargetModelIdentities("TC_2", "TT_3") >> null
        nscsModelServiceImpl.getTargetModelIdentities("TC_2", "TT_4") >> ["TMI_3"]
        nscsModelServiceImpl.getTargetModelIdentities("TC_3", "TT_4") >> []
        nscsModelServiceImpl.getDefaultValue(_ as String, _ as String) >> "DEF_1"
        nscsModelServiceImpl.getCapabilityValue(_ as String, _ as String, _ as String, _ as String, _ as String) >> "VAL_1"
    }

    def "object under test injection" () {
        expect:
        capabilityGetCommandHandler != null
    }

    @Unroll
    def 'A null object should match only another null object, actual #actual'() {
        given:
        def expected = null
        when:
        def result = capabilityGetCommandHandler.equalValue(expected, actual)
        then:
        result == expectedResult
        where:
        actual << [
            null,
            "expected",
            "",
            "*",
            true,
            "true",
            false,
            [],
            ["OAM", "IPSEC"],
            [ "OAM" : "oam", "IPSEC" : "ipsec" ],
            [ "OAM" : ["oam", "Oam"], "IPSEC" : ["ipsec", "IpSec"]]
        ]
        expectedResult << [
            true,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false
        ]
    }

    @Unroll
    def 'Two String values should match if equal case-sensitive, actual #actual'() {
        given:
        def expected = "expected"
        when:
        def result = capabilityGetCommandHandler.equalValue(expected, actual)
        then:
        result == expectedResult
        where:
        actual << [
            null,
            "expected",
            "unexpected",
            "",
            "Expected",
            "*",
            true,
            "true",
            false,
            [],
            ["OAM", "IPSEC"],
            [ "OAM" : "oam", "IPSEC" : "ipsec" ],
            [ "OAM" : ["oam", "Oam"], "IPSEC" : ["ipsec", "IpSec"]]
        ]
        expectedResult << [
            false,
            true,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false
        ]
    }

    @Unroll
    def 'Two boolean values should match if equal, actual #actual'() {
        given:
        def expected = true
        when:
        def result = capabilityGetCommandHandler.equalValue(expected, actual)
        then:
        result == expectedResult
        where:
        actual << [
            null,
            "expected",
            "",
            "*",
            true,
            "true",
            false,
            [],
            ["OAM", "IPSEC"],
            [ "OAM" : "oam", "IPSEC" : "ipsec" ],
            [ "OAM" : ["oam", "Oam"], "IPSEC" : ["ipsec", "IpSec"]],
            [ "SSH" : [ "A" : "a", "B" : "b" ], "TLS" : [ "C" : "c", "D" : "d" ] ]
        ]
        expectedResult << [
            false,
            false,
            false,
            false,
            true,
            false,
            false,
            false,
            false,
            false,
            false,
            false
        ]
    }

    @Unroll
    def 'Two Lists of String should match if containing the same case-sensitive values in any order, actual #actual'() {
        given:
        def expected = ["OAM", "IPSEC"]
        when:
        def result = capabilityGetCommandHandler.equalValue(expected, actual)
        then:
        result == expectedResult
        where:
        actual << [
            null,
            "expected",
            "",
            true,
            [],
            ["OAM"],
            ["IPSEC"],
            ["OAM", "IPSEC"],
            ["IPSEC", "OAM"],
            ["oam", "ipsec"],
            ["OAM", "IPSEC", "other"],
            [ "OAM" : "oam", "IPSEC" : "ipsec" ],
            [ "OAM" : ["oam", "Oam"], "IPSEC" : ["ipsec", "IpSec"]],
            [ "SSH" : [ "A" : "a", "B" : "b" ], "TLS" : [ "C" : "c", "D" : "d" ] ]
        ]
        expectedResult << [
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            true,
            true,
            false,
            false,
            false,
            false,
            false
        ]
    }

    @Unroll
    def 'Two Map of String should match if equal case-sensitive, actual #actual'() {
        given:
        def expected = [ "OAM" : "oam", "IPSEC" : "ipsec" ]
        when:
        def result = capabilityGetCommandHandler.equalValue(expected, actual)
        then:
        result == expectedResult
        where:
        actual << [
            null,
            "expected",
            "",
            true,
            [],
            ["OAM", "IPSEC"],
            [ "OAM" : "oam", "IPSEC" : "ipsec" ],
            [ "IPSEC" : "ipsec", "OAM" : "oam" ],
            [ "IPSEC" : "oam", "OAM" : "ipsec" ],
            [ "OAM" : "ipsec", "IPSEC" : "oam" ],
            [ "OAM" : "", "IPSEC" : "ipsec" ],
            [ "OAM" : null, "IPSEC" : "ipsec" ],
            [ "OAM" : ["oam", "Oam"], "IPSEC" : ["ipsec", "IpSec"]],
            [ "SSH" : [ "A" : "a", "B" : "b" ], "TLS" : [ "C" : "c", "D" : "d" ] ]
        ]
        expectedResult << [
            false,
            false,
            false,
            false,
            false,
            false,
            true,
            true,
            false,
            false,
            false,
            false,
            false,
            false
        ]
    }

    @Unroll
    def 'Two Map of List of String should match if equal case-sensitive, actual #actual'() {
        given:
        def expected = [ "OAM" : ["oam", "Oam"], "IPSEC" : ["ipsec", "IpSec"]]
        when:
        def result = capabilityGetCommandHandler.equalValue(expected, actual)
        then:
        result == expectedResult
        where:
        actual << [
            null,
            "expected",
            "",
            true,
            [],
            ["OAM", "IPSEC"],
            [ "OAM" : "oam", "IPSEC" : "ipsec" ],
            [ "OAM" : ["oam", "Oam"], "IPSEC" : ["ipsec", "IpSec"]],
            [ "OAM" : ["Oam", "oam"], "IPSEC" : ["ipsec", "IpSec"]],
            [ "IPSEC" : ["ipsec", "IpSec"], "OAM" : ["oam", "Oam"]],
            [ "IPSEC" : ["IpSec", "ipsec"], "OAM" : ["oam", "Oam"]],
            [ "OAM" : ["ipsec", "IpSec"], "IPSEC" : ["oam", "Oam"]],
            [ "IPSEC" : ["oam", "Oam"], "OAM" : ["ipsec", "IpSec"]],
            [ "OAM" : ["oam"], "IPSEC" : ["ipsec", "IpSec"]],
            [ "OAM" : ["oam", "Oam"], "IPSEC" : ["ipsec", "IpSec", "other"]],
            [ "OAM" : ["oam", "Oam"], "IPSEC" : null ],
            [ "SSH" : [ "A" : "a", "B" : "b" ], "TLS" : [ "C" : "c", "D" : "d" ] ]
        ]
        expectedResult << [
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            true,
            true,
            true,
            true,
            false,
            false,
            false,
            false,
            false,
            false
        ]
    }

    @Unroll
    def 'Two Map of Map of String should match if equal case-sensitive, actual #actual'() {
        given:
        def expected = [ "SSH" : [ "A" : "a", "B" : "b" ], "TLS" : [ "C" : "c", "D" : "d" ] ]
        when:
        def result = capabilityGetCommandHandler.equalValue(expected, actual)
        then:
        result == expectedResult
        where:
        actual << [
            null,
            "expected",
            "",
            true,
            [],
            ["OAM", "IPSEC"],
            [ "OAM" : "oam", "IPSEC" : "ipsec" ],
            [ "OAM" : ["oam", "Oam"], "IPSEC" : ["ipsec", "IpSec"]],
            [ "SSH" : [ "A" : "a", "B" : "b" ], "TLS" : [ "C" : "c", "D" : "d" ] ],
            [ "TLS" : [ "C" : "c", "D" : "d" ], "SSH" : [ "A" : "a", "B" : "b" ] ],
            [ "TLS" : [ "D" : "d", "C" : "c" ], "SSH" : [ "A" : "a", "B" : "b" ] ],
            [ "SSH" : [ "B" : "b", "C" : "c" ], "TLS" : [ "C" : "c", "D" : "d" ] ],
            [ "TLS" : [ "A" : "a", "B" : "b" ], "SSH" : [ "C" : "c", "D" : "d" ] ],
        ]
        expectedResult << [
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            true,
            true,
            true,
            false,
            false
        ]
    }

    def 'get capability all' () {
        given:
        CapabilityGetCommand capabilityGetCommand = new CapabilityGetCommand()
        capabilityGetCommand.setProperties(["netype": "*"])
        when:
        NscsCommandResponse nscsCommandResponse = capabilityGetCommandHandler.process(capabilityGetCommand, commandContext)
        then:
        nscsCommandResponse != null
        and: 'response contains a multi-value response'
        nscsCommandResponse.isNameMultipleValueResponseType() == true
        and: 'response does not contain an error message response'
        nscsCommandResponse.isMessageResponseType() == false
    }

    @Unroll
    def 'get capability supported target type #targettype' () {
        given:
        CapabilityGetCommand capabilityGetCommand = new CapabilityGetCommand()
        capabilityGetCommand.setProperties(["netype": targettype])
        when:
        NscsCommandResponse nscsCommandResponse = capabilityGetCommandHandler.process(capabilityGetCommand, commandContext)
        then:
        nscsCommandResponse != null
        and: 'response contains a multi-value response'
        nscsCommandResponse.isNameMultipleValueResponseType() == true
        and: 'response does not contain an error message response'
        nscsCommandResponse.isMessageResponseType() == false
        where:
        targettype << [
            "TT_1",
            "TT_2",
            "TT_3",
            "TT_4"
        ]
    }

    def 'get capability unsupported target type' () {
        given:
        CapabilityGetCommand capabilityGetCommand = new CapabilityGetCommand()
        capabilityGetCommand.setProperties(["netype": "TT_UNKNOWN"])
        when:
        NscsCommandResponse nscsCommandResponse = capabilityGetCommandHandler.process(capabilityGetCommand, commandContext)
        then:
        nscsCommandResponse != null
        and: 'response does not contain a multi-value response'
        nscsCommandResponse.isNameMultipleValueResponseType() == false
        and: 'response contains an error message response'
        nscsCommandResponse.isMessageResponseType() == true
    }

    @Unroll
    def 'get capability supported target type #targettype under valid #targetcategory' () {
        given:
        CapabilityGetCommand capabilityGetCommand = new CapabilityGetCommand()
        capabilityGetCommand.setProperties(["netype": targettype, "targetcategory": targetcategory])
        when:
        NscsCommandResponse nscsCommandResponse = capabilityGetCommandHandler.process(capabilityGetCommand, commandContext)
        then:
        nscsCommandResponse != null
        and: 'response contains a multi-value response'
        nscsCommandResponse.isNameMultipleValueResponseType() == true
        and: 'response does not contain an error message response'
        nscsCommandResponse.isMessageResponseType() == false
        where:
        targetcategory << [
            "TC_1",
            "TC_1",
            "TC_2",
            "TC_2",
            "TC_3"
        ]
        targettype << [
            "TT_1",
            "TT_2",
            "TT_3",
            "TT_4",
            "TT_4"
        ]
    }

    @Unroll
    def 'get capability supported target type #targettype under unsupported #targetcategory' () {
        given:
        CapabilityGetCommand capabilityGetCommand = new CapabilityGetCommand()
        capabilityGetCommand.setProperties(["netype": targettype, "targetcategory": targetcategory])
        when:
        NscsCommandResponse nscsCommandResponse = capabilityGetCommandHandler.process(capabilityGetCommand, commandContext)
        then:
        nscsCommandResponse != null
        and: 'response does not contain a multi-value response'
        nscsCommandResponse.isNameMultipleValueResponseType() == false
        and: 'response contains an error message response'
        nscsCommandResponse.isMessageResponseType() == true
        where:
        targetcategory << [
            "TC_2",
            "TC_UNKNOWN"
        ]
        targettype << [
            "TT_1",
            "TT_1"
        ]
    }

    @Unroll
    def 'get capability supported target type #targettype under unsupported #targetcategory with skipconsistencycheck flag' () {
        given:
        CapabilityGetCommand capabilityGetCommand = new CapabilityGetCommand()
        capabilityGetCommand.setProperties(["netype": targettype, "targetcategory": targetcategory, "skipconsistencycheck": ""])
        when:
        NscsCommandResponse nscsCommandResponse = capabilityGetCommandHandler.process(capabilityGetCommand, commandContext)
        then:
        nscsCommandResponse != null
        and: 'response contains a multi-value response'
        nscsCommandResponse.isNameMultipleValueResponseType() == true
        and: 'response does not contain an error message response'
        nscsCommandResponse.isMessageResponseType() == false
        where:
        targetcategory << [
            "TC_2",
            "TC_UNKNOWN"
        ]
        targettype << [
            "TT_1",
            "TT_1"
        ]
    }

    @Unroll
    def 'get capability supported target type #targettype under valid #targetcategory for supported #targetmodelidentity' () {
        given:
        CapabilityGetCommand capabilityGetCommand = new CapabilityGetCommand()
        capabilityGetCommand.setProperties(["netype": targettype, "targetcategory": targetcategory, "ossmodelidentity": targetmodelidentity])
        when:
        NscsCommandResponse nscsCommandResponse = capabilityGetCommandHandler.process(capabilityGetCommand, commandContext)
        then:
        nscsCommandResponse != null
        and: 'response contains a multi-value response'
        nscsCommandResponse.isNameMultipleValueResponseType() == true
        and: 'response does not contain an error message response'
        nscsCommandResponse.isMessageResponseType() == false
        where:
        targetcategory << [
            "TC_1",
            "TC_1",
            "TC_1",
            "TC_2"
        ]
        targettype << [
            "TT_1",
            "TT_1",
            "TT_2",
            "TT_4"
        ]
        targetmodelidentity << [
            "TMI_1",
            "TMI_2",
            "TMI_3",
            "TMI_3"
        ]
    }

    @Unroll
    def 'get capability supported target type #targettype under valid #targetcategory for null targetmodelidentity' () {
        given:
        CapabilityGetCommand capabilityGetCommand = new CapabilityGetCommand()
        capabilityGetCommand.setProperties(["netype": targettype, "targetcategory": targetcategory, "ossmodelidentity": "<null>"])
        when:
        NscsCommandResponse nscsCommandResponse = capabilityGetCommandHandler.process(capabilityGetCommand, commandContext)
        then:
        nscsCommandResponse != null
        and: 'response contains a multi-value response'
        nscsCommandResponse.isNameMultipleValueResponseType() == true
        and: 'response does not contain an error message response'
        nscsCommandResponse.isMessageResponseType() == false
        where:
        targetcategory << [
            "TC_1",
            "TC_1",
            "TC_1",
            "TC_2"
        ]
        targettype << [
            "TT_1",
            "TT_1",
            "TT_2",
            "TT_4"
        ]
    }

    @Unroll
    def 'get capability supported target type #targettype under valid #targetcategory for unsupported #targetmodelidentity' () {
        given:
        CapabilityGetCommand capabilityGetCommand = new CapabilityGetCommand()
        capabilityGetCommand.setProperties(["netype": targettype, "targetcategory": targetcategory, "ossmodelidentity": targetmodelidentity])
        when:
        NscsCommandResponse nscsCommandResponse = capabilityGetCommandHandler.process(capabilityGetCommand, commandContext)
        then:
        nscsCommandResponse != null
        and: 'response does not contain a multi-value response'
        nscsCommandResponse.isNameMultipleValueResponseType() == false
        and: 'response contains an error message response'
        nscsCommandResponse.isMessageResponseType() == true
        where:
        targetcategory << [
            "TC_1",
            "TC_2",
            "TC_3"
        ]
        targettype << [
            "TT_1",
            "TT_3",
            "TT_4"
        ]
        targetmodelidentity << [
            "TMI_UNKNOW",
            "TMI_ANY",
            "TM_ANY"
        ]
    }

    @Unroll
    def 'get capability supported target type #targettype under valid #targetcategory for unsupported #targetmodelidentity with skipconsistencycheck flag' () {
        given:
        CapabilityGetCommand capabilityGetCommand = new CapabilityGetCommand()
        capabilityGetCommand.setProperties(["netype": targettype, "targetcategory": targetcategory, "ossmodelidentity": targetmodelidentity, "skipconsistencycheck": ""])
        when:
        NscsCommandResponse nscsCommandResponse = capabilityGetCommandHandler.process(capabilityGetCommand, commandContext)
        then:
        nscsCommandResponse != null
        and: 'response contains a multi-value response'
        nscsCommandResponse.isNameMultipleValueResponseType() == true
        and: 'response does not contain an error message response'
        nscsCommandResponse.isMessageResponseType() == false
        where:
        targetcategory << [
            "TC_1",
            "TC_2",
            "TC_3"
        ]
        targettype << [
            "TT_1",
            "TT_3",
            "TT_4"
        ]
        targetmodelidentity << [
            "TMI_UNKNOW",
            "TMI_ANY",
            "TMI_ANY"
        ]
    }

    def 'get capability all targets supported capability name' () {
        given:
        CapabilityGetCommand capabilityGetCommand = new CapabilityGetCommand()
        capabilityGetCommand.setProperties(["netype": "*", "capabilityname": "credentialsParams"])
        when:
        NscsCommandResponse nscsCommandResponse = capabilityGetCommandHandler.process(capabilityGetCommand, commandContext)
        then:
        nscsCommandResponse != null
        and: 'response contains a multi-value response'
        nscsCommandResponse.isNameMultipleValueResponseType() == true
        and: 'response does not contain an error message response'
        nscsCommandResponse.isMessageResponseType() == false
    }

    def 'get capability supported capability name' () {
        given:
        CapabilityGetCommand capabilityGetCommand = new CapabilityGetCommand()
        capabilityGetCommand.setProperties(["netype": "TT_1", "capabilityname": "credentialsParams"])
        when:
        NscsCommandResponse nscsCommandResponse = capabilityGetCommandHandler.process(capabilityGetCommand, commandContext)
        then:
        nscsCommandResponse != null
        and: 'response contains a multi-value response'
        nscsCommandResponse.isNameMultipleValueResponseType() == true
        and: 'response does not contain an error message response'
        nscsCommandResponse.isMessageResponseType() == false
    }

    def 'get capability unsupported capability name' () {
        given:
        CapabilityGetCommand capabilityGetCommand = new CapabilityGetCommand()
        capabilityGetCommand.setProperties(["netype": "TT_1", "capabilityname": "unknown"])
        when:
        NscsCommandResponse nscsCommandResponse = capabilityGetCommandHandler.process(capabilityGetCommand, commandContext)
        then:
        nscsCommandResponse != null
        and: 'response does not contain a multi-value response'
        nscsCommandResponse.isNameMultipleValueResponseType() == false
        and: 'response contains an error message response'
        nscsCommandResponse.isMessageResponseType() == true
    }
}
