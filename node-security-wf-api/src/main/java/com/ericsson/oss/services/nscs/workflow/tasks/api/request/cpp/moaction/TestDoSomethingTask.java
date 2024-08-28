/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2019
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
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

public class TestDoSomethingTask extends WorkflowActionTask {

    private static final long serialVersionUID = 4551420940326594579L;
    public static final String TEST_ACTION_RESULT_KEY = WorkflowParameterKeys.TEST_ACTION_RESULT.toString();
    public static final String SHORT_DESCRIPTION = "Test Do Something";

    public TestDoSomethingTask() {
        super(WorkflowTaskType.TEST_DO_SOMETHING);
        setActionResultLocal("SUCCESS");
        setShortDescriptionLocal(SHORT_DESCRIPTION);
    }

    public TestDoSomethingTask(final String fdn, final String actionResult) {
        super(WorkflowTaskType.TEST_DO_SOMETHING, fdn);
        setActionResultLocal(actionResult);
        setShortDescriptionLocal(SHORT_DESCRIPTION);
    }

    /**
     * Gets the action result
     *
     * @return the action result
     */
    public String getActionResult() {
        return (String) getValue(TEST_ACTION_RESULT_KEY);
    }

    /**
     * Sets the action result
     *
     * @param actionResult
     *            the action result
     */
    public void setActionResult(final String actionResult) {
        setValue(TEST_ACTION_RESULT_KEY, actionResult);
    }

    private void setActionResultLocal(final String actionResult) {
        super.setValue(TEST_ACTION_RESULT_KEY, actionResult);
    }

    private void setShortDescriptionLocal(final String shortDescription) {
        super.setShortDescription(shortDescription);
    }
}
