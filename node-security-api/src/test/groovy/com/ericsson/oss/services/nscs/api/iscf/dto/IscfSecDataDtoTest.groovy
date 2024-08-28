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
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation.ModelIdentifierType

import spock.lang.Shared
import spock.lang.Unroll

class IscfSecDataDtoTest extends CdiSpecification {

    @ObjectUnderTest
    IscfSecDataDto dto

    @Shared
    NodeModelInformationDto nodeModelInfo = new NodeModelInformationDto("this is the target category", "this is the target type", ModelIdentifierType.OSS_IDENTIFIER, "this is the model identifier")

    @Shared
    NodeIdentifierDto nodeIdentifier = new NodeIdentifierDto("this is the fdn", "this is the serial number")

    @Shared
    enrollmentModes = EnrollmentMode.values()

    @Shared
    SubjectAltNameParamDto subjectAltNameStringType = new SubjectAltNameParamDto(SubjectAltNameFormat.IPV4, "11.22.33.44")

    @Shared
    SubjectAltNameParamDto subjectAltNameEdiParty = new SubjectAltNameParamDto("this is the name assigner", "this is the party name")

    @Shared
    ipVersions = StandardProtocolFamily.values()

    def "no-args constructor"() {
        expect:
        dto != null
        dto.getNodeModelInfo() == null
        and:
        dto.getEnrollmentMode() == null
        and:
        dto.getNodeId() == null
        and:
        dto.getSubjectAltNameParam() == null
        and:
        dto.getIpVersion() == null
    }

    @Unroll
    def "node model information #nmi"() {
        given:
        dto.setNodeModelInfo(nmi)
        expect:
        dto.getNodeModelInfo() == nmi
        where:
        nmi << [
            null,
            nodeModelInfo
        ]
    }

    @Unroll
    def "enrollment mode #enrollmentMode"() {
        given:
        dto.setEnrollmentMode(enrollmentMode)
        expect:
        dto.getEnrollmentMode() == enrollmentMode
        where:
        enrollmentMode << enrollmentModes
    }

    def "null enrollment mode"() {
        given:
        dto.setEnrollmentMode(null)
        expect:
        dto.getEnrollmentMode() == null
    }

    @Unroll
    def "node identifier #nodeid"() {
        given:
        dto.setNodeId(nodeid)
        expect:
        dto.getNodeId() == nodeid
        where:
        nodeid << [
            null,
            nodeIdentifier
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
    def "IP version #ipver"() {
        given:
        dto.setIpVersion(ipver)
        expect:
        dto.getIpVersion() == ipver
        where:
        ipver << ipVersions
    }

    def "null IP version"() {
        given:
        dto.setIpVersion(null)
        expect:
        dto.getIpVersion() == null
    }
}
