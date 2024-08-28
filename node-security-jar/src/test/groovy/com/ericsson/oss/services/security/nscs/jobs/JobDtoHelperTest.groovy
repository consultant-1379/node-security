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
package com.ericsson.oss.services.security.nscs.jobs

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.exception.NscsBadRequestException

import spock.lang.Shared
import spock.lang.Unroll

class JobDtoHelperTest extends CdiSpecification {

    @Shared
    UUID uuid = UUID.fromString("11ed9ac1-49ce-40dc-bab0-3f347092da6c")

    def "convert null job ID list" () {
        given:
        when:
        JobDtoHelper.fromUuidListDto(null)
        then:
        thrown(NscsBadRequestException)
    }

    @Unroll
    def "convert valid job ID list #ids" () {
        given:
        when:
        def uuids = JobDtoHelper.fromUuidListDto(ids)
        then:
        uuids == expectedUuids
        and:
        notThrown(Exception)
        where:
        ids << [
            "11ed9ac1-49ce-40dc-bab0-3f347092da6c",
            "11ed9ac1-49ce-40dc-bab0-3f347092da6c,11ed9ac1-49ce-40dc-bab0-3f347092da6c"
        ]
        expectedUuids << [[uuid], [uuid, uuid]]
    }

    @Unroll
    def "convert invalid job ID list #ids" () {
        given:
        when:
        JobDtoHelper.fromUuidListDto(ids)
        then:
        thrown(NscsBadRequestException)
        where:
        ids << [
            null,
            "",
            "invalid-UUID",
            "11ed9ac1-49ce-40dc-bab0-3f347092da6c;11ed9ac1-49ce-40dc-bab0-3f347092da6c"
        ]
    }

    @Unroll
    def "convert valid job ID #id" () {
        given:
        when:
        def uuid = JobDtoHelper.fromUuidDto(id)
        then:
        uuid == expectedUuid
        and:
        notThrown(Exception)
        where:
        id << [
            "11ed9ac1-49ce-40dc-bab0-3f347092da6c"
        ]
        expectedUuid << [uuid]
    }

    @Unroll
    def "convert invalid job ID #id" () {
        given:
        when:
        JobDtoHelper.fromUuidDto(id)
        then:
        thrown(NscsBadRequestException)
        where:
        id << [
            null,
            "",
            "invalid-UUID"
        ]
    }
}
