/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;

/**
 * <p>
 * Workflow task representing a request to deactivate the Ip Security on a node
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.CPP_DEACTIVATE_IPSEC
 * </p>
 *
 * Created by esneani.
 */
public class DeactivateIpSecTask extends WorkflowQueryTask {

    private static final long serialVersionUID = 3699607376689351794L;

    public static final String REMOVE_CERT_PARAM_KEY = "REMOVE_CERT";
    public static final String REMOVE_TRUST_PARAM_KEY = "REMOVE_TRUST";
    public static final String NODES_XML_PARAM_KEY = "NODES_XML";

    public static final String SHORT_DESCRIPTION = "Deactivate IPSEC";

    public DeactivateIpSecTask() {
        super(WorkflowTaskType.CPP_DEACTIVATE_IPSEC);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public DeactivateIpSecTask(final String fdn) {
        super(WorkflowTaskType.CPP_DEACTIVATE_IPSEC, fdn);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public String getRemoveCert() {
        return (String) getValue(REMOVE_CERT_PARAM_KEY);
    }

    public void setRemoveCert(final String removeCert) {
        this.setValue(REMOVE_CERT_PARAM_KEY, removeCert);
    }

    public String getRemoveTrust() {
        return (String) getValue(REMOVE_TRUST_PARAM_KEY);
    }

    public void setRemoveTrust(final String removeTrust) {
        this.setValue(REMOVE_TRUST_PARAM_KEY, removeTrust);
    }

    public String getNodesXml() {
        return (String) getValue(NODES_XML_PARAM_KEY);
    }

    public void setNodesXml(final String nodesXml) {
        this.setValue(NODES_XML_PARAM_KEY, nodesXml);
    }
}
