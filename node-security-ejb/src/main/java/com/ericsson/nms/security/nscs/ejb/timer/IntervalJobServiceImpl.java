/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.ejb.timer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.MOActionService;
import com.ericsson.nms.security.nscs.timer.IntervalJobAction;
import com.ericsson.nms.security.nscs.timer.IntervalJobAction.JobActionParameters;
import com.ericsson.nms.security.nscs.timer.IntervalJobService;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.classic.EAccessControlBean;
import com.ericsson.oss.itpf.sdk.context.classic.ContextServiceBean;

@Stateless
public class IntervalJobServiceImpl implements IntervalJobService {

    private static final String INTERVAL_JOB_CONTEXT_USER_ID = "NO USER DATA";

    @Inject
    private Logger logger;

    @Inject
    private EAccessControlBean eAccessControl;

    @Inject
    private SystemRecorder systemRecorder;

    @Inject
    private TimerService service;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private MOActionService moActionService;

    @EServiceRef
    private WorkflowHandler workflowHandler;

    @Inject
    private NscsCapabilityModelService capabilityService;

    @Timeout
    void timeout(final Timer timer) {

        eAccessControl.setAuthUserSubject(INTERVAL_JOB_CONTEXT_USER_ID);

        this.logger.info("Executing IntervalJob for timer [{}]", timer);

        final IntervalJobInfo info = (IntervalJobInfo) timer.getInfo();
        // increment the execution counter
        ++info.currentExecution;

        final Map<JobActionParameters, Object> params = new HashMap<>();
        params.put(JobActionParameters.CM_READER, this.readerService);
        params.put(JobActionParameters.WORKFLOW_HANDLER, this.workflowHandler);
        params.put(JobActionParameters.MO_ACTION_SERVICE, this.moActionService);
        params.put(JobActionParameters.SYSTEM_RECORDER, this.systemRecorder);
        params.put(JobActionParameters.CAPABILITY_SERVICE, this.capabilityService);
        // call action (listener)
        try {
            final boolean cancelJob = info.action.doAction(params);
            if (cancelJob) {
                // terminate timer
                this.logger.info("Cancelling timer by action order after executing [{}] times. Expected to run [{}] times ", info.currentExecution, info.maxExecutionTimes);
                timer.cancel();
           } else if (info.currentExecution == info.maxExecutionTimes) { // check if should end timer
                // terminate timer
                this.logger.info("Terminating timer after executing [{}] times", info.currentExecution);
                timer.cancel();
           }
        } catch (Exception e) {
            this.logger.warn("Timer Exception. Stopping EJB timer", e);
            timer.cancel();
        } finally {
            flushContext();
        }
    }

    @Override
    public void createIntervalJob(final long when, final long intervalTime, final int times, final IntervalJobAction action) {
        if (action == null) {
            throw new IllegalArgumentException("Action must not be null.");
        }
        final TimerConfig config = new TimerConfig(new IntervalJobInfo(times, action), true);
        // TODO: timer solution will be decided by TORF-34735
        config.setPersistent(false);
        service.createIntervalTimer(when, intervalTime, config);
        this.logger.info("Timer wil start in [{}]s and run for [{}] times", when / 1000, times);
    }

    /**
     * Object that is stored inside the timer configuration.
     */
    public static class IntervalJobInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        private final int maxExecutionTimes;
        private int currentExecution;
        private final IntervalJobAction action;

        public IntervalJobInfo(final int times, final IntervalJobAction action) {
            this.maxExecutionTimes = times;
            this.currentExecution = 0;
            this.action = action;
        }

        public int getCurrentExecution() {
            return currentExecution;
        }

        public void setCurrentExecution(int currentExecution) {
            this.currentExecution = currentExecution;
        }

    }

    protected void flushContext() {
        new ContextServiceBean().flushContext();
    }

}
