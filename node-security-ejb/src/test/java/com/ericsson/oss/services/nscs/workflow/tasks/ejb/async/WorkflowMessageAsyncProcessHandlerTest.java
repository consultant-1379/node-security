package com.ericsson.oss.services.nscs.workflow.tasks.ejb.async;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowActionTask;

/**
 * @author emaynes.
 */
@RunWith(MockitoJUnitRunner.class)
public class WorkflowMessageAsyncProcessHandlerTest {

    @Mock
    private WorkflowHandler workflowHandler;

    @Mock
    private WorkflowActionTask task;

    @Mock
    private NodeReference node;

    @Before
    public void setup() {
        doReturn(node).when(task).getNode();
    }

    @Test
    public void successWithMessageTest() {
        String message = "messageToWorkflow";
        WorkflowMessageAsyncProcessHandler underTest = new WorkflowMessageAsyncProcessHandler(workflowHandler, null, message);

        underTest.onSuccess(task);

        verify(workflowHandler).dispatchMessage(node, message);
    }

    @Test
    public void successWithEmptyMessageTest() {
        String message = "";
        WorkflowMessageAsyncProcessHandler underTest = new WorkflowMessageAsyncProcessHandler(workflowHandler, null, message);

        underTest.onSuccess(task);

        verify(workflowHandler, never()).dispatchMessage(any(NodeReference.class), anyString());
    }

    @Test
    public void successWithoutMessageTest() {
        WorkflowMessageAsyncProcessHandler underTest = new WorkflowMessageAsyncProcessHandler(workflowHandler, null, null);

        underTest.onSuccess(task);

        verify(workflowHandler, never()).dispatchMessage(any(NodeReference.class), anyString());
    }

    @Test
    public void errorWithMessageTest() {
        String message = "messageToWorkflow";
        WorkflowMessageAsyncProcessHandler underTest = new WorkflowMessageAsyncProcessHandler(workflowHandler, message, null);

        underTest.onError(task, null);

        verify(workflowHandler).dispatchMessage(node, message);
    }

    @Test
    public void errorWithEmptyMessageTest() {
        String message = "";
        WorkflowMessageAsyncProcessHandler underTest = new WorkflowMessageAsyncProcessHandler(workflowHandler, message, null);

        underTest.onError(task, null);

        verify(workflowHandler, never()).dispatchMessage(any(NodeReference.class), anyString());
    }

    @Test
    public void errorWithoutMessageTest() {
        WorkflowMessageAsyncProcessHandler underTest = new WorkflowMessageAsyncProcessHandler(workflowHandler, null, null);

        underTest.onError(task, null);

        verify(workflowHandler, never()).dispatchMessage(any(NodeReference.class), anyString());
    }
}
