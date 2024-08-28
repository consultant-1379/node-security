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

package com.ericsson.nms.security.nscs.ejb.resources;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.resources.FileResources;
import com.ericsson.nms.security.nscs.laad.service.ResourcesBean;
import com.ericsson.oss.itpf.sdk.resources.Resource;

//TODO Remove this EJB once Resource Adaptor fix (#TORF-34808) for writing files is in place
/**
 *
 * Utility class to access filesystem resource.
 * @author esneani
 */
@Stateless
public class FileResourcesBean implements FileResources{

	/** The logger. */
	@Inject
    private Logger logger;

	/** The resources bean. */
	@Inject
	private ResourcesBean resourcesBean;

    /* (non-Javadoc)
     * @see com.ericsson.nms.security.nscs.api.resources.FileResources#writeFileSystemResource(java.lang.String, byte[])
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Override
    public void writeFileSystemResource(final String fileLocation, final byte[] content) {

    	logger.info("Writing file using resource adaptor at: {}", fileLocation);
    	final Resource resourceXml = resourcesBean.getFileSystemResource(fileLocation);
    	resourceXml.write(content, false);
    	logger.info("Written file at: {}", fileLocation);

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Override
    public boolean existsFileSystemResource(final String fileLocation) {

    	logger.info("Checking if file exists: {}", fileLocation);
    	final Resource resourceXml = resourcesBean.getFileSystemResource(fileLocation);
    	return resourceXml.exists();
    }
}
