package com.ericsson.oss.services.nscs.workflow.tasks.log;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowActionTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;

public class LogErrorTask extends WorkflowActionTask {

    private static final long serialVersionUID = 4584283154863371072L;

    public static final String SHORT_DESCRIPTION = "Log error";

    public LogErrorTask() {
        super(WorkflowTaskType.LOG_ERROR);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public LogErrorTask(final String fdn) {
        super(WorkflowTaskType.LOG_ERROR, fdn);
        setShortDescription(SHORT_DESCRIPTION);
    }
}