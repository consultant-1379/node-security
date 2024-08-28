package com.ericsson.nms.security.nscs.integration.jee.test.workflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityDataConstants;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityDataSetup;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityRadioNodesDataSetup;
import com.ericsson.nms.security.nscs.workflow.task.cpp.WFMessageConstants;
import com.ericsson.oss.services.nscs.workflow.WfQueryService;
import com.ericsson.oss.services.nscs.workflow.tasks.api.WorkflowTaskService;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute.ComEcimCheckEnrollmentProtocolTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute.ComEcimCheckNodeCredentialTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute.ComEcimCheckTrustedAlreadyInstalledTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute.ComEcimCheckTrustedCategoryTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute.ComEcimEnableOrDisableCRLCheckTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute.ComEcimPrepareCheckOnDemandCrlDownloadActionProgressTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.CheckTrustedOAMAlreadyInstalledTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.ReadCertEnrollStateTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.ReadFileTransferClientModeTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.ReadTrustedCertificateInstallationFailureTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.CancelCertEnrollmentTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.CleanupM2MUserAndSmrsTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.ClearInstallTrustFlagsTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.IssueInitCertEnrollmentIpSecTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.IssueInitCertEnrollmentTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.IssueInitTrustedCertEnrollmentTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.IssueInitTrustedCertIpSecEnrollmentTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.RemoveTrustNewIPSECTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.RemoveTrustOAMTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.pki.RevokeNodeCertificateTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.ssh.ConfigureSSHKeyGenerationTask;

public class WorkflowHandlerTestImpl implements WorkflowHandlerTest {

    @Inject
    NodeSecurityDataSetup dataSetup;

    @Inject
    NodeSecurityRadioNodesDataSetup dataRadioNodesSetup;

    @Inject
    Logger log;

    @Inject
    private WorkflowTaskService workflowTaskService;

    @Inject
    private WorkflowHandler handler;

    @Inject
    private WfQueryService wfQuery;

    public static final int WF_LONG_TIMEOUT = 120000;
    private static final String OAM_ENTITY_PROFILE_NAME = "MicroRBSOAM_CHAIN_EP";
    private static final String IPSEC_ENTITY_PROFILE_NAME = "MicroRBSIPSec_SAN_CHAIN_EP";

    @Override
    public void testWfHandlerReadFileTransferClientModeTaskHandler() throws Exception {

        log.info("----------- testWfHandlerReadFileTransferClientModeTaskHandler starts ------------------");

        dataSetup.insertData();

        final String value = workflowTaskService.processTask(new ReadFileTransferClientModeTask(NodeSecurityDataConstants.MECONTEXT_FDN1));

        assertEquals(WFMessageConstants.CPP_FILE_TRANSFER_CLIENT_MODE_UNSECURE, value);

        log.info("----------- testWfHandlerReadFileTransferClientModeTaskHandler ends ------------------");
    }

    @Override
    public void testWfHandlerReadCertEnrollStateTaskHandler() throws Exception {

        log.info("----------- testWfHandlerReadCertEnrollStateTaskHandler starts ------------------");

        dataSetup.insertData();

        final String value = workflowTaskService.processTask(new ReadCertEnrollStateTask(NodeSecurityDataConstants.MECONTEXT_FDN1));

        assertNull("CertEnrollState attribute cannot be read from DPS directly, it must be null", value);

        log.info("----------- testWfHandlerReadCertEnrollStateTaskHandler ends ------------------");
    }

    @Override
    public void testWfHandlerReadTrustedCertificateInstallationFailureTaskHandler() throws Exception {

        log.info("----------- testWfHandlerReadTrustedCertificateInstallationFailureTaskHandler starts ------------------");

        dataSetup.insertData();

        final String value = workflowTaskService
                .processTask(new ReadTrustedCertificateInstallationFailureTask(NodeSecurityDataConstants.MECONTEXT_FDN1));

        assertEquals("true", value);

        log.info("----------- testWfHandlerReadTrustedCertificateInstallationFailureTaskHandler ends ------------------");
    }

    @Override
    public void testWfHandlerCancelCertEnrollmentTaskHandler() throws Exception {

        log.info("----------- testWfHandlerCancelCertEnrollmentTaskHandler starts ------------------");

        dataSetup.insertData();

        workflowTaskService.processTask(new CancelCertEnrollmentTask(NodeSecurityDataConstants.MECONTEXT_FDN1));

        log.info("----------- testWfHandlerCancelCertEnrollmentTaskHandler ends ------------------");
    }

    @Override
    public void testWfHandlerClearInstallTrustFlagsTaskHandler() throws Exception {

        log.info("----------- testWfHandlerClearInstallTrustFlagsTaskHandler starts ------------------");

        dataSetup.insertData();

        workflowTaskService.processTask(new ClearInstallTrustFlagsTask(NodeSecurityDataConstants.MECONTEXT_FDN1));

        log.info("----------- testWfHandlerClearInstallTrustFlagsTaskHandler ends ------------------");
    }

    @Override
    public void testWfHandlerHasWorkflowInstanceInProgress() throws Exception {

        log.info("----------- testWfHandlerHasWorkflowInstanceInProgress starts ------------------");

        assertFalse(wfQuery.isWorkflowInProgress(new NodeRef(NodeSecurityDataConstants.MECONTEXT_FDN1)));

        log.info("----------- testWfHandlerHasWorkflowInstanceInProgress ends ------------------");
    }

    @Override
    public void testWfHandlerHasWorkflowInstanceInProgress_Positive() throws Exception {

        log.info("----------- testWfHandlerHasWorkflowInstanceInProgress_Positive starts ------------------");

        dataSetup.insertData();
        final NodeRef n = new NodeRef(NodeSecurityDataConstants.MECONTEXT_FDN1);
        //Starting a workflow for the node
        handler.startWorkflowInstance(n, NodeSecurityDataConstants.CPP_ACTIVATE_SL_2);

        //Waiting a little for the workflow to kick off then assert
        Thread.sleep(2000);
        assertTrue(wfQuery.isWorkflowInProgress(n));

        //Waiting for the WF to finish and assert
        for (int i = 0; i < 20; i++) {
            Thread.sleep(5000);
            final boolean isInProgress = wfQuery.isWorkflowInProgress(n);
            log.info("---" + i + " Query node in progress: " + isInProgress);

            if (!isInProgress) {
                return;
            }
        }

        log.info("----------- testWfHandlerHasWorkflowInstanceInProgress_Positive ends ------------------");
    }

    @Override
    public void testWfHandlerConfigureSSHKeyGenerationTaskHandlerTest() throws Exception {

        log.info("----------- testWfHandlerConfigureSSHKeyGenerationTaskHandlerTest starts ------------------");

        final String nodeName = NodeSecurityDataConstants.NODE_NAME1;

        //Setup create node
        dataSetup.deleteAllNodes();
        dataSetup.createNode(nodeName, "SYNCHRONIZED", SecurityLevel.LEVEL_2, NodeSecurityDataSetup.IpAddressVersion.IPv4);
        dataSetup.createNetworkElementSecurity(NodeSecurityDataSetup.networkElementNameFromMeContextName(nodeName));

        final ConfigureSSHKeyGenerationTask task = new ConfigureSSHKeyGenerationTask(NodeSecurityDataConstants.MECONTEXT_FDN1);
        task.setIsCreate("false");
        task.setAlgorithm(AlgorithmKeys.RSA_8192.toString());

        workflowTaskService.processTask(task);

        Thread.sleep(WF_LONG_TIMEOUT);

        log.info("----------- testWfHandlerConfigureSSHKeyGenerationTaskHandlerTest ends ------------------");
    }

    @Override
    public void testWfHandlerRevokeNodeCertificateTaskHandlerTest() throws Exception {

        log.info("----------- testWfHandlerRevokeNodeCertificateTaskHandlerTest starts ------------------");

        final String nodeName = NodeSecurityDataConstants.NODE_NAME1;
        //Setup create node
        dataSetup.deleteAllNodes();
        dataSetup.createNode(nodeName, "SYNCHRONIZED", SecurityLevel.LEVEL_2, NodeSecurityDataSetup.IpAddressVersion.IPv4);
        dataSetup.createNetworkElementSecurity(NodeSecurityDataSetup.networkElementNameFromMeContextName(nodeName));

        final RevokeNodeCertificateTask task = new RevokeNodeCertificateTask(NodeSecurityDataConstants.MECONTEXT_FDN1);
        task.setCertificateId("ABC");
        task.setCertificateAuthorityId("AUTO");

        // UnexpectedErrorException should be thrown due to OUTPUT_PARAMETERS not set in the task
        try {
            workflowTaskService.processTask(task);
            assertTrue("No exception thrown", false);
        } catch (Exception e) {
            assertTrue("Unexpected exception " + e.getClass().getCanonicalName(), e instanceof UnexpectedErrorException);
            assertEquals("Unexpected exception message", "Missing internal parameters", e.getMessage());
        }

        log.info("----------- testWfHandlerRevokeNodeCertificateTaskHandlerTest ends ------------------");
    }

    @Override
    public void testWfHandlerIssueInitCertEnrollmentIpSecTaskHandlerTest() throws Exception {

        log.info("----------- testWfHandlerIssueInitCertEnrollmentIpSecTaskHandlerTest starts ------------------");

        final String nodeName = NodeSecurityDataConstants.NODE_NAME1;
        //Setup create node
        dataSetup.deleteAllNodes();
        dataSetup.createNode(nodeName, "SYNCHRONIZED", SecurityLevel.LEVEL_2, NodeSecurityDataSetup.IpAddressVersion.IPv4);
        dataSetup.createNetworkElementSecurity(NodeSecurityDataSetup.networkElementNameFromMeContextName(nodeName));

        final IssueInitCertEnrollmentIpSecTask task = new IssueInitCertEnrollmentIpSecTask(NodeSecurityDataConstants.MECONTEXT_FDN1);
        task.setAlgoKeySize(AlgorithmKeys.RSA_1024.toString());
        task.setEntityProfileName(IPSEC_ENTITY_PROFILE_NAME);
        task.setRollbackTimeout(1);
        task.setSubjectAltName("172.16.0.4");
        task.setSubjectAltNameType(SubjectAltNameFormat.IPV4.name());
        task.setEnrollmentMode(EnrollmentMode.SCEP.toString());

        workflowTaskService.processTask(task);

        log.info("----------- testWfHandlerIssueInitCertEnrollmentIpSecTaskHandlerTest ends ------------------");
    }

    @Override
    public void testWfHandlerIssueInitCertEnrollmentTaskHandlerTest() throws Exception {

        log.info("----------- testWfHandlerIssueInitCertEnrollmentTaskHandlerTest starts ------------------");

        final String nodeName = NodeSecurityDataConstants.NODE_NAME1;
        //Setup create node
        dataSetup.deleteAllNodes();
        dataSetup.createNode(nodeName, "SYNCHRONIZED", SecurityLevel.LEVEL_2, NodeSecurityDataSetup.IpAddressVersion.IPv4);
        dataSetup.createNetworkElementSecurity(NodeSecurityDataSetup.networkElementNameFromMeContextName(nodeName));

        final IssueInitCertEnrollmentTask task = new IssueInitCertEnrollmentTask(NodeSecurityDataConstants.MECONTEXT_FDN1);
        task.setAlgoKeySize(AlgorithmKeys.RSA_1024.toString());
        task.setEntityProfileName(OAM_ENTITY_PROFILE_NAME);
        task.setRollbackTimeout(1);
        task.setEnrollmentMode(EnrollmentMode.SCEP.toString());

        workflowTaskService.processTask(task);

        log.info("----------- testWfHandlerIssueInitCertEnrollmentTaskHandlerTest ends ------------------");
    }

    @Override
    public void testWfHandlerIssueInitTrustedCertEnrollmentTaskHandler() throws Exception {

        log.info("----------- testWfHandlerIssueInitTrustedCertEnrollmentTaskHandler starts ------------------");

        final String nodeName = NodeSecurityDataConstants.NODE_NAME1;
        //Setup create node
        dataSetup.deleteAllNodes();
        dataSetup.createNode(nodeName, "SYNCHRONIZED", SecurityLevel.LEVEL_2, NodeSecurityDataSetup.IpAddressVersion.IPv4);

        final String caName = "";
        final IssueInitTrustedCertEnrollmentTask task = new IssueInitTrustedCertEnrollmentTask(NodeSecurityDataConstants.MECONTEXT_FDN1,
                TrustedCertCategory.CORBA_PEERS.toString(), caName);

        workflowTaskService.processTask(task);

        log.info("----------- testWfHandlerIssueInitTrustedCertEnrollmentTaskHandler ends ------------------");
    }

    @Override
    public void testWfHandlerIssueInitTrustedCertIpSecEnrollmentTaskHandler() throws Exception {

        log.info("----------- testWfHandlerIssueInitTrustedCertIpSecEnrollmentTaskHandler starts ------------------");

        final String nodeName = NodeSecurityDataConstants.NODE_NAME1;
        //Setup create node
        dataSetup.deleteAllNodes();
        dataSetup.createNode(nodeName, "SYNCHRONIZED", SecurityLevel.LEVEL_2, NodeSecurityDataSetup.IpAddressVersion.IPv4);
        final String ca = "";
        final IssueInitTrustedCertIpSecEnrollmentTask task = new IssueInitTrustedCertIpSecEnrollmentTask(NodeSecurityDataConstants.MECONTEXT_FDN1,
                TrustedCertCategory.IPSEC.toString(), ca);

        workflowTaskService.processTask(task);

        log.info("----------- testWfHandlerIssueInitTrustedCertIpSecEnrollmentTaskHandler ends ------------------");
    }

    @Override
    public void testWfHandlerCheckTrustedOAMAlreadyInstalledTaskHandler() throws Exception {

        log.info("----------- testWfHandlerCheckTrustedOAMAlreadyInstalledTaskHandler starts ------------------");

        final String nodeName = NodeSecurityDataConstants.NODE_NAME1;
        dataSetup.deleteAllNodes();
        dataSetup.createNode(nodeName, "SYNCHRONIZED", SecurityLevel.LEVEL_2, NodeSecurityDataSetup.IpAddressVersion.IPv4);

        final String trustedCA = "";
        final CheckTrustedOAMAlreadyInstalledTask task = new CheckTrustedOAMAlreadyInstalledTask(NodeSecurityDataConstants.MECONTEXT_FDN1,
                TrustedCertCategory.CORBA_PEERS.toString(), trustedCA);

        workflowTaskService.processTask(task);

        log.info("----------- testWfHandlerCheckTrustedOAMAlreadyInstalledTaskHandler ends ------------------");
    }

    @Override
    public void testWfHandlerComEcimCheckTrustedAlreadyInstalledTaskHandler() throws Exception {

        log.info("----------- testWfHandlerComEcimCheckTrustedAlreadyInstalledTaskHandler starts ------------------");

        final String nodeName = NodeSecurityDataConstants.NODE_NAME3;

        dataRadioNodesSetup.deleteAllNodes();

        dataRadioNodesSetup.createComEcimNode(nodeName, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");

        final ComEcimCheckTrustedAlreadyInstalledTask task = new ComEcimCheckTrustedAlreadyInstalledTask(NodeSecurityDataConstants.MECONTEXT_FDN3,
                TrustedCertCategory.CORBA_PEERS.name());

        // WorkflowTaskException should be thrown due to OUTPUT_PARAMETERS not set in the task
        try {
            workflowTaskService.processTask(task);
            assertTrue("No exception thrown", false);
        } catch (Exception e) {
            assertTrue("Unexpected exception " + e.getClass().getCanonicalName(), e instanceof WorkflowTaskException);
            assertEquals("Unexpected exception message", "Output params not yet set! ", e.getMessage());
        }

        dataRadioNodesSetup.deleteAllNodes();

        log.info("----------- testWfHandlerComEcimCheckTrustedAlreadyInstalledTaskHandler ends ------------------");
    }

    @Override
    public void testWfHandlerComEcimCheckTrustedCategoryTaskHandler() throws Exception {

        log.info("----------- testWfHandlerComEcimCheckTrustedCategoryTaskHandler starts ------------------");

        final String nodeName = NodeSecurityDataConstants.NODE_NAME3;

        dataRadioNodesSetup.deleteAllNodes();

        dataRadioNodesSetup.createComEcimNode(nodeName, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");

        final ComEcimCheckTrustedCategoryTask task = new ComEcimCheckTrustedCategoryTask(NodeSecurityDataConstants.MECONTEXT_FDN3,
                TrustedCertCategory.CORBA_PEERS.name());

        // UnexpectedErrorException should be thrown due to missing internal parameters in the task
        try {
            workflowTaskService.processTask(task);
            assertTrue("No exception thrown", false);
        } catch (Exception e) {
            assertTrue("Unexpected exception " + e.getClass().getCanonicalName(), e instanceof UnexpectedErrorException);
            assertEquals("Unexpected exception message", "Missing internal parameters for certificate type [OAM]", e.getMessage());
        }

        dataRadioNodesSetup.deleteAllNodes();

        log.info("----------- testWfHandlerComEcimCheckTrustedCategoryTaskHandler ends ------------------");
    }

    @Override
    public void testWfHandlerComEcimCheckEnrollmentProtocolTaskHandler() throws Exception {

        log.info("----------- testWfHandlerComEcimCheckEnrollmentProtocolTaskHandler starts ------------------");

        final String nodeName = NodeSecurityDataConstants.NODE_NAME3;

        dataRadioNodesSetup.deleteAllNodes();

        dataRadioNodesSetup.createComEcimNode(nodeName, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");

        final ComEcimCheckEnrollmentProtocolTask task = new ComEcimCheckEnrollmentProtocolTask(NodeSecurityDataConstants.MECONTEXT_FDN3,
                EnrollmentMode.ONLINE_SCEP.toString());
        final String WFMessageConstants = workflowTaskService.processTask(task);

        assertEquals(WFMessageConstants, "SUPPORTED");

        log.info("----------- testWfHandlerComEcimCheckEnrollmentProtocolTaskHandler ends ------------------");

    }

    @Override
    public void testWfHandlerComEcimCheckNodeCredentialTaskHandler() throws Exception {

        log.info("----------- testWfHandlerComEcimCheckNodeCredentialTaskHandler starts ------------------");

        final String nodeName = NodeSecurityDataConstants.NODE_NAME3;

        dataRadioNodesSetup.deleteAllNodes();

        dataRadioNodesSetup.createComEcimNode(nodeName, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");

        final ComEcimCheckNodeCredentialTask task = new ComEcimCheckNodeCredentialTask(NodeSecurityDataConstants.MECONTEXT_FDN3);
        task.setTrustedCertCategory(TrustedCertCategory.CORBA_PEERS.toString());

        // UnexpectedErrorException should be thrown due to missing internal parameters in the task
        try {
            workflowTaskService.processTask(task);
            assertTrue("No exception thrown", false);
        } catch (Exception e) {
            assertTrue("Unexpected exception " + e.getClass().getCanonicalName(), e instanceof UnexpectedErrorException);
            assertEquals("Unexpected exception message", "An unexpected error has occurred", e.getMessage());
        }

        log.info("----------- testWfHandlerComEcimCheckNodeCredentialTaskHandler ends ------------------");
    }

    @Override
    public void testWfHandlerRemoveTrustNewIPSECTaskHandler() throws Exception {

        log.info("----------- testWfHandlerRemoveTrustNewIPSECTaskHandler starts ------------------");

        final String nodeName = NodeSecurityDataConstants.NODE_NAME3;

        //Setup create node
        dataSetup.deleteAllNodes();
        dataSetup.createNode(nodeName, "SYNCHRONIZED", SecurityLevel.LEVEL_2, NodeSecurityDataSetup.IpAddressVersion.IPv4);
        final RemoveTrustNewIPSECTask task = new RemoveTrustNewIPSECTask(NodeSecurityDataConstants.MECONTEXT_FDN3);
        task.setCertificateSN("123355467");
        task.setIssuer("Some issuer");

        workflowTaskService.processTask(task);

        log.info("----------- testWfHandlerRemoveTrustNewIPSECTaskHandler ends ------------------");
    }

    @Override
    public void testWfHandlerRemoveTrustOAMTaskHandler() throws Exception {

        log.info("----------- testWfHandlerRemoveTrustOAMTaskHandler starts ------------------");

        final String nodeName = NodeSecurityDataConstants.NODE_NAME3;

        dataSetup.deleteAllNodes();
        dataSetup.createNode(nodeName, "SYNCHRONIZED", SecurityLevel.LEVEL_2, NodeSecurityDataSetup.IpAddressVersion.IPv4);

        final RemoveTrustOAMTask task = new RemoveTrustOAMTask(NodeSecurityDataConstants.MECONTEXT_FDN3);
        task.setCertificateSN("123355467");
        task.setIssuer("Some issuer");
        task.setCertCategory(TrustedCertCategory.CORBA_PEERS.toString());

        workflowTaskService.processTask(task);

        log.info("----------- testWfHandlerRemoveTrustOAMTaskHandler ends ------------------");
    }

    @Override
    public void testWfHandlerComEcimRemoveTrustTaskHandler() throws Exception {

        log.info("----------- testWfHandlerComEcimRemoveTrustTaskHandler starts ------------------");

        final String nodeName = NodeSecurityDataConstants.NODE_NAME3;

        dataRadioNodesSetup.deleteAllNodes();

        dataRadioNodesSetup.createComEcimNode(nodeName, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");

        final RemoveTrustOAMTask task = new RemoveTrustOAMTask(NodeSecurityDataConstants.MECONTEXT_FDN3);
        task.setCertificateSN("123355467");
        task.setIssuer("Some issuer");
        task.setCertCategory(TrustedCertCategory.CORBA_PEERS.toString());

        workflowTaskService.processTask(task);

        log.info("----------- testWfHandlerComEcimRemoveTrustTaskHandler ends ------------------");
    }

    @Override
    public void testWfHandlerComEcimEnableOrDisableCRLCheckTaskHandlerOAM_ACTIVATED() throws Exception {

        log.info("----------- testWfHandlerComEcimEnableOrDisableCRLCheckTaskHandlerOAM_ACTIVATED starts ------------------");

        final String nodeName = NodeSecurityDataConstants.NODE_NAME3;

        dataRadioNodesSetup.deleteAllNodes();

        dataRadioNodesSetup.createComEcimNode(nodeName, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");

        final ComEcimEnableOrDisableCRLCheckTask task = new ComEcimEnableOrDisableCRLCheckTask(NodeSecurityDataConstants.MECONTEXT_FDN3);
        task.setCertType("OAM");
        task.setCrlCheckStatus("ACTIVATED");
        workflowTaskService.processTask(task);

        log.info("----------- testWfHandlerComEcimEnableOrDisableCRLCheckTaskHandlerOAM_ACTIVATED ends ------------------");
    }

    @Override
    public void testWfHandlerComEcimEnableOrDisableCRLCheckTaskHandlerIPSEC_ACTIVATED() throws Exception {

        log.info("----------- testWfHandlerComEcimEnableOrDisableCRLCheckTaskHandlerIPSEC_ACTIVATED starts ------------------");

        final String nodeName = NodeSecurityDataConstants.NODE_NAME3;

        dataRadioNodesSetup.deleteAllNodes();

        dataRadioNodesSetup.createComEcimNode(nodeName, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");

        final ComEcimEnableOrDisableCRLCheckTask task = new ComEcimEnableOrDisableCRLCheckTask(NodeSecurityDataConstants.MECONTEXT_FDN3);
        task.setCertType("IPSEC");
        task.setCrlCheckStatus("ACTIVATED");
        workflowTaskService.processTask(task);

        log.info("----------- testWfHandlerComEcimEnableOrDisableCRLCheckTaskHandlerIPSEC_ACTIVATED ends ------------------");
    }

    @Override
    public void testWfHandlerComEcimEnableOrDisableCRLCheckTaskHandlerOAM_DEACTIVATED() throws Exception {

        log.info("----------- testWfHandlerComEcimEnableOrDisableCRLCheckTaskHandlerOAM_DEACTIVATED starts ------------------");

        final String nodeName = NodeSecurityDataConstants.NODE_NAME3;

        dataRadioNodesSetup.deleteAllNodes();

        dataRadioNodesSetup.createComEcimNode(nodeName, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");

        final ComEcimEnableOrDisableCRLCheckTask task = new ComEcimEnableOrDisableCRLCheckTask(NodeSecurityDataConstants.MECONTEXT_FDN3);
        task.setCertType("OAM");
        task.setCrlCheckStatus("DEACTIVATED");
        workflowTaskService.processTask(task);

        log.info("----------- testWfHandlerComEcimEnableOrDisableCRLCheckTaskHandlerOAM_DEACTIVATED ends ------------------");
    }

    @Override
    public void testWfHandlerComEcimEnableOrDisableCRLCheckTaskHandlerIPSEC_DEACTIVATED() throws Exception {

        log.info("----------- testWfHandlerComEcimEnableOrDisableCRLCheckTaskHandlerIPSEC_DEACTIVATED starts ------------------");

        final String nodeName = NodeSecurityDataConstants.NODE_NAME3;

        dataRadioNodesSetup.deleteAllNodes();

        dataRadioNodesSetup.createComEcimNode(nodeName, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");

        final ComEcimEnableOrDisableCRLCheckTask task = new ComEcimEnableOrDisableCRLCheckTask(NodeSecurityDataConstants.MECONTEXT_FDN3);
        task.setCertType("IPSEC");
        task.setCrlCheckStatus("DEACTIVATED");
        workflowTaskService.processTask(task);

        log.info("----------- testWfHandlerComEcimEnableOrDisableCRLCheckTaskHandlerIPSEC_DEACTIVATED ends ------------------");
    }

    @Override
    public void testWfHandlerCleanupM2MUserAndSmrsTask() throws Exception {

        log.info("----------- testWfHandlerCleanupM2MUserAndSmrsTask starts ------------------");

        final String nodeName = NodeSecurityDataConstants.NODE_NAME3;

        dataRadioNodesSetup.deleteAllNodes();

        dataRadioNodesSetup.createComEcimNode(nodeName, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");

        final CleanupM2MUserAndSmrsTask task = new CleanupM2MUserAndSmrsTask(NodeSecurityDataConstants.MECONTEXT_FDN3);
        task.setTrustedCategory(TrustedCertCategory.CORBA_PEERS.toString());
        workflowTaskService.processTask(task);

        log.info("----------- testWfHandlerCleanupM2MUserAndSmrsTask ends ------------------");
    }

    @Override
    public void testWfHandlerComEcimPrepareCheckOnDemandCrlDownloadActionProgressTask() throws Exception {

        log.info("----------- testWfHandlerComEcimPrepareCheckOnDemandCrlDownloadActionProgressTask starts ------------------");

        final String nodeName = NodeSecurityDataConstants.NODE_NAME3;

        dataRadioNodesSetup.deleteAllNodes();

        dataRadioNodesSetup.createComEcimNode(nodeName, "SYNCHRONIZED", SecurityLevel.LEVEL_2, "1");

        final ComEcimPrepareCheckOnDemandCrlDownloadActionProgressTask task = new ComEcimPrepareCheckOnDemandCrlDownloadActionProgressTask(
                NodeSecurityDataConstants.MECONTEXT_FDN3);
        workflowTaskService.processTask(task);

        log.info("----------- testWfHandlerComEcimPrepareCheckOnDemandCrlDownloadActionProgressTask ends ------------------");
    }

}
