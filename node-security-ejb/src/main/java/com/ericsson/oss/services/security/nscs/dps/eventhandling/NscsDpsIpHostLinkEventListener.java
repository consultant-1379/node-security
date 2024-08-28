/*------------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2018
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.dps.eventhandling;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.notification.event.DpsDataChangedEvent;
import com.ericsson.oss.itpf.sdk.eventbus.classic.EMessageListener;

public class NscsDpsIpHostLinkEventListener implements EMessageListener<DpsDataChangedEvent> {

    private final Logger logger = LoggerFactory.getLogger(NscsDpsIpHostLinkEventListener.class);

    @Inject
    private NscsDpsDataChangedEventHandler nscsDpsDataChangedEventHandler;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMessage(final DpsDataChangedEvent event) {
        logger.debug("IpHostLink message received from DPS.\n" + event);
        nscsDpsDataChangedEventHandler.processDpsDataChangedEvent(event);
    }

}
