/*-----------------------------------------------------------------------------
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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute;

import java.io.Serializable;
import java.util.Map;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

/**
 * <p>
 * Workflow task representing a request to perform a generic enrollment task.
 * </p>
 */
public class EnrollmentWorkflowQueryTask extends WorkflowQueryTask {

    private static final long serialVersionUID = -4464577069681985071L;

    /**
     * Key of the output parameters parameter in the workflow parameters map.
     */
    public static final String OUTPUT_PARAMS_KEY = WorkflowParameterKeys.OUTPUT_PARAMS.toString();

    /**
     * Key of the certificate type parameter in the workflow parameters map.
     */
    public static final String TRUSTED_CATEGORY_KEY = WorkflowParameterKeys.TRUSTED_CATEGORY.toString();

    /**
     * Key of the enrollment mode parameter in the workflow parameters map.
     */
    public static final String ENROLLMENT_MODE_KEY = WorkflowParameterKeys.ENROLLMENT_MODE.toString();

    /**
     * Key of the is reissue flag parameter in the workflow parameters map.
     */
    public static final String ISREISSUE_KEY = WorkflowParameterKeys.CERTIFICATE_ISREISSUE.toString();

    public EnrollmentWorkflowQueryTask(final WorkflowTaskType taskType, final String shortDescription) {
        super(taskType);
        setShortDescriptionLocal(shortDescription);
    }

    public EnrollmentWorkflowQueryTask(final WorkflowTaskType taskType, final String shortDescription, final String fdn) {
        super(taskType, fdn);
        setShortDescriptionLocal(shortDescription);
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

    /**
     * @return the trustedCertCategory
     */
    public String getTrustedCertCategory() {
        return (String) getValue(TRUSTED_CATEGORY_KEY);
    }

    /**
     * @param trustedCertCategory
     *            the trustedCertCategory to set
     */
    public void setTrustedCertCategory(final String trustedCertCategory) {
        setValue(TRUSTED_CATEGORY_KEY, trustedCertCategory);
    }

    /**
     * @return the enrollmentMode
     */
    public String getEnrollmentMode() {
        return (String) getValue(ENROLLMENT_MODE_KEY);
    }

    /**
     * @param enrollmentMode
     *            the enrollmentMode to set
     */
    public void setEnrollmentMode(final String enrollmentMode) {
        setValue(ENROLLMENT_MODE_KEY, enrollmentMode);
    }

    /**
     * @return the isReissue
     */
    public String getIsReissue() {
        return (String) getValue(ISREISSUE_KEY);
    }

    /**
     * @param isReissue
     *            the isReissue to set
     */
    public void setIsReissue(final String isReissue) {
        setValue(ISREISSUE_KEY, isReissue);
    }

    private void setShortDescriptionLocal(final String shortDescription) {
        super.setShortDescription(shortDescription);
    }
}
