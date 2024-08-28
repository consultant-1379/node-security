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
package com.ericsson.oss.services.security.nscs.command.util

import com.ericsson.cds.cdi.support.spock.CdiSpecification

class NscsCommandConstantsTest extends CdiSpecification {

    def 'constructor is not allowed'() {
        given:
        when:
        new NscsCommandConstants()
        then:
        thrown(IllegalStateException.class)
    }
}
