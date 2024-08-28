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
package com.ericsson.oss.services.security.nscs.classloading

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification

class NscsClassLoadingBeanTest extends CdiSpecification {

    @ObjectUnderTest
    NscsClassLoadingBean nscsClassLoadingBean

    def "get existent class package version "() {
        given: "NSCS Class Loading returning package version"
        String fullclassname = 'com.ericsson.oss.services.security.nscs.classloading.NscsClassLoadingBean'
        String packver = 'Specification version: null\nImplementation version: null\n'
        when: "getting the package version"
        String packageVersion = nscsClassLoadingBean.getPackageVersion(fullclassname)
        then: "result should not be null"
        packageVersion != null
        and: "result should be the expected one"
        packageVersion == packver
    }

    def "get null class package version "() {
        given: "NSCS Class Loading returning package version"
        String fullclassname = null
        String packver = 'Exception: NullPointerException Caused by: null'
        when: "getting the package version"
        String packageVersion = nscsClassLoadingBean.getPackageVersion(fullclassname)
        then: "result should not be null"
        packageVersion != null
        and: "result should be the expected one"
        packageVersion == packver
    }
}
