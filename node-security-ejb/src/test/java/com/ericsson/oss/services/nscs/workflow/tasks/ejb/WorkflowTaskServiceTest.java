package com.ericsson.oss.services.nscs.workflow.tasks.ejb;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import java.util.HashSet;

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;


import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import javax.validation.Validator;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;

import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.workflow.task.WFActionTaskHandler;

import com.ericsson.nms.security.nscs.workflow.task.cpp.attribute.ReadFileTransferClientModeTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.cpp.moaction.InstallTrustedCertificatesTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.cpp.ssh.EnableSecureFileTransferClientModeTaskHandler;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.InvalidNodeException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskHandlerNotFoundException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowActionTask;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.ReadFileTransferClientModeTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.InstallTrustedCertificatesTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.ssh.EnableSecureFileTransferClientModeTask;
import com.ericsson.oss.services.nscs.workflow.tasks.ejb.async.AsyncProcessHandler;
import com.ericsson.oss.services.nscs.workflow.tasks.ejb.async.WorkflowTaskAsyncExecutorBean;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.classic.EAccessControlBean;

/**
 * Created by emaynes on 25/06/2014.
 */
@RunWith(MockitoJUnitRunner.class)
public class WorkflowTaskServiceTest {

    @Spy
    private Logger log = LoggerFactory.getLogger(WorkflowTaskServiceBean.class);

    @Mock
    private BeanManager beanManager;

    @Mock
    private Bean<?> bean;

    @Mock
    private CreationalContext creationalContext;

    @Mock
    private EAccessControlBean eAccessControl;

    @Mock
    private ReadFileTransferClientModeTaskHandler readFileTransferClientModeTaskHandler;

    @Mock
    private EnableSecureFileTransferClientModeTaskHandler secureFileTransferClientModeTaskHandler;

    @Mock
    private InstallTrustedCertificatesTaskHandler installTrustedCertificatesTaskHandler;

    @Mock
    private Validator validator;

    @Mock
    private WorkflowTaskAsyncExecutorBean workflowTaskAsyncExecutorBean;

    private static final String FDN = "MeContext=ERBS_001";

    @InjectMocks
    private WorkflowTaskServiceBeanStub taskServiceBean;

    @Before
    public void prepare() {
        Set<Bean<?>> beans = new HashSet<Bean<?>>();
        beans.add(bean);
        when(beanManager.getBeans(any(Type.class), any(Annotation.class))).thenReturn(beans);
        when(beanManager.createCreationalContext(bean)).thenReturn(creationalContext);
    }

    @Test
    public void processTask_withCorrectWorkflowQueryTask() {
        final ReadFileTransferClientModeTask workflowQueryTask = new ReadFileTransferClientModeTask(FDN);

        when(beanManager.getReference(Mockito.eq(bean), any(Type.class), Mockito.eq(creationalContext))).thenReturn(readFileTransferClientModeTaskHandler);

        taskServiceBean.processTask(workflowQueryTask);

        verify(readFileTransferClientModeTaskHandler).processTask(workflowQueryTask);
    }

    @Test(expected = WorkflowTaskHandlerNotFoundException.class)
    public void processTask_withInvalidQueryTaskType() {
        final ReadFileTransferClientModeTask workflowQueryTask = new ReadFileTransferClientModeTask(FDN);

        when(beanManager.getBeans(any(Type.class), any(Annotation.class))).thenReturn(new HashSet<Bean<?>>());

        taskServiceBean.processTask(workflowQueryTask);
    }

    @Test(expected = InvalidNodeException.class)
    public void processTask_withQueryTaskHandlerKnowException() {
        final ReadFileTransferClientModeTask workflowQueryTask = new ReadFileTransferClientModeTask(FDN);

        when(beanManager.getReference(Mockito.eq(bean), any(Type.class), Mockito.eq(creationalContext))).thenReturn(readFileTransferClientModeTaskHandler);
        when(readFileTransferClientModeTaskHandler.processTask(any(ReadFileTransferClientModeTask.class))).thenThrow(new InvalidNodeException());

        taskServiceBean.processTask(workflowQueryTask);
    }

    @Test(expected = UnexpectedErrorException.class)
    public void processTask_withQueryTaskHandlerUnknowException() {
        final ReadFileTransferClientModeTask workflowQueryTask = new ReadFileTransferClientModeTask(FDN);

        when(beanManager.getReference(Mockito.eq(bean), any(Type.class), Mockito.eq(creationalContext))).thenReturn(readFileTransferClientModeTaskHandler);
        when(readFileTransferClientModeTaskHandler.processTask(any(ReadFileTransferClientModeTask.class))).thenThrow(new IllegalArgumentException());

        taskServiceBean.processTask(workflowQueryTask);
    }

    @Test
    public void processTask_withCorrectWorkflowActionTask() {
        final EnableSecureFileTransferClientModeTask workflowActionTask = new EnableSecureFileTransferClientModeTask(FDN);

        when(beanManager.getReference(Mockito.eq(bean), any(Type.class), Mockito.eq(creationalContext))).thenReturn(secureFileTransferClientModeTaskHandler);

        taskServiceBean.processTask(workflowActionTask);

        verify(secureFileTransferClientModeTaskHandler).processTask(workflowActionTask);
    }

    @Test(expected = WorkflowTaskHandlerNotFoundException.class)
    public void processTask_withInvalidActionTaskType() {
        final EnableSecureFileTransferClientModeTask workflowActionTask = new EnableSecureFileTransferClientModeTask(FDN);

        when(beanManager.getBeans(any(Type.class), any(Annotation.class))).thenReturn(new HashSet<Bean<?>>());

        taskServiceBean.processTask(workflowActionTask);
    }

    @Test(expected = InvalidNodeException.class)
    public void processTask_withActionTaskHandlerKnowException() {
        final EnableSecureFileTransferClientModeTask workflowActionTask = new EnableSecureFileTransferClientModeTask(FDN);

        when(beanManager.getReference(Mockito.eq(bean), any(Type.class), Mockito.eq(creationalContext))).thenReturn(secureFileTransferClientModeTaskHandler);
        doThrow(new InvalidNodeException()).when(secureFileTransferClientModeTaskHandler)
                .processTask(any(EnableSecureFileTransferClientModeTask.class));

        taskServiceBean.processTask(workflowActionTask);

    }

    @Test(expected = UnexpectedErrorException.class)
    public void processTask_withActionTaskHandlerUnknowException() {
        final EnableSecureFileTransferClientModeTask workflowActionTask = new EnableSecureFileTransferClientModeTask(FDN);

        when(beanManager.getReference(Mockito.eq(bean), any(Type.class), Mockito.eq(creationalContext))).thenReturn(secureFileTransferClientModeTaskHandler);
        doThrow(new IllegalArgumentException()).when(secureFileTransferClientModeTaskHandler)
                .processTask(any(EnableSecureFileTransferClientModeTask.class));

        taskServiceBean.processTask(workflowActionTask);
    }

    @Test
    public void processTask_withAsyncWorkflowActionTask() {
        final InstallTrustedCertificatesTask workflowActionTask = new InstallTrustedCertificatesTask(FDN, null);

        when(beanManager.getReference(Mockito.eq(bean), any(Type.class), Mockito.eq(creationalContext))).thenReturn(installTrustedCertificatesTaskHandler);

        taskServiceBean.processTask(workflowActionTask);

        verify(workflowTaskAsyncExecutorBean).processTaskAsync(any(WFActionTaskHandler.class), any(WorkflowActionTask.class),
                any(AsyncProcessHandler.class));

    }

}

class WorkflowTaskServiceBeanStub extends WorkflowTaskServiceBean {
    @Override
    protected void flushContext() {}
}
