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
package com.ericsson.oss.services.nscs.api.credentials.dto

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification

import spock.lang.Unroll

class NodeCredentialsDtoTest extends CdiSpecification {

    @ObjectUnderTest
    NodeCredentialsDto dto

    def "no-args constructor"() {
        expect:
        dto != null
        and:
        dto.getNodeNameOrFdn() == null
        and:
        dto.getNodeCredentials() == null
        and:
        dto.toString() != null
    }

    @Unroll
    def "set node name or fdn #nodeNameOrFdn"() {
        given:
        dto.setNodeNameOrFdn(nodeNameOrFdn)
        expect:
        dto.getNodeNameOrFdn() == nodeNameOrFdn
        and:
        dto.toString() != null
        where:
        nodeNameOrFdn << [
            null,
            "",
            "this is the node name or fdn"
        ]
    }

    def "set node credentials"() {
        given:
        def nodeCredentials = mock(CredentialAttributesDto)
        and:
        dto.setNodeCredentials(nodeCredentials)
        when:
        def nodeCreds = dto.getNodeCredentials()
        then:
        nodeCreds != null
        and:
        dto.toString() != null
    }

    def "set null node credentials"() {
        given:
        dto.setNodeCredentials(null)
        expect:
        dto.getNodeCredentials() == null
        and:
        dto.toString() != null
    }

    def "not null node credentials to string" () {
        given:
        dto.setNodeNameOrFdn("this is the node name or fdn")
        and:
        def nodeCredentials = mock(CredentialAttributesDto)
        and:
        nodeCredentials.toString() >> "this is the node credentials list"
        and:
        dto.setNodeCredentials(nodeCredentials)
        when:
        String toStr = dto.toString()
        then:
        notThrown(Exception)
        and:
        toStr != null
        and:
        toStr.isEmpty() == false
    }

    def "null node credentials to string" () {
        given:
        dto.setNodeCredentials(null)
        when:
        String toStr = dto.toString()
        then:
        notThrown(Exception)
        and:
        toStr != null
        and:
        toStr.isEmpty() == false
    }
}
