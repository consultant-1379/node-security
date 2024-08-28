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
 * Workflow task representing a request to interact with PKI to create/update the involved EndEntity and get the EnrollmentInfo
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.COM_ECIM_CHECK_AND_UPDATE_ENDENTITY
 * </p>
 *
 * @author xmanvej
 */

public class ComEcimCheckAndUpdateEndEntityTask extends WorkflowQueryTask {

	private static final long serialVersionUID = 4596490029353060470L;

        public static final String SUB_ALT_NAME_KEY = WorkflowParameterKeys.IPSEC_SUB_ALT_NAME.toString();
        public static final String SUB_ALT_NAME_TYPE_KEY = WorkflowParameterKeys.IPSEC_SUB_ALT_NAME_TYPE.toString();
        public static final String ENTITY_PROFILE_NAME_KEY = WorkflowParameterKeys.ENTITY_PROFILE_NAME.toString();
        public static final String ENROLLMENT_MODE_KEY = WorkflowParameterKeys.ENROLLMENT_MODE.toString();
        public static final String KEY_ALGORITHM_KEY = WorkflowParameterKeys.KEY_SSHKEYS_GENERATION_ALGORITHM.toString();
        public static final String TRUSTED_CATEGORY_KEY = WorkflowParameterKeys.TRUSTED_CATEGORY.toString();
        public static final String COMMON_NAME = WorkflowParameterKeys.COMMON_NAME.toString();
        public static final String OUTPUT_PARAMS_KEY = WorkflowParameterKeys.OUTPUT_PARAMS.toString();

	public static final String SHORT_DESCRIPTION = "Check EndEntity";

        public ComEcimCheckAndUpdateEndEntityTask() {
            super(WorkflowTaskType.COM_ECIM_CHECK_AND_UPDATE_ENDENTITY);
            setShortDescription(SHORT_DESCRIPTION);
        }

        public ComEcimCheckAndUpdateEndEntityTask(final String fdn) {
            super(WorkflowTaskType.COM_ECIM_CHECK_AND_UPDATE_ENDENTITY, fdn);
            setShortDescription(SHORT_DESCRIPTION);
        }

        /**
         * @return the subjectAltName
         */
        public String getSubjectAltName() {
            return (String) getValue(SUB_ALT_NAME_KEY);
        }

        /**
         * @param subjectAltName
         *            the subjectAltName to set
         */
        public void setSubjectAltName(final String subjectAltName) {
            setValue(SUB_ALT_NAME_KEY, subjectAltName);
        }

        /**
         * @return the subjectAltNameType
         */
        public String getSubjectAltNameType() {
            return (String) getValue(SUB_ALT_NAME_TYPE_KEY);
        }

        /**
         * @param subjectAltNameType
         *            the subjectAltNameType to set
         */
        public void setSubjectAltNameType(final String subjectAltNameType) {
            setValue(SUB_ALT_NAME_TYPE_KEY, subjectAltNameType);
        }

        /**
         * @return the entityProfileName
         */
        public String getEntityProfileName() {
            return (String) getValue(ENTITY_PROFILE_NAME_KEY);
        }

        /**
         * @param entityProfileName
         *            the entityProfileName to set
         */
        public void setEntityProfileName(final String entityProfileName) {
            setValue(ENTITY_PROFILE_NAME_KEY, entityProfileName);
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
         * @return the keyAlgorithm
         */
        public String getKeyAlgorithm() {
            return (String) getValue(KEY_ALGORITHM_KEY);
        }

        /**
         * @param keyAlgorithm
         *            the keyAlgorithm to set
         */
        public void setKeyAlgorithm(final String keyAlgorithm) {
            setValue(KEY_ALGORITHM_KEY, keyAlgorithm);
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
