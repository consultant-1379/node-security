/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2021
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.classloading;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 * Rest interface of the node-security EJB related to class loading tests. This is ONLY for testing purposes.
 */
@Path("/classloading")
public class NscsClassLoadingRest {

    @Inject
    private NscsClassLoading nscsClassLoading;

    @GET
    @Path("/packver/{fullclassname}")
    public Response getPacketVersion(@PathParam("fullclassname") String fullclassname) {
        final String packetVersion = nscsClassLoading.getPackageVersion(fullclassname);
        return Response.status(Response.Status.OK).entity(packetVersion).build();
    }

}
