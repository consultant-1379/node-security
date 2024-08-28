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

class CompactAuditLogDetailResultTest extends CdiSpecification {

    def 'constructor'() {
        given:
        def result = new CompactAuditLogDetailResult()
        expect:
        result != null
        result.getOpType() == null
        result.getId() == null
        result.getCurrentValues() != null
        result.getCurrentValues().isEmpty() == true
        result.getOldValues() != null
        result.getOldValues().isEmpty() == true
        result.getError() == null
    }

    def 'set operation type'() {
        given:
        def result = new CompactAuditLogDetailResult()
        when:
        result.setOpType("op-type")
        then:
        result != null
        result.getOpType() == "op-type"
        result.getId() == null
        result.getCurrentValues() != null
        result.getCurrentValues().isEmpty() == true
        result.getOldValues() != null
        result.getOldValues().isEmpty() == true
        result.getError() == null
    }

    def 'set identifier'() {
        given:
        def result = new CompactAuditLogDetailResult()
        when:
        result.setId("id")
        then:
        result != null
        result.getOpType() == null
        result.getId() == "id"
        result.getCurrentValues() != null
        result.getCurrentValues().isEmpty() == true
        result.getOldValues() != null
        result.getOldValues().isEmpty() == true
        result.getError() == null
    }

    def 'set current values'() {
        given:
        def result = new CompactAuditLogDetailResult()
        and:
        def currentValues = [attr1: "value1", attr2: 1, attr3: true]
        when:
        result.setCurrentValues(currentValues)
        then:
        result != null
        result.getOpType() == null
        result.getId() == null
        result.getCurrentValues() != null
        result.getCurrentValues().isEmpty() == false
        result.getOldValues() != null
        result.getOldValues().isEmpty() == true
        result.getError() == null
    }

    def 'set empty current values'() {
        given:
        def result = new CompactAuditLogDetailResult()
        and:
        def currentValues = [:]
        when:
        result.setCurrentValues(null)
        then:
        result != null
        result.getOpType() == null
        result.getId() == null
        result.getCurrentValues() != null
        result.getCurrentValues().isEmpty() == true
        result.getOldValues() != null
        result.getOldValues().isEmpty() == true
        result.getError() == null
    }

    def 'set null current values'() {
        given:
        def result = new CompactAuditLogDetailResult()
        when:
        result.setCurrentValues(null)
        then:
        result != null
        result.getOpType() == null
        result.getId() == null
        result.getCurrentValues() != null
        result.getCurrentValues().isEmpty() == true
        result.getOldValues() != null
        result.getOldValues().isEmpty() == true
        result.getError() == null
    }

    def 'set old values'() {
        given:
        def result = new CompactAuditLogDetailResult()
        and:
        def oldValues = [attr1: "value1", attr2: 1, attr3: true]
        when:
        result.setOldValues(oldValues)
        then:
        result != null
        result.getOpType() == null
        result.getId() == null
        result.getCurrentValues() != null
        result.getCurrentValues().isEmpty() == true
        result.getOldValues() != null
        result.getOldValues().isEmpty() == false
        result.getError() == null
    }

    def 'set empty old values'() {
        given:
        def result = new CompactAuditLogDetailResult()
        and:
        def oldValues = [:]
        when:
        result.setOldValues(oldValues)
        then:
        result != null
        result.getOpType() == null
        result.getId() == null
        result.getCurrentValues() != null
        result.getCurrentValues().isEmpty() == true
        result.getOldValues() != null
        result.getOldValues().isEmpty() == true
        result.getError() == null
    }

    def 'set null old values'() {
        given:
        def result = new CompactAuditLogDetailResult()
        when:
        result.setOldValues(null)
        then:
        result != null
        result.getOpType() == null
        result.getId() == null
        result.getCurrentValues() != null
        result.getCurrentValues().isEmpty() == true
        result.getOldValues() != null
        result.getOldValues().isEmpty() == true
        result.getError() == null
    }

    def 'set error'() {
        given:
        def result = new CompactAuditLogDetailResult()
        when:
        result.setError("error")
        then:
        result != null
        result.getOpType() == null
        result.getId() == null
        result.getCurrentValues() != null
        result.getCurrentValues().isEmpty() == true
        result.getOldValues() != null
        result.getOldValues().isEmpty() == true
        result.getError() == "error"
    }
}
