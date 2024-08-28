/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2018
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.instrumentation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.ericsson.nms.security.nscs.api.instrumentation.NscsInstrumentationService;
import com.ericsson.nms.security.nscs.api.instrumentation.NscsMonitoredEntityTypes;
import com.ericsson.oss.services.security.nscs.util.NscsRestResult;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("instrumentation/")
public class NscsInstrumentationRestResource {

    @Inject
    private NscsInstrumentationService nscsInstrumentationService;

    @GET
    @Path("getStats")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNscsStats() {
        final ObjectMapper mapper = new ObjectMapper();
        String result = "";
        final NscsRestResult restResult = new NscsRestResult("Get NSCS statistics", null);
        try {
            final List<NscsRestResult> stats = new ArrayList<>();
            stats.add(new NscsRestResult("Num Of Successful Workflows", nscsInstrumentationService.getNumOfSuccessfulWorkflows()));
            stats.add(new NscsRestResult("Num Of Failed Workflows", nscsInstrumentationService.getNumOfFailedWorkflows()));
            stats.add(new NscsRestResult("Num Of Errored Workflows", nscsInstrumentationService.getNumOfErroredWorkflows()));
            stats.add(new NscsRestResult("Num Of Timed-Out Workflows", nscsInstrumentationService.getNumOfTimedOutWorkflows()));
            stats.add(new NscsRestResult("Num Of Running Workflows", nscsInstrumentationService.getNumOfRunningWorkflows()));
            stats.add(new NscsRestResult("Num Of Pending Workflows", nscsInstrumentationService.getNumOfPendingWorkflows()));
            for (final NscsMonitoredEntityTypes monitoredEntityType : NscsMonitoredEntityTypes.values()) {
                stats.add(new NscsRestResult("Num Of Total " + monitoredEntityType.name(),
                        nscsInstrumentationService.getNumOfInstances(monitoredEntityType)));
                stats.add(new NscsRestResult("Num Of Successful " + monitoredEntityType.name(),
                        nscsInstrumentationService.getNumOfSuccessfulInstances(monitoredEntityType)));
                stats.add(new NscsRestResult("Average Duration Of Successful " + monitoredEntityType.name(),
                        nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(monitoredEntityType)));
                stats.add(new NscsRestResult("Num Of Unsuccessful " + monitoredEntityType.name(),
                        nscsInstrumentationService.getNumOfUnsuccessfulInstances(monitoredEntityType)));
                stats.add(new NscsRestResult("Num Of Failed " + monitoredEntityType.name(),
                        nscsInstrumentationService.getNumOfFailedInstances(monitoredEntityType)));
                stats.add(new NscsRestResult("Num Of Errored " + monitoredEntityType.name(),
                        nscsInstrumentationService.getNumOfErroredInstances(monitoredEntityType)));
                stats.add(new NscsRestResult("Num Of Timed-Out " + monitoredEntityType.name(),
                        nscsInstrumentationService.getNumOfTimedOutInstances(monitoredEntityType)));
            }
            restResult.setResponse(stats);
            result = mapper.writeValueAsString(restResult);

        } catch (final Exception e) {
            restResult.setResponse("Error: [" + e.getClass().getCanonicalName() + "] " + e.getMessage());
            try {
                result = mapper.writeValueAsString(restResult);
            } catch (final IOException e1) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e1.getMessage()).build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
        }
        return Response.status(Response.Status.OK).entity(result).build();
    }

}
