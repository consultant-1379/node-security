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
package com.ericsson.nms.security.nscs.api.iscf

import com.ericsson.cds.cdi.support.spock.CdiSpecification

import spock.lang.Unroll

class NodeIdentifierTest extends CdiSpecification {

    @Unroll
    def "constructor by fdn #fdn and serial number #sn"() {
        given:
        def NodeIdentifier nodeIdentifier = new NodeIdentifier(fdn, sn)
        expect:
        nodeIdentifier.getFdn() == fdn
        and:
        nodeIdentifier.getSerialNumber() == sn
        and:
        nodeIdentifier.toString() != null
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

    def 'equality to itself'() {
        given:
        def NodeIdentifier nodeIdentifier = new NodeIdentifier("NODE", null)
        expect:
        nodeIdentifier.equals(nodeIdentifier) == true
    }

    def 'equality to null'() {
        given:
        def NodeIdentifier nodeIdentifier = new NodeIdentifier("NODE", null)
        expect:
        nodeIdentifier.equals(null) == false
    }

    def 'equality to other class'() {
        given:
        def NodeIdentifier nodeIdentifier = new NodeIdentifier("NODE", null)
        and:
        def other = new Object()
        expect:
        nodeIdentifier.equals(other) == false
    }

    @Unroll
    def 'equality by FDN #fdn vs #otherfdn'() {
        given:
        def NodeIdentifier nodeIdentifier = new NodeIdentifier(fdn, null)
        and:
        def NodeIdentifier otherNodeIdentifier = new NodeIdentifier(otherfdn, null)
        expect:
        nodeIdentifier.equals(otherNodeIdentifier) == isequal
        and:
        if (isequal) {
            nodeIdentifier.hashCode() == otherNodeIdentifier.hashCode()
        } else {
            nodeIdentifier.hashCode() != otherNodeIdentifier.hashCode()
        }
        where:
        fdn << [
            "NODE",
            "NetworkElement=NODE",
            null,
            "NODE",
            "NetworkElement=NODE",
            null,
            "NODE",
            "NetworkElement=NODE",
            null,
            "NODE",
            "NetworkElement=NODE",
            "NODE",
            "NetworkElement=NODE"
        ]
        otherfdn << [
            "NODE",
            "NODE",
            "NODE",
            "NetworkElement=NODE",
            "NetworkElement=NODE",
            "NetworkElement=NODE",
            null,
            null,
            null,
            "OTHERNODE",
            "OTHERNODE",
            "NetworkElement=OTHERNODE",
            "NetworkElement=OTHERNODE"
        ]
        isequal << [
            true,
            false,
            false,
            false,
            true,
            false,
            false,
            false,
            true,
            false,
            false,
            false,
            false
        ]
    }

    @Unroll
    def 'equality by serial number #sn vs #othersn'() {
        given:
        def NodeIdentifier nodeIdentifier = new NodeIdentifier("NODE", sn)
        and:
        def NodeIdentifier otherNodeIdentifier = new NodeIdentifier("NODE", othersn)
        expect:
        nodeIdentifier.equals(otherNodeIdentifier) == isequal
        and:
        if (isequal) {
            nodeIdentifier.hashCode() == otherNodeIdentifier.hashCode()
        } else {
            nodeIdentifier.hashCode() != otherNodeIdentifier.hashCode()
        }
        where:
        sn << [
            "this is the SN",
            null,
            "this is the SN",
            null,
            "this is the SN"
        ]
        othersn << [
            "this is the SN",
            "this is the SN",
            null,
            null,
            "this is another SN"
        ]
        isequal << [
            true,
            false,
            false,
            true,
            false
        ]
    }
}
