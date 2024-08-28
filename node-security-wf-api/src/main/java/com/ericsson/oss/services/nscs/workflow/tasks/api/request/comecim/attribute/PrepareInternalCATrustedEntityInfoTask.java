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
 * Workflow task representing a request to check to fetch trusted entities info from PKI to distribute over the COM ECIM node
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.PREPARE_EXTERNAL_CA_TRUSTED_ENTITY_INFO
 * </p>
 *
 */
public class PrepareInternalCATrustedEntityInfoTask extends WorkflowQueryTask {

    private static final long serialVersionUID = 5077295591739975937L;

    /**
     * Key of the trustedCertCategory value in the map
     */
    public static final String TRUST_CERTS_KEY = WorkflowParameterKeys.TRUSTED_CATEGORY.toString();

    /**
     * Key of the certificate authority value in the map
     */
    public static final String TRUSTED_CERTIFICATE_AUTHORITY_KEY = WorkflowParameterKeys.CERTIFICATE_AUTHORITY_ID.toString();

    /**
     * Key for the entity profile passed to the WF in case of issue command, that executes the Trust distribution too. In case of trust distribution only, it may be null, so default profile will be
     * used in handler.
     */
    public static final String ENTITY_PROFILE_KEY = WorkflowParameterKeys.ENTITY_PROFILE_NAME.toString();

    /**
     * Key of the reissue flag value in the map
     */
    public static final String ISREISSUE_KEY = WorkflowParameterKeys.CERTIFICATE_ISREISSUE.toString();

    /**
     * Key of the output parameters in the map
     */
    public static final String OUTPUT_PARAMS_KEY = WorkflowParameterKeys.OUTPUT_PARAMS.toString();

    public static final String SHORT_DESCRIPTION = "Prepare IntCA TrustInstall";

    public PrepareInternalCATrustedEntityInfoTask() {
        super(WorkflowTaskType.PREPARE_INTERNAL_CA_TRUSTED_ENTITY_INFO);
        setValue(TRUST_CERTS_KEY, "");
        setValue(TRUSTED_CERTIFICATE_AUTHORITY_KEY, "");
        setShortDescription(SHORT_DESCRIPTION);
    }

    public PrepareInternalCATrustedEntityInfoTask(final String fdn, final String trustCerts, final String trustedCA) {
        super(WorkflowTaskType.PREPARE_INTERNAL_CA_TRUSTED_ENTITY_INFO, fdn);
        setValue(TRUST_CERTS_KEY, trustCerts);
        setValue(TRUSTED_CERTIFICATE_AUTHORITY_KEY, trustedCA);
        setShortDescription(SHORT_DESCRIPTION);
    }

    /**
     * Gets the trust certs.
     *
     * @return trust certS.
     */
    public String getTrustCerts() {
        return (String) getValue(TRUST_CERTS_KEY);
    }

    /**
     * Sets the trust certs. It may be the trust file path or TrustedCertCategory
     *
     * @param trustCerts
     *            trust certs.
     */
    public void setTrustCerts(final String trustCerts) {
        setValue(TRUST_CERTS_KEY, trustCerts);
    }

    /**
     * Gets the TrustedCertificateAuthority
     *
     * @return the TrustedCertificateAuthority
     */
    public final String getTrustedCertificateAuthority() {
        return (String) getValue(TRUSTED_CERTIFICATE_AUTHORITY_KEY);
    }

    /**
     * Sets the TrustedCertificateAuthority
     *
     * @param trustedCertificateAuthority
     *            TrustedCertificateAuthority
     */
    public void setTrustedCertificateAuthority(final String trustedCertificateAuthority) {
        setValue(TRUSTED_CERTIFICATE_AUTHORITY_KEY, trustedCertificateAuthority);
    }

    /**
     * @return the entityProfileName
     */
    public String getEntityProfileName() {
        return (String) getValue(ENTITY_PROFILE_KEY);
    }

    /**
     * Sets entityProfileName
     *
     * @param entityProfileName the entityProfileName
     */
    public void setEntityProfileName(final String entityProfileName) {
        setValue(ENTITY_PROFILE_KEY, entityProfileName);
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
