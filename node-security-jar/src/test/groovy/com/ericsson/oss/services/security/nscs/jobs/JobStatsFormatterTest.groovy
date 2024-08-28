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
package com.ericsson.oss.services.security.nscs.jobs

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.services.dto.JobStatusRecord
import com.ericsson.oss.services.dto.WfResult
import com.ericsson.oss.services.enums.JobGlobalStatusEnum
import com.ericsson.oss.services.enums.WfStatusEnum

class JobStatsFormatterTest extends CdiSpecification {

    def 'constructor with empty job status record' () {
        given:
        def jobStatusRecord = new JobStatusRecord()
        when:
        def jobStatsFormatter = new JobStatsFormatter(jobStatusRecord)
        and:
        def eventData = jobStatsFormatter.toEventData()
        and:
        def calAdditionalInfo = jobStatsFormatter.toCompactAuditAdditionalInfo(jobStatusRecord.getNumOfInvalid())
        then:
        jobStatsFormatter != null
        and:
        jobStatsFormatter.getJobId() == "N/A"
        and:
        jobStatsFormatter.getUserId() == "N/A"
        and:
        jobStatsFormatter.getCommandId() == "N/A"
        and:
        jobStatsFormatter.getJobInsertDate() == "N/A"
        and:
        jobStatsFormatter.getJobStartDate() == "N/A"
        and:
        jobStatsFormatter.getJobEndDate() == "N/A"
        and:
        jobStatsFormatter.getJobInsertDuration() == "N/A"
        and:
        jobStatsFormatter.getJobStartDuration() == "N/A"
        and:
        jobStatsFormatter.getJobStatus() == "N/A"
        and:
        jobStatsFormatter.getNumWfs() == "0"
        and:
        jobStatsFormatter.getNumNotCompletedWfs() == "0"
        and:
        jobStatsFormatter.getNumInsertedWfs() == "0"
        and:
        jobStatsFormatter.getNumPendingWfs() == "0"
        and:
        jobStatsFormatter.getNumRunningWfs() == "0"
        and:
        jobStatsFormatter.getNumSuccessWfs() == "0"
        and:
        jobStatsFormatter.getNumErrorWfs() == "0"
        and:
        jobStatsFormatter.hasErrors() == false
        and:
        jobStatsFormatter.getMinDurationSuccessWfs() == "N/A"
        and:
        jobStatsFormatter.getMaxDurationSuccessWfs() == "N/A"
        and:
        jobStatsFormatter.getAvgDurationSuccessWfs() == "N/A"
        and:
        eventData.get("JOB_COMMAND_ID") == "N/A"
        eventData.get("JOB_START_DURATION") == 0
        eventData.get("JOB_INSERT_DURATION") == 0
        eventData.get("JOB_NUM_WORKFLOWS") == 0
        eventData.get("JOB_NUM_SUCCESS_WORKFLOWS") == 0
        eventData.get("JOB_NUM_ERROR_WORKFLOWS") == 0
        eventData.get("JOB_MIN_SUCCESS_WORKFLOWS_DURATION") == null
        eventData.get("JOB_MAX_SUCCESS_WORKFLOWS_DURATION") == null
        eventData.get("JOB_AVG_SUCCESS_WORKFLOWS_DURATION") == null
        and:
        calAdditionalInfo.get("valid") == 0
        calAdditionalInfo.get("invalid") == 0
        calAdditionalInfo.get("total") == 0
        calAdditionalInfo.get("success") == 0
        calAdditionalInfo.get("failed") == 0
    }

    def 'constructor with old empty job status record' () {
        given:
        def jobStatusRecord = new JobStatusRecord()
        when:
        def jobStatsFormatter = new JobStatsFormatter(jobStatusRecord)
        and:
        def eventData = jobStatsFormatter.toEventData()
        and:
        def calAdditionalInfo = jobStatsFormatter.toCompactAuditAdditionalInfo(-1)
        then:
        jobStatsFormatter != null
        and:
        jobStatsFormatter.getJobId() == "N/A"
        and:
        jobStatsFormatter.getUserId() == "N/A"
        and:
        jobStatsFormatter.getCommandId() == "N/A"
        and:
        jobStatsFormatter.getJobInsertDate() == "N/A"
        and:
        jobStatsFormatter.getJobStartDate() == "N/A"
        and:
        jobStatsFormatter.getJobEndDate() == "N/A"
        and:
        jobStatsFormatter.getJobInsertDuration() == "N/A"
        and:
        jobStatsFormatter.getJobStartDuration() == "N/A"
        and:
        jobStatsFormatter.getJobStatus() == "N/A"
        and:
        jobStatsFormatter.getNumWfs() == "0"
        and:
        jobStatsFormatter.getNumNotCompletedWfs() == "0"
        and:
        jobStatsFormatter.getNumInsertedWfs() == "0"
        and:
        jobStatsFormatter.getNumPendingWfs() == "0"
        and:
        jobStatsFormatter.getNumRunningWfs() == "0"
        and:
        jobStatsFormatter.getNumSuccessWfs() == "0"
        and:
        jobStatsFormatter.getNumErrorWfs() == "0"
        and:
        jobStatsFormatter.hasErrors() == false
        and:
        jobStatsFormatter.getMinDurationSuccessWfs() == "N/A"
        and:
        jobStatsFormatter.getMaxDurationSuccessWfs() == "N/A"
        and:
        jobStatsFormatter.getAvgDurationSuccessWfs() == "N/A"
        and:
        eventData.get("JOB_COMMAND_ID") == "N/A"
        eventData.get("JOB_START_DURATION") == 0
        eventData.get("JOB_INSERT_DURATION") == 0
        eventData.get("JOB_NUM_WORKFLOWS") == 0
        eventData.get("JOB_NUM_SUCCESS_WORKFLOWS") == 0
        eventData.get("JOB_NUM_ERROR_WORKFLOWS") == 0
        eventData.get("JOB_MIN_SUCCESS_WORKFLOWS_DURATION") == null
        eventData.get("JOB_MAX_SUCCESS_WORKFLOWS_DURATION") == null
        eventData.get("JOB_AVG_SUCCESS_WORKFLOWS_DURATION") == null
        and:
        calAdditionalInfo.get("valid") == 0
        calAdditionalInfo.get("invalid") == "N/A"
        calAdditionalInfo.get("total") == "N/A"
        calAdditionalInfo.get("success") == 0
        calAdditionalInfo.get("failed") == 0
    }

    def 'inserted job' () {
        given:
        def jobId = UUID.fromString("11ed9ac1-49ce-40dc-bab0-3f347092da6c")
        def jobStatusRecord = new JobStatusRecord()
        jobStatusRecord.setJobId(jobId)
        jobStatusRecord.setUserId('user')
        jobStatusRecord.setCommandId('CMD')
        // job inserted 10 seconds ago
        jobStatusRecord.setInsertDate(new Date(System.currentTimeMillis() - 10000))
        jobStatusRecord.setGlobalStatus(JobGlobalStatusEnum.PENDING)
        jobStatusRecord.setNumOfTotWf(0)
        jobStatusRecord.setLastStartedWfId(0)
        jobStatusRecord.setNumOfInvalid(0)
        when:
        def jobStatsFormatter = new JobStatsFormatter(jobStatusRecord)
        and:
        def eventData = jobStatsFormatter.toEventData()
        and:
        def calAdditionalInfo = jobStatsFormatter.toCompactAuditAdditionalInfo(jobStatusRecord.getNumOfInvalid())
        then:
        jobStatsFormatter != null
        and:
        jobStatsFormatter.getJobId() == "11ed9ac1-49ce-40dc-bab0-3f347092da6c"
        and:
        jobStatsFormatter.getUserId() == "user"
        and:
        jobStatsFormatter.getCommandId() == "CMD"
        and:
        jobStatsFormatter.getJobInsertDate() != "N/A"
        and:
        jobStatsFormatter.getJobStartDate() == "N/A"
        and:
        jobStatsFormatter.getJobEndDate() == "N/A"
        and:
        jobStatsFormatter.getJobInsertDuration() == "N/A"
        and:
        jobStatsFormatter.getJobStartDuration() == "N/A"
        and:
        jobStatsFormatter.getJobStatus() == "PENDING"
        and:
        jobStatsFormatter.getNumWfs() == "0"
        and:
        jobStatsFormatter.getNumNotCompletedWfs() == "0"
        and:
        jobStatsFormatter.getNumInsertedWfs() == "0"
        and:
        jobStatsFormatter.getNumPendingWfs() == "0"
        and:
        jobStatsFormatter.getNumRunningWfs() == "0"
        and:
        jobStatsFormatter.getNumSuccessWfs() == "0"
        and:
        jobStatsFormatter.getNumErrorWfs() == "0"
        and:
        jobStatsFormatter.hasErrors() == false
        and:
        jobStatsFormatter.getMinDurationSuccessWfs() == "N/A"
        and:
        jobStatsFormatter.getMaxDurationSuccessWfs() == "N/A"
        and:
        jobStatsFormatter.getAvgDurationSuccessWfs() == "N/A"
        and:
        eventData.get("JOB_COMMAND_ID") == "CMD"
        eventData.get("JOB_START_DURATION") == 0
        eventData.get("JOB_INSERT_DURATION") == 0
        eventData.get("JOB_NUM_WORKFLOWS") == 0
        eventData.get("JOB_NUM_SUCCESS_WORKFLOWS") == 0
        eventData.get("JOB_NUM_ERROR_WORKFLOWS") == 0
        eventData.get("JOB_MIN_SUCCESS_WORKFLOWS_DURATION") == null
        eventData.get("JOB_MAX_SUCCESS_WORKFLOWS_DURATION") == null
        eventData.get("JOB_AVG_SUCCESS_WORKFLOWS_DURATION") == null
        and:
        calAdditionalInfo.get("valid") == 0
        calAdditionalInfo.get("invalid") == 0
        calAdditionalInfo.get("total") == 0
        calAdditionalInfo.get("success") == 0
        calAdditionalInfo.get("failed") == 0
    }

    def 'inserted old job' () {
        given:
        def jobId = UUID.fromString("11ed9ac1-49ce-40dc-bab0-3f347092da6c")
        def jobStatusRecord = new JobStatusRecord()
        jobStatusRecord.setJobId(jobId)
        jobStatusRecord.setUserId('user')
        jobStatusRecord.setCommandId('CMD')
        // job inserted 10 seconds ago
        jobStatusRecord.setInsertDate(new Date(System.currentTimeMillis() - 10000))
        jobStatusRecord.setGlobalStatus(JobGlobalStatusEnum.PENDING)
        jobStatusRecord.setNumOfTotWf(0)
        jobStatusRecord.setLastStartedWfId(0)
        when:
        def jobStatsFormatter = new JobStatsFormatter(jobStatusRecord)
        and:
        def eventData = jobStatsFormatter.toEventData()
        and:
        def calAdditionalInfo = jobStatsFormatter.toCompactAuditAdditionalInfo(-1)
        then:
        jobStatsFormatter != null
        and:
        jobStatsFormatter.getJobId() == "11ed9ac1-49ce-40dc-bab0-3f347092da6c"
        and:
        jobStatsFormatter.getUserId() == "user"
        and:
        jobStatsFormatter.getCommandId() == "CMD"
        and:
        jobStatsFormatter.getJobInsertDate() != "N/A"
        and:
        jobStatsFormatter.getJobStartDate() == "N/A"
        and:
        jobStatsFormatter.getJobEndDate() == "N/A"
        and:
        jobStatsFormatter.getJobInsertDuration() == "N/A"
        and:
        jobStatsFormatter.getJobStartDuration() == "N/A"
        and:
        jobStatsFormatter.getJobStatus() == "PENDING"
        and:
        jobStatsFormatter.getNumWfs() == "0"
        and:
        jobStatsFormatter.getNumNotCompletedWfs() == "0"
        and:
        jobStatsFormatter.getNumInsertedWfs() == "0"
        and:
        jobStatsFormatter.getNumPendingWfs() == "0"
        and:
        jobStatsFormatter.getNumRunningWfs() == "0"
        and:
        jobStatsFormatter.getNumSuccessWfs() == "0"
        and:
        jobStatsFormatter.getNumErrorWfs() == "0"
        and:
        jobStatsFormatter.hasErrors() == false
        and:
        jobStatsFormatter.getMinDurationSuccessWfs() == "N/A"
        and:
        jobStatsFormatter.getMaxDurationSuccessWfs() == "N/A"
        and:
        jobStatsFormatter.getAvgDurationSuccessWfs() == "N/A"
        and:
        eventData.get("JOB_COMMAND_ID") == "CMD"
        eventData.get("JOB_START_DURATION") == 0
        eventData.get("JOB_INSERT_DURATION") == 0
        eventData.get("JOB_NUM_WORKFLOWS") == 0
        eventData.get("JOB_NUM_SUCCESS_WORKFLOWS") == 0
        eventData.get("JOB_NUM_ERROR_WORKFLOWS") == 0
        eventData.get("JOB_MIN_SUCCESS_WORKFLOWS_DURATION") == null
        eventData.get("JOB_MAX_SUCCESS_WORKFLOWS_DURATION") == null
        eventData.get("JOB_AVG_SUCCESS_WORKFLOWS_DURATION") == null
        and:
        calAdditionalInfo.get("valid") == 0
        calAdditionalInfo.get("invalid") == "N/A"
        calAdditionalInfo.get("total") == "N/A"
        calAdditionalInfo.get("success") == 0
        calAdditionalInfo.get("failed") == 0
    }

    def 'updated before one workflow insertion job' () {
        given:
        def jobId = UUID.fromString("11ed9ac1-49ce-40dc-bab0-3f347092da6c")
        def jobStatusRecord = new JobStatusRecord()
        jobStatusRecord.setJobId(jobId)
        jobStatusRecord.setUserId('user')
        jobStatusRecord.setCommandId('CMD')
        // job inserted 10 seconds ago
        jobStatusRecord.setInsertDate(new Date(System.currentTimeMillis() - 10000))
        jobStatusRecord.setGlobalStatus(JobGlobalStatusEnum.PENDING)
        jobStatusRecord.setNumOfTotWf(1)
        jobStatusRecord.setLastStartedWfId(1)
        jobStatusRecord.setNumOfInvalid(1)
        when:
        def jobStatsFormatter = new JobStatsFormatter(jobStatusRecord)
        and:
        def eventData = jobStatsFormatter.toEventData()
        and:
        def calAdditionalInfo = jobStatsFormatter.toCompactAuditAdditionalInfo(1)
        then:
        jobStatsFormatter != null
        and:
        jobStatsFormatter.getJobId() == "11ed9ac1-49ce-40dc-bab0-3f347092da6c"
        and:
        jobStatsFormatter.getUserId() == "user"
        and:
        jobStatsFormatter.getCommandId() == "CMD"
        and:
        jobStatsFormatter.getJobInsertDate() != "N/A"
        and:
        jobStatsFormatter.getJobStartDate() == "N/A"
        and:
        jobStatsFormatter.getJobEndDate() == "N/A"
        and:
        jobStatsFormatter.getJobInsertDuration() == "N/A"
        and:
        jobStatsFormatter.getJobStartDuration() == "N/A"
        and:
        jobStatsFormatter.getJobStatus() == "PENDING"
        and:
        jobStatsFormatter.getNumWfs() == "1"
        and:
        jobStatsFormatter.getNumNotCompletedWfs() == "1"
        and:
        jobStatsFormatter.getNumInsertedWfs() == "0"
        and:
        jobStatsFormatter.getNumPendingWfs() == "0"
        and:
        jobStatsFormatter.getNumRunningWfs() == "0"
        and:
        jobStatsFormatter.getNumSuccessWfs() == "0"
        and:
        jobStatsFormatter.getNumErrorWfs() == "0"
        and:
        jobStatsFormatter.hasErrors() == false
        and:
        jobStatsFormatter.getMinDurationSuccessWfs() == "N/A"
        and:
        jobStatsFormatter.getMaxDurationSuccessWfs() == "N/A"
        and:
        jobStatsFormatter.getAvgDurationSuccessWfs() == "N/A"
        and:
        eventData.get("JOB_COMMAND_ID") == "CMD"
        eventData.get("JOB_START_DURATION") == 0
        eventData.get("JOB_INSERT_DURATION") == 0
        eventData.get("JOB_NUM_WORKFLOWS") == 1
        eventData.get("JOB_NUM_SUCCESS_WORKFLOWS") == 0
        eventData.get("JOB_NUM_ERROR_WORKFLOWS") == 0
        eventData.get("JOB_MIN_SUCCESS_WORKFLOWS_DURATION") == null
        eventData.get("JOB_MAX_SUCCESS_WORKFLOWS_DURATION") == null
        eventData.get("JOB_AVG_SUCCESS_WORKFLOWS_DURATION") == null
        and:
        calAdditionalInfo.get("valid") == 1
        calAdditionalInfo.get("invalid") == 1
        calAdditionalInfo.get("total") == 2
        calAdditionalInfo.get("success") == 0
        calAdditionalInfo.get("failed") == 0
    }

    def 'updated after one workflow insertion job' () {
        given:
        def jobId = UUID.fromString("11ed9ac1-49ce-40dc-bab0-3f347092da6c")
        def jobStatusRecord = new JobStatusRecord()
        jobStatusRecord.setJobId(jobId)
        jobStatusRecord.setUserId('user')
        jobStatusRecord.setCommandId('CMD')
        // job inserted 10 seconds ago
        jobStatusRecord.setInsertDate(new Date(System.currentTimeMillis() - 10000))
        jobStatusRecord.setGlobalStatus(JobGlobalStatusEnum.PENDING)
        jobStatusRecord.setNumOfTotWf(1)
        jobStatusRecord.setLastStartedWfId(1)
        jobStatusRecord.setNumOfInvalid(0)
        and:
        def String wfResultUUIDString = jobId.toString() + "1"
        def UUID wfWakeId = UUID.nameUUIDFromBytes(wfResultUUIDString.getBytes())
        def wfParams = ['PARAM1': 'VALUE1', 'PARAM2': 'VALUE2']
        def WfResult result = new WfResult()
        result.setNodeName('NODE')
        result.setStatus(WfStatusEnum.PENDING)
        result.setWfWakeId(wfWakeId)
        result.setWfParams(wfParams)
        result.setJobId(jobId)
        when:
        def jobStatsFormatter = new JobStatsFormatter(jobStatusRecord, [result])
        and:
        def eventData = jobStatsFormatter.toEventData()
        and:
        def calAdditionalInfo = jobStatsFormatter.toCompactAuditAdditionalInfo(0)
        then:
        jobStatsFormatter != null
        and:
        jobStatsFormatter.getJobId() == "11ed9ac1-49ce-40dc-bab0-3f347092da6c"
        and:
        jobStatsFormatter.getUserId() == "user"
        and:
        jobStatsFormatter.getCommandId() == "CMD"
        and:
        jobStatsFormatter.getJobInsertDate() != "N/A"
        and:
        jobStatsFormatter.getJobStartDate() == "N/A"
        and:
        jobStatsFormatter.getJobEndDate() == "N/A"
        and:
        jobStatsFormatter.getJobInsertDuration() == "N/A"
        and:
        jobStatsFormatter.getJobStartDuration() == "N/A"
        and:
        jobStatsFormatter.getJobStatus() == "PENDING"
        and:
        jobStatsFormatter.getNumWfs() == "1"
        and:
        jobStatsFormatter.getNumNotCompletedWfs() == "1"
        and:
        jobStatsFormatter.getNumInsertedWfs() == "1"
        and:
        jobStatsFormatter.getNumPendingWfs() == "1"
        and:
        jobStatsFormatter.getNumRunningWfs() == "0"
        and:
        jobStatsFormatter.getNumSuccessWfs() == "0"
        and:
        jobStatsFormatter.getNumErrorWfs() == "0"
        and:
        jobStatsFormatter.hasErrors() == false
        and:
        jobStatsFormatter.getMinDurationSuccessWfs() == "N/A"
        and:
        jobStatsFormatter.getMaxDurationSuccessWfs() == "N/A"
        and:
        jobStatsFormatter.getAvgDurationSuccessWfs() == "N/A"
        and:
        eventData.get("JOB_COMMAND_ID") == "CMD"
        eventData.get("JOB_START_DURATION") == 0
        eventData.get("JOB_INSERT_DURATION") == 0
        eventData.get("JOB_NUM_WORKFLOWS") == 1
        eventData.get("JOB_NUM_SUCCESS_WORKFLOWS") == 0
        eventData.get("JOB_NUM_ERROR_WORKFLOWS") == 0
        eventData.get("JOB_MIN_SUCCESS_WORKFLOWS_DURATION") == null
        eventData.get("JOB_MAX_SUCCESS_WORKFLOWS_DURATION") == null
        eventData.get("JOB_AVG_SUCCESS_WORKFLOWS_DURATION") == null
        and:
        calAdditionalInfo.get("valid") == 1
        calAdditionalInfo.get("invalid") == 0
        calAdditionalInfo.get("total") == 1
        calAdditionalInfo.get("success") == 0
        calAdditionalInfo.get("failed") == 0
    }

    def 'started job with one workflow' () {
        given:
        def jobId = UUID.fromString("11ed9ac1-49ce-40dc-bab0-3f347092da6c")
        def jobStatusRecord = new JobStatusRecord()
        jobStatusRecord.setJobId(jobId)
        jobStatusRecord.setUserId('user')
        jobStatusRecord.setCommandId('CMD')
        // job inserted 10 seconds ago
        jobStatusRecord.setInsertDate(new Date(System.currentTimeMillis() - 10000))
        jobStatusRecord.setGlobalStatus(JobGlobalStatusEnum.RUNNING)
        // job started 5 seconds ago
        jobStatusRecord.setStartDate(new Date(System.currentTimeMillis() - 5000))
        jobStatusRecord.setNumOfTotWf(1)
        jobStatusRecord.setLastStartedWfId(1)
        jobStatusRecord.setNumOfInvalid(2)
        and:
        def String wfResultUUIDString = jobId.toString() + "1"
        def UUID wfWakeId = UUID.nameUUIDFromBytes(wfResultUUIDString.getBytes())
        def wfParams = ['PARAM1': 'VALUE1', 'PARAM2': 'VALUE2']
        def WfResult result = new WfResult()
        result.setWfId('1234')
        result.setNodeName('NODE')
        result.setStatus(WfStatusEnum.RUNNING)
        // workflow started 5 seconds ago
        result.setStartDate(new Date(System.currentTimeMillis() - 5000))
        result.setWfWakeId(wfWakeId)
        result.setWfParams(wfParams)
        result.setJobId(jobId)
        when:
        def jobStatsFormatter = new JobStatsFormatter(jobStatusRecord, [result])
        and:
        def eventData = jobStatsFormatter.toEventData()
        and:
        def calAdditionalInfo = jobStatsFormatter.toCompactAuditAdditionalInfo(2)
        then:
        jobStatsFormatter != null
        and:
        jobStatsFormatter.getJobId() == "11ed9ac1-49ce-40dc-bab0-3f347092da6c"
        and:
        jobStatsFormatter.getUserId() == "user"
        and:
        jobStatsFormatter.getCommandId() == "CMD"
        and:
        jobStatsFormatter.getJobInsertDate() != "N/A"
        and:
        jobStatsFormatter.getJobStartDate() != "N/A"
        and:
        jobStatsFormatter.getJobEndDate() == "N/A"
        and:
        jobStatsFormatter.getJobInsertDuration() == "N/A"
        and:
        jobStatsFormatter.getJobStartDuration() == "N/A"
        and:
        jobStatsFormatter.getJobStatus() == "RUNNING"
        and:
        jobStatsFormatter.getNumWfs() == "1"
        and:
        jobStatsFormatter.getNumNotCompletedWfs() == "1"
        and:
        jobStatsFormatter.getNumInsertedWfs() == "1"
        and:
        jobStatsFormatter.getNumPendingWfs() == "0"
        and:
        jobStatsFormatter.getNumRunningWfs() == "1"
        and:
        jobStatsFormatter.getNumSuccessWfs() == "0"
        and:
        jobStatsFormatter.getNumErrorWfs() == "0"
        and:
        jobStatsFormatter.hasErrors() == false
        and:
        jobStatsFormatter.getMinDurationSuccessWfs() == "N/A"
        and:
        jobStatsFormatter.getMaxDurationSuccessWfs() == "N/A"
        and:
        jobStatsFormatter.getAvgDurationSuccessWfs() == "N/A"
        and:
        eventData.get("JOB_COMMAND_ID") == "CMD"
        eventData.get("JOB_START_DURATION") == 0
        eventData.get("JOB_INSERT_DURATION") == 0
        eventData.get("JOB_NUM_WORKFLOWS") == 1
        eventData.get("JOB_NUM_SUCCESS_WORKFLOWS") == 0
        eventData.get("JOB_NUM_ERROR_WORKFLOWS") == 0
        eventData.get("JOB_MIN_SUCCESS_WORKFLOWS_DURATION") == null
        eventData.get("JOB_MAX_SUCCESS_WORKFLOWS_DURATION") == null
        eventData.get("JOB_AVG_SUCCESS_WORKFLOWS_DURATION") == null
        and:
        calAdditionalInfo.get("valid") == 1
        calAdditionalInfo.get("invalid") == 2
        calAdditionalInfo.get("total") == 3
        calAdditionalInfo.get("success") == 0
        calAdditionalInfo.get("failed") == 0
    }

    def 'completed job with one workflow completed with success' () {
        given:
        def jobStatusRecord = new JobStatusRecord()
        def jobId = UUID.fromString("11ed9ac1-49ce-40dc-bab0-3f347092da6c")
        jobStatusRecord.setJobId(jobId)
        jobStatusRecord.setUserId('user')
        jobStatusRecord.setCommandId('CMD')
        // inserted 10 seconds ago
        jobStatusRecord.setInsertDate(new Date(System.currentTimeMillis() - 10000))
        jobStatusRecord.setNumOfTotWf(1)
        jobStatusRecord.setLastStartedWfId(0)
        jobStatusRecord.setGlobalStatus(JobGlobalStatusEnum.RUNNING)
        // job started 5 seconds ago
        jobStatusRecord.setStartDate(new Date(System.currentTimeMillis() - 5000))
        jobStatusRecord.setNumOfInvalid(3)
        and:
        def String wfResultUUIDString = jobId.toString() + "1"
        def UUID wfWakeId = UUID.nameUUIDFromBytes(wfResultUUIDString.getBytes())
        def wfParams = ['PARAM1': 'VALUE1', 'PARAM2': 'VALUE2']
        def WfResult result = new WfResult()
        result.setStatus(WfStatusEnum.RUNNING)
        // workflow started 5 seconds ago
        result.setStartDate(new Date(System.currentTimeMillis() - 5000))
        and:
        result.setWfId('1234')
        result.setNodeName('NODE')
        result.setWfWakeId(wfWakeId)
        result.setWfParams(wfParams)
        result.setJobId(jobId)
        and:
        result.setStatus(WfStatusEnum.SUCCESS)
        jobStatusRecord.setGlobalStatus(JobGlobalStatusEnum.COMPLETED)
        // job ended now
        jobStatusRecord.setEndDate(new Date(System.currentTimeMillis()))
        when:
        def jobStatsFormatter = new JobStatsFormatter(jobStatusRecord, [result])
        and:
        def eventData = jobStatsFormatter.toEventData()
        and:
        def calAdditionalInfo = jobStatsFormatter.toCompactAuditAdditionalInfo(3)
        then:
        jobStatsFormatter != null
        and:
        jobStatsFormatter.getJobId() == "11ed9ac1-49ce-40dc-bab0-3f347092da6c"
        and:
        jobStatsFormatter.getUserId() == "user"
        and:
        jobStatsFormatter.getCommandId() == "CMD"
        and:
        jobStatsFormatter.getJobInsertDate() != "N/A"
        and:
        jobStatsFormatter.getJobStartDate() != "N/A"
        and:
        jobStatsFormatter.getJobEndDate() != "N/A"
        and:
        jobStatsFormatter.getJobInsertDuration() != "N/A"
        and:
        jobStatsFormatter.getJobStartDuration() != "N/A"
        and:
        jobStatsFormatter.getJobStatus() == "COMPLETED"
        and:
        jobStatsFormatter.getNumWfs() == "1"
        and:
        jobStatsFormatter.getNumNotCompletedWfs() == "0"
        and:
        jobStatsFormatter.getNumInsertedWfs() == "1"
        and:
        jobStatsFormatter.getNumPendingWfs() == "0"
        and:
        jobStatsFormatter.getNumRunningWfs() == "0"
        and:
        jobStatsFormatter.getNumSuccessWfs() == "1"
        and:
        jobStatsFormatter.getNumErrorWfs() == "0"
        and:
        jobStatsFormatter.hasErrors() == false
        and:
        jobStatsFormatter.getMinDurationSuccessWfs() != "N/A"
        and:
        jobStatsFormatter.getMaxDurationSuccessWfs() != "N/A"
        and:
        jobStatsFormatter.getAvgDurationSuccessWfs() != "N/A"
        and:
        eventData.get("JOB_COMMAND_ID") == "CMD"
        eventData.get("JOB_START_DURATION") != 0
        eventData.get("JOB_INSERT_DURATION") != 0
        eventData.get("JOB_NUM_WORKFLOWS") == 1
        eventData.get("JOB_NUM_SUCCESS_WORKFLOWS") == 1
        eventData.get("JOB_NUM_ERROR_WORKFLOWS") == 0
        eventData.get("JOB_MIN_SUCCESS_WORKFLOWS_DURATION") != null
        eventData.get("JOB_MAX_SUCCESS_WORKFLOWS_DURATION") != null
        eventData.get("JOB_AVG_SUCCESS_WORKFLOWS_DURATION") != null
        and:
        calAdditionalInfo.get("valid") == 1
        calAdditionalInfo.get("invalid") == 3
        calAdditionalInfo.get("total") == 4
        calAdditionalInfo.get("success") == 1
        calAdditionalInfo.get("failed") == 0
    }

    def 'completed old job with one workflow completed with success' () {
        given:
        def jobStatusRecord = new JobStatusRecord()
        def jobId = UUID.fromString("11ed9ac1-49ce-40dc-bab0-3f347092da6c")
        jobStatusRecord.setJobId(jobId)
        jobStatusRecord.setUserId('user')
        jobStatusRecord.setCommandId('CMD')
        // inserted 10 seconds ago
        jobStatusRecord.setInsertDate(new Date(System.currentTimeMillis() - 10000))
        jobStatusRecord.setNumOfTotWf(1)
        jobStatusRecord.setLastStartedWfId(0)
        jobStatusRecord.setGlobalStatus(JobGlobalStatusEnum.RUNNING)
        // job started 5 seconds ago
        jobStatusRecord.setStartDate(new Date(System.currentTimeMillis() - 5000))
        and:
        def String wfResultUUIDString = jobId.toString() + "1"
        def UUID wfWakeId = UUID.nameUUIDFromBytes(wfResultUUIDString.getBytes())
        def wfParams = ['PARAM1': 'VALUE1', 'PARAM2': 'VALUE2']
        def WfResult result = new WfResult()
        result.setStatus(WfStatusEnum.RUNNING)
        // workflow started 5 seconds ago
        result.setStartDate(new Date(System.currentTimeMillis() - 5000))
        and:
        result.setWfId('1234')
        result.setNodeName('NODE')
        result.setWfWakeId(wfWakeId)
        result.setWfParams(wfParams)
        result.setJobId(jobId)
        and:
        result.setStatus(WfStatusEnum.SUCCESS)
        jobStatusRecord.setGlobalStatus(JobGlobalStatusEnum.COMPLETED)
        // job ended now
        jobStatusRecord.setEndDate(new Date(System.currentTimeMillis()))
        when:
        def jobStatsFormatter = new JobStatsFormatter(jobStatusRecord, [result])
        and:
        def eventData = jobStatsFormatter.toEventData()
        and:
        def calAdditionalInfo = jobStatsFormatter.toCompactAuditAdditionalInfo(-1)
        then:
        jobStatsFormatter != null
        and:
        jobStatsFormatter.getJobId() == "11ed9ac1-49ce-40dc-bab0-3f347092da6c"
        and:
        jobStatsFormatter.getUserId() == "user"
        and:
        jobStatsFormatter.getCommandId() == "CMD"
        and:
        jobStatsFormatter.getJobInsertDate() != "N/A"
        and:
        jobStatsFormatter.getJobStartDate() != "N/A"
        and:
        jobStatsFormatter.getJobEndDate() != "N/A"
        and:
        jobStatsFormatter.getJobInsertDuration() != "N/A"
        and:
        jobStatsFormatter.getJobStartDuration() != "N/A"
        and:
        jobStatsFormatter.getJobStatus() == "COMPLETED"
        and:
        jobStatsFormatter.getNumWfs() == "1"
        and:
        jobStatsFormatter.getNumNotCompletedWfs() == "0"
        and:
        jobStatsFormatter.getNumInsertedWfs() == "1"
        and:
        jobStatsFormatter.getNumPendingWfs() == "0"
        and:
        jobStatsFormatter.getNumRunningWfs() == "0"
        and:
        jobStatsFormatter.getNumSuccessWfs() == "1"
        and:
        jobStatsFormatter.getNumErrorWfs() == "0"
        and:
        jobStatsFormatter.hasErrors() == false
        and:
        jobStatsFormatter.getMinDurationSuccessWfs() != "N/A"
        and:
        jobStatsFormatter.getMaxDurationSuccessWfs() != "N/A"
        and:
        jobStatsFormatter.getAvgDurationSuccessWfs() != "N/A"
        and:
        eventData.get("JOB_COMMAND_ID") == "CMD"
        eventData.get("JOB_START_DURATION") != 0
        eventData.get("JOB_INSERT_DURATION") != 0
        eventData.get("JOB_NUM_WORKFLOWS") == 1
        eventData.get("JOB_NUM_SUCCESS_WORKFLOWS") == 1
        eventData.get("JOB_NUM_ERROR_WORKFLOWS") == 0
        eventData.get("JOB_MIN_SUCCESS_WORKFLOWS_DURATION") != null
        eventData.get("JOB_MAX_SUCCESS_WORKFLOWS_DURATION") != null
        eventData.get("JOB_AVG_SUCCESS_WORKFLOWS_DURATION") != null
        and:
        calAdditionalInfo.get("valid") == 1
        calAdditionalInfo.get("invalid") == "N/A"
        calAdditionalInfo.get("total") == "N/A"
        calAdditionalInfo.get("success") == 1
        calAdditionalInfo.get("failed") == 0
    }

    def 'completed job with one workflow completed with error' () {
        given:
        def jobStatusRecord = new JobStatusRecord()
        def jobId = UUID.fromString("11ed9ac1-49ce-40dc-bab0-3f347092da6c")
        jobStatusRecord.setJobId(jobId)
        jobStatusRecord.setUserId('user')
        jobStatusRecord.setCommandId('CMD')
        // job inserted 10 seconds ago
        jobStatusRecord.setInsertDate(new Date(System.currentTimeMillis() - 10000))
        jobStatusRecord.setNumOfTotWf(1)
        jobStatusRecord.setLastStartedWfId(0)
        jobStatusRecord.setGlobalStatus(JobGlobalStatusEnum.RUNNING)
        // job started 5 seconds ago
        jobStatusRecord.setStartDate(new Date(System.currentTimeMillis() - 5000))
        jobStatusRecord.setNumOfInvalid(4)
        and:
        def String wfResultUUIDString = jobId.toString() + "1"
        def UUID wfWakeId = UUID.nameUUIDFromBytes(wfResultUUIDString.getBytes())
        def wfParams = ['PARAM1': 'VALUE1', 'PARAM2': 'VALUE2']
        def WfResult result = new WfResult()
        // to set workflow startDate
        result.setStatus(WfStatusEnum.RUNNING)
        // workflow started 5 seconds ago
        result.setStartDate(new Date(System.currentTimeMillis() - 5000))
        and:
        result.setWfId('1234')
        result.setNodeName('NODE')
        result.setWfWakeId(wfWakeId)
        result.setWfParams(wfParams)
        result.setJobId(jobId)
        result.setStatus(WfStatusEnum.ERROR)
        jobStatusRecord.setGlobalStatus(JobGlobalStatusEnum.COMPLETED)
        // job ended now
        jobStatusRecord.setEndDate(new Date(System.currentTimeMillis()))
        when:
        def jobStatsFormatter = new JobStatsFormatter(jobStatusRecord, [result])
        and:
        def eventData = jobStatsFormatter.toEventData()
        and:
        def calAdditionalInfo = jobStatsFormatter.toCompactAuditAdditionalInfo(4)
        then:
        jobStatsFormatter != null
        and:
        jobStatsFormatter.getJobId() == "11ed9ac1-49ce-40dc-bab0-3f347092da6c"
        and:
        jobStatsFormatter.getUserId() == "user"
        and:
        jobStatsFormatter.getCommandId() == "CMD"
        and:
        jobStatsFormatter.getJobInsertDate() != "N/A"
        and:
        jobStatsFormatter.getJobStartDate() != "N/A"
        and:
        jobStatsFormatter.getJobEndDate() != "N/A"
        and:
        jobStatsFormatter.getJobInsertDuration() != "N/A"
        and:
        jobStatsFormatter.getJobStartDuration() != "N/A"
        and:
        jobStatsFormatter.getJobStatus() == "COMPLETED"
        and:
        jobStatsFormatter.getNumWfs() == "1"
        and:
        jobStatsFormatter.getNumNotCompletedWfs() == "0"
        and:
        jobStatsFormatter.getNumInsertedWfs() == "1"
        and:
        jobStatsFormatter.getNumPendingWfs() == "0"
        and:
        jobStatsFormatter.getNumRunningWfs() == "0"
        and:
        jobStatsFormatter.getNumSuccessWfs() == "0"
        and:
        jobStatsFormatter.getNumErrorWfs() == "1"
        and:
        jobStatsFormatter.hasErrors() == true
        and:
        jobStatsFormatter.getMinDurationSuccessWfs() == "N/A"
        and:
        jobStatsFormatter.getMaxDurationSuccessWfs() == "N/A"
        and:
        jobStatsFormatter.getAvgDurationSuccessWfs() == "N/A"
        and:
        eventData.get("JOB_COMMAND_ID") == "CMD"
        eventData.get("JOB_START_DURATION") != 0
        eventData.get("JOB_INSERT_DURATION") != 0
        eventData.get("JOB_NUM_WORKFLOWS") == 1
        eventData.get("JOB_NUM_SUCCESS_WORKFLOWS") == 0
        eventData.get("JOB_NUM_ERROR_WORKFLOWS") == 1
        eventData.get("JOB_MIN_SUCCESS_WORKFLOWS_DURATION") == null
        eventData.get("JOB_MAX_SUCCESS_WORKFLOWS_DURATION") == null
        eventData.get("JOB_AVG_SUCCESS_WORKFLOWS_DURATION") == null
        and:
        calAdditionalInfo.get("valid") == 1
        calAdditionalInfo.get("invalid") == 4
        calAdditionalInfo.get("total") == 5
        calAdditionalInfo.get("success") == 0
        calAdditionalInfo.get("failed") == 1
    }

    def 'completed old job with one workflow completed with error' () {
        given:
        def jobStatusRecord = new JobStatusRecord()
        def jobId = UUID.fromString("11ed9ac1-49ce-40dc-bab0-3f347092da6c")
        jobStatusRecord.setJobId(jobId)
        jobStatusRecord.setUserId('user')
        jobStatusRecord.setCommandId('CMD')
        // job inserted 10 seconds ago
        jobStatusRecord.setInsertDate(new Date(System.currentTimeMillis() - 10000))
        jobStatusRecord.setNumOfTotWf(1)
        jobStatusRecord.setLastStartedWfId(0)
        jobStatusRecord.setGlobalStatus(JobGlobalStatusEnum.RUNNING)
        // job started 5 seconds ago
        jobStatusRecord.setStartDate(new Date(System.currentTimeMillis() - 5000))
        and:
        def String wfResultUUIDString = jobId.toString() + "1"
        def UUID wfWakeId = UUID.nameUUIDFromBytes(wfResultUUIDString.getBytes())
        def wfParams = ['PARAM1': 'VALUE1', 'PARAM2': 'VALUE2']
        def WfResult result = new WfResult()
        // to set workflow startDate
        result.setStatus(WfStatusEnum.RUNNING)
        // workflow started 5 seconds ago
        result.setStartDate(new Date(System.currentTimeMillis() - 5000))
        and:
        result.setWfId('1234')
        result.setNodeName('NODE')
        result.setWfWakeId(wfWakeId)
        result.setWfParams(wfParams)
        result.setJobId(jobId)
        result.setStatus(WfStatusEnum.ERROR)
        jobStatusRecord.setGlobalStatus(JobGlobalStatusEnum.COMPLETED)
        // job ended now
        jobStatusRecord.setEndDate(new Date(System.currentTimeMillis()))
        when:
        def jobStatsFormatter = new JobStatsFormatter(jobStatusRecord, [result])
        and:
        def eventData = jobStatsFormatter.toEventData()
        and:
        def calAdditionalInfo = jobStatsFormatter.toCompactAuditAdditionalInfo(-1)
        then:
        jobStatsFormatter != null
        and:
        jobStatsFormatter.getJobId() == "11ed9ac1-49ce-40dc-bab0-3f347092da6c"
        and:
        jobStatsFormatter.getUserId() == "user"
        and:
        jobStatsFormatter.getCommandId() == "CMD"
        and:
        jobStatsFormatter.getJobInsertDate() != "N/A"
        and:
        jobStatsFormatter.getJobStartDate() != "N/A"
        and:
        jobStatsFormatter.getJobEndDate() != "N/A"
        and:
        jobStatsFormatter.getJobInsertDuration() != "N/A"
        and:
        jobStatsFormatter.getJobStartDuration() != "N/A"
        and:
        jobStatsFormatter.getJobStatus() == "COMPLETED"
        and:
        jobStatsFormatter.getNumWfs() == "1"
        and:
        jobStatsFormatter.getNumNotCompletedWfs() == "0"
        and:
        jobStatsFormatter.getNumInsertedWfs() == "1"
        and:
        jobStatsFormatter.getNumPendingWfs() == "0"
        and:
        jobStatsFormatter.getNumRunningWfs() == "0"
        and:
        jobStatsFormatter.getNumSuccessWfs() == "0"
        and:
        jobStatsFormatter.getNumErrorWfs() == "1"
        and:
        jobStatsFormatter.hasErrors() == true
        and:
        jobStatsFormatter.getMinDurationSuccessWfs() == "N/A"
        and:
        jobStatsFormatter.getMaxDurationSuccessWfs() == "N/A"
        and:
        jobStatsFormatter.getAvgDurationSuccessWfs() == "N/A"
        and:
        eventData.get("JOB_COMMAND_ID") == "CMD"
        eventData.get("JOB_START_DURATION") != 0
        eventData.get("JOB_INSERT_DURATION") != 0
        eventData.get("JOB_NUM_WORKFLOWS") == 1
        eventData.get("JOB_NUM_SUCCESS_WORKFLOWS") == 0
        eventData.get("JOB_NUM_ERROR_WORKFLOWS") == 1
        eventData.get("JOB_MIN_SUCCESS_WORKFLOWS_DURATION") == null
        eventData.get("JOB_MAX_SUCCESS_WORKFLOWS_DURATION") == null
        eventData.get("JOB_AVG_SUCCESS_WORKFLOWS_DURATION") == null
        and:
        calAdditionalInfo.get("valid") == 1
        calAdditionalInfo.get("invalid") == "N/A"
        calAdditionalInfo.get("total") == "N/A"
        calAdditionalInfo.get("success") == 0
        calAdditionalInfo.get("failed") == 1
    }

    def 'job with one pending workflow'() {
        given:
        def jobId = UUID.fromString("11ed9ac1-49ce-40dc-bab0-3f347092da6c")
        def String wfResultUUIDString = jobId.toString() + "1"
        def UUID wfWakeId = UUID.nameUUIDFromBytes(wfResultUUIDString.getBytes())
        and:
        def jobStatusRecord = new JobStatusRecord()
        jobStatusRecord.setJobId(jobId)
        and:
        def WfResult result = new WfResult()
        result.setJobId(jobId)
        result.setWfWakeId(wfWakeId)
        result.setStatus(WfStatusEnum.PENDING)
        when:
        def jobStatsFormatter = new JobStatsFormatter(jobStatusRecord, [result])
        and:
        def eventData = jobStatsFormatter.toEventData()
        then:
        jobStatsFormatter != null
        and:
        jobStatsFormatter.getJobId() == "11ed9ac1-49ce-40dc-bab0-3f347092da6c"
        and:
        jobStatsFormatter.getUserId() == "N/A"
        and:
        jobStatsFormatter.getCommandId() == "N/A"
        and:
        jobStatsFormatter.getJobInsertDate() == "N/A"
        and:
        jobStatsFormatter.getJobStartDate() == "N/A"
        and:
        jobStatsFormatter.getJobEndDate() == "N/A"
        and:
        jobStatsFormatter.getJobInsertDuration() == "N/A"
        and:
        jobStatsFormatter.getJobStartDuration() == "N/A"
        and:
        jobStatsFormatter.getJobStatus() == "N/A"
        and:
        jobStatsFormatter.getNumWfs() == "0"
        and:
        jobStatsFormatter.getNumNotCompletedWfs() == "0"
        and:
        jobStatsFormatter.getNumInsertedWfs() == "1"
        and:
        jobStatsFormatter.getNumPendingWfs() == "1"
        and:
        jobStatsFormatter.getNumRunningWfs() == "0"
        and:
        jobStatsFormatter.getNumSuccessWfs() == "0"
        and:
        jobStatsFormatter.getNumErrorWfs() == "0"
        and:
        jobStatsFormatter.hasErrors() == false
        and:
        jobStatsFormatter.getMinDurationSuccessWfs() == "N/A"
        and:
        jobStatsFormatter.getMaxDurationSuccessWfs() == "N/A"
        and:
        jobStatsFormatter.getAvgDurationSuccessWfs() == "N/A"
        and:
        eventData.get("JOB_COMMAND_ID") == "N/A"
        eventData.get("JOB_START_DURATION") == 0
        eventData.get("JOB_INSERT_DURATION") == 0
        eventData.get("JOB_NUM_WORKFLOWS") == 0
        eventData.get("JOB_NUM_SUCCESS_WORKFLOWS") == 0
        eventData.get("JOB_NUM_ERROR_WORKFLOWS") == 0
        eventData.get("JOB_MIN_SUCCESS_WORKFLOWS_DURATION") == null
        eventData.get("JOB_MAX_SUCCESS_WORKFLOWS_DURATION") == null
        eventData.get("JOB_AVG_SUCCESS_WORKFLOWS_DURATION") == null
    }

    def 'job with one running workflow'() {
        given:
        def jobId = UUID.fromString("11ed9ac1-49ce-40dc-bab0-3f347092da6c")
        def String wfResultUUIDString = jobId.toString() + "1"
        def UUID wfWakeId = UUID.nameUUIDFromBytes(wfResultUUIDString.getBytes())
        and:
        def jobStatusRecord = new JobStatusRecord()
        jobStatusRecord.setJobId(jobId)
        and:
        def WfResult result = new WfResult()
        result.setJobId(jobId)
        result.setWfWakeId(wfWakeId)
        result.setStatus(WfStatusEnum.RUNNING)
        when:
        def jobStatsFormatter = new JobStatsFormatter(jobStatusRecord, [result])
        and:
        def eventData = jobStatsFormatter.toEventData()
        then:
        jobStatsFormatter != null
        and:
        jobStatsFormatter.getJobId() == "11ed9ac1-49ce-40dc-bab0-3f347092da6c"
        and:
        jobStatsFormatter.getUserId() == "N/A"
        and:
        jobStatsFormatter.getCommandId() == "N/A"
        and:
        jobStatsFormatter.getJobInsertDate() == "N/A"
        and:
        jobStatsFormatter.getJobStartDate() == "N/A"
        and:
        jobStatsFormatter.getJobEndDate() == "N/A"
        and:
        jobStatsFormatter.getJobInsertDuration() == "N/A"
        and:
        jobStatsFormatter.getJobStartDuration() == "N/A"
        and:
        jobStatsFormatter.getJobStatus() == "N/A"
        and:
        jobStatsFormatter.getNumWfs() == "0"
        and:
        jobStatsFormatter.getNumNotCompletedWfs() == "0"
        and:
        jobStatsFormatter.getNumInsertedWfs() == "1"
        and:
        jobStatsFormatter.getNumPendingWfs() == "0"
        and:
        jobStatsFormatter.getNumRunningWfs() == "1"
        and:
        jobStatsFormatter.getNumSuccessWfs() == "0"
        and:
        jobStatsFormatter.getNumErrorWfs() == "0"
        and:
        jobStatsFormatter.hasErrors() == false
        and:
        jobStatsFormatter.getMinDurationSuccessWfs() == "N/A"
        and:
        jobStatsFormatter.getMaxDurationSuccessWfs() == "N/A"
        and:
        jobStatsFormatter.getAvgDurationSuccessWfs() == "N/A"
        and:
        eventData.get("JOB_COMMAND_ID") == "N/A"
        eventData.get("JOB_START_DURATION") == 0
        eventData.get("JOB_INSERT_DURATION") == 0
        eventData.get("JOB_NUM_WORKFLOWS") == 0
        eventData.get("JOB_NUM_SUCCESS_WORKFLOWS") == 0
        eventData.get("JOB_NUM_ERROR_WORKFLOWS") == 0
        eventData.get("JOB_MIN_SUCCESS_WORKFLOWS_DURATION") == null
        eventData.get("JOB_MAX_SUCCESS_WORKFLOWS_DURATION") == null
        eventData.get("JOB_AVG_SUCCESS_WORKFLOWS_DURATION") == null
    }

    def 'job with one successful workflow'() {
        given:
        def jobId = UUID.fromString("11ed9ac1-49ce-40dc-bab0-3f347092da6c")
        def String wfResultUUIDString = jobId.toString() + "1"
        def UUID wfWakeId = UUID.nameUUIDFromBytes(wfResultUUIDString.getBytes())
        and:
        def jobStatusRecord = new JobStatusRecord()
        jobStatusRecord.setJobId(jobId)
        and:
        def WfResult result = new WfResult()
        result.setJobId(jobId)
        result.setWfWakeId(wfWakeId)
        // to set startDate
        result.setStatus(WfStatusEnum.RUNNING)
        when:
        // to set endDate
        result.setStatus(WfStatusEnum.SUCCESS)
        def jobStatsFormatter = new JobStatsFormatter(jobStatusRecord, [result])
        and:
        def eventData = jobStatsFormatter.toEventData()
        then:
        jobStatsFormatter != null
        and:
        jobStatsFormatter.getJobId() == "11ed9ac1-49ce-40dc-bab0-3f347092da6c"
        and:
        jobStatsFormatter.getUserId() == "N/A"
        and:
        jobStatsFormatter.getCommandId() == "N/A"
        and:
        jobStatsFormatter.getJobInsertDate() == "N/A"
        and:
        jobStatsFormatter.getJobStartDate() == "N/A"
        and:
        jobStatsFormatter.getJobEndDate() == "N/A"
        and:
        jobStatsFormatter.getJobInsertDuration() == "N/A"
        and:
        jobStatsFormatter.getJobStartDuration() == "N/A"
        and:
        jobStatsFormatter.getJobStatus() == "N/A"
        and:
        jobStatsFormatter.getNumWfs() == "0"
        and:
        jobStatsFormatter.getNumNotCompletedWfs() == "0"
        and:
        jobStatsFormatter.getNumInsertedWfs() == "1"
        and:
        jobStatsFormatter.getNumPendingWfs() == "0"
        and:
        jobStatsFormatter.getNumRunningWfs() == "0"
        and:
        jobStatsFormatter.getNumSuccessWfs() == "1"
        and:
        jobStatsFormatter.getNumErrorWfs() == "0"
        and:
        jobStatsFormatter.hasErrors() == false
        and:
        jobStatsFormatter.getMinDurationSuccessWfs() != "N/A"
        and:
        jobStatsFormatter.getMaxDurationSuccessWfs() != "N/A"
        and:
        jobStatsFormatter.getAvgDurationSuccessWfs() != "N/A"
        and:
        eventData.get("JOB_COMMAND_ID") == "N/A"
        eventData.get("JOB_START_DURATION") == 0
        eventData.get("JOB_INSERT_DURATION") == 0
        eventData.get("JOB_NUM_WORKFLOWS") == 0
        eventData.get("JOB_NUM_SUCCESS_WORKFLOWS") == 1
        eventData.get("JOB_NUM_ERROR_WORKFLOWS") == 0
    }

    def 'job with one failed workflow'() {
        given:
        def jobId = UUID.fromString("11ed9ac1-49ce-40dc-bab0-3f347092da6c")
        def String wfResultUUIDString = jobId.toString() + "1"
        def UUID wfWakeId = UUID.nameUUIDFromBytes(wfResultUUIDString.getBytes())
        and:
        def jobStatusRecord = new JobStatusRecord()
        jobStatusRecord.setJobId(jobId)
        and:
        def WfResult result = new WfResult()
        result.setJobId(jobId)
        result.setWfWakeId(wfWakeId)
        // to set startDate
        result.setStatus(WfStatusEnum.RUNNING)
        when:
        // to set endDate
        result.setStatus(WfStatusEnum.ERROR)
        def jobStatsFormatter = new JobStatsFormatter(jobStatusRecord, [result])
        and:
        def eventData = jobStatsFormatter.toEventData()
        then:
        jobStatsFormatter != null
        and:
        jobStatsFormatter.getJobId() == "11ed9ac1-49ce-40dc-bab0-3f347092da6c"
        and:
        jobStatsFormatter.getUserId() == "N/A"
        and:
        jobStatsFormatter.getCommandId() == "N/A"
        and:
        jobStatsFormatter.getJobInsertDate() == "N/A"
        and:
        jobStatsFormatter.getJobStartDate() == "N/A"
        and:
        jobStatsFormatter.getJobEndDate() == "N/A"
        and:
        jobStatsFormatter.getJobInsertDuration() == "N/A"
        and:
        jobStatsFormatter.getJobStartDuration() == "N/A"
        and:
        jobStatsFormatter.getJobStatus() == "N/A"
        and:
        jobStatsFormatter.getNumWfs() == "0"
        and:
        jobStatsFormatter.getNumNotCompletedWfs() == "0"
        and:
        jobStatsFormatter.getNumInsertedWfs() == "1"
        and:
        jobStatsFormatter.getNumPendingWfs() == "0"
        and:
        jobStatsFormatter.getNumRunningWfs() == "0"
        and:
        jobStatsFormatter.getNumSuccessWfs() == "0"
        and:
        jobStatsFormatter.getNumErrorWfs() == "1"
        and:
        jobStatsFormatter.hasErrors() == true
        and:
        jobStatsFormatter.getMinDurationSuccessWfs() == "N/A"
        and:
        jobStatsFormatter.getMaxDurationSuccessWfs() == "N/A"
        and:
        jobStatsFormatter.getAvgDurationSuccessWfs() == "N/A"
        and:
        eventData.get("JOB_COMMAND_ID") == "N/A"
        eventData.get("JOB_START_DURATION") == 0
        eventData.get("JOB_INSERT_DURATION") == 0
        eventData.get("JOB_NUM_WORKFLOWS") == 0
        eventData.get("JOB_NUM_SUCCESS_WORKFLOWS") == 0
        eventData.get("JOB_NUM_ERROR_WORKFLOWS") == 1
        eventData.get("JOB_MIN_SUCCESS_WORKFLOWS_DURATION") == null
        eventData.get("JOB_MAX_SUCCESS_WORKFLOWS_DURATION") == null
        eventData.get("JOB_AVG_SUCCESS_WORKFLOWS_DURATION") == null
    }

    def 'job with two workflows, one successful and one failed'() {
        given:
        def jobId = UUID.fromString("11ed9ac1-49ce-40dc-bab0-3f347092da6c")
        def String wfResultUUIDString = jobId.toString() + "1"
        def UUID wfWakeId = UUID.nameUUIDFromBytes(wfResultUUIDString.getBytes())
        def String wfResultUUIDString2 = jobId.toString() + "2"
        def UUID wfWakeId2 = UUID.nameUUIDFromBytes(wfResultUUIDString2.getBytes())
        and:
        def jobStatusRecord = new JobStatusRecord()
        jobStatusRecord.setJobId(jobId)
        and:
        def WfResult result = new WfResult()
        result.setJobId(jobId)
        result.setWfWakeId(wfWakeId)
        // to set startDate
        result.setStatus(WfStatusEnum.RUNNING)
        and:
        def WfResult result2 = new WfResult()
        result2.setJobId(jobId)
        result2.setWfWakeId(wfWakeId2)
        // to set startDate
        result2.setStatus(WfStatusEnum.RUNNING)
        when:
        // to set endDate
        result.setStatus(WfStatusEnum.SUCCESS)
        result2.setStatus(WfStatusEnum.ERROR)
        def jobStatsFormatter = new JobStatsFormatter(jobStatusRecord, [result, result2])
        and:
        def eventData = jobStatsFormatter.toEventData()
        then:
        jobStatsFormatter != null
        and:
        jobStatsFormatter.getJobId() == "11ed9ac1-49ce-40dc-bab0-3f347092da6c"
        and:
        jobStatsFormatter.getUserId() == "N/A"
        and:
        jobStatsFormatter.getCommandId() == "N/A"
        and:
        jobStatsFormatter.getJobInsertDate() == "N/A"
        and:
        jobStatsFormatter.getJobStartDate() == "N/A"
        and:
        jobStatsFormatter.getJobEndDate() == "N/A"
        and:
        jobStatsFormatter.getJobInsertDuration() == "N/A"
        and:
        jobStatsFormatter.getJobStartDuration() == "N/A"
        and:
        jobStatsFormatter.getJobStatus() == "N/A"
        and:
        jobStatsFormatter.getNumWfs() == "0"
        and:
        jobStatsFormatter.getNumNotCompletedWfs() == "0"
        and:
        jobStatsFormatter.getNumInsertedWfs() == "2"
        and:
        jobStatsFormatter.getNumPendingWfs() == "0"
        and:
        jobStatsFormatter.getNumRunningWfs() == "0"
        and:
        jobStatsFormatter.getNumSuccessWfs() == "1"
        and:
        jobStatsFormatter.getNumErrorWfs() == "1"
        and:
        jobStatsFormatter.hasErrors() == true
        and:
        jobStatsFormatter.getMinDurationSuccessWfs() != "N/A"
        and:
        jobStatsFormatter.getMaxDurationSuccessWfs() != "N/A"
        and:
        jobStatsFormatter.getAvgDurationSuccessWfs() != "N/A"
        and:
        eventData.get("JOB_COMMAND_ID") == "N/A"
        eventData.get("JOB_START_DURATION") == 0
        eventData.get("JOB_INSERT_DURATION") == 0
        eventData.get("JOB_NUM_WORKFLOWS") == 0
        eventData.get("JOB_NUM_SUCCESS_WORKFLOWS") == 1
        eventData.get("JOB_NUM_ERROR_WORKFLOWS") == 1
    }
}
