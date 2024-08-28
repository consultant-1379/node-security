/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2023
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.interceptor;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

public class RestLoggerInterceptor {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsContextService nscsContextService;

    @AroundInvoke
    public Object restFinished(final InvocationContext invocationContext) throws Exception {
        Object result = null;
        try {
            result = invocationContext.proceed();
        } catch (final Exception e) {
            final String errorDetail = e.getMessage() != null ? e.getMessage()
                    : String.format("Exception [%s] occurred.", e.getClass().getCanonicalName());
            nscsContextService.setErrorDetailContextValue(errorDetail);
            nscsLogger.restFinishedWithError();
            throw e;
        }
        nscsLogger.restFinishedWithSuccess();
        return result;
    }

}
