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
package com.ericsson.oss.services.security.nscs.command;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.ericsson.oss.services.security.nscs.stats.NscsStatsFormatter;

/**
 * Auxiliary class to format statistics related to a command handler.
 */
public class CommandHandlerStatsFormatter extends NscsStatsFormatter {
    private String commandId;
    private Long startTimeInMillis;
    private Integer numItems;
    private Integer numSuccessItems;
    private Integer numErrorItems;

    public CommandHandlerStatsFormatter() {
        super();
        this.startTimeInMillis = Long.valueOf(Calendar.getInstance().getTimeInMillis());
    }

    /**
     * @return the commandId
     */
    public String getCommandId() {
        return commandId;
    }

    /**
     * @param commandId
     *            the commandId to set
     */
    public void setCommandId(final String commandId) {
        this.commandId = commandId;
    }

    /**
     * @return the startTimeInMillis
     */
    public Long getStartTimeInMillis() {
        return startTimeInMillis;
    }

    /**
     * @return the numItems
     */
    public Integer getNumItems() {
        return numItems;
    }

    /**
     * @param numItems
     *            the numItems to set
     */
    public void setNumItems(final Integer numItems) {
        this.numItems = numItems;
    }

    /**
     * @return the numSuccessItems
     */
    public Integer getNumSuccessItems() {
        return numSuccessItems;
    }

    /**
     * @param numSuccessItems
     *            the numSuccessItems to set
     */
    public void setNumSuccessItems(final Integer numSuccessItems) {
        this.numSuccessItems = numSuccessItems;
    }

    /**
     * @return the numErrorItems
     */
    public Integer getNumErrorItems() {
        return numErrorItems;
    }

    /**
     * @param numErrorItems
     *            the numErrorItems to set
     */
    public void setNumErrorItems(final Integer numErrorItems) {
        this.numErrorItems = numErrorItems;
    }

    /**
     * Convert command handler statistics to SDK recording event data format.
     * 
     * @return a Map containing the key-value pairs for the event data. The names must be non-empty strings, white space in the key is prohibited. The
     *         value must be non-null. For privacy and security reasons, usernames / user IDs / IP addresses and any types of PII data are not
     *         allowed.
     */
    public Map<String, Object> toEventData() {
        final Map<String, Object> eventData = new HashMap<>();
        eventData.put("COMMAND_ID", getCommandId());
        eventData.put("COMMAND_DURATION", getDurationInSec(getCommandDurationInMillis()));
        if (getNumItems() != null) {
            eventData.put("COMMAND_NUM_ITEMS", getNumItems());
        }
        if (getNumSuccessItems() != null) {
            eventData.put("COMMAND_NUM_SUCCESS_ITEMS", getNumSuccessItems());
        }
        if (getNumErrorItems() != null) {
            eventData.put("COMMAND_NUM_ERROR_ITEMS", getNumErrorItems());
        }
        return eventData;
    }

    /**
     * Calculate the command duration as delta from the command start timestamp to current timestamp.
     * 
     * @return the command duration in millis.
     */
    private Long getCommandDurationInMillis() {
        final Long now = Long.valueOf(Calendar.getInstance().getTimeInMillis());
        return now - this.startTimeInMillis;
    }

}
