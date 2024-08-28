/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2022
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs;

import java.io.UnsupportedEncodingException;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.iscf.IscfResponse;
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse;
import com.ericsson.oss.services.nscs.api.iscf.dto.IscfSecDataDto;
import com.ericsson.oss.services.nscs.api.iscf.dto.IscfXmlComboDto;
import com.ericsson.oss.services.nscs.api.iscf.dto.IscfXmlIpsecDto;
import com.ericsson.oss.services.nscs.api.iscf.dto.IscfXmlOamDto;
import com.ericsson.oss.services.security.nscs.iscf.IscfManager;
import com.ericsson.oss.services.security.nscs.util.NscsRestResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * REST interface for NSCS ISCF Service.
 */
@Path("iscf/")
public class IscfRestResource {

    @Inject
    private Logger logger;

    @Inject
    private IscfManager iscfManager;

    /**
     * Generate ISCF XML for OAM (CPP nodes).
     * 
     * @param dto
     *            DTO containing parameters.
     * @return the ISCF response.
     * @throws UnsupportedEncodingException
     */
    @POST
    @Path("oam")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response generateXmlOam(final IscfXmlOamDto dto) throws UnsupportedEncodingException {

        logger.debug("POST iscf xml oam: {}", dto);
        final IscfResponse iscfResponse = iscfManager.generateXmlOam(dto);
        final String result = new String(iscfResponse.getIscfContent(), "UTF-8");
        logger.debug(result);

        return Response.status(Response.Status.OK).entity(result).build();
    }

    /**
     * Generate ISCF XML for IPSEC (CPP nodes).
     * 
     * @param dto
     *            DTO containing parameters.
     * @return the ISCF response.
     * @throws UnsupportedEncodingException
     */
    @POST
    @Path("ipsec")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response generateXmlIpsec(final IscfXmlIpsecDto dto) throws UnsupportedEncodingException {

        logger.debug("POST iscf xml ipsec: {}", dto);
        final IscfResponse iscfResponse = iscfManager.generateXmlIpsec(dto);
        final String result = new String(iscfResponse.getIscfContent(), "UTF-8");
        logger.debug(result);

        return Response.status(Response.Status.OK).entity(result).build();
    }

    /**
     * Generate ISCF XML for both OAM and IPSEC (CPP nodes).
     * 
     * @param dto
     *            DTO containing parameters.
     * @return the ISCF response.
     * @throws UnsupportedEncodingException
     */
    @POST
    @Path("combined")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response generateXmlCombined(final IscfXmlComboDto dto) throws UnsupportedEncodingException {

        logger.debug("POST iscf xml combined: {}", dto);
        final IscfResponse iscfResponse = iscfManager.generateXmlCombo(dto);
        final String result = new String(iscfResponse.getIscfContent(), "UTF-8");
        logger.debug(result);

        return Response.status(Response.Status.OK).entity(result).build();
    }

    /**
     * Cancel End Entity for OAM (if given node is nodename-oam) or IPSEC (if given node is nodename-ipsec) or both OAM and IPSEC (if given node is
     * nodename or node FDN).
     * 
     * The related certificates are revoked too.
     * 
     * Any possible associated SMRS account is removed too.
     * 
     * @param node
     *            the node name or FDN or the OAM/IPSEC entity name.
     * @return the result of the operation.
     * @throws JsonProcessingException
     */
    @DELETE
    @Path("{node}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteIscf(@PathParam("node") final String node) throws JsonProcessingException {

        final String inputParams = String.format("DELETE iscf node: %s", node);
        logger.debug(inputParams);
        final String nscsResult = iscfManager.cancel(node);
        final NscsRestResult nscsRestResult = new NscsRestResult(inputParams, nscsResult);
        final ObjectMapper objectMapper = new ObjectMapper();
        final String result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(nscsRestResult);
        logger.debug(result);

        return Response.status(Response.Status.OK).entity(result).build();
    }

    /**
     * Generate ISCF Security Data for OAM (ECIM nodes).
     * 
     * @param dto
     *            DTO containing parameters.
     * @return the ISCF Security Data.
     * @throws JsonProcessingException
     */
    @POST
    @Path("secdata/oam")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateSecurityDataOam(final IscfSecDataDto dto) throws JsonProcessingException {

        logger.debug("POST iscf security data oam: {}", dto);
        final SecurityDataResponse secDataResponse = iscfManager.generateSecurityDataOam(dto);
        final ObjectMapper mapper = new ObjectMapper();
        final String result = mapper.writeValueAsString(secDataResponse);
        logger.debug(result);

        return Response.status(Response.Status.OK).entity(result).build();
    }

    /**
     * Generate ISCF Security Data for IPSEC (ECIM nodes).
     * 
     * @param dto
     *            DTO containing parameters.
     * @return the ISCF Security Data.
     * @throws JsonProcessingException
     */
    @POST
    @Path("secdata/ipsec")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateSecurityDataIpsec(final IscfSecDataDto dto) throws JsonProcessingException {

        logger.debug("POST iscf security data ipsec: {}", dto);
        final SecurityDataResponse secDataResponse = iscfManager.generateSecurityDataIpsec(dto);
        final ObjectMapper mapper = new ObjectMapper();
        final String result = mapper.writeValueAsString(secDataResponse);
        logger.debug(result);

        return Response.status(Response.Status.OK).entity(result).build();
    }

    /**
     * Generate ISCF Security Data for both OAM and IPSEC (ECIM nodes).
     * 
     * @param dto
     *            DTO containing parameters.
     * @return the ISCF Security Data.
     * @throws JsonProcessingException
     */
    @POST
    @Path("secdata/combined")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateSecurityDataCombined(final IscfSecDataDto dto) throws JsonProcessingException {

        logger.debug("POST iscf security data combined: {}", dto);
        final SecurityDataResponse secDataResponse = iscfManager.generateSecurityDataCombo(dto);
        final ObjectMapper mapper = new ObjectMapper();
        final String result = mapper.writeValueAsString(secDataResponse);
        logger.debug(result);

        return Response.status(Response.Status.OK).entity(result).build();
    }
}
