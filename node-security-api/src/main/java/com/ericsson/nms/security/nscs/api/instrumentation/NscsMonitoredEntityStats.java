/*-----------------------------------------------------------------------------
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
package com.ericsson.nms.security.nscs.api.instrumentation;

/**
 * Auxiliary class containing statistics of a generic monitored entity.
 *
 * A monitored entity can be: all workflow instances of a specific type, all remote invocations of a specific service.
 */
public class NscsMonitoredEntityStats {
    /**
     * Number of instances of the monitored entity finished with success
     */
    private Integer successful;

    /**
     * Number of instances of the monitored entity finished with failure
     */
    private Integer failed;

    /**
     * Number of instances of the monitored entity finished with error
     */
    private Integer errored;

    /**
     * Number of instances of the monitored entity finished with timeout
     */
    private Integer timedOut;

    /**
     * Total duration of successful instances of the monitored entity
     */
    private Long totalDurationOfSuccessful;

    /**
     * @param successful
     *          the successful
     * @param failed
     *          the failed
     * @param errored
     *          the errored
     * @param timedOut
     *          the timedOut
     * @param totalDurationOfSuccessful
     *          the totalDurationOfSuccessful
     */
    public NscsMonitoredEntityStats(final Integer successful, final Integer failed, final Integer errored, final Integer timedOut,
            final Long totalDurationOfSuccessful) {
        super();
        this.successful = successful;
        this.failed = failed;
        this.errored = errored;
        this.timedOut = timedOut;
        this.totalDurationOfSuccessful = totalDurationOfSuccessful;
    }

    /**
     * @return the successful
     */
    public Integer getSuccessful() {
        return successful;
    }

    /**
     * @return the failed
     */
    public Integer getFailed() {
        return failed;
    }

    /**
     * @return the errored
     */
    public Integer getErrored() {
        return errored;
    }

    /**
     * @return the timedOut
     */
    public Integer getTimedOut() {
        return timedOut;
    }

    /**
     * @return the totalDurationOfSuccessful
     */
    public Long getTotalDurationOfSuccessful() {
        return totalDurationOfSuccessful;
    }

    /**
     * Updates stats for successful instances of the monitored entity.
     *
     * @param duration
     *            the duration of current successful instances of the monitored entity
     */
    public void updateSuccessful(final Long duration) {
        successful = successful + 1;
        totalDurationOfSuccessful = totalDurationOfSuccessful + duration;
    }

    /**
     * Updates stats for failed instances of the monitored entity.
     */
    public void updateFailed() {
        failed = failed + 1;
    }

    /**
     * Updates stats for errored instances of the monitored entity.
     */
    public void updateErrored() {
        errored = errored + 1;
    }

    /**
     * Updates stats for timed-out instances of the monitored entity.
     */
    public void updateTimedOut() {
        timedOut = timedOut + 1;
    }

}
