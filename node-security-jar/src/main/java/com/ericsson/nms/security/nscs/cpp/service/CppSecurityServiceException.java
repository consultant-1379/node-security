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
package com.ericsson.nms.security.nscs.cpp.service;

import java.io.Serializable;
import javax.ejb.ApplicationException;

/**
 * CppSecurityServiceException to support Exceptions related to the CppSecurityService.
 * 
 * @author egbobcs
 * 
 */
@ApplicationException(rollback=true)
public class CppSecurityServiceException extends Exception implements Serializable {

    private static final long serialVersionUID = 1L;

    public CppSecurityServiceException(final String msg) {
        super(msg);
    }

    public CppSecurityServiceException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
