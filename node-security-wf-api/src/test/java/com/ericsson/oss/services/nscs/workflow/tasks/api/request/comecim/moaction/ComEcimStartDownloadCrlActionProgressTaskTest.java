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
 *----------------------------------------------------------------------------
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.moaction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;

@RunWith(MockitoJUnitRunner.class)
public class ComEcimStartDownloadCrlActionProgressTaskTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private static final String NODE_FDN = "ERBS_01";

    @Test
    public void testComEcimStartDownloadCrlActionProgressTask() {
        log.info("testComEcimStartDownloadCrlActionProgressTask");
        ComEcimStartDownloadCrlActionProgressTask task = new ComEcimStartDownloadCrlActionProgressTask();
        assertNotNull("Task is null!", task);
        assertEquals("Unexpected Task type", WorkflowTaskType.COM_ECIM_START_DOWNLOAD_CRL_ACTION, task.getTaskType());
    }

    @Test
    public void testComEcimStartDownloadCrlActionProgressTask_fdn() {
        log.info("testComEcimStartDownloadCrlActionProgressTask_fdn");
        ComEcimStartDownloadCrlActionProgressTask task = new ComEcimStartDownloadCrlActionProgressTask(NODE_FDN);
        assertNotNull("Task is null!", task);
        assertEquals("Unexpected Task type", WorkflowTaskType.COM_ECIM_START_DOWNLOAD_CRL_ACTION, task.getTaskType());
    }

}
*/