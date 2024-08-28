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

public class IssueInitTrustedCertIpSecEnrollmentTaskTest {

	private static final String NODE_FDN = "ERBS_01";

	private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@Test
    public void test_IssueInitTrustedCertIpSecEnrollmentTask() {
        log.info("test_IssueInitTrustedCertIpSecEnrollmentTask");
        IssueInitTrustedCertIpSecEnrollmentTask task = new IssueInitTrustedCertIpSecEnrollmentTask();
        assertNotNull("Task is null!", task);
        assertEquals("Unexpected Task type", WorkflowTaskType.CPP_ISSUE_TRUSTED_CERTIFICATE_IPSEC, task.getTaskType());
    }

	@Test
    public void test_IssueInitTrustedCertIpSecEnrollmentTask_NoDefault() {
        log.info("test_IssueInitTrustedCertIpSecEnrollmentTask_NoDefault");
        String trustCert = "Some cert";
        String ca = "";
        IssueInitTrustedCertIpSecEnrollmentTask task = new IssueInitTrustedCertIpSecEnrollmentTask(NODE_FDN, trustCert, ca);
        assertNotNull("Task is null!", task);
        assertEquals("Unexpected Task type", WorkflowTaskType.CPP_ISSUE_TRUSTED_CERTIFICATE_IPSEC, task.getTaskType());
        assertEquals("Unexpected node", NODE_FDN, task.getNodeFdn());
        assertEquals("Unexpected Trusted Cert", trustCert, task.getTrustCerts());
    }
	
	@Test
    public void test_IssueInitTrustedCertIpSecEnrollmentTask_getTrustCerts() {
        log.info("test_IssueInitTrustedCertIpSecEnrollmentTask_getTrustCerts");
        String expectedTrustCert = "cert";
        String ca = "";
        IssueInitTrustedCertIpSecEnrollmentTask task = new IssueInitTrustedCertIpSecEnrollmentTask(NODE_FDN, "abc", ca);
        task.setTrustCerts(expectedTrustCert);   
        assertEquals("Unexpected Trusted Cert", expectedTrustCert, task.getTrustCerts());
    }
	
	@Test
    public void test_IssueInitTrustedCertIpSecEnrollmentTask_setTrustCerts() {
        log.info("test_IssueInitTrustedCertIpSecEnrollmentTask_setTrustCerts");
        String expectedTrustCert = "cert";
        String ca = "";
        IssueInitTrustedCertIpSecEnrollmentTask task = new IssueInitTrustedCertIpSecEnrollmentTask(NODE_FDN, "abc",ca);
        task.setTrustCerts(expectedTrustCert);   
        assertEquals("Unexpected Trusted Cert", expectedTrustCert, task.getTrustCerts());
    }
	
	@Test
    public void test_IssueInitTrustedCertIpSecEnrollmentTask_setTrustedCertificateAuthority() {
        log.info("test_IssueInitTrustedCertIpSecEnrollmentTask_setTrustedCertificateAuthority");
        TrustedCertCategory trustedCertCategory = TrustedCertCategory.IPSEC;
        String trustedCertificateAuthority = "NE_OAM_CA";
        IssueInitTrustedCertIpSecEnrollmentTask task = new IssueInitTrustedCertIpSecEnrollmentTask(NODE_FDN, trustedCertCategory.toString(), null);
        String expectedTrustedCA = trustedCertificateAuthority;
		task.setTrustedCertificateAuthority(trustedCertificateAuthority);
        assertEquals("Unexpected Trusted CA", expectedTrustedCA, task.getTrustedCertificateAuthority());
    }
	
	@Test
    public void test_IssueInitTrustedCertIpSecEnrollmentTask_getTrustedCertificateAuthority() {
        log.info("test_IssueInitTrustedCertIpSecEnrollmentTask_getTrustedCertificateAuthority");
        TrustedCertCategory trustedCertCategory = TrustedCertCategory.IPSEC;
        String expectedCA = "NE_OAM_CA";
        String trustedCA = expectedCA;
        IssueInitTrustedCertIpSecEnrollmentTask task = new IssueInitTrustedCertIpSecEnrollmentTask(NODE_FDN, trustedCertCategory.toString(), trustedCA);
        assertEquals("Unexpected Trusted CA", expectedCA, task.getTrustedCertificateAuthority());
    }

}
