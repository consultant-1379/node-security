package com.ericsson.oss.services.nscs.workflow.tasks.api.exception;

import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

/**
 * @author emaynes.
 */
public class InvalidMoAttributeValueExceptionTest extends ExceptionInstantiationBaseTest {

    public InvalidMoAttributeValueExceptionTest(Class<? extends Exception> exceptonClazz, Object[] constructorArgs, String message) {
        super(exceptonClazz, constructorArgs, message);
    }

    @Parameterized.Parameters
    public static List<Object[]> exceptions() {
        return Arrays.asList(new Object[][]{
                {InvalidMoAttributeValueException.class, new Object[]{}, null},
                {InvalidMoAttributeValueException.class, new Object[]{DEFAULT_ERROR_MESSAGE}, DEFAULT_ERROR_MESSAGE},
                {InvalidMoAttributeValueException.class, new Object[]{DEFAULT_ERROR_MESSAGE, new Throwable()}, DEFAULT_ERROR_MESSAGE},
                {InvalidMoAttributeValueException.class, new Object[]{new Throwable(DEFAULT_ERROR_MESSAGE)}, INNER_ERROR_MESSAGE},
                {InvalidMoAttributeValueException.class, new Object[]{DEFAULT_ERROR_MESSAGE, new Throwable(), true, true}, DEFAULT_ERROR_MESSAGE},
                {InvalidMoAttributeValueException.class, new Object[]{"node1", "attribute", "wrongValue"}, "Value 'wrongValue' is invalid for attribute 'attribute' at node [node1]."},
                {InvalidMoAttributeValueException.class, new Object[]{"node1", "attribute", "wrongValue", "rightValue"}, "Value 'wrongValue' is invalid for attribute 'attribute' at node [node1]. Expected value(s): rightValue"}
        });
    }

}
