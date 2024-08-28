package com.ericsson.oss.services.nscs.workflow.tasks.log;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowActionTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;

public class LogSuccessTask extends WorkflowActionTask {

    private static final long serialVersionUID = 4505400069072580615L;

    public static final String SHORT_DESCRIPTION = "Log success";

    public LogSuccessTask() {
        super(WorkflowTaskType.LOG_SUCCESS);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public LogSuccessTask(final String fdn) {
        super(WorkflowTaskType.LOG_SUCCESS, fdn);
        setShortDescription(SHORT_DESCRIPTION);
    }
}