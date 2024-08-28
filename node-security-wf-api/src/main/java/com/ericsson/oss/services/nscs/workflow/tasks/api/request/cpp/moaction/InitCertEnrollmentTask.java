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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction;

import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.AsyncActionTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowActionTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

/**
 * <p>
 * Workflow task representing a request to initialize the certificate enrollment process
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.CPP_INIT_CERT_ENROLLMENT
 * </p>
 *
 * @author emaynes
 */
@AsyncActionTask(errorMessage = "CppInitCertEnrollmentTaskFailed")
public class InitCertEnrollmentTask extends WorkflowActionTask {

    private static final long serialVersionUID = -6360709409532140347L;

    /**
     * Key of the rollbackTimeout value in the map
     */
    public static final String CPP_ROLLBACK_TIMEOUT = "rollbackTimeout";

    /**
     * Key of the subjectAltName in the map
     */
    public static final String CPP_SUBJECT_ALT_NAME_KEY = WorkflowParameterKeys.IPSEC_SUB_ALT_NAME.toString();

    /**
     * Key of the subjectAltNameType in the map
     */
    public static final String CPP_SUBJECT_ALT_NAME_FORMAT_KEY = WorkflowParameterKeys.IPSEC_SUB_ALT_NAME_TYPE.toString();

    /**
     * Task EnrollmentMode
     */
    public static final String ENROLLMENT_MODE = WorkflowParameterKeys.ENROLLMENT_MODE.toString();

    /**
     * Default value of the rollback timeout is 0 minutes
     */
    public static final int defaultRollbackTimeout = 0;

    public static final String SHORT_DESCRIPTION = "Init OAM certEnroll";

    /**
     * Constructs InitCertEnrollmentTask. Sets the rollbackTimeout to default.
     */
    public InitCertEnrollmentTask() {
        super(WorkflowTaskType.CPP_INIT_CERT_ENROLLMENT);
        setRollbackTimeout(defaultRollbackTimeout);
        setShortDescription(SHORT_DESCRIPTION);
    }

    /**
     * Constructs InitCertEnrollmentTask. Sets the rollbackTimeout to default.
     *
     * @param nodeName
     *            of the NE
     */
    public InitCertEnrollmentTask(final String nodeName) {
        super(WorkflowTaskType.CPP_INIT_CERT_ENROLLMENT, nodeName);
        setRollbackTimeout(defaultRollbackTimeout);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public InitCertEnrollmentTask(final String nodeName, final String enrollmentMode) {
        super(WorkflowTaskType.CPP_INIT_CERT_ENROLLMENT, nodeName);
        setValue(ENROLLMENT_MODE, enrollmentMode);
        setRollbackTimeout(defaultRollbackTimeout);
        setShortDescription(SHORT_DESCRIPTION);
    }

    /**
     * Constructs InitCertEnrollmentTask.
     *
     * @param nodeFDN
     *            fdn of the NE
     * @param rollbackTimeout
     *            of the InitCertEnrollment MO action
     * @param enrollmentMode the enrollmentMode
     */
    public InitCertEnrollmentTask(final String nodeFDN, final int rollbackTimeout, final String enrollmentMode) {
        this(nodeFDN);
        setRollbackTimeout(rollbackTimeout);
        setEnrollmentMode(enrollmentMode);
        setShortDescription(SHORT_DESCRIPTION);
    }

    /**
     * Constructs InitCertEnrollmentTask.
     *
     * @param nodeName
     *            of the NE
     * @param rollbackTimeout
     *            of the InitCertEnrollment MO action
     */
    public InitCertEnrollmentTask(final String nodeName, final int rollbackTimeout) {
        this(nodeName);
        setRollbackTimeout(rollbackTimeout);
        setShortDescription(SHORT_DESCRIPTION);
    }

    /**
     * Gets rollbackTimeout
     *
     * @return rollbackTimeout minutes (int)
     */
    public int getRollbackTimeout() {
        return (Integer) getValue(CPP_ROLLBACK_TIMEOUT);
    }

    /**
     * Sets rollbackTimeout
     *
     * @param rollbackTimeout
     *            minutes (int)
     */
    public final void setRollbackTimeout(final int rollbackTimeout) {
        this.setValue(CPP_ROLLBACK_TIMEOUT, rollbackTimeout);
    }

    /**
     * Sets enrollmentMode
     *
     * @param enrollmentMode the enrollmentMode
     */
    public void setEnrollmentMode(final String enrollmentMode) {
        setValue(ENROLLMENT_MODE, enrollmentMode);
    }

    /**
     * @return the enrollmentMode
     */
    public EnrollmentMode getEnrollmentMode() {
        try {
            return EnrollmentMode.valueOf((String) getValue(ENROLLMENT_MODE));
        } catch (final Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
}