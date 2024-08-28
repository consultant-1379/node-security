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
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;

public class IssueInitCertEnrollmentIpSecTaskTest {

    private static final String NODE_FDN = "ERBS_01";

    private static final int rollbackTimeout = 2;

    private static final String entityProfileName = "Profile123";

    private static final SubjectAltNameStringType subjectAltName = new SubjectAltNameStringType("Subject alt name");

    private static final SubjectAltNameStringType expectedSubjectAltName = new SubjectAltNameStringType("Subject alt name-test");

    private static final SubjectAltNameFormat subjectAltNameType = SubjectAltNameFormat.IPV4;

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Test
    public void test_IssueInitCertEnrollmentIpSecTask() {
        log.info("test_IssueInitCertEnrollmentIpSecTask");
        final IssueInitCertEnrollmentIpSecTask task = new IssueInitCertEnrollmentIpSecTask();
        assertNotNull("Task is null!", task);
        assertEquals("Unexpected Task type", WorkflowTaskType.CPP_ISSUE_CERT_ENROLLMENT_IPSEC, task.getTaskType());
        assertEquals("Unexpected short description", IssueInitCertEnrollmentIpSecTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitCertEnrollmentIpSecTask_NoDefault() {
        log.info("test_IssueInitCertEnrollmentIpSecTask_NoDefault");
        final IssueInitCertEnrollmentIpSecTask task = new IssueInitCertEnrollmentIpSecTask(NODE_FDN);
        assertNotNull("Task is null!", task);
        assertEquals("Unexpected Task type", WorkflowTaskType.CPP_ISSUE_CERT_ENROLLMENT_IPSEC, task.getTaskType());
        assertEquals("Unexpected node", NODE_FDN, task.getNodeFdn());
        assertEquals("Unexpected short description", IssueInitCertEnrollmentIpSecTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitCertEnrollmentIpSecTask_ConstructorWithParameters() {
        log.info("test_IssueInitCertEnrollmentIpSecTask_ConstructorWithParameters");
        final IssueInitCertEnrollmentIpSecTask task = new IssueInitCertEnrollmentIpSecTask(NODE_FDN, rollbackTimeout, subjectAltName.getValue(),
                subjectAltNameType.name());
        assertEquals("Unexpected Task type", WorkflowTaskType.CPP_ISSUE_CERT_ENROLLMENT_IPSEC, task.getTaskType());
        assertEquals("Unexpected node", NODE_FDN, task.getNodeFdn());
        assertEquals("Unexpected timeout", rollbackTimeout, task.getRollbackTimeout().intValue());
        assertEquals("Unexpected subjectAltName", subjectAltName, task.getSubjectAltName());
        assertEquals("Unexpected subjectAltNameType", subjectAltNameType, task.getSubjectAltNameType());
        assertEquals("Unexpected short description", IssueInitCertEnrollmentIpSecTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitCertEnrollmentIpSecTask_setAlgoKeySize() {
        log.info("test_IssueInitCertEnrollmentIpSecTask_setAlgoKeySize");
        final IssueInitCertEnrollmentIpSecTask task = new IssueInitCertEnrollmentIpSecTask(NODE_FDN);
        final AlgorithmKeys expectedAlgoKey = AlgorithmKeys.RSA_2048;
        final String expectedAlgoKeyString = expectedAlgoKey.toString();
        task.setAlgoKeySize(expectedAlgoKeyString);
        assertEquals("Unexpected AlgorithmKeys", expectedAlgoKey, task.getAlgoKeySize());
        assertEquals("Unexpected short description", IssueInitCertEnrollmentIpSecTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitCertEnrollmentIpSecTask_getAlgoKeySize() {
        log.info("test_IssueInitCertEnrollmentIpSecTask_getAlgoKeySize");
        final IssueInitCertEnrollmentIpSecTask task = new IssueInitCertEnrollmentIpSecTask(NODE_FDN);
        final AlgorithmKeys expectedAlgoKey = AlgorithmKeys.RSA_8192;
        final String expectedAlgoKeyString = expectedAlgoKey.toString();
        task.setAlgoKeySize(expectedAlgoKeyString);
        assertEquals("Unexpected AlgorithmKeys", expectedAlgoKey, task.getAlgoKeySize());
        assertEquals("Unexpected short description", IssueInitCertEnrollmentIpSecTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitCertEnrollmentIpSecTask_getAlgoKeySize_Null() {
        log.info("test_IssueInitCertEnrollmentIpSecTask_getAlgoKeySize_Null");
        final IssueInitCertEnrollmentIpSecTask task = new IssueInitCertEnrollmentIpSecTask(NODE_FDN);
        final AlgorithmKeys expectedAlgoKey = AlgorithmKeys.RSA_8192;
        //set invalid AlgorithmKeys
        final String expectedAlgoKeyString = expectedAlgoKey.toString() + "__invalid";
        task.setAlgoKeySize(expectedAlgoKeyString);
        assertNull("AlgorithmKeys must be null", task.getAlgoKeySize());
        assertEquals("Unexpected short description", IssueInitCertEnrollmentIpSecTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitCertEnrollmentIpSecTask_setEntityProfileName() {
        log.info("test_IssueInitCertEnrollmentIpSecTask_setEntityProfileName");
        final IssueInitCertEnrollmentIpSecTask task = new IssueInitCertEnrollmentIpSecTask(NODE_FDN);
        task.setEntityProfileName(entityProfileName);
        assertEquals("Unexpected EntityProfileName", entityProfileName, task.getEntityProfileName());
        assertEquals("Unexpected short description", IssueInitCertEnrollmentIpSecTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitCertEnrollmentIpSecTask_getEntityProfileName() {
        log.info("test_IssueInitCertEnrollmentIpSecTask_getEntityProfileName");
        final IssueInitCertEnrollmentIpSecTask task = new IssueInitCertEnrollmentIpSecTask(NODE_FDN);
        task.setEntityProfileName(entityProfileName);
        assertEquals("Unexpected EntityProfileName", entityProfileName, task.getEntityProfileName());
        assertEquals("Unexpected short description", IssueInitCertEnrollmentIpSecTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitCertEnrollmentIpSecTask_setRollbackTimeout() {
        log.info("test_IssueInitCertEnrollmentIpSecTask_setRollbackTimeout");
        final IssueInitCertEnrollmentIpSecTask task = new IssueInitCertEnrollmentIpSecTask(NODE_FDN);
        final int expectedTimeout = rollbackTimeout + 2;
        task.setRollbackTimeout(expectedTimeout);
        assertEquals("Unexpected rollbackTimeout", expectedTimeout, task.getRollbackTimeout().intValue());
        assertEquals("Unexpected short description", IssueInitCertEnrollmentIpSecTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitCertEnrollmentIpSecTask_getRollbackTimeout() {
        log.info("test_IssueInitCertEnrollmentIpSecTask_getRollbackTimeout");
        final IssueInitCertEnrollmentIpSecTask task = new IssueInitCertEnrollmentIpSecTask(NODE_FDN);
        final int expectedTimeout = rollbackTimeout + 20;
        task.setRollbackTimeout(expectedTimeout);
        assertEquals("Unexpected rollbackTimeout", expectedTimeout, task.getRollbackTimeout().intValue());
        assertEquals("Unexpected short description", IssueInitCertEnrollmentIpSecTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitCertEnrollmentIpSecTask_setSubjectAltName() {
        log.info("test_IssueInitCertEnrollmentIpSecTask_setSubjectAltName");
        final IssueInitCertEnrollmentIpSecTask task = new IssueInitCertEnrollmentIpSecTask(NODE_FDN);
        task.setSubjectAltName(subjectAltName.getValue());
        assertEquals("Unexpected subjectAltName", subjectAltName, task.getSubjectAltName());
        assertEquals("Unexpected short description", IssueInitCertEnrollmentIpSecTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitCertEnrollmentIpSecTask_getSubjectAltName() {
        log.info("test_IssueInitCertEnrollmentIpSecTask_getSubjectAltName");
        final IssueInitCertEnrollmentIpSecTask task = new IssueInitCertEnrollmentIpSecTask(NODE_FDN);
        task.setSubjectAltName(expectedSubjectAltName.getValue());
        assertEquals("Unexpected subjectAltName", expectedSubjectAltName, task.getSubjectAltName());
        assertEquals("Unexpected short description", IssueInitCertEnrollmentIpSecTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitCertEnrollmentIpSecTask_setSubjectAltNameType() {
        log.info("test_IssueInitCertEnrollmentIpSecTask_setSubjectAltNameType");
        final IssueInitCertEnrollmentIpSecTask task = new IssueInitCertEnrollmentIpSecTask(NODE_FDN);
        task.setSubjectAltNameType(subjectAltNameType.name());
        assertEquals("Unexpected subjectAltNameType", subjectAltNameType, task.getSubjectAltNameType());
        assertEquals("Unexpected short description", IssueInitCertEnrollmentIpSecTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitCertEnrollmentIpSecTask_getSubjectAltNameType() {
        log.info("test_IssueInitCertEnrollmentIpSecTask_getSubjectAltNameType");
        final IssueInitCertEnrollmentIpSecTask task = new IssueInitCertEnrollmentIpSecTask(NODE_FDN);
        task.setSubjectAltNameType(subjectAltNameType.name());
        assertEquals("Unexpected subjectAltNameType", subjectAltNameType, task.getSubjectAltNameType());
        assertEquals("Unexpected short description", IssueInitCertEnrollmentIpSecTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitCertEnrollmentIpSecTask_getSubjectAltNameType_Invalid() {
        log.info("test_IssueInitCertEnrollmentIpSecTask_getSubjectAltNameType_Invalid");
        final IssueInitCertEnrollmentIpSecTask task = new IssueInitCertEnrollmentIpSecTask(NODE_FDN);
        //set some invalid subject alt name type 
        task.setSubjectAltNameType(subjectAltNameType.name() + "__invalid");
        assertNull("Invalid SubjectAltNameType", task.getSubjectAltNameType());
        assertEquals("Unexpected short description", IssueInitCertEnrollmentIpSecTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitCertEnrollmentIpSecTask_setEnrollmentMode() {
        log.info("test_IssueInitCertEnrollmentIpSecTask_setEnrollmentMode");
        final IssueInitCertEnrollmentIpSecTask task = new IssueInitCertEnrollmentIpSecTask(NODE_FDN);
        final EnrollmentMode enrollmentMode = EnrollmentMode.CMPv2_INITIAL;
        final String expectedEnrollmentMode = enrollmentMode.toString();
        task.setEnrollmentMode(expectedEnrollmentMode);
        assertEquals("Unexpected Enrollment Mode", enrollmentMode, task.getEnrollmentMode());
        assertEquals("Unexpected short description", IssueInitCertEnrollmentIpSecTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitCertEnrollmentIpSecTask_getEnrollmentMode() {
        log.info("test_IssueInitCertEnrollmentIpSecTask_getEnrollmentMode");
        final IssueInitCertEnrollmentIpSecTask task = new IssueInitCertEnrollmentIpSecTask(NODE_FDN);
        final EnrollmentMode enrollmentMode = EnrollmentMode.SCEP;
        final String expectedEnrollmentMode = enrollmentMode.toString();
        task.setEnrollmentMode(expectedEnrollmentMode);
        assertEquals("Unexpected Enrollment Mode", enrollmentMode, task.getEnrollmentMode());
        assertEquals("Unexpected short description", IssueInitCertEnrollmentIpSecTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_IssueInitCertEnrollmentIpSecTask_getEnrollmentMode_Null() {
        log.info("test_IssueInitCertEnrollmentIpSecTask_getEnrollmentMode_Null");
        final IssueInitCertEnrollmentIpSecTask task = new IssueInitCertEnrollmentIpSecTask(NODE_FDN);
        final EnrollmentMode enrollmentMode = EnrollmentMode.SCEP;
        //set some invalid Enrollment Mode
        final String expectedEnrollmentMode = enrollmentMode.toString() + "__invalid";
        task.setEnrollmentMode(expectedEnrollmentMode);
        assertNull("Enrollment Mode must be null", task.getEnrollmentMode());
        assertEquals("Unexpected short description", IssueInitCertEnrollmentIpSecTask.SHORT_DESCRIPTION, task.getShortDescription());
    }
}
