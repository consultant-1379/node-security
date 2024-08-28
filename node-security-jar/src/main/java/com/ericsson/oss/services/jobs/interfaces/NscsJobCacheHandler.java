package com.ericsson.oss.services.jobs.interfaces;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.dto.WfResult;
import com.ericsson.oss.services.jobs.dto.JobDto;

public interface NscsJobCacheHandler {

    List<JobDto> getAllJobs();

    List<JobDto> getAllJobs(List<String> filter);

    List<JobDto> getJobList(List<UUID> uuids);

    JobDto getJob(UUID jobId, List<String> filter);

    JobDto getJob(UUID jobId);

    JobStatusRecord insertJob(NscsCommandType commandType);

    void clearCache();

    void removeJob(UUID jobId);

    List<WfResult> getPendingWorkflows();

    int getRunningWorkflowsCount();

    boolean checkNoRunningWFByNodeName(final String nodeName, final List<WfResult> runningWfList);

    /**
     * Updates the status of a workflow in cache and if needed the status of the overall job.
     * 
     * The job status shall be updated when first workflow starts and when last workflow ends.
     *
     * @param data
     *            the workflow result
     */
    void updateWorkflow(WfResult data);

    /**
     * Updates only the status of a workflow in cache. The overall job status is not modified here so this method SHALL not be invoked at workflow
     * start and at workflow end!
     *
     * @param data
     *            the workflow result
     */
    void updateWorkflowOnly(WfResult data);

    /**
     * Gets the status of the given workflow in cache
     * 
     * @param wfWakeId
     *            the workflow ID
     * @return the status of the workflow
     */
    WfResult getWfResult(UUID wfWakeId);

    /**
     * Aborts a job of given UUID in NSCS job cache.
     * 
     * @param jobId
     *            the job UUID.
     * @return the updated job DTO.
     */
    JobDto abortJob(UUID jobId);

    /**
     * Inserts in cache a batch of workflows
     * 
     * @param wfResultMap
     *            the batch of workflows
     */
    void insertWorkflowBatch(Map<UUID, WfResult> wfResultMap);

    /**
     * Returns the status of the given job
     * 
     * @param jobId
     *            the job ID
     * @return the job status
     */
    JobStatusRecord getJobStatusRecord(UUID jobId);

    Map<String, List<WfResult>> getRunningAndPendingWorkflows(final int wfCongestionThreshold);

    /**
     * Updates in cache the job status with the given value
     * 
     * @param jobStatusRecord
     *            the job status
     */
    void updateJob(JobStatusRecord jobStatusRecord);

}