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
package com.ericsson.oss.services.jobs.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.dto.WfResult;
import com.ericsson.oss.services.enums.JobGlobalStatusEnum;

public class JobDto implements Serializable, Comparable<JobDto> {

    /**
     *
     */
    private static final long serialVersionUID = 2223146398325631149L;
    // private JobStatusRecord job;
    //private List<WfResult> workflows;

    /**
     *
     */
    private UUID jobId;
    private String userId;
    private String commandId;
    private Date startDate;
    private Date endDate;
    private Date insertDate;
    private JobGlobalStatusEnum globalStatus;
    private Map<String, WfResult> status;
    private int numOfTotWf;
    private int lastStartedWfId;

    /**
     *
     */
    public JobDto(final JobStatusRecord job, final List<WfResult> workflows) {
        setJobId(job.getJobId());
        setUserId(job.getUserId());
        setCommandId(job.getCommandId());
        setStartDate(job.getStartDate());
        setInsertDate(job.getInsertDate());
        setEndDate(job.getEndDate());
        setGlobalStatus(job.getGlobalStatus());
        setStatus(workflows);
        setNumOfTotWf(job.getNumOfTotWf());
        setLastStartedWfId(job.getLastStartedWfId());
    }

    public UUID getJobId() {
        return jobId;
    }

    public void setJobId(final UUID jobId) {
        this.jobId = jobId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public String getCommandId() {
        return commandId;
    }

    public void setCommandId(final String commandId) {
        this.commandId = commandId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(final Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(final Date endDate) {
        this.endDate = endDate;
    }

    public JobGlobalStatusEnum getGlobalStatus() {
        return globalStatus;
    }

    public void setGlobalStatus(final JobGlobalStatusEnum globalStatus) {
        this.globalStatus = globalStatus;
    }

    public Map<String, WfResult> getStatus() {
        return status;
    }

    public WfResult getStatus(final String wfWakeId) {
        return status.get(wfWakeId);
    }

    public void setStatus(final List<WfResult> workflows) {
        final Map<String, WfResult> wfStatus = new HashMap<String, WfResult>();
        for (final WfResult wfResult : workflows) {
            wfStatus.put(wfResult.getWfWakeId().toString(), wfResult);
        }

        this.status = wfStatus;
    }

    @Override
    public int compareTo(final JobDto arg) {
        //  JobStatusRecord arg = (JobStatusRecord) o;

        if (arg.getInsertDate() != null && this.getInsertDate() != null) {

            if (arg.getInsertDate().getTime() < this.getInsertDate().getTime()) {
                return 1;
            } else if (arg.getInsertDate().getTime() > this.getInsertDate().getTime()) {
                return -1;
            } else {
                return 0;
            }

        } else {
            if (arg.getStartDate() == null || this.getStartDate() == null) {
                return 0;
            }

            if (arg.getStartDate().getTime() < this.getStartDate().getTime()) {
                return 1;
            } else if (arg.getStartDate().getTime() > this.getStartDate().getTime()) {
                return -1;
            } else {
                return 0;
            }
        }

    }

    @Override
    public String toString() {
        return "JobDto [jobId=" + jobId + ", userId=" + userId + ", commandId=" + commandId + ", startDate=" + startDate + ", endDate=" + endDate
                + ", insertDate=" + insertDate + ", globalStatus=" + globalStatus + ", status=" + status + ", numOfTotWf=" + numOfTotWf
                + ", lastStartedWfId=" + lastStartedWfId + "]";
    }

    /**
     * @return the insertDate
     */
    public Date getInsertDate() {
        return insertDate;
    }

    /**
     * @param insertDate
     *            the insertDate to set
     */
    public void setInsertDate(final Date insertDate) {
        this.insertDate = insertDate;
    }

    /**
     * @return the numOfTotWf
     */
    public int getNumOfTotWf() {
        return numOfTotWf;
    }

    /**
     * @param numOfTotWf
     *            the numOfTotWf to set
     */
    public void setNumOfTotWf(final int numOfTotWf) {
        this.numOfTotWf = numOfTotWf;
    }

    /**
     * @return the lastStartedWfId
     */
    public int getLastStartedWfId() {
        return lastStartedWfId;
    }

    /**
     * @param lastStartedWfId
     *            the lastStartedWfId to set
     */
    public void setLastStartedWfId(final int lastStartedWfId) {
        this.lastStartedWfId = lastStartedWfId;
    }

}
