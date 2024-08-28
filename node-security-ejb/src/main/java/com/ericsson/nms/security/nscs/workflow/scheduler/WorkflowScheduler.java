/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.workflow.scheduler;

import javax.annotation.*;
import javax.ejb.*;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.scheduler.WorkflowSchedulerInterface;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.classic.EAccessControlBean;
import com.ericsson.oss.itpf.sdk.context.classic.ContextServiceBean;

@Startup
@Singleton
public class WorkflowScheduler implements WorkflowSchedulerInterface {

    private static final String WORKFLOW_SCHEDULER_CONTEXT_USER_ID = "NO USER DATA";

    Logger logger = LoggerFactory.getLogger(WorkflowScheduler.class);

    @Inject
    private EAccessControlBean eAccessControl;
    
    @Inject
    private WorkflowSchedulerProcessor workflowSchedulerProcessor;

    @Resource
    TimerService timerService;

    private Timer schedulingTimer;
    private long schedulingInterval = 30 * 1000; /* in ms */

    @PostConstruct
    public void startSchedulingActivity() {
        logger.info("Starting scheduling activity");
        schedulingTimer = timerService.createIntervalTimer(5 * 60 * 1000, schedulingInterval, createNonPersistentTimerConfig());
    }

    @Timeout
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void schedule() {

        eAccessControl.setAuthUserSubject(WORKFLOW_SCHEDULER_CONTEXT_USER_ID);

        logger.debug("Periodic Workflow Schedule");
        workflowSchedulerProcessor.scheduledUpdate();

        flushContext();

    }

    private TimerConfig createNonPersistentTimerConfig() {
        final TimerConfig timerConfig = new TimerConfig();
        timerConfig.setPersistent(false);
        return timerConfig;
    }

    @PreDestroy
    public void cleanUp() {
        if (schedulingTimer != null) {
            schedulingTimer.cancel();
        }
    }

    /**
     * @return the schedulinginterval
     */
    public long getSchedulinginterval() {
        return schedulingInterval;
    }

    /**
     * @param schedulingInterval
     *            the schedulingInterval to set
     */
    public void setSchedulingInterval(final long schedulingInterval) {
        logger.debug("Requested setting of workflows scheduling interval");
        this.schedulingInterval = schedulingInterval;

        if (schedulingTimer != null) {
            schedulingTimer.cancel();
        }

        logger.debug("Starting scheduling activity with new interval value : {} ms", schedulingInterval);
        schedulingTimer = timerService.createIntervalTimer(schedulingInterval, schedulingInterval, createNonPersistentTimerConfig());
    }

    @Override
    public void forcedSchedule() {
        workflowSchedulerProcessor.scheduledUpdate();
        logger.debug("Forced execution workflow schedule");
    }

    protected void flushContext() {
        new ContextServiceBean().flushContext();
    }

}