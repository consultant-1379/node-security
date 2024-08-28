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

public class CbpOiCheckCertificatesAlreadyInstalledTask extends WorkflowQueryTask {

    private static final long serialVersionUID = -899235502460082415L;

    public static final String OUTPUT_PARAMS_KEY = WorkflowParameterKeys.OUTPUT_PARAMS.toString();

    public static final String SHORT_DESCRIPTION = "Check certificates already installed In Truststore";

    public CbpOiCheckCertificatesAlreadyInstalledTask() {
        super(WorkflowTaskType.CBPOI_CHECK_CERTIFICATES_ALREADY_INSTALLED);
        setShortDescriptionLocal(SHORT_DESCRIPTION);
    }

    public CbpOiCheckCertificatesAlreadyInstalledTask(String fdn) {
        super(WorkflowTaskType.CBPOI_CHECK_CERTIFICATES_ALREADY_INSTALLED, fdn);
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
