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
package com.ericsson.oss.services.security.nscs.util

import com.ericsson.cds.cdi.support.spock.CdiSpecification

import spock.lang.Unroll

class NscsPacketVersionUtilTest extends CdiSpecification {

    @Unroll
    def "dump packet version of fullclassname #fullclassname" () {
        given:
        when:
        NscsPacketVersionUtil.dumpPacketVersion(fullclassname)
        then:
        noExceptionThrown()
        where:
        fullclassname << [
            "org.bouncycastle.crypto.engines.AESEngine",
            "org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator",
            "com.ericsson.oss.services.security.nscs.util.NscsPacketVersionUtil",
            "com.acme.notexisting.NotExistingClass"
        ]
    }
}
