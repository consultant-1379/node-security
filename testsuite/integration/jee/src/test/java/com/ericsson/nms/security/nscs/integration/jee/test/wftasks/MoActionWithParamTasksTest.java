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
package com.ericsson.nms.security.nscs.integration.jee.test.wftasks;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityDataSetup;
import com.ericsson.oss.services.nscs.workflow.tasks.api.WorkflowTaskService;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.InitCertEnrollmentTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.InstallTrustedCertificatesTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;

public class MoActionWithParamTasksTest implements MoActionWithParamTasksTests {

    @Inject
    private Logger logger;

    @Inject
    WorkflowTaskService workflowTaskService;

    @Inject
    NodeSecurityDataSetup data;

    public static final String NODE_TEST_MO_TASK = "nodeTestMoTask";

    @Override
    public void testMoTaskInitCertEnrollmentTask() throws Exception {

        logger.info("testMoTaskInitCertEnrollmentTask........................starts");

        setup();
        final InitCertEnrollmentTask task = new InitCertEnrollmentTask(NODE_TEST_MO_TASK, 5);
        workflowTaskService.processTask(task);
        //Nothing to assert at this point other than no exception is thrown
        //Once the call get mediated the result can be checked.
        tearDown();

        logger.info("testMoTaskInitCertEnrollmentTask........................ends");
    }

    @Override
    public void testMoTaskInstallTrustedCertificatesTask() throws Exception {

        logger.info("testMoTaskInstallTrustedCertificatesTask........................starts");

        setup();
        final InstallTrustedCertificatesTask task = new InstallTrustedCertificatesTask(NODE_TEST_MO_TASK, TrustedCertCategory.CORBA_PEERS.toString());
        workflowTaskService.processTask(task);
        //Nothing to assert at this point other than no exception is thrown
        //Once the call get mediated the result can be checked.
        tearDown();

        logger.info("testMoTaskInstallTrustedCertificatesTask........................ends");
    }

    private void setup() throws Exception {
        data.deleteAllNodes();
        data.createNode(NODE_TEST_MO_TASK);
    }

    private void tearDown() throws Exception {
        data.deleteAllNodes();
    }
}
