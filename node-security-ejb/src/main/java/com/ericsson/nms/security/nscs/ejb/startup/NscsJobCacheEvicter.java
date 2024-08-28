/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2021
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.ejb.startup;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Timer;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.ejb.credential.MembershipListener;
import com.ericsson.oss.services.jobs.dto.JobDto;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;
import com.ericsson.oss.services.security.nscs.jobs.JobStatsFormatter;

@Singleton
public class NscsJobCacheEvicter {

    private static final int EVICTION_DAYS = 1;
    private static final long EVICTION_INTERVAL = 60 * 60 * 24 * 1000L * EVICTION_DAYS; //N days in ms
    private static final String RUNNING_JOBS = "runningJobs";
    private static final String COMPLETED_JOBS = "completedJobs";

    @Inject
    private NscsJobCacheHandler cacheHandler;

    @Inject
    private MembershipListener membershipListener;

    @Inject
    private Logger logger;

    @Schedule(hour = "0", persistent = false)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void timeoutHandler(final Timer timer) {
        evict(EVICTION_INTERVAL);
    }

    public void evict(final Long interval) {

        if (membershipListener.isMaster()) {
            logger.info("Start evicting jobs older than [{}] ms", interval);

            final List<JobDto> records = cacheHandler.getAllJobs();
            if (records != null && !records.isEmpty()) {
                logger.info("Found [{}] jobs in cache", records.size());

                final Map<String, Integer> evictedJobsCounters = new HashMap<>();
                evictedJobsCounters.put(COMPLETED_JOBS, 0);
                evictedJobsCounters.put(RUNNING_JOBS, 0);

                evictJobsOlderThan(interval, records, evictedJobsCounters);

                if (evictedJobsCounters.get(RUNNING_JOBS) > 0) {
                    logger.warn("Evicted [{}] completed jobs and [{}] running jobs older than [{}] ms", evictedJobsCounters.get(COMPLETED_JOBS),
                            evictedJobsCounters.get(RUNNING_JOBS), interval);
                } else {
                    logger.info("Evicted [{}] completed jobs older than [{}] ms", evictedJobsCounters.get(COMPLETED_JOBS), interval);
                }
            } else {
                logger.info("No jobs found in cache: records {}", records);
            }
        } else {
            logger.info("I'm slave, no need to evict any jobs");
        }
    }

    /**
     * Evicts from cache the jobs in the given list that are older than the given interval (in ms).
     * 
     * Counters of the evicted jobs are updated as well.
     * 
     * @param interval
     *            the interval (in ms).
     * @param records
     *            the jobs currently in the cache.
     * @param evictedJobsCounters
     *            the counters of evicted jobs.
     */
    private void evictJobsOlderThan(final Long interval, final List<JobDto> records, final Map<String, Integer> evictedJobsCounters) {
        final Calendar c = getCalendarOlderThanInterval(interval);

        for (final JobDto record : records) {
            evictJobOlderThan(c, record, evictedJobsCounters);
        }
    }

    /**
     * Gets Calendar instance for instant of time older than given interval (in ms) respect of current time.
     * 
     * @param interval
     *            the interval (in ms).
     * @return the Calendar instance older than given interval.
     */
    private Calendar getCalendarOlderThanInterval(final Long interval) {
        final Calendar today = Calendar.getInstance();
        final long todayMs = today.getTimeInMillis();
        final long delta = todayMs - interval;

        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(delta);
        return c;
    }

    /**
     * Evicts from cache the given job if it is older than the given date.
     * 
     * Counters of the evicted jobs are updated as well.
     * 
     * @param c
     *            the calendar date.
     * @param record
     *            the job.
     * @param evictedJobsCounters
     *            the counters of evicted jobs.
     */
    private void evictJobOlderThan(final Calendar c, final JobDto record, final Map<String, Integer> evictedJobsCounters) {
        final JobStatsFormatter jobStatsFormatter = new JobStatsFormatter(record);
        final Date startDate = record.getStartDate();
        final Date endDate = record.getEndDate();
        final String startDateStr = jobStatsFormatter.getJobStartDate();
        final String endDateStr = jobStatsFormatter.getJobEndDate();
        logger.debug("Job ID [{}] in cache, start date [{}], end date [{}]", record.getJobId(), startDateStr, endDateStr);
        boolean isJobToBeEvicted = false;
        if (endDate != null) {
            if (endDate.compareTo(c.getTime()) < 0) {
                logger.debug("To be evicted completed job ID [{}], start date [{}], end date [{}]", record.getJobId(), startDateStr, endDateStr);
                evictedJobsCounters.put(COMPLETED_JOBS, evictedJobsCounters.get(COMPLETED_JOBS) + 1);
                isJobToBeEvicted = true;
            }
        } else {
            if (startDate != null && startDate.compareTo(c.getTime()) < 0) {
                logger.debug("To be evicted running job ID [{}], start date [{}], end date [{}]", record.getJobId(), startDateStr, endDateStr);
                evictedJobsCounters.put(RUNNING_JOBS, evictedJobsCounters.get(RUNNING_JOBS) + 1);
                isJobToBeEvicted = true;
            }
        }
        if (isJobToBeEvicted) {
            logger.info("Evicting job ID [{}], start date [{}], end date [{}]", record.getJobId(), startDateStr, endDateStr);
            cacheHandler.removeJob(record.getJobId());
            logger.info("Evicted job ID [{}], start date [{}], end date [{}]", record.getJobId(), startDateStr, endDateStr);
        }
    }

    /**
     * Returns the default eviction interval (in ms).
     * 
     * @return the default eviction interval (in ms)
     */
    public long getDefaultEvictionInterval() {
        return EVICTION_INTERVAL;
    }
}
