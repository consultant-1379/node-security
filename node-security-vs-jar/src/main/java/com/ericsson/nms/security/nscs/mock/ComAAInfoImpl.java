/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.mock;

import javax.ejb.Stateless;

import com.ericsson.oss.itpf.security.identitymgmtservices.comaa.*;

@Stateless
public class ComAAInfoImpl implements ComAAInfo {

    private String IPV4ADDRESS = "192.168.100.2";
    private String IPV6ADDRESS = "2001:cdba:0:0:0:0:3257:9652";

    @Override
    public String getCOMAAFallbackIPAddress() {
        return IPV4ADDRESS;
    }

    @Override
    public String getCOMAAIpAddress() {
        return IPV4ADDRESS;
    }

    @Override
    public ConnectionData getConnectionData() {
        LdapAddress ipv4Address = new LdapAddress(IPV4ADDRESS, IPV4ADDRESS);
        LdapAddress ipv6Address = new LdapAddress(IPV6ADDRESS, IPV6ADDRESS);
        return new ConnectionData(ipv4Address, ipv6Address, 1389, 1636);
    }
}
