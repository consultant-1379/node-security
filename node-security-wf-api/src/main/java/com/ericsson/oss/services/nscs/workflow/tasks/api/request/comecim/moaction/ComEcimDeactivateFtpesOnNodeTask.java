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

public class ComEcimDeactivateFtpesOnNodeTask extends WorkflowActionTask {

    public static final String SHORT_DESCRIPTION = "FTPES Deactivate";
    private static final long serialVersionUID = -3898923307017902368L;


    public ComEcimDeactivateFtpesOnNodeTask() {
        super(WorkflowTaskType.COM_DEACTIVATE_FTPES);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public ComEcimDeactivateFtpesOnNodeTask(final String fdn) {
        super(WorkflowTaskType.COM_DEACTIVATE_FTPES, fdn);
        setShortDescription(SHORT_DESCRIPTION);
    }
}
