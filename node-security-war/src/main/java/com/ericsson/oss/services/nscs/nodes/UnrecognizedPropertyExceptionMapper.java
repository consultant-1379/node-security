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
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

/**
 * @author elucbot
 * 
 *         Invalid field name in JSON
 * 
 *         { "nameInvalid": "Gaurav Varma", "phone": 0009991122, "email": "gaurav.yourfriend@gmail.com" }
 *
 */
@Provider
public class UnrecognizedPropertyExceptionMapper implements ExceptionMapper<UnrecognizedPropertyException> {

    @Inject
    private Logger logger;

    @Override
    public Response toResponse(UnrecognizedPropertyException exception) {

        DTOIpSecConfigValidityStatus dtoIpSecConfigValidityStatus = new DTOIpSecConfigValidityStatus();

        /*
         * Exception example
         * 
         * exception.getUnrecognizedPropertyName()
         * 
         * 
         * This is an invalid request. The field 22222nodeFdn is not recognized by the system.
         */

        String invalidAttribute = exception.getPropertyName();
        String invalidAttributeMessage = "The field is not recognized by the system.";
        String errorMessage = "This is an invalid request. Attribute [" + invalidAttribute + "] " + invalidAttributeMessage + " Exception: " + exception.getMessage();
        logger.error(errorMessage);

        List<IpSecConfigValidityStatus> result = new ArrayList<IpSecConfigValidityStatus>();
        IpSecConfigValidityStatus e = new IpSecConfigValidityStatus();
        e.setName("*");
        List<IpSecConfigInvalidElement> ipsecConfigInvalidElements = new ArrayList<IpSecConfigInvalidElement>();
        //TODO Insert invalid element here
        String elementName = invalidAttribute;

        IpSecConfigInvalidElement ipsecInvalidElement = new IpSecConfigInvalidElement(elementName, invalidAttributeMessage, IpSecValidityErrorCode.BAD_DATA_FORMAT);
        ipsecConfigInvalidElements.add(ipsecInvalidElement);
        e.setIpsecConfigInvalidElements(ipsecConfigInvalidElements);
        result.add(e);
        dtoIpSecConfigValidityStatus.setIpSecConfigValidityStatus(result);

        return Response.status(Response.Status.BAD_REQUEST).entity(dtoIpSecConfigValidityStatus).type(MediaType.APPLICATION_JSON).build();

    }
}
