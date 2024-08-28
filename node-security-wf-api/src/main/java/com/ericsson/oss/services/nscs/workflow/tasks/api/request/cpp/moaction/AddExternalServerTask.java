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

import java.util.List;
import java.util.Map;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowActionTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

/**
 * <P>
 * Workflow task representing a request to AddExternalServer for RTSEL
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.CPP_ADD_EXTERNAL_SERVER
 * </p>
 * 
 * @author tcsramc
 * 
 */
public class AddExternalServerTask extends WorkflowActionTask {

    private static final long serialVersionUID = -6424933246784924070L;
    public static String SERVER_CONFIG = WorkflowParameterKeys.SERVER_CONFIG.toString();
    public static final String SHORT_DESCRIPTION = "Add External Server";

    public AddExternalServerTask() {
        super(WorkflowTaskType.CPP_ADD_EXTERNAL_SERVER);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public AddExternalServerTask(final String nodeName) {
        super(WorkflowTaskType.CPP_ADD_EXTERNAL_SERVER, nodeName);
        setShortDescription(SHORT_DESCRIPTION);
    }

    /**
     * @return serverConfig
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getServerConfig() {
        return (List<Map<String, Object>>) getValue(SERVER_CONFIG);
    }

    /**
     * @param serverConfig
     *            the serverConfig to set
     */
    public void setServerConfig(final List<Map<String, Object>> serverConfig) {
        setValue(SERVER_CONFIG, serverConfig);
    }

}
