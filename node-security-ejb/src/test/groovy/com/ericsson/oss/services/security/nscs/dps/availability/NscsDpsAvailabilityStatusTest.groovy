/*------------------------------------------------------------------------------
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
package com.ericsson.oss.services.security.nscs.dps.availability;

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification

class NscsDpsAvailabilityStatusTest extends CdiSpecification {

    @ObjectUnderTest
    private NscsDpsAvailabilityStatus nscsDpsAvailabilityStatus

    def 'Given DPS available when reading availability status and unavailability start time then expected values are returned'() {
        given:
            nscsDpsAvailabilityStatus.setDpsAvailable(true)

        when:
            def isAvailable = nscsDpsAvailabilityStatus.isDpsAvailable()
            def unavailabilityStartTimeMs = nscsDpsAvailabilityStatus.getUnavailabilityStartTimeMs()

        then:
            isAvailable == true
         and:
            unavailabilityStartTimeMs == Long.MAX_VALUE
    }

    def 'Given DPS available when setting DPS available and reading availability status and unavailability start time then their values do not change'() {
        given:
            nscsDpsAvailabilityStatus.setDpsAvailable(true)
            def isAvailable = nscsDpsAvailabilityStatus.isDpsAvailable()
            def unavailabilityStartTimeMs = nscsDpsAvailabilityStatus.getUnavailabilityStartTimeMs()

        when:
            nscsDpsAvailabilityStatus.setDpsAvailable(true)
            def isAvailableAfter = nscsDpsAvailabilityStatus.isDpsAvailable()
            def unavailabilityStartTimeMsAfter = nscsDpsAvailabilityStatus.getUnavailabilityStartTimeMs()

        then:
            isAvailable == isAvailableAfter
         and:
            unavailabilityStartTimeMs == unavailabilityStartTimeMsAfter
    }

    def 'Given DPS available when setting DPS unavailable and reading availability status and unavailability start time then their values change to expected values'() {
        given:
            nscsDpsAvailabilityStatus.setDpsAvailable(true)
            def isAvailable = nscsDpsAvailabilityStatus.isDpsAvailable()
            def unavailabilityStartTimeMs = nscsDpsAvailabilityStatus.getUnavailabilityStartTimeMs()

        when:
            nscsDpsAvailabilityStatus.setDpsAvailable(false)
            def isAvailableAfter = nscsDpsAvailabilityStatus.isDpsAvailable()
            def unavailabilityStartTimeMsAfter = nscsDpsAvailabilityStatus.getUnavailabilityStartTimeMs()

        then:
            isAvailable != isAvailableAfter
            isAvailableAfter == false
         and:
            unavailabilityStartTimeMs != unavailabilityStartTimeMsAfter
            unavailabilityStartTimeMsAfter != null
            unavailabilityStartTimeMsAfter != Long.MAX_VALUE
    }

    def 'Given DPS unavailable when reading availability status and unavailability start time then expected values are returned'() {
        given:
            nscsDpsAvailabilityStatus.setDpsAvailable(false)

        when:
            def isAvailable = nscsDpsAvailabilityStatus.isDpsAvailable()
            def unavailabilityStartTimeMs = nscsDpsAvailabilityStatus.getUnavailabilityStartTimeMs()

        then:
            isAvailable == false
         and:
            unavailabilityStartTimeMs == null
    }

    def 'Given DPS unavailable when setting DPS unavailable and reading availability status and unavailability start time then their values do not change'() {
        given:
            nscsDpsAvailabilityStatus.setDpsAvailable(false)
            def isAvailable = nscsDpsAvailabilityStatus.isDpsAvailable()
            def unavailabilityStartTimeMs = nscsDpsAvailabilityStatus.getUnavailabilityStartTimeMs()

        when:
            nscsDpsAvailabilityStatus.setDpsAvailable(false)
            def isAvailableAfter = nscsDpsAvailabilityStatus.isDpsAvailable()
            def unavailabilityStartTimeMsAfter = nscsDpsAvailabilityStatus.getUnavailabilityStartTimeMs()

        then:
            isAvailable == isAvailableAfter
         and:
            unavailabilityStartTimeMs == unavailabilityStartTimeMsAfter
    }

    def 'Given DPS unavailable when setting DPS available and reading availability status and unavailability start time then their values change to expected values'() {
        given:
            nscsDpsAvailabilityStatus.setDpsAvailable(false)
            def isAvailable = nscsDpsAvailabilityStatus.isDpsAvailable()
            def unavailabilityStartTimeMs = nscsDpsAvailabilityStatus.getUnavailabilityStartTimeMs()

        when:
            nscsDpsAvailabilityStatus.setDpsAvailable(true)
            def isAvailableAfter = nscsDpsAvailabilityStatus.isDpsAvailable()
            def unavailabilityStartTimeMsAfter = nscsDpsAvailabilityStatus.getUnavailabilityStartTimeMs()

        then:
            isAvailable != isAvailableAfter
            isAvailableAfter == true
         and:
            unavailabilityStartTimeMs != unavailabilityStartTimeMsAfter
            unavailabilityStartTimeMsAfter == Long.MAX_VALUE
    }

}
