package com.ericsson.oss.services.nscs.workflow.tasks.api.exception;

import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

/**
 * @author emaynes.
 */
public class WorkflowTaskHandlerNotFoundExceptionTest extends ExceptionInstantiationBaseTest {

    public WorkflowTaskHandlerNotFoundExceptionTest(Class<? extends Exception> exceptonClazz, Object[] constructorArgs, String message) {
        super(exceptonClazz, constructorArgs, message);
    }

    @Parameterized.Parameters
    public static List<Object[]> exceptions() {
        return Arrays.asList(new Object[][]{
                {WorkflowTaskHandlerNotFoundException.class, new Object[]{}, null},
                {WorkflowTaskHandlerNotFoundException.class, new Object[]{DEFAULT_ERROR_MESSAGE}, DEFAULT_ERROR_MESSAGE},
                {WorkflowTaskHandlerNotFoundException.class, new Object[]{DEFAULT_ERROR_MESSAGE, new Throwable()}, DEFAULT_ERROR_MESSAGE},
                {WorkflowTaskHandlerNotFoundException.class, new Object[]{new Throwable(DEFAULT_ERROR_MESSAGE)}, INNER_ERROR_MESSAGE},
                {WorkflowTaskHandlerNotFoundException.class, new Object[]{DEFAULT_ERROR_MESSAGE, new Throwable(), true, true}, DEFAULT_ERROR_MESSAGE},
        });
    }

}
