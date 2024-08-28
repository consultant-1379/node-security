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
package com.ericsson.nms.security.nscs.fm.eventhandling;

import com.ericsson.oss.services.fm.models.processedevent.ProcessedAlarmEvent;

/**
 * 
 * This service handles appropriate alarms based on certain security events and moreover any alarms raised for a particular node.
 * 
 * @author eabdsin
 * 
 */
public interface FMAlarmEventHandler {    
    /**
     * Receives the message from the event bus Message Queue for node - MO attribute value changes
     * 
     * @param payload
     *            - the changed event
     */
    void receiveFMAlarmEvent(ProcessedAlarmEvent payload);

}