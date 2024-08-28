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
package com.ericsson.nms.security.nscs.ldap.control;

import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.itpf.security.identitymgmtservices.comaa.ComAAInfo;
import com.ericsson.oss.itpf.security.identitymgmtservices.comaa.ConnectionData;

/**
 * Provides ConnectionData using ComAAInfo API
 * 
 * @author xsrirko
 *
 */
public class LdapConfigurationProxy {

    @EServiceRef
    private ComAAInfo comAAInfo;

    public ConnectionData getConnectionData() {
        return comAAInfo.getConnectionData();
    }

}
