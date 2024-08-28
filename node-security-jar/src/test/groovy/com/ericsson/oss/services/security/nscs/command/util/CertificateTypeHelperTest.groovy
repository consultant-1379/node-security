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
package com.ericsson.oss.services.security.nscs.command.util;

import com.ericsson.cds.cdi.support.spock.CdiSpecification

class CertificateTypeHelperTest extends CdiSpecification {

    def 'When getting valid certificate types then the expected values should be retrieved'() {
        given:
            def expectedValidCertificateTypes = [ "OAM", "IPSEC" ]

        when:
            def validCertificateTypes = CertificateTypeHelper.getValidCertificateTypes()

        then:
            validCertificateTypes.size() == expectedValidCertificateTypes.size()
            validCertificateTypes.containsAll(expectedValidCertificateTypes)
    }

    def 'When checking if null certificate type is valid then an exception is thrown'() {
        given:

        when:
            CertificateTypeHelper.isCertificateTypeValid(null)

        then:
            thrown IllegalArgumentException
    }

    def 'When checking if certificateType is valid then the expectedResult is returned'() {
        given:
            def result

        when:
            result = CertificateTypeHelper.isCertificateTypeValid(certificateType)

        then:
            result == expectedResult

        where:
            certificateType | expectedResult
            "IPSEC"         | true
            "OAM"           | true
            ""              | false
            "oam"           | false
            "ipsec"         | false
            "OEM"           | false
    }

}
