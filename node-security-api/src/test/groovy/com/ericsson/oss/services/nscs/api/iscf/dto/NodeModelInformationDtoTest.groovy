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
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation.ModelIdentifierType

import spock.lang.Shared
import spock.lang.Unroll

class NodeModelInformationDtoTest extends CdiSpecification {

    @Shared
    modelIdentifierTypes = ModelIdentifierType.values()

    def "no-args constructor"() {
        given:
        def dto = new NodeModelInformationDto()
        expect:
        dto.getTargetCategory() == null
        and:
        dto.getTargetType() == null
        and:
        dto.getModelIdentifierType() == null
        and:
        dto.getTargetModelIdentifier() == null
    }

    def "fields constructor"() {
        given:
        def dto = new NodeModelInformationDto("this is the target category", "this is the target type", ModelIdentifierType.OSS_IDENTIFIER, "this is the model identifier")
        expect:
        dto.getTargetCategory() == "this is the target category"
        and:
        dto.getTargetType() == "this is the target type"
        and:
        dto.getModelIdentifierType() == ModelIdentifierType.OSS_IDENTIFIER
        and:
        dto.getTargetModelIdentifier() == "this is the model identifier"
    }

    @Unroll
    def "set target category #targetCategory"() {
        given:
        def dto = new NodeModelInformationDto()
        when:
        dto.setTargetCategory(targetCategory)
        then:
        dto.getTargetCategory() == targetCategory
        and:
        dto.getTargetType() == null
        and:
        dto.getModelIdentifierType() == null
        and:
        dto.getTargetModelIdentifier() == null
        where:
        targetCategory << [
            null,
            "",
            "this is the target category"
        ]
    }

    @Unroll
    def "set target type #targetType"() {
        given:
        def dto = new NodeModelInformationDto()
        when:
        dto.setTargetType(targetType)
        then:
        dto.getTargetCategory() == null
        and:
        dto.getTargetType() == targetType
        and:
        dto.getModelIdentifierType() == null
        and:
        dto.getTargetModelIdentifier() == null
        where:
        targetType << [
            null,
            "",
            "this is the target type"
        ]
    }

    @Unroll
    def "set model identifier type #modelIdentifierType"() {
        given:
        def dto = new NodeModelInformationDto()
        when:
        dto.setModelIdentifierType(modelIdentifierType)
        then:
        dto.getTargetCategory() == null
        and:
        dto.getTargetType() == null
        and:
        dto.getModelIdentifierType() == modelIdentifierType
        and:
        dto.getTargetModelIdentifier() == null
        where:
        modelIdentifierType << modelIdentifierTypes
    }

    @Unroll
    def "set target model identifier #targetModelIdentifier"() {
        given:
        def dto = new NodeModelInformationDto()
        when:
        dto.setTargetModelIdentifier(targetModelIdentifier)
        then:
        dto.getTargetCategory() == null
        and:
        dto.getTargetType() == null
        and:
        dto.getModelIdentifierType() == null
        and:
        dto.getTargetModelIdentifier() == targetModelIdentifier
        where:
        targetModelIdentifier << [
            null,
            "",
            "this is the target model identifier"
        ]
    }
}
