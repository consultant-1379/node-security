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
package com.ericsson.oss.services.nscs.workflow.tasks.log;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowActionTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;

/**
 * Task to log workflow task handler timeout.
 *
 * @author emaborz
 *
 */
public class LogTimeoutTask extends WorkflowActionTask {

    private static final long serialVersionUID = 1652847294570355199L;

    public static final String SHORT_DESCRIPTION = "Log timeout";

    public LogTimeoutTask() {
        super(WorkflowTaskType.LOG_TIMEOUT);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public LogTimeoutTask(final String fdn) {
        super(WorkflowTaskType.LOG_TIMEOUT, fdn);
        setShortDescription(SHORT_DESCRIPTION);
    }
}
