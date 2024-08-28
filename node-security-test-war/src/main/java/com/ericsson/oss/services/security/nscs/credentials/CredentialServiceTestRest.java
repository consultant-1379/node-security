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
package com.ericsson.oss.services.security.nscs.credentials;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.credentials.CredentialAttributes;
import com.ericsson.nms.security.nscs.api.credentials.UserCredentials;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.oss.services.security.nscs.util.EServiceHolder;

@Path("ap/")
public class CredentialServiceTestRest {

    @Inject
    EServiceHolder holder;
    @Inject
    private Logger logger;

    @GET
    @Path("credcreate")
    public Response credentialsCreate(@QueryParam("normalUserName") final String normalUserName,
                                      @QueryParam("normalUserPwd") final String normalUserPwd,
                                      @QueryParam("secureUserName") final String secureUserName,
                                      @QueryParam("secureUserPwd") final String secureUserPwd,
                                      @QueryParam("rootUserName") final String rootUserName,
                                      @QueryParam("rootUserPwd") final String rootUserPwd,
                                      @QueryParam("node") final String nodeName) {
        logger.info(
                "CredentialServiceRestResource invoked to create credentials. normalUserName: {}, normalUserPwd: {}, secureUserName: {}, "
                        + "secureUserPwd: {}, rootUserName: {}, rootUserPwd: {}, Node name: {}",
                normalUserName, normalUserPwd, secureUserName, secureUserPwd, rootUserName, rootUserPwd, nodeName);
        try {
            /*
             * CredentialAttributesBuilder CAB = new CredentialAttributesBuilder(); CredentialAttributes credAttributes = CAB.addRoot(rootUserName,
             * rootUserPwd).addSecure(secureUserName, secureUserPwd) .addUnsecure(normalUserName, normalUserPwd) .build();
             */
            CredentialAttributes credAttributes = new CredentialAttributes(new UserCredentials(rootUserName, rootUserPwd),
                    new UserCredentials(normalUserName, normalUserPwd), new UserCredentials(secureUserName, secureUserPwd));
            holder.getCredentialService().createNodeCredentials(credAttributes, nodeName);

            return Response.ok().entity("CredentialServiceRestResource to create credentials successfully executed").build();

        } catch (final Exception e) {
            logger.warn("Failed to execute credentials create via RestResource");
            return Response.status(Response.Status.BAD_REQUEST).entity("Failed to create credentials via RestResource: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("credbsccreate")
    public Response credentialsBscCreate(@QueryParam("normalUserName") final String normalUserName,
                                         @QueryParam("normalUserPwd") final String normalUserPwd,
                                         @QueryParam("secureUserName") final String secureUserName,
                                         @QueryParam("secureUserPwd") final String secureUserPwd,
                                         @QueryParam("nwieaSecureUserName") final String nwieaSecureUserName,
                                         @QueryParam("nwieaSecureUserPwd") final String nwieaSecureUserPwd,
                                         @QueryParam("nwiebSecureUserName") final String nwiebSecureUserName,
                                         @QueryParam("nwiebSecureUserPwd") final String nwiebSecureUserPwd,
                                         @QueryParam("rootUserName") final String rootUserName,
                                         @QueryParam("rootUserPwd") final String rootUserPwd,
                                         @QueryParam("node") final String nodeName) {
        logger.info(
                "CredentialServiceRestResource invoked to create BSC Node credentials. normalUserName: {}, normalUserPwd: {}, secureUserName: {}, "
                        + "secureUserPwd: {}, rootUserName: {}, rootUserPwd: {}, nwieaSecureUserName: {}, nwieaSecureUserPwd: {}, "
                        + "nwiebSecureUserName: {}, nwiebSecureUserPwd: {},  Node name: {}",
                normalUserName, normalUserPwd, secureUserName, secureUserPwd, rootUserName, rootUserPwd, nwieaSecureUserName, nwieaSecureUserPwd,
                nwiebSecureUserName, nwiebSecureUserPwd, nodeName);
        try {
            CredentialAttributes credAttributes = new CredentialAttributes(new UserCredentials(rootUserName, rootUserPwd),
                    new UserCredentials(normalUserName, normalUserPwd), new UserCredentials(secureUserName, secureUserPwd));
            holder.getCredentialService().createNodeCredentials(credAttributes, nodeName);

            return Response.ok().entity("CredentialServiceRestResource to create BSC Node Credentials successfully executed").build();

        } catch (final Exception e) {
            logger.warn("Failed to execute credentials create via RestResource");
            return Response.status(Response.Status.BAD_REQUEST).entity("Failed to create  BSC Node credentials via RestResource: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("setem")
    public Response configEnrollment(@QueryParam("enrollmentMode") final EnrollmentMode enrollmentMode, @QueryParam("node") final String nodeName) {
        logger.info("CredentialServiceRestResource invoked to set enrollment. enrollmentMode: {}, Node name: {}", enrollmentMode, nodeName);
        try {
            holder.getCredentialService().configureEnrollmentMode(enrollmentMode, nodeName);
            return Response.ok().entity("CredentialServiceRestResource to set enrollment mode successfully executed.").build();
        } catch (final Exception e) {
            logger.warn("Failed to execute credentials create via RestResource");
            return Response.status(Response.Status.BAD_REQUEST).entity("Failed to set enrollment mode via RestResource: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("ldapUser")
    public void getLadpUser() {
        holder.getLdapUser();
    }
}
