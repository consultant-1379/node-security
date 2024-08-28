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

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.exception.NscsSecurityViolationException;
import com.ericsson.oss.itpf.sdk.recording.ErrorSeverity;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.SecurityViolationException;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.annotation.Authorize;

@NscsSecurityViolationHandled
@Interceptor
public class NscsSecurityViolationHandledInterceptor {

    @Inject
    private Logger logger;

    @Inject
    private SystemRecorder systemRecorder;

    @AroundInvoke
    public Object toSecurityViolationException(final InvocationContext invocationContext) throws Exception {

        Object result = null;

        try {
            result = invocationContext.proceed();
        } catch (final SecurityViolationException e) {

            final String source = "Node Security";
            final Method method = invocationContext.getMethod();
            final String resource = getResource(method);
            final String action = getAction(method);
            final String errorMsg = String.format("The User does not have permissions to perform %s action.", action);
            systemRecorder.recordError(NscsSecurityViolationException.class.getCanonicalName(), ErrorSeverity.ERROR, source, resource, errorMsg);
            throw new NscsSecurityViolationException(errorMsg, e);
        }

        return result;
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

    /**
     * Get from the given method the action of the Authorize annotation.
     * 
     * Added essentially for groovy tests since it is not possible to mock the final classes (Method in this case).
     * 
     * @param method
     *            the method.
     * @return the action of the Authorize annotation or empty string if method annotation cannot be retrieved (groovy tests).
     */
    private String getAction(final Method method) {
        Authorize annotation = null;
        try {
            annotation = method.getAnnotation(Authorize.class);
        } catch (final Exception e) {
            final String errorMessage = String.format("Exception [%s] msg [%s] retrieving Authorize annotation", e.getClass().getCanonicalName(),
                    e.getMessage());
            logger.error(errorMessage, e);
        }
        return annotation != null ? annotation.action() : "";
    }
}
