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

import com.ericsson.oss.itpf.datalayer.dps.notification.event.DpsDataChangedEvent;

public interface NscsDpsDataChangedEventHandler {

    /**
     * Processes the received DPS event.
     *
     * @param event
     *            the received DPS event.
     */
    void processDpsDataChangedEvent(final DpsDataChangedEvent event);

}
