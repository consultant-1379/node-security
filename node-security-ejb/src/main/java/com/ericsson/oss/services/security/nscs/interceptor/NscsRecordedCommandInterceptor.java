/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2022
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.interceptor;

import java.lang.reflect.Method;
import java.util.Locale;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;

import com.ericsson.oss.itpf.sdk.recording.CommandPhase;
import com.ericsson.oss.itpf.sdk.recording.ErrorSeverity;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.annotation.Authorize;

@NscsRecordedCommand
@Interceptor
public class NscsRecordedCommandInterceptor {

    @Inject
    private Logger logger;

    @Inject
    private SystemRecorder systemRecorder;

    @AroundInvoke
    public Object recordCommand(final InvocationContext invocationContext) throws Exception {

        final String source = "Node Security API";
        final Method method = invocationContext.getMethod();
        final String commandName = getCommandName(method);
        final String resource = getResource(method);

        systemRecorder.recordCommand(commandName, CommandPhase.STARTED, source, resource, null);

        Object result = null;

        try {
            result = invocationContext.proceed();
        } catch (final Exception e) {
            systemRecorder.recordError(e.getClass().getCanonicalName(), ErrorSeverity.ERROR, source, resource, e.getMessage());
            systemRecorder.recordCommand(commandName, CommandPhase.FINISHED_WITH_ERROR, source, resource, null);
            throw e;
        }

        systemRecorder.recordCommand(commandName, CommandPhase.FINISHED_WITH_SUCCESS, source, resource, null);

        return result;
    }

    /**
     * Get from the given method the method/command name.
     * 
     * Added essentially for groovy tests since it is not possible to mock the final classes (Method in this case).
     * 
     * @param method
     *            the method.
     * @return the method name in upper case or empty string if method name cannot be retrieved (groovy tests).
     */
    private String getCommandName(final Method method) {
        return method.getName() != null ? method.getName().toUpperCase(Locale.ENGLISH) : "";
    }

    /**
     * Get from the given method the resource of the Authorize annotation.
     * 
     * Added essentially for groovy tests since it is not possible to mock the final classes (Method in this case).
     * 
     * @param method
     *            the method.
     * @return the resource of the Authorize annotation or empty string if method annotation cannot be retrieved (groovy tests).
     */
    private String getResource(final Method method) {
        Authorize annotation = null;
        try {
            annotation = method.getAnnotation(Authorize.class);
        } catch (final Exception e) {
            final String errorMessage = String.format("Exception [%s] msg [%s] retrieving Authorize annotation", e.getClass().getCanonicalName(),
                    e.getMessage());
            logger.error(errorMessage, e);
        }
        return annotation != null ? annotation.resource() : "";
    }
}
