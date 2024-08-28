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

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

public class ComEcimCheckIsExternalCATask extends WorkflowQueryTask {
    private static final long serialVersionUID = 4596490029353060470L;

    public static final String CERTIFICATE_ENROLLMENT_CA = WorkflowParameterKeys.CERTIFICATE_ENROLLMENT_CA.toString();
    public static final String TRUSTED_CATEGORY_KEY = WorkflowParameterKeys.TRUSTED_CATEGORY.toString();

    public static final String SHORT_DESCRIPTION = "Is ExtCA";

    public ComEcimCheckIsExternalCATask() {
        super(WorkflowTaskType.COM_ECIM_CHECK_IS_EXTERNAL_CA);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public ComEcimCheckIsExternalCATask(final String fdn) {
        super(WorkflowTaskType.COM_ECIM_CHECK_IS_EXTERNAL_CA, fdn);
        setShortDescription(SHORT_DESCRIPTION);
    }

    /**
     * @return the certificateEnrollmentCa
     */
    public String getCertificateEnrollmentCa() {
        return (String) getValue(CERTIFICATE_ENROLLMENT_CA);
    }

    /**
     * @param certificateEnrollmentCa
     *            the certificateEnrollmentCa to set
     */
    public void setCertificateEnrollmentCa(final String certificateEnrollmentCa) {
        setValue(CERTIFICATE_ENROLLMENT_CA, certificateEnrollmentCa);
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
}
