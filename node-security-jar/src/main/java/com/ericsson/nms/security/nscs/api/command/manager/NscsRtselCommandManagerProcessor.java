/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.command.manager;

import java.util.Set;

import javax.ejb.Local;

import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.rtsel.NodeInfoDetails;
import com.ericsson.nms.security.nscs.rtsel.request.model.NodeRtselConfig;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.dto.WfResult;

/**
 * This interface is having the workflow methods for all RTSEL CommandHandlers.
 * 
 * @author xchowja
 *
 */
@Local
public interface NscsRtselCommandManagerProcessor {

    /**
     * 
     * @param nodeFdn
     * @param nodeInfoDetails
     * @param nodeRtselConfig
     * @param jobStatusRecord
     * @param workflowId
     * @return
     * @throws NscsServiceException
     */
    public WfResult executeActivateRtselSingleWf(final String nodeFdn, final NodeInfoDetails nodeInfoDetails, final NodeRtselConfig nodeRtselConfig, final JobStatusRecord jobStatusRecord,
            final int workflowId) throws NscsServiceException;

    /**
     * @param nodeFdn
     * @param jobStatusRecord
     * @param workflowId
     * @return Workflow result with node and job id details.
     * @throws NscsServiceException
     */
    public WfResult executeDeActivateRtselSingleWf(final String nodeFdn, final JobStatusRecord jobStatusRecord, final int workflowId) throws NscsServiceException;

    /**
     * @param serverNames
     * @param jobStatusRecord
     * @param workflowId
     * @return
     * @throws NscsServiceException
     */
    public WfResult executeRtselDeleteServerWfs(final String nodeFdn, final Set<String> serverNames, final JobStatusRecord jobStatusRecord, final int workflowId) throws NscsServiceException;


}
