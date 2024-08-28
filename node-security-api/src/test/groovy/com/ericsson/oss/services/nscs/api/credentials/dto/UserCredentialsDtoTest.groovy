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
import com.ericsson.nms.security.nscs.api.enums.CredentialsType

import spock.lang.Shared
import spock.lang.Unroll

class UserCredentialsDtoTest extends CdiSpecification {

    @ObjectUnderTest
    UserCredentialsDto dto

    @Shared
    credentialsTypes = CredentialsType.values()

    def "no-args constructor"() {
        expect:
        dto != null
        and:
        dto.getCredType() == null
        and:
        dto.getCredUser() == null
        and:
        dto.getCredPass() == null
    }

    @Unroll
    def "set credentials type #credType"() {
        given:
        dto.setCredType(credType)
        expect:
        dto.getCredType() == credType
        where:
        credType << credentialsTypes
    }

    def "set null credentials type"() {
        given:
        dto.setCredType(null)
        expect:
        dto.getCredType() == null
    }

    @Unroll
    def "set credentials user #credUser"() {
        given:
        dto.setCredUser(credUser)
        expect:
        dto.getCredUser() == credUser
        where:
        credUser << [null, "", "this is the user"]
    }

    @Unroll
    def "set credentials pass #credPass"() {
        given:
        dto.setCredPass(credPass)
        expect:
        dto.getCredPass() == credPass
        where:
        credPass << [null, "", "this is the pass"]
    }

    @Unroll
    def "toString should not contain the credentials pass for #credType"() {
        given:
        dto.setCredType(credType)
        dto.setCredUser("this is the user")
        dto.setCredPass("this is the pass")
        when:
        def str = dto.toString()
        then:
        str.contains("this is the pass") == false
        where:
        credType << credentialsTypes
    }
}
