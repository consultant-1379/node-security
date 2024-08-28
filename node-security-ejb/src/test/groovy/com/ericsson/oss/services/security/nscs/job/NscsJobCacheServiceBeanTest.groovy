/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2022
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.job

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler

import spock.lang.Shared
import spock.lang.Unroll

class NscsJobCacheServiceBeanTest extends CdiSpecification {

    @ObjectUnderTest
    NscsJobCacheServiceBean nscsJobCacheService

    @MockedImplementation
    NscsJobCacheHandler nscsJobCacheHandler

    @Shared
    UUID uuid = UUID.fromString("11ed9ac1-49ce-40dc-bab0-3f347092da6c")

    def "object under test should not be null" () {
        expect:
        nscsJobCacheService != null
    }

    def "get all jobs" () {
        given:
        when:
        nscsJobCacheService.getAllJobs()
        then:
        1 * nscsJobCacheHandler.getAllJobs()
    }

    @Unroll
    def "get job list #uuids" () {
        given:
        when:
        nscsJobCacheService.getJobList(uuids)
        then:
        1 * nscsJobCacheHandler.getJobList(_)
        and:
        notThrown(Exception)
        where:
        uuids << [
            null,
            [],
            [uuid],
            [uuid, uuid]
        ]
    }

    def "abort job" () {
        given:
        when:
        nscsJobCacheService.abortJob(uuid)
        then:
        1 * nscsJobCacheHandler.abortJob(uuid)
    }
}
