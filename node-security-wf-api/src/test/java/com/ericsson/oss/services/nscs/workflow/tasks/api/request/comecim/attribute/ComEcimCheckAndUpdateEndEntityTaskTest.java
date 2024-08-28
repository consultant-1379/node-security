/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
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

import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;

public class ComEcimCheckAndUpdateEndEntityTaskTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private static final String NODE_FDN = "ERBS_01";

    @Test
    public void test_ComEcimCheckAndUpdateEndEntityTaskTest() {
        log.info("test_ComEcimCheckAndUpdateEndEntityTaskTest");
        final ComEcimCheckAndUpdateEndEntityTask task = new ComEcimCheckAndUpdateEndEntityTask();
        assertNotNull("Task is null!", task);
        assertEquals("Unexpected short description", ComEcimCheckAndUpdateEndEntityTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_ComEcimCheckAndUpdateEndEntityTaskTest_NoDefault() {
        log.info("test_ComEcimCheckAndUpdateEndEntityTaskTest_NoDefault");
        final ComEcimCheckAndUpdateEndEntityTask task = new ComEcimCheckAndUpdateEndEntityTask(NODE_FDN);
        assertNotNull("Task is null!", task);
        assertEquals("Unexpected FDN", NODE_FDN, task.getNodeFdn());
        assertNull("Unexpected enrollment mode", task.getEnrollmentMode());
        assertNull("Unexpected entity profile name", task.getEntityProfileName());
        assertNull("Unexpected key algorithm", task.getKeyAlgorithm());
        assertNull("Unexpected subject alt name", task.getSubjectAltName());
        assertNull("Unexpected subject alt name type", task.getSubjectAltNameType());
        assertNull("Unexpected trusted cert category", task.getTrustedCertCategory());
        assertEquals("Unexpected short description", ComEcimCheckAndUpdateEndEntityTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_ComEcimCheckAndUpdateEndEntityTaskTest_setEnrollmentMode() {
        log.info("test_ComEcimCheckAndUpdateEndEntityTaskTest_setEnrollmentMode");
        final EnrollmentMode enrollmentMode = EnrollmentMode.ONLINE_SCEP;
        final ComEcimCheckAndUpdateEndEntityTask task = new ComEcimCheckAndUpdateEndEntityTask(NODE_FDN);
        assertNotNull("Task is null!", task);
        task.setEnrollmentMode(enrollmentMode.toString());
        assertEquals("Unexpected fdn", NODE_FDN, task.getNodeFdn());
        assertEquals("Unexpected enrollment mode", enrollmentMode, EnrollmentMode.valueOf(task.getEnrollmentMode()));
        assertEquals("Unexpected short description", ComEcimCheckAndUpdateEndEntityTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_ComEcimCheckAndUpdateEndEntityTaskTest_getInvalidEnrollmentMode() {
        log.info("test_ComEcimCheckAndUpdateEndEntityTaskTest_getInvalidEnrollmentMode");
        final EnrollmentMode enrollmentMode = EnrollmentMode.ONLINE_SCEP;
        final ComEcimCheckAndUpdateEndEntityTask task = new ComEcimCheckAndUpdateEndEntityTask(NODE_FDN);
        assertNotNull("Task is null!", task);
        task.setEnrollmentMode(enrollmentMode.toString() + "___invalid");
        EnrollmentMode.valueOf(task.getEnrollmentMode());
        assertEquals("Unexpected short description", ComEcimCheckAndUpdateEndEntityTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test(expected = NullPointerException.class)
    public void test_ComEcimCheckAndUpdateEndEntityTaskTest_getNullEnrollmentMode() {
        log.info("test_ComEcimCheckAndUpdateEndEntityTaskTest_getNullEnrollmentMode");
        final ComEcimCheckAndUpdateEndEntityTask task = new ComEcimCheckAndUpdateEndEntityTask(NODE_FDN);
        assertNotNull("Task is null!", task);
        task.setEnrollmentMode(null);
        EnrollmentMode.valueOf(task.getEnrollmentMode());
    }

    @Test
    public void test_ComEcimCheckAndUpdateEndEntityTaskTest_setEntityProfileName() {
        log.info("test_ComEcimCheckAndUpdateEndEntityTaskTest_setEntityProfileName");
        final String entityProfileName = "entityProfileName";
        final ComEcimCheckAndUpdateEndEntityTask task = new ComEcimCheckAndUpdateEndEntityTask(NODE_FDN);
        assertNotNull("Task is null!", task);
        task.setEntityProfileName(entityProfileName);
        assertEquals("Unexpected fdn", NODE_FDN, task.getNodeFdn());
        assertEquals("Unexpected entity profile name", entityProfileName, task.getEntityProfileName());
        assertEquals("Unexpected short description", ComEcimCheckAndUpdateEndEntityTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_ComEcimCheckAndUpdateEndEntityTaskTest_setNullEntityProfileName() {
        log.info("test_ComEcimCheckAndUpdateEndEntityTaskTest_setNullEntityProfileName");
        final ComEcimCheckAndUpdateEndEntityTask task = new ComEcimCheckAndUpdateEndEntityTask(NODE_FDN);
        assertNotNull("Task is null!", task);
        task.setEntityProfileName(null);
        assertEquals("Unexpected fdn", NODE_FDN, task.getNodeFdn());
        assertNull("Entity profile name is not null!", task.getEntityProfileName());
        assertEquals("Unexpected short description", ComEcimCheckAndUpdateEndEntityTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_ComEcimCheckAndUpdateEndEntityTaskTest_setKeyAlgorithm() {
        log.info("test_ComEcimCheckAndUpdateEndEntityTaskTest_setKeyAlgorithm");
        final AlgorithmKeys keyAlgorithm = AlgorithmKeys.RSA_2048;
        final ComEcimCheckAndUpdateEndEntityTask task = new ComEcimCheckAndUpdateEndEntityTask(NODE_FDN);
        assertNotNull("Task is null!", task);
        task.setKeyAlgorithm(keyAlgorithm.toString());
        assertEquals("Unexpected fdn", NODE_FDN, task.getNodeFdn());
        assertEquals("Unexpected key algorithm", keyAlgorithm, AlgorithmKeys.valueOf(task.getKeyAlgorithm()));
        assertEquals("Unexpected short description", ComEcimCheckAndUpdateEndEntityTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_ComEcimCheckAndUpdateEndEntityTaskTest_getInvalidKeyAlgorithm() {
        log.info("test_ComEcimCheckAndUpdateEndEntityTaskTest_getInvalidKeyAlgorithm");
        final AlgorithmKeys keyAlgorithm = AlgorithmKeys.RSA_2048;
        final ComEcimCheckAndUpdateEndEntityTask task = new ComEcimCheckAndUpdateEndEntityTask(NODE_FDN);
        assertNotNull("Task is null!", task);
        task.setKeyAlgorithm(keyAlgorithm.toString() + "___invalid");
        AlgorithmKeys.valueOf(task.getKeyAlgorithm());
    }

    @Test(expected = NullPointerException.class)
    public void test_ComEcimCheckAndUpdateEndEntityTaskTest_getNullKeyAlgorithm() {
        log.info("test_ComEcimCheckAndUpdateEndEntityTaskTest_getNullKeyAlgorithm");
        final ComEcimCheckAndUpdateEndEntityTask task = new ComEcimCheckAndUpdateEndEntityTask(NODE_FDN);
        assertNotNull("Task is null!", task);
        task.setKeyAlgorithm(null);
        AlgorithmKeys.valueOf(task.getKeyAlgorithm());
    }

    @Test
    public void test_ComEcimCheckAndUpdateEndEntityTaskTest_setSubjectAltName() {
        log.info("test_ComEcimCheckAndUpdateEndEntityTaskTest_setSubjectAltName");
        final String subjectAltName = "subjectAltName";
        final ComEcimCheckAndUpdateEndEntityTask task = new ComEcimCheckAndUpdateEndEntityTask(NODE_FDN);
        assertNotNull("Task is null!", task);
        task.setSubjectAltName(subjectAltName);
        assertEquals("Unexpected fdn", NODE_FDN, task.getNodeFdn());
        assertEquals("Unexpected subject alt name", subjectAltName, task.getSubjectAltName());
        assertEquals("Unexpected short description", ComEcimCheckAndUpdateEndEntityTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_ComEcimCheckAndUpdateEndEntityTaskTest_setNullSubjectAltName() {
        log.info("test_ComEcimCheckAndUpdateEndEntityTaskTest_setNullSubjectAltName");
        final ComEcimCheckAndUpdateEndEntityTask task = new ComEcimCheckAndUpdateEndEntityTask(NODE_FDN);
        assertNotNull("Task is null!", task);
        task.setSubjectAltName(null);
        assertEquals("Unexpected fdn", NODE_FDN, task.getNodeFdn());
        assertNull("Subject alt name is not null!", task.getSubjectAltName());
        assertEquals("Unexpected short description", ComEcimCheckAndUpdateEndEntityTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_ComEcimCheckAndUpdateEndEntityTaskTest_setSubjectAltNameType() {
        log.info("test_ComEcimCheckAndUpdateEndEntityTaskTest_setSubjectAltNameType");
        final SubjectAltNameFormat subjectAltNameType = SubjectAltNameFormat.IPV4;
        final ComEcimCheckAndUpdateEndEntityTask task = new ComEcimCheckAndUpdateEndEntityTask(NODE_FDN);
        assertNotNull("Task is null!", task);
        task.setSubjectAltNameType(subjectAltNameType.name());
        assertEquals("Unexpected fdn", NODE_FDN, task.getNodeFdn());
        assertEquals("Unexpected key algorithm", subjectAltNameType, SubjectAltNameFormat.valueOf(task.getSubjectAltNameType()));
        assertEquals("Unexpected short description", ComEcimCheckAndUpdateEndEntityTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_ComEcimCheckAndUpdateEndEntityTaskTest_getInvalidSubjectAltNameType() {
        log.info("test_ComEcimCheckAndUpdateEndEntityTaskTest_getInvalidSubjectAltNameType");
        final SubjectAltNameFormat subjectAltNameType = SubjectAltNameFormat.IPV4;
        final ComEcimCheckAndUpdateEndEntityTask task = new ComEcimCheckAndUpdateEndEntityTask(NODE_FDN);
        assertNotNull("Task is null!", task);
        task.setSubjectAltNameType(subjectAltNameType.name() + "___invalid");
        SubjectAltNameFormat.valueOf(task.getSubjectAltNameType());
    }

    @Test(expected = NullPointerException.class)
    public void test_ComEcimCheckAndUpdateEndEntityTaskTest_getNullSubjectAltNameType() {
        log.info("test_ComEcimCheckAndUpdateEndEntityTaskTest_getNullSubjectAltNameType");
        final ComEcimCheckAndUpdateEndEntityTask task = new ComEcimCheckAndUpdateEndEntityTask(NODE_FDN);
        assertNotNull("Task is null!", task);
        task.setSubjectAltNameType(null);
        SubjectAltNameFormat.valueOf(task.getSubjectAltNameType());
    }

    @Test
    public void test_ComEcimCheckAndUpdateEndEntityTaskTest_setOamTrustedCertCategory() {
        log.info("test_ComEcimCheckAndUpdateEndEntityTaskTest_setOamTrustedCertCategory");
        final TrustedCertCategory category = TrustedCertCategory.CORBA_PEERS;
        final ComEcimCheckAndUpdateEndEntityTask task = new ComEcimCheckAndUpdateEndEntityTask(NODE_FDN);
        assertNotNull("Task is null!", task);
        task.setTrustedCertCategory(category.toString());
        assertEquals("Unexpected fdn", NODE_FDN, task.getNodeFdn());
        assertEquals("Unexpected trusted Category", category, TrustedCertCategory.valueOf(task.getTrustedCertCategory()));
        assertEquals("Unexpected short description", ComEcimCheckAndUpdateEndEntityTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_ComEcimCheckAndUpdateEndEntityTaskTest_setIpSecTrustedCertCategory() {
        log.info("test_ComEcimCheckAndUpdateEndEntityTaskTest_setIpSecTrustedCertCategory");
        final TrustedCertCategory category = TrustedCertCategory.IPSEC;
        final ComEcimCheckAndUpdateEndEntityTask task = new ComEcimCheckAndUpdateEndEntityTask(NODE_FDN);
        assertNotNull("Task is null!", task);
        task.setTrustedCertCategory(category.toString());
        assertEquals("Unexpected fdn", NODE_FDN, task.getNodeFdn());
        assertEquals("Unexpected trusted Category", category, TrustedCertCategory.valueOf(task.getTrustedCertCategory()));
        assertEquals("Unexpected short description", ComEcimCheckAndUpdateEndEntityTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_ComEcimCheckAndUpdateEndEntityTaskTest_getInvalidTrustedCertCategory() {
        log.info("test_ComEcimCheckAndUpdateEndEntityTaskTest_getInvalidTrustedCertCategory");
        final TrustedCertCategory category = TrustedCertCategory.IPSEC;
        final ComEcimCheckAndUpdateEndEntityTask task = new ComEcimCheckAndUpdateEndEntityTask(NODE_FDN);
        assertNotNull("Task is null!", task);
        task.setTrustedCertCategory(category.toString() + "___invalid");
        TrustedCertCategory.valueOf(task.getTrustedCertCategory());
    }

    @Test(expected = NullPointerException.class)
    public void test_ComEcimCheckAndUpdateEndEntityTaskTest_getNullTrustedCertCategory() {
        log.info("test_ComEcimCheckAndUpdateEndEntityTaskTest_getNullTrustedCertCategory");
        final ComEcimCheckAndUpdateEndEntityTask task = new ComEcimCheckAndUpdateEndEntityTask(NODE_FDN);
        assertNotNull("Task is null!", task);
        task.setTrustedCertCategory(null);
        TrustedCertCategory.valueOf(task.getTrustedCertCategory());
    }
}
