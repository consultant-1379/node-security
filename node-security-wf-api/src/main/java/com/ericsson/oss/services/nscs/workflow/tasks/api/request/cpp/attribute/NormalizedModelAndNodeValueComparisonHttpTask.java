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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowActionTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;

/**
 * 
 * @author edudluk
 *
 */
public class NormalizedModelAndNodeValueComparisonHttpTask extends WorkflowActionTask {

    private static final long serialVersionUID = 3965758600650997409L;

    public static final String SHORT_DESCRIPTION = "Compare HTTP";
    
    public NormalizedModelAndNodeValueComparisonHttpTask() {
        super(WorkflowTaskType.CPP_COMPARE_HTTP);
        setShortDescription(SHORT_DESCRIPTION);
    }
    
    public NormalizedModelAndNodeValueComparisonHttpTask(final String fdn) {
        super(WorkflowTaskType.CPP_COMPARE_HTTP, fdn);
        setShortDescription(SHORT_DESCRIPTION);
    }
}
