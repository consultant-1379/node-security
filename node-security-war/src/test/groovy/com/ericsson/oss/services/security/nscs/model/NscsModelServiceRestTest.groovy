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
package com.ericsson.oss.services.security.nscs.model

import javax.ws.rs.core.Response

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.oss.services.security.nscs.util.NscsRestCdiSpecification

import spock.lang.Unroll

class NscsModelServiceRestTest extends NscsRestCdiSpecification {

    @ObjectUnderTest
    NscsModelServiceRest nscsModelServiceRest

    @MockedImplementation
    NscsModelManager modelManager

    def "object under test should be not null"() {
        expect:
        nscsModelServiceRest != null
    }

    @Unroll
    def "get target info for supported target #category #type #tmi"() {
        given:
        def targetCategory = category
        def targetType = type
        def targetModelIdentity = tmi
        when:
        Response response = nscsModelServiceRest.getTargetInfo(targetCategory, targetType, targetModelIdentity)
        then:
        response != null
        where:
        category << [
            null,
            "NODE",
            null,
            "NODE",
            "NODE"
        ]
        type << [
            null,
            null,
            "RadioNode",
            "RadioNode",
            "RadioNode"
        ]
        tmi << [
            null,
            null,
            null,
            null,
            "20.Q3-R13A40"
        ]
    }

    def "get model info"() {
        given:
        def targetCategory = "this is the target category"
        def targetType = "this is the target type"
        def targetModelIdentity = "this is the target model identity"
        def namespace = "this is the namespace"
        def type = "this is the type"
        when:
        nscsModelServiceRest.getModelInfo(targetCategory, targetType, targetModelIdentity, namespace, type)
        then:
        1 * modelManager.getModelInfo(targetCategory, targetType, targetModelIdentity, namespace, type)
    }

    def "get target PO"() {
        given:
        def fdn = "this is the node fdn"
        when:
        nscsModelServiceRest.getTargetPO(fdn)
        then:
        1 * modelManager.getTargetPO(fdn)
    }
}
