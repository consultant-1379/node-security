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
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation.ModelIdentifierType

import spock.lang.Shared
import spock.lang.Unroll

class IscfXmlComboDtoTest extends CdiSpecification {

    @ObjectUnderTest
    IscfXmlComboDto dto

    @Shared
    NodeModelInformationDto nodeModelInfo = new NodeModelInformationDto("this is the target category", "this is the target type", ModelIdentifierType.OSS_IDENTIFIER, "this is the model identifier")

    @Shared
    enrollmentModes = EnrollmentMode.values()

    @Shared
    IscfXmlOamParamsDto oamParams = mock(IscfXmlOamParamsDto)

    @Shared
    IscfXmlIpsecParamsDto ipsecParams = mock(IscfXmlIpsecParamsDto)

    def "no-args constructor"() {
        expect:
        dto != null
        and:
        dto.getLogicalName() == null
        and:
        dto.getNodeFdn() == null
        and:
        dto.getNodeModelInfo() == null
        and:
        dto.getEnrollmentMode() == null
        and:
        dto.getOamParams() == null
        and:
        dto.getIpsecParams() == null
    }

    @Unroll
    def "logical name #logicalName"() {
        given:
        dto.setLogicalName(logicalName)
        expect:
        dto.getLogicalName() == logicalName
        where:
        logicalName << [
            null,
            "",
            "this is the logical name"
        ]
    }

    @Unroll
    def "node FDN #nodeFdn"() {
        given:
        dto.setNodeFdn(nodeFdn)
        expect:
        dto.getNodeFdn() == nodeFdn
        where:
        nodeFdn << [
            null,
            "",
            "this is the node FDN"
        ]
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
    def "OAM parameters #params"() {
        given:
        dto.setOamParams(params)
        expect:
        dto.getOamParams() == params
        where:
        params << [
            null,
            oamParams
        ]
    }

    @Unroll
    def "IPSEC parameters #params"() {
        given:
        dto.setIpsecParams(params)
        expect:
        dto.getIpsecParams() == params
        where:
        params << [
            null,
            ipsecParams
        ]
    }
}
