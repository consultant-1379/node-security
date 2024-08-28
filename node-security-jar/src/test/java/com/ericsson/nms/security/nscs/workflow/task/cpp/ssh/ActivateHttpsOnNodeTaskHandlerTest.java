/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.nms.security.nscs.workflow.task.cpp.ssh;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.handler.command.impl.MockUtils;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.itpf.sdk.eventbus.model.EventSender;
import com.ericsson.oss.mediation.sec.model.SSHCommandJob;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.InvalidNodeException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.ssh.ActivateHttpsOnNodeTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ActivateHttpsOnNodeTaskHandlerTest {

    private static final String NODE_NAME = "ERBS_001";
    private static final String FDN = "MeContext=" + NODE_NAME;

    @Mock
    private NscsLogger nscsLogger;

    @Mock
    private NscsCMReaderService readerService;

    @InjectMocks
    private ActivateHttpsOnNodeTaskHandler handlerUnderTests;

    @Mock
    private EventSender<SSHCommandJob> commandJobSender;


    @Test
    public void processTaskShouldSendSSHCommand(){

        doReturn(MockUtils.createNormalizableNodeRef(NODE_NAME)).when(readerService).getNormalizedNodeReference(any(NodeReference.class));
        activateTask();
        verify(commandJobSender).send(any(SSHCommandJob.class));
    }

    @Test(expected = InvalidNodeException.class)
    public void processTaskShouldThrowInvalidNodeException(){
        activateTask();
    }

    private void activateTask(){
        final ActivateHttpsOnNodeTask task = new ActivateHttpsOnNodeTask(FDN);
        handlerUnderTests.processTask(task);
    }
}
