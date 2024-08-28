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
package com.ericsson.nms.security.nscs.integration.jee.test.gim;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.handler.command.utility.PasswordHelper;
import com.ericsson.oss.services.security.genericidentitymgmtserviceapi.ecimuser.EcimUserRemote;

@Stateless
public class TestEcimUserRemote implements EcimUserRemote {

    @Inject
    private PasswordHelper passwordHelper;

    @Override
    public void changeECIMUserPassword(final String arg0, final String arg1) {
    }

    @Override
    public boolean checkECIMUserExist() {
        return true;
    }

    @Override
    public boolean checkIfECIMUserIsSynchronizedToLdap() {
        return true;
    }

    @Override
    public void createECIMUser(final String arg0, final String arg1) {
    }

    @Override
    public void deleteECIMUser() {
    }

    @Override
    public String generateECIMUserPassword(final String arg0) {
        return "thisisthegeneratedpassword";
    }

    @Override
    public String getEcimUserCryptoPassword() {
        return passwordHelper.encryptEncode("thisistheecimuserpassword");
    }

    @Override
    public boolean setEcimUserCryptoPassword(final String arg0) {
        return true;
    }

}
