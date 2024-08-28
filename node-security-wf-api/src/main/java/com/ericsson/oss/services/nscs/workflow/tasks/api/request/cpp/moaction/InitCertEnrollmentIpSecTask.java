/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction;

import com.ericsson.nms.security.nscs.api.iscf.BaseSubjectAltNameDataType;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowActionTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

/**
 * <p>
 * Workflow task representing a request to initialize the certificate enrollment process for ipsec
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.CPP_INIT_CERT_ENROLLMENT_IPSEC
 * </p>
 *
 * @author emehsau
 */
public class InitCertEnrollmentIpSecTask extends WorkflowActionTask {

    private static final long serialVersionUID = 2337204885164678523L;

    /**
     * Key of the subjectAltName in the map
     */
    public static final String CPP_SUBJECT_ALT_NAME_KEY = WorkflowParameterKeys.IPSEC_SUB_ALT_NAME.toString();

    /**
     * Key of the subjectAltNameType in the map
     */
    public static final String CPP_SUBJECT_ALT_NAME_FORMAT_KEY = WorkflowParameterKeys.IPSEC_SUB_ALT_NAME_TYPE.toString();

    /**
     * Key of the rollbackTimeout value in the map
     */
    public static final String CPP_ROLLBACK_TIMEOUT = "rollbackTimeout";

    /**
     * Default value of the rollback timeout is 10 minutes
     */
    public static final int defaultRollbackTimeout = 2;

    public static final String SHORT_DESCRIPTION = "Init IPSEC certEnroll";

    /**
     * Constructs InitCertEnrollmentTask. Sets the rollbackTimeout to default.
     */
    public InitCertEnrollmentIpSecTask() {
        super(WorkflowTaskType.CPP_INIT_CERT_ENROLLMENT_IPSEC);
        setRollbackTimeout(defaultRollbackTimeout);
        setValue(CPP_SUBJECT_ALT_NAME_KEY, "");
        setValue(CPP_SUBJECT_ALT_NAME_FORMAT_KEY, "");
        setShortDescription(SHORT_DESCRIPTION);
    }

    /**
     * Constructs InitCertEnrollmentTask. Sets the rollbackTimeout to default.
     *
     * @param nodeFDN
     *            fdn of the NE
     */
    public InitCertEnrollmentIpSecTask(final String nodeFDN) {
        super(WorkflowTaskType.CPP_INIT_CERT_ENROLLMENT_IPSEC, nodeFDN);
        setRollbackTimeout(defaultRollbackTimeout);
        setValue(CPP_SUBJECT_ALT_NAME_KEY, "");
        setValue(CPP_SUBJECT_ALT_NAME_FORMAT_KEY, "");
        setShortDescription(SHORT_DESCRIPTION);
    }

    /**
     * Constructs InitCertEnrollmentTask.
     *
     * @param nodeFDN
     *            fdn of the NE
     * @param rollbackTimeout
     *            of the InitCertEnrollment MO action
     * @param subAltName
     *            subject alt name
     * @param subAltNameFormat
     *            subject alt name format
     */
    public InitCertEnrollmentIpSecTask(final String nodeFDN, final int rollbackTimeout, final String subAltName, final String subAltNameFormat) {
        this(nodeFDN);
        setRollbackTimeout(rollbackTimeout);
        setValue(CPP_SUBJECT_ALT_NAME_KEY, subAltName);
        setValue(CPP_SUBJECT_ALT_NAME_FORMAT_KEY, subAltNameFormat);
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
     * Gets the subject alt name.
     *
     * @return subject alt name.
     */
    public BaseSubjectAltNameDataType getSubjectAltName() {
        final BaseSubjectAltNameDataType subjectAltNameString = new SubjectAltNameStringType(getValue(CPP_SUBJECT_ALT_NAME_KEY).toString());
        return subjectAltNameString;
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

    /**
     * @return the subjectAltNameType
     */
    public SubjectAltNameFormat getSubjectAltNameFormat() {
        try {
            //return SubjectAltNameFormat.IPV4;
            return SubjectAltNameFormat.valueOf((String) getValue(CPP_SUBJECT_ALT_NAME_FORMAT_KEY));
        } catch (final Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sets subjectAltNameType
     *
     * @param subjectAltNameType the subjectAltNameType
     */
    public void setSubjectAltNameFormat(final String subjectAltNameType) {
        setValue(CPP_SUBJECT_ALT_NAME_FORMAT_KEY, subjectAltNameType);
    }

}