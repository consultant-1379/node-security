/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
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

/**
 * <p>
 * Workflow task representing a request to enable or disable CRL check process for COM ECIM nodes
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.COM_ECIM_ENABLE_OR_DISABLE_CRL_CHECK
 * </p>
 *
 * @author xchowja
 */
public class ComEcimEnableOrDisableCRLCheckTask extends WorkflowQueryTask {

    private static final long serialVersionUID = 1277048956469711256L;
    public static String CERT_TYPE = WorkflowParameterKeys.CERT_TYPE.toString();
    public static String CRL_CHECK_STATUS = WorkflowParameterKeys.CRL_CHECK_STATUS.toString();

    public static final String SHORT_DESCRIPTION = "Enable/disable crlCheck";

    public ComEcimEnableOrDisableCRLCheckTask() {
        super(WorkflowTaskType.COM_ECIM_ENABLE_OR_DISABLE_CRL_CHECK);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public ComEcimEnableOrDisableCRLCheckTask(final String fdn) {
        super(WorkflowTaskType.COM_ECIM_ENABLE_OR_DISABLE_CRL_CHECK, fdn);
        setShortDescription(SHORT_DESCRIPTION);
    }

    /**
     * @return the certType
     */
    public String getCertType() {
        return (String) getValue(CERT_TYPE);
    }

    /**
     * @param certType
     *            the certType to set
     */
    public void setCertType(final String certType) {
        setValue(CERT_TYPE, certType);
    }

    /**
     * @return the crlCheckStatus
     */
    public String getCrlCheckStatus() {
        return (String) getValue(CRL_CHECK_STATUS);
    }

    /**
     * @param crlCheckStatus
     *            the crlCheckStatus to set
     */
    public void setCrlCheckStatus(final String crlCheckStatus) {
        setValue(CRL_CHECK_STATUS, crlCheckStatus);
    }

}
