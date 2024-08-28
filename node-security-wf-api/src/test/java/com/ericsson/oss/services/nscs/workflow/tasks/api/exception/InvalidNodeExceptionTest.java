package com.ericsson.oss.services.nscs.workflow.tasks.api.exception;

import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

/**
 * @author emaynes.
 */
public class InvalidNodeExceptionTest extends ExceptionInstantiationBaseTest {

    public InvalidNodeExceptionTest(Class<? extends Exception> exceptonClazz, Object[] constructorArgs, String message) {
        super(exceptonClazz, constructorArgs, message);
    }


    @Parameterized.Parameters
    public static List<Object[]> exceptions() {
        return Arrays.asList(new Object[][]{
                {InvalidNodeException.class, new Object[]{}, null},
                {InvalidNodeException.class, new Object[]{DEFAULT_ERROR_MESSAGE}, DEFAULT_ERROR_MESSAGE},
                {InvalidNodeException.class, new Object[]{DEFAULT_ERROR_MESSAGE, new Throwable()}, DEFAULT_ERROR_MESSAGE},
                {InvalidNodeException.class, new Object[]{new Throwable(DEFAULT_ERROR_MESSAGE)}, INNER_ERROR_MESSAGE},
                {InvalidNodeException.class, new Object[]{DEFAULT_ERROR_MESSAGE, new Throwable(), true, true}, DEFAULT_ERROR_MESSAGE},
        });
    }

}
