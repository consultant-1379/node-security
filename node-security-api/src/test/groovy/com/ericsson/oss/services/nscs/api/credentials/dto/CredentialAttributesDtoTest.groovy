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

import spock.lang.Shared
import spock.lang.Unroll

class CredentialAttributesDtoTest extends CdiSpecification {

    @ObjectUnderTest
    CredentialAttributesDto dto

    @Shared
    private UserCredentialsDto credDto1 = mock(UserCredentialsDto)

    @Shared
    private UserCredentialsDto credDto2 = mock(UserCredentialsDto)

    def "no-args constructor"() {
        expect:
        dto != null
        and:
        dto.getCredentialsList() == []
        and:
        dto.toString() != null
    }

    @Unroll
    def "set credentials list #credentialsList"() {
        given:
        dto.setCredentialsList(credentialsList)
        when:
        def List<UserCredentialsDto> credsList = dto.getCredentialsList()
        then:
        credsList != null
        and:
        credsList.isEmpty() == false
        and:
        credsList.size() == size
        and:
        dto.toString() != null
        where:
        credentialsList << [
            [credDto1],
            [credDto1, credDto2]
        ]
        size << [1, 2]
    }

    @Unroll
    def "set null or empty credentials list #credentialsList"() {
        given:
        def List<UserCredentialsDto> credsList = dto.setCredentialsList(credentialsList)
        expect:
        credsList == null
        and:
        dto.toString() != null
        where:
        credentialsList << [
            null,
            []
        ]
    }
}
