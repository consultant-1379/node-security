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

package com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.moaction;


import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowActionTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;

public class ComEcimActivateFtpesOnNodeTask extends WorkflowActionTask {

    public static final String SHORT_DESCRIPTION = "FTPES Activate";
    private static final long serialVersionUID = -3898923307017902368L;


    public ComEcimActivateFtpesOnNodeTask() {
        super(WorkflowTaskType.COM_ACTIVATE_FTPES);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public ComEcimActivateFtpesOnNodeTask(final String fdn) {
        super(WorkflowTaskType.COM_ACTIVATE_FTPES, fdn);
        setShortDescription(SHORT_DESCRIPTION);
    }
}
