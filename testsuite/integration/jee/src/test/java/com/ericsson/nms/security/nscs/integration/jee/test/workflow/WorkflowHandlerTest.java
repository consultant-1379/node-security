package com.ericsson.nms.security.nscs.integration.jee.test.workflow;

public interface WorkflowHandlerTest {

    void testWfHandlerReadFileTransferClientModeTaskHandler() throws Exception;

    void testWfHandlerReadCertEnrollStateTaskHandler() throws Exception;

    void testWfHandlerReadTrustedCertificateInstallationFailureTaskHandler() throws Exception;

    void testWfHandlerCancelCertEnrollmentTaskHandler() throws Exception;

    void testWfHandlerClearInstallTrustFlagsTaskHandler() throws Exception;

    void testWfHandlerHasWorkflowInstanceInProgress() throws Exception;

    void testWfHandlerHasWorkflowInstanceInProgress_Positive() throws Exception;

    void testWfHandlerConfigureSSHKeyGenerationTaskHandlerTest() throws Exception;

    void testWfHandlerRevokeNodeCertificateTaskHandlerTest() throws Exception;

    void testWfHandlerIssueInitCertEnrollmentIpSecTaskHandlerTest() throws Exception;

    void testWfHandlerIssueInitCertEnrollmentTaskHandlerTest() throws Exception;

    void testWfHandlerIssueInitTrustedCertEnrollmentTaskHandler() throws Exception;

    void testWfHandlerIssueInitTrustedCertIpSecEnrollmentTaskHandler() throws Exception;

    void testWfHandlerCheckTrustedOAMAlreadyInstalledTaskHandler() throws Exception;

    void testWfHandlerComEcimCheckTrustedAlreadyInstalledTaskHandler() throws Exception;

    void testWfHandlerComEcimCheckTrustedCategoryTaskHandler() throws Exception;

    void testWfHandlerComEcimCheckEnrollmentProtocolTaskHandler() throws Exception;

    void testWfHandlerComEcimCheckNodeCredentialTaskHandler() throws Exception;

    void testWfHandlerRemoveTrustNewIPSECTaskHandler() throws Exception;

    void testWfHandlerRemoveTrustOAMTaskHandler() throws Exception;

    void testWfHandlerComEcimRemoveTrustTaskHandler() throws Exception;

    void testWfHandlerComEcimEnableOrDisableCRLCheckTaskHandlerOAM_ACTIVATED() throws Exception;

    void testWfHandlerComEcimEnableOrDisableCRLCheckTaskHandlerIPSEC_ACTIVATED() throws Exception;

    void testWfHandlerComEcimEnableOrDisableCRLCheckTaskHandlerOAM_DEACTIVATED() throws Exception;

    void testWfHandlerComEcimEnableOrDisableCRLCheckTaskHandlerIPSEC_DEACTIVATED() throws Exception;

    void testWfHandlerCleanupM2MUserAndSmrsTask() throws Exception;

    void testWfHandlerComEcimPrepareCheckOnDemandCrlDownloadActionProgressTask() throws Exception;

}
