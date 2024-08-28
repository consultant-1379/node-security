/*------------------------------------------------------------------------------
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
package com.ericsson.nms.security.nscs.api.command.manager;

import javax.ejb.Local;

import com.ericsson.nms.security.nscs.ntp.utility.NtpRemoveWorkflowData;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.dto.WfResult;

/**
 * This interface initiates WorkflowInstance for NTP commands on target nodes.
 *
 * @author zmalsiv
 *
 */
@Local
public interface NscsNtpCommandManagerProcessor {

    /**
     * This method is to remove NTP server details on a single node.
     *
     * @param ntpRemoveWorkflowData
     *            contains the data to remove ntp server details on node
     * @param jobStatusRecord
     *            contains job id value which associated with ntp key details remove workflow progress info.
     * @param workflowId
     *            contains work flow id value which associated with ntp key details for existing work flow.
     * @return Object of type WfResult
     */
    WfResult executeNtpRemoveWorkflow(final NtpRemoveWorkflowData ntpRemoveWorkflowData, final JobStatusRecord jobStatusRecord, final int workflowId);

    /**
     * This method is to configure NTP server details on a single node.
     *
     * @param nodeFdn
     *            the Node Fdn
     * @param jobStatusRecord
     *            job id value which associated with NTP Configuration workflow progress info.
     * @return Object of type WfResult
     */
    WfResult executeNtpConfigureWorkflow(final String nodeFdn, final JobStatusRecord jobStatusRecord, int workflowId);

}
