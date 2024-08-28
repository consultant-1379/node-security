package com.ericsson.oss.services.nscs.workflow.tasks.api.exception;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.Constructor;
import java.util.Arrays;

/**
 * @author emaynes.
 */
@RunWith(Parameterized.class)
public abstract class ExceptionInstantiationBaseTest {

    protected static final String DEFAULT_ERROR_MESSAGE = "Error message";
    protected static final String INNER_ERROR_MESSAGE = "java.lang.Throwable: " + DEFAULT_ERROR_MESSAGE;

    private Class<? extends Exception> exceptonClazz;
    private Object[] constructorArgs;
    private String message;

    public ExceptionInstantiationBaseTest(Class<? extends Exception> exceptonClazz, Object[] constructorArgs, String message) {
        this.exceptonClazz = exceptonClazz;
        this.constructorArgs = constructorArgs;
        this.message = message;
    }

    @Test
    public void testInstantiation() throws Exception{

        Class<?>[] argTypes = getArgTypes();
        Constructor<? extends Exception> constructor = findConstructor(exceptonClazz, argTypes);
        Exception exception = constructor.newInstance(constructorArgs);

        Assert.assertEquals(message, exception.getMessage());
    }


    private Class<?>[] getArgTypes() {
        Class<?>[] args = new Class[constructorArgs.length];
        int idx = 0;
        for (Object constructorArg : constructorArgs) {
            if ( constructorArg instanceof Class) {
                args[idx] = (Class<?>) constructorArg;
            } else if(constructorArg != null) {
                args[idx] = constructorArg.getClass();
            }
            idx++;
        }
        return args;
    }

    private <C> Constructor<C> findConstructor(Class<C> c, Class<?>[] argTypes){
        for(Constructor con : c.getDeclaredConstructors()){
            Class[] types = con.getParameterTypes();
            if(types.length!=argTypes.length)
                continue;
            boolean match = true;
            for(int i = 0; i < types.length; i++){
                Class need = types[i], got = argTypes[i];
                if(!need.isAssignableFrom(got)){
                    if(need.isPrimitive()){
                        match = (int.class.equals(need) && Integer.class.equals(got))
                                || (long.class.equals(need) && Long.class.equals(got))
                                || (char.class.equals(need) && Character.class.equals(got))
                                || (short.class.equals(need) && Short.class.equals(got))
                                || (boolean.class.equals(need) && Boolean.class.equals(got))
                                || (byte.class.equals(need) && Byte.class.equals(got));
                    }else{
                        match = false;
                    }
                }
                if(!match)
                    break;
            }
            if(match)
                return con;
        }
        throw new IllegalArgumentException("Cannot find an appropriate constructor for class " + c + " and arguments types " + Arrays.toString(argTypes));
    }
}
