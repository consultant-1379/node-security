/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ericsson.oss.services.nscs.workflow;

import java.io.Serializable;
import java.util.Date;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import com.ericsson.oss.services.nscs.workflow.utils.WorkflowCategory;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowNames;

/**
 *
 * @author elucbot
 */
public class NSCSWorkflowInstance implements Serializable {

    private static final long serialVersionUID = -5060553774402961610L;

    private static final int TIME_TO_LIVE = 60;

    private final String executionId;
    private final String workflowInstanceName;
    private final String businessKey;
    private WorkflowInstanceStatus state;
    private final WorkflowCategory category;
    private final Date startTime;
    private Date endTime;
    private int timeToLive;
    private String durationTimeFormat;

    public NSCSWorkflowInstance(final String executionId, final String workflowInstanceName, final String businessKey) {
        this.executionId = executionId;
        this.workflowInstanceName = workflowInstanceName;
        this.businessKey = businessKey;
        this.state = WorkflowInstanceStatus.RUNNING;
        this.category = defineWorkflowCategory(this.workflowInstanceName);
        this.startTime = new Date();
        this.endTime = null;
        this.timeToLive = TIME_TO_LIVE;
        this.durationTimeFormat = "NA";
    }

    private WorkflowCategory defineWorkflowCategory(final String workflowInstanceName) {

        WorkflowCategory wCategory = WorkflowCategory.NA;

        for (final WorkflowNames wfName : WorkflowNames.values()) {
            if (wfName.getWorkflowName().equalsIgnoreCase(workflowInstanceName)) {
                wCategory = wfName.getCategory();
                break;
            }
        }

        return wCategory;
    }

    /**
     * @return the category
     */
    public WorkflowCategory getCategory() {
        return category;
    }

    /**
     * @return the executionId
     */
    public String getExecutionId() {
        return this.executionId;
    }

    /**
     * @return the workflow name
     */
    public String getWorkflowInstanceName() {
        return this.workflowInstanceName;
    }

    /**
     * @return the business key
     */
    public String getBusinessKey() {
        return this.businessKey;
    }

    /**
     * @return the state
     */
    public WorkflowInstanceStatus getState() {
        return this.state;
    }

    /**
     * @param state
     *            Set the state
     */
    public void setState(final WorkflowInstanceStatus state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return String.format(
                "WorkflowInstance " + "workflowInstanceName %s, " + "executionId %s, " + "businesskey %s, " + "category %s, " + "state %s, " + "startDate %s, " + "endDate %s, " + "duration %s",
                workflowInstanceName, executionId, businessKey, category.name(), state.name(), startTime.toString(), (endTime != null) ? endTime.toString() : "NA", this.getDurationTimeFormat());
    }

    /**
     * @return the startTime
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * @return the endTime
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * @param endTime
     *            Set the endTime
     */
    public void setEndTime(final Date endTime) {
        this.endTime = endTime;

        if (this.endTime != null) {
            Duration duration = null;
            try {
                duration = DatatypeFactory.newInstance().newDuration(endTime.getTime() - startTime.getTime());
                this.setDurationTimeFormat(String.format("%02d:%02d:%02d", duration.getDays() * 24 + duration.getHours(), duration.getMinutes(), duration.getSeconds()));
            } catch (DatatypeConfigurationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * @return the timeToLive
     */
    public int getTimeToLive() {
        return timeToLive;
    }

    /**
     * @param timeToLive
     *            the timeToLive to set
     */
    public void setTimeToLive(final int timeToLive) {
        this.timeToLive = timeToLive;
    }

    /**
     * Decrement the time to live
     *
     * @param delta the delta
     * @return the updated timeToLive
     */
    public int decrementTimeToLive(final int delta) {
        this.timeToLive -= delta;
        return this.timeToLive;
    }

    public enum WorkflowInstanceStatus {
        RUNNING, SUCCESS, FAILED, TIMEOUT, NA
    }

    public String getDurationTimeFormat() {
        return durationTimeFormat;
    }

    public void setDurationTimeFormat(final String durationTimeFormat) {
        this.durationTimeFormat = durationTimeFormat;
    }
}
