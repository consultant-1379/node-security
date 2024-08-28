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

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import com.ericsson.nms.security.nscs.api.exception.NscsSecurityViolationException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.SecurityViolationException;

public class NbiManagerExceptionInterceptor {

    @AroundInvoke
    public Object logException(final InvocationContext invocationContext) throws Exception {

        Object result = null;

        try {
            result = invocationContext.proceed();
        } catch (final SecurityViolationException e) {
            throw new NscsSecurityViolationException(e);
        } catch (final NscsServiceException e) {
            throw e;
        } catch (final Exception e) {
            throw new UnexpectedErrorException(e);
        }

        return result;
    }

}
