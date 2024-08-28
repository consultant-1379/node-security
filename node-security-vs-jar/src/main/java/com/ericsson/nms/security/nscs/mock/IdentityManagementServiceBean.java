package com.ericsson.nms.security.nscs.mock;

import java.util.List;

import javax.ejb.Stateless;

import com.ericsson.oss.itpf.security.identitymgmtservices.IdentityManagementService;
import com.ericsson.oss.itpf.security.identitymgmtservices.IdentityManagementServiceException;
import com.ericsson.oss.itpf.security.identitymgmtservices.M2MUser;
import com.ericsson.oss.itpf.security.identitymgmtservices.M2MUserPassword;
import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountData;
import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountDetails;
import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountGetData;
import com.ericsson.oss.itpf.security.identitymgmtservices.enums.ProxyAgentAccountAdminStatus;

/**
 * MOCK CLASS FOR IdentityManagementService. This class facilitates Arquillian
 * testing of SMRS by automatically returning true for
 * {@code IdentityManagementServiceBean.isExistingM2MUser()} Update this class
 * if further methods need to be mocked User: ebrigun Date: 21/05/14 Time: 11:34
 */
@Stateless
public class IdentityManagementServiceBean implements IdentityManagementService {

    @Override
    public M2MUser createM2MUser(String userName, String groupName, String homeDir, int validDays) throws IdentityManagementServiceException {
        return null;
    }

    @Override
    public M2MUserPassword createM2MUserPassword(String string, String string1, String string2, int i) {
        return null;
    }

    @Override
    public boolean deleteM2MUser(String userName) throws IdentityManagementServiceException {
        return false;
    }

    @Override
    public M2MUser getM2MUser(String s) throws IdentityManagementServiceException {
        return null;
    }

    @Override
    public boolean isExistingM2MUser(String s) throws IdentityManagementServiceException {
        return true;
    }

    @Override
    public char[] getM2MPassword(String s) throws IdentityManagementServiceException {
        return "MockPwd1@3".toCharArray();
    }

    @Override
    public char[] updateM2MPassword(String s) throws IdentityManagementServiceException {
        return new char[0];
    }

    @Override
    public List<String> getAllTargetGroups() throws IdentityManagementServiceException {
        return null;
    }

    @Override
    public String getDefaultTargetGroup() throws IdentityManagementServiceException {
        return null;
    }

    @Override
    public List<String> validateTargetGroups(List<String> targetGroups) throws IdentityManagementServiceException {
        return null;
    }

    @Override
    public ProxyAgentAccountData createProxyAgentAccount() throws IdentityManagementServiceException {

        ProxyAgentAccountData proxyAgentAccountData = new ProxyAgentAccountData("cn=ProxyAccount_8,ou=Profiles,dc=apache,dc=com,baseDn=uid=ssouser,ou=people,dc=apache,dc=com", "sfade11fs");
        return proxyAgentAccountData;
    }

    @Override
    public boolean deleteProxyAgentAccount(String arg0) throws IdentityManagementServiceException {
        return true;
    }

    @Override
    public ProxyAgentAccountGetData getProxyAgentAccount(Boolean arg0, Boolean arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ProxyAgentAccountGetData getProxyAgentAccountByAdminStatus(ProxyAgentAccountAdminStatus arg0, Boolean arg1, Boolean arg2) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ProxyAgentAccountGetData getProxyAgentAccountByInactivityPeriod(Long arg0, Boolean arg1, Boolean arg2) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ProxyAgentAccountDetails getProxyAgentAccountDetails(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Boolean updateProxyAgentAccountAdminStatus(String arg0, ProxyAgentAccountAdminStatus arg1) {
        // TODO Auto-generated method stub
        return null;
    }
}
