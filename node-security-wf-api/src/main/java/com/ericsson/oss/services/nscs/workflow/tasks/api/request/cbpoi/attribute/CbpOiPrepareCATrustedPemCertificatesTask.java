/*------------------------------------------------------------------------------
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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.cbpoi.attribute;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;
import java.io.Serializable;
import java.util.Map;

public class CbpOiPrepareCATrustedPemCertificatesTask extends WorkflowQueryTask {

    private static final long serialVersionUID = -6682817874700144216L;

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

    public static final String SHORT_DESCRIPTION = "Prepare CA trusted certificates";

    public CbpOiPrepareCATrustedPemCertificatesTask() {
        super(WorkflowTaskType.CBPOI_PREPARE_CA_TRUSTED_PEM_CERTIFICATES);
        setShortDescriptionLocal(SHORT_DESCRIPTION);
    }

    public CbpOiPrepareCATrustedPemCertificatesTask(String fdn) {
        super(WorkflowTaskType.CBPOI_PREPARE_CA_TRUSTED_PEM_CERTIFICATES, fdn);
        setShortDescriptionLocal(SHORT_DESCRIPTION);
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
     * Gets the trusted Certificate Authority
     *
     * @return the trusted Certificate Authority
     */
    public final String getTrustedCertificateAuthority() {
        return (String) getValue(TRUSTED_CERTIFICATE_AUTHORITY_KEY);
    }

    /**
     * sets the trusted Certificate Authority
     *
     * @param caName
     *            the certificate authority name to set
     */
    public void  setTrustedCertificateAuthority(final String caName) {
        setValue(TRUSTED_CERTIFICATE_AUTHORITY_KEY, caName);
    }

    /**
     * @return the entity profile name
     */
    public String getEntityProfileName() {
        return (String) getValue(ENTITY_PROFILE_KEY);
    }

    /**
     * sets the entity profile name
     *
     * @param profileName
     *            the entity profile name to set
     */
    public void  setEntityProfileName(final String profileName) {
        setValue(ENTITY_PROFILE_KEY, profileName);
    }

    /**
     * @return the isReissue flag
     */
    public String getIsReissue() {
        return (String) getValue(ISREISSUE_KEY);
    }

    /**
     * sets the isReissue flag
     *
     * @param isReissue
     *            the isReissue flag value to set
     */
    public void  setIsReissue(final String isReissue) {
        setValue(ISREISSUE_KEY, isReissue);
    }

    private void setShortDescriptionLocal(final String shortDescription) {
        super.setShortDescription(shortDescription);
    }
}
