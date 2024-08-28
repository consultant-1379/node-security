/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.utilities

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.data.ModelDefinition

import spock.lang.Unroll

class CbpOiMoNamingTest extends CdiSpecification {

    def "constructor should not be invoked for utility class containing only static methods" () {
        given:
        when:
        def CbpOiMoNaming cbpOiMoNaming = new CbpOiMoNaming()
        then:
        thrown(IllegalStateException)
    }

    @Unroll
    def "get MO name"() {
        given:
        when:
        def moName = CbpOiMoNaming.getName(moType)
        then:
        notThrown(Exception)
        and:
        moName == expected
        where:
        moType                                       || expected
        ModelDefinition.SYSTEM_TYPE                  || "1"
        ModelDefinition.LDAP_TYPE                    || "1"
        ModelDefinition.CBP_OI_SECURITY_TYPE         || "1"
        ModelDefinition.CBP_OI_TLS_TYPE              || "1"
        ModelDefinition.SIMPLE_AUTHENTICATED_TYPE    || "1"
        ModelDefinition.TCP_TYPE                     || "1"
        ModelDefinition.LDAPS_TYPE                   || "1"
        ModelDefinition.KEYSTORE_TYPE                || "1"
        ModelDefinition.CMP_TYPE                     || "1"
        ModelDefinition.CERTIFICATE_AUTHORITIES_TYPE || "1"
        ModelDefinition.CMP_SERVER_GROUPS_TYPE       || "1"
        ModelDefinition.CMP_SERVER_TYPE              || "1"
        ModelDefinition.ASYMMETRIC_KEYS_TYPE         || "1"
    }

    @Unroll
    def "get MO name with invalid MO type"() {
        given:
        when:
        def moName = CbpOiMoNaming.getName(moType)
        then:
        thrown(expected)
        where:
        moType                                || expected
        ModelDefinition.CMP_SERVER_GROUP_TYPE || IllegalArgumentException
        null                                  || IllegalArgumentException
        ""                                    || IllegalArgumentException
        'system$$ldap'                        || IllegalArgumentException
        'tcp$$ldap'                           || IllegalArgumentException
        'keystore$$cmp'                       || IllegalArgumentException
        'asymmetric-keys$$cmp'                || IllegalArgumentException
        'asymmetric-key$$cmp'                 || IllegalArgumentException
    }

    @Unroll
    def "get MO name by certificate type"() {
        given:
        when:
        def moName = CbpOiMoNaming.getNameByCertificateType(moType, certificateType)
        then:
        notThrown(Exception)
        and:
        moName == expected
        where:
        moType                                | certificateType || expected
        ModelDefinition.CMP_SERVER_GROUP_TYPE | "OAM"           || "1"
    }

    @Unroll
    def "get MO name by certificate type with unsupported certificate type"() {
        given:
        when:
        def moName = CbpOiMoNaming.getNameByCertificateType(moType, certificateType)
        then:
        notThrown(Exception)
        and:
        moName == expected
        where:
        moType                                | certificateType || expected
        ModelDefinition.CMP_SERVER_GROUP_TYPE | ""              || null
        ModelDefinition.CMP_SERVER_GROUP_TYPE | "IPSEC"         || null
        ModelDefinition.CMP_SERVER_GROUP_TYPE | "oam"           || null
    }

    @Unroll
    def "get MO name by certificate type with invalid params"() {
        given:
        when:
        def moName = CbpOiMoNaming.getNameByCertificateType(moType, certificateType)
        then:
        thrown(expected)
        where:
        moType                                 | certificateType || expected
        ModelDefinition.CMP_SERVER_GROUPS_TYPE | "OAM"           || IllegalArgumentException
        null                                   | "OAM"           || IllegalArgumentException
        ""                                     | "OAM"           || IllegalArgumentException
        ModelDefinition.CMP_SERVER_GROUP_TYPE  | null            || IllegalArgumentException
    }

    @Unroll
    def "get server MO name by primary flag #isprimary" () {
        given:
        when:
        def serverName = CbpOiMoNaming.getServerName(isprimary)
        then:
        serverName == expected
        where:
        isprimary << [true, false]
        expected << ["primary", "fallback"]
    }
}
