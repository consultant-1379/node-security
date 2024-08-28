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
package com.ericsson.oss.services.security.nscs.nbi

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.security.nscs.api.exception.NodeDoesNotExistException
import com.ericsson.oss.services.security.nscs.utils.NodeDataSetup

class DomainHandlerImplTest extends NodeDataSetup {

    @ObjectUnderTest
    DomainHandlerImpl handler

    def 'object under test'() {
        expect:
        handler != null
    }

    def 'generate OAM enrollment info for not existing node'() {
        given:
        when:
        def response = handler.generateEnrollmentInfo("nodename", "OAM", null, null)
        then:
        thrown(NodeDoesNotExistException.class)
    }
}
