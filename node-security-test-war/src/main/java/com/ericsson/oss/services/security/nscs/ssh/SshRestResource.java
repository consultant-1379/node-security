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
package com.ericsson.oss.services.security.nscs.ssh;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;

import com.ericsson.oss.itpf.sdk.eventbus.model.EventSender;
import com.ericsson.oss.itpf.sdk.eventbus.model.annotation.Modeled;
import com.ericsson.oss.mediation.sec.model.SSHCommandJob;
import com.ericsson.oss.services.security.nscs.util.EServiceHolder;

@Path("ssh/")
public class SshRestResource {

    @Inject
    EServiceHolder holder;

    @Inject
    @Modeled
    private EventSender<SSHCommandJob> jobSender;

    @Inject
    private Logger logger;

    /**
     * 
     * 
     * Posting the FDN and the ssh command to execute to this URL http://localhost:8080/node-security/fdn/command will produce text response as of the
     * result of the command
     * 
     * @param fdn
     *            - the FDN of the node to connect to.
     * @param command
     * @return
     */
    @GET
    @Path("{fdn}/{command}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response executeSSHcommand(@PathParam("fdn") final String fdn, @PathParam("command") final String command) {
        logger.info("Calling:   executeSSHcommand()");

        final String message = "The job request sent to the event bus conatins the parameters:" + "\nfdn = " + fdn + "\ncommandToExecute = "
                + command;

        final SSHCommandJob sshCommandJob = new SSHCommandJob();
        sshCommandJob.setNodeAddress(fdn);
        sshCommandJob.setCommandToExecute(command);
        sshCommandJob.setJobId(fdn + command);

        jobSender.send(sshCommandJob);

        return Response.status(Response.Status.OK).entity(message).build();
    }
}
