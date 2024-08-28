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

import com.ericsson.oss.services.fm.models.processedevent.ProcessedAlarmEvent;

public class EventTest implements FMEventTests {

    @Inject
    FMMessageSender messageSender;

    @Inject
    Logger log;

    @Override
    public void testEventNscsService__FMAlarm_Events() throws Exception {

        log.info("-----------testEventNscsService__FMAlarm_Events starts--------------");

        final ProcessedAlarmEvent alarmEvent = new ProcessedAlarmEvent();
        messageSender.sendMessage(alarmEvent);

        log.info("-----------testEventNscsService__FMAlarm_Events ends--------------");
    }

}
