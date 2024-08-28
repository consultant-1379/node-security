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
package com.ericsson.nms.security.nscs.logger;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.recording.CommandPhase;
import com.ericsson.oss.itpf.sdk.recording.ErrorSeverity;
import com.ericsson.oss.itpf.sdk.recording.EventLevel;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.dto.WfResult;
import com.ericsson.oss.services.enums.JobGlobalStatusEnum;
import com.ericsson.oss.services.security.nscs.command.CommandHandlerStatsFormatter;
import com.ericsson.oss.services.security.nscs.jobs.JobStatsFormatter;
import com.ericsson.oss.services.security.nscs.jobs.WorkflowStatsFormatter;

/**
 * Auxiliary class to manage system recorder logging.
 */
@Stateless
public class NscsSystemRecorder {

    /**
     * Error IDs
     */
    private static final String NOT_EXISTING_ERROR_ID = "NOT_EXISTING";
    private static final String WRONG_WF_COUNTER_ERROR_ID = "WRONG_WF_COUNTER";
    private static final String GENERIC_ERROR_ID = "GENERIC";

    /**
     * Event Types
     */
    private static final String COMPLETED_EVENT_TYPE = "COMPLETED";
    private static final String UPDATED_EVENT_TYPE = "UPDATED";
    private static final String UPDATED_ON_WF_COMPLETION_EVENT_TYPE = "UPDATED_ON_WF_COMPLETION";
    private static final String UPDATED_ON_WF_INSERTION_EVENT_TYPE = "UPDATED_ON_WF_INSERTION";
    private static final String STARTED_EVENT_TYPE = "STARTED";
    private static final String INSERTED_EVENT_TYPE = "INSERTED";
    private static final String JOB_COMPLETED_EVENT_DATA_TYPE = "JOB_COMPLETED";
    private static final String COMMAND_HANDLER_COMPLETED_EVENT_DATA_TYPE = "COMMAND_HANDLER_COMPLETED";

    /**
     * NSCS Resources
     */
    private static final String JOB_CACHE_RESOURCE = "JOB_CACHE";
    private static final String WORKFLOW_CACHE_RESOURCE = "WORKFLOW_CACHE";

    /**
     * NSCS Source
     */
    private static final String NSCS_SOURCE = "NSCS";

    /**
     * Compact Audit Log source.
     * 
     * A string representing the entity which has directly or indirectly caused the logged action.
     * 
     * It shall be the CNA Name of the invoking application, as it is defined in “Product Role Matrix”.
     */
    private static final String COMPACT_AUDIT_NODE_SECURITY_SOURCE = "Node Security";

    /**
     * NSCS namespace
     */
    private static final String NSCS_NS = "NODE_SECURITY";

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private SystemRecorder systemRecorder;

    /**
     * Records the insertion of a job of given ID in the NSCS Job Cache.
     * 
     * @param className
     *            the name of the calling class.
     * @param jobId
     *            the job ID.
     * @param job
     *            the inserted job record.
     */
    public void recordJobCacheInsertedEvent(final String className, final UUID jobId, final JobStatusRecord job) {
        if (job != null) {
            final JobStatsFormatter jobStatsFormatter = new JobStatsFormatter(job);
            recordEvent(getJobCacheEventType(INSERTED_EVENT_TYPE), getSource(className), getJobCacheResource(jobId), jobStatsFormatter.toString());
        } else {
            final String errorMessage = "Null inserted job";
            recordJobCacheNotExistingError(className, jobId, errorMessage);
        }
    }

    /**
     * Records the update of a job of given ID in the NSCS Job Cache on insertion of a workflow batch.
     * 
     * The counter of not completed workflows shall be set.
     * 
     * @param className
     *            the name of the calling class.
     * @param jobId
     *            the job ID.
     * @param job
     *            the updated job record.
     * @param batchSize
     *            the workflow batch size.
     */
    public void recordJobCacheUpdatedOnWfInsertionEvent(final String className, final UUID jobId, final JobStatusRecord job, final int batchSize) {
        if (job != null) {
            final JobStatsFormatter jobStatsFormatter = new JobStatsFormatter(job);
            final String additionalInfo = String.format("Updated job [%s] on insertion of wf batch of size [%s]", jobStatsFormatter.toString(),
                    batchSize);
            recordEvent(getJobCacheEventType(UPDATED_ON_WF_INSERTION_EVENT_TYPE), getSource(className), getJobCacheResource(jobId), additionalInfo);
        } else {
            final String errorMessage = String.format("Null job in cache on insertion of wf batch of size [%s]", batchSize);
            recordJobCacheNotExistingError(className, jobId, errorMessage);
        }
    }

    /**
     * Records the start of a job of given ID in the NSCS Job Cache.
     * 
     * A job starts when its status changes from PENDING to RUNNING.
     * 
     * @param className
     *            the name of the calling class.
     * @param jobId
     *            the job ID.
     * @param job
     *            the started job record.
     */
    public void recordJobCacheStartedEvent(final String className, final UUID jobId, final JobStatusRecord job) {
        if (job != null) {
            final JobStatsFormatter jobStatsFormatter = new JobStatsFormatter(job);
            recordEvent(getJobCacheEventType(STARTED_EVENT_TYPE), getSource(className), getJobCacheResource(jobId), jobStatsFormatter.toString());
        } else {
            final String errorMessage = "Null job in cache on start";
            recordJobCacheNotExistingError(className, jobId, errorMessage);
        }
    }

    /**
     * Records the update of a job of given ID in the NSCS Job Cache on completion of one of its workflows.
     * 
     * The job can complete (its status changes from RUNNING to COMPLETED) if all its workflows are completed.
     * 
     * The stats of results of workflows of involved job are recorded too.
     * 
     * @param className
     *            the name of the calling class.
     * @param jobId
     *            the job ID.
     * @param job
     *            the updated job record.
     * @param wfResults
     *            the updated list of results for the workflows of the involved job.
     */
    public void recordJobCacheCompletedEvent(final String className, final UUID jobId, final JobStatusRecord job, final List<WfResult> wfResults) {
        if (job != null) {
            final JobStatsFormatter jobStatsFormatter = new JobStatsFormatter(job, wfResults);
            if (JobGlobalStatusEnum.COMPLETED.equals(job.getGlobalStatus())) {
                recordEvent(getJobCacheEventType(COMPLETED_EVENT_TYPE), getSource(className), getJobCacheResource(jobId),
                        jobStatsFormatter.toString());

                /**
                 * DDP is parsing the following event data and DDP team needs to be notified of changes
                 */
                recordEventData(getEventDataType(JOB_COMPLETED_EVENT_DATA_TYPE), jobStatsFormatter.toEventData());
            } else {
                recordEvent(getJobCacheEventType(UPDATED_ON_WF_COMPLETION_EVENT_TYPE), getSource(className), getJobCacheResource(jobId),
                        jobStatsFormatter.toString());
            }
        } else {
            final String errorMessage = "Null job in cache on wf completion";
            recordJobCacheNotExistingError(className, jobId, errorMessage);
        }
    }

    /**
     * Records a not existing job error.
     * 
     * @param className
     *            the name of the calling class.
     * @param jobId
     *            the job ID.
     * @param errorMessage
     *            the error message.
     */
    public void recordJobCacheNotExistingError(final String className, final UUID jobId, final String errorMessage) {
        recordError(getJobCacheErrorId(NOT_EXISTING_ERROR_ID), getSource(className), getJobCacheResource(jobId), errorMessage);
    }

    /**
     * Records a wrong workflows counter error.
     * 
     * @param className
     *            the name of the calling class.
     * @param jobId
     *            the job ID.
     * @param errorMessage
     *            the error message.
     */
    public void recordJobCacheWrongWfCounterError(final String className, final UUID jobId, final String errorMessage) {
        recordError(getJobCacheErrorId(WRONG_WF_COUNTER_ERROR_ID), getSource(className), getJobCacheResource(jobId), errorMessage);
    }

    /**
     * Records a generic error (e.g. unexpected exceptions) managing Job Cache.
     * 
     * @param className
     *            the name of the calling class.
     * @param jobId
     *            the job ID.
     * @param errorMessage
     *            the error message.
     */
    public void recordJobCacheGenericError(final String className, final UUID jobId, final String errorMessage) {
        recordError(getJobCacheErrorId(GENERIC_ERROR_ID), getSource(className), getJobCacheResource(jobId), errorMessage);
    }

    /**
     * Records the update of the NSCS Workflow Cache on insertion of a workflow batch.
     * 
     * @param className
     *            the name of the calling class.
     * @param batchSize
     *            the workflow batch size.
     */
    public void recordWorkflowCacheUpdatedOnWfInsertionEvent(final String className, final int batchSize) {

        final String additionalInfo = String.format("Updated on insertion of wf batch of size [%s]", batchSize);
        recordEvent(getWorkflowCacheEventType(UPDATED_ON_WF_INSERTION_EVENT_TYPE), getSource(className), getWorkflowCacheResource(null),
                additionalInfo);
    }

    /**
     * Records the update of a workflow of given wake ID in the NSCS Workflow Cache.
     * 
     * @param className
     *            the name of the calling class.
     * @param wfWakeId
     *            the workflow wake ID.
     * @param workflow
     *            the updated workflow record.
     */
    public void recordWorkflowCacheUpdatedEvent(final String className, final UUID wfWakeId, final WfResult workflow) {
        if (workflow != null) {
            final WorkflowStatsFormatter workflowStatsFormatter = new WorkflowStatsFormatter(workflow);
            recordEvent(getWorkflowCacheEventType(UPDATED_EVENT_TYPE), getSource(className), getWorkflowCacheResource(wfWakeId),
                    workflowStatsFormatter.toString());
        } else {
            final String errorMessage = "Null workflow in cache on update";
            recordWorkflowCacheNotExistingError(className, wfWakeId, errorMessage);
        }
    }

    /**
     * Records a not existing workflow error.
     * 
     * @param className
     *            the name of the calling class.
     * @param wfWakeId
     *            the workflow wake ID.
     * @param errorMessage
     *            the error message.
     */
    public void recordWorkflowCacheNotExistingError(final String className, final UUID wfWakeId, final String errorMessage) {
        recordError(getWorkflowCacheErrorId(NOT_EXISTING_ERROR_ID), getSource(className), getWorkflowCacheResource(wfWakeId), errorMessage);
    }

    /**
     * Records a not yet existing workflow notice.
     * 
     * @param className
     *            the name of the calling class.
     * @param wfWakeId
     *            the workflow wake ID.
     * @param noticeMessage
     *            the notice message.
     */
    public void recordWorkflowCacheNotYetExistingNotice(final String className, final UUID wfWakeId, final String noticeMessage) {
        recordNotice(getWorkflowCacheErrorId(NOT_EXISTING_ERROR_ID), getSource(className), getWorkflowCacheResource(wfWakeId), noticeMessage);
    }

    /**
     * Records a command.
     * 
     * @param commandName
     *            the command name.
     * @param commandPhase
     *            the command phase.
     * @param source
     *            the source of the command.
     * @param resource
     *            the resource involved in the command.
     * @param additionalInfo
     *            the additional information about the command.
     */
    public void recordCommand(final String commandName, final CommandPhase commandPhase, final String source, final String resource,
            final String additionalInfo) {
        systemRecorder.recordCommand(commandName, commandPhase, source, resource, additionalInfo);
    }

    /**
     * Records error.
     * 
     * @param errorId
     *            the type of the error recorded. The error type is considered API, i.e. once baselined may not be modified spontaneously. Consumers
     *            of recordings may code their logic against error types. The error type should contain a simplified namespace to avoid name clashes,
     *            e.g. "OLTP_DATABASE.VOLUME_FULL". Must not be null or empty String.
     * @param severity
     *            error severity. Must not be null.
     * @param source
     *            the source of the error. This should be the entity which has directly or indirectly caused the error (note: not the service itself).
     *            What this is depends on the error that is being recorded. Most often, this will be the identity of a Network Element, or possibly a
     *            Planned View.
     * @param resource
     *            the entity directly affected by the error. Good examples are a Cell ID, a FDN, a Subscriber ID etc., in other words, the entity
     *            which directly relates to the event.
     * @param additionalInformation
     *            text with additional information. Must not exceed 50KB in size.
     */
    public void recordError(final String errorId, final ErrorSeverity severity, final String source, final String resource,
            final String additionalInformation) {
        systemRecorder.recordError(errorId, severity, source, resource, additionalInformation);
    }

    /**
     * Record security events.
     * 
     * @param target
     *            This field must record the target on which the operation was executed.
     * @param distinguishedName
     *            This field must record the program/process that originated the log entry.
     * @param additionalInfo
     *            This field must record a textual description of the network access and any other related information from an operator’s perspective,
     *            such as specific parameter information.
     * @param eventType
     *            This field must record the type of security event that is being logged.
     * @param severity
     *            This field must record the severity level in relation to access violations.
     * @param eventStatus
     *            This field must record the status of the event as it is now which can be SUCCESS or FAILURE.
     */
    public void recordSecurityEvent(final String target, final String distinguishedName, final String additionalInfo, final String eventType,
            final ErrorSeverity severity, final String eventStatus) {
        systemRecorder.recordSecurityEvent(target, distinguishedName, additionalInfo, eventType, severity, eventStatus);
    }

    /**
     * Records the command handler completed event for command requiring it (its command ID is not null).
     * 
     * @param commandHandlerStatsFormatter
     *            the command handler statistics formatter.
     */
    public void recordCommandHandlerCompletedEvent(final CommandHandlerStatsFormatter commandHandlerStatsFormatter) {
        if (commandHandlerStatsFormatter.getCommandId() != null) {
            /**
             * DDP is parsing the following event data and DDP team needs to be notified of changes
             */
            recordEventData(getEventDataType(COMMAND_HANDLER_COMPLETED_EVENT_DATA_TYPE), commandHandlerStatsFormatter.toEventData());
        }
    }

    /**
     * Records Compact Audit Log (CAL).
     * 
     * @param userId
     *            the user ID.
     * @param commandName
     *            the name of the command executed. Must not be null or empty String.
     * @param commandPhase
     *            the phase of command. Must not be null.
     * @param resource
     *            the entity directly affected by the command. Good examples for Node Security are a Node, a Proxy Account etc., in other words, the
     *            entity which directly relates to the command.
     * @param sourceIP
     *            the source IP Address.
     * @param sessionId
     *            the session id.
     * @param additionalInfo
     *            text with additional information. Must not exceed 50KB in size.
     */
    public void recordCompactAudit(final String userId, final String commandName, final CommandPhase commandPhase,
            final String resource, final String sourceIP, final String sessionId, final String additionalInfo) {
        logger.debug(
                "CAL_DEBUG : invoking systemRecorder.recordCompactAudit : userId [{}] commandName [{}] commandPhase [{}] source [{}] resource [{}] sourceIP [{}] sessionId [{}] additionalInfo [{}]",
                userId, commandName, commandPhase, COMPACT_AUDIT_NODE_SECURITY_SOURCE, resource, sourceIP, sessionId != null ? "*******" : null,
                additionalInfo);
        systemRecorder.recordCompactAudit(userId, commandName, commandPhase, COMPACT_AUDIT_NODE_SECURITY_SOURCE, resource, sourceIP, sessionId,
                additionalInfo);
    }

    /**
     * Records a generic event of level DETAILED.
     * 
     * @param eventType
     *            the type of the event recorded. Must not be null or empty String. The event type is considered API, i.e. once baselined may not be
     *            modified spontaneously. Consumers of recordings may code their logic against event types. The event type should contain a simplified
     *            namespace to avoid name clashes, e.g. "JOB_CACHE.INSERTED".
     * @param source
     *            the source of the event. This should be the entity which has directly or indirectly caused the event (note: not the service itself).
     *            What this is depends on the event that is being recorded.
     * @param resource
     *            the resource involved in the event.
     * @param additionalInformation
     *            the additional information about the event.
     */
    private void recordEvent(final String eventType, final String source, final String resource, final String additionalInformation) {
        systemRecorder.recordEvent(eventType, EventLevel.DETAILED, source, resource, additionalInformation);
    }

    /**
     * Records an event data.
     * 
     * @param eventType
     *            the type of the event recorded. Must not be null or empty String or contain white space. The event type is considered API, i.e. once
     *            baselined may not be modified spontaneously. Consumers of recordings may code their logic against event types. The event type should
     *            contain a simplified namespace to avoid name clashes, e.g. "NODE_SECURITY.JOB_COMPLETED".
     * @param eventData
     *            a Map containing the key-value pairs for the event data. The names must be non-empty strings, white space in the key is prohibited
     *            The value must be non-null For privacy and security reasons, usernames / user IDs / IP addresses and any types of PII data are not
     *            allowed.
     */
    private void recordEventData(final String eventType, final Map<String, Object> eventData) {
        systemRecorder.recordEventData(eventType, eventData);
    }

    /**
     * Records a generic error with severity ERROR.
     * 
     * This severity denotes non-urgent failures, these should be relayed to developers or administrators.
     * 
     * @param errorId
     *            the type of the error recorded. The error type is considered API, i.e. once baselined may not be modified spontaneously. Consumers
     *            of recordings may code their logic against error types. The error type should contain a simplified namespace to avoid name clashes,
     *            e.g. "JOB_CACHE.WRONG_WF_COUNTER". Must not be null or empty String.
     * @param source
     *            the source of the error. This should be the entity which has directly or indirectly caused the error (note: not the service itself).
     *            What this is depends on the error that is being recorded.
     * @param resource
     *            the entity directly affected by the error.
     * @param additionalInformation
     *            text with additional information. Must not exceed 50KB in size.
     */
    private void recordError(final String errorId, final String source, final String resource, final String additionalInformation) {
        systemRecorder.recordError(errorId, ErrorSeverity.ERROR, source, resource, additionalInformation);
    }

    /**
     * Records a generic error with severity NOTICE.
     * 
     * This severity denotes an event that are unusual but not error conditions.
     * 
     * @param errorId
     *            the type of the error recorded. The error type is considered API, i.e. once baselined may not be modified spontaneously. Consumers
     *            of recordings may code their logic against error types. The error type should contain a simplified namespace to avoid name clashes,
     *            e.g. "JOB_CACHE.WRONG_WF_COUNTER". Must not be null or empty String.
     * @param source
     *            the source of the error. This should be the entity which has directly or indirectly caused the error (note: not the service itself).
     *            What this is depends on the error that is being recorded.
     * @param resource
     *            the entity directly affected by the error.
     * @param additionalInformation
     *            text with additional information. Must not exceed 50KB in size.
     */
    private void recordNotice(final String errorId, final String source, final String resource, final String additionalInformation) {
        systemRecorder.recordError(errorId, ErrorSeverity.NOTICE, source, resource, additionalInformation);
    }

    /**
     * Returns the NSCS source for the given class name.
     * 
     * @param className
     *            the class name.
     * @return the NSCS source.
     */
    private String getSource(final String className) {
        return String.format("%s.%s", NSCS_SOURCE, className);
    }

    /**
     * Returns the NSCS Job Cache resource for the given job ID.
     * 
     * @param jobId
     *            the job ID.
     * @return the NSCS Job Cache resource.
     */
    private String getJobCacheResource(final UUID jobId) {
        if (jobId != null) {
            return String.format("%s_ID_%s", JOB_CACHE_RESOURCE, jobId);
        } else {
            return JOB_CACHE_RESOURCE;
        }
    }

    /**
     * Returns the NSCS Job Cache event type for the given event type.
     * 
     * @param eventType
     *            the type of the event recorded. Must not be null or empty String.
     * @return the NSCS Job Cache event type.
     */
    private String getJobCacheEventType(final String eventType) {
        return String.format("%s.%s", JOB_CACHE_RESOURCE, eventType);
    }

    /**
     * Returns the NSCS event data type for the given event type.
     * 
     * @param eventType
     *            the type of the event recorded. Must not be null or empty String or contain white space. The event type is considered API, i.e. once
     *            baselined may not be modified spontaneously. Consumers of recordings may code their logic against event types. The event type should
     *            contain a simplified namespace to avoid name clashes, e.g. "NODE_SECURITY.JOB_COMPLETED".
     * @return the NSCS event data type.
     */
    private String getEventDataType(final String eventType) {
        return String.format("%s.%s", NSCS_NS, eventType);
    }

    /**
     * Returns the NSCS Job Cache error ID for the given error type.
     * 
     * @param errorType
     *            the type of the error recorded. Must not be null or empty String.
     * @return the NSCS Job Cache error ID.
     */
    private String getJobCacheErrorId(final String errorType) {
        return String.format("%s.%s", JOB_CACHE_RESOURCE, errorType);
    }

    /**
     * Returns the NSCS Workflow Cache resource for the given workflow wake ID.
     * 
     * @param wfWakeId
     *            the workflow wake ID.
     * @return the NSCS Workflow Cache resource.
     */
    private String getWorkflowCacheResource(final UUID wfWakeId) {
        if (wfWakeId != null) {
            return String.format("%s_ID_%s", WORKFLOW_CACHE_RESOURCE, wfWakeId);
        } else {
            return WORKFLOW_CACHE_RESOURCE;
        }
    }

    /**
     * Returns the NSCS Workflow Cache event type for the given event type.
     * 
     * @param eventType
     *            the type of the event recorded. Must not be null or empty String.
     * @return the NSCS Workflow Cache event type.
     */
    private String getWorkflowCacheEventType(final String eventType) {
        return String.format("%s.%s", WORKFLOW_CACHE_RESOURCE, eventType);
    }

    /**
     * Returns the NSCS Workflow Cache error ID for the given error type.
     * 
     * @param errorType
     *            the type of the error recorded. Must not be null or empty String.
     * @return the NSCS Workflow Cache error ID.
     */
    private String getWorkflowCacheErrorId(final String errorType) {
        return String.format("%s.%s", WORKFLOW_CACHE_RESOURCE, errorType);
    }
}
