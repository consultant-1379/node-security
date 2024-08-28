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
package com.ericsson.oss.services.security.nscs.pib;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.pib.configuration.ConfigurationListener;
import com.ericsson.nms.security.nscs.pib.configuration.WorkflowConfigurationListener;

/**
 * REST used by Arquillian tests to verify NSCS PIB parameters.
 */
@Path("pib/")
public class PIBModelRestResource {

    @Inject
    private Logger logger;

    @Inject
    private WorkflowConfigurationListener workflowConfigurationListener;

    @Inject
    private ConfigurationListener configurationListener;

    private static String NECERTAUTORENEWALTIMER = "neCertAutoRenewalTimer";
    private static String NECERTAUTORENEWALENABLED = "neCertAutoRenewalEnabled";
    private static String NECERTAUTORENEWALMAX = "neCertAutoRenewalMax";
    private static String WFCONGESTIONTHRESHOLD = "wfCongestionThreshold";

    @GET
    @Path("confparam/{paramName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJobList(@PathParam("paramName") String paramName) {
        String result = "";
        logger.info("Invoking REST pib/confparam/" + paramName);

        boolean isBadRequest = false;

        if (NECERTAUTORENEWALTIMER.equalsIgnoreCase(paramName)) {
            result = paramName + ": " + String.valueOf(configurationListener.getPibNeCertAutoRenewalTimer());
        } else if (NECERTAUTORENEWALENABLED.equalsIgnoreCase(paramName)) {
            result = paramName + ": " + String.valueOf(configurationListener.getPibNeCertAutoRenewalEnabled());
        } else if (NECERTAUTORENEWALMAX.equalsIgnoreCase(paramName)) {
            result = paramName + ": " + String.valueOf(configurationListener.getPibNeCertAutoRenewalMax());
        } else if (WFCONGESTIONTHRESHOLD.equalsIgnoreCase(paramName)) {
            result = paramName + ": " + String.valueOf(workflowConfigurationListener.getPibWfCongestionThreshold());
        } else {
            //bad request
            result = "Bad request, invalid parameter: " + paramName;
            isBadRequest = true;
        }
        logger.info("Invoking REST pib/confparam/" + paramName + " with result: " + result);
        if (!isBadRequest) {
            return Response.status(Response.Status.OK).entity(result).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity(result).build();
        }
    }
}
