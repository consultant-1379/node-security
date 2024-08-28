/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.ldap.utility;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.types.NscsNodeCommand;
import com.ericsson.nms.security.nscs.api.exception.InvalidFileContentException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;

/**
 * This class is responsible for reading the LDAP configuration file and returns
 * the stringified version of the file contents.
 * 
 * @author xsrirko
 *
 */
public class LdapCommandHandlerUtils {

    @Inject
    private Logger logger;

    /**
     * This method gets the string equivalent of input LDAP configuration file.
     * 
     */
    public String getLdapConfigurationXML(final NscsNodeCommand command, final String propertyKey) {

        logger.debug("command {}, propertyKey {}", command, propertyKey);

        final Map<String, Object> properties = command.getProperties();
        final byte[] fileDataInByte = (byte[]) properties.get(propertyKey);

        String fileData = null;
        if (fileDataInByte != null) {
            try {
                fileData = new String(fileDataInByte, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                logger.error(NscsErrorCodes.INVALID_ENCODING);
                throw new InvalidFileContentException(NscsErrorCodes.INVALID_INPUT_XML_FILE);
            }
        } else {
            logger.error("Invalid content of input XML file.");
            throw new InvalidFileContentException(NscsErrorCodes.INVALID_INPUT_XML_FILE);
        }

        return fileData;
    }

}
