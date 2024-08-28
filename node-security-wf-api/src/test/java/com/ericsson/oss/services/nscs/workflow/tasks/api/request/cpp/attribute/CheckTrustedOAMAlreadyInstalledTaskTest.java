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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;

public class CheckTrustedOAMAlreadyInstalledTaskTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private static final String NODE_FDN = "ERBS_01";

    @Test
    public void test_CheckTrustedOAMAlreadyInstalledTaskTest() {
        log.info("test_CheckTrustedOAMAlreadyInstalledTaskTest");
        final CheckTrustedOAMAlreadyInstalledTask task = new CheckTrustedOAMAlreadyInstalledTask();
        assertNotNull("Task is null!", task);
        assertEquals("Unexpected Task type", WorkflowTaskType.CPP_CHECK_TRUSTED_OAM_ALREADY_INSTALLED, task.getTaskType());
        assertEquals("Unexpected short description", CheckTrustedOAMAlreadyInstalledTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_CheckTrustedOAMAlreadyInstalledTaskTest_NoDefault() {
        log.info("test_CheckTrustedOAMAlreadyInstalledTaskTest_NoDefault");
        final String trustedCA = "";
        final CheckTrustedOAMAlreadyInstalledTask task = new CheckTrustedOAMAlreadyInstalledTask(NODE_FDN, TrustedCertCategory.CORBA_PEERS.toString(),
                trustedCA);
        assertNotNull("Task is null!", task);
        assertEquals("Unexpected Task type", WorkflowTaskType.CPP_CHECK_TRUSTED_OAM_ALREADY_INSTALLED, task.getTaskType());
        assertEquals("Unexpected Trust Certs", TrustedCertCategory.CORBA_PEERS.toString(), task.getTrustCerts());
        assertEquals("Unexpected short description", CheckTrustedOAMAlreadyInstalledTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_CheckTrustedOAMAlreadyInstalledTaskTest_SetTrustCerts() {
        log.info("test_CheckTrustedOAMAlreadyInstalledTaskTest_SetTrustCerts");
        final String trustedCA = "";
        final CheckTrustedOAMAlreadyInstalledTask task = new CheckTrustedOAMAlreadyInstalledTask(NODE_FDN, TrustedCertCategory.CORBA_PEERS.toString(),
                trustedCA);
        final String expectedTrustCerts = TrustedCertCategory.IPSEC.toString();
        task.setTrustCerts(expectedTrustCerts);
        assertEquals("Unexpected Trust Certs", expectedTrustCerts, task.getTrustCerts());
        assertEquals("Unexpected short description", CheckTrustedOAMAlreadyInstalledTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_CheckTrustedOAMAlreadyInstalledTask_setTrustedCertificateAuthority() {
        log.info("test_CheckTrustedOAMAlreadyInstalledTask_setTrustedCertificateAuthority");
        final TrustedCertCategory trustedCertCategory = TrustedCertCategory.IPSEC;
        final String trustedCertificateAuthority = "NE_OAM_CA";
        final CheckTrustedOAMAlreadyInstalledTask task = new CheckTrustedOAMAlreadyInstalledTask(NODE_FDN, trustedCertCategory.toString(), null);
        final String expectedTrustedCA = trustedCertificateAuthority;
        task.setTrustedCertificateAuthority(trustedCertificateAuthority);
        assertEquals("Unexpected Trusted CA", expectedTrustedCA, task.getTrustedCertificateAuthority());
        assertEquals("Unexpected short description", CheckTrustedOAMAlreadyInstalledTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_CheckTrustedOAMAlreadyInstalledTask_getTrustedCertificateAuthority() {
        log.info("test_CheckTrustedOAMAlreadyInstalledTask_getTrustedCertificateAuthority");
        final TrustedCertCategory trustedCertCategory = TrustedCertCategory.IPSEC;
        final String expectedCA = "NE_OAM_CA";
        final String trustedCA = expectedCA;
        final CheckTrustedOAMAlreadyInstalledTask task = new CheckTrustedOAMAlreadyInstalledTask(NODE_FDN, trustedCertCategory.toString(), trustedCA);
        assertEquals("Unexpected Trusted CA", expectedCA, task.getTrustedCertificateAuthority());
        assertEquals("Unexpected short description", CheckTrustedOAMAlreadyInstalledTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

}
