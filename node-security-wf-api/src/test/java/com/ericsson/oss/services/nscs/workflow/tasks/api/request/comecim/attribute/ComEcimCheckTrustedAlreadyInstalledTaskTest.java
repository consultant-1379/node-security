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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;

public class ComEcimCheckTrustedAlreadyInstalledTaskTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private static final String NODE_FDN = "ERBS_01";

    @Test
    public void test_ComEcimCheckTrustedAlreadyInstalledTaskTest() {
        log.info("test_ComEcimCheckTrustedAlreadyInstalledTaskTest");
        final ComEcimCheckTrustedAlreadyInstalledTask task = new ComEcimCheckTrustedAlreadyInstalledTask();
        assertNotNull("Task is null!", task);
        assertEquals("Unexpected Task type", WorkflowTaskType.COM_ECIM_CHECK_TRUSTED_ALREADY_INSTALLED, task.getTaskType());
        assertEquals("Unexpected short description", ComEcimCheckTrustedAlreadyInstalledTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_ComEcimCheckTrustedAlreadyInstalledTaskTest_NoDefault() {
        log.info("test_ComEcimCheckTrustedAlreadyInstalledTaskTest_NoDefault");
        final TrustedCertCategory category = TrustedCertCategory.CORBA_PEERS;
        final ComEcimCheckTrustedAlreadyInstalledTask task = new ComEcimCheckTrustedAlreadyInstalledTask(NODE_FDN, category.toString());
        assertNotNull("Task is null!", task);
        assertEquals("Unexpected Task type", WorkflowTaskType.COM_ECIM_CHECK_TRUSTED_ALREADY_INSTALLED, task.getTaskType());
        assertEquals("Unexpected trusted Category", category.toString(), task.getTrustCerts());
        assertEquals("Unexpected short description", ComEcimCheckTrustedAlreadyInstalledTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_ComEcimCheckTrustedAlreadyInstalledTaskTest_getTrustCerts() {
        log.info("test_ComEcimCheckTrustedAlreadyInstalledTaskTest_getTrustCerts");
        final ComEcimCheckTrustedAlreadyInstalledTask task = new ComEcimCheckTrustedAlreadyInstalledTask(NODE_FDN, TrustedCertCategory.CORBA_PEERS.toString());
        final TrustedCertCategory category = TrustedCertCategory.IPSEC;
        task.setTrustCerts(category.toString());
        assertEquals("Unexpected trusted Category", category.toString(), task.getTrustCerts());
        assertEquals("Unexpected short description", ComEcimCheckTrustedAlreadyInstalledTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_ComEcimCheckTrustedAlreadyInstalledTask_setTrustedCertificateAuthority() {
        log.info("test_ComEcimCheckTrustedAlreadyInstalledTask_setTrustedCertificateAuthority");
        final TrustedCertCategory trustedCertCategory = TrustedCertCategory.IPSEC;
        final ComEcimCheckTrustedAlreadyInstalledTask task = new ComEcimCheckTrustedAlreadyInstalledTask(NODE_FDN, trustedCertCategory.toString());
        assertEquals("Unexpected short description", ComEcimCheckTrustedAlreadyInstalledTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_ComEcimCheckTrustedAlreadyInstalledTask_getTrustedCertificateAuthority() {
        log.info("test_ComEcimCheckTrustedAlreadyInstalledTask_getTrustedCertificateAuthority");
        final TrustedCertCategory trustedCertCategory = TrustedCertCategory.IPSEC;
        final ComEcimCheckTrustedAlreadyInstalledTask task = new ComEcimCheckTrustedAlreadyInstalledTask(NODE_FDN, trustedCertCategory.toString());
        assertEquals("Unexpected short description", ComEcimCheckTrustedAlreadyInstalledTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

}
