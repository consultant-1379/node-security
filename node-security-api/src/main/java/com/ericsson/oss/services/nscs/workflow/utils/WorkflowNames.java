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
package com.ericsson.oss.services.nscs.workflow.utils;

/**
 * Defines the name of Workflows to be invoked by CLI Command Handler
 *
 * @author elucbot
 *
 */
public enum WorkflowNames {

    WORKFLOW_CPPActivateSL2("CPPActivateSL2", WorkflowCategory.NA),
    WORKFLOW_CPPDeactivateSL2("CPPDeactivateSL2", WorkflowCategory.NA),
    WORKFLOW_CPPActivateIpSec("CPPActivateIpSec", WorkflowCategory.NA),
    WORKFLOW_CPPDeactivateIpSec("CPPDeactivateIpSec", WorkflowCategory.NA),
    WORKFLOW_CPPInstallCertificatesIpSec("CPPInstallCertificatesIpSec", WorkflowCategory.NA),
    WORKFLOW_SSHKeyGeneration("SSHKeyGeneration", WorkflowCategory.SSHKEY),
    WORKFLOW_CPPIssueCertificate("CPPIssueCert", WorkflowCategory.ISSUE),
    WORKFLOW_CPPIssueReissueCertificate_IpSec("CPPIssueCertIpSec", WorkflowCategory.ISSUE),
    WORKFLOW_RevokeNodeCertificate("RevokeNodeCertificate", WorkflowCategory.NA),
    WORKFLOW_CPPIssueTrustCert("CPPIssueTrustCert", WorkflowCategory.TRUST),
    WORKFLOW_CPPIssueTrustCertIpSec("CPPIssueTrustCertIpSec", WorkflowCategory.TRUST),
    WORKFLOW_COMECIM_ComIssueTrustCert("COMIssueTrustCert", WorkflowCategory.TRUST),
    WORKFLOW_COMECIM_ComIssueCert("COMIssueCert", WorkflowCategory.ISSUE),
    WORKFLOW_CPPRemoveTrustOAM("CPPRemoveTrustOAM", WorkflowCategory.NA),
    WORKFLOW_CPPRemoveTrustIPSEC("CPPRemoveTrustNewIPSEC", WorkflowCategory.NA),
    WORKFLOW_COMECIMRemoveTrust("COMRemoveTrust", WorkflowCategory.NA),
    WORKFLOW_COMECIM_ENABLE_OR_DISABLE_CRLCHECK("COMEnableOrDisableCRLCheck", WorkflowCategory.NA),
    WORKFLOW_CPP_ENABLE_OR_DISABLE_CRLCHECK("CPPEnableOrDisableCRLCheck", WorkflowCategory.NA),
    WORKFLOW_COMECIM_ON_DEMAND_DOWNLOAD_CRL("COMOnDemandCrlDownload", WorkflowCategory.NA),
    WORKFLOW_CPP_ON_DEMAND_DOWNLOAD_CRL("CPPOnDemandCrlDownload", WorkflowCategory.NA),
    WORKFLOW_SET_CIPHERS("SetCiphers", WorkflowCategory.NA),
    WORKFLOW_CPPACTIVATERTSEL("CPPActivateRTSEL", WorkflowCategory.NA),
    WORKFLOW_CPPDEACTIVATERTSEL("CPPDeactivateRTSEL", WorkflowCategory.NA),
    WORKFLOW_CPP_RTSEL_DELETE_SERVER("CPPRtselDeleteServer", WorkflowCategory.NA),
    WORKFLOW_CPP_ACTIVATE_HTTPS("CPPActivateHTTPS", WorkflowCategory.NA),
    WORKFLOW_CPP_DEACTIVATE_HTTPS("CPPDeactivateHTTPS", WorkflowCategory.NA),
    WORKFLOW_CPP_GET_HTTPS("CPPGetHTTPS", WorkflowCategory.NA),
    WORKFLOW_COM_ACTIVATE_FTPES("COMActivateFTPES", WorkflowCategory.NA),
    WORKFLOW_COM_DEACTIVATE_FTPES("COMDeactivateFTPES", WorkflowCategory.NA),
    WORKFLOW_CPP_LAAD_FILES_DISTRIBUTION("CPPLaadDistribution",WorkflowCategory.NA),
    WORKFLOW_CPP_NTP_REMOVE("CPPNtpRemove",WorkflowCategory.NA),
    WORKFLOW_COMECIM_NTP_REMOVE("COMNtpRemove", WorkflowCategory.NA),
    WORKFLOW_CPP_NTP_CONFIGURE("CPPNtpConfigure",WorkflowCategory.NA),
    WORKFLOW_COMECIM_CONFIGURE_LDAP("COMConfigureLdap", WorkflowCategory.NA),
    WORKFLOW_CBPOI_CONFIGURE_LDAP("CbpOiConfigureLdap", WorkflowCategory.NA),
    WORKFLOW_CBPOI_INSTALL_TRUST_CERTS("CbpOiInstallTrustCerts",WorkflowCategory.NA),
    WORKFLOW_CBPOI_REMOVE_TRUST("CbpOiRemoveTrust", WorkflowCategory.NA),
    WORKFLOW_CBPOI_START_ONLINE_ENROLLMENT("CbpOiStartOnlineEnrollment", WorkflowCategory.ISSUE);

    private final String wfName;
    private final WorkflowCategory wfCategory;

    private WorkflowNames(final String name, final WorkflowCategory category) {
        this.wfName = name;
        this.wfCategory = category;
    }

    @Override
    public String toString() {
        return this.wfName;
    }

    /**
     * @return The workflow name
     */
    public String getWorkflowName() {
        return this.wfName;
    }

    /**
     * @return WorkflowCategory
     */
    public WorkflowCategory getCategory() {
        return this.wfCategory;
    }

    public static WorkflowNames getWorkflowName(final String wfName) {
        for (final WorkflowNames workflowName : WorkflowNames.values()) {
            if (workflowName.getWorkflowName().equalsIgnoreCase(wfName)) {
                return workflowName;
            }
        }
        return null;
    }

}
