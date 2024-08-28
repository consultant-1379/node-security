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
import com.fasterxml.jackson.core.JsonParseException;

/**
 * @author elucbot
 * 
 *         Invalid JSON format exception handler
 * 
 *         Example { "name": "Gaurav", IamInvalid "phone": 0009991122, "email": "gaurav.yourfriend@gmail.com" }
 *
 */
@Provider
public class JsonParseExceptionMapper implements ExceptionMapper<JsonParseException> {

    @Inject
    private Logger logger;

    /*
     * (non-Javadoc)
     * 
     * @see javax.ws.rs.ext.ExceptionMapper#toResponse(java.lang.Throwable)
     */
    @Override
    public Response toResponse(JsonParseException exception) {

        DTOIpSecConfigValidityStatus dtoIpSecConfigValidityStatus = new DTOIpSecConfigValidityStatus();

        /*
         * Exception example
         * 
         * exception.getMessage()
         * 
         * This is an invalid json. The request can not be parsed, error: Unexpected character ('d' (code 100)): was expecting double-quote to start field name at [Source:
         * org.jboss.resteasy.core.interception.MessageBodyReaderContextImpl$InputStreamWrapper@1e39aa6f; line: 63, column: 9]
         * 
         * Regex: \('(\S+)'
         */

        final String errorMessage = "This is an invalid json. The request can not be parsed, error: " + exception.getMessage();
        logger.error(errorMessage);

        List<IpSecConfigValidityStatus> result = new ArrayList<IpSecConfigValidityStatus>();
        final IpSecConfigValidityStatus e = new IpSecConfigValidityStatus();
        e.setName("*");
        List<IpSecConfigInvalidElement> ipsecConfigInvalidElements = new ArrayList<IpSecConfigInvalidElement>();
        //TODO Insert invalid element here
        final Pattern unexpectedCharacterPattern = Pattern.compile("\\('(\\S+)'");
        final Matcher matcher = unexpectedCharacterPattern.matcher(exception.getMessage());
        String elementName = "*";
        if (matcher.find()) {
            elementName = matcher.group(1);
        } else {
            logger.warn("Not able to parse exception and pick up data!");
        }

        final String errorMessages = "Unexpected character";
        final IpSecConfigInvalidElement ipsecInvalidElement = new IpSecConfigInvalidElement(elementName, errorMessages, IpSecValidityErrorCode.BAD_DATA_FORMAT);
        ipsecConfigInvalidElements.add(ipsecInvalidElement);
        e.setIpsecConfigInvalidElements(ipsecConfigInvalidElements);
        result.add(e);
        dtoIpSecConfigValidityStatus.setIpSecConfigValidityStatus(result);

        return Response.status(Response.Status.BAD_REQUEST).entity(dtoIpSecConfigValidityStatus).type(MediaType.APPLICATION_JSON).build();
    }

}

/*
 * 
 * ANOTHER EXCEPTION HANDLER Invalid attribute type in request
 * 
 * 
 * @Provider public class JsonMappingExceptionMapper implements ExceptionMapper<JsonMappingException >{
 * 
 * @Override public Response toResponse(JsonMappingException exception) { return Response .status(Response.Status.BAD_REQUEST) .entity(
 * "This is an invalid request. At least one field format is not readable by the system.") .type( MediaType.TEXT_PLAIN) .build(); }
 * 
 * }
 */