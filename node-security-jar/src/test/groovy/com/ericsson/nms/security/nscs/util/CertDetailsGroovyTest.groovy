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
package com.ericsson.nms.security.nscs.util

import com.ericsson.cds.cdi.support.spock.CdiSpecification

import spock.lang.Unroll

class CertDetailsGroovyTest extends CdiSpecification {

    def "get BouncyCastle X500 name for null DN"() {
        given:
        when: "getting BouncyCastle X500 name of null DN"
        def bcX500Name = CertDetails.getBcX500Name(null)
        then: "BouncyCastle X500 name should equal null"
        bcX500Name == null
    }

    @Unroll
    def "get BouncyCastle X500 name for invalid DN"() {
        given:
        when: "getting BouncyCastle X500 name of DN #dn"
        def bcX500Name = CertDetails.getBcX500Name(dn)
        then: "BouncyCastle X500 name should equal #expected"
        bcX500Name == expected
        where:
        dn              | expected
        null            | null
        ""              | null
        "CN"            | null
        "="             | null
        "=myAttrValue"  | null
        ","             | null
        "CN=,"          | null
    }

    @Unroll
    def "get BouncyCastle X500 name for DN with comma and whitespace in attribute value"() {
        given:
        when: "getting BouncyCastle X500 name of DN #dn"
        def bcX500Name = CertDetails.getBcX500Name(dn)
        then: "BouncyCastle X500 name should equal #expected"
        bcX500Name == expected
        where:
        dn                                                  | expected
        "OU=RANI, O=KI, C=SE, CN=NE_OAM_CA"                 | "OU=RANI,O=KI,C=SE,CN=NE_OAM_CA"
        "CN=NE_OAM_CA,O=ERICSSON"                           | "CN=NE_OAM_CA,O=ERICSSON"
        "CN=NE OAM CA,O=ERICSSON"                           | "CN=NE OAM CA,O=ERICSSON"
        "  CN  =  NE OAM CA  ,  O  =  ERICSSON  "           | "CN=NE OAM CA,O=ERICSSON"
        "  CN  =  NE OAM CA  ,  O  =  ERICSSON\\, Inc.  "   | "CN=NE OAM CA,O=ERICSSON\\, Inc."
        "  CN  =  NE OAM CA\\  ,  O  =  ERICSSON\\, Inc.  " | "CN=NE OAM CA\\ ,O=ERICSSON\\, Inc."
    }

    @Unroll
    def "get BouncyCastle X500 name for attribute name case insensitiveness"() {
        given:
        when: "getting BouncyCastle X500 name of DN #dn"
        def bcX500Name = CertDetails.getBcX500Name(dn)
        then: "BouncyCastle X500 name should equal #expected"
        bcX500Name == expected
        where:
        dn                | expected
        "CN=myCommonName" | "CN=myCommonName"
        "cn=myCommonName" | "CN=myCommonName"
        "cN=myCommonName" | "CN=myCommonName"
        "Cn=myCommonName" | "CN=myCommonName"
    }

    @Unroll
    def "get BouncyCastle X500 name with empty or containg only escaped char attribute value"() {
        given:
        when: "getting BouncyCastle X500 name of DN #dn"
        def bcX500Name = CertDetails.getBcX500Name(dn)
        then: "BouncyCastle X500 name should equal #expected"
        bcX500Name == expected
        where:
        dn           | expected
        "CN="        | "CN="
        "CN= "       | "CN="
        "CN=\\ "     | "CN=\\\\ "
        "CN=\\,"     | "CN=\\,"
        "CN=\\,\\ "  | "CN=\\,\\ "
        "CN=\\, \\ " | "CN=\\,\\ \\ "
        "CN=\\  \\ " | "CN=\\ \\ \\\\ "
    }

    @Unroll
    def "get BouncyCastle X500 name with common name"() {
        given:
        when: "getting BouncyCastle X500 name of DN #dn"
        def bcX500Name = CertDetails.getBcX500Name(dn)
        then: "BouncyCastle X500 name should equal #expected"
        bcX500Name == expected
        where:
        dn                | expected
        "CN=myCommonName" | "CN=myCommonName"
    }

    @Unroll
    def "get BouncyCastle X500 name with country, location, state, street"() {
        given:
        when: "getting BouncyCastle X500 name of DN #dn"
        def bcX500Name = CertDetails.getBcX500Name(dn)
        then: "BouncyCastle X500 name should equal #expected"
        bcX500Name == expected
        where:
        dn                | expected
        "C=myCountry"     | "C=myCountry"
        "L=myLocation"    | "L=myLocation"
        "ST=myState"      | "ST=myState"
        "STREET=myStreet" | "STREET=myStreet"
    }

    @Unroll
    def "get BouncyCastle X500 name with organization, organizational unit"() {
        given:
        when: "getting BouncyCastle X500 name of DN #dn"
        def bcX500Name = CertDetails.getBcX500Name(dn)
        then: "BouncyCastle X500 name should equal #expected"
        bcX500Name == expected
        where:
        dn             | expected
        "OU=myOrgUnit" | "OU=myOrgUnit"
        "O=myOrg"      | "O=myOrg"
    }

    @Unroll
    def "get BouncyCastle X500 name with DN qualifier"() {
        given:
        when: "getting BouncyCastle X500 name of DN #dn"
        def bcX500Name = CertDetails.getBcX500Name(dn)
        then: "BouncyCastle X500 name should equal #expected"
        bcX500Name == expected
        where:
        dn                          | expected
        "DN=myDnQualifier"          | "DN=myDnQualifier"
        "DNQ=myDnQualifier"         | null
        "DNQUALIFIER=myDnQualifier" | null
    }

    @Unroll
    def "get BouncyCastle X500 name with title"() {
        given:
        when: "getting BouncyCastle X500 name of DN #dn"
        def bcX500Name = CertDetails.getBcX500Name(dn)
        then: "BouncyCastle X500 name should equal #expected"
        bcX500Name == expected
        where:
        dn              | expected
        "T=myTitle"     | "T=myTitle"
        "TITLE=myTitle" | null
    }

    @Unroll
    def "get BouncyCastle X500 name with given name"() {
        given:
        when: "getting BouncyCastle X500 name of DN #dn"
        def bcX500Name = CertDetails.getBcX500Name(dn)
        then: "BouncyCastle X500 name should equal #expected"
        bcX500Name == expected
        where:
        dn                      | expected
        "GN=myGivenName"        | null
        "GIVENNAME=myGivenName" | "GIVENNAME=myGivenName"
    }

    @Unroll
    def "get BouncyCastle X500 name with serial number"() {
        given:
        when: "getting BouncyCastle X500 name of DN #dn"
        def bcX500Name = CertDetails.getBcX500Name(dn)
        then: "BouncyCastle X500 name should equal #expected"
        bcX500Name == expected
        where:
        dn                            | expected
        "SERIALNUMBER=mySerialNumber" | "SERIALNUMBER=mySerialNumber"
        "serialNumber=mySerialNumber" | "SERIALNUMBER=mySerialNumber"
    }

    @Unroll
    def "get BouncyCastle X500 name with surname"() {
        given:
        when: "getting BouncyCastle X500 name of DN #dn"
        def bcX500Name = CertDetails.getBcX500Name(dn)
        then: "BouncyCastle X500 name should equal #expected"
        bcX500Name == expected
        where:
        dn                  | expected
        "SN=mySurname"      | "SURNAME=mySurname"
        "SURNAME=mySurname" | "SURNAME=mySurname"
    }

    @Unroll
    def "get BouncyCastle X500 name with domain component"() {
        given:
        when: "getting BouncyCastle X500 name of DN #dn"
        def bcX500Name = CertDetails.getBcX500Name(dn)
        then: "BouncyCastle X500 name should equal #expected"
        bcX500Name == expected
        where:
        dn                                  | expected
        "DC=myDomainComponent"              | "DC=myDomainComponent"
        "domainComponent=myDomainComponent" | null
    }
}
