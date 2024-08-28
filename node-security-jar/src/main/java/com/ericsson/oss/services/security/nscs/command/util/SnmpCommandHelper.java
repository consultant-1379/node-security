/*-----------------------------------------------------------------------------
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

import java.util.Arrays;
import java.util.List;

import com.ericsson.nms.security.nscs.api.command.types.SnmpAuthnopriv;
import com.ericsson.nms.security.nscs.api.command.types.SnmpAuthpriv;

/**
 * Auxiliary class to manage 'secadm snmp' commands.
 *
 * @author emaborz
 *
 */
public class SnmpCommandHelper {
    static final List<String> expectedSnmpAuthprivParams = Arrays.asList(SnmpAuthpriv.AUTH_ALGO_PARAM, SnmpAuthpriv.AUTH_PWD_PARAM,
            SnmpAuthpriv.PRIV_ALGO_PARAM, SnmpAuthpriv.PRIV_PWD_PARAM);
    static final List<String> expectedSnmpAuthnoprivParams = Arrays.asList(SnmpAuthnopriv.AUTH_ALGO_PARAM, SnmpAuthnopriv.AUTH_PWD_PARAM);
    static final List<String> expectedSnmpGetAuthParams = Arrays.asList("snmpAuthKey", "snmpPrivKey", "snmpAuthProtocol", "snmpPrivProtocol");

    public static List<String> getExpectedSnmpAuthprivParams() {
        return expectedSnmpAuthprivParams;
    }

    public static List<String> getExpectedSnmpAuthnoprivParams() {
        return expectedSnmpAuthnoprivParams;
    }

    public static List<String> getExpectedSnmpGetAuthParams() {
        return expectedSnmpGetAuthParams;
    }

}
