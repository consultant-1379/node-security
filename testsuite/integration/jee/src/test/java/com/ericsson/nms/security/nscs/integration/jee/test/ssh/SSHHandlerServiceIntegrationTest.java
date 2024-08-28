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
package com.ericsson.nms.security.nscs.integration.jee.test.ssh;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.integration.jee.test.utils.IntegrationTestBase;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityDataSetup;
import com.ericsson.oss.services.nscs.workflow.tasks.api.WorkflowTaskService;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.ssh.EnableCorbaSecurityTask;

@RunWith(Arquillian.class)
@Stateless
public class SSHHandlerServiceIntegrationTest extends IntegrationTestBase {

    @Inject
    private WorkflowTaskService workflowTaskService;

    @Inject
    SSHResultHandler shhResultHandler;

    @Inject
    NodeSecurityDataSetup dataSetup;

    @Inject
    Logger logger;

    @Test
    //@Ignore // Passed
    @InSequence(1)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testSendSSHHandlerServiceRequestAndSucceed() throws Exception {
        dataSetup.insertData();
        workflowTaskService.processTask(new EnableCorbaSecurityTask("MeContext=ERBS1"));
        Thread.sleep(3000);
    }

    @Test
    //@Ignore // Passed
    @InSequence(2)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testSendSSHHandlerServiceRequestAndFail() throws Exception {
        dataSetup.insertData();
        shhResultHandler.setShouldSendFail(true);
        workflowTaskService.processTask(new EnableCorbaSecurityTask("MeContext=ERBS1"));
        Thread.sleep(3000);
    }
}
