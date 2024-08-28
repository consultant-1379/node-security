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
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

/**
 * @author enmadmin
 *
 */
public class IssueInitCertEnrollmentIpSecTask extends WorkflowQueryTask {

    private static final long serialVersionUID = -8767557940854727299L;

    /**
     * Key of the subjectAltName in the map
     */
    public static final String SUB_ALT_NAME = WorkflowParameterKeys.IPSEC_SUB_ALT_NAME.toString();

    /**
     * Key of the subjectAltNameType in the map
     */
    public static final String SUB_ALT_NAME_TYPE = WorkflowParameterKeys.IPSEC_SUB_ALT_NAME_TYPE.toString();

    /**
     * Key of the rollback timeout in the map
     */
    public static final String CPP_ROLLBACK_TIMEOUT = WorkflowParameterKeys.ROLLBACK_TIMEOUT.toString();

    /**
     * Key of the entityProfileName in the map
     */
    public static final String ENTITY_PROFILE_NAME = WorkflowParameterKeys.ENTITY_PROFILE_NAME.toString();

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

    /**
     * Default value of the rollback timeout is 10 minutes
     */
    public static final int defaultRollbackTimeout = 2;

    /**
     * Maps containing the the params used for Revoke workflow
     */
    public static final String OUTPUT_PARAMS_KEY = WorkflowParameterKeys.OUTPUT_PARAMS.toString();

    public static final String SHORT_DESCRIPTION = "Init IPSEC certEnroll";

    /**
     * Construct IssueInitCertEnrollmentIpSecTask
     */
    public IssueInitCertEnrollmentIpSecTask() {
        super(WorkflowTaskType.CPP_ISSUE_CERT_ENROLLMENT_IPSEC);
        setRollbackTimeout(defaultRollbackTimeout);
        setValue(SUB_ALT_NAME, "");
        setValue(SUB_ALT_NAME_TYPE, "");
        setValue(ALGORITHM_KEY, "");
        setValue(ENTITY_PROFILE_NAME, "");
        setValue(ENROLLMENT_MODE, "");
        setValue(COMMON_NAME, "");
        setShortDescription(SHORT_DESCRIPTION);
    }

    /**
     * Construct IssueInitCertEnrollmentIpSecTask
     *
     * @param nodeFDN the nodeFDN
     */
    public IssueInitCertEnrollmentIpSecTask(final String nodeFDN) {
        super(WorkflowTaskType.CPP_ISSUE_CERT_ENROLLMENT_IPSEC, nodeFDN);
        setRollbackTimeout(defaultRollbackTimeout);
        setValue(SUB_ALT_NAME, "");
        setValue(SUB_ALT_NAME_TYPE, "");
        setValue(ALGORITHM_KEY, "");
        setValue(ENTITY_PROFILE_NAME, "");
        setValue(ENROLLMENT_MODE, "");
        setValue(COMMON_NAME, "");
        setShortDescription(SHORT_DESCRIPTION);
    }

    /**
     * Construct IssueInitCertEnrollmentIpSecTask
     *
     * @param nodeFDN the nodeFDN
     * @param rollbackTimeout the rollbackTimeout
     * @param subAltName the subAltName
     * @param subAltNameType teh subAltName
     */
    public IssueInitCertEnrollmentIpSecTask(final String nodeFDN, final int rollbackTimeout, final String subAltName, final String subAltNameType) {
        this(nodeFDN);
        setRollbackTimeout(rollbackTimeout);
        setValue(SUB_ALT_NAME, subAltName);
        setValue(SUB_ALT_NAME_TYPE, subAltNameType);
        setValue(ALGORITHM_KEY, "");
        setValue(ENTITY_PROFILE_NAME, "");
        setValue(COMMON_NAME, "");
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
     * Gets the subject alt name.
     *
     * @return subject alt name.
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
     * Sets the subject alt name.
     *
     * @param subAltName
     *            subject alt name.
     */
    public void setSubjectAltName(final String subAltName) {
        setValue(SUB_ALT_NAME, subAltName);
    }

    /**
     * Gets the subject alt name type.
     *
     * @return subject alt name type.
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
     * Sets the subject alt name type.
     *
     * @param subAltNametype
     *            subject alt name type.
     */
    public void setSubjectAltNameType(final String subAltNametype) {
        setValue(SUB_ALT_NAME_TYPE, subAltNametype);

    }

    /**
     * Gets entityProfileName
     *
     * @return entityProfileName
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
