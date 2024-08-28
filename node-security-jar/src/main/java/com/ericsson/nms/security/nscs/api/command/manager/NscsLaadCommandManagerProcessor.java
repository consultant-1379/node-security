/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.command.manager;

import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.dto.WfResult;

/**
 * This interface initiates the workflow for all LAAD operations.
 *
 * @author tcsgoja
 *
 */
public interface NscsLaadCommandManagerProcessor {
    /**
     * Executes the LaadFilesDistributeWorkFlow for a valid node.
     *
     * @param nodeFDN
     *            the node FDN
     * @param jobStatusRecord
     *            provides job status for each node
     * @param workflowId
     *            the workflow id
     * @return Workflow result with node and job id details
     *
     */
    public WfResult executeLaadFilesDistributeWorkflow(final String nodeFDN, final JobStatusRecord jobStatusRecord, final int workflowId);

}
