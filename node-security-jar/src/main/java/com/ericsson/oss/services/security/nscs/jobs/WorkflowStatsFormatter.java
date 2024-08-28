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

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.ericsson.nms.security.nscs.handler.command.utility.NscsGetJobResponseBuilder;
import com.ericsson.oss.services.dto.WfResult;
import com.ericsson.oss.services.enums.WfStatusEnum;
import com.ericsson.oss.services.security.nscs.stats.NscsStatsFormatter;

/**
 * Auxiliary class to format statistics related to a workflow.
 */
public class WorkflowStatsFormatter extends NscsStatsFormatter {
    private static final String NOT_APPLICABLE = "Not Applicable";

    private String wfId;
    private String nodeName;
    private String wfStatus;
    private String wfDetails;
    private String wfStartDate;
    private String wfEndDate;
    private String wfDuration;
    private String wfResult;
    private String wfWakeId;
    private String wfParams;
    private String jobId;
    private String isWaiting;
    private String timestamp;

    public WorkflowStatsFormatter(final WfResult result) {
        wfId = (result.getWfId() != null) ? result.getWfId() : NOT_AVAILABLE;
        nodeName = (result.getNodeName() != null) ? result.getNodeName() : NOT_AVAILABLE;
        wfStatus = (result.getStatus() != null) ? result.getStatus().name() : NOT_AVAILABLE;
        wfDetails = (result.getMessage() != null) ? result.getMessage() : NOT_AVAILABLE;
        updateWorkflowDatesStats(result);
        wfParams = (result.getWfParams() != null) ? result.getWfParams().toString() : NOT_AVAILABLE;
        updateWorkflowResultStatistic(result);
        wfWakeId = (result.getWfWakeId() != null) ? result.getWfWakeId().toString() : NOT_AVAILABLE;
        jobId = (result.getJobId() != null) ? result.getJobId().toString() : NOT_AVAILABLE;
        isWaiting = String.valueOf(result.isWaiting());
        timestamp = String.valueOf(result.getTimestamp());
    }

    /**
     * Updates the dates statistics for the given workflow result.
     * 
     * @param result
     *            the workflow result.
     */
    private void updateWorkflowDatesStats(final WfResult result) {
        wfStartDate = NOT_AVAILABLE;
        wfEndDate = NOT_AVAILABLE;
        wfDuration = NOT_AVAILABLE;
        if (result.getStartDate() != null) {
            final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            wfStartDate = df.format(result.getStartDate());
            if (result.getEndDate() != null) {
                wfEndDate = df.format(result.getEndDate());
                final Long duration = result.getEndDate().getTime() - result.getStartDate().getTime();
                wfDuration = formatDuration(duration);
            }
        }
    }

    /**
     * Updates the workflow result statistic for the given workflow result.
     * 
     * @param result
     *            the workflow result.
     */
    private void updateWorkflowResultStatistic(final WfResult result) {
        if (result.getWfParams() != null && !result.getWfParams().isEmpty()
                && result.getWfParams().containsKey(NscsGetJobResponseBuilder.WORKFLOW_RESULT)) {
            wfResult = (String) result.getWfParams().get(NscsGetJobResponseBuilder.WORKFLOW_RESULT);
        } else {
            if (result.getStatus() != null && WfStatusEnum.SUCCESS.equals(result.getStatus())) {
                wfResult = NOT_APPLICABLE;
            } else {
                wfResult = NOT_AVAILABLE;
            }
        }
    }

    /**
     * @return the wfId
     */
    public String getWfId() {
        return wfId;
    }

    /**
     * @return the nodeName
     */
    public String getNodeName() {
        return nodeName;
    }

    /**
     * @return the wfStatus
     */
    public String getWfStatus() {
        return wfStatus;
    }

    /**
     * @return the wfDetails
     */
    public String getWfDetails() {
        return wfDetails;
    }

    /**
     * @return the wfStartDate
     */
    public String getWfStartDate() {
        return wfStartDate;
    }

    /**
     * @return the wfEndDate
     */
    public String getWfEndDate() {
        return wfEndDate;
    }

    /**
     * @return the wfDuration
     */
    public String getWfDuration() {
        return wfDuration;
    }

    /**
     * @return the wfResult
     */
    public String getWfResult() {
        return wfResult;
    }

    /**
     * @return the wfWakeId
     */
    public String getWfWakeId() {
        return wfWakeId;
    }

    /**
     * @return the wfParams
     */
    public String getWfParams() {
        return wfParams;
    }

    /**
     * @return the jobId
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * @return the isWaiting as string
     */
    public String getIsWaiting() {
        return isWaiting;
    }

    /**
     * @return the timestamp as string
     */
    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "WorkflowStatsFormatter [wfId=" + getWfId() + ", nodeName=" + getNodeName() + ", wfStatus=" + getWfStatus() + ", wfDetails="
                + getWfDetails() + ", wfStartDate=" + getWfStartDate() + ", wfEndDate=" + getWfEndDate() + ", wfDuration=" + getWfDuration()
                + ", wfResult=" + getWfResult() + ", wfWakeId=" + getWfWakeId() + ", wfParams=" + getWfParams() + ", jobId=" + getJobId()
                + ", isWaiting=" + getIsWaiting() + ", timestamp=" + getTimestamp() + "]";
    }

}
