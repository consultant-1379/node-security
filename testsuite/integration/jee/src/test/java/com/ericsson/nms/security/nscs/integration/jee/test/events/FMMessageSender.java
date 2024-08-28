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
package com.ericsson.nms.security.nscs.integration.jee.test.events;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.oss.itpf.sdk.eventbus.Channel;
import com.ericsson.oss.itpf.sdk.eventbus.annotation.Endpoint;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedAlarmEvent;

/**
 * A test message send for FM to generate alarms for testing purposes.
 *
 */
public class FMMessageSender {

    @Inject
    @Endpoint("jms:/topic/FMAlarmOutTopic")
    private Channel channel;

    @Inject
    private Logger logger;

    /**
     * FMMessageSender sends these messages
     * 
     * @param messagePayload
     *            - the ProcessedAlarmEvent
     */
    public void sendMessage(final ProcessedAlarmEvent messagePayload) {

        logger.info("Sending Message to Topic: [{}] Sent, Node Alarm Message from FM is . Payload =.................[{}]", channel.toString(), messagePayload);
        this.channel.send(messagePayload); // send single message, no persistence
    }

}
