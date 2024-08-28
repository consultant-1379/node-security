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

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.AsyncActionTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowActionTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

/*
 * /*------------------------------------------------------------------------------
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
/**
 * <p>
 * Workflow task representing a request to issue a trusted certificates
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.CPP_ISSUE_TRUSTED_CERTIFICATE
 * </p>
 *
 * Created by elucbot.
 */
@AsyncActionTask(errorMessage = "CppInstallTrustedCertificateTaskFailed")
public class IssueInitTrustedCertEnrollmentTask extends WorkflowActionTask {

    private static final long serialVersionUID = 820502206662057439L;
    /**
     * Key of the trustedCertCategory value in the map
     */
    public static final String CPP_TRUSTED_CERT_CATEGORY_KEY = WorkflowParameterKeys.TRUSTED_CATEGORY.toString();

    /**
     * Key of the certificate authority value in the map
     */
    public static final String CPP_TRUSTED_CERTIFICATE_AUTHORITY_KEY = WorkflowParameterKeys.CERTIFICATE_AUTHORITY_ID.toString();

    public static final String SHORT_DESCRIPTION = "Init OAM trustInstall";

    /**
     * Constructs IssueInitTrustedCertEnrollmentTask.
     */
    public IssueInitTrustedCertEnrollmentTask() {
        super(WorkflowTaskType.CPP_ISSUE_TRUSTED_CERTIFICATE);
        setValue(CPP_TRUSTED_CERT_CATEGORY_KEY, TrustedCertCategory.CORBA_PEERS);
        setValue(CPP_TRUSTED_CERTIFICATE_AUTHORITY_KEY, "");
        setShortDescription(SHORT_DESCRIPTION);
    }

    /**
     * Constructs IssueInitTrustedCertEnrollmentTask.
     *
     * @param nodeName
     *            of the NE
     * @param category
     *            of the Trust Store (See TrustedCertCategory class)
     * @param ca
     *            trusted Certificate Authority
     * @throws IllegalArgumentException
     *             if the category String is not represents any TrustedCertCategory
     */
    public IssueInitTrustedCertEnrollmentTask(final String nodeName, final String category, final String ca) {
        super(WorkflowTaskType.CPP_ISSUE_TRUSTED_CERTIFICATE, nodeName);
        final TrustedCertCategory c = getTrustCategory(category);
        setValue(CPP_TRUSTED_CERT_CATEGORY_KEY, c);
        setValue(CPP_TRUSTED_CERTIFICATE_AUTHORITY_KEY, ca);
        setShortDescription(SHORT_DESCRIPTION);
    }

    /**
     * Returns the category based on the String. Returns default category CORBA_PEERS if null is supplied.
     *
     * @param category
     *            TrustedCertCategory as String or null
     * @return category
     * @throws IllegalArgumentException
     *             if the category String is not represents any TrustedCertCategory
     */
    public static TrustedCertCategory getTrustCategory(final String category) {
        if (null == category) {
            //Default category
            return TrustedCertCategory.CORBA_PEERS;
        } else {
            return TrustedCertCategory.valueOf(category);
        }
    }

    /**
     * Gets the TrustedCertCategory
     * 
     * @return the TrustedCertCategory
     */
    public final TrustedCertCategory getTrustCategory() {
        return (TrustedCertCategory) getValue(CPP_TRUSTED_CERT_CATEGORY_KEY);
    }

    /**
     * Sets the TrustedCertCategory Sets default category CORBA_PEERS if null is supplied.
     * 
     * @param category
     *            TrustedCertCategory as String or null
     * @throws IllegalArgumentException
     *             if the category String is not represents any TrustedCertCategory
     */
    public void setTrustCategory(final String category) {
        final TrustedCertCategory c = getTrustCategory(category);
        setTrustCategory(c);
    }

    /**
     * Sets the TrustedCertCategory
     * 
     * @param category
     *            TrustedCertCategory
     */
    public void setTrustCategory(final TrustedCertCategory category) {
        setValue(CPP_TRUSTED_CERT_CATEGORY_KEY, category);
    }

    /**
     * Gets the TrustedCertificateAuthority
     * 
     * @return the TrustedCertificateAuthority
     */
    public final String getTrustedCertificateAuthority() {
        return (String) getValue(CPP_TRUSTED_CERTIFICATE_AUTHORITY_KEY);
    }

    /**
     * Sets the TrustedCertificateAuthority
     * 
     * @param trustedCertificateAuthority
     *            TrustedCertificateAuthority
     */
    public void setTrustedCertificateAuthority(final String trustedCertificateAuthority) {
        setValue(CPP_TRUSTED_CERTIFICATE_AUTHORITY_KEY, trustedCertificateAuthority);
    }
}