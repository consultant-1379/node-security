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
package com.ericsson.oss.services.nscs.cache.rest

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.services.security.nscs.job.NscsJobCacheManager

class JobCacheServiceRestTest extends CdiSpecification {

    @ObjectUnderTest
    JobCacheServiceRest jobCacheServiceRest

    @MockedImplementation
    NscsJobCacheManager nscsJobCacheManager

    def "get all jobs"() {
        given:
        when:
        jobCacheServiceRest.getAllJobs()
        then:
        1 * nscsJobCacheManager.getAllJobs()
    }

    def "get job list"() {
        given:
        def ids = "11ed9ac1-49ce-40dc-bab0-3f347092da6c"
        when:
        jobCacheServiceRest.getJobList(ids)
        then:
        1 * nscsJobCacheManager.getJobList(ids)
    }

    def "abort job"() {
        given:
        def id = "11ed9ac1-49ce-40dc-bab0-3f347092da6c"
        when:
        jobCacheServiceRest.abortJob(id)
        then:
        1 * nscsJobCacheManager.abortJob(id)
    }
}
