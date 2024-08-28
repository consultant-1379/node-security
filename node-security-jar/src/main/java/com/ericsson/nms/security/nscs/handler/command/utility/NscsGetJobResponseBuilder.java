/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2019
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.handler.command.utility;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsMessageCommandResponse;
import com.ericsson.oss.services.dto.WfResult;
import com.ericsson.oss.services.jobs.dto.JobDto;
import com.ericsson.oss.services.security.nscs.jobs.JobStatsFormatter;
import com.ericsson.oss.services.security.nscs.jobs.WorkflowStatsFormatter;

/**
 * Auxiliary class to build the response to a 'secadm job get' command.
 */
public class NscsGetJobResponseBuilder extends NscsNameMultipleValueResponseBuilder {

    private static final String JOB_COMMAND_ID = "Command Id";
    private static final String JOB_ID = "Job Id";
    private static final String JOB_STATUS = "Job Status";
    private static final String JOB_USER = "Job User";
    private static final String JOB_START_DATE = "Job Start Date";
    private static final String JOB_END_DATE = "Job End Date";
    private static final String WF_NODE = "Node Name";
    private static final String WF_STATUS = "Workflow Status";
    private static final String WF_START_DATE = "Workflow Start Date";
    private static final String WF_DURATION = "Workflow Duration";
    private static final String WF_DETAILS = "Workflow Details";

    public static final String WORKFLOW_RESULT = "Workflow Result";

    private static final String JOB_SUMMARY_TITLE = "Job(s) Summary";
    private static final String NUM_WFS = "Num Of Workflows";
    private static final String NUM_PENDING_WFS = "Num Of Pending Workflows";
    private static final String NUM_RUNNING_WFS = "Num Of Running Workflows";
    private static final String NUM_SUCCESS_WFS = "Num Of Success Workflows";
    private static final String NUM_ERROR_WFS = "Num Of Error Workflows";
    private static final String MAX_DURATION_SUCCESS_WFS = "Max Duration Of Success Workflows";
    private static final String MIN_DURATION_SUCCESS_WFS = "Min Duration Of Success Workflows";
    private static final String AVG_DURATION_SUCCESS_WFS = "Avg Duration Of Success Workflows";
    private static final String JOB_SUMMARY_TAIL = "Command Executed Successfully";

    private static final String NO_JOB_FOUND = "No job found";

    Logger logger = LoggerFactory.getLogger(getClass());

    private static Map<String, Integer> JoblistStatusRow = new HashMap<String, Integer>();

    static {
        JoblistStatusRow.put(JOB_COMMAND_ID, 0);
        JoblistStatusRow.put(JOB_USER, 1);
        JoblistStatusRow.put(JOB_STATUS, 2);
        JoblistStatusRow.put(JOB_START_DATE, 3);
        JoblistStatusRow.put(JOB_END_DATE, 4);
        JoblistStatusRow.put(WF_NODE, 5);
        JoblistStatusRow.put(WF_STATUS, 6);
        JoblistStatusRow.put(WF_START_DATE, 7);
        JoblistStatusRow.put(WF_DURATION, 8);
        JoblistStatusRow.put(WF_DETAILS, 9);
        JoblistStatusRow.put(WORKFLOW_RESULT, 10);
    }

    private static final int JOBS_CACHE_ROW_SIZE = JoblistStatusRow.size();

    private boolean isSummary = false;

    private List<String> messages;

    public NscsGetJobResponseBuilder() {
        super(JOBS_CACHE_ROW_SIZE);
    }

    /**
     * Sets the flag of summary request
     * 
     * @param isSummary
     *            true if summary requested
     */
    public void setIsSummary(final boolean isSummary) {
        if (isSummary) {
            this.messages = new LinkedList<>();
        }
        this.isSummary = isSummary;
    }

    /**
     * Returns the command response.
     * 
     * If summary is not requested, it is a name multiple values response.
     * 
     * If summary is requested, it is a message response.
     * 
     * @return the command response
     */
    public NscsCommandResponse getCommandResponse() {
        if (this.isSummary) {
            return new NscsMessageCommandResponse(this.messages.toArray(new String[0]));
        } else {
            return super.getResponse();
        }
    }

    /**
     * Returns the message command response in case no requested job is found.
     * 
     * @return the message command response
     */
    public NscsCommandResponse noJobFoundCommandResponse() {
        return NscsCommandResponse.message(NO_JOB_FOUND);
    }

    /**
     * Adds the header to the response to a 'secadm job get' command
     */
    public void addHeader() {
        if (this.isSummary) {
            this.messages.add(JOB_SUMMARY_TITLE);
            this.messages.add(EMPTY_STRING);
        } else {
            add(JOB_ID, formatHeader());
        }
    }

    /**
     * Formats the header row of the multiple values section of response to a 'secadm job get' command.
     *
     * @return the formatted header row
     */
    private String[] formatHeader() {
        return formatHeader(JoblistStatusRow);
    }

    /**
     * Adds the tail to the response to a 'secadm job get' command
     */
    public void addTail() {
        if (this.isSummary) {
            this.messages.add(JOB_SUMMARY_TAIL);
            this.messages.add(EMPTY_STRING);
        }
    }

    /**
     * Add the job info to the response to a 'secadm job get' command according to given job record.
     *
     * @param record
     *            the job info
     */
    public void addJobInfo(final JobDto record) {
        if (this.isSummary) {
            addJobSummary(record);
        } else {
            addRows(record);
        }
    }

    /**
     * Add the summary job info to the response to a 'secadm job get' command according to given job record.
     * 
     * @param record
     *            the job info
     */
    private void addJobSummary(final JobDto record) {

        if (record != null) {
            final JobStatsFormatter jobStatsFormatter = new JobStatsFormatter(record);
            addJobSummaryMessage(JOB_ID, jobStatsFormatter.getJobId());
            addJobSummaryMessage(JOB_COMMAND_ID, jobStatsFormatter.getCommandId());
            addJobSummaryMessage(JOB_USER, jobStatsFormatter.getUserId());
            addJobSummaryMessage(JOB_STATUS, jobStatsFormatter.getJobStatus());
            addJobSummaryMessage(JOB_START_DATE, jobStatsFormatter.getJobStartDate());
            addJobSummaryMessage(JOB_END_DATE, jobStatsFormatter.getJobEndDate());
            addJobSummaryMessage(NUM_WFS, jobStatsFormatter.getNumInsertedWfs());
            addJobSummaryMessage(NUM_PENDING_WFS, jobStatsFormatter.getNumPendingWfs());
            addJobSummaryMessage(NUM_RUNNING_WFS, jobStatsFormatter.getNumRunningWfs());
            addJobSummaryMessage(NUM_SUCCESS_WFS, jobStatsFormatter.getNumSuccessWfs());
            addJobSummaryMessage(NUM_ERROR_WFS, jobStatsFormatter.getNumErrorWfs());
            addJobSummaryMessage(MIN_DURATION_SUCCESS_WFS, jobStatsFormatter.getMinDurationSuccessWfs());
            addJobSummaryMessage(MAX_DURATION_SUCCESS_WFS, jobStatsFormatter.getMaxDurationSuccessWfs());
            addJobSummaryMessage(AVG_DURATION_SUCCESS_WFS, jobStatsFormatter.getAvgDurationSuccessWfs());
            addJobSummaryTail();
        }
    }

    /**
     * Add a message to message response.
     * 
     * @param property
     *            the name of property
     * @param value
     *            the value of the property as string
     */
    private void addJobSummaryMessage(final String property, final String value) {
        this.messages.add(String.format("%s : %s", property, value));
    }

    /**
     * Add a message to message response.
     * 
     * @param property
     *            the name of property
     * @param value
     *            the value of the property as string
     */
    private void addJobSummaryTail() {
        this.messages.add(EMPTY_STRING);
    }

    /**
     * Add the job info to the response to a 'secadm job get' command according to given job record.
     *
     * If summary is not requested, adds the rows (one row for each workflow of the job) to the name multiple values response. The common job fields
     * are present only for the first row.
     * 
     * If summary is requested, adds to message response the job info.
     *
     * @param record
     *            the job info
     */
    private void addRows(final JobDto record) {
        if (record != null) {
            final List<WfResult> linkedWfResults = getWorkflowResults(record);
            addRowsToResponse(record, linkedWfResults);
        }
    }

    /**
     * Adds the rows to the response
     * 
     * @param record
     *            the job info
     * @param linkedWfResults
     *            the workflow results
     */
    private void addRowsToResponse(final JobDto record, final List<WfResult> linkedWfResults) {
        add(record.getJobId().toString(), formatJobRow(record, (linkedWfResults.isEmpty() ? new WfResult() : linkedWfResults.get(0))));
        int count = 0;
        for (final WfResult wfResult : linkedWfResults) {
            if (count > 0) {
                add(EMPTY_STRING, formatRow(JoblistStatusRow, formatWfRow(wfResult)));
            }
            count++;
        }
    }

    /**
     * Gets linked list results of all workflows results.
     * 
     * @param record
     *            the job info
     * @return the linked list results of all workflows results
     * 
     */
    private List<WfResult> getWorkflowResults(final JobDto record) {
        final List<WfResult> linkedWfResults = new LinkedList<>();
        for (final Map.Entry<String, WfResult> wfStatus : record.getStatus().entrySet()) {
            linkedWfResults.add(wfStatus.getValue());
        }
        Collections.sort(linkedWfResults);
        return linkedWfResults;
    }

    /**
     * Format a row following the first one for the multiple values section of response to a 'secadm job get' command for the given workflow info.
     *
     * These rows don't contain common job fields but only the fields related to a specific workflow.
     * 
     * @param result
     *            the workflow info
     * @return the formatted row
     */
    public Map<String, String> formatWfRow(final WfResult result) {

        final Map<String, String> row = new HashMap<String, String>();
        final WorkflowStatsFormatter workflowStatsFormatter = new WorkflowStatsFormatter(result);
        row.put(WF_NODE, workflowStatsFormatter.getNodeName());
        row.put(WF_STATUS, workflowStatsFormatter.getWfStatus());
        row.put(WF_DETAILS, workflowStatsFormatter.getWfDetails());
        row.put(WF_START_DATE, workflowStatsFormatter.getWfStartDate());
        row.put(WF_DURATION, workflowStatsFormatter.getWfDuration());
        row.put(WORKFLOW_RESULT, workflowStatsFormatter.getWfResult());
        return row;
    }

    /**
     * Format the first row for the multiple values section of response to a 'secadm job get' command for the given values.
     *
     * This row contains common job fields and the fields related to a specific workflow.
     * 
     * @param record
     *            the job info
     * @param result
     *            the workflow info
     * @return the formatted row
     */
    public String[] formatJobRow(final JobDto record, final WfResult result) {
        String[] formattedRow = null;
        final Map<String, String> row = new HashMap<String, String>();
        final JobStatsFormatter jobStatsFormatter = new JobStatsFormatter(record);

        row.put(JOB_COMMAND_ID, jobStatsFormatter.getCommandId());
        row.put(JOB_USER, jobStatsFormatter.getUserId());
        row.put(JOB_STATUS, jobStatsFormatter.getJobStatus());
        row.put(JOB_START_DATE, jobStatsFormatter.getJobStartDate());
        row.put(JOB_END_DATE, jobStatsFormatter.getJobEndDate());

        if (result != null) {
            final Map<String, String> rowWf = formatWfRow(result);
            row.putAll(rowWf);
        }

        formattedRow = formatRow(JoblistStatusRow, row);

        return formattedRow;
    }
}
