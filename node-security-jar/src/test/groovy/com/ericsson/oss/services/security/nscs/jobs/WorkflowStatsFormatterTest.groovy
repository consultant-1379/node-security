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
import com.ericsson.oss.services.dto.WfResult
import com.ericsson.oss.services.enums.WfStatusEnum

class WorkflowStatsFormatterTest extends CdiSpecification {

    def 'workflow empty' () {
        given:
        def WfResult result = new WfResult()
        when:
        def workflowStatsFormatter = new WorkflowStatsFormatter(result)
        then:
        workflowStatsFormatter != null
        and:
        workflowStatsFormatter.getWfId() == "N/A"
        and:
        workflowStatsFormatter.getNodeName() == "N/A"
        and:
        workflowStatsFormatter.getWfStatus() == "N/A"
        and:
        workflowStatsFormatter.getWfDetails() == "N/A"
        and:
        workflowStatsFormatter.getWfStartDate() == "N/A"
        and:
        workflowStatsFormatter.getWfEndDate() == "N/A"
        and:
        workflowStatsFormatter.getWfDuration() == "N/A"
        and:
        workflowStatsFormatter.getWfResult() == "N/A"
        and:
        workflowStatsFormatter.getWfWakeId() == "N/A"
        and:
        workflowStatsFormatter.getWfParams() == "N/A"
        and:
        workflowStatsFormatter.getJobId() == "N/A"
        and:
        workflowStatsFormatter.getIsWaiting() == "false"
        and:
        workflowStatsFormatter.getTimestamp() == "0"
    }

    def 'workflow pending' () {
        given:
        def jobId = UUID.fromString("11ed9ac1-49ce-40dc-bab0-3f347092da6c")
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
        def workflowStatsFormatter = new WorkflowStatsFormatter(result)
        then:
        workflowStatsFormatter != null
        and:
        workflowStatsFormatter.getWfId() == "N/A"
        and:
        workflowStatsFormatter.getNodeName() == "NODE"
        and:
        workflowStatsFormatter.getWfStatus() == "PENDING"
        and:
        workflowStatsFormatter.getWfDetails() == "N/A"
        and:
        workflowStatsFormatter.getWfStartDate() == "N/A"
        and:
        workflowStatsFormatter.getWfEndDate() == "N/A"
        and:
        workflowStatsFormatter.getWfDuration() == "N/A"
        and:
        workflowStatsFormatter.getWfResult() == "N/A"
        and:
        workflowStatsFormatter.getWfWakeId() == wfWakeId.toString()
        and:
        workflowStatsFormatter.getWfParams() != "N/A"
        and:
        workflowStatsFormatter.getJobId() == "11ed9ac1-49ce-40dc-bab0-3f347092da6c"
        and:
        workflowStatsFormatter.getIsWaiting() == "false"
        and:
        workflowStatsFormatter.getTimestamp() == "0"
    }

    def 'workflow started no params' () {
        given:
        def jobId = UUID.fromString("11ed9ac1-49ce-40dc-bab0-3f347092da6c")
        def String wfResultUUIDString = jobId.toString() + "1"
        def UUID wfWakeId = UUID.nameUUIDFromBytes(wfResultUUIDString.getBytes())
        def WfResult result = new WfResult()
        result.setWfId('1234')
        result.setNodeName('NODE')
        result.setStatus(WfStatusEnum.RUNNING)
        result.setWfWakeId(wfWakeId)
        result.setJobId(jobId)
        when:
        def workflowStatsFormatter = new WorkflowStatsFormatter(result)
        then:
        workflowStatsFormatter != null
        and:
        workflowStatsFormatter.getWfId() == "1234"
        and:
        workflowStatsFormatter.getNodeName() == "NODE"
        and:
        workflowStatsFormatter.getWfStatus() == "RUNNING"
        and:
        workflowStatsFormatter.getWfDetails() == "N/A"
        and:
        workflowStatsFormatter.getWfStartDate() != "N/A"
        and:
        workflowStatsFormatter.getWfEndDate() == "N/A"
        and:
        workflowStatsFormatter.getWfDuration() == "N/A"
        and:
        workflowStatsFormatter.getWfResult() == "N/A"
        and:
        workflowStatsFormatter.getWfWakeId() == wfWakeId.toString()
        and:
        workflowStatsFormatter.getWfParams() == "N/A"
        and:
        workflowStatsFormatter.getJobId() == "11ed9ac1-49ce-40dc-bab0-3f347092da6c"
        and:
        workflowStatsFormatter.getIsWaiting() == "false"
        and:
        workflowStatsFormatter.getTimestamp() == "0"
    }

    def 'workflow completed with success with result and details' () {
        given:
        def jobId = UUID.fromString("11ed9ac1-49ce-40dc-bab0-3f347092da6c")
        def String wfResultUUIDString = jobId.toString() + "1"
        def UUID wfWakeId = UUID.nameUUIDFromBytes(wfResultUUIDString.getBytes())
        def wfParams = ['PARAM1': 'VALUE1', 'PARAM2': 'VALUE2', 'Workflow Result': 'result']
        def WfResult result = new WfResult()
        result.setNodeName('NODE')
        // to set start date
        result.setWfId('1234')
        result.setStatus(WfStatusEnum.RUNNING)
        result.setStatus(WfStatusEnum.SUCCESS)
        result.setMessage('[OK]')
        result.setWfWakeId(wfWakeId)
        result.setWfParams(wfParams)
        result.setJobId(jobId)
        when:
        def workflowStatsFormatter = new WorkflowStatsFormatter(result)
        then:
        workflowStatsFormatter != null
        and:
        workflowStatsFormatter.getWfId() == "1234"
        and:
        workflowStatsFormatter.getNodeName() == "NODE"
        and:
        workflowStatsFormatter.getWfStatus() == "SUCCESS"
        and:
        workflowStatsFormatter.getWfDetails() == "[OK]"
        and:
        workflowStatsFormatter.getWfStartDate() != "N/A"
        and:
        workflowStatsFormatter.getWfEndDate() != "N/A"
        and:
        workflowStatsFormatter.getWfDuration() != "N/A"
        and:
        workflowStatsFormatter.getWfResult() == "result"
        and:
        workflowStatsFormatter.getWfWakeId() == wfWakeId.toString()
        and:
        workflowStatsFormatter.getWfParams() != "N/A"
        and:
        workflowStatsFormatter.getJobId() == "11ed9ac1-49ce-40dc-bab0-3f347092da6c"
        and:
        workflowStatsFormatter.getIsWaiting() == "false"
        and:
        workflowStatsFormatter.getTimestamp() == "0"
    }

    def 'workflow completed with success without result and with details' () {
        given:
        def jobId = UUID.fromString("11ed9ac1-49ce-40dc-bab0-3f347092da6c")
        def String wfResultUUIDString = jobId.toString() + "1"
        def UUID wfWakeId = UUID.nameUUIDFromBytes(wfResultUUIDString.getBytes())
        def wfParams = ['PARAM1': 'VALUE1', 'PARAM2': 'VALUE2']
        def WfResult result = new WfResult()
        result.setNodeName('NODE')
        // to set start date
        result.setWfId('1234')
        result.setStatus(WfStatusEnum.RUNNING)
        result.setStatus(WfStatusEnum.SUCCESS)
        result.setMessage('[OK]')
        result.setWfWakeId(wfWakeId)
        result.setWfParams(wfParams)
        result.setJobId(jobId)
        when:
        def workflowStatsFormatter = new WorkflowStatsFormatter(result)
        then:
        workflowStatsFormatter != null
        and:
        workflowStatsFormatter.getWfId() == "1234"
        and:
        workflowStatsFormatter.getNodeName() == "NODE"
        and:
        workflowStatsFormatter.getWfStatus() == "SUCCESS"
        and:
        workflowStatsFormatter.getWfDetails() == "[OK]"
        and:
        workflowStatsFormatter.getWfStartDate() != "N/A"
        and:
        workflowStatsFormatter.getWfEndDate() != "N/A"
        and:
        workflowStatsFormatter.getWfDuration() != "N/A"
        and:
        workflowStatsFormatter.getWfResult() == "Not Applicable"
        and:
        workflowStatsFormatter.getWfWakeId() == wfWakeId.toString()
        and:
        workflowStatsFormatter.getWfParams() != "N/A"
        and:
        workflowStatsFormatter.getJobId() == "11ed9ac1-49ce-40dc-bab0-3f347092da6c"
        and:
        workflowStatsFormatter.getIsWaiting() == "false"
        and:
        workflowStatsFormatter.getTimestamp() == "0"
    }

    def 'workflow completed with error with details' () {
        given:
        def jobId = UUID.fromString("11ed9ac1-49ce-40dc-bab0-3f347092da6c")
        def String wfResultUUIDString = jobId.toString() + "1"
        def UUID wfWakeId = UUID.nameUUIDFromBytes(wfResultUUIDString.getBytes())
        def wfParams = ['PARAM1': 'VALUE1', 'PARAM2': 'VALUE2']
        def WfResult result = new WfResult()
        result.setNodeName('NODE')
        // to set start date
        result.setWfId('1234')
        result.setStatus(WfStatusEnum.RUNNING)
        result.setStatus(WfStatusEnum.ERROR)
        result.setMessage('[FAILURE]')
        result.setWfWakeId(wfWakeId)
        result.setWfParams(wfParams)
        result.setJobId(jobId)
        when:
        def workflowStatsFormatter = new WorkflowStatsFormatter(result)
        then:
        workflowStatsFormatter != null
        and:
        workflowStatsFormatter.getWfId() == "1234"
        and:
        workflowStatsFormatter.getNodeName() == "NODE"
        and:
        workflowStatsFormatter.getWfStatus() == "ERROR"
        and:
        workflowStatsFormatter.getWfDetails() == "[FAILURE]"
        and:
        workflowStatsFormatter.getWfStartDate() != "N/A"
        and:
        workflowStatsFormatter.getWfEndDate() != "N/A"
        and:
        workflowStatsFormatter.getWfDuration() != "N/A"
        and:
        workflowStatsFormatter.getWfResult() == "N/A"
        and:
        workflowStatsFormatter.getWfWakeId() == wfWakeId.toString()
        and:
        workflowStatsFormatter.getWfParams() != "N/A"
        and:
        workflowStatsFormatter.getJobId() == "11ed9ac1-49ce-40dc-bab0-3f347092da6c"
        and:
        workflowStatsFormatter.getIsWaiting() == "false"
        and:
        workflowStatsFormatter.getTimestamp() == "0"
    }
}
