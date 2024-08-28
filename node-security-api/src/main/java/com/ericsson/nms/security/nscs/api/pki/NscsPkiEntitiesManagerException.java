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
package com.ericsson.nms.security.nscs.api.pki;
import java.io.Serializable;
import javax.ejb.ApplicationException;

/**
 * NscsPkiEntitiesManagerException to support Exceptions related to the NscsPkiEntitiesManager.
 * 
 * @author elucbot
 * 
 */
@ApplicationException(rollback=true)
public class NscsPkiEntitiesManagerException extends Exception implements Serializable {

    private static final long serialVersionUID = 1L;

    public NscsPkiEntitiesManagerException(final String msg) {
        super(msg);
    }

    public NscsPkiEntitiesManagerException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
