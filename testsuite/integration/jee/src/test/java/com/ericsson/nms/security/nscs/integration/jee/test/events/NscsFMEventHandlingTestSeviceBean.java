/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.nms.security.nscs.integration.jee.test.events;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.fm.eventhandling.FMAlarmEventHandler;
import com.ericsson.oss.itpf.sdk.eventbus.model.annotation.Modeled;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedAlarmEvent;

/**
 * This test service handles appropriate user case actions based on node creations and deletions and moreover any attribute changes for MO on node.
 *
 */

public class NscsFMEventHandlingTestSeviceBean implements FMAlarmEventHandler {

    @Inject
    private Logger logger;

    @Override
    public void receiveFMAlarmEvent(@Observes @Modeled(channelId = "//global/NscsAlarmDivertQueue") final ProcessedAlarmEvent payload) {
        logger.info("TestCode Queue: NSCS FM Service on Queue in progress, Node Alarm Message Received from FM");
        if (payload != null /* && payload.getPoId() != null */) {
            logger.info("Topic: Node Alarm Message Received from FM is consumed. Payload =.................{}", payload);
        } else {
            logger.info("Topic: Cannot Receive Alarms from FM for Node MO Attribute Value Changes, the node name is null : {}", payload);
        }
    }
}