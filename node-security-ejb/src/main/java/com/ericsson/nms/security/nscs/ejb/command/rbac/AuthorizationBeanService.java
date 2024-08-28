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
package com.ericsson.nms.security.nscs.ejb.command.rbac;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.oss.itpf.sdk.security.accesscontrol.EPredefinedRole;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.annotation.Authorize;

@Stateless
public class AuthorizationBeanService {

    private static final String READ = "read";
    private static final String CREATE = "create";
    private static final String UPDATE = "update";
    private static final String EXECUTE = "execute";
    private static final String DELETE = "delete";
    private static final String PATCH = "patch";

    private static final String HTTPS_RESOURCE = "https";
    private static final String CREDENTIALS_RESOURCE = "credentials";
    private static final String CREDENTIALS_PLAIN_TEXT_RESOURCE = "credentials_plain_text";
    private static final String SSHKEY_RESOURCE = "sshkey";
    private static final String IPSEC_RESOURCE = "ipsec";
    private static final String OAM_RESOURCE = "oam";
    private static final String LDAP_RESOURCE = "ldap";
    private static final String LDAP_PROXY_RESOURCE = "nodesec_proxy";
    private static final String SNMPV3_RESOURCE = "snmpv3";
    private static final String SNMPV3_PLAIN_TEXT_RESOURCE = "snmpv3_plain_text";
    private static final String CRLCHECK_RESOURCE = "crlcheck";
    private static final String ON_DEMAND_CRL_DOWNLOAD_RESOURCE = "on_demand_crl_download";
    private static final String SECURITY_ENROLLMENT_DOWNLOAD_RESOURCE = "security_enrollment_download";
    private static final String CIPHERS_RESOURCE = "ciphers";
    private static final String RTSEL_RESOURCE = "rtsel";
    private static final String FTPES_RESOURCE = "ftpes";
    private static final String SSO_RESOURCE = "sso";
    private static final String NODE_SPECIFIC_RESOURCE = "ftp";
    private static final String CAPABILITY_RESOURCE = "capability";
    private static final String LAAD_RESOURCE = "laad";
    private static final String NTP_RESOURCE = "ntp";
    private static final String TEST_RESOURCE = "nodesec_test";

    @Inject
    private Logger logger;

    @Authorize(resource = CREDENTIALS_RESOURCE, action = CREATE, role = { EPredefinedRole.ADMINISTRATOR })
    public void checkCredentialsCreate() {
        logger.info("create  credentials with role [{}]", EPredefinedRole.ADMINISTRATOR);
    }

    @Authorize(resource = CREDENTIALS_RESOURCE, action = UPDATE, role = { EPredefinedRole.ADMINISTRATOR })
    public void checkCredentialsUpdate() {
        logger.info("update credentials with role [{}]", EPredefinedRole.ADMINISTRATOR);
    }

    @Authorize(resource = CREDENTIALS_RESOURCE, action = AuthorizationBeanService.READ, role = { EPredefinedRole.ADMINISTRATOR,
            EPredefinedRole.OPERATOR })
    public void checkCredentialsRead() {
        logger.info("read credentials");
    }

    @Authorize(resource = CREDENTIALS_PLAIN_TEXT_RESOURCE, action = AuthorizationBeanService.READ, role = { EPredefinedRole.ADMINISTRATOR })
    public void checkCredentialsPlainTextRead() {
        logger.info("read credentials_plain_text");
    }

    @Authorize(resource = SSHKEY_RESOURCE, action = CREATE, role = { EPredefinedRole.ADMINISTRATOR })
    public void checkSshKeyCreate() {
        logger.info("create ssh key  with role [{}]", EPredefinedRole.ADMINISTRATOR);
    }

    @Authorize(resource = SSHKEY_RESOURCE, action = UPDATE, role = { EPredefinedRole.ADMINISTRATOR })
    public void checkSshKeyUpdate() {
        logger.info("update ssh key with role [{}]", EPredefinedRole.ADMINISTRATOR);
    }

    @Authorize(resource = SSHKEY_RESOURCE, action = DELETE)
    public void checkSshKeyDelete() {logger.info("delete ssh key");}

    @Authorize(resource = SSHKEY_RESOURCE, action = EXECUTE)
    public void checkSshKeyImport() {
        logger.info("import ssh key");
    }

    @Authorize(resource = IPSEC_RESOURCE, action = EXECUTE, role = { EPredefinedRole.ADMINISTRATOR })
    public void checkIpsecExecute() {
        logger.info("ipsec execute  with role [{}]", EPredefinedRole.ADMINISTRATOR);
    }

    @Authorize(resource = IPSEC_RESOURCE, action = AuthorizationBeanService.READ, role = { EPredefinedRole.ADMINISTRATOR, EPredefinedRole.OPERATOR })
    public void checkIpsecRead() {
        logger.info("ipsec read");
    }

    @Authorize(resource = IPSEC_RESOURCE, action = "delete", role = { EPredefinedRole.ADMINISTRATOR })
    public void checkIpsecDelete() {
        logger.info("ipsec delete with role [{}]", EPredefinedRole.ADMINISTRATOR);
    }

    @Authorize(resource = OAM_RESOURCE, action = EXECUTE, role = { EPredefinedRole.ADMINISTRATOR })
    public void checkOamExecute() {
        logger.info("oam execute  with role [{}]", EPredefinedRole.ADMINISTRATOR);
    }

    @Authorize(resource = OAM_RESOURCE, action = AuthorizationBeanService.READ, role = { EPredefinedRole.ADMINISTRATOR, EPredefinedRole.OPERATOR })
    public void checkOamRead() {
        logger.info("oam read");
    }

    @Authorize(resource = OAM_RESOURCE, action = "delete", role = { EPredefinedRole.ADMINISTRATOR })
    public void checkOamDelete() {
        logger.info("oam delete with role [{}]", EPredefinedRole.ADMINISTRATOR);
    }

    @Authorize(resource = LDAP_RESOURCE, action = CREATE, role = { EPredefinedRole.ADMINISTRATOR })
    public void checkLdapCreate() {
        logger.info("create  Ldap with role [{}]", EPredefinedRole.ADMINISTRATOR);
    }

    @Authorize(resource = LDAP_RESOURCE, action = UPDATE, role = { EPredefinedRole.ADMINISTRATOR })
    public void checkLdapUpdate() {
        logger.info("update Ldap with role [{}]", EPredefinedRole.ADMINISTRATOR);
    }

    @Authorize(resource = LDAP_RESOURCE, action = PATCH)
    public void checkLdapPatch() {
        logger.info("patch Ldap");
    }

    @Authorize(resource = LDAP_PROXY_RESOURCE, action = READ)
    public void checkLdapProxyRead() {
        logger.info("read Ldap proxy");
    }

    @Authorize(resource = LDAP_PROXY_RESOURCE, action = UPDATE)
    public void checkLdapProxyUpdate() {
        logger.info("update Ldap proxy");
    }

    @Authorize(resource = LDAP_PROXY_RESOURCE, action = DELETE)
    public void checkLdapProxyDelete() {
        logger.info("delete Ldap proxy");
    }

    @Authorize(resource = SNMPV3_RESOURCE, action = CREATE, role = { EPredefinedRole.ADMINISTRATOR })
    public void checksnmpv3Create() {
        logger.info("create  snmpv3 with role [{}]", EPredefinedRole.ADMINISTRATOR);
    }

    @Authorize(resource = SNMPV3_RESOURCE, action = UPDATE, role = { EPredefinedRole.ADMINISTRATOR })
    public void checksnmpv3Update() {
        logger.info("update snmpv3 with role [{}]", EPredefinedRole.ADMINISTRATOR);
    }
    
    @Authorize(resource = SNMPV3_RESOURCE, action = READ, role = { EPredefinedRole.ADMINISTRATOR })
    public void checksnmpv3Read() {
        logger.info("read snmpv3 with role [{}]", EPredefinedRole.ADMINISTRATOR);
    }

    @Authorize(resource = SNMPV3_PLAIN_TEXT_RESOURCE, action = READ, role = { EPredefinedRole.ADMINISTRATOR, })
    public void checksnmpv3PlainTextRead() {
        logger.info("read snmpv3 plain text status with role [{}]", EPredefinedRole.ADMINISTRATOR);
    }


    @Authorize(resource = CRLCHECK_RESOURCE, action = UPDATE, role = { EPredefinedRole.ADMINISTRATOR })
    public void checkCrlCheckUpdate() {
        logger.info("update crlcheck with role [{}]", EPredefinedRole.ADMINISTRATOR);
    }

    @Authorize(resource = CRLCHECK_RESOURCE, action = AuthorizationBeanService.READ, role = { EPredefinedRole.ADMINISTRATOR,
            EPredefinedRole.OPERATOR })
    public void checkReadCrlCheck() {
        logger.info("read crlcheck with role [{}]", EPredefinedRole.ADMINISTRATOR, EPredefinedRole.OPERATOR);
    }

    @Authorize(resource = ON_DEMAND_CRL_DOWNLOAD_RESOURCE, action = EXECUTE, role = { EPredefinedRole.ADMINISTRATOR })
    public void checkOnDemandCrlDownload() {
        logger.info("execute on demand crl download action with role [{}]", EPredefinedRole.ADMINISTRATOR);
    }

    @Authorize(resource = SECURITY_ENROLLMENT_DOWNLOAD_RESOURCE, action = EXECUTE, role = { EPredefinedRole.ADMINISTRATOR })
    public void generateEnrollmentInfoFile() {
        logger.info("generate security file with role [{}]", EPredefinedRole.ADMINISTRATOR);
    }

    @Authorize(resource = CIPHERS_RESOURCE, action = UPDATE, role = { EPredefinedRole.ADMINISTRATOR })
    public void checkSetCiphers() {
        logger.info("update ciphers with role [{}]", EPredefinedRole.ADMINISTRATOR);
    }

    @Authorize(resource = CIPHERS_RESOURCE, action = AuthorizationBeanService.READ, role = { EPredefinedRole.ADMINISTRATOR,
            EPredefinedRole.OPERATOR })
    public void checkGetCiphers() {
        logger.info("read ciphers with role [{}]", EPredefinedRole.ADMINISTRATOR, EPredefinedRole.OPERATOR);
    }

    @Authorize(resource = RTSEL_RESOURCE, action = EXECUTE, role = { EPredefinedRole.ADMINISTRATOR })
    public void checkRTSEL() {
        logger.info("execute rtsel action with role [{}]", EPredefinedRole.ADMINISTRATOR);
    }

    @Authorize(resource = HTTPS_RESOURCE, action = EXECUTE, role = { EPredefinedRole.ADMINISTRATOR })
    public void checkHttps() {
        logger.info("execute https action with role [{}]", EPredefinedRole.ADMINISTRATOR);
    }

    @Authorize(resource = HTTPS_RESOURCE, action = READ, role = { EPredefinedRole.ADMINISTRATOR, EPredefinedRole.OPERATOR })
    public void checkHttpsGetStatus() {
        logger.info("read https status with role [{}]", EPredefinedRole.ADMINISTRATOR, EPredefinedRole.OPERATOR);
    }

    @Authorize(resource = FTPES_RESOURCE, action = EXECUTE, role = { EPredefinedRole.ADMINISTRATOR })
    public void checkFTPES() {
        logger.info("execute FTPES action with role [{}]", EPredefinedRole.ADMINISTRATOR);
    }

    @Authorize(resource = FTPES_RESOURCE, action = READ, role = { EPredefinedRole.ADMINISTRATOR, EPredefinedRole.OPERATOR })
    public void checkFtpesGetStatus() {
        logger.info("ftpes read");
    }

    @Authorize(resource = SSO_RESOURCE, action = EXECUTE, role = { EPredefinedRole.ADMINISTRATOR })
    public void checkSSO() {
        logger.info("execute SSO action with role [{}]", EPredefinedRole.ADMINISTRATOR);
    }

    @Authorize(resource = SSO_RESOURCE, action = READ, role = { EPredefinedRole.ADMINISTRATOR, EPredefinedRole.OPERATOR })
    public void checkSsoGetStatus() {
        logger.info("sso read");
    }

    @Authorize(resource = NODE_SPECIFIC_RESOURCE, action = EXECUTE, role = { EPredefinedRole.SECURITYADMIN })
    public void checkGetNodeSpecificPassword() {
        logger.info("read node specific user password [{}]", EPredefinedRole.SECURITYADMIN);
    }

    @Authorize(resource = CAPABILITY_RESOURCE, action = READ, role = { EPredefinedRole.ADMINISTRATOR, EPredefinedRole.OPERATOR })
    public void checkCapabilityGet() {
        logger.info("read capability");
    }

    @Authorize(resource = LAAD_RESOURCE, action = EXECUTE, role = { EPredefinedRole.ADMINISTRATOR })
    public void checkLaadDistribute() {
        logger.info("execute LAAD distribute action with role [{}]", EPredefinedRole.ADMINISTRATOR);
    }

    @Authorize(resource = LAAD_RESOURCE, action = READ, role = { EPredefinedRole.ADMINISTRATOR, EPredefinedRole.OPERATOR })
    public void checkLaadRead() {
        logger.info("laad read");
    }

    @Authorize(resource = LAAD_RESOURCE, action = DELETE, role = { EPredefinedRole.ADMINISTRATOR })
    public void checkLaadDelete() {
        logger.info("laad delete with role [{}]", EPredefinedRole.ADMINISTRATOR);
    }

    @Authorize(resource = NTP_RESOURCE, action = READ, role = { EPredefinedRole.ADMINISTRATOR, EPredefinedRole.OPERATOR })
    public void checkNtpList() {
        logger.info("ntp list");
    }

    @Authorize(resource = NTP_RESOURCE, action = DELETE, role = { EPredefinedRole.ADMINISTRATOR })
    public void checkNtpRemove() {
        logger.info("Ntp remove with the role [{}]", EPredefinedRole.ADMINISTRATOR);
    }

    @Authorize(resource = NTP_RESOURCE, action = CREATE, role = { EPredefinedRole.ADMINISTRATOR })
    public void checkNtpConfigure() {
        logger.info("execute NTP configure action with role [{}]", EPredefinedRole.ADMINISTRATOR);
    }

    @Authorize(resource = TEST_RESOURCE, action = READ)
    public void checkTestRead() {
        logger.info("read test");
    }

    @Authorize(resource = TEST_RESOURCE, action = EXECUTE)
    public void checkTestExecute() {
        logger.info("execute test");
    }

}
