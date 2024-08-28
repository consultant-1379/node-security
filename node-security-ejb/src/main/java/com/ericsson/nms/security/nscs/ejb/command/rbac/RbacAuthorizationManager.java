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
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;
import com.ericsson.nms.security.nscs.api.command.types.CertificateIssueCommand;
import com.ericsson.nms.security.nscs.api.command.types.CertificateReissueCommand;
import com.ericsson.nms.security.nscs.api.command.types.GetCertEnrollStateCommand;
import com.ericsson.nms.security.nscs.api.command.types.GetCredentialsCommand;
import com.ericsson.nms.security.nscs.api.command.types.GetSnmpCommand;
import com.ericsson.nms.security.nscs.api.command.types.GetTrustCertInstallStateCommand;
import com.ericsson.nms.security.nscs.api.command.types.TestCommand;
import com.ericsson.nms.security.nscs.api.command.types.TrustDistributeCommand;
import com.ericsson.nms.security.nscs.api.command.types.TrustRemoveCommand;
import com.ericsson.nms.security.nscs.api.enums.CertificateType;
import com.ericsson.nms.security.nscs.api.enums.TrustCategoryType;

@Stateless
public class RbacAuthorizationManager {

    @Inject
    private Logger logger;

    @Inject
    private AuthorizationBeanService authorizationBeanService;

    @TransactionAttribute()
    public void checkAuthorization(final NscsPropertyCommand nscsPropertyCommand) {

        final NscsCommandType commandType = nscsPropertyCommand.getCommandType();

        logger.info("CommandType : [{}]", commandType);

        switch (commandType) {

        case CREATE_CREDENTIALS:
            authorizationBeanService.checkCredentialsCreate();
            break;
        case UPDATE_CREDENTIALS:
            authorizationBeanService.checkCredentialsUpdate();
            break;
        case GET_CREDENTIALS:
            checkCredentialsGet(nscsPropertyCommand);
            break;
        case CREATE_SSH_KEY:
            authorizationBeanService.checkSshKeyCreate();
            break;
        case UPDATE_SSH_KEY:
            authorizationBeanService.checkSshKeyUpdate();
            break;
        case DELETE_SSH_KEY:
            authorizationBeanService.checkSshKeyDelete();
            break;
        case IMPORT_NODE_SSH_PRIVATE_KEY:
            authorizationBeanService.checkSshKeyImport();
            break;
        case CERTIFICATE_ISSUE:
            checkCertificateIssue(nscsPropertyCommand);
            break;
        case CERTIFICATE_REISSUE:
            checkCertificateReissue(nscsPropertyCommand);
            break;
        case TRUST_DISTRIBUTE:
            checkTrustDistribute(nscsPropertyCommand);
            break;
        case GET_CERT_ENROLL_STATE:
            checkCertificateGet(nscsPropertyCommand);
            break;
        case GET_TRUST_CERT_INSTALL_STATE:
            checkTrustGet(nscsPropertyCommand);
            break;
        case TRUST_REMOVE:
            checkTrustRemove(nscsPropertyCommand);
            break;
        case CPP_IPSEC:
            authorizationBeanService.checkIpsecExecute();
            break;
        case CPP_IPSEC_STATUS:
            authorizationBeanService.checkIpsecRead();
            break;
        case CPP_SET_SL:
            authorizationBeanService.checkOamExecute();
            break;
        case CPP_GET_SL:
            authorizationBeanService.checkOamRead();
            break;
        case LDAP_CONFIGURATION:
            authorizationBeanService.checkLdapCreate();
            break;
        case LDAP_RECONFIGURATION:
            authorizationBeanService.checkLdapUpdate();
            break;
        case LDAP_RENEW:
            authorizationBeanService.checkLdapPatch();
            break;
        case LDAP_PROXY_GET:
            authorizationBeanService.checkLdapProxyRead();
            break;
        case LDAP_PROXY_SET:
            authorizationBeanService.checkLdapProxyUpdate();
            break;
        case LDAP_PROXY_DELETE:
            authorizationBeanService.checkLdapProxyDelete();
            break;
        case SNMP_AUTHNOPRIV:
        case SNMP_AUTHPRIV:
            authorizationBeanService.checksnmpv3Update();
            break;
        case GET_SNMP:
            checkSnmpGet(nscsPropertyCommand);
            break;
        case CRL_CHECK_ENABLE:
        case CRL_CHECK_DISABLE:
            authorizationBeanService.checkCrlCheckUpdate();
            break;
        case CRL_CHECK_GET_STATUS:
            authorizationBeanService.checkReadCrlCheck();
            break;
        case ON_DEMAND_CRL_DOWNLOAD:
            authorizationBeanService.checkOnDemandCrlDownload();
            break;
        case SET_CIPHERS:
            authorizationBeanService.checkSetCiphers();
            break;
        case GET_CIPHERS:
            authorizationBeanService.checkGetCiphers();
            break;
        case ENROLLMENT_INFO_FILE:
            authorizationBeanService.generateEnrollmentInfoFile();
            break;
        case RTSEL_ACTIVATE:
        case RTSEL_DEACTIVATE:
        case RTSEL_GET:
        case RTSEL_DELETE:
            authorizationBeanService.checkRTSEL();
            break;
        case HTTPS_GET_STATUS:
            authorizationBeanService.checkHttpsGetStatus();
            break;
        case HTTPS_ACTIVATE:
        case HTTPS_DEACTIVATE:
            authorizationBeanService.checkHttps();
            break;
        case GET_NODE_SPECIFIC_PASSWORD:
            authorizationBeanService.checkGetNodeSpecificPassword();
            break;
        case FTPES_ACTIVATE:
        case FTPES_DEACTIVATE:
            authorizationBeanService.checkFTPES();
            break;
        case FTPES_GET_STATUS:
            authorizationBeanService.checkFtpesGetStatus();
            break;
        case SSO_ENABLE:
        case SSO_DISABLE:
            authorizationBeanService.checkSSO();
            break;
        case SSO_GET:
            authorizationBeanService.checkSsoGetStatus();
            break;
        case CAPABILITY_GET:
            authorizationBeanService.checkCapabilityGet();
            break;
        case LAAD_FILES_DISTRIBUTE:
            authorizationBeanService.checkLaadDistribute();
            break;
        case NTP_LIST:
            authorizationBeanService.checkNtpList();
            break;
        case NTP_CONFIGURE:
            authorizationBeanService.checkNtpConfigure();
            break;
        case NTP_REMOVE:
            authorizationBeanService.checkNtpRemove();
            break;
        case TEST_COMMAND:
            checkTestCommand(nscsPropertyCommand);
            break;
        default:
            break;
        }
    }

    /**
     * Check test command authorization.
     * 
     * @param nscsPropertyCommand
     *            the command.
     */
    private void checkTestCommand(final NscsPropertyCommand nscsPropertyCommand) {
        final String workflows = ((TestCommand) nscsPropertyCommand).getWorkflows();
        if (workflows != null) {
            authorizationBeanService.checkTestExecute();
        } else {
            authorizationBeanService.checkTestRead();
        }
    }

    /**
     * Check snmp get command authorization.
     * 
     * @param nscsPropertyCommand
     *            the command.
     */
    private void checkSnmpGet(final NscsPropertyCommand nscsPropertyCommand) {
        final String snmpv3plainText = ((GetSnmpCommand) nscsPropertyCommand).getPlainText();
        if ("show".equals(snmpv3plainText)) {
            authorizationBeanService.checksnmpv3PlainTextRead();
        } else {
            authorizationBeanService.checksnmpv3Read();
        }
    }

    /**
     * Check trust remove command authorization.
     * 
     * @param nscsPropertyCommand
     *            the command.
     */
    private void checkTrustRemove(final NscsPropertyCommand nscsPropertyCommand) {
        String removeTrustCategoryType = null;
        if (((TrustRemoveCommand) nscsPropertyCommand).getCertType() != null) {
            removeTrustCategoryType = ((TrustRemoveCommand) nscsPropertyCommand).getCertType();
        } else {
            removeTrustCategoryType = ((TrustRemoveCommand) nscsPropertyCommand).getTrustCategory();
        }
        if (TrustCategoryType.IPSEC.name().equals(removeTrustCategoryType)) {
            authorizationBeanService.checkIpsecDelete();
        } else if (TrustCategoryType.OAM.name().equals(removeTrustCategoryType)) {
            authorizationBeanService.checkOamDelete();
        } else if (TrustCategoryType.LAAD.name().equals(removeTrustCategoryType)) {
            authorizationBeanService.checkLaadDelete();
        }
    }

    /**
     * Check trust get command authorization.
     * 
     * @param nscsPropertyCommand
     *            the command.
     */
    private void checkTrustGet(final NscsPropertyCommand nscsPropertyCommand) {
        String getTrustCategoryType = null;
        if (((GetTrustCertInstallStateCommand) nscsPropertyCommand).getCertType() != null) {
            getTrustCategoryType = ((GetTrustCertInstallStateCommand) nscsPropertyCommand).getCertType();
        } else {
            getTrustCategoryType = ((GetTrustCertInstallStateCommand) nscsPropertyCommand).getTrustCategory();
        }
        if (TrustCategoryType.IPSEC.name().equals(getTrustCategoryType)) {
            authorizationBeanService.checkIpsecRead();
        } else if (TrustCategoryType.OAM.name().equals(getTrustCategoryType)) {
            authorizationBeanService.checkOamRead();
        } else if (TrustCategoryType.LAAD.name().equals(getTrustCategoryType)) {
            authorizationBeanService.checkLaadRead();
        }
    }

    /**
     * Check trust distribute command authorization.
     * 
     * @param nscsPropertyCommand
     *            the command.
     */
    private void checkTrustDistribute(final NscsPropertyCommand nscsPropertyCommand) {
        String trustCategoryType = null;
        if (((TrustDistributeCommand) nscsPropertyCommand).getCertType() != null) {
            trustCategoryType = ((TrustDistributeCommand) nscsPropertyCommand).getCertType();
        } else {
            trustCategoryType = ((TrustDistributeCommand) nscsPropertyCommand).getTrustCategory();
        }
        if (TrustCategoryType.IPSEC.name().equals(trustCategoryType)) {
            authorizationBeanService.checkIpsecExecute();
        } else if (TrustCategoryType.OAM.name().equals(trustCategoryType)) {
            authorizationBeanService.checkOamExecute();
        } else if (TrustCategoryType.LAAD.name().equals(trustCategoryType)) {
            authorizationBeanService.checkLaadDistribute();
        }
    }

    /**
     * Check certificate get command authorization.
     * 
     * @param nscsPropertyCommand
     *            the command.
     */
    private void checkCertificateGet(final NscsPropertyCommand nscsPropertyCommand) {
        final String getEnrollCertType = ((GetCertEnrollStateCommand) nscsPropertyCommand).getCertType();
        if (CertificateType.IPSEC.name().equals(getEnrollCertType)) {
            authorizationBeanService.checkIpsecRead();
        } else if (CertificateType.OAM.name().equals(getEnrollCertType)) {
            authorizationBeanService.checkOamRead();
        }
    }

    /**
     * Check certificate reissue command authorization.
     * 
     * @param nscsPropertyCommand
     *            the command.
     */
    private void checkCertificateReissue(final NscsPropertyCommand nscsPropertyCommand) {
        final String certReissueCertType = ((CertificateReissueCommand) nscsPropertyCommand).getCertType();
        if (CertificateType.IPSEC.name().equals(certReissueCertType)) {
            authorizationBeanService.checkIpsecExecute();
        } else if (CertificateType.OAM.name().equals(certReissueCertType)) {
            authorizationBeanService.checkOamExecute();
        }
    }

    /**
     * Check certificate issue command authorization.
     * 
     * @param nscsPropertyCommand
     *            the command.
     */
    private void checkCertificateIssue(final NscsPropertyCommand nscsPropertyCommand) {
        final String certIssueCertType = ((CertificateIssueCommand) nscsPropertyCommand).getCertType();
        if (CertificateType.IPSEC.name().equals(certIssueCertType)) {
            authorizationBeanService.checkIpsecExecute();
        } else if (CertificateType.OAM.name().equals(certIssueCertType)) {
            authorizationBeanService.checkOamExecute();
        }
    }

    /**
     * Check credentials get command authorization.
     * 
     * @param nscsPropertyCommand
     *            the command.
     */
    private void checkCredentialsGet(final NscsPropertyCommand nscsPropertyCommand) {
        final String plainText = ((GetCredentialsCommand) nscsPropertyCommand).getPlainText();
        if ("show".equals(plainText)) {
            authorizationBeanService.checkCredentialsPlainTextRead();
        } else {
            authorizationBeanService.checkCredentialsRead();
        }
    }

}
