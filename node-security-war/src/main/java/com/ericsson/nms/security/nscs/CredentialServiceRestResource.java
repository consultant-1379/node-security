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
package com.ericsson.nms.security.nscs;

import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.credentials.SnmpV3Attributes;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.enums.SnmpSecurityLevel;
import com.ericsson.oss.services.nscs.api.credentials.dto.NodeCredentialsDto;
import com.ericsson.oss.services.security.nscs.credentials.CredentialManager;
import com.ericsson.oss.services.security.nscs.util.NscsRestResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * REST interface for NSCS Credential Service.
 */
@Path("credentials/")
public class CredentialServiceRestResource {

    @Inject
    private Logger logger;

    @Inject
    private CredentialManager credentialManager;

    /**
     * Creates or updates via Credential Service the node credentials for a given node.
     * 
     * @param dto
     *            DTO containing the node credentials.
     * 
     * @return the result of the operation.
     * @throws JsonProcessingException
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response credentialsCreateOrUpdate(final NodeCredentialsDto dto) throws JsonProcessingException {

        final String inputParams = String.format("POST credentials create or update: %s", dto.toString());
        logger.debug(inputParams);
        final String nscsResult = credentialManager.createOrUpdateNodeCredentials(dto);
        final NscsRestResult nscsRestResult = new NscsRestResult(inputParams, nscsResult);
        final ObjectMapper objectMapper = new ObjectMapper();
        final String result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(nscsRestResult);
        logger.debug(result);

        return Response.status(Response.Status.OK).entity(result).build();
    }

    /**
     * Configure via Credential Service the enrollment mode for a given node.
     * 
     * @param node
     *            the node name or FDN.
     * @param enrollmentMode
     *            the enrollment mode.
     * 
     * @return the result of the operation.
     * @throws JsonProcessingException
     */
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{node}/{enrollmentmode}")
    public Response configEnrollmentMode(@PathParam("node") final String node, @PathParam("enrollmentmode") final EnrollmentMode enrollmentMode)
            throws JsonProcessingException {

        final String inputParams = String.format("PUT configure enrollment mode: %s for node %s", enrollmentMode, node);
        logger.debug(inputParams);
        final String nscsResult = credentialManager.configureEnrollmentMode(enrollmentMode, node);
        final NscsRestResult nscsRestResult = new NscsRestResult(inputParams, nscsResult);
        final ObjectMapper objectMapper = new ObjectMapper();
        final String result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(nscsRestResult);
        logger.debug(result);

        return Response.status(Response.Status.OK).entity(result).build();
    }

    /**
     * Configures via Credential Service the SNMPv3 configuration for the given <nodeList> (nodes separated with "&") path parameter.
     * 
     * @param nodeList
     *            list of the nodes as a string separated with "&"
     * 
     *            A single node can be specified by node name or by FDN (both mirrored and normalized). So valid values are:
     * 
     *            netsim_LTE02ERBS00002
     * 
     *            SubNetwork=Europe,SubNetwork=Ireland,SubNetwork=ERBS-SUBNW-1,MeContext=netsim_LTE02ERBS00002
     * 
     *            NetworkElement=netsim_LTE02ERBS00002
     * @param snmpSecurityLevel
     *            The SNMP security level.
     * @param snmpV3Attributes
     *            The SNMPv3 parameters.
     * 
     * @return the result of the operation.
     * @throws JsonProcessingException
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("snmpv3/{nodelist}/{snmpsecuritylevel}")
    public Response configureSnmpV3(@PathParam("nodelist") final String nodeList,
            @PathParam("snmpsecuritylevel") final SnmpSecurityLevel snmpSecurityLevel, final SnmpV3Attributes snmpV3Attributes)
            throws JsonProcessingException {

        final String inputParams = String.format("PUT configure snmpV3: %s for nodelist %s", snmpSecurityLevel, nodeList);
        logger.debug(inputParams);
        final String nscsResult = credentialManager.configureSnmpV3(snmpSecurityLevel, snmpV3Attributes, nodeList);
        final NscsRestResult nscsRestResult = new NscsRestResult(inputParams, nscsResult);
        final ObjectMapper objectMapper = new ObjectMapper();
        final String result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(nscsRestResult);
        logger.debug(result);

        return Response.status(Response.Status.OK).entity(result).build();
    }

    /**
     * Gets via Credential Service the SNMPv3 configuration for the given <nodeList> (nodes separated with "&") path parameter.
     * 
     * @param nodeList
     *            list of the nodes as a string separated with "&"
     * 
     *            A single node can be specified by node name or by FDN (both mirrored and normalized). So valid values are:
     * 
     *            netsim_LTE02ERBS00002
     * 
     *            SubNetwork=Europe,SubNetwork=Ireland,SubNetwork=ERBS-SUBNW-1,MeContext=netsim_LTE02ERBS00002
     * 
     *            NetworkElement=netsim_LTE02ERBS00002
     * @param plainText
     *            If this query parameter is specified plain text is required.
     * @return the SNMPv3 configuration parameters.
     * @throws JsonProcessingException
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("snmpv3/{nodelist}")
    public Response getSnmpV3Configuration(@PathParam("nodelist") final String nodeList, @QueryParam("plaintext") final String plainText)
            throws JsonProcessingException {

        logger.debug("GET snmpV3 configuration for nodelist: {} plainText: {}", nodeList, plainText);
        final boolean isPlainText = plainText != null;
        final Map<String, SnmpV3Attributes> snmpV3Attributes = credentialManager.getSnmpV3Configuration(nodeList, isPlainText);
        final ObjectMapper mapper = new ObjectMapper();
        final String result = mapper.writeValueAsString(snmpV3Attributes);
        logger.debug(result);

        return Response.status(Response.Status.OK).entity(result).build();
    }
}
