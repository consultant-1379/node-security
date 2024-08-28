package com.ericsson.nms.security.nscs.cpp.level;

import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.dto.WfResult;

/**
 * The base interface for all supported Command Handlers.
 *
 * @author eabdsin
 *
 */
public interface SecLevelProcessor {

    /**
     * To process the request passed in with the required status check
     *
     * @param newParam newParam
     *
     *
     */
    void processCommand(SecLevelRequest newParam);

    /**
     * To process the request passed in with the required status check Overloaded for job id cmd triggered from webcli
     *
     * @param newParam
     *            the securityLevelChange request
     *
     * @param jobStatusRecord
     *            the jobStatusRecord to track the status of job
     *
     * @param workflowId
     *            the workflowId 1-based to identify each single workflow of the job record
     * @return - the WfResult instance
     */
    WfResult processCommand(SecLevelRequest newParam, JobStatusRecord jobStatusRecord, final int workflowId);
}