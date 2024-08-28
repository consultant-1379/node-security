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
import com.ericsson.nms.security.nscs.api.enums.SecurityLevel

import spock.lang.Shared
import spock.lang.Unroll

class IscfXmlOamParamsDtoTest extends CdiSpecification {

    @ObjectUnderTest
    IscfXmlOamParamsDto dto

    @Shared
    securityLevels = SecurityLevel.values()

    def "no-args constructor"() {
        expect:
        dto != null
        and:
        dto.getMinimumSecurityLevel() == null
        and:
        dto.getWantedSecurityLevel() == null
    }

    @Unroll
    def "minimum security level #securityLevel"() {
        given:
        dto.setMinimumSecurityLevel(securityLevel)
        expect:
        dto.getMinimumSecurityLevel() == securityLevel
        where:
        securityLevel << securityLevels
    }

    def "null minimum security level"() {
        given:
        dto.setMinimumSecurityLevel(null)
        expect:
        dto.getMinimumSecurityLevel() == null
    }

    @Unroll
    def "wanted security level #securityLevel"() {
        given:
        dto.setWantedSecurityLevel(securityLevel)
        expect:
        dto.getWantedSecurityLevel() == securityLevel
        where:
        securityLevel << securityLevels
    }

    def "null wanted security level"() {
        given:
        dto.setWantedSecurityLevel(null)
        expect:
        dto.getWantedSecurityLevel() == null
    }
}
