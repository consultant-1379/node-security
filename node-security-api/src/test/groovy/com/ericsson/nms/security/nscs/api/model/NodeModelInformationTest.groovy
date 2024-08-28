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
package com.ericsson.nms.security.nscs.api.model

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation.ModelIdentifierType

import spock.lang.Shared
import spock.lang.Unroll

class NodeModelInformationTest extends CdiSpecification {

    @Shared
    modelIdentifierTypes = ModelIdentifierType.values()

    @Unroll
    def "constructor with model identifier type #modelIdentifierType"() {
        given:
        def NodeModelInformation nodeModelInformation = new NodeModelInformation("this is the model identifier", modelIdentifierType, "this is the node type")
        expect:
        nodeModelInformation.getModelIdentifier() == "this is the model identifier"
        and:
        nodeModelInformation.getModelIdentifierType() == modelIdentifierType
        and:
        nodeModelInformation.getNodeType() == "this is the node type"
        and:
        nodeModelInformation.toString() != null
        where:
        modelIdentifierType << modelIdentifierTypes
    }
}
