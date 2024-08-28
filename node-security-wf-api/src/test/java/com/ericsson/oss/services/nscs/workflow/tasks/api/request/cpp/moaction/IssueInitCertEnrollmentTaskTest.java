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
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;

public class IssueInitCertEnrollmentTaskTest {

    private static final String NODE_FDN = "ERBS_01";

    private static final int rollbackTimeout = 2;

    private static final String entityProfileName = "Profile123";

    private static final SubjectAltNameStringType subjectAltName = new SubjectAltNameStringType("Subject alt name");

    private static final SubjectAltNameStringType expectedSubjectAltName = new SubjectAltNameStringType("Subject alt name test");

    private static final SubjectAltNameFormat subjectAltNameType = SubjectAltNameFormat.IPV4;

    private static final AlgorithmKeys expectedAlgoKey = AlgorithmKeys.RSA_8192;

    private static final EnrollmentMode expectedEnrollmentMode = EnrollmentMode.SCEP;

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Test
    public void test_IssueInitCertEnrollmentTask() {
        log.info("test_IssueInitCertEnrollmentTask");
        final IssueInitCertEnrollmentTask task = new IssueInitCertEnrollmentTask();
        assertNotNull("Task is null!", task);
        assertEquals("Unexpected Task type", WorkflowTaskType.CPP_ISSUE_CERT_ENROLLMENT, task.getTaskType());
        assertEquals("Unexpected short description", IssueInitCertEnrollmentTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitCertEnrollmentTask_NoDefault() {
        log.info("test_IssueInitCertEnrollmentTask_NoDefault");
        final IssueInitCertEnrollmentTask task = new IssueInitCertEnrollmentTask(NODE_FDN);
        assertNotNull("Task is null!", task);
        assertEquals("Unexpected Task type", WorkflowTaskType.CPP_ISSUE_CERT_ENROLLMENT, task.getTaskType());
        assertEquals("Unexpected node", NODE_FDN, task.getNodeFdn());
        assertEquals("Unexpected short description", IssueInitCertEnrollmentTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitCertEnrollmentTask_ConstructorWithTimeout() {
        log.info("test_IssueInitCertEnrollmentTask_ConstructorWithTimeout");
        final IssueInitCertEnrollmentTask task = new IssueInitCertEnrollmentTask(NODE_FDN, rollbackTimeout);
        assertNotNull("Task is null!", task);
        assertEquals("Unexpected Task type", WorkflowTaskType.CPP_ISSUE_CERT_ENROLLMENT, task.getTaskType());
        assertEquals("Unexpected node", NODE_FDN, task.getNodeFdn());
        assertEquals("Unexpected timeout", rollbackTimeout, task.getRollbackTimeout().intValue());
        assertEquals("Unexpected short description", IssueInitCertEnrollmentTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitCertEnrollmentTask_setAlgoKeySize() {
        log.info("test_IssueInitCertEnrollmentTask_setAlgoKeySize");
        final IssueInitCertEnrollmentTask task = new IssueInitCertEnrollmentTask(NODE_FDN);
        final AlgorithmKeys expectedAlgoKey = AlgorithmKeys.RSA_2048;
        final String expectedAlgoKeyString = expectedAlgoKey.toString();
        task.setAlgoKeySize(expectedAlgoKeyString);
        assertEquals("Unexpected AlgorithmKeys", expectedAlgoKey, task.getAlgoKeySize());
        assertEquals("Unexpected short description", IssueInitCertEnrollmentTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitCertEnrollmentTask_getAlgoKeySize() {
        log.info("test_IssueInitCertEnrollmentTask_getAlgoKeySize");
        final IssueInitCertEnrollmentTask task = new IssueInitCertEnrollmentTask(NODE_FDN);
        final String expectedAlgoKeyString = expectedAlgoKey.toString();
        task.setAlgoKeySize(expectedAlgoKeyString);
        assertEquals("Unexpected AlgorithmKeys", expectedAlgoKey, task.getAlgoKeySize());
        assertEquals("Unexpected short description", IssueInitCertEnrollmentTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitCertEnrollmentTask_getAlgoKeySize_Null() {
        log.info("test_IssueInitCertEnrollmentTask_getAlgoKeySize_Null");
        final IssueInitCertEnrollmentTask task = new IssueInitCertEnrollmentTask(NODE_FDN);
        // set invalid AlgorithmKeys
        final String expectedAlgoKeyString = expectedAlgoKey.toString() + "__invalid";
        task.setAlgoKeySize(expectedAlgoKeyString);
        assertNull("AlgorithmKeys must be null", task.getAlgoKeySize());
        assertEquals("Unexpected short description", IssueInitCertEnrollmentTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitCertEnrollmentTask_setEntityProfileName() {
        log.info("test_IssueInitCertEnrollmentTask_setEntityProfileName");
        final IssueInitCertEnrollmentTask task = new IssueInitCertEnrollmentTask(NODE_FDN);
        task.setEntityProfileName(entityProfileName);
        assertEquals("Unexpected EntityProfileName", entityProfileName, task.getEntityProfileName());
        assertEquals("Unexpected short description", IssueInitCertEnrollmentTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitCertEnrollmentTask_getEntityProfileName() {
        log.info("test_IssueInitCertEnrollmentTask_getEntityProfileName");
        final IssueInitCertEnrollmentTask task = new IssueInitCertEnrollmentTask(NODE_FDN);
        task.setEntityProfileName(entityProfileName);
        assertEquals("Unexpected EntityProfileName", entityProfileName, task.getEntityProfileName());
        assertEquals("Unexpected short description", IssueInitCertEnrollmentTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitCertEnrollmentTask_setRollbackTimeout() {
        log.info("test_IssueInitCertEnrollmentTask_setRollbackTimeout");
        final IssueInitCertEnrollmentTask task = new IssueInitCertEnrollmentTask(NODE_FDN);
        final int expectedTimeout = rollbackTimeout + 2;
        task.setRollbackTimeout(expectedTimeout);
        assertEquals("Unexpected rollbackTimeout", expectedTimeout, task.getRollbackTimeout().intValue());
        assertEquals("Unexpected short description", IssueInitCertEnrollmentTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitCertEnrollmentTask_getRollbackTimeout() {
        log.info("test_IssueInitCertEnrollmentTask_getRollbackTimeout");
        final IssueInitCertEnrollmentTask task = new IssueInitCertEnrollmentTask(NODE_FDN);
        final int expectedTimeout = rollbackTimeout + 20;
        task.setRollbackTimeout(expectedTimeout);
        assertEquals("Unexpected rollbackTimeout", expectedTimeout, task.getRollbackTimeout().intValue());
        assertEquals("Unexpected short description", IssueInitCertEnrollmentTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitCertEnrollmentTask_setSubjectAltName() {
        log.info("test_IssueInitCertEnrollmentTask_setSubjectAltName");
        final IssueInitCertEnrollmentTask task = new IssueInitCertEnrollmentTask(NODE_FDN);
        task.setSubjectAltName(subjectAltName.getValue());
        assertEquals("Unexpected subjectAltName", subjectAltName, task.getSubjectAltName());
        assertEquals("Unexpected short description", IssueInitCertEnrollmentTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitCertEnrollmentTask_getSubjectAltName() {
        log.info("test_IssueInitCertEnrollmentTask_getSubjectAltName");
        final IssueInitCertEnrollmentTask task = new IssueInitCertEnrollmentTask(NODE_FDN);
        //		String expectedSubjectAltName = subjectAltName + "-test";
        task.setSubjectAltName(expectedSubjectAltName.getValue());
        assertEquals("Unexpected subjectAltName", expectedSubjectAltName, task.getSubjectAltName());
        assertEquals("Unexpected short description", IssueInitCertEnrollmentTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitCertEnrollmentTask_setSubjectAltNameType() {
        log.info("test_IssueInitCertEnrollmentTask_setSubjectAltNameType");
        final IssueInitCertEnrollmentTask task = new IssueInitCertEnrollmentTask(NODE_FDN);
        task.setSubjectAltNameType(subjectAltNameType.name());
        assertEquals("Unexpected subjectAltNameType", subjectAltNameType, task.getSubjectAltNameType());
        assertEquals("Unexpected short description", IssueInitCertEnrollmentTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitCertEnrollmentTask_getSubjectAltNameType() {
        log.info("test_IssueInitCertEnrollmentTask_getSubjectAltNameType");
        final IssueInitCertEnrollmentTask task = new IssueInitCertEnrollmentTask(NODE_FDN);
        task.setSubjectAltNameType(subjectAltNameType.name());
        assertEquals("Unexpected subjectAltNameType", subjectAltNameType, task.getSubjectAltNameType());
        assertEquals("Unexpected short description", IssueInitCertEnrollmentTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitCertEnrollmentTask_getSubjectAltNameType_Invalid() {
        log.info("test_IssueInitCertEnrollmentTask_getSubjectAltNameType_Invalid");
        final IssueInitCertEnrollmentTask task = new IssueInitCertEnrollmentTask(NODE_FDN);
        //set some invalid subject alt name type 
        task.setSubjectAltNameType(subjectAltNameType.name() + "__invalid");
        assertNull("Invalid SubjectAltNameType", task.getSubjectAltNameType());
        assertEquals("Unexpected short description", IssueInitCertEnrollmentTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitCertEnrollmentTask_setEnrollmentMode() {
        log.info("test_IssueInitCertEnrollmentTask_setEnrollmentMode");
        final IssueInitCertEnrollmentTask task = new IssueInitCertEnrollmentTask(NODE_FDN);
        final String enrollmentMode = expectedEnrollmentMode.toString();
        task.setEnrollmentMode(enrollmentMode);
        assertEquals("Unexpected Enrollment Mode", expectedEnrollmentMode, task.getEnrollmentMode());
        assertEquals("Unexpected short description", IssueInitCertEnrollmentTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitCertEnrollmentTask_getEnrollmentMode() {
        log.info("test_IssueInitCertEnrollmentTask_getEnrollmentMode");
        final IssueInitCertEnrollmentTask task = new IssueInitCertEnrollmentTask(NODE_FDN);
        final String enrollmentMode = expectedEnrollmentMode.toString();
        task.setEnrollmentMode(enrollmentMode);
        assertEquals("Unexpected Enrollment Mode", expectedEnrollmentMode, task.getEnrollmentMode());
        assertEquals("Unexpected short description", IssueInitCertEnrollmentTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitCertEnrollmentTask_getEnrollmentMode_Null() {
        log.info("test_IssueInitCertEnrollmentTask_getEnrollmentMode_Null");
        final IssueInitCertEnrollmentTask task = new IssueInitCertEnrollmentTask(NODE_FDN);
        //set some invalid Enrollment Mode
        final String enrollmentMode = expectedEnrollmentMode.toString() + "__invalid";
        task.setEnrollmentMode(enrollmentMode);
        assertNull("Enrollment Mode must be null", task.getEnrollmentMode());
        assertEquals("Unexpected short description", IssueInitCertEnrollmentTask.SHORT_DESCRIPTION, task.getShortDescription());
    }
}
