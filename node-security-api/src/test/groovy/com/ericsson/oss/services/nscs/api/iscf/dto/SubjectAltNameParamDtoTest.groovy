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
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat

import spock.lang.Shared
import spock.lang.Unroll

class SubjectAltNameParamDtoTest extends CdiSpecification {

    @Shared
    subjectAltNameFormats = SubjectAltNameFormat.values()

    def "no-args constructor"() {
        given:
        def dto = new SubjectAltNameParamDto()
        expect:
        dto.getSubjectAltNameFormat() == null
        and:
        dto.getValue() == null
        and:
        dto.getNameAssigner() == null
        and:
        dto.getPartyName() == null
    }

    @Unroll
    def "simple string name constructor #subjectAltNameFormat"() {
        given:
        def dto = new SubjectAltNameParamDto(subjectAltNameFormat, "this is the value")
        expect:
        dto.getSubjectAltNameFormat() == subjectAltNameFormat
        and:
        dto.getValue() == "this is the value"
        and:
        dto.getNameAssigner() == null
        and:
        dto.getPartyName() == null
        where:
        subjectAltNameFormat << subjectAltNameFormats
    }

    def "EDI party name constructor"() {
        given:
        def dto = new SubjectAltNameParamDto("this is the name assigner", "this is the party name")
        expect:
        dto.getSubjectAltNameFormat() == SubjectAltNameFormat.FQDN
        and:
        dto.getValue() == null
        and:
        dto.getNameAssigner() == "this is the name assigner"
        and:
        dto.getPartyName() == "this is the party name"
    }

    @Unroll
    def "set subject alt name format #subjectAltNameFormat"() {
        given:
        def dto = new SubjectAltNameParamDto()
        when:
        dto.setSubjectAltNameFormat(subjectAltNameFormat)
        then:
        dto.getSubjectAltNameFormat() == subjectAltNameFormat
        and:
        dto.getValue() == null
        and:
        dto.getNameAssigner() == null
        and:
        dto.getPartyName() == null
        where:
        subjectAltNameFormat << subjectAltNameFormats
    }

    @Unroll
    def "set value #value"() {
        given:
        def dto = new SubjectAltNameParamDto()
        when:
        dto.setValue(value)
        then:
        dto.getSubjectAltNameFormat() == null
        and:
        dto.getValue() == value
        and:
        dto.getNameAssigner() == null
        and:
        dto.getPartyName() == null
        where:
        value << [
            null,
            "",
            "this is the value"
        ]
    }

    @Unroll
    def "set name assigner #nameAssigner"() {
        given:
        def dto = new SubjectAltNameParamDto()
        when:
        dto.setNameAssigner(nameAssigner)
        then:
        dto.getSubjectAltNameFormat() == null
        and:
        dto.getValue() == null
        and:
        dto.getNameAssigner() == nameAssigner
        and:
        dto.getPartyName() == null
        where:
        nameAssigner << [
            null,
            "",
            "this is the name assigner"
        ]
    }

    @Unroll
    def "set party name #partyName"() {
        given:
        def dto = new SubjectAltNameParamDto()
        when:
        dto.setPartyName(partyName)
        then:
        dto.getSubjectAltNameFormat() == null
        and:
        dto.getValue() == null
        and:
        dto.getNameAssigner() == null
        and:
        dto.getPartyName() == partyName
        where:
        partyName << [
            null,
            "",
            "this is the party name"
        ]
    }
}
