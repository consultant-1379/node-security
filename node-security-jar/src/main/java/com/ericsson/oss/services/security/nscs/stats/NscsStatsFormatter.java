/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2023
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.stats;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Auxiliary class to format generic NSCS statistics.
 */
public class NscsStatsFormatter {

    public static final String NOT_AVAILABLE = "N/A";

    /**
     * Formats the given date.
     * 
     * @param date
     *            the date.
     * @return the date as formatted string.
     */
    protected String formatDate(final Date date) {
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return (date != null) ? df.format(date) : NOT_AVAILABLE;
    }

    /**
     * Formats number of milliseconds
     * 
     * @param millis
     *            number of milliseconds
     * @return formatted duration
     */
    protected String formatDuration(final Long millis) {
        Date date = new Date(millis);
        DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        return df.format(date);
    }

    /**
     * Gets the duration (in milliseconds) between a start and an end date.
     * 
     * @param startDate
     *            the start date.
     * @param endDate
     *            the end date.
     * @return the duration (in milliseconds) or 0 if any of the dates is null or start date is after or equal to end date.
     */
    protected Long getDurationInMillis(final Date startDate, final Date endDate) {
        Long durationInMillis = 0L;
        if (startDate != null && endDate != null) {
            if (startDate.equals(endDate)) {
                durationInMillis = 0L;
            } else if (startDate.before(endDate)) {
                durationInMillis = endDate.getTime() - startDate.getTime();
            }
        }
        return durationInMillis;
    }

    /**
     * Gets the duration (in seconds) of a given duration expressed in milliseconds.
     * 
     * @param durationInMillis
     *            the duration expressed in milliseconds.
     * @return the duration (in seconds).
     */
    protected Integer getDurationInSec(final Long durationInMillis) {
        Long durationInSec = 0L;
        if (durationInMillis > 0L) {
            durationInSec = durationInMillis / 1000L + ((durationInMillis % 1000) == 0L ? 0L : 1L);
        }
        return durationInSec.intValue();
    }

}