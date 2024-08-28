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
package com.ericsson.nms.security.nscs.ejb.startup

import static org.junit.Assert.assertTrue

import javax.ejb.Timer

import org.junit.Before

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.ejb.credential.MembershipListener
import com.ericsson.oss.services.dto.JobStatusRecord
import com.ericsson.oss.services.jobs.dto.JobDto
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler

import spock.lang.Shared
import spock.lang.Unroll

class NscsJobCacheEvicterTest extends CdiSpecification {

    @ObjectUnderTest
    NscsJobCacheEvicter nscsJobCacheEvicter

    @MockedImplementation
    MembershipListener membershipListener

    @MockedImplementation
    NscsJobCacheHandler nscsJobCacheHandler

    @MockedImplementation
    Timer timer

    @Shared
    JobDto pendingJob = null

    @Shared
    JobDto runningJob = null

    @Shared
    JobDto completedJob = null

    @Shared
    JobDto oldRunningJob = null

    @Shared
    JobDto oldCompletedJob = null

    @Before
    void before() {
        long evictionInterval = nscsJobCacheEvicter.getDefaultEvictionInterval()
        long now = System.currentTimeMillis()
        Date startedAfter = new Date(now - 1);
        Date completedAfter = new Date(now);
        assertTrue(completedAfter.compareTo(startedAfter) > 0)
        Date startedBefore = new Date(now - evictionInterval - 2);
        Date completedBefore = new Date(now - evictionInterval - 1);
        assertTrue(completedBefore.compareTo(startedBefore) > 0)

        // jobs not older than
        JobStatusRecord pendingRecord = new JobStatusRecord()
        pendingRecord.setStartDate(null)
        pendingRecord.setEndDate(null)
        pendingJob = new JobDto(pendingRecord, [])
        JobStatusRecord runningRecord = new JobStatusRecord()
        runningRecord.setStartDate(startedAfter)
        runningRecord.setEndDate(null)
        runningJob = new JobDto(runningRecord, [])
        JobStatusRecord completedRecord = new JobStatusRecord()
        completedRecord.setStartDate(startedAfter)
        completedRecord.setEndDate(completedAfter)
        completedJob = new JobDto(completedRecord, [])

        // jobs older than
        JobStatusRecord oldRunningRecord = new JobStatusRecord()
        oldRunningRecord.setStartDate(startedBefore)
        oldRunningRecord.setEndDate(null)
        oldRunningJob = new JobDto(oldRunningRecord, [])
        JobStatusRecord oldCompletedRecord = new JobStatusRecord()
        oldCompletedRecord.setStartDate(startedBefore)
        oldCompletedRecord.setEndDate(completedBefore)
        oldCompletedJob = new JobDto(oldCompletedRecord, [])
    }

    def "object under test injection" () {
        expect:
        nscsJobCacheEvicter != null
    }

    def "evict on slave" () {
        given:
        membershipListener.isMaster() >> false
        when:
        nscsJobCacheEvicter.timeoutHandler(timer)
        then:
        noExceptionThrown()
        and:
        0 * nscsJobCacheEvicter.cacheHandler.getAllJobs()
    }

    @Unroll
    def "evict on master with jobs #records" () {
        given:
        membershipListener.isMaster() >> true
        and:
        nscsJobCacheHandler.getAllJobs() >> records
        when:
        nscsJobCacheEvicter.timeoutHandler(timer)
        then:
        noExceptionThrown()
        and:
        numOfRemove * nscsJobCacheHandler.removeJob(_)
        where:
        records << [
            [],
            null,
            [
                pendingJob,
                runningJob,
                completedJob
            ],
            [
                pendingJob,
                oldRunningJob,
                oldCompletedJob
            ],
            [
                pendingJob,
                runningJob,
                completedJob,
                oldRunningJob,
                oldCompletedJob
            ]
        ]
        numOfRemove << [0, 0, 0, 2, 2]
    }
}
