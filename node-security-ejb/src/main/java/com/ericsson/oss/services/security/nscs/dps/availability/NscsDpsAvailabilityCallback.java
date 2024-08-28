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
package com.ericsson.oss.services.security.nscs.dps.availability;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.availability.DpsAvailabilityCallback;

/**
 * This class implements the {@link DpsAvailabilityCallback} interface to listen to DPS availability status changes and calls methods in
 * {@link NscsDpsAvailabilityStatus} accordingly.
 */
@ApplicationScoped
public class NscsDpsAvailabilityCallback implements DpsAvailabilityCallback {

    private final Logger logger = LoggerFactory.getLogger(NscsDpsAvailabilityCallback.class);

    @Inject
    private NscsDpsAvailabilityStatus nscsDpsAvailabilityStatus;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServiceAvailable() {
        logger.info("DPS is available");
        nscsDpsAvailabilityStatus.setDpsAvailable(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServiceUnavailable() {
        logger.warn("DPS is unavailable");
        nscsDpsAvailabilityStatus.setDpsAvailable(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCallbackName() {
        return NscsDpsAvailabilityCallback.class.getCanonicalName();
    }

}
