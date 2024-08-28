/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;

public class IssueInitTrustedCertEnrollmentTaskTest {

    private static final String NODE_FDN = "ERBS_01";

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Test
    public void test_IssueInitTrustedCertEnrollmentTask() {
        log.info("test_IssueInitCertEnrollmentTask");
        final IssueInitTrustedCertEnrollmentTask task = new IssueInitTrustedCertEnrollmentTask();
        assertNotNull("Task is null!", task);
        assertEquals("Unexpected Task type", WorkflowTaskType.CPP_ISSUE_TRUSTED_CERTIFICATE, task.getTaskType());
        assertEquals("Unexpected short description", IssueInitTrustedCertEnrollmentTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitTrustedCertEnrollmentTask_NoDefault() {
        log.info("test_IssueInitCertEnrollmentTask_NoDefault");
        final TrustedCertCategory expectedTrustedCertCategory = TrustedCertCategory.CORBA_PEERS;
        final String trustedCertificateAuthority = "";
        final IssueInitTrustedCertEnrollmentTask task = new IssueInitTrustedCertEnrollmentTask(NODE_FDN, expectedTrustedCertCategory.toString(),
                trustedCertificateAuthority);
        assertNotNull("Task is null!", task);
        assertEquals("Unexpected Task type", WorkflowTaskType.CPP_ISSUE_TRUSTED_CERTIFICATE, task.getTaskType());
        assertEquals("Unexpected node", NODE_FDN, task.getNodeFdn());
        assertEquals("Unexpected Trusted Category", expectedTrustedCertCategory, task.getTrustCategory());
        assertEquals("Unexpected short description", IssueInitTrustedCertEnrollmentTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitTrustedCertEnrollmentTask_TrustedCertCategoryIsNull() {
        log.info("test_IssueInitTrustedCertEnrollmentTask_TrustedCertCategoryIsNull");
        final TrustedCertCategory expectedTrustedCertCategory = TrustedCertCategory.CORBA_PEERS;
        final String trustedCertificateAuthority = "";
        final IssueInitTrustedCertEnrollmentTask task = new IssueInitTrustedCertEnrollmentTask(NODE_FDN, null, trustedCertificateAuthority);
        assertNotNull("Task is null!", task);
        assertEquals("Unexpected Task type", WorkflowTaskType.CPP_ISSUE_TRUSTED_CERTIFICATE, task.getTaskType());
        assertEquals("Unexpected node", NODE_FDN, task.getNodeFdn());
        assertEquals("Unexpected Trusted Category", expectedTrustedCertCategory, task.getTrustCategory());
        assertEquals("Unexpected short description", IssueInitTrustedCertEnrollmentTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitTrustedCertEnrollmentTask_setTrustCategory_asString() {
        log.info("test_IssueInitTrustedCertEnrollmentTask_setTrustCategory_String");
        final String trustedCertificateAuthority = "";
        final IssueInitTrustedCertEnrollmentTask task = new IssueInitTrustedCertEnrollmentTask(NODE_FDN, null, trustedCertificateAuthority);
        final TrustedCertCategory expectedTrustedCertCategory = TrustedCertCategory.IPSEC;
        final String category = expectedTrustedCertCategory.toString();
        task.setTrustCategory(category);
        assertEquals("Unexpected Trusted Category", expectedTrustedCertCategory, task.getTrustCategory());
        assertEquals("Unexpected short description", IssueInitTrustedCertEnrollmentTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitTrustedCertEnrollmentTask_setTrustCategory_asTrustCategory() {
        log.info("test_IssueInitTrustedCertEnrollmentTask_setTrustCategory_asTrustCategory");
        final String trustedCertificateAuthority = "";
        final IssueInitTrustedCertEnrollmentTask task = new IssueInitTrustedCertEnrollmentTask(NODE_FDN, null, trustedCertificateAuthority);
        final TrustedCertCategory expectedTrustedCertCategory = TrustedCertCategory.IPSEC;
        task.setTrustCategory(expectedTrustedCertCategory);
        assertEquals("Unexpected Trusted Category", expectedTrustedCertCategory, task.getTrustCategory());
        assertEquals("Unexpected short description", IssueInitTrustedCertEnrollmentTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitTrustedCertEnrollmentTask_getTrustCategory() {
        log.info("test_IssueInitTrustedCertEnrollmentTask_getTrustCategory");
        final TrustedCertCategory expectedTrustedCertCategory = TrustedCertCategory.IPSEC;
        final String trustedCertificateAuthority = "";
        final IssueInitTrustedCertEnrollmentTask task = new IssueInitTrustedCertEnrollmentTask(NODE_FDN, expectedTrustedCertCategory.toString(),
                trustedCertificateAuthority);
        assertEquals("Unexpected Trusted Category", expectedTrustedCertCategory, task.getTrustCategory());
        assertEquals("Unexpected short description", IssueInitTrustedCertEnrollmentTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitTrustedCertEnrollmentTask_getTrustCategory_asString() {
        log.info("test_IssueInitTrustedCertEnrollmentTask_getTrustCategory_asString");
        final TrustedCertCategory expectedTrustedCertCategory = TrustedCertCategory.IPSEC;
        assertEquals("Unexpected Trusted Category", expectedTrustedCertCategory,
                IssueInitTrustedCertEnrollmentTask.getTrustCategory(expectedTrustedCertCategory.toString()));
    }

    @Test
    public void test_IssueInitTrustedCertEnrollmentTask_getTrustCategory_asNullString() {
        log.info("test_IssueInitTrustedCertEnrollmentTask_getTrustCategory_asString");
        final TrustedCertCategory expectedTrustedCertCategory = TrustedCertCategory.CORBA_PEERS;
        assertEquals("Unexpected Trusted Category", expectedTrustedCertCategory, IssueInitTrustedCertEnrollmentTask.getTrustCategory(null));
    }

    @Test
    public void test_IssueInitTrustedCertEnrollmentTask_setTrustedCertificateAuthority() {
        log.info("test_IssueInitTrustedCertEnrollmentTask_setTrustedCertificateAuthority");
        final TrustedCertCategory trustedCertCategory = TrustedCertCategory.IPSEC;
        final String trustedCertificateAuthority = "NE_OAM_CA";
        final IssueInitTrustedCertEnrollmentTask task = new IssueInitTrustedCertEnrollmentTask(NODE_FDN, trustedCertCategory.toString(), null);
        final String expectedTrustedCA = trustedCertificateAuthority;
        task.setTrustedCertificateAuthority(trustedCertificateAuthority);
        assertEquals("Unexpected Trusted CA", expectedTrustedCA, task.getTrustedCertificateAuthority());
        assertEquals("Unexpected short description", IssueInitTrustedCertEnrollmentTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitTrustedCertEnrollmentTask_getTrustedCertificateAuthority() {
        log.info("test_IssueInitTrustedCertEnrollmentTask_getTrustedCertificateAuthority");
        final TrustedCertCategory trustedCertCategory = TrustedCertCategory.IPSEC;
        final String expectedCA = "NE_OAM_CA";
        final String trustedCA = expectedCA;
        final IssueInitTrustedCertEnrollmentTask task = new IssueInitTrustedCertEnrollmentTask(NODE_FDN, trustedCertCategory.toString(), trustedCA);
        assertEquals("Unexpected Trusted CA", expectedCA, task.getTrustedCertificateAuthority());
        assertEquals("Unexpected short description", IssueInitTrustedCertEnrollmentTask.SHORT_DESCRIPTION, task.getShortDescription());
    }
}
