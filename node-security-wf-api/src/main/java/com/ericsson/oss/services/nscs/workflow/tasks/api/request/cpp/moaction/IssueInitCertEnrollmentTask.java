/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction;

import java.io.Serializable;
import java.util.Map;

import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.iscf.BaseSubjectAltNameDataType;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.AsyncActionTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

/*
 **
 * <p>
 * Workflow task representing a request to initialize the certificate enrollment process
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.CPP_ISSUE_CERT_ENROLLMENT
 * </p>
 *
 * @author elucbot
 */
@AsyncActionTask(errorMessage = "CppInitCertEnrollmentTaskFailed")
public class IssueInitCertEnrollmentTask extends WorkflowQueryTask {

    private static final long serialVersionUID = -7250547733310503790L;

    /**
     * Default value of the rollback timeout is 0 minutes
     */
    private static final int defaultRollbackTimeout = 0;

    /**
     * Key of the rollbackTimeout value in the map
     */
    public static final String CPP_ROLLBACK_TIMEOUT = WorkflowParameterKeys.ROLLBACK_TIMEOUT.toString();

    /**
     * Key of the entityProfileName in the map
     */
    public static final String ENTITY_PROFILE_NAME = WorkflowParameterKeys.ENTITY_PROFILE_NAME.toString();

    /**
     * Key of the subjectAltName in the map
     */
    public static final String SUB_ALT_NAME = WorkflowParameterKeys.IPSEC_SUB_ALT_NAME.toString();

    /**
     * Key of the subjectAltNameType in the map
     */
    public static final String SUB_ALT_NAME_TYPE = WorkflowParameterKeys.IPSEC_SUB_ALT_NAME_TYPE.toString();

    /**
     * Key of the AlgorithmKeys in the map
     */
    public static final String ALGORITHM_KEY = WorkflowParameterKeys.KEY_SSHKEYS_GENERATION_ALGORITHM.toString();

    /**
     * Task EnrollmentMode
     */
    public static final String ENROLLMENT_MODE = WorkflowParameterKeys.ENROLLMENT_MODE.toString();

    /**
     * entity Common Name
     */
    public static final String COMMON_NAME = WorkflowParameterKeys.COMMON_NAME.toString();

    public static final String OUTPUT_PARAMS_KEY = WorkflowParameterKeys.OUTPUT_PARAMS.toString();

    public static final String SHORT_DESCRIPTION = "Init OAM certEnroll";

    /**
     * Constructs IssueInitCertEnrollmentTask. Sets the rollbackTimeout to default.
     */
    public IssueInitCertEnrollmentTask() {
        super(WorkflowTaskType.CPP_ISSUE_CERT_ENROLLMENT);
        setRollbackTimeout(defaultRollbackTimeout);
        setEntityProfileName("");
        setSubjectAltName("");
        setSubjectAltNameType("");
        setAlgoKeySize("");
        setEnrollmentMode("");
        setCommonName("");
        setShortDescription(SHORT_DESCRIPTION);
    }

    /**
     * Constructs IssueInitCertEnrollmentTask. Sets the rollbackTimeout to default.
     *
     * @param nodeName
     *            of the NE
     */
    public IssueInitCertEnrollmentTask(final String nodeName) {
        super(WorkflowTaskType.CPP_ISSUE_CERT_ENROLLMENT, nodeName);
        setRollbackTimeout(defaultRollbackTimeout);
        setEntityProfileName("");
        setSubjectAltName("");
        setSubjectAltNameType("");
        setAlgoKeySize("");
        setEnrollmentMode("");
        setCommonName("");
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
    public IssueInitCertEnrollmentTask(final String nodeName, final int rollbackTimeout) {
        this(nodeName);
        setRollbackTimeout(rollbackTimeout);
        setShortDescription(SHORT_DESCRIPTION);
    }

    /**
     * Gets rollbackTimeout
     *
     * @return rollbackTimeout minutes (Integer)
     */
    public Integer getRollbackTimeout() {
        return (Integer) getValue(CPP_ROLLBACK_TIMEOUT);
    }

    /**
     * Sets rollbackTimeout
     *
     * @param rollbackTimeout
     *            minutes (int)
     */
    public void setRollbackTimeout(final int rollbackTimeout) {
        setValue(CPP_ROLLBACK_TIMEOUT, rollbackTimeout);
    }

    /**
     * @return the entityProfileName
     */
    public String getEntityProfileName() {
        return (String) getValue(ENTITY_PROFILE_NAME);
    }

    /**
     * Sets entityProfileName
     *
     * @param entityProfileName the entityProfileName
     */
    public void setEntityProfileName(final String entityProfileName) {
        setValue(ENTITY_PROFILE_NAME, entityProfileName);
    }

    /**
     * @return the subjectAltName
     */
    public BaseSubjectAltNameDataType getSubjectAltName() {
        String initVal = "";
        if (getValue(SUB_ALT_NAME) != null) {
            initVal = getValue(SUB_ALT_NAME).toString();
        }
        final BaseSubjectAltNameDataType subjectAltNameString = new SubjectAltNameStringType(initVal);
        return subjectAltNameString;
    }

    /**
     * Sets subjectAltName
     *
     * @param subjectAltName the subjectAltName
     */
    public void setSubjectAltName(final String subjectAltName) {
        setValue(SUB_ALT_NAME, subjectAltName);
    }

    /**
     * @return the subjectAltNameType
     */
    public SubjectAltNameFormat getSubjectAltNameType() {
        try {
            return SubjectAltNameFormat.valueOf((String) getValue(SUB_ALT_NAME_TYPE));
        } catch (final Exception e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
            return null;
        }
    }

    /**
     * Sets subjectAltNameType
     *
     * @param subjectAltNameType the subjectAltNameType
     */
    public void setSubjectAltNameType(final String subjectAltNameType) {
        setValue(SUB_ALT_NAME_TYPE, subjectAltNameType);
    }

    /**
     * @return the algoKeySize
     */
    public AlgorithmKeys getAlgoKeySize() {
        try {
            return AlgorithmKeys.valueOf((String) getValue(ALGORITHM_KEY));
        } catch (final Exception e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
            return null;
        }
    }

    /**
     * Sets algoKeySize
     *
     * @param algorithmKeySize the algorithmKeySize
     */
    public void setAlgoKeySize(final String algorithmKeySize) {
        setValue(ALGORITHM_KEY, algorithmKeySize);
    }

    /**
     * @return the enrollmentMode
     */
    public EnrollmentMode getEnrollmentMode() {
        try {
            return EnrollmentMode.valueOf((String) getValue(ENROLLMENT_MODE));
        } catch (final Exception e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
            return null;
        }
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
     * @return the commonName
     */
    public String getCommonName() {
        return (String) getValue(COMMON_NAME);
    }

    /**
     * @param commonName
     *            the commonName to set
     */
    public void setCommonName(final String commonName) {
        setValue(COMMON_NAME, commonName);
    }
}
