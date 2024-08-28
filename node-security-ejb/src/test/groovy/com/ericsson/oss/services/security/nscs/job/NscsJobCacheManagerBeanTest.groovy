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
import com.ericsson.nms.security.nscs.api.exception.NscsBadRequestException

import spock.lang.Unroll

class NscsJobCacheManagerBeanTest extends CdiSpecification {

    @ObjectUnderTest
    NscsJobCacheManagerBean nscsJobCacheManager

    @MockedImplementation
    NscsJobCacheService nscsJobCacheService

    def "object under test should not be null" () {
        expect:
        nscsJobCacheManager != null
    }

    def "get all jobs" () {
        given:
        when:
        nscsJobCacheManager.getAllJobs()
        then:
        1 * nscsJobCacheService.getAllJobs()
    }

    @Unroll
    def "get valid job list #ids" () {
        given:
        when:
        nscsJobCacheManager.getJobList(ids)
        then:
        1 * nscsJobCacheService.getJobList(_)
        and:
        notThrown(Exception)
        where:
        ids << [
            "11ed9ac1-49ce-40dc-bab0-3f347092da6c",
            "11ed9ac1-49ce-40dc-bab0-3f347092da6c,11ed9ac1-49ce-40dc-bab0-3f347092da6c"
        ]
    }

    @Unroll
    def "get invalid job list #ids" () {
        given:
        when:
        nscsJobCacheManager.getJobList(ids)
        then:
        0 * nscsJobCacheService.getJobList(_)
        and:
        thrown(NscsBadRequestException)
        where:
        ids << [
            null,
            "",
            "invalid-UUID",
            "11ed9ac1-49ce-40dc-bab0-3f347092da6c;11ed9ac1-49ce-40dc-bab0-3f347092da6c"
        ]
    }

    def "abort valid job" () {
        given:
        when:
        nscsJobCacheManager.abortJob("11ed9ac1-49ce-40dc-bab0-3f347092da6c")
        then:
        1 * nscsJobCacheService.abortJob(_)
        and:
        notThrown(Exception)
    }

    @Unroll
    def "abort invalid job #id" () {
        given:
        when:
        nscsJobCacheManager.abortJob(id)
        then:
        0 * nscsJobCacheService.abortJob(_)
        and:
        thrown(NscsBadRequestException)
        where:
        id << [
            null,
            "",
            "invalid-UUID",
            "11ed9ac1-49ce-40dc-bab0-3f347092da6c,11ed9ac1-49ce-40dc-bab0-3f347092da6c"
        ]
    }
}
