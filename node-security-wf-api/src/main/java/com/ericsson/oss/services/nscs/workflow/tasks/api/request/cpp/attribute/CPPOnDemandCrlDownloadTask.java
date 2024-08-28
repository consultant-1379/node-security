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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;

/**
 * <p>
 * Workflow task representing a request to on demand crl download process for CPP nodes
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.CPP_ON_DEMAND_CRL_DOWNLOAD
 * </p>
 *
 * @author xlakdag
 */
public class CPPOnDemandCrlDownloadTask extends WorkflowQueryTask {

    private static final long serialVersionUID = 8056165450061874113L;

    public static final String SHORT_DESCRIPTION = "CRL download";

    public CPPOnDemandCrlDownloadTask() {
        super(WorkflowTaskType.CPP_ON_DEMAND_CRL_DOWNLOAD);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public CPPOnDemandCrlDownloadTask(final String fdn) {
        super(WorkflowTaskType.CPP_ON_DEMAND_CRL_DOWNLOAD, fdn);
        setShortDescription(SHORT_DESCRIPTION);
    }

}
