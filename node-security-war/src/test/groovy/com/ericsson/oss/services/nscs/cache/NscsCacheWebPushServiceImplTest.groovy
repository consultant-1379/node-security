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
package com.ericsson.oss.services.nscs.cache

import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.util.CacheWebPushServiceEvent
import com.ericsson.oss.services.cache.NodesConfigurationStatusRecord
import com.ericsson.oss.services.nscs.cache.webpush.NscsCacheWebPushServiceImpl
import com.ericsson.oss.uisdk.restsdk.webpush.api.WebPushClient
import com.ericsson.oss.uisdk.restsdk.webpush.api.exception.WebPushBroadcastException

import javax.inject.Inject

class NscsCacheWebPushServiceImplTest extends CdiSpecification {

    static final String NODE_UT_NAME = "LTE01dg200001"

    @ObjectUnderTest
    NscsCacheWebPushServiceImpl nscsCacheWebPushService

    @Inject
    CacheWebPushServiceEvent cacheWebPushServiceEvent

    @Inject
    NodesConfigurationStatusRecord nodesConfigurationStatusRecord

    @ImplementationInstance
    WebPushClient webPushClientException = [
            broadcast : { var1,var2 ->
                throw new WebPushBroadcastException("web push client broadcast exception")
            }
    ] as WebPushClient

    @MockedImplementation
    WebPushClient webPushClient


    def "listen for event due to node cache changes and notify web push" () {
        given:
            nodesConfigurationStatusRecord.setName(NODE_UT_NAME)
            cacheWebPushServiceEvent.setNode(nodesConfigurationStatusRecord)
            nscsCacheWebPushService.client = webPushClient
        when:
            nscsCacheWebPushService.listenToNodeEvent(cacheWebPushServiceEvent)
        then:
            noExceptionThrown()
    }

    def "listen for event due to node cache changes and catch a web push broadcast  exception raised from web push client " () {
        given:
            nodesConfigurationStatusRecord.setName(NODE_UT_NAME)
            cacheWebPushServiceEvent.setNode(nodesConfigurationStatusRecord)
            nscsCacheWebPushService.client = webPushClientException
        when:
            nscsCacheWebPushService.listenToNodeEvent(cacheWebPushServiceEvent)
        then:
            noExceptionThrown()
    }
}
