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
package com.ericsson.nms.security.nscs.utilities

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.data.ModelDefinition

class NSCSCertificateUtilityTest extends CdiSpecification {

    def 'get trusted certificate reserved-by MO attribute value from null map'() {
        given:
        def reservedByMoAttribute = ModelDefinition.TrustedCertificate.RESERVED_BY;
        when:
        def reservedByMoAttributeValue = NSCSCertificateUtility.getTrustedCertificateReservedByMoAttributeValue(null, reservedByMoAttribute)
        then:
        reservedByMoAttributeValue == null
    }

    def 'get trusted certificate reserved-by MO attribute value from empty map'() {
        given:
        def entries = [:]
        and:
        def reservedByMoAttribute = ModelDefinition.TrustedCertificate.RESERVED_BY;
        when:
        def reservedByMoAttributeValue = NSCSCertificateUtility.getTrustedCertificateReservedByMoAttributeValue(entries, reservedByMoAttribute)
        then:
        reservedByMoAttributeValue == null
    }

    def 'get trusted certificate #attr MO attribute value from map containing it'() {
        given:
        def entries = [:]
        entries.put(attr, ["value"])
        and:
        def reservedByMoAttribute = attr;
        when:
        def reservedByMoAttributeValue = NSCSCertificateUtility.getTrustedCertificateReservedByMoAttributeValue(entries, reservedByMoAttribute)
        then:
        reservedByMoAttributeValue == ["value"]
        where:
        attr << [
            ModelDefinition.TrustedCertificate.RESERVED_BY,
            ModelDefinition.TrustedCertificate.RESERVED_BY_CATEGORY
        ]
    }

    def 'get trusted certificate #expectedAttr MO attribute value from map not containing it'() {
        given:
        def entries = [:]
        entries.put(actualAttr, ["value"])
        and:
        def reservedByMoAttribute = expectedAttr;
        when:
        def reservedByMoAttributeValue = NSCSCertificateUtility.getTrustedCertificateReservedByMoAttributeValue(entries, reservedByMoAttribute)
        then:
        reservedByMoAttributeValue == null
        where:
        actualAttr << [
            ModelDefinition.TrustedCertificate.RESERVED_BY,
            ModelDefinition.TrustedCertificate.RESERVED_BY_CATEGORY
        ]
        expectedAttr << [
            ModelDefinition.TrustedCertificate.RESERVED_BY_CATEGORY,
            ModelDefinition.TrustedCertificate.RESERVED_BY
        ]
    }
}
