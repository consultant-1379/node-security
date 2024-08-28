package com.ericsson.oss.services.nscs.workflow.tasks.ejb.async;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.workflow.task.WFActionTaskHandler;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowActionTask;

/**
 * @author emaynes.
 */
@RunWith(MockitoJUnitRunner.class)
public class WorkflowTaskAsyncExecutorBeanTest {

    @Spy
    private Logger log = LoggerFactory.getLogger(WorkflowTaskAsyncExecutorBean.class);

    @Mock
    private WFActionTaskHandler<WorkflowActionTask> taskHandler;

    @Mock
    private WorkflowActionTask task;

    @Mock
    private AsyncProcessHandler<WorkflowActionTask> processHandler;

    @InjectMocks
    private WorkflowTaskAsyncExecutorBean underTest;

    @Test
    public void successCallbackInvocationTest() {
        underTest.processTaskAsync(taskHandler, task, processHandler);

        verify(processHandler).onSuccess(task);
    }

    @Test
    public void errorCallbackInvocationTest() {
        Exception e = new NullPointerException();
        doThrow(e).when(taskHandler).processTask(task);
        underTest.processTaskAsync(taskHandler, task, processHandler);

        verify(processHandler).onError(task, e);
    }

}
