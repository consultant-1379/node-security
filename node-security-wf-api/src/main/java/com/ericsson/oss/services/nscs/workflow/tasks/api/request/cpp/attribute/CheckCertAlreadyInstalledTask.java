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
 * Workflow task representing a request to check if the certs are already installed over the node
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.CPP_CHECK_CERT_ALREADY_INSTALLED
 * </p>
 *
 * @author eanbuzz
 */
public class CheckCertAlreadyInstalledTask extends WorkflowQueryTask {

    private static final long serialVersionUID = 6368791987234658292L;

    public static final String CPP_SUBJECT_ALT_NAME_KEY = "SUB_ALT_NAME";

    public static final String SHORT_DESCRIPTION = "Check IPSEC trustInstall";

    public CheckCertAlreadyInstalledTask() {
        super(WorkflowTaskType.CPP_CHECK_CERT_ALREADY_INSTALLED);
        setValue(CPP_SUBJECT_ALT_NAME_KEY, "");
        setShortDescription(SHORT_DESCRIPTION);
    }

    public CheckCertAlreadyInstalledTask(final String fdn, final String subAltName) {
        super(WorkflowTaskType.CPP_CHECK_CERT_ALREADY_INSTALLED, fdn);
        setValue(CPP_SUBJECT_ALT_NAME_KEY, subAltName);
        setShortDescription(SHORT_DESCRIPTION);
    }

    /**
     * Gets the subject alt name.
     *
     * @return subject alt name.
     */
    public String getSubjectAltName() {
        return (String) getValue(CPP_SUBJECT_ALT_NAME_KEY);
    }

    /**
     * Sets the subject alt name.
     *
     * @param subAltName
     *            subject alt name.
     */
    public void setSubjectAltName(final String subAltName) {
        setValue(CPP_SUBJECT_ALT_NAME_KEY, subAltName);
    }

}
