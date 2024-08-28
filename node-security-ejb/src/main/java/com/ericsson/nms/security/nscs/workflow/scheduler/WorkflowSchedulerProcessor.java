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
package com.ericsson.nms.security.nscs.workflow.scheduler;

import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.api.instrumentation.NscsInstrumentationService;
import com.ericsson.nms.security.nscs.ejb.credential.MembershipListener;
import com.ericsson.nms.security.nscs.pib.configuration.WorkflowConfigurationListener;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.services.dto.WfResult;
import com.ericsson.oss.services.enums.WfStatusEnum;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;
import com.ericsson.oss.services.nscs.jobs.NscsJobCacheHandlerImpl;
import com.ericsson.oss.services.wfs.api.instance.WorkflowInstance;

@ApplicationScoped
public class WorkflowSchedulerProcessor {

    Logger logger = LoggerFactory.getLogger(WorkflowScheduler.class);
    private boolean isScheduling = false;

    @EServiceRef
    WorkflowHandler wfHandler;

    @Inject
    NscsJobCacheHandler cacheHandler;

    @Inject
    NscsInstrumentationService nscsInstrumentationService;

    @Inject
    MembershipListener membershipListener;

    @Inject
    WorkflowConfigurationListener workflowConfListener;

    public void scheduledUpdate() {
        try {
            if (membershipListener.isMaster()) {

                if (!isScheduling) {
                    isScheduling = true;

                    /* Here set congestionThreshold from PIB */
                    final int pibCongestionThreshold = workflowConfListener.getPibWfCongestionThreshold();

                    final Map<String, List<WfResult>> wfMap = cacheHandler.getRunningAndPendingWorkflows(pibCongestionThreshold);

                    final int runningWkfsCount = wfMap.get(NscsJobCacheHandlerImpl.WF_KEY_RUNNING).size();
                    nscsInstrumentationService.setNumOfRunningWorkflows(runningWkfsCount);

                    final List<WfResult> wkfsToWake = wfMap.get(NscsJobCacheHandlerImpl.WF_KEY_PENDING);
                    nscsInstrumentationService.setNumOfPendingWorkflows(wkfsToWake.size());

                    logger.debug("Workflow Scheduler Processor: Workflows Threshold[{}], Running[{}], Pending[{}]", pibCongestionThreshold,
                            runningWkfsCount, wkfsToWake.size());

                    if (wkfsToWake.size() > 0) {
                        int nrOfWfsWaked = 0;
                        for (final WfResult wfToWake : wkfsToWake) {
                            /*
                             * Set RUNNING state of workflow in cache before starting workflow in order to avoid race conditions with task handlers
                             */
                            wfToWake.setStatus(WfStatusEnum.RUNNING);
                            if (logger.isInfoEnabled()) {
                                logger.info("Workflow Scheduler Processor: starting wf {} of job {}", wfToWake.getWfWakeId(), wfToWake.getJobId());
                            }
                            cacheHandler.updateWorkflow(wfToWake);
                            logger.debug("Workflow Scheduler Processor: Starting waiting workflow[{}]", wfToWake);
                            WorkflowInstance wf = null;
                            try {
                                wf = wfHandler.dispatch(wfToWake.getWfParams());
                            } catch (final Exception e) {
                                wf = null;
                                logger.error("Workflow Scheduler Processor: exception on dispatch", e);
                            }
                            if (wf != null) {
                                nrOfWfsWaked++;
                                wfToWake.setWfId(wf.getId());
                                cacheHandler.updateWorkflow(wfToWake);
                                logger.info("Workflow Scheduler Processor: Started {}-th workflow[{}]", nrOfWfsWaked, wfToWake);
                            } else {
                                /*
                                 * Rollback workflow status in cache
                                 */
                                wfToWake.setStatus(WfStatusEnum.PENDING);
                                cacheHandler.updateWorkflow(wfToWake);
                                logger.error("Workflow Scheduler Processor: rollback on error starting workflow[{}]", wfToWake);
                            }
                        }
                        logger.debug("Workflow Scheduler Processor: Started [{}] workflows", nrOfWfsWaked);
                    } else {
                        logger.debug("Workflow Scheduler Processor: No workflows to be started at current run");
                    }
                }
            } else {
                nscsInstrumentationService.setNumOfRunningWorkflows(0);
                nscsInstrumentationService.setNumOfPendingWorkflows(0);
            }
        } catch (final Exception e) {
            logger.error("Workflow Scheduler Processor: Problem with periodic workflow schedule", e);
        } finally {
            isScheduling = false;
        }
    }

}