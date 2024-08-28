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
package com.ericsson.oss.services.security.nscs.credentials.dto

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.credentials.CredentialAttributes
import com.ericsson.nms.security.nscs.api.enums.CredentialsType
import com.ericsson.nms.security.nscs.api.exception.NscsBadRequestException
import com.ericsson.oss.services.nscs.api.credentials.dto.CredentialAttributesDto
import com.ericsson.oss.services.nscs.api.credentials.dto.NodeCredentialsDto
import com.ericsson.oss.services.nscs.api.credentials.dto.UserCredentialsDto

import spock.lang.Shared
import spock.lang.Unroll

class CredentialDtoHelperTest extends CdiSpecification {

    private String nodeNameOrFdn = "this is the node name or fdn"

    @Shared
    def credentialsTypes = CredentialsType.values()

    def "validate valid node credential"() {
        given:
        NodeCredentialsDto dto = new NodeCredentialsDto()
        when:
        CredentialDtoHelper.validate(dto)
        then:
        notThrown(NscsBadRequestException)
    }

    @Unroll
    def "convert valid credentials dto with #credType credentials"() {
        given:
        UserCredentialsDto creds1 = new UserCredentialsDto()
        creds1.setCredType(credType)
        creds1.setCredUser("this is the user")
        creds1.setCredPass("this is the pass")
        and:
        def dto = new CredentialAttributesDto()
        dto.setCredentialsList([creds1])
        when:
        def CredentialAttributes credentialAttributes = CredentialDtoHelper.fromDto(dto)
        then:
        credentialAttributes != null
        and:
        notThrown(NscsBadRequestException)
        where:
        credType << credentialsTypes
    }

    @Unroll
    def "convert valid credentials dto with #credUser and #credPass credentials"() {
        given:
        UserCredentialsDto creds1 = new UserCredentialsDto()
        creds1.setCredType(CredentialsType.ROOT)
        creds1.setCredUser(credUser)
        creds1.setCredPass(credPass)
        and:
        def dto = new CredentialAttributesDto()
        dto.setCredentialsList([creds1])
        when:
        def CredentialAttributes credentialAttributes = CredentialDtoHelper.fromDto(dto)
        then:
        credentialAttributes != null
        and:
        notThrown(NscsBadRequestException)
        where:
        credUser << [
            "this is the user",
            "this is the user",
            null
        ]
        credPass << [
            "this is the pass",
            null,
            "this is the pass"
        ]
    }

    def "convert null credentials dto"() {
        given:
        when:
        CredentialDtoHelper.fromDto((CredentialAttributesDto)null)
        then:
        thrown(NscsBadRequestException)
    }

    def "convert invalid credentials dto due to null credentials type"() {
        given:
        UserCredentialsDto creds1 = new UserCredentialsDto()
        creds1.setCredType(null)
        creds1.setCredUser("this is the user")
        creds1.setCredPass("this is the pass")
        and:
        def dto = new CredentialAttributesDto()
        dto.setCredentialsList([creds1])
        when:
        def CredentialAttributes credentialAttributes = CredentialDtoHelper.fromDto(dto)
        then:
        thrown(NscsBadRequestException)
    }

    @Unroll
    def "convert invalid credentials dto due to invalide #credType credentials"() {
        given:
        UserCredentialsDto creds1 = new UserCredentialsDto()
        creds1.setCredType(credType)
        creds1.setCredUser(null)
        creds1.setCredPass(null)
        and:
        def dto = new CredentialAttributesDto()
        dto.setCredentialsList([creds1])
        when:
        def CredentialAttributes credentialAttributes = CredentialDtoHelper.fromDto(dto)
        then:
        thrown(NscsBadRequestException)
        where:
        credType << credentialsTypes
    }
}
