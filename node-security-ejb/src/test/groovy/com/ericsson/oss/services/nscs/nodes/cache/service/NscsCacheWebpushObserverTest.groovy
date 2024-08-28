/*------------------------------------------------------------------------------
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
package com.ericsson.oss.services.nscs.nodes.cache.service

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.services.cache.NodesConfigurationStatusRecord

import javax.inject.Inject

class NscsCacheWebpushObserverTest extends CdiSpecification {

    static final String NODE_UT_NAME = "LTE01dg200001"

    @ObjectUnderTest
    NscsCacheWebpushObserver nscsCacheWebpushObserver

    @Inject
    NodesConfigurationStatusRecord nodesConfigurationStatusRecord

    def "update node cache and send event to web push client" () {
        given:
            nodesConfigurationStatusRecord.setName(NODE_UT_NAME)
        when:
            nscsCacheWebpushObserver.update(nodesConfigurationStatusRecord)
        then:
            noExceptionThrown()
    }
}
