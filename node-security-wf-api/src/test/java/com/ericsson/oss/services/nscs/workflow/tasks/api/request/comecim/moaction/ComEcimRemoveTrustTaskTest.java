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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.moaction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;

public class ComEcimRemoveTrustTaskTest {

    private static final String NODE_FDN = "ERBS_01";

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Test
    public void test_ComEcimRemoveTrustTask() {
        log.info("test_ComEcimRemoveTrustTask");
        final ComEcimRemoveTrustTask task = new ComEcimRemoveTrustTask();
        assertNotNull("Task is null!", task);
        assertEquals("Unexpected Task type", WorkflowTaskType.COM_ECIM_REMOVE_TRUST, task.getTaskType());
        assertEquals("Unexpected short description", ComEcimRemoveTrustTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_ComEcimRemoveTrustTask_NoDefault() {
        log.info("test_ComEcimRemoveTrustTask_NoDefault");
        final ComEcimRemoveTrustTask task = new ComEcimRemoveTrustTask(NODE_FDN);
        assertNotNull("Task is null!", task);
        assertEquals("Unexpected Task type", WorkflowTaskType.COM_ECIM_REMOVE_TRUST, task.getTaskType());
        assertEquals("Unexpected Name", NODE_FDN, task.getNodeFdn());
        assertEquals("Unexpected short description", ComEcimRemoveTrustTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_ComEcimRemoveTrustTask_setCertificateSN() {
        log.info("test_ComEcimRemoveTrustTask_setCertificateSN");
        final ComEcimRemoveTrustTask task = new ComEcimRemoveTrustTask();
        assertNotNull("Task is null!", task);
        final String certificateSN = "12334545";
        task.setCertificateSN(certificateSN);
        assertEquals("Unexpected Certificate SN", certificateSN, task.getCertificateSN());
        assertEquals("Unexpected short description", ComEcimRemoveTrustTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_ComEcimRemoveTrustTask_setCertificateIssuer() {
        log.info("test_ComEcimRemoveTrustTask_setCertificateIssuer");
        final ComEcimRemoveTrustTask task = new ComEcimRemoveTrustTask();
        assertNotNull("Task is null!", task);
        final String certificateIssuer = "Some Issuer";
        task.setIssuer(certificateIssuer);
        assertEquals("Unexpected Certificate SN", certificateIssuer, task.getIssuer());
        assertEquals("Unexpected short description", ComEcimRemoveTrustTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_ComEcimRemoveTrustTask_setCertificateCategory() {
        log.info("test_ComEcimRemoveTrustTask_setCertificateCategory");
        final ComEcimRemoveTrustTask task = new ComEcimRemoveTrustTask();
        assertNotNull("Task is null!", task);
        final TrustedCertCategory certificateCategory = TrustedCertCategory.CORBA_PEERS;
        final String certificateCategoryString = certificateCategory.toString();
        task.setCertCategory(certificateCategoryString);
        assertEquals("Unexpected Certificate Category", certificateCategoryString, task.getCertCategory());
        assertEquals("Unexpected short description", ComEcimRemoveTrustTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_ComEcimRemoveTrustTask_setOutputParams() {
        log.info("test_ComEcimRemoveTrustTask_setOutputParams");
        final ComEcimRemoveTrustTask task = new ComEcimRemoveTrustTask();
        assertNotNull("Task is null!", task);
        final Map<String, Serializable> outputParams = new HashMap<String, Serializable>();
        final String trustedCertificateFdn = "ManagedElement=NODE_NAME,SystemFunctions=1,SecM=1,CertM=1,TrustedCertificate=1";
        outputParams.put(WorkflowOutputParameterKeys.TRUSTED_CERTIFICATE_FDN.toString(), trustedCertificateFdn);
        task.setOutputParams(outputParams);
        assertEquals("Unexpected Output Params", outputParams, task.getOutputParams());
        assertEquals("Unexpected trusted certificate FDN", trustedCertificateFdn,
                task.getOutputParams().get(WorkflowOutputParameterKeys.TRUSTED_CERTIFICATE_FDN.toString()));
        assertEquals("Unexpected short description", ComEcimRemoveTrustTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

}
