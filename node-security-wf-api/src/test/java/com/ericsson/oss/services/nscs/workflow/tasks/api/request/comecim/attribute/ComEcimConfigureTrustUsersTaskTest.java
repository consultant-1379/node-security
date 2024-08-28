package com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;

public class ComEcimConfigureTrustUsersTaskTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private static final String NODE_FDN = "RadioNode01";

    @Test
    public void test_ComEcimConfigureTrustUsersTaskTest() {
        log.info("test_ComEcimConfigureTrustUsersTaskTest");
        final ComEcimConfigureTrustUsersTask task = new ComEcimConfigureTrustUsersTask();
        assertNotNull("Task is null!", task);
        assertEquals("Unexpected Task type", WorkflowTaskType.COM_ECIM_CONFIGURE_TRUST_USERS, task.getTaskType());
        assertEquals("Unexpected short description", ComEcimConfigureTrustUsersTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_ComEcimConfigureTrustUsersTaskTest_NoDefault() {
        log.info("test_ComEcimConfigureTrustUsersTaskTest_NoDefault");
        final TrustedCertCategory category = TrustedCertCategory.IPSEC;
        final ComEcimConfigureTrustUsersTask task = new ComEcimConfigureTrustUsersTask(NODE_FDN, category.toString());
        assertNotNull("Task is null!", task);
        assertEquals("Unexpected Task type", WorkflowTaskType.COM_ECIM_CONFIGURE_TRUST_USERS, task.getTaskType());
        assertEquals("Unexpected short description", ComEcimConfigureTrustUsersTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_ComEcimConfigureTrustUsersTaskTest_setTrustedCertCategory() {
        log.info("test_ComEcimConfigureTrustUsersTaskTest_setTrustCerts");
        final TrustedCertCategory category = TrustedCertCategory.CORBA_PEERS;
        final ComEcimConfigureTrustUsersTask task = new ComEcimConfigureTrustUsersTask(NODE_FDN, TrustedCertCategory.IPSEC.toString());
        assertNotNull("Task is null!", task);
        task.setTrustedCertCategory(category.toString());
        assertEquals("Unexpected Trusted Category", category.toString(), task.getTrustedCertCategory());
        assertEquals("Unexpected short description", ComEcimConfigureTrustUsersTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_ComEcimConfigureTrustUsersTaskTest_setOamTrustedCertCategory() {
        log.info("test_ComEcimConfigureTrustUsersTaskTest_setOamTrustedCertCategory");
        final TrustedCertCategory category = TrustedCertCategory.CORBA_PEERS;
        final ComEcimConfigureTrustUsersTask task = new ComEcimConfigureTrustUsersTask();
        assertNotNull("Task is null!", task);
        task.setNodeFdn(NODE_FDN);
        task.setTrustedCertCategory(category.toString());
        assertEquals("Unexpected Task type", WorkflowTaskType.COM_ECIM_CONFIGURE_TRUST_USERS, task.getTaskType());
        assertEquals("Unexpected fdn", NODE_FDN, task.getNodeFdn());
        assertEquals("Unexpected trusted Category", category, TrustedCertCategory.valueOf(task.getTrustedCertCategory()));
        assertEquals("Unexpected short description", ComEcimConfigureTrustUsersTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_ComEcimConfigureTrustUsersTaskTest_setIpSecTrustedCertCategory() {
        log.info("test_ComEcimConfigureTrustUsersTaskTest_setIpSecTrustedCertCategory");
        final TrustedCertCategory category = TrustedCertCategory.IPSEC;
        final ComEcimConfigureTrustUsersTask task = new ComEcimConfigureTrustUsersTask();
        assertNotNull("Task is null!", task);
        task.setNodeFdn(NODE_FDN);
        task.setTrustedCertCategory(category.toString());
        assertEquals("Unexpected Task type", WorkflowTaskType.COM_ECIM_CONFIGURE_TRUST_USERS, task.getTaskType());
        assertEquals("Unexpected fdn", NODE_FDN, task.getNodeFdn());
        assertEquals("Unexpected trusted Category", category, TrustedCertCategory.valueOf(task.getTrustedCertCategory()));
        assertEquals("Unexpected short description", ComEcimConfigureTrustUsersTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_ComEcimConfigureTrustUsersTaskTest_getInvalidTrustedCertCategory() {
        log.info("test_ComEcimConfigureTrustUsersTaskTest_getInvalidTrustedCertCategory");
        final TrustedCertCategory category = TrustedCertCategory.IPSEC;
        final ComEcimConfigureTrustUsersTask task = new ComEcimConfigureTrustUsersTask();
        assertNotNull("Task is null!", task);
        task.setNodeFdn(NODE_FDN);
        task.setTrustedCertCategory(category.toString() + "___invalid");
        TrustedCertCategory.valueOf(task.getTrustedCertCategory());
    }

    @Test(expected = NullPointerException.class)
    public void test_ComEcimConfigureTrustUsersTaskTest_getNullTrustedCertCategory() {
        log.info("test_ComEcimConfigureTrustUsersTaskTest_getNullTrustedCertCategory");
        final ComEcimConfigureTrustUsersTask task = new ComEcimConfigureTrustUsersTask();
        assertNotNull("Task is null!", task);
        task.setNodeFdn(NODE_FDN);
        task.setTrustedCertCategory(null);
        TrustedCertCategory.valueOf(task.getTrustedCertCategory());
    }
}
