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

public class ComEcimCheckTrustedCategoryTaskTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private static final String NODE_FDN = "RadioNode01";

    @Test
    public void test_ComEcimCheckTrustedCategoryTaskTest() {
        log.info("test_ComEcimCheckTrustedCategoryTaskTest");
        final ComEcimCheckTrustedCategoryTask task = new ComEcimCheckTrustedCategoryTask();
        assertNotNull("Task is null!", task);
        assertEquals("Unexpected Task type", WorkflowTaskType.COM_ECIM_CHECK_TRUSTED_CATEGORY, task.getTaskType());
        assertEquals("Unexpected short description", ComEcimCheckTrustedCategoryTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_ComEcimCheckTrustedCategoryTaskTest_NoDefault() {
        log.info("test_ComEcimCheckTrustedCategoryTaskTest_NoDefault");
        final TrustedCertCategory category = TrustedCertCategory.IPSEC;
        final ComEcimCheckTrustedCategoryTask task = new ComEcimCheckTrustedCategoryTask(NODE_FDN, category.toString());
        assertNotNull("Task is null!", task);
        assertEquals("Unexpected Task type", WorkflowTaskType.COM_ECIM_CHECK_TRUSTED_CATEGORY, task.getTaskType());
        assertEquals("Unexpected short description", ComEcimCheckTrustedCategoryTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_ComEcimCheckTrustedCategoryTaskTest_setTrustCerts() {
        log.info("test_ComEcimCheckTrustedCategoryTaskTest_setTrustCerts");
        final TrustedCertCategory category = TrustedCertCategory.CORBA_PEERS;
        final ComEcimCheckTrustedCategoryTask task = new ComEcimCheckTrustedCategoryTask(NODE_FDN, TrustedCertCategory.IPSEC.toString());
        assertNotNull("Task is null!", task);
        task.setTrustCerts(category.toString());
        assertEquals("Unexpected Trusted Category", category.toString(), task.getTrustCerts());
        assertEquals("Unexpected short description", ComEcimCheckTrustedCategoryTask.SHORT_DESCRIPTION, task.getShortDescription());
    }
}
