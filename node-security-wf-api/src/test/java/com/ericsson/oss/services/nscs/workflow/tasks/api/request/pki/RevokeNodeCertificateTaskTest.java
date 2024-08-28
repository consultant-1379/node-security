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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.pki;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;

public class RevokeNodeCertificateTaskTest {

    private static final String NODE_FDN = "ERBS_01";
    private static final String CERTIFICATE_ID = "ABC123";
    private static final String CERTIFICATE_AUTHORITY_ID = "AUTHO123";
    private static final String REVOCATION_REASON = "keyCompromise";

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Test
    public void test_RevokeNodeCertificateTask() {
        log.info("test_RevokeNodeCertificateTask");
        final RevokeNodeCertificateTask task = new RevokeNodeCertificateTask();
        assertEquals("Unexpected Task type", WorkflowTaskType.REVOKE_NODE_CERTIFICATE, task.getTaskType());
        assertEquals("Unexpected short description", RevokeNodeCertificateTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_RevokeNodeCertificateTask_NotDefault() {
        log.info("test_RevokeNodeCertificateTask_NotDefault");
        final RevokeNodeCertificateTask task = new RevokeNodeCertificateTask(NODE_FDN);
        assertEquals("Unexpected Task type", WorkflowTaskType.REVOKE_NODE_CERTIFICATE, task.getTaskType());
        assertEquals("Unexpected node", NODE_FDN, task.getNodeFdn());
        assertEquals("Unexpected short description", RevokeNodeCertificateTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_RevokeNodeCertificateTask_setCertificateId() {
        log.info("test_RevokeNodeCertificateTask_setCertificateId");
        final RevokeNodeCertificateTask task = new RevokeNodeCertificateTask(NODE_FDN);
        final String expectedCertificateId = CERTIFICATE_ID;
        task.setCertificateId(expectedCertificateId);
        assertEquals("Unexpected Certificate Id", expectedCertificateId, task.getCertificateId());
        assertEquals("Unexpected short description", RevokeNodeCertificateTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_RevokeNodeCertificateTask_getCertificateId() {
        log.info("test_RevokeNodeCertificateTask_getCertificateId");
        final RevokeNodeCertificateTask task = new RevokeNodeCertificateTask(NODE_FDN);
        final String expectedCertificateId = CERTIFICATE_ID;
        task.setCertificateId(expectedCertificateId);
        assertEquals("Unexpected Certificate Id", expectedCertificateId, task.getCertificateId());
        assertEquals("Unexpected short description", RevokeNodeCertificateTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_RevokeNodeCertificateTask_setCertificateAuthorityId() {
        log.info("test_RevokeNodeCertificateTask_setCertificateAuthorityId");
        final RevokeNodeCertificateTask task = new RevokeNodeCertificateTask(NODE_FDN);
        final String expectedCertificateAuthorityId = CERTIFICATE_AUTHORITY_ID;
        task.setCertificateAuthorityId(expectedCertificateAuthorityId);
        assertEquals("Unexpected Certificate Authority Id", expectedCertificateAuthorityId, task.getCertificateAuthorityId());
        assertEquals("Unexpected short description", RevokeNodeCertificateTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_RevokeNodeCertificateTask_getCertificateAuthorityId() {
        log.info("test_RevokeNodeCertificateTask_getCertificateAuthorityId");
        final RevokeNodeCertificateTask task = new RevokeNodeCertificateTask(NODE_FDN);
        final String expectedCertificateAuthorityId = CERTIFICATE_AUTHORITY_ID;
        task.setCertificateAuthorityId(expectedCertificateAuthorityId);
        assertEquals("Unexpected Certificate Authority Id", expectedCertificateAuthorityId, task.getCertificateAuthorityId());
        assertEquals("Unexpected short description", RevokeNodeCertificateTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_RevokeNodeCertificateTask_setRevocationReason() {
        log.info("test_RevokeNodeCertificateTask_setRevocationReason");
        final RevokeNodeCertificateTask task = new RevokeNodeCertificateTask(NODE_FDN);
        final String expectedRevocationReason = REVOCATION_REASON;
        task.setRevocationReason(expectedRevocationReason);
        assertEquals("Unexpected Revocation Reason", expectedRevocationReason, task.getRevocationReason());
        assertEquals("Unexpected short description", RevokeNodeCertificateTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_RevokeNodeCertificateTask_getRevocationReason() {
        log.info("test_RevokeNodeCertificateTask_getRevocationReason");
        final RevokeNodeCertificateTask task = new RevokeNodeCertificateTask(NODE_FDN);
        final String expectedRevocationReason = REVOCATION_REASON;
        task.setRevocationReason(expectedRevocationReason);
        assertEquals("Unexpected Revocation Reason", expectedRevocationReason, task.getRevocationReason());
        assertEquals("Unexpected short description", RevokeNodeCertificateTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

}
