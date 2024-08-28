/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2026
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.ejb.timer;

import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.workflow.task.ssh.ConfigureSSHKeyTimerDto;
import com.ericsson.nms.security.nscs.workflow.task.ssh.ConfigureSSHKeyTimerInterface;
import com.ericsson.oss.itpf.sdk.eventbus.model.EventSender;
import com.ericsson.oss.itpf.sdk.eventbus.model.annotation.Modeled;
import com.ericsson.oss.mediation.sec.model.SSHCommandResult;
import com.ericsson.oss.mediation.sec.model.SSHCommandSuccess;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.inject.Inject;
import java.util.UUID;

@Stateless
public class ConfigureSSHKeyTimer implements ConfigureSSHKeyTimerInterface {

    private static final String OUTPUT_SUCCESS = "_OK";

    @Resource
    private TimerService timerService;

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    @Modeled
    private EventSender<SSHCommandResult> sshKeyResultSuccessJob;

    @Timeout
    public void handleSSHKeyTimeout(final Timer timer) {
        final ConfigureSSHKeyTimerDto configureSSHKeyTimer = (ConfigureSSHKeyTimerDto) timer.getInfo();

        String nodeName = configureSSHKeyTimer.getNodeName();
        String sshCommandToExecute = configureSSHKeyTimer.getSshkeyOperation();
        String nesFdn = configureSSHKeyTimer.getNetworkElementSecurityFdn();
        nscsLogger.info("Woke up from timeout. Timed activity context [{}], node is: [{}], ssh command is:[{}]", configureSSHKeyTimer,
                nodeName, sshCommandToExecute);
        try {
            sendSuccessResult(nodeName, nesFdn, sshCommandToExecute);
        } catch (Exception e) {
            nscsLogger.error("Error:[{}] in Sending sshCommandJob:[{}] for node:[{}]", e.getMessage(), sshCommandToExecute, nodeName);
            nscsLogger.error("Exception:[{}]",e);
        }
    }

    @Override
    public void startSSHKeyTimer(final long duration, final ConfigureSSHKeyTimerDto configureSSHKeyTimer) {
        nscsLogger.info("Configuring timer with timeout value of [{}] milliseconds for node: [{}], ssh command is:[{}]",
                duration, configureSSHKeyTimer.getNodeName(), configureSSHKeyTimer.getSshkeyOperation());
        final TimerConfig timerConfig = new TimerConfig();
        timerConfig.setPersistent(false);
        timerConfig.setInfo(configureSSHKeyTimer);
        timerService.createSingleActionTimer(duration, timerConfig);
    }

    private void sendSuccessResult(final String nodeName,
                                   final String networkElementSecurityFdn,
                                   final String sshkeyOperation) {
        SSHCommandSuccess resultEvent = new SSHCommandSuccess(
                String.format("%s-%s", nodeName, UUID.randomUUID()),
                sshkeyOperation + OUTPUT_SUCCESS,
                0,
                0,
                networkElementSecurityFdn);
        resultEvent.setCommand(sshkeyOperation);
        sshKeyResultSuccessJob.send(resultEvent);
    }
}
