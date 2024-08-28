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

package com.ericsson.oss.services.nscs.ldap;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.ldap.EcimPwdFileManager;
import com.ericsson.nms.security.nscs.handler.command.utility.PasswordHelper;
import com.ericsson.oss.itpf.sdk.resources.Resource;
import com.ericsson.oss.itpf.sdk.resources.Resources;

@Stateless
public class EcimPwdFileManagerBean implements EcimPwdFileManager {
    //This constant is for back-compatibility only.
    private static final String SECSERV_COMMON_DIR = "/ericsson/tor/data/secserv";

    @Inject
    PasswordHelper passwordHelper;

    @Inject
    Logger logger;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String readPassword(final String filename) {
        final Resource resource = Resources.getFileSystemResource(SECSERV_COMMON_DIR + "/" + filename);
        logger.debug("Reading password from resource : " + resource.getName());
        if (resource.exists()) {
            return passwordHelper.decryptDecode(resource.getAsText());
        } else {
            return null;
        }
    }

}

