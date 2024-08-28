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

class GenericLogRecorderTest extends CdiSpecification {

    @ObjectUnderTest
    GenericLogRecorder recorder

    def 'object under test'() {
        expect:
        recorder != null
        and:
        recorder.getUserId() == null
        recorder.getSourceIpAddr() == null
        recorder.getSessionId() == null
    }

    def 'set user ID'() {
        given:
        when:
        recorder.setUserId("user-id")
        then:
        recorder.getUserId() == "user-id"
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
}
