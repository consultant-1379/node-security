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
package com.ericsson.nms.security.nscs.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;
import com.ericsson.nms.security.nscs.api.exception.InvalidFileContentException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;

/**
 * Utility to get content of input file through web cli.
 * 
 * @author tcsmave
 *
 */
public class CliUtil {

    @Inject
    private Logger logger;

    /**
     * Method to fetch the contents of input xml file(readable file)
     * 
     * @param command
     *            NscsPropertyCommand in which the file contents are provided.
     * @param propertyKey
     *            the property name of the file in command.
     * @return the XML file contents in String
     */
    public String getCommandInputData(final NscsPropertyCommand command, final String propertyKey) {

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

    /**
     * New method to fetch the contents of input xml file (readable file).
     * 
     * Since the old getCommandInputData method throws an {@link InvalidFileContentException} exception with a misleading message in case an
     * {@link UnsupportedEncodingException} is thrown and in order not to cause backward incompatibility in documentation for already delivered use
     * cases, this new method is added to be used in new use cases. The behavior is the same but the exceptions handling is changed according to the
     * actual errors.
     * 
     * @param command
     *            NscsPropertyCommand in which the file contents are provided.
     * @param propertyKey
     *            the property name of the file in command.
     * @return the XML file contents in String.
     * @throws {@link
     *             InvalidFileContentException} if invalid encoding or invalid contents for the input file.
     */
    public String getCommandInputDataWithNewExceptionHandling(final NscsPropertyCommand command, final String propertyKey) {

        logger.debug("get CommandInputDataWithNewExceptionHandling: command {}, propertyKey {}", command, propertyKey);

        final Map<String, Object> properties = command.getProperties();
        final byte[] fileDataInByte = (byte[]) properties.get(propertyKey);

        String fileData = null;
        if (fileDataInByte != null) {
            fileData = new String(fileDataInByte, StandardCharsets.UTF_8);
        } else {
            logger.error("get CommandInputDataWithNewExceptionHandling: invalid content of input XML file.");
            throw new InvalidFileContentException(NscsErrorCodes.INVALID_INPUT_XML_FILE);
        }

        return fileData;
    }
}
