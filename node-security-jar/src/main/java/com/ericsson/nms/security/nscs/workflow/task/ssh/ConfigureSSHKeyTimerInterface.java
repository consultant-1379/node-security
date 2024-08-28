package com.ericsson.nms.security.nscs.workflow.task.ssh;

import com.ericsson.oss.itpf.sdk.core.annotation.EService;

import javax.ejb.Local;

@EService
@Local
public interface ConfigureSSHKeyTimerInterface {

    void startSSHKeyTimer(final long duration, final ConfigureSSHKeyTimerDto configureSSHKeyTimer);

}
