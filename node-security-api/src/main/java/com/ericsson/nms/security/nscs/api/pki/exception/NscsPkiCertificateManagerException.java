/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.pki.exception;
import java.io.Serializable;

/**
 * NscsPkiCertificateManagerException to support Exceptions related to the NscsPkiCertificateManager.
 * 
 * @author zlaxsri
 * 
 */
public class NscsPkiCertificateManagerException extends Exception implements Serializable {

    private static final long serialVersionUID = 1L;

    public NscsPkiCertificateManagerException(final String msg) {
        super(msg);
    }

    public NscsPkiCertificateManagerException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}