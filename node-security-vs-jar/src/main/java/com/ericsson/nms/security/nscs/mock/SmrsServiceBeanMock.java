/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.mock;

import java.io.File;

import javax.ejb.Stateless;

import com.ericsson.oss.itpf.smrs.SmrsAccount;
import com.ericsson.oss.itpf.smrs.SmrsAddressRequest;
import com.ericsson.oss.itpf.smrs.SmrsService;

/**
 * This class is a mocked implementation of SmrsService used in Arquillian tests.
 */
@Stateless
public class SmrsServiceBeanMock implements SmrsService {

    final static private String smrsRootDir = "target/resources/smrs/";
    final static private String host = "localhost";

    @Override
    public SmrsAccount getCommonAccount(String accountType , String neType) {
        final SmrsAccount smrsAccount = new SmrsAccount("mm-cert-arquillian", smrsRootDir);
        smrsAccount.setAccountType(accountType);
        smrsAccount.setNeType(neType);
        smrsAccount.setPassword("secret");
        smrsAccount.setSmrsRootDirectory(smrsRootDir);
        final String relativePath = "smrsroot/" + accountType.toLowerCase() + "/" + neType.toLowerCase() + "/";
        smrsAccount.setRelativePath(relativePath);
        final String homeDir = smrsRootDir + relativePath;
        smrsAccount.setHomeDirectory(homeDir);
        final File smrsLocalDir = new File(homeDir);
        if (!smrsLocalDir.exists())
            smrsLocalDir.mkdirs();
        return smrsAccount;
    }

    @Override
    public SmrsAccount getNodeSpecificAccount(String accountType, String neType, String neName) {
        final SmrsAccount smrsAccount = new SmrsAccount("mm-cert-arquillian", smrsRootDir);
        smrsAccount.setAccountType(accountType);
        smrsAccount.setNeType(neType);
        smrsAccount.setNeName(neName);
        smrsAccount.setPassword("secret");
        smrsAccount.setSmrsRootDirectory(smrsRootDir);
        final String relativePath = "smrsroot/" + accountType.toLowerCase() + "/" + neType.toLowerCase() + "/" + neName + "/";
        smrsAccount.setRelativePath(relativePath);
        final String homeDir = smrsRootDir + relativePath;
        smrsAccount.setHomeDirectory(homeDir);
        final File smrsLocalDir = new File(homeDir);
        if (!smrsLocalDir.exists())
            smrsLocalDir.mkdirs();
        return smrsAccount;
    }

    @Override
    public String getNetworkTypeDirectory(String networkType) {
        return smrsRootDir + "smrsroot/" + networkType.toLowerCase();
    }

    @Override
    public String getFileServerAddress(SmrsAddressRequest addressRequest) {
        return host;
    }

    @Override
    public boolean deleteSmrsAccount(SmrsAccount smrsAccount) {
        final String accountType = smrsAccount.getAccountType();
        final String neType = smrsAccount.getNeType();
        final String neName = smrsAccount.getNeName();
        String relativePath = "smrsroot/" + accountType.toLowerCase() + "/" + neType.toLowerCase() + "/";
        String homeDir = null;
        if (neName != null) {
            homeDir = smrsRootDir + relativePath + neName + "/";
        } else {
            homeDir = smrsRootDir + relativePath;
        }
        final File smrsLocalDir = new File(homeDir);
        if (smrsLocalDir.exists())
            return smrsLocalDir.delete();
        return false;
    }

    @Override
    public Integer getFileServerPort(final SmrsAddressRequest addressRequest) {
        return 0;
    }
}
