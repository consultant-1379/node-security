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

import spock.lang.Shared
import spock.lang.Unroll

class IscfBaseDtoTest extends CdiSpecification {

    @ObjectUnderTest
    IscfBaseDto dto

    @Shared
    NodeModelInformationDto nodeModelInfo = new NodeModelInformationDto()

    @Shared
    enrollmentModes = EnrollmentMode.values()

    def "no-args constructor"() {
        expect:
        dto != null
        and:
        dto.getNodeModelInfo() == null
        and:
        dto.getEnrollmentMode() == null
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
}
