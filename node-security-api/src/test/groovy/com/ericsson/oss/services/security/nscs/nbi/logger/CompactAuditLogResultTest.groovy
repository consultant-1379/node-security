/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2024
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.nbi.logger

import com.ericsson.cds.cdi.support.spock.CdiSpecification

class CompactAuditLogResultTest extends CdiSpecification {

    def 'constructor'() {
        given:
        def result = new CompactAuditLogResult()
        expect:
        result != null
        result.getOpType() == null
        result.getId() == null
    }

    def 'set operation type'() {
        given:
        def result = new CompactAuditLogResult()
        when:
        result.setOpType("op-type")
        then:
        result != null
        result.getOpType() == "op-type"
        result.getId() == null
    }

    def 'set identifier'() {
        given:
        def result = new CompactAuditLogResult()
        when:
        result.setId("id")
        then:
        result != null
        result.getOpType() == null
        result.getId() == "id"
    }
}
