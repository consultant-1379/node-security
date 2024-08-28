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

class CompactAuditLogAdditionalInfoTest extends CdiSpecification {

    def 'no args constructor'() {
        given:
        def additionalInfo = new CompactAuditLogAdditionalInfo()
        expect:
        additionalInfo.getDetailResult() != null
        additionalInfo.getDetailResult().isEmpty()
        additionalInfo.getSummaryResult() != null
        additionalInfo.getSummaryResult().isEmpty()
        additionalInfo.getErrorDetail() == null
    }

    def 'detail result'() {
        given:
        def CompactAuditLogAdditionalInfo additionalInfo = new CompactAuditLogAdditionalInfo()
        def Map<String, Serializable> currentValues = new HashMap<>()
        currentValues.put("total", 3)
        currentValues.put("success", 2)
        currentValues.put("failed", 1)
        def Map<String, Serializable> detailResult = new HashMap<>()
        detailResult.put("opType", "operation type")
        detailResult.put("id", "instance identifier")
        detailResult.put("currentValues", currentValues)
        when:
        additionalInfo.setDetailResult([detailResult])
        then:
        additionalInfo.getDetailResult() != null
        additionalInfo.getDetailResult().isEmpty() == false
        additionalInfo.getSummaryResult() != null
        additionalInfo.getSummaryResult().isEmpty()
        additionalInfo.getErrorDetail() == null
    }

    def 'add entity create detail result'() {
        given:
        def CompactAuditLogAdditionalInfo additionalInfo = new CompactAuditLogAdditionalInfo()
        when:
        additionalInfo.addEntityCreateDetailResult("id")
        then:
        additionalInfo.getDetailResult() != null
        additionalInfo.getDetailResult().isEmpty() == false
        additionalInfo.getDetailResult().get(0).getOpType() == 'create'
        additionalInfo.getDetailResult().get(0).getId() == 'id'
        additionalInfo.getSummaryResult() != null
        additionalInfo.getSummaryResult().isEmpty()
        additionalInfo.getErrorDetail() == null
    }

    def 'add two entities create detail result'() {
        given:
        def CompactAuditLogAdditionalInfo additionalInfo = new CompactAuditLogAdditionalInfo()
        when:
        additionalInfo.addEntityCreateDetailResult("id1")
        additionalInfo.addEntityCreateDetailResult("id2")
        then:
        additionalInfo.getDetailResult() != null
        additionalInfo.getDetailResult().isEmpty() == false
        additionalInfo.getDetailResult().get(0).getOpType() == 'create'
        additionalInfo.getDetailResult().get(1).getOpType() == 'create'
        additionalInfo.getSummaryResult() != null
        additionalInfo.getSummaryResult().isEmpty()
        additionalInfo.getErrorDetail() == null
    }

    def 'add entity AVCs detail result'() {
        given:
        def CompactAuditLogAdditionalInfo additionalInfo = new CompactAuditLogAdditionalInfo()
        and:
        def avcString = new AttributeValueChange('attrString', 'curr', 'old')
        def avcInteger = new AttributeValueChange('attrInteger', 1, 0)
        def avcBoolean = new AttributeValueChange('attrBoolean', true, false)
        def avcs = [
            avcString,
            avcInteger,
            avcBoolean
        ]
        when:
        additionalInfo.addEntityAttributeValueChangesDetailResult("id", avcs)
        then:
        additionalInfo.getDetailResult() != null
        additionalInfo.getDetailResult().isEmpty() == false
        additionalInfo.getDetailResult().get(0).getOpType() == 'update'
        additionalInfo.getDetailResult().get(0).getId() == 'id'
        additionalInfo.getDetailResult().get(0).getCurrentValues() != null
        additionalInfo.getDetailResult().get(0).getCurrentValues().isEmpty() == false
        additionalInfo.getDetailResult().get(0).getOldValues() != null
        additionalInfo.getDetailResult().get(0).getOldValues().isEmpty() == false
        additionalInfo.getSummaryResult() != null
        additionalInfo.getSummaryResult().isEmpty()
        additionalInfo.getErrorDetail() == null
    }

    def 'add two entities AVCs detail result'() {
        given:
        def CompactAuditLogAdditionalInfo additionalInfo = new CompactAuditLogAdditionalInfo()
        and:
        def avcString = new AttributeValueChange('attrString', 'curr', 'old')
        def avcInteger = new AttributeValueChange('attrInteger', 1, 0)
        def avcBoolean = new AttributeValueChange('attrBoolean', true, false)
        def avcs = [
            avcString,
            avcInteger,
            avcBoolean
        ]
        when:
        additionalInfo.addEntityAttributeValueChangesDetailResult("id1", avcs)
        additionalInfo.addEntityAttributeValueChangesDetailResult("id2", avcs)
        then:
        additionalInfo.getDetailResult() != null
        additionalInfo.getDetailResult().isEmpty() == false
        additionalInfo.getDetailResult().get(0).getOpType() == 'update'
        additionalInfo.getDetailResult().get(0).getCurrentValues() != null
        additionalInfo.getDetailResult().get(0).getCurrentValues().isEmpty() == false
        additionalInfo.getDetailResult().get(0).getOldValues() != null
        additionalInfo.getDetailResult().get(0).getOldValues().isEmpty() == false
        additionalInfo.getDetailResult().get(1).getOpType() == 'update'
        additionalInfo.getDetailResult().get(1).getCurrentValues() != null
        additionalInfo.getDetailResult().get(1).getCurrentValues().isEmpty() == false
        additionalInfo.getDetailResult().get(1).getOldValues() != null
        additionalInfo.getDetailResult().get(1).getOldValues().isEmpty() == false
        additionalInfo.getSummaryResult() != null
        additionalInfo.getSummaryResult().isEmpty()
        additionalInfo.getErrorDetail() == null
    }

    def 'add two executes summary result no result'() {
        given:
        def CompactAuditLogAdditionalInfo additionalInfo = new CompactAuditLogAdditionalInfo()
        when:
        additionalInfo.addExecuteSummaryResult("id1", "Entity", null)
        additionalInfo.addExecuteSummaryResult("id2", "Entity", null)
        then:
        additionalInfo.getDetailResult() != null
        additionalInfo.getDetailResult().isEmpty() == true
        additionalInfo.getSummaryResult() != null
        additionalInfo.getSummaryResult().isEmpty() == false
        additionalInfo.getSummaryResult().get(0).getOpType() == 'execute'
        additionalInfo.getSummaryResult().get(0).getEntity() == 'Entity'
        additionalInfo.getSummaryResult().get(0).getResult() != null
        additionalInfo.getSummaryResult().get(0).getResult().isEmpty() == true
        additionalInfo.getSummaryResult().get(1).getOpType() == 'execute'
        additionalInfo.getSummaryResult().get(1).getEntity() == 'Entity'
        additionalInfo.getSummaryResult().get(1).getResult() != null
        additionalInfo.getSummaryResult().get(1).getResult().isEmpty() == true
        additionalInfo.getErrorDetail() == null
    }

    def 'add execute summary result no result'() {
        given:
        def CompactAuditLogAdditionalInfo additionalInfo = new CompactAuditLogAdditionalInfo()
        when:
        additionalInfo.addExecuteSummaryResult("id", "Entity", null)
        then:
        additionalInfo.getDetailResult() != null
        additionalInfo.getDetailResult().isEmpty() == true
        additionalInfo.getSummaryResult() != null
        additionalInfo.getSummaryResult().isEmpty() == false
        additionalInfo.getSummaryResult().get(0).getOpType() == 'execute'
        additionalInfo.getSummaryResult().get(0).getId() == 'id'
        additionalInfo.getSummaryResult().get(0).getEntity() == 'Entity'
        additionalInfo.getSummaryResult().get(0).getResult() != null
        additionalInfo.getSummaryResult().get(0).getResult().isEmpty() == true
        additionalInfo.getErrorDetail() == null
    }
}
