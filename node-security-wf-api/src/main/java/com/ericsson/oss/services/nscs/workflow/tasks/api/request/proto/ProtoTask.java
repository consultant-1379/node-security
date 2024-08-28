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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.proto;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowActionTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;

/**
 * Created by emaynes on 24/06/2014.
 */
public class ProtoTask extends WorkflowActionTask {

    public static final String NETWORK_TYPE_PARAMETER = "networkType";
    public static final String NODE_TYPE_PARAMETER = "nodeType";
    private static final long serialVersionUID = -6190997222907356191L;

    public ProtoTask() {
        super(WorkflowTaskType.PROTOTYPE_TASK);
    }

    public ProtoTask(final String fdn, final String networkType, final String nodeType) {
        super(WorkflowTaskType.PROTOTYPE_TASK, fdn);
        setNetworkType(networkType);
        setNodeType(nodeType);
    }

    public final void setNetworkType(final String networkType) {
        setValueString(NETWORK_TYPE_PARAMETER, networkType);
    }

    public final String getNetworkType() {
        return getValueString(NETWORK_TYPE_PARAMETER);
    }

    public final void setNodeType(final String nodeType) {
        setValueString(NODE_TYPE_PARAMETER, nodeType);
    }

    public final String getNodeType() {
        return getValueString(NODE_TYPE_PARAMETER);
    }
}
