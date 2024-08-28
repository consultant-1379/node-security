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

class CompactAuditLogSummaryResultTest extends CdiSpecification {

    def 'constructor'() {
        given:
        def result = new CompactAuditLogSummaryResult()
        expect:
        result != null
        result.getOpType() == null
        result.getId() == null
        result.getEntity() == null
        result.getResult() != null
        result.getResult().isEmpty() == true
    }

    def 'set operation type'() {
        given:
        def result = new CompactAuditLogSummaryResult()
        when:
        result.setOpType("op-type")
        then:
        result != null
        result.getOpType() == "op-type"
        result.getId() == null
        result.getEntity() == null
        result.getResult() != null
        result.getResult().isEmpty() == true
    }

    def 'set identifier'() {
        given:
        def result = new CompactAuditLogSummaryResult()
        when:
        result.setId("id")
        then:
        result != null
        result.getOpType() == null
        result.getId() == "id"
        result.getEntity() == null
        result.getResult() != null
        result.getResult().isEmpty() == true
    }

    def 'set entity'() {
        given:
        def result = new CompactAuditLogSummaryResult()
        when:
        result.setEntity("entity")
        then:
        result != null
        result.getOpType() == null
        result.getId() == null
        result.getEntity() == "entity"
        result.getResult() != null
        result.getResult().isEmpty() == true
    }

    def 'set result'() {
        given:
        def result = new CompactAuditLogSummaryResult()
        and:
        def res = [result1: "result1", result2: 1]
        when:
        result.setResult(res)
        then:
        result != null
        result.getOpType() == null
        result.getId() == null
        result.getEntity() == null
        result.getResult() != null
        result.getResult().isEmpty() == false
    }

    def 'set empty result'() {
        given:
        def result = new CompactAuditLogSummaryResult()
        and:
        def res = [:]
        when:
        result.setResult(res)
        then:
        result != null
        result.getOpType() == null
        result.getId() == null
        result.getEntity() == null
        result.getResult() != null
        result.getResult().isEmpty() == true
    }

    def 'set null result'() {
        given:
        def result = new CompactAuditLogSummaryResult()
        when:
        result.setResult(null)
        then:
        result != null
        result.getOpType() == null
        result.getId() == null
        result.getEntity() == null
        result.getResult() != null
        result.getResult().isEmpty() == true
    }
}
