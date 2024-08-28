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
import com.ericsson.nms.security.nscs.api.command.types.SnmpAuthnopriv
import com.ericsson.nms.security.nscs.api.command.types.SnmpAuthpriv

class SnmpCommandHelperTest extends CdiSpecification {

    def 'When getting the snmp authpriv expected parameters then the expected values should be retrieved'() {
        given:
            def expectedSnmpAuthprivParams = [ "auth_algo", "auth_password", "priv_algo", "priv_password" ]

        when:
            def snmpAuthprivParams = SnmpCommandHelper.getExpectedSnmpAuthprivParams()

        then:
            snmpAuthprivParams.size() == expectedSnmpAuthprivParams.size()
            snmpAuthprivParams.containsAll(expectedSnmpAuthprivParams)
            expectedSnmpAuthprivParams.containsAll(snmpAuthprivParams)
    }

    def 'When getting the snmp authnopriv expected parameters then the expected values should be retrieved'() {
        given:
            def expectedSnmpAuthnoprivParams = [ "auth_algo", "auth_password" ]

        when:
            def snmpAuthnoprivParams = SnmpCommandHelper.getExpectedSnmpAuthnoprivParams()

        then:
            snmpAuthnoprivParams.size() == expectedSnmpAuthnoprivParams.size()
            snmpAuthnoprivParams.containsAll(expectedSnmpAuthnoprivParams)
            expectedSnmpAuthnoprivParams.containsAll(snmpAuthnoprivParams)
    }

    def 'When getting the get snmp expected parameters then the expected values should be retrieved'() {
        given:
            def expectedGetSnmpParams = [ "snmpAuthKey", "snmpPrivKey", "snmpAuthProtocol", "snmpPrivProtocol" ]

        when:
            def getSnmpParams = SnmpCommandHelper.getExpectedSnmpGetAuthParams()

        then:
            getSnmpParams.size() == expectedGetSnmpParams.size()
            getSnmpParams.containsAll(expectedGetSnmpParams)
            expectedGetSnmpParams.containsAll(getSnmpParams)
    }

}
