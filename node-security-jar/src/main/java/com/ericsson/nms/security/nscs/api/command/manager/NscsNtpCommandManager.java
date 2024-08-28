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

import java.util.List;

import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.ntp.utility.NtpRemoveWorkflowData;
import com.ericsson.oss.services.dto.JobStatusRecord;

/**
 * This class is used to start WorkflowInstance for NTP remove command on target nodes.
 *
 * @author zmalsiv
 *
 */
public interface NscsNtpCommandManager {

    /**
     * This method is used to start WorkflowInstance for removal of installed NTP server details on the given nodes.
     *
     * @param ntpRemoveWorkFlowDataList
     *            contains details of node name and key Id or server Id to be removed on the target node.
     * @param jobStatusRecord
     *            job id value which associated with ntp key details remove workflow progress info.
     */
    void removeNtpServerData(final List<NtpRemoveWorkflowData> ntpRemoveWorkFlowDataList, final JobStatusRecord jobStatusRecord);

    /**
     * This method is used to start WorkflowInstance for configuration of NTP server details for the given nodes.
     *
     * @param nodes
     *            contains list of nodes.
     * @param jobStatusRecord
     *            job id value which associated with NTP configuration workflow progress info.
     */
    void configureNtpServer(final List<NormalizableNodeReference> nodes, final JobStatusRecord jobStatusRecord);

}
