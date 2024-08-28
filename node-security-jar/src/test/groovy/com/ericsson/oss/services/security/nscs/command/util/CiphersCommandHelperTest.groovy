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

class CiphersCommandHelperTest extends CdiSpecification {

    def 'When getting the cipher MO names then the expected values should be retrieved'() {
        given:
            def expectedSshCipherMoName = "Ssh"
            def expectedTlsCipherMoName = "Tls"

        when:
            def cipherMoNames = CiphersCommandHelper.getCipherMoNames()

        then:
            assert cipherMoNames.get("SSH/SFTP") == expectedSshCipherMoName
            assert cipherMoNames.get("SSL/HTTPS/TLS") == expectedTlsCipherMoName
    }

}
