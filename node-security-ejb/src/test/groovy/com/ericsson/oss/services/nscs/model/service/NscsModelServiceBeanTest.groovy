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
package com.ericsson.oss.services.nscs.model.service

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.data.NscsCMReaderService

import spock.lang.Unroll

class NscsModelServiceBeanTest extends CdiSpecification {

    @ObjectUnderTest
    NscsModelServiceBean nscsModelServiceBean

    @MockedImplementation
    NscsCMReaderService nscsCMReaderService

    def 'object under test'() {
        expect:
        nscsModelServiceBean != null
    }

    def 'get target po'() {
        given:
        when:
        nscsModelServiceBean.getTargetPO("FDN")
        then:
        1 * nscsCMReaderService.getTargetPO("FDN")
    }

    @Unroll
    def 'get target po for invalid FDN #fdn'() {
        given:
        when:
        nscsModelServiceBean.getTargetPO(fdn)
        then:
        thrown(IllegalArgumentException)
        where:
        fdn << [null, ""]
    }
}
