/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.nscs.cache.webpush;

import java.util.HashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.util.CacheWebPushServiceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.cache.NodesConfigurationStatusRecord;
import com.ericsson.oss.uisdk.restsdk.webpush.api.WebPushClient;
import com.ericsson.oss.uisdk.restsdk.webpush.api.WebPushEndpoint;
import com.ericsson.oss.uisdk.restsdk.webpush.api.WebPushRestEvent;
import com.ericsson.oss.uisdk.restsdk.webpush.api.impl.WebPushRestEventImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author egicass
 *         creates the web Push channel for cache update
 */
@ApplicationScoped
public class NscsCacheWebPushServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(NscsCacheWebPushServiceImpl.class);

    @Inject
    @WebPushEndpoint(resourceUrn = "nodesecurity", channelName = "cacheUpdate")
    private WebPushClient client;

    public void listenToNodeEvent(@Observes CacheWebPushServiceEvent cacheWebPushServiceEvent) {
        broadcastEvent(cacheWebPushServiceEvent.getNode());
    }

    private void broadcastEvent(final NodesConfigurationStatusRecord payload) {

        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            final String eventAsJSONString = objectMapper.writeValueAsString(payload);
            final WebPushRestEvent event = new WebPushRestEventImpl(eventAsJSONString);
            logger.debug("Nodes cache update: event published for node [{}]", eventAsJSONString);
            client.broadcast(event, new HashMap<>());
        } catch (Exception e) {
            logger.error("Error broadcasting webpush event, exception[{}], message: [{}]", e.getClass().getCanonicalName(), e.getMessage());
        }
    }
}