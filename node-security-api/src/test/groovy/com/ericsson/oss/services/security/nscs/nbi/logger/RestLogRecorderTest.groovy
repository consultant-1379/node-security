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

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification

class RestLogRecorderTest extends CdiSpecification {

    @ObjectUnderTest
    RestLogRecorder recorder

    def 'object under test'() {
        expect:
        recorder != null
        and:
        recorder.getUserId() == null
        recorder.getSourceIpAddr() == null
        recorder.getSessionId() == null
        recorder.getMethod() == null
        recorder.getUrlFile() == null
        recorder.getUrlPath() == null
        recorder.getRequestPayload() == null
        recorder.getJobId() == null
        recorder.getAdditionalInfo() != null
    }

    def 'set source IP addr'() {
        given:
        when:
        recorder.setSourceIpAddr("1.2.3.4")
        then:
        recorder.getSourceIpAddr() == "1.2.3.4"
    }

    def 'set session ID'() {
        given:
        when:
        recorder.setSessionId("session-id")
        then:
        recorder.getSessionId() == "session-id"
    }

    def 'set method'() {
        given:
        when:
        recorder.setMethod("method")
        then:
        recorder.getMethod() == "method"
    }

    def 'set url file'() {
        given:
        when:
        recorder.setUrlFile("url-file")
        then:
        recorder.getUrlFile() == "url-file"
    }

    def 'set url path'() {
        given:
        when:
        recorder.setUrlPath("url-path")
        then:
        recorder.getUrlPath() == "url-path"
    }

    def 'set request payload'() {
        given:
        when:
        recorder.setRequestPayload("request-payload")
        then:
        recorder.getRequestPayload() == "request-payload"
    }

    def 'set job ID'() {
        given:
        when:
        recorder.setJobId("job-id")
        then:
        recorder.getJobId() == "job-id"
    }

    def 'set additional info error detail'() {
        given:
        when:
        recorder.getAdditionalInfo().setErrorDetail("error-detail")
        then:
        recorder.getAdditionalInfo().getErrorDetail() == "error-detail"
    }

    def 'set additional info detail result'() {
        given:
        def CompactAuditLogDetailResult detailResult = new CompactAuditLogDetailResult();
        detailResult.setOpType("op-type")
        detailResult.setId("id")
        when:
        recorder.getAdditionalInfo().setDetailResult([detailResult])
        then:
        recorder.getAdditionalInfo().getDetailResult() == [detailResult]
    }

    def 'set additional info summary result'() {
        given:
        def CompactAuditLogSummaryResult summaryResult = new CompactAuditLogSummaryResult();
        summaryResult.setOpType("op-type")
        summaryResult.setId("id")
        summaryResult.setEntity("entity")
        when:
        recorder.getAdditionalInfo().setSummaryResult([summaryResult])
        then:
        recorder.getAdditionalInfo().getSummaryResult() == [summaryResult]
    }
}
