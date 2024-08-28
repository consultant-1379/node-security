/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.ldap;

import javax.ejb.Local;

@Local
public interface EcimPwdFileManager {

    /**
     * Retrieves the LDAP ECIM Common User password from the store file
     *
     * @param filename the stored filename
     * @return the retrieved password
     */
    String readPassword(String filename);

}
