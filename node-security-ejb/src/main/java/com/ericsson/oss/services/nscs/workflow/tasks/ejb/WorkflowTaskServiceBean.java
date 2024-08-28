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
package com.ericsson.oss.services.nscs.workflow.tasks.ejb;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import com.ericsson.nms.security.nscs.workflow.task.*;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.services.nscs.workflow.tasks.api.WorkflowTaskService;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.InvalidWorkflowTaskException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskFailureException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskHandlerNotFoundException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskTimeoutException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.AsyncActionTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowActionTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.ejb.async.AsyncProcessHandler;
import com.ericsson.oss.services.nscs.workflow.tasks.ejb.async.WorkflowMessageAsyncProcessHandler;
import com.ericsson.oss.services.nscs.workflow.tasks.ejb.async.WorkflowTaskAsyncExecutorBean;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.classic.EAccessControlBean;
import com.ericsson.oss.itpf.sdk.context.classic.ContextServiceBean;

/**
 * <p>
 * Main implementation of {@link WorkflowTaskService}.
 * </p>
 * <p>
 * This implementation creates an instance of {@link WFTaskHandler} according to the WorkflowTaskType value of the provided WorkflowTask and
 * dispatches the processing to the task handler.
 * </p>
 *
 * @author emaynes on 13/06/2014.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class WorkflowTaskServiceBean implements WorkflowTaskService {

    private static final String WORKFLOW_CONTEXT_USER_ID = "NO USER DATA";

    @Inject
    private Logger log;

    @Inject
    private BeanManager beanManager;

    @Inject
    private EAccessControlBean eAccessControl;

    @Inject
    private Validator validator;

    @Inject
    private WorkflowTaskAsyncExecutorBean workflowTaskAsyncExecutor;

    @EServiceRef
    private WorkflowHandler workflowHandler;

    @Override
    public void processTask(final WorkflowActionTask task) {

        final Bean<?> bean = getWorkflowActionTaskHandlerBeanForTaskType(task.getTaskType());
        final CreationalContext<?> creationalContext = beanManager.createCreationalContext(bean);
        try {
            @SuppressWarnings("unchecked")
            final WFActionTaskHandler<WorkflowActionTask> workflowHandler = (WFActionTaskHandler<WorkflowActionTask>) beanManager.getReference(bean,
                    WFTaskHandlerInterface.class, creationalContext);

            final WorkflowActionTask expectedTaskForHandler = (WorkflowActionTask) createExpectedTaskForHandler(workflowHandler, task);
            eAccessControl.setAuthUserSubject(WORKFLOW_CONTEXT_USER_ID);

            validateTask(expectedTaskForHandler);

            try {
                if (isAsyncTask(expectedTaskForHandler)) {
                    log.debug("Invoking [async] workflow task handler [{}] with arg [{}]", workflowHandler.getClass().getName(),
                            expectedTaskForHandler);
                    final AsyncProcessHandler<?> asyncProcessHandler = createAsyncProcessHandler(expectedTaskForHandler);
                    workflowTaskAsyncExecutor.processTaskAsync(workflowHandler, expectedTaskForHandler, asyncProcessHandler);
                } else {
                    log.debug("Invoking [sync] workflow task handler [{}] with arg [{}]", workflowHandler.getClass().getName(),
                            expectedTaskForHandler);
                    workflowHandler.processTask(expectedTaskForHandler);
                    log.debug("Task handler [{}] finished successfully", workflowHandler.getClass().getName());
                }
            } catch (final WorkflowTaskTimeoutException e) {
                log.error("Timeout during process task execution, re-throwing", e);
                throw e;
            } catch (final WorkflowTaskFailureException e) {
                log.error("Failure during process task execution, re-throwing", e);
                throw e;
            } catch (final WorkflowTaskException e) {
                log.error("Error during process task execution, re-throwing", e);
                throw e;
            } catch (final Exception e) {
                log.error("Unexpected error has occurred during process task execution.", e);
                throw new UnexpectedErrorException(e);
            }
        } finally {
            creationalContext.release();
            flushContext();
        }
    }

    @Override
    public String processTask(final WorkflowQueryTask task) {

        final Bean<?> bean = getWorkflowQueryTaskHandlerBeanForTaskType(task.getTaskType());
        final CreationalContext<?> creationalContext = beanManager.createCreationalContext(bean);
        try {
            @SuppressWarnings("unchecked")
            final WFQueryTaskHandler<WorkflowQueryTask> workflowHandler = (WFQueryTaskHandler<WorkflowQueryTask>) beanManager.getReference(bean,
                    WFTaskHandlerInterface.class, creationalContext);

            final WorkflowQueryTask expectedTaskForHandler = (WorkflowQueryTask) createExpectedTaskForHandler(workflowHandler, task);
            eAccessControl.setAuthUserSubject(WORKFLOW_CONTEXT_USER_ID);

            validateTask(expectedTaskForHandler);

            try {
                log.debug("Invoking workflow task handler [{}] with arg [{}]", workflowHandler.getClass().getName(), expectedTaskForHandler);
                return workflowHandler.processTask(expectedTaskForHandler);
            } catch (final WorkflowTaskTimeoutException e) {
                log.error("Timeout during process task execution, re-throwing", e);
                throw e;
            } catch (final WorkflowTaskFailureException e) {
                log.error("Failure during process task execution, re-throwing", e);
                throw e;
            } catch (final WorkflowTaskException e) {
                log.error("Error during process task execution, re-throwing", e);
                throw e;
            } catch (final Exception e) {
                log.error("Unexpected error has occurred during process task execution.", e);
                throw new UnexpectedErrorException(e);
            }
        } finally {
            creationalContext.release();
            flushContext();
        }
    }

    /**
     * Get the instance of workflow action task handler for the given qualifier.
     *
     * @param taskType
     *            the qualifier (the workflow task type).
     * @return the bean instance.
     * @throws WorkflowTaskHandlerNotFoundException
     *             if no instance or more than one instance found.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Bean<?> getWorkflowActionTaskHandlerBeanForTaskType(final WorkflowTaskType taskType) throws WorkflowTaskHandlerNotFoundException {
        try {
            final Set<Bean<?>> beans = beanManager.getBeans(WFTaskHandlerInterface.class, new TaskTypeQualifier(taskType));
            if (beans.size() == 1) {
                final Bean<WFActionTaskHandler> bean = (Bean<WFActionTaskHandler>) beans.iterator().next();
                return bean;
            } else if (beans.size() < 1) {
                final String msg = "No workflow action task handler registered for type " + taskType;
                log.error(msg);
                throw new WorkflowTaskHandlerNotFoundException(msg);
            } else {
                final String msg = "Multiple workflow action task handler found for type " + taskType;
                log.error(msg);
                throw new WorkflowTaskHandlerNotFoundException(msg);
            }
        } catch (final WorkflowTaskHandlerNotFoundException e) {
            throw e;
        } catch (final Exception e) {
            log.error("Internal Error retrieving workflow action task handler for type " + taskType);
            throw new WorkflowTaskHandlerNotFoundException(e);
        }
    }

    /**
     * Get the instance of workflow query task handler for the given qualifier.
     *
     * @param taskType
     *            the qualifier (the workflow task type).
     * @return the bean instance.
     * @throws WorkflowTaskHandlerNotFoundException
     *             if no instance or more than one instance found.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Bean<?> getWorkflowQueryTaskHandlerBeanForTaskType(final WorkflowTaskType taskType) {
        try {
            final Set<Bean<?>> beans = beanManager.getBeans(WFTaskHandlerInterface.class, new TaskTypeQualifier(taskType));
            if (beans.size() == 1) {
                final Bean<WFQueryTaskHandler> bean = (Bean<WFQueryTaskHandler>) beans.iterator().next();
                return bean;
            } else if (beans.size() < 1) {
                final String msg = "No workflow query task handler registered for type " + taskType;
                log.error(msg);
                throw new WorkflowTaskHandlerNotFoundException(msg);
            } else {
                final String msg = "Multiple workflow query task handler found for type " + taskType;
                log.error(msg);
                throw new WorkflowTaskHandlerNotFoundException(msg);
            }
        } catch (final WorkflowTaskHandlerNotFoundException e) {
            throw e;
        } catch (final Exception e) {
            log.error("Internal Error retrieving workflow query task handler for type ", taskType);
            throw new WorkflowTaskHandlerNotFoundException(e);
        }
    }

    private void validateTask(final WorkflowTask expectedTaskForHandler) {
        final Set<ConstraintViolation<WorkflowTask>> constraintViolations = validator.validate(expectedTaskForHandler);
        if (constraintViolations.size() > 0) {
            throw new InvalidWorkflowTaskException(generateValidationMessage(expectedTaskForHandler, constraintViolations));
        }
    }

    private String generateValidationMessage(final WorkflowTask workflowTask, final Set<ConstraintViolation<WorkflowTask>> constraintViolations) {
        final StringBuilder message = new StringBuilder();
        message.append("Workflow task [").append(workflowTask.getClass().getSimpleName()).append("] violated the following constraints:");
        int i = 1;
        for (final ConstraintViolation<?> constraintViolation : constraintViolations) {
            message.append("\n").append(i).append(" - message: [").append(constraintViolation.getMessage()).append("], property path : [")
                    .append(constraintViolation.getPropertyPath()).append("], constraint : [")
                    .append(constraintViolation.getConstraintDescriptor().getAnnotation()).append("]");
            i++;
        }

        return message.toString();
    }

    private WorkflowTask createExpectedTaskForHandler(final WFTaskHandler<?> handler, final WorkflowTask receivedTask) {
        log.debug("inside createExpectedTaskForHandler for handler:" + handler);
        final Class<? extends WorkflowTask> taskClass = getExpectedWorkflowTaskForHandler(handler);
        log.debug("taskClass inside createExpectedTaskForHandler:" + taskClass);
        try {
            final WorkflowTask workflowTask = taskClass.newInstance();
            workflowTask.setTaskType(receivedTask.getTaskType());
            workflowTask.getParameters().putAll(receivedTask.getParameters());

            final Map<String, Object> params = workflowTask.getParameters();

            log.debug("parameters from xml input is WorkflowTask:" + params);
            for (final String key : params.keySet()) {
                log.debug("key:" + key + "  value:" + params.get(key));
            }

            log.debug("created task handler's ({}) expected task instance as : {}", handler.getClass().getSimpleName(), workflowTask);
            return workflowTask;
        } catch (final Exception e) {
            log.debug("could not creates task handler's expected task instance (Missing default constructor?), throwing an exception...", e);
            throw new UnexpectedErrorException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private Class<? extends WorkflowTask> getExpectedWorkflowTaskForHandler(final WFTaskHandler<?> handler) {
        log.debug("inside getExpectedWorkflowTaskForHandler for handler:" + handler);
        Class<? extends WorkflowTask> taskClazz = null;

        Class<?> handlerClass = handler.getClass();
        log.debug("inside getExpectedWorkflowTaskForHandler for handler class:" + handlerClass);
        //this workaround to make this class testable
        if (handlerClass.getName().contains("ByMockito")) {
            handlerClass = handlerClass.getSuperclass();
        }
        log.debug("Before for loop inside getExpectedWorkflowTaskForHandler");
        for (final Type type : handlerClass.getGenericInterfaces()) {
            log.debug("Inside for loop type is:" + type);

            if (type instanceof ParameterizedType) {
                log.debug("Inside if condition type is:" + type);
                final ParameterizedType pType = (ParameterizedType) type;
                final Class<?> interfaceClazz = (Class<?>) pType.getRawType();

                log.debug("Before WFTaskHandler.class.isAssignableFrom(interfaceClazz):" + interfaceClazz);
                if (WFTaskHandler.class.isAssignableFrom(interfaceClazz)) {
                    log.debug("Inside WFTaskHandler.class.isAssignableFrom(interfaceClazz):" + interfaceClazz);

                    taskClazz = (Class<? extends WorkflowTask>) pType.getActualTypeArguments()[0];
                    log.debug("After taskClazz = (Class<? extends WorkflowTask>) pType.getActualTypeArguments()[0]:" + taskClazz);
                }
            }
        }

        log.debug("After for loop inside getExpectedWorkflowTaskForHandler handler class name :" + handlerClass.getName());

        if (handlerClass != null && handlerClass.getName().contains("CheckCertAlreadyInstalledTaskHandler") && taskClazz == null) {
            taskClazz = com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.CheckCertAlreadyInstalledTask.class;
        }

        if (handlerClass != null && handlerClass.getName().contains("InstallTrustedCertificatesIpSecTaskHandler") && taskClazz == null) {
            taskClazz = com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.InstallTrustedCertificatesIpSecTask.class;
        }

        if (handlerClass != null && handlerClass.getName().contains("ChangeIpForOMSettingHandler") && taskClazz == null) {
            taskClazz = com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.ChangeIpForOMSettingTask.class;
        }
        if (handlerClass != null && handlerClass.getName().contains("AddExternalServerTaskHandler") && taskClazz == null) {
            taskClazz = com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.AddExternalServerTask.class;
        }
        if (taskClazz == null) {
            log.error("Could not find expected task class for the task handler : {}", handler.getClass().getName());
            throw new UnexpectedErrorException("Could not determine expected handler's task type");
        }

        log.debug("Found task handler's expected task class. Task handler={}, task type={}", handler.getClass().getSimpleName(), taskClazz);
        return taskClazz;
    }

    private AsyncProcessHandler createAsyncProcessHandler(final WorkflowActionTask actionTask) {

        final AsyncActionTask annotation = actionTask.getClass().getAnnotation(AsyncActionTask.class);
        final WorkflowMessageAsyncProcessHandler asyncProcessHandler = new WorkflowMessageAsyncProcessHandler(workflowHandler,
                annotation.errorMessage(), annotation.successMessage());

        return asyncProcessHandler;
    }

    private boolean isAsyncTask(final WorkflowActionTask actionTask) {
        return actionTask.getClass().isAnnotationPresent(AsyncActionTask.class);
    }

    private class TaskTypeQualifier extends AnnotationLiteral<WFTaskType> implements WFTaskType {

        private static final long serialVersionUID = 8291177785341597991L;

        private final WorkflowTaskType taskType;

        private TaskTypeQualifier(final WorkflowTaskType taskType) {
            this.taskType = taskType;
        }

        @Override
        public WorkflowTaskType value() {
            return this.taskType;
        }
    }

    protected void flushContext() {
        new ContextServiceBean().flushContext();
    }
}
