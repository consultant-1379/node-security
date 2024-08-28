/*
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package com.ericsson.oss.services.nscs.workflow.tasks.api.request;

import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.ReadCertEnrollStateTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.ReadFileTransferClientModeTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.ReadTrustedCertificateInstallationFailureTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.CancelCertEnrollmentTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.ClearInstallTrustFlagsTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.IssueInitCertEnrollmentIpSecTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.IssueInitCertEnrollmentTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.ssh.DisableCorbaSecurityTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.ssh.EnableCorbaSecurityTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.ssh.EnableSecureFileTransferClientModeTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.pki.RevokeNodeCertificateTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.ssh.ConfigureSSHKeyGenerationTask;

/**
 * Unit tests for WorkflowTask implementations
 *
 * @author ealemca
 */
@RunWith(Parameterized.class)
public class WorkflowTasksTest {

    private static final String NODE_FDN = "ERBS_01";

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private final String name;
    private final WorkflowTask task;

    public WorkflowTasksTest(final String name, final WorkflowTask task) {
        this.name = name;
        this.task = task;
    }

    @Parameterized.Parameters
    public static List<Object[]> workflowTaskInstances() {
        return Arrays.asList(new Object[][] { { "ReadCertEnrollStateTask()", new ReadCertEnrollStateTask() },
                { "ReadCertEnrollStateTask(NODE_FDN)", new ReadCertEnrollStateTask(NODE_FDN) },
                { "ReadFileTransferClientModeTask()", new ReadFileTransferClientModeTask() },
                { "ReadFileTransferClientModeTask(NODE_FDN)", new ReadFileTransferClientModeTask(NODE_FDN) },
                { "ReadTrustedCertificateInstallationFailureTask()", new ReadTrustedCertificateInstallationFailureTask() },
                { "ReadTrustedCertificateInstallationFailureTask(NODE_FDN)", new ReadTrustedCertificateInstallationFailureTask(NODE_FDN) },
                { "DisableCorbaSecurityTask()", new DisableCorbaSecurityTask() },
                { "DisableCorbaSecurityTask(NODE_FDN)", new DisableCorbaSecurityTask(NODE_FDN) },
                { "EnableCorbaSecurityTask()", new EnableCorbaSecurityTask() },
                { "EnableCorbaSecurityTask(NODE_FDN)", new EnableCorbaSecurityTask(NODE_FDN) },
                { "EnableSecureFileTransferClientModeTask()", new EnableSecureFileTransferClientModeTask() },
                { "EnableSecureFileTransferClientModeTask(NODE_FDN)", new EnableSecureFileTransferClientModeTask(NODE_FDN) },
                { "CancelCertEnrollmentTask()", new CancelCertEnrollmentTask() },
                { "CancelCertEnrollmentTask(NODE_FDN)", new CancelCertEnrollmentTask(NODE_FDN) },
                { "ClearInstallTrustFlagsTask()", new ClearInstallTrustFlagsTask() },
                { "ClearInstallTrustFlagsTask(NODE_FDN)", new ClearInstallTrustFlagsTask(NODE_FDN) },
                { "ConfigureSSHKeyGenerationTask()", new ConfigureSSHKeyGenerationTask() },
                { "ConfigureSSHKeyGenerationTask(NODE_FDN)", new ConfigureSSHKeyGenerationTask(NODE_FDN) },
                { "RevokeNodeCertificateTask()", new RevokeNodeCertificateTask() },
                { "RevokeNodeCertificateTask(NODE_FDN)", new RevokeNodeCertificateTask(NODE_FDN) },
                { "IssueInitCertEnrollmentIpSecTask()", new IssueInitCertEnrollmentIpSecTask() },
                { "IssueInitCertEnrollmentIpSecTask()", new IssueInitCertEnrollmentIpSecTask(NODE_FDN) },
                { "IssueInitCertEnrollmentIpSecTask()", new IssueInitCertEnrollmentIpSecTask(NODE_FDN, 2, "alt_name", "alt_name_type") },
                { "IssueInitCertEnrollmentTask()", new IssueInitCertEnrollmentTask() },
                { "IssueInitCertEnrollmentTask(NODE_FDN)", new IssueInitCertEnrollmentTask(NODE_FDN) },
                { "IssueInitCertEnrollmentTask(NODE_FDN, timeout)", new IssueInitCertEnrollmentTask(NODE_FDN, 2) }, });
    }

    @Test
    public void testTaskConstructors() {
        log.info("Testing WorkflowTask instance [{}]", this.name);
        assertNotNull(String.format("Task instance is null [%s]", this.name), this.task);
    }

}