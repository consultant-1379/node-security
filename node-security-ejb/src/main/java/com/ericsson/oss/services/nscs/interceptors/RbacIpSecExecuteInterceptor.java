/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.nscs.interceptors;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.ejb.command.rbac.AuthorizationBeanService;
import com.ericsson.nms.security.nscs.exception.RbacException;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.SecurityViolationException;

public class RbacIpSecExecuteInterceptor {

    @Inject
    private Logger logger;

    @Inject
    private AuthorizationBeanService authorizationBeanService;

    @AroundInvoke
    private Object authorize(InvocationContext ic) throws RbacException {

        Object obj = null;

        try {

            authorizationBeanService.checkIpsecExecute();

            obj = ic.proceed();

        } catch (Exception e) {
            if (SecurityViolationException.class.equals(e.getCause().getClass())) {
                throw new RbacException("Logged user is not authorized to perform this action: " + e.getMessage());
            } else {
                logger.error(this.getClass() + ": authorize: " + e.getMessage());
            }
        }
        return obj;

    }

}
