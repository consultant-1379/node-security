package com.ericsson.nms.security.nscs.interceptor;

import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

public class EjbLoggerInterceptor {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsContextService nscsContextService;

    @AroundInvoke
    public Object remoteEjbLogger(final InvocationContext invocationContext) throws Exception {
        Object result = null;
        try {
            final String className = invocationContext.getMethod().getDeclaringClass().getSimpleName();
            final String methodName = invocationContext.getMethod().getName();
            nscsContextService.setClassNameContextValue(className);
            nscsContextService.setMethodNameContextValue(methodName);
            nscsLogger.remoteEjbStarted();
            result = invocationContext.proceed();
        } catch (final Exception e) {
            final String errorDetail = e.getMessage() != null ? e.getMessage()
                    : String.format("Exception [%s] occurred.", e.getClass().getCanonicalName());
            nscsContextService.setErrorDetailContextValue(errorDetail);
            nscsLogger.remoteEjbFinishedWithError();
            throw e;
        }
        nscsLogger.remoteEjbFinishedWithSuccess();
        return result;
    }

}
