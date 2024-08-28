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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

public class TestCheckSomethingTask extends WorkflowQueryTask {

    private static final long serialVersionUID = 3715794150828760704L;
    public static final String TEST_CHECK_RESULT_KEY = WorkflowParameterKeys.TEST_CHECK_RESULT.toString();
    public static final String SHORT_DESCRIPTION = "Test Check Something";

    public TestCheckSomethingTask() {
        super(WorkflowTaskType.TEST_CHECK_SOMETHING);
        setCheckResultLocal("CHECK_OK");
        setShortDescriptionLocal(SHORT_DESCRIPTION);
    }

    public TestCheckSomethingTask(final String fdn, final String checkResult) {
        super(WorkflowTaskType.TEST_CHECK_SOMETHING, fdn);
        setCheckResultLocal(checkResult);
        setShortDescriptionLocal(SHORT_DESCRIPTION);
    }

    /**
     * Gets the check result
     *
     * @return the check result
     */
    public String getCheckResult() {
        return (String) getValue(TEST_CHECK_RESULT_KEY);
    }

    /**
     * Sets the check result
     *
     * @param checkResult
     *            the check result
     */
    public void setCheckResult(final String checkResult) {
        setValue(TEST_CHECK_RESULT_KEY, checkResult);
    }

    private void setCheckResultLocal(final String checkResult) {
        super.setValue(TEST_CHECK_RESULT_KEY, checkResult);
    }

    private void setShortDescriptionLocal(final String shortDescription) {
        super.setShortDescription(shortDescription);
    }
}
