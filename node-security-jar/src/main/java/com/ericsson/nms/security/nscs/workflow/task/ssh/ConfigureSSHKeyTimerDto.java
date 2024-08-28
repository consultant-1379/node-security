/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2024
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.workflow.task.ssh;

import java.io.Serializable;
public class ConfigureSSHKeyTimerDto implements Serializable {
    private static final long serialVersionUID = 1;
    private final String nodeName;
    private final String networkElementSecurityFdn;
    private final String sshkeyOperation;

    public ConfigureSSHKeyTimerDto(String nodeName, String networkElementSecurityFdn, String sshkeyOperation) {
        this.nodeName = nodeName;
        this.networkElementSecurityFdn = networkElementSecurityFdn;
        this.sshkeyOperation = sshkeyOperation;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getNetworkElementSecurityFdn() {
        return networkElementSecurityFdn;
    }
    public String getSshkeyOperation() {
        return sshkeyOperation;
    }
}
