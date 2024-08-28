/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.cbpoi.attribute;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;
import java.io.Serializable;
import java.util.Map;

public class CbpOiPrepareInstallTrustedCertsTask extends WorkflowQueryTask {

    private static final long serialVersionUID = 6175483947135881429L;

    public static final String OUTPUT_PARAMS_KEY = WorkflowParameterKeys.OUTPUT_PARAMS.toString();

    public static final String SHORT_DESCRIPTION = "Prepare install trusted certificates";

    public CbpOiPrepareInstallTrustedCertsTask() {
        super(WorkflowTaskType.CBPOI_PREPARE_INSTALL_TRUSTED_CERTS);
        setShortDescriptionLocal(SHORT_DESCRIPTION);
    }

    public CbpOiPrepareInstallTrustedCertsTask(String fdn) {
        super(WorkflowTaskType.CBPOI_PREPARE_INSTALL_TRUSTED_CERTS, fdn);
        setShortDescriptionLocal(SHORT_DESCRIPTION);
    }

    /**
     * @return the outputParams
     */
    @SuppressWarnings("unchecked")
    public Map<String, Serializable> getOutputParams() {
        return (Map<String, Serializable>) getValue(OUTPUT_PARAMS_KEY);
    }

    /**
     * @param outputParams
     *            the outputParams to set
     */
    public void setOutputParams(final Map<String, Serializable> outputParams) {
        setValue(OUTPUT_PARAMS_KEY, outputParams);
    }

    private void setShortDescriptionLocal(final String shortDescription) {
        super.setShortDescription(shortDescription);
    }
}
