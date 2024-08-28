/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2024
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.nbi.interceptor;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import com.ericsson.oss.services.security.nscs.nbi.logger.NbiLogRecorderDto;
import com.ericsson.oss.services.security.nscs.nbi.logger.NbiLogger;

public class NbiLoggerInterceptor {

    @Inject
    private NbiLogger nbiLogger;

    @Inject
    private NbiLogRecorderDto nbiLogRecorderDto;

    @AroundInvoke
    public Object restResultLog(final InvocationContext invocationContext) throws Exception {

        Object result = null;
        try {
            nbiLogger.recordRestStarted();
            result = invocationContext.proceed();
            nbiLogger.recordRestFinishedWithSuccess();
        } catch (final Exception e) {
            final String errorDetail = String.format("Exception [%s] occurred : message [%s].", e.getClass().getCanonicalName(), e.getMessage());
            nbiLogRecorderDto.getAdditionalInfo().setErrorDetail(errorDetail);
            nbiLogger.recordRestFinishedWithError();
            throw e;
        }
        return result;
    }
}
