/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2023
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.ssh;

import java.util.HashMap;
import java.util.Map;

public class SSHKeyWfsConfigurationDto {

    String nodeFdn;

    String workflowName;

    Map<String, Object> workflowParams = new HashMap<>();

    public void setNodeFdn(String nodeFdn) {
        this.nodeFdn = nodeFdn;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    public Map<String, Object> getWorkflowParams() {
        return workflowParams;
    }

    @Override
    public String toString() {
        return "SSHKeyWfsConfigurationDto{" +
                "nodeFdn='" + nodeFdn + '\'' +
                ", workflowName='" + workflowName + '\'' +
                ", workflowParams=" + workflowParams +
                '}';
    }
}
