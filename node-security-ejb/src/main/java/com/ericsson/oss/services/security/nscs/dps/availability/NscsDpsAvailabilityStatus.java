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

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds the DPS availability status. This singleton bean guarantees one single instance with the appropriate locks on the read and write methods.
 */
@Singleton
public class NscsDpsAvailabilityStatus {

    private static final Logger logger = LoggerFactory.getLogger(NscsDpsAvailabilityStatus.class);

    private static final Long NO_UNAVAILABILITY_START_TIME = Long.MAX_VALUE;

    private boolean isDpsAvailable = false;

    private Long unavailabilityStartTimeMs;

    /**
     * Retrieves the DPS availability status. Any thread can read as long as there is no write thread running.
     *
     * @return the DPS availability status (true = available; false = unavailable)
     */
    @Lock(LockType.READ)
    public boolean isDpsAvailable() {
        return this.isDpsAvailable;
    }

    /**
     * Updates the DPS availability status. Concurrent write accesses to this method are blocked.
     *
     * @param isDpsAvailable
     *            the DPS availability status to set (true = available; false = unavailable)
     */
    @Lock(LockType.WRITE)
    public void setDpsAvailable(final boolean isDpsAvailable) {
        if (isDpsAvailable) {
            if (this.unavailabilityStartTimeMs == null) {
                logger.info("DPS is available for the first time");
            } else {
                if (this.isDpsAvailable) {
                    logger.warn("DPS already available");
                } else {
                    final long unavailabilityTime = System.currentTimeMillis() - this.unavailabilityStartTimeMs;
                    logger.warn("DPS was unavailable for " + unavailabilityTime + " ms");
                }
            }
            this.unavailabilityStartTimeMs = NO_UNAVAILABILITY_START_TIME;
        } else {
            if (this.unavailabilityStartTimeMs == null) {
                logger.warn("DPS is unavailable but never received availability");
            } else {
                if (this.isDpsAvailable) {
                    logger.warn("DPS now unavailable");
                    this.unavailabilityStartTimeMs = System.currentTimeMillis();
                } else {
                    final long unavailabilityTime = System.currentTimeMillis() - this.unavailabilityStartTimeMs;
                    logger.warn("DPS already unavailable for " + unavailabilityTime + " ms");
                }
            }
        }
        this.isDpsAvailable = isDpsAvailable;
    }

    /**
     * Gets the current DPS unavailability start time in ms. Any thread can read as long as there is no write thread running.
     *
     * @return the current DPS unavailability start time in ms or null (if availability never received) or Long.MAX_VALUE (if currently available)
     */
    @Lock(LockType.READ)
    public Long getUnavailabilityStartTimeMs() {
        return this.unavailabilityStartTimeMs;
    }

}
