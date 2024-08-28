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
package com.ericsson.nms.security.nscs.manager;

import java.util.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.manager.*;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.rtsel.NodeInfoDetails;
import com.ericsson.nms.security.nscs.rtsel.utility.RtselDeleteServerDetails;
import com.ericsson.nms.security.nscs.rtsel.utility.RtselJobInfo;
import com.ericsson.nms.security.nscs.rtsel.utility.RtselUtility;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.dto.WfResult;

/**
 * This class is having the implementation of NscsRtselCommandManager interface.
 * 
 * @author xchowja
 *
 */
@Stateless
public class NscsRtselCommandManagerBean implements NscsRtselCommandManager {

    @Inject
    private Logger logger;

    @EJB
    private NscsCommandManagerProcessor nscsCommandManagerProcessor;

    @EJB
    private NscsRtselCommandManagerProcessor nscsRtselCommandManagerProcessor;
    
    @Inject
    RtselUtility rtselUtility;

    @Override
    public void executeActivateRtselWfs(final List<RtselJobInfo> rtselJobInfoList, final JobStatusRecord jobStatusRecord) throws NscsServiceException {
        final Map<UUID, WfResult> wfResultMap = new HashMap<UUID, WfResult>();
        int workflowId = 1;
        for (RtselJobInfo rtselJobInfo : rtselJobInfoList) {
            logger.info("executeActivateRtselWfs. nodeInfoDetailsList:{} ", rtselJobInfo.getNodeInfoDetailsList().size());
            for (final NodeInfoDetails nodeInfoDetails : rtselJobInfo.getNodeInfoDetailsList()) {
                {
                    final String enrollmentMode = nodeInfoDetails.getEnrollmentMode();
                    for (final String nodeFdn : nodeInfoDetails.getNodeFdnsList()) {
                        
                        //set the enrollment mode before executing the workflow to NetworkElementSecurity MO
                        rtselUtility.setEnrollmentMode(enrollmentMode, nodeFdn);
                        
                        final WfResult result = nscsRtselCommandManagerProcessor.executeActivateRtselSingleWf(nodeFdn, nodeInfoDetails, rtselJobInfo.getNodeRtselConfig(), jobStatusRecord, workflowId);
                        if (result != null) {
                            wfResultMap.put(result.getWfWakeId(), result);
                            workflowId++;
                        }
                    }
                }
            }
        }
        nscsCommandManagerProcessor.insertWorkflowBatch(wfResultMap);
    }

    @Override
    public void executeDeActivateRtselWfs(final List<String> nodeFdnList, final JobStatusRecord jobStatusRecord) throws NscsServiceException {

        final Map<UUID, WfResult> wfResultMap = new HashMap<UUID, WfResult>();
        int workflowId = 1;
        for (String nodeFdn : nodeFdnList) {
            {
                final WfResult result = nscsRtselCommandManagerProcessor.executeDeActivateRtselSingleWf(nodeFdn, jobStatusRecord, workflowId);
                if (result != null) {
                    wfResultMap.put(result.getWfWakeId(), result);
                    workflowId++;
                }
            }
        }
        nscsCommandManagerProcessor.insertWorkflowBatch(wfResultMap);
    }

	@Override
	public void executeRtselDeleteServerWfs(final List<RtselDeleteServerDetails> rtselDeleteServerDetailsList, final JobStatusRecord jobStatusRecord) {
        final Map<UUID, WfResult> wfResultMap = new HashMap<UUID, WfResult>();
        int workflowId = 1;
        for (final RtselDeleteServerDetails rtselDeleteServerDetails : rtselDeleteServerDetailsList) {
            logger.info("executeRtselDeleteServerWfs. nodeFdnsList:{} ", rtselDeleteServerDetails.getNodeFdnsList().size());
            for (final String nodeFdn : rtselDeleteServerDetails.getNodeFdnsList()) {
                final WfResult result = nscsRtselCommandManagerProcessor.executeRtselDeleteServerWfs(nodeFdn, rtselDeleteServerDetails.getServerNames(), jobStatusRecord, workflowId);
                if(result != null){
                    wfResultMap.put(result.getWfWakeId(), result);
                    workflowId++;
                }
            }
        }
        nscsCommandManagerProcessor.insertWorkflowBatch(wfResultMap);
	}
}
