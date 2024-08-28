/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.nscs.extidp.service.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.handler.command.utility.PasswordHelper;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.annotation.Authorize;
import com.ericsson.oss.services.nscs.extidp.service.ExtIdpService;

@Stateless
public class ExtIdpServiceBean implements ExtIdpService {

    @Inject
    private PasswordHelper passwordHelper;

    @Override
    @Authorize(resource = "ext_idp_bind", action = "execute")
    public String bind(final String value) {
        return passwordHelper.encryptEncode(value);
    }

}
