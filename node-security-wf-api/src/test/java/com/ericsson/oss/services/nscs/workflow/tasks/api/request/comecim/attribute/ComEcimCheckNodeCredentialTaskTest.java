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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;

public class ComEcimCheckNodeCredentialTaskTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private static final String NODE_FDN = "ERBS_01";

    @Test
    public void test_ComEcimCheckNodeCredentialTaskTest() {
        log.info("test_ComEcimCheckNodeCredentialTaskTest");
        final ComEcimCheckNodeCredentialTask task = new ComEcimCheckNodeCredentialTask();
        assertNotNull("Task is null!", task);
        assertEquals("Unexpected Task type", WorkflowTaskType.COM_ECIM_CHECK_NODE_CREDENTIAL, task.getTaskType());
        assertEquals("Unexpected short description", ComEcimCheckNodeCredentialTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_ComEcimCheckNodeCredentialTaskTest_NoDefault() {
        log.info("test_ComEcimCheckNodeCredentialTaskTest_NoDefault");
        final ComEcimCheckNodeCredentialTask task = new ComEcimCheckNodeCredentialTask(NODE_FDN);
        assertNotNull("Task is null!", task);
        assertEquals("Unexpected task type", WorkflowTaskType.COM_ECIM_CHECK_NODE_CREDENTIAL, task.getTaskType());
        assertEquals("Unexpected FDN", NODE_FDN, task.getNodeFdn());
        assertNull("Unexpected enrollment mode", task.getEnrollmentMode());
        assertNull("Unexpected trusted cert category", task.getTrustedCertCategory());
        assertEquals("Unexpected short description", ComEcimCheckNodeCredentialTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_ComEcimCheckNodeCredentialTaskTest_setEnrollmentMode() {
        log.info("test_ComEcimCheckNodeCredentialTaskTest_setEnrollmentMode");
        final EnrollmentMode enrollmentMode = EnrollmentMode.ONLINE_SCEP;
        final ComEcimCheckNodeCredentialTask task = new ComEcimCheckNodeCredentialTask(NODE_FDN);
        assertNotNull("Task is null!", task);
        task.setEnrollmentMode(enrollmentMode.toString());
        assertEquals("Unexpected task type", WorkflowTaskType.COM_ECIM_CHECK_NODE_CREDENTIAL, task.getTaskType());
        assertEquals("Unexpected fdn", NODE_FDN, task.getNodeFdn());
        assertEquals("Unexpected enrollment mode", enrollmentMode, EnrollmentMode.valueOf(task.getEnrollmentMode()));
        assertEquals("Unexpected short description", ComEcimCheckNodeCredentialTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_ComEcimCheckNodeCredentialTaskTest_getInvalidEnrollmentMode() {
        log.info("test_ComEcimCheckNodeCredentialTaskTest_getInvalidEnrollmentMode");
        final EnrollmentMode enrollmentMode = EnrollmentMode.ONLINE_SCEP;
        final ComEcimCheckNodeCredentialTask task = new ComEcimCheckNodeCredentialTask(NODE_FDN);
        assertNotNull("Task is null!", task);
        task.setEnrollmentMode(enrollmentMode.toString() + "___invalid");
        EnrollmentMode.valueOf(task.getEnrollmentMode());
        assertEquals("Unexpected short description", ComEcimCheckNodeCredentialTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test(expected = NullPointerException.class)
    public void test_ComEcimCheckNodeCredentialTaskTest_getNullEnrollmentMode() {
        log.info("test_ComEcimCheckNodeCredentialTaskTest_getNullEnrollmentMode");
        final ComEcimCheckNodeCredentialTask task = new ComEcimCheckNodeCredentialTask(NODE_FDN);
        assertNotNull("Task is null!", task);
        task.setEnrollmentMode(null);
        EnrollmentMode.valueOf(task.getEnrollmentMode());
    }

    @Test
    public void test_ComEcimCheckNodeCredentialTaskTest_setOamTrustedCertCategory() {
        log.info("test_ComEcimCheckNodeCredentialTaskTest_setOamTrustedCertCategory");
        final TrustedCertCategory category = TrustedCertCategory.CORBA_PEERS;
        final ComEcimCheckNodeCredentialTask task = new ComEcimCheckNodeCredentialTask(NODE_FDN);
        assertNotNull("Task is null!", task);
        task.setTrustedCertCategory(category.toString());
        assertEquals("Unexpected Task type", WorkflowTaskType.COM_ECIM_CHECK_NODE_CREDENTIAL, task.getTaskType());
        assertEquals("Unexpected fdn", NODE_FDN, task.getNodeFdn());
        assertEquals("Unexpected trusted Category", category, TrustedCertCategory.valueOf(task.getTrustedCertCategory()));
        assertEquals("Unexpected short description", ComEcimCheckNodeCredentialTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_ComEcimCheckNodeCredentialTaskTest_setIpSecTrustedCertCategory() {
        log.info("test_ComEcimCheckNodeCredentialTaskTest_setIpSecTrustedCertCategory");
        final TrustedCertCategory category = TrustedCertCategory.IPSEC;
        final ComEcimCheckNodeCredentialTask task = new ComEcimCheckNodeCredentialTask(NODE_FDN);
        assertNotNull("Task is null!", task);
        task.setTrustedCertCategory(category.toString());
        assertEquals("Unexpected Task type", WorkflowTaskType.COM_ECIM_CHECK_NODE_CREDENTIAL, task.getTaskType());
        assertEquals("Unexpected fdn", NODE_FDN, task.getNodeFdn());
        assertEquals("Unexpected trusted Category", category, TrustedCertCategory.valueOf(task.getTrustedCertCategory()));
        assertEquals("Unexpected short description", ComEcimCheckNodeCredentialTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_ComEcimCheckNodeCredentialTaskTest_getInvalidTrustedCertCategory() {
        log.info("test_ComEcimCheckNodeCredentialTaskTest_getInvalidTrustedCertCategory");
        final TrustedCertCategory category = TrustedCertCategory.IPSEC;
        final ComEcimCheckNodeCredentialTask task = new ComEcimCheckNodeCredentialTask(NODE_FDN);
        assertNotNull("Task is null!", task);
        task.setTrustedCertCategory(category.toString() + "___invalid");
        TrustedCertCategory.valueOf(task.getTrustedCertCategory());
    }

    @Test(expected = NullPointerException.class)
    public void test_ComEcimCheckNodeCredentialTaskTest_getNullTrustedCertCategory() {
        log.info("test_ComEcimCheckNodeCredentialTaskTest_getNullTrustedCertCategory");
        final ComEcimCheckNodeCredentialTask task = new ComEcimCheckNodeCredentialTask(NODE_FDN);
        assertNotNull("Task is null!", task);
        task.setTrustedCertCategory(null);
        TrustedCertCategory.valueOf(task.getTrustedCertCategory());
    }
}
