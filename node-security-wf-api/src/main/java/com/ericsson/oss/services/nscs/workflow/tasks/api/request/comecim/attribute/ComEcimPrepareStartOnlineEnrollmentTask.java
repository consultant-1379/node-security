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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute;

import java.io.Serializable;
import java.util.Map;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

public class ComEcimPrepareStartOnlineEnrollmentTask extends WorkflowQueryTask {

    private static final long serialVersionUID = 5514106792289475161L;

    /**
     * Key of the trustedCertCategory value in the map
     */
    public static final String TRUSTED_CATEGORY_KEY = WorkflowParameterKeys.TRUSTED_CATEGORY.toString();

    /**
     * Key of the output parameters in the map
     */
    public static final String OUTPUT_PARAMS_KEY = WorkflowParameterKeys.OUTPUT_PARAMS.toString();

    public static final String SHORT_DESCRIPTION = "Prepare certEnroll";

    public ComEcimPrepareStartOnlineEnrollmentTask() {
        super(WorkflowTaskType.COM_ECIM_PREPARE_START_ONLINE_ENROLLMENT);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public ComEcimPrepareStartOnlineEnrollmentTask(final String fdn) {
        super(WorkflowTaskType.COM_ECIM_PREPARE_START_ONLINE_ENROLLMENT, fdn);
        setShortDescription(SHORT_DESCRIPTION);
    }

    /**
     * @return the trustedCategory
     */
    public String getTrustedCategory() {
        return (String) getValue(TRUSTED_CATEGORY_KEY);
    }

    /**
     * @param trustedCategory
     *            the trustedCategory to set
     */
    public void setTrustedCategory(final String trustedCategory) {
        setValue(TRUSTED_CATEGORY_KEY, trustedCategory);
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

}
