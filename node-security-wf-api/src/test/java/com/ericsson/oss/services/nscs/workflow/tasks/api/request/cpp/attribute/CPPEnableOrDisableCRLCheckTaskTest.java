/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
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

public class CPPEnableOrDisableCRLCheckTaskTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Test
    public void test_ComEcimEnableORDisableCRLCheckTaskTest() {
        log.info("test_ComEcimEnableCRLCheckTaskTest");
        final CPPEnableOrDisableCRLCheckTask task = new CPPEnableOrDisableCRLCheckTask();
        assertNotNull("Task is null!", task);
        assertEquals("Unexpected Task type", WorkflowTaskType.CPP_ENABLE_OR_DISABLE_CRL_CHECK, task.getTaskType());
        assertEquals("Unexpected short description", CPPEnableOrDisableCRLCheckTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

}
