/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2021
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.nscs.jobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

import javax.cache.Cache;
import javax.cache.Cache.Entry;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.handler.command.utility.PasswordHelper;
import com.ericsson.nms.security.nscs.logger.NscsCompactAuditLogConstants;
import com.ericsson.nms.security.nscs.logger.NscsCompactAuditLogger;
import com.ericsson.nms.security.nscs.logger.NscsSystemRecorder;
import com.ericsson.oss.itpf.sdk.cache.annotation.NamedCache;
import com.ericsson.oss.itpf.sdk.cluster.lock.LockManager;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.dto.WfResult;
import com.ericsson.oss.services.enums.JobGlobalStatusEnum;
import com.ericsson.oss.services.enums.WfStatusEnum;
import com.ericsson.oss.services.jobs.dto.JobDto;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;
import com.ericsson.oss.services.nscs.workflow.impl.WorkflowHandlerImpl;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

@ApplicationScoped
public class NscsJobCacheHandlerImpl implements NscsJobCacheHandler {

    private static final String JOB_CACHE_NAME = "JobManagementCache";
    private static final String WF_CACHE_NAME = "WfManagementCache";
    private static final String ABORTED_WORKFLOW = "Aborted Workflow";
    private static final String SUCCESSFUL_CANCEL = "successfulCancel";
    private static final String FAILED_CANCEL = "failedCancel";
    private static final String SKIPPED_CANCEL = "skippedCancel";

    public static final String WF_KEY_RUNNING = "RUNNING";
    public static final String WF_KEY_PENDING = "PENDING";

    @Inject
    private NscsContextService nscsContextService;

    @Inject
    private LockManager jobManagementCacheLockManager;

    @Inject
    @NamedCache(JOB_CACHE_NAME)
    private Cache<UUID, JobStatusRecord> jobManagementCache;

    @Inject
    @NamedCache(WF_CACHE_NAME)
    private Cache<UUID, WfResult> wfManagementCache;

    @Inject
    private Logger logger;

    @EServiceRef
    WorkflowHandler wfHandler;

    @Inject
    private NscsSystemRecorder nscsSystemRecorder;

    @Inject
    private NscsCompactAuditLogger nscsCompactAuditLogger;

    @Inject
    private PasswordHelper passwordHelper;

    @Override
    public JobStatusRecord insertJob(final NscsCommandType commandType) {
        final String userId = nscsContextService.getUserIdContextValue();
        final String commandName = nscsContextService.getCommandTextContextValue();
        final String sourceIP = nscsContextService.getSourceIpAddrContextValue();
        final String sessionId = nscsContextService.getSessionIdContextValue();
        final String encryptedSessionId = sessionId != null ? passwordHelper.encryptEncode(sessionId) : null;
        final int numOfInvalid = (nscsContextService.getNumInvalidItemsContextValue() != null ? nscsContextService.getNumInvalidItemsContextValue()
                : NscsCompactAuditLogConstants.UNKNOWN_NUM_OF);
        logger.info("CAL_DEBUG INSERT_JOB: userId [{}] commandName [{}] encryptedSessionId [{}] sourceIP [{}] numOfInvalid [{}]", userId, commandName,
                encryptedSessionId, sourceIP, numOfInvalid);
        final UUID jobId = UUID.randomUUID();

        final JobStatusRecord record = new JobStatusRecord();
        record.setCommandId(commandType.name());
        record.setGlobalStatus(JobGlobalStatusEnum.PENDING);
        record.setUserId(userId);
        record.setJobId(jobId);
        record.setInsertDate(new Date());
        record.setCommandName(commandName);
        record.setSourceIP(sourceIP);
        record.setSessionId(encryptedSessionId);
        record.setNumOfInvalid(numOfInvalid);
        jobManagementCache.put(jobId, record);

        nscsSystemRecorder.recordJobCacheInsertedEvent(getClass().getSimpleName(), jobId, record);

        nscsContextService.setJobIdContextValue(jobId);
        logger.info("CAL_DEBUG INSERT_JOB: jobId [{}]", jobId);

        return record;
    }

    @Override
    // unused
    public void updateJob(final JobStatusRecord jobStatusRecord) {

        jobManagementCache.put(jobStatusRecord.getJobId(), jobStatusRecord);

    }

    @Override
    public void updateWorkflow(final WfResult data) {

        final UUID wfWakeId = data.getWfWakeId();
        boolean isError = true;

        //update workflow cache
        wfManagementCache.put(wfWakeId, data);

        nscsSystemRecorder.recordWorkflowCacheUpdatedEvent(getClass().getSimpleName(), wfWakeId, data);

        JobStatusRecord job = null;

        if (WfStatusEnum.RUNNING.equals(data.getStatus())) {
            //if workflow is running, we should need to move the related job id from PENDING to RUNNING just the very first time
            //no lock is acquired since the start is performed by an internal scheduler that serializes the starts themselves
            if (logger.isDebugEnabled()) {
                logger.debug("Workflow [{}] starting, need to check if job can be marked as running. No lock acquired", data);
            }
            try {
                job = jobManagementCache.get(data.getJobId());
                if (job != null) {
                    if (JobGlobalStatusEnum.PENDING.equals(job.getGlobalStatus())) {
                        job.setGlobalStatus(JobGlobalStatusEnum.RUNNING);
                        jobManagementCache.put(data.getJobId(), job);

                        nscsSystemRecorder.recordJobCacheStartedEvent(getClass().getSimpleName(), data.getJobId(), job);

                        isError = false;
                    }
                } else {
                    final String errorMessage = String.format("Null job in cache starting wf [%s]", data);
                    nscsSystemRecorder.recordJobCacheNotExistingError(getClass().getSimpleName(), data.getJobId(), errorMessage);
                }
            } catch (final Exception e) {

                final String errorMessage = String.format("Exception [%s] msg [%s] starting wf [%s]", e.getClass().getCanonicalName(),
                        e.getMessage(), data);
                nscsSystemRecorder.recordJobCacheGenericError(getClass().getSimpleName(), data.getJobId(), errorMessage);

                logger.error("Error updating a new job running", e);
            } finally {
                if (!isError) {
                    logger.debug("updateWorkflow() - Cache updated job [{}] status [{}]", data.getJobId().toString(), job.getGlobalStatus().name());
                }
            }
        } else if (WfStatusEnum.ERROR.equals(data.getStatus()) || WfStatusEnum.SUCCESS.equals(data.getStatus())) {
            logger.debug("Workflow [{}] completed, need to check if job can be marked as completed", data);

            final Lock jobManagementCacheLock = getJobManagementCacheLock();
            jobManagementCacheLock.lock();
            if (logger.isInfoEnabled()) {
                logger.info("[NSCS_DISTRIBUTED_LOCK] acquired lock {} on completed wf of jobId {}", JOB_CACHE_NAME,
                        data.getJobId() != null ? data.getJobId().toString() : null);
            }
            try {
                job = jobManagementCache.get(data.getJobId());
                if (job != null) {
                    final int actualValue = job.getLastStartedWfId();
                    if (actualValue > 0) {
                        final int counter = actualValue - 1;
                        job.setLastStartedWfId(counter);
                        if (counter == 0) {
                            job.setGlobalStatus(JobGlobalStatusEnum.COMPLETED);
                        }
                        jobManagementCache.put(data.getJobId(), job);

                        final List<WfResult> workflowList = getWorkflowsByJobId(data.getJobId());
                        nscsCompactAuditLogger.recordJobCacheCompletedCompactAudit(job, workflowList);
                        nscsSystemRecorder.recordJobCacheCompletedEvent(getClass().getSimpleName(), data.getJobId(), job,
                                workflowList);

                        isError = false;
                    } else {
                        final String errorMessage = String.format("Wrong wf counter [%s] in job [%s] completing wf [%s]", actualValue, job, data);
                        nscsSystemRecorder.recordJobCacheWrongWfCounterError(getClass().getSimpleName(), data.getJobId(), errorMessage);
                    }
                } else {
                    final String errorMessage = String.format("Null job in cache completing wf [%s]", data);
                    nscsSystemRecorder.recordJobCacheNotExistingError(getClass().getSimpleName(), data.getJobId(), errorMessage);
                }
            } catch (final Exception e) {

                final String errorMessage = String.format("Exception [%s] msg [%s] completing workflow [%s].", e.getClass().getCanonicalName(),
                        e.getMessage(), data);
                nscsSystemRecorder.recordJobCacheGenericError(getClass().getSimpleName(), data.getJobId(), errorMessage);

                logger.error("Error updating a completed job", e);
            } finally {
                if (logger.isInfoEnabled()) {
                    logger.info("[NSCS_DISTRIBUTED_LOCK] releasing lock {} on completed wf of jobId {}", JOB_CACHE_NAME,
                            data.getJobId() != null ? data.getJobId().toString() : null);
                }
                jobManagementCacheLock.unlock();
                if (!isError) {
                    logger.debug("updateWorkflow() - Cache updated job [{}] status [{}]", data.getJobId().toString(), job.getGlobalStatus().name());
                }
            }
        }
    }

    @Override
    public void updateWorkflowOnly(final WfResult data) {
        final UUID wfWakeId = data.getWfWakeId();
        wfManagementCache.put(wfWakeId, data);
        logger.debug("updateWorkflowOnly() - Cache updated for workflow [{}]", data);
    }

    @Override
    public JobDto getJob(final UUID jobId, final List<String> filters) {
        final JobStatusRecord job = jobManagementCache.get(jobId);
        if (job != null) {
            return new JobDto(job, getWorkflowsByJobId(job.getJobId(), filters));
        } else {
            return null;
        }
    }

    @Override
    public JobDto getJob(final UUID jobId) {
        return getJob(jobId, null);
    }

    @Override
    public JobStatusRecord getJobStatusRecord(final UUID jobId) {
        final JobStatusRecord job = jobManagementCache.get(jobId);
        return job;
    }

    @Override
    public WfResult getWfResult(final UUID wfWakeId) {
        return wfManagementCache.get(wfWakeId);
    }

    @Override
    public List<JobDto> getAllJobs(final List<String> filters) {

        final List<JobDto> result = new LinkedList<JobDto>();

        final Iterator<Entry<UUID, JobStatusRecord>> i = jobManagementCache.iterator();
        while (i != null && i.hasNext()) {
            final Entry<UUID, JobStatusRecord> next = i.next();
            if (next != null) {
                final JobStatusRecord job = next.getValue();
                logger.debug("Reading from cache job " + job.getJobId().toString());
                final JobDto dto = new JobDto(job, getWorkflowsByJobId(job.getJobId(), filters));
                result.add(dto);
                logger.debug("Added JobDto for job " + job);
            }
        }

        try {
            Collections.sort(result);
        } catch (final Exception e) {
            logger.error("Exception while sorting JobDto list: " + e.getMessage());
        }
        logger.debug("After sorting returning JobDto list size " + result.size());
        for (final JobDto dto : result) {
            logger.debug("Ordered job " + dto.getJobId().toString());
        }

        return result;
    }

    @Override
    public List<JobDto> getAllJobs() {
        return getAllJobs(null);
    }

    @Override
    public List<JobDto> getJobList(final List<UUID> uuids) {

        final List<JobDto> result = new LinkedList<JobDto>();
        final Set<UUID> set = new HashSet<UUID>(uuids);
        final Map<UUID, JobStatusRecord> m = jobManagementCache.getAll(set);

        for (final Map.Entry<UUID, JobStatusRecord> e : m.entrySet()) {
            final JobStatusRecord job = e.getValue();
            final JobDto dto = new JobDto(job, getWorkflowsByJobId(job.getJobId()));
            result.add(dto);
        }
        try {
            Collections.sort(result);
        } catch (final Exception e1) {
            logger.error("Exception while sorting JobDto list: " + e1.getMessage());
        }
        return result;
    }

    @Override
    public void clearCache() {
        wfManagementCache.removeAll();
        jobManagementCache.removeAll();
    }

    @Override
    public void removeJob(final UUID jobId) {
        logger.info("Removing from cache job ID [{}]", jobId);
        final List<WfResult> results = getWorkflowsByJobId(jobId);

        if (!results.isEmpty()) {
            logger.info("Removing from cache [{}] workflows of job ID [{}]", results.size(), jobId);

            final Map<String, Integer> removedWorkflowsCounters = new HashMap<>();
            removedWorkflowsCounters.put(SUCCESSFUL_CANCEL, 0);
            removedWorkflowsCounters.put(FAILED_CANCEL, 0);
            removedWorkflowsCounters.put(SKIPPED_CANCEL, 0);

            removeJobWorkflows(jobId, results, removedWorkflowsCounters);

            logger.info("Removed from cache [{}] workflows of job ID [{}] : successful cancel [{}] failed cancel [{}] skipped cancel [{}]",
                    results.size(), jobId, removedWorkflowsCounters.get(SUCCESSFUL_CANCEL), removedWorkflowsCounters.get(FAILED_CANCEL),
                    removedWorkflowsCounters.get(SKIPPED_CANCEL));
        } else {
            logger.info("No workflows for job ID [{}]", jobId);
        }
        jobManagementCache.remove(jobId);
        logger.info("Removed from cache job ID [{}]", jobId);
    }

    /**
     * Removes from cache the given workflows of given job.
     * 
     * Counters of the removed workflows are updated as well.
     * 
     * @param jobId
     *            the job ID.
     * @param results
     *            the workflows to remove.
     * @param removedWorkflowsCounters
     *            the counters.
     */
    private void removeJobWorkflows(final UUID jobId, final List<WfResult> results, final Map<String, Integer> removedWorkflowsCounters) {
        for (final WfResult result : results) {
            removeJobWorkflow(jobId, result, removedWorkflowsCounters);
        }
    }

    /**
     * Removes from cache the given workflow of given job. If the workflow is running it is also cancelled via Workflow Service.
     * 
     * Counters of the removed workflows are updated as well.
     * 
     * @param jobId
     *            the job ID.
     * @param result
     *            the workflow to remove.
     * @param removedWorkflowsCounters
     *            the counters.
     */
    private void removeJobWorkflow(final UUID jobId, final WfResult result, final Map<String, Integer> removedWorkflowsCounters) {
        logger.debug("Removing from cache workflow [{}]", result);
        if (result.getStartDate() != null) {
            if (result.getEndDate() == null) {
                if (result.getWfId() != null && !WorkflowHandlerImpl.NOT_AVAILABLE_WORKFLOW_ID.equals(result.getWfId())) {
                    logger.warn("Cancelling running workflow ID [{}] status [{}] of job ID [{}]", result.getWfId(), result.getStatus(),
                            jobId);
                    try {
                        wfHandler.cancelWorkflowInstance(result.getWfId());
                        removedWorkflowsCounters.put(SUCCESSFUL_CANCEL, removedWorkflowsCounters.get(SUCCESSFUL_CANCEL) + 1);
                    } catch (Exception e) {
                        final String errorMsg = String.format("Exception while cancelling workflow ID [%s] status [%s] of job ID [%s]",
                                result.getWfId(), result.getStatus(), jobId);
                        logger.error(errorMsg, e);
                        removedWorkflowsCounters.put(FAILED_CANCEL, removedWorkflowsCounters.get(FAILED_CANCEL) + 1);
                    }
                } else {
                    // Old-style workflow. This could happen during an upgrade from an ISO not containing changes related to TORF-534080
                    // (cancel of long-lived workflows during cache eviction) to an ISO containing the changes.
                    // Before implementation of TORF-534080, the workflow ID was not saved in workflow cache after starting the workflow,
                    // so long-lived running workflows started with old ISO cannot be deleted by cache eviction in new ISO.
                    logger.warn("Cannot cancel running workflow [{}] of job ID [{}]", result, jobId);
                    removedWorkflowsCounters.put(SKIPPED_CANCEL, removedWorkflowsCounters.get(SKIPPED_CANCEL) + 1);
                }
            } else {
                logger.debug("No need to cancel finished workflow ID [{}] status [{}] of job ID [{}]", result.getWfId(), result.getStatus(),
                        jobId);
            }
        } else {
            logger.debug("No need to cancel not started workflow ID [{}] status [{}] of job ID [{}]", result.getWfId(), result.getStatus(),
                    jobId);
        }
        wfManagementCache.remove(result.getWfWakeId());
        logger.debug("Removed from cache workflow [{}] of job ID [{}]", result, jobId);
    }

    @Override
    public List<WfResult> getPendingWorkflows() {

        final List<WfResult> results = new LinkedList<WfResult>();
        final List<WfResult> workflows = getAllWorkflows();
        for (final WfResult wf : workflows) {
            if (wf != null && WfStatusEnum.PENDING.equals(wf.getStatus())) {
                results.add(wf);
            }
        }
        return results;
    }

    @Override
    public int getRunningWorkflowsCount() {
        int count = 0;
        final List<WfResult> workflows = getAllWorkflows();
        for (final WfResult wf : workflows) {
            if (wf != null && WfStatusEnum.RUNNING.equals(wf.getStatus())) {
                count++;
            }
        }

        return count;
    }

    /**
     *
     * @param nodeName
     * @return true if no running workflow is present for the node
     */
    @Override
    public boolean checkNoRunningWFByNodeName(final String nodeName, final List<WfResult> runningWfList) {
        boolean result = true;

        for (final WfResult wf : runningWfList) {
            if (wf != null && wf.getNodeName().equals(nodeName)) {
                result = false;
                break;
            }
        }
        return result;
    }

    private Lock getJobManagementCacheLock() {
        return jobManagementCacheLockManager.getDistributedLock(JOB_CACHE_NAME);
    }

    public List<WfResult> getWorkflowsByJobId(final UUID jobId, final List<String> filters) {

        final List<WfResult> workflows = new ArrayList<WfResult>();
        final JobStatusRecord record = jobManagementCache.get(jobId);

        if (record != null) {

            int numOfTotWf = -1;

            try {
                numOfTotWf = record.getNumOfTotWf();
            } catch (final Exception e) {
                logger.error("Exception while reading the getNumOfTotWf() for job " + jobId);
            }

            if (numOfTotWf > 0) {
                for (int workflowId = 1; workflowId <= numOfTotWf; workflowId++) {
                    //calculate the wfUUID depending on index and job UUID
                    final String wfResultUUIDString = record.getJobId().toString() + workflowId;
                    final UUID wakeID = UUID.nameUUIDFromBytes(wfResultUUIDString.getBytes());
                    logger.debug("getWorkflowsByJobId Finding wfID " + wakeID.toString() + " for job id " + jobId);

                    final WfResult wfResult = wfManagementCache.get(wakeID);
                    if (wfResult != null) {
                        if (filters == null || (filters.size() > 0 && filters.contains(wfResult.getStatus().name()))) {
                            workflows.add(wfResult);
                            logger.debug("getWorkflowsByJobId get wfid " + wfResult.getWfWakeId().toString() + " for jobId "
                                    + wfResult.getJobId().toString() + " filters " + filters);
                        }
                    }
                }
            } else {
                logger.debug("getWorkflowsByJobId Job id " + jobId + " is old style, numOfTotWf [" + numOfTotWf + "], scanning whole workflow cache");
                final Iterator<Entry<UUID, WfResult>> i = wfManagementCache.iterator();
                while (i.hasNext()) {
                    final WfResult result = i.next().getValue();

                    if (jobId.equals(result.getJobId()) && (filters == null || (filters.size() > 0 && filters.contains(result.getStatus().name())))) {
                        workflows.add(result);
                        logger.debug("getWorkflowsByJobId get wfid " + result.getWfWakeId().toString() + " for jobId " + result.getJobId().toString()
                                + " filters " + filters);
                    }
                }
            }

            try {
                Collections.sort(workflows);
            } catch (final Exception e) {
                logger.error("Exception while sorting WfResult list: " + e.getMessage());
            }
        }
        return workflows;
    }

    public List<WfResult> getWorkflowsByJobId(final UUID jobId) {
        return getWorkflowsByJobId(jobId, null);
    }

    public List<WfResult> getAllWorkflows() {
        final Iterator<Entry<UUID, WfResult>> i = wfManagementCache.iterator();
        final List<WfResult> workflows = new ArrayList<WfResult>();
        while (i.hasNext()) {
            final WfResult result = i.next().getValue();
            workflows.add(result);
        }

        try {
            Collections.sort(workflows);
        } catch (final Exception e) {
            logger.error("Exception while sorting WfResult list: " + e.getMessage());
        }
        return workflows;
    }

    @Override
    public JobDto abortJob(final UUID jobId) {

        logger.info("Aborting job [{}]", jobId);

        JobStatusRecord job = jobManagementCache.get(jobId);
        if (job == null) {
            logger.info("Not existing job [{}]", jobId);
            return null;
        }
        final List<WfResult> results = getWorkflowsByJobId(jobId);
        if (JobGlobalStatusEnum.COMPLETED.equals(job.getGlobalStatus())) {
            logger.info("Already completed job [{}]", jobId);
            return new JobDto(job, results);
        }
        for (final WfResult result : results) {
            if (!WfStatusEnum.SUCCESS.equals(result.getStatus()) && !WfStatusEnum.ERROR.equals(result.getStatus())) {
                if (result.getWfId() != null && !WorkflowHandlerImpl.NOT_AVAILABLE_WORKFLOW_ID.equals(result.getWfId())) {
                    logger.info("Cancelling running workflow ID [{}] status [{}] of to be aborted job ID [{}]", result.getWfId(), result.getStatus(),
                            jobId);
                    try {
                        wfHandler.cancelWorkflowInstance(result.getWfId());
                        logger.info("Successfully cancelled running workflow ID [{}] status [{}] of to be aborted job ID [{}]", result.getWfId(),
                                result.getStatus(), jobId);
                    } catch (final Exception e) {
                        final String errorMsg = String.format("Exception while cancelling workflow ID [%s] status [%s] of job ID [%s]",
                                result.getWfId(), result.getStatus(), jobId);
                        logger.error(errorMsg, e);
                    }
                } else {
                    // Pending workflow or old-style running workflow.
                    // The second case could happen after an upgrade from an ISO not containing changes related to TORF-534080
                    // to an ISO containing the changes.
                    // Before implementation of TORF-534080, the workflow ID was not saved in workflow cache after starting the workflow.
                    logger.warn("Skipped cancel of pending or running workflow [{}] of job ID [{}]", result, jobId);
                }
                result.setStatus(WfStatusEnum.ERROR);
                result.setMessage(ABORTED_WORKFLOW);
                updateWorkflow(result);
                logger.info("Forced finished with error for workflow [{}]", result);
            }
        }

        // read again job and its workflows
        job = jobManagementCache.get(jobId);
        return new JobDto(job, getWorkflowsByJobId(jobId));

    }

    @Override
    public void insertWorkflowBatch(final Map<UUID, WfResult> wfResultMap) {

        if (wfResultMap != null && !wfResultMap.isEmpty()) {

            /*
             * TORF-545019
             * 
             * Starting from sprint 21.15, the insertion of the workflow batch in the Workflow Cache has been moved from here to AFTER the update of
             * the correspondent job record in the Job Cache (its wf counters are updated at this stage).
             * 
             * This to avoid a possible race condition: once the workflows are added as PENDING in the Workflow Cache, the Workflow Periodic Scheduler
             * can start them.
             * 
             * If the workflows should be completed BEFORE the wf counters in the correspondent job record in the Job Cache have been updated, it
             * could happen that the updateWorkflow method finds wrong counters in job record and skips its update (leaving the job in RUNNING state
             * even if all its workflows are completed).
             */

            final Map.Entry<UUID, WfResult> entry = wfResultMap.entrySet().iterator().next();
            final JobStatusRecord job = jobManagementCache.get(entry.getValue().getJobId());
            job.setNumOfTotWf(wfResultMap.size());
            job.setLastStartedWfId(wfResultMap.size());
            jobManagementCache.put(job.getJobId(), job);

            nscsSystemRecorder.recordJobCacheUpdatedOnWfInsertionEvent(getClass().getSimpleName(), entry.getValue().getJobId(), job,
                    wfResultMap.size());
            /*
             * TORF-545019
             * 
             * Starting from sprint 21.15, the insertion of the workflow batch in the Workflow Cache has been moved here from BEFORE the update of the
             * correspondent job record in the Job Cache (its wf counters are updated at this stage).
             */
            wfManagementCache.putAll(wfResultMap);

            nscsSystemRecorder.recordWorkflowCacheUpdatedOnWfInsertionEvent(getClass().getSimpleName(), wfResultMap.size());

            logger.debug("insertWorkflowBatch() - Workflow and Job Caches updated with data: {}", entry.getValue());
        }
    }

    @Override
    public Map<String, List<WfResult>> getRunningAndPendingWorkflows(final int wfCongestionThreshold) {

        logger.debug("Starting getRunningAndPendingWorkflows, threshold " + wfCongestionThreshold);
        final Map<String, List<WfResult>> map = new HashMap<String, List<WfResult>>();

        final List<JobStatusRecord> jobStatusRecordList = new ArrayList<JobStatusRecord>();

        /*
         * Map <String, WfResult>:
         *
         * String: node name ; WfResult: WfResult instance
         */

        final Map<String, WfResult> nodePendingWfMap = new HashMap<String, WfResult>();
        final Map<String, WfResult> nodeRunningWfMap = new HashMap<String, WfResult>();

        if (wfCongestionThreshold > 0) {

            //Iterate through job cache and get just RUNNING or PENDING and return a sorted list
            final Iterator<Entry<UUID, JobStatusRecord>> j = jobManagementCache.iterator();
            while (j.hasNext()) {
                final JobStatusRecord record = j.next().getValue();

                if (record != null) {
                    if (JobGlobalStatusEnum.RUNNING.equals(record.getGlobalStatus())
                            || JobGlobalStatusEnum.PENDING.equals(record.getGlobalStatus())) {
                        jobStatusRecordList.add(record);
                        logger.debug("Adding job " + record.getJobId().toString() + ", status " + record.getGlobalStatus().name() + " to the list");
                    }
                }
            }
            //sort job list
            try {
                Collections.sort(jobStatusRecordList);
            } catch (final Exception e1) {
                logger.error("Exception while sorting JobStatusRecord list: " + e1.getMessage());
            }

            for (final JobStatusRecord record : jobStatusRecordList) {
                final String jobId = record.getJobId().toString();

                int numOfTotWf = -1;

                try {
                    numOfTotWf = record.getNumOfTotWf();
                } catch (final Exception e) {
                    logger.error("Exception while reading the getNumOfTotWf() for job " + jobId);
                }

                if (numOfTotWf > 0) {
                    logger.debug("Job id " + jobId + " contains " + numOfTotWf + " workflows");

                    for (int workflowId = 1; workflowId <= numOfTotWf; workflowId++) {
                        //calculate the wfUUID depending on index and job UUID
                        final String wfResultUUIDString = jobId + workflowId;
                        final UUID wakeID = UUID.nameUUIDFromBytes(wfResultUUIDString.getBytes());
                        logger.debug("Finding wfID " + wakeID.toString() + " for job id " + jobId);

                        final WfResult wfResult = wfManagementCache.get(wakeID);
                        if (wfResult != null) {
                            if (WfStatusEnum.PENDING.equals(wfResult.getStatus())) {
                                //check if this pending workflow instance is already running or already set in pending list
                                if (!nodeRunningWfMap.containsKey(wfResult.getNodeName()) && !nodePendingWfMap.containsKey(wfResult.getNodeName())) {
                                    nodePendingWfMap.put(wfResult.getNodeName(), wfResult);
                                    logger.debug("Putting node " + wfResult.getNodeName() + " in pending list");
                                }
                            } else if (WfStatusEnum.RUNNING.equals(wfResult.getStatus())) {
                                if (!nodeRunningWfMap.containsKey(wfResult.getNodeName())) {
                                    nodeRunningWfMap.put(wfResult.getNodeName(), wfResult);
                                    logger.debug("Putting node " + wfResult.getNodeName() + " in running list");
                                    if (nodeRunningWfMap.size() == wfCongestionThreshold) {
                                        //system full
                                        logger.info("System full, all available [" + wfCongestionThreshold + "] workflows instances are running");
                                        nodePendingWfMap.clear();
                                        break;
                                    }

                                    //if a workflow is running for a node, the pending map should be cleared for the same node
                                    if (nodePendingWfMap.containsKey(wfResult.getNodeName())) {
                                        nodePendingWfMap.remove(wfResult.getNodeName());
                                        logger.debug("Removing running node " + wfResult.getNodeName() + " from pending list");
                                    }
                                }
                            } else {
                                logger.debug("Found wfID " + wakeID.toString() + " for job id " + jobId + " in state " + wfResult.getStatus().name());
                            }
                        } else {
                            final String noticeMessage = String.format("Null wf in cache for job ID [%s] with wf counter [%s]", jobId, numOfTotWf);
                            nscsSystemRecorder.recordWorkflowCacheNotYetExistingNotice(getClass().getSimpleName(), wakeID, noticeMessage);
                        }
                    }
                } else {
                    //backward compatibility for scheduling old job with no workflow information and with
                    //workflow UUID completely random, so scanning the whole cache is required
                    logger.info("Job id " + jobId + " is old style, numOfTotWf [" + numOfTotWf + "], scanning whole workflow cache");
                    final Iterator<Entry<UUID, WfResult>> i = wfManagementCache.iterator();
                    while (i.hasNext()) {
                        final WfResult result = i.next().getValue();

                        if (result != null) {
                            if (WfStatusEnum.RUNNING.equals(result.getStatus())) {

                                if (!nodeRunningWfMap.containsKey(result.getNodeName())) {
                                    nodeRunningWfMap.put(result.getNodeName(), result);
                                    logger.debug("Putting node " + result.getNodeName() + " in running list");
                                    if (nodeRunningWfMap.size() == wfCongestionThreshold) {
                                        //system full
                                        logger.info("System full, all available [" + wfCongestionThreshold + "] workflows instances are running");
                                        nodePendingWfMap.clear();
                                        break;
                                    }

                                    //if a workflow is running for a node, the pending map should be cleared for the same node
                                    if (nodePendingWfMap.containsKey(result.getNodeName())) {
                                        nodePendingWfMap.remove(result.getNodeName());
                                        logger.debug("Removing runnig node " + result.getNodeName() + " from pending list");
                                    }
                                }

                            } else if (WfStatusEnum.PENDING.equals(result.getStatus())) {
                                if (!nodeRunningWfMap.containsKey(result.getNodeName()) && !nodePendingWfMap.containsKey(result.getNodeName())) {
                                    nodePendingWfMap.put(result.getNodeName(), result);
                                    logger.debug("Putting node " + result.getNodeName() + " in pending list");
                                }
                            }
                        }
                    }
                }
            }
        }

        List<WfResult> pendingWorkflows = new ArrayList<WfResult>(nodePendingWfMap.values());
        final List<WfResult> runningWorkflows = new ArrayList<WfResult>(nodeRunningWfMap.values());

        //sort just pending, no matter on running
        try {
            Collections.sort(pendingWorkflows);
        } catch (final Exception e) {
            logger.error("Exception while sorting workflow list: " + e.getMessage());
        }

        //cut from pending list just the first items depending on threshold and running workflow map size
        if (wfCongestionThreshold > 0) {
            final int numOfPendingWfToGet = wfCongestionThreshold - nodeRunningWfMap.size();
            logger.debug("Read numOfPendingWfToGet [" + numOfPendingWfToGet + "], possible pending workflows size " + pendingWorkflows.size());
            if (numOfPendingWfToGet <= 0) {
                //system is full, no more pending can be executed
                pendingWorkflows.clear();
                logger.info("No pending workflows can be scheduled, wfCongestionThreshold [" + wfCongestionThreshold
                        + "], running workflows map size[" + nodeRunningWfMap.size() + "], numOfPendingWfToGet [" + numOfPendingWfToGet + "]");
            } else {

                if (pendingWorkflows.size() > numOfPendingWfToGet) {
                    pendingWorkflows = pendingWorkflows.subList(0, numOfPendingWfToGet);
                    logger.debug("Getting just first [" + numOfPendingWfToGet + "] pending workflows");
                }
            }
        }

        map.put(WF_KEY_RUNNING, runningWorkflows);
        map.put(WF_KEY_PENDING, pendingWorkflows);

        return map;

    }

}