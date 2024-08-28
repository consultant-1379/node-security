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
package com.ericsson.oss.services.gdpr.anonymize

import com.ericsson.cds.cdi.support.rule.ImplementationClasses
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.gdpr.anonymize.exception.GdprAnonymizerException

class GdprAnonymizeTest extends CdiSpecification {

    static final int HASHED_LEN = 44

    @ObjectUnderTest
    private GdprAnonymizerDelegateImpl objectUnderTest

    @ImplementationClasses
    def myclass = [GdprAnonymizerImpl.class]

    def setup() {
        System.properties['configuration.java.properties']='src/test/resources/global.properties'
    }

    def "anonymize filename happy path"() {
        given:
        when: "anonymize valid filename in input"
            def hashedFileName = objectUnderTest.gdprBuildAnonymization("IMSI_IMEI_FILE")
        then: "hashed filename is not null"
            hashedFileName != null
        and:  "hashed filename has correct len (44 bytes)"
            hashedFileName.length() == HASHED_LEN
        and:  "hashed filename has correct encoded chars"
            gdprCheckHashedFileName(hashedFileName) == true
    }

    def "anonymize filename with input salt happy path"() {
        given:
        when: "anonymize valid filename in input with salt set by user"
            def hashedFileName = objectUnderTest.gdprBuildAnonymization("IMSI_IMEI_FILE", "enmapache")
        then: "hashed filename is not null"
            hashedFileName != null
        and:  "hashed filename has correct len (44 bytes)"
            hashedFileName.length() == HASHED_LEN
        and : "hashed filename has correct encoded chars"
            gdprCheckHashedFileName(hashedFileName) == true
    }

    def "anonymize with filename null"() {
        given:
        when: "anonymize null filename"
            objectUnderTest.gdprBuildAnonymization(null)
        then: "exception due to invalid input parameters is raised "
            GdprAnonymizerException e = thrown()
            e.message == GdprConstantsUtils.GDPR_INVALID_INPUT_PARMS
    }

    def "anonymize filename with global prop null or not well formatted"() {
        given: "global.properties file with tag web_host_default bad formatted "
            System.properties['configuration.java.properties']= a
        when: "anonymize valid filename in input"
            objectUnderTest.gdprBuildAnonymization("IMSI_IMEI_FILE")
        then: "exception due to invalid global.properties file is raised "
            GdprAnonymizerException e = thrown()
            e.message == b
        where:
            a           | b
            'src/test/resources/global.properties.filenotpresent' | GdprConstantsUtils.GDPR_GLB_PROP_ID_NOT_READ
            'src/test/resources/global.properties.urlkeynotpresent' | GdprConstantsUtils.GDPR_GLB_PROP_ID_NOT_READ
            'src/test/resources/global.properties.urlempty'  | GdprConstantsUtils.GDPR_GLB_PROP_ID_NOT_READ
            'src/test/resources/global.properties.urlbadformat' | GdprConstantsUtils.GDPR_GLB_PROP_ID_NOT_OK
            'src/test/resources/global.properties.urlbadformat1' | GdprConstantsUtils.GDPR_GLB_PROP_ID_NOT_OK
    }

    def "anonymize filename with input salt null"() {
        given:
        when: "anonymize valid filename but null salt in input"
            def hashedFileName = objectUnderTest.gdprBuildAnonymization("IMSI_IMEI_FILE", null)
        then: "exception due to invalid input parameters is raised "
            GdprAnonymizerException e = thrown()
            e.message == GdprConstantsUtils.GDPR_INVALID_INPUT_PARMS
    }

    def private gdprCheckHashedFileName ( def hashedFileName) {
        return (hashedFileName.matches("[a-zA-Z0-9]*") || hashedFileName.matches(".*[-=_].*"))
    }
}