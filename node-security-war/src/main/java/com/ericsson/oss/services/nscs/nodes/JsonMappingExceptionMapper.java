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
package com.ericsson.oss.services.nscs.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.rest.DTOIpSecConfigValidityStatus;
import com.ericsson.nms.security.nscs.api.rest.IpSecConfigInvalidElement;
import com.ericsson.nms.security.nscs.api.rest.IpSecConfigValidityStatus;
import com.ericsson.nms.security.nscs.api.rest.IpSecValidityErrorCode;
import com.fasterxml.jackson.databind.JsonMappingException;

@Provider
public class JsonMappingExceptionMapper implements ExceptionMapper<JsonMappingException> {
    @Inject
    private Logger logger;

    @Override
    public Response toResponse(JsonMappingException exception) {

        DTOIpSecConfigValidityStatus dtoIpSecConfigValidityStatus = new DTOIpSecConfigValidityStatus();

        /*
         * Exception example
         * 
         * exception.getMessage()
         * 
         * 
         * This is an invalid request. At least one field format is not readable by the system, error: Can not construct instance of boolean from String value '34': only "true" or "false" recognized
         * at [Source: org.jboss.resteasy.core.interception.MessageBodyReaderContextImpl$InputStreamWrapper@6010f4cd; line: 68, column: 11] (through reference chain:
         * com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.Nodes["node"]->com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.Node["disableOMConfiguration"]->com.ericsson.nms.security.nscs.cpp.ipsec
         * .input.xml.DisableOMConfiguration["removeCert"])
         * 
         * Regex: ^[\s\S]*'(\S+)':[\s\S]*"(\w+)"\]\)$
         */

        String errorMessage = "This is an invalid request. At least one field format is not readable by the system, Exception: " + exception.getMessage();
        logger.error(errorMessage);

        List<IpSecConfigValidityStatus> result = new ArrayList<IpSecConfigValidityStatus>();
        IpSecConfigValidityStatus e = new IpSecConfigValidityStatus();
        e.setName("*");
        List<IpSecConfigInvalidElement> ipsecConfigInvalidElements = new ArrayList<IpSecConfigInvalidElement>();

        //Match any value (alphanumeric or dot or anything else as invalid value and name from exception)
        final Pattern fieldFormatIsNotReadableByTheSystemPattern = Pattern.compile("^[\\s\\S]*'(\\S+)':[\\s\\S]*\"(\\w+)\"\\]\\)$");
        //It will get 2 groups, 
        //group(1) = value of invalid element
        //group(2) = name of invalid element
        final Matcher matcher = fieldFormatIsNotReadableByTheSystemPattern.matcher(exception.getMessage());
        String elementName = "*";
        String elementvalue = "*";
        if (matcher.find()) {
            elementvalue = matcher.group(1);
            elementName = matcher.group(2);
        } else {
            logger.warn("Not able to parse exception and pick up data!");
        }

        String errorMessages = "Value [" + elementvalue + "] is not valid";
        IpSecConfigInvalidElement ipsecInvalidElement = new IpSecConfigInvalidElement(elementName, errorMessages, IpSecValidityErrorCode.BAD_DATA_FORMAT);
        ipsecConfigInvalidElements.add(ipsecInvalidElement);
        e.setIpsecConfigInvalidElements(ipsecConfigInvalidElements);
        result.add(e);
        dtoIpSecConfigValidityStatus.setIpSecConfigValidityStatus(result);

        return Response.status(Response.Status.BAD_REQUEST).entity(dtoIpSecConfigValidityStatus).type(MediaType.APPLICATION_JSON).build();

    }
}
