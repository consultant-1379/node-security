/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
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

/**
 * <p>
 * Workflow task representing a request to check NodeCredential MO parameters on Com Ecim Node
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.COM_ECIM_CHECK_NODE_CREDENTIAL_FOR_EXTERNAL_CA_REISSUE
 * </p>
 *
 * @author xrajesp
 */
public class ComEcimCheckNodeCredentialForExternalCAReIssueTask extends WorkflowQueryTask {

    private static final long serialVersionUID = 4596490029353060470L;

    public static final String ENROLLMENT_MODE_KEY = WorkflowParameterKeys.ENROLLMENT_MODE.toString();
    public static final String TRUSTED_CATEGORY_KEY = WorkflowParameterKeys.TRUSTED_CATEGORY.toString();
    public static final String OUTPUT_PARAMS_KEY = WorkflowParameterKeys.OUTPUT_PARAMS.toString();

    public static final String SHORT_DESCRIPTION = "Check nodeCredential for ExtCA";

    public ComEcimCheckNodeCredentialForExternalCAReIssueTask() {
        super(WorkflowTaskType.COM_ECIM_CHECK_NODE_CREDENTIAL_FOR_EXTERNAL_CA_REISSUE);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public ComEcimCheckNodeCredentialForExternalCAReIssueTask(final String fdn) {
        super(WorkflowTaskType.COM_ECIM_CHECK_NODE_CREDENTIAL_FOR_EXTERNAL_CA_REISSUE, fdn);
        setShortDescription(SHORT_DESCRIPTION);
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
