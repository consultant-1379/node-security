/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2022
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.jobs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ericsson.nms.security.nscs.logger.NscsCompactAuditLogConstants;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.dto.WfResult;
import com.ericsson.oss.services.enums.JobGlobalStatusEnum;
import com.ericsson.oss.services.enums.WfStatusEnum;
import com.ericsson.oss.services.jobs.dto.JobDto;
import com.ericsson.oss.services.security.nscs.stats.NscsStatsFormatter;

/**
 * Auxiliary class to format statistics related to a job.
 * 
 * Summary statistics for job's workflows are managed too.
 */
public class JobStatsFormatter extends NscsStatsFormatter {

    // private static final String JOB_WITH_ERRORS = " WITH ERRORS"; //NOSONAR
    private static final String JOB_WITH_ERRORS = "";

    private String jobId;
    private String userId;
    private String commandId;
    private String jobInsertDate;
    private String jobStartDate;
    private String jobEndDate;
    private Long jobInsertDuration;
    private Long jobStartDuration;
    private String jobStatus;
    private Integer numWfs;
    private Integer numNotCompletedWfs;
    private Integer numInsertedWfs;
    private Integer numPendingWfs;
    private Integer numRunningWfs;
    private Integer numSuccessWfs;
    private Integer numErrorWfs;
    private Long minDurationSuccessWfs;
    private Long maxDurationSuccessWfs;
    private Long avgDurationSuccessWfs;

    public JobStatsFormatter(final JobStatusRecord jobStatusRecord) {
        this(new JobDto(jobStatusRecord, new ArrayList<>()));
    }

    public JobStatsFormatter(final JobStatusRecord jobStatusRecord, final List<WfResult> wfResults) {
        this(new JobDto(jobStatusRecord, wfResults));
    }

    public JobStatsFormatter(final JobDto jobDto) {
        setJobStats(jobDto);
        setJobWorkflowsStats(jobDto);
        setJobStatus(jobDto);
    }

    /**
     * @return the jobId
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @return the commandId
     */
    public String getCommandId() {
        return commandId;
    }

    /**
     * @return the jobInsertDate as formatted string.
     */
    public String getJobInsertDate() {
        return jobInsertDate;
    }

    /**
     * @return the jobInsertDuration as formatted string
     */
    public String getJobInsertDuration() {
        return !NOT_AVAILABLE.equals(jobEndDate) ? formatDuration(jobInsertDuration) : NOT_AVAILABLE;
    }

    /**
     * @return the jobStartDate as formatted string.
     */
    public String getJobStartDate() {
        return jobStartDate;
    }

    /**
     * @return the jobStartDuration as formatted string
     */
    public String getJobStartDuration() {
        return !NOT_AVAILABLE.equals(jobEndDate) ? formatDuration(jobStartDuration) : NOT_AVAILABLE;
    }

    /**
     * @return the jobEndDate as formatted string
     */
    public String getJobEndDate() {
        return jobEndDate;
    }

    /**
     * @return the jobStatus
     */
    public String getJobStatus() {
        return jobStatus;
    }

    /**
     * @return the numWfs as string
     */
    public String getNumWfs() {
        return numWfs.toString();
    }

    /**
     * @return the numNotCompletedWfs as string
     */
    public String getNumNotCompletedWfs() {
        return numNotCompletedWfs.toString();
    }

    /**
     * @return the numInsertedWfs as string
     */
    public String getNumInsertedWfs() {
        return numInsertedWfs.toString();
    }

    /**
     * @return the numPendingWfs as string
     */
    public String getNumPendingWfs() {
        return numPendingWfs.toString();
    }

    /**
     * @return the numRunningWfs as string
     */
    public String getNumRunningWfs() {
        return numRunningWfs.toString();
    }

    /**
     * @return the numSuccessWfs as string
     */
    public String getNumSuccessWfs() {
        return numSuccessWfs.toString();
    }

    /**
     * @return the numErrorWfs as string
     */
    public String getNumErrorWfs() {
        return numErrorWfs.toString();
    }

    /**
     * @return true if at least a workflow finished with error
     */
    public Boolean hasErrors() {
        return numErrorWfs > 0;
    }

    /**
     * @return the minDurationSuccessWfs as formatted string
     */
    public String getMinDurationSuccessWfs() {
        return numSuccessWfs > 0 ? formatDuration(minDurationSuccessWfs) : NOT_AVAILABLE;
    }

    /**
     * @return the maxDurationSuccessWfs as formatted string
     */
    public String getMaxDurationSuccessWfs() {
        return numSuccessWfs > 0 ? formatDuration(maxDurationSuccessWfs) : NOT_AVAILABLE;
    }

    /**
     * @return the avgDurationSuccessWfs as formatted string
     */
    public String getAvgDurationSuccessWfs() {
        return numSuccessWfs > 0 ? formatDuration(avgDurationSuccessWfs) : NOT_AVAILABLE;
    }

    @Override
    public String toString() {
        return "JobStatsFormatter [jobId=" + getJobId() + ", userId=" + getUserId() + ", commandId=" + getCommandId() + ", jobInsertDate="
                + getJobInsertDate() + ", jobInsertDuration=" + getJobInsertDuration() + ", jobStartDate=" + getJobStartDate() + ", jobStartDuration="
                + getJobStartDuration() + ", jobEndDate=" + getJobEndDate() + ", jobStatus=" + getJobStatus() + ", numWfs=" + getNumWfs()
                + ", numNotCompletedWfs=" + getNumNotCompletedWfs() + ", numInsertedWfs=" + getNumInsertedWfs() + ", numPendingWfs="
                + getNumPendingWfs() + ", numRunningWfs=" + getNumRunningWfs() + ", numSuccessWfs=" + getNumSuccessWfs() + ", numErrorWfs="
                + getNumErrorWfs() + ", minDurationSuccessWfs=" + getMinDurationSuccessWfs() + ", maxDurationSuccessWfs=" + getMaxDurationSuccessWfs()
                + ", avgDurationSuccessWfs=" + getAvgDurationSuccessWfs() + "]";
    }

    /**
     * Converts job statistics to SDK recording event data format.
     * 
     * @return a Map containing the key-value pairs for the event data. The names must be non-empty strings, white space in the key is prohibited. The
     *         value must be non-null. For privacy and security reasons, usernames / user IDs / IP addresses and any types of PII data are not
     *         allowed.
     */
    public Map<String, Object> toEventData() {
        final Map<String, Object> eventData = new HashMap<>();
        eventData.put("JOB_INSERT_DURATION", getDurationInSec(jobInsertDuration));
        eventData.put("JOB_START_DURATION", getDurationInSec(jobStartDuration));
        eventData.put("JOB_COMMAND_ID", getCommandId());
        eventData.put("JOB_NUM_WORKFLOWS", numWfs);
        eventData.put("JOB_NUM_SUCCESS_WORKFLOWS", numSuccessWfs);
        eventData.put("JOB_NUM_ERROR_WORKFLOWS", numErrorWfs);
        if (numSuccessWfs > 0) {
            eventData.put("JOB_MIN_SUCCESS_WORKFLOWS_DURATION", getDurationInSec(minDurationSuccessWfs));
            eventData.put("JOB_MAX_SUCCESS_WORKFLOWS_DURATION", getDurationInSec(maxDurationSuccessWfs));
            eventData.put("JOB_AVG_SUCCESS_WORKFLOWS_DURATION", getDurationInSec(avgDurationSuccessWfs));
        }
        return eventData;
    }

    /**
     * Convert job statistics to SDK compact audit log additional info format.
     * 
     * @param numInvalidItems
     *            the number of invalid items on which workflow was not started.
     * 
     * @return The job additional info.
     */
    public Map<String, Serializable> toCompactAuditAdditionalInfo(final int numInvalidItems) {
        final Map<String, Serializable> jobAdditionalInfo = new HashMap<>();
        jobAdditionalInfo.put("valid", numWfs);
        if (numInvalidItems != NscsCompactAuditLogConstants.UNKNOWN_NUM_OF) {
            jobAdditionalInfo.put("invalid", numInvalidItems);
            jobAdditionalInfo.put("total", numWfs + numInvalidItems);
        } else {
            // this can happen in an upgrade scenario
            jobAdditionalInfo.put("invalid", NOT_AVAILABLE);
            jobAdditionalInfo.put("total", NOT_AVAILABLE);
        }
        jobAdditionalInfo.put("success", numSuccessWfs);
        jobAdditionalInfo.put("failed", numErrorWfs);
        return jobAdditionalInfo;
    }

    /**
     * Sets global job statistics.
     * 
     * @param jobDto
     *            the job DTO.
     */
    private void setJobStats(final JobDto jobDto) {
        jobId = (jobDto.getJobId() != null) ? jobDto.getJobId().toString() : NOT_AVAILABLE;
        userId = (jobDto.getUserId() != null) ? jobDto.getUserId() : NOT_AVAILABLE;
        commandId = (jobDto.getCommandId() != null) ? jobDto.getCommandId() : NOT_AVAILABLE;
        jobInsertDate = formatDate(jobDto.getInsertDate());
        jobStartDate = formatDate(jobDto.getStartDate());
        jobEndDate = formatDate(jobDto.getEndDate());
        jobInsertDuration = getDurationInMillis(jobDto.getInsertDate(), jobDto.getEndDate());
        jobStartDuration = getDurationInMillis(jobDto.getStartDate(), jobDto.getEndDate());
        numWfs = Integer.valueOf(jobDto.getNumOfTotWf());
        numNotCompletedWfs = Integer.valueOf(jobDto.getLastStartedWfId());
    }

    /**
     * Sets the job status.
     * 
     * @param jobDto
     *            the job DTO.
     */
    private void setJobStatus(final JobDto jobDto) {
        if (jobDto.getGlobalStatus() != null) {
            jobStatus = jobDto.getGlobalStatus().name();
            if (JobGlobalStatusEnum.COMPLETED.equals(jobDto.getGlobalStatus()) && numErrorWfs > 0) {
                jobStatus += JOB_WITH_ERRORS;
            }
        } else {
            jobStatus = NOT_AVAILABLE;
        }
    }

    /**
     * Sets statistics of job workflows.
     * 
     * @param jobDto
     *            the job DTO
     */
    private void setJobWorkflowsStats(final JobDto jobDto) {
        final List<WfResult> linkedWfResults = buildWorkflowsLinkedList(jobDto);
        numInsertedWfs = linkedWfResults.size();
        numPendingWfs = 0;
        numRunningWfs = 0;
        numSuccessWfs = 0;
        numErrorWfs = 0;
        minDurationSuccessWfs = Long.MAX_VALUE;
        maxDurationSuccessWfs = 0L;
        avgDurationSuccessWfs = 0L;
        updateJobWorkflowsStats(linkedWfResults);
    }

    /**
     * Updates the job workflows statistics for the given linked list of workflows results.
     * 
     * @param linkedWfResults
     *            the linked list of workflows results.
     */
    private void updateJobWorkflowsStats(final List<WfResult> linkedWfResults) {
        Long totalDurationSuccessWfs = 0L;
        for (final WfResult wfResult : linkedWfResults) {
            totalDurationSuccessWfs = updateJobWorkflowsStats(wfResult, totalDurationSuccessWfs);
        }
        if (numSuccessWfs > 0) {
            avgDurationSuccessWfs = totalDurationSuccessWfs / numSuccessWfs;
        }
    }

    /**
     * Updates the job workflows statistics for the given workflow result.
     * 
     * @param wfResult
     *            the workflow result.
     * @param totalDurationSuccessWfs
     *            the initial total duration of successful workflows.
     * @return the possibly updated total duration of successful workflows.
     */
    private Long updateJobWorkflowsStats(final WfResult wfResult, Long totalDurationSuccessWfs) {
        if (WfStatusEnum.PENDING.equals(wfResult.getStatus())) {
            numPendingWfs++;
        } else if (WfStatusEnum.RUNNING.equals(wfResult.getStatus())) {
            numRunningWfs++;
        } else if (WfStatusEnum.SUCCESS.equals(wfResult.getStatus())) {
            numSuccessWfs++;
            totalDurationSuccessWfs = updateSuccessfulJobWorkflowsDurationsStats(wfResult, totalDurationSuccessWfs);
        } else if (WfStatusEnum.ERROR.equals(wfResult.getStatus())) {
            numErrorWfs++;
        }
        return totalDurationSuccessWfs;
    }

    /**
     * Updates the job workflows durations statistics for the given successful workflow result.
     * 
     * @param wfResult
     *            the successful workflow result.
     * @param totalDurationSuccessWfs
     *            the initial total duration of successful workflows.
     * @return the updated total duration of successful workflows.
     */
    private Long updateSuccessfulJobWorkflowsDurationsStats(final WfResult wfResult, Long totalDurationSuccessWfs) {
        final Long duration = getDurationInMillis(wfResult.getStartDate(), wfResult.getEndDate());
        totalDurationSuccessWfs += duration;
        if (duration < minDurationSuccessWfs) {
            minDurationSuccessWfs = duration;
        }
        if (duration > maxDurationSuccessWfs) {
            maxDurationSuccessWfs = duration;
        }
        return totalDurationSuccessWfs;
    }

    /**
     * Builds a linked list for the job workflows.
     * 
     * @param jobDto
     *            the job DTO.
     * @return the job workflows linked list.
     */
    private List<WfResult> buildWorkflowsLinkedList(final JobDto jobDto) {
        final List<WfResult> linkedWfResults = new LinkedList<>();
        if (jobDto.getStatus() != null) {
            for (final Map.Entry<String, WfResult> wfStatus : jobDto.getStatus().entrySet()) {
                linkedWfResults.add(wfStatus.getValue());
            }
        }
        return linkedWfResults;
    }
}
