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

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;

public class ComEcimCheckEnrollmentProtocolTaskTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private static final String NODE_FDN = "ERBS_01";

    @Test
    public void test_ComEcimCheckEnrollmentProtocolTaskTest() {
        log.info("test_ComEcimCheckEnrollmentProtocolTaskTest");
        final ComEcimCheckEnrollmentProtocolTask task = new ComEcimCheckEnrollmentProtocolTask();
        assertNotNull("Task is null!", task);
        assertEquals("Unexpected Task type", WorkflowTaskType.COM_ECIM_CHECK_ENROLLMENT_PROTOCOL, task.getTaskType());
        assertEquals("Unexpected short description", ComEcimCheckEnrollmentProtocolTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_ComEcimCheckEnrollmentProtocolTaskTest_NoDefault() {
        log.info("test_ComEcimCheckEnrollmentProtocolTaskTest_NoDefault");
        final EnrollmentMode enrollmentProtocol = EnrollmentMode.ONLINE_SCEP;
        final ComEcimCheckEnrollmentProtocolTask task = new ComEcimCheckEnrollmentProtocolTask(NODE_FDN, enrollmentProtocol.toString());
        assertNotNull("Task is null!", task);
        assertEquals("Unexpected Task type", WorkflowTaskType.COM_ECIM_CHECK_ENROLLMENT_PROTOCOL, task.getTaskType());
        assertEquals("Unexpected EnrollmentMode", enrollmentProtocol.toString(), task.getEnrollmentMode());
        assertEquals("Unexpected short description", ComEcimCheckEnrollmentProtocolTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_ComEcimCheckEnrollmentProtocolTaskTest_setEnrollmentMode() {
        log.info("test_ComEcimCheckEnrollmentProtocolTaskTest_setEnrollmentMode");
        final EnrollmentMode enrollmentProtocol = EnrollmentMode.CMPv2_INITIAL;
        final ComEcimCheckEnrollmentProtocolTask task = new ComEcimCheckEnrollmentProtocolTask(NODE_FDN, EnrollmentMode.ONLINE_SCEP.toString());
        assertNotNull("Task is null!", task);
        task.setEnrollmentMode(enrollmentProtocol.toString());
        assertEquals("Unexpected EnrollmentMode", enrollmentProtocol.toString(), task.getEnrollmentMode());
        assertEquals("Unexpected short description", ComEcimCheckEnrollmentProtocolTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

}
