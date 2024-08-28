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
package com.ericsson.oss.services.security.nscs.smrs;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;

import com.ericsson.oss.itpf.smrs.SmrsAccount;
import com.ericsson.oss.itpf.smrs.SmrsAddressRequest;
import com.ericsson.oss.services.security.nscs.util.EServiceHolder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * REST used by Arquillian tests to verify SMRS service mock.
 */
@Path("smrs/")
public class SmrsRestResource {

    @Inject
    private Logger logger;

    @Inject
    private EServiceHolder holder;

    @POST
    @Path("account")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNodeSpecificAccount(final SmrsAddressRequest addressRequest) throws JsonProcessingException {

        logger.info("POST account: accountType {} neType {} neName {}", addressRequest.getAccountType(), addressRequest.getNeType(),
                addressRequest.getNeName());

        final SmrsAccount smrsAccount = holder.getSmrsService().getNodeSpecificAccount(addressRequest.getAccountType(), addressRequest.getNeType(),
                addressRequest.getNeName());
        final ObjectMapper objectMapper = new ObjectMapper();
        final String result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(smrsAccount);

        return Response.status(Response.Status.OK).entity(result).build();
    }

    @POST
    @Path("address")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFileServerAddress(final SmrsAddressRequest addressRequest) throws JsonProcessingException {

        logger.info("POST address: accountType {} neType {} neName {}", addressRequest.getAccountType(), addressRequest.getNeType(),
                addressRequest.getNeName());

        final String fileServerAddress = holder.getSmrsService().getFileServerAddress(addressRequest);

        return Response.status(Response.Status.OK).entity(fileServerAddress).build();
    }

    @POST
    @Path("delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAccount(final SmrsAccount account) throws JsonProcessingException {

        logger.info("POST delete: accountType {} neType {} neName {}", account.getAccountType(), account.getNeType(), account.getNeName());

        final boolean deleteResult = holder.getSmrsService().deleteSmrsAccount(account);
        final ObjectMapper objectMapper = new ObjectMapper();
        final String result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(deleteResult);

        return Response.status(Response.Status.OK).entity(result).build();
    }
}
