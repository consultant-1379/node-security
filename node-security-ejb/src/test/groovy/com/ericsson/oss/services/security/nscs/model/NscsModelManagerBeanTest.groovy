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

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.model.service.NscsModelService

class NscsModelManagerBeanTest extends CdiSpecification {

    @ObjectUnderTest
    NscsModelManagerBean nscsModelManager

    @MockedImplementation
    NscsModelService nscsModelService

    def "object under test should not be null" () {
        expect:
        nscsModelManager != null
    }

    def "get target info" () {
        given:
        def targetCategory = "this is the target category"
        def targetType = "this is the target type"
        def targetModelIdentity = "this is the target model identity"
        when:
        nscsModelManager.getTargetInfo(targetCategory, targetType, targetModelIdentity)
        then:
        1 * nscsModelService.getTargetTypeVersionDetails(targetCategory, targetType, targetModelIdentity)
        and:
        notThrown(Exception)
    }

    def "get target info with null target model identity" () {
        given:
        def targetCategory = "this is the target category"
        def targetType = "this is the target type"
        when:
        nscsModelManager.getTargetInfo(targetCategory, targetType, null)
        then:
        1 * nscsModelService.getTargetTypeVersionDetails(targetCategory, targetType)
        and:
        notThrown(Exception)
    }

    def "get target info with null target type" () {
        given:
        def targetCategory = "this is the target category"
        def targetModelIdentity = "this is the target model identity"
        when:
        nscsModelManager.getTargetInfo(targetCategory, null, targetModelIdentity)
        then:
        1 * nscsModelService.getTargetTypeDetails(targetCategory)
        and:
        notThrown(Exception)
    }

    def "get target info with null target category" () {
        given:
        def targetType = "this is the target type"
        def targetModelIdentity = "this is the target model identity"
        when:
        nscsModelManager.getTargetInfo(null, targetType, targetModelIdentity)
        then:
        1 * nscsModelService.getTargetTypeDetails(null, targetType)
        and:
        notThrown(Exception)
    }

    def "get target info with null target category and type" () {
        given:
        def targetModelIdentity = "this is the target model identity"
        when:
        nscsModelManager.getTargetInfo(null, null, targetModelIdentity)
        then:
        1 * nscsModelService.getTargetTypeDetails()
        and:
        notThrown(Exception)
    }
}
