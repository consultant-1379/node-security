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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowActionTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;

/**
 * <p>
 * Workflow task representing a request to create and upload the files to SMRS.
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.CPP_CREATE_UPLOAD_FILES_SMRS
 * </p>
 *
 * Created by ediniku.
 */
public class ChangeIpForOMSettingTask extends WorkflowActionTask {

    private static final long serialVersionUID = -6212237611575261826L;

    public static final String NODES_XML = "NODES_XML";

    public static final String SHORT_DESCRIPTION = "Change IP";

    public ChangeIpForOMSettingTask() {
        super(WorkflowTaskType.CPP_CHANGE_IP_OAM_SETTING);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public ChangeIpForOMSettingTask(final String nodeName) {
        super(WorkflowTaskType.CPP_CHANGE_IP_OAM_SETTING, nodeName);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public String getUserInputXml() {
        return (String) getValue(NODES_XML);
    }

    public void setUserInputXml(final String nodesXml) {
        setValue(NODES_XML, nodesXml);
    }

}
