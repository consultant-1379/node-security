/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.nms.security.nscs.api.resources;

import javax.ejb.Remote;

import com.ericsson.oss.itpf.sdk.core.annotation.EService;

//TODO Remove this EJB once Resource Adaptor fix (#TORF-34808) for writing files is in place
/**
 * The Interface FileResources.
 */
@EService
@Remote
public interface FileResources {

    /**
     * Write file system resource
     *
     * @param fileLocation the file location
     * @param content the content
     */
    public void writeFileSystemResource(final String fileLocation, final byte[] content);

    public boolean existsFileSystemResource(final String fileLocation);
}
