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
package com.ericsson.nms.security.nscs.integration.jee.test.ssh;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.ericsson.oss.itpf.sdk.eventbus.model.EventSender;
import com.ericsson.oss.itpf.sdk.eventbus.model.annotation.Modeled;
import com.ericsson.oss.mediation.sec.model.*;

@ApplicationScoped
public class SSHResultHandler {

    public static final String TEST_COMMAND_ERROR_TYPE = "I/O";
    public static final String TEST_COMMAND_ERROR_MESSAGE = "Host not found.";
    public static final long TEST_JOB_START_TIME = 10056982;
    public static final long TEST_JOB_END_TIME = 20005986;

    @Inject
    @Modeled
    private EventSender<SSHCommandSuccess> successSender;

    @Inject
    @Modeled
    private EventSender<SSHCommandFailure> failedSender;

    private boolean shouldSendFail = false;

    public void processSSHCommandResult(@Observes @Modeled final SSHCommandJob sshCommandJob) {
        if (shouldSendFail) {
            SSHCommandFailure sshCommandFailure = new SSHCommandFailure();
            sshCommandFailure.setFdn(sshCommandJob.getNodeAddress());
            sshCommandFailure.setErrorType(TEST_COMMAND_ERROR_TYPE);
            sshCommandFailure.setErrorMessage(TEST_COMMAND_ERROR_MESSAGE);
            sshCommandFailure.setJobId(sshCommandJob.getJobId());
            sshCommandFailure.setCommand(sshCommandJob.getCommandToExecute());
            failedSender.send(sshCommandFailure);
        } else {
            SSHCommandSuccess sshCommandSuccess = new SSHCommandSuccess();
            sshCommandSuccess.setFdn(sshCommandJob.getNodeAddress());
            sshCommandSuccess.setJobId(sshCommandJob.getJobId());
            sshCommandSuccess.setCommand(sshCommandJob.getCommandToExecute());
            sshCommandSuccess.setJobStartTime(TEST_JOB_START_TIME);
            sshCommandSuccess.setJobEndTime(TEST_JOB_END_TIME);
            successSender.send(sshCommandSuccess);
        }
    }

    public void setShouldSendFail(final boolean shouldSendFail) {
        this.shouldSendFail = shouldSendFail;
    }
}
