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

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.iscf.IpsecArea
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat

import spock.lang.Shared
import spock.lang.Unroll

class IscfXmlIpsecParamsDtoTest extends CdiSpecification {

    @ObjectUnderTest
    IscfXmlIpsecParamsDto dto

    @Shared
    SubjectAltNameParamDto subjectAltNameStringType = new SubjectAltNameParamDto(SubjectAltNameFormat.IPV4, "11.22.33.44")

    @Shared
    SubjectAltNameParamDto subjectAltNameEdiParty = new SubjectAltNameParamDto("this is the name assigner", "this is the party name")

    def "no-args constructor"() {
        expect:
        dto.getUserLabel() == null
        and:
        dto.getSubjectAltNameParam() == null
        and:
        dto.getIpsecAreas() == null
    }

    @Unroll
    def "user label #userLabel"() {
        given:
        dto.setUserLabel(userLabel)
        expect:
        dto.getUserLabel() == userLabel
        where:
        userLabel << [
            null,
            "",
            "this is the user label"
        ]
    }

    @Unroll
    def "subject alternative name #san"() {
        given:
        dto.setSubjectAltNameParam(san)
        expect:
        dto.getSubjectAltNameParam() == san
        where:
        san << [
            null,
            subjectAltNameStringType,
            subjectAltNameEdiParty
        ]
    }

    @Unroll
    def "IPSEC areas #ipsecAreas"() {
        given:
        dto.setIpsecAreas(ipsecAreas)
        expect:
        dto.getIpsecAreas() == ipsecAreas
        where:
        ipsecAreas << [
            null,
            [] as Set,
            [IpsecArea.TRANSPORT] as Set,
            [IpsecArea.OM] as Set,
            [
                IpsecArea.OM,
                IpsecArea.TRANSPORT
            ] as Set
        ]
    }
}
