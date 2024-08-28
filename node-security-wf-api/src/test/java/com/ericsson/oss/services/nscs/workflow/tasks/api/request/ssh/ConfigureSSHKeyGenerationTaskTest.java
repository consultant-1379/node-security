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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.ssh;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;

public class ConfigureSSHKeyGenerationTaskTest {

    private static final String NODE_FDN = "ERBS_01";

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Test
    public void test_ConfigureSSHKeyGenerationTask() {
        log.info("test_ConfigureSSHKeyGenerationTask");
        final ConfigureSSHKeyGenerationTask task = new ConfigureSSHKeyGenerationTask();
        assertEquals("Unexpected Task type", WorkflowTaskType.SSH_KEY_GENERATION, task.getTaskType());
        assertEquals("Unexpected short description", ConfigureSSHKeyGenerationTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_ConfigureSSHKeyGenerationTask_notDefault() {
        log.info("test_ConfigureSSHKeyGenerationTask_notDefault");
        final ConfigureSSHKeyGenerationTask task = new ConfigureSSHKeyGenerationTask(NODE_FDN);
        assertEquals("Unexpected Task type", WorkflowTaskType.SSH_KEY_GENERATION, task.getTaskType());
        assertEquals("Unexpected node", NODE_FDN, task.getNodeFdn());
        assertEquals("Unexpected short description", ConfigureSSHKeyGenerationTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_ConfigureSSHKeyGenerationTask_setAlgorithm() {
        log.info("test_ConfigureSSHKeyGenerationTask_notDefault");
        final ConfigureSSHKeyGenerationTask task = new ConfigureSSHKeyGenerationTask(NODE_FDN);
        final AlgorithmKeys algorithm = AlgorithmKeys.DSA_1024;
        final String expectedAlgorithmAndSize = algorithm.toString();
        task.setAlgorithm(expectedAlgorithmAndSize);
        assertEquals("Unexpected AlgorithmKeys", algorithm, task.getAlgorithm());
        assertEquals("Unexpected short description", ConfigureSSHKeyGenerationTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_ConfigureSSHKeyGenerationTask_getAlgorithm() {
        log.info("test_ConfigureSSHKeyGenerationTask_notDefault");
        final ConfigureSSHKeyGenerationTask task = new ConfigureSSHKeyGenerationTask(NODE_FDN);
        final AlgorithmKeys algorithm = AlgorithmKeys.RSA_4096;
        final String expectedAlgorithmAndSize = algorithm.toString();
        task.setAlgorithm(expectedAlgorithmAndSize);
        assertEquals("Unexpected AlgorithmKeys", algorithm, task.getAlgorithm());
        assertEquals("Unexpected short description", ConfigureSSHKeyGenerationTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_ConfigureSSHKeyGenerationTask_getAlgorithm_Null() {
        log.info("test_ConfigureSSHKeyGenerationTask_getAlgorithm_Null");
        final ConfigureSSHKeyGenerationTask task = new ConfigureSSHKeyGenerationTask(NODE_FDN);
        final AlgorithmKeys algorithm = AlgorithmKeys.RSA_4096;
        //set some invalid AlgorithmKeys
        final String invalidAlgorithmAndSize = algorithm.toString() + "___invalid";
        task.setAlgorithm(invalidAlgorithmAndSize);
        assertNull("getAlgorithm must return null", task.getAlgorithm());
        assertEquals("Unexpected short description", ConfigureSSHKeyGenerationTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_ConfigureSSHKeyGenerationTask_setIsCreate() {
        log.info("test_ConfigureSSHKeyGenerationTask_setIsCreate");
        final ConfigureSSHKeyGenerationTask task = new ConfigureSSHKeyGenerationTask(NODE_FDN);
        final boolean expectedIsCreate = true;
        task.setIsCreate(String.valueOf(expectedIsCreate));
        assertEquals("old create ssh key command", expectedIsCreate, task.getIsCreate());
        assertEquals("short description", ConfigureSSHKeyGenerationTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_ConfigureSSHKeyGenerationTask_getIsCreate() {
        log.info("test_ConfigureSSHKeyGenerationTask_getIsCreate");
        final ConfigureSSHKeyGenerationTask task = new ConfigureSSHKeyGenerationTask(NODE_FDN);
        final boolean expectedIsCreate = false;
        task.setIsCreate(String.valueOf(expectedIsCreate));
        assertEquals("old update ssh key command", expectedIsCreate, task.getIsCreate());
        assertEquals("short description", ConfigureSSHKeyGenerationTask.SHORT_DESCRIPTION, task.getShortDescription());
    }
    @Test
    public void test_ConfigureSSHKeyGenerationTask_setSshkeyOperation() {
        log.info("test_ConfigureSSHKeyGenerationTask_setSshkeyOperation");
        final ConfigureSSHKeyGenerationTask task = new ConfigureSSHKeyGenerationTask(NODE_FDN);
        final String expectedSshkeyOperation = "ssh_key_to_be_created";
        task.setSshkeyOperation(expectedSshkeyOperation);
        assertEquals("create ssh key command", expectedSshkeyOperation, task.getSshkeyOperation());
        assertEquals("short description", ConfigureSSHKeyGenerationTask.SHORT_DESCRIPTION, task.getShortDescription());
    }
}
