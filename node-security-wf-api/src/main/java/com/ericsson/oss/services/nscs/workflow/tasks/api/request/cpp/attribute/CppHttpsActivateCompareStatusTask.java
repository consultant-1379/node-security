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

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;

/**
 * Created by ekrzsia on 9/6/17.
 */
public class CppHttpsActivateCompareStatusTask extends WorkflowQueryTask {

    private static final long serialVersionUID = 7614902885910048430L;
    public static final String SHORT_DESCRIPTION = "Compare Https status for activate";


    public CppHttpsActivateCompareStatusTask(){
        super(WorkflowTaskType.CPP_COMPARE_HTTPS_STATUS_FOR_ACTIVATE);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public CppHttpsActivateCompareStatusTask(final String nodeName){
        super(WorkflowTaskType.CPP_COMPARE_HTTPS_STATUS_FOR_ACTIVATE,nodeName);
        setShortDescription(SHORT_DESCRIPTION);
    }
}
