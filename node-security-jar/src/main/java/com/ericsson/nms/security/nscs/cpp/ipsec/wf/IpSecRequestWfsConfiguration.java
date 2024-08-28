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
package com.ericsson.nms.security.nscs.cpp.ipsec.wf;

import java.util.Map;

public class IpSecRequestWfsConfiguration {
    String nodeFdn;

    String workflowName;
    Map<String, Object> workflowParams;

    public String getNodeFdn() {
        return nodeFdn;
    }

    public void setNodeFdn(String nodeFdn) {
        this.nodeFdn = nodeFdn;
    }

    public Map<String, Object> getWorkflowParams() {
        return workflowParams;
    }

    public void setWorkflowParams(Map<String, Object> workflowParams) {
        this.workflowParams = workflowParams;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

}
