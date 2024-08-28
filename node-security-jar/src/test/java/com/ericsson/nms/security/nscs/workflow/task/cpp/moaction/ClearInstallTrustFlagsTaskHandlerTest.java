package com.ericsson.nms.security.nscs.workflow.task.cpp.moaction;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.MOActionService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithoutParameter;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.ClearInstallTrustFlagsTask;

/**
 * Unit test for ClearInstallTrustFlagsTaskHandler
 * 
 * @author emaynes
 */
@RunWith(MockitoJUnitRunner.class)
public class ClearInstallTrustFlagsTaskHandlerTest {

    private static final NodeReference NODE = new NodeRef("MeContext=ERBS_001");

    @Mock
    private NscsLogger nscsLogger;

    @Mock
    private MOActionService moActionService;

    @Mock
    private WorkflowHandler workflowHandler;

    @Mock
    private ClearInstallTrustFlagsTask task;

    @Mock
    NscsCMReaderService readerService;

    @Mock
    NormalizableNodeReference normNode;

    @InjectMocks
    private ClearInstallTrustFlagsTaskHandler handlerUnderTest;

    @Before
    public void setup() {
        when(task.getNodeFdn()).thenReturn(NODE.getFdn());
        when(task.getNode()).thenReturn(NODE);
        when(readerService.getNormalizableNodeReference(NODE)).thenReturn(normNode);
    }

    @Test
    public void successHandlerInvocationTest() {

        handlerUnderTest.processTask(task);

        verify(moActionService).performMOAction(normNode.getFdn(), MoActionWithoutParameter.Security_cancelInstallTrustedCertificates);
    }

    @Test(expected = RuntimeException.class)
    public void failedHandlerInvocationTest() {

        doThrow(new RuntimeException("Error at MO action execution")).when(moActionService).performMOAction(any(String.class),
                any(MoActionWithoutParameter.class));

        handlerUnderTest.processTask(task);

    }
}
