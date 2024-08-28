/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2022
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.nscs.api.iscf.dto

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.services.nscs.api.iscf.dto.NodeIdentifierDto

import spock.lang.Unroll

class NodeIdentifierDtoTest extends CdiSpecification {

    def "no-args constructor"() {
        given:
        def NodeIdentifierDto nodeIdentifier = new NodeIdentifierDto()
        expect:
        nodeIdentifier.getNodeFdn() == null
        and:
        nodeIdentifier.getNodeSn() == null
    }

    @Unroll
    def "constructor by fdn #fdn and serial number #sn"() {
        given:
        def NodeIdentifierDto nodeIdentifier = new NodeIdentifierDto(fdn, sn)
        expect:
        nodeIdentifier.getNodeFdn() == fdn
        and:
        nodeIdentifier.getNodeSn() == sn
        where:
        fdn << [
            null,
            "this is the fdn",
            null,
            "this is the fdn"
        ]
        sn << [
            null,
            null,
            "this is the sn",
            "this is the sn"
        ]
    }

    @Unroll
    def "set fdn #fdn"() {
        given:
        def NodeIdentifierDto nodeIdentifier = new NodeIdentifierDto()
        when:
        nodeIdentifier.setNodeFdn(fdn)
        then:
        nodeIdentifier.getNodeFdn() == fdn
        and:
        nodeIdentifier.getNodeSn() == null
        where:
        fdn << [
            null,
            "this is the fdn"
        ]
    }

    @Unroll
    def "set serial number #sn"() {
        given:
        def NodeIdentifierDto nodeIdentifier = new NodeIdentifierDto()
        when:
        nodeIdentifier.setNodeSn(sn)
        then:
        nodeIdentifier.getNodeFdn() == null
        and:
        nodeIdentifier.getNodeSn() == sn
        where:
        sn << [
            null,
            "this is the sn"
        ]
    }
}
