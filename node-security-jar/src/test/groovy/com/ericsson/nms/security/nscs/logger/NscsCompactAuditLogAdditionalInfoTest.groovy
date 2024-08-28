/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2023
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.logger

import com.ericsson.cds.cdi.support.spock.CdiSpecification

class NscsCompactAuditLogAdditionalInfoTest extends CdiSpecification {

    def 'no args constructor'() {
        given:
        def additionalInfo = new NscsCompactAuditLogAdditionalInfo()
        expect:
        additionalInfo.getDetailResult() != null
        additionalInfo.getDetailResult().isEmpty()
        additionalInfo.getSummaryResult() != null
        additionalInfo.getSummaryResult().isEmpty()
        additionalInfo.getErrorDetail() == null
    }

    def 'detail result'() {
        given:
        def NscsCompactAuditLogAdditionalInfo additionalInfo = new NscsCompactAuditLogAdditionalInfo()
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
}
