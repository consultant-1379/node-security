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
package com.ericsson.nms.security.nscs.timer;

import javax.ejb.Local;

import com.ericsson.oss.itpf.sdk.core.annotation.EService;

/**
 * This service exposes a recurring interval job executed a determined number of times.
 * 
 * @author eanbuzz
 */
@EService
@Local
public interface IntervalJobService {

    /**
     * Create and starts a new interval job timer.<br/>
     * <br/>
     * This timer will trigger the first time after <code>when</code> parameter milliseconds and will repeat <code>times</code>. The time between two
     * notifications is described by the <code>intervalTime</code> parameter. <br/>
     * <br/>
     * Every time that the timer is executed the {@link IntervalJobAction action} is called and the return of it will decide if this interval job
     * should be terminated.
     * 
     * @param when
     *            time in milliseconds when the first time of this interval job should occur .
     * @param intervalTime
     *            time in milliseconds of the interval between job executions.
     * @param times
     *            number of times that this job should be executed.
     * @param action
     *            listener that is called every time that the job is executed.
     */
    void createIntervalJob(final long when, final long intervalTime, final int times, final IntervalJobAction action);

}