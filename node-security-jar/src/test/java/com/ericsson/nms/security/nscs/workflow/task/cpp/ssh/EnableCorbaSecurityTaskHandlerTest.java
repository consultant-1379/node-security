package com.ericsson.nms.security.nscs.workflow.task.cpp.ssh;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.handler.command.impl.MockUtils;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.itpf.sdk.eventbus.model.EventSender;
import com.ericsson.oss.mediation.sec.model.SSHCommandJob;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.ssh.EnableCorbaSecurityTask;

/**
 * Unit test for EnableCorbaSecurityTaskHandler
 * @author emaynes
 */
@RunWith(MockitoJUnitRunner.class)
public class EnableCorbaSecurityTaskHandlerTest {

    private static final String NODE_NAME = "ERBS_001";
    private static final String FDN = "MeContext=" + NODE_NAME;

    @Mock
    private NscsLogger nscsLogger;

    @Mock
    private EventSender<SSHCommandJob> commandJobSender;

    @Mock
    private NscsCMReaderService readerService;

    @InjectMocks
    private EnableCorbaSecurityTaskHandler handlerUnderTest;

    @Test
    public void handlerInvocationTest() {

        doReturn(MockUtils.createNormalizableNodeRef(NODE_NAME)).when(readerService).getNormalizedNodeReference(any(NodeReference.class));

        final EnableCorbaSecurityTask task = new EnableCorbaSecurityTask(FDN);
        handlerUnderTest.processTask(task);

        verify(commandJobSender).send(any(SSHCommandJob.class));
    }
}
