/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction;

import java.util.Set;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowActionTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

/**
 * <p>
 * Workflow task representing a request to delete RTSEL server configuration for CPP nodes
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.RTSEL_DELETE
 * </p>
 *
 * @author xchowja
 */
public class RtselDeleteServerTask extends WorkflowActionTask {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public static String SERVER_NAMES = WorkflowParameterKeys.SERVER_NAMES.toString();
    public static final String SHORT_DESCRIPTION = "Delete External Server";

    public RtselDeleteServerTask() {
        super(WorkflowTaskType.RTSEL_DELETE_SERVER);
        setShortDescription(SHORT_DESCRIPTION);

    }

    public RtselDeleteServerTask(final String nodeName) {
        super(WorkflowTaskType.RTSEL_DELETE_SERVER, nodeName);
        setShortDescription(SHORT_DESCRIPTION);

    }

    /**
     * @return the servername
     */
    @SuppressWarnings("unchecked")
    public Set<String> getServerNames() {
        return (Set<String>) getValue(SERVER_NAMES);
    }

    /**
     * @param serverNames
     *            the SERVERNAME to set
     */
    public void setServerNames(final Set<String> serverNames) {
        setValue(SERVER_NAMES, serverNames);

    }

}
