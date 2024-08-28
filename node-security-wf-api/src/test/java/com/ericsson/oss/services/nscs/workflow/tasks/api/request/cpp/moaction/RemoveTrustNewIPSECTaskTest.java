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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;

public class RemoveTrustNewIPSECTaskTest {

    private static final String NODE_FDN = "ERBS_01";

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Test
    public void test_RemoveTrustNewIPSECTask() {
        log.info("test_RemoveTrustNewIPSECTask");
        final RemoveTrustNewIPSECTask task = new RemoveTrustNewIPSECTask();
        assertNotNull("Task is null!", task);
        assertEquals("Unexpected Task type", WorkflowTaskType.CPP_REMOVE_TRUST_NEW_IPSEC, task.getTaskType());
        assertEquals("Unexpected short description", RemoveTrustNewIPSECTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_RemoveTrustNewIPSECTask_NoDefault() {
        log.info("test_RemoveTrustNewIPSECTask");
        final RemoveTrustNewIPSECTask task = new RemoveTrustNewIPSECTask(NODE_FDN);
        assertNotNull("Task is null!", task);
        assertEquals("Unexpected Task type", WorkflowTaskType.CPP_REMOVE_TRUST_NEW_IPSEC, task.getTaskType());
        assertEquals("Unexpected Name", NODE_FDN, task.getNodeFdn());
        assertEquals("Unexpected short description", RemoveTrustNewIPSECTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_RemoveTrustNewIPSECTask_setCertificateSN() {
        log.info("test_RemoveTrustNewIPSECTask_setCertificateSN");
        final RemoveTrustNewIPSECTask task = new RemoveTrustNewIPSECTask();
        assertNotNull("Task is null!", task);
        final String certificateSN = "12334545";
        task.setCertificateSN(certificateSN);
        assertEquals("Unexpected Certificate SN", certificateSN, task.getCertificateSN());
        assertEquals("Unexpected short description", RemoveTrustNewIPSECTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_RemoveTrustNewIPSECTask_setCertificateIssuer() {
        log.info("test_RemoveTrustNewIPSECTask_setCertificateIssuer");
        final RemoveTrustNewIPSECTask task = new RemoveTrustNewIPSECTask();
        assertNotNull("Task is null!", task);
        final String certificateIssuer = "Some Issuer";
        task.setIssuer(certificateIssuer);
        assertEquals("Unexpected Certificate SN", certificateIssuer, task.getIssuer());
        assertEquals("Unexpected short description", RemoveTrustNewIPSECTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

}
