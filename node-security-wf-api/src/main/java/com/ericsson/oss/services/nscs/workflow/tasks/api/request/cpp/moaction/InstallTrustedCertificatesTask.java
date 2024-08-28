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

/**
 * <p>
 * Workflow task representing a request to install trusted certificates
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.CPP_INSTALL_TRUSTED_CERTIFICATE
 * </p>
 *
 * Created by emaynes on 24/06/2014.
 */
@AsyncActionTask(errorMessage = "CppInstallTrustedCertificateTaskFailed")
public class InstallTrustedCertificatesTask extends WorkflowActionTask {

    private static final long serialVersionUID = 8296335580208415223L;

    /**
     * Key of the trustedCertCategory value in the map
     */
    public static final String CPP_TRUSTED_CERT_CATEGORY_KEY = WorkflowParameterKeys.TRUSTED_CATEGORY.toString();

    public static final String SHORT_DESCRIPTION = "Init OAM trustInstall";

    /**
     * Constructs InstallTrustedCertificatesTask. Sets the rollbackTimeout to default: CORBA_PEERS
     */
    public InstallTrustedCertificatesTask() {
        super(WorkflowTaskType.CPP_INSTALL_TRUSTED_CERTIFICATE);
        setValue(CPP_TRUSTED_CERT_CATEGORY_KEY, TrustedCertCategory.CORBA_PEERS);
        setShortDescription(SHORT_DESCRIPTION);
    }

    /**
     * Constructs InstallTrustedCertificatesTask.
     *
     * @param nodeName
     *            of the NE
     * @param category
     *            of the Trust Store (See TrustedCertCategory class)
     * @throws IllegalArgumentException
     *             if the category String is not represents any TrustedCertCategory
     */
    public InstallTrustedCertificatesTask(final String nodeName, final String category) {
        super(WorkflowTaskType.CPP_INSTALL_TRUSTED_CERTIFICATE, nodeName);
        final TrustedCertCategory c = getTrustCategory(category);
        setValue(CPP_TRUSTED_CERT_CATEGORY_KEY, c);
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
     * @return the trusted category
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
    public final void setTrustCategory(final TrustedCertCategory category) {
        setValue(CPP_TRUSTED_CERT_CATEGORY_KEY, category);
    }
}
